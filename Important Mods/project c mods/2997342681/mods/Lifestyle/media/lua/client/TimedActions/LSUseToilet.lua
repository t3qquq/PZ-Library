
require "TimedActions/ISBaseTimedAction"
require "Hygiene/ToiletFunctions"

LSUseToilet = ISBaseTimedAction:derive("LSUseToilet");

--local isPlayingJukeSong = nil;


local function TransferTP(character, TP, destContainer)


	if destContainer and TP then

		if destContainer then
			local capacity = destContainer:hasRoomFor(character, TP)
			local allowed = destContainer:isItemAllowed(TP)

			if capacity and allowed then
				destContainer:addItemOnServer(TP);

			
				character:getInventory():setDrawDirty(true);
				character:getInventory():DoRemoveItem(TP);
				destContainer:setDrawDirty(true);
				destContainer:AddItem(TP)

				getPlayerInventory(character:getPlayerNum()):refreshBackpacks()
			end
		end
	end


end

local function hygieneDecreaseBase(hygieneNeed)
	if hygieneNeed > 90 then return 0.2;
	elseif hygieneNeed > 80 then return 0.4;
	elseif hygieneNeed > 70 then return 0.6;
	elseif hygieneNeed > 60 then return 0.8; end
	return 1
end

local function adjustDirtVal(character, TP, TPQuality, Type, ToiletCondition)

	local doHaloBad = 1
	local toiletDirtVal = 0.005
	local hygieneDecrease = hygieneDecreaseBase(character:getModData().hygieneNeed)
	local TPDirtRemove = 1

	local Trait = 1

	--TRAIT
	if character:HasTrait("Sloppy") or character:HasTrait("Clumsy") then
		Trait = 1.5
	elseif character:HasTrait("Tidy") then
		Trait = 0.5
	end
	
	if Type == "Hanging" then--fancy and low use default value 0.005 - ground is 0.02
		toiletDirtVal = 0.01
		--hygieneDecrease = 0.8
	elseif Type == "Chemical" then
		toiletDirtVal = 0.012
		--hygieneDecrease = 1
	elseif Type == "Wooden" then
		toiletDirtVal = 0.015
		--hygieneDecrease = 1.5
	end

	if ToiletCondition then
		if ToiletCondition == 1 then
			hygieneDecrease = hygieneDecrease * 1.5
			toiletDirtVal = toiletDirtVal * 1.5
		elseif ToiletCondition == 2 then
			hygieneDecrease = hygieneDecrease * 2.5
			toiletDirtVal = toiletDirtVal * 2.5
		elseif ToiletCondition == 3 then
			hygieneDecrease = hygieneDecrease * 3.5
			toiletDirtVal = toiletDirtVal * 3.5
		end
	end

	if TP and TPQuality then
		if TPQuality == "bad" then
			TPDirtRemove = 0.7
		elseif TPQuality == "normal" then
			TPDirtRemove = 0.3
		elseif TPQuality == "good" then
			TPDirtRemove = 0.1
		end
	end

	hygieneDecrease = (hygieneDecrease*Trait)*TPDirtRemove*SandboxVars.LSHygiene.HygieneNeedMultiplier
	toiletDirtVal = (toiletDirtVal*Trait)*TPDirtRemove

	if math.floor(hygieneDecrease) >= 2.5 then
		doHaloBad = 4
	elseif math.floor(hygieneDecrease) >= 1.5 then
		doHaloBad = 3
	elseif math.floor(hygieneDecrease) >= 1 then
		doHaloBad = 2
	end

	return toiletDirtVal, hygieneDecrease, doHaloBad

end

