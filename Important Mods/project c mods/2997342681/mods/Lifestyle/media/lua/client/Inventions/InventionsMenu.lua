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

InventionsMenu = InventionsMenu or {};

InventionsMenu.loadDefs = function(Invention, invDefs, improvDefs, data)
	local seen, changed = {}, false
	-- add missing data
	for k, v in pairs(invDefs) do
		seen[k] = true
		if not data['inventionData'][k] then data['inventionData'][k] = v; changed = true; end
	end
	-- remove unused data
	for k, v in pairs(data['inventionData']) do
		if not seen[k] then data['inventionData'][k] = nil; changed = true; end
	end
	-- clear table
	seen = {}
	-- add missing improvement data (if missing then it will always be level 0, but we need the keys for debug)
	for k, v in pairs(improvDefs) do
		seen[k] = true
		if not data['improvementData'][k] then
			local maxNum = 1 -- max value of 1 means inventionData uses result value
			if v and v.repeatable then maxNum = #v.repeatable; end
			data['improvementData'][k] = {0,maxNum}
			changed = true
		end
	end
	-- remove unused data
	for k, v in pairs(data['improvementData']) do
		if not seen[k] then data['improvementData'][k] = nil; changed = true; end
	end
	if changed then Invention:transmitModData(); end
	return data
end

InventionsMenu.resetData = function(worldobjects, Invention, data, customName)
	local invDefs = LSInventionDefs and LSInventionDefs.Items and LSInventionDefs.Items[customName]
	local improvDefs = LSInventionDefs and LSInventionDefs.Improvements and LSInventionDefs.Improvements[customName]
	data['inventionData'] = {}
	for k, v in pairs(invDefs) do
		data['inventionData'][k] = v
	end
	data['improvementData'] = {}
	for k, v in pairs(improvDefs) do
		local maxNum = 1
		if v and v.repeatable then maxNum = #v.repeatable; end
		data['improvementData'][k] = {0,maxNum}
	end
	Invention:transmitModData()
end

local function getOrCreateInventionData(Invention, invDefs, improvDefs)
	if not invDefs or not improvDefs then return false; end
	if not Invention:getModData().movableData then Invention:getModData().movableData = {}; end
	local data = Invention:getModData().movableData
	if not data then return false; end
	if not data['inventionData'] then data['inventionData'] = {}; end
	if not data['improvementData'] then data['improvementData'] = {}; end
	data = InventionsMenu.loadDefs(Invention, invDefs, improvDefs, data)
	return data
end

InventionsMenu.updateInvData = function(obj, CN)
	local customName = CN or LSUtil.getObjCustomName(obj)
	if not customName then return false; end
	local invDefs = LSInventionDefs and LSInventionDefs.Items and LSInventionDefs.Items[customName]
	local improvDefs = LSInventionDefs and LSInventionDefs.Improvements and LSInventionDefs.Improvements[customName]
	local data = getOrCreateInventionData(obj, invDefs, improvDefs)
	return data
end

InventionsMenu.hasInvData = function(obj)
	return obj:hasModData() and obj:getModData().movableData and obj:getModData().movableData['inventionData'] and obj:getModData().movableData['improvementData']
end

local function doDebugSubOptions(subMenu, worldobjects, character, Invention, data, invName, customName)
	-- reset
	local resetOption = subMenu:addOption("Reset Data",worldobjects,InventionsMenu.resetData,Invention,data,customName)
	-- edit
	local editOption = subMenu:addOption("Edit Values",worldobjects,InventionsMenu.editData,character,invName,Invention,data, customName)
	-- force fix
	if data['inventionData']['isBroken'] then
		local fixOption = subMenu:addOption("Fix",worldobjects,InventionsMenu.onFixObj,character,Invention,data,false,true)
	-- reset cooldown
	elseif LSUtil.isCooldown(data['inventionData']) then
		local fixOption = subMenu:addOption("Reset Cooldown",worldobjects,InventionsMenu.onResetCooldown,Invention,data)
	end
end

