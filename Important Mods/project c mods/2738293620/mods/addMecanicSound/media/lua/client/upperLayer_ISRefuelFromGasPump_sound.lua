--***********************************************************
--**                    THE INDIE STONE                    **
--***********************************************************

require "Vehicles/TimedActions/ISRefuelFromGasPump"
-----------------------------------------------------------------------------------------------------------------------------------------------------------
local upperLayer = {}
upperLayer.ISRefuelFromGasPump = {}
-----------------------------------------------------------------------------------------------------------------------------------------------------------
upperLayer.ISRefuelFromGasPump.update = ISRefuelFromGasPump.update
function ISRefuelFromGasPump:update()
    upperLayer.ISRefuelFromGasPump.update(self)
    --if self.sound and self.sound ~= 0 then self.character:stopOrTriggerSound(self.sound) end
   	--if UIManager.getSpeedControls() and (UIManager.getSpeedControls():getCurrentGameSpeed() == 0) or (UIManager.getSpeedControls():getCurrentGameSpeed() > 1) then return end

    --if not self.sound then self.sound = self.character:playSound("StationFuelDepart") end
	if self.sound and self.sound ~= 0 and not self.character:getEmitter():isPlaying(self.sound) then self.sound = self.character:playSound("StationFuelUpdate") end
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
upperLayer.ISRefuelFromGasPump.start = ISRefuelFromGasPump.start
function ISRefuelFromGasPump:start()
	upperLayer.ISRefuelFromGasPump.start(self)
 if self.sound and self.sound ~= 0 then self.character:stopOrTriggerSound(self.sound) end
 self.sound = self.character:playSound("StationFuelDepart");
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
upperLayer.ISRefuelFromGasPump.stop = ISRefuelFromGasPump.stop
function ISRefuelFromGasPump:stop()
    upperLayer.ISRefuelFromGasPump.stop(self)
	if self.sound and self.sound ~= 0 and self.character:getEmitter():isPlaying(self.sound) then self.character:getEmitter():stopSound(self.sound) end
    self.sound = self.character:playSound("StationFuelStop2")
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
upperLayer.ISRefuelFromGasPump.perform = ISRefuelFromGasPump.perform
function ISRefuelFromGasPump:perform()
	upperLayer.ISRefuelFromGasPump.perform(self)
	if self.sound and self.sound ~= 0 and self.character:getEmitter():isPlaying(self.sound) then self.character:getEmitter():stopSound(self.sound) end
    self.sound = self.character:playSound("StationFuelStop")
end
-----------------------------------------------------------------------------------------------------------------------------------------------------------
	