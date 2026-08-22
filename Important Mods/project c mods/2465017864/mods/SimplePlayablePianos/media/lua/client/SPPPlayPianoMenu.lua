--***********************************************************
--** PseudonymousEd, the Dev
--** Simple Playable Pianos 41.50+
--** Context menu for playing a piano
--***********************************************************
require 'XpSystem/ISUI/extraskills'

SPPPlayPianoMenu = {};

-- Add play option to piano context menus
SPPPlayPianoMenu.doBuildMenu = function(player, context, worldobjects)

	-- Default to using Custom Animations	
	local USE_CUSTOM_ANIMATION = true
	
	local thisPlayer = getSpecificPlayer(player);

	-- Default to not using Custom Animations if in Build 41.50
	if getCore():getVersionNumber() == "41.50 - IWBUMS" then
		USE_CUSTOM_ANIMATION = false
	end
	--****************************************
	--* ATTENTION: If you have installed
	--*      the custom animations manually for Build 41.50,
	--*      make sure to uncomment
	--*      (remove -- from the front of)
	--*      local USE_CUSTOM_ANIMATION = true
	--****************************************
	
	-- To enable Custom Animations for Build 41.50,
	-- remove the -- from the following line:
	--local USE_CUSTOM_ANIMATION = true

	local piano = nil
	local spriteName = nil

	local objects = {}
	for _,object in ipairs(worldobjects) do
		local square = object:getSquare()
		if square then
			for i=1,square:getObjects():size() do
				local thisObject = square:getObjects():get(i-1)

				local thisSprite = thisObject:getSprite()
				
				if thisSprite ~= nil then
				
					local properties = thisObject:getSprite():getProperties()

					if properties ~= nil then
						local groupName = nil
						local customName = nil
						local thisSpriteName = nil
					
						--local thisSprite = thisObject:getSprite()
						if thisSprite:getName() then
							thisSpriteName = thisSprite:getName()
							--print("PseudoEdSPP: Sprite Name is " .. spriteName)
						end
					
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
							--print("PseudoEdSPP: Sprite GroupName: " .. groupName);
						end
					
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
							--print("PseudoEdSPP: Sprite CustomName: " .. customName);
						end
					
						-- For Pianos, Custom Name = "Piano".
						-- In vanilla PZ, GroupName = "Western", but leave that open for modders
						if customName == "Piano" then
							piano = thisObject;
							spriteName = thisSpriteName;
						end
					end
				end
			end
		end
	end

	if not piano then return end

	--print("PseudoEdSPP: Getting Piano Level ");
	
	local level = -1
	
	if getCore():getVersionNumber() == "41.50 - IWBUMS" then
		level = thisPlayer:getModData().skills["Piano"].level;
		--print("PseudoEdSPP: Build 41.50 ");
	else
		level = thisPlayer:getPerkLevel(Perks.PseudonymousEdPiano)
		--print("PseudoEdSPP: Not Build 41.50 ");
	end
	--thisPlayer:Say("Piano level is..." .. level)
	
	-- local piano = thisObject;
	local soundFile = nil;
	local contextMenu = nil;
	local actionType = nil;
	
	actionType = "Loot"

	-- There are slightly different animations for each piano position
	if USE_CUSTOM_ANIMATION == true then
		if spriteName == "recreational_01_8" then
			actionType = "PlayPianoSL"
		elseif spriteName == "recreational_01_9" then
			actionType = "PlayPianoSR"
		elseif spriteName == "recreational_01_12" then
			actionType = "PlayPianoEL"
		elseif spriteName == "recreational_01_13" then
			actionType = "PlayPianoER"
		else
			actionType = "Loot"
		end
	end

	-- Benefits of calculating XP here:
	-- not affected by maxTime changes from injured hands or sadness
	-- no need to worry about calculating xp in multiple locations
	local xp = 0
	local XPPerSecond = 0.01
	
	local boredomReduction = 0
	local baseBoredomPerSecond = 0.12
	local practiceBoredomPerSecond = 0.1
	local stressReduction = 0
	local baseStressPerSecond = 0.03
	
	-- Play Piano
	local performancePieces = require("TimedActions/SPPPerformancePieces")
	
	local leveledPieces = {}
	
	-- Select pieces up to the player's level
	for k,v in pairs(performancePieces) do
		if v.level <= level then
			table.insert(leveledPieces, v)
		end
	end
	
	local randomNumber = ZombRand(#leveledPieces) + 1
	
	local randomPiece = leveledPieces[randomNumber]
	
	local length = randomPiece.length * 48
	
	if level >= 10 then
		xp = 0
	else
		xp = XPPerSecond * randomPiece.length * (randomPiece.level + 1)
		
		if randomPiece.level < 5 then 
			xp = XPPerSecond * randomPiece.length * (randomPiece.level + 1)
		else
			xp = 3 * randomPiece.length * (randomPiece.level / level)
		end
	end
	
	boredomReduction = baseBoredomPerSecond * randomPiece.length * (randomPiece.level + 1)
	stressReduction = baseStressPerSecond * randomPiece.length * (randomPiece.level + 1)
	
	contextMenu = "ContextMenu_Play_Random_Piano"
	
	context:addOption(getText(contextMenu),
					  worldobjects,
					  SPPPlayPianoMenu.onPlayPiano,
					  getSpecificPlayer(player),
					  piano,
					  randomPiece.sound,
					  length,
					  randomPiece.level,
					  false,
					  xp,
					  boredomReduction,
					  stressReduction,
					  actionType);
	
	-- Play L# or Virtuoso Piano
	
	if level < 10 then
		contextMenu = "ContextMenu_Play_L" .. level .. "_Piano"
	else
		contextMenu = "ContextMenu_Play_Virtuoso_Piano"
	end
	
	local buildOption = context:addOption(getText(contextMenu), worldobjects, nil);
	local subMenu = ISContextMenu:getNew(context);
	context:addSubMenu(buildOption, subMenu)
	
	-- Select pieces at the player's level only
	for k,v in pairs(performancePieces) do
		if v.level == level then
			local length = v.length * 48
			if level >= 10 then
				xp = 0
			else
				if v.level < 5 then 
					xp = XPPerSecond * v.length * (v.level + 1)
				else
					xp = 3 * v.length * (v.level / level)
				end
			end
			
			boredomReduction = baseBoredomPerSecond * v.length * (v.level + 1)
			stressReduction = baseStressPerSecond * v.length * (v.level + 1)
			
			contextMenu = v.name
			
			subMenu:addOption(getText(contextMenu),
							  worldobjects,
							  SPPPlayPianoMenu.onPlayPiano,
							  getSpecificPlayer(player),
							  piano,
							  v.sound,
							  length,
							  v.level,
							  false,
							  xp,
							  boredomReduction,
							  stressReduction,
							  actionType);
		end
	end
	
	-- Play Groups of Pieces
	if level > 0 then
		
		-- Beginner pieces are L0-L3
		contextMenu = "ContextMenu_Play_Beginner_Piano"
		
		local buildOption = context:addOption(getText(contextMenu), worldobjects, nil);
		local subMenu = ISContextMenu:getNew(context);
		context:addSubMenu(buildOption, subMenu)
		
		for k,v in pairs(performancePieces) do
			if v.level <= 3 and v.level <= level then
				local length = v.length * 48
				if level >= 10 then
					xp = 0
				else
					xp = XPPerSecond * v.length * (v.level + 1)
				end
				
				boredomReduction = baseBoredomPerSecond * v.length * (v.level + 1)
				stressReduction = baseStressPerSecond * v.length * (v.level + 1)
				
				contextMenu = v.name
				
				subMenu:addOption(getText(contextMenu),
								  worldobjects,
								  SPPPlayPianoMenu.onPlayPiano,
								  getSpecificPlayer(player),
								  piano,
								  v.sound,
								  length,
								  v.level,
								  false,
								  xp,
								  boredomReduction,
								  stressReduction,
								  actionType);
			end
		end
		
		if level > 4 then
			
			-- Intermediate pieces are L4-L7
			contextMenu = "ContextMenu_Play_Intermediate_Piano"
			local buildOption = context:addOption(getText(contextMenu), worldobjects, nil);
			local subMenu = ISContextMenu:getNew(context);
			context:addSubMenu(buildOption, subMenu)
			
			for k,v in pairs(performancePieces) do
				if v.level > 3 and v.level <= 7 and v.level <= level then
					local length = v.length * 48
					if level >= 10 then
						xp = 0
					else
						if v.level < 5 then 
							xp = XPPerSecond * v.length * (v.level + 1)
						else
							xp = 3 * v.length * (v.level / level)
						end
					end
					
					boredomReduction = baseBoredomPerSecond * v.length * (v.level + 1)
					stressReduction = baseStressPerSecond * v.length * (v.level + 1)
					
					contextMenu = v.name
					
					subMenu:addOption(getText(contextMenu),
									  worldobjects,
									  SPPPlayPianoMenu.onPlayPiano,
									  getSpecificPlayer(player),
									  piano,
									  v.sound,
									  length,
									  v.level,
									  false,
									  xp,
									  boredomReduction,
									  stressReduction,
									  actionType);
				end
			end
			
			if level > 8 then
				
				-- Advanced pieces are L8-L10
				contextMenu = "ContextMenu_Play_Advanced_Piano"
				local buildOption = context:addOption(getText(contextMenu), worldobjects, nil);
				local subMenu = ISContextMenu:getNew(context);
				context:addSubMenu(buildOption, subMenu)
				
				for k,v in pairs(performancePieces) do
					if v.level > 7 and v.level <= level then
						local length = v.length * 48
						if level >= 10 then
							xp = 0
						else
							xp = 3 * v.length * (v.level / level)
						end
						
						boredomReduction = baseBoredomPerSecond * v.length * (v.level + 1)
						stressReduction = baseStressPerSecond * v.length * (v.level + 1)
						
						contextMenu = v.name
						
						subMenu:addOption(getText(contextMenu),
										  worldobjects,
										  SPPPlayPianoMenu.onPlayPiano,
										  getSpecificPlayer(player),
										  piano,
										  v.sound,
										  length,
										  v.level,
										  false,
										  xp,
										  boredomReduction,
										  stressReduction,
										  actionType);
					end -- if v.level > 7 and v.level <= level
				end -- for k,v in pairs(performancePieces)
			end -- if level > 8
		end -- if level > 4
	end -- if level > 0
	
	
	-- Practice Piano
	if level < 5 then
		local allPracticePieces = require("TimedActions/SPPPracticePieces")
		local practicePieces = {}
		
		for k,v in pairs(allPracticePieces) do
			if v.level == level then
				table.insert(practicePieces, v)
			end
		end
		
		local randomNumber = ZombRand(#practicePieces) + 1
		
		local randomPiece = practicePieces[randomNumber]
		
		local length = randomPiece.length * 48
		
		xp = 10 * 5 * XPPerSecond * randomPiece.length * (randomPiece.level + 1)
		boredomReduction = -1 * practiceBoredomPerSecond * randomPiece.length
		stressReduction = 0
		
		contextMenu = "ContextMenu_Practice_Piano"
		context:addOption(getText(contextMenu),
						  worldobjects,
						  SPPPlayPianoMenu.onPlayPiano,
						  getSpecificPlayer(player),
						  piano,
						  randomPiece.sound,
						  length,
						  randomPiece.level,
						  true,
						  xp,
						  boredomReduction,
						  stressReduction,
						  actionType);
	end -- if level < 5 -- Practice
end -- SPPPlayPianoMenu.doBuildMenu

-- Walk to the front of the piano, which currently can only face S or E
SPPPlayPianoMenu.walkToFront = function(thisPlayer, thisObject)
	local frontSquare = nil
	local controllerSquare = nil
	local spriteName = thisObject:getSprite():getName()
	if not spriteName then
		return false
	end

	local properties = thisObject:getSprite():getProperties()
	
	local facing = nil
	if properties:Is("Facing") then
		facing = properties:Val("Facing")
	else
		print("PseudoEdSPP Warning: " .. spriteName .. " has no facing")
		return
	end
	
	if facing == "S" then
		frontSquare = thisObject:getSquare():getS()
	elseif facing == "E" then
		frontSquare = thisObject:getSquare():getE()
	end
	
	if not frontSquare then
		return false
	end
	
	if not controllerSquare then
		controllerSquare = thisObject:getSquare()
	end

	-- If the front of piano square is valid, walk to it
	if AdjacentFreeTileFinder.privTrySquare(controllerSquare, frontSquare) then
		ISTimedActionQueue.add(ISWalkToTimedAction:new(thisPlayer, frontSquare))
		return true
	end
	return false
end

-- Do when player selects option to play the piano (from context menu)
SPPPlayPianoMenu.onPlayPiano = function(worldobjects, player, piano, soundFile, length, level, isPractice, xp, boredomReduction, stressReduction, actionType)
	if SPPPlayPianoMenu.walkToFront(player, piano) then
		ISTimedActionQueue.add(SPPPlayPiano:new(player, piano, soundFile, length, level, isPractice, xp, boredomReduction, stressReduction, actionType))
	end
end

Events.OnPreFillWorldObjectContextMenu.Add(SPPPlayPianoMenu.doBuildMenu);
