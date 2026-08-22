textBar = {};

local settingsTextBar = {
    ["visible"] = true,
    ["x"] = 0,
    ["y"] = 0,
    ["movable"] = false,
    ["color"] = {r = (250 / 255), g = (128 / 255), b = (114 / 255), a = 0.5}
};

local StandardText = ISUIElement:derive("StandardText")

function StandardText:initialize()
	ISUIElement.initialise(self);
end

function StandardText:prerender()
	self:setStencilRect(
        getPlayerScreenLeft(self.playerNum),
        getPlayerScreenTop(self.playerNum),
        getPlayerScreenWidth(self.playerNum),
        getPlayerScreenHeight(self.playerNum)
    );

    if (self.message) then
        self:drawText(
            (self.message),
            self.locationX,
            self.locationY,
            1.0F,
            0.0F,
            0.0F,
            1.0F,
            UIFont.Large
        );
    end
end

function StandardText:update()
    local playerX = self.player:getX();
    local playerY = self.player:getY();
    local playerZ = self.player:getZ();
    local barX = isoToScreenX(self.playerNum, playerX, playerY, playerZ);
    local barY = isoToScreenY(self.playerNum, playerX, playerY, playerZ);

    self.locationX = barX + (30 / self.zoom);
    self.locationY = barY - (20 / self.zoom);
end

function StandardText:render()
    self.zoom = getCore():getZoom(self.playerNum);
    
    self.player:setHighlightColor(0.8F, 0.1F, 0.1F, 0.5F);
    self.player:setHighlighted(true);
    self.player:setOutlineHighlight(true);
    self.player:setOutlineHighlightCol(1, 1, 1, 1);
    
    local playerX = self.player:getX();
    local playerY = self.player:getY();
    local playerZ = self.player:getZ();
    local barX = isoToScreenX(self.playerNum, playerX, playerY, playerZ);
    local barY = isoToScreenY(self.playerNum, playerX, playerY, playerZ);
    
    self.locationX = barX + (30 / self.zoom);
    self.locationY = barY - (20 / self.zoom);

    self:clearStencilRect();
end

function StandardText:new(player, playerNum)
    local settings = settingsTextBar;

	local instance = ISUIElement:new(settings["x"], settings["y"], 1, 1);
	setmetatable(instance, self);
	self.__index = self;

    instance.player = player;
	instance.playerNum = playerNum;
    instance.locationX = 0;
    instance.locationY = 0;
    instance.color = settings["color"];
    instance.zoom = 1;

    instance:setFollowGameWorld(true);
	instance:setVisible(true);
    
    instance.message = "";

	return instance
end

local function OnCreatePlayer(playerIndex, player)
    textBar[player] = StandardText:new(
        player,
        playerIndex
    );
    textBar[player]:initialise();
    textBar[player]:instantiate();
    textBar[player]:addToUIManager();
end
Events.OnCreatePlayer.Add(OnCreatePlayer);

-- /reloadlua client/YourBar/ISBarManager.lua