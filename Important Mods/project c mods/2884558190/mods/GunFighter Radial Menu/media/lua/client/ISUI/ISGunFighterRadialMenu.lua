require "TimedActions/ISBaseTimedAction"
require "ISUI/ISFirearmRadialMenu"

--------------------------------------------
---------- Gunfighter Radial Menu ----------
--------------------------------------------

function ISFirearmRadialMenu:getWeapon()
	local weapon = self.character:getPrimaryHandItem()
	if not weapon then return nil end
	if not instanceof(weapon, "HandWeapon") then return nil end
	-- if not weapon:isAimedFirearm() then return nil end 			-- GunFighter Radial Menu -- Changed weapon:isRanged() to weapon:isAimedFirearm() to enable for Melee Mode.
	return weapon
end

local ISFirearmRadialMenu_fillMenu_old = ISFirearmRadialMenu.fillMenu
function ISFirearmRadialMenu:fillMenu(submenu)
	
	local menu = getPlayerRadialMenu(self.playerNum)

	ISFirearmRadialMenu_fillMenu_old(self)

	local Version2 = GunFighterWindow 	-- GunFighter Version 2.0
	local playerObj = getSpecificPlayer(self.playerNum)
	local Gun = self:getWeapon()
	local GunisRanged = Gun:isRanged()
	local GunType = Gun:getType()
	local MagType = Gun:getMagazineType()
	local MagTypeExt = Gun:getModData().ExtMagType
	local MagTypeDrum = Gun:getModData().DrumMagType
	local MeleeGripSwingType = Gun:getSwingAnim()
	local MeleeGripSpear = Gun:getModData().SpearGrip
	local MeleeGripClose = Gun:getModData().CloseGrip
	local MeleeGripWide = Gun:getModData().WideGrip
	local MeleeGripNormal = Gun:getModData().NormalGrip
	local MeleeGripThrown = Gun:getModData().ThrownGrip
	local GunAmmoType = Gun:getAmmoType()
	local GunTrajectory = Gun:getModData().Trajectory
	local GunIntegral = Gun:getModData().Integral
	local GunMelee = Gun:getModData().Melee
	local GunSling = Gun:getSling()
	local GunFold = Gun:getModData().Fold
	local GunFold2 = Gun:getModData().Fold2
	local GunStock = Gun:getStock()
	local GunLight = nil
	if GunStock and (string.find(GunStock:getType(), "Light") or GunStock:hasTag("Light")) then
		GunLight = true
	end
	local GunLightOn = Gun:getModData().LightOn
	local GunLaser = nil
	if GunStock and (string.find(GunStock:getType(), "Laser") or GunStock:hasTag("Laser")) then
		GunLaser = true
	end
	local GunLaserOn = Gun:getModData().LaserOn
	local GunScope = Gun:getScope()

	ISFirearmRadialMenu.main = {}

	if Version2 ~= nil then 				------------------------------ GunFighter Version 2.0 ------------------------------
		
		-- Magazine Functions
		if GunisRanged and (MagTypeExt or MagTypeDrum) then
			local result = InventoryItemFactory.CreateItem(GunType)
			local MagTypeStd = result:getMagazineType()

			-- Change Magazine Type
			ISFirearmRadialMenu.main["CMagType"] = {}
			if MagType == MagTypeStd then
				ISFirearmRadialMenu.main["CMagType"].name = getText("IGUI_GFR_MagType")..'\n'..'['..getText("IGUI_GFR_MagTypeStd")..']'
				if MagTypeExt then
					ISFirearmRadialMenu.main["CMagType"].icons = getTexture("media/ui/GFR_MagTypeExt.png")
				else
					ISFirearmRadialMenu.main["CMagType"].icons = getTexture("media/ui/GFR_MagTypeDrum.png")
				end
			elseif MagType == MagTypeExt then
				ISFirearmRadialMenu.main["CMagType"].name = getText("IGUI_GFR_MagType")..'\n'..'['..getText("IGUI_GFR_MagTypeExt")..']'
				if MagTypeDrum then
					ISFirearmRadialMenu.main["CMagType"].icons = getTexture("media/ui/GFR_MagTypeDrum.png")
				else
					ISFirearmRadialMenu.main["CMagType"].icons = getTexture("media/ui/GFR_MagTypeStd.png")
				end
			elseif MagType == MagTypeDrum then
				ISFirearmRadialMenu.main["CMagType"].name = getText("IGUI_GFR_MagType")..'\n'..'['..getText("IGUI_GFR_MagTypeDrum")..']'
				ISFirearmRadialMenu.main["CMagType"].icons = getTexture("media/ui/GFR_MagTypeStd.png")
			end
			ISFirearmRadialMenu.main["CMagType"].functions = self.GFRMSimKeyPressMagtype
			ISFirearmRadialMenu.main["CMagType"].arguments = "MAG_TYPE_2"

			-- Auto-Select Magazine
			ISFirearmRadialMenu.main["CMagAuto"] = {}
			if GUNFIGHTER.OPTIONS.options.dropdown132 == 1 then
				ISFirearmRadialMenu.main["CMagAuto"].name = getText("IGUI_GFR_MagAuto")..'\n'..'['..getText("IGUI_GFR_Off")..']'
			elseif GUNFIGHTER.OPTIONS.options.dropdown132 == 2 then
				ISFirearmRadialMenu.main["CMagAuto"].name = getText("IGUI_GFR_MagAuto")..'\n'..'['..getText("IGUI_GFR_MagAutoSmall")..']'
			elseif GUNFIGHTER.OPTIONS.options.dropdown132 == 3 then
				ISFirearmRadialMenu.main["CMagAuto"].name = getText("IGUI_GFR_MagAuto")..'\n'..'['..getText("IGUI_GFR_MagAutoLarge")..']'
			end
			ISFirearmRadialMenu.main["CMagAuto"].icons = getTexture("media/ui/GFR_MagAuto.png")
			ISFirearmRadialMenu.main["CMagAuto"].functions = self.GFRMSimKeyPressMagtype
			ISFirearmRadialMenu.main["CMagAuto"].arguments = "MAG_TYPE_1"
		end


		-- Melee Grips + Throwing Weapons
		if 	MeleeGripSpear or
			MeleeGripClose or
			MeleeGripWide or
			MeleeGripNormal or
			MeleeGripThrown then
				ISFirearmRadialMenu.main["CMeleeGripUp"] = {}
				ISFirearmRadialMenu.main["CMeleeGripDown"] = {}
				if string.find(Gun:getType(), "Thrown") then
					ISFirearmRadialMenu.main["CMeleeGripUp"].name = getText("IGUI_GFR_MeleeGripNext")..'\n'..'['..getText("IGUI_GFR_MeleeGripThrown")..']'
					ISFirearmRadialMenu.main["CMeleeGripDown"].name = getText("IGUI_GFR_MeleeGripPrev")..'\n'..'['..getText("IGUI_GFR_MeleeGripThrown")..']'
				elseif MeleeGripThrown then
					ISFirearmRadialMenu.main["CMeleeGripUp"].name = getText("IGUI_GFR_MeleeGripNext")..'\n'..'['..getText("IGUI_GFR_MeleeGripNormal")..']'
					ISFirearmRadialMenu.main["CMeleeGripDown"].name = getText("IGUI_GFR_MeleeGripPrev")..'\n'..'['..getText("IGUI_GFR_MeleeGripNormal")..']'
				elseif MeleeGripSwingType then
					ISFirearmRadialMenu.main["CMeleeGripUp"].name = getText("IGUI_GFR_MeleeGripNext")..'\n'..'['..MeleeGripSwingType.." Grip"..']'
					ISFirearmRadialMenu.main["CMeleeGripDown"].name = getText("IGUI_GFR_MeleeGripPrev")..'\n'..'['..MeleeGripSwingType.." Grip"..']'
				else
					ISFirearmRadialMenu.main["CMeleeGripUp"].name = getText("IGUI_GFR_MeleeGripNext")
					ISFirearmRadialMenu.main["CMeleeGripDown"].name = getText("IGUI_GFR_MeleeGripPrev")
				end
				ISFirearmRadialMenu.main["CMeleeGripUp"].icons = getTexture("media/ui/GFR_MeleeGripNext.png")
				ISFirearmRadialMenu.main["CMeleeGripDown"].icons = getTexture("media/ui/GFR_MeleeGripPrev.png")
				ISFirearmRadialMenu.main["CMeleeGripUp"].functions = self.GFRMSimKeyPressMagtype
				ISFirearmRadialMenu.main["CMeleeGripDown"].functions = self.GFRMSimKeyPressMagtype
				ISFirearmRadialMenu.main["CMeleeGripUp"].arguments = "MAG_TYPE_2"
				ISFirearmRadialMenu.main["CMeleeGripDown"].arguments = "MAG_TYPE_1"
		end


		-- Alternate Load - Load with Magazine or Clip
		if Gun:getModData().ClipType and Gun:getModData().MagType then
			if Gun:getModData().MagType ~= "Base.SKSFixedBox" then
				ISFirearmRadialMenu.main["CAltLoad"] = {}
				if MagType == Gun:getModData().MagType then
					ISFirearmRadialMenu.main["CAltLoad"].name = getText("IGUI_GFR_AltLoad")..'\n'..'['..getText("IGUI_GFR_AltLoadMag")..']'
				end
				if MagType == Gun:getModData().ClipType then
					ISFirearmRadialMenu.main["CAltLoad"].name = getText("IGUI_GFR_AltLoad")..'\n'..'['..getText("IGUI_GFR_AltLoadClip")..']'
				end
				ISFirearmRadialMenu.main["CAltLoad"].icons = getTexture("media/ui/GFR_AltLoad.png")
				ISFirearmRadialMenu.main["CAltLoad"].functions = self.GFRMSimKeyPress
				ISFirearmRadialMenu.main["CAltLoad"].arguments = "ALT_LOAD"
			end
		end


		-- Launcher Trajectory Up/Down
		if 	(isFlamer(Gun) or isLauncher(Gun)) and
			(not string.find(GunType, "P21_Chainsaw")) and
			(not string.find(GunType, "WD_Flame")) then
				ISFirearmRadialMenu.main["CRangeUp"] = {}
				ISFirearmRadialMenu.main["CRangeUp"].name = getText("IGUI_GFR_RangeUp")..'\n'..'['..GunTrajectory..']'
				ISFirearmRadialMenu.main["CRangeUp"].icons = getTexture("media/ui/GFR_RangeUp.png")
				ISFirearmRadialMenu.main["CRangeUp"].functions = self.GFRMSimKeyPress
				ISFirearmRadialMenu.main["CRangeUp"].arguments = "TRAJECTORY_UP"
				ISFirearmRadialMenu.main["CRangeDown"] = {}
				ISFirearmRadialMenu.main["CRangeDown"].name = getText("IGUI_GFR_RangeDown")..'\n'..'['..GunTrajectory..']'
				ISFirearmRadialMenu.main["CRangeDown"].icons = getTexture("media/ui/GFR_RangeDown.png")
				ISFirearmRadialMenu.main["CRangeDown"].functions = self.GFRMSimKeyPress
				ISFirearmRadialMenu.main["CRangeDown"].arguments = "TRAJECTORY_DOWN"
		
		-- CQB Toggle
		elseif GunisRanged then
			ISFirearmRadialMenu.main["CCQB"] = {}
			ISFirearmRadialMenu.main["CCQB"].icons = getTexture("media/ui/GFR_CQB.png")
			ISFirearmRadialMenu.main["CCQB"].functions = self.GFRMSimKeyPress
			if GunTrajectory ~= "CQB" then
				ISFirearmRadialMenu.main["CCQB"].name = getText("IGUI_GFR_CQB")..'\n'..'['..getText("IGUI_GFR_Off")..']'
				ISFirearmRadialMenu.main["CCQB"].arguments = "TRAJECTORY_UP"
			elseif GunTrajectory == "CQB" then
				ISFirearmRadialMenu.main["CCQB"].name = getText("IGUI_GFR_CQB")..'\n'..'['..getText("IGUI_GFR_On")..']'
				ISFirearmRadialMenu.main["CCQB"].arguments = "TRAJECTORY_DOWN"
			end
		end


		-- Enter Melee Mode or Exit Launcher Mode
		if (GunSling and string.find(GunSling:getType(), "Launcher")) or GunMelee then
			ISFirearmRadialMenu.main["CMeleeLauncher"] = {}
			if GunisRanged then
				ISFirearmRadialMenu.main["CMeleeLauncher"].name = getText("IGUI_GFR_MeleeLauncher")..'\n'..'['..getText("IGUI_GFR_MeleeRanged")..']'
			else
				ISFirearmRadialMenu.main["CMeleeLauncher"].name = getText("IGUI_GFR_MeleeLauncher")..'\n'..'['..getText("IGUI_GFR_MeleeMelee")..']'
			end
			ISFirearmRadialMenu.main["CMeleeLauncher"].icons = getTexture("media/ui/GFR_MeleeLauncher.png")
			ISFirearmRadialMenu.main["CMeleeLauncher"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CMeleeLauncher"].arguments = "MELEE_MODE"
		end


		-- Deploy/Fold Stock, Toggle Bayonet for guns with built in bayonets (SKS)
		if GunFold then
			ISFirearmRadialMenu.main["CStockToggle"] = {}
			ISFirearmRadialMenu.main["CStockToggle"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CStockToggle"].arguments = "FOLD_STOCK"
			if string.find(GunType, "Fold") or string.find(GunFold, "Stock") then
				ISFirearmRadialMenu.main["CStockToggle"].name = getText("IGUI_GFR_StockToggle")..'\n'..'['..getText("IGUI_GFR_StockFolded")..']'
				ISFirearmRadialMenu.main["CStockToggle"].icons = getTexture("media/ui/GFR_StockToggle.png")
			elseif string.find(GunType, "Stock") or string.find(GunFold, "Fold") then
				ISFirearmRadialMenu.main["CStockToggle"].name = getText("IGUI_GFR_StockToggle")..'\n'..'['..getText("IGUI_GFR_StockExtended")..']'
				ISFirearmRadialMenu.main["CStockToggle"].icons = getTexture("media/ui/GFR_StockToggle.png")
			else 
				ISFirearmRadialMenu.main["CStockToggle"].name = getText("IGUI_GFR_StockBayonet")
				ISFirearmRadialMenu.main["CStockToggle"].icons = getTexture("media/ui/GFR_StockBayonet.png")
			end
		end


		-- Deploy/Fold Stock Bipod/Foregrip
		if (GunSling and string.find(GunSling:getType(), "Bipod")) or GunFold2 then
			ISFirearmRadialMenu.main["CBipodToggle"] = {}
			ISFirearmRadialMenu.main["CBipodToggle"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CBipodToggle"].arguments = "WEAPON_BIPOD"
			ISFirearmRadialMenu.main["CBipodToggle"].icons = getTexture("media/ui/GFR_BipodToggle.png")
			if (GunFold2 and (string.find(GunFold2, "_Bipod") or string.find(GunFold2, "_Grip"))) 
					or (GunSling and not string.find(GunSling:getType(), "_OPEN")) then
				ISFirearmRadialMenu.main["CBipodToggle"].name = getText("IGUI_GFR_BipodToggle")..'\n'..'['..getText("IGUI_GFR_StockFolded")..']'
			else
				ISFirearmRadialMenu.main["CBipodToggle"].name = getText("IGUI_GFR_BipodToggle")..'\n'..'['..getText("IGUI_GFR_StockExtended")..']'
			end
		end


		-- Cycle Available Grenades (Launcher)
		if (GunSling and string.find(GunSling:getType(), "Launcher")) or (GunIntegral and GunIntegral == "Launcher") or isLauncher(Gun) then
			ISFirearmRadialMenu.main["CCycleGrenade"] = {}
			if GunAmmoType == "Base.40HERound" then
				ISFirearmRadialMenu.main["CCycleGrenade"].name = getText("IGUI_GFR_CycleGrenade")..'\n'..'['..getText("IGUI_GFR_CycleGrenadeHE")..']'
			elseif GunAmmoType == "Base.40INCRound" then
				ISFirearmRadialMenu.main["CCycleGrenade"].name = getText("IGUI_GFR_CycleGrenade")..'\n'..'['..getText("IGUI_GFR_CycleGrenadeINC")..']'
			else
				ISFirearmRadialMenu.main["CCycleGrenade"].name = getText("IGUI_GFR_CycleGrenade")
			end
			ISFirearmRadialMenu.main["CCycleGrenade"].icons = getTexture("media/ui/GFR_CycleGrenade.png")
			ISFirearmRadialMenu.main["CCycleGrenade"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CCycleGrenade"].arguments = "AMMO_TYPE_2"
		end


		-- Cycle Available Calibers
		if Gun:getModData().CompAmmo and not ((GunSling and string.find(GunSling:getType(), "Launcher")) or isLauncher(Gun)) then
			local Caliber = getScriptManager():FindItem(GunAmmoType):getDisplayName()
			Caliber = Caliber:gsub(" Round", "")
			ISFirearmRadialMenu.main["CCycleCaliber"] = {}
			ISFirearmRadialMenu.main["CCycleCaliber"].name = getText("IGUI_GFR_CycleCaliber")..'\n'..'['..Caliber..']'
			ISFirearmRadialMenu.main["CCycleCaliber"].icons = getTexture("media/ui/GFR_CycleCaliber.png")
			ISFirearmRadialMenu.main["CCycleCaliber"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CCycleCaliber"].arguments = "AMMO_TYPE_1"
		end


		-- Cycle Available Fire Modes
		if Gun:getFireModePossibilities() then
			ISFirearmRadialMenu.main["CCycleFireMode"] = {}
			if Gun:getFireMode() then
				ISFirearmRadialMenu.main["CCycleFireMode"].name = getText("IGUI_GFR_CycleFiremode")..'\n'..'['..Gun:getFireMode()..']'
			else
				ISFirearmRadialMenu.main["CCycleFireMode"].name = getText("IGUI_GFR_CycleFiremode")
			end
			ISFirearmRadialMenu.main["CCycleFireMode"].icons = getTexture("media/ui/GFR_CycleFiremode.png")
			ISFirearmRadialMenu.main["CCycleFireMode"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CCycleFireMode"].arguments = "SELECT_FIRE"
		end


		-- Toggle Attached Gun Light/Laser
		if GunStock and (GunLight or GunLaser) then
			ISFirearmRadialMenu.main["CGunLight"] = {}
			ISFirearmRadialMenu.main["CGunLight"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CGunLight"].arguments = "WEAPON_LIGHT"
			if GunLaser and playerObj:isAiming() then
				if GunLaserOn ~= true then
					ISFirearmRadialMenu.main["CGunLight"].name = getText("IGUI_GFR_GunLaser")..'\n'..'['..getText("IGUI_GFR_Off")..']'
					ISFirearmRadialMenu.main["CGunLight"].icons = getTexture("media/ui/GFR_LaserOn.png")
				else
					ISFirearmRadialMenu.main["CGunLight"].name = getText("IGUI_GFR_GunLaser")..'\n'..'['..getText("IGUI_GFR_On")..']'
					ISFirearmRadialMenu.main["CGunLight"].icons = getTexture("media/ui/GFR_LaserOff.png")
				end
			else
				if GunLightOn ~= true then
					ISFirearmRadialMenu.main["CGunLight"].name = getText("IGUI_GFR_GunLight")..'\n'..'['..getText("IGUI_GFR_Off")..']'
					ISFirearmRadialMenu.main["CGunLight"].icons = getTexture("media/ui/GFR_GunLightOn.png")
				else
					ISFirearmRadialMenu.main["CGunLight"].name = getText("IGUI_GFR_GunLight")..'\n'..'['..getText("IGUI_GFR_On")..']'
					ISFirearmRadialMenu.main["CGunLight"].icons = getTexture("media/ui/GFR_GunLightOff.png")
				end
			end
		end


		-- Weapon Mounted Night-Vision Optics
		local nvctrl = require "NVAPI/ctrl/instance"
		local nvcheck = isWearingNightVision
		local WearingNVG = nil
		if nvctrl ~= nil and nvcheck ~= nil then
			WearingNVG = isWearingNightVision(playerObj)
			if (GunScope and GunScope:hasTag("NVITEM")) or WearingNVG then
				ISFirearmRadialMenu.main["CNVO"] = {}
				ISFirearmRadialMenu.main["CNVO"].functions = self.GFRMSimKeyPress
				ISFirearmRadialMenu.main["CNVO"].arguments = "WEAPON_NVG"
				ISFirearmRadialMenu.main["CNVO"].icons = getTexture("media/ui/GFR_NV.png")
				if WearingNVG then
					ISFirearmRadialMenu.main["CNVO"].name = getText("IGUI_GFR_NVToggle")
				else
					if playerObj:isAiming() then
						ISFirearmRadialMenu.main["CNVO"].name = getText("IGUI_GFR_NVToggle")
					else
						if GUNFIGHTER.OPTIONS.options.dropdown133 == 3 then
							ISFirearmRadialMenu.main["CNVO"].name = getText("IGUI_GFR_NVMode")..'\n'..'['..getText("IGUI_GFR_NVModeAuto")..']'
						elseif GUNFIGHTER.OPTIONS.options.dropdown133 == 2 then
							ISFirearmRadialMenu.main["CNVO"].name = getText("IGUI_GFR_NVMode")..'\n'..'['..getText("IGUI_GFR_NVModeManual")..']'
						else
							ISFirearmRadialMenu.main["CNVO"].name = getText("IGUI_GFR_NVMode")..'\n'..'['..getText("IGUI_GFR_Off")..']'
						end
					end
				end
			end
		end


		-- Expanded Information Window
		if GunType then
			ISFirearmRadialMenu.main["CInfo"] = {}
			ISFirearmRadialMenu.main["CInfo"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CInfo"].arguments = "GUNFIGHTER_WINDOW"
			ISFirearmRadialMenu.main["CInfo"].icons = getTexture("media/ui/GFR_Info.png")
			ISFirearmRadialMenu.main["CInfo"].name = getText("IGUI_GFR_Info")
		end

	else 									------------------------------ GunFighter Version Legacy ------------------------------

		-- Magazine Functions
		if GunisRanged and (MagTypeExt or MagTypeDrum) then
			local result = InventoryItemFactory.CreateItem(GunType)
			local MagTypeStd = result:getMagazineType()
			ISFirearmRadialMenu.main["CMagType"] = {}
			if MagType == MagTypeStd then
				ISFirearmRadialMenu.main["CMagType"].name = getText("IGUI_GFR_MagType") .. '\n' .. '[' .. getText("IGUI_GFR_MagTypeStd") .. ']'
			elseif MagType == MagTypeExt then
				ISFirearmRadialMenu.main["CMagType"].name = getText("IGUI_GFR_MagType") .. '\n' .. '[' .. getText("IGUI_GFR_MagTypeExt") .. ']'
			elseif MagType == MagTypeDrum then
				ISFirearmRadialMenu.main["CMagType"].name = getText("IGUI_GFR_MagType") .. '\n' .. '[' .. getText("IGUI_GFR_MagTypeDrum") .. ']'
			end
			ISFirearmRadialMenu.main["CMagType"].icons = getTexture("media/ui/GFR_MagType.png")
			ISFirearmRadialMenu.main["CMagType"].subMenu = {}
			-- To Standard
			if MagType ~= MagTypeStd then
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeStd"] = {}
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeStd"].name = getText("IGUI_GFR_MagTypeStd")
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeStd"].icons = getTexture("media/ui/GFR_MagTypeStd.png")
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeStd"].functions = self.GFRMSimKeyPressMagtype
				if MagType == MagTypeExt then
					ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeStd"].arguments = "MAG_TYPE_2"
				elseif MagType == MagTypeDrum then
					ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeStd"].arguments = "MAG_TYPE_1"
				end
			end
			-- To Extended -- and self.character:getInventory():contains(MagTypeExt) 
			if MagTypeExt and MagType ~= MagTypeExt then
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeExt"] = {}
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeExt"].name = getText("IGUI_GFR_MagTypeExt")
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeExt"].icons = getTexture("media/ui/GFR_MagTypeExt.png")
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeExt"].functions = self.GFRMSimKeyPressMagtype
				if MagType == MagTypeStd then
					ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeExt"].arguments = "MAG_TYPE_1"
				elseif MagType == MagTypeDrum then
					ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeExt"].arguments = "MAG_TYPE_2"
				end
			end
			-- -- To Drum
			if MagTypeDrum and MagType ~= MagTypeDrum then
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeDrum"] = {}
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeDrum"].name = getText("IGUI_GFR_MagTypeDrum")
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeDrum"].icons = getTexture("media/ui/GFR_MagTypeDrum.png")
				ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeDrum"].functions = self.GFRMSimKeyPressMagtype
				if MagType == MagTypeStd then
					ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeDrum"].arguments = "MAG_TYPE_2"
				elseif MagType == MagTypeExt then
					ISFirearmRadialMenu.main["CMagType"].subMenu["MagTypeDrum"].arguments = "MAG_TYPE_1"
				end
			end
		end


		-- Alternate Load - Load with Magazine or Clip
		if Gun:getModData().ClipType and Gun:getModData().MagType then
			if Gun:getModData().MagType ~= "Base.SKSFixedBox" then
				ISFirearmRadialMenu.main["CAltLoad"] = {}
				if MagType == Gun:getModData().MagType then
					ISFirearmRadialMenu.main["CAltLoad"].name = getText("IGUI_GFR_AltLoad")..'\n'..'['..getText("IGUI_GFR_AltLoadMag")..']'
				end
				if MagType == Gun:getModData().ClipType then
					ISFirearmRadialMenu.main["CAltLoad"].name = getText("IGUI_GFR_AltLoad")..'\n'..'['..getText("IGUI_GFR_AltLoadClip")..']'
				end
				ISFirearmRadialMenu.main["CAltLoad"].icons = getTexture("media/ui/GFR_AltLoad.png")
				ISFirearmRadialMenu.main["CAltLoad"].functions = self.GFRMSimKeyPress
				ISFirearmRadialMenu.main["CAltLoad"].arguments = "ALT_LOAD"
			end
		end


		-- Launcher Trajectory Up/Down
		if 	(isFlamer(Gun) or isLauncher(Gun)) and
			(not string.find(GunType, "P21_Chainsaw")) and
			(not string.find(GunType, "WD_Flame")) then
				ISFirearmRadialMenu.main["CRangeUp"] = {}
				ISFirearmRadialMenu.main["CRangeUp"].name = getText("IGUI_GFR_RangeUp")..'\n'..'['..GunTrajectory..']'
				ISFirearmRadialMenu.main["CRangeUp"].icons = getTexture("media/ui/GFR_RangeUp.png")
				ISFirearmRadialMenu.main["CRangeUp"].functions = self.GFRMSimKeyPress
				ISFirearmRadialMenu.main["CRangeUp"].arguments = "TRAJECTORY_UP"
				ISFirearmRadialMenu.main["CRangeDown"] = {}
				ISFirearmRadialMenu.main["CRangeDown"].name = getText("IGUI_GFR_RangeDown")..'\n'..'['..GunTrajectory..']'
				ISFirearmRadialMenu.main["CRangeDown"].icons = getTexture("media/ui/GFR_RangeDown.png")
				ISFirearmRadialMenu.main["CRangeDown"].functions = self.GFRMSimKeyPress
				ISFirearmRadialMenu.main["CRangeDown"].arguments = "TRAJECTORY_DOWN"
		
		-- CQB Toggle
		elseif GunisRanged then
			ISFirearmRadialMenu.main["CCQB"] = {}
			ISFirearmRadialMenu.main["CCQB"].icons = getTexture("media/ui/GFR_CQB.png")
			ISFirearmRadialMenu.main["CCQB"].functions = self.GFRMSimKeyPress
			if GunTrajectory ~= "CQB" then
				ISFirearmRadialMenu.main["CCQB"].name = getText("IGUI_GFR_CQB")..'\n'..'['..getText("IGUI_GFR_Off")..']'
				ISFirearmRadialMenu.main["CCQB"].arguments = "TRAJECTORY_DOWN"
			elseif GunTrajectory == "CQB" then
				ISFirearmRadialMenu.main["CCQB"].name = getText("IGUI_GFR_CQB")..'\n'..'['..getText("IGUI_GFR_On")..']'
				ISFirearmRadialMenu.main["CCQB"].arguments = "TRAJECTORY_UP"
			end
		end


		-- Enter Melee Mode or Exit Launcher Mode
		if (GunSling and string.find(GunSling:getType(), "Launcher")) or GunMelee then
			ISFirearmRadialMenu.main["CMeleeLauncher"] = {}
			if GunisRanged then
				ISFirearmRadialMenu.main["CMeleeLauncher"].name = getText("IGUI_GFR_MeleeLauncher")..'\n'..'['..getText("IGUI_GFR_MeleeRanged")..']'
			else
				ISFirearmRadialMenu.main["CMeleeLauncher"].name = getText("IGUI_GFR_MeleeLauncher")..'\n'..'['..getText("IGUI_GFR_MeleeMelee")..']'
			end
			ISFirearmRadialMenu.main["CMeleeLauncher"].icons = getTexture("media/ui/GFR_MeleeLauncher.png")
			ISFirearmRadialMenu.main["CMeleeLauncher"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CMeleeLauncher"].arguments = "MELEE_MODE"
		end


		-- Deploy/Fold Stock, Toggle Bayonet for guns with built in bayonets (SKS)
		if GunFold then
			ISFirearmRadialMenu.main["CStockToggle"] = {}
			ISFirearmRadialMenu.main["CStockToggle"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CStockToggle"].arguments = "FOLD_STOCK"
			if string.find(GunType, "Fold") or string.find(GunFold, "Stock") then
				ISFirearmRadialMenu.main["CStockToggle"].name = getText("IGUI_GFR_StockToggle")..'\n'..'['..getText("IGUI_GFR_StockFolded")..']'
				ISFirearmRadialMenu.main["CStockToggle"].icons = getTexture("media/ui/GFR_StockToggle.png")
			elseif string.find(GunType, "Stock") or string.find(GunFold, "Fold") then
				ISFirearmRadialMenu.main["CStockToggle"].name = getText("IGUI_GFR_StockToggle")..'\n'..'['..getText("IGUI_GFR_StockExtended")..']'
				ISFirearmRadialMenu.main["CStockToggle"].icons = getTexture("media/ui/GFR_StockToggle.png")
			else 
				ISFirearmRadialMenu.main["CStockToggle"].name = getText("IGUI_GFR_StockBayonet")
				ISFirearmRadialMenu.main["CStockToggle"].icons = getTexture("media/ui/GFR_StockBayonet.png")
			end
		end


		-- Cycle Available Grenades (Launcher)
		if (GunSling and string.find(GunSling:getType(), "Launcher")) or (GunIntegral and GunIntegral == "Launcher") or isLauncher(Gun) then
			ISFirearmRadialMenu.main["CCycleGrenade"] = {}
			if GunAmmoType == "Base.40HERound" then
				ISFirearmRadialMenu.main["CCycleGrenade"].name = getText("IGUI_GFR_CycleGrenade")..'\n'..'['..getText("IGUI_GFR_CycleGrenadeHE")..']'
			elseif GunAmmoType == "Base.40INCRound" then
				ISFirearmRadialMenu.main["CCycleGrenade"].name = getText("IGUI_GFR_CycleGrenade")..'\n'..'['..getText("IGUI_GFR_CycleGrenadeINC")..']'
			else
				ISFirearmRadialMenu.main["CCycleGrenade"].name = getText("IGUI_GFR_CycleGrenade")
			end
			ISFirearmRadialMenu.main["CCycleGrenade"].icons = getTexture("media/ui/GFR_CycleGrenade.png")
			ISFirearmRadialMenu.main["CCycleGrenade"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CCycleGrenade"].arguments = "AMMO_TYPE_2"
		end


		-- Cycle Available Calibers
		if Gun:getModData().CompAmmo and not ((GunSling and string.find(GunSling:getType(), "Launcher")) or isLauncher(Gun)) then
			local Caliber = getScriptManager():FindItem(GunAmmoType):getDisplayName()
			Caliber = Caliber:gsub(" Round", "")
			ISFirearmRadialMenu.main["CCycleCaliber"] = {}
			ISFirearmRadialMenu.main["CCycleCaliber"].name = getText("IGUI_GFR_CycleCaliber")..'\n'..'['..Caliber..']'
			ISFirearmRadialMenu.main["CCycleCaliber"].icons = getTexture("media/ui/GFR_CycleCaliber.png")
			ISFirearmRadialMenu.main["CCycleCaliber"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CCycleCaliber"].arguments = "AMMO_TYPE_1"
		end


		-- Cycle Available Fire Modes
		if Gun:getFireModePossibilities() then
			ISFirearmRadialMenu.main["CCycleFireMode"] = {}
			if Gun:getFireMode() then
				ISFirearmRadialMenu.main["CCycleFireMode"].name = getText("IGUI_GFR_CycleFiremode")..'\n'..'['..Gun:getFireMode()..']'
			else
				ISFirearmRadialMenu.main["CCycleFireMode"].name = getText("IGUI_GFR_CycleFiremode")
			end
			ISFirearmRadialMenu.main["CCycleFireMode"].icons = getTexture("media/ui/GFR_CycleFiremode.png")
			ISFirearmRadialMenu.main["CCycleFireMode"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CCycleFireMode"].arguments = "SELECT_FIRE"
		end


		-- Toggle Attached Gun Light/Laser
		if GunStock and (GunLight or string.find(GunStock:getType(), "PEQ") or string.find(GunStock:getType(), "DVAL")) then
			ISFirearmRadialMenu.main["CGunLight"] = {}
			ISFirearmRadialMenu.main["CGunLight"].functions = self.GFRMSimKeyPress
			ISFirearmRadialMenu.main["CGunLight"].arguments = "WEAPON_LIGHT"
			if GunLightOn ~= true then
				ISFirearmRadialMenu.main["CGunLight"].name = getText("IGUI_GFR_GunLight")..'\n'..'['..getText("IGUI_GFR_Off")..']'
				ISFirearmRadialMenu.main["CGunLight"].icons = getTexture("media/ui/GFR_GunLightOn.png")
			else
				ISFirearmRadialMenu.main["CGunLight"].name = getText("IGUI_GFR_GunLight")..'\n'..'['..getText("IGUI_GFR_On")..']'
				ISFirearmRadialMenu.main["CGunLight"].icons = getTexture("media/ui/GFR_GunLightOff.png")
			end
		end


	end


	if not submenu then -- Main menu buttons
		for i,v in pairs(ISFirearmRadialMenu.main) do
			if v.subMenu then
				menu:addSlice(v.name, v.icons, self.fillMenu, self, i) -- Button with submenu
			else 
				menu:addSlice(v.name, v.icons, v.functions, self, v.arguments)  -- Button without submenu
			end
		end
	else -- Submenu Buttons
		menu:clear()
		for _,v in pairs(ISFirearmRadialMenu.main[submenu].subMenu) do
			menu:addSlice(v.name, v.icons, v.functions, self, v.arguments)
		end
		menu:addSlice(getText("IGUI_Emote_Back"), getTexture("media/ui/emotes/back.png"), self.fillMenu, self)
	end
	self:display()
end

function ISFirearmRadialMenu:GFRMSimKeyPress(arguments)
	if arguments == nil then return end
	triggerEvent('OnKeyPressed', getCore():getKey(arguments))
end

-- Timed action to sequence Eject Magazine > Change Magazine Type
GFRMChangeMagtype = ISBaseTimedAction:derive("GFRMChangeMagtype");
function GFRMChangeMagtype:new(frm, arguments)
	local o = ISBaseTimedAction.new(self, character)
	o.frm = frm
    o.character = frm.character
	o.stopOnWalk = false
	o.stopOnRun = true
	o.stopOnAim = false
	o.maxTime = 1
	o.useProgressBar = false
	o.arguments = arguments
	return o
end
function GFRMChangeMagtype:isValid() return true end;
function GFRMChangeMagtype:stop() ISBaseTimedAction.stop(self) end;
function GFRMChangeMagtype:start() end;
function GFRMChangeMagtype:perform()
	triggerEvent('OnKeyPressed', getCore():getKey(self.arguments))
	ISBaseTimedAction.perform(self)
end

function ISFirearmRadialMenu:GFRMSimKeyPressMagtype(arguments)
	if arguments == nil then return end
	local Gun = self:getWeapon()
	if (Gun:isContainsClip() == true) and (Gun:getMagazineType() ~= "Base.Fixed") then
		ISTimedActionQueue.add(ISEjectMagazine:new(self.character, Gun))
	end
	ISTimedActionQueue.add(GFRMChangeMagtype:new(self, arguments))
end

function ISFirearmRadialMenu.checkWeapon(playerObj)
	local weapon = playerObj:getPrimaryHandItem()
	if not weapon then return false end
	if not instanceof(weapon, "HandWeapon") then return false end
	-- if not weapon:isAimedFirearm() then return false end 		-- GunFighter Radial Menu -- Changed weapon:isRanged() to weapon:isAimedFirearm() to enable for Melee Mode.
	return true
end

Events.OnGameStart.Add(OnGameStart)