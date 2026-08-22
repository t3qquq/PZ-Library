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

-- DEPRECATED
--[[
require "Properties/Player/CleaningSkill"

local function doCleaningItemTransfer(player, CleaningItem)

	if instanceof(CleaningItem, "InventoryItem") then
		if luautils.haveToBeTransfered(player, CleaningItem) then
			ISTimedActionQueue.add(ISInventoryTransferAction:new(player, CleaningItem, CleaningItem:getContainer(), player:getInventory()))
		end
		return true
	elseif instanceof(CleaningItem, "ArrayList") then
		local items = CleaningItem
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

local function TransferCleaningItems(player, CleaningItem, CleaningItem2)

	if CleaningItem and not CleaningItem2 then
		if doCleaningItemTransfer(player, CleaningItem) then return true; end
	end
	if CleaningItem and	CleaningItem2 then
		if doCleaningItemTransfer(player, CleaningItem) and doCleaningItemTransfer(player, CleaningItem2) then return true; end
	end

	return false
end

local function CheckForCleaningItemsLoot(thisPlayer, BroomItem, MopItem, BleachItem)

	local BleachBucketItem

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
			if not BleachBucketItem and (v:getFullType() == 'Lifestyle.BucketBleachFull') then
				BleachBucketItem = v
			end
			if not BleachItem and (v:getType() == "Bleach" and v:getFullType() ~= "BleachEmpty" and v:isPoison()) then
				BleachItem = v
			end
			if not BroomItem and (v:getType() == "Broom" and not v:isBroken()) then
				BroomItem = v
			end
			if not MopItem and (v:getType() == "Mop" and not v:isBroken()) then
				MopItem = v
			end
			if BroomItem and MopItem and BleachItem and BleachBucketItem then
				break
			end
		end
	end

	if BleachBucketItem then BleachItem = BleachBucketItem; end

	return BroomItem, MopItem, BleachItem

end

local function CheckForCleaningItems(thisPlayer)

    local inventory = thisPlayer:getInventory();
	local it = inventory:getItems();
	local BroomItem, MopItem, BleachItem, BleachBucketItem

	for j = 0, it:size()-1 do
		local item = it:get(j);
		if item:getType() == "Broom" and not item:isBroken() and not BroomItem then
			BroomItem = item
		elseif item:getType() == "Mop" and not item:isBroken() and not MopItem then
			MopItem = item
		elseif item:getType() == "Bleach" and item:getFullType() ~= "BleachEmpty" and item:isPoison() then
			BleachItem = item
		elseif item:getFullType() == "Lifestyle.BucketBleachFull" and item:getFullType() ~= "BucketEmpty" and item:isPoison() then
			BleachBucketItem = item
		end
		if BroomItem and MopItem and BleachItem and BleachBucketItem then
			break
		end
	end

	if BleachBucketItem then BleachItem = BleachBucketItem; end

	if (not BroomItem) or (not MopItem) or (not BleachItem) then
		BroomItem, MopItem, BleachItem = CheckForCleaningItemsLoot(thisPlayer, BroomItem, MopItem, BleachItem)
	end

	return BroomItem, MopItem, BleachItem

end

local function walkToDirtTile(thisPlayer, dirtObject, hasBlood)
	
	local thisSquare
	
	if hasBlood then
		thisSquare = dirtObject
	else
		thisSquare = dirtObject:getSquare()
	end
	
	if not thisSquare then
	--thisPlayer:Say("No sqr")
		return false
	end

	local freeSquare = (thisSquare:getS() or thisSquare:getE() or thisSquare:getW() or thisSquare:getN())

	if not freeSquare then
		if luautils.walkAdj(thisPlayer, thisSquare, true) then
		return true
		else
		return false
		end
	end

	--now we make the character move to the closest tile near the dirt - this will make the action more efficient and fluid than simply picking the first available tile
	local N
	local S
	local E
	local W

	if thisSquare:getS() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getS()) then
		if thisPlayer:getY() >= thisSquare:getS():getY() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getS()))
			return true
		else
			S = 1
		end
	end
	
	if thisSquare:getN() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getN()) then
		if thisPlayer:getY() <= thisSquare:getN():getY() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getN()))
			return true
		else
			N = 1
		end
	end

	if thisSquare:getE() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getE()) then
		if thisPlayer:getX() >= thisSquare:getE():getX() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getE()))
			return true
		else
			E = 1
		end
	end

	if thisSquare:getW() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getW()) then
		if thisPlayer:getX() >= thisSquare:getW():getX() then
			ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getW()))
			return true
		else
			W = 1
		end
	end

	if S then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getS()))
		return true
	elseif N then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getN()))
		return true
	elseif W then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getW()))
		return true
	elseif E then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getE()))
		return true
	end

