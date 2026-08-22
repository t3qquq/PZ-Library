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

require "TimedActions/ISBaseTimedAction"

LSInvHarvesterAction = ISBaseTimedAction:derive("LSInvHarvesterAction");

local function itemHasUses(item)
	return item:isInPlayerInventory() and item:getDrainableUsesInt() > 0
end

function LSInvHarvesterAction:isValid()
	--print("-----------WARN - LSInvHarvesterAction: isValid")
	return true
end

function LSInvHarvesterAction:waitToStart()
	self.action:setUseProgressBar(false)
	return false
end

local function stopSound(character, sound)
	if sound and sound ~= 0 and character:getEmitter():isPlaying(sound) then
		character:getEmitter():stopSound(sound)
	end
end

function LSInvHarvesterAction:update()
	if self.character:isSitOnGround() or self.character:getVehicle() then self:forceStop(); end
	self.jobProgress = self:getJobDelta() * self.maxTime
	

	for _, phase in ipairs(self.phases) do
		if (not self.phaseStates[phase.name]) and self.jobProgress > (self.maxTime * phase.threshold) then
			self.phaseStates[phase.name] = true
			self[phase.handler](self)
			break
		--elseif self.phaseStates[phase.name] and self.jobProgress < (self.maxTime * phase.threshold) then
			--self.phaseStates[phase.name] = false
		end
	end
	
	self.character:setMetabolicTarget(Metabolics[self.exerciseMetabolics])
end

function LSInvHarvesterAction:gadgetStartSound()
	if self.gadgetStart then return; end
	self:setOverrideHandModels(self.item, self.item)
	self.character:getEmitter():playSound("Gadget_START")
	self.gadgetStart = true
end

function LSInvHarvesterAction:vaccuumStartSound()
	if self.vaccuumStart then return; end
	self.startSound = self.character:getEmitter():playSound("Vaccuum_START")
	self.vaccuumStart = true
end

function LSInvHarvesterAction:endAction()
	stopSound(self.character, self.sound)
	self.character:getEmitter():playSound("Vaccuum_STOP")
	self:forceComplete()
end

function LSInvHarvesterAction:doFinalInteraction()
	--stopSound(self.character, self.sound)
	--self:setActionAnim("Bob_CleaningGFE")
	self.character:getEmitter():playSound("Gadget_STOP")
end

function LSInvHarvesterAction:endInteraction()
	--stopSound(self.character, self.sound)

	self.loopCount = self.loopCount+1
	if self.loopCount < self.loopsGoal then
		self:resetJobDelta()
		self:resetPhases({"d"},false) -- {"a","b",...}, all phases
	end
	if self.loopCount < #self.plantsData then self["harvestValidPlant"](self); end

end

function LSInvHarvesterAction:resetPhases(list, all)
	for _, phase in ipairs(self.phases) do
		if self.phaseStates[phase.name] and (all or (list and list[phase.name])) then
			self.phaseStates[phase.name] = false
		end
	end
end

function LSInvHarvesterAction:doInteraction()	
	if self.sound and self.sound == 0 then
		self.sound = self.character:getEmitter():playSound("Vaccuum_LOOP")
		self:setActionAnim("Bob_Vaccuum_M")
	end
end

function LSInvHarvesterAction:start()
	--print("-----------WARN - LSInvHarvesterAction: start")
	if not self.loopsGoal then self:forceStop(); end
	--self:setOverrideHandModels(nil, nil)
	self:setActionAnim("Bob_Vaccuum_M")
	self.character:getEmitter():playSound("PutItemInBag")
	
	self.phases = {
		{name="a",threshold=0.02,handler="gadgetStartSound"},
		{name="b",threshold=0.05,handler="vaccuumStartSound"},
		{name="c",threshold=0.10,handler="doInteraction"},
		{name="d",threshold=0.30,handler="endInteraction"}, -- resets back to "a" until goal is reached (then executes final interaction)
		{name="e",threshold=0.40,handler="doFinalInteraction"}, -- quick stop interaction
		{name="f",threshold=0.45,handler="endAction"}, -- perform action earlier
	}
	
