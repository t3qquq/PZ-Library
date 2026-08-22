
-- DEPRECATED
--[[
DiscoBallMenu = {};

function DiscoBallMenu.doBuildMenuTurnOnOff(player, context, worldobjects)

    if getCore():getGameMode()=="LastStand" then
        return;
    end
 
    local thisPlayer = getSpecificPlayer(player)

    if thisPlayer:getVehicle() then
	--print("player in car")
	return; end
    
	local DiscoBall = nil
	local spriteName = nil

	local objects = {}
	for _,object in ipairs(worldobjects) do
		local square = object:getSquare()
		if square then
			for i=1,square:getObjects():size() do
				local thisObject = square:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						--local thisSprite = thisObject:getSprite()
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
						end
					
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
						end
					
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
						end
					
						if customName == "Disco Ball" then
							DiscoBall = thisObject;
							spriteName = thisSpriteName;
						end
					end
				end
			end
		end
	end

	if not DiscoBall then
	--print("not discoball")
	return end

	if not ((SandboxVars.ElecShutModifier > -1 and
	GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
	DiscoBall:getSquare():haveElectricity()) then
	--print("discoball is off no electricity")
	return
	end
	
    --ModData.request("LSDATA")
	--local LSDATA = ModData.getOrCreate("LSDATA")
	--local DiscoBallData = LSDATA[DiscoBallID]

	--if DiscoBallData == nil then
  -- The DiscoBallID is not in the LSDATA table, so we need to add it.
		--DiscoBallData = {}
		--DiscoBallData.onOff = "off"
		--LSDATA[DiscoBallID] = DiscoBallData
		--if isClient() then ModData.transmit("LSDATA") end
	--end

	--if DiscoBallData ~= nil then
	--if DiscoBallData.onOff == nil then
    --DiscoBallData.onOff = "off"
    --if isClient() then ModData.transmit("LSDATA") end
	--end
	--end

	--if DiscoBallData ~= nil and DiscoBallData.onOff == "off" then

    if not DiscoBall:getModData().DiscoBallID then
	DiscoBall:getModData().DiscoBallID = {(tostring(DiscoBall:getX()) .. "," .. tostring(DiscoBall:getY()) .. "," .. tostring(DiscoBall:getZ()))};
	DiscoBall:transmitModData();
	end
    if not DiscoBall:getModData().OnOff then 
	DiscoBall:getModData().OnOff = "off"
	DiscoBall:transmitModData();
	end

	if DiscoBall:getModData().OnOff == "on" then
----------------
		local contextMenu = nil;
		contextMenu = "ContextMenu_Select_Disco_Mode"
		local buildOption = context:addOption(getText(contextMenu));
		local parentMenu = ISContextMenu:getNew(context);
		context:addSubMenu(buildOption, parentMenu)
----------------Default
	local soundFile = "JukeboxTurnOn";
	local contextMenu1 = "ContextMenu_Disco_Mode_Default"
	local Mode = "default"

	parentMenu:addOption(getText(contextMenu1),
		worldobjects,
		DiscoBallMenu.onPlay,
		getSpecificPlayer(player),
		DiscoBall,
		soundFile,
		Mode);	

----------------Romantic
	local soundFile = "JukeboxTurnOn";
	local contextMenu2 = "ContextMenu_Disco_Mode_Valentine"
	local Mode = "valentine"

	parentMenu:addOption(getText(contextMenu2),
		worldobjects,
		DiscoBallMenu.onPlay,
		getSpecificPlayer(player),
		DiscoBall,
		soundFile,
		Mode);	

----------------OFF
	local soundFile = "JukeboxTurnOff";
	local contextMenu = nil;

	contextMenu0 = "ContextMenu_DiscoBall_TurnOff"

	local discoballoptionOff = context:addOption(getText(contextMenu0),
		worldobjects,
		DiscoBallMenu.onTurnOff,
		getSpecificPlayer(player),
		DiscoBall,
		soundFile);	
	discoballoptionOff.iconTexture = getTexture('media/ui/lightbulbOff_icon.png')

	else

	local soundFile = "JukeboxTurnOn";
	local contextMenu = nil;

	contextMenu0 = "ContextMenu_DiscoBall_TurnOn"

	local discoballoptionOn = context:addOption(getText(contextMenu0),
		worldobjects,
		DiscoBallMenu.onTurnOn,
		getSpecificPlayer(player),
		DiscoBall,
		soundFile);	
	discoballoptionOn.iconTexture = getTexture('media/ui/lightbulbOn_icon.png')
	--elseif DiscoBallData ~= nil and DiscoBallData.onOff == "on" then


	--else
	--print("DiscoBallData is nil")
	--return
	end

end

DiscoBallMenu.walkToFront = function(thisPlayer, thisObject)
	local frontSquare = nil
	local controllerSquare = nil
	local spriteName = thisObject:getSprite():getName()
	if not spriteName then
		return false
	end

	local properties = thisObject:getSprite():getProperties()
	
	local facing = nil
	if properties:Is("Facing") then
		facing = properties:Val("Facing")
	else
		return
	end
	
	if facing == "S" then
		frontSquare = thisObject:getSquare():getS()
	elseif facing == "E" then
		frontSquare = thisObject:getSquare():getE()
	elseif facing == "W" then
		frontSquare = thisObject:getSquare():getW()
	elseif facing == "N" then
		frontSquare = thisObject:getSquare():getN()
	end
	
	if not frontSquare then
		return false
	end
	
	if not controllerSquare then
		controllerSquare = thisObject:getSquare()
	end

	if AdjacentFreeTileFinder.privTrySquare(controllerSquare, frontSquare) then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, frontSquare))
		return true
	end
	return false
end

DiscoBallMenu.onTurnOn = function(worldobjects, player, DiscoBall, soundFile)
	if DiscoBallMenu.walkToFront(player, DiscoBall) then
		ISTimedActionQueue.add(DiscoBallOn:new(player, DiscoBall, soundFile));
	end
end

DiscoBallMenu.onTurnOff = function(worldobjects, player, DiscoBall, soundFile)
	if DiscoBallMenu.walkToFront(player, DiscoBall) then
		ISTimedActionQueue.add(DiscoBallOff:new(player, DiscoBall, soundFile));
	end
end

DiscoBallMenu.onPlay = function(worldobjects, player, DiscoBall, soundFile, Mode)
	if DiscoBallMenu.walkToFront(player, DiscoBall) then
		ISTimedActionQueue.add(DiscoBallPlay:new(player, DiscoBall, soundFile, Mode));
	end
end


Events.OnFillWorldObjectContextMenu.Add(DiscoBallMenu.doBuildMenuTurnOnOff);

]]--