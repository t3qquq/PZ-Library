
-- fix for multiplayer errors
if isClient() then return end


local original_STrapGlobalObject_checkForAnimal = STrapGlobalObject.checkForAnimal
-- C:\Games\Steam\steamapps\common\ProjectZomboid\media\lua\server\Traps\STrapGlobalObject.lua
 

function STrapGlobalObject:checkForAnimal(square)
	
	-- first call original function
	original_STrapGlobalObject_checkForAnimal(self,square)

	if not square then --this is the check the original code uses to decide if a trap is too close, but reversed
		return;
	end
	
	-- Now we duplicate the orignal code... feels wasteful, but also feels safer than completely replacing the original function and potentially breaking other mods
	
    if self.destroyed then
        return;
    end
    -- first, get which animal we'll attract
    local animalsList = {};
    if self.zones then
        for zoneType in pairs(self.zones) do
            self:testForAnimal(zoneType, animalsList);
        end;
    else
        self:testForAnimal(self.zone, animalsList);
    end;
    -- random an animal
    if #animalsList > 0 then
        local int = ZombRand(#animalsList) + 1;
        local testAnimal = animalsList[int];
        if testAnimal then
            self:noise('trapped '..testAnimal.type..' '..self.x..','..self.y..','..self.z)
            self:setAnimal(testAnimal)
        end
    end

end