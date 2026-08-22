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

require 'ISUI/ISWorldObjectContextMenu'

LSDanceContextMenu = {}

LSDanceContextMenu.onDancingPartner = function(worldobjects, playerObj, DanceTargetID, DanceProposer)
    --playerObj:getModData().IsDancingFull = true
	PlayerIsDancingToMusic = require("TimedActions/PlayerIsDancingToMusic")
	local player = playerObj
	local actionType = "Bob_PreDancingDefault"
	ISTimedActionQueue.add(PlayerIsDancingToMusic:new(player, actionType));
	sendClientCommand(playerObj, "LS", "AskToDance", {DanceTargetID, DanceProposer})
end

LSDanceContextMenu.onDancing = function(worldobjects, playerObj)
    --playerObj:getModData().IsDancingFull = true
	PlayerIsDancingToMusic = require("TimedActions/PlayerIsDancingToMusic")
	local player = playerObj
	local actionType = "Bob_PreDancingDefault"
	ISTimedActionQueue.add(PlayerIsDancingToMusic:new(player, actionType));
end


LSDanceContextMenu.doBuildMenu = function(player, context, worldobjects, DebugBuildOption)

	local playersList = {};--get players
   -- for playerIndex = 0, getNumActivePlayers()-1 do
    
	local playerObj = getSpecificPlayer(0)
	local currentEndurance = playerObj:getStats():getEndurance()
	local inventory = playerObj:getInventory()	
	local it = inventory:getItems();
	local debugremoveOption = true

	for j = 0, it:size()-1 do
		local item = it:get(j);
		if item:getType() == "Hat_EarMuff_Protectors" or item:getType() == "Hat_EarMuffs" then
			if playerObj:isEquippedClothing(item) then
				return
			end
		end
	end

	if (playerObj ~= nil) and
	(playerObj:hasModData()) and
	(playerObj:getModData().PlayingInstrument ~= nil) and
	(playerObj:getModData().PlayingInstrument == false) and
	(playerObj:getModData().PlayingDJBooth ~= nil) and
	(playerObj:getModData().PlayingDJBooth == false) and
	(playerObj:getModData().IsListeningToJukebox ~= nil) and
	(playerObj:getModData().IsListeningToDJ ~= nil) and
	(playerObj:getModData().IsListeningToJukebox == true or playerObj:getModData().IsListeningToDJ == true) and not
	--(playerObj:getPrimaryHandItem() == nil) and
	--(playerObj:getSecondaryHandItem() == nil) and not
	(playerObj:isSitOnGround()) and not
	(playerObj:isSneaking()) and not
	(playerObj:isRunning()) and not
	(playerObj:isSprinting()) then

		if not (playerObj:getModData().IsDancingFull) then
			if (playerObj:getModData().IsDancingInit == true) then
				--if isKeyDown(Keyboard.KEY_X) then
				local OtherPlayersAround
				OtherPlayersAround = false
				for x = playerObj:getX()-1,playerObj:getX()+1 do
					for y = playerObj:getY()-1,playerObj:getY()+1 do
						local square = getCell():getGridSquare(x,y,playerObj:getZ());
						if square then
							for i = 0,square:getMovingObjects():size()-1 do
								local moving = square:getMovingObjects():get(i);
								if instanceof(moving, "IsoPlayer") then
									table.insert(playersList, moving);
								end
							end
						end
					end
				end

            if #playersList > 0 and playerObj:getModData().LSMoodles["Embarrassed"].Value ~= nil and playerObj:getModData().LSMoodles["Embarrassed"].Value < 0.2 and currentEndurance > 0.3 and debugremoveOption == false then
                for i,v in ipairs(playersList) do
					if v:getUsername() ~= playerObj:getUsername() and
					v:isOutside() == playerObj:isOutside() then
						OtherPlayersAround = true

						if #playersList <= 2 then
						local DanceTargetName = tostring(v:getDescriptor():getForename())
						local DanceTargetSurname = tostring(v:getDescriptor():getSurname())
						local DanceTargetID = v:getOnlineID()
						local DanceProposer = tostring(playerObj:getUsername())
						local PlayerIsDancingToMusicOption = context:addOption(getText("ContextMenu_Dancing_Option"), worldobjects, LSDanceContextMenu.onDancing, playerObj);
						local PlayerIsDancingTogetherToMusicOption = context:addOption(getText("ContextMenu_Dancing_Partner_Option", DanceTargetName.." "..DanceTargetSurname), worldobjects, LSDanceContextMenu.onDancingPartner, playerObj, DanceTargetID, DanceProposer);

					end
				end
                end			
            end
			if OtherPlayersAround == false then
				local PlayerIsDancingToMusicOption = context:addOptionOnTop(getText("ContextMenu_Dancing_Option"), worldobjects, LSDanceContextMenu.onDancing, playerObj);
				PlayerIsDancingToMusicOption.iconTexture = getTexture('media/ui/dance_icon.png')
				local tooltip = ISToolTip:new();
				tooltip:initialise();
				tooltip:setVisible(false);
		
				if playerObj:getModData().LSMoodles["Embarrassed"].Value ~= nil and playerObj:getModData().LSMoodles["Embarrassed"].Value >= 0.2 then
					local contextMenuEmbarrassed = "ContextMenu_Embarrassed"
					PlayerIsDancingToMusicOption.notAvailable = true;
					description = " <RED>" .. getText(contextMenuEmbarrassed);
					tooltip.description = description
					PlayerIsDancingToMusicOption.toolTip = tooltip
					PlayerIsDancingToMusicOption.iconTexture = getTexture('media/ui/danceNo_icon.png')
				elseif currentEndurance <= 0.3 then
					PlayerIsDancingToMusicOption.notAvailable = true;
					description = " <RED>" .. getText("ContextMenu_Exhausted");
					tooltip.description = description
					PlayerIsDancingToMusicOption.toolTip = tooltip
				elseif playerObj:getStats():getPain() > 20 then
					PlayerIsDancingToMusicOption.notAvailable = true;
					description = " <RED>" .. getText("ContextMenu_InPain");
					tooltip.description = description
					PlayerIsDancingToMusicOption.toolTip = tooltip
				--else
				--PlayerIsDancingToMusicOption.iconTexture = getTexture('media/ui/djbooth_icon.png')
				end
				
			elseif OtherPlayersAround == true and #playersList > 2 then
				--local PlayerIsDancingToMusicOption = context:addOption(getText("ContextMenu_Dancing_Option"), worldobjects, LSDanceContextMenu.onDancing, playerObj);
			--print("GROUP OPTION HERE")			
			end
		--end	
			end
		end

	end
	--end
end

