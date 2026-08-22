-- Runtime restoration helpers for More Traits on Project Zomboid b41.
-- TraitFactory registration stays in More Traits; this file never adds traits.

ProjectCMoreTraitFix41 = ProjectCMoreTraitFix41 or {}

ProjectCMoreTraitFix41.MODULE = "ProjectCMoreTraitFix41"
ProjectCMoreTraitFix41.COMMAND_REAPPLY = "reapply"

local UNWAVERING_SCRATCH = 30
local UNWAVERING_CUT = 30
local UNWAVERING_DEEP = 60
local UNWAVERING_BURN = 60
local MAX_WEIGHT_CAP = 50

local appliedPlayers = {}

local function log(message)
	DebugLog.log("ProjectCMoreTraitFix41: " .. message)
end

function ProjectCMoreTraitFix41.log(message)
	log(message)
end

function ProjectCMoreTraitFix41.shouldApply(player)
	local i = 1
	while i <= #appliedPlayers do
		if appliedPlayers[i] == player then
			return false
		end
		i = i + 1
	end
	return true
end

function ProjectCMoreTraitFix41.markApplied(player)
	local i = 1
	while i <= #appliedPlayers do
		if appliedPlayers[i] == player then
			return
		end
		i = i + 1
	end
	table.insert(appliedPlayers, player)
end

function ProjectCMoreTraitFix41.pruneApplied()
	local players = getOnlinePlayers()
	if not players then
		return
	end
	local keep = {}
	local i = 1
	while i <= #appliedPlayers do
		local stored = appliedPlayers[i]
		local found = false
		local j = 0
		while j < players:size() do
			if players:get(j) == stored then
				found = true
				break
			end
			j = j + 1
		end
		if not found then
			local n = getNumActivePlayers()
			local k = 0
			while k < n do
				if getSpecificPlayer(k) == stored then
					found = true
					break
				end
				k = k + 1
			end
		end
		if found then
			table.insert(keep, stored)
		end
		i = i + 1
	end
	appliedPlayers = keep
end

-- Same formula as More Traits checkWeight() (client MoreTraits.lua).
function ProjectCMoreTraitFix41.computeMaxWeight(player)
	local strength = player:getPerkLevel(Perks.Strength)
	local mule = 10
	local mouse = 6
	local default = 8
	local globalmod = 0
	if SandboxVars.MoreTraits.WeightPackMule then
		mule = SandboxVars.MoreTraits.WeightPackMule
	end
	if SandboxVars.MoreTraits.WeightPackMouse then
		mouse = SandboxVars.MoreTraits.WeightPackMouse
	end
	if SandboxVars.MoreTraits.WeightDefault then
		default = SandboxVars.MoreTraits.WeightDefault
	end
	if SandboxVars.MoreTraits.WeightGlobalMod then
		globalmod = SandboxVars.MoreTraits.WeightGlobalMod
	end
	local weight = math.floor(default + globalmod)
	if player:HasTrait("packmule") then
		weight = math.floor(mule + strength / 5 + globalmod)
	elseif player:HasTrait("packmouse") then
		weight = math.floor(mouse + globalmod)
	end
	if getActivatedMods():contains("DracoExpandedTraits") and player:HasTrait("Hoarder") then
		weight = math.floor(weight * 1.25)
	end
	if weight > MAX_WEIGHT_CAP then
		weight = MAX_WEIGHT_CAP
	end
	return weight
end

function ProjectCMoreTraitFix41.applyMaxWeight(player)
	local before = player:getMaxWeightBase()
	local weight = ProjectCMoreTraitFix41.computeMaxWeight(player)
	player:setMaxWeightBase(weight)
	log("weight " .. tostring(before) .. " -> " .. tostring(weight) .. " onlineID=" .. tostring(player:getOnlineID()))
end

function ProjectCMoreTraitFix41.applyUnwavering(player)
	if not player:HasTrait("unwavering") then
		return
	end
	local parts = player:getBodyDamage():getBodyParts()
	local n = 0
	local i = 0
	while i < parts:size() do
		local bodyPart = parts:get(i)
		bodyPart:setScratchSpeedModifier(bodyPart:getScratchSpeedModifier() + UNWAVERING_SCRATCH)
		bodyPart:setCutSpeedModifier(bodyPart:getCutSpeedModifier() + UNWAVERING_CUT)
		bodyPart:setDeepWoundSpeedModifier(bodyPart:getDeepWoundSpeedModifier() + UNWAVERING_DEEP)
		bodyPart:setBurnSpeedModifier(bodyPart:getBurnSpeedModifier() + UNWAVERING_BURN)
		n = n + 1
		i = i + 1
	end
	player:getModData().UnwaveringInjurySpeedChanged = true
	log("unwavering parts=" .. tostring(n) .. " scratch0=" .. tostring(parts:get(0):getScratchSpeedModifier()) .. " onlineID=" .. tostring(player:getOnlineID()))
end

local function verifyBoot()
	local ids = { "gunspecialist", "packmule", "unwavering", "superimmune", "terminator" }
	local i = 1
	while i <= #ids do
		local id = ids[i]
		local trait = TraitFactory.getTrait(id)
		if trait == nil then
			log("MISSING trait '" .. id .. "' isClient=" .. tostring(isClient()) .. " isServer=" .. tostring(isServer()))
		else
			log("OK trait '" .. id .. "'")
		end
		i = i + 1
	end
end

Events.OnGameBoot.Add(verifyBoot)
