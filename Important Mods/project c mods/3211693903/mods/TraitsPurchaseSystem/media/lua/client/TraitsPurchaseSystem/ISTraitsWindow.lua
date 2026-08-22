--if not isClient() then return end

windows = {};
ISTraitsWindow = ISCollapsableWindow:derive("ISTraitsWindow");

--[[ function ISPlayerDataObject:createInventoryInterface()
    monkey patch original createInventoryInterface()
]]
local originalcreateInventoryInterface = ISPlayerDataObject.createInventoryInterface;
function ISPlayerDataObject:createInventoryInterface()
    originalcreateInventoryInterface(self);
    
    local player = getSpecificPlayer(self.id);
    local bind = player:getJoypadBind();
    local isMouse = self.id == 0 and bind == -1;
    
    if not (windows[player]) then
        local playerNum = self.id;
        local x = getPlayerScreenLeft(self.id)
        local y = getPlayerScreenTop(self.id)
        windows[player] = ISTraitsWindow:new(x, y, player, playerNum);
        windows[player]:initialise();
    end
    
    if (isMouse) then
        windows[player]:addToUIManager();
        windows[player]:setVisible(isMouse and (windows[player].visibleOnStartup == true));
    end
end

--[[ removeInventoryUI(id)
    monkey patch original removeInventoryUI(id)
]]
local originalRemoveInventoryUI = removeInventoryUI;
function removeInventoryUI(id)
    originalRemoveInventoryUI(id);
    
    local player = getSpecificPlayer(id + 1);
    if (player == nil) then return end;
    if (windows[player] and windows[player]:getIsVisible()) then
        windows[player]:removeFromUIManager();
    end
end

function ISTraitsWindow:initialise()
	ISCollapsableWindow.initialise(self);
end

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

function ISTraitsWindow:createChildren()
	ISCollapsableWindow.createChildren(self);
    
	self.panel = ISTraitsPurchasePanel:new(0, 8, self.width, self.height, self.player, self.playerNum, self);
    self.panel:initialise();
    self:addView(self.panel);
    
    -- Only save window layout for single player
    if (self.playerNum == 0) then
        ISLayoutManager.RegisterWindow('traitspurchasewindow', ISTraitsWindow, self);
    end
    self.visibleOnStartup = self:getIsVisible() -- hack, see ISPlayerDataObject.lua
end

function ISTraitsWindow:render()
	ISCollapsableWindow.render(self);
    
    if JoypadState.players[self.playerNum + 1] then
        if JoypadState.players[self.playerNum + 1].focus == self.panel then
            self:drawRectBorder(0, 0, self:getWidth(), self:getHeight(), 0.4, 0.2, 1.0, 1.0);
            self:drawRectBorder(1, 1, self:getWidth() - 2, self:getHeight() - 2, 0.4, 0.2, 1.0, 1.0);
        end
	end
end

function ISTraitsWindow:close()
    self:setVisible(false);
    self:removeFromUIManager();
    
	if JoypadState.players[self.playerNum + 1] then
		setJoypadFocus(self.playerNum, nil);
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

function ISTraitsWindow:new(x, y, player, playerNum)
	local instance = ISCollapsableWindow:new(x + 300, y + 100, 420, 250);
	setmetatable(instance, self);
	self.__index = self;
    
    instance.title = "Traits";
	instance.backgroundColor.a = 0.9;
	instance:setResizable(false);
    instance.visibleOnStartup = false;
    instance.player = player;
	instance.playerNum = playerNum;
    instance:setResizable(false);
	instance:setDrawFrame(true);
    
	--ISTraitsWindow.instance = o;

	--o:initialise();
    
    return instance
end

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISTraitsWindow.lua