function Recipe.OnGiveXP.Tailoring3(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Tailoring, 3);
end

function Recipe.OnGiveXP.Tailoring5(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Tailoring, 5);
end

function Recipe.OnGiveXP.Tailoring10(recipe, ingredients, result, player)
    player:getXp():AddXP(Perks.Tailoring, 10);
end

-- These functions are defined to avoid breaking mods.
Give3TailoringXP = Recipe.OnGiveXP.Tailoring3
Give5TailoringXP = Recipe.OnGiveXP.Tailoring5
Give10TailoringXP = Recipe.OnGiveXP.Tailoring10