local function adjustStats(character, TP, TPQuality)

	local bodyDamage = character:getBodyDamage()
	local stats = character:getStats()
	local currentBoredom = bodyDamage:getBoredomLevel()
	local currentUnhappyness = bodyDamage:getUnhappynessLevel()
	local currentStress = stats:getStress();
	if character:HasTrait("Smoker") then
		stats:setStressFromCigarettes(0)
	end

	--VARIABLES
	local addUnhappiness = 0.4
	local addStress = 0.004
	local Trait = 1

	if TP and TPQuality then
		if TPQuality == "bad" then
			addUnhappiness = 0.2
			addStress = 0.002
		elseif TPQuality == "normal" then
			addUnhappiness = 0.1
			addStress = 0.001
		elseif TPQuality == "good" then
			addUnhappiness = 0
			addStress = 0
		end
	end


	--TRAIT
	if character:HasTrait("Sloppy") then
		Trait = 2
	elseif character:HasTrait("NeatFreak") then
		Trait = 0.5
	elseif character:HasTrait("Outdoorsman") then
		Trait = 1.2
	end
	
	--DEFINES
	--STRESS 0 - 1
	local stressChange = addStress/Trait
	stats:setStress(currentStress + stressChange)

	--UNHAPPYNESS 0 - 100
	local unhappynessChange = addUnhappiness/Trait
	bodyDamage:setUnhappynessLevel(currentUnhappyness + unhappynessChange)

end


function LSUseToilet:isValid()
	--local flushed = true
	
	--if self.toiletObject:getModData().NeedsFlush then
		--flushed = false
	--end
	
	return true
end

function LSUseToilet:waitToStart()
	self.action:setUseProgressBar(false)
	if isClient() then
		local cX = self.character:getX()
		local cY = self.character:getY()
		self.character:setLy(cY)
		self.character:setLx(cX)
	end

	local wait = false

	if self.toiletType == "Hanging" then
		self.character:faceThisObject(self.toiletObject)
		wait = self.character:shouldBeTurning()
	end
	

	return wait
end

function LSUseToilet:update()
	if self.toiletType == "Hanging" then
		self.character:faceThisObject(self.toiletObject)
	end

	if self.character:getModData().IsSittingOnSeat and self.doAnim >= 80 and not self.freeTime then--360

			local animType = self.animList
			
			if self.character:getModData().bathroomNeed > 60 then
				animType = self.animPainfulList
			end
			
			local idxSitAnim = ZombRand(#animType) + 1
			local sitAnim = animType[idxSitAnim]
			--self.character:Say(tostring(sitAnim))
			self:setActionAnim(sitAnim)
			
			self.doAnim = 0
	--elseif self.doAnim >= 1 then
	
	----debug
		--self.character:Say(tostring(sitAnim))
	----
	
	else
		self.doAnim = self.doAnim + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
	end

	if self.decreaseNeed >= self.decreaseNeedTotal and self.character:getModData().bathroomNeed > 0 then
		self.character:getModData().bathroomNeed = self.character:getModData().bathroomNeed - 2.5
		--self.hygieneTotal = self.hygieneTotal + self.hygieneDecrease
		self.character:getModData().hygieneNeed = math.floor((self.character:getModData().hygieneNeed + self.hygieneDecrease)*10)/10
		
		self.decreaseNeed = 0
		
		if (not self.toiletpaperItem) or
		(self.toiletpaperQuality ~= "good") then
		
			local visual = self.character:getHumanVisual()
		
			for i = 1, BloodBodyPartType.MAX:index() do
				local part = BloodBodyPartType.FromIndex(i - 1)
				local bodyPartList = require("LSBodyPartList")
				for k,v in pairs(bodyPartList) do
					local partFromList = BloodBodyPartType.FromString(v.name)
					if v.category == "low" and part:getDisplayName() == partFromList:getDisplayName() then
						local dirt = visual:getDirt(part)
						if dirt < 1 then
							visual:setDirt(part, dirt+self.toiletDirtVal)
							if visual:getDirt(part) > 1 then visual:setDirt(part, 1); end
							--self.character:Say("Adding dirt.. dirt now is " .. tonumber(visual:getDirt(part)))
						end
					end
				end
			end
		
		end
		
		adjustStats(self.character, self.toiletpaperItem, self.toiletpaperQuality)
		
		HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Bladder"), false, 170, 255, 150)
		if self.doHaloBad == 1 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Hygiene"), false, 255, 210, 210)
		elseif self.doHaloBad == 2 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Hygiene"), false, 255, 175, 175)
		elseif self.doHaloBad == 3 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Hygiene"), false, 255, 150, 150)
		elseif self.doHaloBad == 4 then
			HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Hygiene"), false, 255, 120, 120)
		end
		if isClient() and not SandboxVars.LSHygiene.NotEmbarrassed then
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
							if playerObj:CanSee(v) and playerIso:checkCanSeeClient(v) then
								local characterData = self.character:getModData()
								if characterData.LSMoodles["Embarrassed"] and characterData.LSMoodles["Embarrassed"].Value then
									characterData.LSMoodles["Embarrassed"].Value = characterData.LSMoodles["Embarrassed"].Value + 0.45
								end
								HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Embarrassed"), true, 255, 120, 120)
						
								self.wasDisturbedBy = v
								self:forceComplete()
								break
							end
						end
					end	
				end
			end
		end
	end
	
	end
	
	self.decreaseNeed = self.decreaseNeed + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)

	if self.character:getModData().bathroomNeed <= 0 then
		self.character:getModData().bathroomNeed = 0
		self:forceComplete()
	end
	
	self:resetJobDelta()
