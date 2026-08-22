-----------------------------------------------------------------------------------------------------------------------------------------------------------
local TIsnd = {}
TIsnd.ISInventoryTransferAction = {}
TIsnd.ISDetachItemHotbar = {}
TIsnd.ISAttachItemHotbar = {}
TIsnd.ISEquipWeaponAction = {}
TIsnd.ISUnequipAction = {} 
TIsnd.ISWearClothing = {}
TIsnd.ISApplyBandage = {}
TIsnd.ISCleanBandage = {}
TIsnd.ISDisinfect = {}
TIsnd.ISStitch = {}
TIsnd.ISTakePillAction = {}
TIsnd.ISClearAshes = {}
TIsnd.ISRepairClothing = {}
TIsnd.ISTransferWaterAction = {}
TIsnd.ISRemovePatch = {}
TIsnd.ISUpgradeWeapon = {}
TIsnd.ISRemoveWeaponUpgrade = {}
TIsnd.ISRemoveGlass = {}
TIsnd.ISRemoveBullet = {}
TIsnd.ISCleanBurn = {}
TIsnd.ISScavengeAction = {}
TIsnd.ISClothingExtraAction = {}
TIsnd.ISEquipHeavyItem = {}
TIsnd.ISSplint = {}
TIsnd.ISInventoryPaneContextMenu = {}
TIsnd.ISDropWorldItemAction = {}

TIsnd.sound = nil
TIsnd.delay  = nil
local delayStart = 77

--cannette vide >>>     
--cannette pleine >> Pop Pop2 Pop3 
--bouteil plastic vide >>>    
-----------------------------------------------------------------------------------------------------------------------------------------------------------
local function TI_getSoundName(player,suFFixe)
-----------------------------------------------------------------------------------------------------------------------------------------------------------
	if player:getVehicle() then
		 return "inTIsnd"..suFFixe
	else
		 return "TIsnd"..suFFixe
	end