--	if thisSquare:getS() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getS()) then
--		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getS()))
--		return true
--	elseif thisSquare:getE() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getE()) then
--		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getE()))
--		return true
--	elseif thisSquare:getW() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getW()) then
--		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getW()))
--		return true
--	elseif thisSquare:getN() and AdjacentFreeTileFinder.privTrySquare(thisSquare, thisSquare:getN()) then
--		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, thisSquare:getN()))
--		return true
--	end
	--thisPlayer:Say("returning false")
	return false

end

function LSIsCleanRoomCheck(isHeavy, originalSquare)
	local thisPlayer = getPlayer()
	--thisPlayer:Say("Starting Check")
	local heavyCleaning = isHeavy
	local sourceSquare = originalSquare
	if thisPlayer and thisPlayer:hasModData() and not
		thisPlayer:isDead() and not
		(thisPlayer:isSitOnGround()) and not
		(thisPlayer:isSneaking()) and not
		(thisPlayer:isRunning()) and not
		(thisPlayer:isSprinting()) then

		local playerData = thisPlayer:getModData()
		local DirtList = {}; HeavyList = {}; BloodTileList = {}
		local hasBlood = false
		--local sqr
		local BroomItem, MopItem, BleachItem = CheckForCleaningItems(thisPlayer)


			for x = sourceSquare:getX()-6,sourceSquare:getX()+6 do
				for y = sourceSquare:getY()-6,sourceSquare:getY()+6 do
					local square = getCell():getGridSquare(x,y,thisPlayer:getZ())
					if square and (sourceSquare:isOutside() == square:isOutside()) and square:getRoom() == sourceSquare:getRoom() then
						for i = 0,square:getObjects():size()-1 do------------------------------------
							local object = square:getObjects():get(i)
							if heavyCleaning and square:haveBlood() and MopItem and BleachItem then
								hasBlood = true
								table.insert(BloodTileList, square)
							elseif object then
								local attachedsprite = object:getAttachedAnimSprite()
								if object:getTextureName() and heavyCleaning and
								(luautils.stringStarts(object:getTextureName(), "overlay_messages") or 
								luautils.stringStarts(object:getTextureName(), "overlay_graffiti") or 
								--luautils.stringStarts(object:getTextureName(), "floors_burnt") or 
								luautils.stringStarts(object:getTextureName(), "overlay_blood") or 
								luautils.stringStarts(object:getTextureName(), "blood_floor")) then
									table.insert(HeavyList, object)
								elseif object:getTextureName() and
								(luautils.stringStarts(object:getTextureName(), "overlay_grime") or 
								luautils.stringStarts(object:getTextureName(), "brokenglass_") or 
								luautils.stringStarts(object:getTextureName(), "trash&junk") or 
								luautils.stringStarts(object:getTextureName(), "d_floorleaves") or 
								luautils.stringStarts(object:getTextureName(), "d_trash")) then
									table.insert(DirtList, object)
								elseif object:getOverlaySprite() and object:getOverlaySprite():getName() and heavyCleaning and
								(luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_messages") or 
								luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_graffiti") or 
								--luautils.stringStarts(object:getOverlaySprite():getName(), "floors_burnt") or 
								luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_blood") or 
								luautils.stringStarts(object:getOverlaySprite():getName(), "blood_floor")) then
									table.insert(HeavyList, object)
								elseif object:getOverlaySprite() and object:getOverlaySprite():getName() and
								(luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_grime") or 
								luautils.stringStarts(object:getOverlaySprite():getName(), "trash_") or 
								luautils.stringStarts(object:getOverlaySprite():getName(), "trash&junk") or 
								luautils.stringStarts(object:getOverlaySprite():getName(), "d_floorleaves") or 
								luautils.stringStarts(object:getOverlaySprite():getName(), "d_trash") or
								luautils.stringStarts(object:getOverlaySprite():getName(), "LS_Scraps")) then
									table.insert(DirtList, object)
								elseif attachedsprite then
									for n=1,attachedsprite:size() do
										local sprite = attachedsprite:get(n-1)
										if sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() and heavyCleaning and 
											(luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_messages") or 
											luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_graffiti") or 
											--luautils.stringStarts(sprite:getParentSprite():getName(), "floors_burnt") or 
											luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_blood") or 
											luautils.stringStarts(sprite:getParentSprite():getName(), "blood_floor")) then
											table.insert(HeavyList, object)
										elseif sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() and 
											(luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_grime") or 
											luautils.stringStarts(sprite:getParentSprite():getName(), "trash_") or 
											luautils.stringStarts(sprite:getParentSprite():getName(), "trash&junk") or 
											luautils.stringStarts(sprite:getParentSprite():getName(), "d_floorleaves") or 
											luautils.stringStarts(sprite:getParentSprite():getName(), "d_trash")) then
											table.insert(DirtList, object)
										end
									end
									
								end
							end
                        end
                    end
                end
				
            end

			local dirtObject

