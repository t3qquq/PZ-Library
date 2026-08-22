--------------------------
-- GUNFIGHTER WINDOW	--
--------------------------
GunFighterWindow = ISCollapsableWindow:derive("GunFighterWindow");
GunFighterWindow.compassLines = {}
function GunFighterWindow:initialise()
	ISCollapsableWindow.initialise(self);
end

function GunFighterWindow:new(x, y, width, height)
	local o = {};
	o = ISCollapsableWindow:new(x, y, width, height);
	setmetatable(o, self);
	self.__index = self;
	o.title = "GunFighter Info";
	o.pin = false;
	o:noBackground();
	return o;
end

function GunFighterWindow:setText(newText)
	GunFighterWindow.HomeWindow.text = newText;
	GunFighterWindow.HomeWindow:paginate();
end

function GunFighterWindow:createChildren()
	ISCollapsableWindow.createChildren(self);
	self.HomeWindow = ISRichTextPanel:new(0, 30, 400, 560);
	self.HomeWindow:initialise();
	self.HomeWindow:addScrollBars();		-- THIS ONE WORKS!!
	self.HomeWindow.autosetheight = false
--	self.HomeWindow.autosetheight = true
--	self.HomeWindow:ignoreHeightChange()
--	self.HomeWindow.resizable = true
	self:addChild(self.HomeWindow)
--	self.HomeWindow:setScrollChildren(true)	-- DOESNT DO SHIT.

end

function GunFighterWindowCreate()
	GunFighterWindow = GunFighterWindow:new(80, 50, 400, 600)
	GunFighterWindow:addToUIManager();
	GunFighterWindow:setVisible(false);
	GunFighterWindow.pin = true;
--	GunFighterWindow.resizable = true
end

Events.OnGameStart.Add(GunFighterWindowCreate);



