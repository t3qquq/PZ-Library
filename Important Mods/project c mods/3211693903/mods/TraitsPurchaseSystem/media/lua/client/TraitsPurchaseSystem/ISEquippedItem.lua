--if not isClient() then return end

--[[ ISEquippedItem:prerender()
    monkey patch original prerender
]]
local originalPrerender = ISEquippedItem.prerender;
function ISEquippedItem:prerender()
    originalPrerender(self);

    if self.chr:getPlayerNum() == 0 then
        if (self.healthBtn:isMouseOver()) then
            self.traitsPopup:setVisible(true)
        elseif (self.traitsPopup:isMouseOver()) then
            --
        else
            self.traitsPopup:setVisible(false)
        end
    end
end

--[[ ISEquippedItem:initialise()
    monkey patch original initialise
]]
local originalInitialise = ISEquippedItem.initialise;
function ISEquippedItem:initialise()
    originalInitialise(self);

    if self.chr:getPlayerNum() == 0 then
        self.traitsPopup = ISTraitsIconPopup:new(10 + self.healthBtn:getX(), 10 + self.healthBtn:getY(), self.healthBtn.width * 2, self.healthBtn.height)
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
    self:drawRect(0, 0, self.width, self.height, 0.80, 0, 0, 0);

    local index = math.floor(self:getMouseX() / 50);
    if index > 0 then
        self:drawRect(index * 50, 0, 50, self.height, 0.15, 1, 1, 1);
    end

    local x = 0;
    local y = 0;

    local tex = self.heartIcon;
    if (self.owner.infopanel:getIsVisible()) then
        tex = self.heartIconOn;
    end
    self:drawTexture(tex, x + (50 - tex:getWidthOrig()) / 2, y + (self.height - tex:getHeightOrig()) / 2, 1, 1, 1, 1);

    tex = self.targetIcon;
    if (windows[self.owner.chr]:getIsVisible()) then
        self:drawTexture(tex, x + 50 + (50 - tex:getWidthOrig()) / 2, y + (self.height - tex:getHeightOrig()) / 2, 1, 1, 1, 1);
    else
        self:drawTexture(tex, x + 50 + (50 - tex:getWidthOrig()) / 2, y + (self.height - tex:getHeightOrig()) / 2, 1, 0.6F, 0.6F, 0.6F);
    end
end

function ISTraitsIconPopup:onMouseDown(x, y)
    return true
end

function ISTraitsIconPopup:onMouseUp(x, y)
    self:setVisible(false);
    local playerNum = self.owner.chr:getPlayerNum();
    local index = math.floor(x / 50);
    local mode = nil;
    if index == 0 then
        self.owner.infopanel:toggleView(getText("IGUI_XP_Health"));
    elseif index == 1 then
        windows[self.owner.chr]:toggleWindow();
    end
    return true
end

function ISTraitsIconPopup:new(x, y, width, height)
    local o = ISPanel.new(self, x, y, width, height);
    o.heartIcon = getTexture("media/ui/Heart2_Off.png");
	o.heartIconOn = getTexture("media/ui/Heart2_On.png");
    o.targetIcon = TraitFactory.getTrait("Marksman"):getTexture();
    return o
end

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISEquippedItem.lua