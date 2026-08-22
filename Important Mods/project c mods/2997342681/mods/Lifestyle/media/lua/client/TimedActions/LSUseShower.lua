
require "TimedActions/ISBaseTimedAction"
require "Hygiene/ShowerFunctions"

LSUseShower = ISBaseTimedAction:derive("LSUseShower");

--local isPlayingJukeSong = nil;

local function doPlayerStats(character, cleanVal)
	-------------------------
	--------------DIRT/BLOOD
	local visual, bloodCleanVal, hasDirtOrBlood = character:getHumanVisual(), cleanVal-0.01, false

	for i = 1, BloodBodyPartType.MAX:index() do
		local part = BloodBodyPartType.FromIndex(i - 1)
		local dirt, blood = visual:getDirt(part), visual:getBlood(part)
		if dirt > 0 and (dirt-cleanVal >= 0) then visual:setDirt(part, dirt-cleanVal); elseif dirt ~= 0 then visual:setDirt(part, 0); end
		if blood > 0 and (blood-bloodCleanVal >= 0) then visual:setBlood(part, blood-bloodCleanVal); elseif blood ~= 0 then visual:setBlood(part, 0); end		
		if (dirt > 0) or (blood > 0) then hasDirtOrBlood = true; end
	end	
	-------------------------
	--------------ADJUST HYGIENE NEED
	if (character:getModData().hygieneNeed > 60) and (character:getModData().hygieneNeed > 0) then
		character:getModData().hygieneNeed = math.floor((character:getModData().hygieneNeed - 3)*10)/10
	elseif (character:getModData().hygieneNeed > character:getModData().hygieneNeedLimit) and (character:getModData().hygieneNeed > 0) then
		character:getModData().hygieneNeed = math.floor((character:getModData().hygieneNeed - 2)*10)/10
			end
	if character:getModData().hygieneNeed < 0 then character:getModData().hygieneNeed = 0; end
	-------------------------
	--------------WETNESS
	if character:getBodyDamage():getWetness() < 70 then character:getBodyDamage():setWetness(70); end

	return hasDirtOrBlood
end

local function checkPlayerEmbarrassed(character, showerType)

	local wasDisturbedBy = false

	for playerIndex = 0, getNumActivePlayers()-1 do
		local playersList = {};--get players
		local playerObj = getSpecificPlayer(playerIndex)
		local playerIso

		if (playerObj ~= nil) then
			for x = playerObj:getX()-8,playerObj:getX()+8 do
				for y = playerObj:getY()-8,playerObj:getY()+8 do
					local square = getCell():getGridSquare(x,y,playerObj:getZ());
					if square then
						for i = 0,square:getMovingObjects():size()-1 do
							local moving = square:getMovingObjects():get(i);
							if instanceof(moving, "IsoPlayer") then
								table.insert(playersList, moving);
							end
						end
					end
				end
			end

			if #playersList > 0 then
				for i,v in ipairs(playersList) do
					if v:getUsername() == playerObj:getUsername() then
						playerIso = v
						break
					end
				end
				for i,v in ipairs(playersList) do
					if playerIso and
					v:getUsername() ~= playerObj:getUsername() and
					v:isOutside() == playerObj:isOutside() then
					--if playerIso:checkCanSeeClient(v) then
						if playerObj:CanSee(v) and playerIso:checkCanSeeClient(v) and not v:isInvisible() then
							local characterData = character:getModData()
							if characterData.LSMoodles["Embarrassed"] and characterData.LSMoodles["Embarrassed"].Value then
								characterData.LSMoodles["Embarrassed"].Value = characterData.LSMoodles["Embarrassed"].Value + 0.45
							end		
							if showerType == "Deluxe" then character:getEmitter():playSound("Faucet_Deluxe"); else character:getEmitter():playSound("Faucet_Common"); end
							character:getEmitter():playSound("Shower_End")
							wasDisturbedBy = v
							break
						end
					end
				end	
			end
		end
	end

	return wasDisturbedBy
