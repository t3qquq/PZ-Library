require 'XPSystem_SkillBook'

MoreBrewsWinexpUpdate = {};

-- If you start with Vintner trait it stays no matter the level. If you don't you gain it at level 6
MoreBrewsWinexpUpdate.levelPerk = function(owner, perk, level)
    if perk == Perks.WineMaking then
        if level >= 6 and level <= 10 then
            if not owner:HasTrait("Vintner") then
                owner:getTraits():add("Vintner");
            end
        else
            if owner:HasTrait("Vintner") then
                owner:getTraits():remove("Vintner");
            end
        end
    end
end

-- get xp when you craft something
MoreBrewsWinexpUpdate.onMakeItem = function(item, result, recipe)
	if item:getType():contains("Wine") then
		getPlayer():getXp():AddXP(Perks.WineMaking, 5);
	end
end

-- Add Wine Making Skill books to the game if not using the MoreBrews beer branch

	SkillBook["WineMaking"] = {};
	SkillBook["WineMaking"].perk = Perks.WineMaking;
	SkillBook["WineMaking"].maxMultiplier1 = 3;
	SkillBook["WineMaking"].maxMultiplier2 = 5;
	SkillBook["WineMaking"].maxMultiplier3 = 8;
	SkillBook["WineMaking"].maxMultiplier4 = 12;
	SkillBook["WineMaking"].maxMultiplier5 = 16;


Events.LevelPerk.Add(MoreBrewsWinexpUpdate.levelPerk);
Events.OnMakeItem.Add(MoreBrewsWinexpUpdate.onMakeItem);
