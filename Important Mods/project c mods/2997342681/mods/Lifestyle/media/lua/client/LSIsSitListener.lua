--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

require "Helper/CheckPlayerHelper"



local function LSIsSitListenerClear(playerIndex, player)
	player:clearVariable("SittingToggleStart")
	player:clearVariable("SittingToggleLoop")
	player:clearVariable("IsSittingInChair")
end

--[[
sitToggleStart = false
sitToggleLoop = false
sitToggleChair = false
sitToggleLoopG = false
sitToggleChairP = false
sitToggleChairG = false

function LSIsSitListener()
	local thisPlayer = getPlayer()

	if not isClient() then return; end

	if thisPlayer and thisPlayer:hasModData() and not thisPlayer:isDead() then
	local playerData = thisPlayer:getModData()

		if sitToggleStart == true and ((thisPlayer:getVariableString("SittingToggleStart") ~= "N") and (thisPlayer:getVariableString("SittingToggleStart") ~= "S")) then--clear the variable
			ScanForPlayers("ChangeAnimVar", "SittingToggleStart", false, false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleStart", false})
			sitToggleStart = false
			--print("SITLISTENER EVENT: toggle start cleared")
		elseif sitToggleStart == false and (thisPlayer:getVariableString("SittingToggleStart") == "N") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleStart", "N", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleStart", "N"})
			sitToggleStart = true
			--print("SITLISTENER EVENT: toggle start N")
		elseif sitToggleStart == false and (thisPlayer:getVariableString("SittingToggleStart") == "S") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleStart", "S", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleStart", "N"})
			sitToggleStart = true
			--print("SITLISTENER EVENT: toggle start N")
		end

		if (sitToggleLoop == true or sitToggleLoopG == true) and (thisPlayer:getVariableString("SittingToggleLoop") == "") then--clear the variable
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", false, false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", false})
			sitToggleLoop = false
			sitToggleLoopG = false
			--print("SITLISTENER EVENT: toggle loop cleared")
		elseif sitToggleLoop == false and (thisPlayer:getVariableString("SittingToggleLoop") == "N") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", "N", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", "N"})
			sitToggleLoop = true
			sitToggleLoopG = false
			--print("SITLISTENER EVENT: toggle loop N")
		elseif sitToggleLoop == false and (thisPlayer:getVariableString("SittingToggleLoop") == "S") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", "S", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", "N"})
			sitToggleLoop = true
			sitToggleLoopG = false
			--print("SITLISTENER EVENT: toggle loop N")
		elseif sitToggleLoopG == false and (thisPlayer:getVariableString("SittingToggleLoop") == "GetUp") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", "GetUp", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", "GetUp"})
			sitToggleLoopG = true
			sitToggleLoop = false
			--print("SITLISTENER EVENT: toggle loop GetUp")
		end

		if (sitToggleChair == true or sitToggleChairP == true or sitToggleChairG == true) and (thisPlayer:getVariableString("IsSittingInChair") == "") then--clear the variable
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", false, false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", false})
			sitToggleChair = false
			sitToggleChairP = false
			sitToggleChairG = false
			--print("SITLISTENER EVENT: toggle chair cleared")
		elseif sitToggleChair == false and (thisPlayer:getVariableString("IsSittingInChair") == "IsSitting") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsSitting", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "IsSitting"})
			sitToggleChair = true
			sitToggleChairG = false
			sitToggleChairP = false
			--print("SITLISTENER EVENT: toggle chair IsSitting")
		elseif sitToggleChair == false and (thisPlayer:getVariableString("IsSittingInChair") == "IsSittingS") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsSittingS", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "IsSitting"})
			sitToggleChair = true
			sitToggleChairG = false
			sitToggleChairP = false
			--print("SITLISTENER EVENT: toggle chair IsSitting")
		elseif sitToggleChairP == false and (thisPlayer:getVariableString("IsSittingInChair") == "IsPlayingGuitar") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsPlayingGuitar", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "IsPlayingGuitar"})
			sitToggleChairP = true
			sitToggleChair = false
			sitToggleChairG = false
			--print("SITLISTENER EVENT: toggle chair IsPlayingGuitar")
		elseif sitToggleChairP == false and (thisPlayer:getVariableString("IsSittingInChair") == "IsPlayingGuitarS") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsPlayingGuitarS", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "IsPlayingGuitar"})
			sitToggleChairP = true
			sitToggleChair = false
			sitToggleChairG = false
			--print("SITLISTENER EVENT: toggle chair IsPlayingGuitar")
		elseif sitToggleChairG == false and (thisPlayer:getVariableString("IsSittingInChair") == "GetUp") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "GetUp", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "GetUp"})
			sitToggleChairG = true
			sitToggleChair = false
			sitToggleChairP = false
			--print("SITLISTENER EVENT: toggle chair GetUp")
		end


	end
