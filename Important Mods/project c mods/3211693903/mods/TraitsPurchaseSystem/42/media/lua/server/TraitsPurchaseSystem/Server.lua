if isClient() then return end

-------------------------------------------------------------------------
-- global functions

--[[ purchaseTrait(player, amount)
    subtraces the amount to the traits and redistributes xp
]]
function spendTraitPoints(player, amount)
    local traitpoints = getCurrentTraitPoints(player);
    local playerXP = player:getXp();
    local currentXP = { playerXP:getXP(TPS.Traits[1]), playerXP:getXP(TPS.Traits[2]), playerXP:getXP(TPS.Traits[3]) };
    local totalXP = { 0, 0, 0 };
    
    if (traitpoints > 0) then
        local points = { player:getPerkLevel(TPS.Traits[1]), player:getPerkLevel(TPS.Traits[2]), player:getPerkLevel(TPS.Traits[3]) };
        totalXP[1] = TPS.Traits[1]:getTotalXpForLevel(points[1]);
        totalXP[2] = TPS.Traits[2]:getTotalXpForLevel(points[2]);
        totalXP[3] = TPS.Traits[3]:getTotalXpForLevel(points[3]);
    end
    currentXP[1] = currentXP[1] - totalXP[1];
    currentXP[2] = currentXP[2] - totalXP[2];
    currentXP[3] = currentXP[3] - totalXP[3];

    for i=1, (amount) do
        if (player:getPerkLevel(TPS.Traits[3]) > 0) then 
            player:LoseLevel(TPS.Traits[3]);
        elseif (player:getPerkLevel(TPS.Traits[2]) > 0) then
            player:LoseLevel(TPS.Traits[2]);
        else
            player:LoseLevel(TPS.Traits[1]);
        end
    end
    playerXP:setXPToLevel(TPS.Traits[1], player:getPerkLevel(TPS.Traits[1]));
    playerXP:setXPToLevel(TPS.Traits[2], player:getPerkLevel(TPS.Traits[2]));
    playerXP:setXPToLevel(TPS.Traits[3], player:getPerkLevel(TPS.Traits[3]));

    for i=1, (currentXP[1] + currentXP[2] + currentXP[3]) do
        if (traitpoints >= 20) then
            addXpNoMultiplier(player, TPS.Traits[3], 1);
        elseif (traitpoints >= 10) then
            addXpNoMultiplier(player, TPS.Traits[2], 1);
        else
            addXpNoMultiplier(player, TPS.Traits[1], 1);
        end
    end
    SyncXp(player);
end

-------------------------------------------------------------------------
-- local functions

--[[ AddTraitXP(player, amount)
    Adds Trait XP
]]
local function AddTraitXP(player, amount)
    if (not player or amount <= 0) then
        return
    end
    local trait = TPS.Traits[1];
    local traitpoints = getCurrentTraitPoints(player);
    
    noise('AddTraitXP : ' .. player:getUsername() .. ' : ' .. amount);
    if (traitpoints >= 20) then
        trait = TPS.Traits[3];
    elseif (traitpoints >= 10) then
        trait = TPS.Traits[2];
    else
        trait = TPS.Traits[1];
    end
    addXpNoMultiplier(player, trait, amount);
end

