require 'Definitions/HairOutfitDefinitions'





local excludedFrontierHairs = {
"Riven_WildFire",
"Ada",
"Arkenaux",
"Mango_WildFire",
"Parrallax",
"Riven",
"LINCL",
"Mango",
"FennexWildFireClassy",
"Fennex",
"FennexAlt",
"FennexSurvivor",
"FennexWildFire",
"FennexParadigm",
"FennexRogue",
"Maemento",
"UmbralPheonix",
"Nexus",
"SeaBreeze",
"Autumn",
"Ark",
"Fate",
"Clara",
"Carter",
"Kitara",
"DateNight",
"Carbonite",
"SecondDream",
"Valiant",
"Illenium",
"Accursed",
"Evergent",
"CountDown",
"Abbadon",
"Azazel",
"UnConstrained",
"Rememberance",
"SummerTime",
"MangoReversed",
"Naxium",
"Warranted",
"Evelyn",
"KeyToMyHeart",
"frontierGP2",
"Cirrium",
"Loyalty",
"AfterDark",
"frontierGP3",
"SomberMoments",
"AlteredReflections",
"Rufflet",
"Avantime",
}






















for k,v in pairs(excludedFrontierHairs) do
	local cat = {name = v, onlyFor = "FakeZ"};
	table.insert(HairOutfitDefinitions.haircutDefinition, cat);
end