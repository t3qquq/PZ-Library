-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

MapLocationHeader = ISPanelJoypad:derive("MapLocationHeader")

function MapLocationHeader:createChildren()
	self:setHeight(40)
end

function MapLocationHeader:prerender()
	ISPanelJoypad.prerender(self)

	if BB_WhereAmI.currentArea == "" then
		if SandboxVars.WhereAmI.HideUIOffRegion then
			if self.backgroundColor.a == 0.8 then
				self.borderColor.a = 0
				self.backgroundColor.a = 0
			end
		else
			self:drawText("Kentucky, USA", self.width / 2 - (getTextManager():MeasureStringX(UIFont.Medium, "Kentucky, USA") / 2), 10, 1,1,1,1, UIFont.Large)
		end
	else
		if SandboxVars.WhereAmI.HideUIOffRegion then
			if self.backgroundColor.a == 0 then
				self.borderColor.a = 1
				self.backgroundColor.a = 0.8
			end
		end

		self:drawText(BB_WhereAmI.currentArea, self.width / 2 - (getTextManager():MeasureStringX(UIFont.Medium, BB_WhereAmI.currentArea) / 2), 10, 1,1,1,1, UIFont.Large)
	end
end

function MapLocationHeader:render()
	ISPanelJoypad.render(self)
end

function MapLocationHeader:new(x, y, width, height, mapUI)
	local o = ISPanelJoypad.new(self, x, y, width, height)
	o.borderColor = {r=0.4, g=0.4, b=0.4, a=1}
	o.backgroundColor = {r=0, g=0, b=0, a=0.8}
	o.character = mapUI.character
	o.playerNum = mapUI.playerNum or 0
	o.textCursor = getTexture("media/ui/LootableMaps/textCursor.png")
	o.symbolTexList = {}
	o.showTranslationOption = false
	return o
end