end    
----------------------------------------------------------------------------------------------------------------------------------------------------------- 
local function TI_getSoundItem(item,stat)
	if stat == "up" or stat == "down" or stat == "cloth" then suFFixe = "_TakingM" else suFFixe = "_Clothing" end --"transf"
	local itemType = item:getType()
	if itemType and stat ~= "cloth" then
		-------------------------------------------------------------------------------
		if     	luautils.stringStarts(itemType, 'Guitar') or
				itemType == "Violin" 				or 
			    itemType == "Banjo"  			  	then suFFixe = "_itemGuitar_"..stat		
		-------------------------------------------------------------------------------
		elseif 	itemType == "PetrolCan" 		  	then suFFixe = "_itemPetrolCan_"..stat 
		-------------------------------------------------------------------------------
		elseif  itemType == "SmashedBottle" 	  	then suFFixe = "_SmashedBottle_"..stat
		-------------------------------------------------------------------------------
		elseif  itemType == "Molotov" 		      	then suFFixe = "_itemMolotov_"..stat 
		-------------------------------------------------------------------------------
		-------------------------------------------------------------------------------
		elseif	string.contains(itemType,"Ripped")	or
				string.contains(itemType,"cigarette") or
				string.contains(itemType,"Bandage")	or
				string.contains(itemType,"eedle")	or
				itemType == "Sheet" 				then suFFixe = "_Clothing" 
		-------------------------------------------------------------------------------
		elseif  itemType == "WhiskeyWaterFull"  	or        
		   		itemType == "BeerWaterFull"     	or 
		   		itemType == "WineWaterFull"     	or 
		   		itemType == "WhiskeyPetrol"     	or 
		   		itemType == "WinePetrol" 	    	or 
		   		itemType == "Beer" 	   		    	or 
		   		itemType == "BeerBottle" 	    	or 
		   		itemType == "Wine" 	   		    	or
		   		itemType == "Wine2"  	   	    	or
		   		itemType == "WhiskeyFull"  	    	then suFFixe = "_itemMolotov_"..stat
		-------------------------------------------------------------------------------
		elseif  itemType == "PetrolBleachBottle"	or        
		   		itemType == "WaterBottlePetrol" 	or
		   		itemType == "PetrolPopBottle"  		then suFFixe = "_Water"
		-------------------------------------------------------------------------------
		elseif  itemType == "BeerEmpty"       		or 
				itemType == "EmptyJar"    			or
		 		itemType == "WhiskeyEmpty"    		or 
		 		itemType == "WineEmpty2"      		or 
		 		itemType == "WineEmpty"       		or
		 		itemType == "GlassTumbler"    		then suFFixe = "_GlassBottle"
		-------------------------------------------------------------------------------
		elseif 	itemType == "Saucepan" 			  	or
				itemType == "Pan" 				  	or 
			    itemType == "GridlePan" 		  	then suFFixe = "_itemPan_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "Plank" 			  	or 
				itemType == "MayonnaiseEmpty" 		or
				itemType == "BeerCanEmpty" 			or
				itemType == "PopEmpty" 				or
				itemType == "Pop2Empty" 			or
				itemType == "Pop3Empty" 			or
				itemType == "RemouladeEmpty" 		or
				itemType == "BucketEmpty" 			or
				itemType == "PopBottleEmpty" 		or
				itemType == "WaterBottleEmpty" 		or
				itemType == "BleachEmpty" 			or 
			    itemType == "SpearCrafted" 		  	or
				itemType == "EmptyPetrolCan" 		or
				itemType == "Nightstick" 			or
			    itemType == "AntiCrowbar"  			then suFFixe = "_TakingM"
		-------------------------------------------------------------------------------
		elseif 	itemType == "AntiLockpick" 			or
				itemType == "CarLockNeimanPart" 	or
				itemType == "CarLockDoorPart" 		or
				itemType == "PrintTool" 			or
				itemType == "PrintToolCar" 			or
				itemType == "CarKeyCutter" 			or
				itemType == "Key_Duplication_Machine"or
				itemType == "GPSdayz" 			  	or
				itemType == "GPSdestroyed" 			then suFFixe = "_WeaponPart"
		-------------------------------------------------------------------------------
		elseif 	itemType == "Machete" 			  	or 
			    itemType == "Katana"  			  	then suFFixe = "_itemKatana_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "BarBell" 	 		  	or
				itemType == "DumbBell"   		  	or
				itemType == "Wrench" 	 		  	or 
			    itemType == "PipeWrench" 		  	then suFFixe = "_itemWrench_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "BreadKnife"   		  	or
				itemType == "ButterKnife"  		  	or
				itemType == "Fork" 		   		  	or
				itemType == "IcePick" 	   		  	or
				itemType == "LetterOpener" 		  	or
				itemType == "TowHookCar" 		  	or
			    itemType == "Spoon" 	   		  	then suFFixe = "_itemMetalObj_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "HandScythe"   		  	or
				itemType == "HuntingKnife" 		  	or
				itemType == "KitchenKnife" 		  	or
				itemType == "MeatCleaver"  		  	or 
			    itemType == "Scalpel" 	   		  	then suFFixe = "_itemKnife_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "HandShovel" 		  	or 
			    itemType == "HandFork" 	 		  	then suFFixe = "_itemHandShovel_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "BaseballBat" 		  	or
				itemType == "HockeyStick" 		  	or
				itemType == "IceHockeyStick" 	  	or
				itemType == "Poolcue" 			  	or
				itemType == "BaseballBatNails" 	  	or
				itemType == "PlankNail" 		  	or
				itemType == "PickAxeHandle" 	  	or
				itemType == "RollingPin" 		  	or
				itemType == "PickAxeHandleSpiked" 	or
				itemType == "TableLeg" 			  	then suFFixe = "_itemBat_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "Crowbar" 			  	or
				itemType == "Golfclub" 			  	or
				itemType == "LeadPipe" 			  	or
				itemType == "MetalBar" 			  	or 
				itemType == "TowBarCar" 			or
				itemType == "AntiCrowbar" 			or
			    itemType == "MetalPipe" 		  	then suFFixe = "_itemBar_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "LeafRake" 	 		  	or
				itemType == "Rake" 		 		  	or 
			    itemType == "GardenFork" 		  	then suFFixe = "_itemRake_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "Shovel" 	 		  	or
				itemType == "Shovel2" 	 		  	or
				itemType == "SnowShovel" 		  	or
			    itemType == "GardenHoe"  		  	then suFFixe = "_itemShovel_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "Sledgehammer" 		  	or
				itemType == "Sledgehammer2" 	  	then suFFixe = "_itemSledge_"..stat
		-------------------------------------------------------------------------------
		elseif  itemType == "BallPeenHammer" 	  	or 
				itemType == "ClubHammer" 		  	or
				itemType == "PickAxe"			  	or
			    itemType == "Hammer" 		 	  	then suFFixe = "_itemHammer_"..stat
		-------------------------------------------------------------------------------
		elseif 	itemType == "Axe" 	  			  	or
				itemType == "HandAxe" 			  	or 
			    itemType == "WoodAxe" 			  	then suFFixe = "_itemAxe_"..stat
		-------------------------------------------------------------------------------
		elseif 	luautils.stringStarts(itemType, 'Spear') then suFFixe = "_itemSpear_"..stat
		-------------------------------------------------------------------------------
		elseif	string.contains(itemType,"LogStack")or
				string.contains(itemType,"Wood")	or
				string.contains(itemType,"wood")	or
				--itemType == "UnusableWood" 			or
				itemType == "Twigs" 				or
				itemType == "TreeBranch" 			or
				itemType == "Log" 					then suFFixe = "_TreeWood"
		end
	end
	return suFFixe
end
----------------------------------------------------------------------------------------------------------------------------------------------------------- 
local function TI_SoundDisplayCategory2(player,item,stat)
	if not item or not player then return end
	local suFFixe = TI_getSoundItem(item,stat)
	local Cat = item:getDisplayCategory()
	if Cat ~= nil and (suFFixe == "_TakingM" or (not string.contains(suFFixe,"_up") and not string.contains(suFFixe,"_down"))) then
		suFFixe = "_itemTaking"
		---------------------------------------------
		local StrgItemType = item:getStringItemType()
		local MetalValue   = item:getMetalValue()
		local StrgItemType = item:getStringItemType()
		local itemCat	   = item:getCategory()
		if itemCat == "Weapon" 		then
			if 	Cat == "Weapon" 	then  if StrgItemType ~= "MeleeWeapon" then suFFixe = "_itemWeapon" end --Cat == "WeaponCrafted" or item:getType() == Type.Weapon  --Cat == "Container"
			end
		elseif Cat == "Clothing"  	or 
			   Cat == "Accessory" 	or 
			   Cat == "Bag" 	  	or 
			   Cat == "Bandage"   	then suFFixe = "_itemClothing"
		end 	
	end
	--print("-----------------")
	--print("vanillaCat : "..Cat)
	--print("TIsnd Cat : "..suFFixe)
	--print("getType : "..item:getType())
	--print("metal value : "..item:getMetalValue())
	--print("StrgItemType : "..item:getStringItemType())
	--print("TIsnddelay".. tostring(TIsnd.delay))

	return TI_getSoundName(player,suFFixe)
