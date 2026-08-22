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

-- Item distribution
function LSItemsDistribution.Instruments()
--CrateInstruments
table.insert(ProceduralDistributions["list"]["CrateInstruments"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["CrateInstruments"].items, 4);

--MusicStoreOthers
table.insert(ProceduralDistributions["list"]["MusicStoreOthers"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["MusicStoreOthers"].items, 10);

--WARDROBECHILD
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.Banjo");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.Violin");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.Flute");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 1);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.Saxophone");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.Trumpet");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.GuitarAcoustic");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 1);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.GuitarElectricBassBlack");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.GuitarElectricBassBlue");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.GuitarElectricBassRed");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.GuitarElectricBlack");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.GuitarElectricBlue");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.GuitarElectricRed");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.Guitarcase");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Base.Keytar");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["WardrobeChild"].items, 0.8);

--WARDROBEMAN
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.Banjo");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.Flute");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.Saxophone");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.Trumpet");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.Violin");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.GuitarAcoustic");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.GuitarElectricBassBlack");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.GuitarElectricBassBlue");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.GuitarElectricBassRed");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.GuitarElectricBlack");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.GuitarElectricBlue");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.GuitarElectricRed");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.Guitarcase");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Base.Keytar");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["WardrobeMan"].items, 0.08);

--WARDROBEMANClassy
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.Banjo");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.Flute");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.Saxophone");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.Trumpet");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.Violin");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.GuitarAcoustic");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.GuitarElectricBassBlack");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.GuitarElectricBassBlue");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.GuitarElectricBassRed");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.GuitarElectricBlack");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.GuitarElectricBlue");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.GuitarElectricRed");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.Guitarcase");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Base.Keytar");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["WardrobeManClassy"].items, 0.08);

--WARDROBERedneck
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.Banjo");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.2);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.Flute");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.Saxophone");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.Trumpet");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.Violin");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.GuitarAcoustic");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.GuitarElectricBassBlack");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.GuitarElectricBassBlue");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.GuitarElectricBassRed");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.GuitarElectricBlack");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.GuitarElectricBlue");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.GuitarElectricRed");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.Guitarcase");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Base.Keytar");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["WardrobeRedneck"].items, 0.9);

--WARDROBEWoman
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.Banjo");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.Flute");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.Saxophone");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.Trumpet");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.Violin");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.GuitarAcoustic");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.GuitarElectricBassBlack");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.GuitarElectricBassBlue");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.GuitarElectricBassRed");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.GuitarElectricBlack");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.GuitarElectricBlue");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.GuitarElectricRed");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.Guitarcase");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Base.Keytar");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["WardrobeWoman"].items, 0.08);

--WARDROBEWomanClassy
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.Banjo");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.Flute");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.Saxophone");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.Trumpet");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.Violin");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.GuitarAcoustic");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.1);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.GuitarElectricBassBlack");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.GuitarElectricBassBlue");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.GuitarElectricBassRed");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.GuitarElectricBlack");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.GuitarElectricBlue");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.GuitarElectricRed");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.Guitarcase");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Base.Keytar");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, "Lifestyle.Harmonica");
table.insert(ProceduralDistributions["list"]["WardrobeWomanClassy"].items, 0.08);

	ItemPickerJava.Parse()
end