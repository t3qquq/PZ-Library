--if not isClient() then return end

-------------------------------------------------------------------------
-- local variables
local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small);
local UI_BORDER_SPACING = 10;
local BUTTON_HGT = FONT_HGT_SMALL + 6
local currentTraitsSize = 0;
local currentTraitPoints = 0;

local Colors = {
    good       = { r=0, g=1, b=0 },
    bad        = { r=1, g=0, b=0 },
    free       = { r=1, g=1, b=1 },
    profession = { r=0, g=1, b=1 }
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
    checks if our trait (string) is in the TPS.Blocklist
    returns true/false
]]
local function isBlackListedTrait(trait)
    if (extraTraitBlockList ~= SandboxVars.TraitsPurchaseSystem.TraitsBlockList) then
        extraTraitBlockList = SandboxVars.TraitsPurchaseSystem.TraitsBlockList;
        TPS.ExtraBlocklist = nil;
        if (extraTraitBlockList ~= nil) then
            extraTraitBlockList = string.gsub(extraTraitBlockList, '"', '');
            if string.match(extraTraitBlockList, ",") then
                TPS.ExtraBlocklist = {};
                TPS.ExtraBlocklist = split(",", extraTraitBlockList);
            end
        end
    end

    for i=1, #TPS.Blocklist do
        local blackListedTrait = TPS.Blocklist[i];
        if blackListedTrait then
            if (trait == blackListedTrait) then
                return true;
            end
        end
    end
    if (TPS.ExtraBlocklist ~= nil) then
        for _, name in ipairs(TPS.ExtraBlocklist) do
            if (trait:toString() == name) then
                return true;
            end
        end
    else
        if (extraTraitBlockList ~= nil) then
            if (trait:toString() == extraTraitBlockList) then
                return true;
            end
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
    local traitpoints = getCurrentTraitPoints(player);
    -- check here, player still has points, still has trait
    if (not player:hasTrait(trait:getType()) or traitpoints < cost) then
        return
    end
    
    --[[
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
    ]]

    if (isClient()) then
        local args = { TPStrait = trait:getLabel(), TPStraitString = trait:getType():toString(), TPScost = cost }
        sendClientCommand(getPlayer(), 'TraitsPurchaseSystem', 'TPSRemoveTrait', args);
    else
        spendTraitPoints(player, cost);
        player:getCharacterTraits():remove(trait:getType());
        playTraitRemoved();
    end
    self:updateList();
end

