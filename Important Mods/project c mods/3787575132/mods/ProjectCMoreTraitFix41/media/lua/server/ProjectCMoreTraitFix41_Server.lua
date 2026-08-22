-- Server-authoritative restore. Ignores client-supplied numbers and recomputes from traits.

local function onClientCommand(module, command, player, args)
	if module ~= ProjectCMoreTraitFix41.MODULE then
		return
	end
	if command ~= ProjectCMoreTraitFix41.COMMAND_REAPPLY then
		return
	end
	if not ProjectCMoreTraitFix41.shouldApply(player) then
		ProjectCMoreTraitFix41.log("server skip duplicate onlineID=" .. tostring(player:getOnlineID()))
		return
	end
	ProjectCMoreTraitFix41.applyMaxWeight(player)
	ProjectCMoreTraitFix41.applyUnwavering(player)
	ProjectCMoreTraitFix41.markApplied(player)
	ProjectCMoreTraitFix41.pruneApplied()
	ProjectCMoreTraitFix41.log("server reapply done onlineID=" .. tostring(player:getOnlineID()))
end

Events.OnClientCommand.Add(onClientCommand)
