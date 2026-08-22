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
require "LSEffects"

local function sqrHasEnergy(obj)
	if not ((SandboxVars.ElecShutModifier > -1 and
	GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
	obj:getSquare():haveElectricity()) then
		return false
	end
	return true
end

local function getObj(v, cm)
	local obj, facing, groupName --!
	local sqr = v:getCell():getGridSquare(v:getX(), v:getY(), v:getZ()) or v:getModData().JukeBckpSquare

	for i=0,sqr:getObjects():size()-1 do
		local thisObject = sqr:getObjects():get(i)	
		local thisSprite = thisObject:getSprite()
				
		if thisSprite then	
			local properties = thisObject:getSprite():getProperties()
			if properties and properties:Is("CustomName") and (properties:Val("CustomName") == cm) then
				obj = thisObject;
				if properties:Is("Facing") then
					facing = properties:Val("Facing")
				end
				groupName = properties:Is("GroupName") and properties:Val("GroupName") --!
			end
		end
	end
	return obj, facing, groupName --!
end

local function playerIsInRange(player, obj, numb)
	if player and (player:getX() >= obj:getX() - numb and player:getX() <= obj:getX() + numb and
	player:getY() >= obj:getY() - numb and player:getY() <= obj:getY() + numb) then return true; end
	return false
end

local function getObjAndRemove(JukeboxSquare)
	local objects = JukeboxSquare:getObjects()
	if (not objects) or (objects:size() == 0) then return; end
	--for i=0,JukeboxSquare:getObjects():size()-1 do
	for i = objects:size()-1, 1, -1 do
		local object2 = JukeboxSquare:getObjects():get(i);
		if object2 and ((object2:getName() == "JukeLight") or (object2:getName() == "JukePlayLight1") or
		(object2:getName() == "JukePlayLight2") or (object2:getName() == "JukePlayLightOverlay")) then
			JukeboxSquare:RemoveTileObject(object2)
		end
	end--for
end

local function getObjAndRemoveSecondaryLights(JukeboxSquare)
	local objects = JukeboxSquare:getObjects()
	if (not objects) or (objects:size() == 0) then return; end
	--for i=0,JukeboxSquare:getObjects():size()-1 do
	for i = objects:size()-1, 1, -1 do
		local object2 = JukeboxSquare:getObjects():get(i);
		if object2 and ((object2:getName() == "JukePlayLightOverlay") or (object2:getName() == "JukePlayLight1") or
		(object2:getName() == "JukePlayLight2")) then
			JukeboxSquare:RemoveTileObject(object2)
		end
	end--for
end

local function getjbObj(JukeboxSquare, name1, name2, name3)
	if not JukeboxSquare then return false; end
	local obj, obj2, obj3
	for i=0,JukeboxSquare:getObjects():size()-1 do
		local object = JukeboxSquare:getObjects():get(i);		
		if object and object:getName() then
			if object:getName() == name1 then
				obj = object
				if not name2 then break; end
			elseif name2 and (object:getName() == name2) then
				obj2 = object
				if obj and (not name3) then break; end
			elseif name3 and (object:getName() == name3) then
				obj3 = object
			end
		end
		if obj and obj2 and obj3 then break; end
	end
	if name3 then return obj, obj2, obj3; end
	if name2 and (not name3) then return obj, obj2; end
	return obj
end

local function removeObjSingle(JukeboxSquare)
	local obj
	for i=0,JukeboxSquare:getObjects():size()-1 do
		local object2 = JukeboxSquare:getObjects():get(i);
		if object2 and ((object2:getName() == "JukePlayLight1") or (object2:getName() == "JukePlayLight2")) then
			obj = object2
			break
		end
	end--for
	if obj then JukeboxSquare:RemoveTileObject(obj); end
end

local function getCustomName(object)
	if not object then return nil; end
    local properties = object:getSprite() and object:getSprite():getProperties()
    if properties and properties:Is("CustomName") and (properties:Val("CustomName") == "Jukebox") then
        return true
    end
    return nil
end

local function getPlayerDataStrings()
	return {
		"IsListeningToJukebox",
	}
end

local function setPlayerListeningData(playerData, boolean)
	local dataList = getPlayerDataStrings()
	for n=1,#dataList do
		local value = dataList[n]
		playerData[value] = boolean
	end
end

function LSrefreshJB(playerObj)
	local jukelist = require("Properties/Objects/List")
	if (not jukelist) or (#jukelist == 0) then return; end
	local hasJukeNearby
	for i,v in ipairs(jukelist) do
		local hasCustomName = getCustomName(v)
		if v:hasModData() and v:getModData().OnOff and hasCustomName then
			local JukeboxLightSprite, JukeboxLightSpritePlay1, JukeboxLightSpritePlay2, JukeboxLightSpritePlayOverlay = "LS_JukeboxLight_A_1", "LS_JukeboxLight_A_2", "LS_JukeboxLight_A_3", "LS_JukeboxLight_A_4"
			local Facing
			local groupName --!
			---
			if v:getModData().JukeinRange and (v:getModData().JukeinRange ~= "out of range") and (v:getModData().OnOff == "on") and v:getModData().JukeBckpSquare then
				local Jukebox
				Jukebox, Facing, groupName = getObj(v, "Jukebox") --!
				if Facing and (Facing == "S") then
					JukeboxLightSprite, JukeboxLightSpritePlay1, JukeboxLightSpritePlay2, JukeboxLightSpritePlayOverlay = "LS_JukeboxLight_B_1", "LS_JukeboxLight_B_2", "LS_JukeboxLight_B_3", "LS_JukeboxLight_B_4"
				end
				if not Jukebox then v:getModData().OnOff = "off"; v:getModData().OnPlay = "nothing"; v:getModData().JukeinRange = "out of range"; end
				if Jukebox and (not sqrHasEnergy(Jukebox)) then v:getModData().OnOff = "off"; v:getModData().OnPlay = "nothing"; end	
			end
			---
			if not playerIsInRange(playerObj, v, 60) then
				if v:getModData().JukeinRange and (v:getModData().JukeinRange ~= "out of range") then
					v:getModData().JukeinRange = "out of range"
					if v:getModData().OnPlay and (v:getModData().OnPlay ~= "nothing") then
						v:getModData().SilenceMusic = "yes"
					end
				end
			else
				if (not v:getModData().JukeinRange) or (v:getModData().JukeinRange and (v:getModData().JukeinRange ~= "out of range") and (v:getModData().JukeinRange ~= "in range")) then
					v:getModData().JukeinRange = "in range"
					if v:getModData().OnOff == "on" then OnJukeboxStart(v:getX(), v:getY(), v:getZ()); end
					if v:getModData().OnPlay and (v:getModData().OnOff == "on") and v:getModData().Length and
					v:getModData().Style and v:getModData().genre then
						v:getModData().OnPlay, v:getModData().Length, v:getModData().genre = "nothing", 3, "JukeboxAfterTurnOn"							
						OnJukeboxStyleChange(v:getX(), v:getY(), v:getZ(), v:getModData().Style, v:getModData().Length, v:getModData().genre)
						hasJukeNearby = true
					end
				else
					if v:getModData().JukeinRange == "out of range" then
						v:getModData().JukeinRange = "in range"
						if v:getModData().OnPlay and (v:getModData().OnOff == "on") and v:getModData().Style then
							OnJukeboxStart(v:getX(), v:getY(), v:getZ())
							v:getModData().OnPlay, v:getModData().Length, v:getModData().genre = "nothing", 3, "JukeboxAfterTurnOn"
							OnJukeboxSendSong(v:getX(), v:getY(), v:getZ(), v)
							hasJukeNearby = true
						end
					elseif v:getModData().JukeinRange == "in range" then
						if v:getModData().JukeNoObject and (v:getModData().OnOff == "on") and v:getModData().OnPlay and v:getModData().Style then
							v:getModData().JukeNoObject = false
							OnJukeboxStart(v:getX(), v:getY(), v:getZ())
							v:getModData().OnPlay, v:getModData().Length, v:getModData().genre = "nothing", 3, "JukeboxAfterTurnOn"	
							OnJukeboxSendSong(v:getX(), v:getY(), v:getZ(), v)
							hasJukeNearby = true
						end
					end
				end
			end--range
			
			------------------------------------
			if not v:getModData().Cell then v:getModData().Cell = v:getCell(); end
			if not v:getModData().JukeBckpSquare then v:getModData().JukeBckpSquare = (v:getCell():getGridSquare(v:getX(), v:getY(), v:getZ())); end		
			if groupName and groupName == "Gramophone" then --!
				if v:getModData().OnOff and v:getModData().OnOff == "on" and v:getModData().OnPlay and v:getModData().OnPlay ~= "nothing" and
				v:getModData().genre and v:getModData().genre ~= "JukeboxAfterTurnOn" and playerIsInRange(playerObj, v, 30) then --!
					hasJukeNearby = true
				end
			else --!
			local JukeboxCell = v:getModData().Cell
			local JukeboxSquare = v:getCell():getGridSquare(v:getX(), v:getY(), v:getZ()) or v:getModData().JukeBckpSquare
			--[[
			print("TROUBLESHOOT START")
			if not v:getModData().JukeBckpSquare then print("v:getModData().JukeBckpSquare NOT FOUND"); end
			if not playerIsInRange(playerObj, v, 30) then print("playerIsInRange NOT IN RANGE"); end
			if not v:getModData().OnOff then print("v:getModData().OnOff NOT FOUND"); end
			if v:getModData().OnOff ~= "on" then print("v:getModData().OnOff NOT ON"); end
			if v:getModData().MainLight then print("v:getModData().MainLight IS FOUND"); end
			if not JukeboxSquare then print("JukeboxSquare NOT FOUND"); end
			if JukeboxSquare and getjbObj(JukeboxSquare, "JukeLight", false, false) then print("getjbObj IS FOUND"); end
			print("TROUBLESHOOT END")
			]]--
			if (v:getModData().OnOff ~= "on") or (not playerIsInRange(playerObj, v, 30)) then
				if JukeboxCell then
					if v:getModData().MainLight then JukeboxCell:removeLamppost(v:getModData().MainLight); v:getModData().MainLight = false; end
					if v:getModData().RGBLightOverlay then JukeboxCell:removeLamppost(v:getModData().RGBLightOverlay); v:getModData().RGBLightOverlay = false; end
				end
				if JukeboxSquare then getObjAndRemove(JukeboxSquare); end			
			elseif v:getModData().OnOff == "on" and playerIsInRange(playerObj, v, 30) and Facing then
				
				if (not v:getModData().MainLight) or (not getjbObj(JukeboxSquare, "JukeLight", false, false)) then
					--print("CREATING NEW LIGHT")
					local JukeboxLight = IsoObject.new(JukeboxSquare, JukeboxLightSprite)
					JukeboxLight:setName("JukeLight")
					if JukeboxSquare then JukeboxSquare:AddTileObject(JukeboxLight); end
					v:getModData().MainLight = IsoLightSource.new(v:getX(), v:getY(), v:getZ(), 75, 75, 0, 2)
					if JukeboxCell then JukeboxCell:addLamppost(v:getModData().MainLight); end
				end
				if v:getModData().OnPlay and JukeboxSquare then
					if v:getModData().OnPlay == "nothing" then
						removeObjSingle(JukeboxSquare)
					elseif (v:getModData().genre == "JukeboxAfterTurnOn") and JukeboxCell then
						if v:getModData().RGBLightOverlay then JukeboxCell:removeLamppost(v:getModData().RGBLightOverlay); v:getModData().RGBLightOverlay = false; end
						getObjAndRemoveSecondaryLights(JukeboxSquare)					
					elseif JukeboxCell then
						local JukeboxLightPlayOn1, JukeboxLightPlayOn2, JukeboxPlayLightOverlay = getjbObj(JukeboxSquare, "JukePlayLight1", "JukePlayLight2", "JukePlayLightOverlay")
						if not JukeboxPlayLightOverlay then
							JukeboxPlayLightOverlay = IsoObject.new(JukeboxSquare, JukeboxLightSpritePlayOverlay)
							JukeboxPlayLightOverlay:setName("JukePlayLightOverlay")
							JukeboxSquare:AddTileObject(JukeboxPlayLightOverlay)
						else
							if (not v:getModData().changecolor) or ((v:getModData().changecolor ~= 0) and (v:getModData().changecolor ~= 1) and (v:getModData().changecolor ~= 2) and
							(v:getModData().changecolor ~= 3) and (v:getModData().changecolor ~= 4) and (v:getModData().changecolor ~= 5)) then
								v:getModData().changecolor = 1
							end
							local rgbTable = JukeboxProps.get("overlay", v:getModData().changecolor)
							if rgbTable and rgbTable.objR then	
								JukeboxPlayLightOverlay:setCustomColor(rgbTable.objR, rgbTable.objG, rgbTable.objB, 1)
								if v:getModData().RGBLightOverlay then JukeboxCell:removeLamppost(v:getModData().RGBLightOverlay); end
								v:getModData().RGBLightOverlay = IsoLightSource.new(v:getX(), v:getY(), v:getZ(), rgbTable.lightR, rgbTable.lightG, rgbTable.lightB, 3)
								JukeboxCell:addLamppost(v:getModData().RGBLightOverlay)		
							end
							v:getModData().changecolor = math.floor(v:getModData().changecolor+1)
							if v:getModData().changecolor > 5 then v:getModData().changecolor = 0; end
						end
						local lightSprite, lightName = JukeboxLightSpritePlay1, "JukePlayLight1"
						local r, g, b
						local lightsRGB = require("Properties/Light/JBStyles")
						for j, style in ipairs(lightsRGB) do
							if (style.name == v:getModData().Style) or (style.cname == v:getModData().Style) then
								r, g, b = style.r, style.g, style.b
								break
							end
						end
						if JukeboxLightPlayOn1 then
							lightSprite, lightName = JukeboxLightSpritePlay2, "JukePlayLight2"
							JukeboxSquare:RemoveTileObject(JukeboxLightPlayOn1)
						elseif JukeboxLightPlayOn2 then
							JukeboxSquare:RemoveTileObject(JukeboxLightPlayOn2)
						end
						local JukeboxNewPlayLight = IsoObject.new(JukeboxSquare, lightSprite)
						JukeboxNewPlayLight:setName(lightName)
						if r and g and b then JukeboxNewPlayLight:setCustomColor(r, g, b, 1); end
						JukeboxSquare:AddTileObject(JukeboxNewPlayLight)
						hasJukeNearby = true
					end--OnPlay==nothing
				end--OnPlay
			end--ONOFF Lights
			end--GRAMOPHONE --!
		end--HASMODDATA
	end--for
	setPlayerListeningData(playerObj:getModData(), hasJukeNearby)
end