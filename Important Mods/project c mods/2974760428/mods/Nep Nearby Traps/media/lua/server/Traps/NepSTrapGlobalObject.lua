
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
    for i,v in ipairs(Animals) do
        -- check if at this hour we can get this animal
        local timesOk = self:checkTime(v);
--        local timesOk = true;
        if v.traps[self.trapType] and
                v.baits[self.bait] and ZombRand(100) < (v.traps[self.trapType] + v.baits[self.bait] + (self.trappingSkill * 1.5)) and
                timesOk and v.zone[self.zone] and ZombRand(100) < (v.zone[self.zone] + (self.trappingSkill * 1.5)) then -- this animal can be caught by this trap and we have the correct bait for it
            -- now check if the bait is still fresh
            if self:checkBaitFreshness() then
--                print("can catch " .. v.type);
                table.insert(animalsList, v);
            end
        end
    end
    -- random an animal
    if #animalsList > 0 then
        local int = ZombRand(#animalsList) + 1;
        local testAnimal = animalsList[int];
        if testAnimal then
--            print("get animal : " .. testAnimal.type .. " in zone " .. self.zone);
            self:noise('trapped '..testAnimal.type..' '..self.x..','..self.y..','..self.z)
            self:setAnimal(testAnimal)
        end
    end
	

end