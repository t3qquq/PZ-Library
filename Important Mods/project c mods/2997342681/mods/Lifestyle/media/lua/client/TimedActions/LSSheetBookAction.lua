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
require "Helper/TransferHelper"

local LSSheetBookAction = ISBaseTimedAction:derive('LSSheetBookAction');

local function checkBookConditionsWrite(character, sheetBookTracksData, songName, instrumentType)

	if #sheetBookTracksData >= (8+character:getPerkLevel(Perks.Music)) then
		return false
	end


	if #sheetBookTracksData > 0 then
		for i, entry in ipairs(sheetBookTracksData) do
			local song = entry[1]
			local instrument = entry[2]
			if instrument == instrumentType then
				if songName == song.name then
					return false
				end
			end
		end
	end


	return true

end

function LSSheetBookAction:isValid()

   return true;
end


function LSSheetBookAction:waitToStart()
	--self.character:faceThisObject(self.instrument)
	--self.character:faceThisObject(self.instrument)
		--return self.character:shouldBeTurning();
		return false
	end

function LSSheetBookAction:update()

	if self.actionType == "isRead" then



	else

		if not self.checkConditions then
			if not checkBookConditionsWrite(self.character, self.sheetBookTracksData, self.songToWrite.name, self.instrumentType) then
				self:forceStop()
			end
			self.checkConditions = true
		end

	end

end

local function getInstrumentParams(instrument)
	local t = {
		trumpet = "TrumpetLearnedTracks",
		guitarA = "GuitarALearnedTracks",
		banjo = "BanjoLearnedTracks",
		keytar = "KeytarLearnedTracks",
		sax = "SaxophoneLearnedTracks",
		guitarEB = "GuitarEBLearnedTracks",
		guitarE = "GuitarELearnedTracks",
		flute = "FluteLearnedTracks",
		piano = "PianoLearnedTracks",
		harmonica = "HarmonicaLearnedTracks",
		violin = "ViolinLearnedTracks",
	}
	return t[instrument]
end

function LSSheetBookAction:start()

	local characterData = self.character:getModData()

	self.action:setUseProgressBar(true)

	local playerlevel = self.character:getPerkLevel(Perks.Music)

	if not self.instrumentType then self.actionType = "isRead"; end
	
	if self.actionType == "isWrite" then
	
		if not self.writingSound then
			if self.pencilOrPen:getFullType() == "Base.Pencil" then
				self.writingSound = self.character:getEmitter():playSound("WriteSongPencil")
			else
				self.writingSound = self.character:getEmitter():playSound("WriteSongPen")
			end
		end

	end

	self.sheetBookTracksData = self.sheetBook:getModData().InscribedSongs or {}

	if self.actionType == "isRead" then
--	if self.character:isItemInBothHands(self.instrument) then
--        self.handItem = 'BothHands';
--    else
--        if self.character:isPrimaryHandItem(self.instrument) then
--            self.handItem = 'PrimaryHand';
--        elseif self.character:isSecondaryHandItem(self.instrument) then
--            self.handItem = 'SecundaryHand';
--        end
--    end
	
--	self:setOverrideHandModels(self.instrument, nil)

		self:setAnimVariable("ReadType", "book")

		self:setActionAnim(CharacterActionAnims.Read);
		self:setOverrideHandModels(nil, self.sheetBook);
		self.character:setReading(true)
    
		self.character:reportEvent("EventRead");

	else

		self:setOverrideHandModels(self.pencilOrPen, self.sheetBook);
		self:setActionAnim("Bob_WriteBook")

	end

		self.character:playSound("OpenBook")

end

function LSSheetBookAction:stop()

	local characterData = self.character:getModData()

	if self.writingSound and self.character:getEmitter():isPlaying(self.writingSound) then
		self.character:getEmitter():stopSound(self.writingSound);
	end

	if self.actionType == "isRead" then
		self.character:setReading(false)
		self.character:playSound("CloseBook")
	end

	ISBaseTimedAction.stop(self);
end

