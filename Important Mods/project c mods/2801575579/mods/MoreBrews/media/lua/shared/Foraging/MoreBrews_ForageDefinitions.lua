require 'Foraging/forageSystem'

MoreBrews = MoreBrews or {};

local sVars = SandboxVars.MoreBrews;
sVars.ForageHops = sVars.ForageHops or false;
sVars.ForageHopsRate = sVars.ForageHopsRate or 5;

if sVars.ForageHops == false then
	sVars.ForageHopsRate = 0
end

forageDefs.HopsCascade = {
		type = "MoreBrews.HopsCascade",
		minCount = 1,
		maxCount = 1,
		skill = 3,
		xp = 10,
		categories = { "WildPlants" },
		zones = {
			Forest          = sVars.ForageHopsRate,
			Vegitation      = sVars.ForageHopsRate,
			FarmLand        = sVars.ForageHopsRate,
			Farm            = sVars.ForageHopsRate,
		},
		months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
		bonusMonths = { 6, 7, 8 },
		malusMonths = { 11, 12 },
}

forageDefs.HopsCentennial = {
	type = "MoreBrews.HopsCentennial",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}

forageDefs.HopsCitra = {
	type = "MoreBrews.HopsCitra",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}

forageDefs.HopsGolding = {
	type = "MoreBrews.HopsGolding",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}

forageDefs.HopsHallertau = {
	type = "MoreBrews.HopsHallertau",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}

forageDefs.HopsMosaic = {
	type = "MoreBrews.HopsMosaic",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}

forageDefs.HopsNugget = {
	type = "MoreBrews.HopsNugget",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}

forageDefs.HopsSaaz = {
	type = "MoreBrews.HopsSaaz",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}

forageDefs.HopsSimcoe = {
	type = "MoreBrews.HopsSimcoe",
	minCount = 1,
	maxCount = 1,
	skill = 3,
	xp = 10,
	categories = { "WildPlants" },
	zones = {
		Forest          = sVars.ForageHopsRate,
		Vegitation      = sVars.ForageHopsRate,
		FarmLand        = sVars.ForageHopsRate,
		Farm            = sVars.ForageHopsRate,
	},
	months = { 3, 4, 5, 6, 7, 8, 9, 10, 11 },
	bonusMonths = { 6, 7, 8 },
	malusMonths = { 11, 12 },
}