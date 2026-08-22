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

PaintItemContextMenu = {}
--PaintItemContextMenu.Colors = {"random","black","blue","cyan","darkGray","darkGreen","gray","green","lightGray","lightGreen","magenta","orange","pink","purple","red","white","yellow","transparent"}
PaintItemContextMenu.Textures = {"Red","Green","Blue","ScrapA","ScrapB","ScrapC"}

local function doRetextureOptions(parentMenu, character, item)
	local defaultOption = parentMenu:addOption("Default", character, PaintItemContextMenu.onRetexture, item, "")
	local randomOption = parentMenu:addOption("Random", character, PaintItemContextMenu.onRetexture, item, PaintItemContextMenu.Textures[ZombRand(#PaintItemContextMenu.Textures)+1])
	for n=1, #PaintItemContextMenu.Textures do
		parentMenu:addOption(PaintItemContextMenu.Textures[n], character, PaintItemContextMenu.onRetexture, item, PaintItemContextMenu.Textures[n])
	end
end

PaintItemContextMenu.doInventoryMenu = function(player, context, items, item)
	-- Conditions
	if not isAdmin() and not isDebugEnabled() then return; end
	local character = LSUtil.getValidPlayer(player)
	if not character then return; end
	if item:isBroken() or item:getContainer() ~= character:getInventory() then return; end
	-- Get Textures
	--local texList = getTextures(item)
	--if #texList == 0 then return; end
	-- Main Option
	local buildOption = LSUtil.getDummyOption(context, "Change Texture", false, false, "addOptionOnTop", false)
	-- Submenu
	local subMenu = ISContextMenu:getNew(context);
	context:addSubMenu(buildOption, subMenu)
	-- Retexture Option
	doRetextureOptions(subMenu, character, item)
	-- Retexture Option
	--for n=1,#texList do
	--	doRetextureOption(subMenu, item, texList[n])
	--end
end

PaintItemContextMenu.onRetexture = function(character, item, cat) --b41
	-- Check conditions again
	if item:isBroken() or item:getContainer() ~= character:getInventory() then return; end
	-- get item base type
	local baseType = item:getType()
	for n=1,#PaintItemContextMenu.Textures do
		if luautils.stringEnds(baseType, PaintItemContextMenu.Textures[n]) then
			baseType = baseType:gsub(PaintItemContextMenu.Textures[n].."$", "")
			break
		end
	end
	-- get new item
	local newItem = InventoryItemFactory.CreateItem("Lifestyle."..baseType..cat)
	-- do Retexture
	character:getInventory():DoRemoveItem(item)
	character:getInventory():AddItem(newItem)
	character:getInventory():setDrawDirty(true)
	getPlayerInventory(character:getPlayerNum()):refreshBackpacks()
end
