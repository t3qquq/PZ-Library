require('keyBinding.lua');

-- Copied from ISUI/ISFirearmRadialMenu
local function predicateNotFullMagazine(item, magazineType)
	return (item:getType() == magazineType or item:getFullType() == magazineType) and item:getCurrentAmmoCount() < item:getMaxAmmo()
end

-- Copied from ISUI/ISFirearmRadialMenu
local function predicateFullestMagazine(item1, item2)
	return item1:getCurrentAmmoCount() - item2:getCurrentAmmoCount()
end

-- Based off of the ISFirearmRadialMenu and ISInventoryPaneContextMenu functionality. 
-- Seeks to replicate all of the steps and stopping conditions therein.
function ESMR_keyHandler(keynum)
	if keynum == getCore():getKey("ESMR Reload Magazine") then
		local player = getSpecificPlayer(0);
		if player == nil then return end

		local weapon = player:getPrimaryHandItem()
		if not weapon then return end
		if not instanceof(weapon, "HandWeapon") then return end
		if not weapon:isRanged() then return end
		
		if not weapon:getMagazineType() then return end
		
		local inventory = player:getInventory()
		local magazine = inventory:getBestEvalArgRecurse(predicateNotFullMagazine, predicateFullestMagazine, weapon:getMagazineType())
		if not magazine then return end
		
		ISInventoryPaneContextMenu.transferIfNeeded(player, magazine)

		if not (inventory:getCountTypeRecurse(magazine:getAmmoType()) > 0) then return end

		local ammoCount = ISInventoryPaneContextMenu.transferBullets(player, magazine:getAmmoType(), magazine:getCurrentAmmoCount(), magazine:getMaxAmmo())
		if ammoCount == 0 then return end
		
		ISTimedActionQueue.add(ISLoadBulletsInMagazine:new(player, magazine, ammoCount))
	end
end

-- Add to the game's Keybinding options menu to allow for remapping of the key used
local function ESMR_keyBindAdder()
	keyBinding[#keyBinding + 1] = { value = '[ESMR Keys]' }
	keyBinding[#keyBinding + 1] = { value = "ESMR Reload Magazine", key = Keyboard.KEY_Y }	
end

-- Register our keybind in the options menu
Events.OnGameBoot.Add(ESMR_keyBindAdder);

-- Register our keypress handler
Events.OnKeyPressed.Add(ESMR_keyHandler);