end

function LSUseToilet:start()

	--if self.toiletObject:getModData().NeedsFlush then
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

	local inventory = self.character:getInventory()	
	local it = inventory:getItems()
	self.itemsToRemove = {}


	for j = 0, it:size()-1 do
		local item = it:get(j);
		if item:getClothingItem() and self.character:isEquippedClothing(item) and (item:getBodyLocation() == "Bottoms" or item:getBodyLocation() == "Underwear" or
		item:getBodyLocation() == "Skirt" or item:getBodyLocation() == "Legs1" or item:getBodyLocation() == "Pants" or item:getBodyLocation() == "UnderwearBottom" or
		item:getBodyLocation() == "Torso1Legs1" or item:getBodyLocation() == "BathRobe" or item:getBodyLocation() == "FullSuit" or item:getBodyLocation() == "Tail" or
		item:getBodyLocation() == "FullSuitHead" or item:getBodyLocation() == "Boilersuit" or item:getBodyLocation() == "Dress") then
			table.insert(self.itemsToRemove, item)
		end
		---debug
		--if item:getClothingItem() then self.character:Say("Item is Clothing"); end
		--if self.character:isEquippedClothing(item) then self.character:Say("Item is Equipped"); end
		----
	end

	if #self.itemsToRemove > 0 then
		for _, item in ipairs(self.itemsToRemove) do
			self.character:getInventory():setDrawDirty(true);
			self.character:removeWornItem(item, false)
			triggerEvent("OnClothingUpdated", self.character)
		end
		
		local soundrandomiser = ZombRand(1, 100)
		local sound = "Zipper_OPEN1"

			if soundrandomiser >=75 then
				sound = "Zipper_OPEN1"
			elseif soundrandomiser >=50 then
				sound = "Zipper_OPEN2"
			elseif soundrandomiser >=25 then
				sound = "Zipper_OPEN3"
			else
				sound = "Zipper_OPEN4"
			end

		self.character:getEmitter():playSound(sound)
	end

	getPlayerInventory(self.character:getPlayerNum()):refreshBackpacks()

	self.character:getModData().IsDoingToilet = true

	if self.freeTime then
		self.decreaseNeedTotal = 28--140
		if (self.freeTime:getType() == "Newspaper") then
			self:setAnimVariable("ReadType", "newspaper")
			self.character:playSound("OpenMagazine")
		else
			self:setAnimVariable("ReadType", "book")
			self.character:playSound("OpenBook")
		end

		self:setActionAnim(CharacterActionAnims.Read);
		self:setOverrideHandModels(nil, self.freeTime);
		self.character:setReading(true)
    
		self.character:reportEvent("EventRead");
	else
		self:setOverrideHandModels(nil, nil)
	end

