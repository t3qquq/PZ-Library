
require "TimedActions/ISBaseTimedAction"
require "Hygiene/BathTubFunctions"

LSUseTub = ISBaseTimedAction:derive("LSUseTub");

--local isPlayingJukeSong = nil;

local function getDirtSprites(ThisSpriteName)

	local dirtSprite, dirtSprite2, dirtSprite3

	if (ThisSpriteName == "fixtures_bathroom_01_26") then
		dirtSprite = "LS_Misc_2_0"
		dirtSprite2 = "LS_Misc_2_8"
		dirtSprite3 = "LS_Misc_2_16"
	elseif (ThisSpriteName == "fixtures_bathroom_01_27") then
		dirtSprite = "LS_Misc_2_1"
		dirtSprite2 = "LS_Misc_2_9"
		dirtSprite3 = "LS_Misc_2_17"
	elseif (ThisSpriteName == "fixtures_bathroom_01_25") then
		dirtSprite = "LS_Misc_2_2"
		dirtSprite2 = "LS_Misc_2_10"
		dirtSprite3 = "LS_Misc_2_18"
	elseif (ThisSpriteName == "fixtures_bathroom_01_24") then
		dirtSprite = "LS_Misc_2_3"
		dirtSprite2 = "LS_Misc_2_11"
		dirtSprite3 = "LS_Misc_2_19"
	elseif (ThisSpriteName == "fixtures_bathroom_01_55") then
		dirtSprite = "LS_Misc_2_4"
		dirtSprite2 = "LS_Misc_2_12"
		dirtSprite3 = "LS_Misc_2_20"
	elseif (ThisSpriteName == "fixtures_bathroom_01_54") then
		dirtSprite = "LS_Misc_2_5"
		dirtSprite2 = "LS_Misc_2_13"
		dirtSprite3 = "LS_Misc_2_21"
	elseif (ThisSpriteName == "fixtures_bathroom_01_53") then
		dirtSprite = "LS_Misc_2_6"
		dirtSprite2 = "LS_Misc_2_14"
		dirtSprite3 = "LS_Misc_2_22"
	elseif (ThisSpriteName == "fixtures_bathroom_01_52") then
		dirtSprite = "LS_Misc_2_7"
		dirtSprite2 = "LS_Misc_2_15"
		dirtSprite3 = "LS_Misc_2_23"
	end

	return dirtSprite, dirtSprite2, dirtSprite3

end

local function getXYTexture(Facing, bubbleBath)

	if not Facing then return false, false, false, false, false, false; end
	local offSetX, offSetY, t1, t2, posX, posY, tileX, tileY = 1, 0, "LS_Fog_10", "LS_Fog_11", 0.2, 0, 0, 1
	if (bubbleBath > 0) then t1, t2 = "LS_Fog_14", "LS_Fog_15"; end

	if (Facing == "E") or (Facing == "W") then
		offSetX, offSetY, t1, t2, posX, posY, tileX, tileY = 1, 0, "LS_Fog_8", "LS_Fog_9", 0, 0.2, 0, 1
		if (bubbleBath > 0) then t1, t2 = "LS_Fog_12", "LS_Fog_13"; end
	end

	return offSetX, offSetY, t1, t2, posX, posY, tileX, tileY

end

local function getTubEnterXY(Facing)

	if not Facing then return false, false; end

	if Facing == "S" then
		return 0.2, 0.35
	elseif Facing == "E" then
		return 0.3, 0.2
	elseif Facing == "N" then
		return 0, 0
	elseif Facing == "W" then
		return 0, 0
	end

	return false, false

end

local function getFacingLocation(Facing)

	if Facing == "N" then
		return 0, -1
	elseif Facing == "S" then
		return 0, 1
	elseif Facing == "E" then
		return 1, 0
	elseif Facing == "W" then
		return -1, 0
	end

	return 0, 0

end

local function getBubbleIdx(Facing, Part)

	if ((Facing == "E") or (Facing == "W")) and (Part == "M") then
	return {"LS_Bubbles_0","LS_Bubbles_8","LS_Bubbles_16","LS_Bubbles_24","LS_Bubbles_32","LS_Bubbles_40","LS_Bubbles_48","LS_Bubbles_56"}
	elseif ((Facing == "E") or (Facing == "W")) and (Part == "B") then
	return {"LS_Bubbles_1","LS_Bubbles_9","LS_Bubbles_17","LS_Bubbles_25","LS_Bubbles_33","LS_Bubbles_41","LS_Bubbles_49","LS_Bubbles_57"}
	elseif ((Facing == "S") or (Facing == "N")) and (Part == "M") then
	return {"LS_Bubbles_2","LS_Bubbles_10","LS_Bubbles_18","LS_Bubbles_26","LS_Bubbles_34","LS_Bubbles_42","LS_Bubbles_50","LS_Bubbles_58"}
	elseif ((Facing == "S") or (Facing == "N")) then
	return {"LS_Bubbles_3","LS_Bubbles_11","LS_Bubbles_19","LS_Bubbles_27","LS_Bubbles_35","LS_Bubbles_43","LS_Bubbles_51","LS_Bubbles_59"}
	end

