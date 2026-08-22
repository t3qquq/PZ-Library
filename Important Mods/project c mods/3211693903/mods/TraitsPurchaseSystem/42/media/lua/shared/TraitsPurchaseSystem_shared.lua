-------------------------------------------------------------------------
-- global variables
TPS = TPS or {}
TPS.Blocklist = {
    CharacterTrait.EMACIATED,
    CharacterTrait.VERY_UNDERWEIGHT,
    CharacterTrait.UNDERWEIGHT,
    CharacterTrait.OVERWEIGHT,
    CharacterTrait.OBESE,
    CharacterTrait.FIT,
    CharacterTrait.ATHLETIC,
    CharacterTrait.STOUT,
    CharacterTrait.STRONG,
    CharacterTrait.BLACKSMITH2,
    CharacterTrait.COOK2,
    CharacterTrait.MECHANICS2,
    CharacterTrait.NUTRITIONIST2
}
TPS.ExtraBlocklist = TPS.ExtraBlocklist or {}
TPS.Hooked = TPS.Hooked or {}
TPS.Traits = { Perks.Traits1, Perks.Traits2, Perks.Traits3 };
TPS.Windows = TPS.Windows or {}

extraTraitBlockList = nil;

-------------------------------------------------------------------------
-- global functions

--[[ noise(message)
    prints noise
]]
function noise(message)
    print('TraitsPurchaseSystem: ' .. message);
end

--[[ playTraitAdded()
    plays sound
]]
function playTraitAdded()
    getSoundManager():playUISound("VehicleHotwireSuccess");
end

--[[ playTraitRemoved()
    plays sound
]]
function playTraitRemoved()
    getSoundManager():playUISound("VehicleHotwireFail");
end

--[[ playTraitSelected()
    plays sound
]]
function playTraitSelected()
    getSoundManager():playUISound("UISelectListItem");
end


--[[ getCurrentTraitPoints(player)
    returns current amount of Trait points
]]
function getCurrentTraitPoints(player)
    return player:getPerkLevel(TPS.Traits[1]) + player:getPerkLevel(TPS.Traits[2]) + player:getPerkLevel(TPS.Traits[3])
end