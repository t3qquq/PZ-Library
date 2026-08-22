require "TimedActions/ISEatFoodAction"

SSO_last_match_sound = 0;
SSO_last_lighter_sound = 0;
SSO_last_puff_sound = 0;
SSO_last_sound = "";

SmokingSoundsOverhaul = SmokingSoundsOverhaul or {}

-- pick 1..3, avoiding an immediate repeat
local function rollVariant(last)
	for _ = 1, 16 do
		local r = ZombRand(1, 4)
		if r ~= last then return r end
	end
	return last
end

--Support for True Smoking
function SmokingSoundsOverhaul:getPuffSound(isFemale)
	local gender = isFemale and "f" or "m"
	SSO_last_puff_sound = 1 -- only one puff variant ships today
	return "Smoking_puff1" .. gender
end

function SmokingSoundsOverhaul:getLightingSound(player)
	return SSO_last_sound
end
--

local start_function_o = ISEatFoodAction.start;
local stop_function_o = ISEatFoodAction.stop;

function ISEatFoodAction:start()
	--Susceptible support for removing mask before smoking/eating * Not my code! *
	if getActivatedMods():contains("Susceptible") then
		if SusceptibleMod.isPlayerSusceptible(self.character) then
			local threat = SusceptibleMod.threatByPlayer[self.character];
			if threat and threat > 0 then
				self:forceStop();
				return;
			end

			local mask = SusceptibleMod.getEquippedMaskItemAndData(self.character);
			if mask then
				self:stop();
				self:autoManageMask(mask);
				return;
			end
		end
	end
	--

	local customEatSound = self.item:getCustomEatSound() or ""

	local eatType = self.item:getEatType()
	local fullType = self.item:getFullType()
	local isSmokable = fullType == "Base.Cigarettes" or (eatType == "Cigarettes")

	--Run the original unless this is a smokable whose CustomEatSound we claim
	local doOriginal = true
	if isSmokable
		and (customEatSound == "" or customEatSound == "Eating"
			or customEatSound == "sm_smoking" or customEatSound == "GF_LightUp") then
		doOriginal = false
	end

	if doOriginal then
		start_function_o(self);
	else
		local playerInv = self.character:getInventory()

		local lighter = playerInv:getFirstTypeRecurse("Base.Lighter")
			or playerInv:getFirstTypeRecurse("Base.LighterDisposable")
			or playerInv:getFirstTypeRecurse("Base.LighterBBQ")
		local matches = playerInv:getFirstTypeRecurse("Base.Matches")

		--Smoker support
		local SM_foil_lighter = playerInv:getFirstType("SM.SMFoil_Lighter")
		local SM_Matchbox = playerInv:getFirstType("SM.Matches")

		local current_sound = ""

		--Randomly select one of the 3 sounds (avoid immediate repeat)
		if lighter or SM_foil_lighter then
			SSO_last_lighter_sound = rollVariant(SSO_last_lighter_sound)
			current_sound = "Smoking_lighter" .. SSO_last_lighter_sound
		elseif matches or SM_Matchbox then
			SSO_last_match_sound = rollVariant(SSO_last_match_sound)
			current_sound = "Smoking_matches" .. SSO_last_match_sound
		end

		local isFemale = self.character:isFemale()
		local gender = isFemale and "f" or "m"

		if current_sound ~= "" then
			self.eatSound = current_sound .. gender
		elseif self.carLighter or self.openFlame then
			self.eatSound = SmokingSoundsOverhaul:getPuffSound(isFemale)
		else
			self.eatSound = ""
		end

		SSO_last_sound = self.eatSound

		start_function_o(self);
		self.eatSound = ""   -- clear so update() does not replay it
	end
end

function ISEatFoodAction:stop()
	stop_function_o(self);
end