end

function LSInvHarvesterAction:stop()
	stopSound(self.character, self.startSound)
	stopSound(self.character, self.sound)
	if self.vaccuumStart then self.character:getEmitter():playSound("Vaccuum_STOP"); end
    ISBaseTimedAction.stop(self);		
end

function LSInvHarvesterAction:useFuel()
	self.item:Use()
end

function LSInvHarvesterAction:harvestValidPlant()
	--if #self.plantsData <= self.loopsGoal then return; end
	local hasPlants
	for n=1,#self.plantsData do
		if not self.plantsData[n][2] and self.plantsData[n][1] then
			local plant = self.plantsData[n][1]
			if n ~= #self.plantsData then
				local randomData = ZombRand(n,#self.plantsData+1)
				if not self.plantsData[randomData][2] and self.plantsData[randomData][1] then
					plant = self.plantsData[randomData][1]
				end
			end
			plant:updateFromIsoObject()
			if plant:getObject() and plant:canHarvest() then
				self.character:faceThisObject(plant)
				local sqr = plant:getSquare()
				local args = {x=sqr:getX(),y=sqr:getY(),z=sqr:getZ()}
				CFarmingSystem.instance:sendCommand(self.character, 'harvest', args)
				CFarmingSystem.instance:gainXp(self.character, plant)
				self["useFuel"](self) -- reduce fuel
				self.character:getEmitter():playSound("Suction_Pull")
			end
			self.plantsData[n][2] = true
			hasPlants = true
			break
		end
	end
	if not itemHasUses(self.item) then self:forceStop(); end -- no fuel
	if not hasPlants then self:forceStop(); end -- in case something goes wrong (eg. another player harvests remaining plants)
end

function LSInvHarvesterAction:harvestRemainingPlants()
	for n=1,#self.plantsData do
		if not self.plantsData[n][2] and self.plantsData[n][1] then			
			local plant = self.plantsData[n][1]
			plant:updateFromIsoObject()
			if plant:getObject() and plant:canHarvest() then
				local sqr = plant:getSquare()
				local args = {x=sqr:getX(),y=sqr:getY(),z=sqr:getZ()}
				CFarmingSystem.instance:sendCommand(self.character, 'harvest', args)
				CFarmingSystem.instance:gainXp(self.character, plant)
				self["useFuel"](self) -- reduce fuel
			end
		end
	end
	self.character:getEmitter():playSound("Suction_Pull")
end

function LSInvHarvesterAction:perform()
	stopSound(self.character, self.sound)

	self["harvestRemainingPlants"](self) -- harvest any remaining plants

	self.character:getEmitter():playSound("UI_CleanObject_Perform")

	ISBaseTimedAction.perform(self);
end

local function getLoopsGoal(t)
	if type(t) ~= "table" or #t == 0 then return false; end
	local loopsGoal, threshold = #t, {{15,10},{10,6},{5,3}}
	for n=1, #threshold do
		if #t >= threshold[n][1] then loopsGoal = loopsGoal-threshold[n][2]; break; end
	end
	return loopsGoal
end

function LSInvHarvesterAction:new(Player, Item, Plants)
	--print("-----------WARN - LSInvHarvesterAction: new")
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = Player
	o.item = Item
	o.plantsData = Plants -- {plants, harvested}
	o.loopsGoal = getLoopsGoal(Plants)
	o.ignoreHandsWounds = true
    o.stopOnWalk        = true
    o.stopOnRun         = true
	o.stopOnAim         = true
	o.maxTime = 2000
	o.jobProgress = 0
	o.phases = false
	o.phaseStates = {}
	o.loopCount = 0
	o.sound = 0
	--o.gadgetStart = false
	--o.vaccuumStart = false
	o.exerciseMetabolics = "UsingTools"
	if o.character:isTimedActionInstant() then o.maxTime = 1; end
	return o;
end

return LSInvHarvesterAction