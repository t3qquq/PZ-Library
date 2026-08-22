require 'Items/ProceduralDistributions'

MoreBrewsWineMeUp = MoreBrewsWineMeUp or {};

function MoreBrewsWineMeUp:addDistributions(itemsAndChances, locations)
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

local sBvars = SandboxVars.MoreBrewsWineMeUp;
local WineMakingKitRarity = SandboxVars.MoreBrewsWineMeUp.WineMakingKits * SandboxVars.MoreBrewsWineMeUp.Loot * 1;
local SuppliesRarity = SandboxVars.MoreBrewsWineMeUp.Supplies * SandboxVars.MoreBrewsWineMeUp.Loot * 1;
local RecipesRarity = SandboxVars.MoreBrewsWineMeUp.Recipes * SandboxVars.MoreBrewsWineMeUp.Loot * 0.5;
local BooksLowRarity = SandboxVars.MoreBrewsWineMeUp.Books * SandboxVars.MoreBrewsWineMeUp.Loot * 1;
local BooksMidRarity = SandboxVars.MoreBrewsWineMeUp.Books * SandboxVars.MoreBrewsWineMeUp.Loot * 0.5;
local WineRarity = SandboxVars.MoreBrewsWineMeUp.Wine * SandboxVars.MoreBrewsWineMeUp.Loot * 0.05;



if sBvars.WineKitSpawnOption then
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.WineKit"] = WineMakingKitRarity,
    }, {
        "BarShelfLiquor", 
        "GarageTools",
        "GigamartHousewares",
        "StoreShelfBeer",
    });
else
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.WineKit"] = WineMakingKitRarity,
    }, {
    });
end

if sBvars.SuppliesSpawnOption then
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.RubberBandsBag"] = SuppliesRarity,
        ["MoreBrews.CorksBag"] = SuppliesRarity,
    }, {
        "BarShelfLiquor", 
        "GarageTools",
        "GigamartTools",
    });
else
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.RubberBandsBag"] = SuppliesRarity,
        ["MoreBrews.CorksBag"] = SuppliesRarity,
    }, {
    });
end

if sBvars.MagazinesSpawnOption then
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.MagazineWineMaking1"] = RecipesRarity,
        ["MoreBrews.MagazineWineMaking2"] = RecipesRarity,
        ["MoreBrews.MagazineWineMaking3"] = RecipesRarity,
        ["MoreBrews.MagazineWineMaking4"] = RecipesRarity,
        ["MoreBrews.MagazineYeastMaking"] = RecipesRarity,
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
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.MagazineWineMaking1"] = RecipesRarity,
        ["MoreBrews.MagazineWineMaking2"] = RecipesRarity,
        ["MoreBrews.MagazineWineMaking3"] = RecipesRarity,
        ["MoreBrews.MagazineWineMaking4"] = RecipesRarity,
        ["MoreBrews.MagazineYeastMaking"] = RecipesRarity,
    }, {
    });
end

if sBvars.BookSpawnOption then
    MoreBrewsWineMeUp:addDistributions({
        -- Books 1
        ["MoreBrews.BookWineMaking1"] = BooksLowRarity,
        ["MoreBrews.BookWineMaking2"] = BooksLowRarity,
        ["MoreBrews.BookWineMaking3"] = BooksLowRarity,
    }, {
        "BookstoreBooks", 
        "CrateBooks",
        "LibraryBooks",
    });

    MoreBrewsWineMeUp:addDistributions({
        -- Books 2
        ["MoreBrews.BookWineMaking4"] = BooksMidRarity,
        ["MoreBrews.BookWineMaking5"] = BooksMidRarity,
    }, {
        "BookstoreBooks", 
        "CrateBooks",
        "LibraryBooks",
    });
else
    MoreBrewsWineMeUp:addDistributions({
        -- Books 1
        ["MoreBrews.BookWineMaking1"] = BooksLowRarity,
        ["MoreBrews.BookWineMaking2"] = BooksLowRarity,
        ["MoreBrews.BookWineMaking3"] = BooksLowRarity,
    }, {
    });

    MoreBrewsWineMeUp:addDistributions({
        -- Books 2
        ["MoreBrews.BookWineMaking4"] = BooksMidRarity,
        ["MoreBrews.BookWineMaking5"] = BooksMidRarity,
    }, {
    });
end

if sBvars.WineSpawnOption then
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.WhiteWine"] = WineRarity,
        ["MoreBrews.WhiteWineAged"] = WineRarity,
        ["MoreBrews.WhiteWineAged"] = WineRarity,
        ["MoreBrews.RedWine"] = WineRarity,
        ["MoreBrews.RedWineAged"] = WineRarity,
        ["MoreBrews.RedWineAgedFully"] = WineRarity,
        ["MoreBrews.WineBoxSmall1"] = WineRarity,
        ["MoreBrews.WineBoxSmall2"] = WineRarity,
        ["MoreBrews.WineBoxSmall3"] = WineRarity,
        ["MoreBrews.WineBoxSmall4"] = WineRarity,
        ["MoreBrews.WineBoxSmall5"] = WineRarity,
        ["MoreBrews.WineBoxSmall6"] = WineRarity,
    }, {
        "BarShelfLiquor", 
        "BarCounterLiquor",
        "DishCabinetLiquor",
        "KitchenBottles",
        "FridgeRich",
        "StoreShelfWine",
    });
else
    MoreBrewsWineMeUp:addDistributions({
        -- Wine Kit
        ["MoreBrews.WhiteWine"] = WineRarity,
        ["MoreBrews.WhiteWineAged"] = WineRarity,
        ["MoreBrews.WhiteWineAged"] = WineRarity,
        ["MoreBrews.RedWine"] = WineRarity,
        ["MoreBrews.RedWineAged"] = WineRarity,
        ["MoreBrews.RedWineAgedFully"] = WineRarity,
        ["MoreBrews.WineBoxSmall1"] = WineRarity,
        ["MoreBrews.WineBoxSmall2"] = WineRarity,
        ["MoreBrews.WineBoxSmall3"] = WineRarity,
        ["MoreBrews.WineBoxSmall4"] = WineRarity,
        ["MoreBrews.WineBoxSmall5"] = WineRarity,
        ["MoreBrews.WineBoxSmall6"] = WineRarity,
    }, {
    });
end