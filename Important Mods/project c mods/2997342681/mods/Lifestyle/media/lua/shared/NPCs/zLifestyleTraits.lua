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

require('NPCs/MainCreationMethods');

local function onGameBootAddLSTraits()

	--local sandboxHygiene = getServerOptions():getBoolean("Text.DividerHygiene")
	--print("SANDBOX HYGIENE"..getServerOptions():getOption("Text.DividerHygiene"))
	--[[
	local sandboxHygiene, optFound
	local serverM = getServerSettingsManager()
	for i=1,serverM:getSettingsCount() do
	local serverOption = serverM:getSettingsByIndex(i-1)
	local options = serverOption:getSandboxOptions()
		if options and options:getNumOptions() then
			for n=1,options:getNumOptions() do
				local option = options:getOptionByIndex(n-1)
				local optionName = option:getName()
				if optionName and (optionName == "Text.DividerHygiene") then
					sandboxHygiene = option:getValue()
					optFound = true
					print("SANDBOX HYGIENE TRUE GET VALUE")
					break
				end
			end
		end
		if optFound then break; end
	end
	]]--
	
	--[[
    local options = getSandboxOptions()
	local sandboxHygiene
	options:updateFromLua()
   -- local waterShut = options:getOptionByName("WaterShut"):getValue()
	for i=1,options:getNumOptions() do
		local option = options:getOptionByIndex(i-1)
		local optionName = option:getName()
		if optionName and (optionName == "Text.DividerHygiene") then
			sandboxHygiene = option:getValue()
			print("SANDBOX HYGIENE TRUE GET VALUE")
			break
		end
	end
	if not sandboxHygiene then
		sandboxHygiene = options:getOptionByName("Text.DividerHygiene"):getValue()
		print("SANDBOX HYGIENE TRUE GET VALUE BY NAME")
	end
	]]--
		---1st boolean - profession; 2nd boolean - remove in mp

		-- ARTISTIC
		local artistic = TraitFactory.addTrait("Artistic", getText("UI_trait_artistic"), 3, getText("UI_trait_artisticdesc"), false, false);
		artistic:addXPBoost(Perks.Art, 1)

		-- DISCIPLINED
		local disciplined = TraitFactory.addTrait("Disciplined", getText("UI_trait_disciplined"), 10, getText("UI_trait_disciplineddesc"), false, false);
		disciplined:addXPBoost(Perks.Strength, 1)
		disciplined:addXPBoost(Perks.Fitness, 1)
		disciplined:addXPBoost(Perks.Meditation, 1)
		disciplined:addXPBoost(Perks.Nimble, 1)
		
		-- COUCH POTATO
		local couchpotato = TraitFactory.addTrait("CouchPotato", getText("UI_trait_couchpotato"), -8, getText("UI_trait_couchpotatodesc"), false, false);
		couchpotato:addXPBoost(Perks.Strength, -1)
		couchpotato:addXPBoost(Perks.Fitness, -1)

		-- VIRTUOSO
		local virtuoso = TraitFactory.addTrait("Virtuoso", getText("UI_trait_virtuoso"), 3, getText("UI_trait_virtuosodesc"), false, false);
		virtuoso:addXPBoost(Perks.Music, 1)
		
		-- TONE DEAF
		local tonedeaf = TraitFactory.addTrait("ToneDeaf", getText("UI_trait_tonedeaf"), -3, getText("UI_trait_tonedeafdesc"), false, false);

		-- PARTY ANIMAL
		local partyanimal = TraitFactory.addTrait("PartyAnimal", getText("UI_trait_partyanimal"), 2, getText("UI_trait_partyanimaldesc"), false, false);
		partyanimal:addXPBoost(Perks.Dancing, 1)
		
		-- KILLJOY
		local killjoy = TraitFactory.addTrait("Killjoy", getText("UI_trait_killjoy"), -1, getText("UI_trait_killjoydesc"), false, false);

		-- SLOPPY
		local sloppy = TraitFactory.addTrait("Sloppy", getText("UI_trait_sloppy"), -3, getText("UI_trait_sloppydesc"), false, false);

		-- CLEAN FREAK
		local cleanfreak = TraitFactory.addTrait("CleanFreak", getText("UI_trait_cleanfreak"), -6, getText("UI_trait_cleanfreakdesc"), false, false);

		-- TIDY
		local tidy = TraitFactory.addTrait("Tidy", getText("UI_trait_tidy"), 2, getText("UI_trait_tidydesc"), false, false);
		tidy:addXPBoost(Perks.Cleaning, 1)

		-- MUSIC PREFERENCES
		local disco = TraitFactory.addTrait("disco", getText("UI_trait_disco"), 0, getText("UI_trait_discodesc"), false, false);
		local discono = TraitFactory.addTrait("discono", getText("UI_trait_discono"), 0, getText("UI_trait_disconodesc"), false, false);
		local beach = TraitFactory.addTrait("beach", getText("UI_trait_beach"), 0, getText("UI_trait_beachdesc"), false, false);
		local beachno = TraitFactory.addTrait("beachno", getText("UI_trait_beachno"), 0, getText("UI_trait_beachnodesc"), false, false);
		local classical = TraitFactory.addTrait("classical", getText("UI_trait_classical"), 0, getText("UI_trait_classicaldesc"), false, false);
		local classicalno = TraitFactory.addTrait("classicalno", getText("UI_trait_classicalno"), 0, getText("UI_trait_classicalnodesc"), false, false);
		local country = TraitFactory.addTrait("country", getText("UI_trait_country"), 0, getText("UI_trait_countrydesc"), false, false);
		local countryno = TraitFactory.addTrait("countryno", getText("UI_trait_countryno"), 0, getText("UI_trait_countrynodesc"), false, false);
		local holiday = TraitFactory.addTrait("holiday", getText("UI_trait_holiday"), 0, getText("UI_trait_holidaydesc"), false, false);
		local holidayno = TraitFactory.addTrait("holidayno", getText("UI_trait_holidayno"), 0, getText("UI_trait_holidaynodesc"), false, false);
		local jazz = TraitFactory.addTrait("jazz", getText("UI_trait_jazz"), 0, getText("UI_trait_jazzdesc"), false, false);
		local jazzno = TraitFactory.addTrait("jazzno", getText("UI_trait_jazzno"), 0, getText("UI_trait_jazznodesc"), false, false);
		local metal = TraitFactory.addTrait("metal", getText("UI_trait_metal"), 0, getText("UI_trait_metaldesc"), false, false);
		local metalno = TraitFactory.addTrait("metalno", getText("UI_trait_metalno"), 0, getText("UI_trait_metalnodesc"), false, false);
		local muzak = TraitFactory.addTrait("muzak", getText("UI_trait_muzak"), 0, getText("UI_trait_muzakdesc"), false, false);
		local muzakno = TraitFactory.addTrait("muzakno", getText("UI_trait_muzakno"), 0, getText("UI_trait_muzaknodesc"), false, false);
		local pop = TraitFactory.addTrait("pop", getText("UI_trait_pop"), 0, getText("UI_trait_popdesc"), false, false);
		local popno = TraitFactory.addTrait("popno", getText("UI_trait_popno"), 0, getText("UI_trait_popnodesc"), false, false);
		local rap = TraitFactory.addTrait("rap", getText("UI_trait_rap"), 0, getText("UI_trait_rapdesc"), false, false);
		local rapno = TraitFactory.addTrait("rapno", getText("UI_trait_rapno"), 0, getText("UI_trait_rapnodesc"), false, false);
		local rbsoul = TraitFactory.addTrait("rbsoul", getText("UI_trait_rbsoul"), 0, getText("UI_trait_rbsouldesc"), false, false);
		local rbsoulno = TraitFactory.addTrait("rbsoulno", getText("UI_trait_rbsoulno"), 0, getText("UI_trait_rbsoulnodesc"), false, false);
		local reggae = TraitFactory.addTrait("reggae", getText("UI_trait_reggae"), 0, getText("UI_trait_reggaedesc"), false, false);
		local reggaeno = TraitFactory.addTrait("reggaeno", getText("UI_trait_reggaeno"), 0, getText("UI_trait_reggaenodesc"), false, false);
		local rock = TraitFactory.addTrait("rock", getText("UI_trait_rock"), 0, getText("UI_trait_rockdesc"), false, false);
		local rockno = TraitFactory.addTrait("rockno", getText("UI_trait_rockno"), 0, getText("UI_trait_rocknodesc"), false, false);
		local world = TraitFactory.addTrait("world", getText("UI_trait_world"), 0, getText("UI_trait_worlddesc"), false, false);
		local worldno = TraitFactory.addTrait("worldno", getText("UI_trait_worldno"), 0, getText("UI_trait_worldnodesc"), false, false);
		local salsa = TraitFactory.addTrait("salsa", getText("UI_trait_salsa"), 0, getText("UI_trait_salsadesc"), false, false);
		local salsano = TraitFactory.addTrait("salsano", getText("UI_trait_salsano"), 0, getText("UI_trait_salsanodesc"), false, false);

		-- MUTUAL EXCLUSIVES
		
		TraitFactory.setMutualExclusive("CouchPotato", "Disciplined");
		TraitFactory.setMutualExclusive("CouchPotato", "Athletic");
		TraitFactory.setMutualExclusive("CouchPotato", "Fit");
		TraitFactory.setMutualExclusive("CouchPotato", "Gymnast");
		TraitFactory.setMutualExclusive("CouchPotato", "Jogger");
		TraitFactory.setMutualExclusive("Disciplined", "Overweight");
		TraitFactory.setMutualExclusive("Disciplined", "Obese");
		TraitFactory.setMutualExclusive("Disciplined", "Out of Shape");
		TraitFactory.setMutualExclusive("Disciplined", "Unfit");
		TraitFactory.setMutualExclusive("Virtuoso", "HardOfHearing");
		TraitFactory.setMutualExclusive("Virtuoso", "Deaf");
		TraitFactory.setMutualExclusive("Virtuoso", "ToneDeaf");
		TraitFactory.setMutualExclusive("ToneDeaf", "Deaf");
		TraitFactory.setMutualExclusive("PartyAnimal", "Killjoy");
		TraitFactory.setMutualExclusive("PartyAnimal", "Deaf");
		TraitFactory.setMutualExclusive("Killjoy", "Deaf");
		TraitFactory.setMutualExclusive("CleanFreak", "Sloppy");
		TraitFactory.setMutualExclusive("Tidy", "Sloppy");

end

Events.OnGameBoot.Add(onGameBootAddLSTraits);