-----TRANSFER TP
	if self.toiletpaperItem then
		if self.toiletpaperItem:getContainer() and not self.character:getInventory():contains(self.toiletpaperItem) then
			
			self.destContainer = self.toiletpaperItem:getContainer()
			
			self.toiletpaperItem:getContainer():setDrawDirty(true)
			self.destContainer:setHasBeenLooted(true);
			self.destContainer:removeItemOnServer(self.toiletpaperItem)
			self.destContainer:DoRemoveItem(self.toiletpaperItem)
			self.character:getInventory():AddItem(self.toiletpaperItem)

			getPlayerInventory(self.character:getPlayerNum()):refreshBackpacks();
		end
	end
------HYGIENE STUFF
	self.toiletDirtVal, self.hygieneDecrease, self.doHaloBad = adjustDirtVal(self.character, self.toiletpaperItem, self.toiletpaperQuality, self.toiletType, self.toiletObject:getModData().ConditionLevel)

end

function LSUseToilet:stop()

	--getSearchMode():setEnabled(self.character:getPlayerNum(), false)

	if self.toiletpaperItem then

		if self.toiletpaperItem:getFullType() == "Base.ToiletPaper" then
			self.toiletpaperItem:setUseDelta(0.1)
			self.toiletpaperItem:Use()		
		elseif self.toiletpaperItem:getFullType() == "Base.Tissue" then
			self.toiletpaperItem:setUseDelta(0.3)
			self.toiletpaperItem:Use()		
		else
			self.character:getInventory():setDrawDirty(true);

			if (self.toiletpaperItem:getFullType() == "Base.RippedSheets" or self.toiletpaperItem:getFullType() == "Base.AlcoholRippedSheets") then
				self.character:getInventory():AddItem("Base.RippedSheetsDirty")
			end
				
			self.character:getInventory():DoRemoveItem(self.toiletpaperItem);
			self.toiletpaperItem = false
			getPlayerInventory(self.character:getPlayerNum()):refreshBackpacks()	
		end
	end

	if self.freeTime then
		self.character:setReading(false)
	end

	self.character:getModData().IsDoingToilet = false

	local dice30 = ZombRand(30) + 1
	local soundrandomiser = ZombRand(1, 100)
	local sound = "Zipper_CLOSE1"

	if dice30 >= 25 and self.toiletObject:getModData().NeedsFlush and not self.toiletObject:getModData().IsClogged then
		if soundrandomiser >= 50 then
			sound = "Toilet_Clogged_1"
		else
			sound = "Toilet_Clogged_2"
		end
		
		self.character:getEmitter():playSound(sound)
		
		self.toiletObject:getModData().IsClogged = 0
		
	elseif dice30 == 30 and not self.toiletObject:getModData().IsClogged then
		if soundrandomiser >= 50 then
			sound = "Toilet_Clogged_1"
		else
			sound = "Toilet_Clogged_2"
		end
		
		self.character:getEmitter():playSound(sound)
		
		self.toiletObject:getModData().IsClogged = 0
		
	end


	self.toiletObject:getModData().NeedsFlush = true

	local dice10 = ZombRand(10) + 1
	local addDirt = ZombRand(4) + 1
	
	if self.character:HasTrait("Sloppy") then
		addDirt = 4
	elseif self.character:HasTrait("Tidy") then
		addDirt = 1
	end

	if self.toiletObject:getModData().IsClogged and self.toiletObject:getModData().IsClogged >= 0 then
		addDirt = 40
		dice10 = 10
	end

	local thisDirtSprite

	if dice10 > 5 then--REENABLE THIS
		if self.toiletObject:getModData().Condition then
			self.toiletObject:getModData().Condition = self.toiletObject:getModData().Condition + addDirt
			if self.toiletObject:getModData().Condition > 100 then
				self.toiletObject:getModData().Condition = 100
			end
		else
			self.toiletObject:getModData().Condition = addDirt
		end

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
	end
	
	if self.toiletObject:getModData().ConditionLevel == 1 then
		thisDirtSprite = self.overlayDirtSprite
	elseif self.toiletObject:getModData().ConditionLevel == 2 then
		thisDirtSprite = self.overlayDirtSprite2
	elseif self.toiletObject:getModData().ConditionLevel == 3 then
		thisDirtSprite = self.overlayDirtSprite3
	end
	
	----debug
		--self.character:Say("toilet condition is " .. tonumber(self.toiletObject:getModData().Condition) .. " and level is " .. tonumber(self.toiletObject:getModData().ConditionLevel))
		--if thisDirtSprite then
		--	self.character:Say("sprite is " .. tostring(thisDirtSprite))
		--end
	----

	if isClient() and thisDirtSprite then
		self.toiletObject:setOverlaySprite(thisDirtSprite, true)
		self.toiletObject:transmitUpdatedSpriteToServer()
		self.toiletObject:transmitModData()
	elseif isClient() then
		self.toiletObject:transmitModData()
	elseif thisDirtSprite then
		self.toiletObject:setOverlaySprite(thisDirtSprite, false)
	end

	if #self.itemsToRemove > 0 then
		local inventory = self.character:getInventory()	
		local it = inventory:getItems();
		for j = 0, it:size()-1 do
			local itemToBeWorn = it:get(j);
			for _, item in ipairs(self.itemsToRemove) do
				if (item == itemToBeWorn or item == itemToBeWorn:getClothingItem() or item == itemToBeWorn:getFullType()) and
					self.character:getInventory():contains(itemToBeWorn) and not self.character:isEquippedClothing(itemToBeWorn) then

					if (itemToBeWorn:getBodyLocation() ~= "" or (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "")) then
						if itemToBeWorn:getContainer() then
							itemToBeWorn:getContainer():setDrawDirty(true)
						end
						self.character:getInventory():AddItem(itemToBeWorn)
						if (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "") then
							self.character:setWornItem(itemToBeWorn:canBeEquipped(), itemToBeWorn);
							getPlayerInventory(self.character:getPlayerNum()):refreshBackpacks();
						else
						self.character:setWornItem(itemToBeWorn:getBodyLocation(), itemToBeWorn);
						end
						triggerEvent("OnClothingUpdated", self.character)
					end
				end
			end
		end


			if soundrandomiser >=66 then
				sound = "Zipper_CLOSE1"
			elseif soundrandomiser >=33 then
				sound = "Zipper_CLOSE2"
			else
				sound = "Zipper_CLOSE3"
			end

		self.character:getEmitter():playSound(sound)
	end

	--local addHygiene = math.floor(tonumber(self.hygieneTotal))
	--if addHygiene > 100 then
	--	addHygiene = 100
	--end
	if self.character:getModData().hygieneNeed and (self.character:getModData().hygieneNeed > 100) then
		self.character:getModData().hygieneNeed = 100
	end

	if self.character:getBodyDamage():getUnhappynessLevel() > 100 then
		self.character:getBodyDamage():setUnhappynessLevel(100)
	end
	if self.character:getStats():getStress() > 1 then
		self.character:getStats():setStress(1)
	end

    ISBaseTimedAction.stop(self);
		