local function OnClientCommand(module, command, player, args)
	if (module ~= "TraitsPurchaseSystem") then
        return
    end
    
    local username = player:getUsername();
    local playerID = player:getOnlineID();
    local playerByID = getPlayerByOnlineID(playerID);
    local player = playerByID;
    if (not player) then
        return;
    end
    
    local argStr = ''
	for k,v in pairs(args) do argStr = argStr..' '..k..'='..v end
	noise('received OnClientCommand from : ' .. player:getUsername() .. ' - ' .. module .. ' ' .. command .. ' ' .. argStr);
    
    local trait = nil;
    local traitList = CharacterTraitDefinition.getTraits();
    for i = 0, traitList:size() - 1 do
        local localTrait = traitList:get(i);
        local localTraitString = localTrait:getType():toString();
        if (localTraitString == args.TPStraitString) then
            trait = localTrait;
            break;
        end
    end
    
    if (not trait) then
        noise('cannot not find trait - ' .. player:getUsername() .. ' : ' .. args.TPStrait .. ' : ' .. args.TPStraitString);
        return;
    elseif (trait == CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.BLACKSMITH2)) then
        trait = CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.BLACKSMITH);
    elseif (trait == CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.COOK2)) then
        trait = CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.COOK);
    elseif (trait == CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.MECHANICS2)) then
        trait = CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.MECHANICS);
    elseif (trait == CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.NUTRITIONIST2)) then
        trait = CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.NUTRITIONIST);
    end
    
    if (command == "TPSAddTrait") then
        if (player:hasTrait(trait:getType())) then
            return;
        end
        noise('adding trait - ' .. player:getUsername() .. ' : ' .. trait:getType():toString());
        spendTraitPoints(player, args.TPScost)
        player:getCharacterTraits():add(trait:getType());
        --player:modifyTraitXPBoost(trait:getType(), false);
        local map = trait:getXpBoosts()
        map = transformIntoKahluaTable(map)
        for perk, value in pairs(map) do
            for i=1, tonumber(tostring(value)) do -- for loop chokes using value as a integer (its a double..)
                if player:getPerkLevel(perk) == 10 then break end
                player:LevelPerk(perk)
                luautils.updatePerksXp(perk, player)
            end
        end
        if (trait:hasGrantedRecipes()) then
            for i=0, trait:getGrantedRecipes():size()-1 do
                local recipe = trait:getGrantedRecipes():get(i);
                player:learnRecipe(recipe);
            end
        end

        args = { TPSusername = username, TPSplayerID = playerID }
        sendServerCommand("TraitsPurchaseSystem", "TPSTraitAdded", args);
        
        return;
    end
    
    if (command == "TPSRemoveTrait") then
        if (not player:hasTrait(trait:getType())) then
            return;
        end
        noise('removing trait - ' .. player:getUsername() .. ' : ' .. trait:getType():toString());
        spendTraitPoints(player, args.TPScost);
        player:getCharacterTraits():remove(trait:getType());

        args = { TPSusername = username, TPSplayerID = playerID }
        sendServerCommand("TraitsPurchaseSystem", "TPSTraitRemoved", args);

        return;
    end
end
Events.OnClientCommand.Add(OnClientCommand);

--[[ TPSystemHandleOnCharacterDeath(character)
    Receives OnCharacterDeath event
        - Add Traits XP if a zombie died
]]
local function TPSystemHandleOnCharacterDeath(character)
    if (not character or not character:isZombie()) then
        return
    end
    local attacker = character:getAttackedBy();
    if (not attacker or not instanceof(attacker, "IsoPlayer")) then
        return
    end

    noise('zombie died - ' .. attacker:getUsername());
    local amount = SandboxVars.TraitsPurchaseSystem.XPperZombieKill * SandboxVars.TraitsPurchaseSystem.XPMultiplier;
    AddTraitXP(attacker, amount);
end
Events.OnCharacterDeath.Add(TPSystemHandleOnCharacterDeath);

--[[ LevelPerk(player, perk, level, levelUp)
    Receives LevelPerk event
        - Add Traits XP if any Perk but Traits level up
]]
local function TPSystemHandlePlayerLevelPerk(player, perk, level, levelUp)
    if (not player) then
        return;
    end
    if (player:getHoursSurvived() < 0.1) then
        return;
    end
    if (TPS.Traits[1] == perk or TPS.Traits[2] == perk or TPS.Traits[3] == perk or not levelUp) then
        return;
    end
    
    noise('perk leveled up - ' .. player:getUsername() .. ' : ' .. perk:getName());
    local amount = SandboxVars.TraitsPurchaseSystem.XPperPerkLevelup * SandboxVars.TraitsPurchaseSystem.XPMultiplier;
    AddTraitXP(player, amount);
end
Events.LevelPerk.Add(TPSystemHandlePlayerLevelPerk);

-- /reloadlua server/TraitsPurchaseSystem/Server.lua