-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

BB_WhereAmI = {}
BB_WhereAmI.currentArea = ""

BB_WhereAmI.areaDB = {

	-- MODDED
	{
		name = "Ashenwood, KY",
		mod = "Ashenwood",
		activated = false,
		startX = 11400,
		endX = 11700,
		startY = 11100,
		endY = 11700,
	},
	{
		name = "Bedford Falls, KY",
		mod = "BedfordFalls",
		activated = false,
		startX = 12900,
		endX = 15000,
		startY = 9900,
		endY = 11400,
	},
	{
		name = "Big Bear Lake, KY",
		mod = "BBL",
		activated = false,
		startX = 4800,
		endX = 7200,
		startY = 6900,
		endY = 7800,
	},
	{
		name = "Blackwood, KY",
		mod = "Blackwood",
		activated = false,
		startX = 7800,
		endX = 8100,
		startY = 10500,
		endY = 10800,
	},
	{
		name = "Cedar Hill, KY",
		mod = "CedarHill",
		activated = false,
		startX = 4800,
		endX = 5100,
		startY = 5700,
		endY = 6000,
	},
	{
		name = "Chestown, KY",
		mod = "Chestown",
		activated = false,
		startX = 4500,
		endX = 4800,
		startY = 6600,
		endY = 6900,
	},
	{
		name = "Elysium Island, KY",
		mod = "Elysium_Island",
		activated = false,
		startX = 10500,
		endX = 10800,
		startY = 6600,
		endY = 6900,
	},
	{
		name = "Fort Redstone, KY",
		mod = "FORTREDSTONE",
		activated = false,
		startX = 5400,
		endX = 6000,
		startY = 11100,
		endY = 12300,
	},
	{
		name = "Fort Rock Ridge, KY",
		mod = "Fort Rock Ridge",
		activated = false,
		startX = 6600,
		endX = 7200,
		startY = 6000,
		endY = 6300,
	},
	{
		name = "Grapeseed, KY",
		mod = "Grapeseed",
		activated = false,
		startX = 7200,
		endX = 7500,
		startY = 11100,
		endY = 11400,
	},
	{
		name = "Greenleaf, KY",
		mod = "Greenleaf",
		activated = false,
		startX = 6300,
		endX = 6600,
		startY = 10200,
		endY = 10500,
	},
	{
		name = "Kingsmouth, KY",
		mod = "KingsmouthKY",
		activated = false,
		startX = 3000,
		endX = 4200,
		startY = 4200,
		endY = 6800,
	},
	{
		name = "Lake Ivy Township, KY",
		mod = "lakeivytownship",
		activated = false,
		startX = 8700,
		endX = 9300,
		startY = 9600,
		endY = 10500,
	},
	{
		name = "Petroville, KY",
		mod = "Petroville",
		activated = false,
		startX = 10500,
		endX = 11100,
		startY = 11700,
		endY = 12600,
	},
	{
		name = "Pitstop, KY",
		mod = "Pitstop",
		activated = false,
		startX = 9600,
		endX = 9900,
		startY = 11100,
		endY = 11400,
	},
	{
		name = "Raven Creek, KY",
		mod = "RavenCreek",
		activated = false,
		startX = 3000,
		endX = 5400,
		startY = 11100,
		endY = 13500,
	},
	{
		name = "Seaside, KY",
		mod = "Seaside",
		activated = false,
		startX = 12300,
		endX = 12600,
		startY = 900,
		endY = 1200,
	},
	{
		name = "The Frigate, KY",
		mod = "The Frigate",
		activated = false,
		startX = 12300,
		endX = 12600,
		startY = 6300,
		endY = 6600,
	},
	{
		name = "Utopia, KY",
		mod = "Utopia",
		activated = false,
		startX = 7200,
		endX = 7500,
		startY = 9600,
		endY = 9900,
	},
	{
		name = "Fort Knox, KY",
		mod = "FortKnoxLinked",
		activated = false,
		startX = 12300,
		endX = 15899,
		startY = 13200,
		endY = 17999
	},
	{
		name = "Road between Fort Knox and Bedford Falls, KY",
		mod = "FortKnoxRoad",
		activated = false,
		startX = 13500,
		endX = 13799,
		startY = 11400,
		endY = 13199
	},
	{
		name = "Road from Monmouth County to Bedford Falls, KY",
		mod = "RfMCtBF_addon",
		activated = false,
		startX = 12900,
		endX = 13499,
		startY = 8100,
		endY = 8399
	},
	{
		name = "Monmouth County, KY",
		mod = "MonmouthCounty_new",
		activated = false,
		startX = 11700,
		endX = 12899,
		startY = 7800,
		endY = 8999
	},
	{
		name = "Addams Family Mansion, KY",
		mod = "AddamsMansion",
		activated = false,
		startX = 11100,
		endX = 11399,
		startY = 9300,
		endY = 9599
	},
	{
		name = "Bendy's Bunker, KY",
		mod = "Bendys Bunker v2",
		activated = false,
		startX = 6600,
		endX = 6899,
		startY = 8100,
		endY = 8399
	},
	{
		name = "Betsy's Farm, KY",
		mod = "DJBetsysFarm",
		activated = false,
		startX = 9000,
		endX = 9299,
		startY = 9300,
		endY = 9599
	},
	{
		name = "Breakpoint, KY",
		mod = "Breakpoint",
		activated = false,
		startX = 12600,
		endX = 12899,
		startY = 4800,
		endY = 5099
	},
	{
		name = "Bunker = Day of the Dead, KY",
		mod = "BunkerDayOfTheDead",
		activated = false,
		startX = 6000,
		endX = 6299,
		startY = 10500,
		endY = 10799
	},
	{
		name = "Bunker = Last Minute Prepper, KY",
		mod = "LastMinutePrepperReloaded",
		activated = false,
		startX = 13200,
		endX = 13499,
		startY = 3600,
		endY = 3899
	},
	{
		name = "C.O.N. Research & Testing Facility, KY",
		mod = "CONRTF",
		activated = false,
		startX = 9300,
		endX = 9599,
		startY = 12600,
		endY = 12899
	},
	{
		name = "Canvasback Studios, KY",
		mod = "Canvasback Studios",
		activated = false,
		startX = 9900,
		endX = 10199,
		startY = 10200,
		endY = 10499
	},
	{
		name = "Chernaville, KY",
		mod = "Chernaville",
		activated = false,
		startX = 9600,
		endX = 9899,
		startY = 10200,
		endY = 10799
	},
	{
		name = "Chinatown, KY",
		mod = "Chinatown",
		activated = false,
		startX = 11100,
		endX = 11399,
		startY = 8700,
		endY = 8999
	},
	{
		name = "Chinatown Expansion, KY",
		mod = "Chinatown expansion",
		activated = false,
		startX = 10800,
		endX = 11099,
		startY = 8400,
		endY = 8999
	},
	{
		name = "Cigaro Gated Community, KY",
		mod = "CigaroHouse",
		activated = false,
		startX = 7200,
		endX = 7499,
		startY = 5400,
		endY = 5699
	},
	{
		name = "Ciudad Madriguera, KY",
		mod = "CM_Rivendel",
		activated = false,
		startX = 10500,
		endX = 10799,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Corn Farm, KY",
		mod = "FarmHouseMatrioshka",
		activated = false,
		startX = 9600,
		endX = 9899,
		startY = 7500,
		endY = 7799
	},
	{
		name = "Coryerdon Over the River Road, KY",
		mod = "Over the River by JW-Corydondoner",
		activated = false,
		startX = 9900,
		endX = 10199,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Cotton Candy Mine, KY",
		mod = "CottonCandyMine",
		activated = false,
		startX = 9600,
		endX = 9899,
		startY = 5700,
		endY = 5999
	},
	{
		name = "Crisis at Raccoon Tunnel, KY",
		mod = "CrisisAtRaccoonTunnel",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 3300,
		endY = 3499
	},
	{
		name = "Croft Manor, KY",
		mod = "CroftManor",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 9900,
		endY = 10099
	},
	{
		name = "D.E.R.P. Research Facility, KY",
		mod = "DERP",
		activated = false,
		startX = 10500,
		endX = 10699,
		startY = 10800,
		endY = 10999
	},
	{
		name = "Dancin' Donuts, KY",
		mod = "DancinDonuts",
		activated = false,
		startX = 13500,
		endX = 13799,
		startY = 5400,
		endY = 5599
	},
	{
		name = "Dead Pool Lake, KY",
		mod = "DeadPool",
		activated = false,
		startX = 6900,
		endX = 7199,
		startY = 4500,
		endY = 4699
	},
	{
		name = "DeathCreek, KY",
		mod = "DeathCreek",
		activated = false,
		startX = 13200,
		endX = 13399,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Desert City, KY",
		mod = "DesertCity",
		activated = false,
		startX = 12900,
		endX = 13199,
		startY = 4500,
		endY = 4799
	},
	{
		name = "Dinkletown, KY",
		mod = "Dinkletown",
		activated = false,
		startX = 12000,
		endX = 12199,
		startY = 10500,
		endY = 10799
	},
	{
		name = "Dirtbelt Raceway, KY",
		mod = "Dirtbelt_Raceway",
		activated = false,
		startX = 6900,
		endX = 7199,
		startY = 9900,
		endY = 10199
	},
	{
		name = "Dudly Hargraves Shooting Range, KY",
		mod = "DudlyHargraves",
		activated = false,
		startX = 13500,
		endX = 13799,
		startY = 9900,
		endY = 10199
	},
	{
		name = "Eastsville, KY",
		mod = "Eastsville",
		activated = false,
		startX = 7500,
		endX = 7799,
		startY = 12300,
		endY = 12599
	},
	{
		name = "Elysium, KY",
		mod = "Elysium",
		activated = false,
		startX = 10500,
		endX = 10699,
		startY = 4200,
		endY = 4399
	},
	{
		name = "Epic Compound, KY",
		mod = "Epic_Compound",
		activated = false,
		startX = 6600,
		endX = 6899,
		startY = 12300,
		endY = 12599
	},
	{
		name = "Fairfield, KY",
		mod = "Fairfield",
		activated = false,
		startX = 8100,
		endX = 8399,
		startY = 9300,
		endY = 9599
	},
	{
		name = "Fallow Hills, KY",
		mod = "FallowHills",
		activated = false,
		startX = 9900,
		endX = 10199,
		startY = 7500,
		endY = 7799
	},
	{
		name = "Fisherville, KY",
		mod = "Fisherville",
		activated = false,
		startX = 12300,
		endX = 12599,
		startY = 5100,
		endY = 5399
	},
	{
		name = "Foxton, KY",
		mod = "Foxton",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 11700,
		endY = 11999
	},
	{
		name = "Frank Lloyd Ranch, KY",
		mod = "FrankLloydWright",
		activated = false,
		startX = 12900,
		endX = 13099,
		startY = 6300,
		endY = 6599
	},
	{
		name = "Friden, KY",
		mod = "Friden",
		activated = false,
		startX = 4500,
		endX = 4799,
		startY = 11100,
		endY = 11299
	},
	{
		name = "Frosty Pines Resort, KY",
		mod = "FrostyPinesResort",
		activated = false,
		startX = 6900,
		endX = 7099,
		startY = 12900,
		endY = 13099
	},
	{
		name = "Graham Residence, KY",
		mod = "GrahamResidence",
		activated = false,
		startX = 12300,
		endX = 12499,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Gravelands, KY",
		mod = "Gravelands",
		activated = false,
		startX = 11700,
		endX = 11999,
		startY = 4500,
		endY = 4799
	},
	{
		name = "Greenbriar Acres, KY",
		mod = "GreenbriarAcres",
		activated = false,
		startX = 8100,
		endX = 8399,
		startY = 10500,
		endY = 10799
	},
	{
		name = "Greenhill, KY",
		mod = "Greenhill",
		activated = false,
		startX = 11100,
		endX = 11299,
		startY = 8100,
		endY = 8399
	},
	{
		name = "Green Valley, KY",
		mod = "Green_Valley",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 6900,
		endY = 7099
	},
	{
		name = "Greenwood Cemetery, KY",
		mod = "GreenwoodCemetery",
		activated = false,
		startX = 7800,
		endX = 8099,
		startY = 11700,
		endY = 11999
	},
	{
		name = "Grizzlys Bar & Grill, KY",
		mod = "GrizzlysBarAndGrill",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Grove Street Apartments, KY",
		mod = "GroveStreet",
		activated = false,
		startX = 7200,
		endX = 7399,
		startY = 8400,
		endY = 8599
	},
	{
		name = "Havendale, KY",
		mod = "Havendale",
		activated = false,
		startX = 12000,
		endX = 12199,
		startY = 6300,
		endY = 6499
	},
	{
		name = "Helms Deep, KY",
		mod = "HelmsDeep",
		activated = false,
		startX = 9000,
		endX = 9299,
		startY = 11700,
		endY = 11999
	},
	{
		name = "Hidden Springs, KY",
		mod = "HiddenSprings",
		activated = false,
		startX = 9000,
		endX = 9299,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Highland Park, KY",
		mod = "HighlandPark",
		activated = false,
		startX = 12600,
		endX = 12799,
		startY = 6900,
		endY = 7099
	},
	{
		name = "Hillside, KY",
		mod = "Hillside",
		activated = false,
		startX = 7800,
		endX = 8099,
		startY = 6600,
		endY = 6799
	},
	{
		name = "Hobbiton, KY",
		mod = "Hobbiton",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Horse Ranch, KY",
		mod = "HorseRanch",
		activated = false,
		startX = 6900,
		endX = 7099,
		startY = 8700,
		endY = 8899
	},
	{
		name = "Hudson River Valley, KY",
		mod = "HudsonRiverValley",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 4500,
		endY = 4699
	},
	{
		name = "Ironforge, KY",
		mod = "Ironforge",
		activated = false,
		startX = 9900,
		endX = 10099,
		startY = 12900,
		endY = 13099
	},
	{
		name = "Jade Helm, KY",
		mod = "JadeHelm",
		activated = false,
		startX = 11700,
		endX = 11999,
		startY = 10500,
		endY = 10799
	},
	{
		name = "Lake Galway, KY",
		mod = "LakeGalway",
		activated = false,
		startX = 11100,
		endX = 11399,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Lakeside Cabins, KY",
		mod = "LakesideCabins",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 11400,
		endY = 11599
	},
	{
		name = "Lakeview Hotel, KY",
		mod = "LakeviewHotel",
		activated = false,
		startX = 12600,
		endX = 12799,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Layton Lake, KY",
		mod = "LaytonLake",
		activated = false,
		startX = 8100,
		endX = 8399,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Leah's Landing, KY",
		mod = "LeahsLanding",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 12300,
		endY = 12599
	},
	{
		name = "Leesburg, KY",
		mod = "Leesburg",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Lemoyne, KY",
		mod = "Lemoyne",
		activated = false,
		startX = 12600,
		endX = 12799,
		startY = 4200,
		endY = 4399
	},
	{
		name = "Lighthouse Point, KY",
		mod = "LighthousePoint",
		activated = false,
		startX = 12900,
		endX = 13099,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Lillysville, KY",
		mod = "Lillysville",
		activated = false,
		startX = 13200,
		endX = 13399,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Little Creek, KY",
		mod = "LittleCreek",
		activated = false,
		startX = 6900,
		endX = 7099,
		startY = 9000,
		endY = 9199
	},
	{
		name = "Little Hope, KY",
		mod = "LittleHope",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 11100,
		endY = 11299
	},
	{
		name = "Lost Lakes, KY",
		mod = "LostLakes",
		activated = false,
		startX = 12300,
		endX = 12599,
		startY = 8700,
		endY = 8899
	},
	{
		name = "Lost Village, KY",
		mod = "LostVillage",
		activated = false,
		startX = 12600,
		endX = 12799,
		startY = 11700,
		endY = 11999
	},
	{
		name = "Lucky Landing, KY",
		mod = "LuckyLanding",
		activated = false,
		startX = 9600,
		endX = 9799,
		startY = 5100,
		endY = 5299
	},
	{
		name = "Lynncreek, KY",
		mod = "Lynncreek",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 7500,
		endY = 7799
	},
	{
		name = "Malden, KY",
		mod = "Malden",
		activated = false,
		startX = 11100,
		endX = 11399,
		startY = 4500,
		endY = 4699
	},
	{
		name = "Maple Ridge, KY",
		mod = "MapleRidge",
		activated = false,
		startX = 8700,
		endX = 8899,
		startY = 10500,
		endY = 10799
	},
	{
		name = "Mason's Point, KY",
		mod = "MasonsPoint",
		activated = false,
		startX = 7800,
		endX = 7999,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Meadowview, KY",
		mod = "Meadowview",
		activated = false,
		startX = 7500,
		endX = 7699,
		startY = 10800,
		endY = 10999
	},
	{
		name = "Melody Acres, KY",
		mod = "MelodyAcres",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 12900,
		endY = 13099
	},
	{
		name = "Misty Falls, KY",
		mod = "MistyFalls",
		activated = false,
		startX = 12000,
		endX = 12199,
		startY = 10200,
		endY = 10399
	},
	{
		name = "Misty Hollow, KY",
		mod = "MistyHollow",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Moonlight Bay, KY",
		mod = "MoonlightBay",
		activated = false,
		startX = 9900,
		endX = 10199,
		startY = 12900,
		endY = 13099
	},
	{
		name = "Morgantown, KY",
		mod = "Morgantown",
		activated = false,
		startX = 6900,
		endX = 7099,
		startY = 8400,
		endY = 8599
	},
	{
		name = "Mount Autumn, KY",
		mod = "MountAutumn",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Mountain Shadows, KY",
		mod = "MountainShadows",
		activated = false,
		startX = 8700,
		endX = 8899,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Nashville, KY",
		mod = "Nashville",
		activated = false,
		startX = 9000,
		endX = 9299,
		startY = 6300,
		endY = 6499
	},
	{
		name = "Newport, KY",
		mod = "Newport",
		activated = false,
		startX = 8700,
		endX = 8899,
		startY = 10200,
		endY = 10399
	},
	{
		name = "Nightshade Mansion, KY",
		mod = "NightshadeMansion",
		activated = false,
		startX = 11700,
		endX = 11899,
		startY = 6300,
		endY = 6499
	},
	{
		name = "Northbridge, KY",
		mod = "Northbridge",
		activated = false,
		startX = 7200,
		endX = 7399,
		startY = 11400,
		endY = 11599
	},
	{
		name = "Northpoint, KY",
		mod = "Northpoint",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 7500,
		endY = 7699
	},
	{
		name = "Oakridge, KY",
		mod = "Oakridge",
		activated = false,
		startX = 11700,
		endX = 11899,
		startY = 8100,
		endY = 8299
	},
	{
		name = "Oceanview, KY",
		mod = "Oceanview",
		activated = false,
		startX = 7200,
		endX = 7399,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Old Town, KY",
		mod = "OldTown",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 8400,
		endY = 8599
	},
	{
		name = "Overlook, KY",
		mod = "Overlook",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Oxford, KY",
		mod = "Oxford",
		activated = false,
		startX = 12300,
		endX = 12499,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Pacific Bay, KY",
		mod = "PacificBay",
		activated = false,
		startX = 9900,
		endX = 10099,
		startY = 5100,
		endY = 5299
	},
	{
		name = "Palisades, KY",
		mod = "Palisades",
		activated = false,
		startX = 12300,
		endX = 12499,
		startY = 11700,
		endY = 11999
	},
	{
		name = "Palm Grove, KY",
		mod = "PalmGrove",
		activated = false,
		startX = 9000,
		endX = 9199,
		startY = 7200,
		endY = 7399
	},
	{
		name = "Paradise Hills, KY",
		mod = "ParadiseHills",
		activated = false,
		startX = 12600,
		endX = 12799,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Pebblebrook, KY",
		mod = "Pebblebrook",
		activated = false,
		startX = 7500,
		endX = 7699,
		startY = 10500,
		endY = 10799
	},
	{
		name = "Pikeville, KY",
		mod = "Pikeville",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Pinegrove, KY",
		mod = "Pinegrove",
		activated = false,
		startX = 8100,
		endX = 8299,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Pine Hollow, KY",
		mod = "PineHollow",
		activated = false,
		startX = 9600,
		endX = 9799,
		startY = 8700,
		endY = 8899
	},
	{
		name = "Portside, KY",
		mod = "Portside",
		activated = false,
		startX = 12900,
		endX = 13099,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Prosperity, KY",
		mod = "Prosperity",
		activated = false,
		startX = 12900,
		endX = 13099,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Quarry Junction, KY",
		mod = "QuarryJunction",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 9000,
		endY = 9199
	},
	{
		name = "Quincy, KY",
		mod = "Quincy",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Raven Rock, KY",
		mod = "RavenRock",
		activated = false,
		startX = 11700,
		endX = 11999,
		startY = 8700,
		endY = 8899
	},
	{
		name = "Redwood Forest, KY",
		mod = "RedwoodForest",
		activated = false,
		startX = 12300,
		endX = 12499,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Ridgeview, KY",
		mod = "Ridgeview",
		activated = false,
		startX = 12000,
		endX = 12199,
		startY = 10200,
		endY = 10399
	},
	{
		name = "Rivertown, KY",
		mod = "Rivertown",
		activated = false,
		startX = 11100,
		endX = 11399,
		startY = 9000,
		endY = 9199
	},
	{
		name = "Rockford, KY",
		mod = "Rockford",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 7200,
		endY = 7399
	},
	{
		name = "Rustic Hills, KY",
		mod = "RusticHills",
		activated = false,
		startX = 12900,
		endX = 13099,
		startY = 10800,
		endY = 10999
	},
	{
		name = "Salem, KY",
		mod = "Salem",
		activated = false,
		startX = 7800,
		endX = 7999,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Sandstone, KY",
		mod = "Sandstone",
		activated = false,
		startX = 12600,
		endX = 12799,
		startY = 12300,
		endY = 12499
	},
	{
		name = "Sapphire Springs, KY",
		mod = "SapphireSprings",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Shadowbrook, KY",
		mod = "Shadowbrook",
		activated = false,
		startX = 7800,
		endX = 7999,
		startY = 12900,
		endY = 13099
	},
	{
		name = "Silver Creek, KY",
		mod = "SilverCreek",
		activated = false,
		startX = 12300,
		endX = 12499,
		startY = 6000,
		endY = 6199
	},
	{
		name = "Sleepy Hollow, KY",
		mod = "SleepyHollow",
		activated = false,
		startX = 7800,
		endX = 7999,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Southfield, KY",
		mod = "Southfield",
		activated = false,
		startX = 9900,
		endX = 10099,
		startY = 8700,
		endY = 8899
	},
	{
		name = "South Haven, KY",
		mod = "SouthHaven",
		activated = false,
		startX = 7800,
		endX = 7999,
		startY = 12900,
		endY = 13099
	},
	{
		name = "Southport, KY",
		mod = "Southport",
		activated = false,
		startX = 9600,
		endX = 9799,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Southridge, KY",
		mod = "Southridge",
		activated = false,
		startX = 8100,
		endX = 8299,
		startY = 12300,
		endY = 12499
	},
	{
		name = "Southside, KY",
		mod = "Southside",
		activated = false,
		startX = 9900,
		endX = 10199,
		startY = 7200,
		endY = 7399
	},
	{
		name = "Springfield, KY",
		mod = "Springfield",
		activated = false,
		startX = 10500,
		endX = 10699,
		startY = 6300,
		endY = 6499
	},
	{
		name = "Stonewood, KY",
		mod = "Stonewood",
		activated = false,
		startX = 7200,
		endX = 7399,
		startY = 10200,
		endY = 10399
	},
	{
		name = "Stonybrook, KY",
		mod = "Stonybrook",
		activated = false,
		startX = 7800,
		endX = 7999,
		startY = 6900,
		endY = 7099
	},
	{
		name = "Sunset Bay, KY",
		mod = "SunsetBay",
		activated = false,
		startX = 8100,
		endX = 8299,
		startY = 8100,
		endY = 8299
	},
	{
		name = "Sycamore, KY",
		mod = "Sycamore",
		activated = false,
		startX = 11100,
		endX = 11299,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Sylvan Springs, KY",
		mod = "SylvanSprings",
		activated = false,
		startX = 11700,
		endX = 11899,
		startY = 10800,
		endY = 10999
	},
	{
		name = "Tanglewood, KY",
		mod = "Tanglewood",
		activated = false,
		startX = 12300,
		endX = 12499,
		startY = 10200,
		endY = 10399
	},
	{
		name = "The Pines, KY",
		mod = "ThePines",
		activated = false,
		startX = 9900,
		endX = 10099,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Timberline, KY",
		mod = "Timberline",
		activated = false,
		startX = 12600,
		endX = 12799,
		startY = 8700,
		endY = 8899
	},
	{
		name = "Tranquil Valley, KY",
		mod = "TranquilValley",
		activated = false,
		startX = 12300,
		endX = 12499,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Twin Lakes, KY",
		mod = "TwinLakes",
		activated = false,
		startX = 10500,
		endX = 10699,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Valleyview, KY",
		mod = "Valleyview",
		activated = false,
		startX = 11700,
		endX = 11899,
		startY = 10200,
		endY = 10399
	},
	{
		name = "Victoria, KY",
		mod = "Victoria",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 6300,
		endY = 6499
	},
	{
		name = "Vineyard, KY",
		mod = "Vineyard",
		activated = false,
		startX = 7200,
		endX = 7399,
		startY = 9900
		,
		endY = 10099
	},
	{
		name = "Waverly, KY",
		mod = "Waverly",
		activated = false,
		startX = 10500,
		endX = 10699,
		startY = 12300,
		endY = 12499
	},
	{
		name = "Westfield, KY",
		mod = "Westfield",
		activated = false,
		startX = 10800,
		endX = 10999,
		startY = 7200,
		endY = 7399
	},
	{
		name = "Westgate, KY",
		mod = "Westgate",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 12900,
		endY = 13099
	},
	{
		name = "Weston, KY",
		mod = "Weston",
		activated = false,
		startX = 7500,
		endX = 7699,
		startY = 9000,
		endY = 9199
	},
	{
		name = "Westport, KY",
		mod = "Westport",
		activated = false,
		startX = 10500,
		endX = 10699,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Westwood, KY",
		mod = "Westwood",
		activated = false,
		startX = 9000,
		endX = 9199,
		startY = 11100,
		endY = 11399
	},
	{
		name = "Whispering Pines, KY",
		mod = "WhisperingPines",
		activated = false,
		startX = 9000,
		endX = 9199,
		startY = 6000,
		endY = 6199
	},
	{
		name = "White Oaks, KY",
		mod = "WhiteOaks",
		activated = false,
		startX = 7800,
		endX = 7999,
		startY = 10200,
		endY = 10399
	},
	{
		name = "Whitewater, KY",
		mod = "Whitewater",
		activated = false,
		startX = 12900,
		endX = 13099,
		startY = 7200,
		endY = 7399
	},
	{
		name = "Wildwood, KY",
		mod = "Wildwood",
		activated = false,
		startX = 8100,
		endX = 8299,
		startY = 6300,
		endY = 6499
	},
	{
		name = "Willow Creek, KY",
		mod = "WillowCreek",
		activated = false,
		startX = 10200,
		endX = 10399,
		startY = 7800,
		endY = 7999
	},
	{
		name = "Windsor, KY",
		mod = "Windsor",
		activated = false,
		startX = 7200,
		endX = 7399,
		startY = 10800,
		endY = 10999
	},
	{
		name = "Woodland, KY",
		mod = "Woodland",
		activated = false,
		startX = 11400,
		endX = 11599,
		startY = 9000,
		endY = 9199
	},
	{
		name = "Woodridge, KY",
		mod = "Woodridge",
		activated = false,
		startX = 11100,
		endX = 11299,
		startY = 9900,
		endY = 10099
	},
	{
		name = "Woodside, KY",
		mod = "Woodside",
		activated = false,
		startX = 7200,
		endX = 7399,
		startY = 11700,
		endY = 11899
	},
	{
		name = "Wyndham, KY",
		mod = "Wyndham",
		activated = false,
		startX = 11700,
		endX = 11899,
		startY = 6300,
		endY = 6499
	},		


	-- VANILLA
	{
		name = "Dixie, KY",
		activated = true,
		startX = 5400,
		endX = 5700,
		startY = 9300,
		endY = 9900,
	},
	{
		name = "Doe Valley, KY",
		activated = true,
		startX = 6600,
		endX = 6900,
		startY = 9900,
		endY = 10200,
	},
	{
		name = "Echo Creek, KY",
		activated = true,
		startX = 3100,
		endX = 4500,
		startY = 10800,
		endY = 11600,
	},
	{
		name = "Fallas Lake, KY",
		activated = true,
		startX = 6900,
		endX = 7500,
		startY = 8100,
		endY = 8700,
	},
	{
		name = "Louisville, KY",
		activated = true,
		startX = 11700,
		endX = 14100,
		startY = 1200,
		endY = 4500,
	},
	{
		name = "Muldraugh, KY",
		activated = true,
		startX = 10500,
		endX = 11100,
		startY = 9000,
		endY = 10800,
	},
	{
		name = "March Ridge, KY",
		activated = true,
		startX = 9600,
		endX = 10500,
		startY = 12300,
		endY = 13200,
	},
	{
		name = "Riverside, KY",
		activated = true,
		startX = 5700,
		endX = 6900,
		startY = 5100,
		endY = 5700,
	},
	{
		name = "Riverside Trailer Park, KY",
		activated = true,
		startX = 5100,
		endX = 5400,
		startY = 5700,
		endY = 6300,
	},
	{
		name = "Rosewood, KY",
		activated = true,
		startX = 7900,
		endX = 8700,
		startY = 11100,
		endY = 12300,
	},
	{
		name = "Kentucky State Prison, KY",
		activated = true,
		startX = 7500,
		endX = 7840,
		startY = 11800,
		endY = 12000,
	},
	{
		name = "Valley Station, KY",
		activated = true,
		startX = 12300,
		endX = 13200,
		startY = 4500,
		endY = 6300,
	},
	{
		name = "West Point, KY",
		activated = true,
		startX = 10800,
		endX = 12600,
		startY = 6600,
		endY = 7500,
	},
}