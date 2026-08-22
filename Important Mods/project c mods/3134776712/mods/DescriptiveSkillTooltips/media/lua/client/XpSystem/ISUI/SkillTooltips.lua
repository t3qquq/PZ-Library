require "ISUI/ISSkillProgressBar"
require "ISUI/SkillTooltipDefinitions"
require "ISUI/ISPanel"
ISSkillProgressBar = ISSkillProgressBar or {}
DST = DST or {}
DST.SkillTooltipDefinitions = DST.SkillTooltipDefinitions or {}


function ISSkillProgressBar:render()
	self:renderPerkRect();
	if self.message ~= nil then
		if self.tooltip == nil then
			self.tooltip = ISToolTip:new();
			self.tooltip:initialise();
			self.tooltip:addToUIManager();
			self.tooltip:setOwner(self)
            self.tooltip.maxLineWidth = 600;
		end
		self.tooltip.description = self.message;
	end
end

DST.old_updateTooltip = ISSkillProgressBar.updateTooltip


-- function ISSkillProgressBar:vanilla_updateTooltip(lvlSelected)
-- 	-- we display the correct message
-- 	self.message = self.perk:getName() .. xpSystemText.lvl .. lvlSelected + 1;
-- 	-- first if the lvl is unlocked
-- 	if lvlSelected < self.level then
-- 		self.message = self.message .. " <LINE> " .. xpSystemText.unlocked;
-- 	-- if we selected the level wich is in progress, we display the xp we already got / xp needed for lvl
-- 	elseif self.level == lvlSelected then
-- 		self.message = self.message .. " <LINE> " .. getText("IGUI_XP_tooltipxp", round(self.xp, 2), self.xpForLvl);
-- 		-- if we got a multiplier, we show it
-- 		local multiplier = self.char:getXp():getMultiplier(self.perk:getType());
-- 		if multiplier > 0 then
-- 			self.message = self.message .. " <LINE> " .. getText("IGUI_skills_Multiplier", round(multiplier, 2));
-- 		end
-- 	else
-- 		self.message = self.message .. " <LINE> " .. xpSystemText.locked;
--     end
--     local xpBoost = self.char:getXp():getPerkBoost(self.perk:getType());
--     local percentage = nil;
--     if xpBoost == 1 then
--         percentage = "75%";
--     elseif xpBoost == 2 then
--         percentage = "100%";
--     elseif xpBoost == 3 then
--         percentage = "125%";
--     end

--     if percentage then
--         self.message = self.message .. " <LINE> " .. getText("IGUI_XP_tooltipxpboost", percentage);
--     end
-- end


function ISSkillProgressBar:DST_updateTooltip(lvlSelected)
    if DST.SkillTooltipDefinitions then
        if DST.SkillTooltipDefinitions[self.perk:getType():toString()] then
            
            local lvl = lvlSelected + 1
            if DST.SkillTooltipDefinitions[self.perk:getType():toString()]["level_"..(lvl)] then
                self.message = self.message .. " <LINE> ";
                -- self.message = self.message .. " <LINE> " .. "--- Benefits ---";
                local extraTooltips = DST.SkillTooltipDefinitions[self.perk:getType():toString()]["level_"..(lvl)]
                for k, v in ipairs(extraTooltips) do
                    self.message = self.message .. " <LINE> " .. extraTooltips[k];
                end

                if self.perk:getType():toString() == "Fishing" then
                    local catchChanceForSpear = DST.round((20 + (lvl*1.5))/(DST.AttractFishNumber+10))
                    local catchChanceForRodWithLure  = DST.round((10 + (lvl*2.5))/(DST.AttractFishNumber))
                    local catchChanceForRodWithBait  = DST.round((20 + (lvl*1.5))/(DST.AttractFishNumber))
                    self.message = self.message .. " <LINE> "..catchChanceForSpear.."% "..getText("IGUI_skilltooltip_chanceofcatchingfishwithspears");
                    self.message = self.message .. " <LINE> "..catchChanceForRodWithBait.."% "..getText("IGUI_skilltooltip_chanceofcatchingfishwithfishingrodsandbait");
                    self.message = self.message .. " <LINE> "..catchChanceForRodWithLure.."% "..getText("IGUI_skilltooltip_chanceofcatchingfishwithfishingrodsandlure");
                    
                    if lvl >= 3 then
                        self.message = self.message .. " <LINE><LINE> "..getText("IGUI_skilltooltip_doesntgetboredfromfishing");
                    end
                end
            end
        end
    end 
end

function ISSkillProgressBar:updateTooltip(lvlSelected)
    DST.old_updateTooltip(self, lvlSelected)
    self:DST_updateTooltip(lvlSelected)
end
