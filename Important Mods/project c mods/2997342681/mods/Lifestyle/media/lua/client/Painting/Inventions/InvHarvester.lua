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
-- InvHarvester.harvestLimit must be defined might remaining uses
-- timedaction - each plant harvested adds to how much fuel is drained, if fuel reaches a very small percentage then it is replaced by empty version;
-- timedAction - maxTime is static (for anim control), but number of loop repetitions is defined by a base amount + plant number, increasing the time it takes the more plants there are to harvest (up to a limit)

require "ISUI/ISInventoryPane"

InvHarvester = {};
InvHarvester.harvestLimit = 20

function predicatePetrol(item)
	return (item:hasTag("Petrol") or item:getType() == "PetrolCan") and (item:getDrainableUsesInt() > 0)
end

local function predicatePetrolNotFull(item)
	return (item:hasTag("Petrol") or item:getType() == "PetrolCan") and item:getUsedDelta() < 1 
end

local function isHarvesterEmpty(item)
	return item:getDrainableUsesInt() < 1
end

local function isHarvesterNotFull(item)
	return item:getUsedDelta() < 1
end

local function getValidPlants(character)
	local t
	local playerX, playerY = character:getX(), character:getY()
	--local cellSqr = getCell():getGridSquare(character:getX(), character:getY(), character:getZ())
  	for x = playerX-6,playerX+6 do
		for y = playerY-6,playerY+6 do
			local square = getCell():getGridSquare(x,y,character:getZ())
			if square then
				local plant = CFarmingSystem.instance:getLuaObjectOnSquare(square)
				if plant and plant:canHarvest() then
					if not t then t = {}; end
					local plantData = {plant, false}
					table.insert(t, plantData)
					if #t >= InvHarvester.harvestLimit then break; end
				end
			end
		end
	end
	return t
end

local function getHarvestRGB(plantsNum)
	local rgbColor = " <RGB:1,0,0>"
	local t = {
		[math.floor(InvHarvester.harvestLimit*0.75)] = " <RGB:0,1,0>",
		[math.floor(InvHarvester.harvestLimit*0.5)] = " <RGB:1,1,0>",
		[math.floor(InvHarvester.harvestLimit*0.2)] = " <RGB:1,0.5,0>",
	}
	for k, v in pairs(t) do
		if plantsNum >= k then rgbColor = v; break; end
	end
	return rgbColor
end

