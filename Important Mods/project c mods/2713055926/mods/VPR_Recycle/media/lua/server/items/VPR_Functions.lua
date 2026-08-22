--require "recipecode";

function Recipe.OnGiveXP.MetalWelding01(recipe, ingredients, result, player)
	player:getXp():AddXP(Perks.MetalWelding, 1);
	player:getXp():AddXP(Perks.Strength, 4);
end

function Recipe.OnGiveXP.MetalWelding03(recipe, ingredients, result, player)
	player:getXp():AddXP(Perks.MetalWelding, 3);
	player:getXp():AddXP(Perks.Strength, 8);
end

function Recipe.OnGiveXP.MetalWelding05(recipe, ingredients, result, player)
	player:getXp():AddXP(Perks.MetalWelding, 5);
	player:getXp():AddXP(Perks.Strength, 12);
end

function Recipe.OnGiveXP.MechWeld05(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Mechanics, 3);
    player:getXp():AddXP(Perks.MetalWelding, 5);	
end

function Recipe.OnGiveXP.Electricity03(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Electricity, 3);
end

function Recipe.OnGiveXP.Tailoring01(recipe, ingredients, result, player)
	player:getXp():AddXP(Perks.Tailoring, 1);
end

-- keep favs when dismantling
function Recipe.OnTest.DismantleFavs(item)
    if not item  then return end
    if item:getType() == "WeldingMask" or item:getType() == "BlowTorch" or item:isBroken() then
       return true
    end
    return not item:isFavorite() and not item:isEquipped()
end



--change parameter in item
local item = ScriptManager.instance:getItem("Base.Lighter")    
if item then 
    item:DoParam("ticksPerEquipUse = 250")
	item:DoParam("ReplaceOnDeplete = EmptyLighter")
end


--breaking items for strenght
function Recipe.OnGiveXP.BreakStr(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Strength, 4);
end


--recycle metal items
function Recipe.OnCreate.RecycleToScrapMetal(items, result, player)
    local weight = 0
    for i = 0, items:size()-1 do
        local item = items:get(i)
        if item and item:getType() ~= "WeldingMask" and item:getType() ~= "BlowTorch" then
            weight = weight + item:getWeight()
        end
    end

    local scrapQuantity = math.floor(weight*10*0.5)
    if scrapQuantity < 1 then 
		player:getXp():AddXP(Perks.Strength, 4)
    else
        player:getInventory():AddItems("Base.ScrapMetal", scrapQuantity)
        player:getXp():AddXP(Perks.MetalWelding, scrapQuantity*2)
		player:getXp():AddXP(Perks.Strength, scrapQuantity*4)
    end
end


--recycle vehicle items
function Recipe.OnCreate.RecycleToScrapMetalVHC(items, result, player)
    local weight = 0
    for i = 0, items:size()-1 do
        local item = items:get(i)
        if item and item:getType() ~= "WeldingMask" and item:getType() ~= "BlowTorch" then
            weight = weight + item:getWeight()
        end
    end

    local scrapQuantity = math.floor(weight*10*0.5)

        player:getInventory():AddItems("Base.ScrapMetal", scrapQuantity*0.6)
        player:getXp():AddXP(Perks.MetalWelding, scrapQuantity*4)
		player:getXp():AddXP(Perks.Mechanics, scrapQuantity*2)
		player:getXp():AddXP(Perks.Strength, scrapQuantity*4)
end


--glass creation chance
function Recipe.OnCreate.TryMakeGlass(items, result, player)
    local skillChance = player:getPerkLevel(Perks.MetalWelding)
    local finalChance = 1 + skillChance

    if ZombRand(0,9) < finalChance then
		player:getInventory():AddItems("GlassPane",1)
		player:getXp():AddXP(Perks.Strength,4)
		else
		player:getInventory():AddItems("GlassShard",4)
		player:getXp():AddXP(Perks.Strength,4)
    end
end

-- Scrapping Accesories and Watches (many thanks to JS)--

local Recipe = Recipe
 
