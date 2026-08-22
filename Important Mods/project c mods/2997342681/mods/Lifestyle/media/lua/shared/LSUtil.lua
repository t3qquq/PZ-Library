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

LSUtil = {}

local function getLitterChanceRoll(sandboxOption)
	local roll = 200
	if (not sandboxOption) or (not tonumber(sandboxOption)) then print("WARNING: getLitterChanceRoll failed to get sandboxOption value"); return roll; end
	if sandboxOption == 1 then
		roll = 600
	elseif sandboxOption == 2 then
		roll = 400
	elseif sandboxOption == 4 then
		roll = 100
	end
	return roll
end

function LSUtil.canLitter(chance)
	if chance <= 0 then return false; end
	local maxRoll = getLitterChanceRoll(SandboxVars.LSHygiene.CleaningLitterChance)
	local doRoll = ZombRand(maxRoll)+1
	if doRoll <= chance then return true; end
	return false
end

------------ UI

function LSUtil.doNote(character, args)  -- args = {text, queueType, tex, time, closePerm, infoPanel, noSpam, TextureCustomProps(w,h,size)}
	if not LSUtil.getValidCharacter(character) then return; end
	LSNoteMng.addToQueue(getCore():getScreenWidth()-400,(getCore():getScreenHeight()/5)-50,300,50, {character, args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]}) -- player, mainText, queueType, tex, time, closePerm, infoPanel, noSpam, TextureCustomProps(w,h,size)
	--	note args cheat sheet
	--	1. queueType - "TutorialYoga" - game won't queue notes of same type
	--	2. time - NUMBER - how long note will stay on screen before disappearing. n = real seconds
	--	3. closePerm - "noteYoga" - if note can be closed permanently (don't do this for repeatables like art)
	--	4. infoPanel - richText - add info panel text, otherwise info option won't appear
	--	5. noSpam - BOOLEAN - if note should only appear once per session
	--	6. TextureCustomProps - TABLE - texture is a custom image (not a tile texture). Must provide placement position (width and height) and image size.
end

function LSUtil.doRichTextType(x,y,w,h,customText,font,r,g,b)
	local newRichText = ISRichTextPanel:new(x, y, w, h)
	newRichText.backgroundColor = {r=0, g=0, b=0, a=0}
	newRichText.text = customText
	newRichText.defaultFont = font
	newRichText.autosetheight = false
	newRichText.marginLeft = 0
	newRichText.marginTop = 0
	newRichText.marginRight = 0
	newRichText.marginBottom = 0
	newRichText.textR = r
	newRichText.textG = g
	newRichText.textB = b
	return newRichText
end

function LSUtil.measureString(textManager, axis, fontType, text)
	if not textManager then textManager = getTextManager(); end
	local measureY
	if axis == "XY" then axis, measureY = "X", textManager['MeasureStringY'](textManager, fontType, text); end
	return textManager['MeasureString'..axis](textManager, fontType, text), measureY
end

local function getSkillIconsTable(key)
	local t = {
		Art = "artpalette_icon",
		Farming = "naked_icon",
		Woodwork = "woodwork_icon",
		MetalWelding = "metalwork_icon",
		Electricity = "electrical_icon",
		Mechanics = "mechanics_icon",
		Maintenance = "maintenance_icon",
	}
	return t[key]
end

function LSUtil.getSkillIcon(skillName)
	local icon = getSkillIconsTable(skillName)
	if not icon then return ""; end
	return "<IMAGE:media/ui/"..icon..".png,16,16>"
end

function LSUtil.getTexIcon(itemName)
	local prop
	local items = getAllItems()
    for i=0, items:size()-1 do
        local item = items:get(i)
        if item and item:getFullName() and item:getFullName() == "Base."..itemName then
			prop = item:InstanceItem(item:getFullName())
			break
        end
    end
	local texString
	local itemText = itemName
	if prop then
		texString = "<IMAGE:"..prop:getTexture():getName()..",16,16>"
		itemText = prop:getName()
	end
	return texString, itemText
end

------------ Tooltip

function LSUtil.getSimpleTooltip(description, background)
	local tooltip = ISToolTip:new();
	if background then tooltip.backgroundColor = background; tooltip.descriptionPanel.backgroundColor = background; end
	tooltip:initialise();
	tooltip:setVisible(false);
	tooltip.description = description
	return tooltip
end

function LSUtil.getNewTooltip(description, texture, name, footNote, lineWidth, background)
	local tooltip = LSUtil.getSimpleTooltip(description, background)
	if name then tooltip:setName(name); end
	if texture then tooltip:setTexture(texture); end
	if footNote then tooltip.footNote = footNote; end
	if lineWidth then tooltip.maxLineWidth = lineWidth; tooltip:doLayout(); end
	return tooltip
end

------------ Halo

function LSUtil.doSimpleArrowHalo(character, description, pos)
	local rgb = {255, 120, 120}
	if pos then rgb = {170, 255, 150}; end
	HaloTextHelper.addTextWithArrow(character, getText("IGUI_HaloNote_Hygiene"), not pos, rgb[1], rgb[2], rgb[3])
end

------------ Context

function LSUtil.getDummyOption(parentMenu, name, tooltipText, tex, add, disable)
	local option = parentMenu[add](parentMenu, name)
	option.notAvailable = disable
	if tooltipText then option.toolTip = LSUtil.getSimpleTooltip(tooltipText); end
	if tex then option.iconTexture = tex; end
	return option
end

------------ Calc

function LSUtil.truncateToTwoDecimals(num)
    local s = tostring(num)
    local dot = string.find(s, "%.")
    if dot then
        local decimals = string.sub(s, dot + 1)
        if #decimals > 2 then
            return math.floor(num * 100) / 100
        end
    end
    return num
end

function LSUtil.getPercentage(total,value, decimals, isModifier)
	-- returns the percentage of value, based on total being 100%
	if not total or total == 0 then return 0; end
	local percentage = (value/total)*100
	local mult = 1
	if decimals then
		mult = 10^decimals -- 10, 100, 1000...
	end
	local clean100 = 0
	if total == 1 and isModifier then clean100 = 100; end -- for modifiers
	return math.floor(((percentage*mult+0.5)/mult)-clean100)
end

------------ Square

function LSUtil.walkToAdjSqr(character, sqr)
	if not sqr then return false; end
	-- get nearest adj square
	local adjSqr = AdjacentFreeTileFinder.Find(sqr, character)
	-- do walk
	if adjSqr then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(character, adjSqr))
		return true
	end
	return false
