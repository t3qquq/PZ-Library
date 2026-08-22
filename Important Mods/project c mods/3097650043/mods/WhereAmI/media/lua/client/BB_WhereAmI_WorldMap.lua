-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local createChildren = ISWorldMap.createChildren

function ISWorldMap:createChildren()
	createChildren(self)
	self.whereAmI = MapLocationHeader:new(100, self.height - 60, 400, self.height - 60, self)
	self.whereAmI:initialise()
	self.whereAmI:setAnchorLeft(true)
	self.whereAmI:setAnchorRight(false)
	self:addChild(self.whereAmI)
end

local showWorldMap = ISWorldMap.ShowWorldMap

function ISWorldMap.ShowWorldMap(playerNum)
	BB_WhereAmI.CheckPlayerArea()
	showWorldMap(playerNum)
end