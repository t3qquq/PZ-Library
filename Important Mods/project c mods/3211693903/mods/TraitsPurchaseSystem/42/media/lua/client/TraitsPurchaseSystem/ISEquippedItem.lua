--if not isClient() then return end

-------------------------------------------------------------------------
-- local variables
local TEXTURE_WIDTH = 0;
local TEXTURE_HEIGHT = 0;

-------------------------------------------------------------------------
-- local functions
local function setTextureWidth()
    local size = getCore():getOptionSidebarSize();
    if (size == 6) then
        size = getCore():getOptionFontSizeReal() - 1
    end
        TEXTURE_WIDTH = 48;
    if size == 2  then
        TEXTURE_WIDTH = 64;
    elseif size == 3  then
        TEXTURE_WIDTH = 80;
    elseif size == 4  then
        TEXTURE_WIDTH = 96;
    elseif size == 5 then
        TEXTURE_WIDTH = 128;
    end

    TEXTURE_HEIGHT = TEXTURE_WIDTH * 0.75;
end

-------------------------------------------------------------------------
-- monkey patches

if (not TPS.Hooked.ISEquippedItem_prerender) then
    TPS.Hooked.ISEquippedItem_prerender = ISEquippedItem.prerender;
end

if (not TPS.Hooked.ISEquippedItem_initialise) then
    TPS.Hooked.ISEquippedItem_initialise = ISEquippedItem.initialise;
end

--[[ ISEquippedItem:prerender()
    monkey patch original prerender
]]
function ISEquippedItem:prerender(...)
    TPS.Hooked.ISEquippedItem_prerender(self, ...);

    if (self.chr:getPlayerNum() == 0) then
        if (self.healthBtn:isMouseOver()) then
            self.traitsPopup:setVisible(true);
        elseif (self.traitsPopup:isMouseOver()) then
            --
        else
            self.traitsPopup:setVisible(false);
        end
    end
end


--[[ ISEquippedItem:initialise()
    monkey patch original initialise
]]
function ISEquippedItem:initialise(...)
    TPS.Hooked.ISEquippedItem_initialise(self, ...);

    setTextureWidth();
    if (self.chr:getPlayerNum() == 0) then
        self.traitsPopup = ISTraitsIconPopup:new(10 + self.healthBtn:getX(), 10 + self.healthBtn:getY(), TEXTURE_WIDTH * 2, TEXTURE_HEIGHT);
        self.traitsPopup.owner = self;
        self.traitsPopup:addToUIManager();
        self.traitsPopup:setVisible(false);
    end
end

ISTraitsIconPopup = ISPanel:derive("ISTraitsIconPopup")

function ISTraitsIconPopup:prerender()
    self:setAlwaysOnTop(true);
end

function ISTraitsIconPopup:render()
    if (not TPS.Windows) then
        return
    end
    
    self:drawRect(0, 0, self.width, self.height, 0.80, 0, 0, 0);

    local index = math.floor(self:getMouseX() / TEXTURE_WIDTH);
    if (index > 0) then
        self:drawRect(index * TEXTURE_WIDTH, 0, TEXTURE_WIDTH, self.height, 0.15, 1, 1, 1);
    end

    local tex = self.heartIconOff;
    if (self.owner.infopanel:getIsVisible()) then
        tex = self.heartIconOn;
    end
    self:drawTexture(tex, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, 1, 1, 1, 1);

    tex = self.targetIcon;
    if (TPS.Windows[self.owner.chr]:getIsVisible()) then
        self:drawTextureScaled(tex, TEXTURE_WIDTH + TEXTURE_WIDTH * 0.25, TEXTURE_HEIGHT * 0.25, TEXTURE_WIDTH * 0.5, TEXTURE_HEIGHT * 0.5, 1, 1, 1, 1);
    else
        self:drawTextureScaled(tex, TEXTURE_WIDTH + TEXTURE_WIDTH * 0.25, TEXTURE_HEIGHT * 0.25, TEXTURE_WIDTH* 0.5, TEXTURE_HEIGHT * 0.5, 1, 0.6F, 0.6F, 0.6F);
    end
end

function ISTraitsIconPopup:onMouseDown(x, y)
    return true
end

function ISTraitsIconPopup:onMouseUp(x, y)
    if (TPS.Windows == nil) then
        return false
    end
    
    self:setVisible(false);
    local playerNum = self.owner.chr:getPlayerNum();
    local index = math.floor(x / TEXTURE_WIDTH);
    local mode = nil;
    if (index == 0) then
        self.owner.infopanel:toggleView(getText("IGUI_XP_Health"));
    elseif (index == 1) then
        TPS.Windows[self.owner.chr]:toggleWindow();
    end
    return true
end

function ISTraitsIconPopup:new(x, y, width, height)
    local o = ISPanel.new(self, x, y, width, height);
    o.heartIconOn = getTexture("media/ui/Sidebar/" .. TEXTURE_WIDTH .."/Heart_On_" .. TEXTURE_WIDTH .. ".png");
    o.heartIconOff = getTexture("media/ui/Sidebar/" .. TEXTURE_WIDTH .."/Heart_Off_" .. TEXTURE_WIDTH .. ".png");
    o.targetIcon = CharacterTraitDefinition.getCharacterTraitDefinition(CharacterTrait.MARKSMAN):getTexture();
    return o
end

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISEquippedItem.lua