function LSSheetBookAction:perform()

	if self.writingSound and self.character:getEmitter():isPlaying(self.writingSound) then
		self.character:getEmitter():stopSound(self.writingSound);
	end

	local characterData = self.character:getModData()
	local playerlevel = self.character:getPerkLevel(Perks.Music)
	local newLearnedSongs = 0
	local firstSong

	if self.actionType == "isRead" then

		self.character:setReading(false)

		for i, entry in ipairs(self.sheetBookTracksData) do
			local v = entry[1]
			local instrument = entry[2]

			if v.level <= playerlevel then
				local dataName = getInstrumentParams(instrument)
				if dataName then
					local canLearn = true
					if #self.character:getModData()[dataName] > 0 then
						for j,k in pairs(self.character:getModData()[dataName]) do
							if k.isaddon ~= 2 and k.name == v.name then canLearn = false; break; end
						end
					end
					if canLearn then
						table.insert(self.character:getModData()[dataName],v)
						newLearnedSongs = newLearnedSongs + 1
						if not firstSong then firstSong = v.name; end
					end
				end
			end
		end

		if newLearnedSongs > 0 then
			HaloTextHelper.addText(self.character, getText("IGUI_HaloNote_LearnSong"), 210, 210, 210)
			HaloTextHelper.addText(self.character, getText(firstSong), 150, 255, 150)
			if newLearnedSongs > 1 then
				local moreSongs = newLearnedSongs-1
				local tooltipText = getText("IGUI_HaloNote_ReadSongPlusStart") .. moreSongs .. getText("IGUI_HaloNote_ReadSongPlusEnd")
				HaloTextHelper.addText(self.character, tooltipText, 30, 190, 240)
			end
			getSoundManager():playUISound("PZLevelSound")
		end

	elseif self.actionType == "isWrite" then

		table.insert(self.sheetBookTracksData, {self.songToWrite,self.instrumentType});
		HaloTextHelper.addText(self.character, getText("IGUI_HaloNote_WriteSongSuccess"), 210, 210, 210)
		HaloTextHelper.addText(self.character, getText(self.songToWrite.name), 150, 255, 150)

		local soundrandomiser = ZombRand(1, 100)
		local actionSound = "WriteSongSting01"
		if soundrandomiser >=66 then
			actionSound = "WriteSongSting02"
		elseif soundrandomiser >=33 then
			actionSound = "WriteSongSting03"
		end
			getSoundManager():playUISound(actionSound)

	end

	if self.containerBook then
		if not self.containerBook:isItemAllowed(self.sheetBook) then
			-- 
		elseif self.containerBook:getType() == "floor" then
			TransferHelper.dropItem(self.sheetBook, self.character)
		else
			TransferHelper.onMoveItemsTo(self.sheetBook, self.containerBook, self.character, true)
		end
	end

	if self.containerPen then
		if not self.containerPen:isItemAllowed(self.pencilOrPen) then
			-- 
		elseif self.containerPen:getType() == "floor" then
			TransferHelper.dropItem(self.pencilOrPen, self.character)
		else
			TransferHelper.onMoveItemsTo(self.pencilOrPen, self.containerPen, self.character, true)
		end
	end

    ISBaseTimedAction.perform(self);
end

function LSSheetBookAction:animEvent(event, parameter)
    if event == "PageFlip" then
        if getGameSpeed() ~= 1 then
            return
        end
        if self.actionType == "isRead" then
            self.character:playSound("PageFlipBook")
        end
    end
end

function LSSheetBookAction:new(character, Item, Time, Song, Type, Write, Cont1, Cont2)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
	o.sheetBook = Item;
	o.songToWrite = Song
    o.stopOnWalk = false;
    o.stopOnRun = true;
    o.stopOnAim = true;
	o.ignoreHandsWounds = true;
	o.maxTime = Time
	o.instrumentType = Type
	o.learnedTracksData = false
	o.sheetBookTracksData = false
	o.actionType = "isWrite"
	o.gameSound = false
	o.handItem = false
	o.pencilOrPen = Write
	o.containerBook = Cont1
	o.containerPen = Cont2
	o.writingSound = false
	o.checkConditions = false

    return o;
end

return LSSheetBookAction;