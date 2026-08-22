MoreBrews = MoreBrews or {};

local sVars = SandboxVars.MoreBrews;
sVars.TotalAmount = sVars.TotalAmount or 3;

Events.OnGameStart.Add(function()
    local useDeltaCarboy = 0.05
    local useDeltaKeg = 0.04

    if sVars.TotalAmount == 1 then
        useDeltaCarboy = 0.1;
        useDeltaKeg = 0.06667;
    elseif sVars.TotalAmount == 2 then
        useDeltaCarboy = 0.06667;
        useDeltaKeg = 0.05;
    elseif sVars.TotalAmount == 4 then
        useDeltaCarboy = 0.04;
        useDeltaKeg = 0.03333;
    elseif sVars.TotalAmount == 5 then
        useDeltaCarboy = 0.03333;
        useDeltaKeg = 0.0286;    
    end

    local changeDeltaKeg = function(food)
        if sVars.TotalAmount ~= 3 then
            local item = ScriptManager.instance:getItem(food)
            if item then
                item:DoParam("UseDelta = " .. useDeltaKeg)
            end
        else
            return
        end
    end

    local kegDelta = {
        "MoreBrews.KegAmericanLager",
        "MoreBrews.KegAPA1",
        "MoreBrews.KegAPA2",
        "MoreBrews.KegIPA1",
        "MoreBrews.KegIPA2",
        "MoreBrews.KegLightLager",
        "MoreBrews.KegPilsner",
        "MoreBrews.KegPorter",
        "MoreBrews.KegStout",
        "MoreBrews.KegSkunked"
    }

    for _, food in ipairs(kegDelta) do
        changeDeltaKeg(food)
    end

    local changeDeltaCarboy = function(food)
        if sVars.TotalAmount ~= 3 then
            local item = ScriptManager.instance:getItem(food)
            if item then
                item:DoParam("UseDelta = " .. useDeltaCarboy)
            end
        else
            return
        end
    end

    local carboyDelta = {
        "MoreBrews.CarboyAmericanLagerTapped",
        "MoreBrews.CarboyAPA1DryHoppedTapped",
        "MoreBrews.CarboyAPA2DryHoppedTapped",
        "MoreBrews.CarboyIPA1DryHoppedTapped",
        "MoreBrews.CarboyIPA2DryHoppedTapped",
        "MoreBrews.CarboyLightLagerTapped",
        "MoreBrews.CarboyPilsnerTapped",
        "MoreBrews.CarboyPorterTapped",
        "MoreBrews.CarboyStoutTapped",
        "MoreBrews.CarboySkunkedDarkTapped",
        "MoreBrews.CarboySkunkedLightTapped"
    }

    for _, food in ipairs(carboyDelta) do
        changeDeltaCarboy(food)
    end

end);


sVars.FermentChange = sVars.FermentChange or 10;
sVars.RottenChange = sVars.RottenChange or 10;

