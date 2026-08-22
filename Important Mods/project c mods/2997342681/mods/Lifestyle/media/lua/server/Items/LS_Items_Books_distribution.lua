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

local bookTable = {
	BookstoreBooks = {1, 1, 0.8, 0.6, 0.4},
	ClassroomMisc = {0.8, 0.8, 0.6, 0.4, 0.2},
	ClassroomShelves = {0.8, 0.8, 0.6, 0.4, 0.2},
	CrateBooks = {0.8, 0.8, 0.6, 0.4, 0.2},
	LibraryBooks = {1, 1, 0.8, 0.6, 0.4},
	LivingRoomShelf = {0.08, 0.08, 0.05, 0.05, 0.02},
	LivingRoomShelfNoTapes = {0.08, 0.08, 0.05, 0.05, 0.02},
	PostOfficeBooks = {0.08, 0.08, 0.05, 0.05, 0.02},
	ShelfGeneric = {0.06, 0.06, 0.03, 0.03, 0.01},
}

local magTable = {
	BookstoreBooks = {LSMagazineEdition1=0.4,LSMagazineEdition2=0.4},
	ClassroomDesk = {SheetMusicBook=1,LSMagazineEdition1=1,LSMagazineEdition2=1},
	ClassroomMisc = {SheetMusicBook=0.2,LSMagazineEdition1=0.2,LSMagazineEdition2=0.2},
	ClassroomShelves = {SheetMusicBook=0.2,LSMagazineEdition1=0.2,LSMagazineEdition2=0.2},
	CrateBooks = {LSMagazineEdition1=0.2,LSMagazineEdition2=0.2},
	CrateMagazines = {LSMagazineEdition1=1,LSMagazineEdition2=1},
	LibraryBooks = {LSMagazineEdition1=0.4,LSMagazineEdition2=0.4},
	LivingRoomShelf = {SheetMusicBook=0.05,LSMagazineEdition1=0.03,LSMagazineEdition2=0.03},
	LivingRoomShelfNoTapes = {SheetMusicBook=0.05,LSMagazineEdition1=0.03,LSMagazineEdition2=0.03},
	MagazineRackMixed = {LSMagazineEdition1=1,LSMagazineEdition2=1},
	PostOfficeMagazines = {LSMagazineEdition1=1,LSMagazineEdition2=1},
	ShelfGeneric = {SheetMusicBook=0.05,LSMagazineEdition1=0.01,LSMagazineEdition2=0.01},
}

-- Item distribution
function LSItemsDistribution.Books(books, magazines)

	if #books > 0 then
		for k, v in pairs(bookTable) do
			for i=1,#books do
				for n=1, 5 do
					table.insert(ProceduralDistributions.list[k].items, "Lifestyle.Book"..books[i]..tostring(n));
					table.insert(ProceduralDistributions.list[k].items, v[n]);
				end
			end
		end
	end

	if #magazines > 0 then
		for k, v in pairs(magTable) do
			for i=1,#magazines do
				if v[magazines[i]] then
					table.insert(ProceduralDistributions.list[k].items, "Lifestyle."..magazines[i]);
					table.insert(ProceduralDistributions.list[k].items, v[magazines[i]]);
				end
			end
		end
	end

	ItemPickerJava.Parse()
end