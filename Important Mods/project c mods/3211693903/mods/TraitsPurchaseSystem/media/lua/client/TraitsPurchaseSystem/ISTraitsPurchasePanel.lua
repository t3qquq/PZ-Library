--if not isClient() then return end

-------------------------------------------------------------------------
-- global variables
-- trait blackslist, these cannot be bought or sold
TraitBlackListTable = {"Emaciated", "Very Underweight", "Underweight", "Overweight", "Obese", "Fit", "Athletic", "Stout", "Strong"}
extraTraitBlockList = nil;
extraTraitBlockListTable = nil;

-------------------------------------------------------------------------
-- local variables
local currentTraitsSize = 0;

local Colors = {
    good={r=0, g=1, b=0},
    bad={r=1, g=0,b=0},
    free={r=1, g=1, b=1},
    profession={r=0, g=1, b=1}
}

-------------------------------------------------------------------------
-- local functions

local function split(inSplitPattern, text)
    outResults = {};
    local theStart = 1;
    local theSplitStart, theSplitEnd = string.find(text, inSplitPattern, theStart)
    while theSplitStart do
        table.insert(outResults, string.sub(text, theStart, theSplitStart - 1))
        theStart = theSplitEnd + 1;
        theSplitStart, theSplitEnd = string.find(text, inSplitPattern, theStart)
    end
    table.insert(outResults, string.sub(text, theStart))
    return outResults
end

--[[ isBlackListedTrait(trait)
    checks if our trait (string) is in the TraitBlackListTable
    returns true/false
]]
local function isBlackListedTrait(trait)
    if (extraTraitBlockList ~= SandboxVars.TraitsPurchaseSystem.TraitsBlockList) then
        extraTraitBlockList = SandboxVars.TraitsPurchaseSystem.TraitsBlockList;
        extraTraitBlockListTable = nil;
        if (extraTraitBlockList ~= nil) then
            extraTraitBlockList = string.gsub(extraTraitBlockList, '"', '');
            if string.match(extraTraitBlockList, ",") then
                extraTraitBlockListTable = {};
                extraTraitBlockListTable = split(",", extraTraitBlockList);
            end
        end
    end

    for _, name in ipairs(TraitBlackListTable) do
        if trait == name then return true end
    end
    if (extraTraitBlockListTable ~= nil) then
        for _, name in ipairs(extraTraitBlockListTable) do
            if trait == name then return true end
        end
    else
        if (extraTraitBlockList ~= nil) then
            if trait == extraTraitBlockList then return true end
        end
    end
    return false
end

--[[ sortByCost(a, b)
    Sort function, used to sort the trait listboxes by cost. 
    Note uses the original trait cost, not the new modified one.
]]
local function sortByCost(a, b)
    if a.item:getCost() == b.item:getCost() then
        return not string.sort(a.text, b.text)
    end
    return a.item:getCost() < b.item:getCost()
end

local function smoker(self, button)
    return
end

