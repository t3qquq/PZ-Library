-------------------------------------------------------------------------
-- monkey patches

if (not TPS.Hooked.ISBackButtonWheel_addCommands) then
    TPS.Hooked.ISBackButtonWheel_addCommands = ISBackButtonWheel.addCommands;
end

if (not TPS.Hooked.ISBackButtonWheel_onCommand) then
    TPS.Hooked.ISBackButtonWheel_onCommand = ISBackButtonWheel.onCommand;
end

--[[ ISBackButtonWheel:addCommands()
    monkey patch original addCommands
]]
function ISBackButtonWheel:addCommands()
    TPS.Hooked.ISBackButtonWheel_addCommands(self);
    
	local isPaused = UIManager.getSpeedControls() and UIManager.getSpeedControls():getCurrentGameSpeed() == 0;

	if (not isPaused) then
		if (not ISBackButtonWheel.disablePlayerInfo) then
            self:addSlice(getText("IGUI_BackButton_PlayerInfo"), TraitFactory.getTrait("Marksman"):getTexture(), self.onCommand, self, "TraitsPurchase");
		else
			self:addSlice(nil, nil, nil);
		end
	end
end

--[[ ISBackButtonWheel:onCommand()
    monkey patch original onCommand
]]
function ISBackButtonWheel:onCommand(command)
    TPS.Hooked.ISBackButtonWheel_onCommand(self, command);
    
	local isPaused = UIManager.getSpeedControls() and UIManager.getSpeedControls():getCurrentGameSpeed() == 0;

	if (command == "TraitsPurchase" and not isPaused) then
        if (not TPS.Windows) then
            return
        end
        local player = getSpecificPlayer(self.playerNum);
        if (not player) then
            return
        end
        TPS.Windows[player]:setVisible(true);
		TPS.Windows[player]:addToUIManager();
        setJoypadFocus(self.playerNum, TPS.Windows[player].panel);
	end
end

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISBackButtonWheel.lua