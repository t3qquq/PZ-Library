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

local function getAmbition(character)
	if character and character:hasModData() and character:getModData().Ambitions then return character:getModData().Ambitions['LSGrimeFighter']; end
	return false
end

function GetCleaningTime(player, bleach)

	-----------------
	----ARGS---------

	local cleanTime = 350
	local skillLevel = player:getPerkLevel(Perks.Cleaning)

	-----------------
	----BLEACH-------

	if bleach then
	
		if bleach:getFullType() == "Lifestyle.BucketBleachFull" then
			cleanTime = 650
		else
			cleanTime = 500
		end
	end

	-----------------
	----SKILL--------

	if skillLevel == 10 then
		cleanTime = cleanTime*0.25
	elseif skillLevel == 9 then
		cleanTime = cleanTime*0.3
	elseif skillLevel == 8 then
		cleanTime = cleanTime*0.35
	elseif skillLevel == 7 then
		cleanTime = cleanTime*0.45
	elseif skillLevel == 6 then
		cleanTime = cleanTime*0.5
	elseif skillLevel == 5 then
		cleanTime = cleanTime*0.6
	elseif skillLevel == 4 then
		cleanTime = cleanTime*0.65
	elseif skillLevel == 3 then
		cleanTime = cleanTime*0.75
	elseif skillLevel == 2 then
		cleanTime = cleanTime*0.8
	elseif skillLevel == 1 then
		cleanTime = cleanTime*0.95
	end

	-----------------
	----TRAITS-------

	if player:HasTrait("Tidy") then
		cleanTime = cleanTime*0.6
	elseif player:HasTrait("Sloppy") or player:HasTrait("CleanFreak") then
		cleanTime = cleanTime*1.5
	elseif player:HasTrait("AllThumbs") then
		cleanTime = cleanTime*1.1
	elseif player:HasTrait("Dextrous") then
		cleanTime = cleanTime*0.9
	end

	-----------------
	----AMBT---------
	local ambt = getAmbition(player)
	if ambt and ambt.completed then cleanTime = cleanTime/2; end
	-----------------

	return cleanTime

end

function GetCleaningTimeTile(player, object, condition)

	-----------------
	----ARGS---------

	local cleanTime = 300
	local skillLevel = player:getPerkLevel(Perks.Cleaning)

	-----------------
	----CONDITION----

	if condition then
		cleanTime = cleanTime*condition
	end

	-----------------
	----SKILL--------

	if skillLevel == 10 then
		cleanTime = cleanTime*0.25
	elseif skillLevel == 9 then
		cleanTime = cleanTime*0.3
	elseif skillLevel == 8 then
		cleanTime = cleanTime*0.35
	elseif skillLevel == 7 then
		cleanTime = cleanTime*0.45
	elseif skillLevel == 6 then
		cleanTime = cleanTime*0.5
	elseif skillLevel == 5 then
		cleanTime = cleanTime*0.6
	elseif skillLevel == 4 then
		cleanTime = cleanTime*0.65
	elseif skillLevel == 3 then
		cleanTime = cleanTime*0.75
	elseif skillLevel == 2 then
		cleanTime = cleanTime*0.8
	elseif skillLevel == 1 then
		cleanTime = cleanTime*0.95
	end

	-----------------
	----TRAITS-------

	if player:HasTrait("Tidy") then
		cleanTime = cleanTime*0.6
	elseif player:HasTrait("Sloppy") or player:HasTrait("CleanFreak") then
		cleanTime = cleanTime*1.5
	elseif player:HasTrait("AllThumbs") then
		cleanTime = cleanTime*1.1
	elseif player:HasTrait("Dextrous") then
		cleanTime = cleanTime*0.9
	end

	-----------------
	----AMBT---------
	local ambt = getAmbition(player)
	if ambt and ambt.completed then cleanTime = cleanTime/2; end
	-----------------

	return cleanTime

end

function GetUnclogBaseDuration(player, object)

	-----------------
	----ARGS---------

	local unclogTime = 3000
	local skillLevel = player:getPerkLevel(Perks.Cleaning)

	-----------------
	----SKILL--------

	if skillLevel == 10 then
		unclogTime = unclogTime*0.5
	elseif skillLevel >= 8 then
		unclogTime = unclogTime*0.6
	elseif skillLevel >= 6 then
		unclogTime = unclogTime*0.7
	elseif skillLevel >= 4 then
		unclogTime = unclogTime*0.8
	elseif skillLevel >= 2 then
		unclogTime = unclogTime*0.9
	end

	-----------------
	----TRAITS-------

	if player:HasTrait("Tidy") then
		unclogTime = unclogTime*0.7
	end

	-----------------
	----AMBT---------
	local ambt = getAmbition(player)
	if ambt and ambt.completed then unclogTime = unclogTime/2; end
	-----------------

	return unclogTime

end