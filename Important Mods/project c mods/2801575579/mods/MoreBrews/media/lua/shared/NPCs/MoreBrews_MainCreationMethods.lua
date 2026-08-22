require('NPCs/MainCreationMethods');

local function initMoreBrewsRecipes()

	local  Brewer = TraitFactory.addTrait("Brewer", getText("UI_trait_brewer"), 2, getText("UI_trait_brewerinfo"), false, false);
	Brewer:addXPBoost(Perks.Brewing, 2);
    Brewer:getFreeRecipes():add("Create American Lager Wort");
	Brewer:getFreeRecipes():add("Create APA1 Wort");
	Brewer:getFreeRecipes():add("Create APA2 Wort");
    Brewer:getFreeRecipes():add("Create IPA1 Wort");
	Brewer:getFreeRecipes():add("Create IPA2 Wort");
	Brewer:getFreeRecipes():add("Create Light Lager Wort");
    Brewer:getFreeRecipes():add("Create Pilsner Wort");
	Brewer:getFreeRecipes():add("Create Porter Wort");
	Brewer:getFreeRecipes():add("Create Stout Wort");
    Brewer:getFreeRecipes():add("Prep to Ferment American Lager Beer");
	Brewer:getFreeRecipes():add("Prep to Ferment Dark American Pale Ale Beer");
	Brewer:getFreeRecipes():add("Prep to Ferment Light American Pale Ale Beer");
    Brewer:getFreeRecipes():add("Prep to Ferment Light India Pale Ale Beer");
	Brewer:getFreeRecipes():add("Prep to Ferment Dark India Pale Ale Beer");
	Brewer:getFreeRecipes():add("Prep to Ferment Light Lager Beer");
    Brewer:getFreeRecipes():add("Prep to Ferment Pilsner Beer");
	Brewer:getFreeRecipes():add("Prep to Ferment Porter Beer");
	Brewer:getFreeRecipes():add("Prep to Ferment Stout Beer");
    Brewer:getFreeRecipes():add("Dry Hop for Secondary Fermenting Dark American Pale Ale Beer");
	Brewer:getFreeRecipes():add("Dry Hop for Secondary Fermenting Light American Pale Ale Beer");
    Brewer:getFreeRecipes():add("Dry Hop for Secondary Fermenting Light India Pale Ale Beer");
	Brewer:getFreeRecipes():add("Dry Hop for Secondary Fermenting Dark India Pale Ale Beer");
    Brewer:getFreeRecipes():add("Fill American Lager Keg");
	Brewer:getFreeRecipes():add("Fill APA1 Keg");
	Brewer:getFreeRecipes():add("Fill APA2 Keg");
    Brewer:getFreeRecipes():add("Fill IPA1 Keg");
	Brewer:getFreeRecipes():add("Fill IPA2 Keg");
	Brewer:getFreeRecipes():add("Fill Light Lager Keg");
    Brewer:getFreeRecipes():add("Fill Pilsner Keg");
	Brewer:getFreeRecipes():add("Fill Porter Keg");
	Brewer:getFreeRecipes():add("Fill Stout Keg");
    Brewer:getFreeRecipes():add("Fill Bottle with American Lager");
	Brewer:getFreeRecipes():add("Fill Bottle with APA 1");
	Brewer:getFreeRecipes():add("Fill Bottle with APA 2");
    Brewer:getFreeRecipes():add("Fill Bottle with IPA 1");
	Brewer:getFreeRecipes():add("Fill Bottle with IPA 2");
	Brewer:getFreeRecipes():add("Fill Bottle with Light Lager");
    Brewer:getFreeRecipes():add("Fill Bottle with Pilsner");
	Brewer:getFreeRecipes():add("Fill Bottle with Porter");
	Brewer:getFreeRecipes():add("Fill Bottle with Stout");
    Brewer:getFreeRecipes():add("Fill Bottle with Skunked Beer");
    Brewer:getFreeRecipes():add("Fill Can with American Lager");
	Brewer:getFreeRecipes():add("Fill Can with APA 1");
	Brewer:getFreeRecipes():add("Fill Can with APA 2");
    Brewer:getFreeRecipes():add("Fill Can with IPA 1");
	Brewer:getFreeRecipes():add("Fill Can with IPA 2");
	Brewer:getFreeRecipes():add("Fill Can with Light Lager");
    Brewer:getFreeRecipes():add("Fill Can with Pilsner");
	Brewer:getFreeRecipes():add("Fill Can with Porter");
	Brewer:getFreeRecipes():add("Fill Can with Stout");
    Brewer:getFreeRecipes():add("Fill Can with Skunked Beer");
end

local function MoreBrewsStarterKit(char, square)
	if SandboxVars.StarterKit then
		local bag = char:getInventory():FindAndReturn("Base.Bag_Schoolbag");
		if not bag then
			bag = char:getInventory():AddItem("Base.Bag_Schoolbag");
		end
		if char:HasTrait("Brewer") then
			for i=1, 6 do
				bag:getItemContainer():AddItem("MoreBrews.BeerCanPilsner");
			end
			bag:getItemContainer():AddItem("MoreBrews.BrewingKit");
		end
	end
	return
end

Events.OnGameBoot.Add(initMoreBrewsRecipes);
Events.OnNewGame.Add(MoreBrewsStarterKit);