--------------------------------
-- GUNFIGHTER INFO WINDOW	--
--------------------------------
function Show_GunFighter_Window(keynum)
	if getSpecificPlayer(0) == nil then return end
	
	if keynum == getCore():getKey("GUNFIGHTER_WINDOW") and GunFighterWindow:isVisible() then
		GunFighterWindow:setVisible(false)
		return
	end

	if ( keynum == getCore():getKey("GUNFIGHTER_WINDOW") or (keynum == "REFRESH" and GunFighterWindow:isVisible()) ) and getSpecificPlayer(0) ~= nil then

		local player	= getSpecificPlayer(0);
		local item		= player:getPrimaryHandItem();
		local name		= nil
		local	cat		= nil

		local gunfighterstring = "NO ITEM \n";

		if item ~= nil then
			cat = item:getDisplayCategory()
			name = item:getDisplayName()
			gunfighterstring = "[ITEM NAME] : "..tostring(name).. "\n\n";

			if item:getTooltip() ~= nil then
				gunfighterstring	= gunfighterstring .. "[MANUFACTURER NOTES] : \n";
				gunfighterstring	= gunfighterstring .. getText(item:getTooltip()).. " \n";
				gunfighterstring	= gunfighterstring .. "\n";
			end

			if	cat == "WeaponPart" then
				gunfighterstring	= gunfighterstring .. "[GENERAL] : \n";
				gunfighterstring	= gunfighterstring .. " - TYPE........ "..tostring(cat).. "\n";

				if	item:getWeight() then
					gunfighterstring	= gunfighterstring .. " - WEIGHT.... "..tostring(round(item:getWeight(),2)).. "\n";
				end

				gunfighterstring	= gunfighterstring .. "\n";

				gunfighterstring = gunfighterstring .. "[STAT AUGMENTATION] : \n";

				if	item:getWeightModifier() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - WEIGHT MOD.... "..tostring(round(item:getWeightModifier(),2))..	"\n";
				end
				if	item:getHitChance() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - ACCURACY........ "..tostring(item:getHitChance())..			"\n";
				end
				if	item:getRecoilDelay() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - RECOIL............ "..tostring(item:getRecoilDelay())..			"\n";
				end
				if	item:getAimingTime() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - SPEED............. "..tostring(item:getAimingTime())..			"\n";
				end
				if	item:getReloadTime() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - RELOAD........... "..tostring(item:getReloadTime())..			"\n";
				end
				if	item:getMinRangeRanged() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - MIN RANGE...... "..tostring(round(item:getMinRangeRanged(),1))..	"\n";
				end
				if	item:getMaxRange() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - MAX RANGE..... "..tostring(round(item:getMaxRange(),1))..		"\n";
				end
				if	item:getAngle() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - FEILD OF VIEW... "..tostring(round(item:getAngle(),3))..		"\n";
				end
				if	item:getDamage() ~= 0 then
					gunfighterstring	= gunfighterstring ..	" - DAMAGE.......... "..tostring(item:getDamage())..				"\n";
				end

				gunfighterstring	= gunfighterstring .. "\n";
				gunfighterstring	= gunfighterstring .. "[COMPATIBLE FIREARMS] : \n";
				gunfighterstring	= gunfighterstring .. tostring(item:getMountOn())..				"\n";
				
			elseif cat == "WeaponConversion" then
				gunfighterstring	= gunfighterstring .. "[GENERAL] : \n";
				gunfighterstring	= gunfighterstring .. " - TYPE........ "..tostring(cat).. "\n";

				if	item:getWeight() then
					gunfighterstring	= gunfighterstring .. " - WEIGHT.... "..tostring(round(item:getWeight(),2)).. "\n";
				end

				gunfighterstring	= gunfighterstring .. "\n";
			
				if	item:getModData().TriggerType then
					gunfighterstring	= gunfighterstring .. " - SELECT-FIRE MODES.. ";
					if		item:getModData().TriggerType == 7 then
							gunfighterstring	= gunfighterstring .. " [Single / [3]Burst / Auto]\n";
					elseif	item:getModData().TriggerType == 6 then
							gunfighterstring	= gunfighterstring .. " [Single / [2]Burst / Auto]\n";
					elseif	item:getModData().TriggerType == 5 then
							gunfighterstring	= gunfighterstring .. " [Single / Auto]\n";
					elseif	item:getModData().TriggerType == 4 then
							gunfighterstring	= gunfighterstring .. " [Single / [3]Burst]\n";
					elseif	item:getModData().TriggerType == 3 then
							gunfighterstring	= gunfighterstring .. " [Single / [2]Burst]\n";
					elseif	item:getModData().TriggerType == 2 then
							gunfighterstring	= gunfighterstring .. " [Auto]\n";
					elseif	item:getModData().TriggerType == 1 then
							gunfighterstring	= gunfighterstring .. " [Single]\n";
					elseif	item:getModData().TriggerType == 0 then
							gunfighterstring	= gunfighterstring .. " [N/A]\n";
					end
				end
			
			--------------------------
			--	FIREARM NOT BAYO	--
			--------------------------
			elseif (instanceof(item,"HandWeapon")) and item:isAimedFirearm() and not item:getCategories():contains("Spear") then
				local	tempMag
				if	item:getMagazineType() then
					tempMag = InventoryItemFactory.CreateItem(item:getMagazineType());
				end

				gunfighterstring	= gunfighterstring .. "[WEAPON STATUS] : \n";
				gunfighterstring	= gunfighterstring .. " - CONDITION (Cur|Max)....  ["..tostring(round(item:getCondition(),1)).." | "..tostring(round(item:getConditionMax(),1)).."] \n";
				local	chamber = ""
				if	item:haveChamber() and item:isRoundChambered() then
					chamber = "+1"
				end
				gunfighterstring	= gunfighterstring .. " - AMMUNITION (Cur|Max).  ["..tostring(round(item:getCurrentAmmoCount(),1))..tostring(chamber).." | "..tostring(round(item:getMaxAmmo(),1)).."] \n";
				if	item:getMagazineType() and item:isContainsClip() and tempMag ~= nil then
					gunfighterstring	= gunfighterstring .. " - MAGAZINE INSERTED....  ["..tostring(tempMag:getDisplayName())..				"] \n";
				else	gunfighterstring	= gunfighterstring .. " - MAGAZINE INSERTED....  [None] \n";
				end

				gunfighterstring	= gunfighterstring .. " - WEIGHT (Load|Empty)....  ["..tostring(round(getLoadedWeight(item),1)).. " | "..tostring(round(item:getWeight(),1))..	"] \n";

				gunfighterstring	= gunfighterstring .. " - ACTIVATED ..................  ["..tostring(item:isActivated()).."] \n";
				gunfighterstring	= gunfighterstring .. " - CAN EMIT LIGHT............  ["..tostring(item:canEmitLight()).."] \n";

				gunfighterstring	= gunfighterstring .. "\n";

				gunfighterstring	= gunfighterstring .. "[SPECIFICATIONS] : \n";
				gunfighterstring	= gunfighterstring .. " - HIT-CHANCE................  ["..tostring(round(item:getHitChance(),1))..			"] \n";
		--		gunfighterstring	= gunfighterstring .. " - NET-CHANCE................  ["..tostring(round(item:getModData().TempHitChance,1))..	"] \n";
				gunfighterstring	= gunfighterstring .. " - RECOIL-DELAY.............  ["..tostring(round(item:getRecoilDelay(),1))..			"] \n";
				gunfighterstring	= gunfighterstring .. " - AIM-TIME.....................  ["..tostring(70 - round(item:getAimingTime(),1))..			"] \n";
				gunfighterstring	= gunfighterstring .. " - RELOAD-TIME..............  ["..tostring(round(item:getReloadTime(),1))..			"] \n";
				gunfighterstring	= gunfighterstring .. " - ANGLE (Max|Min)..........  ["..tostring(round(item:getMaxAngle(),1)).." | "..tostring(round(item:getMinAngle(),1))..	"] \n\n";

				local cyclic = "Standard"
				if (not item:getFireModePossibilities()) then
					gunfighterstring = gunfighterstring .. "[NON SELECT-FIRE] : \n";
					local firemode = item:getFireMode();
					if		firemode == "[6]Rotary" then
							gunfighterstring = gunfighterstring .. " - Full Automatic \n";
							cyclic = "Extremely Fast"
					elseif	firemode == "Auto" then
							gunfighterstring = gunfighterstring .. " - Full Automatic \n";
							cyclic = "Fast"
					elseif	firemode == "Single" then
							gunfighterstring = gunfighterstring .. " - [1]Round Single \n";
							cyclic = "Fast"
					elseif	firemode == "[2]Burst" then
							gunfighterstring = gunfighterstring .. " - [2]Round Burst \n";
							cyclic = "Fast"
					elseif	firemode == "[3]Burst" then
							gunfighterstring = gunfighterstring .. " - [3]Round Burst \n";
							cyclic = "Fast"
					elseif	firemode == "Auto[H]" then
							gunfighterstring = gunfighterstring .. " - Full Automatic \n";
							cyclic = "Standard"
					elseif	firemode == "Single[H]" then
							gunfighterstring = gunfighterstring .. " - [1]Round Single \n";
							cyclic = "Standard"
					elseif	firemode == "[H2]Burst" then
							gunfighterstring = gunfighterstring .. " - [2]Round Burst \n";
							cyclic = "Standard"
					elseif	firemode == "[H3]Burst" then
							gunfighterstring = gunfighterstring .. " - [3]Round Burst \n";
							cyclic = "Standard"
					elseif	firemode == "Auto[L]" then
							gunfighterstring = gunfighterstring .. " - Full Automatic \n";
							cyclic = "Very Fast"
					elseif	firemode == "Single[L]" then
							gunfighterstring = gunfighterstring .. " - [1]Round Single \n";
							cyclic = "Very Fast"
					elseif	firemode == "[L2]Burst" then
							gunfighterstring = gunfighterstring .. " - [2]Round Burst \n";
							cyclic = "Very Fast"
					elseif	firemode == "[L3]Burst" then
							gunfighterstring = gunfighterstring .. " - [3]Round Burst \n";
							cyclic = "Very Fast"
					end

				else	gunfighterstring = gunfighterstring .. "[SELECT-FIRE MODES] : \n";

					for i=0, item:getFireModePossibilities():size() - 1 do
						local selectfire = item:getFireModePossibilities():get(i);
						if selectfire == "Auto" then
							gunfighterstring = gunfighterstring .. " - Full Automatic";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. "..............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Fast"
						elseif selectfire == "Single" then
							gunfighterstring = gunfighterstring .. " - Semi Automatic";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. "............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Fast"
						elseif selectfire == "[2]Burst" then
							gunfighterstring = gunfighterstring .. " - [2]Round Burst";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. ".............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Fast"
						elseif selectfire == "[3]Burst" then
							gunfighterstring = gunfighterstring .. " - [3]Round Burst";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. ".............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Fast"
						elseif selectfire == "Auto[H]" then
							gunfighterstring = gunfighterstring .. " - Full Automatic";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. "..............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Standard"
						elseif selectfire == "Single[H]" then
							gunfighterstring = gunfighterstring .. " - Semi Automatic";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. "............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Standard"
						elseif selectfire == "[H2]Burst" then
							gunfighterstring = gunfighterstring .. " - [2]Round Burst";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. ".............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Standard"
						elseif selectfire == "[H3]Burst" then
							gunfighterstring = gunfighterstring .. " - [3]Round Burst";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. ".............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Standard"
						elseif selectfire == "Auto[L]" then
							gunfighterstring = gunfighterstring .. " - Full Automatic";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. "..............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Very Fast"
						elseif selectfire == "Single[L]" then
							gunfighterstring = gunfighterstring .. " - Semi Automatic";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. "............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Very Fast"
						elseif selectfire == "[L2]Burst" then
							gunfighterstring = gunfighterstring .. " - [2]Round Burst";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. ".............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Very Fast"
						elseif selectfire == "[L3]Burst" then
							gunfighterstring = gunfighterstring .. " - [3]Round Burst";
							if selectfire == item:getFireMode() then
								gunfighterstring = gunfighterstring .. ".............[x]";
							end
							gunfighterstring	= gunfighterstring .. "\n";
							cyclic = "Very Fast"
						end
					end
				end

				gunfighterstring	= gunfighterstring .. "\n";
				gunfighterstring	= gunfighterstring .. "[CYCLIC RATE] : "..tostring(cyclic).." \n";
				local MTBF		= (item:getConditionMax() * item:getConditionLowerChance())
				gunfighterstring	= gunfighterstring .. "[M.T.B.F.] : "..tostring(MTBF).." \n";
				gunfighterstring	= gunfighterstring .. "\n";

				local	tempAmmo	= InventoryItemFactory.CreateItem(item:getAmmoType());
				local AP		= isAmmoPiercing(item:getAmmoType())

				gunfighterstring	= gunfighterstring .. "[AMMUNITION SPECS] : \n";
				gunfighterstring	= gunfighterstring .. " - CALIBER......................  ["..tostring(tempAmmo:getDisplayName())..		"] \n";
				if AP == true then
					gunfighterstring = gunfighterstring .. " - ARMOR PIERCING..........  ["..tostring(round(item:getMaxHitCount(),1))..	"] \n";
				end
				local maxRange = round(item:getMaxRange(),1)
				local minRange = round(item:getMinRange(),1)
				if	isLauncher(item) or isFlamer(item) then
					maxRange = item:getModData().Trajectory
					minRange = item:getModData().Trajectory
				end				
				gunfighterstring	= gunfighterstring .. " - RANGE  (Max|Min)..........  ["..tostring(maxRange).." | "..tostring(minRange)..	"] \n";
				gunfighterstring	= gunfighterstring .. " - DAMAGE (Max|Min)........  ["..tostring(round(item:getMaxDamage(),1)).." | "..tostring(round(item:getMinDamage(),1)).. "] \n";
				gunfighterstring	= gunfighterstring .. " - CRIT-CHANCE...............  ["..tostring(round(item:getCriticalChance(),1))..	"] \n";
				gunfighterstring	= gunfighterstring .. " - PROJECTILE-COUNT......  ["..tostring(round(item:getProjectileCount(),1))..		"] \n";
				if AP == false then
					gunfighterstring = gunfighterstring .. " - HIT-COUNT..................  ["..tostring(round(item:getMaxHitCount(),1))..	"] \n";
				end
				gunfighterstring	= gunfighterstring .. " - KNOCK-DOWN.............  ["..tostring(round(item:getKnockdownMod(),1))..		"] \n";
				gunfighterstring	= gunfighterstring .. " - PUSH-BACK.................  ["..tostring(round(item:getPushBackMod(),1))..		"] \n";
				gunfighterstring	= gunfighterstring .. " - SOUND-RADIUS...........  ["..tostring(round(item:getSoundRadius(),1))..		"] \n";
				gunfighterstring	= gunfighterstring .. " - SOUND-VOLUME..........  ["..tostring(round(item:getSoundVolume(),1))..		"] \n";

				gunfighterstring	= gunfighterstring .. "\n";

				gunfighterstring	= gunfighterstring .. "[MAGAZINE TYPES] : \n";
				local clone = InventoryItemFactory.CreateItem(item:getType());

				if clone:getMagazineType() ~= nil then
					tempMag	= InventoryItemFactory.CreateItem(clone:getMagazineType());
				end
				
				if clone:getMagazineType() and tempMag ~= nil then
					gunfighterstring = gunfighterstring .. " - STANDARD-MAG..........  ["..tostring(tempMag:getDisplayName())..		"] \n";
				else	gunfighterstring = gunfighterstring .. " - INTEGRAL-MAG..........  [NONE] \n";
				end
				if item:getModData().ExtMagType ~= nil then
					tempMag	= InventoryItemFactory.CreateItem(item:getModData().ExtMagType);
					gunfighterstring = gunfighterstring .. " - EXTENDED-MAG..........  ["..tostring(tempMag:getDisplayName())..		"] \n";
				end
				if item:getModData().DrumMagType ~= nil then
					tempMag	= InventoryItemFactory.CreateItem(item:getModData().DrumMagType);
					gunfighterstring = gunfighterstring .. " - DRUM-MAG.................  ["..tostring(tempMag:getDisplayName())..	"] \n";
				end
				if item:getModData().FixedMagType ~= nil then
					tempMag	= InventoryItemFactory.CreateItem(item:getModData().FixedMagType);
					gunfighterstring = gunfighterstring .. " - FIXED-MAG.................  ["..tostring(tempMag:getDisplayName())..	"] \n";
				end
				if item:getModData().ClipType ~= nil then
					tempItem	= InventoryItemFactory.CreateItem(item:getModData().ClipType);
					gunfighterstring = gunfighterstring .. " - ALTERNATE.................  ["..tostring(tempItem:getDisplayName())..		"] \n";
				end

				gunfighterstring	= gunfighterstring .. "\n";

				gunfighterstring	= gunfighterstring .. "[ATTACHMENTS] : \n";
				if item:getScope() ~= nil then	
					tempItem	= InventoryItemFactory.CreateItem(item:getScope():getType());				
					gunfighterstring	= gunfighterstring .. " - OPTIC................. ["..tostring(tempItem:getDisplayName())..	"] \n";
				end
				if item:getCanon() ~= nil then
					tempItem	= InventoryItemFactory.CreateItem(item:getCanon():getType());
					gunfighterstring	= gunfighterstring .. " - MUZZLE.............. ["..tostring(tempItem:getDisplayName())..		"] \n";
				end
				if item:getStock() ~= nil then
					local charge = nil
					local Charge = item:getStock():getModData().Charge
					if	Charge ~= nil then
						if		Charge <= 0 then
								charge = 0
						else	charge = round(item:getStock():getModData().Charge,1)
						end
					end
					tempItem	= InventoryItemFactory.CreateItem(item:getStock():getType());
					gunfighterstring	= gunfighterstring .. " - SIDE RAIL............ ["..tostring(tempItem:getDisplayName())..		"]";
					if		charge ~= nil then
							gunfighterstring	= gunfighterstring .. " - "..tostring(charge).."\n";
					else	gunfighterstring	= gunfighterstring .. "\n";
					end
					
				end
				if item:getSling() ~= nil then
					local charge = nil
					local Charge = item:getSling():getModData().Charge
					if	Charge ~= nil then
						if		Charge <= 0 then
								charge = 0
						else	charge = round(item:getSling():getModData().Charge,1)
						end
					end				
					tempItem	= InventoryItemFactory.CreateItem(item:getSling():getType());
					gunfighterstring	= gunfighterstring .. " - BOTTOM RAIL...... ["..tostring(tempItem:getDisplayName())..		"]";
					if		charge ~= nil then
							gunfighterstring	= gunfighterstring .. " - "..tostring(charge).."\n";
					else	gunfighterstring	= gunfighterstring .. "\n";
					end
					
				end
				if item:getRecoilpad() ~= nil then
					local air = nil
					local Air = item:getModData().Air
					if	Air ~= nil then
						if		Air <= 0 then
								air = 0
						else	air = round(item:getModData().Air,2)
						end
					end
					tempItem	= InventoryItemFactory.CreateItem(item:getRecoilpad():getType());
					gunfighterstring	= gunfighterstring .. " - EXTRA................ ["..tostring(tempItem:getDisplayName())..	"]";
					if		air ~= nil then
							gunfighterstring	= gunfighterstring .. " - "..tostring(air).."\n";
					else	gunfighterstring	= gunfighterstring .. "\n";
					end
				end
				if item:getModData().TriggerType ~= nil then
					gunfighterstring	= gunfighterstring .. " - TRIGGER GROUP... [Type - "..tostring(item:getModData().TriggerType)..	"] \n";
				end

				gunfighterstring	= gunfighterstring .. "\n";

				if item:getModData().Melee ~= nil then
					gunfighterstring = gunfighterstring .. "[MELEE-MODE] \n\n";
				end

				if item:getModData().Fold ~= nil then
					if	item:getModData().Fold:contains("Bayo") or ( item:getModData().Melee ~= nil and item:getModData().Melee:contains("Bayo") )then
						gunfighterstring = gunfighterstring .. "[FOLDING - BAYONET] \n\n";
					else
						gunfighterstring = gunfighterstring .. "[FOLDING - STOCK] \n\n";
					end
				end

				if item:getModData().Integral ~= nil then
					gunfighterstring = gunfighterstring .. "[INTEGRAL-LAUNCHER] \n\n";
				end

				if item:getModData().CompAmmo ~= nil then
					local Comp = InventoryItemFactory.CreateItem(item:getModData().CompAmmo);
					CompAmmo	= InventoryItemFactory.CreateItem(Comp:getAmmoType());

					gunfighterstring = gunfighterstring .. "[COMPATIBLE-AMMO] : "..tostring(CompAmmo:getDisplayName())..	" \n";
				end
			--------------------------
			--	MELEE WEAPON	--
			--------------------------
			elseif (instanceof(item,"HandWeapon")) and (not item:isAimedFirearm()) or ( (instanceof(item,"HandWeapon")) and item:isAimedFirearm() and item:getCategories():contains("Spear") ) then

				gunfighterstring	= gunfighterstring .. "[WEAPON STATUS] : \n";
				gunfighterstring	= gunfighterstring .. " - CONDITION (Cur|Max)....  ["..tostring(round(item:getCondition(),1)).." | "..tostring(round(item:getConditionMax(),1)).."] \n";
				gunfighterstring	= gunfighterstring .. " - WEIGHT ......................  ["..tostring(round(item:getWeight(),1))..	"] \n";
				gunfighterstring	= gunfighterstring .. " - ACTIVATED ..................  ["..tostring(item:isActivated()).."] \n";
				gunfighterstring	= gunfighterstring .. " - CAN EMIT LIGHT............  ["..tostring(item:canEmitLight()).."] \n";

				gunfighterstring	= gunfighterstring .. "\n";
				gunfighterstring	= gunfighterstring .. "[SPECIFICATIONS] : \n";
				local maxRange	= round(item:getMaxRange(),1)
				local minRange	= round(item:getMinRange(),1)
				gunfighterstring	= gunfighterstring .. " - RANGE  (Max|Min)..........  ["..tostring(maxRange).." | "..tostring(minRange)..	"] \n";
				gunfighterstring	= gunfighterstring .. " - DAMAGE (Max|Min)........  ["..tostring(round(item:getMaxDamage(),1)).." | "..tostring(round(item:getMinDamage(),1)).. "] \n";
				gunfighterstring	= gunfighterstring .. " - CRIT-CHANCE...............  ["..tostring(round(item:getCriticalChance(),1))..	"] \n";
				gunfighterstring	= gunfighterstring .. " - HIT-COUNT..................  ["..tostring(round(item:getMaxHitCount(),1))..	"] \n";
				gunfighterstring	= gunfighterstring .. " - KNOCK-DOWN.............  ["..tostring(round(item:getKnockdownMod(),1))..		"] \n";
				gunfighterstring	= gunfighterstring .. " - PUSH-BACK.................  ["..tostring(round(item:getPushBackMod(),1))..		"] \n";
				gunfighterstring	= gunfighterstring .. " - SOUND-RADIUS...........  ["..tostring(round(item:getSoundRadius(),1))..		"] \n";
				gunfighterstring	= gunfighterstring .. " - SOUND-VOLUME..........  ["..tostring(round(item:getSoundVolume(),1))..		"] \n";

				gunfighterstring	= gunfighterstring .. "\n";
				local MTBF		= (item:getConditionMax() * item:getConditionLowerChance())
				gunfighterstring	= gunfighterstring .. "[M.T.B.F.] : "..tostring(MTBF).." \n";

				gunfighterstring	= gunfighterstring .. "\n";
				gunfighterstring	= gunfighterstring .. "[POSSIBLE GRIPS] : \n";
				if item:getModData().ThrownGrip ~= nil then
					gunfighterstring	= gunfighterstring .. " - THROWN \n";
				end
				if item:getModData().NormalGrip ~= nil then
					gunfighterstring	= gunfighterstring .. " - DEFAULT \n";
				end
				if item:getModData().CloseGrip ~= nil then
					if item:getModData().CloseGrip:contains("Thrust") then
						gunfighterstring	= gunfighterstring .. " - THRUST \n";
					else
						gunfighterstring	= gunfighterstring .. " - DEFAULT \n";
					end
				end
				if item:getModData().WideGrip ~= nil then
					if item:getModData().WideGrip:contains("Thrown") then
						gunfighterstring	= gunfighterstring .. " - THROWN \n";
					else
						gunfighterstring	= gunfighterstring .. " - HEAVY \n";
					end
				end
				if item:getModData().SpearGrip ~= nil then
					gunfighterstring	= gunfighterstring .. " - SPEAR \n";
				end
			--------------------------
			--	OTHER ITEM		--
			--------------------------
			else	gunfighterstring	= gunfighterstring .. "[NO INFORMATION AVAILABLE] : \n";
					gunfighterstring	= gunfighterstring .. "\n";
					gunfighterstring	= gunfighterstring .. " - WEIGHT ......................  ["..tostring(round(item:getWeight(),1))..	"] \n";
					gunfighterstring	= gunfighterstring .. " - ACTIVATED ..................  ["..tostring(item:isActivated()).."] \n";
					gunfighterstring	= gunfighterstring .. " - CAN EMIT LIGHT............  ["..tostring(item:canEmitLight()).."] \n";
			
			end
		end


		GunFighterWindow.title = "GUNFIGHTER - INFO";
		GunFighterWindow:setText(gunfighterstring);
		GunFighterWindow:setVisible(true);
	end
end


--------------------------------------------------------------------------
--  ARSENAL[26] UPDATE GUNFIGHTER WINDOW IF VISIBLE				--
--------------------------------------------------------------------------
function Update_GunFighter_Window()
	if	GunFighterWindow:isVisible() then
--		Show_GunFighter_Window(getCore():getKey("GUNFIGHTER_WINDOW"))
		Show_GunFighter_Window("REFRESH")
	end
end


Events.OnPlayerUpdate.Add(Update_GunFighter_Window);
Events.OnKeyPressed.Add(Show_GunFighter_Window);