--[[ onRemoveBadTrait(self, button, trait, cost)
    Callback function for the popup confirmation box.
]]
local function onRemoveBadTrait(self, button, trait, cost)
    if button.internal ~= "YES" then return end
    local player = self.player;
    
    local points = { player:getPerkLevel(Perks.Traits), player:getPerkLevel(Perks.Traits2), player:getPerkLevel(Perks.Traits3) };
    local total_points = points[1] + points[2] + points[3];
    -- check here, player still has points, still has trait
    if not player:HasTrait(trait:getType()) or total_points < cost then return end
    
    if (trait:getLabel() == Translator.getText("UI_trait_Smoker")) then
        local stress = player:getStats():getStress();
        if (stress > 0.24) then
            local width = 250.0F;
            local height = 150.0F;
            local x = self.owner:getX() + (self.owner:getWidth() / 2.0F) - (width / 2.0F);
            local y = self.owner:getY() + (self.owner:getHeight() / 2.0F) - (height / 2.0F);
            local modal = ISModalDialog:new(x, y, width, height, "Forget to take your last drag ?", false, nil, smoker, player:getPlayerNum(), item, cost);
            modal:initialise();
            modal:addToUIManager();
            modal:bringToTop();
            if (JoypadState.players[self.playerNum + 1]) then
                modal.prevFocus = JoypadState.players[self.playerNum + 1].focus;
                setJoypadFocus(self.playerNum, modal);
            end
            return
        end
    end
    

    local xp = player:getXp();
    local xp_points = { xp:getXP(Perks.Traits), xp:getXP(Perks.Traits2), xp:getXP(Perks.Traits3) };
    local xp_total = { 0, 0, 0 };

    if total_points > 0 then
        xp_total[1] = Perks.Traits:getTotalXpForLevel(points[1]);
        xp_total[2] = Perks.Traits:getTotalXpForLevel(points[2]);
        xp_total[3] = Perks.Traits:getTotalXpForLevel(points[3]);
    end
    xp_points[1] = xp_points[1] - xp_total[1];
    xp_points[2] = xp_points[2] - xp_total[2];
    xp_points[3] = xp_points[3] - xp_total[3];

    for i=1, cost do
        if (player:getPerkLevel(Perks.Traits3) > 0) then 
            player:LoseLevel(Perks.Traits3);
        elseif (player:getPerkLevel(Perks.Traits2) > 0) then
            player:LoseLevel(Perks.Traits2);
        else
            player:LoseLevel(Perks.Traits);
        end
    end
    xp:setXPToLevel(Perks.Traits, player:getPerkLevel(Perks.Traits));
    xp:setXPToLevel(Perks.Traits2, player:getPerkLevel(Perks.Traits2));
    xp:setXPToLevel(Perks.Traits3, player:getPerkLevel(Perks.Traits3));

    for i=1, (xp_points[3] + xp_points[2] + xp_points[1]) do
        if (player:getPerkLevel(Perks.Traits) == 10 and player:getPerkLevel(Perks.Traits2) == 10) then
            xp:AddXP(Perks.Traits3, 1, false, false, true);
        elseif (player:getPerkLevel(Perks.Traits) == 10) then
            xp:AddXP(Perks.Traits2, 1, false, false, true);
        else
            xp:AddXP(Perks.Traits, 1, false, false, true);
        end
    end

    local map = trait:getXPBoostMap()
    map = transformIntoKahluaTable(map)
    for perk, value in pairs(map) do
        if (perk == Perks.Traits or perk == Perks.Traits2 or perk == Perks.Traits3) then
            break;
        end
        value = tonumber(tostring(value)) -- for loop chokes using value as a integer (its a double..)
        if value < 0 then
            value = value * -1
            for i=1, tonumber(tostring(value)) do
                if player:getPerkLevel(perk) == 10 then break end
                player:LevelPerk(perk)
                luautils.updatePerksXp(perk, player)
            end
        end
    end

    player:getTraits():remove(trait:getType())
    self:updateList()
end

--[[ onAddGoodTrait(self, button, trait, cost)
    Callback function for the popup confirmation box.
]]
local function onAddGoodTrait(self, button, trait, cost)
    if button.internal ~= "YES" then return end
    local player = self.player;
    local points = { player:getPerkLevel(Perks.Traits), player:getPerkLevel(Perks.Traits2), player:getPerkLevel(Perks.Traits3) };
    local total_points = points[1] + points[2] + points[3];
    -- check here, player still has points, still has trait
    if player:HasTrait(trait:getType()) or total_points < cost then return end
    
    local xp = player:getXp();
    local xp_points = { xp:getXP(Perks.Traits), xp:getXP(Perks.Traits2), xp:getXP(Perks.Traits3) };
    local xp_total = { 0, 0, 0 };

    if total_points > 0 then
        xp_total[1] = Perks.Traits:getTotalXpForLevel(points[1]);
        xp_total[2] = Perks.Traits:getTotalXpForLevel(points[2]);
        xp_total[3] = Perks.Traits:getTotalXpForLevel(points[3]);
    end
    xp_points[1] = xp_points[1] - xp_total[1];
    xp_points[2] = xp_points[2] - xp_total[2];
    xp_points[3] = xp_points[3] - xp_total[3];

    for i=1, cost do
        if (player:getPerkLevel(Perks.Traits3) > 0) then 
            player:LoseLevel(Perks.Traits3);
        elseif (player:getPerkLevel(Perks.Traits2) > 0) then
            player:LoseLevel(Perks.Traits2);
        else
            player:LoseLevel(Perks.Traits);
        end
    end
    xp:setXPToLevel(Perks.Traits, player:getPerkLevel(Perks.Traits));
    xp:setXPToLevel(Perks.Traits2, player:getPerkLevel(Perks.Traits2));
    xp:setXPToLevel(Perks.Traits3, player:getPerkLevel(Perks.Traits3));

    for i=1, (xp_points[3] + xp_points[2] + xp_points[1]) do
        if (player:getPerkLevel(Perks.Traits) == 10 and player:getPerkLevel(Perks.Traits2) == 10) then
            xp:AddXP(Perks.Traits3, 1, false, false, true);
        elseif (player:getPerkLevel(Perks.Traits) == 10) then
            xp:AddXP(Perks.Traits2, 1, false, false, true);
        else
            xp:AddXP(Perks.Traits, 1, false, false, true);
        end
    end

    local map = trait:getXPBoostMap()
    map = transformIntoKahluaTable(map)
    for perk, value in pairs(map) do
        for i=1, tonumber(tostring(value)) do -- for loop chokes using value as a integer (its a double..)
            if player:getPerkLevel(perk) == 10 then break end
            player:LevelPerk(perk)
            luautils.updatePerksXp(perk, player)
        end
    end

    for i=0, trait:getFreeRecipes():size()-1 do
        local r = trait:getFreeRecipes():get(i)
        if not player:getKnownRecipes():contains(r) then
            player:getKnownRecipes():add(r)
        end
    end

    player:getTraits():add(trait:getType())
    self:updateList()
