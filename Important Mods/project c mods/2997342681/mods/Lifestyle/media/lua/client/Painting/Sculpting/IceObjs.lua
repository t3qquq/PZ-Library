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

LSIntObjs = LSIntObjs or {}

local function playMeltingSound(object)
	local meltEmitter = getWorld():getFreeEmitter(object:getX(),object:getY(),object:getZ())
	meltEmitter:playSoundImpl("Toilet_Flush_Clogged", false, object)
end

local function doDirtPuddle(x, y, z, outside)
	if outside then return; end
	local puddleList = {"LS_HScraps_DirtPuddle_0","LS_HScraps_DirtPuddle_1","LS_HScraps_DirtPuddle_2","LS_HScraps_DirtPuddle_3","LS_HScraps_DirtPuddle_4",
	"LS_HScraps_DirtPuddle_5","LS_HScraps_DirtPuddle_6","LS_HScraps_DirtPuddle_7"}
	local dirtSprite = puddleList[ZombRand(#puddleList)+1]
	if isClient() then
		sendClientCommand("LS", "AddDirtPuddle", {x, y, z, 2, dirtSprite})
	else
		LSAddLitter(x, y, z, 2, dirtSprite)
	end
end

local function doMeltSculpture(object)
	playMeltingSound(object)
	local sqr = object:getSquare()
	doDirtPuddle(object:getX(),object:getY(),object:getZ(), sqr:isOutside())
	if isClient() then
		sledgeDestroy(object)
	else
		sqr:transmitRemoveItemFromSquare(object); sqr:RemoveTileObject(object)
	end
end

local function doMeltWork(object)
	playMeltingSound(object)

	object:getModData().style = false
	object:getModData().meltStartTime = false
	object:setOverlaySprite("LS_HScraps_DirtPuddle_0", isClient()) --get puddle sprite
	if isClient() then
		object:transmitUpdatedSpriteToServer()
		object:transmitModData()
	end
end

local function getMeltRate(square)
	if not square then return 1; end
	local climate = getClimateManager():getAirTemperatureForSquare(square) or 20
	local temp = 2
	if climate < 0 then temp = 0; elseif climate < 10 then temp = 0.5; elseif climate < 30 then temp = 1; end
	return temp
end

LSIntObjs.StationWork = function(player, object)
	-- set style to false once it becomes a puddle to disable the work option
	if (not object:getModData().style) or (object:getModData().style ~= "Ice") then
		if object:getModData().meltStartTime then object:getModData().meltStartTime = false; if isClient() then object:transmitModData(); end; end
		return
	end
	if not object:getModData().meltStartTime then object:getModData().meltStartTime = 24000; end -- about 6 real hours at room temperature, 12 at 0 - 10, only 3 at 30+
	local meltRate = getMeltRate(object:getSquare())
	object:getModData().meltStartTime = object:getModData().meltStartTime - meltRate
	if object:getModData().meltStartTime <= 0 then doMeltWork(object);
	elseif isClient() then object:transmitModData(); end
end

LSIntObjs.SculptureIce = function(player, object)
	-- set style to false once it becomes a puddle to disable the work option
	object:getModData().movableData = object:getModData().movableData or {}
	if not object:getModData().movableData['meltStartTime'] then object:getModData().movableData['meltStartTime'] = 24000; end -- about 6 real hours at room temperature, 12 at 0 - 10, only 3 at 30+
	local meltRate = getMeltRate(object:getSquare())
	object:getModData().movableData['meltStartTime'] = object:getModData().movableData['meltStartTime'] - meltRate
	if object:getModData().movableData['meltStartTime'] <= 0 then object:getModData().movableData['meltStartTime'] = false; if isClient() then object:transmitModData(); end; doMeltSculpture(object);
	elseif isClient() then object:transmitModData(); end
end