end

function LSUtil.srqGetClosest(list, character)
	if #list == 0 then return false; end
	local lowestdist = 100000
	local distchoice
	local playerSqr = character:getSquare()
	for k, v in ipairs(list) do
		if v and AdjacentFreeTileFinder.privTrySquare(playerSqr, v) then
			local dist = v:DistToProper(character)
			if dist < lowestdist then
				distchoice = v
			end
		end
	end
	return distchoice
end

function LSUtil.sqrHasEnergy(sqr)
	return sqr:haveElectricity() or (SandboxVars.ElecShutModifier > -1 and GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier)
end

------------ Objects

LSUtil.pianoPos = false

function LSUtil.getObjCustomName(obj)
	if not obj or not instanceof(obj, "IsoObject") then return false; end
	local properties = obj:getSprite() and obj:getSprite():getProperties()
	return properties and properties:Is("CustomName") and properties:Val("CustomName")
end

function LSUtil.updateObjData(obj, upSprite)
	if upSprite then obj:transmitUpdatedSpriteToServer(); end
	obj:transmitModData()
end

function LSUtil.getMoveableDisplayName(genName, obj, cN, gN)
    if not obj or not instanceof(obj, "IsoObject") or not obj:getSprite() then return tostring(genName); end
	if cN and gN then return Translator.getMoveableDisplayName(gN.." "..cN); end
    local props = obj:getSprite():getProperties()
    if props:Is("CustomName") then
        local name = props:Val("CustomName")
        if props:Is("GroupName") then
            name = props:Val("GroupName") .. " " .. name
        end
        return Translator.getMoveableDisplayName(name)
    end
    return tostring(genName)
end

function LSUtil.isObjOnSqr(obj)
	local sqr = obj:getSquare()
	if not sqr then return false; end
	for i=0,sqr:getObjects():size()-1 do
		local thisObject = sqr:getObjects():get(i)
		if LSUtil.isValidObj(thisObject, "ignore this warning") and thisObject == obj then return true; end
	end
	return false
end

function LSUtil.isValidObj(obj, spriteName)
	if not obj or not instanceof(obj, "IsoObject") then print("------------WARN: LSUtil.isValidObj - obj is NIL, spriteName: "..tostring(spriteName)); return false; end
	return true
end

function LSUtil.isObjClose(obj, character, range)
	if character:getX() >= obj:getX() - range and character:getX() <= obj:getX() + range and
	character:getY() >= obj:getY() - range and character:getY() <= obj:getY() + range then return true; end
	return false
end

function LSUtil.isObjClosePrecise(obj, character, range) -- obj getX, getY returns top corner so we add 0.5 to get center
	local objX, objY = obj:getX()+0.5, obj:getY()+0.5
	if character:getX() >= objX - range and character:getX() <= objX + range and
	character:getY() >= objY - range and character:getY() <= objY + range then return true; end
	return false
end

function LSUtil.isObjSameSqr(obj, character)
	return not obj:getSquare() ~= character:getSquare()
end

function LSUtil.getObjTexture(spriteName, dir)
	local texture = getSprite(spriteName):getTextureForCurrentFrame(IsoDirections[dir])
	return texture
end

local function getFacing(obj)
	local properties = obj:getSprite():getProperties()
	if properties:Is("Facing") then return properties:Val("Facing"); end
	return false
end

function LSUtil.walkToFront(character, obj)
	if not LSUtil.isValidObj(obj, "nil") then return false; end
	-- get square
	local sqr = obj:getSquare()
	if not sqr then return false; end
	-- get obj face
	local facing = getFacing(obj)
	if not facing then return false; end
	-- get front square
	local frontSqr = sqr["get"..facing](sqr)
	if not frontSqr then return false; end
	-- do walk
	if AdjacentFreeTileFinder.privTrySquare(sqr, frontSqr) then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(character, frontSqr))
		return true
	end
	return false
end