end

function LSUseToilet:perform()

	--getSearchMode():setEnabled(self.character:getPlayerNum(), false)

	--self.character:getModData().bathroomNeed = 0

	if self.toiletpaperItem then

		if self.toiletpaperItem:getFullType() == "Base.ToiletPaper" then
			self.toiletpaperItem:setUseDelta(0.1)
			self.toiletpaperItem:Use()		
		elseif self.toiletpaperItem:getFullType() == "Base.Tissue" then
			self.toiletpaperItem:setUseDelta(0.3)
			self.toiletpaperItem:Use()		
		else
			self.character:getInventory():setDrawDirty(true);

			if (self.toiletpaperItem:getFullType() == "Base.RippedSheets" or self.toiletpaperItem:getFullType() == "Base.AlcoholRippedSheets") then
				self.character:getInventory():AddItem("Base.RippedSheetsDirty")
			end
				
			self.character:getInventory():DoRemoveItem(self.toiletpaperItem);
			self.toiletpaperItem = false
			getPlayerInventory(self.character:getPlayerNum()):refreshBackpacks()	
		end
	end

	if self.toiletpaperItem and self.destContainer and self.character:getInventory():contains(self.toiletpaperItem) then
		TransferTP(self.character, self.toiletpaperItem, self.destContainer)
	end

	if self.freeTime then
		self.character:setReading(false)
	end
	
	self.character:getModData().IsDoingToilet = false
	
	local dice30 = ZombRand(30) + 1
	local soundrandomiser = ZombRand(1, 100)
	local sound = "Zipper_CLOSE1"

	if dice30 >= 25 and self.toiletObject:getModData().NeedsFlush and not self.toiletObject:getModData().IsClogged then
		if soundrandomiser >= 50 then
			sound = "Toilet_Clogged_1"
		else
			sound = "Toilet_Clogged_2"
		end
		
		self.character:getEmitter():playSound(sound)
		
		self.toiletObject:getModData().IsClogged = true
		self.toiletObject:getModData().IsClogged = 0
		
	elseif dice30 == 30 and not self.toiletObject:getModData().IsClogged then
		if soundrandomiser >= 50 then
			sound = "Toilet_Clogged_1"
		else
			sound = "Toilet_Clogged_2"
		end
		
		self.character:getEmitter():playSound(sound)
		
		self.toiletObject:getModData().IsClogged = true
		self.toiletObject:getModData().IsClogged = 0
		
	end

	self.toiletObject:getModData().NeedsFlush = true

	local dice10 = ZombRand(10) + 1
	local addDirt = ZombRand(4) + 1
	
	if self.character:HasTrait("Sloppy") then
		addDirt = 4
	elseif self.character:HasTrait("Tidy") then
		addDirt = 1
	end

	if self.toiletObject:getModData().IsClogged and self.toiletObject:getModData().IsClogged >= 0 then
		addDirt = 20
		dice10 = 10
	end

	local thisDirtSprite

	if dice10 > 5 then
		if self.toiletObject:getModData().Condition then
			self.toiletObject:getModData().Condition = self.toiletObject:getModData().Condition + addDirt
			if self.toiletObject:getModData().Condition > 100 then
				self.toiletObject:getModData().Condition = 100
			end
		else
			self.toiletObject:getModData().Condition = addDirt
		end

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
	end

	if self.toiletObject:getModData().ConditionLevel == 1 then
		thisDirtSprite = self.overlayDirtSprite
	elseif self.toiletObject:getModData().ConditionLevel == 2 then
		thisDirtSprite = self.overlayDirtSprite2
	elseif self.toiletObject:getModData().ConditionLevel == 3 then
		thisDirtSprite = self.overlayDirtSprite3
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

	if #self.itemsToRemove > 0 then
		local inventory = self.character:getInventory()	
		local it = inventory:getItems();
		for j = 0, it:size()-1 do
			local itemToBeWorn = it:get(j);
			for _, item in ipairs(self.itemsToRemove) do
				if (item == itemToBeWorn or item == itemToBeWorn:getClothingItem() or item == itemToBeWorn:getFullType()) and
					self.character:getInventory():contains(itemToBeWorn) and not self.character:isEquippedClothing(itemToBeWorn) then

					if (itemToBeWorn:getBodyLocation() ~= "" or (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "")) then
						if itemToBeWorn:getContainer() then
							itemToBeWorn:getContainer():setDrawDirty(true)
						end
						self.character:getInventory():AddItem(itemToBeWorn)
						if (instanceof(itemToBeWorn, "InventoryContainer") and itemToBeWorn:canBeEquipped() ~= "") then
							self.character:setWornItem(itemToBeWorn:canBeEquipped(), itemToBeWorn);
							getPlayerInventory(self.character:getPlayerNum()):refreshBackpacks();
						else
						self.character:setWornItem(itemToBeWorn:getBodyLocation(), itemToBeWorn);
						end
						triggerEvent("OnClothingUpdated", self.character)
					end
				end
			end
		end

			if soundrandomiser >=66 then
				sound = "Zipper_CLOSE1"
			elseif soundrandomiser >=33 then
				sound = "Zipper_CLOSE2"
			else
				sound = "Zipper_CLOSE3"
			end

		self.character:getEmitter():playSound(sound)
	end

	if self.character:getModData().hygieneNeed and (self.character:getModData().hygieneNeed > 100) then
		self.character:getModData().hygieneNeed = 100
	end

	--self.character:setSitOnGround(false)

	if self.wasDisturbedBy then
	
		local characterData = self.character:getModData()
		if characterData.LSMoodles["Embarrassed"] and characterData.LSMoodles["Embarrassed"].Value and (characterData.LSMoodles["Embarrassed"].Value > 1) then
			characterData.LSMoodles["Embarrassed"].Value = 1
		end

		local TargetID = self.wasDisturbedBy:getOnlineID()
		
		local TargetX = self.wasDisturbedBy:getX()
		local TargetY = self.wasDisturbedBy:getY()
		
		sendClientCommand(self.character, "LS", "SendGetEmbarrassed", {TargetID})
		ToiletFunctions.DoActionDisturbed(self.character, TargetX, TargetY, self.toiletObject, self.newToilet)
	elseif (self.toiletObject:hasWater() and self.toiletObject:getWaterAmount() >= self.waterUsage) then
		ToiletFunctions.DoActionFlush(self.character, self.toiletObject, self.toiletType, self.waterUsage, self.newToilet)
	--else
	--	ToiletFunctions.onAction(self.character, self.toiletObject, self.toiletType, false, false, false, false, "IsMove", false, self.newToilet)
	end

	if self.character:getBodyDamage():getUnhappynessLevel() > 100 then
		self.character:getBodyDamage():setUnhappynessLevel(100)
	end
	if self.character:getStats():getStress() > 1 then
		self.character:getStats():setStress(1)
	end

	ISBaseTimedAction.perform(self);