--[[ onAddGoodTrait(self, button, trait, cost)
    Callback function for the popup confirmation box.
]]
local function onAddGoodTrait(self, button, trait, cost)
    if button.internal ~= "YES" then return end
    local player = self.player;
    local traitpoints = getCurrentTraitPoints(player);
    -- check here, player still has points, still has trait
    if (player:hasTrait(trait:getType()) or traitpoints < cost) then
        return
    end

    
    if (isClient()) then
        local args = { TPStrait = trait:getLabel(), TPStraitString = trait:getType():toString(), TPScost = cost }
        sendClientCommand(getPlayer(), 'TraitsPurchaseSystem', 'TPSAddTrait', args);
    else
        spendTraitPoints(player, cost);
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
        playTraitAdded();
    end
    self:updateList();
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
    self:drawRectBorder(0, y, self:getWidth(), self.itemheight - 1, 0.5, self.borderColor.r, self.borderColor.g, self.borderColor.b)
    local trait = item.item
    
    -- if we selected an item, we display a grey rect over it
    local isMouseOver = self.mouseoverselected == item.index and not self:isMouseOverScrollBar()
    if (self.selected == item.index) then
        self:drawRect(0, y, self:getWidth(), self.itemheight - 1, 0.3, 0.7, 0.35, 0.15)
    elseif (isMouseOver) then
        self:drawRect(1, y + 1, self:getWidth() - 2, item.height - 4, 0.95, 0.1, 0.1, 0.1)
    end

    local cost = trait:getCost()
    local isProfession = false;
    if (cost <= 0) then
        if (trait:getType() == CharacterTrait.BURGLAR) then
            isProfession = true;
            cost = 6;
        elseif (trait:getType() == CharacterTrait.MECHANICS) then
            isProfession = true;
            cost = 5;
        elseif (trait:getType() == CharacterTrait.AXEMAN) then
            isProfession = true;
            cost = 2;
        elseif (trait:getType() == CharacterTrait.COOK) then
            isProfession = true;
            cost = 4;
        elseif (trait:getType() == CharacterTrait.DESENSITIZED) then
            isProfession = true;
            cost = 6;
        elseif (trait:getType() == CharacterTrait.MARKSMAN) then
            isProfession = true;
            cost = 2;
        elseif (trait:getType() == CharacterTrait.NIGHT_OWL) then
            isProfession = true;
            cost = 6;
        end
    end
    local texture = trait:getTexture();
    local offsetX = 16;
    if (texture) then
        self:drawTexture(texture, offsetX - 2, y + (self.itemheight - texture:getHeight()) / 2, 1, 1, 1, 1);
        offsetX = texture:getWidth() + 40;
    end

    local color = Colors.free
    if (cost > 0) then
        if (isProfession) then
            color = Colors.profession;
        else
            color = Colors.good
        end
    elseif (cost < 0) then
        color = Colors.bad
    end

    local dy = (self.itemheight - FONT_HGT_SMALL) / 2.0F;
    -- trait label
    self:drawText(trait:getLabel(), offsetX, y + dy, color.r, color.g, color.b, 0.9, UIFont.Small);

    if (self == self.owner.listboxTraitsPurchaseable) then -- buyable good traits
        cost = cost;
    elseif (self == self.owner.listboxTraitsOwned) then -- sellable bad traits
        cost = cost * -1
    end
    -- blacklisted traits set to 0
    if (isBlackListedTrait(item.item:getType())) then
        cost = 0;
    end
    
    -- cost label
    if (cost > 0) then
        self:drawTextRight(tostring(cost), self:getWidth() - 40, y + dy, color.r, color.g, color.b, 0.9, UIFont.Small);
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
    local traits = CharacterTraitDefinition.getTraits();
    local player = self.player;
    local playerTraits = player:getCharacterTraits():getKnownTraits();
    self.listboxTraitsOwned:clear();
    self.listboxTraitsPurchaseable:clear();
    
    for i=0, playerTraits:size()-1 do
        --print(playerTraits:get(i))
        local trait = CharacterTraitDefinition.getCharacterTraitDefinition(playerTraits:get(i))
        if (trait ~= nil) then 
            local newItem = self.listboxTraitsOwned:addItem(trait:getLabel(), trait, 4)
            newItem.tooltip = trait:getType():toString() .. "\r\n" .. trait:getDescription()
        end
    end
    for i=0, traits:size() -1 do repeat
        local trait = traits:get(i)
        local cost = trait:getCost()
        if (cost <= 0) then
            if (trait:getType() == CharacterTrait.BURGLAR) then
                if (player:hasTrait(CharacterTrait.NIGHT_VISION) and player:hasTrait(CharacterTrait.GRACEFUL)) then
                    cost = 11;
                end
            elseif (trait:getType() == CharacterTrait.MECHANICS) then
                if (player:hasTrait(CharacterTrait.MECHANICS)) then
                    cost = 11;
                end
            elseif (trait:getType() == CharacterTrait.AXEMAN) then
                if (player:hasTrait(CharacterTrait.BRAWLER)) then
                    cost = 11;
                end
            elseif (trait:getType() == CharacterTrait.COOK) then
                if (player:hasTrait(CharacterTrait.COOK)) then
                    cost = 11;
                end
            elseif (trait:getType() == CharacterTrait.DESENSITIZED) then
                if (player:hasTrait(CharacterTrait.BRAVE)) then
                    cost = 11;
                end
            elseif (trait:getType() == CharacterTrait.MARKSMAN) then
                if (player:hasTrait(CharacterTrait.HUNTER)) then
                    cost = 11;
                end
            elseif (trait:getType() == CharacterTrait.NIGHT_OWL) then
                if (player:hasTrait(CharacterTrait.NEEDS_LESS_SLEEP)) then
                    cost = 11;
                end
            end
        end
        if cost <= 0 then break end -- no buying negative or free traits
        if player:hasTrait(trait:getType()) then break end
        local exclusiveTraits = trait:getMutuallyExclusiveTraits()
        local isOK = true
        for j=0, exclusiveTraits:size() -1 do
            local exclusiveTrait = exclusiveTraits:get(j);
            if player:hasTrait(exclusiveTrait) then
                exclusiveTrait = CharacterTraitDefinition.getCharacterTraitDefinition(exclusiveTrait);
                if (exclusiveTrait:getType() == CharacterTrait.BRAVE) then
                    break;
                elseif (exclusiveTrait:getType() == CharacterTrait.COOK) then
                    break;
                elseif (exclusiveTrait:getType() == CharacterTrait.MECHANICS) then
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
        newItem.tooltip = trait:getType():toString() .. "\r\n" .. trait:getDescription()
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
    if (not item) then
        return
    end
    if (isBlackListedTrait(item:getType())) then
        return
    end
    
    local player = self.player;
    local cost = item:getCost() * -1
    local traitpoints = getCurrentTraitPoints(player);
    if (cost > 0 and (traitpoints >= cost)) then
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

        if (item:getLabel() == Translator.getText("UI_trait_Smoker")) then
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
        end
    end