function LSUtil.walkToAdj(character, obj)
	if not LSUtil.isValidObj(obj, "nil") then return false; end
	-- get square
	local sqr = obj:getSquare()
	if not sqr then return false; end
	-- get nearest adj square
	local adjSqr = AdjacentFreeTileFinder.Find(sqr, character)
	-- do walk
	if adjSqr then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(character, adjSqr))
		return true
	end
	return false
end

---------- Items

function LSUtil.doItemTransfer(character, targetItem)
	if instanceof(targetItem, "InventoryItem") then
		if luautils.haveToBeTransfered(character, targetItem) then
			ISTimedActionQueue.add(ISInventoryTransferAction:new(character, targetItem, targetItem:getContainer(), character:getInventory()))
		end
		return true
	elseif instanceof(targetItem, "ArrayList") then
		local items = targetItem
		for i=1,items:size() do
			local item = items:get(i-1)
			if luautils.haveToBeTransfered(character, item) then
				ISTimedActionQueue.add(ISInventoryTransferAction:new(character, item, item:getContainer(), character:getInventory()))
			end
		end
		return true
	end
	return false
end

function LSUtil.canEquipItem(character, item, primary, twoHands)
	if not instanceof(item, "InventoryItem") then return false; end
	if (primary or twoHands) and character:getPrimaryHandItem() == item then return false; end
	if not primary and not twoHands and character:getSecondaryHandItem() == item then return false; end
	return true
end

function LSUtil.doItemEquip(character, item, primary, twoHands)
	if not instanceof(item, "InventoryItem") then return; end
	if (primary or twoHands) and character:getPrimaryHandItem() == item then return; end
	if not primary and not twoHands and character:getSecondaryHandItem() == item then return; end
	ISTimedActionQueue.add(ISEquipWeaponAction:new(character, item, 50, primary, twoHands))
end

function LSUtil.getItemAndEquip(character, itemTag, itemName, primary, twoHands)
	local playerInv = character:getInventory()
	
	local predicateItem = function(item)
		return ((itemTag and item:hasTag(itemTag)) or (itemName and item:getType() == itemName)) and not item:isBroken()
	end
	
	if not playerInv:containsEvalRecurse(predicateItem) then return false; end
	local item = playerInv:getFirstEvalRecurse(predicateItem)
	
	if item and LSUtil.doItemTransfer(character, item) then
		LSUtil.doItemEquip(character, item, primary, twoHands)
		return item
	end
	return false
end

function LSUtil.getItem(character, itemTag, itemName)
	local playerInv = character:getInventory()
	local predicateItem = function(item)
		return ((itemTag and item:hasTag(itemTag)) or (itemName and item:getType() == itemName)) and not item:isBroken()
	end
	if not playerInv:containsEvalRecurse(predicateItem) then return false; end
	return playerInv:getFirstEvalRecurse(predicateItem)
end

function LSUtil.hasItem(character, itemTag, itemName)
	local playerInv = character:getInventory()
	local predicateItem = function(item)
		return ((itemTag and item:hasTag(itemTag)) or (itemName and item:getType() == itemName)) and not item:isBroken()
	end
	return playerInv:containsEvalRecurse(predicateItem)
end

function LSUtil.isValidInvItem(item)
	if not item or not instanceof(item, "InventoryItem") then LSUtil.debugPrint("---- LS (shared) - called LSUtil.isValidInvItem, item is NIL or NOT instanceof InventoryItem ----"); return false; end --!
	return true
end

function LSUtil.removeItemOnChar(character, item)
	character:removeAttachedItem(item)
	if not character:isEquipped(item) then return true; end
	local removed = character:removeFromHands(item)
	character:removeWornItem(item, false)
	triggerEvent("OnClothingUpdated", character)
	return removed
end

function LSUtil.hasItemsOnChar(character, list) --!
	for k, v in pairs(list) do
		local items = character:getInventory():getItemsFromType(k, true)
		local amount = 0
		for n=0,items:size() - 1 do
			local item = items:get(n)
			local itemCont = item:getContainer()
			if amount < v and itemCont and itemCont:isExistYet() and itemCont:isRemoveItemAllowed(item) and LSUtil.removeItemOnChar(character, item) and not item:isEquipped() then
				if item:IsDrainable() then
					amount = amount+item:getDrainableUsesFloat()
				else
					amount = amount+1
				end
			end
		end
		if amount < v then return false; end
	end
	return true
end

function LSUtil.consumeItemsOnChar(character, list) --!
	if not LSUtil.hasItemsOnChar(character, list) then return false; end
	for k, v in pairs(list) do
		local items = character:getInventory():getItemsFromType(k, true)
		local amount = 0
		for n=0,items:size() - 1 do
			local item = items:get(n)
			local itemCont = item:getContainer()
			if amount < v and itemCont and itemCont:isExistYet() and itemCont:isRemoveItemAllowed(item) and LSUtil.removeItemOnChar(character, item) and not item:isEquipped() then
				if item:IsDrainable() then
					for n=1,v do
						if not item or amount >= v or item:getDrainableUsesFloat() < 1 then break; end
						item:Use()
						amount = amount+1
					end					
				else
					itemCont:DoRemoveItem(item)
					amount = amount+1
				end
				itemCont:setDrawDirty(true)
				itemCont:setHasBeenLooted(true)
			end
		end
		if amount < v then return false; end
	end
	return true
end

