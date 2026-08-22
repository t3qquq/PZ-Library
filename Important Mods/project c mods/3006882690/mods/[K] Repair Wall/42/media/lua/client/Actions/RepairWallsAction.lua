KAMER_RepairWallsAction = ISBaseTimedAction:derive("KAMER_RepairWallsAction");

-- The FMOD events have approx. 10-second duration even though the sounds are shorter.
KAMER_RepairWallsAction.soundDelay = 6

function KAMER_RepairWallsAction:isValid()
    return self.items.HasItems;
end

function KAMER_RepairWallsAction:waitToStart()
	self:faceLocation()
	return self.character:shouldBeTurning()
end

function KAMER_RepairWallsAction:update()
	local playingHammer = self.hammerSound ~= 0 and self.character:getEmitter():isPlaying(self.hammerSound)

    if not playingHammer then
		if self.material == "Wood" then
			self.hammerSound = self.character:getEmitter():playSound("Hammering");
		else
			self.hammerSound = self.character:getEmitter():playSound("BlowTorch");
		end
    end

    self.character:setMetabolicTarget(Metabolics.LightWork);
	self:faceLocation();
end

function KAMER_RepairWallsAction:start()
	if self.material == "Wood" then
		self:setActionAnim("Build")
	else
		self:setActionAnim("BlowTorch")
		self:setOverrideHandModels(self.character:getPrimaryHandItem(), nil)
	end
end

function KAMER_RepairWallsAction:stop()
    if self.hammerSound and self.hammerSound ~= 0 and self.character:getEmitter():isPlaying(self.hammerSound) then
        self.character:getEmitter():stopSound(self.hammerSound);
    end
    ISBaseTimedAction.stop(self);
end

function KAMER_RepairWallsAction:perform()
	if self.hammerSound and self.hammerSound ~= 0 and self.character:getEmitter():isPlaying(self.hammerSound) then
        self.character:getEmitter():stopSound(self.hammerSound);
    end

	sendClientCommand(self.character, "KAMER_RepairWall", "DeleteItems", { crash = 1 });

	local objectData = KAMER_RepairWallsUtils:Data(self.object)
	local objHealth = objectData.health
	local objMaxHealth = objectData.maxHealth
	local playerSkills = KAMER_RepairWallsUtils:getCurrentSkills(self.character)
	local addLV = 6
	if self.material == "Wood" and playerSkills["Woodwork"] then
		addLV = (playerSkills["Woodwork"] + 1) * 6
	elseif self.material == "Metal" and playerSkills["MetalWelding"] then
		addLV = (playerSkills["MetalWelding"] + 1) * 6
	end
	local addHealth = math.ceil((objMaxHealth * addLV)/100)
	local total = objHealth + addHealth
		if total >= objMaxHealth then
			self.object:setHealth(objMaxHealth)
		else
			self.object:setHealth(total)
		end
		if isClient() then
			self.object:transmitModData()
            local sin = self.object:getObjectIndex()
            local dobrexd = self.object:getSquare()
            local sx = dobrexd:getX()
            local sy = dobrexd:getY()
            local sz = dobrexd:getZ()
            sendClientCommand(self.character, "KAMER_RepairWall", "updateHealth", { kamerSin=sin,kamerX=sx,kamerY=sy,kamerZ=sz,kamerLv=addLV});
		end
	ISBaseTimedAction.perform(self);
end

function KAMER_RepairWallsAction:faceLocation()
    self.character:faceThisObject(self.object)
end

function KAMER_RepairWallsAction:new(objectMaterial, character, object, ObjSq, itemHeld, playerItems, player)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character;
	o.player = player
    o.material = objectMaterial
	o.items = playerItems;
    o.square = ObjSq;
	o.stopOnWalk = true;
	o.stopOnRun = true;
	o.maxTime = 300;
	-- if character:HasTrait("Handy") then
	-- 	o.maxTime = 300 - 50;
    -- end - zara
    o.caloriesModifier = 4;
    o.object = object
    o.objectx = object:getX();
	o.objecty = object:getY();
	o.objectz = object:getZ();
	o.ItemInHand = itemHeld
	o.hammerSound = 0
	o.soundTime = 0
	return o;
end