end
----------------------------------------------------------------------------------------------------------------------------------------------------------- 
local function TI_SoundDisplayCategory(player,item,stat)
	if not item or not player then return end
	local itemType = item:getType()
	local Cat 	   = item:getDisplayCategory()
	local maxAmmo = item:getMaxAmmo()
	local suFFixe  = TI_getSoundItem(item,stat)
	local rand 	   = ZombRand(6)
	if Cat ~= nil and itemType and suFFixe == "_Clothing" then
		----------------------------------------------------------------------------------------
		local ReplaceOnUse = item:getReplaceOnUse()
		local StrgItemType = item:getStringItemType()
		local MetalValue   = item:getMetalValue()
		
		if 		ReplaceOnUse == "BeerEmpty"     or
		   		ReplaceOnUse == "WhiskeyEmpty"  or
		   		ReplaceOnUse == "WineEmpty2"    or
		   		ReplaceOnUse == "WineEmpty"     or
		   		ReplaceOnUse == "GlassTumbler"  then if rand < 3 then suFFixe = "_itemMolotov_up" else suFFixe = "_itemMolotov_down" end
		----------------------------------------------------------------------------
		elseif 	Cat 		== "Water" 			or 
		   		Cat 		== "WaterContainer" then suFFixe = "_Water"	
		----------------------------------------------------------------------------
		elseif Cat == "Weapon" 		  		  	then 	if StrgItemType ~= "MeleeWeapon" then suFFixe = "_Weapon" elseif rand < 4 then suFFixe = "_WeaponMelee" else suFFixe = "_TakingM" end
		----------------------------------------------------------------------------
		elseif Cat == "ToolWeapon"    		  	or 
			   Cat == "Tool" 		  		  	then  suFFixe = "_TakingM"  
		----------------------------------------------------------------------------
		elseif Cat == "Sports"   	  		  	then  if MetalValue > 0 then suFFixe = "_ToolM" elseif rand < 3 then suFFixe = "_TakingM" elseif rand < 5 then suFFixe = "_Furniture" end
		----------------------------------------------------------------------------
		elseif 			Cat == "Food" 			or 
			   ReplaceOnUse == "TinCanEmpty" 	or 
			   ReplaceOnUse == "BeerCanEmpty"	or 
			   itemType 	== "BeerCanEmpty" 	or 
			   itemType 	== "TinCanEmpty"  	then	
			---------------------------------------
			if(ReplaceOnUse == "TinCanEmpty" 	or 
			   ReplaceOnUse == "BeerCanEmpty" 	or
			   ------------------------------------
			   itemType 	== "BeerCanEmpty" 	or 
			   itemType 	== "TinCanEmpty"  	or 
			   StrgItemType == "CannedFood")  	and
			   rand 		< 5 			  	then suFFixe = "_FoodM" 
			else 								   	 suFFixe = "_Taking"
			end
		----------------------------------------------------------------------------
		elseif Cat == "Security"		  then 	suFFixe = "_Security"
		elseif Cat == "Ammo" 			  then 	if maxAmmo > 0 then suFFixe = "_WeaponPart" else suFFixe = "_Ammo" end
		elseif Cat == "Entertainment" 	  then 	suFFixe = "_Entertainment"
		elseif Cat == "Bag"				  then  suFFixe = "_Bag"
		elseif Cat == "VehicleMaintenance"then 	suFFixe = "_VehicleMaintenance"
		----------------------------------------------------------------------------
		elseif Cat == "WeaponPart" 	 	  then 	suFFixe = "_WeaponPart" 
		----------------------------------------------------------------------------
		elseif Cat == "Explosives" 	 	  or 
			   Cat == "Instrument" 		  or 
			   Cat == "Trapping" 	 	  or 
			   Cat == "Electronics" 	  or 
			   Cat == "Communications"	  then 	suFFixe = "_Instrument"
		----------------------------------------------------------------------------
		elseif string.contains(itemType,"Ripped") or
			   itemType == "Sheet"		  or
			   Cat == "Clothing" 		  or 
			   Cat == "Accessory" 		  or 
			   Cat == "Bandage" 		  then suFFixe = "_Clothing" 
		----------------------------------------------------------------------------
		elseif Cat == "Cartography"  	  or 
			   Cat == "Literature" 		  or 
			   Cat == "SkillBook" 		  then 	suFFixe = "_Literature"
		----------------------------------------------------------------------------
		elseif Cat == "Household" 		  or
			   Cat == "Fishing" 	 	  or 
			   Cat == "Furniture" 	 	  or 
			   Cat == "Junk" 			  or
			   Cat == "Camping" 		  then 	if MetalValue > 0 then suFFixe = "_VehicleMaintenance" elseif rand < 2 then suFFixe = "_TakingM" elseif rand < 4 then suFFixe = "_Furniture" else suFFixe = "_Box" end
		----------------------------------------------------------------------------
		elseif Cat == "FirstAid" and rand < 4 then  suFFixe = "_Taking"
		----------------------------------------------------------------------------
		elseif Cat == "Material" and rand < 4 then  if MetalValue > 0 then suFFixe = "_VehicleMaintenance" elseif rand < 3 then suFFixe = "_Furniture" else suFFixe = "_Box" end
		----------------------------------------------------------------------------
		elseif StrgItemType == "MeleeWeapon"  then  suFFixe = "_TakingM"
		----------------------------------------------------------------------------
		elseif Cat 			== "Gardening"    then  if rand < 2 then suFFixe = "_Taking" elseif rand < 4 then suFFixe = "_Furniture" else suFFixe = "_Box" end
		----------------------------------------------------------------------------
		elseif Cat 			== "Cooking"      then  if MetalValue >= 19 then suFFixe = "_CookingM" elseif MetalValue > 0 then if rand < 4 then suFFixe = "_Cooking" else suFFixe = "_TakingM" end else if rand < 4 then suFFixe = "_Box" else suFFixe = "_Taking" end end
		----------------------------------------------------------------------------
		end
	end
	------------------------------------------------------------------------------------------------------------------------------------
	if luautils.stringStarts(suFFixe, '_item') and rand > 3 then if rand > 4 then suFFixe = "_TakingM" else suFFixe = "_Clothing" end end
	------------------------------------------------------------------------------------------------------------------------------------
	--print("-----------------")
	--print("vanillaCat : "..Cat)
	--print("TIsnd Cat : "..suFFixe)
	--print("getType : "..item:getType())
	--
	--print("metal value : "..item:getMetalValue())
	--print("StrgItemType : "..item:getStringItemType())
	--print("TIsnddelay".. tostring(TIsnd.delay))

	return TI_getSoundName(player,suFFixe)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