end

--------------------------------------------------------------------------
--[[ ISTraitsPurchasePanel
    Class for the Purchase tab
]]

ISTraitsPurchasePanel = ISPanelJoypad:derive("ISTraitsPurchasePanel")

--[[ ISTraitsPurchasePanel:drawTraitMap(y, item, alt)
    This function is mostly copied from the initial character creation, but cleaned up a bit
    and modified to suit our purposes
]]
function ISTraitsPurchasePanel:drawTraitMap(y, item, alt)
    self:drawRectBorder(0, (y), self:getWidth(), self.itemheight - 1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b)
    local trait = item.item
    
    -- if we selected an item, we display a grey rect over it
    local isMouseOver = self.mouseoverselected == item.index and not self:isMouseOverScrollBar()
    if (self.selected == item.index) then
        self:drawRect(0, (y), self:getWidth(), self.itemheight - 1, 0.3, 0.7, 0.35, 0.15)
    elseif (isMouseOver) then
        self:drawRect(1, y + 1, self:getWidth() - 2, item.height - 4, 0.95, 0.1, 0.1, 0.1)
    end

    local cost = trait:getCost()
    local isProfession = false;
    if (cost <= 0) then
        if (trait:getLabel() == Translator.getText("UI_prof_Burglar")) then
            isProfession = true;
            cost = 6;
        elseif (trait:getLabel() == Translator.getText("UI_trait_Mechanics")) then
            isProfession = true;
            cost = 5;
        elseif (trait:getLabel() == Translator.getText("UI_trait_axeman")) then
            isProfession = true;
            cost = 2;
        elseif (trait:getLabel() == Translator.getText("UI_trait_Cook")) then
            isProfession = true;
            cost = 4;
        elseif (trait:getLabel() == Translator.getText("UI_trait_Desensitized")) then
            isProfession = true;
            cost = 6;
        elseif (trait:getLabel() == Translator.getText("UI_trait_marksman")) then
            isProfession = true;
            cost = 2;
        elseif (trait:getLabel() == Translator.getText("UI_trait_nightowl")) then
            isProfession = true;
            cost = 6;
        end
    end
    local tex = trait:getTexture()
    local w = 16
    if (tex) then
        self:drawTexture(tex, 16-2, y + (self.itemheight - tex:getHeight()) / 2, 1, 1, 1, 1);
        w = tex:getWidth() + 20
    end

    local col = Colors.free
    if (cost > 0) then
        if (isProfession) then
            col = Colors.profession;
        else
            col = Colors.good
        end
    elseif (cost < 0) then
        col = Colors.bad
    end

    local dy = (self.itemheight - self.owner.fontHgt) / 2
    self:drawText(trait:getLabel(), w, y + dy, col.r, col.g, col.b, 0.9, UIFont.Small);

    -- need to figure out what listbox we're drawing in so we can properly set the cost.
    if self == self.owner.listboxTraitsPurchaseable then -- buyable good traits
        cost = cost
    elseif self == self.owner.listboxTraitsOwned then -- sellable bad traits
        cost = cost * -1
    end
    -- blacklisted traits set to 0
    if isBlackListedTrait(item.item:getType()) then cost = 0 end
    
    if cost > 0 then -- costs something, so print the cost
        -- normally we'd use trait:getRightLabel(), but we modified the cost
        self:drawTextRight(tostring(cost), self:getWidth() - 20, y + 8, col.r, col.g, col.b, 0.9, UIFont.Small);
    end
    self.itemheightoverride[trait:getLabel()] = self.itemheight
    y = y + self.itemheightoverride[trait:getLabel()]
    return y
