if getCore():getVersionNumber() == "41.50 - IWBUMS" then
	require 'XpSystem/ISUI/extraskills'

	--table.insert(extraskills.PerkCat, "Others"); To add a new category
	--extraskills.PerkList["Others"] = {}; Add new skills to this category in this table
	--extraskills.PerkName["Others"] = "ContextMenu_Others"; text entry for the category's name

	--To add your skill in an existing category change "Misc" for the internal name i.e. "Crafting" or "Survivalist"
	table.insert(extraskills.PerkList["Libations"], "WineMaking");

	extraskills.skills["WineMaking"] = {};
	extraskills.skills["WineMaking"].name = "ContextMenu_WineMaking_Skill";
	extraskills.skills["WineMaking"].level = 0; --Starting level
	extraskills.skills["WineMaking"].xp = 0;    --Starting XP
	extraskills.skills["WineMaking"].boost = 0.25;
	extraskills.skills["WineMaking"].multiplier = 0; --Standard multiplier for skills
end