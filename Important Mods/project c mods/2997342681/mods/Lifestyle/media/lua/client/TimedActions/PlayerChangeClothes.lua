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

PlayerChangeClothes = ISBaseTimedAction:derive("PlayerChangeClothes")

function PlayerChangeClothes:isValid()
	return true
end


function PlayerChangeClothes:waitToStart()
	if self.optiontype ~= "isBathNoLaundryEnd" then
		self.character:faceThisObject(self.wardrobe);
	end
	return self.character:shouldBeTurning();
	end

function PlayerChangeClothes:update()

	if self.count >= self.countTotal then
		if self.sentChange == 0 then
			ClothesAboutToChange(self.character, self.wardrobe, self.optiontype)
			self.sentChange = 1
		end
		self.count = 0
	else
		self.count = self.count + (getGameTime():getGameWorldSecondsSinceLastUpdate()*GTLSCheck)
	end
end

function PlayerChangeClothes:start()

	-- we get the moddata and voice tracks
	--local characterData = self.character:getModData()

	self:setOverrideHandModels(nil, nil)

	self.character:setLy(self.character:getY())
	self.character:setLx(self.character:getX())

	self:setActionAnim("Bob_ChangingClothes")
	
	self.character:getEmitter():playSound("ChangeClothes");
	
end

function PlayerChangeClothes:stop()
    ISBaseTimedAction.stop(self);
end

function PlayerChangeClothes:perform()
	ISBaseTimedAction.perform(self);
end

function PlayerChangeClothes:new(character, Wardrobe, optiontype)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character
	o.wardrobe = Wardrobe
	o.optiontype = optiontype
	o.stopOnAim = false
	o.stopOnWalk = false
	o.stopOnRun = false
	o.sentChange = 0
	o.count = 0
	o.countTotal = 4
	o.maxTime = 30
	o.ignoreHandsWounds = true
	o.useProgressBar = false
	return o;
end

return PlayerChangeClothes;