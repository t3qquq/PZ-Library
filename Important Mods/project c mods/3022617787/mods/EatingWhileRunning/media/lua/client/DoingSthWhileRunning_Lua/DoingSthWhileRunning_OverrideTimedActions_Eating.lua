--Made by SportXAI
require "TimedActions/ISEatFoodAction"
require "TimedActions/ISDrinkFromBottle"
local function ControlSetting(player)
	local key = getCore():getKey("Run")
	if not getCore():isToggleToRun() then
		player:setRunning(false)
		player:setForceRun(false)
	elseif getCore():isToggleToRun() and player:isRunning() then
		player:setRunning(false)
		player:setForceRun(false)
		if not player:getVariableBoolean("DoingSthWhileRunning_Eating_ToggleToRun") then
			player:setVariable("DoingSthWhileRunning_Eating_Enable",true)
			player:setVariable("DoingSthWhileRunning_Eating_ToggleToRun",true)
		end
	end
	if GameKeyboard.isKeyPressed(key) then
		if not player:getVariableBoolean("DoingSthWhileRunning_Eating_Enable") then
			player:setVariable("DoingSthWhileRunning_Eating_Enable", true)
		else
			player:setVariable("DoingSthWhileRunning_Eating_Enable", false)
		end
	end
end

local ISEatFoodAction_New = ISEatFoodAction.new
local ISDrinkFromBottle_New = ISDrinkFromBottle.new
local ISEatFoodAction_Update = ISEatFoodAction.update
local ISDrinkFromBottle_Update = ISDrinkFromBottle.update
function ISEatFoodAction:new(...)
	local o = ISEatFoodAction_New(self,...)
	o.stopOnRun = false
	return o
end

function ISDrinkFromBottle:new(...)
	local o = ISDrinkFromBottle_New(self,...)
	o.stopOnRun = false
	return o	
end

function ISEatFoodAction:update()
	ISEatFoodAction_Update(self)
	ControlSetting(self.character)
end

function ISDrinkFromBottle:update()
	ISDrinkFromBottle_Update(self)
	ControlSetting(self.character)
end