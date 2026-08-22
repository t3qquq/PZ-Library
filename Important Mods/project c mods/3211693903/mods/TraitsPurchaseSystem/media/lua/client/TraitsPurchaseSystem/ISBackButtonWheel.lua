--[[ ISBackButtonWheel:addCommands()
    monkey patch original addCommands
]]
local originaladdCommands = ISBackButtonWheel.addCommands;
function ISBackButtonWheel:addCommands()
    originaladdCommands(self);
	local isPaused = UIManager.getSpeedControls() and UIManager.getSpeedControls():getCurrentGameSpeed() == 0;

	if not isPaused then
		if not ISBackButtonWheel.disablePlayerInfo then
            self:addSlice(getText("IGUI_BackButton_PlayerInfo"), TraitFactory.getTrait("Marksman"):getTexture(), self.onCommand, self, "TraitsPurchase");
		else
			self:addSlice(nil, nil, nil);
		end
	end
end

--[[ ISBackButtonWheel:onCommand()
    monkey patch original onCommand
]]
local originalonCommand = ISBackButtonWheel.onCommand;
function ISBackButtonWheel:onCommand(command)
    originalonCommand(self, command);
	local isPaused = UIManager.getSpeedControls() and UIManager.getSpeedControls():getCurrentGameSpeed() == 0;

	if command == "TraitsPurchase" and not isPaused then
        local player = getSpecificPlayer(self.playerNum);
        windows[player]:setVisible(true);
		windows[player]:addToUIManager();
        setJoypadFocus(self.playerNum, windows[player].panel);
	end
end

-- /reloadlua client/ISUI/TraitsPurchaseSystem/ISBackButtonWheel.lua