local function doHarvestOption(parentMenu, character, harvester, plants)
	local plantsNum = (plants and #plants) or 0
	local rgb = getHarvestRGB(plantsNum)
	local option, text = parentMenu:addOption(getText("ContextMenu_InvHarvester_Harvest"), false, InvHarvester.onAction, character, harvester),
	getText("Tooltip_InvHarvester_Harvest").." <LINE>"..rgb..plantsNum.." <RGB:1,1,1>".."/"..InvHarvester.harvestLimit.." <SPACE>"..getText("Tooltip_InvHarvester_HarvestLimit")
	option.toolTip = LSUtil.getSimpleTooltip(text)
	option.iconTexture = getTexture('media/ui/gears_icon.png')
end

local function doRefillOption(parentMenu, character, harvester, tex)
	if not isHarvesterNotFull(harvester) then LSUtil.getDummyOption(parentMenu, getText("ContextMenu_InvHarvester_Refill"), getText("Tooltip_InvHarvester_Full"), tex, "addOption", true); return; end
	local playerInv = character:getInventory()
	if not playerInv:containsEvalRecurse(predicatePetrol) then LSUtil.getDummyOption(parentMenu, getText("ContextMenu_InvHarvester_Refill"), getText("Tooltip_InvHarvester_NoPetrol"), tex, "addOption", true); return; end
	local option = parentMenu:addOption(getText("ContextMenu_InvHarvester_Refill"), false, InvHarvester.onRefillAction, character, harvester)
	option.toolTip = LSUtil.getSimpleTooltip("Tooltip_InvHarvester_Petrol")
	option.iconTexture = tex
end

InvHarvester.doInventoryMenu = function(player, context, items, harvester)
	-- Conditions
	if not LSUtil.isValidInvItem(harvester) then return; end
	--if isHarvesterEmpty(harvester) then return; end -- recipe handles refilling / empty is a different item - handles like paint palette
    local character = LSUtil.getValidPlayer(player)
	if LSUtil.isCharBusy(character) then return; end
	if LSUtil.isCharSitting(character, character:getModData()) then return; end --! remove data for B42 !!!!!!!!!!!!!!!!!
	-- Main Option
	local buildOption = LSUtil.getDummyOption(context, harvester:getDisplayName(), getText("Tooltip_InvHarvester"), harvester:getTexture(), "addOptionOnTop", false)
	-- Submenu
	local subMenu = ISContextMenu:getNew(context);
	context:addSubMenu(buildOption, subMenu)
	-- Harvest and Refill Options
	-- Refill
	local gasIcon = getTexture('media/ui/vehicles/vehicle_add_gas.png')
	doRefillOption(subMenu, character, harvester, gasIcon)
	if isHarvesterEmpty(harvester) then return; end
	-- Harvest
	local plants = getValidPlants(character)
	if not plants then LSUtil.getDummyOption(subMenu, getText("ContextMenu_InvHarvester_Harvest"), getText("Tooltip_InvHarvester_NoHarvest"), getTexture('media/ui/gearsBAD_icon.png'), "addOption", true); return; end
	doHarvestOption(subMenu, character, harvester, plants)
end

local function doItemTransfer(player, targetItem)

	if instanceof(targetItem, "InventoryItem") then
		if luautils.haveToBeTransfered(player, targetItem) then
			ISTimedActionQueue.add(ISInventoryTransferAction:new(player, targetItem, targetItem:getContainer(), player:getInventory()))
		end
		return true
	elseif instanceof(targetItem, "ArrayList") then
		local items = targetItem
		for i=1,items:size() do
			local item = items:get(i-1)
			if luautils.haveToBeTransfered(player, item) then
				ISTimedActionQueue.add(ISInventoryTransferAction:new(player, item, item:getContainer(), player:getInventory()))
			end
		end
		return true
	end
	return false
end

local function doItemEquip(character, item, primary, twoHands)
	if instanceof(item, "InventoryItem") and character:getPrimaryHandItem() ~= item then
		ISTimedActionQueue.add(ISEquipWeaponAction:new(character, item, 50, primary, twoHands))
	end
end

InvHarvester.onRefillAction = function(worldobjects, character, item)
	-- Check conditions again
	if not LSUtil.isValidInvItem(item) then return; end
	if not isHarvesterNotFull(item) then return; end
	if LSUtil.isCharBusy(character) then return; end
	if LSUtil.isCharSitting(character, character:getModData()) then return; end --! remove data for B42 !!!!!!!!!!!!!!!!!
	-- Get Petrol Again
	local playerInv = character:getInventory()
	if not playerInv:containsEvalRecurse(predicatePetrol) then return; end
	local petrolCan = playerInv:getFirstEvalRecurse(predicatePetrol)

	local timedAction = require "TimedActions/LSAddFuelAction"
	if item and doItemTransfer(character, item) and doItemTransfer(character, petrolCan) then
		doItemEquip(character, petrolCan, true, false)
		ISTimedActionQueue.add(timedAction:new(character, item, petrolCan, 70 + math.min(petrolCan:getUsedDelta() * 40, 10/math.max(0.001,item:getUsedDelta()))));
	end
end

InvHarvester.onAction = function(worldobjects, character, item)
	-- Check conditions again
	if not LSUtil.isValidInvItem(item) then return; end
	if isHarvesterEmpty(item) then return; end -- recipe handles refilling / empty is a different item - handles like paint palette
	if LSUtil.isCharBusy(character) then return; end
	if LSUtil.isCharSitting(character, character:getModData()) then return; end --! remove data for B42 !!!!!!!!!!!!!!!!!
	-- Get Plants Again (protection from cheesing)
	local plants = getValidPlants(character)
	if not plants then return; end

	local timedAction = require "TimedActions/LSInvHarvesterAction"
	if item and doItemTransfer(character, item) then
		doItemEquip(character, item, true, true)
		ISTimedActionQueue.add(timedAction:new(character, item, plants));
	end
end
]]--