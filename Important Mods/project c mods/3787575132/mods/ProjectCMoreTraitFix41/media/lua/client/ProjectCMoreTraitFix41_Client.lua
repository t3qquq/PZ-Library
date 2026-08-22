-- Client session restore after load/reconnect. Runs once per IsoPlayer object.
-- Also stops wyweight.lua from overwriting Pack Mule after a server reboot.

local lastPlayerByIndex = {}
local wyweightHooked = false
local lastWyweightLogged = nil

local function restoreLuckImpact()
	if SandboxVars.MoreTraits.LuckImpact then
		luckimpact = SandboxVars.MoreTraits.LuckImpact * 0.01
	else
		luckimpact = 1.0
	end
	ProjectCMoreTraitFix41.log("luckimpact=" .. tostring(luckimpact))
end

local function restoreSessionBaseline(player)
	local md = player:getModData()
	md.itemWeaponBareHands = nil
	md.MotionActive = false
	md.ContainerTraitIllegal = false
	md.ContainerTraitPlayerCurrentPositionX = 0
	md.ContainerTraitPlayerCurrentPositionY = 0
	md.GymGoerStiffnessList = nil
	md.fLastHP = player:getBodyDamage():getOverallBodyHealth()
	md.isSleeping = false
	md.OldCalories = player:getNutrition():getCalories()
	if md.QuickRestActive == true then
		md.QuickRestActive = false
		md.QuickRestEndurance = -1
		md.QuickRestFinished = false
	end
end

local function readWyweightExtra()
	local extra = 0
	if SandboxVars.wyweight.extraweight then
		extra = SandboxVars.wyweight.extraweight
	end
	return extra
end

-- wyweight.lua captures getMaxWeightBase() on OnGameStart into an upvalue `base`,
-- then EveryOneMinute does setMaxWeightBase(base + extra). After a reboot that
-- capture is vanilla 8 (Pack Mule is not saved), so extra 0 still stamps 8 forever.
local function applyMaxWeightWithWyweightExtra(player)
	local extra = readWyweightExtra()
	local before = player:getMaxWeightBase()
	local weight = math.floor(ProjectCMoreTraitFix41.computeMaxWeight(player) + extra)
	player:setMaxWeightBase(weight)
	if lastWyweightLogged ~= weight then
		ProjectCMoreTraitFix41.log("weight " .. tostring(before) .. " -> " .. tostring(weight) .. " wyweight extra=" .. tostring(extra) .. " onlineID=" .. tostring(player:getOnlineID()))
		lastWyweightLogged = weight
	end
end

local function onWyweightMinute()
	applyMaxWeightWithWyweightExtra(getPlayer())
end

local function recaptureWyweightBase()
	if type(wyweight1) == "function" then
		wyweight1()
	end
end

local function hookWyweight()
	if wyweightHooked then
		return
	end
	if type(wyweight) ~= "function" then
		return
	end
	Events.EveryOneMinute.Remove(wyweight)
	Events.EveryOneMinute.Add(onWyweightMinute)
	wyweightHooked = true
	applyMaxWeightWithWyweightExtra(getPlayer())
	recaptureWyweightBase()
	ProjectCMoreTraitFix41.log("wyweight EveryOneMinute replaced")
end

local function onCreatePlayer(playerIndex, player)
	if not player:isLocalPlayer() then
		return
	end
	if lastPlayerByIndex[playerIndex] == player then
		return
	end
	lastPlayerByIndex[playerIndex] = player

	restoreLuckImpact()
	restoreSessionBaseline(player)

	-- Listen host is both client and server. Unwavering += must not run twice on
	-- the same IsoPlayer. Weight set is idempotent so the host still applies it
	-- here; otherwise wyweight OnGameStart can capture vanilla 8 before the
	-- server command returns.
	if isClient() then
		if not isServer() then
			ProjectCMoreTraitFix41.applyMaxWeight(player)
			ProjectCMoreTraitFix41.applyUnwavering(player)
		else
			ProjectCMoreTraitFix41.applyMaxWeight(player)
		end
		sendClientCommand(player, ProjectCMoreTraitFix41.MODULE, ProjectCMoreTraitFix41.COMMAND_REAPPLY, {})
	else
		ProjectCMoreTraitFix41.applyMaxWeight(player)
		ProjectCMoreTraitFix41.applyUnwavering(player)
	end

	recaptureWyweightBase()
	ProjectCMoreTraitFix41.log("session reinit done playerIndex=" .. tostring(playerIndex) .. " onlineID=" .. tostring(player:getOnlineID()))
end

local function onGameStart()
	hookWyweight()
	if not wyweightHooked then
		ProjectCMoreTraitFix41.applyMaxWeight(getPlayer())
	end
end

Events.OnCreatePlayer.Add(onCreatePlayer)
Events.OnGameStart.Add(onGameStart)
