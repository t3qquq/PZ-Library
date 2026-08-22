require 'Items/ProceduralDistributions'

MoreBrews = MoreBrews or {};

function MoreBrews:addDistributions(itemsAndChances, locations)
	for item, chance in pairs(itemsAndChances)
	do
		for i, location in ipairs(locations)
		do
			if ProceduralDistributions.list[location] and ProceduralDistributions.list[location].items
			then
				table.insert(ProceduralDistributions.list[location].items, item);
				table.insert(ProceduralDistributions.list[location].items, chance);
			end
		end
	end
end

local sBvars = SandboxVars.MoreBrews;
local BrewingKitRarity = sBvars.BrewingKits * sBvars.Loot * 1;
local KegMidRarity = sBvars.Kegs * sBvars.Loot * 0.1;
local KegHighRarity = sBvars.Kegs * sBvars.Loot * 0.01;
local HopsRarity = sBvars.Hops * sBvars.Loot * 0.01;
local SuppliesRarity = sBvars.FillingSupplies * sBvars.Loot * 1;
local RecipesRarity = sBvars.Recipes * sBvars.Loot * 0.5;
local BooksLowRarity = sBvars.Books * sBvars.Loot * 1;
local BooksMidRarity = sBvars.Books * sBvars.Loot * 0.5;
local BeerMidRarity = sBvars.Cans * sBvars.Loot * 0.1;
local BeerHighRarity = sBvars.Cans * sBvars.Loot * 0.01;

if sBvars.KitSpawnOption then
    MoreBrews:addDistributions({
        -- Brewing Kit
        ["MoreBrews.BrewingKit"] = BrewingKitRarity,
    }, {
        "BarShelfLiquor", 
        "GarageTools",
        "GigamartHousewares",
        "StoreShelfBeer",
    });
else
    MoreBrews:addDistributions({
        -- Brewing Kit
        ["MoreBrews.BrewingKit"] = BrewingKitRarity,
    }, {
    });
end

if sBvars.KegSpawnOption then
    MoreBrews:addDistributions({
        -- Empty Kegs
        ["MoreBrews.EmptyKeg"] = KegHighRarity,
    }, {
        "BarShelfLiquor", 
    });

    MoreBrews:addDistributions({
        -- Empty Kegs
        ["MoreBrews.EmptyKeg"] = KegMidRarity,
    }, {
        "CrateEmptyBottles1", 
        "CrateEmptyBottles2",
    });
else
    MoreBrews:addDistributions({
        -- Empty Kegs
        ["MoreBrews.EmptyKeg"] = KegHighRarity,
    }, {
    });

    MoreBrews:addDistributions({
        -- Empty Kegs
        ["MoreBrews.EmptyKeg"] = KegMidRarity,
    }, {
    });
end

if sBvars.HopsSpawnOption then
    MoreBrews:addDistributions({
        -- Hops
        ["MoreBrews.HopsHallertau"] = HopsRarity,
        ["MoreBrews.HopsCascade"] = HopsRarity,
        ["MoreBrews.HopsSimcoe"] = HopsRarity,
        ["MoreBrews.HopsMosaic"] = HopsRarity,
        ["MoreBrews.HopsCitra"] = HopsRarity,
        ["MoreBrews.HopsCentennial"] = HopsRarity,
        ["MoreBrews.HopsGolding"] = HopsRarity,
        ["MoreBrews.HopsNugget"] = HopsRarity,
    }, {
        "BarShelfLiquor", 
        "KitchenRandom",
        "GardenStoreMisc",
    });
else
    MoreBrews:addDistributions({
        -- Hops
        ["MoreBrews.HopsHallertau"] = HopsRarity,
        ["MoreBrews.HopsCascade"] = HopsRarity,
        ["MoreBrews.HopsSimcoe"] = HopsRarity,
        ["MoreBrews.HopsMosaic"] = HopsRarity,
        ["MoreBrews.HopsCitra"] = HopsRarity,
        ["MoreBrews.HopsCentennial"] = HopsRarity,
        ["MoreBrews.HopsGolding"] = HopsRarity,
        ["MoreBrews.HopsNugget"] = HopsRarity,
    }, {
    });
end

