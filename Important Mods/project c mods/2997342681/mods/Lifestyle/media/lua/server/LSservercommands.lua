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

local LS_Commands = {}

LS_Commands["logAmbition"] = function(_, arg)
	local currentTime = " on ["..tostring(PZCalendar.getInstance():getTime()).."] "
	local admin, id = "", arg[1] or ""
	if arg[5] then admin = " with admin rights."; end
    local text = id.." - [Player:"..arg[2].."/Char:"..arg[6].."] concluded ambition ["..arg[4].."] "..currentTime..admin
	local file = getFileWriter("LSAmbitionLog.log",true,true)
	if not file then return; end -- failed to write file
	file:write(text.."\n")
	file:close()
end

LS_Commands["CompleteTargetAmbt"] = function(_, arg)
    local Target = getPlayerByOnlineID(arg[1])
    local Target_id = arg[1]
	local ambtName = arg[2]
    sendServerCommand(Target, "LS", "CompleteAmbtSelf", {Target_id, ambtName})
end

LS_Commands["ResetTargetAmbt"] = function(_, arg)
    local Target = getPlayerByOnlineID(arg[1])
    local Target_id = arg[1]
	local ambtName = arg[2]
    sendServerCommand(Target, "LS", "ResetAmbtSelf", {Target_id, ambtName})
end

LS_Commands["LSSCTest"] = function(_, arg)

    local Argument = arg[1]


	if not Argument then
		--print("server received LSSCTest from client and Argument is nil")
	else
		--print("server received LSSCTest from client")
	end

end

LS_Commands["InteractionStart"] = function(_, arg)

    local Target = getPlayerByOnlineID(arg[1])
    local Target_id = arg[1]
   -- local Source = arg[2]
	local Source = arg[2]
    --local SourceX = arg[3]
    --local SourceY = arg[4]
	local Interaction = arg[3]
	local IsClose = arg[4]
	local actionArg = arg[5]
	--print("server received InteractionStart from client and is sending the command")
   -- sendServerCommand(Target, "LS", "WasAskedToInteract", {Target_id, Source, SourceX, SourceY, Interaction, IsClose, actionArg})
    sendServerCommand(Target, "LS", "WasAskedToInteract", {Target_id, Source, Interaction, IsClose, actionArg})
end

LS_Commands["StopOrStartInteraction"] = function(_, arg)

    local Target = getPlayerByOnlineID(arg[1])
    local Target_id = arg[1]
	local InteractionState = arg[2]
	--print("server received StopOrStartInteraction from client and is sending the command")
    sendServerCommand(Target, "LS", "ChangeInteractionState", {Target_id, InteractionState})

end

LS_Commands["makeNauseous"] = function(_, arg)
    local Target = getPlayerByOnlineID(arg[1])
    local Target_id = arg[1]
    sendServerCommand(Target, "LS", "makeNauseous", {Target_id})
end

LS_Commands["SendGetEmbarrassed"] = function(_, arg)

    local Target = getPlayerByOnlineID(arg[1])
    local Target_id = arg[1]
	--print("server received SendGetEmbarrassed from client and is sending the command")
    sendServerCommand(Target, "LS", "GetEmbarrassed", {Target_id})

end

LS_Commands["AddDirtPuddle"] = function(_, arg)
	LSServerCommandHandler("CreateDirtPuddle", arg)
end