local function TI_playSoundAction(player,item,stat)
-----------------------------------------------------------------------------------------------------------------------------------------------------------
	--Events.OnPlayerUpdate.Add(TI_ISInventoryTransferAction_delay_stop_sound)--if TIsnd.delay == nil or TIsnd.delay <= 0 then  end
	--------------------------------------------------------------------------------------
	if (player and item) and (TIsnd.sound == nil or (TIsnd.sound and not player:getEmitter():isPlaying(TIsnd.sound))) then
		local soundCat = TI_SoundDisplayCategory2(player,item,stat)
		if soundCat ~= nil then
			TIsnd.sound = player:getEmitter():playSound(soundCat)
			--print("sound name: "..soundCat)
		end
	end
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
function TI_ISInventoryTransferAction_delay_stop_sound()
-----------------------------------------------------------------------------------------------------------------------------------------------------------
	local player = getSpecificPlayer(0)
	if TIsnd.delay == nil then TIsnd.delay = delayStart end
	if TIsnd.delay <= 0 then
		if player and TIsnd.sound and TIsnd.sound ~= 0 and player:getEmitter():isPlaying(TIsnd.sound) then
			player:getEmitter():stopSound(TIsnd.sound)
		end
		TIsnd.delay = nil 
		TIsnd.sound = nil
		------------------------------------------------------------------------
		Events.OnPlayerUpdate.Remove(TI_ISInventoryTransferAction_delay_stop_sound)
		------------------------------------------------------------------------
		return
	end
	TIsnd.delay = TIsnd.delay -1
end




