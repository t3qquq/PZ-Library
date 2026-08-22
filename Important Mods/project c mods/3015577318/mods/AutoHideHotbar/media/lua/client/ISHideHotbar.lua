----------------------------------------------------------------------------------------------

---	AutoHide Hotbar
---	@author peteR_pg
---	Steam profile: https://steamcommunity.com/id/peter_pg/

---	Automatically hides the hotbar when the player is driving

----------------------------------------------------------------------------------------------

---Save vanilla function
local old_HotbarUpdate = ISHotbar.update
function ISHotbar.update(self)
	---Call vanilla update
	old_HotbarUpdate(self)
	---Check if the player is in a car and is the driver
	if self.character:isSeatedInVehicle() then
		if self.character:getVehicle():getDriver() == self.character then
			self:setVisible(false)
		end
	end
end