--			if #BloodTileList > 0 and MopItem and BleachItem then
--				for i,v in ipairs(BloodTileList) do
--					if thisPlayer and (thisPlayer:getX() >= v:getX() - 2 and thisPlayer:getX() <= v:getX() + 2 and
--					thisPlayer:getY() >= v:getY() - 2 and thisPlayer:getY() <= v:getY() + 2) then 
--						dirtObject = v
--						break
--					elseif thisPlayer and (thisPlayer:getX() >= v:getX() - 3 and thisPlayer:getX() <= v:getX() + 3 and
--					thisPlayer:getY() >= v:getY() - 3 and thisPlayer:getY() <= v:getY() + 3) then 
--						dirtObject = v
--						break
--					elseif thisPlayer and (thisPlayer:getX() >= v:getX() - 5 and thisPlayer:getX() <= v:getX() + 5 and
--					thisPlayer:getY() >= v:getY() - 5 and thisPlayer:getY() <= v:getY() + 5) then 
--						dirtObject = v
--						break
--					elseif thisPlayer and (thisPlayer:getX() >= v:getX() - 7 and thisPlayer:getX() <= v:getX() + 7 and
--					thisPlayer:getY() >= v:getY() - 7 and thisPlayer:getY() <= v:getY() + 7) then 
--						dirtObject = v
--						break
--					elseif thisPlayer and (thisPlayer:getX() >= v:getX() - 10 and thisPlayer:getX() <= v:getX() + 10 and
--					thisPlayer:getY() >= v:getY() - 10 and thisPlayer:getY() <= v:getY() + 10) then 
--						dirtObject = v
--						break
--					end
--				end

			if #BloodTileList > 0 and MopItem and BleachItem and isHeavy then
				local randomTile = ZombRand(#BloodTileList) + 1
				dirtObject = BloodTileList[randomTile]
			elseif #HeavyList > 0 and MopItem and BleachItem and isHeavy then
				local randomTile = ZombRand(#HeavyList) + 1
				dirtObject = HeavyList[randomTile]
			elseif #DirtList > 0 then
				local randomTile = ZombRand(#DirtList) + 1
				dirtObject = DirtList[randomTile]
			end

				
				if dirtObject then
					if ((heavyCleaning and MopItem and BleachItem and (#HeavyList > 0 or #BloodTileList > 0) and TransferCleaningItems(thisPlayer, MopItem, BleachItem)) or
					(BroomItem and (#DirtList > 0) and TransferCleaningItems(thisPlayer, BroomItem, false))) and walkToDirtTile(thisPlayer, dirtObject, hasBlood) then
					local square

					local duration
					
					if heavyCleaning and MopItem and BleachItem and (#HeavyList > 0 or #BloodTileList > 0) then
						if hasBlood then
							square = dirtObject
						else
							square = dirtObject:getSquare()
						end
						if not MopItem:isEquipped() then
						ISTimedActionQueue.add(ISEquipWeaponAction:new(thisPlayer, MopItem, 50, true, true))
						end
						duration = GetCleaningTime(thisPlayer, BleachItem)
						ISTimedActionQueue.add(CleanRoomAction:new(thisPlayer, MopItem, dirtObject, BleachItem, duration, square, sourceSquare, hasBlood))
					elseif BroomItem and #DirtList > 0 then
						square = dirtObject:getSquare()
						if not BroomItem:isEquipped() then
							ISTimedActionQueue.add(ISEquipWeaponAction:new(thisPlayer, BroomItem, 50, true, true))
						end
						duration = GetCleaningTime(thisPlayer, false)
						ISTimedActionQueue.add(CleanRoomAction:new(thisPlayer, BroomItem, dirtObject, false, duration, square, sourceSquare, false))
					end
					
					--if item:getType() == "DishCloth" or item:getType() == "BathTowel" then
						--if not item:isEquipped() then
						--	ISTimedActionQueue.add(ISEquipWeaponAction:new(thisPlayer, item, 50, true, false))
						----end
				end
			end

	end
end
]]--