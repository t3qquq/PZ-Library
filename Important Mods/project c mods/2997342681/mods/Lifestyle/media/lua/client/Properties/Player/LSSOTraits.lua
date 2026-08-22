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
require"Properties/Player/LSSOModulesLoad"

local function LSgetSandboxDividerOptions()
	return {
		{svar=SandboxVars.Text.DividerHygiene, traits={"Sloppy","Tidy","CleanFreak"}},
		{svar=SandboxVars.Text.DividerMeditationNew, traits={"Disciplined","CouchPotato"}},
		{svar=SandboxVars.Text.DividerMusicNew, traits={"Virtuoso","ToneDeaf"}},
		{svar=SandboxVars.Text.DividerDancingNew, traits={"PartyAnimal","Killjoy"}},
	}
end

local function LSgetTraitsToRemove()
	local TraitsToRemove = {}
	local SandboxOptions = LSgetSandboxDividerOptions()
	for k, v in ipairs(SandboxOptions) do
		if not v.svar then
			for n=1, #v.traits do
				table.insert(TraitsToRemove, v.traits[n])
			end
		end
	end
	return TraitsToRemove
end

function LSSOModules.Traits.updateTraitSandbox()
	if not SandboxVars then return; end
    local CharCreation = MainScreen.instance.charCreationProfession
	if not CharCreation then return; end
	local TraitsToRemove = LSgetTraitsToRemove()
	if #TraitsToRemove > 0 then
		local label
		for i=1, #TraitsToRemove do
			--print("LSSOModules.Traits.updateTraitSandbox remove trait: "..TraitsToRemove[i])
			label = TraitFactory.getTrait(TraitsToRemove[i]):getLabel()
			CharCreation.listboxTrait:removeItem(label)
			CharCreation.listboxBadTrait:removeItem(label)
			CharCreation.listboxTraitSelected:removeItem(label)
		end
	end
end

function LSSOModules.Traits.removeTraitsFromCharacter(playerIndex, player)
	local TraitsToRemove = LSgetTraitsToRemove()
	if #TraitsToRemove > 0 then
		for i=1, #TraitsToRemove do
			--print("LSSOModules.Traits.removeTraitsFromCharacter look for trait and remove it: "..TraitsToRemove[i])
			if player:HasTrait(TraitsToRemove[i]) then player:getTraits():remove(TraitsToRemove[i]); end
		end
	end
end

local OGsetSandboxVars = SandboxOptionsScreen.setSandboxVars

SandboxOptionsScreen.setSandboxVars = function(self)
    OGsetSandboxVars(self)
    LSSOModules.Traits.updateTraitSandbox()
end

return LSSOModules.Traits