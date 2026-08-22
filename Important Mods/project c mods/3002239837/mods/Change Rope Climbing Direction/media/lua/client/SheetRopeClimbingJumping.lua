local function SheetRopeClimbingJumpingKeyHandler(_keyPressed)
	local player = getPlayer();
	if not player or not player:isClimbing() then	
		return;
	end
	
	-- State change from ClimbRope to ClimbDownRope is no problem because there is a transition declared in xml file
	-- $PZ_DIR/media/actiongroups/player/climbrope/finishclimb.xml
	-- But there is no transition from ClimbDownRope to ClimbRope and PZ doesn't include transitions from mods.
	
	if _keyPressed == getCore():getKey("Forward") then
		if player:getCurrentStateName() == "ClimbDownSheetRopeState" or player:getCurrentStateName() == "IdleState" then
			player:changeState(IdleState:instance());
			player:setbClimbing(true);
			player:setIgnoreMovement(true);
			player:postupdate();
			
			player:getStateMachineParams(ClimbSheetRopeState:instance()):clear();
			player:reportEvent("EventClimbRope");
		end
		
	elseif _keyPressed == getCore():getKey("Backward") then
		if player:getCurrentStateName() == "ClimbSheetRopeState" or player:getCurrentStateName() == "IdleState" then
			player:getStateMachineParams(ClimbDownSheetRopeState:instance()):clear();
			player:reportEvent("EventClimbDownRope");
		end
	end	
end

Events.OnCustomUIKeyPressed.Add(SheetRopeClimbingJumpingKeyHandler);
