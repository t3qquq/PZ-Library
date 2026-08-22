
if ISPlace3DItemCursor then
	local ogGetSurface = ISPlace3DItemCursor.getSurface
	function ISPlace3DItemCursor:getSurface( _square )
		for i=1,_square:getObjects():size() do
			local object = _square:getObjects():get(i-1);
			if object:getSurfaceOffsetNoTable() > 0 then
				local properties = object:getSprite() and object:getSprite():getProperties()
				if properties and properties:Is("CustomName") and properties:Val("CustomName") == "Jukebox" then
					return 0
				end
			end
		end
		return ogGetSurface(self, _square)
	end
end

local function getOverlayL(object)
	local hasOverlayL, sprite, attachedsprite = false, false, object:getAttachedAnimSprite()
	if object:getOverlaySprite() and object:getOverlaySprite():getName() and
	(luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_messages") or 
	luautils.stringStarts(object:getOverlaySprite():getName(), "overlay_graffiti") or 
	luautils.stringStarts(object:getOverlaySprite():getName(), "floors_burnt") or 
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
	if attachedsprite and object:isFloor() and not hasOverlayL then
		for n=1,attachedsprite:size() do
			sprite = attachedsprite:get(n-1)
			if sprite and sprite:getParentSprite() and sprite:getParentSprite():getName() and
			(luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_messages") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_graffiti") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "floors_burnt") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_blood") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "blood_floor") or
			luautils.stringStarts(sprite:getParentSprite():getName(), "overlay_grime") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "trash_") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "trash&junk") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "d_floorleaves") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "LS_HScraps") or 
			luautils.stringStarts(sprite:getParentSprite():getName(), "d_trash")) then-----------if object already has overlay litter then do not add more
				hasOverlayL = true
				break
			end
		end
	end
	return hasOverlayL
end

local function getSolidL(object)
	if object:getTextureName() and
	(luautils.stringStarts(object:getTextureName(), "overlay_messages") or 
	luautils.stringStarts(object:getTextureName(), "overlay_graffiti") or 
	luautils.stringStarts(object:getTextureName(), "floors_burnt") or 
	luautils.stringStarts(object:getTextureName(), "overlay_blood") or 
	luautils.stringStarts(object:getTextureName(), "blood_floor") or
	luautils.stringStarts(object:getTextureName(), "overlay_grime") or 
	--luautils.stringStarts(object:getTextureName(), "trash_") or 
	luautils.stringStarts(object:getTextureName(), "trash&junk") or 
	luautils.stringStarts(object:getTextureName(), "d_floorleaves") or 
	luautils.stringStarts(object:getTextureName(), "d_trash")) then-----------if object already has solid litter then do not add more
		return true
	end
	return false
end

local AvailableFloorList = {}

local function getObjectAndHasSolidOrOverlay(thisSquare,SolidOrOverlay)
	local object, hasOverlayL, hasSolidL
	for i=0,thisSquare:getObjects():size()-1 do
		local ThisObject = thisSquare:getObjects():get(i)
		if instanceof(ThisObject, "IsoObject") then
			object = ThisObject
			local blocked = object:getSprite():getProperties() and object:getSprite():getProperties():Is("BlocksPlacement")
			if object and not blocked then
				if SolidOrOverlay == 1 then
					hasSolidL = getSolidL(object)
				else
					hasOverlayL = getOverlayL(object)
					if not hasOverlayL then
						table.insert(AvailableFloorList, object)
					end
				end
			end
			if not hasSolidL and (SolidOrOverlay == 1) and not blocked then
				table.insert(AvailableFloorList, thisSquare)
			end
		end
		object, hasOverlayL, hasSolidL = false, false, false
	end
end

local function getSquareIsValid(sSquare, thisSquare)
	--if sSquare then print("getSquareIsValid sSquare is valid"); else print("getSquareIsValid sSquare is NIL"); end
	--if thisSquare then print("getSquareIsValid thisSquare is valid"); else print("getSquareIsValid thisSquare is NIL"); end
	if thisSquare and sSquare and thisSquare:getRoom() == sSquare:getRoom() and thisSquare:isOutside() == sSquare:isOutside() and thisSquare:isInARoom() and thisSquare:getFloor() and not 
	thisSquare:isSolid() and not thisSquare:isSolidTrans() then
		return true
	else
		return false
	end