end

--[[ ISTraitsPurchasePanel:updateList()
    Updates the listboxes of traits.
]]
function ISTraitsPurchasePanel:updateList()
    local TraitsPurchaseableSelection = self.listboxTraitsPurchaseable.selected;
    local TraitsOwnedSelection = self.listboxTraitsOwned.selected;
    local traits = TraitFactory.getTraits();
    local player = self.player;
    local playerTraits = player:getTraits();
    self.listboxTraitsOwned:clear();
    self.listboxTraitsPurchaseable:clear();
    
    for i=0, playerTraits:size()-1 do
        --print(playerTraits:get(i))
        local trait = TraitFactory.getTrait(playerTraits:get(i))
        if (trait ~= nil) then 
            local newItem = self.listboxTraitsOwned:addItem(trait:getLabel(), trait, 4)
            newItem.tooltip = trait:getType() .. "\r\n" .. trait:getDescription()
        end
    end
    for i=0, traits:size() -1 do repeat
        local trait = traits:get(i)
        local cost = trait:getCost()
        if (cost <= 0) then
            if (trait:getLabel() == Translator.getText("UI_prof_Burglar")) then
                if (player:HasTrait("NightVision") and player:HasTrait("Graceful")) then
                    cost = 11;
                end
            elseif (trait:getLabel() == Translator.getText("UI_trait_Mechanics")) then
                if (player:HasTrait("Mechanics")) then
                    cost = 11;
                end
            elseif (trait:getLabel() == Translator.getText("UI_trait_axeman")) then
                if (player:HasTrait("Brawler")) then
                    cost = 11;
                end
            elseif (trait:getLabel() == Translator.getText("UI_trait_Cook")) then
                if (player:HasTrait("Cook")) then
                    cost = 11;
                end
            elseif (trait:getLabel() == Translator.getText("UI_trait_Desensitized")) then
                if (player:HasTrait("Brave")) then
                    cost = 11;
                end
            elseif (trait:getLabel() == Translator.getText("UI_trait_marksman")) then
                if (player:HasTrait("Hunter")) then
                    cost = 11;
                end
            elseif (trait:getLabel() == Translator.getText("UI_trait_nightowl")) then
                if (player:HasTrait("NeedsLessSleep")) then
                    cost = 11;
                end
            end
        end
        if cost <= 0 then break end -- no buying negative or free traits
        if player:HasTrait(trait:getType()) then break end
        local exclude = trait:getMutuallyExclusiveTraits()
        local isOK = true
        for i2=0, exclude:size() -1 do
            if player:HasTrait(exclude:get(i2)) then
                if (exclude:get(i2) == "Brave") then
                    break;
                elseif (exclude:get(i2) == "Cook") then
                    break;
                elseif (exclude:get(i2) == "Mechanics") then
                    break;
                else
                    -- we cant buy this trait, we have a conflicting one
                    isOK = false;
                    break
                end
            end
        end
        if not isOK then break end
        local newItem = self.listboxTraitsPurchaseable:addItem(trait:getLabel(), trait);
        newItem.tooltip = trait:getType() .. "\r\n" .. trait:getDescription()
    until true end
    table.sort(self.listboxTraitsPurchaseable.items, sortByCost)
    table.sort(self.listboxTraitsOwned.items, sortByCost)
    self.listboxTraitsPurchaseable.selected = TraitsPurchaseableSelection;
    self.listboxTraitsOwned.selected = TraitsOwnedSelection;