end

local function doPlayerSinging(character, oldSound, AvailablePlayerVoiceTracks)

	local chanceToSing, musicSkill, originalSound, newSound = 0, 0, oldSound, false
	if character:getPerkLevel(Perks.Music) > 0 then
		musicSkill = math.floor(tonumber(character:getPerkLevel(Perks.Music))/2)
	end
	if character:HasTrait("ToneDeaf") then
		chanceToSing = ZombRand(18)+1
	else
		chanceToSing = ZombRand(20)+1
	end
				
	if (chanceToSing + musicSkill) >= 18 then
		local randomLine = ZombRand(#AvailablePlayerVoiceTracks)+1
		local sound = AvailablePlayerVoiceTracks[randomLine].sound
	
		if character:getDescriptor():isFemale() then
			sound = AvailablePlayerVoiceTracks[randomLine].soundF
		end

		if (not originalSound) or (originalSound ~= sound) then
			originalSound = sound
			newSound = sound
		end
	end

	return originalSound, newSound
end

function LSUseShower:isValid()
	--local flushed = true
	
	--if self.showerObject:getModData().NeedsFlush then
		--flushed = false
	--end
	
	return true
end

function LSUseShower:waitToStart()
	self.action:setUseProgressBar(false)
	local cX = self.showerObject:getSquare():getX()
	local cY = self.showerObject:getSquare():getY()
	--self.character:setX(cY)
	--self.character:setY(cX)
	if isClient() then
		self.character:setLy(cY)
		self.character:setLx(cX)
	end

	--local wait = false

	--if self.showerType == "Hanging" then
	--	self.character:faceThisObject(self.showerObject)
	--	wait = self.character:shouldBeTurning()
	--end
	

	return false
end

function LSUseShower:update()

	if self.doAnim == 2 then

		self.character:getEmitter():playSound("Shower_Start")
		self.doAnim = 3
		self:setActionAnim("WashFace")

	elseif self.doAnim >= 5 then--20
		
		if self.gameSoundLoop == 0 then
			if SandboxVars.LSHygiene.CleansMakeup then self:removeAllMakeup(); end
			self.gameSoundLoop = self.character:getEmitter():playSound(self.soundWaterLoop);
		end

		if self.doAnim < 8 then
			self.doAnim = 8--30
			
			if self.isFacing and self.waterObj and self.waterObjClone then
				self.spriteNum = self.spriteNum + 1
				if self.spriteNum >= 8 then
					self.spriteNum = 0
				end
				self.spriteNumClone = self.spriteNum + 1
				if self.spriteNumClone >= 8 then
					self.spriteNumClone = 0
				end
				--self.waterObj:setSprite("LS_Shower_" .. self.isFacing .. "_" .. tostring(self.spriteNum))
				self.waterObj:setSprite("LS_Fog_" .. tostring(self.spriteNum))
				self.waterObjClone:setSprite("LS_Fog_" .. tostring(self.spriteNumClone))
				--self.waterObj:transmitUpdatedSpriteToServer()
				self.waterObjClone:transmitUpdatedSpriteToServer()

			elseif self.isFacing and not self.waterObj then
				local sqrX = self.character:getX()
				local sqrY = self.character:getY()
				local offSet
				if self.isFacing == "S" or self.isFacing == "N" or self.isFacing == "E" or self.isFacing == "W" then--this didnt need to exist but its a nice reference for future stuff
					sqrY = self.character:getY()+1
					offSet = -1	
				end
				self.tileSqr = getCell():getGridSquare(sqrX, sqrY, self.character:getZ())
				self.tileSqrClone = getCell():getGridSquare(self.character:getX(), self.character:getY(), self.character:getZ())
				--self.waterObj = IsoObject.new(self.tileSqr, "LS_Shower_" .. self.isFacing .. "_0")
				self.waterObj = IsoObject.new(self.tileSqr, "LS_Fog_0")
				self.waterObjClone = IsoObject.new(self.tileSqrClone, "LS_Fog_1")
				--self.waterObj:setCustomColor(1, 1, 1, 1)
				--self.waterObj:renderlast()
				if offSet then
					self.waterObj:setOffsetX(offSet)
				end
				self.waterObj:setAlpha(0.8)
				self.waterObjClone:setAlpha(0.4)
				self.tileSqr:AddTileObject(self.waterObj)	
				self.tileSqrClone:AddTileObject(self.waterObjClone)	
				--self.waterObj:transmitModData()
				--self.waterObj:transmitCompleteItemToServer()
				self.waterObjClone:transmitCompleteItemToServer()
			end
		elseif self.doAnim < 12 then--50
			self.doAnim = self.doAnim + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
		elseif self.doAnim >= 12 then
			self.doAnim = 7--29
		end

		if self.decreaseNeed >= self.decreaseNeedTotal then
			self.decreaseNeed = 0

			if self.depressed == 1 then

				self.depressed = 2
			elseif self.depressed == 2 then
				self.depressed = 3
				self:setActionAnim("Bob_ShowerSadStart")
			elseif self.depressed == 3 then
				if self.facing then
					print("SHOWER IS FACING: " .. self.facing)
					if self.facing == "N" then
						self.character:faceLocation(self.character:getX(), self.character:getY()-1)
						self.character:faceLocationF(self.character:getX(), self.character:getY()-1)
					elseif self.facing == "S" then
						self.character:faceLocation(self.character:getX(), self.character:getY()+1)
						self.character:faceLocationF(self.character:getX(), self.character:getY()+1)
					elseif self.facing == "E" then
						self.character:faceLocation(self.character:getX()+1, self.character:getY())
						self.character:faceLocationF(self.character:getX()+1, self.character:getY())
					elseif self.facing == "W" then
						self.character:faceLocation(self.character:getX()-1, self.character:getY())
						self.character:faceLocationF(self.character:getX()-1, self.character:getY())
					end

				end
				self:setActionAnim("Bob_ShowerSad")
				self.depressed = 4
			elseif self.depressed >= 4 then
				self.depressed = self.depressed + 1
			end
		
			addSound(self.character,
				 self.character:getX(),
				 self.character:getY(),
				 self.character:getZ(),
				 6,
				 5)

			-------------------------
			--------------DO STATS
			local hasDirtOrBlood = doPlayerStats(self.character, self.showerCleanVal)

			-------------------------
			--------------PERFORM CONDITIONS (no water / hygiene fulfilled and no dirt/blood)
			if (self.character:getModData().hygieneNeed < 40) and not hasDirtOrBlood and ((self.depressed == 0) or (self.depressedEnd >= 1))  then
				self.character:getEmitter():stopSound(self.gameSoundLoop)
				self.gameSoundLoop = 0
				if self.showerType == "Deluxe" then
					self.character:getEmitter():playSound("Faucet_Deluxe")
				else
					self.character:getEmitter():playSound("Faucet_Common")
				end
				self.character:getEmitter():playSound("Shower_End")
				self:forceComplete()
			elseif (self.character:getModData().hygieneNeed < 40) and not hasDirtOrBlood and self.depressed > 30 and self.depressedEnd == 0 then
				self:setActionAnim("Bob_ShowerSadEnd")
				self.depressedEnd = 1
			end

			-------------------------
			--------------EMBARRASSED
			if isClient() and not SandboxVars.LSHygiene.NotEmbarrassed then
				self.wasDisturbedBy = checkPlayerEmbarrassed(self.character, self.showerType)

				if self.wasDisturbedBy then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Embarrassed"), true, 255, 120, 120)
					self:forceComplete()
				end
			end			
			-------------------------
			--------------HALO
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Hygiene"), true, 170, 255, 150)
			--------------SINGING
			local isPlaying = self.gameSoundVoice and self.character:getEmitter():isPlaying(self.gameSoundVoice)
			
			if self.AvailablePlayerVoiceTracks and
			#self.AvailablePlayerVoiceTracks > 0 and not
			isPlaying then
				local oldSound, newSound = self.lastSound, false
				self.lastSound, newSound = doPlayerSinging(self.character, oldSound, self.AvailablePlayerVoiceTracks)
				if newSound then self.gameSoundVoice = self.character:getEmitter():playSound(newSound); end
			end
			
			--------------HEAT
			if self.showerHeat then
				if not ((SandboxVars.ElecShutModifier > -1 and
				GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
				self.showerObject:getSquare():haveElectricity()) then
					--print("no electricity for shower")
					--self.showerObject:getCell():removeHeatSource(self.showerHeat)
					self.showerHeat:destroy()
					self.showerHeat = nil
				end
			
			end
		else
			self.decreaseNeed = self.decreaseNeed + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
		end

	else
		self.doAnim = self.doAnim + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
		--getAverageFSP()
		--getPerformance():getLockFPS()
		--(getGameTime():getMultiplier() * 60 / getAverageFSP())
	end

end

function LSUseShower:start()

	--if self.showerObject:getModData().NeedsFlush then
		--self:forceStop()
	--end
	--self:setActionAnim("Loot")
	--self.character:SetVariable("LootPosition", "Mid")

	--local sm = getSearchMode():getSearchModeForPlayer(self.character:getPlayerNum())
	--sm:getBlur():setTargets(1, 1);
	--sm:getDesat():setTargets(0.5, 0.5);
	--sm:getRadius():setTargets(0.5, 0.5);
	--sm:getRadius():set(2, 20, 2, 20);
	--sm:getDarkness():setTargets(0.5, 0.5);
	--sm:getGradientWidth():setTargets(1, 1);

	--getSearchMode():setEnabled(self.character:getPlayerNum(), true)
	
	self:setOverrideHandModels(nil, nil)

	local characterData = self.character:getModData()
	local PlayerVoice = characterData.PlayerVoice

	local PlayerVoiceTracks = require("TimedActions/PlayerVoiceTracks")
	local PlayerVoiceHygieneTracks = require("Hygiene/Tracks/PlayerVoiceHygiene")
	self.AvailablePlayerVoiceTracks = {}

-----------DEPRESSED
	local currentUnhappiness = self.character:getBodyDamage():getUnhappynessLevel()

	if currentUnhappiness > 30 then
		self.depressed = 1
	end
-------------
	
	-- we loop the voice tracks and select the ones that we want, making sure to only select the ones that match the player voice
	if self.depressed == 0 then
		if (self.character:HasTrait("Virtuoso") or (self.character:getPerkLevel(Perks.Music) > 4)) and not self.character:HasTrait("ToneDeaf") then
			for k,v in pairs(PlayerVoiceTracks) do
				if v.Voice == PlayerVoice and
				v.Type == "SingGood" then--MAKE SURE TO CHANGE THIS LINE
					table.insert(self.AvailablePlayerVoiceTracks, v)
				end
			end
		end
		if self.character:HasTrait("ToneDeaf") then
			for k,v in pairs(PlayerVoiceHygieneTracks) do
				if v.Type == "bad" then--MAKE SURE TO CHANGE THIS LINE
					table.insert(self.AvailablePlayerVoiceTracks, v)
				end
			end
		else
			for k,v in pairs(PlayerVoiceHygieneTracks) do
				if v.Type == "hum" then--MAKE SURE TO CHANGE THIS LINE
					table.insert(self.AvailablePlayerVoiceTracks, v)
				end
			end
		end
	else
		for k,v in pairs(PlayerVoiceTracks) do
			if v.Voice == PlayerVoice and
			v.Type == "Depressed" then
				table.insert(self.AvailablePlayerVoiceTracks, v)
			end
		end			
	end

	if self.showerType == "Deluxe" then
		self.character:getEmitter():playSound("Faucet_Deluxe")
		self.showerCleanVal = 0.04
	else
		self.character:getEmitter():playSound("Faucet_Common")
	end
	self:setActionAnim("Loot")

	self.character:getModData().hygieneNeed = self.character:getModData().hygieneNeed or 0
	
	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit or 100
	
	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit - 65

----------------HEAT

	if not ((SandboxVars.ElecShutModifier > -1 and
	GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
	self.showerObject:getSquare():haveElectricity()) then
		--print("no electricity for shower")
	else
		local square = getSquare(self.showerObject:getX(), self.showerObject:getY(), self.showerObject:getZ())
		if square then
			self.showerHeat = HygieneHeatObject:new(self.showerObject:getSquare():getX(), self.showerObject:getSquare():getY(), self.showerObject:getSquare():getZ(), 3, 35)
			--self.showerHeat = HygieneHeatObject:new(self.character:getX(), self.character:getY(), self.character:getZ(), 15, 30)
		end
	end

-----------FACING
	local properties = self.showerObject:getSprite():getProperties()
	
	if properties:Is("Facing") then
		self.facing = properties:Val("Facing")
	end


end

function LSUseShower:stop()

	local characterData = self.character:getModData()

	if self.showerHeat then--ADD NEG/POS MOODLES HERE
		--self.showerObject:getCell():removeHeatSource(self.showerHeat)
		self.showerHeat:destroy()
	elseif characterData.LSMoodles["BathCold"] and characterData.LSMoodles["BathCold"].Value then
		if characterData.LSMoodles["BathHot"] and characterData.LSMoodles["BathHot"].Value and characterData.LSMoodles["BathHot"].Value > 0 then
			characterData.LSMoodles["BathHot"].Value = 0
		end
			characterData.LSMoodles["BathCold"].Value = 0.2
	end

	if isClient() and self.tileSqr and self.waterObj then
		--sledgeDestroy(self.waterObj);
		sledgeDestroy(self.waterObjClone);
		self.tileSqr:transmitRemoveItemFromSquare(self.waterObj)
		self.tileSqr:RemoveTileObject(self.waterObj)
	elseif self.tileSqr and self.waterObj then
		self.tileSqr:transmitRemoveItemFromSquare(self.waterObj)
		self.tileSqr:RemoveTileObject(self.waterObj)
		self.tileSqrClone:transmitRemoveItemFromSquare(self.waterObjClone)
		self.tileSqrClone:RemoveTileObject(self.waterObjClone)
		--self.waterObj:transmitCompleteItemToServer()
	end

	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit + 65
	self.character:getBodyDamage():decreaseBodyWetness(self.character:getBodyDamage():getWetness() / 20)

	self.character:getModData().lastBath = self.character:getHoursSurvived()

	if self.gameSoundLoop ~= 0 then
		self.character:getEmitter():stopSound(self.gameSoundLoop)
	end
	if self.gameSoundVoice then
		self.character:getEmitter():stopSound(self.gameSoundVoice)
	end
	if self.showerType == "Deluxe" then
		self.character:getEmitter():playSound("Faucet_Deluxe")
	else
		self.character:getEmitter():playSound("Faucet_Common")
	end
	self.character:getEmitter():playSound("Shower_End")

	--getSearchMode():setEnabled(self.character:getPlayerNum(), false)

	self.character:resetModelNextFrame();
	sendVisual(self.character);
	triggerEvent("OnClothingUpdated", self.character)

	local dice10 = ZombRand(10) + 1
	local addDirt = ZombRand(4) + 1
	
	if self.character:HasTrait("Sloppy") then
		addDirt = 4
	elseif self.character:HasTrait("Tidy") then
		addDirt = 1
	end

	local thisDirtSprite

	if dice10 > 5 then--REENABLE THIS
		if self.showerObject:getModData().Condition then
			self.showerObject:getModData().Condition = self.showerObject:getModData().Condition + addDirt
			if self.showerObject:getModData().Condition > 100 then
				self.showerObject:getModData().Condition = 100
			end
		else
			self.showerObject:getModData().Condition = addDirt
		end

		if self.showerObject:getModData().ConditionLevel then
			if self.showerObject:getModData().ConditionLevel == 0 and self.showerObject:getModData().Condition >= 30 then
				self.showerObject:getModData().ConditionLevel = 1
				thisDirtSprite = self.overlayDirtSprite
			elseif self.showerObject:getModData().ConditionLevel == 1 and self.showerObject:getModData().Condition >= 60 then
				self.showerObject:getModData().ConditionLevel = 2
				thisDirtSprite = self.overlayDirtSprite2
			elseif self.showerObject:getModData().ConditionLevel == 2 and self.showerObject:getModData().Condition >= 90 then
				self.showerObject:getModData().ConditionLevel = 3
				thisDirtSprite = self.overlayDirtSprite3
			end
		else
			self.showerObject:getModData().ConditionLevel = 0
		end
	end
	
	if self.showerObject:getModData().ConditionLevel == 1 then
		thisDirtSprite = self.overlayDirtSprite
	elseif self.showerObject:getModData().ConditionLevel == 2 then
		thisDirtSprite = self.overlayDirtSprite2
	elseif self.showerObject:getModData().ConditionLevel == 3 then
		thisDirtSprite = self.overlayDirtSprite3
	end
	
	----debug
		--self.character:Say("toilet condition is " .. tonumber(self.showerObject:getModData().Condition) .. " and level is " .. tonumber(self.showerObject:getModData().ConditionLevel))
		--if thisDirtSprite then
		--	self.character:Say("sprite is " .. tostring(thisDirtSprite))
		--end
	----

	if isClient() and thisDirtSprite then
		self.showerObject:setOverlaySprite(thisDirtSprite, true)
		self.showerObject:transmitUpdatedSpriteToServer()
		self.showerObject:transmitModData()
	elseif isClient() then
		self.showerObject:transmitModData()
	elseif thisDirtSprite then
		self.showerObject:setOverlaySprite(thisDirtSprite, false)
	end

	if self.wearClothes then
		self.character:getEmitter():playSound("ChangeClothes")
		ClothesAboutToChange(self.character, self.showerObject, "isBathNoLaundryEnd")
	end

    ISBaseTimedAction.stop(self);		
end

function LSUseShower:perform()

	local characterData = self.character:getModData()
	--if self.tileSqr and self.waterObj then
		--self.tileSqr:RemoveTileObject(self.waterObj)
		--self.tileSqr:transmitRemoveItemFromSquare(self.waterObj)
		--self.tileSqr:transmitRemoveItemFromSquareOnServer(self.waterObj)
		--self.tileSqr:RemoveTileObject(self.waterObj)
		--self.waterObj:transmitCompleteItemToServer()
	--end

	--if isClient() and self.tileSqr and self.waterObj then
	--	sledgeDestroy(self.waterObj);
	--elseif self.tileSqr and self.waterObj then
		--self.tileSqr:transmitRemoveItemFromSquare(self.waterObj)
		--self.tileSqr:RemoveTileObject(self.waterObj)
		--self.waterObj:transmitCompleteItemToServer()
	--end

	if self.showerHeat then--ADD NEG/POS MOODLES HERE
		--self.showerObject:getCell():removeHeatSource(self.showerHeat)
		self.showerHeat:destroy()
		if characterData.LSMoodles["BathCold"] and characterData.LSMoodles["BathCold"].Value and characterData.LSMoodles["BathCold"].Value > 0 then
			characterData.LSMoodles["BathCold"].Value = 0
		end
		if characterData.LSMoodles["BathHot"] and characterData.LSMoodles["BathHot"].Value then
			characterData.LSMoodles["BathHot"].Value = 0.2
		end
	elseif characterData.LSMoodles["BathCold"] and characterData.LSMoodles["BathCold"].Value then
		if characterData.LSMoodles["BathHot"] and characterData.LSMoodles["BathHot"].Value and characterData.LSMoodles["BathHot"].Value > 0 then
			characterData.LSMoodles["BathHot"].Value = 0
		end
		characterData.LSMoodles["BathCold"].Value = 0.2
	end

	if isClient() and self.tileSqr and self.waterObj then
		--sledgeDestroy(self.waterObj);
		sledgeDestroy(self.waterObjClone);
		self.tileSqr:transmitRemoveItemFromSquare(self.waterObj)
		self.tileSqr:RemoveTileObject(self.waterObj)
	elseif self.tileSqr and self.waterObj then
		self.tileSqr:transmitRemoveItemFromSquare(self.waterObj)
		self.tileSqr:RemoveTileObject(self.waterObj)
		self.tileSqrClone:transmitRemoveItemFromSquare(self.waterObjClone)
		self.tileSqrClone:RemoveTileObject(self.waterObjClone)
		--self.waterObj:transmitCompleteItemToServer()
	end

	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit + 65
	self.character:getBodyDamage():decreaseBodyWetness(self.character:getBodyDamage():getWetness() / 20)

	self.character:getModData().lastBath = self.character:getHoursSurvived()

	if self.gameSoundLoop ~= 0 then
		self.character:getEmitter():stopSound(self.gameSoundLoop)
	end
	if self.gameSoundVoice then
		self.character:getEmitter():stopSound(self.gameSoundVoice)
	end
	if self.showerType == "Deluxe" then
		self.character:getEmitter():playSound("Faucet_Deluxe")
	else
		self.character:getEmitter():playSound("Faucet_Common")
	end
	self.character:getEmitter():playSound("Shower_End")
	--getSearchMode():setEnabled(self.character:getPlayerNum(), false)

	--self.character:getModData().bathroomNeed = 0

	self.character:resetModelNextFrame();
	sendVisual(self.character);
	triggerEvent("OnClothingUpdated", self.character)
	
	local dice20 = ZombRand(20) + 1
	local soundrandomiser = ZombRand(1, 100)
	local sound = "Zipper_CLOSE1"

	local dice10 = ZombRand(10) + 1
	local addDirt = ZombRand(4) + 1
	
	if self.character:HasTrait("Sloppy") then
		addDirt = 4
	elseif self.character:HasTrait("Tidy") then
		addDirt = 1
	end

	local thisDirtSprite

	if dice10 > 5 then
		if self.showerObject:getModData().Condition then
			self.showerObject:getModData().Condition = self.showerObject:getModData().Condition + addDirt
			if self.showerObject:getModData().Condition > 100 then
				self.showerObject:getModData().Condition = 100
			end
		else
			self.showerObject:getModData().Condition = addDirt
		end

		if self.showerObject:getModData().ConditionLevel then
			if self.showerObject:getModData().ConditionLevel == 0 and self.showerObject:getModData().Condition >= 30 then
				self.showerObject:getModData().ConditionLevel = 1
				thisDirtSprite = self.overlayDirtSprite
			elseif self.showerObject:getModData().ConditionLevel == 1 and self.showerObject:getModData().Condition >= 60 then
				self.showerObject:getModData().ConditionLevel = 2
				thisDirtSprite = self.overlayDirtSprite2
			elseif self.showerObject:getModData().ConditionLevel == 2 and self.showerObject:getModData().Condition >= 90 then
				self.showerObject:getModData().ConditionLevel = 3
				thisDirtSprite = self.overlayDirtSprite3
			end
		else
			self.showerObject:getModData().ConditionLevel = 0
		end
	end

	if self.showerObject:getModData().ConditionLevel == 1 then
		thisDirtSprite = self.overlayDirtSprite
	elseif self.showerObject:getModData().ConditionLevel == 2 then
		thisDirtSprite = self.overlayDirtSprite2
	elseif self.showerObject:getModData().ConditionLevel == 3 then
		thisDirtSprite = self.overlayDirtSprite3
	end

	if isClient() and thisDirtSprite then
		self.showerObject:setOverlaySprite(thisDirtSprite, true)
		self.showerObject:transmitUpdatedSpriteToServer()
		self.showerObject:transmitModData()
	elseif isClient() then
		self.showerObject:transmitModData()
	elseif thisDirtSprite then
		self.showerObject:setOverlaySprite(thisDirtSprite, false)
	end


	if self.wasDisturbedBy then
	
		if characterData.LSMoodles["Embarrassed"] and characterData.LSMoodles["Embarrassed"].Value and (characterData.LSMoodles["Embarrassed"].Value > 1) then
			characterData.LSMoodles["Embarrassed"].Value = 1
		end

		local TargetID = self.wasDisturbedBy:getOnlineID()
		
		local TargetX = self.wasDisturbedBy:getX()
		local TargetY = self.wasDisturbedBy:getY()
		
		sendClientCommand(self.character, "LS", "SendGetEmbarrassed", {TargetID})
		if self.wearClothes then
			ShowerFunctions.DoActionDisturbed(self.character, TargetX, TargetY, self.showerObject, true)
		else
			ShowerFunctions.DoActionDisturbed(self.character, TargetX, TargetY, self.showerObject, false)
		end
	elseif self.wearClothes then
		ShowerFunctions.DoAction(self.character, self.showerObject)
	end

	ISBaseTimedAction.perform(self);

end

function LSUseShower:removeAllMakeup()
	local item = self.character:getWornItem("MakeUp_FullFace");
	self:removeMakeup(item);
	item = self.character:getWornItem("MakeUp_Eyes");
	self:removeMakeup(item);
	item = self.character:getWornItem("MakeUp_EyesShadow");
	self:removeMakeup(item);
	item = self.character:getWornItem("MakeUp_Lips");
	self:removeMakeup(item);
end

function LSUseShower:removeMakeup(item)
	if item then
		self.character:removeWornItem(item);
		self.character:getInventory():Remove(item);
	end
end

function LSUseShower:new(character, Shower, Type, WaterUsage, faucetSound, soundLoop, facingDir, dirtSprite, dirtSprite2, dirtSprite3, wasDressed)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.showerObject = Shower
	o.showerType = Type
	o.waterUsage = WaterUsage
	o.soundFaucet = faucetSound
	o.soundWaterLoop = soundLoop
	o.isFacing = facingDir
	o.wearClothes = wasDressed
	o.gameSoundLoop = 0
	o.ignoreHandsWounds = true
    o.stopOnWalk = true
    o.stopOnRun = true
    o.stopOnAim = true
	o.maxTime = 6000
	o.doAnim = 2
	o.decreaseNeed = 0
	o.decreaseNeedTotal = 14--70
	o.showerCleanVal = 0.03
	o.overlayDirtSprite = dirtSprite
	o.overlayDirtSprite2 = dirtSprite2
	o.overlayDirtSprite3 = dirtSprite3
	o.wasDisturbedBy = false
	o.waterObj = false
	o.waterObjClone = false
	o.spriteNum = 0
	o.spriteNumClone = 0
	o.tileSqr = false
	o.tileSqrClone = false
	o.AvailablePlayerVoiceTracks = false
	o.gameSoundVoice = false
	o.lastSound = false
	o.showerHeat = false
	o.depressed = 0
	o.depressedEnd = 0
	o.facing = false
	return o;
end

return LSUseShower