require('NPCs/MainCreationMethods');

local base
function wyweight1()
    local player = getPlayer();
    base=player:getMaxWeightBase()
    return base
end

function wyweight()
    local player = getPlayer();
    local default = player:getMaxWeightBase();
    local extra = 0;

    if SandboxVars.wyweight.extraweight then
        extra = SandboxVars.wyweight.extraweight;
    end
    
    if default ~= base then
        player:setMaxWeightBase(math.floor(base + extra))
    else
        player:setMaxWeightBase(math.floor(base + extra))
    end
   
end


Events.OnGameStart.Add(wyweight1);
Events.EveryOneMinute.Add(wyweight);