end

function LSIsSitListenerM()
	local thisPlayer = getPlayer()

	if not isClient() then return; end

	if thisPlayer and thisPlayer:hasModData() and not thisPlayer:isDead() then
	local playerData = thisPlayer:getModData()

		if (thisPlayer:getVariableString("SittingToggleStart") ~= "N") and (thisPlayer:getVariableString("SittingToggleStart") ~= "S") then--clear the variable
			ScanForPlayers("ChangeAnimVar", "SittingToggleStart", false, false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleStart", false})
			--print("SITLISTENER EVENT: toggle start cleared")
		elseif (thisPlayer:getVariableString("SittingToggleStart") == "N") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleStart", "N", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleStart", "N"})
		elseif (thisPlayer:getVariableString("SittingToggleStart") == "S") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleStart", "S", false)
		end

		if (thisPlayer:getVariableString("SittingToggleLoop") == "") then--clear the variable
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", false, false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", false})
		elseif (thisPlayer:getVariableString("SittingToggleLoop") == "N") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", "N", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", "N"})
			--print("SITLISTENER EVENT: toggle loop N")
		elseif (thisPlayer:getVariableString("SittingToggleLoop") == "S") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", "S", false)
		elseif (thisPlayer:getVariableString("SittingToggleLoop") == "GetUp") then
			ScanForPlayers("ChangeAnimVar", "SittingToggleLoop", "GetUp", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "SittingToggleLoop", "GetUp"})
		end

		if (thisPlayer:getVariableString("IsSittingInChair") == "") then--clear the variable
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", false, false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", false})
		elseif (thisPlayer:getVariableString("IsSittingInChair") == "IsSitting") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsSitting", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "IsSitting"})
			--print("SITLISTENER EVENT: toggle chair IsSitting")
		elseif (thisPlayer:getVariableString("IsSittingInChair") == "IsSittingS") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsSittingS", false)
		elseif (thisPlayer:getVariableString("IsSittingInChair") == "IsPlayingGuitar") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsPlayingGuitar", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "IsPlayingGuitar"})
			--print("SITLISTENER EVENT: toggle chair IsPlayingGuitar")
		elseif (thisPlayer:getVariableString("IsSittingInChair") == "IsPlayingGuitarS") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "IsPlayingGuitarS", false)
		elseif (thisPlayer:getVariableString("IsSittingInChair") == "GetUp") then
			ScanForPlayers("ChangeAnimVar", "IsSittingInChair", "GetUp", false)
			--sendClientCommand("LS", "ChangeAnimVar", {thisPlayer:getDisplayName(), "IsSittingInChair", "GetUp"})
			--print("SITLISTENER EVENT: toggle chair GetUp")
		end


	end
end

Events.EveryOneMinute.Add(LSIsSitListener)
Events.EveryOneMinute.Add(LSIsSitListenerM)
]]--
Events.OnCreatePlayer.Add(LSIsSitListenerClear)