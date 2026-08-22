require('NPCs/MainCreationMethods');

local function initMoreBrewsWineRecipes()

	local  Vintner = TraitFactory.addTrait("Vintner", getText("UI_trait_vintner"), 2, getText("UI_trait_vintnerinfo"), false, false);
	Vintner:addXPBoost(Perks.WineMaking, 2);
    Vintner:getFreeRecipes():add("Create Rosehip Wine Base");
	Vintner:getFreeRecipes():add("Create Pineapple Wine Base");
	Vintner:getFreeRecipes():add("Create Strawberry Wine Base");
	Vintner:getFreeRecipes():add("Create Peach Wine Base");
	Vintner:getFreeRecipes():add("Create Pear Wine Base");
	Vintner:getFreeRecipes():add("Create Banana Wine Base");
	Vintner:getFreeRecipes():add("Create Honey Mead Base");
	Vintner:getFreeRecipes():add("Create Hard Cider Base");
	Vintner:getFreeRecipes():add("Prep Ferment Rosehip Wine");
	Vintner:getFreeRecipes():add("Prep Ferment Pineapple Wine");
	Vintner:getFreeRecipes():add("Prep Ferment Strawberry Wine");
	Vintner:getFreeRecipes():add("Prep Ferment Peach Wine");
	Vintner:getFreeRecipes():add("Prep Ferment Pear Wine");
	Vintner:getFreeRecipes():add("Prep Ferment Banana Wine");
	Vintner:getFreeRecipes():add("Prep Ferment Honey Mead");
	Vintner:getFreeRecipes():add("Prep Ferment Hard Cider");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Rosehip Wine");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Pineapple Wine");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Strawberry Wine");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Peach Wine");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Pear Wine");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Banana Wine");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Honey Mead");
	Vintner:getFreeRecipes():add("Fill Gallon Barrel Hard Cider");
	Vintner:getFreeRecipes():add("Fill Bottle Red Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Red Wine Aged");
	Vintner:getFreeRecipes():add("Fill Bottle Red Wine Aged Fully");
	Vintner:getFreeRecipes():add("Fill Bottle White Wine");
	Vintner:getFreeRecipes():add("Fill Bottle White Wine Aged");
	Vintner:getFreeRecipes():add("Fill Bottle White Wine Aged Fully");
	Vintner:getFreeRecipes():add("Fill Bottle Wild Berry Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Rosehip Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Pineapple Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Strawberry Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Peach Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Pear Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Banana Wine");
	Vintner:getFreeRecipes():add("Fill Bottle Honey Mead");
	Vintner:getFreeRecipes():add("Fill Bottle Hard Cider");
	Vintner:getFreeRecipes():add("Fill Small Box Red Wine");
	Vintner:getFreeRecipes():add("Fill Small Box Red Wine Aged");
	Vintner:getFreeRecipes():add("Fill Small Box Red Wine Aged Fully");
	Vintner:getFreeRecipes():add("Fill Small Box White Wine");
	Vintner:getFreeRecipes():add("Fill Small Box White Wine Aged");
	Vintner:getFreeRecipes():add("Fill Small Box White Wine Aged Fully");
end

local function MoreBrewsWineStarterKit(char, square)
	if SandboxVars.StarterKit then
		local bag = char:getInventory():FindAndReturn("Base.Bag_Schoolbag");
		if not bag then
			bag = char:getInventory():AddItem("Base.Bag_Schoolbag");
		end
		if char:HasTrait("Vintner") and char:HasTrait("Brewer") then
			bag:getItemContainer():AddItem("MoreBrews.WineBottleRed");
			bag:getItemContainer():AddItem("MoreBrews.WineKit");
			bag:getItemContainer():AddItem("MoreBrews.BrewingKit");
		elseif char:HasTrait("Vintner") and not char:HasTrait("Brewer") then
			bag:getItemContainer():AddItem("MoreBrews.WineBottleRed");
			bag:getItemContainer():AddItem("MoreBrews.WineBottleWhite");
			bag:getItemContainer():AddItem("MoreBrews.WineKit");
		end
	end
	return
end

Events.OnGameBoot.Add(initMoreBrewsWineRecipes);
Events.OnNewGame.Add(MoreBrewsWineStarterKit);