Events.OnGameStart.Add(function()
    local rotSpeed = 1

    if SandboxVars.FoodRotSpeed == 1 then
        rotSpeed = 1.7
    elseif SandboxVars.FoodRotSpeed == 2 then
        rotSpeed = 1.4
    elseif SandboxVars.FoodRotSpeed == 4 then
        rotSpeed = 0.7
    elseif SandboxVars.FoodRotSpeed == 5 then
        rotSpeed = 0.4
    end

    -- Define item names and their corresponding fermenting/rotting values
    local itemDataFerment = {
        { name = "CarboyFermentingAmericanLager", ferment = 0.7 * sVars.FermentChange },
        { name = "CarboyFermentingAPA1", ferment = 0.7 * sVars.FermentChange },
        { name = "CarboyFermentingAPA1DryHopped", ferment = 0.4 * sVars.FermentChange },
        { name = "CarboyFermentingAPA2", ferment = 0.7 * sVars.FermentChange },
        { name = "CarboyFermentingAPA2DryHopped", ferment = 0.4 * sVars.FermentChange },
        { name = "CarboyFermentingIPA1", ferment = 0.7 * sVars.FermentChange },
        { name = "CarboyFermentingIPA1DryHopped", ferment = 0.4 * sVars.FermentChange },
        { name = "CarboyFermentingIPA2", ferment = 0.7 * sVars.FermentChange },
        { name = "CarboyFermentingIPA2DryHopped", ferment = 0.5 * sVars.FermentChange },
        { name = "CarboyFermentingLightLager", ferment = 0.5 * sVars.FermentChange },
        { name = "CarboyFermentingPilsner", ferment = 0.5 * sVars.FermentChange },
        { name = "CarboyFermentingPorter", ferment = 0.7 * sVars.FermentChange },
        { name = "CarboyFermentingStout", ferment = 0.7 * sVars.FermentChange },
    }

    for _, data in ipairs(itemDataFerment) do
        local item = ScriptManager.instance:getItem("MoreBrews." .. data.name)
        if item then
            item:DoParam("DaysTotallyRotten = " .. math.ceil(data.ferment * rotSpeed))
            item:DoParam("DaysFresh = " .. math.ceil(data.ferment * rotSpeed))
        end
    end

    local itemDataRotten = {
        { name = "CarboyAmericanLager", rotten = 1.8 * sVars.RottenChange },
        { name = "CarboyAPA1", rotten = 1.8 * sVars.RottenChange },
        { name = "CarboyAPA1DryHopped", rotten = 2.2 * sVars.RottenChange },
        { name = "CarboyAPA2", rotten = 1.4 * sVars.RottenChange },
        { name = "CarboyAPA2DryHopped", rotten = 1.8 * sVars.RottenChange },
        { name = "CarboyIPA1", rotten = 1.4 * sVars.RottenChange },
        { name = "CarboyIPA1DryHopped", rotten = 1.8 * sVars.RottenChange },
        { name = "CarboyIPA2", rotten = 1.8 * sVars.RottenChange },
        { name = "CarboyIPA2DryHopped", rotten = 2.2 * sVars.RottenChange },
        { name = "CarboyLightLager", rotten = 1.4 * sVars.RottenChange },
        { name = "CarboyPilsner",  rotten = 1.4 * sVars.RottenChange },
        { name = "CarboyPorter", rotten = 1.8 * sVars.RottenChange },
        { name = "CarboyStout", rotten = 1.8 * sVars.RottenChange }
    }

    for _, data in ipairs(itemDataRotten) do
        local item = ScriptManager.instance:getItem("MoreBrews." .. data.name)
        if item then
            item:DoParam("DaysTotallyRotten = " .. math.ceil(data.rotten * rotSpeed))
            item:DoParam("DaysFresh = " .. math.ceil(data.rotten * rotSpeed))
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
        { food = "MoreBrews.BeerBottleAmericanLager", calories = 145 },
        { food = "MoreBrews.BeerBottleAPA1", calories = 185 },
        { food = "MoreBrews.BeerBottleAPA2", calories = 170 },
        { food = "MoreBrews.BeerBottleIPA1", calories = 220 },
        { food = "MoreBrews.BeerBottleIPA2", calories = 240 },
        { food = "MoreBrews.BeerBottleLightLager", calories = 145 },
        { food = "MoreBrews.BeerBottlePilsner", calories = 165 },
        { food = "MoreBrews.BeerBottlePorter", calories = 155 },
        { food = "MoreBrews.BeerBottleStout", calories = 200 },
        { food = "MoreBrews.BeerBottleSkunked", calories = 150 },
        { food = "MoreBrews.BeerCanAmericanLager", calories = 145 },
        { food = "MoreBrews.BeerCanAPA1", calories = 185 },
        { food = "MoreBrews.BeerCanAPA2", calories = 170 },
        { food = "MoreBrews.BeerCanIPA1", calories = 220 },
        { food = "MoreBrews.BeerCanIPA2", calories = 240 },
        { food = "MoreBrews.BeerCanLightLager", calories = 145 },
        { food = "MoreBrews.BeerCanPilsner", calories = 165 },
        { food = "MoreBrews.BeerCanPorter", calories = 155 },
        { food = "MoreBrews.BeerCanStout", calories = 200 },
        { food = "MoreBrews.BeerCanSkunked", calories = 150 },
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
            local rottenChange = math.ceil((28 * expireChange) * 0.1)
            local freshChange = math.ceil((14 * expireChange) * 0.1)
            if item then
                item:DoParam("DaysTotallyRotten = " .. rottenChange)
                item:DoParam("DaysFresh = " .. freshChange)
            end
        end
    
        local expiredFood = {
            "MoreBrews.BeerBottleAmericanLager",
            "MoreBrews.BeerBottleAPA1",
            "MoreBrews.BeerBottleAPA2",
            "MoreBrews.BeerBottleIPA1",
            "MoreBrews.BeerBottleIPA2",
            "MoreBrews.BeerBottleLightLager",
            "MoreBrews.BeerBottlePilsner",
            "MoreBrews.BeerBottlePorter",
            "MoreBrews.BeerBottleStout",
            "MoreBrews.BeerBottleSkunked",
            "MoreBrews.BeerCanAmericanLager",
            "MoreBrews.BeerCanAPA1",
            "MoreBrews.BeerCanAPA2",
            "MoreBrews.BeerCanIPA1",
            "MoreBrews.BeerCanIPA2",
            "MoreBrews.BeerCanLightLager",
            "MoreBrews.BeerCanPilsner",
            "MoreBrews.BeerCanPorter",
            "MoreBrews.BeerCanStout",
            "MoreBrews.BeerCanSkunked",
        }
    
        for _, food in ipairs(expiredFood) do
            makeExpire(food)
        end
    end
end);