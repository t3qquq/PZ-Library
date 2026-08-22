
require "TimedActions/ISBaseTimedAction"
require "Hygiene/ToiletFunctions"

LSUseToiletGround = ISBaseTimedAction:derive("LSUseToiletGround");

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
	elseif hygieneNeed > 80 then return 0.5;
	elseif hygieneNeed > 70 then return 1;
	elseif hygieneNeed > 60 then return 1.5; end
	return 2
end

local function adjustDirtVal(character, TP, TPQuality)

	local toiletDirtVal = 0.02
	local hygieneDecrease = hygieneDecreaseBase(character:getModData().hygieneNeed)
	local TPDirtRemove = 1

	local Trait = 1

	--TRAIT
	if character:HasTrait("Sloppy") or character:HasTrait("Clumsy") then
		Trait = 1.5
	elseif character:HasTrait("Tidy") then
		Trait = 0.5
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


	return toiletDirtVal, hygieneDecrease

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
	local addUnhappiness = 0.7
	local addStress = 0.007
	local Trait = 1

	if TP and TPQuality then
		if TPQuality == "bad" then
			addUnhappiness = 0.4
			addStress = 0.004
		elseif TPQuality == "normal" then
			addUnhappiness = 0.2
			addStress = 0.002
		elseif TPQuality == "good" then
			addUnhappiness = 0.05
			addStress = 0.0005
		end
	end


	--TRAIT
	if character:HasTrait("NeatFreak") then
		Trait = 0.5
	elseif character:HasTrait("Sloppy") or character:HasTrait("Outdoorsman") then
		Trait = 2
	end
	
	--DEFINES
	--STRESS 0 - 1
	local stressChange = addStress/Trait
	stats:setStress(currentStress + stressChange)

	--UNHAPPYNESS 0 - 100
	local unhappynessChange = addUnhappiness/Trait
	bodyDamage:setUnhappynessLevel(currentUnhappyness + unhappynessChange)

end

