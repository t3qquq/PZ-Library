if ISSearchManager then
    function ISSearchManager:checkShouldDisable()
        if ISSearchManager.showDebug then return false; end;
        local plStats = self.character:getStats();
        if plStats then
            if (plStats:getNumVeryCloseZombies() > 0) then return true; end;
            if (plStats:getNumVisibleZombies() >=3) and (plStats:getNumChasingZombies() >=3) then return true; end;
        end;
        -- Removed the running check
        if self.character:isSprinting() and self.character:isJustMoved() then
            return true;
        end;
        return false;
    end
end