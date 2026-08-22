require 'Items/ProceduralDistributions'

local function addItem(distribution, item, chance)
    table.insert(ProceduralDistributions["list"][distribution].items, item)
    table.insert(ProceduralDistributions["list"][distribution].items, chance)
end

local itemsToAdd = {

    BigSalt = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"CrateCereal", 20},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigSalt2 = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigPepper = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"CrateCereal", 20},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigPepper2 = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigSoysauce = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"CrateCereal", 20},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigSugar = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigSugar2 = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigSugarBrown = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigSugarBrown2 = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigYeast = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
    BigJarLid = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
   Big_BoxofVinegars = {
        {"BreakRoomCounter", 5},
        {"KitchenBreakfast", 5},
        {"BreakRoomCounter", 5},
        {"KitchenDryFood", 5},
        {"GigamartDryGoods", 5},
    },
}

for item, distributions in pairs(itemsToAdd) do
    for _, dist in ipairs(distributions) do
        addItem(dist[1], item, dist[2])
    end
end
