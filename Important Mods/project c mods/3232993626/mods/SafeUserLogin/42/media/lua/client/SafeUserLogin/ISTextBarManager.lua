if getCore():isDedicated() then return end

-------------------------------------------------------------------------
-- local variables
local Utils = SafeUserLogin_mod.Utils;

local settingsTextBar = {
    ["visible"] = true,
    ["x"] = 0,
    ["y"] = 0,
    ["movable"] = false,
    ["color"] = {r = (250 / 255), g = (128 / 255), b = (114 / 255), a = 0.5}
};
local StandardText = ISUIElement:derive("StandardText")
local textBar = {};

-------------------------------------------------------------------------
-- global functions

--[[ getTextBar(player)
    returns the players textBar
]]
function Utils.getTextBar(player)
    if (not textBar[player]) then
        return nil
    end
    
    return textBar[player]
end

--[[ getTextBarMessage(player)
    returns the players textBar message
]]
function Utils.getTextBarMessage(player)
    if (not textBar[player]) then
        return nil
    end
    
    return textBar[player].message
end

--[[ setTextBarMessage(player, message)
    sets the players textBar message
]]
function Utils.setTextBarMessage(player, message)
    if (not textBar[player]) then
        return
    end
    
    textBar[player].message = message;
end

--[[ createTextBar(player, playerIndex)
    creates the players textBar
]]
function Utils.createTextBar(player, playerIndex)
    if (not player) then
        return false
    end
    
    textBar[player] = StandardText:new(
        player,
        playerIndex
    );
    textBar[player]:initialise();
    textBar[player]:instantiate();
    textBar[player]:addToUIManager();
    
    return true
end

--[[ removeTextBar(player, )
    removes the players textBar
]]
function Utils.removeTextBar(player)
    if (not textBar[player]) then
        return
    end
    
    textBar[player]:removeFromUIManager();
    textBar[player] = nil;
    
    if (not player) then
        return
    end
    player:setOutlineHighlight(false);
end

-------------------------------------------------------------------------
-- local functions

function StandardText:initialize()
	ISUIElement.initialise(self);
end

function StandardText:prerender()
    self.player:setOutlineHighlight(true);
    self.player:setOutlineHighlightCol(1, 1, 1, 0.25F);

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
    self.player:setOutlineHighlight(true);
    self.player:setOutlineHighlightCol(1, 1, 1, 0.25F);

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
    
    self.player:setOutlineHighlight(true);
    self.player:setOutlineHighlightCol(1, 1, 1, 0.25F);
    
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

-- /reloadlua client/YourBar/ISBarManager.lua