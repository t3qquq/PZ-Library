
require "WardrobeFunctions"

function AboutToSetCasualClothes(player)	

	--if object and object:getContainer() then
	
			local item
			local playerData = player:getModData()
			player:getModData().CasualClothes = {}
			
			player:getModData().CasualClothes0 = false
			player:getModData().CasualClothes1 = false
			player:getModData().CasualClothes2 = false
			player:getModData().CasualClothes3 = false
			player:getModData().CasualClothes4 = false
			player:getModData().CasualClothes5 = false
			player:getModData().CasualClothes6 = false
			player:getModData().CasualClothes7 = false
			player:getModData().CasualClothes8 = false
			player:getModData().CasualClothes9 = false
			player:getModData().CasualClothes10 = false			
			player:getModData().CasualClothes11 = false
			player:getModData().CasualClothes12 = false
			player:getModData().CasualClothes13 = false
			player:getModData().CasualClothes14 = false
			player:getModData().CasualClothes15 = false
			player:getModData().CasualClothes16 = false
			player:getModData().CasualClothes17 = false
			player:getModData().CasualClothes18 = false
			player:getModData().CasualClothes19 = false
			player:getModData().CasualClothes20 = false			
			player:getModData().CasualClothes21 = false
			player:getModData().CasualClothes22 = false
			player:getModData().CasualClothes23 = false
			player:getModData().CasualClothes24 = false
			player:getModData().CasualClothes25 = false
			player:getModData().CasualClothes26 = false
			player:getModData().CasualClothes27 = false
			player:getModData().CasualClothes28 = false
			player:getModData().CasualClothes29 = false
			player:getModData().CasualClothes30 = false		
	
			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) and (not itemToSet:isHidden()) then
					--table.insert(player:getModData().CasualClothes, item)
					if debugNum == 0 then
						player:getModData().CasualClothes0 = itemToSet:getFullType()
						item = playerData.CasualClothes0; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 1 then
						player:getModData().CasualClothes1 = itemToSet:getFullType()
						item = playerData.CasualClothes1; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 2 then
						player:getModData().CasualClothes2 = itemToSet:getFullType()
						item = playerData.CasualClothes2; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 3 then
						player:getModData().CasualClothes3 = itemToSet:getFullType()
						item = playerData.CasualClothes3; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 4 then
						player:getModData().CasualClothes4 = itemToSet:getFullType()
						item = playerData.CasualClothes4; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 5 then
						player:getModData().CasualClothes5 = itemToSet:getFullType()
						item = playerData.CasualClothes5; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 6 then
						player:getModData().CasualClothes6 = itemToSet:getFullType()
						item = playerData.CasualClothes6; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 7 then
						player:getModData().CasualClothes7 = itemToSet:getFullType()
						item = playerData.CasualClothes7; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 8 then
						player:getModData().CasualClothes8 = itemToSet:getFullType()
						item = playerData.CasualClothes8; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 9 then
						player:getModData().CasualClothes9 = itemToSet:getFullType()
						item = playerData.CasualClothes9; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 10 then
						player:getModData().CasualClothes10 = itemToSet:getFullType()
						item = playerData.CasualClothes10; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 11 then
						player:getModData().CasualClothes11 = itemToSet:getFullType()
						item = playerData.CasualClothes11; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 12 then
						player:getModData().CasualClothes12 = itemToSet:getFullType()
						item = playerData.CasualClothes12; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 13 then
						player:getModData().CasualClothes13 = itemToSet:getFullType()
						item = playerData.CasualClothes13; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 14 then
						player:getModData().CasualClothes14 = itemToSet:getFullType()
						item = playerData.CasualClothes14; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 15 then
						player:getModData().CasualClothes15 = itemToSet:getFullType()
						item = playerData.CasualClothes15; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 16 then
						player:getModData().CasualClothes16 = itemToSet:getFullType()
						item = playerData.CasualClothes16; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 17 then
						player:getModData().CasualClothes17 = itemToSet:getFullType()
						item = playerData.CasualClothes17; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 18 then
						player:getModData().CasualClothes18 = itemToSet:getFullType()
						item = playerData.CasualClothes18; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 19 then
						player:getModData().CasualClothes19 = itemToSet:getFullType()
						item = playerData.CasualClothes19; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 20 then
						player:getModData().CasualClothes20 = itemToSet:getFullType()
						item = playerData.CasualClothes20; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 21 then
						player:getModData().CasualClothes21 = itemToSet:getFullType()
						item = playerData.CasualClothes21; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 22 then
						player:getModData().CasualClothes22 = itemToSet:getFullType()
						item = playerData.CasualClothes22; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 23 then
						player:getModData().CasualClothes23 = itemToSet:getFullType()
						item = playerData.CasualClothes23; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 24 then
						player:getModData().CasualClothes24 = itemToSet:getFullType()
						item = playerData.CasualClothes24; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 25 then
						player:getModData().CasualClothes25 = itemToSet:getFullType()
						item = playerData.CasualClothes25; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 26 then
						player:getModData().CasualClothes26 = itemToSet:getFullType()
						item = playerData.CasualClothes26; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 27 then
						player:getModData().CasualClothes27 = itemToSet:getFullType()
						item = playerData.CasualClothes27; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 28 then
						player:getModData().CasualClothes28 = itemToSet:getFullType()
						item = playerData.CasualClothes28; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 29 then
						player:getModData().CasualClothes29 = itemToSet:getFullType()
						item = playerData.CasualClothes29; table.insert(playerData.CasualClothes, item)
					elseif debugNum == 30 then
						player:getModData().CasualClothes30 = itemToSet:getFullType()
						item = playerData.CasualClothes30; table.insert(playerData.CasualClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Casual set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetFormalClothes(player)	

	--if object and object:getContainer() then
	
			local item		
			local playerData = player:getModData()
			player:getModData().FormalClothes = {}
			
			player:getModData().FormalClothes0 = false
			player:getModData().FormalClothes1 = false
			player:getModData().FormalClothes2 = false
			player:getModData().FormalClothes3 = false
			player:getModData().FormalClothes4 = false
			player:getModData().FormalClothes5 = false
			player:getModData().FormalClothes6 = false
			player:getModData().FormalClothes7 = false
			player:getModData().FormalClothes8 = false
			player:getModData().FormalClothes9 = false
			player:getModData().FormalClothes10 = false			
			player:getModData().FormalClothes11 = false
			player:getModData().FormalClothes12 = false
			player:getModData().FormalClothes13 = false
			player:getModData().FormalClothes14 = false
			player:getModData().FormalClothes15 = false
			player:getModData().FormalClothes16 = false
			player:getModData().FormalClothes17 = false
			player:getModData().FormalClothes18 = false
			player:getModData().FormalClothes19 = false
			player:getModData().FormalClothes20 = false		
			player:getModData().FormalClothes21 = false
			player:getModData().FormalClothes22 = false
			player:getModData().FormalClothes23 = false
			player:getModData().FormalClothes24 = false
			player:getModData().FormalClothes25 = false
			player:getModData().FormalClothes26 = false
			player:getModData().FormalClothes27 = false
			player:getModData().FormalClothes28 = false
			player:getModData().FormalClothes29 = false
			player:getModData().FormalClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().FormalClothes, item)
					if debugNum == 0 then
						player:getModData().FormalClothes0 = itemToSet:getFullType()
						item = playerData.FormalClothes0; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 1 then
						player:getModData().FormalClothes1 = itemToSet:getFullType()
						item = playerData.FormalClothes1; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 2 then
						player:getModData().FormalClothes2 = itemToSet:getFullType()
						item = playerData.FormalClothes2; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 3 then
						player:getModData().FormalClothes3 = itemToSet:getFullType()
						item = playerData.FormalClothes3; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 4 then
						player:getModData().FormalClothes4 = itemToSet:getFullType()
						item = playerData.FormalClothes4; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 5 then
						player:getModData().FormalClothes5 = itemToSet:getFullType()
						item = playerData.FormalClothes5; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 6 then
						player:getModData().FormalClothes6 = itemToSet:getFullType()
						item = playerData.FormalClothes6; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 7 then
						player:getModData().FormalClothes7 = itemToSet:getFullType()
						item = playerData.FormalClothes7; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 8 then
						player:getModData().FormalClothes8 = itemToSet:getFullType()
						item = playerData.FormalClothes8; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 9 then
						player:getModData().FormalClothes9 = itemToSet:getFullType()
						item = playerData.FormalClothes9; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 10 then
						player:getModData().FormalClothes10 = itemToSet:getFullType()
						item = playerData.FormalClothes10; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 11 then
						player:getModData().FormalClothes11 = itemToSet:getFullType()
						item = playerData.FormalClothes11; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 12 then
						player:getModData().FormalClothes12 = itemToSet:getFullType()
						item = playerData.FormalClothes12; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 13 then
						player:getModData().FormalClothes13 = itemToSet:getFullType()
						item = playerData.FormalClothes13; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 14 then
						player:getModData().FormalClothes14 = itemToSet:getFullType()
						item = playerData.FormalClothes14; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 15 then
						player:getModData().FormalClothes15 = itemToSet:getFullType()
						item = playerData.FormalClothes15; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 16 then
						player:getModData().FormalClothes16 = itemToSet:getFullType()
						item = playerData.FormalClothes16; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 17 then
						player:getModData().FormalClothes17 = itemToSet:getFullType()
						item = playerData.FormalClothes17; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 18 then
						player:getModData().FormalClothes18 = itemToSet:getFullType()
						item = playerData.FormalClothes18; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 19 then
						player:getModData().FormalClothes19 = itemToSet:getFullType()
						item = playerData.FormalClothes19; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 20 then
						player:getModData().FormalClothes20 = itemToSet:getFullType()
						item = playerData.FormalClothes20; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 21 then
						player:getModData().FormalClothes21 = itemToSet:getFullType()
						item = playerData.FormalClothes21; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 22 then
						player:getModData().FormalClothes22 = itemToSet:getFullType()
						item = playerData.FormalClothes22; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 23 then
						player:getModData().FormalClothes23 = itemToSet:getFullType()
						item = playerData.FormalClothes23; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 24 then
						player:getModData().FormalClothes24 = itemToSet:getFullType()
						item = playerData.FormalClothes24; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 25 then
						player:getModData().FormalClothes25 = itemToSet:getFullType()
						item = playerData.FormalClothes25; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 26 then
						player:getModData().FormalClothes26 = itemToSet:getFullType()
						item = playerData.FormalClothes26; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 27 then
						player:getModData().FormalClothes27 = itemToSet:getFullType()
						item = playerData.FormalClothes27; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 28 then
						player:getModData().FormalClothes28 = itemToSet:getFullType()
						item = playerData.FormalClothes28; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 29 then
						player:getModData().FormalClothes29 = itemToSet:getFullType()
						item = playerData.FormalClothes29; table.insert(playerData.FormalClothes, item)
					elseif debugNum == 30 then
						player:getModData().FormalClothes30 = itemToSet:getFullType()
						item = playerData.FormalClothes30; table.insert(playerData.FormalClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Formal set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetGymClothes(player)	

	--if object and object:getContainer() then
	
			local item		
			local playerData = player:getModData()
			player:getModData().GymClothes = {}
			
			player:getModData().GymClothes0 = false
			player:getModData().GymClothes1 = false
			player:getModData().GymClothes2 = false
			player:getModData().GymClothes3 = false
			player:getModData().GymClothes4 = false
			player:getModData().GymClothes5 = false
			player:getModData().GymClothes6 = false
			player:getModData().GymClothes7 = false
			player:getModData().GymClothes8 = false
			player:getModData().GymClothes9 = false
			player:getModData().GymClothes10 = false			
			player:getModData().GymClothes11 = false
			player:getModData().GymClothes12 = false
			player:getModData().GymClothes13 = false
			player:getModData().GymClothes14 = false
			player:getModData().GymClothes15 = false
			player:getModData().GymClothes16 = false
			player:getModData().GymClothes17 = false
			player:getModData().GymClothes18 = false
			player:getModData().GymClothes19 = false
			player:getModData().GymClothes20 = false		
			player:getModData().GymClothes21 = false
			player:getModData().GymClothes22 = false
			player:getModData().GymClothes23 = false
			player:getModData().GymClothes24 = false
			player:getModData().GymClothes25 = false
			player:getModData().GymClothes26 = false
			player:getModData().GymClothes27 = false
			player:getModData().GymClothes28 = false
			player:getModData().GymClothes29 = false
			player:getModData().GymClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().GymClothes, item)
					if debugNum == 0 then
						player:getModData().GymClothes0 = itemToSet:getFullType()
						item = playerData.GymClothes0; table.insert(playerData.GymClothes, item)
					elseif debugNum == 1 then
						player:getModData().GymClothes1 = itemToSet:getFullType()
						item = playerData.GymClothes1; table.insert(playerData.GymClothes, item)
					elseif debugNum == 2 then
						player:getModData().GymClothes2 = itemToSet:getFullType()
						item = playerData.GymClothes2; table.insert(playerData.GymClothes, item)
					elseif debugNum == 3 then
						player:getModData().GymClothes3 = itemToSet:getFullType()
						item = playerData.GymClothes3; table.insert(playerData.GymClothes, item)
					elseif debugNum == 4 then
						player:getModData().GymClothes4 = itemToSet:getFullType()
						item = playerData.GymClothes4; table.insert(playerData.GymClothes, item)
					elseif debugNum == 5 then
						player:getModData().GymClothes5 = itemToSet:getFullType()
						item = playerData.GymClothes5; table.insert(playerData.GymClothes, item)
					elseif debugNum == 6 then
						player:getModData().GymClothes6 = itemToSet:getFullType()
						item = playerData.GymClothes6; table.insert(playerData.GymClothes, item)
					elseif debugNum == 7 then
						player:getModData().GymClothes7 = itemToSet:getFullType()
						item = playerData.GymClothes7; table.insert(playerData.GymClothes, item)
					elseif debugNum == 8 then
						player:getModData().GymClothes8 = itemToSet:getFullType()
						item = playerData.GymClothes8; table.insert(playerData.GymClothes, item)
					elseif debugNum == 9 then
						player:getModData().GymClothes9 = itemToSet:getFullType()
						item = playerData.GymClothes9; table.insert(playerData.GymClothes, item)
					elseif debugNum == 10 then
						player:getModData().GymClothes10 = itemToSet:getFullType()
						item = playerData.GymClothes10; table.insert(playerData.GymClothes, item)
					elseif debugNum == 11 then
						player:getModData().GymClothes11 = itemToSet:getFullType()
						item = playerData.GymClothes11; table.insert(playerData.GymClothes, item)
					elseif debugNum == 12 then
						player:getModData().GymClothes12 = itemToSet:getFullType()
						item = playerData.GymClothes12; table.insert(playerData.GymClothes, item)
					elseif debugNum == 13 then
						player:getModData().GymClothes13 = itemToSet:getFullType()
						item = playerData.GymClothes13; table.insert(playerData.GymClothes, item)
					elseif debugNum == 14 then
						player:getModData().GymClothes14 = itemToSet:getFullType()
						item = playerData.GymClothes14; table.insert(playerData.GymClothes, item)
					elseif debugNum == 15 then
						player:getModData().GymClothes15 = itemToSet:getFullType()
						item = playerData.GymClothes15; table.insert(playerData.GymClothes, item)
					elseif debugNum == 16 then
						player:getModData().GymClothes16 = itemToSet:getFullType()
						item = playerData.GymClothes16; table.insert(playerData.GymClothes, item)
					elseif debugNum == 17 then
						player:getModData().GymClothes17 = itemToSet:getFullType()
						item = playerData.GymClothes17; table.insert(playerData.GymClothes, item)
					elseif debugNum == 18 then
						player:getModData().GymClothes18 = itemToSet:getFullType()
						item = playerData.GymClothes18; table.insert(playerData.GymClothes, item)
					elseif debugNum == 19 then
						player:getModData().GymClothes19 = itemToSet:getFullType()
						item = playerData.GymClothes19; table.insert(playerData.GymClothes, item)
					elseif debugNum == 20 then
						player:getModData().GymClothes20 = itemToSet:getFullType()
						item = playerData.GymClothes20; table.insert(playerData.GymClothes, item)
					elseif debugNum == 21 then
						player:getModData().GymClothes21 = itemToSet:getFullType()
						item = playerData.GymClothes21; table.insert(playerData.GymClothes, item)
					elseif debugNum == 22 then
						player:getModData().GymClothes22 = itemToSet:getFullType()
						item = playerData.GymClothes22; table.insert(playerData.GymClothes, item)
					elseif debugNum == 23 then
						player:getModData().GymClothes23 = itemToSet:getFullType()
						item = playerData.GymClothes23; table.insert(playerData.GymClothes, item)
					elseif debugNum == 24 then
						player:getModData().GymClothes24 = itemToSet:getFullType()
						item = playerData.GymClothes24; table.insert(playerData.GymClothes, item)
					elseif debugNum == 25 then
						player:getModData().GymClothes25 = itemToSet:getFullType()
						item = playerData.GymClothes25; table.insert(playerData.GymClothes, item)
					elseif debugNum == 26 then
						player:getModData().GymClothes26 = itemToSet:getFullType()
						item = playerData.GymClothes26; table.insert(playerData.GymClothes, item)
					elseif debugNum == 27 then
						player:getModData().GymClothes27 = itemToSet:getFullType()
						item = playerData.GymClothes27; table.insert(playerData.GymClothes, item)
					elseif debugNum == 28 then
						player:getModData().GymClothes28 = itemToSet:getFullType()
						item = playerData.GymClothes28; table.insert(playerData.GymClothes, item)
					elseif debugNum == 29 then
						player:getModData().GymClothes29 = itemToSet:getFullType()
						item = playerData.GymClothes29; table.insert(playerData.GymClothes, item)
					elseif debugNum == 30 then
						player:getModData().GymClothes30 = itemToSet:getFullType()
						item = playerData.GymClothes30; table.insert(playerData.GymClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Gym set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetSleepClothes(player)	

	--if object and object:getContainer() then
	
			local item
			local playerData = player:getModData()
			player:getModData().SleepClothes = {}
			
			player:getModData().SleepClothes0 = false
			player:getModData().SleepClothes1 = false
			player:getModData().SleepClothes2 = false
			player:getModData().SleepClothes3 = false
			player:getModData().SleepClothes4 = false
			player:getModData().SleepClothes5 = false
			player:getModData().SleepClothes6 = false
			player:getModData().SleepClothes7 = false
			player:getModData().SleepClothes8 = false
			player:getModData().SleepClothes9 = false
			player:getModData().SleepClothes10 = false			
			player:getModData().SleepClothes11 = false
			player:getModData().SleepClothes12 = false
			player:getModData().SleepClothes13 = false
			player:getModData().SleepClothes14 = false
			player:getModData().SleepClothes15 = false
			player:getModData().SleepClothes16 = false
			player:getModData().SleepClothes17 = false
			player:getModData().SleepClothes18 = false
			player:getModData().SleepClothes19 = false
			player:getModData().SleepClothes20 = false		
			player:getModData().SleepClothes21 = false
			player:getModData().SleepClothes22 = false
			player:getModData().SleepClothes23 = false
			player:getModData().SleepClothes24 = false
			player:getModData().SleepClothes25 = false
			player:getModData().SleepClothes26 = false
			player:getModData().SleepClothes27 = false
			player:getModData().SleepClothes28 = false
			player:getModData().SleepClothes29 = false
			player:getModData().SleepClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().SleepClothes, item)
					if debugNum == 0 then
						player:getModData().SleepClothes0 = itemToSet:getFullType()
						item = playerData.SleepClothes0; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 1 then
						player:getModData().SleepClothes1 = itemToSet:getFullType()
						item = playerData.SleepClothes1; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 2 then
						player:getModData().SleepClothes2 = itemToSet:getFullType()
						item = playerData.SleepClothes2; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 3 then
						player:getModData().SleepClothes3 = itemToSet:getFullType()
						item = playerData.SleepClothes3; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 4 then
						player:getModData().SleepClothes4 = itemToSet:getFullType()
						item = playerData.SleepClothes4; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 5 then
						player:getModData().SleepClothes5 = itemToSet:getFullType()
						item = playerData.SleepClothes5; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 6 then
						player:getModData().SleepClothes6 = itemToSet:getFullType()
						item = playerData.SleepClothes6; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 7 then
						player:getModData().SleepClothes7 = itemToSet:getFullType()
						item = playerData.SleepClothes7; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 8 then
						player:getModData().SleepClothes8 = itemToSet:getFullType()
						item = playerData.SleepClothes8; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 9 then
						player:getModData().SleepClothes9 = itemToSet:getFullType()
						item = playerData.SleepClothes9; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 10 then
						player:getModData().SleepClothes10 = itemToSet:getFullType()
						item = playerData.SleepClothes10; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 11 then
						player:getModData().SleepClothes11 = itemToSet:getFullType()
						item = playerData.SleepClothes11; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 12 then
						player:getModData().SleepClothes12 = itemToSet:getFullType()
						item = playerData.SleepClothes12; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 13 then
						player:getModData().SleepClothes13 = itemToSet:getFullType()
						item = playerData.SleepClothes13; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 14 then
						player:getModData().SleepClothes14 = itemToSet:getFullType()
						item = playerData.SleepClothes14; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 15 then
						player:getModData().SleepClothes15 = itemToSet:getFullType()
						item = playerData.SleepClothes15; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 16 then
						player:getModData().SleepClothes16 = itemToSet:getFullType()
						item = playerData.SleepClothes16; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 17 then
						player:getModData().SleepClothes17 = itemToSet:getFullType()
						item = playerData.SleepClothes17; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 18 then
						player:getModData().SleepClothes18 = itemToSet:getFullType()
						item = playerData.SleepClothes18; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 19 then
						player:getModData().SleepClothes19 = itemToSet:getFullType()
						item = playerData.SleepClothes19; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 20 then
						player:getModData().SleepClothes20 = itemToSet:getFullType()
						item = playerData.SleepClothes20; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 21 then
						player:getModData().SleepClothes21 = itemToSet:getFullType()
						item = playerData.SleepClothes21; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 22 then
						player:getModData().SleepClothes22 = itemToSet:getFullType()
						item = playerData.SleepClothes22; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 23 then
						player:getModData().SleepClothes23 = itemToSet:getFullType()
						item = playerData.SleepClothes23; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 24 then
						player:getModData().SleepClothes24 = itemToSet:getFullType()
						item = playerData.SleepClothes24; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 25 then
						player:getModData().SleepClothes25 = itemToSet:getFullType()
						item = playerData.SleepClothes25; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 26 then
						player:getModData().SleepClothes26 = itemToSet:getFullType()
						item = playerData.SleepClothes26; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 27 then
						player:getModData().SleepClothes27 = itemToSet:getFullType()
						item = playerData.SleepClothes27; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 28 then
						player:getModData().SleepClothes28 = itemToSet:getFullType()
						item = playerData.SleepClothes28; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 29 then
						player:getModData().SleepClothes29 = itemToSet:getFullType()
						item = playerData.SleepClothes29; table.insert(playerData.SleepClothes, item)
					elseif debugNum == 30 then
						player:getModData().SleepClothes30 = itemToSet:getFullType()
						item = playerData.SleepClothes30; table.insert(playerData.SleepClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Sleep set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetPartyClothes(player)	

	--if object and object:getContainer() then
	
			local item		
			local playerData = player:getModData()
			player:getModData().PartyClothes = {}
			
			player:getModData().PartyClothes0 = false
			player:getModData().PartyClothes1 = false
			player:getModData().PartyClothes2 = false
			player:getModData().PartyClothes3 = false
			player:getModData().PartyClothes4 = false
			player:getModData().PartyClothes5 = false
			player:getModData().PartyClothes6 = false
			player:getModData().PartyClothes7 = false
			player:getModData().PartyClothes8 = false
			player:getModData().PartyClothes9 = false
			player:getModData().PartyClothes10 = false			
			player:getModData().PartyClothes11 = false
			player:getModData().PartyClothes12 = false
			player:getModData().PartyClothes13 = false
			player:getModData().PartyClothes14 = false
			player:getModData().PartyClothes15 = false
			player:getModData().PartyClothes16 = false
			player:getModData().PartyClothes17 = false
			player:getModData().PartyClothes18 = false
			player:getModData().PartyClothes19 = false
			player:getModData().PartyClothes20 = false		
			player:getModData().PartyClothes21 = false
			player:getModData().PartyClothes22 = false
			player:getModData().PartyClothes23 = false
			player:getModData().PartyClothes24 = false
			player:getModData().PartyClothes25 = false
			player:getModData().PartyClothes26 = false
			player:getModData().PartyClothes27 = false
			player:getModData().PartyClothes28 = false
			player:getModData().PartyClothes29 = false
			player:getModData().PartyClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().PartyClothes, item)
					if debugNum == 0 then
						player:getModData().PartyClothes0 = itemToSet:getFullType()
						item = playerData.PartyClothes0; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 1 then
						player:getModData().PartyClothes1 = itemToSet:getFullType()
						item = playerData.PartyClothes1; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 2 then
						player:getModData().PartyClothes2 = itemToSet:getFullType()
						item = playerData.PartyClothes2; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 3 then
						player:getModData().PartyClothes3 = itemToSet:getFullType()
						item = playerData.PartyClothes3; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 4 then
						player:getModData().PartyClothes4 = itemToSet:getFullType()
						item = playerData.PartyClothes4; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 5 then
						player:getModData().PartyClothes5 = itemToSet:getFullType()
						item = playerData.PartyClothes5; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 6 then
						player:getModData().PartyClothes6 = itemToSet:getFullType()
						item = playerData.PartyClothes6; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 7 then
						player:getModData().PartyClothes7 = itemToSet:getFullType()
						item = playerData.PartyClothes7; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 8 then
						player:getModData().PartyClothes8 = itemToSet:getFullType()
						item = playerData.PartyClothes8; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 9 then
						player:getModData().PartyClothes9 = itemToSet:getFullType()
						item = playerData.PartyClothes9; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 10 then
						player:getModData().PartyClothes10 = itemToSet:getFullType()
						item = playerData.PartyClothes10; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 11 then
						player:getModData().PartyClothes11 = itemToSet:getFullType()
						item = playerData.PartyClothes11; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 12 then
						player:getModData().PartyClothes12 = itemToSet:getFullType()
						item = playerData.PartyClothes12; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 13 then
						player:getModData().PartyClothes13 = itemToSet:getFullType()
						item = playerData.PartyClothes13; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 14 then
						player:getModData().PartyClothes14 = itemToSet:getFullType()
						item = playerData.PartyClothes14; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 15 then
						player:getModData().PartyClothes15 = itemToSet:getFullType()
						item = playerData.PartyClothes15; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 16 then
						player:getModData().PartyClothes16 = itemToSet:getFullType()
						item = playerData.PartyClothes16; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 17 then
						player:getModData().PartyClothes17 = itemToSet:getFullType()
						item = playerData.PartyClothes17; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 18 then
						player:getModData().PartyClothes18 = itemToSet:getFullType()
						item = playerData.PartyClothes18; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 19 then
						player:getModData().PartyClothes19 = itemToSet:getFullType()
						item = playerData.PartyClothes19; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 20 then
						player:getModData().PartyClothes20 = itemToSet:getFullType()
						item = playerData.PartyClothes20; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 21 then
						player:getModData().PartyClothes21 = itemToSet:getFullType()
						item = playerData.PartyClothes21; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 22 then
						player:getModData().PartyClothes22 = itemToSet:getFullType()
						item = playerData.PartyClothes22; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 23 then
						player:getModData().PartyClothes23 = itemToSet:getFullType()
						item = playerData.PartyClothes23; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 24 then
						player:getModData().PartyClothes24 = itemToSet:getFullType()
						item = playerData.PartyClothes24; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 25 then
						player:getModData().PartyClothes25 = itemToSet:getFullType()
						item = playerData.PartyClothes25; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 26 then
						player:getModData().PartyClothes26 = itemToSet:getFullType()
						item = playerData.PartyClothes26; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 27 then
						player:getModData().PartyClothes27 = itemToSet:getFullType()
						item = playerData.PartyClothes27; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 28 then
						player:getModData().PartyClothes28 = itemToSet:getFullType()
						item = playerData.PartyClothes28; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 29 then
						player:getModData().PartyClothes29 = itemToSet:getFullType()
						item = playerData.PartyClothes29; table.insert(playerData.PartyClothes, item)
					elseif debugNum == 30 then
						player:getModData().PartyClothes30 = itemToSet:getFullType()
						item = playerData.PartyClothes30; table.insert(playerData.PartyClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Party set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetSummerClothes(player)	

	--if object and object:getContainer() then
	
			local item		
			local playerData = player:getModData()
			player:getModData().SummerClothes = {}
			
			player:getModData().SummerClothes0 = false
			player:getModData().SummerClothes1 = false
			player:getModData().SummerClothes2 = false
			player:getModData().SummerClothes3 = false
			player:getModData().SummerClothes4 = false
			player:getModData().SummerClothes5 = false
			player:getModData().SummerClothes6 = false
			player:getModData().SummerClothes7 = false
			player:getModData().SummerClothes8 = false
			player:getModData().SummerClothes9 = false
			player:getModData().SummerClothes10 = false			
			player:getModData().SummerClothes11 = false
			player:getModData().SummerClothes12 = false
			player:getModData().SummerClothes13 = false
			player:getModData().SummerClothes14 = false
			player:getModData().SummerClothes15 = false
			player:getModData().SummerClothes16 = false
			player:getModData().SummerClothes17 = false
			player:getModData().SummerClothes18 = false
			player:getModData().SummerClothes19 = false
			player:getModData().SummerClothes20 = false		
			player:getModData().SummerClothes21 = false
			player:getModData().SummerClothes22 = false
			player:getModData().SummerClothes23 = false
			player:getModData().SummerClothes24 = false
			player:getModData().SummerClothes25 = false
			player:getModData().SummerClothes26 = false
			player:getModData().SummerClothes27 = false
			player:getModData().SummerClothes28 = false
			player:getModData().SummerClothes29 = false
			player:getModData().SummerClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().SummerClothes, item)
					if debugNum == 0 then
						player:getModData().SummerClothes0 = itemToSet:getFullType()
						item = playerData.SummerClothes0; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 1 then
						player:getModData().SummerClothes1 = itemToSet:getFullType()
						item = playerData.SummerClothes1; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 2 then
						player:getModData().SummerClothes2 = itemToSet:getFullType()
						item = playerData.SummerClothes2; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 3 then
						player:getModData().SummerClothes3 = itemToSet:getFullType()
						item = playerData.SummerClothes3; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 4 then
						player:getModData().SummerClothes4 = itemToSet:getFullType()
						item = playerData.SummerClothes4; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 5 then
						player:getModData().SummerClothes5 = itemToSet:getFullType()
						item = playerData.SummerClothes5; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 6 then
						player:getModData().SummerClothes6 = itemToSet:getFullType()
						item = playerData.SummerClothes6; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 7 then
						player:getModData().SummerClothes7 = itemToSet:getFullType()
						item = playerData.SummerClothes7; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 8 then
						player:getModData().SummerClothes8 = itemToSet:getFullType()
						item = playerData.SummerClothes8; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 9 then
						player:getModData().SummerClothes9 = itemToSet:getFullType()
						item = playerData.SummerClothes9; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 10 then
						player:getModData().SummerClothes10 = itemToSet:getFullType()
						item = playerData.SummerClothes10; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 11 then
						player:getModData().SummerClothes11 = itemToSet:getFullType()
						item = playerData.SummerClothes11; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 12 then
						player:getModData().SummerClothes12 = itemToSet:getFullType()
						item = playerData.SummerClothes12; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 13 then
						player:getModData().SummerClothes13 = itemToSet:getFullType()
						item = playerData.SummerClothes13; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 14 then
						player:getModData().SummerClothes14 = itemToSet:getFullType()
						item = playerData.SummerClothes14; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 15 then
						player:getModData().SummerClothes15 = itemToSet:getFullType()
						item = playerData.SummerClothes15; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 16 then
						player:getModData().SummerClothes16 = itemToSet:getFullType()
						item = playerData.SummerClothes16; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 17 then
						player:getModData().SummerClothes17 = itemToSet:getFullType()
						item = playerData.SummerClothes17; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 18 then
						player:getModData().SummerClothes18 = itemToSet:getFullType()
						item = playerData.SummerClothes18; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 19 then
						player:getModData().SummerClothes19 = itemToSet:getFullType()
						item = playerData.SummerClothes19; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 20 then
						player:getModData().SummerClothes20 = itemToSet:getFullType()
						item = playerData.SummerClothes20; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 21 then
						player:getModData().SummerClothes21 = itemToSet:getFullType()
						item = playerData.SummerClothes21; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 22 then
						player:getModData().SummerClothes22 = itemToSet:getFullType()
						item = playerData.SummerClothes22; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 23 then
						player:getModData().SummerClothes23 = itemToSet:getFullType()
						item = playerData.SummerClothes23; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 24 then
						player:getModData().SummerClothes24 = itemToSet:getFullType()
						item = playerData.SummerClothes24; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 25 then
						player:getModData().SummerClothes25 = itemToSet:getFullType()
						item = playerData.SummerClothes25; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 26 then
						player:getModData().SummerClothes26 = itemToSet:getFullType()
						item = playerData.SummerClothes26; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 27 then
						player:getModData().SummerClothes27 = itemToSet:getFullType()
						item = playerData.SummerClothes27; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 28 then
						player:getModData().SummerClothes28 = itemToSet:getFullType()
						item = playerData.SummerClothes28; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 29 then
						player:getModData().SummerClothes29 = itemToSet:getFullType()
						item = playerData.SummerClothes29; table.insert(playerData.SummerClothes, item)
					elseif debugNum == 30 then
						player:getModData().SummerClothes30 = itemToSet:getFullType()
						item = playerData.SummerClothes30; table.insert(playerData.SummerClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Summer set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetWinterClothes(player)	

	--if object and object:getContainer() then
	
			local item		
			local playerData = player:getModData()
			player:getModData().WinterClothes = {}
			
			player:getModData().WinterClothes0 = false
			player:getModData().WinterClothes1 = false
			player:getModData().WinterClothes2 = false
			player:getModData().WinterClothes3 = false
			player:getModData().WinterClothes4 = false
			player:getModData().WinterClothes5 = false
			player:getModData().WinterClothes6 = false
			player:getModData().WinterClothes7 = false
			player:getModData().WinterClothes8 = false
			player:getModData().WinterClothes9 = false
			player:getModData().WinterClothes10 = false			
			player:getModData().WinterClothes11 = false
			player:getModData().WinterClothes12 = false
			player:getModData().WinterClothes13 = false
			player:getModData().WinterClothes14 = false
			player:getModData().WinterClothes15 = false
			player:getModData().WinterClothes16 = false
			player:getModData().WinterClothes17 = false
			player:getModData().WinterClothes18 = false
			player:getModData().WinterClothes19 = false
			player:getModData().WinterClothes20 = false		
			player:getModData().WinterClothes21 = false
			player:getModData().WinterClothes22 = false
			player:getModData().WinterClothes23 = false
			player:getModData().WinterClothes24 = false
			player:getModData().WinterClothes25 = false
			player:getModData().WinterClothes26 = false
			player:getModData().WinterClothes27 = false
			player:getModData().WinterClothes28 = false
			player:getModData().WinterClothes29 = false
			player:getModData().WinterClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().WinterClothes, item)
					if debugNum == 0 then
						player:getModData().WinterClothes0 = itemToSet:getFullType()
						item = playerData.WinterClothes0; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 1 then
						player:getModData().WinterClothes1 = itemToSet:getFullType()
						item = playerData.WinterClothes1; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 2 then
						player:getModData().WinterClothes2 = itemToSet:getFullType()
						item = playerData.WinterClothes2; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 3 then
						player:getModData().WinterClothes3 = itemToSet:getFullType()
						item = playerData.WinterClothes3; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 4 then
						player:getModData().WinterClothes4 = itemToSet:getFullType()
						item = playerData.WinterClothes4; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 5 then
						player:getModData().WinterClothes5 = itemToSet:getFullType()
						item = playerData.WinterClothes5; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 6 then
						player:getModData().WinterClothes6 = itemToSet:getFullType()
						item = playerData.WinterClothes6; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 7 then
						player:getModData().WinterClothes7 = itemToSet:getFullType()
						item = playerData.WinterClothes7; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 8 then
						player:getModData().WinterClothes8 = itemToSet:getFullType()
						item = playerData.WinterClothes8; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 9 then
						player:getModData().WinterClothes9 = itemToSet:getFullType()
						item = playerData.WinterClothes9; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 10 then
						player:getModData().WinterClothes10 = itemToSet:getFullType()
						item = playerData.WinterClothes10; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 11 then
						player:getModData().WinterClothes11 = itemToSet:getFullType()
						item = playerData.WinterClothes11; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 12 then
						player:getModData().WinterClothes12 = itemToSet:getFullType()
						item = playerData.WinterClothes12; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 13 then
						player:getModData().WinterClothes13 = itemToSet:getFullType()
						item = playerData.WinterClothes13; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 14 then
						player:getModData().WinterClothes14 = itemToSet:getFullType()
						item = playerData.WinterClothes14; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 15 then
						player:getModData().WinterClothes15 = itemToSet:getFullType()
						item = playerData.WinterClothes15; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 16 then
						player:getModData().WinterClothes16 = itemToSet:getFullType()
						item = playerData.WinterClothes16; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 17 then
						player:getModData().WinterClothes17 = itemToSet:getFullType()
						item = playerData.WinterClothes17; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 18 then
						player:getModData().WinterClothes18 = itemToSet:getFullType()
						item = playerData.WinterClothes18; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 19 then
						player:getModData().WinterClothes19 = itemToSet:getFullType()
						item = playerData.WinterClothes19; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 20 then
						player:getModData().WinterClothes20 = itemToSet:getFullType()
						item = playerData.WinterClothes20; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 21 then
						player:getModData().WinterClothes21 = itemToSet:getFullType()
						item = playerData.WinterClothes21; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 22 then
						player:getModData().WinterClothes22 = itemToSet:getFullType()
						item = playerData.WinterClothes22; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 23 then
						player:getModData().WinterClothes23 = itemToSet:getFullType()
						item = playerData.WinterClothes23; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 24 then
						player:getModData().WinterClothes24 = itemToSet:getFullType()
						item = playerData.WinterClothes24; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 25 then
						player:getModData().WinterClothes25 = itemToSet:getFullType()
						item = playerData.WinterClothes25; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 26 then
						player:getModData().WinterClothes26 = itemToSet:getFullType()
						item = playerData.WinterClothes26; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 27 then
						player:getModData().WinterClothes27 = itemToSet:getFullType()
						item = playerData.WinterClothes27; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 28 then
						player:getModData().WinterClothes28 = itemToSet:getFullType()
						item = playerData.WinterClothes28; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 29 then
						player:getModData().WinterClothes29 = itemToSet:getFullType()
						item = playerData.WinterClothes29; table.insert(playerData.WinterClothes, item)
					elseif debugNum == 30 then
						player:getModData().WinterClothes30 = itemToSet:getFullType()
						item = playerData.WinterClothes30; table.insert(playerData.WinterClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Winter set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetWorkClothes(player)	

	--if object and object:getContainer() then
	
			local item		
			local playerData = player:getModData()
			player:getModData().WorkClothes = {}
			
			player:getModData().WorkClothes0 = false
			player:getModData().WorkClothes1 = false
			player:getModData().WorkClothes2 = false
			player:getModData().WorkClothes3 = false
			player:getModData().WorkClothes4 = false
			player:getModData().WorkClothes5 = false
			player:getModData().WorkClothes6 = false
			player:getModData().WorkClothes7 = false
			player:getModData().WorkClothes8 = false
			player:getModData().WorkClothes9 = false
			player:getModData().WorkClothes10 = false			
			player:getModData().WorkClothes11 = false
			player:getModData().WorkClothes12 = false
			player:getModData().WorkClothes13 = false
			player:getModData().WorkClothes14 = false
			player:getModData().WorkClothes15 = false
			player:getModData().WorkClothes16 = false
			player:getModData().WorkClothes17 = false
			player:getModData().WorkClothes18 = false
			player:getModData().WorkClothes19 = false
			player:getModData().WorkClothes20 = false		
			player:getModData().WorkClothes21 = false
			player:getModData().WorkClothes22 = false
			player:getModData().WorkClothes23 = false
			player:getModData().WorkClothes24 = false
			player:getModData().WorkClothes25 = false
			player:getModData().WorkClothes26 = false
			player:getModData().WorkClothes27 = false
			player:getModData().WorkClothes28 = false
			player:getModData().WorkClothes29 = false
			player:getModData().WorkClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().WorkClothes, item)
					if debugNum == 0 then
						player:getModData().WorkClothes0 = itemToSet:getFullType()
						item = playerData.WorkClothes0; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 1 then
						player:getModData().WorkClothes1 = itemToSet:getFullType()
						item = playerData.WorkClothes1; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 2 then
						player:getModData().WorkClothes2 = itemToSet:getFullType()
						item = playerData.WorkClothes2; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 3 then
						player:getModData().WorkClothes3 = itemToSet:getFullType()
						item = playerData.WorkClothes3; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 4 then
						player:getModData().WorkClothes4 = itemToSet:getFullType()
						item = playerData.WorkClothes4; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 5 then
						player:getModData().WorkClothes5 = itemToSet:getFullType()
						item = playerData.WorkClothes5; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 6 then
						player:getModData().WorkClothes6 = itemToSet:getFullType()
						item = playerData.WorkClothes6; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 7 then
						player:getModData().WorkClothes7 = itemToSet:getFullType()
						item = playerData.WorkClothes7; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 8 then
						player:getModData().WorkClothes8 = itemToSet:getFullType()
						item = playerData.WorkClothes8; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 9 then
						player:getModData().WorkClothes9 = itemToSet:getFullType()
						item = playerData.WorkClothes9; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 10 then
						player:getModData().WorkClothes10 = itemToSet:getFullType()
						item = playerData.WorkClothes10; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 11 then
						player:getModData().WorkClothes11 = itemToSet:getFullType()
						item = playerData.WorkClothes11; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 12 then
						player:getModData().WorkClothes12 = itemToSet:getFullType()
						item = playerData.WorkClothes12; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 13 then
						player:getModData().WorkClothes13 = itemToSet:getFullType()
						item = playerData.WorkClothes13; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 14 then
						player:getModData().WorkClothes14 = itemToSet:getFullType()
						item = playerData.WorkClothes14; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 15 then
						player:getModData().WorkClothes15 = itemToSet:getFullType()
						item = playerData.WorkClothes15; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 16 then
						player:getModData().WorkClothes16 = itemToSet:getFullType()
						item = playerData.WorkClothes16; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 17 then
						player:getModData().WorkClothes17 = itemToSet:getFullType()
						item = playerData.WorkClothes17; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 18 then
						player:getModData().WorkClothes18 = itemToSet:getFullType()
						item = playerData.WorkClothes18; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 19 then
						player:getModData().WorkClothes19 = itemToSet:getFullType()
						item = playerData.WorkClothes19; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 20 then
						player:getModData().WorkClothes20 = itemToSet:getFullType()
						item = playerData.WorkClothes20; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 21 then
						player:getModData().WorkClothes21 = itemToSet:getFullType()
						item = playerData.WorkClothes21; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 22 then
						player:getModData().WorkClothes22 = itemToSet:getFullType()
						item = playerData.WorkClothes22; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 23 then
						player:getModData().WorkClothes23 = itemToSet:getFullType()
						item = playerData.WorkClothes23; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 24 then
						player:getModData().WorkClothes24 = itemToSet:getFullType()
						item = playerData.WorkClothes24; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 25 then
						player:getModData().WorkClothes25 = itemToSet:getFullType()
						item = playerData.WorkClothes25; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 26 then
						player:getModData().WorkClothes26 = itemToSet:getFullType()
						item = playerData.WorkClothes26; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 27 then
						player:getModData().WorkClothes27 = itemToSet:getFullType()
						item = playerData.WorkClothes27; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 28 then
						player:getModData().WorkClothes28 = itemToSet:getFullType()
						item = playerData.WorkClothes28; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 29 then
						player:getModData().WorkClothes29 = itemToSet:getFullType()
						item = playerData.WorkClothes29; table.insert(playerData.WorkClothes, item)
					elseif debugNum == 30 then
						player:getModData().WorkClothes30 = itemToSet:getFullType()
						item = playerData.WorkClothes30; table.insert(playerData.WorkClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Work set total:  " .. tonumber(debugNum))

	--end
end


function AboutToSetCombatClothes(player)	

	--if object and object:getContainer() then
	
			local item		
			local playerData = player:getModData()
			player:getModData().CombatClothes = {}
			
			player:getModData().CombatClothes0 = false
			player:getModData().CombatClothes1 = false
			player:getModData().CombatClothes2 = false
			player:getModData().CombatClothes3 = false
			player:getModData().CombatClothes4 = false
			player:getModData().CombatClothes5 = false
			player:getModData().CombatClothes6 = false
			player:getModData().CombatClothes7 = false
			player:getModData().CombatClothes8 = false
			player:getModData().CombatClothes9 = false
			player:getModData().CombatClothes10 = false			
			player:getModData().CombatClothes11 = false
			player:getModData().CombatClothes12 = false
			player:getModData().CombatClothes13 = false
			player:getModData().CombatClothes14 = false
			player:getModData().CombatClothes15 = false
			player:getModData().CombatClothes16 = false
			player:getModData().CombatClothes17 = false
			player:getModData().CombatClothes18 = false
			player:getModData().CombatClothes19 = false
			player:getModData().CombatClothes20 = false		
			player:getModData().CombatClothes21 = false
			player:getModData().CombatClothes22 = false
			player:getModData().CombatClothes23 = false
			player:getModData().CombatClothes24 = false
			player:getModData().CombatClothes25 = false
			player:getModData().CombatClothes26 = false
			player:getModData().CombatClothes27 = false
			player:getModData().CombatClothes28 = false
			player:getModData().CombatClothes29 = false
			player:getModData().CombatClothes30 = false		

			local inventory = player:getInventory()	
			local it = inventory:getItems();
			local debugNum = 0

			for j = 0, it:size()-1 do
				local itemToSet = it:get(j);
				if player:isEquippedClothing(itemToSet) then
					--table.insert(player:getModData().CombatClothes, item)
					if debugNum == 0 then
						player:getModData().CombatClothes0 = itemToSet:getFullType()
						item = playerData.CombatClothes0; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 1 then
						player:getModData().CombatClothes1 = itemToSet:getFullType()
						item = playerData.CombatClothes1; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 2 then
						player:getModData().CombatClothes2 = itemToSet:getFullType()
						item = playerData.CombatClothes2; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 3 then
						player:getModData().CombatClothes3 = itemToSet:getFullType()
						item = playerData.CombatClothes3; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 4 then
						player:getModData().CombatClothes4 = itemToSet:getFullType()
						item = playerData.CombatClothes4; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 5 then
						player:getModData().CombatClothes5 = itemToSet:getFullType()
						item = playerData.CombatClothes5; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 6 then
						player:getModData().CombatClothes6 = itemToSet:getFullType()
						item = playerData.CombatClothes6; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 7 then
						player:getModData().CombatClothes7 = itemToSet:getFullType()
						item = playerData.CombatClothes7; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 8 then
						player:getModData().CombatClothes8 = itemToSet:getFullType()
						item = playerData.CombatClothes8; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 9 then
						player:getModData().CombatClothes9 = itemToSet:getFullType()
						item = playerData.CombatClothes9; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 10 then
						player:getModData().CombatClothes10 = itemToSet:getFullType()
						item = playerData.CombatClothes10; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 11 then
						player:getModData().CombatClothes11 = itemToSet:getFullType()
						item = playerData.CombatClothes11; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 12 then
						player:getModData().CombatClothes12 = itemToSet:getFullType()
						item = playerData.CombatClothes12; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 13 then
						player:getModData().CombatClothes13 = itemToSet:getFullType()
						item = playerData.CombatClothes13; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 14 then
						player:getModData().CombatClothes14 = itemToSet:getFullType()
						item = playerData.CombatClothes14; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 15 then
						player:getModData().CombatClothes15 = itemToSet:getFullType()
						item = playerData.CombatClothes15; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 16 then
						player:getModData().CombatClothes16 = itemToSet:getFullType()
						item = playerData.CombatClothes16; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 17 then
						player:getModData().CombatClothes17 = itemToSet:getFullType()
						item = playerData.CombatClothes17; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 18 then
						player:getModData().CombatClothes18 = itemToSet:getFullType()
						item = playerData.CombatClothes18; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 19 then
						player:getModData().CombatClothes19 = itemToSet:getFullType()
						item = playerData.CombatClothes19; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 20 then
						player:getModData().CombatClothes20 = itemToSet:getFullType()
						item = playerData.CombatClothes20; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 21 then
						player:getModData().CombatClothes21 = itemToSet:getFullType()
						item = playerData.CombatClothes21; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 22 then
						player:getModData().CombatClothes22 = itemToSet:getFullType()
						item = playerData.CombatClothes22; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 23 then
						player:getModData().CombatClothes23 = itemToSet:getFullType()
						item = playerData.CombatClothes23; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 24 then
						player:getModData().CombatClothes24 = itemToSet:getFullType()
						item = playerData.CombatClothes24; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 25 then
						player:getModData().CombatClothes25 = itemToSet:getFullType()
						item = playerData.CombatClothes25; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 26 then
						player:getModData().CombatClothes26 = itemToSet:getFullType()
						item = playerData.CombatClothes26; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 27 then
						player:getModData().CombatClothes27 = itemToSet:getFullType()
						item = playerData.CombatClothes27; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 28 then
						player:getModData().CombatClothes28 = itemToSet:getFullType()
						item = playerData.CombatClothes28; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 29 then
						player:getModData().CombatClothes29 = itemToSet:getFullType()
						item = playerData.CombatClothes29; table.insert(playerData.CombatClothes, item)
					elseif debugNum == 30 then
						player:getModData().CombatClothes30 = itemToSet:getFullType()
						item = playerData.CombatClothes30; table.insert(playerData.CombatClothes, item)
					end
					debugNum = debugNum + 1
				end
			end
			getSoundManager():playUISound("UI_Button_SELECT")
			player:Say("Combat set total:  " .. tonumber(debugNum))

	--end
end