if sBvars.SuppliesSpawnOption then
    MoreBrews:addDistributions({
        -- Supplies
        ["MoreBrews.BottleCapper"] = SuppliesRarity,
        ["MoreBrews.CanSealer"] = SuppliesRarity,
        ["MoreBrews.BottleCapsBag"] = SuppliesRarity,
        ["MoreBrews.MaltExtractDark"] = SuppliesRarity,
        ["MoreBrews.MaltExtractLight"] = SuppliesRarity,
    }, {
        "BarShelfLiquor", 
        "GarageTools",
        "GigamartTools",
    });
else
    MoreBrews:addDistributions({
        -- Supplies
        ["MoreBrews.BottleCapper"] = SuppliesRarity,
        ["MoreBrews.CanSealer"] = SuppliesRarity,
        ["MoreBrews.BottleCapsBag"] = SuppliesRarity,
        ["MoreBrews.MaltExtractDark"] = SuppliesRarity,
        ["MoreBrews.MaltExtractLight"] = SuppliesRarity,
    }, {
    });
end

if sBvars.MagazineSpawnOption then
    MoreBrews:addDistributions({
        -- Magazines
        ["MoreBrews.MagazineHomeBrew1"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew2"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew3"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew4"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew5"] = RecipesRarity,
    }, {
        "BookstoreBooks", 
        "BookstoreMisc",
        "CrateMagazines",
        "CrateBooks", 
        "LibraryBooks",
        "MagazineRackMixed",
        "PostOfficeMagazines",
    });
