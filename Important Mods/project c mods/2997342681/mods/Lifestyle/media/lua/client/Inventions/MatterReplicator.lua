--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------
--[[
LSIntObjs = LSIntObjs or {}

local function getValidMRData(obj)
	if not InventionsMenu or not InventionsMenu.hasInvData(obj) then return false; end
	return obj:getModData().movableData['inventionData']
end

local function itemHasItems(item)
	if not item or item:getCategory() ~= "Container" then return false; end
	local containerList = item:getInventory()
	return containerList and #containerList > 0
end

local function hasItems(container)
	return not container:getItems():isEmpty()
end

local function canTransferToMR(obj, container, item)
	if hasItems(container) or itemHasItems(item) then return false; end
	local data = getValidMRData(obj)
	if not data or not data.enabled then return false; end


	return not data.isRunning
end

local function canTransferFromMR(obj)
	local data = getValidMRData(obj)
	if not data or not data.enabled then return false; end
	

	return not data.isRunning
end

local function isMatterReplicator(obj)
	local customName = LSUtil.getObjCustomName(obj)
	return customName and customName == "MatterReplicator"
end

local ogTransferValid = ISInventoryTransferAction.isValid;
function ISInventoryTransferAction:isValid()
	local destObj = self.destContainer and self.destContainer:getParent()
	local srcObj = self.srcContainer and self.srcContainer:getParent()
	if isMatterReplicator(destObj) then
	
		if not canTransferToMR(destObj, self.destContainer, self.item) then return false; end
	end
	if isMatterReplicator(srcObj) then
	
		if not canTransferFromMR(srcObj) then return false; end
	end


    return ogTransferValid(self)
end

local function updateObjData(object, upSprite)
	if not isClient() then return; end
	if upSprite then object:transmitUpdatedSpriteToServer(); end
	object:transmitModData()
end

local function doCooldown(data, player)
	if not data['cooldownTime'] then data['cooldownTime'] = 48; end
	local hour = getGameTime():getWorldAgeHours()
	data['cooldown'] = hour+data['cooldownTime']
end

local function doShowerMoodle(data, player, fail)
	local moodle, opposite = "BathCold", "BathHot"
	if not fail and data["isHeated"] then moodle = "BathHot"; opposite = "BathCold"; end
	LSUtil.addMoodleValue(player:getModData().LSMoodles, moodle, 0.2, opposite, player, true)
end

local function playFailUISound(soundName)
	getSoundManager():playUISound(soundName)
end

local function playSound(object, soundName)
	local emitter = getWorld():getFreeEmitter(object:getX(),object:getY(),object:getZ())
	emitter:playSoundImpl(soundName, false, object)
end

local function doDirtPuddle(object, x, y, z, outside)
	playSound(object, "Toilet_Flush_Clogged")
	if outside then return; end
	local puddleList = {"LS_HScraps_DirtPuddle_0","LS_HScraps_DirtPuddle_1","LS_HScraps_DirtPuddle_2","LS_HScraps_DirtPuddle_3","LS_HScraps_DirtPuddle_4",
	"LS_HScraps_DirtPuddle_5","LS_HScraps_DirtPuddle_6","LS_HScraps_DirtPuddle_7"}
	local dirtSprite = puddleList[ZombRand(#puddleList)+1]
	if isClient() then
		sendClientCommand("LS", "DebugAddLitter", {x, y, z, 2, dirtSprite})
	else
		LSAddLitter(x, y, z, 2, dirtSprite)
	end
end

local function doBreakdown(data, object, sqr)
	doDirtPuddle(object, object:getX(),object:getY(),object:getZ(), sqr:isOutside())
	data['isBroken'] = true
	playFailUISound("Toilet_Clogged_"..tostring(ZombRand(2)+1))
	updateObjData(object, false)
end

--local function doBreakdownRoll(data, object, sqr)
--	if ZombRand(100)+1 > data['durability'][1] then return false; end
--	doBreakdown(data, object, sqr)
--	return true
--end

local function doFailRoll(data, player, object, sqr)
	if ZombRand(100)+1 > data['durability'][1] then return false; end
	LSUtil.makeCharWet(player, true)
	LSUtil.changeCharVisualDirt(player, 0.8, 0.6, true)
	doShowerMoodle(data, player, true)
	local moodles = player:getModData().LSMoodles
	if LSUtil.isValidMoodle(moodles, "Nauseous") and moodles['Nauseous'].Value <= 0 then LSUtil.playCharVoice(player, "_Yuck0", 8); end
	LSUtil.addMoodleValue(moodles, "Nauseous", 0.4, "SmellGood", player, true)
	doBreakdown(data, object, sqr)
	LSUtil.reduceHygiene(player:getModData(), player, 70, 100)
	return true
end

local function doPuddleRoll(data, object, sqr)
	if ZombRand(100)+1 > data['durability'][2] then return; end
	doDirtPuddle(object, object:getX(),object:getY(),object:getZ(), sqr:isOutside())
end

local function doHygieneBenefits(data, player)
	if not data['hasDryJet'] then LSUtil.makeCharWet(player, true); end
	LSUtil.changeCharVisualDirt(player, data['efficiencyMult'][2]*-1, data['efficiencyMult'][3]*-1, data['hasHighPressureJet']) -- vals should be negative to clean
	LSUtil.playCharVoice(player, "LikeHMM0", 3)
	doShowerMoodle(data, player, false)
	if data["isPerfumed"] then -- level - 0.2, 0.4, 0.6; buff - 10, 25, 40
		local moodles = player:getModData().LSMoodles
		if LSUtil.isValidMoodle(moodles, "SmellGood") and moodles['SmellGood'].Value <= 0 then LSUtil.playCharVoice(player, "LikeHMM0", 3); end
		LSUtil.addMoodleValue(moodles, "SmellGood", data["isPerfumed"].level, "Nauseous", player, true)
		player:getBodyDamage():setUnhappynessLevel(math.max(0, player:getBodyDamage():getUnhappynessLevel()-data["isPerfumed"].buff))
	end
	LSUtil.addHygiene(player:getModData(), player, data["efficiencyMult"][1], data["hygieneMax"]) -- hygieneNeed of 0 = completely satisfied; 30 == shower. e.g. hygieneMax of 30 with hygieneVal of 70
end

LSIntObjs.MatterReplicator = function(player, object)
	if not LSUtil.isValidObj(object, "MatterReplicator") or not LSUtil.isObjClosePrecise(object, player, 0.5) then return; end
	if not LSUtil.isObjOnSqr(object) then object:setOverlaySprite(nil); return; end
	local data = object:getModData().movableData and object:getModData().movableData['inventionData']
	if not data or not data['enabled'] or data['isBroken'] or LSUtil.isCooldown(data) then return; end
	local sqr = object:getSquare()
	if not LSUtil.InvHasWater(object, data['waterUsage'][1], data['noPlumbing']) or (not LSUtil.sqrHasEnergy(sqr) and not data['selfPowered']) then return; end

	doCooldown(data, player)
	updateObjData(object, false)

	local count, jetSound, jetSprite = 0, false, false
	local objOverlaySprite
	if object:getOverlaySprite() then objOverlaySprite = object:getOverlaySprite():getName(); end
	
	local waitABit
	waitABit = function()
		if not jetSound then playSound(object, "Steam_FIZZ1"); jetSound = true; end
		if count == 0 or math.floor(count)%3 == 0 then object:setOverlaySprite("LS_Fog_" .. tostring(ZombRand(7)+1), 1, 1, 1, 1); end
		count = count + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
		if count > 6 then
			Events.OnTick.Remove(waitABit)
			object:setOverlaySprite(objOverlaySprite)
			if doFailRoll(data, player, object, sqr) then return; end
			doHygieneBenefits(data, player)
			doPuddleRoll(data, object, sqr)
			--if not doBreakdownRoll(data, object, sqr) then doPuddleRoll(data, object, sqr); end
		end
	end
	Events.OnTick.Add(waitABit)

	if not data['noPlumbing'] then object:setWaterAmount(math.max(0, object:getWaterAmount() - data['waterUsage'][1])); updateObjData(object, false); end -- b41 only
	-- ISTakeWaterAction.SendTakeWaterCommand BUGGED DO NOT USE (messes with obj data)
	
	-- requirements:
	-- x electricity
	-- x plumbing (req water)
	-- improvements:
	-- x hygiene amount 8/8
	-- x activation cooldown 8/8
	-- power efficiency (how much electricity it requires) 5/5
	-- liquid efficiency (how much water and cleaning liquid is used per wash) 5/5
	-- liquid storage (how much cleaning liquid it can store) 5/5
	-- x precision (chance to generate dirt puddles) 5/5
	-- x breakdown chance (will not work again until repaired, generates dirt puddle) 5/5
	-- x fail chance (increase need instead of decreasing, makes character more visually dirty, bad smell moodle and generate dirt puddle) 5/5
	-- x perfume smell (adds good smell moodle and a trickle of happiness) 3/3
	-- x heated (switch from cold shower to hot shower moodle) 1/1
	-- x dry air jets (won't leave character wet) 1/1
	-- x high pressure jets (will also clean worn clothes) 1/1
	-- x self-powered (won't require electricity) 1/1
	-- x moisture absorber (won't require plumbing, still requires cleaning liquid) 1/1

end

InventionsMenu = InventionsMenu or {}

local function disableOption(option, tooltipDescription, iconTexture)
	option.notAvailable = true
	option.toolTip = getNewTooltip(tooltipDescription, false, false)
	if iconTexture then option.iconTexture = getTexture(iconTexture); end
end

local function getAdditionalStatsDesc(object, data)
	local main, improv, other
	local waterAmount = tostring((object:hasWater() and object:getWaterAmount()) or 0)
	waterAmount = waterAmount.." <SPACE>"..getText("IGUI_FitnessNeedItem",data['waterUsage'][1])
	local wTxtM, wTxtO = " <RGB:0.8,0.6,0.6>"..getText("IGUI_RequiresWaterSupply"), " <RGB:0.8,0.6,0.6>"..waterAmount
	if data['noPlumbing'] then wTxtM = " <RGB:0.5,0.9,0.5>"..getText("Tooltip_WaterUnlimited"); wTxtO = wTxtM;
	elseif LSUtil.InvHasWater(object, data['waterUsage'][1], false) then wTxtM, wTxtO = " <RGB:0.6,0.8,0.6>"..getText("IGUI_RainCollectorHasWater"), " <RGB:0.6,0.8,0.6>"..waterAmount ; end
	local powerText = " <RGB:0.8,0.6,0.6>"..getText("IGUI_RadioRequiresPowerNearby")
	if data['selfPowered'] then powerText = " <RGB:0.5,0.9,0.5>"..getText("Tooltip_WaterUnlimited");
	elseif LSUtil.sqrHasEnergy(object:getSquare()) then powerText = " <RGB:0.6,0.8,0.6>"..getText("IGUI_RadioPowerNearby"); end

	main = " <TEXT><LINE><RGB:0.9,0.9,0.9>"..getText("IGUI_ItemCat_Water")..": <SPACE>"..wTxtM.." <LINE><LINE><RGB:0.9,0.9,0.9>"..getText("IGUI_RadioPower")..": <SPACE>"..powerText
	other = " <TEXT><RGB:0.9,0.9,0.9>"..getText("IGUI_ItemCat_Water")..": <SPACE>"..wTxtO

	return {main, improv, 1, other, 1} -- main page, improvements page and number (for pages), other and number
end

InventionsMenu.MatterReplicator = function(context, parentMenu, worldobjects, character, obj, data, objName, spriteName)
	if not data['inventionData']['enabled'] then return; end
	local bhs, mhs, ghs = " <RGB:1,0,0> "," <RGB:0,1,0> "," <RGB:1,1,0> "
	--local playerNum = character:getPlayerNum()
	--local width, height = getTextManager():MeasureStringX(UIFont.NewSmall, getText('IGUI_Inventions_repairPenalty'))+30, 50
	--local toolTipPosArgs = {(getPlayerScreenWidth(playerNum)-width)/2,(getPlayerScreenHeight(playerNum)-height)/2,width}
	local toolTipLineWidth = getTextManager():MeasureStringX(UIFont.NewSmall, getText('IGUI_Inventions_repairPenalty'))+200
	-- repair and stuff
	-- broken
	if data['inventionData']['isBroken'] then
		local researchLevels, totalResearch = 0, 0
		for k, v in pairs(data['improvementData']) do
			researchLevels = researchLevels+v[1]
			totalResearch = totalResearch+v[2]
		end
		local percentCompleted = 0
		if researchLevels > 0 then percentCompleted = math.ceil((researchLevels*10)/totalResearch); end -- returns 1, 2, 3 ... 10
		percentCompleted = math.min(10, math.max(1, percentCompleted))
		
		local reqList = getInventionDefinitionsMult(percentCompleted, data['inventionData']['costDefs'][1], data['inventionData'], true, false)
		local disable, description, footNote = LSUtil.getInventionFixParams(reqList, character, bhs, mhs, ghs)
		local fixTooltip = LSUtil.getNewTooltip(description, nil, nil, footNote)

		local fixOption = parentMenu:addOption(getText('ContextMenu_Inventions_Fix'),worldobjects,InventionsMenu.onFixObj,character,obj,data,reqList,false)
		fixOption.notAvailable = disable
		fixOption.toolTip = fixTooltip
		fixOption.iconTexture = getTexture('media/ui/maintenance_icon.png')
		return
	end
	-- stats -- should show a breakdown of what the obj does and its stats with researched improvements and percentages
	local statsAddArgs = getAdditionalStatsDesc(obj, data['inventionData'])
	local statsDesc = LSUtil.getInventionStatsParams(data, obj, objName, character, "MatterReplicator", statsAddArgs)
	local statsTooltip = LSUtil.getNewTooltip(statsDesc[1], nil, nil, nil, toolTipLineWidth, {r=0, g=0, b=0, a=0.7})
	statsTooltip.descList = statsDesc
	statsTooltip.descCurrent = 1
	--local statParentMenu = parentMenu
	local statsOption = parentMenu:addOption(getText('ContextMenu_Inventions_Stats'),worldobjects,InventionsMenu.onChangeStatsTooltip,statsTooltip)
	statsOption.toolTip = statsTooltip
	statsOption.iconTexture = getTexture('media/ui/bookWrite_icon.png')
	statsOption.notClose = true

	-- cooldown/recharging
	local rechargeText, ready = 'Inventions_Ready', ""
	local tooltipTxt = getText('Tooltip_'..rechargeText)
	if LSUtil.isCooldown(data['inventionData']) then ready, rechargeText = "No",'Inventions_Recharging'; tooltipTxt = getText('Tooltip_'..rechargeText..'N', math.floor(data['inventionData']['cooldown']-getGameTime():getWorldAgeHours())); end
	
	local rechargeOption = parentMenu:addOption(getText('ContextMenu_'..rechargeText))
	local rechargeTooltip = LSUtil.getSimpleTooltip(tooltipTxt)
	rechargeOption.toolTip = rechargeTooltip
	rechargeOption.iconTexture = getTexture('media/ui/shareknowledge'..ready..'_icon.png')
	-- if LSUtil.walkToFront(player, Invention) then
end
]]--