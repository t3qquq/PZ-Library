
require "TimedActions/ISBaseTimedAction"
require "Hygiene/ToiletFunctions"

LSFlushToilet = ISBaseTimedAction:derive("LSFlushToilet");

--local isPlayingJukeSong = nil;

local function doDirtPuddle(thisObject)

	local puddleList = {"LS_HScraps_DirtPuddle_0","LS_HScraps_DirtPuddle_1","LS_HScraps_DirtPuddle_2","LS_HScraps_DirtPuddle_3","LS_HScraps_DirtPuddle_4",
	"LS_HScraps_DirtPuddle_5","LS_HScraps_DirtPuddle_6","LS_HScraps_DirtPuddle_7"}
	local dirtSprite = puddleList[ZombRand(#puddleList)+1]
	
		--thisPlayer:Say(tostring(dirtSprite))
		--local x = thisPlayer:getX()
		--local y = thisPlayer:getY()
		--local z = thisPlayer:getZ()
		--sendClientCommand("LS", "DebugAddLitter", {x, y, z, dirtSolid, dirtSprite})
	if isClient() then
		sendClientCommand("LS", "AddDirtPuddle", {thisObject:getX(), thisObject:getY(), thisObject:getZ(), 2, dirtSprite})
	else
		LSAddLitter(thisObject:getX(), thisObject:getY(), thisObject:getZ(), 2, dirtSprite)
	end

end

function LSFlushToilet:isValid()
	return true;
end

function LSFlushToilet:waitToStart()
	self.action:setUseProgressBar(false)
	if isClient() then
		local cX = self.character:getX()
		local cY = self.character:getY()
		self.character:setLy(cY)
		self.character:setLx(cX)
	end
	self.character:faceThisObject(self.toiletObject);
	return self.character:shouldBeTurning();
end

function LSFlushToilet:update()

	self.character:faceThisObject(self.toiletObject)

end

function LSFlushToilet:start()

	self:setActionAnim("Bob_IsFlushing")
	--self.character:SetVariable("LootPosition", "Mid")


end

function LSFlushToilet:stop()

    ISBaseTimedAction.stop(self);
		
end

function LSFlushToilet:perform()

	if self.toiletObject:getModData().IsClogged and self.toiletObject:getModData().IsClogged >= 0 then

		self.character:getEmitter():playSound("Toilet_Flush_Clogged")
		
		--first we reset the toilet unclog work
		self.toiletObject:getModData().IsClogged = 0
		
		if self.toiletObject:getModData().Condition then
			self.toiletObject:getModData().Condition = self.toiletObject:getModData().Condition + 20
			if self.toiletObject:getModData().Condition > 100 then
				self.toiletObject:getModData().Condition = 100
			end
		else
			self.toiletObject:getModData().Condition = 40
		end

		local thisDirtSprite

		if self.toiletObject:getModData().ConditionLevel then
			if self.toiletObject:getModData().ConditionLevel == 0 and self.toiletObject:getModData().Condition >= 30 then
				self.toiletObject:getModData().ConditionLevel = 1
				thisDirtSprite = self.overlayDirtSprite
			elseif self.toiletObject:getModData().ConditionLevel == 1 and self.toiletObject:getModData().Condition >= 60 then
				self.toiletObject:getModData().ConditionLevel = 2
				thisDirtSprite = self.overlayDirtSprite2
			elseif self.toiletObject:getModData().ConditionLevel == 2 and self.toiletObject:getModData().Condition >= 90 then
				self.toiletObject:getModData().ConditionLevel = 3
				thisDirtSprite = self.overlayDirtSprite3
			end
		else
			self.toiletObject:getModData().ConditionLevel = 0
		end
		
		if isClient() and thisDirtSprite then
			self.toiletObject:setOverlaySprite(thisDirtSprite, true)
			self.toiletObject:transmitUpdatedSpriteToServer()
			self.toiletObject:transmitModData()
		elseif isClient() then
			self.toiletObject:transmitModData()
		elseif thisDirtSprite then
			self.toiletObject:setOverlaySprite(thisDirtSprite, false)
		end
		
		--do more dirt if not max and add brown puddles around the toilet
		doDirtPuddle(self.toiletObject)
	else
		self.character:getEmitter():playSound(self.soundFlush)
		self.toiletObject:getModData().NeedsFlush = false
		self.toiletObject:setWaterAmount(self.toiletObject:getWaterAmount() - self.waterUsage)
	end

	if isClient() then
		self.toiletObject:transmitModData()
	end

	ISBaseTimedAction.perform(self);

end

function LSFlushToilet:new(character, Toilet, Type, WaterUsage, Flush, SeatDown, dirtSprite, dirtSprite2, dirtSprite3)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.toiletObject = Toilet
	o.toiletType = Type
	o.waterUsage = WaterUsage
	o.soundFlush = Flush
	o.ignoreHandsWounds = true
    o.stopOnWalk = true
    o.stopOnRun = true
	o.maxTime = 40
	o.overlayDirtSprite = dirtSprite
	o.overlayDirtSprite2 = dirtSprite2
	o.overlayDirtSprite3 = dirtSprite3
	return o;
end

return LSFlushToilet