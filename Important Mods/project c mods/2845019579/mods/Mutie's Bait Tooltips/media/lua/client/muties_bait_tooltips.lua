print("[ Loading Mutie's Bait Tooltips ]");

-- Mod Settings
--#region
local OPTIONS = {
    showRelativeStrength = true,
    showChance = false,
}

if ModOptions and ModOptions.getInstance then
    ModOptions:getInstance(OPTIONS, "MutieBaitTooltips", "Mutie's Bait Tooltips");
end

Events.OnGameStart.Add(function()
    print("checkbox1 = ", OPTIONS.showRelativeStrength)
    print("checkbox2 = ", OPTIONS.showChance)
end)

--#endregion

local function findLargestStringSize(_drawfont, _strTable)
    local textWidth = 0;
    for i = 1, #_strTable do
        textWidth = math.max(
            getTextManager():MeasureStringX(_drawfont, _strTable[i]),
            textWidth
        );
    end
    return textWidth;
end

local function lerp(min, max, actual)
    local range = max - min;
    local placeInRange = actual - min;
    return placeInRange / range;
end

local currentItem;
local drawTable;
local strengthTable;
local callback_render = ISToolTipInv.render;
local chances = nil;
---@diagnostic disable-next-line: duplicate-set-field
function ISToolTipInv:render()
    if not ISContextMenu.instance or not ISContextMenu.instance.visibleCheck then
        if not self.item then
            return callback_render(self);
        end

        if getPlayer():getPerkLevel(Perks.Trapping) < SandboxVars.MutieBaitTooltips.RequiredTrappingSkill then
            return callback_render(self);
        end

        if SandboxVars.MutieBaitTooltips.RequiredMagazine and not getPlayer():isRecipeKnown("Trapping Baits") then
            return callback_render(self);
        end

        if OPTIONS.showRelativeStrength and chances == nil then
            chances = {};
            for i = 1, #Animals do
                local baitChances = {};
                for bait, chance in pairs(Animals[i].baits) do
                    table.insert(baitChances, chance);
                end
                table.sort(baitChances);

                local animal = getItemNameFromFullType(Animals[i].item);
                chances[animal] = {}
                chances[animal].bait = {}
                chances[animal].bait.minimum = baitChances[1];
                chances[animal].bait.maximum = baitChances[#baitChances];
            end
        end

        if currentItem ~= self.item then
            currentItem = self.item;
            drawTable = {};
            strengthTable = {};
            local itemName = currentItem:getScriptItem():getFullName();

            if SandboxVars.MutieBaitTooltips.ShowTooltipOnBait then
                for i = 1, #Animals do
                    local baits = Animals[i].baits;
                    local animal = getItemNameFromFullType(Animals[i].item);
                    for bait, chance in pairs(baits) do
                        if bait == itemName then
                            table.insert(drawTable, animal);
                            table.insert(strengthTable, chance);
                            break;
                        end
                    end
                end
            end

            if SandboxVars.MutieBaitTooltips.ShowTooltipOnTrap then
                for i = 1, #Animals do
                    local traps = Animals[i].traps;
                    local animal = getItemNameFromFullType(Animals[i].item);
                    for trap, chance in pairs(traps) do
                        if trap == itemName then
                            table.insert(drawTable, animal);
                            table.insert(strengthTable, chance);
                            break;
                        end
                    end
                end
            end
        end

        if #drawTable > 0 then
            local drawFont = UIFont[getCore():getOptionTooltipFont()];
            if self.x > 1 and self.y > 1 then
                local currentY = self.tooltip:getHeight();
                local yOffsetIncrement = self.tooltip:getLineSpacing();
                self.tooltip:setHeight(currentY + yOffsetIncrement * #drawTable);

                local newHeight = yOffsetIncrement * #drawTable;

                local currentWidth = self.tooltip:getWidth();
                currentWidth = math.max(
                    currentWidth + 11,
                    findLargestStringSize(drawFont, drawTable) + 22
                );
                self.tooltip:setWidth(currentWidth - 11);

                self:drawRect(
                    0, currentY,
                    currentWidth, newHeight,
                    self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b
                );
                self:drawRectBorder(
                    0, currentY,
                    currentWidth, newHeight,
                    self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b
                );
                for i = 1, #drawTable do
                    local text = drawTable[i];
                    if OPTIONS.showChance then
                        text = text .. " " .. strengthTable[i] .. "%";
                    end
                    if OPTIONS.showRelativeStrength then
                        local greenRedAmount
                        if chances[drawTable[i]].bait.maximum == strengthTable[i] then
                            greenRedAmount = 1.0;
                        else
                            greenRedAmount = 
                                lerp(
                                    0,
                                    chances[drawTable[i]].bait.maximum,
                                    strengthTable[i]
                                ) * 0.7;
                        end
                        self.tooltip:DrawText(
                            drawFont, text,
                            5, currentY,
                            1.0 - greenRedAmount, greenRedAmount, 0.0, 1.0
                        );
                        currentY = currentY + yOffsetIncrement;
                    else
                        self.tooltip:DrawText(
                            drawFont, text,
                            5, currentY,
                            1.0, 1.0, 0.8, 1.0
                        );
                        currentY = currentY + yOffsetIncrement;
                    end
                    
                end
            end
        end
    end
    return callback_render(self);
end