end

function ISTraitsPurchasePanel:onClickTrait(listbox, item)
    if (listbox == self.listboxTraitsOwned) then
        self.selectedList = self.listboxTraitsOwned;
        self.listboxTraitsPurchaseable.selected = -1;
    else
        self.selectedList = self.listboxTraitsPurchaseable;
        self.listboxTraitsOwned.selected = -1;
    end
end

function ISTraitsPurchasePanel:onDblClickOwnedTrait(item)
    if not item then return end
    if isBlackListedTrait(item:getType()) then return end
    local player = self.player;
    local cost = item:getCost() * -1
    if (cost <= 0) then
        if (item:getLabel() == Translator.getText("UI_prof_Burglar")) then cost = -6;
        elseif (item:getLabel() == Translator.getText("UI_trait_Mechanics")) then cost = -5;
        elseif (item:getLabel() == Translator.getText("UI_trait_axeman")) then cost = -2;
        elseif (item:getLabel() == Translator.getText("UI_trait_Cook")) then cost = -4;
        elseif (item:getLabel() == Translator.getText("UI_trait_Desensitized")) then cost = -6;
        elseif (item:getLabel() == Translator.getText("UI_trait_marksman")) then cost = -2;
        elseif (item:getLabel() == Translator.getText("UI_trait_nightowl")) then cost = -6; end
    end
    if (cost > 0 and (player:getPerkLevel(Perks.Traits) >= cost or player:getPerkLevel(Perks.Traits) == 10)) then
        -- TODO: add translation
        -- IGUI_BuyableTraits_remove = "Remove %1 for %2 points?"
        --local modal = ISModalDialog:new(48, 48, 250, 150, getText("IGUI_BuyableTraits_add", item:getLabel(), cost), true, nil, ISTraitsPurchasePanel.onAddGoodTrait, player:getPlayerNum(), item, cost)
        local width = 250.0F;
        local height = 150.0F;
        local x = self.owner:getX() + (self.owner:getWidth() / 2.0F) - (width / 2.0F);
        local y = self.owner:getY() + (self.owner:getHeight() / 2.0F) - (height / 2.0F);
        local modal = ISModalDialog:new(x, y, width, height, "Remove ".. item:getLabel() .. " for "..cost.. " points?", true, self, onRemoveBadTrait, player:getPlayerNum(), item, cost);
        modal:initialise();
        modal:addToUIManager();
        modal:bringToTop();
        if (JoypadState.players[self.playerNum + 1]) then
            modal.prevFocus = JoypadState.players[self.playerNum + 1].focus;
            setJoypadFocus(self.playerNum, modal);
        end
    end
end

function ISTraitsPurchasePanel:onDblClickPurchaseableTrait(item)
    if not item then return end
    if isBlackListedTrait(item:getType()) then return end
    local player = self.player;
    local cost = item:getCost()
    if (cost <= 0) then
        if (item:getLabel() == Translator.getText("UI_prof_Burglar")) then cost = 6;
        elseif (item:getLabel() == Translator.getText("UI_trait_Mechanics")) then cost = 5;
        elseif (item:getLabel() == Translator.getText("UI_trait_axeman")) then cost = 2;
        elseif (item:getLabel() == Translator.getText("UI_trait_Cook")) then cost = 4;
        elseif (item:getLabel() == Translator.getText("UI_trait_Desensitized")) then cost = 6;
        elseif (item:getLabel() == Translator.getText("UI_trait_marksman")) then cost = 2;
        elseif (item:getLabel() == Translator.getText("UI_trait_nightowl")) then cost = 6; end
    end
    if (cost > 0 and (player:getPerkLevel(Perks.Traits) >= cost or player:getPerkLevel(Perks.Traits) == 10)) then
        -- TODO: add translation
        -- IGUI_BuyableTraits_add = "Add %1 for %2 points?"
        --local modal = ISModalDialog:new(48, 48, 250, 150, getText("IGUI_BuyableTraits_add", item:getLabel(), cost), true, nil, ISTraitsPurchasePanel.onAddGoodTrait, player:getPlayerNum(), item, cost)
        local width = 250.0F;
        local height = 150.0F;
        local x = self.owner:getX() + (self.owner:getWidth() / 2.0F) - (width / 2.0F);
        local y = self.owner:getY() + (self.owner:getHeight() / 2.0F) - (height / 2.0F);
        local modal = ISModalDialog:new(x, y, width, height, "Add ".. item:getLabel() .. " for "..cost.. " points?", true, self, onAddGoodTrait, player:getPlayerNum(), item, cost);
        modal:initialise();
        modal:addToUIManager();
        modal:bringToTop();
        if (JoypadState.players[self.playerNum + 1]) then
            modal.prevFocus = JoypadState.players[self.playerNum + 1].focus;
            setJoypadFocus(self.playerNum, modal);
        end
    end
