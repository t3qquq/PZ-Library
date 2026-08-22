-- define weapons to be attached to zombies when creating them
-- random knives inside their neck, spear in their stomach, meatcleaver in their back...
-- this is used in IsoZombie.addRandomAttachedWeapon()



-- assault rifle on back
AttachedWeaponDefinitions.assaultRifleOnBackLynx = {
	id = "assaultRifleOnBackLynx",
	chance = 70,
	outfit = {"Lynx_Black", "Lynx_Green", "Lynx_BallisticFaceShield_Black", "Lynx_BallisticFaceShield_Green", "Lynx_RysT_Black", "Lynx_RysT_Green"},
	weaponLocation =  {"Rifle On Back", "Rifle On Back with Bag"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.AssaultRifle",
		"Base.AssaultRifle2",
	},
}

-- random weapon on Lynx zombies holster
AttachedWeaponDefinitions.handgunHolsterLynx = {
	id = "handgunHolsterLynx",
	chance = 70,
	outfit = {"Lynx_Black", "Lynx_Green", "Lynx_BallisticFaceShield_Black", "Lynx_BallisticFaceShield_Green", "Lynx_RysT_Black", "Lynx_RysT_Green", "Lynx_Clothes"},
	weaponLocation =  {"Holster Right"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	ensureItem = "Base.HolsterSimple",
	weapons = {
		"Base.Pistol",
		"Base.Pistol2",
		"Base.Pistol3",
	},
}

-- knives in belt left
AttachedWeaponDefinitions.knivesBeltLynx = {
	chance = 10,
	outfit = {"Lynx_Black", "Lynx_Green", "Lynx_BallisticFaceShield_Black", "Lynx_BallisticFaceShield_Green", "Lynx_RysT_Black", "Lynx_RysT_Green"},
	weaponLocation = {"Belt Left Upside"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.HuntingKnife",
	},
}



AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Lynx_Black = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackLynx,
		AttachedWeaponDefinitions.handgunHolsterLynx,
		AttachedWeaponDefinitions.knivesBeltLynx,
	},
}
AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Lynx_Green = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackLynx,
		AttachedWeaponDefinitions.handgunHolsterLynx,
		AttachedWeaponDefinitions.knivesBeltLynx,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Lynx_BallisticFaceShield_Black = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackLynx,
		AttachedWeaponDefinitions.handgunHolsterLynx,
		AttachedWeaponDefinitions.knivesBeltLynx,
	},
}
AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Lynx_BallisticFaceShield_Green = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackLynx,
		AttachedWeaponDefinitions.handgunHolsterLynx,
		AttachedWeaponDefinitions.knivesBeltLynx,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Lynx_RysT_Black = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackLynx,
		AttachedWeaponDefinitions.handgunHolsterLynx,
		AttachedWeaponDefinitions.knivesBeltLynx,
	},
}
AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Lynx_RysT_Green = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackLynx,
		AttachedWeaponDefinitions.handgunHolsterLynx,
		AttachedWeaponDefinitions.knivesBeltLynx,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Lynx_Clothes = {
	chance = 70;
	maxitem = 1;
	weapons = {
		AttachedWeaponDefinitions.handgunHolsterLynx,
	},
}
