--if not isClient() then return end

-------------------------------------------------------------------------
-- local variables
local ISTraitsWindow = ISCollapsableWindow:derive("ISTraitsWindow");
local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small);
local UI_BORDER_SPACING = 10;
local BUTTON_HGT = FONT_HGT_SMALL + 6

function ISTraitsWindow:toggleWindow()
    if (self:getIsVisible()) then
        self:close();
    else
        self:addToUIManager();
        self:setVisible(true);
        self:bringToTop();
        self.tooltipForced = nil;
    end
end

--

function ISTraitsWindow:onResize()
    ISUIElement.onResize(self);

    self.panel:setWidth(self.width - UI_BORDER_SPACING * 2 - 2);
    self.panel:setHeight(self.height - UI_BORDER_SPACING * 2 + 1);
end

--

function ISTraitsWindow:createChildren()
	ISCollapsableWindow.createChildren(self);
    
	self.panel = ISTraitsPurchasePanel:new(0, 8, self.width, self.height, self.player, self.playerIndex, self);
    self.panel:initialise();
    self:addView(self.panel);
    
    -- Only save window layout for single player
    if (self.playerIndex == 0) then
        ISLayoutManager.RegisterWindow('traitspurchasewindow', ISTraitsWindow, self);
    end
    self.visibleOnStartup = self:getIsVisible();
    
    self.resizeWidget2:bringToTop();
	self.resizeWidget:bringToTop();
end

function ISTraitsWindow:prerender()
	ISCollapsableWindowJoypad.prerender(self);
end

--[[
function ISTraitsWindow:render()
	ISCollapsableWindow.render(self);
    
    if JoypadState.players[self.playerIndex + 1] then
        if JoypadState.players[self.playerIndex + 1].focus == self.panel then
            self:drawRectBorder(0, 0, self:getWidth(), self:getHeight(), 0.4, 0.2, 1.0, 1.0);
            self:drawRectBorder(1, 1, self:getWidth() - 2, self:getHeight() - 2, 0.4, 0.2, 1.0, 1.0);
        end
	end
end
]]

function ISTraitsWindow:close()
    self:setVisible(false);
    self:removeFromUIManager();
    
	if JoypadState.players[self.playerIndex + 1] then
		setJoypadFocus(self.playerIndex, nil);
	end
end

function ISTraitsWindow:RestoreLayout(name, layout)
    ISLayoutManager.DefaultRestoreWindow(self, layout);
    if (layout.pin == 'true') then
		ISCollapsableWindow.pin(self); -- don't call self:pin()
	elseif (layout.pin == 'false') then
		self:collapse();
		self.isCollapsed = true;
		self:setMaxDrawHeight(self:titleBarHeight());
	end
end

function ISTraitsWindow:SaveLayout(name, layout)
    if (self.pin) then
        layout.pin = 'true';
    else
        layout.pin = 'false';
    end
    ISLayoutManager.DefaultSaveWindow(self, layout);
end

function ISTraitsWindow:new(x, y, player, playerIndex)
	local instance = ISCollapsableWindow:new(x + 200, y + 100, 700, 400);
	setmetatable(instance, self);
	self.__index = self;
    
    instance.title = "Traits";
	instance.backgroundColor.a = 0.9;
    instance.minimumWidth = 700;
    instance.minimumHeight = 400;
	instance:setResizable(true);
    instance.visibleOnStartup = false;
    instance.player = player;
	instance.playerIndex = playerIndex;
	instance:setDrawFrame(true);
    
    return instance
end

function ISTraitsWindow:initialise()
	ISCollapsableWindow.initialise(self);
end

function TPSystemHandleOnCreatePlayer(playerIndex, player)
    if (getCore():isDedicated()) then
        return;
    end
    
    if (not (TPS.Windows[player])) then
        local x = getPlayerScreenLeft(playerIndex);
        local y = getPlayerScreenTop(playerIndex);
        TPS.Windows[player] = ISTraitsWindow:new(x, y, player, playerIndex);
        TPS.Windows[player]:initialise();
    end
end

function TPSystemHandleOnPlayerDeath(player)
    local TPSwindow = TPS.Windows[player];
	if (TPSwindow) then
		TPSwindow:setVisible(false);
		TPSwindow:removeFromUIManager();
	end
end

function TPSystemHandleOnResolutionChange(oldw, oldh, neww, newh)
	if (getPlayer() == nil) then
        return;
    end
    
	for playerIndex=0, getNumActivePlayers() - 1 do
        local x = getPlayerScreenLeft(playerIndex);
        local y = getPlayerScreenTop(playerIndex);
        self:setX(x + 300);
        self:setY(y + 100);
	end
end

Events.OnCreatePlayer.Add(TPSystemHandleOnCreatePlayer)
Events.OnPlayerDeath.Add(TPSystemHandleOnPlayerDeath)
Events.OnResolutionChange.Add(TPSystemHandleOnResolutionChange)

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISTraitsWindow.lua