end

function LSUseToilet:animEvent(event, parameter)
    if event == "PageFlip" then
        if getGameSpeed() ~= 1 then
            return
        end
        if (self.freeTime:getType() == "Book") then
            self.character:playSound("PageFlipBook")
        else
            self.character:playSound("PageFlipMagazine")
        end
    end
end

function LSUseToilet:new(character, Toilet, Type, WaterUsage, Flush, TPQuality, Relax, dirtSprite, dirtSprite2, dirtSprite3, newToiletObject, PlungerOrTPItem)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.toiletObject = Toilet
	o.toiletType = Type
	o.waterUsage = WaterUsage
	o.soundFlush = Flush
	o.ignoreHandsWounds = true
    o.stopOnWalk = false
    o.stopOnRun = true
	o.maxTime = 3000
	o.doAnim = 2
	o.decreaseNeed = 0
	o.decreaseNeedTotal = 14--70
	o.itemsToRemove = 0
	o.overlayDirtSprite = dirtSprite
	o.overlayDirtSprite2 = dirtSprite2
	o.overlayDirtSprite3 = dirtSprite3
	o.toiletDirtVal = 0.005
	o.hygieneDecrease = 0.5
	o.doHaloBad = 1
	o.newToilet = newToiletObject
	o.wasDisturbedBy = false
	o.freeTime = Relax
	o.toiletpaperItem = PlungerOrTPItem
	o.toiletpaperQuality = TPQuality
	o.destContainer = false
	o.animList = {"Bob_IsSittingLoopLeanForward_Pensive","Bob_IsSittingLoopHandsOnThigh","Bob_IsSittingLoopHandsOnThigh_SlightLeanForward","Bob_IsSittingLoopHandsOnThigh_SlightLeanForward_CleanFace"}
	o.animPainfulList = {"Bob_IsSittingLoopHandsOnFace_LeanForward","Bob_IsSittingLoopHandsOnThigh_SlightLeanForward_CleanTear","Bob_IsSittingLoopHandsOnThigh_SlightLeanForward","Bob_IsSittingLoopHandsOnThigh_SlightLeanForward_CleanFace"}
	return o;
end

return LSUseToilet