-----------------------------------------------------------------------------------------------------------------------------------------------------------
Events.OnGameStart.Add(function()
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISInventoryTransferAction.update = ISInventoryTransferAction.update
function ISInventoryTransferAction:update()
	TIsnd.ISInventoryTransferAction.update(self)
	--------------------------------------------------------------------------------------
	if self.character and ISInventoryTransferAction.putSound and ISInventoryTransferAction.putSound ~= 0 and self.character:getEmitter():isPlaying(ISInventoryTransferAction.putSound) then
		self.character:getEmitter():stopSound(ISInventoryTransferAction.putSound)
	end
	--------------------------------------------------------------------------------------
	if (self.character and self.item) and (TIsnd.sound == nil or (TIsnd.sound and not self.character:getEmitter():isPlaying(TIsnd.sound))) then
		local soundCat = TI_SoundDisplayCategory(self.character,self.item,"transf")
		if soundCat ~= nil then
			TIsnd.sound = self.character:getEmitter():playSound(soundCat)
			--print("ISInventoryTransferAction:update sound name: "..soundCat)
		end
	end
	--------------------------------------------------------------------------------------
	if TIsnd.delay == nil then TIsnd.delay = delayStart end
	TIsnd.delay = TIsnd.delay +1
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISInventoryTransferAction.start = ISInventoryTransferAction.start
function ISInventoryTransferAction:start()
	TIsnd.ISInventoryTransferAction.start(self)
	--------------------------------------------------------------------------------------
	Events.OnPlayerUpdate.Add(TI_ISInventoryTransferAction_delay_stop_sound) --TIsnd.delay == nil or TIsnd.delay <= 0 -- if self.startTest == nil then end
	--------------------------------------------------------------------------------------
	if self.character and ISInventoryTransferAction.putSound and ISInventoryTransferAction.putSound ~= 0 and self.character:getEmitter():isPlaying(ISInventoryTransferAction.putSound) then
		self.character:getEmitter():stopSound(ISInventoryTransferAction.putSound)
	end
	if (self.startTest == nil and self.character and self.item) and (TIsnd.sound == nil or (TIsnd.sound and not self.character:getEmitter():isPlaying(TIsnd.sound))) then
		self.startTest = true
		local soundCat = TI_SoundDisplayCategory(self.character,self.item,"transf")
		if soundCat ~= nil then
			TIsnd.sound = self.character:getEmitter():playSound(soundCat)
			--print("ISInventoryTransferAction:start sound name: "..soundCat)
		end
	end
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISInventoryPaneContextMenu.onPlaceItemOnGround = ISInventoryPaneContextMenu.onPlaceItemOnGround
ISInventoryPaneContextMenu.onPlaceItemOnGround = function(items, playerObj)
	for _,item in ipairs(items) do
			TI_playSoundAction(playerObj,item,"up")
    	break
    end
    TIsnd.ISInventoryPaneContextMenu.onPlaceItemOnGround(items, playerObj)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISDropWorldItemAction.start = ISDropWorldItemAction.start
function ISDropWorldItemAction:start()
	TIsnd.ISDropWorldItemAction.start(self)
	if self.sound then self.character:stopOrTriggerSound(self.sound) end
	TI_playSoundAction(self.character,self.item,"down")
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISDetachItemHotbar.start = ISDetachItemHotbar.start
function ISDetachItemHotbar:start()
	TIsnd.ISDetachItemHotbar.start(self)
	TI_playSoundAction(self.character,self.item,"down")
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISAttachItemHotbar.start = ISAttachItemHotbar.start
function ISAttachItemHotbar:start()
	TIsnd.ISAttachItemHotbar.start(self)
	TI_playSoundAction(self.character,self.item,"up")
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISEquipWeaponAction.start = ISEquipWeaponAction.start
function ISEquipWeaponAction:start()
	TIsnd.ISEquipWeaponAction.start(self)
	if not self.item:getEquipSound() then
		TI_playSoundAction(self.character,self.item,"up")
	end
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISUnequipAction.start = ISUnequipAction.start
function ISUnequipAction:start()
	TIsnd.ISUnequipAction.start(self)
	TI_playSoundAction(self.character,self.item,"down")
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISWearClothing.start = ISWearClothing.start
function ISWearClothing:start()
	TIsnd.ISWearClothing.start(self)
	TI_playSoundAction(self.character,self.item,"cloth")
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISApplyBandage.start = ISApplyBandage.start
function ISApplyBandage:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionBandage"))
	TIsnd.ISApplyBandage.start(self)
end
TIsnd.ISApplyBandage.stop = ISApplyBandage.stop
function ISApplyBandage:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISApplyBandage.stop(self)
end
TIsnd.ISApplyBandage.perform = ISApplyBandage.perform
function ISApplyBandage:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionBandagePerf"))
	TIsnd.ISApplyBandage.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISCleanBandage.start = ISCleanBandage.start
function ISCleanBandage:start()
	self.TIsnd = self.character:playSound("WashClothing")
	TIsnd.ISCleanBandage.start(self)
end
TIsnd.ISCleanBandage.stop = ISCleanBandage.stop
function ISCleanBandage:stop()
	if self.TIsnd and self.character:getEmitter():isPlaying(self.TIsnd) then self.character:stopOrTriggerSound(self.TIsnd) end
	TIsnd.ISCleanBandage.stop(self)
end
TIsnd.ISCleanBandage.perform = ISCleanBandage.perform
function ISCleanBandage:perform()
	if self.TIsnd and self.character:getEmitter():isPlaying(self.TIsnd) then self.character:stopOrTriggerSound(self.TIsnd) end
	TIsnd.ISCleanBandage.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISDisinfect.start = ISDisinfect.start
function ISDisinfect:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionDisinfect"))
	TIsnd.ISDisinfect.start(self)
end
TIsnd.ISDisinfect.stop = ISDisinfect.stop
function ISDisinfect:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISDisinfect.stop(self)
end
TIsnd.ISDisinfect.perform = ISDisinfect.perform
function ISDisinfect:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionDisinfectPerf"))
	TIsnd.ISDisinfect.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISTransferWaterAction.start = ISTransferWaterAction.start
function ISTransferWaterAction:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionWaterTransfer"))
	TIsnd.ISTransferWaterAction.start(self)
end
TIsnd.ISTransferWaterAction.stop = ISTransferWaterAction.stop
function ISTransferWaterAction:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISTransferWaterAction.stop(self)
end
TIsnd.ISTransferWaterAction.perform = ISTransferWaterAction.perform
function ISTransferWaterAction:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionWaterTransferPerf"))
	TIsnd.ISTransferWaterAction.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISTakePillAction.start = ISTakePillAction.start
function ISTakePillAction:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionTakePill"))
	TIsnd.ISTakePillAction.start(self)
end
TIsnd.ISTakePillAction.stop = ISTakePillAction.stop
function ISTakePillAction:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISTakePillAction.stop(self)
end
TIsnd.ISTakePillAction.perform = ISTakePillAction.perform
function ISTakePillAction:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionTakePillPerf"))
	TIsnd.ISTakePillAction.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISClearAshes.start = ISClearAshes.start
function ISClearAshes:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionCleanAshes"))
	TIsnd.ISClearAshes.start(self)
end
TIsnd.ISClearAshes.stop = ISClearAshes.stop
function ISClearAshes:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISClearAshes.stop(self)
end
TIsnd.ISClearAshes.perform = ISClearAshes.perform
function ISClearAshes:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionCleanAshesPerf"))
	TIsnd.ISClearAshes.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISRepairClothing.start = ISRepairClothing.start
function ISRepairClothing:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRepairClothing"))
	TIsnd.ISRepairClothing.start(self)
end
TIsnd.ISRepairClothing.stop = ISRepairClothing.stop
function ISRepairClothing:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISRepairClothing.stop(self)
end
TIsnd.ISRepairClothing.perform = ISRepairClothing.perform
function ISRepairClothing:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRepairClothingPerf"))
	TIsnd.ISRepairClothing.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISRemovePatch.start = ISRemovePatch.start
function ISRemovePatch:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRemoveClothing"))
	TIsnd.ISRemovePatch.start(self)
end
TIsnd.ISRemovePatch.stop = ISRemovePatch.stop
function ISRemovePatch:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISRemovePatch.stop(self)
end
TIsnd.ISRemovePatch.perform = ISRemovePatch.perform
function ISRemovePatch:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRepairClothingPerf"))
	TIsnd.ISRemovePatch.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISStitch.start = ISStitch.start
function ISStitch:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionToStich"))
	TIsnd.ISStitch.start(self)
end
TIsnd.ISStitch.stop = ISStitch.stop
function ISStitch:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISStitch.stop(self)
end
TIsnd.ISStitch.perform = ISStitch.perform
function ISStitch:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRepairClothingPerf"))
	TIsnd.ISStitch.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISCleanBurn.start = ISCleanBurn.start
function ISCleanBurn:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionDisinfect"))
	TIsnd.ISCleanBurn.start(self)
end
TIsnd.ISCleanBurn.stop = ISCleanBurn.stop
function ISCleanBurn:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISCleanBurn.stop(self)
end
TIsnd.ISCleanBurn.perform = ISCleanBurn.perform
function ISCleanBurn:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionDisinfectPerf"))
	TIsnd.ISCleanBurn.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISRemoveWeaponUpgrade.start = ISRemoveWeaponUpgrade.start
function ISRemoveWeaponUpgrade:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_Weapon"))
	TIsnd.ISRemoveWeaponUpgrade.start(self)
end
TIsnd.ISRemoveWeaponUpgrade.stop = ISRemoveWeaponUpgrade.stop
function ISRemoveWeaponUpgrade:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISRemoveWeaponUpgrade.stop(self)
end
TIsnd.ISRemoveWeaponUpgrade.perform = ISRemoveWeaponUpgrade.perform
function ISRemoveWeaponUpgrade:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	--self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_WeaponPartPerf"))
	TIsnd.ISRemoveWeaponUpgrade.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISUpgradeWeapon.start = ISUpgradeWeapon.start
function ISUpgradeWeapon:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_Weapon"))
	TIsnd.ISUpgradeWeapon.start(self)
end
TIsnd.ISUpgradeWeapon.stop = ISUpgradeWeapon.stop
function ISUpgradeWeapon:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISUpgradeWeapon.stop(self)
end
TIsnd.ISUpgradeWeapon.perform = ISUpgradeWeapon.perform
function ISUpgradeWeapon:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	--self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_WeaponPartPerf"))
	TIsnd.ISUpgradeWeapon.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISScavengeAction.start = ISScavengeAction.start
function ISScavengeAction:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_Clothing"))
	TIsnd.ISScavengeAction.start(self)
end
TIsnd.ISScavengeAction.stop = ISScavengeAction.stop
function ISScavengeAction:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISScavengeAction.stop(self)
end
TIsnd.ISScavengeAction.perform = ISScavengeAction.perform
function ISScavengeAction:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	--self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_WeaponPartPerf"))
	TIsnd.ISScavengeAction.perform(self)
end
---------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISEquipHeavyItem.start = ISEquipHeavyItem.start
function ISEquipHeavyItem:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_Clothing"))
	TIsnd.ISEquipHeavyItem.start(self)
end
TIsnd.ISEquipHeavyItem.stop = ISEquipHeavyItem.stop
function ISEquipHeavyItem:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISEquipHeavyItem.stop(self)
end
TIsnd.ISEquipHeavyItem.perform = ISEquipHeavyItem.perform
function ISEquipHeavyItem:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	--self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_WeaponPartPerf"))
	TIsnd.ISEquipHeavyItem.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISClothingExtraAction.start = ISClothingExtraAction.start
function ISClothingExtraAction:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_Clothing"))
	TIsnd.ISClothingExtraAction.start(self)
end
TIsnd.ISClothingExtraAction.stop = ISClothingExtraAction.stop
function ISClothingExtraAction:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISClothingExtraAction.stop(self)
end
TIsnd.ISClothingExtraAction.perform = ISClothingExtraAction.perform
function ISClothingExtraAction:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	--self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_WeaponPartPerf"))
	TIsnd.ISClothingExtraAction.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISRemoveGlass.start = ISRemoveGlass.start
function ISRemoveGlass:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRemoveGlass"))
	TIsnd.ISRemoveGlass.start(self)
end
TIsnd.ISRemoveGlass.stop = ISRemoveGlass.stop
function ISRemoveGlass:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISRemoveGlass.stop(self)
end
TIsnd.ISRemoveGlass.perform = ISRemoveGlass.perform
function ISRemoveGlass:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRemoveGlassPerf"))
	TIsnd.ISRemoveGlass.perform(self)
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISRemoveBullet.start = ISRemoveBullet.start
function ISRemoveBullet:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRemoveBullet"))
	TIsnd.ISRemoveBullet.start(self)
end
TIsnd.ISRemoveBullet.stop = ISRemoveBullet.stop
function ISRemoveBullet:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISRemoveBullet.stop(self)
end
TIsnd.ISRemoveBullet.perform = ISRemoveBullet.perform
function ISRemoveBullet:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionRemoveBulletPerf"))
	TIsnd.ISRemoveBullet.perform(self)
end
---------------------------------------------------------------------------------------------------------------------------------------------------------
TIsnd.ISSplint.start = ISSplint.start
function ISSplint:start()
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionBandage"))
	TIsnd.ISSplint.start(self)
end
TIsnd.ISSplint.stop = ISSplint.stop
function ISSplint:stop()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	TIsnd.ISSplint.stop(self)
end
TIsnd.ISSplint.perform = ISSplint.perform
function ISSplint:perform()
	if self.TIsnd and self.TIsnd ~= 0 then self.character:getEmitter():stopSound(self.TIsnd) end
	self.TIsnd = self.character:playSound(TI_getSoundName(self.character,"_ActionBandagePerf"))
	TIsnd.ISSplint.perform(self)
end



end)