end

function ISTraitsPurchasePanel:initialise()
    ISPanelJoypad.initialise(self);
end

function ISTraitsPurchasePanel:onGainJoypadFocus(joypadData)
	ISPanelJoypad.onGainJoypadFocus(self, joypadData);
end

function ISTraitsPurchasePanel:onLoseJoypadFocus(joypadData)
	ISPanelJoypad.onLoseJoypadFocus(self, joypadData);
end

function ISTraitsPurchasePanel:onJoypadDown(button)
	if (button == Joypad.AButton) then
        local item = self.selectedList.items[self.selectedList.selected].item;
        if (self.selectedList == self.listboxTraitsOwned) then
            self:onDblClickOwnedTrait(item);
        else
            self:onDblClickPurchaseableTrait(item);
        end
    end
	if (button == Joypad.BButton) then
        self.owner:close();
	end
    if (button == Joypad.LBumper) then
        if not (self.selectedList == self.listboxTraitsOwned) then
            getSoundManager():playUISound("UISelectListItem");
            self.selectedList = self.listboxTraitsOwned;
            self.listboxTraitsPurchaseable.selected = -1;
        end
        if (self.selectedList.selected == -1) then self.selectedList.selected = 1; end
    end
    if (button == Joypad.RBumper) then
        if not (self.selectedList == self.listboxTraitsPurchaseable) then
            getSoundManager():playUISound("UISelectListItem");
            self.selectedList = self.listboxTraitsPurchaseable;
            self.listboxTraitsOwned.selected = -1;
        end
        if (self.selectedList.selected == -1) then self.selectedList.selected = 1; end
    end
end

function ISTraitsPurchasePanel:onJoypadDirUp()
    self.selectedList:onJoypadDirUp();
end

function ISTraitsPurchasePanel:onJoypadDirDown()
    self.selectedList:onJoypadDirDown();
end

function ISTraitsPurchasePanel:createChildren()
    self.tablePadX = 20
    self.tableWidth = 256
    self.tableHeight = 256
    self.topOfLists = 48
    self.traitButtonHgt = 25
    self.traitButtonPad = 6

    self.smallFontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight() + 1
    self.mediumFontHgt = getTextManager():getFontFromEnum(UIFont.Medium):getLineHeight()
    
    self.listboxTraitsOwned = ISScrollingListBox:new(8, self.topOfLists + self.smallFontHgt, self.tableWidth, self.tableHeight);
    self.listboxTraitsOwned:initialise();
    self.listboxTraitsOwned:instantiate();
    self.listboxTraitsOwned:setAnchorLeft(true);
    self.listboxTraitsOwned:setAnchorRight(true);
    self.listboxTraitsOwned:setAnchorTop(true);
    self.listboxTraitsOwned:setAnchorBottom(true);
    self.listboxTraitsOwned.itemheight = 30;
    self.listboxTraitsOwned.selected = 1;
    self.listboxTraitsOwned.doDrawItem = self.drawTraitMap;
    self.listboxTraitsOwned:setOnMouseDownFunction(self.listboxTraitsOwned, function(item) self:onClickTrait(self.listboxTraitsOwned, item) end)
    self.listboxTraitsOwned:setOnMouseDoubleClick(self, self.onDblClickOwnedTrait);
    self.listboxTraitsOwned.owner = self;
    self.listboxTraitsOwned.drawBorder = true;
    self.listboxTraitsOwned.owner = self;
    self:addChild(self.listboxTraitsOwned);
    
    self.selectedList = self.listboxTraitsOwned;

    
    -- the traits list choice
    self.listboxTraitsPurchaseable = ISScrollingListBox:new(8 + self.tablePadX + self.tableWidth, self.topOfLists + self.smallFontHgt, self.tableWidth, self.tableHeight);
    self.listboxTraitsPurchaseable:initialise();
    self.listboxTraitsPurchaseable:instantiate();
    self.listboxTraitsPurchaseable:setAnchorLeft(true);
    self.listboxTraitsPurchaseable:setAnchorRight(true);
    self.listboxTraitsPurchaseable:setAnchorTop(true);
    self.listboxTraitsPurchaseable:setAnchorBottom(true);
    self.listboxTraitsPurchaseable.itemheight = 30;
    self.listboxTraitsPurchaseable.selected = -1;
    self.listboxTraitsPurchaseable.doDrawItem = self.drawTraitMap;
    self.listboxTraitsPurchaseable:setOnMouseDownFunction(self.listboxTraitsPurchaseable, function(item) self:onClickTrait(self.listboxTraitsPurchaseable, item) end)
    self.listboxTraitsPurchaseable:setOnMouseDoubleClick(self, self.onDblClickPurchaseableTrait);
    self.listboxTraitsPurchaseable.owner = self;
    self.listboxTraitsPurchaseable.drawBorder = true;
    self.listboxTraitsPurchaseable.owner = self;
    self:addChild(self.listboxTraitsPurchaseable);

    self:updateList()