local accessoryTypes = {
    "Bracelet_BangleLeftGold",
    "Bracelet_BangleRightGold",
    "Bracelet_ChainLeftGold",
    "Bracelet_ChainRightGold",
    "Bracelet_BangleLeftSilver",
    "Bracelet_BangleRightSilver",
    "Bracelet_ChainLeftSilver",
    "Bracelet_ChainRightSilver",
    "BellyButton_DangleGold",
    "BellyButton_DangleGoldRuby",
    "BellyButton_DangleSilver",
    "BellyButton_DangleSilverDiamond",
    "BellyButton_RingGold",
    "BellyButton_RingGoldDiamond",
    "BellyButton_RingGoldRuby",
    "BellyButton_RingSilver",
    "BellyButton_RingSilverAmethyst",
    "BellyButton_RingSilverDiamond",
    "BellyButton_RingSilverRuby",
    "BellyButton_StudGold",
    "BellyButton_StudGoldDiamond",
    "BellyButton_StudSilver",
    "BellyButton_StudSilverDiamond",
    "Earring_Dangly_Diamond",
    "Earring_Dangly_Emerald",
    "Earring_Dangly_Pearl",
    "Earring_Dangly_Ruby",
    "Earring_Dangly_Sapphire",
    "Earring_LoopLrg_Gold",
    "Earring_LoopLrg_Silver",
    "Earring_LoopMed_Gold",
    "Earring_LoopMed_Silver",
    "Earring_LoopSmall_Gold_Both",
    "Earring_LoopSmall_Gold_Top",
    "Earring_LoopSmall_Silver_Both",
    "Earring_LoopSmall_Silver_Top",
    "Earring_Pearl",
    "Earring_Stone_Emerald",
    "Earring_Stone_Ruby",
    "Earring_Stone_Sapphire",
    "Earring_Stud_Gold",
    "Earring_Stud_Silver",
    "NoseRing_Gold",
    "NoseRing_Silver",
    "NoseStud_Gold",
    "NoseStud_Silver",
    "Necklace_Crucifix",
    "Necklace_DogTag",
    "Necklace_Gold",
    "Necklace_GoldDiamond",
    "Necklace_GoldRuby",
    "NecklaceLong_Amber",
    "NecklaceLong_Gold",
    "NecklaceLong_GoldDiamond",
    "NecklaceLong_Silver",
    "NecklaceLong_SilverDiamond",
    "NecklaceLong_SilverEmerald",
    "NecklaceLong_SilverSapphire",
    "Necklace_Pearl",
    "Necklace_Silver",
    "Necklace_SilverCrucifix",
    "Necklace_SilverDiamond",
    "Necklace_SilverSapphire",
    "Necklace_YingYang",
    "Ring_Left_MiddleFinger_Gold",
    "Ring_Left_MiddleFinger_GoldDiamond",
    "Ring_Left_MiddleFinger_GoldRuby",
    "Ring_Left_MiddleFinger_Silver",
    "Ring_Left_MiddleFinger_SilverDiamond",
    "Ring_Left_RingFinger_Gold",
    "Ring_Left_RingFinger_GoldDiamond",
    "Ring_Left_RingFinger_GoldRuby",
    "Ring_Left_RingFinger_Silver",
    "Ring_Left_RingFinger_SilverDiamond",
    "Ring_Right_MiddleFinger_Gold",
    "Ring_Right_MiddleFinger_GoldDiamond",
    "Ring_Right_MiddleFinger_GoldRuby",
    "Ring_Right_MiddleFinger_Silver",
    "Ring_Right_MiddleFinger_SilverDiamond",
    "Ring_Right_RingFinger_Gold",
    "Ring_Right_RingFinger_GoldDiamond",
    "Ring_Right_RingFinger_GoldRuby",
    "Ring_Right_RingFinger_Silver",
    "Ring_Right_RingFinger_SilverDiamond",
}
 
local watchTypes = {
    "WristWatch_Left_ClassicBlack",
    "WristWatch_Right_ClassicBlack",
    "WristWatch_Left_ClassicBrown",
    "WristWatch_Right_ClassicBrown",
    "WristWatch_Left_ClassicGold",
    "WristWatch_Right_ClassicGold",
    "WristWatch_Left_ClassicMilitary",
    "WristWatch_Right_ClassicMilitary",
}
 
local function getCraftableItemsFromTypes(inventory, types, maxCount)
    local result = {}
    local count = 0
    if not maxCount then maxCount = 0 end
    for _, type in ipairs(types) do
        local itemList = inventory:getItemsFromType(type, true)
        for i = itemList:size() - 1, 0, -1 do
            local item = itemList:get(i)
            if item and not item:isFavorite() and not item:isEquipped() then
                table.insert(result, item)
                count = count + 1
                if maxCount > 0 and count >= maxCount then
                    return result
                end
            end
        end
    end
    return result
end
 
local function removeInventoryItems(items)
    for _ , item in ipairs(items) do
        local container = item:getContainer()
        if container then
            container:Remove(item)
        end
    end
end
 
function Recipe.OnCanPerform.AnyAccessory3(recipe, player, selectedItem)
    local craftableItems = getCraftableItemsFromTypes(player:getInventory(), accessoryTypes, 3)
    return #craftableItems == 3
end
 
function Recipe.OnCanPerform.AnyWristWatch3(recipe, player, selectedItem)
    local craftableItems = getCraftableItemsFromTypes(player:getInventory(), watchTypes, 3)
    return #craftableItems == 3
end
 
function Recipe.OnCreate.ScrapMetalFromAnyAccessory3(items, result, player, selectedItem)
    local inventory = player:getInventory()
    local craftableItems = getCraftableItemsFromTypes(inventory, accessoryTypes, 3)
    if #craftableItems == 3 then
        removeInventoryItems(craftableItems)
        inventory:AddItem("Base.ScrapMetal")
        return true
    end
    return false
end
 
function Recipe.OnCreate.ScrapMetalFromAnyWristWatch3(items, result, player, selectedItem)
    local inventory = player:getInventory()
    local craftableItems = getCraftableItemsFromTypes(inventory, watchTypes, 3)
    if #craftableItems == 3 then
        removeInventoryItems(craftableItems)
        inventory:AddItem("Base.ScrapMetal")
        return true
    end
    return false
end
 