local function doDirtPuddle(thisPlayer)

	local puddleList = {"LS_HScraps_DirtPuddle_0","LS_HScraps_DirtPuddle_1","LS_HScraps_DirtPuddle_2","LS_HScraps_DirtPuddle_3","LS_HScraps_DirtPuddle_4",
	"LS_HScraps_DirtPuddle_5","LS_HScraps_DirtPuddle_6","LS_HScraps_DirtPuddle_7"}
	local dirtSprite = puddleList[ZombRand(#puddleList)+1]
	
		--thisPlayer:Say(tostring(dirtSprite))
		--local x = thisPlayer:getX()
		--local y = thisPlayer:getY()
		--local z = thisPlayer:getZ()
		--sendClientCommand("LS", "DebugAddLitter", {x, y, z, dirtSolid, dirtSprite})
	if isClient() then
		sendClientCommand("LS", "AddDirtPuddle", {thisPlayer:getX(), thisPlayer:getY(), thisPlayer:getZ(), 2, dirtSprite})
	else
		LSAddLitter(thisPlayer:getX(), thisPlayer:getY(), thisPlayer:getZ(), 2, dirtSprite)
	end

end

function LSUseToiletGround:isValid()
	--local flushed = true
	
	--if self.toiletObject:getModData().NeedsFlush then
		--flushed = false
	--end
	
	return true
end

function LSUseToiletGround:waitToStart()
	self.action:setUseProgressBar(false)

	return false
end

function LSUseToiletGround:update()

	if self.character:pressedMovement(true) then
		self:forceStop()
	end

	if self.character:getModData().bathroomNeed > 0 and self.doAnim <= 12 then--60

		if self.doAnim == 0 then
			self:setActionAnim("Bob_Bladder_To_Ground")
		end
		self.doAnim = self.doAnim + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)

	elseif self.character:getModData().bathroomNeed > 0 then
		
		if self.doAnim < 20 then--100
			self:setActionAnim("Bob_Bladder_Ground")
		end
		
		self.doAnim = 20
	
	else
		if self.doAnim >= 32 then--160
			self:forceComplete()
		end
		if self.doAnim == 20 then--100
			self:setActionAnim("Bob_Bladder_Ground_Stand")
			if not self.character:isOutside() then
				doDirtPuddle(self.character)
				self.puddleThreshold = 0
			end
		end
		self.doAnim = self.doAnim + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
	end

	if self.decreaseNeed >= 12 and self.character:getModData().bathroomNeed > 0 then--60
		self.character:getModData().bathroomNeed = self.character:getModData().bathroomNeed - 2.5
		self.character:getModData().hygieneNeed = math.floor((self.character:getModData().hygieneNeed + self.hygieneDecrease)*10)/10
		--self.hygieneTotal = self.hygieneTotal + self.hygieneDecrease
		self.decreaseNeed = 0
		self.puddleThreshold = self.puddleThreshold + 1
		
		local visual = self.character:getHumanVisual()
		
		for i = 1, BloodBodyPartType.MAX:index() do
			local part = BloodBodyPartType.FromIndex(i - 1)
		
			--if part == ""
			--self.character:Say(tonumber(#self.bodyPartList))
			local bodyPartList = require("LSBodyPartList")
			for k,v in pairs(bodyPartList) do
			--for j = 0, 8-1 do
				local partFromList = BloodBodyPartType.FromString(v.name)
				--self.character:Say("print")
				--print("part is " .. tostring(part:getDisplayName()))
				--print("part from list is " .. tostring(partFromList:getDisplayName()))
				if v.category == "low" and part:getDisplayName() == partFromList:getDisplayName() then
				--if part == bodypartList then
					local dirt = visual:getDirt(part)
					if dirt < 1 then
						visual:setDirt(part, dirt+self.DirtVal)
						if visual:getDirt(part) > 1 then visual:setDirt(part, 1); end
						--self.character:Say("Adding dirt.. dirt now is " .. tonumber(visual:getDirt(part)))
					end
				end
			end
		end
		
		--for i = 1, #self.bodyPartList do
		--	local part = self.bodyPartList(i - 1)
		--	self.character:addDirt(part, 1, true)
		--end
		
		--unhappiness
			--self.character:getBodyDamage():setUnhappynessLevel(self.character:getBodyDamage():getUnhappynessLevel() + self.unhappinessValue)
			adjustStats(self.character, self.toiletpaperItem, self.toiletpaperQuality)
		--
		
		HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Bladder"), false, 200, 255, 200)
		HaloTextHelper.addTextWithArrow(self.character, getText("IGUI_HaloNote_Hygiene"), false, 255, 120, 120)
		
		if isClient() and not SandboxVars.LSHygiene.NotEmbarrassed then
			for playerIndex = 0, getNumActivePlayers()-1 do
			local playersList = {};--get players
			local playerObj = getSpecificPlayer(playerIndex)
			local playerIso

			if (playerObj ~= nil) then


				for x = playerObj:getX()-4,playerObj:getX()+4 do
					for y = playerObj:getY()-4,playerObj:getY()+4 do
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

	self:resetJobDelta()
end

function LSUseToiletGround:start()

	self.character:setBlockMovement(true)
	self.character:nullifyAiming()

	self.doAnim = 0

	local sm = getSearchMode():getSearchModeForPlayer(self.character:getPlayerNum())
	sm:getBlur():setTargets(1, 1);
	sm:getDesat():setTargets(0.5, 0.5);
	sm:getRadius():setTargets(4, 4);
	--sm:getRadius():set(2, 20, 2, 20);
	sm:getDarkness():setTargets(0.5, 0.5);
	sm:getGradientWidth():setTargets(1, 1);

	getSearchMode():setEnabled(self.character:getPlayerNum(), true)

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

	self:setOverrideHandModels(nil, nil)

	self.character:getModData().IsDoingToilet = true

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
	self.DirtVal, self.hygieneDecrease = adjustDirtVal(self.character, self.toiletpaperItem, self.toiletpaperQuality)


end

function LSUseToiletGround:stop()

	self.character:setBlockMovement(false)
	getSearchMode():setEnabled(self.character:getPlayerNum(), false)

	self.character:getModData().IsDoingToilet = false

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
	
	if self.character:getBodyDamage():getUnhappynessLevel() > 100 then
		self.character:getBodyDamage():setUnhappynessLevel(100)
	end
	if self.character:getStats():getStress() > 1 then
		self.character:getStats():setStress(1)
	end

	if not self.character:isOutside() and (self.puddleThreshold > 10) then
		doDirtPuddle(self.character)
	end

    ISBaseTimedAction.stop(self);
		
end

function LSUseToiletGround:perform()

	self.character:setBlockMovement(false)

	getSearchMode():setEnabled(self.character:getPlayerNum(), false)

	--self.character:getModData().bathroomNeed = 0
	self.character:getModData().IsDoingToilet = false

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

	if self.wasDisturbedBy then
	
		local characterData = self.character:getModData()
		if characterData.LSMoodles["Embarrassed"] and characterData.LSMoodles["Embarrassed"].Value and (characterData.LSMoodles["Embarrassed"].Value > 1) then
			characterData.LSMoodles["Embarrassed"].Value = 1
		end

		local TargetID = self.wasDisturbedBy:getOnlineID()
		
		local TargetX = self.wasDisturbedBy:getX()
		local TargetY = self.wasDisturbedBy:getY()
		
		sendClientCommand(self.character, "LS", "SendGetEmbarrassed", {TargetID})
		ToiletFunctions.DoActionDisturbed(self.character, TargetX, TargetY, false, false)
	end

	if self.character:getBodyDamage():getUnhappynessLevel() > 100 then
		self.character:getBodyDamage():setUnhappynessLevel(100)
	end
	if self.character:getStats():getStress() > 1 then
		self.character:getStats():setStress(1)
	end

	ISBaseTimedAction.perform(self);

end

function LSUseToiletGround:new(character, TPItem, TPQuality)
	local o = {}
	setmetatable(o, self)
	self.__index = self
	o.character = character
	o.ignoreHandsWounds = true
    o.stopOnWalk = false
    o.stopOnRun = true
	o.stopOnAim = false
	o.maxTime = 3000
	o.doAnim = 0
	o.decreaseNeed = 0
	o.puddleThreshold = 0
	o.hygieneDecrease = 2
	o.DirtVal = 0.02
	o.itemsToRemove = 0
	o.unhappinessValue = 1
	o.stressValue = 0.01
	o.wasDisturbedBy = false
	o.destContainer = false
	o.toiletpaperItem = TPItem
	o.toiletpaperQuality = TPQuality
	return o;
end

return LSUseToiletGround