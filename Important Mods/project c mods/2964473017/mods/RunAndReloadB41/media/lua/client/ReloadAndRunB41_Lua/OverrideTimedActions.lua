--Made by SportXAI
require "TimedActions/ISRackFirearm"
require "TimedActions/ISLoadBulletsInMagazine"
require "TimedActions/ISUnloadBulletsFromFirearm"
require "TimedActions/ISUnloadBulletsFromMagazine"
require "TimedActions/ISInsertMagazine"
require "TimedActions/ISReloadWeaponAction"
require "TimedActions/ISEjectMagazine"
local function ReloadingControl(player)
	if player == nil then return end
	local key = getCore():getKey("Run")

	if not getCore():isToggleToRun() then
		player:setRunning(false)
		player:setForceRun(false)
	elseif getCore():isToggleToRun() then
		player:setRunning(false)
		player:setForceRun(false)
		--player:setVariable("RunAndReload_Enable", true)
	end

    if not getCore():isToggleToRun() then
        if GameKeyboard.isKeyPressed(key) then
            if not player:getVariableBoolean("RunAndReload_Enable") then
                player:setVariable("RunAndReload_Enable", true)
            else
                player:setVariable("RunAndReload_Enable", false)
            end
        end
    end

    if getCore():isToggleToRun() then
        if GameKeyboard.isKeyPressed(key) then
            if player:getVariableBoolean("RunAndReload_Enable") then
                player:setVariable("RunAndReload_Enable", false)
            else
                player:setVariable("RunAndReload_Enable", true)
            end
        end
    end
end


local ISLoadBulletsInMagazine_Update = ISLoadBulletsInMagazine.update
local ISUnloadBulletsFromMagazine_Update = ISUnloadBulletsFromMagazine.update
local ISUnloadBulletsFromFirearm_Update = ISUnloadBulletsFromFirearm.update
local ISRackFirearm_Update = ISRackFirearm.update
local ISInsertMagazine_Update = ISInsertMagazine.update
local ISReloadWeaponAction_Update = ISReloadWeaponAction.update
local ISEjectMagazine_Update = ISEjectMagazine.update

local ISLoadBulletsInMagazine_New = ISLoadBulletsInMagazine.new
local ISUnloadBulletsFromFirearm_New = ISUnloadBulletsFromFirearm.new
local ISUnloadBulletsFromMagazine_New = ISUnloadBulletsFromMagazine.new
local ISRackFirearm_New = ISRackFirearm.new
local ISInsertMagazine_New = ISInsertMagazine.new
local ISReloadWeaponAction_New = ISReloadWeaponAction.new
local ISEjectMagazine_New = ISEjectMagazine.new

function ISLoadBulletsInMagazine:new(...)
	local o = ISLoadBulletsInMagazine_New(self,...)
	o.stopOnRun = false
	return o
end

function ISUnloadBulletsFromFirearm:new(...)
	local o = ISUnloadBulletsFromFirearm_New(self,...)
	o.stopOnRun = false
	return o
end

function ISUnloadBulletsFromMagazine:new(...)
	local o = ISUnloadBulletsFromMagazine_New(self,...)
	o.stopOnRun = false
	return o
end

function ISRackFirearm:new(...)
	local o = ISRackFirearm_New(self,...)
	o.stopOnRun = false
	return o
end

function ISInsertMagazine:new(...)
	local o = ISInsertMagazine_New(self,...)
	o.stopOnRun = false
	return o
end

function ISReloadWeaponAction:new(...)
	local o = ISReloadWeaponAction_New(self,...)
	o.stopOnRun = false
	return o;
end

function ISEjectMagazine:new(...)
	local o = ISEjectMagazine_New(self,...)
	o.stopOnRun = false
	return o
end

function ISRackFirearm:update()
	ISRackFirearm_Update(self)
	ReloadingControl(self.character)
end

function ISInsertMagazine:update()
	ISInsertMagazine_Update(self)
	ReloadingControl(self.character)
end

function ISReloadWeaponAction:update()
	ISReloadWeaponAction_Update(self)
	ReloadingControl(self.character)
end

function ISLoadBulletsInMagazine:update()
	ISLoadBulletsInMagazine_Update(self)
	ReloadingControl(self.character)
end

function ISUnloadBulletsFromMagazine:update()
	ISUnloadBulletsFromMagazine_Update(self)
	ReloadingControl(self.character)
end

function ISUnloadBulletsFromFirearm:update()
	ISUnloadBulletsFromFirearm_Update(self)
	ReloadingControl(self.character)
end

function ISEjectMagazine:update()
	ISEjectMagazine_Update(self)
	ReloadingControl(self.character)
end