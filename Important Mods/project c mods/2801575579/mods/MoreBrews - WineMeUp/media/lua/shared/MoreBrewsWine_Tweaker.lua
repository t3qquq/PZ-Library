MoreBrews = MoreBrews or {};

local sVars = SandboxVars.MoreBrewsWineMeUp;
sVars.TotalAmount = sVars.TotalAmount or 3;

Events.OnGameStart.Add(function()
    local useDeltaSmallBarrel = 0.2
    local useDeltaLargeBarrel = 0.05

    if sVars.TotalAmount == 1 then
        useDeltaSmallBarrel = 0.3333;
        useDeltaLargeBarrel = 0.0833;
    elseif sVars.TotalAmount == 2 then
        useDeltaSmallBarrel = 0.25;
        useDeltaLargeBarrel = 0.0625;
    elseif sVars.TotalAmount == 4 then
        useDeltaSmallBarrel = 0.1667;
        useDeltaLargeBarrel = 0.0417;
    elseif sVars.TotalAmount == 5 then
        useDeltaSmallBarrel = 0.1429;
        useDeltaLargeBarrel = 0.0357;   
    end

    local changeDeltaSmallBarrel = function(food)
        if sVars.TotalAmount ~= 3 then
            local item = ScriptManager.instance:getItem(food)
            if item then
                item:DoParam("UseDelta = " .. useDeltaSmallBarrel)
            end
        else
            return
        end
    end

    local smallBarrelDelta = {
        "MoreBrews.BarrelDispenserSmallRedWine",
        "MoreBrews.BarrelDispenserSmallRedWineAged",
        "MoreBrews.BarrelDispenserSmallRedWineAgedFully",
        "MoreBrews.BarrelDispenserSmallWhiteWine",
        "MoreBrews.BarrelDispenserSmallWhiteWineAged",
        "MoreBrews.BarrelDispenserSmallWhiteWineAgedFully",
        "MoreBrews.BarrelDispenserSmallHoneyMead",
        "MoreBrews.BarrelDispenserSmallHardCider",
        "MoreBrews.BarrelDispenserSmallWildBerryWine",
        "MoreBrews.BarrelDispenserSmallRosehipWine",
        "MoreBrews.BarrelDispenserSmallPineappleWine",
        "MoreBrews.BarrelDispenserSmallStrawberryWine",
        "MoreBrews.BarrelDispenserSmallPeachWine",
        "MoreBrews.BarrelDispenserSmallPearWine",
        "MoreBrews.BarrelDispenserSmallBananaWine"
    }

    for _, food in ipairs(smallBarrelDelta) do
        changeDeltaSmallBarrel(food)
    end

    local changeDeltaLargeBarrel = function(food)
        if sVars.TotalAmount ~= 3 then
            local item = ScriptManager.instance:getItem(food)
            if item then
                item:DoParam("UseDelta = " .. useDeltaLargeBarrel)
            end
        else
            return
        end
    end

    local largeBarrelDelta = {
        "MoreBrews.BarrelDispenserLargeRedWine",
        "MoreBrews.BarrelDispenserLargeRedWineAged",
        "MoreBrews.BarrelDispenserLargeRedWineAgedFully",
        "MoreBrews.BarrelDispenserLargeWhiteWine",
        "MoreBrews.BarrelDispenserLargeWhiteWineAged",
        "MoreBrews.BarrelDispenserLargeWhiteWineAgedFully",
        "MoreBrews.BarrelDispenserLargeHoneyMead",
        "MoreBrews.BarrelDispenserLargeHardCider",
        "MoreBrews.BarrelDispenserLargeWildBerryWine",
        "MoreBrews.BarrelDispenserLargeRosehipWine",
        "MoreBrews.BarrelDispenserLargePineappleWine",
        "MoreBrews.BarrelDispenserLargeStrawberryWine",
        "MoreBrews.BarrelDispenserLargePeachWine",
        "MoreBrews.BarrelDispenserLargePearWine",
        "MoreBrews.BarrelDispenserLargeBananaWine"
    }

    for _, food in ipairs(largeBarrelDelta) do
        changeDeltaLargeBarrel(food)
    end

end);


sVars.FermentChange = sVars.FermentChange or 10;

