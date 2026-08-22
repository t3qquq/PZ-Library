if getCore():getVersionNumber() == "41.50 - IWBUMS" then
	require 'XpSystem/ISUI/extraskills'

	--table.insert(extraskills.PerkCat, "Others"); To add a new category
	--extraskills.PerkList["Others"] = {}; Add new skills to this category in this table
	--extraskills.PerkName["Others"] = "ContextMenu_Others"; text entry for the category's name

	--To add your skill in an existing category change "Misc" for the internal name i.e. "Crafting" or "Survivalist"
	table.insert(extraskills.PerkList["Libations"], "Brewing");

	extraskills.skills["Brewing"] = {};
	extraskills.skills["Brewing"].name = "ContextMenu_Brewing_Skill";
	extraskills.skills["Brewing"].level = 0; --Starting level
	extraskills.skills["Brewing"].xp = 0;    --Starting XP
	extraskills.skills["Brewing"].boost = 0.25;
	extraskills.skills["Brewing"].multiplier = 0; --Standard multiplier for skills
end

