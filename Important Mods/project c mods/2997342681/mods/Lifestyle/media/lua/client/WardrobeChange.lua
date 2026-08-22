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

require "WardrobeFunctions"

local oldHotbarRI = ISHotbar.reloadIcons
--ISHotbar:reloadIcons()

function ISHotbar:reloadIcons()
	if self.chr and self.chr:getModData() and self.chr:getModData().ShowerEndUpdate then
		self.chr:getModData().ShowerEndUpdate = nil
		if self.chr:getModData().ShowerSlots and (#self.chr:getModData().ShowerSlots > 0) then
			LSrestoreHotbarAttachments(self.chr, self.chr:getModData().ShowerSlots)
		end
		self.chr:getModData().ShowerSlots = nil
	end
	
    oldHotbarRI(self)
end

function LSrestoreHotbarAttachments(player, itemsToMove)
    local hotbar = getPlayerHotbar(player:getPlayerNum())
    if not hotbar then return; end
	for _, item in pairs(itemsToMove) do
	-- Move the item from the player's inventory back to the hotbar
		local slotName = item:getModData().beltSlot
		local slotDef = hotbar:getSlotDef(slotName)
		local slotIndex = -1
		if slotDef then
			for i, slot in pairs(hotbar.availableSlot) do
				if tostring(hotbar.availableSlot[i].slotType) == slotName then slotIndex = i; break; end
			end
			if slotIndex ~= -1 then
				hotbar:attachItem(item, slotDef.attachments[item:getAttachmentType()], slotIndex, slotDef, false)
			end
		end
		-- Clear item modData
		item:getModData().beltSlot = nil
	end
	ISInventoryPage.renderDirty = true
end

local function saveHotbarAttachments(player)
    local itemsToMove = {}-- Temporary table to store items
    local hotbar = getPlayerHotbar(player:getPlayerNum())
	if not hotbar then return; end
 	-- check if we need to remove item from the hotbar or attached model on the player
	for i, item in pairs(hotbar.attachedItems) do
		local slot = hotbar.availableSlot[i]
		--if (not slot) or (not hotbar:canBeAttached(slot, item)) or (not player:getInventory():contains(item)) or item:isBroken() then
		if item and slot then
			local slotDef = slot.def
			if slotDef.type then -- Check if the item is attached to the belt / slot types are in ISHotbarAttachDefinition
				-- Store the item in the temporary table for later
				item:getModData().beltSlot = tostring(slotDef.type)
				hotbar:removeItem(item, false)--item stays in player inventory
				table.insert(itemsToMove, item)
			end
		end
	end
	if itemsToMove and (type(itemsToMove) == "table") and (#itemsToMove > 0) then ISInventoryPage.renderDirty = true; end
    -- Return the temporarily stored items for use in restoration
    return itemsToMove
end

local function getCanRemoveItem(player, item)
	if not player:isEquippedClothing(item) then return false; end
	if item:isHidden() then return false; end
	--if item:getType() and ((item:getType() == "Belt") or (item:getType() == "Belt2") or (item:getType() == "HolsterDouble") or (item:getType() == "HolsterSimple")) then return false; end
	--if item:getCategory() and (item:getCategory() == "Bag") then return true; end
	--if item:getAttachmentsProvided() then return false; end
	return true
end

local function getAvailableStorageOptions()
	return {
		{CN="Wardrobe",GN="none"},
		{CN="Drawers",GN="none"},
		{CN="Rack",GN="none"},
		{CN="Clothes Stand",GN="none"},
		{CN="Locker",GN="Yellow Wall"},
		{CN="Locker",GN="Blue Wall"},
	}
end

local function isValidStorageType(properties)
	local isValidProp
	for k, prop in ipairs(getAvailableStorageOptions()) do
		if properties and ((properties:Val("CustomName") == prop.CN) or (prop.CN == "none")) and ((properties:Val("GroupName") == prop.GN) or (prop.GN == "none")) then
			isValidProp = true
			break
		end
	end
	return isValidProp
end

local function IsPlayerWearingIt(player,itemToBeWorn)
    local wornItems = player:getWornItems()
    for i=1,wornItems:size() do
        local item = wornItems:get(i-1):getItem()
        if item:getFullType() == itemToBeWorn and item:getBodyLocation() == itemToBeWorn:getBodyLocation() then
            return true
        end
    end
    return false
end

function ClothesAboutToChange(player, object, optiontype)	
	if object and optiontype and optiontype == "isBathNoLaundryStart" then

			local playerData = player:getModData()
			playerData.ShowerClothes = {}
			local inventory = player:getInventory()	
			local it = inventory:getItems();
			playerData.ShowerEndUpdate = nil
			playerData.ShowerSlots = saveHotbarAttachments(player)

			for j = 0, it:size()-1 do
				local item = it:get(j);
				if getCanRemoveItem(player, item) then
					table.insert(playerData.ShowerClothes, item)
				end
			end

			for _, item in ipairs(playerData.ShowerClothes) do

				player:getInventory():setDrawDirty(true);
				player:removeWornItem(item, false)
				triggerEvent("OnClothingUpdated", player)
			end

			getPlayerInventory(player:getPlayerNum()):refreshBackpacks()

	elseif object and optiontype and optiontype == "isBathNoLaundryEnd" then

		local playerData = player:getModData()

		if playerData.ShowerClothes and #playerData.ShowerClothes > 0 then
			local inventory = player:getInventory()	
			local it = inventory:getItems();
			for j = 0, it:size()-1 do
				local itemToBeWorn = it:get(j);
				for _, item in ipairs(playerData.ShowerClothes) do
					if (item == itemToBeWorn or item == itemToBeWorn:getClothingItem() or item == itemToBeWorn:getFullType()) and
						player:getInventory():contains(itemToBeWorn) and not player:isEquippedClothing(itemToBeWorn) then

						if (itemToBeWorn:getBodyLocation() ~= "" or (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "")) then
							if itemToBeWorn:getContainer() then
								itemToBeWorn:getContainer():setDrawDirty(true)
							end
							player:getInventory():AddItem(itemToBeWorn)
							if (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "") then
								player:setWornItem(itemToBeWorn:canBeEquipped(), itemToBeWorn);
								getPlayerInventory(player:getPlayerNum()):refreshBackpacks();
							else
							player:setWornItem(itemToBeWorn:getBodyLocation(), itemToBeWorn);
							end
							triggerEvent("OnClothingUpdated", player)
						end
					end
				end
			end
			--if playerData.ShowerSlots and (#playerData.ShowerSlots > 0) then restoreHotbarAttachments(player, playerData.ShowerSlots); end -- too soon to call here
			--playerData.ShowerSlots = nil
			if playerData.ShowerSlots then playerData.ShowerEndUpdate = true; end
			playerData.ShowerClothes = {}
		end

	elseif object and object:getContainer() then
	
	local destContainer
	
		if optiontype == "naked" then
		
			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local itemsToRemove = {}

			--we get the main container
			destContainer = object:getContainer()

			--we get nearby containers(like the second wadrobe obj or nearby dressers
			--getParent()
			local containerList = ArrayList.new();
			local playerNum = player and player:getPlayerNum() or -1
			--for i,v in ipairs(getPlayerInventory(playerNum).inventoryPane.inventoryPage.backpacks) do
			--	containerList:add(v.inventory);
			--end
			for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
				containerList:add(v.inventory);
			end


			for j = 0, it:size()-1 do
				local item = it:get(j);
				if player:isEquippedClothing(item) and (not item:isHidden()) then
					table.insert(itemsToRemove, item)
				end
			end

			for _, item in ipairs(itemsToRemove) do

				local capacity = destContainer:hasRoomFor(player, item)
				local allowed = destContainer:isItemAllowed(item)

				player:getInventory():setDrawDirty(true);
				player:removeWornItem(item, false)
				if capacity and allowed then
				destContainer:addItemOnServer(item);
				player:getInventory():DoRemoveItem(item);
				destContainer:setDrawDirty(true);
				destContainer:AddItem(item)
				else--no more capacity in main container or not allowed so we try nearby ones
				
					for i=0,containerList:size()-1 do
						local container = containerList:get(i);
						local parentObj = container:getParent()
						if parentObj ~= nil then
							local thisSprite = parentObj:getSprite()
							if thisSprite ~= nil then
								local properties = parentObj:getSprite():getProperties()
								if isValidStorageType(properties) then
									local capacityN = container:hasRoomFor(player, item)
									local allowedN = container:isItemAllowed(item)
									if capacityN and allowedN then
										container:addItemOnServer(item);
										player:getInventory():DoRemoveItem(item);
										container:setDrawDirty(true);
										container:AddItem(item)
										break
									end
								end
							end
						end
					end
				
				end

		--		if capacity and allowed then
		--		player:getInventory():DoRemoveItem(item);
		--		destContainer:setDrawDirty(true);
		--		destContainer:AddItem(item)
		--		end
				triggerEvent("OnClothingUpdated", player)
			end

			getPlayerInventory(player:getPlayerNum()):refreshBackpacks()

		elseif optiontype == "test" then
			destContainer = object:getContainer()
			local items = object:getContainer():getItems();
			for i=items:size()-1, 0, -1 do
				local itemToBeWorn = items:get(i);
				if itemToBeWorn and itemToBeWorn:getCategory() == "Clothing" and player:getWornItem(itemToBeWorn:getBodyLocation()) == itemToBeWorn then
					player:Say("Item is equipped")
				end
				if itemToBeWorn and itemToBeWorn:getCategory() == "Clothing" then
					if itemToBeWorn:getBodyLocation() ~= "" then
						itemToBeWorn:getContainer():setDrawDirty(true)
						destContainer:setHasBeenLooted(true);
						destContainer:removeItemOnServer(itemToBeWorn)
						destContainer:DoRemoveItem(itemToBeWorn)
						player:getInventory():AddItem(itemToBeWorn)
						player:setWornItem(itemToBeWorn:getBodyLocation(), itemToBeWorn);
						triggerEvent("OnClothingUpdated", player)
						--destContainer:Remove(itemToBeWorn)
					-- here we handle flating the mohawk!
						if player:getHumanVisual():getHairModel():contains("Mohawk") and (itemToBeWorn:getBodyLocation() == "Hat" or itemToBeWorn:getBodyLocation() == "FullHat") then
							player:getHumanVisual():setHairModel("MohawkFlat");
							player:resetModel();
						end
					end
				else
					player:Say("Item failed")
				end
			end
			
		else

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local itemsToRemove = {}
			local ClothesList
			
			if optiontype == "casual" then
				ClothesList = player:getModData().CasualClothes
			elseif optiontype == "formal" then
				ClothesList = player:getModData().FormalClothes
			elseif optiontype == "gym" then
				ClothesList = player:getModData().GymClothes
			elseif optiontype == "sleep" then
				ClothesList = player:getModData().SleepClothes
			elseif optiontype == "party" then
				ClothesList = player:getModData().PartyClothes
			elseif optiontype == "summer" then
				ClothesList = player:getModData().SummerClothes
			elseif optiontype == "winter" then
				ClothesList = player:getModData().WinterClothes
			elseif optiontype == "work" then
				ClothesList = player:getModData().WorkClothes
			elseif optiontype == "combat" then
				ClothesList = player:getModData().CombatClothes
			end

			--we get the main container
			destContainer = object:getContainer()

			--we get nearby containers(like the second wadrobe obj or nearby dressers
			--getParent()
			local containerList = ArrayList.new();
			local playerNum = player and player:getPlayerNum() or -1
			--for i,v in ipairs(getPlayerInventory(playerNum).inventoryPane.inventoryPage.backpacks) do
			--	containerList:add(v.inventory);
			--end
			for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
				containerList:add(v.inventory);
			end

			for j = 0, it:size()-1 do
				local itemToRemove = it:get(j);
				if player:isEquippedClothing(itemToRemove) and (not itemToRemove:isHidden()) then
					local isItEquipped = false
					for _, item in ipairs(ClothesList) do
						if item == itemToRemove or item == itemToRemove:getFullType() then
							isItEquipped = true
							break
						end
					end
					if isItEquipped == false then
						local item = itemToRemove
						table.insert(itemsToRemove, item)
					end
				end
			end

			getPlayerInventory(player:getPlayerNum()):refreshBackpacks()

			for _, item in ipairs(itemsToRemove) do

				local capacity = destContainer:hasRoomFor(player, item)
				local allowed = destContainer:isItemAllowed(item)

				player:getInventory():setDrawDirty(true);
				player:removeWornItem(item, false)
				if capacity and allowed then
				destContainer:addItemOnServer(item);
				player:getInventory():DoRemoveItem(item);
				destContainer:setDrawDirty(true);
				destContainer:AddItem(item)

				else--no more capacity in main container or not allowed so we try nearby ones
				
					for i=0,containerList:size()-1 do
						local container = containerList:get(i);
						local parentObj = container:getParent()
						if parentObj ~= nil then
							local thisSprite = parentObj:getSprite()
							if thisSprite ~= nil then
								local properties = parentObj:getSprite():getProperties()
								if isValidStorageType(properties) then
									local capacityN = container:hasRoomFor(player, item)
									local allowedN = container:isItemAllowed(item)
									if capacityN and allowedN then
										container:addItemOnServer(item);
										player:getInventory():DoRemoveItem(item);
										container:setDrawDirty(true);
										container:AddItem(item)
										break
									end
								end
							end
						end
					end
				
				end

		--		if capacity and allowed then
		--		player:getInventory():DoRemoveItem(item);
		--		destContainer:setDrawDirty(true);
		--		destContainer:AddItem(item)
	--			end
				triggerEvent("OnClothingUpdated", player)
			end

			destContainer = object:getContainer()
			
			containerList = ArrayList.new();
			--for i,v in ipairs(getPlayerInventory(playerNum).inventoryPane.inventoryPage.backpacks) do
			--	containerList:add(v.inventory);
			--end
			for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
				containerList:add(v.inventory);
			end

			local itemsEquipped = {}
			for i=0,containerList:size()-1 do
				local container = containerList:get(i);
				local parentObj = container:getParent()
				if parentObj ~= nil then
					local thisSprite = parentObj:getSprite()
					if thisSprite ~= nil then
						local properties = parentObj:getSprite():getProperties()
						if isValidStorageType(properties) then
							--for x=0,container:getItems():size() - 1 do
							for x=container:getItems():size()-1, 0, -1 do
								local itemToBeWorn = container:getItems():get(x);
								local isItemFromList = false
		
								for _, item in ipairs(ClothesList) do
									if item == itemToBeWorn or (itemToBeWorn and item == itemToBeWorn:getClothingItem()) and (not itemToBeWorn:isHidden()) then
										isItemFromList = true
									elseif itemToBeWorn and item == itemToBeWorn:getFullType() and (not itemToBeWorn:isHidden()) then
										isItemFromList = true
										local item = itemToBeWorn
										table.insert(ClothesList, item)
									end
								end

								if itemToBeWorn and isItemFromList == true then

									local isItemNotEquipped = true
					
									if #itemsEquipped > 0 then
										for _, item in ipairs(itemsEquipped) do
											if item == itemToBeWorn or item == itemToBeWorn:getClothingItem() or item == itemToBeWorn:getFullType() then
												isItemNotEquipped = false
												--player:Say("Already wearing one")
											end
										end
									end

									if (itemToBeWorn:getBodyLocation() ~= "" or (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "")) and isItemNotEquipped == true then
										if itemToBeWorn:getContainer() then
											itemToBeWorn:getContainer():setDrawDirty(true)
										end
										--destContainer:setDrawDirty(true)
											container:setHasBeenLooted(true);
											container:removeItemOnServer(itemToBeWorn)
											container:DoRemoveItem(itemToBeWorn)
										--player:getInventory():setDrawDirty(true)
											player:getInventory():AddItem(itemToBeWorn)
										if (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "") then
											player:setWornItem(itemToBeWorn:canBeEquipped(), itemToBeWorn);
											getPlayerInventory(player:getPlayerNum()):refreshBackpacks();
										else
											player:setWornItem(itemToBeWorn:getBodyLocation(), itemToBeWorn);
										end
										triggerEvent("OnClothingUpdated", player)
										--destContainer:Remove(itemToBeWorn)
						
										-- here we handle flating the mohawk!
										if player:getHumanVisual():getHairModel():contains("Mohawk") and (itemToBeWorn:getBodyLocation() == "Hat" or itemToBeWorn:getBodyLocation() == "FullHat") then
											player:getHumanVisual():setHairModel("MohawkFlat");
											player:resetModel();
										end
										local item = itemToBeWorn
											table.insert(itemsEquipped, item)
										end
										
								end
							end
						end
					end
				end
			end
			
--			local items = object:getContainer():getItems();
--			local itemsEquipped = {}
--			for i=items:size()-1, 0, -1 do
--				local itemToBeWorn = items:get(i);
--				local isItemFromList = false
--		
--				for _, item in ipairs(ClothesList) do
--					if item == itemToBeWorn or item == itemToBeWorn:getClothingItem() then
--						isItemFromList = true
--					elseif item == itemToBeWorn:getFullType() then
--						isItemFromList = true
--						local item = itemToBeWorn
--						table.insert(ClothesList, item)
--					end
--				end

				--if itemToBeWorn and isItemFromList == true and (itemToBeWorn:getCategory() == "Clothing" or itemToBeWorn:getDisplayCategory() == "Accessory" or itemToBeWorn:getDisplayCategory() == "Bag") then
	
	--		if itemToBeWorn and isItemFromList == true then
				
	--				local isItemNotEquipped = true
					
	--				if #itemsEquipped > 0 then
	--					for _, item in ipairs(itemsEquipped) do
	--						if item == itemToBeWorn or item == itemToBeWorn:getClothingItem() or item == itemToBeWorn:getFullType() then
	--							isItemNotEquipped = false
	--							player:Say("Already wearing one")
	--						end
	--					end
	--				end
					--if IsPlayerWearingIt(player,itemToBeWorn) then
					--if player:isEquippedClothing(itemToBeWorn) then
						--player:Say("Already wearing one")
	--				if (itemToBeWorn:getBodyLocation() ~= "" or (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "")) and isItemNotEquipped == true then
	--					if itemToBeWorn:getContainer() then
	--						itemToBeWorn:getContainer():setDrawDirty(true)
	--					end
	--					--destContainer:setDrawDirty(true)
	--					destContainer:setHasBeenLooted(true);
	--					destContainer:removeItemOnServer(itemToBeWorn)
	--					destContainer:DoRemoveItem(itemToBeWorn)
	--					--player:getInventory():setDrawDirty(true)
	--					player:getInventory():AddItem(itemToBeWorn)
	--					if (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "") then
	--						player:setWornItem(itemToBeWorn:canBeEquipped(), itemToBeWorn);
	--						getPlayerInventory(player:getPlayerNum()):refreshBackpacks();
	--					else
	--					player:setWornItem(itemToBeWorn:getBodyLocation(), itemToBeWorn);
	--					end
	--					triggerEvent("OnClothingUpdated", player)
						--destContainer:Remove(itemToBeWorn)
						
						
					-- here we handle flating the mohawk!
	--					if player:getHumanVisual():getHairModel():contains("Mohawk") and (itemToBeWorn:getBodyLocation() == "Hat" or itemToBeWorn:getBodyLocation() == "FullHat") then
	--						player:getHumanVisual():setHairModel("MohawkFlat");
	--						player:resetModel();
	--					end
	--					local item = itemToBeWorn
	--					table.insert(itemsEquipped, item)
	--				end
	--			else
	--				--player:Say("Item failed")
	--			end
	--		end
			
			inventory = player:getInventory()	
			it = inventory:getItems();
			itemsToRemove = {}

			destContainer = object:getContainer()

			--we get nearby containers(like the second wadrobe obj or nearby dressers
			--getParent()
			containerList = ArrayList.new();
			--for i,v in ipairs(getPlayerInventory(playerNum).inventoryPane.inventoryPage.backpacks) do
			--	containerList:add(v.inventory);
			--end
			for i,v in ipairs(getPlayerLoot(playerNum).inventoryPane.inventoryPage.backpacks) do
				containerList:add(v.inventory);
			end

			for j = 0, it:size()-1 do
				local itemToRemove = it:get(j);
				if (itemToRemove:getCategory() == "Clothing" or itemToRemove:getDisplayCategory() == "Accessory" or itemToRemove:getCategory() == "Bag") and not player:isEquippedClothing(itemToRemove) and (not itemToRemove:isHidden()) then
					local item = itemToRemove
					table.insert(itemsToRemove, item)
				end
			end

			for _, item in ipairs(itemsToRemove) do

				local capacity = destContainer:hasRoomFor(player, item)
				local allowed = destContainer:isItemAllowed(item)

				player:getInventory():setDrawDirty(true);
				if capacity and allowed then
				destContainer:addItemOnServer(item);
				player:getInventory():DoRemoveItem(item);
				destContainer:setDrawDirty(true);
				destContainer:AddItem(item)
				else--no more capacity in main container or not allowed so we try nearby ones
				
					for i=0,containerList:size()-1 do
						local container = containerList:get(i);
						local parentObj = container:getParent()
						if parentObj ~= nil then
							local thisSprite = parentObj:getSprite()
							if thisSprite ~= nil then
								local properties = parentObj:getSprite():getProperties()
								if isValidStorageType(properties) then
									local capacityN = container:hasRoomFor(player, item)
									local allowedN = container:isItemAllowed(item)
									if capacityN and allowedN then
										container:addItemOnServer(item);
										player:getInventory():DoRemoveItem(item);
										container:setDrawDirty(true);
										container:AddItem(item)
										break
									end
								end
							end
						end
					end



				end
				
--				if capacity and allowed then
--				player:getInventory():DoRemoveItem(item);
--				destContainer:setDrawDirty(true);
--				destContainer:AddItem(item)
--				end
				triggerEvent("OnClothingUpdated", player)
			end

		end
	end
end