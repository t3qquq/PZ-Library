function NoXP_OnGiveXP(recipe, ingredients, result, player)
end

--[[thanks to Glytch3r for the help optimizing the code!!

--sapph: Hi... i guess it's been a while since i last did something with this mod, (this is sapph from june 2023, hello!), so
 i should probably explain this .lua file.
 anyhow, the way Zomboid works (in 2023), it can only give the player, one result item at a time.
 this code is for giving the player more result itens and some other stuff.
 i'm not that good at explaining it am i?
 well, you'll probably get it if you take a look around!
 see ya! --]]

--[[sapph: so... i kinda mess up everything, so a big thanks to Gravy and Poltergeist for the help, so yeah, learn to test vanila recipes on your mods!
never know when you'll break them!]]

require "recipecode"


--Recipes that adds on the inventory. 
function recipe_PeelPotato(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.PotatoPeel");
end

function recipe_BrewClothfilter(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.DirtyClothFilter");
end

function recipe_SaucepanBowl(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("Base.Saucepan");
end

function recipe_FryingpanBowl(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("Base.Pan");
end

function recipe_FryingpanBowl2(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("Base.Pan");
	inv:AddItem("Base.Bowl");
end

function recipe_EggYolk(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.EggYolk");
end

function recipe_ChurnMilk(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.Churn_Buttermilk");
end

--Sachets!
function recipe_Noodles1(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.NoodleSachet_Beef");
end

function recipe_Noodles2(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.NoodleSachet_Chicken");
end


--MRE opening spawns.
--You can get two varieties of items.
--OPTION 1 MRE
local items_to_add = {
  "SapphCooking.SaltPacket",
  "SapphCooking.Mouth_Toothpick",
  "Base.SugarPacket",
  "SapphCooking.Drinkmix_Lemon",
  "SapphCooking.PlasticSpork",
  "SapphCooking.Mustard_Sachet",
  "SapphCooking.PeanutButter_Sachet",
  "SapphCooking.Tomato_Sachet",
  "SapphCooking.HotsaucePacket",
  "SapphCooking.MRE_FlamelessRationHeater",
  "Base.Teabag",
  "Base.Matches",
  "Base.Crackers",
  "Base.Gum",
}

function recipe_MREopen1(items, result, player)
  local inv = player:getInventory()
  for _, item_name in pairs(items_to_add) do
    inv:AddItem(item_name)
  end
end

--OPTION 2 MRE
local items_to_add2 = {
  "SapphCooking.SaltPacket",
  "Base.SugarPacket",
  "SapphCooking.Mouth_Toothpick",
  "SapphCooking.Drinkmix_Orange",
  "SapphCooking.PlasticSpork",
  "SapphCooking.PeanutButter_Sachet",
  "SapphCooking.Tomato_Sachet",
  "SapphCooking.HotsaucePacket",
  "SapphCooking.MRE_FlamelessRationHeater",
  "SapphCooking.CoffeePacket",
  "Base.Matches",
  "Base.GrahamCrackers",
  "Base.Gum",
  "Base.MintCandy",
}

function recipe_MREopen2(items, result, player)
  local inv = player:getInventory()
  for _, item_name in pairs(items_to_add2) do
    inv:AddItem(item_name)
  end
end

function recipe_EmptyBottle(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("Base.WaterBottleEmpty");
	end


function recipe_BowlStack(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("Base.Bowl");
end

	--Tag Recipes

function Recipe.GetItemTypes.SapphCookingOil(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingOil"));
    scriptItems:addAll(getScriptManager():getItemsTag("Oil"));
 end

 function Recipe.GetItemTypes.SapphCookingJuice(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Juice"));
    scriptItems:addAll(getScriptManager():getItemsTag("FruitJuice"));
 end

 --for recipes with bacon and eggs pans
 function Recipe.GetItemTypes.SapphCookingBaconEggsPan(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("BaconEggsPan"));
 end
 
function Recipe.GetItemTypes.SapphCookingFalafel(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingFalafel"));
    scriptItems:addAll(getScriptManager():getItemsTag("Peas"));
    scriptItems:addAll(getScriptManager():getItemsTag("Beans"));
 end

 function Recipe.GetItemTypes.SapphCookingMushroom(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Mushroom"));
    scriptItems:addAll(getScriptManager():getItemsTag("Mushrooms"));
 end

 
 
 function Recipe.GetItemTypes.SapphCookingBread(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingBread"));
    scriptItems:addAll(getScriptManager():getItemsTag("Bread"));
 end

 function Recipe.GetItemTypes.SapphCookingKnifes(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingKnife"));
    scriptItems:addAll(getScriptManager():getItemsTag("SharpKnife"));
    scriptItems:addAll(getScriptManager():getItemsTag("DullKnife"));
 end

 function Recipe.GetItemTypes.SapphCookingTomatoSauce(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingKetchup"));
    scriptItems:addAll(getScriptManager():getItemsTag("Ketchup"));
    scriptItems:addAll(getScriptManager():getItemsTag("TomatoKetchup"));
 end


 function Recipe.GetItemTypes.SapphCookingMincedMeat(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingMincedMeat"));
    scriptItems:addAll(getScriptManager():getItemsTag("MincedMeat"));
 end

 function Recipe.GetItemTypes.SapphCookingBroth(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingBroth"));
    scriptItems:addAll(getScriptManager():getItemsTag("Broth"));
 end

 function Recipe.GetItemTypes.SapphCookingAlcoholic(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingAlcohol"));
    scriptItems:addAll(getScriptManager():getItemsTag("Alcoholic"));
    scriptItems:addAll(getScriptManager():getItemsTag("Alcohol"));
 end



 function Recipe.GetItemTypes.SapphCookingMinceMeat(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingMinceMeat"));
    scriptItems:addAll(getScriptManager():getItemsTag("MinceMeat"));
 end

 function Recipe.GetItemTypes.SapphCookingThermos(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingThermos"));
    scriptItems:addAll(getScriptManager():getItemsTag("Thermos"));
 end


function Recipe.GetItemTypes.SapphCookingRice(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingRice"));
	scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingRiceBowl"));
	scriptItems:addAll(getScriptManager():getItemsTag("CookedRice"));
end

function Recipe.GetItemTypes.SapphCookingCarrots(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Carrots"));
	scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingCarrot"));
    scriptItems:addAll(getScriptManager():getItemsTag("CannedCarrots"));
	scriptItems:addAll(getScriptManager():getItemsTag("Carrot"));
end

function Recipe.GetItemTypes.SapphCookingPotatoes(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Potatoes"));
	scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingPotato"));
    scriptItems:addAll(getScriptManager():getItemsTag("CannedPotatoes"));
	scriptItems:addAll(getScriptManager():getItemsTag("Potato"));
end

function Recipe.GetItemTypes.SapphCookingSoysauce(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingSoysauce"));
	scriptItems:addAll(getScriptManager():getItemsTag("Soysauce"));
end

function Recipe.GetItemTypes.SapphCookingCitrus(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingCitrus"));
	scriptItems:addAll(getScriptManager():getItemsTag("Citrus"));
end

	function Recipe.GetItemTypes.SapphCookingRiceBowl(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingRiceBowl"));
	scriptItems:addAll(getScriptManager():getItemsTag("RiceBowl"));
end

function Recipe.GetItemTypes.SapphCookingEgg(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingEgg"));
	scriptItems:addAll(getScriptManager():getItemsTag("Egg"));
end

function Recipe.GetItemTypes.SapphCookingCheese(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingCheese"));
	scriptItems:addAll(getScriptManager():getItemsTag("Cheese"));
	scriptItems:addAll(getScriptManager():getItemsTag("Cheeses"));
end

function Recipe.GetItemTypes.SapphCookingMilk(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingMilk"));
	scriptItems:addAll(getScriptManager():getItemsTag("Milk"));
end

function Recipe.GetItemTypes.SapphCookingSausages(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingSausages"));
	scriptItems:addAll(getScriptManager():getItemsTag("Sausages"));
	scriptItems:addAll(getScriptManager():getItemsTag("Sausage"));
end

function Recipe.GetItemTypes.SapphCookingChicken(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingChicken"));
	scriptItems:addAll(getScriptManager():getItemsTag("Chicken"));
end

function Recipe.GetItemTypes.SapphCookingFriedChickenRecipe(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingFriedChicken"));
	scriptItems:addAll(getScriptManager():getItemsTag("FriedChickenRecipe"));
end

function Recipe.GetItemTypes.SapphCookingSugar(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingSugar"));
	scriptItems:addAll(getScriptManager():getItemsTag("Sugar"));
end

function Recipe.GetItemTypes.SapphCookingBerry(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingBerry"));
	scriptItems:addAll(getScriptManager():getItemsTag("Berries"));
    scriptItems:addAll(getScriptManager():getItemsTag("Berry"));
end


function Recipe.GetItemTypes.SapphCookingPepper(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingPepper"));
	scriptItems:addAll(getScriptManager():getItemsTag("Pepper"));
end

function Recipe.GetItemTypes.SapphCookingSalt(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingSalt"));
	scriptItems:addAll(getScriptManager():getItemsTag("Salt"));
end

function Recipe.GetItemTypes.SapphCookingPasta(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingPasta"));
	scriptItems:addAll(getScriptManager():getItemsTag("Pasta"));
end

function Recipe.GetItemTypes.SapphCookingSyrup(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingSyrup"));
	scriptItems:addAll(getScriptManager():getItemsTag("Syrup"));
    scriptItems:addAll(getScriptManager():getItemsTag("Syrups"));
end

function Recipe.GetItemTypes.SapphCookingBeets(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Beet"));
	scriptItems:addAll(getScriptManager():getItemsTag("Beets"));
end

function Recipe.GetItemTypes.SapphCookingSliced(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingSlicedVegetables"));
	scriptItems:addAll(getScriptManager():getItemsTag("SlicedVegetables"));
end

function Recipe.GetItemTypes.SapphCookingCoffeeCup(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("CoffeeCup"));
end

function Recipe.GetItemTypes.SapphCookingMeltedChocolate(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("MeltedChocolate"));
	scriptItems:addAll(getScriptManager():getItemsTag("SapphCookingMeltedChocolate"));
end

function Recipe.GetItemTypes.SapphCookingIcing(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Icing"));
end

function Recipe.GetItemTypes.SapphCookingChocolate(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Chocolate"));
    scriptItems:addAll(getScriptManager():getItemsTag("ChocolateEgg"));
end

function Recipe.GetItemTypes.SapphCookingChocolate2(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("Chocolate"));
end

function Recipe.GetItemTypes.SapphCookingCakes(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("isCake"));
end

function Recipe.GetItemTypes.SapphCookingPastryCream(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("PastryCream"));
    scriptItems:addAll(getScriptManager():getItemsTag("Custard"));
end

function Recipe.GetItemTypes.SapphCookingFortuneCookie(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("isFortuneCookie"));
end

function Recipe.GetItemTypes.SapphCookingRisotto(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("isRisotto"));
end






function Recipe.OnCreate.SapphAutoCook(items, result, player) -- auto cooks foods, and reduces boredom/stress.
    result:setCooked(true);
    result:setHeat(2.5);
    result:setAge(0);

    --get the boredom/status of the player.
    --makes the player happy when cooking c:
    local body = player:getBodyDamage(); 
    local stats = player:getStats();    
    local currentUnhappiness = body:getUnhappynessLevel(); 
    local currentBoredom = body:getBoredomLevel();  
    local currentStress = stats:getStress();
    body:setBoredomLevel(currentBoredom - 4); 
    body:setUnhappynessLevel(currentUnhappiness - 4); 
    stats:setStress(currentStress - .1);
    HaloTextHelper.addTextWithArrow(player, getText("IGUI_HaloNote_Stress").. ", " .. getText("IGUI_HaloNote_Boredom"), false, HaloTextHelper.getColorGreen());
end

-- Adding Candles on cakes.
--  sapph(from 2023-ish): So... wasn't sure why i added this.
-- someone a few months ago said they had their birthday on zomboid,
-- so i guess this was made with that in mind, some way of adding candles on a cakes
-- birthdays should be fun, so hopefully this can help with that.
-- heh, only time will tell.

--sapph(from june/2024): guess, i'm doing back on this idea again :3
--this code is here, cause i don't want the player to be able to 
--re-use candles on cakes (since they'll give the player a stress/happiness boost).

function LightHalfCandle_OnCreate(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "HalfCandle" then
            result:setUsedDelta(item:getUsedDelta());
            result:setCondition(item:getCondition());
            result:setFavorite(item:isFavorite());
            if player:getPrimaryHandItem() == player:getSecondaryHandItem() then
                player:setPrimaryHandItem(nil)
            end
            player:setSecondaryHandItem(result);
            result:setActivated(true); --ensure the candle emits light upon creation
        end
    end
end

function ExtinguishHalfCandle_OnCreate(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "HalfCandleLit" then
            result:setUsedDelta(item:getUsedDelta());
            result:setCondition(item:getCondition());
            result:setFavorite(item:isFavorite());
        end
    end
end


    --Used for Cheese Making-
    --I could change it into the future, if more recipes end up needing to give back Pots, Buckets or Molds.
--Gives back a Pot.
function Recipe.OnCreate.SapphGivePot(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("Base.Pot", 1);
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "ClothFilter" then
            inv:AddItem("SapphCooking.DirtyClothFilter"); 
        end
    end
    result:setAge(0);
    result:setBoredomChange(-10);
end

--Gives back Cheese Mold
function Recipe.OnCreate.SapphGiveCheeseMold(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.Wooden_CheeseMolds", 1);
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "ClothFilter" then
            inv:AddItem("SapphCooking.DirtyClothFilter"); 
        end
    end
    result:setAge(0);
    result:setBoredomChange(-10);
end

--Gives back a Bucket (DEAR GOD!)
function Recipe.OnCreate.SapphGiveBucket(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("SapphCooking.BucketEmpty", 1);
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "ClothFilter" then
            inv:AddItem("SapphCooking.DirtyClothFilter"); 
        end
    end
    result:setAge(0);
    result:setBoredomChange(-10);
end

--Gives back a Baking Tray
function Recipe.OnCreate.SapphGiveTray(items, result, player)
	local inv = player:getInventory();
	inv:AddItem("Base.BakingTray", 1);
end

--Used to fix any rotten issues in multiplayer.
function Recipe.OnCreate.SapphSetAgeZero(items, result, player)
	result:setAge(0);
end




--Tests to see if a food has been eaten before crafting it.
function Recipe.OnTest.WholeSlicedVegetables(item)
	if not item:getTags():contains("SlicedVegetables") then return true end
	local baseHunger = math.abs(item:getBaseHunger())
	local hungerChange = math.abs(item:getHungerChange())
    if item:isFresh() then return not (baseHunger > hungerChange) end
    return not ((baseHunger * 0.75) > hungerChange)
end
--sapph (25/01/2024): okay, so this function does not work! so,  i'll look for a work-around!



--Adds the code to make Wok/Frying Pan Evolved Bowls... or pieces.
--sapph: Okay... so... what this does is, it first checks what type of pan the player has using "addType", then,
-- it creates a condition with a set value, that way  whatever condition the pan was, it will still stay the same.
-- after that, it checks every item in the recipe, and checks if the item type matches the recipe type.
-- it divides the values by 2 or 4, depending on how many bowls the player will get.
-- this is mainly for evolved recipes, where the player adds stuff on something.
-- anyhow, thanks! see ya around!

--used for checking if a food item is cooked.
function Recipe.OnCanPerform.CheckCooking(recipe, playerObj, item)
    return item and item:isCooked();
end

--for icecubes and popscicles
function Recipe.OnCanPerform.CheckFrozen(recipe, playerObj, item)
    return item and item:isFrozen();
end

--used for checking if a food item is rotten (this is mainly used to bypass some recipes not working in multiplayer ex:cheese).
function Recipe.OnCanPerform.CheckRotten(recipe, playerObj, item)
    return item and item:isRotten();
end


--Woks--
function Recipe.OnCreate.MakeBowlofFood2Wok(items, result, player)
    local addType = "SapphCooking.WokPan"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "WokPanwithFriedRice" or item:getType() == "WokPan_KungPaoChicken" or item:getType() == "WokFriedRiceEvolved" or item:getType() == "WokPan_Yakisoba" or item:getType() == "WokPan_YakisobaEvolved" then
            result:setBaseHunger(item:getBaseHunger() / 2);
            result:setHungChange(item:getHungChange() / 2);
            result:setThirstChange(item:getThirstChangeUnmodified() / 2);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 2);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 2);
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            condition = item:getCondition();
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition);
end

function Recipe.OnCreate.MakeBowlofFood4Wok(items, result, player)
    local addType = "SapphCooking.WokPan"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "WokPanwithFriedRice" or item:getType() == "WokFriedRiceEvolved" or item:getType() == "WokPan_Yakisoba" or item:getType() == "WokPan_YakisobaEvolved" then
            result:setBaseHunger(item:getBaseHunger() / 4);
            result:setHungChange(item:getHungChange() / 4);
            result:setThirstChange(item:getThirstChangeUnmodified() / 4);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 4);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 4);
            result:setCarbohydrates(item:getCarbohydrates() / 4);
            result:setLipids(item:getLipids() / 4);
            result:setProteins(item:getProteins() / 4);
            result:setCalories(item:getCalories() / 4);
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            condition = item:getCondition();
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition);
end

--Frying Pans
function Recipe.OnCreate.MakeBowlofFood2Pan(items, result, player)
    local addType = "Base.Pan"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "FryingPanwithFriedRice" or item:getType() == "FryingPanFriedRiceEvolved" then
            result:setBaseHunger(item:getBaseHunger() / 2);
            result:setHungChange(item:getHungChange() / 2);
            result:setThirstChange(item:getThirstChangeUnmodified() / 2);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 2);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 2);
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            condition = item:getCondition();
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition);
end

function Recipe.OnCreate.MakeBowlofFood4Pan(items, result, player)
    local addType = "Base.Pan"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "FryingPanwithFriedRice" or item:getType() == "FryingPanFriedRiceEvolved" then
            result:setBaseHunger(item:getBaseHunger() / 4);
            result:setHungChange(item:getHungChange() / 4);
            result:setThirstChange(item:getThirstChangeUnmodified() / 4);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 4);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 4);
            result:setCarbohydrates(item:getCarbohydrates() / 4);
            result:setLipids(item:getLipids() / 4);
            result:setProteins(item:getProteins() / 4);
            result:setCalories(item:getCalories() / 4);
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            condition = item:getCondition();
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition);
end

--Saucepans
function Recipe.OnCreate.MakeBowlofFood2Saucepan(items, result, player)
    local addType = "Base.Saucepan"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "SaucepanwithRavioli"  or item:getType() == "SaucepanwithChickenStroganoff" or item:getType() == "SaucepanwithJapaneseCurry" or item:getType() == "SaucepanwithMashedPotatoes" or item:getType() == "SaucepanwithBeefStew" or item:getType() == "SaucepanwithSpaguetti" or item:getType() == "SaucepanwithUncookedSpaguetti" or item:getType() == "Saucepan_InstantNoodlesEvolved" or item:getType() == "Saucepan_InstantNoodles" or item:getType() == "SaucepanwithTortellini" or item:getType() == "Saucepan_ArrozLeche" or item:getType() == "Saucepan_ArrozLecheUn" or item:getType() == "SaucepanwithBorscht" or item:getType() == "SaucepanwithRisotto" or item:getType() == "SaucepanwithNoodleSoup" then
            result:setBaseHunger(item:getBaseHunger() / 2);
            result:setHungChange(item:getHungChange() / 2);
            result:setThirstChange(item:getThirstChangeUnmodified() / 2);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 2);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 2);
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
            result:setCooked(true);
        end
    end
    local pot = player:getInventory():AddItem(addType);
end

--Lasagna/Ratatouille  Pieces 
--sapph: so... this checks if the item name is lasagna or ratattouile, then divides it by x amount.
function Recipe.OnCreate.CutLasagna4Pieces(items, result, player)
    local addType = "Base.RoastingPan"
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "RoastingpanwithLasagna" or item:getType() == "RoastingpanwithUncookedLasagna" or item:getType() == "RoastingpanwithRatatouilleUn" or item:getType() == "RoastingpanwithRatatouilleCooked" then
            result:setBaseHunger(item:getBaseHunger() / 4);
            result:setHungChange(item:getHungChange() / 4);
            result:setThirstChange(item:getThirstChangeUnmodified() / 4);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 4);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 4);
            result:setCarbohydrates(item:getCarbohydrates() / 4);
            result:setLipids(item:getLipids() / 4);
            result:setProteins(item:getProteins() / 4);
            result:setCalories(item:getCalories() / 4);
        end
    end
    local pot = player:getInventory():AddItem(addType);
end

function Recipe.OnCreate.CutLasagna8Pieces(items, result, player)
    local addType = "Base.RoastingPan"
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "RoastingpanwithLasagna" or item:getType() == "RoastingpanwithUncookedLasagna" or item:getType() == "RoastingpanwithRatatouilleUn" or item:getType() == "RoastingpanwithRatatouilleCooked" then
            result:setBaseHunger(item:getBaseHunger() / 8);
            result:setHungChange(item:getHungChange() / 8);
            result:setThirstChange(item:getThirstChangeUnmodified() / 8);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 8);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 8);
            result:setCarbohydrates(item:getCarbohydrates() / 8);
            result:setLipids(item:getLipids() / 8);
            result:setProteins(item:getProteins() / 8);
            result:setCalories(item:getCalories() / 8);
        end
    end
    local pot = player:getInventory():AddItem(addType);
end

--Thermos Soup/Stew
--sapph:code that transfer every stats of a soup pot, into a thermos can!
function Recipe.OnCreate.SoupThermos(items, result, player)
    local addType = "Base.Pot"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "PotOfSoup" or  item:getType() == "PotOfSoupRecipe" or item:getType() == "PotOfStew" then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            condition = item:getCondition();
            if string.contains(item:getType(), "Pan") or string.contains(item:getType(), "Saucepan") then
                addType = "Base.Saucepan"
            end
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition)
end


--Hotdogs
--sapph:code for custom hotdogs.
function Recipe.OnCreate.HotdogEvolved(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Sausage" or item:getType() == "FrankfurterSausage" or item:getType() == "ViennaSausage" or item:getType() == "SausageEvolved" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories() + 200);
        end
    end
end

--Stock
--sapph:code for custom vegetable broths
function Recipe.OnCreate.StockBottle(items, result, player)
    local addType = "Base.Pot"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "PotofVegetableStock" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setWeight(item:getWeight());
            result:setActualWeight(item:getActualWeight())
            result:setCooked(true);
            condition = item:getCondition();
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition)
end


--GRIND Meat

function Recipe.OnCreate.GrindMeat(items, result, player)
    --sapph: this code is being used in tenderizing meat as well, since it uses the same base items
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Sausage" or item:getType() == "Chicken" or item:getType() == "Smallanimalmeat" or item:getType() == "Meatballs" or item:getType() == "Smallbirdmeat" or item:getType() == "SlicedChicken" or item:getType() == "SlicedChickenBatter" or item:getType() == "FrankfurterSausage" or item:getType() == "ViennaSausage" or item:getType() == "MuttonChop" or item:getType() == "SlicedSteak" or item:getType() == "Steak" or item:getType() == "PorkChop" or item:getType() == "Rabbitmeat" or item:getType() == "MincedMeat" or item:getType() == "MincedMeat_Chicken"then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setCustomWeight(true);
            result:setWeight(0.8);
            result:setActualWeight(0.8);
        end
    end
end

function Recipe.OnCreate.PreparePasta(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Ravioli" or item:getType() == "RolledPastaDough" or item:getType() == "Tortellini" or item:getType() == "Meatballs" or item:getType() == "FilledMeatPastaDough" or item:getType() == "SlicedChicken" or item:getType() == "SlicedChickenBatter" or item:getType() == "FrankfurterSausage" or item:getType() == "ViennaSausage" or item:getType() == "MuttonChop" or item:getType() == "SlicedSteak" or item:getType() == "Steak" or item:getType() == "PorkChop" or item:getType() == "Rabbitmeat" or item:getType() == "MincedMeat" or item:getType() == "MincedMeat_Chicken" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setCooked(true);
            result:setHeat(2.5);
        end
    end
end
function Recipe.OnCreate.FillPasta(items, result, player)
--sapph: so, i just decided to hard code it, since it seems it overlaps the current calorie intake of the pasta!
--since its only used in one recipe, i think this is a way that works, until a smart and better way is done!
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() =="MincedMeat" or item:getType() == "MincedMeat_Chicken" then
            result:setBaseHunger(item:getBaseHunger() * 1.5);
            result:setHungChange(item:getHungChange() * 1.5);
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids() + 6);
            result:setProteins(item:getProteins() + 17);
            result:setCalories(item:getCalories() + 532);

        end
    end
end


--Batter/Fry Recipes
--adds itens to the batter
function Recipe.OnCreate.FryingBatter(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "TenderizedMeat" or item:getType() == "BreadedTenderizedMeat" or item:getType() == "FishFilletinBatter" or item:getType() == "SapphFishFried" or item:getType() == "Schnitzel" or item:getType() == "Smallbirdmeat" or item:getType() == "SmallbirdmeatinBatter" or item:getType() == "FishFillet" or item:getType() == "SausageinBatter" or item:getType() == "Sausage" or item:getType() == "SausageEvolved" or item:getType() == "ViennaSausage" or item:getType() == "DeadRat" or item:getType() == "SlicedChicken" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories() + 50);
            result:setAge(0);
        end
    end
end

--returns the result cooked!
function Recipe.OnCreate.FryingCooking(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "TenderizedMeat" or item:getType() == "CutPeeledPotato" or item:getType() == "PeeledPotato" or item:getType() == "CrabRangoon" or item:getType() == "Butter" or item:getType() == "CoxinhaPrep" or item:getType() == "BreadedTenderizedMeat" or item:getType() == "FishFilletinBatter" or item:getType() == "SapphFishFried" or item:getType() == "Schnitzel" or item:getType() == "Smallbirdmeat" or item:getType() == "SmallbirdmeatinBatter" or item:getType() == "FishFillet" or item:getType() == "SausageinBatter" or item:getType() == "Sausage" or item:getType() == "SausageEvolved" or item:getType() == "ViennaSausage" or item:getType() == "DeadRat" or item:getType() == "SlicedChicken" or item:getType() == "Tortilla" or item:getType() == "Chicken" or item:getType() == "ScotchEggRaw" or item:getType() == "Potato" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified() - 4);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() - 4);
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories() + 83);
            result:setCooked(true);
            result:setHeat(2.5);
            result:setAge(0);
        end
    end
    --get the boredom/status of the player.
    --makes the player happy when cooking c:
    local body = player:getBodyDamage(); 
    local stats = player:getStats();    
    local currentUnhappiness = body:getUnhappynessLevel(); 
    local currentBoredom = body:getBoredomLevel();  
    local currentStress = stats:getStress();
    body:setBoredomLevel(currentBoredom - 4); 
    body:setUnhappynessLevel(currentUnhappiness - 4); 
    stats:setStress(currentStress - .1);
    HaloTextHelper.addTextWithArrow(player, getText("IGUI_HaloNote_Stress").. ", " .. getText("IGUI_HaloNote_Boredom"), false, HaloTextHelper.getColorGreen());
end

--divides by two
function Recipe.OnCreate.MeatDivide2(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "SlicedChicken" or item:getType() =="MincedMeat" or item:getType() == "MincedMeat_Chicken" or item:getType() == "FishFillet" then
            result:setBaseHunger(item:getBaseHunger() / 2);
            result:setHungChange(item:getHungChange() / 2);
            result:setThirstChange(item:getThirstChangeUnmodified() / 2);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 2);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 2);
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
        end
    end
end
--divides by two but cooks the item.
function Recipe.OnCreate.Divide2Cooked(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "SlicedChicken" or item:getType() == "SaucepanwithRisotto" or item:getType() == "BowlofRisotto" or item:getType() =="MincedMeat" or item:getType() == "MincedMeat_Chicken" or item:getType() == "FishFillet" then
            result:setBaseHunger(item:getBaseHunger() / 2);
            result:setHungChange(item:getHungChange() / 2);
            result:setThirstChange(item:getThirstChangeUnmodified() / 2);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 2);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 2);
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
            result:setCooked(true);
        end
    end

    --get the boredom/status of the player.
    --makes the player happy when cooking c:
    local body = player:getBodyDamage(); 
    local stats = player:getStats();    
    local currentUnhappiness = body:getUnhappynessLevel(); 
    local currentBoredom = body:getBoredomLevel();  
    local currentStress = stats:getStress();
    body:setBoredomLevel(currentBoredom - 4); 
    body:setUnhappynessLevel(currentUnhappiness - 4); 
    stats:setStress(currentStress - .1);
    HaloTextHelper.addTextWithArrow(player, getText("IGUI_HaloNote_Stress").. ", " .. getText("IGUI_HaloNote_Boredom"), false, HaloTextHelper.getColorGreen());
end

--Coffee Thermos 
--sapph: decided to only get calories,proteins and stuff like that, so the original result doenst stay too far off the correct values.
function Recipe.OnCreate.ThermosCups(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "ThermosCoffee" or item:getType() == "ThermosCoffeeEvolved" or item:getType() == "ThermosBeverage" then
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
            result:setWeight(item:getWeight()/2);
            result:setActualWeight(item:getActualWeight()/2)
        end
    end
end

--Sausages Casings.
--sapph:this is basically the original butchering animal code...but it gives you sausage casings at the end.


function Recipe.OnCreate.MakeSausagesCasing(items, result, player)
    local anim = nil;
    for i=0,items:size() - 1 do
        if instanceof(items:get(i), "Food") then
            anim = items:get(i);
            break;
        end
    end
    if anim then
        local new_hunger = anim:getHungChange() * 1.05;
        if(new_hunger < -100) then
            new_hunger = -100;
        end
        result:setBaseHunger(new_hunger);
        result:setHungChange(new_hunger);

        result:setCustomWeight(true);
        result:setWeight(anim:getWeight() * 0.7);
        result:setActualWeight(anim:getActualWeight() * 0.7);

        result:setLipids(anim:getLipids() * 0.75);
        result:setProteins(anim:getProteins() * 0.75);
        result:setCalories(anim:getCalories() * 0.75);
        result:setCarbohydrates(anim:getCarbohydrates() * 0.75);
    end
    local inv = player:getInventory();
	inv:AddItem("SapphCooking.SausageCasing", 3);
end


function Recipe.OnCreate.DrinkMixCups(items, result, player)

    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "CocktailMixerEvolved" or item:getType() == "CocktailMixerPrep" then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
        end
    end
    local inv = player:getInventory();
	inv:AddItem("SapphCooking.CocktailMixer");
end

function Recipe.OnCreate.ChurnBottle(items, result, player)

    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Churn_Buttermilk" or item:getType() == "Churn_Wine" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setCustomWeight(true);
            result:setWeight(0.5);
            result:setActualWeight(0.5);
        end
    end
    local inv = player:getInventory();
	inv:AddItem("SapphCooking.ButterChurn");
end


function Recipe.OnCreate.TakeoutBox(items, result, player)
    local addType = "Base.Pot"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "SaucepanwithRavioli" or item:getType() == "SaucepanwithSpaguetti" or item:getType() == "SaucepanwithUncookedSpaguetti" or item:getType() == "Saucepan_InstantNoodlesEvolved" or item:getType() == "Saucepan_InstantNoodles" or item:getType() == "SaucepanwithTortellini" or item:getType() == "Saucepan_ArrozLeche" or item:getType() == "Saucepan_ArrozLecheUn" or item:getType() == "SaucepanwithBorscht" or item:getType() == "WokPan_RoastVeg" or item:getType() == "Blender_Puree" or item:getType() == "FryingPanFriedRiceEvolved" or item:getType() == "WokFriedRiceEvolved" or item:getType() == "WokPan_YakisobaEvolved" or item:getType() == "PotOfStew" or item:getType() == "PotOfSoupRecipe" or item:getType() == "PanFriedVegetables"  or item:getType() == "GriddlePanFriedVegetables" or item:getType() == "PanFriedVegetables2" or item:getType() == "PastaPot" then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger() / 2);
            result:setHungChange(item:getHungChange() / 2);
            result:setThirstChange(item:getThirstChangeUnmodified() / 2);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 2);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 2);
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1);
            result:setActualWeight(1);
            condition = item:getCondition()
            if string.contains(item:getType(), "Pan") or string.contains(item:getType(), "FryingPan") then
                addType = "Base.Pan"
            elseif string.contains(item:getType(), "Saucepan") then
                addType = "Base.Saucepan"
            elseif string.contains(item:getType(), "Wok") then
                addType = "SapphCooking.WokPan"
            end
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition)
end


function Recipe.OnCreate.RoastPanAddTwo(items, result, player)
    local addType = "Base.RoastingPan"
    local condition = 10;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "GriddlePanFriedVegetables" or item:getType() == "PanFriedVegetables2" then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger() / 2);
            result:setHungChange(item:getHungChange() / 2);
            result:setThirstChange(item:getThirstChangeUnmodified() / 2);
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 2);
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 2);
            result:setCarbohydrates(item:getCarbohydrates() / 2);
            result:setLipids(item:getLipids() / 2);
            result:setProteins(item:getProteins() / 2);
            result:setCalories(item:getCalories() / 2);
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1);
            result:setActualWeight(1);
            condition = item:getCondition()
            if string.contains(item:getType(), "GriddlePanFriedVegetables") then
                addType = "Base.GridlePan"
            end
        end
    end
    local pot = player:getInventory():AddItem(addType);
    pot:setCondition(condition)
end

--BLENDERS
function Recipe.OnCreate.PureeMix(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Blender_Prep_Puree" or item:getType() == "Blender_Puree"  then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1.5);
            result:setActualWeight(1.5);
        end
    end
end

function Recipe.OnCreate.SmoothieMix(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() ==  "Blender_Prep_Smoothie"  or item:getType() == "Blender_Smoothie"  then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1.5);
            result:setActualWeight(1.5);
        end
    end
end

function Recipe.OnCreate.SorbetMix(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Blender_Juice" or item:getType() == "Blender_Sorbet" or item:getType() == "Blender_Prep_Juice" then
            result:setName(item:getName() .. " Sorbet");
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1.5);
            result:setActualWeight(1.5);
        end
    end
end
function Recipe.OnCreate.PopsicleMix(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Blender_Juice" or item:getType() == "Blender_Prep_Juice" then
            result:setName(item:getName() .. " Popsicle");
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
        end
    end
end

function Recipe.OnCreate.JuiceMix(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Blender_Juice" or item:getType() == "Blender_Prep_Juice" then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1.5);
            result:setActualWeight(1.5);
        end
    end
end

function Recipe.OnCreate.MilkshakeMix(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Blender_Milkshake" or item:getType() == "Blender_Prep_Shake" then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1.5);
            result:setActualWeight(1.5);
        end
    end
end

function Recipe.OnCreate.BlenderAddBowl(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Blender_Milkshake" or item:getType() == "Blender_Prep_Shake"  or item:getType() == "Blender_Juice" or item:getType() == "Blender_Prep_Juice" or item:getType() == "Blender_Sorbet" or item:getType() == "Blender_Prep_Puree" or item:getType() == "Blender_Puree" or item:getType() ==  "Blender_Prep_Smoothie"  or item:getType() == "Blender_Smoothie" then
            result:setName(item:getName());
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setTaintedWater(item:isTaintedWater());
            result:setCooked(true);
            result:setCustomWeight(true);
            result:setWeight(1.2);
            result:setActualWeight(1.2);
        end
    end
    local inv = player:getInventory();
	inv:AddItem("SapphCooking.Blender",1);
end

--Divides into five, cooks and gives a baking pan.
function Recipe.OnCreate.SapphSliceCakes(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "PieWholeRaw" or item:getType() == "PieWholeRawSweet" or item:getType() == "Cake_NianGao" or item:getType() == "CakeRaw" or item:getType() == "CakeRaw_Chocolate" or item:getType() == "CakeRaw_Carrot" or item:getType() == "CakeRaw_Strawberry" or item:getType() == "CakeRaw_BlackForest" or item:getType() == "CakeRaw_Birthday" or item:getType() == "CakeRaw_RedVelvet" or item:getType() == "CakePrep_Chocolate" or item:getType() == "CakePrep_Carrot" or item:getType() == "CakePrep_Strawberry" or item:getType() == "CakePrep_BlackForest" or item:getType() == "CakePrep_Birthday" or item:getType() == "CakePrep_RedVelvet" or item:getType() == "Parmesan_CheeseWheel" then
            result:setBaseHunger(item:getBaseHunger() / 5);
            result:setHungChange(item:getHungChange() / 5);
            result:setThirstChange(item:getThirstChangeUnmodified() / 5)
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 5)
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 5)
            result:setCalories(item:getCalories() / 5)
            result:setCarbohydrates(item:getCarbohydrates() / 5)
            result:setLipids(item:getLipids() / 5)
            result:setProteins(item:getProteins() / 5)
            result:setCooked(true);
            result:setAge(0);
        end
    end
    player:getInventory():AddItem("Base.BakingPan");
end

--Divides into five.
function Recipe.OnCreate.SapphSliceIntoFives(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Parmesan_CheeseWheel" or item:getType() == "Roastingpan_FocacciaPrep" or item:getType() == "Blue_CheeseWheel" or item:getType() == "BucketofFriedBird" or item:getType() == "BucketofPopcorn" or item:getType() == "BucketofCandyPopcorn" then
            result:setBaseHunger(item:getBaseHunger() / 5);
            result:setHungChange(item:getHungChange() / 5);
            result:setThirstChange(item:getThirstChangeUnmodified() / 5)
            result:setBoredomChange(item:getBoredomChangeUnmodified() / 5)
            result:setUnhappyChange(item:getUnhappyChangeUnmodified() / 5)
            result:setCalories(item:getCalories() / 5)
            result:setCarbohydrates(item:getCarbohydrates() / 5)
            result:setLipids(item:getLipids() / 5)
            result:setProteins(item:getProteins() / 5)
            result:setAge(0);
            result:setCooked(true);
            if string.contains(item:getType(), "BucketofFriedBird" or "BucketofPopcorn" or "BucketofCandyPopcorn") then
                local addType = "Base.BucketEmpty"
                local pot = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnRoastPan") then
                local addType = "Base.RoastingPan"
                local pot = player:getInventory():AddItem(addType);
            end
        end
    end
end




function Recipe.OnCreate.PopscicleCreate(items, result, player)

    Colors={ "SapphCooking.Popsicle_White", 
    "SapphCooking.Popsicle_Pink", 
    "SapphCooking.Popsicle_Blue",
    "SapphCooking.Popsicle_Purple",
    "SapphCooking.Popsicle_Green",
    "SapphCooking.Popsicle_Red",
    "SapphCooking.Popsicle_Orange",
    "SapphCooking.Popsicle_Yellow", }

    local inv = player:getInventory();
	inv:AddItem(Colors[ZombRand(1, #Colors+1)], 1)
end



function Recipe.OnCreate.SapphCottonCandy(items, result, player)
   
    Results={ "SapphCooking.CottonCandy_White", 
"SapphCooking.CottonCandy_Pink", 
"SapphCooking.CottonCandy_Blue",
"SapphCooking.CottonCandy_Purple",
"SapphCooking.CottonCandy_Green",
"SapphCooking.CottonCandy_Red",
"SapphCooking.CottonCandy_Yellow",
"SapphCooking.CottonCandy_Orange", }

    local inv = player:getInventory();
	inv:AddItem(Results[ZombRand(1, #Results+1)], 1)

end

function Recipe.OnCreate.SapphFortuneCookie(items, result, player)
   
    Fortunes={ "SapphCooking.Fortune_Message1", 
"SapphCooking.Fortune_Message2", 
"SapphCooking.Fortune_Message3",
"SapphCooking.Fortune_Message4",
"SapphCooking.Fortune_Message5",
"SapphCooking.Fortune_Message6",
"SapphCooking.Fortune_Message7",
"SapphCooking.Fortune_Message8",
"SapphCooking.Fortune_Message9",
"SapphCooking.Fortune_Message10", }

    local inv = player:getInventory();
	inv:AddItem(Fortunes[ZombRand(1, #Fortunes+1)], 1)

    --get the boredom/status of the player.
    --makes the player happy when cooking c:
    local body = player:getBodyDamage(); 
    local stats = player:getStats();    
    local currentUnhappiness = body:getUnhappynessLevel(); 
    local currentBoredom = body:getBoredomLevel();  
    local currentStress = stats:getStress();
    body:setBoredomLevel(currentBoredom - 5); 
    body:setUnhappynessLevel(currentUnhappiness - 5); 
    stats:setStress(currentStress - 2);
    HaloTextHelper.addTextWithArrow(player, getText("IGUI_HaloNote_Stress").. ", " .. getText("IGUI_HaloNote_Boredom"), false, HaloTextHelper.getColorGreen());
end


function Recipe.OnCreate.SapphBrewCoffee(items, result, player)
    result:setCooked(true);
    result:setHeat(2.5);
    local inv = player:getInventory();
	inv:AddItem("SapphCooking.DirtyClothFilter");
end

--cakes~
function Recipe.OnCreate.SapphCakeAddCandle(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "CakeRaw" or item:getType() == "CakeRaw_Chocolate" or item:getType() == "CakeRaw_BlackForestCake" or item:getType() == "CakeRaw_Carrot" or item:getType() == "CakeRaw_Strawberry" or item:getType() == "CakeRaw_RedVelvet" or item:getType() == "CakeRaw_Birthday" or item:getType() == "CakePrep_Chocolate" or item:getType() == "CakePrep_Carrot" or item:getType() == "CakePrep_Strawberry" or item:getType() == "CakePrep_BlackForest" or item:getType() == "CakePrep_Birthday" or item:getType() == "CakePrep_RedVelvet" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setWeight(item:getWeight());
            if player:getPrimaryHandItem() == player:getSecondaryHandItem() then
                player:setPrimaryHandItem(nil)
            end
            player:setSecondaryHandItem(result);
        end
    end
end

function Recipe.OnCreate.SapphCakeRemoveCandle(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "Cake_Candle" or item:getType() == "CakeChocolate_Candle" or item:getType() == "CakeBlackForest_Candle" or item:getType() == "CakeCarrot_Candle" or item:getType() == "CakeStrawberry_Candle" or item:getType() == "CakeRedVelvet_Candle" or item:getType() == "CakeBirthday_Candle" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
            result:setWeight(item:getWeight());
            result:setCooked(true);
        end
    end
    --adds a half used candle, so you can't farm wishes~
    local inv = player:getInventory();
	inv:AddItem("SapphCooking.HalfCandle");

    --get the boredom/status of the player.
    --makes the player happy when cooking c:
    local body = player:getBodyDamage(); 
    local stats = player:getStats();    
    local currentUnhappiness = body:getUnhappynessLevel(); 
    local currentBoredom = body:getBoredomLevel();  
    local currentStress = stats:getStress();
    body:setBoredomLevel(currentBoredom - 30); 
    body:setUnhappynessLevel(currentUnhappiness - 30); 
    stats:setStress(currentStress - 10);
    HaloTextHelper.addTextWithArrow(player, getText("IGUI_HaloNote_Stress").. ", " .. getText("IGUI_HaloNote_Boredom"), false, HaloTextHelper.getColorGreen());
end


function Recipe.OnCreate.SapphBirthdayCake(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
	    if item:getTags():contains("isCake") then 
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange());
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories() + 30);
            result:setWeight(item:getWeight());
            result:setCooked(true);
        end
    end
end

--Gives a random tray, with random macaron colors.
function Recipe.OnCreate.SapphMacaronTray(items, result, player)
   
    Trays={ "SapphCooking.BakingTray_MacaronCookies1", 
    "SapphCooking.BakingTray_MacaronCookies2", 
    "SapphCooking.BakingTray_MacaronCookies3",
    "SapphCooking.BakingTray_MacaronCookies4",
    }   
    local inv = player:getInventory();
	inv:AddItem(Trays[ZombRand(1, #Trays+1)], 1)
end

--gives an empty can
function Recipe.OnCreate.SapphEmptyCan(items, result, player)
   
    local inv = player:getInventory();
	inv:AddItem("Base.TinCanEmpty");
end

-- reads the tooltip message on the fortune cookie, and says it out loud.
function Recipe.OnCreate.SapphReadFortuneCookie(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
	    if item:getTags():contains("isFortuneCookie") then 
            local tooltip = item:getTooltip();
            player:Say(getText(tooltip))
        end
    end
end

-- sapph: for prep recipes, it checks for every food item values in the recipe, then adds it on the result.
-- i should... rework my code around this - take note future sapph! 
function Recipe.OnCreate.SapphCreatePrep(items, result, player)
    local hungerSum = 0;
    local hungerChangeSum = 0;
    local thirstSum = 0;
    local unhappySum = 0;
    local carbsSum = 0;
    local lipidsSum = 0;
    local proteinsSum = 0;
    local caloriesSum = 0;
    local weightSum = 0;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if instanceof(item, "Food") then
            hungerSum = hungerSum + item:getBaseHunger();
            hungerChangeSum = hungerChangeSum + item:getHungChange();
            thirstSum = thirstSum;
            unhappySum = unhappySum + item:getUnhappyChangeUnmodified();
            carbsSum = carbsSum + item:getCarbohydrates();
            lipidsSum = lipidsSum + item:getLipids();
            proteinsSum = proteinsSum + item:getProteins();
            caloriesSum = caloriesSum + item:getCalories();
            weightSum = weightSum + item:getWeight();
            if item:getTags():contains("returnSaucepan") then
                local addType = "Base.Saucepan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnPan") then
                local addType = "Base.Pan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnRoastPan") then
                local addType = "Base.RoastingPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnWokPan") then
                local addType = "SapphCooking.WokPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnBowl") then
                local addType = "Base.Bowl"
                local item = player:getInventory():AddItem(addType);
            end
        end
    end
    result:setBaseHunger(hungerSum);
    result:setHungChange(hungerChangeSum);
    result:setThirstChange(thirstSum);
    result:setUnhappyChange(unhappySum);
    result:setCarbohydrates(carbsSum);
    result:setLipids(lipidsSum);
    result:setProteins(proteinsSum);
    result:setCalories(caloriesSum);
    result:setAge(0);
end

function Recipe.OnCreate.SapphCreatePrepCooked(items, result, player)
    local hungerSum = 0;
    local hungerChangeSum = 0;
    local thirstSum = 0;
    local unhappySum = 0;
    local carbsSum = 0;
    local lipidsSum = 0;
    local proteinsSum = 0;
    local caloriesSum = 0;
    local weightSum = 0;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if instanceof(item, "Food") then
            hungerSum = hungerSum + item:getBaseHunger();
            hungerChangeSum = hungerChangeSum + item:getHungChange();
            thirstSum = thirstSum;
            unhappySum = unhappySum + item:getUnhappyChangeUnmodified();
            carbsSum = carbsSum + item:getCarbohydrates();
            lipidsSum = lipidsSum + item:getLipids();
            proteinsSum = proteinsSum + item:getProteins();
            caloriesSum = caloriesSum + item:getCalories();
            weightSum = weightSum + item:getWeight();
            if item:getTags():contains("returnSaucepan") then
                local addType = "Base.Saucepan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnPan") then
                local addType = "Base.Pan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnRoastPan") then
                local addType = "Base.RoastingPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnWokPan") then
                local addType = "SapphCooking.WokPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnBowl") then
                local addType = "Base.Bowl"
                local item = player:getInventory():AddItem(addType);
            end
        end
    end
    result:setBaseHunger(hungerSum);
    result:setHungChange(hungerChangeSum);
    result:setThirstChange(thirstSum);
    result:setUnhappyChange(unhappySum);
    result:setCarbohydrates(carbsSum);
    result:setLipids(lipidsSum);
    result:setProteins(proteinsSum);
    result:setCalories(caloriesSum);
    result:setAge(0);
    result:setCooked(true);
    result:setHeat(2.5);
end

-- for prep recipes with two results.
function Recipe.OnCreate.SapphCreatePrep_Two(items, result, player)
    local hungerSum = 0;
    local hungerChangeSum = 0;
    local thirstSum = 0;
    local boredomSum = 0;
    local unhappySum = 0;
    local carbsSum = 0;
    local lipidsSum = 0;
    local proteinsSum = 0;
    local caloriesSum = 0;
    local weightSum = 0;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if instanceof(item, "Food") then
            hungerSum = hungerSum + item:getBaseHunger();
            hungerChangeSum = hungerChangeSum + item:getHungChange();
            thirstSum = thirstSum + item:getThirstChangeUnmodified();
            boredomSum = boredomSum + item:getBoredomChangeUnmodified();
            unhappySum = unhappySum + item:getUnhappyChangeUnmodified();
            carbsSum = carbsSum + item:getCarbohydrates();
            lipidsSum = lipidsSum + item:getLipids();
            proteinsSum = proteinsSum + item:getProteins();
            caloriesSum = caloriesSum + item:getCalories();
            weightSum = weightSum + item:getWeight();
            if item:getTags():contains("returnSaucepan") then
                local addType = "Base.Saucepan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnPan") then
                local addType = "Base.Pan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnRoastPan") then
                local addType = "Base.RoastingPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnWokPan") then
                local addType = "SapphCooking.WokPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnBowl") then
                local addType = "Base.Bowl"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnJarLid") then
                local addType = "Base.EmptyJar"
                local addType2 = "Base.JarLid"
                local item = player:getInventory():AddItem(addType);
                local item2 = player:getInventory():AddItem(addType2);
            end
        end
    end
    result:setBaseHunger(hungerSum/2);
    result:setHungChange(hungerChangeSum/2);
    result:setThirstChange(thirstSum/2);
    result:setBoredomChange(boredomSum/2);
    result:setUnhappyChange(unhappySum/2);
    result:setCarbohydrates(carbsSum/2);
    result:setLipids(lipidsSum/2);
    result:setProteins(proteinsSum/2);
    result:setCalories(caloriesSum/2);
    result:setCustomWeight(true);
    result:setWeight(weightSum/2);
    result:setActualWeight(weightSum/2);
    result:setAge(0);
    result:setCooked(true);
end

-- for prep recipes with three results.
function Recipe.OnCreate.SapphCreatePrep_Three(items, result, player)
    local hungerSum = 0;
    local hungerChangeSum = 0;
    local thirstSum = 0;
    local boredomSum = 0;
    local unhappySum = 0;
    local carbsSum = 0;
    local lipidsSum = 0;
    local proteinsSum = 0;
    local caloriesSum = 0;
    local weightSum = 0;
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if instanceof(item, "Food") then
            hungerSum = hungerSum + item:getBaseHunger();
            hungerChangeSum = hungerChangeSum + item:getHungChange();
            thirstSum = thirstSum + item:getThirstChangeUnmodified();
            boredomSum = boredomSum + item:getBoredomChangeUnmodified();
            unhappySum = unhappySum + item:getUnhappyChangeUnmodified();
            carbsSum = carbsSum + item:getCarbohydrates();
            lipidsSum = lipidsSum + item:getLipids();
            proteinsSum = proteinsSum + item:getProteins();
            caloriesSum = caloriesSum + item:getCalories();
            weightSum = weightSum + item:getWeight();
            if item:getTags():contains("returnSaucepan") then
                local addType = "Base.Saucepan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnPan") then
                local addType = "Base.Pan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnRoastPan") then
                local addType = "Base.RoastingPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnWokPan") then
                local addType = "SapphCooking.WokPan"
                local item = player:getInventory():AddItem(addType);
            end
            if item:getTags():contains("returnBowl") then
                local addType = "Base.Bowl"
                local item = player:getInventory():AddItem(addType);
            end
        end
    end
    result:setBaseHunger(hungerSum/3);
    result:setHungChange(hungerChangeSum/3);
    result:setThirstChange(thirstSum/3);
    result:setBoredomChange(boredomSum/3);
    result:setUnhappyChange(unhappySum/3);
    result:setCarbohydrates(carbsSum/3);
    result:setLipids(lipidsSum/3);
    result:setProteins(proteinsSum/3);
    result:setCalories(caloriesSum/3);
    result:setCustomWeight(true);
    result:setWeight(weightSum/3);
    result:setActualWeight(weightSum/3);
    result:setAge(0);
end


--for recipes that require a stock pan when creating a prep.
function Recipe.OnCreate.PrepStock(items, result, player)
    for i=0,items:size() - 1 do
        local item = items:get(i)
        if item:getType() == "SaucepanwithStock" or item:getType() == "PotofVegetableStock" then
            result:setBaseHunger(item:getBaseHunger());
            result:setHungChange(item:getHungChange() - 0.05);
            result:setThirstChange(item:getThirstChangeUnmodified());
            result:setBoredomChange(item:getBoredomChangeUnmodified());
            result:setUnhappyChange(item:getUnhappyChangeUnmodified());
            result:setCarbohydrates(item:getCarbohydrates());
            result:setLipids(item:getLipids());
            result:setProteins(item:getProteins());
            result:setCalories(item:getCalories());
        end
    end
end