function LSUtil.setItemVal(item, getter, setter, val) --!
	if not LSUtil.isValidInvItem(item) then return false; end
	local getFunc, setFunc = item[getter], item[setter]
	if not getFunc or not setFunc then return; end
	if getFunc ~= val then
		setFunc(item, val)
		LSUtil.debugPrint("---- LS - called LSUtil.setItemVal, with key: "..setter..", and value: "..tostring(val)..", for item: "..item:getFullType().." ----")
	end
end

---------- Items (drainable)

function LSUtil.isValidDrainableItem(item) --! b41
	return item and instanceof(item, "InventoryItem") and item:IsDrainable() and not item:isBroken()
end

function LSUtil.itemHasUses(item) --! b41
	return item:isInPlayerInventory() and item:getDrainableUsesInt() > 0
end

function LSUtil.itemGetUses(item) --! b41
	if not LSUtil.itemHasUses(item) then return 0; end
	return item:getDrainableUsesInt()
end

function LSUtil.useItem(item) --! b41
	if not LSUtil.itemHasUses(item) then return; end
	LSUtil.debugPrint("---- LS - called LSUtil.useItem, for item: "..item:getFullType().." ----")
	item:Use()
end

function LSUtil.drainItem(item) --! b41
	if not LSUtil.itemHasUses(item) then return; end
	local uses = LSUtil.itemGetUses(item)
	if uses == 1 then item:Use(); return; end
	for n=1,#uses do
		item:Use()
	end
end

---------- Characters

function LSUtil.getValidCharacter(character)
	if not character or not instanceof(character, "IsoPlayer") or character:isDead() then return false; end
	return character
end

function LSUtil.getValidPlayer(player)
	if not player then return false; end
	local character = getSpecificPlayer(player)
	return LSUtil.getValidCharacter(character)
end

function LSUtil.getCharacterPlayerID(character) --!
	if not LSUtil.getValidCharacter(character) then return false; end
	if not isClient() then return character:getPlayerNum(); end
	return character:getOnlineID()
end

function LSUtil.isCharBusy(character)
	if not character then return true; end
	return character:isDead() or character:getVehicle() or character:isSneaking() or character:hasTimedActions() or character:isAsleep()
end

function LSUtil.isCharSitting(character, data)
	if not character or not data then return true; end
	return character:isSitOnGround() or data.IsSittingOnPianoStool or data.IsSittingOnSeat
end

function LSUtil.playCharVoice(character, soundName, num)
	if character:isFemale() then soundName = "Woman"..soundName; else soundName = "Man"..soundName end
	if num then soundName = soundName..tostring(ZombRand(num)+1); end
	character:getEmitter():playSound(soundName)
end

function LSUtil.makeCharWet(character, doClothes)
	if character:getBodyDamage():getWetness() < 70 then character:getBodyDamage():setWetness(70); end
	if doClothes then
		local wornItems = character:getInventory():getItems()
		for j = 0, wornItems:size()-1 do
			local item = wornItems:get(j)
			if instanceof(item, "Clothing") and character:isEquippedClothing(item) then
				item:setWetness(100)
			end
		end
		sendClothing(character)
		triggerEvent("OnClothingUpdated", character)
	end
end

function LSUtil.changeCharVisualDirt(character, val, bloodVal, doClothes)
	-- negative values clean
	local visual = character:getHumanVisual()
	for i = 1, BloodBodyPartType.MAX:index() do
		local part = BloodBodyPartType.FromIndex(i - 1)
		local dirt = math.min(1, math.max(0,visual:getDirt(part)+val))
		visual:setDirt(part, dirt)
		if bloodVal then 
			local blood = math.min(1, math.max(0,visual:getBlood(part)+bloodVal))
			visual:setBlood(part, blood)
		end
	end
	
	if doClothes then -- getDirt and getBlood both returns 0-1, getDirtyness and getBloodLevel return 0-100
		local wornItems = character:getInventory():getItems()
		for j = 0, wornItems:size()-1 do
			local item = wornItems:get(j)
			if instanceof(item, "Clothing") and character:isEquippedClothing(item) then
				local coveredParts = BloodClothingType.getCoveredParts(item:getBloodClothingType())
				if coveredParts then
					for j=0,coveredParts:size()-1 do
						local part = coveredParts:get(j)
						local dirt = math.min(1, math.max(0,item:getDirt(part)+val))
						item:setDirt(part, dirt)
						if bloodVal then 
							local blood = math.min(1, math.max(0,item:getBlood(part)+bloodVal))
							item:setBlood(part, blood)
						end
					end
				end
				local totalDirtyness = math.min(100, math.max(0,item:getDirtyness()+val*100))
				item:setDirtyness(totalDirtyness)
				if bloodVal then
					local totalBlood = math.min(100, math.max(0,item:getBloodLevel()+bloodVal*100))
					item:setBloodLevel(totalBlood)
				end
			end
		end
	end
	character:resetModel() -- b41
	sendClothing(character) -- b41
	sendVisual(character) -- b41
	triggerEvent("OnClothingUpdated", character) -- b41
end

