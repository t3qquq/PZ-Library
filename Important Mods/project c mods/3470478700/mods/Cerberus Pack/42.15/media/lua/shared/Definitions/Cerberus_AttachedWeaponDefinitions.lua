-- define weapons to be attached to zombies when creating them
-- random knives inside their neck, spear in their stomach, meatcleaver in their back...
-- this is used in IsoZombie.addRandomAttachedWeapon()



-- assault rifle on back
AttachedWeaponDefinitions.assaultRifleOnBackCerberus = {
	id = "assaultRifleOnBackCerberus",
	chance = 25,
	outfit = {"Cerberus_LightArmored", "Cerberus_MediumArmored", "Cerberus_HeavyArmored"},
	weaponLocation =  {"Rifle On Back", "Rifle On Back with Bag"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.AssaultRifle",
	},
}

-- random weapon on Cerberus zombies holster
AttachedWeaponDefinitions.handgunHolsterCerberus = {
	id = "handgunHolsterCerberus",
	chance = 25,
	outfit = {"Cerberus_NonArmoredUniformed", "Cerberus_LightArmored", "Cerberus_MediumArmored", "Cerberus_HeavyArmored"},
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
	chance = 25,
	outfit = {"Cerberus_LightArmored", "Cerberus_MediumArmored", "Cerberus_HeavyArmored"},
	weaponLocation = {"Belt Left Upside"},
	bloodLocations = nil,
	addHoles = false,
	daySurvived = 0,
	weapons = {
		"Base.HuntingKnife",
	},
}



AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus_NonArmoredUniformed = {
	chance = 10;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.handgunHolsterCerberus,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus_LightArmored = {
	chance = 10;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackCerberus,
		AttachedWeaponDefinitions.handgunHolsterCerberus,
		AttachedWeaponDefinitions.knivesBeltCerberus,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus_MediumArmored = {
	chance = 10;
	maxitem = 3;
	weapons = {
		AttachedWeaponDefinitions.assaultRifleOnBackCerberus,
		AttachedWeaponDefinitions.handgunHolsterCerberus,
		AttachedWeaponDefinitions.knivesBeltCerberus,
	},
}

AttachedWeaponDefinitions.attachedWeaponCustomOutfit.Cerberus_HeavyArmored = {
	chance = 10;
	maxitem = 1;
	weapons = {
			AttachedWeaponDefinitions.assaultRifleOnBackCerberus,
			AttachedWeaponDefinitions.handgunHolsterCerberus,
			AttachedWeaponDefinitions.knivesBeltCerberus,
	},
}