InventionsMenu.doBuildMenu = function(player, context, worldobjects, Invention, spriteName, customName, groupName, DebugOption)
	-- core
	local character = getSpecificPlayer(player)
	if LSUtil.isCharBusy(character) then return; end
	if not LSUtil.isValidObj(Invention, spriteName) then return; end
	-- data
	local data = InventionsMenu.updateInvData(Invention, customName)
	if not data then return; end
	-- main option
	local invName = LSUtil.getMoveableDisplayName("Invention", Invention, customName, groupName)
	local buildOption = context:addOptionOnTop(invName)
	buildOption.iconTexture = LSUtil.getObjTexture(spriteName, "E")
	local subMenu = ISContextMenu:getNew(context);
	context:addSubMenu(buildOption, subMenu)
	function subMenu:closeAll()
		local option = self:getIsVisible() and self.mouseOver and self.mouseOver ~= -1 and self.options and self.options[self.mouseOver]
		if option and option.notClose then return; end
		self:hideAndChildren()
		local isJoypad = JoypadState.players[self.player+1]
		local parent = self.parent
		if isJoypad and (parent == nil) then
			setJoypadFocus(self.player, self.origin)
		end
		while parent do
			parent:setVisible(false)
			if isJoypad and (parent.parent == nil) then
				setJoypadFocus(self.player, parent.origin)
			end
			parent = parent.parent
		end
	end
	-- sub options (repair, refill, leave it to specific files)
	if InventionsMenu[customName] then InventionsMenu[customName](context, subMenu, worldobjects, character, Invention, data, invName, spriteName); end
	-- debug stuff
	if not isAdmin() and not isDebugEnabled() then return; end
	local debugOption = subMenu:addOption("Debug Tools")
	local debugSubMenu = subMenu:getNew(subMenu);
	context:addSubMenu(debugOption, debugSubMenu)
	doDebugSubOptions(debugSubMenu, worldobjects, character, Invention, data, invName, customName)
end

InventionsMenu.editData = function(worldobjects, character, invName, Invention, data, customName)
	local playerNum = character:getPlayerNum()
	local width, height = 150, 50
	local newUI = LSDebugInventions:new((getPlayerScreenWidth(playerNum)-width)/2,(getPlayerScreenHeight(playerNum)-height)/2,width,height,invName, Invention, data, customName);
    newUI:initialise();
    newUI:addToUIManager()
end

InventionsMenu.onFixObj = function(worldobjects, character, obj, data, reqList, admin)
	-- add stuff
	if admin then
		data['inventionData']['isBroken'] = false
		data['inventionData']['cooldown'] = false
		obj:transmitModData()
		return
	end
	local timedAction = require "TimedActions/LSFixAction"
	local item = LSUtil.getItemAndEquip(character, 'Screwdriver', false, true, false)
	if item and reqList and LSUtil.walkToAdj(character, obj) then
		ISTimedActionQueue.add(timedAction:new(character, obj, reqList, 200));
	end
end

InventionsMenu.onResetCooldown = function(worldobjects, obj, data)
	data['inventionData']['cooldown'] = false
	obj:transmitModData()
end

InventionsMenu.onChangeStatsTooltip = function(worldobjects, tooltip)
	if not tooltip or not tooltip.descList or #tooltip.descList == 1 or (tooltip.owner and not tooltip.owner:isReallyVisible()) then return; end
	local n = 1
	if tooltip.descCurrent < #tooltip.descList then n = tooltip.descCurrent+1; end
	tooltip.description = tooltip.descList[n]
	tooltip.descCurrent = n
	tooltip:doLayout()
	getSoundManager():playUISound('UI_Note_Appear')
end

--[[
InventionsMenu.doInventoryMenu = function(player, context, items, invention) --!
	-- core
	if not LSUtil.isValidInvItem(invention) or invention:isBroken() then return; end
	local character = LSUtil.getValidPlayer(player)
	if not character then return; end
	local itemName = invention:getType()
	-- data
	local data = InventionsMenu.updateInvData(invention, itemName)
	if not data then return; end
	-- main option
	local invName = LSUtil.getMoveableDisplayName("Invention", invention, customName, groupName)
	local buildOption = context:addOptionOnTop(invName)
	buildOption.iconTexture = LSUtil.getObjTexture(spriteName, "E")
	local subMenu = ISContextMenu:getNew(context);
	context:addSubMenu(buildOption, subMenu)
	function subMenu:closeAll()
		local option = self:getIsVisible() and self.mouseOver and self.mouseOver ~= -1 and self.options and self.options[self.mouseOver]
		if option and option.notClose then return; end
		self:hideAndChildren()
		local isJoypad = JoypadState.players[self.player+1]
		local parent = self.parent
		if isJoypad and (parent == nil) then
			setJoypadFocus(self.player, self.origin)
		end
		while parent do
			parent:setVisible(false)
			if isJoypad and (parent.parent == nil) then
				setJoypadFocus(self.player, parent.origin)
			end
			parent = parent.parent
		end
	end
	-- sub options (repair, refill, leave it to specific files) --!
	if InventionsMenu[customName] then InventionsMenu[customName](context, subMenu, worldobjects, character, invention, data, invName, spriteName); end --!
	-- debug stuff
	if not isAdmin() and not isDebugEnabled() then return; end
	local debugOption = subMenu:addOption("Debug Tools")
	local debugSubMenu = subMenu:getNew(subMenu);
	context:addSubMenu(debugOption, debugSubMenu)
	doDebugSubOptions(debugSubMenu, worldobjects, character, invention, data, invName, customName) --!
end
]]--