end

function ISTraitsPurchasePanel:onDblClickPurchaseableTrait(trait)
    if (not trait) then
        return
    end
    if (isBlackListedTrait(trait:getType())) then
        return
    end
    
    local player = self.player;
    local cost = trait:getCost()
    if (cost <= 0) then
            if (trait:getType() == CharacterTrait.BURGLAR)      then cost = 6;
        elseif (trait:getType() == CharacterTrait.MECHANICS)    then cost = 5;
        elseif (trait:getType() == CharacterTrait.AXEMAN)       then cost = 2;
        elseif (trait:getType() == CharacterTrait.COOK)         then cost = 4;
        elseif (trait:getType() == CharacterTrait.DESENSITIZED) then cost = 6;
        elseif (trait:getType() == CharacterTrait.MARKSMAN)     then cost = 2;
        elseif (trait:getType() == CharacterTrait.NIGHT_OWL)    then cost = 6; end
    end
    local traitpoints = getCurrentTraitPoints(player);
    if (cost > 0 and (traitpoints >= cost)) then
        -- TODO: add translation
        -- IGUI_BuyableTraits_add = "Add %1 for %2 points?"
        --local modal = ISModalDialog:new(48, 48, 250, 150, getText("IGUI_BuyableTraits_add", trait:getLabel(), cost), true, nil, ISTraitsPurchasePanel.onAddGoodTrait, player:getPlayerNum(), trait, cost)
        local width = 250.0F;
        local height = 150.0F;
        local x = self.owner:getX() + (self.owner:getWidth() / 2.0F) - (width / 2.0F);
        local y = self.owner:getY() + (self.owner:getHeight() / 2.0F) - (height / 2.0F);
        local modal = ISModalDialog:new(x, y, width, height, "Add ".. trait:getLabel() .. " for "..cost.. " points?", true, self, onAddGoodTrait, player:getPlayerNum(), trait, cost);
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
            playTraitSelected();
            self.selectedList = self.listboxTraitsOwned;
            self.listboxTraitsPurchaseable.selected = -1;
        end
        if (self.selectedList.selected == -1) then self.selectedList.selected = 1; end
    end
    if (button == Joypad.RBumper) then
        if not (self.selectedList == self.listboxTraitsPurchaseable) then
            playTraitSelected();
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

function ISTraitsPurchasePanel:onResize()
    ISUIElement.onResize(self);
    
    local TPSWindowX = self.owner:getWidth();
    local TPSWindowY = self.owner:getHeight();
    
    self.tableWidth = (TPSWindowX / 2.0f) - (self.tablePad * 2.0F);
    self.tableHeight = TPSWindowY - 60 - UI_BORDER_SPACING - (BUTTON_HGT * 2.0F);
    self.tableOffsetX = TPSWindowX - self.tableWidth - self.tablePad;
    
    self.listboxTraitsOwned:setWidth(self.tableWidth);
    self.listboxTraitsOwned:setHeight(self.tableHeight);
    self.listboxTraitsPurchaseable:setWidth(self.tableWidth);
    self.listboxTraitsPurchaseable:setHeight(self.tableHeight);
    self.listboxTraitsPurchaseable:setX(self.tableOffsetX);
    
    self.availableTraitsLabel:setX(self.tableOffsetX);
    self.PointsLabel:setX(TPSWindowX - self.PointsLabel:getWidth() - (self.tablePad * 2.0F));
    self.PointsLabel:setY(TPSWindowY - 40 - UI_BORDER_SPACING - BUTTON_HGT);
end

