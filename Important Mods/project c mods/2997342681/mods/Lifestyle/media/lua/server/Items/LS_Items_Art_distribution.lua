--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

require "Items/ProceduralDistributions"
LSItemsDistribution = LSItemsDistribution or {}

local artTable = {
	ArtStoreOther = {paintTubs=10,oldPaintBrush=2,paintPaletteEmpty=2},
	ArtStorePen = {oldPaintBrush=20},
	ArtSupplies = {paintTubs=8,oldPaintBrush=4,paintPaletteEmpty=4},
	ClassroomDesk = {paintTubs=2,oldPaintBrush=1,paintPaletteEmpty=1},
	ClassroomMisc = {paintTubs=0.8,oldPaintBrush=0.6,paintPaletteEmpty=0.6},
	ClassroomShelves = {paintTubs=1,oldPaintBrush=0.8,paintPaletteEmpty=0.8},
	CrateOfficeSupplies = {paintTubs=2},
	CratePaint = {paintTubs=10,clone={paintTubs=5}},
	CrateRandomJunk = {paintTubs=1,oldPaintBrush=0.8,paintPaletteEmpty=0.8},
	CrateSalonSupplies = {paintTubs=2},
	DaycareCounter = {paintTubs=6},
	DaycareDesk = {paintTubs=6},
	DaycareShelves = {paintTubs=20,clone={paintTubs=10}},
	GigamartSchool = {paintTubs=8,oldPaintBrush=4,paintPaletteEmpty=4,clone={paintTubs=4}},
	GigamartToys = {paintTubs=20,clone={paintTubs=10}},
	Hobbies = {paintTubs=8,oldPaintBrush=4,paintPaletteEmpty=4},
	LivingRoomSideTable = {paintTubs=2},
	LivingRoomSideTableNoRemote = {paintTubs=2},
	OtherGeneric = {paintTubs=2},
	SchoolLockers = {paintTubs=4,oldPaintBrush=2,paintPaletteEmpty=2},
	ShelfGeneric = {paintTubs=2},
	WardrobeChild = {paintTubs=2,oldPaintBrush=0.5,paintPaletteEmpty=0.5},
}

-- Item distribution
function LSItemsDistribution.Art()
	local artItems = {"paintTubs","oldPaintBrush","paintPaletteEmpty"}

	for k, v in pairs(artTable) do
		for i=1,#artItems do
			if v[artItems[i]] then
				table.insert(ProceduralDistributions.list[k].items, "Lifestyle."..artItems[i]);
				table.insert(ProceduralDistributions.list[k].items, v[artItems[i]]);
				if v.clone and v.clone[artItems[i]] then
					table.insert(ProceduralDistributions.list[k].items, "Lifestyle."..artItems[i]);
					table.insert(ProceduralDistributions.list[k].items, v.clone[artItems[i]]);
				end
			end
		end
	end

	ItemPickerJava.Parse()
end