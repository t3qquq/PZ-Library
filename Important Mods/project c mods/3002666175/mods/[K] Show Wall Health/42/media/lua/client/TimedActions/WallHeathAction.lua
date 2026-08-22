Kamer_WallHeathAction = ISBaseTimedAction:derive("Kamer_WallHeathAction")

function Kamer_WallHeathAction:isValid()
	return self.object:getObjectIndex() ~= -1
end

function Kamer_WallHeathAction:waitToStart()
	self:faceLocation()
	return self.character:shouldBeTurning()
end

function Kamer_WallHeathAction:perform()
	local window = Kamer_WallHealthInfoWindow.windows[self.character]
	if window then
		window:setObject(self.object)
	else
		local x = getPlayerScreenLeft(self.playerNum)
		local y = getPlayerScreenTop(self.playerNum)
		local w = getPlayerScreenWidth(self.playerNum)
		local h = getPlayerScreenHeight(self.playerNum)
		window = Kamer_WallHealthInfoWindow:new(x + 70, y + 50, self.character, self.object)
		window:initialise()
		window:addToUIManager()
		Kamer_WallHealthInfoWindow.windows[self.character] = window
		if self.character:getPlayerNum() == 0 then
			ISLayoutManager.RegisterWindow('kamer_wallhealth', ISCollapsableWindow, window)
		end
	end
	window:setVisible(true)
	window:addToUIManager()
	local joypadData = JoypadState.players[self.playerNum+1]
	if joypadData then
		joypadData.focus = window
	end
	ISBaseTimedAction.perform(self)
end

function Kamer_WallHeathAction:faceLocation()
    self.character:faceThisObject(self.object)
end

function Kamer_WallHeathAction:new(character, object)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.maxTime = 0
	o.stopOnWalk = true
	o.stopOnRun = true
	o.character = character
	o.playerNum = character:getPlayerNum()
	o.object = object
	return o
end