function LSUtil.getPlayerCooldowns(intervalMin) --!
	local t = {
		[60] = {
			{ -- moodles
			"TaughtSkill",
			"WasTaughtMeditation",
			"WasTaughtSkill",
			"AdviceWasted",
			"Attractive",
			"Nauseous",
			"SmellGood",
			"FTGood",
			"FTBad",
			},
			{
			"TeachCooldown",
			"LessonCooldown",
			"InteractionSpam",
			"mirrorPT",
			"mirrorCD",
			"brushmaster",
			"grimefighter",
			"unstoppable",
			"FortuneTeller",
			},
		},
		[10] = {
			{ -- moodles
			"Zen",
			},
			{
			"TeachCooldown",
			"LessonCooldown",
			"InteractionSpam",
			"StinkingCooldown",
			},
		},
	}
	if not intervalMin then return t; end
	return t[intervalMin]
end

---------- Inventions

function LSUtil.doInvCooldown(obj, data)
	if not data or not data['cooldownTime'] then return; end
	local hour = getGameTime():getWorldAgeHours()
	data['cooldown'] = hour+data['cooldownTime']
	LSUtil.updateObjData(obj, upSprite)
end

function LSUtil.InvHasWater(obj, waterUse, infinite) --b41
	return infinite or (obj:hasWater() and obj:getWaterAmount() >= waterUse)
end

function LSUtil.isCooldown(data)
	return data['cooldown'] and getGameTime():getWorldAgeHours() <= data['cooldown']
end

local function getInventionTooltipDesc(value, totalVal, stringName, color)
	return color .. stringName .. " " .. tostring(value) .. "/" .. tostring(totalVal) .. " <LINE>";
end

local function getInventionTooltipToolDesc(hasTool, toolType, bhs, ghs)
	local itemTexture, itemText = LSUtil.getTexIcon(toolType)
	if not itemTexture then itemTexture = ""; end
	local color = ghs
	if not hasTool then color = bhs; end
	return color .. itemTexture .. itemText .. " <LINE>";
end

function LSUtil.getInventionFixParams(list, character, bhs, mhs, ghs)
	local footNote = "Tooltip_Inventions_Fix"
	local tooltipDesc = "<H1><ORANGE>"..getText("Tooltip_Inventions_Broken").." <LINE><IMAGECENTRE:media/ui/invFix_icon.png,64,64><LINE><TEXT><CENTRE>"..getText("Tooltip_Inventions_Broken2")..
	" <BR><LEFT><RGB:0.8,0.8,0>"..getText("Tooltip_Inventions_RepairCosts")..": <LINE><TEXT><CENTRE><RGB:0.9,0.9,0.9>"..getText("Tooltip_Inventions_SkillReq")..": <LINE><TEXT>"
	local disable
	
	for k, v in pairs(list.reqSkills) do
		local color = ghs
		local skillName = getText("IGUI_perks_"..k)
		local skill = PerkFactory.getPerkFromName(skillName)
		local skillLvl = character:getPerkLevel(skill)
		if skillLvl < v then disable = true; if skillLvl < v/2 then color = bhs; else color = mhs; end; end
		tooltipDesc = tooltipDesc .. getInventionTooltipDesc(skillLvl, v, LSUtil.getSkillIcon(k)..skillName, color)
	end

	tooltipDesc = tooltipDesc.." <LINE><CENTRE><RGB:0.9,0.9,0.9>"..getText("Tooltip_Inventions_ResReq")..": <LINE><TEXT>"
	
	local toolItem = LSUtil.hasItem(character, 'Screwdriver', false)
	if not toolItem then disable = true; end
	tooltipDesc = tooltipDesc .. getInventionTooltipToolDesc(toolItem, 'Screwdriver', bhs, ghs)
	
	for k, v in pairs(list.reqRes) do
		local color = ghs
		local invItem = character:getInventory():getItemCount(k, true)
		if invItem < v then disable = true; if invItem < v/2 then color = bhs; else color = mhs; end; end
		local itemType = string.gsub(k, "^Base%.", "")
		local itemTexture, itemText = LSUtil.getTexIcon(itemType)
		if not itemTexture then itemTexture = ""; end
		tooltipDesc = tooltipDesc .. getInventionTooltipDesc(invItem, v, itemTexture..itemText, color)
	end

	if disable then footNote = "Tooltip_Inventions_FixMissing"; end

	return disable, tooltipDesc, getText(footNote)
end

local function getStatsIconsTable(key)
	local t = {
		efficiency = "fire_icon",
		costPenalty = "gearsBAD_icon",
		woodwork = "woodwork_icon",
		woodworkHard = "woodwork_icon",
		plumbing = "maintenance_icon",
		plumbingHard = "maintenance_icon",
		machinery = "mechanics_icon",
		machineryHard = "mechanics_icon",
		metalwork = "metalwork_icon",
		metalworkHard = "metalwork_icon",
		electrical = "electrical_icon",
		electricalHard = "electrical_icon",
	}
	return t[key]
end

local function getInvStatIcon(stat, cN, isImprov)
	local icon = getStatsIconsTable(stat)
	if isImprov and not icon then icon = getStatsIconsTable(LSInventionDefs.Improvements[cN][stat].defs); end
	if not icon then return ""; end
	return "<IMAGE:media/ui/"..icon..".png,16,16>"
end