end

local function doIdxVariation(idx, limit)

	local variation = ZombRand(2) + 1
	local newIdx

	if idx == limit then--last
		newIdx = idx-variation
	elseif idx == 1 then--first
		newIdx = idx+variation
	else
		if variation == 1 then
			newIdx = idx-1
		elseif variation == 2 then
			newIdx = idx+1
		end
	end

	return newIdx

end

local function getBubbleTexture(Facing, TM, TB)

	if not Facing then return false, false; end

	local bubbleTexturesM = getBubbleIdx(Facing, "M")
	local bubbleTexturesB = getBubbleIdx(Facing, "B")

	local idxBTM = ZombRand(#bubbleTexturesM) + 1
	local idxBTB = ZombRand(#bubbleTexturesB) + 1

	

	if TM and bubbleTexturesM[idxBTM] == TM then
		local newIdx = doIdxVariation(idxBTM, 8)
		idxBTM = newIdx
	end
	if TB and bubbleTexturesB[idxBTB] == TB then
		local newIdx = doIdxVariation(idxBTB, 8)
		idxBTB = newIdx
	end

	return bubbleTexturesM[idxBTM], bubbleTexturesB[idxBTB]

end

local function getSoundIdx(sound, isFemale)

	if sound == "WashBody" then
	return {"Tub_BodyClean1","Tub_BodyClean2","Tub_BodyClean3","Tub_BodyClean4"}
	elseif sound == "WaterSplash" then
	return {"Tub_Splash1","Tub_Splash2","Tub_Splash3","Tub_Splash4"}
	elseif (sound == "Voice_Yawn") and isFemale then
	return {"WomanBored01","WomanBored02","WomanYawn01","WomanYawn02"}
	elseif (sound == "Voice_Yawn") then
	return {"ManBored01","ManBored02","ManYawn01","ManYawn02"}
	end

end

local function getAnimSoundVariation(sound, oldSound, isFemale)

	local newSound = getSoundIdx(sound, isFemale)

	local idxS = ZombRand(#newSound) + 1

	if oldSound and newSound[idxS] == oldSound then
		local newIdx = doIdxVariation(idxS, 4)
		idxS = newIdx
	end

	return newSound[idxS]

end

local function doAnimRoutine(oldAnim)
	
	TubAnimList = {
		{name="Bob_Tub_WashArms",animTime=300,soundType="WashBody",soundTime=25,isSingle=false},
		{name="Bob_Tub_Rest",animTime=450,soundType="Voice_Yawn",soundTime=10,isSingle=true},
		{name="Bob_Tub_WashFace",animTime=300,soundType="WashBody",soundTime=25,isSingle=false},
		{name="Bob_Tub_WashArms",animTime=300,soundType="WashBody",soundTime=25,isSingle=false},
	}

	local idxA = ZombRand(#TubAnimList) + 1

	if oldAnim and TubAnimList[idxA].name == oldAnim then
		local newIdx = doIdxVariation(idxA, 4)
		idxA = newIdx
	end

	return TubAnimList[idxA].name, TubAnimList[idxA].animTime, TubAnimList[idxA].soundType, TubAnimList[idxA].soundTime, TubAnimList[idxA].isSingle
	--anim, sound type, sound time
end

local function checkPlayerEmbarrassed(character)

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

local function doPlayerStats(character, cleanVal, bubbleBath)

	-------------------------
	--------------DIRT/BLOOD
	local visual = character:getHumanVisual()
	local bloodCleanVal = cleanVal-0.01
	local hasDirtOrBlood = false
	local hygienePenalty = bubbleBath

	for i = 1, BloodBodyPartType.MAX:index() do
		local part = BloodBodyPartType.FromIndex(i - 1)
		local dirt = visual:getDirt(part)
		local blood = visual:getBlood(part)
				
		if dirt > 0 and (dirt-cleanVal >= 0) then
			visual:setDirt(part, dirt-cleanVal)
		elseif dirt > 0 or dirt < 0 then
			visual:setDirt(part, 0)
			--visual:removeDirt()
		end
		if blood > 0 and (blood-bloodCleanVal >= 0) then
			visual:setBlood(part, blood-bloodCleanVal)
		elseif blood > 0 or blood < 0 then
			visual:setBlood(part, 0)
			--visual:removeBlood()
		end
				
		if (visual:getDirt(part) > 0) or (visual:getBlood(part) > 0) then
			hasDirtOrBlood = true
		end
	end
		
	-------------------------
	--------------ADJUST HYGIENE NEED
	if (character:getModData().hygieneNeed > 60) then
		character:getModData().hygieneNeed = math.floor((character:getModData().hygieneNeed - (2.5-hygienePenalty))*10)/10
		
	elseif (character:getModData().hygieneNeed > character:getModData().hygieneNeedLimit) and (character:getModData().hygieneNeed > 0) then
		character:getModData().hygieneNeed = math.floor((character:getModData().hygieneNeed - (1.5-(hygienePenalty*0.5)))*10)/10
	end
	if character:getModData().hygieneNeed < 0 then
		character:getModData().hygieneNeed = 0
	end
	--character:Say("Hygiene need is now " .. character:getModData().hygieneNeed)

	-------------------------
	--------------ADJUST MOOD
	local bodyDamage, stats = character:getBodyDamage(), character:getStats()
	local currentBoredom, currentUnhappyness, currentStress, currentExhaustion = bodyDamage:getBoredomLevel(), bodyDamage:getUnhappynessLevel(), stats:getStress(), stats:getEndurance()
	if character:HasTrait("Smoker") then
		stats:setStressFromCigarettes(0)
	end
	bodyDamage:setBoredomLevel(currentBoredom - 3)
	stats:setStress(currentStress - 0.02)
	if bubbleBath > 0 then
		bodyDamage:setUnhappynessLevel(currentUnhappyness - 2)
	end

	if character:getBodyDamage():getBoredomLevel() < 0 then character:getBodyDamage():setBoredomLevel(0); end
	if character:getStats():getStress() < 0 then character:getStats():setStress(0); end
	if character:getBodyDamage():getUnhappynessLevel() < 0 then character:getBodyDamage():setUnhappynessLevel(0); end


	return hasDirtOrBlood

end

function LSUseTub:isValid()
	--local flushed = true
	
	--if self.mainTubObj:getModData().NeedsFlush then
		--flushed = false
	--end
	
	return true
end

function LSUseTub:waitToStart()
	self.action:setUseProgressBar(false)

	self.character:faceThisObject(self.mainTubObj)

	--local wait = false

	--if self.showerType == "Hanging" then
	--	self.character:faceThisObject(self.mainTubObj)
	--	wait = self.character:shouldBeTurning()
	--end
	

	return self.character:shouldBeTurning()
end

function LSUseTub:update()

	if (self.doAnim >= 110) then
		if (self.doAnim >= self.doStatsRate) then
			local hasDirtOrBlood = doPlayerStats(self.character, 0.03, self.isBubbleBath)
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Hygiene"), true, 170, 255, 150)
			
			-------------------------
			--------------PERFORM CONDITIONS (no water / hygiene fulfilled and no dirt/blood)
			if (((self.character:getModData().hygieneNeed <= 25) and (self.isBubbleBath == 0) and (self.doAnim >= 1500)) or ((self.character:getModData().hygieneNeed <= 15) and (self.doAnim >= 2500))) and not hasDirtOrBlood then
				self:forceComplete()
			end
			-------------------------
			--------------EMBARRASSED
			if isClient() and not SandboxVars.LSHygiene.NotEmbarrassed then
				self.wasDisturbedBy = checkPlayerEmbarrassed(self.character)

				if self.wasDisturbedBy then
					HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Embarrassed"), true, 255, 120, 120)
					self:forceComplete()
				end
			end
			-------------------------			
			--------------WETNESS
			if self.character:getBodyDamage():getWetness() < 70 then
				self.character:getBodyDamage():setWetness(70)
			end
			
			--------------DEBUG
			--self.character:Say("doAnim value is: "..self.doAnim)
			---------------
			self.doStatsRate = (self.doAnim+50)
		end
		if (self.doAnim >= self.doAnimInterval) then
			local doActionAnim, doActionAnimInterval
			doActionAnim, doActionAnimInterval, self.actionAnimSound, self.actionAnimSoundTime, self.actionAnimSoundSingle = doAnimRoutine(self.actionAnim)
			self.actionAnimSoundTimeFinal = (self.actionAnimSoundTime+self.doAnim)
		
			self.actionAnim = doActionAnim
			self:setActionAnim(self.actionAnim)
	
			local sound = getAnimSoundVariation("WaterSplash", false, false)
			self.character:getEmitter():playSound(sound)
	
			self.doAnimInterval = (self.doAnim+doActionAnimInterval)
		end
	end

	if (self.doAnim >= 80) and not (self.doAnim >= 90) then
		
		local facingX, facingY = getFacingLocation(self.isFacing)
		self.character:faceLocation(self.character:getX()+facingX, self.character:getY()+facingY)
		self.character:faceLocationF(self.character:getX()+facingX, self.character:getY()+facingY)

		self.character:setX(self.mainTubObj:getSquare():getX()+self.posX)
		self.character:setY(self.mainTubObj:getSquare():getY()+self.posY)
		if isClient() then
			self.character:setLx(self.mainTubObj:getSquare():getX()+self.posX)
			self.character:setLy(self.mainTubObj:getSquare():getY()+self.posY)
		end

		self.doAnim = 90
		self:setActionAnim("Bob_ShowerSadStart")
		self.doSound = getAnimSoundVariation("WaterSplash", self.doSound2, false)
		self.character:getEmitter():playSound(self.doSound)

	elseif (self.doAnim >= 100) and not (self.doAnim >= 110) then
		self.doAnim = 110
		self:setActionAnim("Bob_Tub_Base")
		self.doSound2 = getAnimSoundVariation("WaterSplash", self.doSound, false)
		self.character:getEmitter():playSound(self.doSound2)
		self.doSound = false
		self.character:getModData().IsSittingOnSeat = true
		self.character:getModData().ComfortVal = 0.75
		if self.isBubbleBath > 0 then self.character:getModData().ComfortVal = 0.95 end
	else
		self.doAnim = self.doAnim + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
	end

	-----------------------SOUNDS

	if self.actionAnimSoundTimeFinal and self.actionAnimSound and (self.doAnim >= self.actionAnimSoundTimeFinal) then
	
		if self.doSound then
			self.doSound2 = getAnimSoundVariation(self.actionAnimSound, self.doSound, self.character:isFemale())
			self.character:getEmitter():playSound(self.doSound2)
			self.doSound = false
		else
			self.doSound = getAnimSoundVariation(self.actionAnimSound, self.doSound2, self.character:isFemale())
			self.character:getEmitter():playSound(self.doSound)
			self.doSound2 = false
		end
	
		if self.actionAnimSoundSingle then
			self.actionAnimSoundTimeFinal = false
		else
			self.actionAnimSoundTimeFinal = (self.actionAnimSoundTime+self.doAnim)
		end
	end

	if (self.doAnim >= 43) and (self.doAnim < 50) and not self.doSound then
		self.doSound = getAnimSoundVariation("WaterSplash", false, false)
		self.character:getEmitter():playSound(self.doSound)
	elseif (self.doAnim >= 67) and (self.doAnim < 80) and not self.doSound2 then
		self.doSound2 = getAnimSoundVariation("WaterSplash", self.doSound, false)
		self.character:getEmitter():playSound(self.doSound2)
	end

			--------------HEAT
			if self.showerHeat then
				if not ((SandboxVars.ElecShutModifier > -1 and
				GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
				self.mainTubObj:getSquare():haveElectricity()) then
					--print("no electricity for shower")
					--self.mainTubObj:getCell():removeHeatSource(self.showerHeat)
					self.showerHeat:destroy()
					self.showerHeat = nil
				end
			
			end

	if self.doAnim > self.bubbleIntervalM then
		local BTM, BTB = getBubbleTexture(self.isFacing, self.bTM, self.bTB)

		self.bubbleObj:setSprite(BTM)
		self.bTM = BTM
		self.bubbleIntervalM = (self.doAnim+ZombRand(40)+15)
	end
	if self.doAnim > self.bubbleIntervalB then
		local BTM, BTB = getBubbleTexture(self.isFacing, self.bTM, self.bTB)
		self.bubbleObjClone:setSprite(BTB)
		self.bTB = BTB
		self.bubbleIntervalB = (self.doAnim+ZombRand(40)+15)
	end
	if self.bTM2 and (self.doAnim > self.bubbleIntervalM2) then
		local BTM, BTB = getBubbleTexture(self.isFacing, self.bTM, self.bTB)

		self.bubbleObj2:setSprite(BTM)
		self.bTM2 = BTM
		self.bubbleIntervalM2 = (self.doAnim+ZombRand(40)+15)
	end
	if self.bTB2 and (self.doAnim > self.bubbleIntervalB2) then
		local BTM, BTB = getBubbleTexture(self.isFacing, self.bTM, self.bTB)
		self.bubbleObjClone2:setSprite(BTB)
		self.bTB2 = BTB
		self.bubbleIntervalB2 = (self.doAnim+ZombRand(40)+15)
	end

	self:resetJobDelta()

end

function LSUseTub:start()
	
	self:setOverrideHandModels(nil, nil)

	if self.isBubbleBath > 0 then self.hNL = 85 end
	self.character:getModData().hygieneNeed = self.character:getModData().hygieneNeed or 0	
	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit or 100
	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit - self.hNL
	self.character:getModData().IsDoingShower = true
----------------HEAT

	if not ((SandboxVars.ElecShutModifier > -1 and
	GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
	self.mainTubObj:getSquare():haveElectricity()) then
		--print("no electricity for shower")
	else
		local square = getSquare(self.mainTubObj:getX(), self.mainTubObj:getY(), self.mainTubObj:getZ())
		if square then
			self.showerHeat = HygieneHeatObject:new(self.mainTubObj:getSquare():getX(), self.mainTubObj:getSquare():getY(), self.mainTubObj:getSquare():getZ(), 3, 35)
			--self.showerHeat = HygieneHeatObject:new(self.character:getX(), self.character:getY(), self.character:getZ(), 15, 30)
		end
	end

---------DIRT

	self.overlayDirtSprite, self.overlayDirtSprite2, self.overlayDirtSprite3 = getDirtSprites(self.mainSpriteName)
	self.overlayDirtSpriteSub, self.overlayDirtSpriteSub2, self.overlayDirtSpriteSub3 = getDirtSprites(self.subSpriteName)

-------WATER BUBBLES

	if not self.waterObj then
		local offSetX, offSetY, textureM, textureS, tileX, tileY
		offSetX, offSetY, textureM, textureS, self.posX, self.posY, tileX, tileY = getXYTexture(self.isFacing, self.isBubbleBath)
		self.bTM, self.bTB = getBubbleTexture(self.isFacing, false, false)
		if self.isBubbleBath > 0 then self.bTM2, self.bTB2 = getBubbleTexture(self.isFacing, self.bTM, self.bTB); end
		
		if not offSetX then self:forceComplete(); end

		self.tileSqr = getCell():getGridSquare(self.mainTubObj:getX()+tileX, self.mainTubObj:getY()+tileY, self.mainTubObj:getZ())
		self.tileSqrClone = getCell():getGridSquare(self.subTubObj:getX(), self.subTubObj:getY(), self.subTubObj:getZ())
		--self.waterObj = IsoObject.new(self.tileSqr, "LS_Shower_" .. self.isFacing .. "_0")
		self.waterObj = IsoObject.new(self.tileSqr, textureM)
		self.waterObjClone = IsoObject.new(self.tileSqrClone, textureS)
		self.bubbleObj = IsoObject.new(self.tileSqr, self.bTM)
		self.bubbleObjClone = IsoObject.new(self.tileSqrClone, self.bTB)
		--self.waterObj:setCustomColor(1, 1, 1, 1)
		--self.waterObj:renderlast()
		if offSetX ~= 0 then
			self.waterObj:setOffsetX(offSetX)
			self.bubbleObj:setOffsetX(offSetX)
		end
		if offSetY ~= 0 then
			self.waterObj:setOffsetY(offSetY)
			self.bubbleObj:setOffsetY(offSetY)
		end
		--self.waterObjClone:setOffsetX(offSetX)
		--self.waterObjClone:setOffsetY(offSetY)

		self.waterObj:setAlpha(0.3)
		self.waterObjClone:setAlpha(0.3)
		self.tileSqr:AddTileObject(self.waterObj)	
		self.tileSqrClone:AddTileObject(self.waterObjClone)	
		self.bubbleObj:setAlpha(0.6)
		self.bubbleObjClone:setAlpha(0.6)
		self.tileSqr:AddTileObject(self.bubbleObj)	
		self.tileSqrClone:AddTileObject(self.bubbleObjClone)
		if self.bTM2 then self.bubbleObj2 = IsoObject.new(self.tileSqr, self.bTM2); self.bubbleObjClone2 = IsoObject.new(self.tileSqrClone, self.bTB2); self.bubbleObj2:setOffsetX(offSetX); self.bubbleObj2:setAlpha(0.6);
		self.bubbleObjClone2:setAlpha(0.6); self.tileSqr:AddTileObject(self.bubbleObj2); self.tileSqrClone:AddTileObject(self.bubbleObjClone2); end
		--self.waterObj:transmitModData()
		self.waterObj:transmitCompleteItemToServer()
		self.waterObjClone:transmitCompleteItemToServer()
	end

	self:setActionAnim("Bob_Tub_Enter")
	self.startX = self.character:getX()
	self.startY = self.character:getY()

	local tubEnterX, tubEnterY = getTubEnterXY(self.isFacing)

	self.character:setX(self.mainTubObj:getSquare():getX()+tubEnterX)
	self.character:setY(self.mainTubObj:getSquare():getY()+tubEnterY)
	if isClient() then
		self.character:setLx(self.mainTubObj:getSquare():getX()+tubEnterX)
		self.character:setLy(self.mainTubObj:getSquare():getY()+tubEnterY)
	end

end

function LSUseTub:stop()

	local characterData = self.character:getModData()

	if self.showerHeat then--ADD NEG/POS MOODLES HERE
		--self.mainTubObj:getCell():removeHeatSource(self.showerHeat)
		self.showerHeat:destroy()
	elseif characterData.LSMoodles["BathCold"] and characterData.LSMoodles["BathCold"].Value then
		if characterData.LSMoodles["BathHot"] and characterData.LSMoodles["BathHot"].Value and characterData.LSMoodles["BathHot"].Value > 0 then
			characterData.LSMoodles["BathHot"].Value = 0
		end
			characterData.LSMoodles["BathCold"].Value = 0.2
	end

	if isClient() and self.tileSqr and self.waterObj then
		sledgeDestroy(self.waterObj); sledgeDestroy(self.waterObjClone); sledgeDestroy(self.bubbleObj); sledgeDestroy(self.bubbleObjClone);
		if self.bTB2 then sledgeDestroy(self.bubbleObj2); sledgeDestroy(self.bubbleObjClone2); end
	elseif self.tileSqr and self.waterObj then
		self.tileSqr:transmitRemoveItemFromSquare(self.waterObj); self.tileSqr:RemoveTileObject(self.waterObj)
		self.tileSqrClone:transmitRemoveItemFromSquare(self.waterObjClone); self.tileSqrClone:RemoveTileObject(self.waterObjClone)
		self.tileSqr:transmitRemoveItemFromSquare(self.bubbleObj); self.tileSqr:RemoveTileObject(self.bubbleObj)
		self.tileSqrClone:transmitRemoveItemFromSquare(self.bubbleObjClone); self.tileSqrClone:RemoveTileObject(self.bubbleObjClone)
		if self.bTB2 then self.tileSqr:transmitRemoveItemFromSquare(self.bubbleObj2); self.tileSqr:RemoveTileObject(self.bubbleObj2);
		self.tileSqrClone:transmitRemoveItemFromSquare(self.bubbleObjClone2); self.tileSqrClone:RemoveTileObject(self.bubbleObjClone2); end
	end

	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit + self.hNL
	self.character:getBodyDamage():decreaseBodyWetness(self.character:getBodyDamage():getWetness() / 20)

	self.character:getModData().lastBath = self.character:getHoursSurvived()
	self.character:getModData().IsSittingOnSeat = false
	self.character:getModData().IsDoingShower = false

	self.character:getEmitter():playSound("Tub_End")

	--getSearchMode():setEnabled(self.character:getPlayerNum(), false)

	self.character:resetModelNextFrame();
	sendVisual(self.character);
	triggerEvent("OnClothingUpdated", self.character)

	if SandboxVars.LSHygiene.CleansMakeup then self:removeAllMakeup(); end

	local dice10 = ZombRand(10) + 1
	local addDirt = ZombRand(4) + 1
	
	if self.character:HasTrait("Sloppy") then
		addDirt = 4
	elseif self.character:HasTrait("Tidy") then
		addDirt = 1
	end

	local thisDirtSprite, thisDirtSpriteConnected

	if dice10 > 5 then
		if self.mainTubObj:getModData().Condition then
			self.mainTubObj:getModData().Condition = self.mainTubObj:getModData().Condition + addDirt
			if self.mainTubObj:getModData().Condition > 100 then
				self.mainTubObj:getModData().Condition = 100
			end
		else
			self.mainTubObj:getModData().Condition = addDirt
		end

		self.subTubObj:getModData().Condition = self.mainTubObj:getModData().Condition

		if self.mainTubObj:getModData().ConditionLevel then
			if self.mainTubObj:getModData().ConditionLevel == 0 and self.mainTubObj:getModData().Condition >= 30 then
				self.mainTubObj:getModData().ConditionLevel = 1
				thisDirtSprite = self.overlayDirtSprite
				thisDirtSpriteConnected = self.overlayDirtSpriteSub
			elseif self.mainTubObj:getModData().ConditionLevel == 1 and self.mainTubObj:getModData().Condition >= 60 then
				self.mainTubObj:getModData().ConditionLevel = 2
				thisDirtSprite = self.overlayDirtSprite2
				thisDirtSpriteConnected = self.overlayDirtSpriteSub2
			elseif self.mainTubObj:getModData().ConditionLevel == 2 and self.mainTubObj:getModData().Condition >= 90 then
				self.mainTubObj:getModData().ConditionLevel = 3
				thisDirtSprite = self.overlayDirtSprite3
				thisDirtSpriteConnected = self.overlayDirtSpriteSub3
			end
		else
			self.mainTubObj:getModData().ConditionLevel = 0
		end
		
		self.subTubObj:getModData().ConditionLevel = self.mainTubObj:getModData().ConditionLevel
		
	end

	if isClient() and thisDirtSprite then
		self.mainTubObj:setOverlaySprite(thisDirtSprite, true)
		self.subTubObj:setOverlaySprite(thisDirtSpriteConnected, true)
		self.mainTubObj:transmitUpdatedSpriteToServer()
		self.subTubObj:transmitUpdatedSpriteToServer()
		self.mainTubObj:transmitModData()
		self.subTubObj:transmitModData()
	elseif isClient() then
		self.mainTubObj:transmitModData()
		self.subTubObj:transmitModData()
	elseif thisDirtSprite then
		self.mainTubObj:setOverlaySprite(thisDirtSprite, false)
		self.subTubObj:setOverlaySprite(thisDirtSpriteConnected, false)
	end

	if self.wearClothes then 
		self.character:getEmitter():playSound("ChangeClothes")
		ClothesAboutToChange(self.character, self.mainTubObj, "isBathNoLaundryEnd")
	end

    ISBaseTimedAction.stop(self);	
end

function LSUseTub:perform()

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
		--self.mainTubObj:getCell():removeHeatSource(self.showerHeat)
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
		sledgeDestroy(self.waterObj); sledgeDestroy(self.waterObjClone); sledgeDestroy(self.bubbleObj); sledgeDestroy(self.bubbleObjClone);
		if self.bTB2 then sledgeDestroy(self.bubbleObj2); sledgeDestroy(self.bubbleObjClone2); end
	elseif self.tileSqr and self.waterObj then
		self.tileSqr:transmitRemoveItemFromSquare(self.waterObj); self.tileSqr:RemoveTileObject(self.waterObj)
		self.tileSqrClone:transmitRemoveItemFromSquare(self.waterObjClone); self.tileSqrClone:RemoveTileObject(self.waterObjClone)
		self.tileSqr:transmitRemoveItemFromSquare(self.bubbleObj); self.tileSqr:RemoveTileObject(self.bubbleObj)
		self.tileSqrClone:transmitRemoveItemFromSquare(self.bubbleObjClone); self.tileSqrClone:RemoveTileObject(self.bubbleObjClone)
		if self.bTB2 then self.tileSqr:transmitRemoveItemFromSquare(self.bubbleObj2); self.tileSqr:RemoveTileObject(self.bubbleObj2);
		self.tileSqrClone:transmitRemoveItemFromSquare(self.bubbleObjClone2); self.tileSqrClone:RemoveTileObject(self.bubbleObjClone2); end
	end

	self.character:getModData().hygieneNeedLimit = self.character:getModData().hygieneNeedLimit + self.hNL
	self.character:getBodyDamage():decreaseBodyWetness(self.character:getBodyDamage():getWetness() / 20)

	self.character:getModData().lastBath = self.character:getHoursSurvived()
	self.character:getModData().IsSittingOnSeat = false
	self.character:getModData().IsDoingShower = false

	self.character:getEmitter():playSound("Tub_End")

	self.character:resetModelNextFrame();
	sendVisual(self.character);
	triggerEvent("OnClothingUpdated", self.character)

	if SandboxVars.LSHygiene.CleansMakeup then self:removeAllMakeup(); end

	local dice10 = ZombRand(10) + 1
	local addDirt = ZombRand(4) + 1
	
	if self.character:HasTrait("Sloppy") then
		addDirt = 4
	elseif self.character:HasTrait("Tidy") then
		addDirt = 1
	end

	local thisDirtSprite, thisDirtSpriteConnected

	if dice10 > 5 then
		if self.mainTubObj:getModData().Condition then
			self.mainTubObj:getModData().Condition = self.mainTubObj:getModData().Condition + addDirt
			if self.mainTubObj:getModData().Condition > 100 then
				self.mainTubObj:getModData().Condition = 100
			end
		else
			self.mainTubObj:getModData().Condition = addDirt
		end

		self.subTubObj:getModData().Condition = self.mainTubObj:getModData().Condition

		if self.mainTubObj:getModData().ConditionLevel then
			if self.mainTubObj:getModData().ConditionLevel == 0 and self.mainTubObj:getModData().Condition >= 30 then
				self.mainTubObj:getModData().ConditionLevel = 1
				thisDirtSprite = self.overlayDirtSprite
				thisDirtSpriteConnected = self.overlayDirtSpriteSub
			elseif self.mainTubObj:getModData().ConditionLevel == 1 and self.mainTubObj:getModData().Condition >= 60 then
				self.mainTubObj:getModData().ConditionLevel = 2
				thisDirtSprite = self.overlayDirtSprite2
				thisDirtSpriteConnected = self.overlayDirtSpriteSub2
			elseif self.mainTubObj:getModData().ConditionLevel == 2 and self.mainTubObj:getModData().Condition >= 90 then
				self.mainTubObj:getModData().ConditionLevel = 3
				thisDirtSprite = self.overlayDirtSprite3
				thisDirtSpriteConnected = self.overlayDirtSpriteSub3
			end
		else
			self.mainTubObj:getModData().ConditionLevel = 0
		end
		
		self.subTubObj:getModData().ConditionLevel = self.mainTubObj:getModData().ConditionLevel
		
	end

	if isClient() and thisDirtSprite then
		self.mainTubObj:setOverlaySprite(thisDirtSprite, true)
		self.subTubObj:setOverlaySprite(thisDirtSpriteConnected, true)
		self.mainTubObj:transmitUpdatedSpriteToServer()
		self.subTubObj:transmitUpdatedSpriteToServer()
		self.mainTubObj:transmitModData()
		self.subTubObj:transmitModData()
	elseif isClient() then
		self.mainTubObj:transmitModData()
		self.subTubObj:transmitModData()
	elseif thisDirtSprite then
		self.mainTubObj:setOverlaySprite(thisDirtSprite, false)
		self.subTubObj:setOverlaySprite(thisDirtSpriteConnected, false)
	end


	if self.wasDisturbedBy then
	
		if characterData.LSMoodles["Embarrassed"] and characterData.LSMoodles["Embarrassed"].Value and (characterData.LSMoodles["Embarrassed"].Value > 1) then
			characterData.LSMoodles["Embarrassed"].Value = 1
		end

		local TargetID = self.wasDisturbedBy:getOnlineID()
		
		local TargetX = self.wasDisturbedBy:getX()
		local TargetY = self.wasDisturbedBy:getY()
		
		sendClientCommand(self.character, "LS", "SendGetEmbarrassed", {TargetID})
		BathTubFunctions.DoActionDisturbed(self.character, TargetX, TargetY, self.mainTubObj, self.startX, self.startY, self.wearClothes)
	else
		BathTubFunctions.DoAction(self.character, self.mainTubObj, self.startX, self.startY, self.wearClothes)
	end

	ISBaseTimedAction.perform(self);

end

function LSUseTub:removeAllMakeup()
	local item = self.character:getWornItem("MakeUp_FullFace");
	self:removeMakeup(item);
	item = self.character:getWornItem("MakeUp_Eyes");
	self:removeMakeup(item);
	item = self.character:getWornItem("MakeUp_EyesShadow");
	self:removeMakeup(item);
	item = self.character:getWornItem("MakeUp_Lips");
	self:removeMakeup(item);
end

function LSUseTub:removeMakeup(item)
	if item then
		self.character:removeWornItem(item);
		self.character:getInventory():Remove(item);
	end
end

function LSUseTub:new(character, BathMaster, BathBottom, spriteName, secondSpriteName, facingDir, wasDressed, BubbleBath)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.mainTubObj = BathMaster
	o.subTubObj = BathBottom
	o.mainSpriteName = spriteName
	o.subSpriteName = secondSpriteName
	o.isFacing = facingDir
	o.wearClothes = wasDressed
	o.ignoreHandsWounds = true
    o.stopOnWalk = true
    o.stopOnRun = true
    o.stopOnAim = true
	o.maxTime = 9000
	o.doAnim = 0
	o.doAnimInterval = 150
	o.overlayDirtSprite = false
	o.overlayDirtSprite2 = false
	o.overlayDirtSprite3 = false
	o.overlayDirtSpriteSub = false
	overlayDirtSpriteSub2 = false
	overlayDirtSpriteSub3 = false
	o.wasDisturbedBy = false
	o.waterObj = false
	o.waterObjClone = false
	o.bubbleObj = false
	o.bubbleObjClone = false
	o.spriteNum = 0
	o.spriteNumClone = 0
	o.tileSqr = false
	o.tileSqrClone = false
	o.gameSoundVoice = false
	o.lastSound = false
	o.showerHeat = false
	o.posX = 0
	o.posY = 0
	o.doSound = false
	o.doSound2 = false
	o.bubbleIntervalM = 15
	o.bubbleIntervalB = 30
	o.bubbleIntervalM2 = 45
	o.bubbleIntervalB2 = 60
	o.bTM = false
	o.bTB = false
	o.actionAnim = false
	o.actionAnimSound = false
	o.actionAnimSoundTime = false
	o.actionAnimSoundSingle = false
	o.actionAnimSoundTimeFinal = false
	o.startX = false
	o.startY = false
	o.doStatsRate = 50
	o.isBubbleBath = BubbleBath
	o.hNL = 75
	o.bTM2 = false
	o.bTB2 = false
	o.bubbleObj2 = false
	o.bubbleObjClone2 = false
	return o;
end

return LSUseTub