end

function ISTraitsPurchasePanel:prerender()
    ISPanelJoypad.prerender(self)
    
    self.listboxTraitsPurchaseable:setWidth(self.tableWidth)
    self.listboxTraitsOwned:setWidth(self.tableWidth);
    self.listboxTraitsPurchaseable:setHeight(self.tableHeight);
    self.listboxTraitsOwned:setHeight(self.tableHeight);
end

--[[ ISTraitsPurchasePanel:render()
    Apparently, we need to draw text here, there is a ISLabel class, but theres not much point using it.
    We also need to reset our width and height (as well as our parents) or our listboxes go right off
]]
function ISTraitsPurchasePanel:render()
    self:drawText(getText("UI_characreation_choosentraits"), self.x + 8, 32, 1, 1, 1, 1, UIFont.Medium);
    self:drawText(getText("UI_characreation_availabletraits"), self.x + 8+self.tablePadX + self.tableWidth, 32, 1, 1, 1, 1, UIFont.Medium);
    local ptstring = getText("IGUI_PlayerStats_AvailableSkillPt")..(self.player:getPerkLevel(Perks.Traits) + self.player:getPerkLevel(Perks.Traits2) + self.player:getPerkLevel(Perks.Traits3));
    self:drawText(ptstring, self.x + 8 + self.tablePadX + self.tableWidth, self.topOfLists + self.smallFontHgt + self.tableHeight + 8, 1, 1, 1, 1, UIFont.Medium);
    self:setWidthAndParentWidth(16 + self.tablePadX + (self.tableWidth*2));
    self:setHeightAndParentHeight(self.topOfLists * 2 + self.smallFontHgt + self.tableHeight);
end

function ISTraitsPurchasePanel:update()
    ISPanelJoypad.update(self);

    --[[ playerTraitsSize self.player:getTraits() never updates, so we can't check against it changing even though it does.
        Size is used here because Apparently, we need to draw text here, there is a ISLabel class, but theres not much point using it.
        This is to ensure that if some external change happens and a trait is added or subtracted we update the list.
    ]]
    local playerTraitsSize = self.player:getTraits():size();
    if (currentTraitsSize ~= playerTraitsSize) then
        currentTraitsSize = playerTraitsSize;
        self:updateList();
    end
end

function ISTraitsPurchasePanel:new(x, y, width, height, player, playerNum, owner)
    local instance = ISPanelJoypad:new(x, y, width, height);
    setmetatable(instance, self);
    self.__index = self;
    
    instance.player = player;
    instance.playerNum = playerNum;
    instance:noBackground();
    instance.txtLen = 0;

    instance.fontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight();
    instance.smallFontHgt = getTextManager():getFontFromEnum(UIFont.Small):getLineHeight() + 1;
    instance.mediumFontHgt = getTextManager():getFontFromEnum(UIFont.Medium):getLineHeight();
    
    instance.owner = owner;
    instance.selectedList = nil;

    return instance;
end

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISTraitsPurchasePanel.lua