local function getOtherInvStats(data, cN)
	-- repair penalty - deliver in percentage
	local t = {}
	local text = "IGUI_Inventions_"
	local cost = data['costPenalty']/data['costDecrease']
	local productionCost = (cost/data['standardization'])/(data['costDefs'][3])
	local repairCost = (cost/data['standardization'])/(data['costDefs'][3]*2)
	
	t.researchModifier = {LSUtil.getPercentage(1,cost,3,true),"bad",text}
	t.productionModifier = {LSUtil.getPercentage(1,productionCost,3,true),"bad",text}
	t.repairPenalty = {LSUtil.getPercentage(1,repairCost,3,true),"bad",text}

	text = "IGUI_Inventions_"..cN.."_"

	if data['efficiency'] then
		t.efficiencyTotal = {LSUtil.getPercentage(1,data['efficiency'],3,false),"good0",text}
	end

	if data['cooldownTime'] then
		local cTotal = LSInventionDefs.Items[cN]['cooldownTime']
		local cTime = data['cooldownTime']-cTotal
		t.cooldownTimeTotal = {LSUtil.getPercentage(cTotal,cTime,3,false),"bad",text}
	end

	if data['durability'] then
		if type(data['durability']) == "table" then
			for n=1,#data['durability'] do
				t['breakDownChance'..n] = {data['durability'][n],"bad",text}
			end
		else
			t.breakDownChance1 = {data['durability'],"bad",text}
		end
	end
	
	return t
end

local function getInvColorsParams(key, value)
	local neutral = " <RGB:0.5,0.5,0.5>"
	local red = {" <RGB:0.6,0.4,0.4>", " <RGB:0.8,0.3,0.3>", " <RGB:1,0.2,0.2>"}
	local green = {" <RGB:0.4,0.6,0.4>", " <RGB:0.3,0.8,0.3>", " <RGB:0.2,1,0.2>"}
	local t = {
		bad = {{value==0, neutral},{value>=75, red[3]},{value>=25, red[2]},{value>0, red[1]},{value<=-75, green[3]},{value<=-25, green[2]},{value<0, green[1]}},
		bad0 = {{value<=0, green[3]},{value>=100, red[3]},{value>=80, red[2]},{value>=60, red[1]},{value>=40, neutral},{value>=20, green[1]},{value>0, green[2]}},
		good = {{value==0, neutral},{value>=75, green[3]},{value>=25, green[2]},{value>0, green[1]},{value<=-75, red[3]},{value<=-25, red[2]},{value<0, red[1]}},
		good0 = {{value<=0, red[3]},{value>=100, green[3]},{value>=80, green[2]},{value>=60, green[1]},{value>=40, neutral},{value>=20, red[1]},{value>0, red[2]}},
	}
	if not t[key] then return neutral; end
	for n=1,#t[key] do
		if t[key][n][1] then return t[key][n][2]; end
	end
	return neutral
end

local function resetTooltipDesc(t, oldDesc, newTxt, endTxt, name, num)
	local numTxt = " <BR>"
	if num then numTxt = " <SPACE>"..num.." <BR>"; end
	oldDesc = oldDesc..endTxt..getText("Tooltip_Inventions_"..name)..numTxt
	table.insert(t, oldDesc)
	local desc = newTxt
	return desc
end

function LSUtil.getInventionStatsParams(data, obj, objName, character, cN, cArgs)
	local tex = obj:getTextureName()
	--local footNote = "Tooltip_Inventions_RepairCostHint"
	local descTable = {}
	local endLine = " <BR><TEXT><CENTRE><RGB:0.8,0.8,0>"..getText("Tooltip_Inventions_NextPage")
	local tooltipTitle = "<H1><ORANGE>"..getText("Tooltip_Inventions_Stats").." <BR><IMAGECENTRE:"..tex..",64,64><LINE><TEXT><CENTRE>"
	local descMain = tooltipTitle..getText("Tooltip_Inventions_Stats2").." <BR><H2><CENTRE>"..objName.." <LINE><TEXT><RGB:0.5,0.5,0.5>"..getText("Tooltip_Inventions_"..cN.."_desc")
	local descImp = tooltipTitle.." <RGB:0.8,0.8,0>"..getText("Tooltip_Inventions_Improv").." <TEXT><BR>"
	local descOther = tooltipTitle.." <RGB:0.8,0.8,0>"..getText("Tooltip_Inventions_OtherStats").." <TEXT><BR>"
	if cArgs and cArgs[1] then descMain = descMain.." <BR>"..cArgs[1]; end

	local newDesc = descMain.." <LINE><LINE>"
	newDesc = resetTooltipDesc(descTable, newDesc, descImp, endLine, "Improv", false)
	local hasImprov
	local n, pg = 0, 1
	if cArgs and cArgs[2] then newDesc, n = newDesc..cArgs[2].." <LINE><LINE>", cArgs[3]; end
	
	for k, v in pairs(data['improvementData']) do
		if v and v[1] and v[2] and v[1] > 0 then
			if n > 7 then
				pg = pg+1
				newDesc = resetTooltipDesc(descTable, newDesc, descImp, endLine, "Improv", tostring(pg))
				n = 0
			end
			hasImprov = true
			local icon = getInvStatIcon(k, cN, true)
			local startLine, middleLine = icon.." <TEXT>", " ("..tostring(v[1])..")"
			if v[2] == 1 then middleLine = ""; end
			if v[1] == v[2] then startLine, middleLine = startLine.." <GREEN>", middleLine.." <SPACE><IMAGE:media/ui/okay_icon.png,16,16>"; end	
			newDesc = newDesc..startLine..getText("IGUI_Inventions_"..cN.."_"..k)..middleLine.." <RGB:0.5,0.5,0.5><LINE>"..getText("IGUI_Inventions_"..cN.."_"..k.."_desc").." <LINE><LINE>"
			n = n+1
		end
	end
	if not hasImprov then newDesc = newDesc.." <CENTRE>"..getText("Tooltip_Inventions_NoImprovs").." <LINE>"; end
	
	newDesc = resetTooltipDesc(descTable, newDesc, descOther, endLine, "OtherStats", false)
	n, pg = 0, 1
	if cArgs and cArgs[4] then newDesc, n = newDesc..cArgs[4].." <LINE><LINE>", cArgs[5]; end
	
	local othersList = getOtherInvStats(data['inventionData'], cN)
	for k, v in pairs(othersList) do
		if n > 7 then
			pg = pg+1
			newDesc = resetTooltipDesc(descTable, newDesc, descOther, endLine, "OtherStats", tostring(pg))
			n = 0
		end
		local color = getInvColorsParams(v[2],v[1])
		local percentage = color..tostring(v[1]).."%"
		local icon = getInvStatIcon(k, cN, false)
		local startLine = icon.." <TEXT>"
		newDesc = newDesc..startLine..getText(v[3]..k)..": <SPACE>"..percentage.." <RGB:0.5,0.5,0.5><LINE>"..getText(v[3]..k.."_desc").." <LINE><LINE>"
		n=n+1
	end

	newDesc = resetTooltipDesc(descTable, newDesc, descMain, endLine, "MainPage", false)
	
	return descTable