end

local function getAvailableFloorList(Sx,Sy,Sz,SolidOrOverlay,sSquare,isLoop)
	local thisSquare, object, hasOverlayL, hasSolidL
	
	if not isLoop then
		--print("getAvailableFloorList not isLoop")
		thisSquare = getCell():getGridSquare(Sx, Sy, Sz)
		if getSquareIsValid(sSquare, thisSquare) then
			--print("getAvailableFloorList getSquareIsValid is TRUE")
			getObjectAndHasSolidOrOverlay(thisSquare,SolidOrOverlay)
		--else print("getAvailableFloorList getSquareIsValid is FALSE");
		end
	else
		--print("getAvailableFloorList is isLoop")
		for x = Sx-1,Sx+1 do---get x range
			for y = Sy-1,Sy+1 do----get y range
				thisSquare = getCell():getGridSquare(x, y, Sz)
				if getSquareIsValid(sSquare, thisSquare) then
					--print("getAvailableFloorList getSquareIsValid is TRUE")
					getObjectAndHasSolidOrOverlay(thisSquare,SolidOrOverlay)
				--else print("getAvailableFloorList getSquareIsValid is FALSE");
				end
			end
		end	
	end
end

local function CreateDirtPuddle(arg)
	--if arg then print("CreateDirtPuddle initialised"); else print("CreateDirtPuddle ERROR arg is NIL"); end
    local Sx, Sy, Sz, SolidOrOverlay, LitterSprite = arg[1], arg[2], arg[3], arg[4], arg[5]
	--if SolidOrOverlay then print("CreateDirtPuddle SolidOrOverlay is "..SolidOrOverlay); else print("CreateDirtPuddle SolidOrOverlay is NIL"); end
	--if LitterSprite then print("CreateDirtPuddle LitterSprite is "..LitterSprite); else print("CreateDirtPuddle LitterSprite is NIL"); end
	local targetFloor
	local sSquare = getCell():getGridSquare(Sx, Sy, Sz)
	if #AvailableFloorList > 0 then AvailableFloorList = {}; end
	getAvailableFloorList(Sx,Sy,Sz,SolidOrOverlay,sSquare,false)
	if #AvailableFloorList == 0 then getAvailableFloorList(Sx,Sy,Sz,SolidOrOverlay,sSquare,true); end

	if #AvailableFloorList > 0 then
		--print("AvailableFloorList is > 0")
		local randomTile = ZombRand(#AvailableFloorList) + 1
		targetFloor = AvailableFloorList[randomTile]
		if targetFloor then
			if SolidOrOverlay == 1 then
				local NewLitterObj = IsoObject.new(targetFloor, LitterSprite)
				targetFloor:AddTileObject(NewLitterObj)
				NewLitterObj:transmitCompleteItemToClients()
				targetFloor:transmitAddObjectToSquare(NewLitterObj, -1)			
			elseif SolidOrOverlay == 2 then
				local square = targetFloor:getSquare()
				local objOnFloor
				if square then
					for i=1,square:getObjects():size() do
						local thisObject = square:getObjects():get(i-1)
						if thisObject and thisObject:getSprite() then
							local objProperties = thisObject:getSprite():getProperties()
							if objProperties and objProperties:Is("BlocksPlacement") then
								objOnFloor = true
							end
						end
					end
					if not objOnFloor then
						targetFloor:setOverlaySprite(LitterSprite, true)--string/transmit
						targetFloor:transmitUpdatedSpriteToClients()
					end
				end
			end
		end
	--else print("AvailableFloorList is 0");
	end
end

function LSServerCommandHandler(command, arg)
	--if command then print("LSServerCommandHandler with command "..command); else print("LSServerCommandHandler failed command is NIL"); end
	if command and (command == "CreateDirtPuddle") then CreateDirtPuddle(arg); end
end
