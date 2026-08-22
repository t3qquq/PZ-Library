require "WallHealth"

Kamer_WallHealthInfoWindow = ISCollapsableWindow:derive("Kamer_WallHealthInfoWindow")
Kamer_WallHealthInfoWindow.windows = {}

function Kamer_WallHealthInfoWindow:createChildren()
	ISCollapsableWindow.createChildren(self)
	self.panel = ISToolTip:new()
	self.panel.followMouse = false
	self.panel:initialise()
	self:setObject(self.object)
	self:addView(self.panel)
end

function Kamer_WallHealthInfoWindow:update()
	ISCollapsableWindow.update(self)
	
	self.panel.maxLineWidth = 400

	if self:getIsVisible() and (not self.object or self.object:getObjectIndex() == -1) then
		if self.joyfocus then
			self.joyfocus.focus = nil
			updateJoypadFocus(self.joyfocus)
		end
		self:removeFromUIManager()
		return
	end

	self:setObject(self.object)
	self:setWidth(self.panel:getWidth())
	self:setHeight(self:titleBarHeight() + self.panel:getHeight())
end

function Kamer_WallHealthInfoWindow:setObject(object)
	self.object = object

	local objName = object:getName() or object:getProperties():Val("CustomName")
	if objName then
		local prepareName = "ContextMenu_" .. string.gsub(objName, " ", "_")
		objName = getText(prepareName)
		if objName == prepareName then
			objName = object:getProperties():Val("CustomName") or object:getName() or "Unknown Object (Unnamed)"
		end
	else
		objName = "Unknown Object (Unnamed)"
	end

	local objHealth = object:getHealth()
	local objMaxHealth = object:getMaxHealth()

	self.panel:setName(objName)
	self.panel:setTexture(object:getTextureName())
	self.panel.description = getText("ContextMenu_Kamer_ShowWallHealth_Health", objHealth, objMaxHealth)
end

function Kamer_WallHealthInfoWindow:onGainJoypadFocus(joypadData)
	self.drawJoypadFocus = true
end

function Kamer_WallHealthInfoWindow:onJoypadDown(button)
	if button == Joypad.BButton then
		self:removeFromUIManager()
		setJoypadFocus(self.playerNum, nil)
	end
end

function Kamer_WallHealthInfoWindow:close()
	self:removeFromUIManager()
end

function Kamer_WallHealthInfoWindow:new(x, y, character, object)
	local width = 320
	local height = 16 + 64 + 16 + 16
	local o = ISCollapsableWindow:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.playerNum = character:getPlayerNum()
	o.object = object
	o:setResizable(false)
	return o
end