end

---------- Inventions (items)

function LSUtil.getInventionItemData(item) --!
	if not item or not instanceof(item, "InventoryItem") then return false; end
	local data = item:getModData() and item:getModData().movableData
	return invData and invData['inventionData']
end

function LSUtil.inventionItemHasUses(item, data) --!
	return item:isInPlayerInventory() and data['fuelUses'] and data['fuelUses'] > 0
end

function LSUtil.inventionItemGetUses(item, data) --!
	if not LSUtil.inventionItemHasUses(item, data) then return 0; end
	return data['fuelUses']
end

function LSUtil.useInventionItem(item, data) --!
	if not LSUtil.inventionItemHasUses(item, data) then return; end
	data['fuelUses'] = math.max(0,math.floor(data['fuelUses']-1))
	LSUtil.debugPrint("---- LS - called LSUtil.useInventionItem, for item: "..item:getFullType()..", new fuel uses is: "..tostring(data['fuelUses']).." ----")
end

function LSUtil.drainInventionIem(item, data) --!
	if not LSUtil.inventionItemHasUses(item, data) then return; end
	data['fuelUses'] = 0
	LSUtil.debugPrint("---- LS - called LSUtil.drainInventionIem, for item: "..item:getFullType().." ----")
end

---------- Hygiene

local function hygieneGetMinDaysSurvived()
	local lsData = ModData.getOrCreate("LSDATA")
	if lsData and lsData["SO"] and lsData["SO"]["HNE"] then return lsData["SO"]["HNE"]; end
	return 3
end

function LSUtil.isHygieneExpected(data, character)
	if not data.hygieneNeedETime then data.hygieneNeedETime = hygieneGetMinDaysSurvived(); end
	return data.hygieneNeedETime and tonumber(character:getHoursSurvived())/24 > data.hygieneNeedETime
end

function LSUtil.isValidHygiene(data)
	if not data then return false; end
	if not SandboxVars.Text.DividerHygiene or not data.hygieneNeed then data.hygieneNeed = 50; return false; end
	return true
end

function LSUtil.addHygiene(data, character, val, valMax)
	if not LSUtil.isValidHygiene(data) then return; end
	valMax = valMax or 0
	data.hygieneNeed = math.max(valMax, data.hygieneNeed-val)
	LSUtil.doSimpleArrowHalo(character, " + "..getText("IGUI_HaloNote_Hygiene"), true)
end

function LSUtil.reduceHygiene(data, character, val, valMin)
	if not LSUtil.isValidHygiene(data) then return; end
	valMin = valMin or 100
	if not LSUtil.isHygieneExpected(data, character) then valMin = 50; end
	data.hygieneNeed = math.min(valMin, data.hygieneNeed+val)
	LSUtil.doSimpleArrowHalo(character, " - "..getText("IGUI_HaloNote_Hygiene"), false)
end

---------- Lifestyle Moodles

function LSUtil.isValidMoodle(data, moodle)
	return data and data[moodle] and data[moodle].Value
end

function LSUtil.moodleIsSingleText(data, moodle)
	return data[moodle].Icon == 2 or data[moodle].Icon == 3 or (data[moodle].Tiers == 0 and data[moodle].Icon == 0)
end

function LSUtil.setMoodleValue(data, moodle, val, opposite, doHalo)
	if not LSUtil.isValidMoodle(data, moodle) then return; end
	data[moodle].Value = val
	if doHalo then 
		local level = "L"..tostring(math.max(1, data[moodle].Level))
		if LSUtil.moodleIsSingleText(data, moodle) then level = "L1"; end -- 1 text
		LSUtil.doSimpleArrowHalo(doHalo, getText("Moodles_"..data[moodle].name..level), data[moodle].Alignment == "Good")
	end
	if opposite then LSUtil.setMoodleValue(data, opposite, 0, false, false); end
