--Made by SportXAI
local function AnimSpeed()
	if not isIngameState() then return end
	if getSpecificPlayer(0) == nil then return end

	local player = getSpecificPlayer(0)
	local perk = math.min(0.9 + player:getPerkLevel(Perks.Sprinting) / 20.0, 1.5)
	local count = math.min(perk * 2.5, 1.0)
	local speedScale = math.min(math.abs((count * perk) + 0.1) , 1.2)
	player:setVariable("RunAndReload_AnimSpeed", speedScale)
end

Events.OnTick.Add(AnimSpeed)