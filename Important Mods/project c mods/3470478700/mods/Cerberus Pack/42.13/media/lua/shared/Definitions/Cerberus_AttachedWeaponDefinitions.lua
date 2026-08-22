-- define weapons to be attached to zombies when creating them
-- random knives inside their neck, spear in their stomach, meatcleaver in their back...
-- this is used in IsoZombie.addRandomAttachedWeapon()



-- assault rifle on back
AttachedWeaponDefinitions.assaultRifleOnBackCerberus = {
	id = "assaultRifleOnBackCerberus",
	chance = 70,
	outfit = {"Cerberus", "Cerberus_Mandible", "Cerberus_BallisticMask"},
	weaponLocation =  {"Rifle On Back", "Rifle On Back with Bag"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.AssaultRifle",
		"Base.AssaultRifle2",
	},
}

-- random weapon on Cerberus zombies holster
AttachedWeaponDefinitions.handgunHolsterCerberus = {
	id = "handgunHolsterCerberus",
	chance = 70,
	outfit = {"Cerberus", "Cerberus_Mandible", "Cerberus_BallisticMask", "Cerberus_Clothes"},
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
AttachedWeaponDefinitions.knivesBeltCerberus = {
	chance = 10,
	outfit = {"Cerberus", "Cerberus_Mandible", "Cerberus_BallisticMask"},
	weaponLocation = {"Belt Left Upside"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.HuntingKnife",
	},
}



AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackCerberus,
		AttachedWeaponDefinitions.handgunHolsterCerberus,
		AttachedWeaponDefinitions.knivesBeltCerberus,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus_Mandible = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackCerberus,
		AttachedWeaponDefinitions.handgunHolsterCerberus,
		AttachedWeaponDefinitions.knivesBeltCerberus,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus_BallisticMask = {
	chance = 70;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackCerberus,
		AttachedWeaponDefinitions.handgunHolsterCerberus,
		AttachedWeaponDefinitions.knivesBeltCerberus,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus_Clothes = {
	chance = 70;
	maxitem = 1;
	weapons = {
		AttachedWeaponDefinitions.handgunHolsterCerberus,
	},
}