LS_Commands["DebugAddLitter"] = function(_, arg)

    local Sx = arg[1]
    local Sy = arg[2]
	local Sz = arg[3]
	local SolidOrOverlay = arg[4]
	local LitterSprite = arg[5]
	local AvailableFloorList = {}
	local targetFloor
	local sSquare = getCell():getGridSquare(Sx, Sy, Sz)

  	for x = Sx-1,Sx+1 do---get x range
		for y = Sy-1,Sy+1 do----get y range

			local thisSquare = getCell():getGridSquare(x, y, Sz)---get grid square (our radius)
        
			if thisSquare and sSquare and thisSquare:getRoom() == sSquare:getRoom() and thisSquare:isOutside() == sSquare:isOutside() and thisSquare:isInARoom() and thisSquare:getFloor() and not 
			thisSquare:isSolid() and not thisSquare:isSolidTrans() then
            
			for i=0,thisSquare:getObjects():size()-1 do-----------search objects for each square on the radius (floor counts as an object)
				local ThisObject = thisSquare:getObjects():get(i);
				if instanceof(ThisObject, "IsoObject") then
				local object = ThisObject
				local hasSolidL = false
				if object then--solid litter is the result of direct actions and as such can happen anywhere the action takes place
					local hasOverlayL = false
					local attachedsprite = object:getAttachedAnimSprite()
					if object:getTextureName() and
					(luautils.stringStarts(object:getTextureName(), "overlay_messages") or 
					luautils.stringStarts(object:getTextureName(), "overlay_graffiti") or 
					--luautils.stringStarts(object:getTextureName(), "floors_burnt") or 
					luautils.stringStarts(object:getTextureName(), "overlay_blood") or 
					luautils.stringStarts(object:getTextureName(), "blood_floor") or
					luautils.stringStarts(object:getTextureName(), "overlay_grime") or 
					--luautils.stringStarts(object:getTextureName(), "trash_") or 
					luautils.stringStarts(object:getTextureName(), "trash&junk") or 
					luautils.stringStarts(object:getTextureName(), "d_floorleaves") or 
					luautils.stringStarts(object:getTextureName(), "d_trash")) then-----------if object already has solid litter then do not add more
						hasSolidL = true
					end
					if object:getOverlaySprite() and object:getOverlaySprite():getName() and
					(luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_messages") or 
					luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_graffiti") or 
					--luautils.stringStarts(object:getOverlaySprite():getName(), "floors_burnt") or 
					luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_blood") or 
					luautils.stringStarts(object:getOverlaySprite():getName(), "blood_floor") or
					luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_grime") or 
					luautils.stringStarts(object:getOverlaySprite():getName(), "trash_") or 
					luautils.stringStarts(object:getOverlaySprite():getName(), "trash&junk") or 
					luautils.stringStarts(object:getOverlaySprite():getName(), "d_floorleaves") or 
					luautils.stringStarts(object:getOverlaySprite():getName(), "d_trash") or
					luautils.stringStarts(object:getOverlaySprite():getName(), "LS_HScraps") or
					luautils.stringStarts(object:getOverlaySprite():getName(), "LS_Scraps")) then-----------if object already has overlay litter then do not add more
						hasOverlayL = true
					end
					if object and attachedsprite and object:isFloor() then--overlays such as dirt and grime almost always occur based on random factors and movement so it only happens indoors
						for n=1,attachedsprite:size() do
							local sprite = attachedsprite:get(n-1)
							if sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() and
							(luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_messages") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_graffiti") or 
							--luautils.stringStarts(sprite:getParentSprite():getName(), "floors_burnt") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_blood") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "blood_floor") or
							luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_grime") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "trash_") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "trash&junk") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "d_floorleaves") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "LS_HScraps") or 
							luautils.stringStarts(sprite:getParentSprite():getName(), "d_trash")) then-----------if object already has overlay litter then do not add more
								hasOverlayL = true
							end
						end
								
					end
					if SolidOrOverlay == 2 and not hasOverlayL then
						table.insert(AvailableFloorList, object)
					end
				end
					if SolidOrOverlay == 1 and not hasSolidL then
						table.insert(AvailableFloorList, thisSquare)
					end
					
				end
				end
				
			end
			
		end
		
	end

	if #AvailableFloorList > 0 then
		local randomTile = ZombRand(#AvailableFloorList) + 1
		targetFloor = AvailableFloorList[randomTile]
		if targetFloor then
			if SolidOrOverlay == 1 then
				local NewLitterObj = IsoObject.new(targetFloor, LitterSprite)
				targetFloor:AddTileObject(NewLitterObj)
				NewLitterObj:transmitCompleteItemToClients()
				targetFloor:transmitAddObjectToSquare(NewLitterObj, -1)
				--targetFloor:transmitAddObjectToSquare(NewLitterObj.javaObject, -1)
			
			
			elseif SolidOrOverlay == 2 then
				--targetFloor:setOverlaySprite(LitterSprite, 1, 1, 1, 1, true)--string/transmit
				--targetFloor:setOverlaySprite(LitterSprite, true)--string/transmit
				--targetFloor:transmitUpdatedSpriteToClients()

				local square = targetFloor:getSquare()
				local objOnFloor
				if square then
					for i=1,square:getObjects():size() do
						local thisObject = square:getObjects():get(i-1)
						if thisObject then
							local objSprite = thisObject:getSprite()
							if objSprite then
								local objProperties = objSprite:getProperties()
								if objProperties:Is("BlocksPlacement") then
									objOnFloor = true
								end
							end
						end
					end
					if not objOnFloor then
						targetFloor:setOverlaySprite(LitterSprite, true)--string/transmit
						targetFloor:transmitUpdatedSpriteToClients()
					end
				end
				
				--targetFloor:setOverlaySprite(LitterSprite, 1, 1, 1, 1, false)--string/transmit
				--if not objOnFloor then
				--	targetFloor:setOverlaySprite(LitterSprite, true)--string/transmit
				--	targetFloor:transmitUpdatedSpriteToClients()
				--end
				
			end
		end
	end
end

LS_Commands["RemoveDirtTileDebug"] = function(_, arg)

    local x = arg[1]
	local y = arg[2]
	local z = arg[3]
	local range = arg[4]

	local square = getCell():getGridSquare(x,y,z)
	if not square then return; end
	local sqrX, sqrY = square:getX(), square:getY()

	local listFull = {"overlay_grime","trash&junk","trash_","d_floorleaves","d_trash","LS_Scraps","brokenglass_",
	"overlay_messages","overlay_graffiti","overlay_blood","LS_HScraps","blood_floor"}

  	for x = sqrX-range,sqrX+range do
		for y = sqrY-range,sqrY+range do
			local thisSqr = getCell():getGridSquare(x,y,z)
			if thisSqr then
				local mustRemove = {}
				for j=0,thisSqr:getObjects():size()-1 do
					if j < 0 or j > thisSqr:getObjects():size() then break; end
					local object = thisSqr:getObjects():get(j)
					if object then
						local texName = object:getTextureName()
						local spriteName = object:getOverlaySprite() and object:getOverlaySprite():getName()
						local hasChange

						for n=1, #listFull do
							if texName and luautils.stringStarts(texName, listFull[n]) then table.insert(mustRemove, object); break; end
							if spriteName and luautils.stringStarts(spriteName, listFull[n]) then object:setOverlaySprite(nil, true); hasChange = true; spriteName = false; end
							local attachedsprite = object:getAttachedAnimSprite()
							if attachedsprite then
								for i=0,attachedsprite:size()-1 do
									if i < 0 or i > attachedsprite:size() then break; end
									local sprite = attachedsprite:get(i)
									local spriteParentName = sprite and sprite:getParentSprite() and sprite:getParentSprite():getName()
									if spriteParentName and luautils.stringStarts(spriteParentName, listFull[n]) then hasChange = true; object:RemoveAttachedAnim(i); break; end
								end
							end
						end
						if hasChange then object:transmitUpdatedSpriteToClients(); end
						--if mustRemove then thisSqr:transmitRemoveItemFromSquare(object); thisSqr:RemoveTileObject(object); end
					end
				end
				if #mustRemove > 0 then
					for n=1, #mustRemove do
						thisSqr:transmitRemoveItemFromSquare(mustRemove[n]); thisSqr:RemoveTileObject(mustRemove[n]);
					end
				end
			end
		end
	end

end


LS_Commands["RemoveDirtTile"] = function(_, arg)

    local x = arg[1]
    local y = arg[2]
	local z = arg[3]
	local isHeavy = arg[4]

    local thisSqr = getCell():getGridSquare(x, y, z)
        
    if not thisSqr then return; end

	local listDirt = {"overlay_grime","trash&junk","trash_","d_floorleaves","d_trash","LS_Scraps"}
	local listBlood = {"overlay_messages","overlay_graffiti","overlay_blood","LS_HScraps","blood_floor"}
	local mustRemove = {}
	for j=0,thisSqr:getObjects():size()-1 do
		if j < 0 or j > thisSqr:getObjects():size() then break; end
		local object = thisSqr:getObjects():get(j)
		if object then
			local attachedsprite = object:getAttachedAnimSprite()
			local texName = object:getTextureName()
			local spriteName = object:getOverlaySprite() and object:getOverlaySprite():getName()

			if not isHeavy then
				for n=1, #listDirt do
					if texName and luautils.stringStarts(texName, listDirt[n]) then table.insert(mustRemove, object); break; end
					if spriteName and luautils.stringStarts(spriteName, listDirt[n]) then object:setOverlaySprite(nil, true); object:transmitUpdatedSpriteToClients(); break; end
					if attachedsprite then
						local removed
						for i=0,attachedsprite:size()-1 do
							if i < 0 or i > attachedsprite:size() then break; end
							local sprite = attachedsprite:get(i)
							local spriteParentName = sprite and sprite:getParentSprite() and sprite:getParentSprite():getName()
							if spriteParentName and luautils.stringStarts(spriteParentName, listDirt[n]) then removed = true; object:RemoveAttachedAnim(i); object:transmitUpdatedSpriteToClients(); break; end
						end
						if removed then break; end
					end
				end
			else
				for n=1, #listBlood do
					if texName and luautils.stringStarts(texName, listBlood[n]) then table.insert(mustRemove, object); break; end
					if spriteName and luautils.stringStarts(spriteName, listBlood[n]) then object:setOverlaySprite(nil, true); object:transmitUpdatedSpriteToClients(); break; end
					if attachedsprite then
						local removed
						for i=0,attachedsprite:size()-1 do
							if i < 0 or i > attachedsprite:size() then break; end
							local sprite = attachedsprite:get(i)
							local spriteParentName = sprite and sprite:getParentSprite() and sprite:getParentSprite():getName()
							if spriteParentName and luautils.stringStarts(spriteParentName, listBlood[n]) then removed = true; object:RemoveAttachedAnim(i); object:transmitUpdatedSpriteToClients(); break; end
						end
						if removed then break; end
					end
				end
			end
		end
	end
	if #mustRemove > 0 then
		for n=1, #mustRemove do
			thisSqr:transmitRemoveItemFromSquare(mustRemove[n]); thisSqr:RemoveTileObject(mustRemove[n]);
		end
	end

end


LS_Commands["TeleportSittingLocation"] = function(_, arg)

    local TargetName = getPlayerByOnlineID(arg[1])
    local TargetName_id = arg[1]
    local SourcePlayerName = arg[2]
    local teleportX = arg[3][1]
	local teleportY = arg[3][2]
	local NSvar = arg[3][3]
	--print("server received from client and is sending the command")
    sendServerCommand(TargetName, "LS", "TeleportSittingLocation", {SourcePlayerName, teleportX, teleportY, NSvar})

    local otherPlayers = getOnlinePlayers()
    
	if otherPlayers then
	
    for index = 1, otherPlayers:size() do
		local sourcePlayer = otherPlayers:get(index-1)

        if sourcePlayer and sourcePlayer:getDisplayName() == SourcePlayerName then
			--thisPlayer:Say("teleporting " .. tostring(sourcePlayer:getDisplayName()))
            if teleportX and teleportY then
				sourcePlayer:setY(teleportY)
				sourcePlayer:setX(teleportX)
				sourcePlayer:setLy(teleportY)
				sourcePlayer:setLx(teleportX)
				
				if not string.match(tostring(sourcePlayer:getCurrentState()), "PlayerSitOnGroundState") then
					sourcePlayer:setVariable("SittingToggleStart", NSvar)
					sourcePlayer:reportEvent("EventSitOnGround");
					sourcePlayer:setVariable("SittingToggleLoop", NSvar)
				end
				
            end

            break

        end

    end

	end

end

LS_Commands["ChangeAnimVarMulti"] = function(_, arg)

    local SourcePlayerName = arg[1]
    local AnimType = arg[2]
	local AnimVar = arg[3]
    local AnimType2 = arg[4]
	local AnimVar2 = arg[5]
	--print("server received from client and is sending the command")
    sendServerCommand("LS", "ChangeAnimVarMulti", {SourcePlayerName, AnimType, AnimVar, AnimType2, AnimVar2})

    local otherPlayers = getOnlinePlayers()
 
    --for index = 0, getOnlinePlayers():size() - 1 do

        --local sourcePlayer = getOnlinePlayers():get(index)
	if otherPlayers then
	
    for index = 1, otherPlayers:size() do
		local sourcePlayer = otherPlayers:get(index-1)

       -- if sourcePlayer:getDisplayName() == SourcePlayerName and sourcePlayer:getDisplayName() ~= thisPlayer:getDisplayName() then
        if sourcePlayer and sourcePlayer:getDisplayName() == SourcePlayerName then
			--thisPlayer:Say("source is " .. tostring(sourcePlayer:getDisplayName()))
			
            if AnimVar then
                sourcePlayer:setVariable(AnimType, AnimVar)
				if AnimType == "SittingToggleStart" and ((AnimVar == "N") or (AnimVar == "S")) then
					--thisPlayer:Say("reporting eventsitOnGround")
					sourcePlayer:reportEvent("EventSitOnGround")
				end
            else
                sourcePlayer:clearVariable(AnimType)
            end

            if AnimVar2 then
                sourcePlayer:setVariable(AnimType2, AnimVar2)
				if AnimType2 == "SittingToggleStart" and ((AnimVar2 == "N") or (AnimVar2 == "S")) then
					--thisPlayer:Say("reporting eventsitOnGround")
					sourcePlayer:reportEvent("EventSitOnGround")
				end
            else
                sourcePlayer:clearVariable(AnimType2)
            end

            break

        end

    end

	end

end

LS_Commands["ChangeAnimVar"] = function(_, arg)

    local TargetName = getPlayerByOnlineID(arg[1])
    local TargetName_id = arg[1]
    local SourcePlayerName = arg[2]
	local args = arg[3]
    sendServerCommand(TargetName, "LS", "ChangeAnimVar", {SourcePlayerName, args})

    local otherPlayers = getOnlinePlayers()
	if not otherPlayers then return; end
	
    for index = 1, otherPlayers:size() do
		local sourcePlayer = otherPlayers:get(index-1)
        if sourcePlayer and sourcePlayer:getDisplayName() == SourcePlayerName then
			for n=1, #args, 2 do
				if n == #args then break; end
				if args[n] and type(args[n]) == "string" then
					if args[n+1] then
						sourcePlayer:setVariable(args[n], args[n+1])
						if args[n] == "SittingToggleStart" then sourcePlayer:reportEvent("EventSitOnGround"); end
					else
						sourcePlayer:clearVariable(args[n])
					end
				end
			end
            break
        end
    end
end

LS_Commands["IsPlayingMusic"] = function(_, arg)

    local listener = getPlayerByOnlineID(arg[1])
    local listener_id = arg[1]
    local SourceMusiclvl = arg[2]
	--print("server received from client and is sending the command")
    sendServerCommand(listener, "LS", "IsListeningToMusic", {listener_id, SourceMusiclvl})

end

LS_Commands["IsStartingDuet"] = function(_, arg)

    local currentPerformer = getPlayerByOnlineID(arg[1])
    local currentPerformer_id = arg[1]
    local SourceWaitingDuet = arg[2]
	--print("server received from client and is sending the command")
    sendServerCommand(currentPerformer, "LS", "IsStartingDuet", {currentPerformer_id, SourceWaitingDuet})

end

LS_Commands["IsPlayingDJ"] = function(_, arg)

    local DJlistener = getPlayerByOnlineID(arg[1])
    local DJlistener_id = arg[1]
    local SourceMusiclvl = arg[2]
	local SourceDJ = arg[3]
	local SourceIsDJ = arg[4]
	--print("server received from client and is sending the command")
    sendServerCommand(DJlistener, "LS", "IsListeningToDJ", {DJlistener_id, SourceMusiclvl, SourceDJ, SourceIsDJ})

end

LS_Commands["AskIfIsDancing"] = function(_, arg)

    local DanceTarget = getPlayerByOnlineID(arg[1])
    local DanceTarget_id = arg[1]
    local DanceProposer = arg[2]
	--print("server received AskToDance from client and is sending the command")
    sendServerCommand(DanceTarget, "LS", "WasAskedIfIsDancing", {DanceTarget_id, DanceProposer})

end

LS_Commands["OtherPlayerIsDancing"] = function(_, arg)

    local DanceProposer = getPlayerByOnlineID(arg[1])
    local DanceProposer_id = arg[1]
    local IsDancing = arg[2]
	--print("server received AcceptedDance from client and is sending the command")
    sendServerCommand(DanceProposer, "LS", "OtherPlayerIsDancingResponse", {DanceProposer_id, IsDancing})

end

LS_Commands["AskToDance"] = function(_, arg)

    local DanceTarget = getPlayerByOnlineID(arg[1])
    local DanceTarget_id = arg[1]
    local DanceProposer = arg[2]
	--print("server received AskToDance from client and is sending the command")
    sendServerCommand(DanceTarget, "LS", "WasAskedToDance", {DanceTarget_id, DanceProposer})

end

LS_Commands["AcceptedDance"] = function(_, arg)

    local DanceProposer = getPlayerByOnlineID(arg[1])
    local DanceProposer_id = arg[1]
    local DancePartner = arg[2]
	local PartnerX = arg[3]
	local PartnerY = arg[4]
	--print("server received AcceptedDance from client and is sending the command")
    sendServerCommand(DanceProposer, "LS", "DanceWasAccepted", {DanceProposer_id, DancePartner, PartnerX, PartnerY})

end

LS_Commands["StopDance"] = function(_, arg)

    local DanceTarget = getPlayerByOnlineID(arg[1])
    local DanceTarget_id = arg[1]
	--print("server received StopDance from client and is sending the command")
    sendServerCommand(DanceTarget, "LS", "PartnerStoppedDancing", {DanceTarget_id})

end

LS_Commands["FaceDanceProposer"] = function(_, arg)

    local DancePartner = getPlayerByOnlineID(arg[1])
    local DancePartner_id = arg[1]
    local ProposerX = arg[2]
	local ProposerY = arg[3]
	print("server received FaceDanceProposer from client and is sending the command")
    sendServerCommand(DancePartner, "LS", "FaceDancingProposer", {DancePartner_id, ProposerX, ProposerY})

end

LS_Commands["ChangeDiscoStyle"] = function(_, arg)

    local style = arg[1]
    local x = arg[2]
    local y = arg[3]
	local z = arg[4]
	local s = arg[5]
	--print("server received from client and is sending the command")
	
    sendServerCommand("LS", "ChangeDiscoStyle", {style, x, y, z, s})
	
	local sqr = getCell():getGridSquare(x,y,z);
	local DiscoBall
	
			for i=1,sqr:getObjects():size() do
				local thisObject = sqr:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						local thisSprite = thisObject:getSprite()
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
						end
					end
				end
			end
	



	if DiscoBall:hasModData() and
	DiscoBall:getModData().OnOff ~= nil and
	DiscoBall:getModData().OnOff == "on" then
	
		DiscoBall:getModData().Mode = style
		DiscoBall:getModData().Shuffle = s
	end

end

LS_Commands["TurnDiscoBallOff"] = function(_, arg)

    local playerDiscoCommand = arg[1]
    local x = arg[2]
    local y = arg[3]
	local z = arg[4]
	--print("server received from client and is sending the command")
    sendServerCommand("LS", "TurnDiscoBallOff", {playerDiscoCommand, x, y, z})

	local sqr = getCell():getGridSquare(x,y,z);
	local DiscoBall
	
			for i=1,sqr:getObjects():size() do
				local thisObject = sqr:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						local thisSprite = thisObject:getSprite()
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
						end
					end
				end
			end


	if not DiscoBall then
	print("failed")
	return end

	if DiscoBall:hasModData() and
	DiscoBall:getModData().OnOff ~= nil and
	DiscoBall:getModData().OnOff == "on" then
	
		DiscoBall:getModData().OnOff = playerDiscoCommand
	
	else
		return
	end


end

LS_Commands["JukeboxStart"] = function(_, arg)

    local x = arg[1]
    local y = arg[2]
	local z = arg[3]
	--print("server received from client and is sending the command")
    sendServerCommand("LS", "JukeboxStart", {x, y, z})

end

LS_Commands["TurnJukeboxOff"] = function(_, arg)

    local x = arg[1]
    local y = arg[2]
	local z = arg[3]
	--print("server received from client and is sending the command")
    sendServerCommand("LS", "TurnJukeboxOff", {x, y, z})

	local sqr = getCell():getGridSquare(x,y,z);
	local Jukebox
	
			for i=1,sqr:getObjects():size() do
				local thisObject = sqr:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						local thisSprite = thisObject:getSprite()
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
						end
					
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
						end
					
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
						end
					
						if customName == "Jukebox" then
							Jukebox = thisObject;
						end
					end
				end
			end


	if not Jukebox then
	print("failed")
	return end

	if Jukebox:hasModData() and
	Jukebox:getModData().OnOff ~= nil and
	Jukebox:getModData().OnOff == "on" then
	
		Jukebox:getModData().OnOff = "off"
		Jukebox:getModData().OnPlay = "nothing"
	
	else
		return
	end


end

LS_Commands["JukeboxStyleChangePlayerPlaylist"] = function(_, arg)

    local x = arg[1]
    local y = arg[2]
	local z = arg[3]
	local style = arg[4]
	local length = arg[5]
	local genre = arg[6]
	local customPlaylist = arg[7]
	
	--print("server received from client and is sending the command")
    sendServerCommand("LS", "JukeboxStyleChangeCustom", {x, y, z, style, length, genre, customPlaylist})

	local sqr = getCell():getGridSquare(x,y,z);
	local Jukebox
	
			for i=1,sqr:getObjects():size() do
				local thisObject = sqr:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						local thisSprite = thisObject:getSprite()
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
						end
					
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
						end
					
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
						end

						if customName == "Jukebox" then
							Jukebox = thisObject;
						end
					end
				end
			end


	if not Jukebox then
	print("failed")
	return end

	if Jukebox:hasModData() and
	Jukebox:getModData().OnOff ~= nil and
	Jukebox:getModData().OnOff == "on" then
	
		Jukebox:getModData().OnPlay = "playing"
		Jukebox:getModData().Style = style
		Jukebox:getModData().customPlaylist = customPlaylist
	else
		return
	end


end

LS_Commands["JukeboxStyleChange"] = function(_, arg)

    local x = arg[1]
    local y = arg[2]
	local z = arg[3]
	local style = arg[4]
	local length = arg[5]
	local genre = arg[6]
	
	--print("server received from client and is sending the command")
    sendServerCommand("LS", "JukeboxStyleChange", {x, y, z, style, length, genre})

	local sqr = getCell():getGridSquare(x,y,z);
	local Jukebox
	
			for i=1,sqr:getObjects():size() do
				local thisObject = sqr:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						local thisSprite = thisObject:getSprite()
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
						end
					
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
						end
					
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
						end

						if customName == "Jukebox" then
							Jukebox = thisObject;
						end
					end
				end
			end


	if not Jukebox then
	print("failed")
	return end

	if Jukebox:hasModData() and
	Jukebox:getModData().OnOff ~= nil and
	Jukebox:getModData().OnOff == "on" then
	
		Jukebox:getModData().OnPlay = "playing"
		Jukebox:getModData().Style = style
	else
		return
	end


end

LS_Commands["StopJukeSong"] = function(_, arg)

    local x = arg[1]
    local y = arg[2]
	local z = arg[3]
	--print("server received from client and is sending the command")
    sendServerCommand("LS", "StopJukeSong", {x, y, z})

end

LS_Commands["isPlayingJuke"] = function(_, arg)

	--local isPlayingJukeSong = nil;
	local genre = arg[2]
	local JukeReusableID = arg[1]
	local playercommand = arg[6]
	
--	print("server received from client and is sending the command")
--	if sqr then
--	print("is sqr")

--			for i=1,sqr:getObjects():size() do
--				local thisObject = sqr:getObjects():get(i-1)
--
--				local thisSprite = thisObject:getSprite()
--				
--				if thisSprite ~= nil then
--				
--					local properties = thisObject:getSprite():getProperties()
--
--					if properties ~= nil then
--						local groupName = nil
--						local customName = nil
--						local thisSpriteName = nil
--					
--						--local thisSprite = thisObject:getSprite()
--						if thisSprite:getName() then
--							thisSpriteName = thisSprite:getName()
--						end
--					
--						if properties:Is("GroupName") then
--							groupName = properties:Val("GroupName")
--						end
--					
--						if properties:Is("CustomName") then
--							customName = properties:Val("CustomName")
--						end
--
--						if customName == "Jukebox" then
--							Jukebox = thisObject;
--							spriteName = thisSpriteName;
--						end
--					end
--				end
--			end


--	if not Jukebox then
--	print("failed")
--	return end

	local x = arg[3]
	local y = arg[4]
	local z = arg[5]
	
--    local emitter = getWorld():getFreeEmitter();
--	emitter:setPos(x, y, 0);

			if playercommand == "beforeplay" then
			print("trying to send beforeplay")
			sendServerCommand("LS", "isPlayingJuke", {genre, x, y, z, JukeReusableID, playercommand})

			end

			if playercommand == "stop" then
			print("trying to send stop")
			sendServerCommand("LS", "isPlayingJuke", {genre, x, y, z, JukeReusableID, playercommand})

			end

    --sendServerCommand("LS", "isPlayingJuke", {genre, x, y, z, JukeReusableID, playercommand})
	--isPlayingJukeSong = getSoundManager():playSound(genre, sqr, 5, 75, 0.7, true);
	--addSound(Jukebox, x, y, z, 30, 10)


	--end
end

LS_Commands["JukeTurnedOn"] = function(_, arg)

	--local isPlayingJukeSong = nil;
	local genre = arg[1]
	local x = arg[2]
	local y = arg[3]
	local z = arg[4]
	local JukeReusableID = arg[5]
	local playercommand = arg[6]
	local sqr = getCell():getGridSquare(x, y, z);
--	print("server received from client and is sending the command")
	if sqr then
	print("is sqr")

			for i=1,sqr:getObjects():size() do
				local thisObject = sqr:getObjects():get(i-1)

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

						if customName == "Jukebox" then
							Jukebox = thisObject;
							spriteName = thisSpriteName;
						end
					end
				end
			end


	if not Jukebox then
	print("failed")
	return end

local JukeboxLightSprite = "LS_JukeboxLight_A_1"
local JukeboxCell = Jukebox:getCell()

				if JukeboxLightOn ~= nil then
					if JukeboxLightOn == false then
							local JukeboxLight = IsoObject.new(sqr, JukeboxLightSprite)
							JukeboxLight:setName("JukeLight")
							JukeboxLight:transmitModData();
							sqr:AddTileObject(JukeboxLight)
							JukeboxLightOn = true
							Jukebox:getModData().MainLight = IsoLightSource.new(Jukebox:getX(), Jukebox:getY(), Jukebox:getZ(), 75, 75, 0, 2)
							local JukeMainLight = Jukebox:getModData().MainLight
							JukeboxCell:addLamppost(JukeMainLight)
							Jukebox:transmitModData();
							print("LIGHTS ON")
					else
							print("LIGHTS ALREADY ON")
					return
					end
				else
							local JukeboxLight = IsoObject.new(sqr, JukeboxLightSprite)
							JukeboxLight:setName("JukeLight")
							JukeboxLight:transmitModData();
							sqr:AddTileObject(JukeboxLight)
							JukeboxLightOn = true
							Jukebox:getModData().MainLight = IsoLightSource.new(Jukebox:getX(), Jukebox:getY(), Jukebox:getZ(), 75, 75, 0, 2)
							local JukeMainLight = Jukebox:getModData().MainLight
							JukeboxCell:addLamppost(JukeMainLight)
							Jukebox:transmitModData();
							print("LIGHTS ON")
				end
	
--    local emitter = getWorld():getFreeEmitter();
--	emitter:setPos(x, y, 0);

			--if playercommand == "beforeplay" then
			--print("trying to send beforeplay")
			--sendServerCommand("LS", "isPlayingJuke", {genre, x, y, z, JukeReusableID, playercommand})

			--end

			--if playercommand == "stop" then
			--print("trying to send stop")
			--sendServerCommand("LS", "isPlayingJuke", {genre, x, y, z, JukeReusableID, playercommand})

			--end

    --sendServerCommand("LS", "isPlayingJuke", {genre, x, y, z, JukeReusableID, playercommand})
	--isPlayingJukeSong = getSoundManager():playSound(genre, sqr, 5, 75, 0.7, true);
	--addSound(Jukebox, x, y, z, 30, 10)


	end
end

---------- GLOBAL MODDATA

function LS_OnInitGlobalModData()
    local lsModData = ModData.getOrCreate("LSDATA")
	if not lsModData["SO"] then lsModData["SO"] = {}; end
	LSgetSandboxOptions(lsModData["SO"])
	if not lsModData["AMBT"] then lsModData["AMBT"] = {}; end
	if not lsModData["BTY"] then lsModData["BTY"] = {}; end
    --if not ModData.exists("LSDATAPlaylists") then
    --    local LSModDataPlaylists = ModData.create("LSDATAPlaylists")
    --    LSModDataPlaylists["CustomPlaylists"] = {};
    --end
end

Events.OnInitGlobalModData.Add(LS_OnInitGlobalModData)

LS_Commands.OnClientCommand = function(module, command, playerObj, args)

    if module == 'LS' and LS_Commands[command] then
        LS_Commands[command](playerObj, args)
    end
end

Events.OnClientCommand.Add(LS_Commands.OnClientCommand)


LS_Commands["UpdateAmbt"] = function(_, args)
	local lsModData = ModData.getOrCreate("LSDATA")
	local ogAmbt = args[1]
	local name = args[2]
	local key = args[3]
	local value = args[4]
	local forceReset = args[5]
	if (not lsModData["AMBT"][name]) or (not lsModData["AMBT"][name].custom) then lsModData["AMBT"][name] = ogAmbt; end
	--print("LS_Commands - UpdateAmbt... key is: "..key); print("LS_Commands - UpdateAmbt... value is: "..tostring(value))
	if (key ~= "resetAdm") and (lsModData["AMBT"][name][key] == value) then return; end
	lsModData["AMBT"][name][key] = value
	if (key == "resetAdm") and value then lsModData["AMBT"][name].custom = false;
	else lsModData["AMBT"][name].custom = true; lsModData["AMBT"][name].resetAdm = false; end
	lsModData["AMBT"][name].resetF = forceReset
	ModData.transmit("LSDATA")
	sendServerCommand("LS", "ResetAmbt", {name, lsModData["AMBT"][name].resetAdm})
end


LS_Commands.ChangePlayerState = function(playerObj, args)
    ModData.get("LSDATA")[playerObj:getUsername()] = args
    ModData.transmit("LSDATA")
end

local function logCustomBeauty(data, args)
	if isClient() then return; end
	if not data then return; end
	local file = getFileReader("LSCustomBeautyValues.ini",true)
	if not file then return; end -- failed to write file
	local oldValue
	while true do
		local line = file:readLine()
		if not line then file:close(); break; end
		local splitedLine = string.split(line, "=")
		local name = splitedLine[1]
		if name == args[1] then
			oldValue = true
			file:close()
			break
		end
	end
	if not oldValue then -- add
		file = getFileWriter("LSCustomBeautyValues.ini",true,true) -- append is true (add to)
		file:write(args[1].."="..tostring(args[2]).."\n")
	else -- edit
		file = getFileWriter("LSCustomBeautyValues.ini",true,false) -- append is false (overwrite)
		for k,v in pairs(data) do
			file:write(tostring(k).."="..tostring(v).."\n")
		end
	end
	file:close()
end

LS_Commands["UpdateServerBeauty"] = function(_, args)
	local lsModData = ModData.getOrCreate("LSDATA")
	local spriteName = args[1]
	local val = args[2]
	lsModData["BTY"][spriteName] = val
	ModData.transmit("LSDATA")
	sendServerCommand("LS", "UpdateClientBeauty", {spriteName, val})
	logCustomBeauty(lsModData["BTY"], args)
end

local function getBeautyLines()
	local file = getFileReader("LSCustomBeautyValues.ini",false)
	if not file then return false; end
	local t, n = {}, 0
	while true do
		local line = file:readLine()
		if not line then file:close(); break; end
		local splitedLine = string.split(line, "=")
		local name = splitedLine[1]
		local val = splitedLine[2]
		t[name] = tonumber(val)
		n = n+1
	end
	return t, n
end

LS_Commands["ImportServerBeauty"] = function(_, args)
	if isClient() then return; end
	local lsModData = ModData.getOrCreate("LSDATA")
	local allLines, num = getBeautyLines()	
	if not allLines or num == 0 then print("-------- WARN: ImportServerBeauty FAILED: LSCustomBeautyValues.ini empty or null"); return; end
	lsModData["BTY"] = allLines
	ModData.transmit("LSDATA")
	sendServerCommand("LS", "ReloadClientBeauty", {})
	print("-------- WARN: ImportServerBeauty COMPLETED: values from LSCustomBeautyValues.ini imported successfully")
end