local selectVolume = { 0,25,50,75,100,125,150,175,200 }

local function SetVolume(optionValues)

	local transfVol = selectVolume[optionValues.settings.options.transf]/100
	local itemVol = selectVolume[optionValues.settings.options.item]/100
	local SearchVol = selectVolume[optionValues.settings.options.Search]/100
	local ActionVol = selectVolume[optionValues.settings.options.Action]/100

	local intransfVol = selectVolume[optionValues.settings.options.intransf]/100
	local initemVol = selectVolume[optionValues.settings.options.initem]/100
	local inSearchVol = selectVolume[optionValues.settings.options.inSearch]/100
	local inActionVol = selectVolume[optionValues.settings.options.inAction]/100

	local categories = GameSounds.getCategories()
	for i=1,categories:size() do
		local tabName = categories:get(i-1)
		if tabName == "Player" then

			local sounds = GameSounds.getSoundsInCategory(categories:get(i-1))
			for i=1,sounds:size() do
				local gameSound = sounds:get(i-1)
				local soundName = gameSound:getName()
				if luautils.stringStarts(soundName, 'TIsnd_') then 

					if 		string.contains(soundName,"_transf") 	then gameSound:setUserVolume(transfVol)
					elseif 	string.contains(soundName,"_item") 		then gameSound:setUserVolume(itemVol)
					elseif 	string.contains(soundName,"_Search") 	then gameSound:setUserVolume(SearchVol)
					elseif 	string.contains(soundName,"_Action") 	then gameSound:setUserVolume(ActionVol)
					else 												 gameSound:setUserVolume(transfVol)
					end

				elseif luautils.stringStarts(soundName, 'inTIsnd_') then 

					if 		string.contains(soundName,"_transf") 	then gameSound:setUserVolume(intransfVol)
					elseif 	string.contains(soundName,"_item") 		then gameSound:setUserVolume(initemVol)
					elseif 	string.contains(soundName,"_Search") 	then gameSound:setUserVolume(inSearchVol)
					elseif 	string.contains(soundName,"_Action") 	then gameSound:setUserVolume(inActionVol)
					else 												 gameSound:setUserVolume(intransfVol)
					end

				end
			end

			GameSounds.saveINI()

			break
		end
	end