function ISTraitsPurchasePanel:createChildren()
    local x,y = 0, 0;
    local TPSWindowX = self.owner:getWidth();
    local TPSWindowY = self.owner:getHeight();
    
    self.tablePad = 20;
    self.tableWidth = (TPSWindowX / 2.0f) - (self.tablePad * 2.0F);
    self.tableHeight = TPSWindowY - 60 - UI_BORDER_SPACING - (BUTTON_HGT * 2.0F);
    self.tableOffsetX = TPSWindowX - self.tableWidth - self.tablePad;
    self.topOfLists = UI_BORDER_SPACING + BUTTON_HGT;
    self.traitButtonHgt = 25;
    self.traitButtonPad = 6;
    
    -- chosen traits label
    self.choosenTraitsLabel = ISLabel:new(self.tablePad, UI_BORDER_SPACING, BUTTON_HGT, getText("UI_characreation_choosentraits") , 1, 1, 1, 1, UIFont.Small, true);
	self.choosenTraitsLabel:initialise();
    self.choosenTraitsLabel:instantiate();
    self:addChild(self.choosenTraitsLabel);
    
    -- available traits label
    self.availableTraitsLabel = ISLabel:new(0, UI_BORDER_SPACING, BUTTON_HGT, getText("UI_characreation_availabletraits") , 1, 1, 1, 1, UIFont.Small, true);
	self.availableTraitsLabel:initialise();
    self.availableTraitsLabel:instantiate();
    self.availableTraitsLabel:setX(self.tableOffsetX);
    self:addChild(self.availableTraitsLabel);
    
    -- owned traits list
    self.listboxTraitsOwned = ISScrollingListBox:new(self.tablePad, self.topOfLists, self.tableWidth, self.tableHeight);
    self.listboxTraitsOwned:initialise();
    self.listboxTraitsOwned:instantiate();
    self.listboxTraitsOwned.itemheight = BUTTON_HGT;
    self.listboxTraitsOwned.selected = 1;
    self.listboxTraitsOwned.doDrawItem = self.drawTraitMap;
    self.listboxTraitsOwned:setOnMouseDownFunction(self.listboxTraitsOwned, function(item) self:onClickTrait(self.listboxTraitsOwned, item) end)
    self.listboxTraitsOwned:setOnMouseDoubleClick(self, self.onDblClickOwnedTrait);
    self.listboxTraitsOwned.drawBorder = true;
    self.listboxTraitsOwned.owner = self;
    self:addChild(self.listboxTraitsOwned);
    
    self.selectedList = self.listboxTraitsOwned;

    -- purchasable traits list
    self.listboxTraitsPurchaseable = ISScrollingListBox:new(self.tableOffsetX, self.topOfLists, self.tableWidth, self.tableHeight);
    self.listboxTraitsPurchaseable:initialise();
    self.listboxTraitsPurchaseable:instantiate();
    self.listboxTraitsPurchaseable.itemheight = BUTTON_HGT;
    self.listboxTraitsPurchaseable.selected = -1;
    self.listboxTraitsPurchaseable.doDrawItem = self.drawTraitMap;
    self.listboxTraitsPurchaseable:setOnMouseDownFunction(self.listboxTraitsPurchaseable, function(item) self:onClickTrait(self.listboxTraitsPurchaseable, item) end)
    self.listboxTraitsPurchaseable:setOnMouseDoubleClick(self, self.onDblClickPurchaseableTrait);
    self.listboxTraitsPurchaseable.drawBorder = true;
    self.listboxTraitsPurchaseable.owner = self;
    self:addChild(self.listboxTraitsPurchaseable);
    
    y = TPSWindowY - 40 - UI_BORDER_SPACING - BUTTON_HGT;
    self.PointsLabel = ISLabel:new(0, y, BUTTON_HGT, getText("IGUI_PlayerStats_AvailableSkillPt") , 1, 1, 1, 1, UIFont.Small, true);
	self.PointsLabel:initialise();
    self.PointsLabel:instantiate();
    self.PointsLabel:setX(TPSWindowX - self.PointsLabel:getWidth() - (self.tablePad * 2.0F));
    self:addChild(self.PointsLabel);

    self:updateList()
end

function ISTraitsPurchasePanel:update()
    ISPanelJoypad.update(self);

    local points = getCurrentTraitPoints(self.player);
    if (points ~= currentTraitPoints) then
        currentTraitPoints = points;
        self.PointsLabel:setTranslation(getText("IGUI_PlayerStats_AvailableSkillPt") .. getCurrentTraitPoints(self.player));
    end
    local traits = self.player:getCharacterTraits():getKnownTraits();
    local size = tonumber(traits:size());
    if (size ~= currentTraitsSize) then
        currentTraitsSize = size;
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
    instance.owner = owner;
    instance.selectedList = nil;

    return instance;
end

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISTraitsPurchasePanel.lua