end

function LSUtil.addMoodleValue(data, moodle, val, opposite, doHalo, force)
	if opposite and not force and LSUtil.isValidMoodle(data, opposite) and data[opposite].Value > 0 then LSUtil.reduceMoodleValue(data, opposite, val, moodle, doHalo, true); return; end
	if not LSUtil.isValidMoodle(data, moodle) then return; end
	data[moodle].Value = math.min(1, data[moodle].Value+val)
	if doHalo then 
		local level = "L"..tostring(math.max(1, data[moodle].Level))
		if LSUtil.moodleIsSingleText(data, moodle) then level = "L1"; end -- 1 text
		LSUtil.doSimpleArrowHalo(doHalo, " + "..getText("Moodles_"..moodle..level), data[moodle].Alignment == "Good")
	end
	if opposite then LSUtil.setMoodleValue(data, opposite, 0, false, false); end
end

function LSUtil.reduceMoodleValue(data, moodle, val, opposite, doHalo, overVal)
	if not LSUtil.isValidMoodle(data, moodle) then return; end
	local level = "L"..tostring(math.max(1, data[moodle].Level))
	local result = data[moodle].Value-val
	data[moodle].Value = math.max(0, result)
	if doHalo then 
		if LSUtil.moodleIsSingleText(data, moodle) then level = "L1"; end -- 1 text
		LSUtil.doSimpleArrowHalo(doHalo, " - "..getText("Moodles_"..data[moodle].name..level), data[moodle].Alignment == "Bad")
	end
	if overVal and result <= -0.2 then LSUtil.addMoodleValue(data, opposite, -result, false, doHalo, false); end
end

-------------- Sounds
LSUtil.loopedSounds = false --!
local stopLoopedSounds = function() --!
	if not LSUtil.loopedSounds then Events.EveryOneMinute.Remove(stopLoopedSounds); return; end
	for n=#LSUtil.loopedSounds, 1, -1 do
		local args = LSUtil.loopedSounds[n]
		args[3] = args[3]-1
		if args[3] <= 0 then
			if args[1][args[4]](args[1], args[2]) then
				args[1][args[5]](args[1], args[2])
			end
			table.remove(LSUtil.loopedSounds, n)
		end
	end
	if #LSUtil.loopedSounds == 0 then LSUtil.loopedSounds = false; Events.EveryOneMinute.Remove(stopLoopedSounds); end
end

function LSUtil.playSoundCharacter(character, soundName, soundVar, loopMins, transmit, proxy, args) --!
--soundVar(float or false) -- if float and higher than 1 then ZombRands soundVar and add it as string at the end of soundName
--loopMins(float or false) -- if float and higher than 1 then sound is looped, creates per minute event and sound ends when game minute count is reached; if 0 then it never ends (end has to be handled elsewhere)
--transmit(boolean) -- if true then uses playSound, otherwise uses playSoundImpl
--proxy(IsoObject or false) -- IsoObject proxy
--args(table or false) -- sound args - volume, pitch
	local soundFunc = (transmit and "playSound") or "playSoundImpl"
	local emitter = character:getEmitter()
	local sound = emitter[soundFunc](emitter, proxy)
	if args then
		if args[1] then sound:setVolume(args[1]); end
		if args[2] then sound:setPitch(args[2]); end
	end
	if loopMins and loopMins > 1 then
		if not LSUtil.loopedSounds then
			LSUtil.loopedSounds = {}
			table.insert(LSUtil.loopedSounds, {emitter,sound,loopMins,'isPlaying','stopSound'})
			Events.EveryOneMinute.Add(stopLoopedSounds)
		else
			table.insert(LSUtil.loopedSounds, {emitter,sound,loopMins,'isPlaying','stopSound'})
		end
	end
end

-------------- Debug

function LSUtil.debugPrint(text) --!
	if not isDebugEnabled() or not SandboxVars.Debug.LSVerbose then return; end
	print(text)
end

--[[
local function findItemsLoot(thisPlayer, ItemName)

	local Item
	local containerList = ArrayList.new();
	local playerNum = thisPlayer and thisPlayer:getPlayerNum() or -1
    for i,v in ipairs(getPlayerInventory(playerNum).inventoryPane.inventoryPage.backpacks) do
		containerList:add(v.inventory);
    end
    for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
		containerList:add(v.inventory);
    end

	for i=0,containerList:size()-1 do
		local container = containerList:get(i);
		for x=0,container:getItems():size() - 1 do
			local v = container:getItems():get(x);
			if not Item and (v:getType() == ItemName) then
				Item = v
				break
			end
		end
	end

	return Item

end

function LSUtil.findItems(thisPlayer, itemNameList)

    local inventory = thisPlayer:getInventory();
	local it = inventory:getItems();
	local item

	for j = 0, it:size()-1 do
		item = it:get(j);
		for k, v in pairs(itemNameList) do
			if (not v.id) and (v.name == item:getType()) then
				v.id = item
				break
			end
		end
	end

	for k, v in pairs(itemNameList) do
		if not v.id then
			v.id = findItemsLoot(thisPlayer, v.name)
		end
	end

	return itemNameList

end
]]--