else
    MoreBrews:addDistributions({
        -- Magazines
        ["MoreBrews.MagazineHomeBrew1"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew2"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew3"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew4"] = RecipesRarity,
        ["MoreBrews.MagazineHomeBrew5"] = RecipesRarity,
    }, {
    });
end

if sBvars.BookSpawnOption then
    MoreBrews:addDistributions({
        -- Books 1
        ["MoreBrews.BookBrewing1"] = BooksLowRarity,
        ["MoreBrews.BookBrewing2"] = BooksLowRarity,
        ["MoreBrews.BookBrewing3"] = BooksLowRarity,
    }, {
        "BookstoreBooks", 
        "CrateBooks",
        "LibraryBooks",
    });

    MoreBrews:addDistributions({
        -- Books 2
        ["MoreBrews.BookBrewing4"] = BooksMidRarity,
        ["MoreBrews.BookBrewing5"] = BooksMidRarity,
    }, {
        "BookstoreBooks", 
        "CrateBooks",
        "LibraryBooks",
    });
else
    MoreBrews:addDistributions({
        -- Books 1
        ["MoreBrews.BookBrewing1"] = BooksLowRarity,
        ["MoreBrews.BookBrewing2"] = BooksLowRarity,
        ["MoreBrews.BookBrewing3"] = BooksLowRarity,
    }, {
    });

    MoreBrews:addDistributions({
        -- Books 2
        ["MoreBrews.BookBrewing4"] = BooksMidRarity,
        ["MoreBrews.BookBrewing5"] = BooksMidRarity,
    }, {
    });
end

if sBvars.BeerSpawnOption then
    MoreBrews:addDistributions({
        -- Beer High Rarity
        ["MoreBrews.BeerCanAmericanLager"] = BeerHighRarity,
        ["MoreBrews.BeerCanAPA1"] = BeerHighRarity,
        ["MoreBrews.BeerCanAPA2"] = BeerHighRarity,
        ["MoreBrews.BeerCanIPA1"] = BeerHighRarity,
        ["MoreBrews.BeerCanIPA2"] = BeerHighRarity,
        ["MoreBrews.BeerCanLightLager"] = BeerHighRarity,
        ["MoreBrews.BeerCanPilsner"] = BeerHighRarity,
        ["MoreBrews.BeerCanPorter"] = BeerHighRarity,
        ["MoreBrews.BeerCanStout"] = BeerHighRarity,
        ["MoreBrews.BeerBottleAmericanLager"] = BeerHighRarity,
        ["MoreBrews.BeerBottleAPA1"] = BeerHighRarity,
        ["MoreBrews.BeerBottleAPA2"] = BeerHighRarity,
        ["MoreBrews.BeerBottleIPA1"] = BeerHighRarity,
        ["MoreBrews.BeerBottleIPA2"] = BeerHighRarity,
        ["MoreBrews.BeerBottleLightLager"] = BeerHighRarity,
        ["MoreBrews.BeerBottlePilsner"] = BeerHighRarity,
        ["MoreBrews.BeerBottlePorter"] = BeerHighRarity,
        ["MoreBrews.BeerBottleStout"] = BeerHighRarity,
    }, {
        "BarCounterLiquor", 
        "BarCounterWeapon", 
        "BreweryCans",
        "KitchenBottles",
        "BarShelfLiquor",
    });

    MoreBrews:addDistributions({
        -- Beer Mid Rarity
        ["MoreBrews.BeerCanAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanPilsner"] = BeerMidRarity,
        ["MoreBrews.BeerCanPorter"] = BeerMidRarity,
        ["MoreBrews.BeerCanStout"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePilsner"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePorter"] = BeerMidRarity,
        ["MoreBrews.BeerBottleStout"] = BeerMidRarity,
    }, {
        "FridgeBeer", 
        "StoreShelfBeer", 
    });

    MoreBrews:addDistributions({
        -- Beer Brewery Cans
        ["MoreBrews.BeerCanAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanPilsner"] = BeerMidRarity,
        ["MoreBrews.BeerCanPorter"] = BeerMidRarity,
        ["MoreBrews.BeerCanStout"] = BeerMidRarity,
    }, {
        "BreweryCans", 
    });

    MoreBrews:addDistributions({
        -- Beer Brewery Bottles
        ["MoreBrews.BeerBottleAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePilsner"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePorter"] = BeerMidRarity,
        ["MoreBrews.BeerBottleStout"] = BeerMidRarity,
    }, {
        "BreweryBottles", 
    });
else
    MoreBrews:addDistributions({
        -- Beer High Rarity
        ["MoreBrews.BeerCanAmericanLager"] = BeerHighRarity,
        ["MoreBrews.BeerCanAPA1"] = BeerHighRarity,
        ["MoreBrews.BeerCanAPA2"] = BeerHighRarity,
        ["MoreBrews.BeerCanIPA1"] = BeerHighRarity,
        ["MoreBrews.BeerCanIPA2"] = BeerHighRarity,
        ["MoreBrews.BeerCanLightLager"] = BeerHighRarity,
        ["MoreBrews.BeerCanPilsner"] = BeerHighRarity,
        ["MoreBrews.BeerCanPorter"] = BeerHighRarity,
        ["MoreBrews.BeerCanStout"] = BeerHighRarity,
        ["MoreBrews.BeerBottleAmericanLager"] = BeerHighRarity,
        ["MoreBrews.BeerBottleAPA1"] = BeerHighRarity,
        ["MoreBrews.BeerBottleAPA2"] = BeerHighRarity,
        ["MoreBrews.BeerBottleIPA1"] = BeerHighRarity,
        ["MoreBrews.BeerBottleIPA2"] = BeerHighRarity,
        ["MoreBrews.BeerBottleLightLager"] = BeerHighRarity,
        ["MoreBrews.BeerBottlePilsner"] = BeerHighRarity,
        ["MoreBrews.BeerBottlePorter"] = BeerHighRarity,
        ["MoreBrews.BeerBottleStout"] = BeerHighRarity,
    }, {
    });

    MoreBrews:addDistributions({
        -- Beer Mid Rarity
        ["MoreBrews.BeerCanAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanPilsner"] = BeerMidRarity,
        ["MoreBrews.BeerCanPorter"] = BeerMidRarity,
        ["MoreBrews.BeerCanStout"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePilsner"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePorter"] = BeerMidRarity,
        ["MoreBrews.BeerBottleStout"] = BeerMidRarity,
    }, {
    });

    MoreBrews:addDistributions({
        -- Beer Brewery Cans
        ["MoreBrews.BeerCanAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerCanIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerCanLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerCanPilsner"] = BeerMidRarity,
        ["MoreBrews.BeerCanPorter"] = BeerMidRarity,
        ["MoreBrews.BeerCanStout"] = BeerMidRarity,
    }, {
    });

    MoreBrews:addDistributions({
        -- Beer Brewery Bottles
        ["MoreBrews.BeerBottleAmericanLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleAPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA1"] = BeerMidRarity,
        ["MoreBrews.BeerBottleIPA2"] = BeerMidRarity,
        ["MoreBrews.BeerBottleLightLager"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePilsner"] = BeerMidRarity,
        ["MoreBrews.BeerBottlePorter"] = BeerMidRarity,
        ["MoreBrews.BeerBottleStout"] = BeerMidRarity,
    }, {
    });
end