Events.OnGameStart.Add(function()
    local rotSpeed = 1;

    if SandboxVars.FoodRotSpeed == 1 then -- very fast
        rotSpeed = 1.7;
    elseif SandboxVars.FoodRotSpeed == 2 then -- fast
        rotSpeed = 1.4;
    elseif SandboxVars.FoodRotSpeed == 4 then -- slow
        rotSpeed = 0.7;
    elseif SandboxVars.FoodRotSpeed == 5 then -- very slow
        rotSpeed = 0.4;    
    end
    --this is to keep the fermenting time close to the intended length of days no matter the in game settings selected
    local itemDataFerment = {
        { name = "GallonCarboyFermentingRedWine", ferment = 1.2 * sVars.FermentChange},
        { name = "GallonCarboyFermentingWhiteWine", ferment = 1.0 * sVars.FermentChange},
        { name = "GallonCarboyFermentingWildBerryWine", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingRosehipWine", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingPineappleWine", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingStrawberryWine", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingPeachWine", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingPearWine", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingBananaWine", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingHoneyMead", ferment = 0.7 * sVars.FermentChange},
        { name = "GallonCarboyFermentingHardCider", ferment = 0.7 * sVars.FermentChange},
        { name = "CarboyFermentingRedWine", ferment = 1.2 * sVars.FermentChange},
        { name = "CarboyFermentingWhiteWine", ferment = 1.0 * sVars.FermentChange},
        { name = "BarrelRedWineAging", ferment = 1.0 * sVars.FermentChange},
        { name = "BarrelRedWineAgingFully", ferment = 1.6 * sVars.FermentChange},
        { name = "BarrelWhiteWineAging", ferment = 0.8 * sVars.FermentChange},
        { name = "BarrelWhiteWineAgingFully", ferment = 1.2 * sVars.FermentChange},
        { name = "BarrelRedWineAgingSmall", ferment = 1.0 * sVars.FermentChange},
        { name = "BarrelRedWineAgingFullySmall", ferment = 1.6 * sVars.FermentChange},
        { name = "BarrelWhiteWineAgingSmall", ferment = 0.8 * sVars.FermentChange},
        { name = "BarrelWhiteWineAgingFullySmall", ferment = 1.2 * sVars.FermentChange},
        { name = "FruitYeastStarter", ferment = 1.0 * sVars.FermentChange},
        { name = "PotatoYeastStarter", ferment = 1.0 * sVars.FermentChange},
        { name = "WildPlantsYeastStarter", ferment = 1.0 * sVars.FermentChange},
    }

    for _, data in ipairs(itemDataFerment) do
        local item = ScriptManager.instance:getItem("MoreBrews." .. data.name)
        if item then
            item:DoParam("DaysTotallyRotten = " .. math.ceil(data.ferment * rotSpeed))
            item:DoParam("DaysFresh = " .. math.ceil(data.ferment * rotSpeed))
        end
    end
end);

sVars.CalorieChange = sVars.CalorieChange or 10;
sVars.Expired = sVars.Expired or false;
sVars.ExpireChange = sVars.ExpireChange or 10;

Events.OnGameStart.Add(function()

    local setCalories = function(food, cals)
        local item = ScriptManager.instance:getItem(food)
        local calMulti = (sVars.CalorieChange or 10) * 0.1;
        if item then
            item:DoParam("Calories = " .. math.ceil(cals * calMulti))
        end
    end

    local foodItems = {
        { food = "MoreBrews.RedWine", calories = 560},
        { food = "MoreBrews.RedWineAged", calories = 620},
        { food = "MoreBrews.RedWineAgedFully", calories = 680},
        { food = "MoreBrews.WineBoxSmall1", calories = 310},
        { food = "MoreBrews.WineBoxSmall2", calories = 310},
        { food = "MoreBrews.WineBoxSmall3", calories = 340},
        { food = "MoreBrews.WhiteWine", calories = 510},
        { food = "MoreBrews.WhiteWineAged", calories = 540},
        { food = "MoreBrews.WhiteWineAgedFully", calories = 580},
        { food = "MoreBrews.WineBoxSmall4", calories = 250},
        { food = "MoreBrews.WineBoxSmall5", calories = 270},
        { food = "MoreBrews.WineBoxSmall6", calories = 290},
        { food = "MoreBrews.WildBerryWine", calories = 250},
        { food = "MoreBrews.PineappleWine", calories = 320},
        { food = "MoreBrews.StrawberryWine", calories = 280},
        { food = "MoreBrews.PeachWine", calories = 220},
        { food = "MoreBrews.PearWine", calories = 220},
        { food = "MoreBrews.BananaWine", calories = 250},
        { food = "MoreBrews.HoneyMead", calories = 420},
        { food = "MoreBrews.HardCider", calories = 300},
    }

    for _, itemInfo in ipairs(foodItems) do
        setCalories(itemInfo.food, itemInfo.calories)
    end

    if sVars.Expired then
        local makeExpire = function(food)
            local item = ScriptManager.instance:getItem(food)
            local expireChange = sVars.ExpireChange or 10
            print("sVars.ExpireChange: " .. tostring(sVars.ExpireChange))
            print("expireChange: " .. expireChange)
            local rottenChange = math.ceil((42 * expireChange) * 0.1)
            local freshChange = math.ceil((21 * expireChange) * 0.1)
            if item then
                item:DoParam("DaysTotallyRotten = " .. rottenChange)
                item:DoParam("DaysFresh = " .. freshChange)
            end
        end
    
        local expiredFood = {
            "MoreBrews.RedWine",
            "MoreBrews.RedWineAged",
            "MoreBrews.RedWineAgedFully",
            "MoreBrews.WineBoxSmall1",
            "MoreBrews.WineBoxSmall2",
            "MoreBrews.WineBoxSmall3",
            "MoreBrews.WhiteWine",
            "MoreBrews.WhiteWineAged",
            "MoreBrews.WhiteWineAgedFully",
            "MoreBrews.WineBoxSmall4",
            "MoreBrews.WineBoxSmall5",
            "MoreBrews.WineBoxSmall6",
            "MoreBrews.WildBerryWine",
            "MoreBrews.PineappleWine",
            "MoreBrews.StrawberryWine",
            "MoreBrews.PeachWine",
            "MoreBrews.PearWine",
            "MoreBrews.BananaWine",
            "MoreBrews.HoneyMead",
            "MoreBrews.HardCider",
        }
    
        for _, food in ipairs(expiredFood) do
            makeExpire(food)
        end
    end
end);