Events.OnGameStart.Add(function()
	if not getActivatedMods():contains("Even Worse Searching") and not getActivatedMods():contains("Worse Searching") and not getActivatedMods():contains("Worst Searching Ever") then return end
	require "TimedActions/PASearchContainerAction"
	local upperLayer = {}
	upperLayer.PASearchContainerAction = {}
	-------------------------------------------------------------------------------------------------------------------------------------------------------
	--upperLayer.PASearchContainerAction.start = PASearchContainerAction.start
	function PASearchContainerAction:start()
	    --upperLayer.PASearchContainerAction.start(self)
	    if self.sound then
			self.character:getEmitter():stopSound(self.sound)

		end
																		
	    local cont = self.container
	    local contType = cont:getType()
	    local vehicleInside = self.character:getVehicle()
	    self.soundName = "_SearchFloor"
	    --print("type contenant : "..contType)
		-------------------------------------------------------------------------------------
		if instanceof(cont:getParent(), "IsoDeadBody") 											then self.soundName = "_SearchDeadBody"								
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "Paper") 	  or string.contains(contType, "paper") 	then self.soundName = "_SearchPaperBag"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "Garbage")   or string.contains(contType, "garbage") 		
			or string.contains(contType, "Grocery")   or string.contains(contType, "grocery") 	then self.soundName = "_SearchPlasticBag"
		-------------------------------------------------------------------------------------		
		elseif string.contains(contType, "Bag") 	  or string.contains(contType, "bag") 		then self.soundName = "_SearchBagCloth"
		-------------------------------------------------------------------------------------
		elseif luautils.stringStarts(contType, 'bin') or luautils.stringStarts(contType, 'Bin') then self.soundName = "_SearchBinTrash" 		
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "post") 	  or string.contains(contType, "Post") 		then self.soundName = "_SearchPost"		
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "wardrobe")  or string.contains(contType, "Wardrobe") 	then self.soundName = "_SearchWardrobe"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "GloveBox")  or string.contains(contType, "gloveBox") 	then self.soundName = "_SearchGloveBox" 
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "medicin")   or string.contains(contType, "Medicin") 	then self.soundName = "_SearchMedicine"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "Box") 	  or string.contains(contType, "box") 		
			or string.contains(contType, "crate") 	  or string.contains(contType, "Crate")  	then self.soundName = "_SearchCrate"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "tool")   	  or string.contains(contType, "Tool") 		then self.soundName = "_SearchTool"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "locker") 	  or string.contains(contType, "Locker") 	then self.soundName = "_SearchLocker"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "overhead")  or string.contains(contType, "Overhead") 	then self.soundName = "_SearchOverhead"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "counter")   or string.contains(contType, "Counter") 	then self.soundName = "_SearchCounter"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "shelve") 	  or string.contains(contType, "Shelve") 	then self.soundName = "_SearchShelve" 
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "TruckBed")  or string.contains(contType, "truckBed")	then self.soundName = "_SearchTruckBed" 
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "microwave") or string.contains(contType, "Microwave")	then self.soundName = "_SearchMicrowave" 
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "desk") 	  or string.contains(contType, "Desk") 		
			or string.contains(contType, "sidetable") or string.contains(contType, "Sidetable") then self.soundName = "_SearchDesk"  
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "stove") 	  or string.contains(contType, "Stove") 	then self.soundName = "_SearchStove"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "fridge")    or string.contains(contType, "Fridge")
			or string.contains(contType, "freezer")   or string.contains(contType, "Freezer") 	then self.soundName = "_SearchFridge"
		-------------------------------------------------------------------------------------
		elseif string.contains(contType, "cabinet")   or string.contains(contType, "Cabinet") 	then self.soundName = "_Searchcabinet"
		-------------------------------------------------------------------------------------
		end
		-------------------------------------------------------------------------------------
		if vehicleInside then self.preFix = "inTIsnd" else self.preFix = "TIsnd" end
		self.sound = self.character:getEmitter():playSound(self.preFix..self.soundName)
		-------------------------------------------------------------------------------------
		local cont = self.container
	
		self:setActionAnim("Loot");
		self:setAnimVariable("LootPosition", "");
		self:setOverrideHandModels(nil, nil);
		self.character:clearVariable("LootPosition");
		if cont:getContainerPosition() then
			self:setAnimVariable("LootPosition", cont:getContainerPosition());
		end
		if cont:getType() == "freezer" and cont:getFreezerPosition() then
			self:setAnimVariable("LootPosition", cont:getFreezerPosition());
		end
		if instanceof(cont:getParent(), "IsoDeadBody") or cont:getType() == "floor" then
			self:setAnimVariable("LootPosition", "Low");
		end
		if cont:getContainingItem() and cont:getContainingItem():getWorldItem() then
			self:setAnimVariable("LootPosition", "Low");
		end
				
	end
	-------------------------------------------------------------------------------------------------------------------------------------------------------
	upperLayer.PASearchContainerAction.update = PASearchContainerAction.update
	function PASearchContainerAction:update()
		if self.sound and self.sound ~= 0 and not self.character:getEmitter():isPlaying(self.sound) then 
			local vehicleInside = self.character:getVehicle()
			if vehicleInside then self.preFix = "inTIsnd" else self.preFix = "TIsnd" end
			self.sound = self.character:playSound(self.preFix..self.soundName) 
		end
	    upperLayer.PASearchContainerAction.update(self)
	end
end)