end

local SETTINGS = {
    options_data = {
        transf = {
           	"0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Transfer items",            
            tooltip = "When you transfer objects to the ground, into your inventory or containers.",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
        item = {
            "0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Equip/Unequip",
            tooltip = "When you are equip yourself with a weapon or other item in your hand.",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
        Search = {
            "0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Furnitures search",
            tooltip = "With the inventory tetris mod, and search mode is activated.",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
        Action = {
            "0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Actions",
            tooltip = "During some actions such as medical procedures and others.",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
        intransf = {
           	"0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Transfer items (inside)",            
            tooltip = "When you are in a vehicle and you transfer objects to the containers to your inventory. (low frequency)",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
        initem = {
            "0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Equip/Unequip (inside)",
            tooltip = "When you are in a vehicle and equip yourself with a weapon or other item in your hand. (low frequency)",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
        inSearch = {
            "0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Furnitures search (inside)",
            tooltip = "When you are in a vehicle, with the inventory tetris mod, and search mode is activated. (low frequency)",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
        inAction = {
            "0%", "25%", "50%", "75%", "100%", "125%", "150%", "175%", "200%",
            name = "Actions (inside)",
            tooltip = "When you are in a vehicle and during some actions such as medical procedures and others. (low frequency)",
            default = 5,
            OnApplyMainMenu = SetVolume,
            OnApplyInGame = SetVolume,
        },
    },
 	mod_id = "addTransferItemSounds",
  	mod_shortname = "Items sounds",
}

if ModOptions and ModOptions.getInstance then
  	ModOptions:getInstance(SETTINGS)
end


--[[




local delay = 5
local sound
local name
function diffuseSound () 
	--print(tostring(delay))
	local player = getPlayer()
	if not sound or not player:getEmitter():isPlaying(sound) then
		delay = delay -1
		sound = player:getEmitter():playSound(name)
	end
	if delay == 0 then Events.OnPlayerUpdate.Remove(diffuseSound) end
end
function diffuse(n,d)
	Events.OnPlayerUpdate.Remove(diffuseSound)
	if not d then  return end
	print("name =" .. tostring(n))
	name = n
	delay = d
	Events.OnPlayerUpdate.Add(diffuseSound)
end


local v = {}
v.name1 = {
	
	"TIsnd_Clothing",
	"TIsnd_Cooking",
	"TIsnd_CookingM",
	"TIsnd_FoodM",
	"TIsnd_Furniture",
	"TIsnd_GlassBottle",
	"TIsnd_Instrument",
	"TIsnd_Literature",
	"TIsnd_Security",
	"TIsnd_Taking",
	"TIsnd_TakingM",
	"TIsnd_Tool",
	"TIsnd_ToolM",
	"TIsnd_TreeWood",
	"TIsnd_VehicleMaintenance",
	"TIsnd_Water",
	"TIsnd_Weapon",
	"TIsnd_WeaponMelee",
	"TIsnd_WeaponPart",
	"TIsnd_Ammo",
	"TIsnd_Bag",
	"TIsnd_Box"
}
v.name2 = {
	
	"TIsnd_itemClothing",
	"TIsnd_itemWeapon",
	"TIsnd_itemTaking",
	"TIsnd_itemPetrolCan_down",
	"TIsnd_itemPetrolCan_up",
	"TIsnd_itemMolotov_down",
	"TIsnd_itemMolotov_up",
	"TIsnd_SmashedBottle_down",
	"TIsnd_SmashedBottle_up",
	"TIsnd_itemGuitar_up",
	"TIsnd_itemGuitar_down",
	"TIsnd_itemPan_up",
	"TIsnd_itemPan_down",
	"TIsnd_itemKatana_up",
	"TIsnd_itemKatana_down",
	"TIsnd_itemWrench_down",
	"TIsnd_itemWrench_up",
	"TIsnd_itemMetalObj_down",
	"TIsnd_itemMetalObj_up",
	"TIsnd_itemKnife_up",
	"TIsnd_itemKnife_down",
	"TIsnd_itemHandShovel_down",
	"TIsnd_itemHandShovel_up",
	"TIsnd_itemBat_down",
	"TIsnd_itemBat_up",
	"TIsnd_itemBar_down",
	"TIsnd_itemBar_up",
	"TIsnd_itemRake_up",
	"TIsnd_itemRake_down",
	"TIsnd_itemShovel_up",
	"TIsnd_itemShovel_down",
	"TIsnd_itemSledge_up",
	"TIsnd_itemSledge_down",
	"TIsnd_itemHammer_up",
	"TIsnd_itemHammer_down",
	"TIsnd_itemAxe_up",
	"TIsnd_itemAxe_down",
	"TIsnd_itemSpear_down",
	"TIsnd_itemSpear_up"
}
v.name3 = {
	"TIsnd_itemPetrolCan_transf",
	"TIsnd_itemMolotov_transf",
	"TIsnd_SmashedBottle_transf",
	"TIsnd_itemGuitar_transf",
	"TIsnd_itemPan_transf",
	"TIsnd_itemKatana_transf",
	"TIsnd_itemWrench_transf",
	"TIsnd_itemMetalObj_transf",
	"TIsnd_itemKnife_transf",
	"TIsnd_itemHandShovel_transf",
	"TIsnd_itemBat_transf",
	"TIsnd_itemBar_transf",
	"TIsnd_itemRake_transf",
	"TIsnd_itemShovel_transf",
	"TIsnd_itemSledge_transf",
	"TIsnd_itemHammer_transf",
	"TIsnd_itemAxe_transf",
	"TIsnd_itemSpear_transf"
}
v.name4 = {

"TIsnd_Searchcabinet",
"TIsnd_SearchPlasticBag",
"TIsnd_SearchPaperBag",
"TIsnd_SearchFridge",
"TIsnd_SearchDesk",
"TIsnd_SearchMicrowave",
"TIsnd_SearchTruckBed",
"TIsnd_SearchShelve",
"TIsnd_SearchOverhead",
"TIsnd_SearchMedicine",
"TIsnd_SearchGloveBox",
"TIsnd_SearchPost",
"TIsnd_SearchDeadBody",
"TIsnd_SearchFloor",
"TIsnd_SearchBagCloth",
"TIsnd_SearchCounter",
"TIsnd_SearchStove",
"TIsnd_SearchBinTrash",
"TIsnd_SearchWardrobe",
"TIsnd_SearchCrate",
"TIsnd_SearchLocker",
"TIsnd_SearchTool"
}



v.name5 = {
	
"TIsnd_Bandage",
"TIsnd_BandagePerf",
"TIsnd_Disinfect",
"TIsnd_DisinfectPerf",
"TIsnd_TakePill",
"TIsnd_TakePillPerf",
"TIsnd_CleanAshes",
"TIsnd_CleanAshesPerf",
"TIsnd_WaterTransfer",
"TIsnd_WaterTransferPerf",
"TIsnd_RepairClothing",
"TIsnd_RemoveClothing",
"TIsnd_RepairClothingPerf",
"TIsnd_ToStich",
"TIsnd_RemoveBullet",
"TIsnd_RemoveBulletPerf",
"TIsnd_RemoveGlass",
"TIsnd_RemoveGlassPerf"
}

local delay = 0
local sound
local name
local suffix
function diffuseSoundlist () 
	local player = getPlayer()
	if not sound or not player:getEmitter():isPlaying(sound) then
		delay = delay +1
		print("name: "..v[name][delay])
		sound = player:getEmitter():playSound(suffix..v[name][delay])
	end
	if delay == #v[name] then Events.OnPlayerUpdate.Remove(diffuseSoundlist) ; delay = 0 end
end
function diffuselist(n,s)
	Events.OnPlayerUpdate.Remove(diffuseSoundlist)
	if not n then  return end
	name = n
	suffix = s
	delay = 0
	Events.OnPlayerUpdate.Add(diffuseSoundlist)
end


diffuse("TIsnd_itemGuitar_transf",30)

diffuselist("name5","")


Events.OnPlayerUpdate.Remove(diffuseSoundlist)
Events.OnPlayerUpdate.Remove(diffuseSound)




ANIMAUX

DisplayCategory = Beaver,
DisplayCategory = Fox,
DisplayCategory = Hedgehog,
DisplayCategory = Mole,
DisplayCategory = Bunny,
DisplayCategory = Squirrel,
DisplayCategory = Badger,
DisplayCategory = Raccoon,
DisplayCategory = Corpse,

DisplayCategory = Paint,

DisplayCategory = LightSource,

DisplayCategory = ZedDmg,
DisplayCategory = Hidden
DisplayCategory = Appearance
]]



	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 
	
	
	
	
	
	
	