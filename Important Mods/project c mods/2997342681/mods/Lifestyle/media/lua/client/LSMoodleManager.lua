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

LSMoodleManager = LSMoodleManager or {};
LSMoodleManager.LSMoodles = {}
LSMoodleManager.BUIInstance = false
local MoodleManagerEnabled = false
local MoodleManagerPlayerSpawn = false
--local isDTMoodleAPI = true
--local MoodleManagerPlayerDeath = false

--[[
if getActivatedMods():contains('DynamicTraits') or getActivatedMods():contains('DynamicTraits[RF4]') or getActivatedMods():contains('DynamicTraits[RF5]') then
require "MoodleAPI/MoodleAPIClient"
end
]]--

LSMoodleManager.init = function(player)

	player:getModData().LSMoodles = player:getModData().LSMoodles or {}; 

	local moodleProperties = require("Properties/MoodleProperties")
	
	for k,v in pairs(moodleProperties) do
		if v.name then
			if not player:getModData().LSMoodles[v.name] or
			not player:getModData().LSMoodles[v.name].Alignment or
			not player:getModData().LSMoodles[v.name].Value or
			not player:getModData().LSMoodles[v.name].Level or
			not player:getModData().LSMoodles[v.name].Icon or
			(player:getModData().LSMoodles[v.name].Icon ~= v.Icon) or
			not player:getModData().LSMoodles[v.name].Tiers then

				player:getModData().LSMoodles[v.name] = {};
				player:getModData().LSMoodles[v.name].Level = v.Level;
				player:getModData().LSMoodles[v.name].Value = v.Value;
				player:getModData().LSMoodles[v.name].Tiers = v.Tiers;
				player:getModData().LSMoodles[v.name].Icon = v.Icon;
				player:getModData().LSMoodles[v.name].Alignment = v.Alignment;

			end
		end
	end

end

function LSMoodleManager.getMoodle(moodleName)
    return LSMoodleManager.LSMoodles[moodleName];
end

LSMoodleManager.getValue = function(moodleName)
	if MoodleManagerEnabled == false then return 0 end
    local player = getPlayer()
   LSMoodleManager.init(player)
    return player:getModData().LSMoodles[moodleName].Value
end

LSMoodleManager.getLevel = function(moodleName)
    local player = getPlayer()
    LSMoodleManager.init(player)

    return player:getModData().LSMoodles[moodleName].Level
end

LSMoodleManager.setValue = function(moodleName, value)
	if MoodleManagerEnabled == false then return end
    local player = getPlayer()
    LSMoodleManager.init(player)

    player:getModData().LSMoodles[moodleName].Value = value
end

local function getMoodleIconText(moodleLevel, moodleName, data)
	local tiers, textOnly, textAdd = data.LSMoodles[moodleName].Tiers, data.LSMoodles[moodleName].Icon, ""
	local moodleIcon, moodleText, moodleTooltip = "media/ui/moodles/"..moodleName..".png", "Moodles_"..moodleName.."_L1", "Moodles_"..moodleName.."_L1_desc"
	if moodleName == "WasTaughtSkill" and data.WasTaughtLast then textAdd = " ("..getText("IGUI_perks_"..tostring(data.WasTaughtLast))..")"; end
	if textOnly == 3 then return moodleIcon, getText(moodleText)..textAdd, moodleTooltip; end --multiple tiers, 1 text, 1 icon
	if (moodleLevel == 4) and ((tiers == 3) or (textOnly == 1)) then
		moodleIcon, moodleText, moodleTooltip = "media/ui/moodles/"..moodleName.."3.png", "Moodles_"..moodleName.."_L4", "Moodles_"..moodleName.."_L4_desc"
	elseif (moodleLevel >= 3) and ((tiers >= 2) or (textOnly == 1)) then
		moodleIcon, moodleText, moodleTooltip = "media/ui/moodles/"..moodleName.."2.png", "Moodles_"..moodleName.."_L3", "Moodles_"..moodleName.."_L3_desc"
	elseif (moodleLevel >= 2) and ((tiers >= 1) or (textOnly == 1)) then
		moodleIcon, moodleText, moodleTooltip = "media/ui/moodles/"..moodleName.."1.png", "Moodles_"..moodleName.."_L2", "Moodles_"..moodleName.."_L2_desc"
	end

	if textOnly == 1 then moodleIcon = "media/ui/moodles/"..moodleName..".png"; --1 icon (no tiers), multiple texts
	elseif textOnly == 2 then moodleText, moodleTooltip = "Moodles_"..moodleName.."_L1", "Moodles_"..moodleName.."_L1_desc"; end --multiple icons, 1 text
	return moodleIcon, getText(moodleText)..textAdd, moodleTooltip
end

function LSgetMoodleBkg(player, moodleLevel, moodleName)
	local alignment = player:getModData().LSMoodles[moodleName].Alignment
	local moodleBkg = "media/ui/Moodle_Bkg_"..alignment.."_1.png"
	
	if moodleLevel == 4 then
		moodleBkg = "media/ui/Moodle_Bkg_"..alignment.."_4.png"
	elseif moodleLevel == 3 then
		moodleBkg = "media/ui/Moodle_Bkg_"..alignment.."_3.png"
	elseif moodleLevel == 2 then
		moodleBkg = "media/ui/Moodle_Bkg_"..alignment.."_2.png"
	end

	return moodleBkg
end

local function checkWiggle(player, moodleLevel, moodleName, level)
	local wiggleBidirectional = false
	if ZombRand(2) == 0 then
		wiggleBidirectional = true
	end

	player:getModData().LSMoodles[moodleName].Level = moodleLevel
	--print("Moodle Level for "..moodleName.." is now "..moodleLevel);

	return true, wiggleBidirectional
end

local function isMouseOverFirstOrLast(x, y, w, h)
    local mX, mY = getMouseX(), getMouseY()
    if (mX >= x) and (mY >= y) and (mX <= x + w) and (mY <= y + h) then return true; end
    return false;
end

LSMoodleManager.newType = function(player, moodleName)

	--if MoodleManagerEnabled == false then return end
	if player ~= nil then
	--get screen and font
    local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small);
    local FONT_HGT_MEDIUM = getTextManager():getFontHeight(UIFont.Medium);
    local FONT_HGT_LARGE = getTextManager():getFontHeight(UIFont.Large);
    local SCREEN_X = getCore():getScreenWidth();
    local SCREEN_Y = getCore():getScreenHeight();
    local WIDTH = 31;
    local HEIGHT = 50;
	--wiggle motion
    local enableWiggle, wiggle, wiggleX, wiggleY, wiggleBidirectional, wiggleDegradation = false, 0, 0, 0, false, 0;
	--init
    LSMoodleManager.init(player);

    local ISMoodles = ISUIElement:derive("ISMoodles");

    ISMoodles.initialise = function(LSMoodleManager)
        ISUIElement:initialise(LSMoodleManager);
    end
	--MoodleManagerEnabled = true
    ISMoodles.render = function(LSMoodleManager)
		--if MoodleManagerEnabled == false then return end
		if MoodleManagerEnabled == true and player:getModData().LSMoodles[moodleName] ~= nil then
        local moodleLevel = LSMoodleManager:level();
        local icon = getTexture("media/ui/moodles/"..moodleName..".png");

        if enableWiggle then
            if wiggleBidirectional then
                wiggle = wiggle - 13;
                wiggleX = wiggle * math.sin(0.5);
                if wiggle <= -30 then
                    wiggleBidirectional = false;
                    wiggleDegradation = wiggleDegradation + 1;
                end
            else 
                wiggle = wiggle + 13;
                wiggleX = wiggle * math.sin(0.5);
                if wiggle >= 30 then
                    wiggleBidirectional = true;
                    wiggleDegradation = wiggleDegradation + 1;
                end
            end
        end

        if wiggleDegradation > 2 then
            enableWiggle = false;
                
            if wiggle <= 2 and wiggle >= -2 then
                wiggleX = 0;
                wiggleDegradation = 0;
            elseif wiggle < 0 then
                wiggle = wiggle + 5;
                wiggleX = wiggle * math.sin(0.5);
            elseif wiggle > 0 then
                wiggle = wiggle - 5;
                wiggleX = wiggle * math.sin(0.5);
            end
        end

        if moodleLevel == 0 then
            if player:getModData().LSMoodles[moodleName].Level ~= 0 then
                player:getModData().LSMoodles[moodleName].Level = 0;
            end
			--LSMoodleManager:backMost()
		else
			if (player:getModData().LSMoodles[moodleName].Level ~= moodleLevel) then
				enableWiggle, wiggleBidirectional = checkWiggle(player, moodleLevel, moodleName, player:getModData().LSMoodles[moodleName].Level)
			end
			
			
			local moodleIcon, moodleText, moodleTooltip = getMoodleIconText(moodleLevel, moodleName, player:getModData())
			local moodleBkg
			moodleBkg = LSgetMoodleBkg(player, moodleLevel, moodleName)
			LSMoodleManager:drawTexture(getTexture(moodleBkg), wiggleX, 0, 1, 1, 1, 1)
            LSMoodleManager:drawTexture(getTexture(moodleIcon), wiggleX, 0, 1, 1, 1, 1)
			LSMoodleManager:mouseOverMoodle(moodleName, moodleText, getText(moodleTooltip))
            --LSMoodleManager:bringToTop()
			--backMost()
		end

        local x, y = ISMoodles:updateLSMoodles();
        if y ~= LSMoodleManager:getY() then LSMoodleManager:setY(y) end
		if x ~= LSMoodleManager:getX() then LSMoodleManager:setX(x) end
		end
    end

    ISMoodles.mouseOverMoodle = function(LSMoodleManager, moodleName, title, description)
	--if MoodleManagerEnabled == false then return end
		if MoodleManagerEnabled == true and player:getModData().LSMoodles[moodleName] ~= nil then
        local rectWidth = 5;
        local rectHeight = 31;

        if LSMoodleManager:isMouseOver() or isMouseOverFirstOrLast(LSMoodleManager:getX(), LSMoodleManager:getY(), LSMoodleManager:getWidth(), LSMoodleManager:getHeight()) then
            local titleLength = getTextManager():MeasureStringX(UIFont.Small, title) + 7;
            local descriptionLength = getTextManager():MeasureStringX(UIFont.Small, description) + 7;

            if titleLength >= descriptionLength then
                LSMoodleManager:drawRect(-4 - (rectWidth + titleLength), 0, rectWidth + titleLength, rectHeight, 0.6, 0, 0, 0);
            elseif titleLength <= descriptionLength then
                LSMoodleManager:drawRect(-4 - (rectWidth + descriptionLength), 0, rectWidth + descriptionLength, rectHeight, 0.6, 0, 0, 0);
            end

            LSMoodleManager:drawTextRight(title, -10, 2, 1, 1, 1, 1);
            LSMoodleManager:drawTextRight(description, -10, 15, 1, 1, 1, 0.7);
			if moodleName and (moodleName == "BeautyGood" or moodleName == "BeautyNeg") and not LSMoodleManager.BUIInstance then
				LSMoodleManager.BUIInstance = LSBeautyScore:new(self, getPlayer(), true)
				LSMoodleManager.BUIInstance:initialise()
				LSMoodleManager.BUIInstance:addToUIManager()
				LSMoodleManager.BUIHovering = true
			end
		elseif LSMoodleManager.BUIInstance and LSMoodleManager.BUIHovering then
			LSMoodleManager.BUIInstance:close()
			LSMoodleManager.BUIInstance = false
			LSMoodleManager.BUIHovering = false
        end
		end
    end

    ISMoodles.level = function(LSMoodleManager)
	--if MoodleManagerEnabled == false then return end
		if MoodleManagerEnabled == true and player:getModData().LSMoodles[moodleName] ~= nil then
        local value = player:getModData().LSMoodles[moodleName].Value;

        if value >= 0.7 then
            return 4
        elseif value >= 0.5 then
            return 3
        elseif value >= 0.3 then
            return 2
        elseif value >= 0.15 then
            return 1
        end

        return 0
		end
    end

    ISMoodles.updateLSMoodles = function(LSMoodleManager)
	--if MoodleManagerEnabled == false then return end
		--if MoodleManagerEnabled == true and player:getModData().LSMoodles[moodleName] ~= nil then
        local x = (getCore():getScreenWidth() - WIDTH) - 19;
        local y = 101;
		if MoodleManagerEnabled == false then return x,y end
        for i = 0, 23 do
            if player:getMoodles():getMoodleLevel(MoodleType.FromIndex(i)) ~= 0 then
                y = y + 36;
            end
        end

        for k, v in pairs(player:getModData().LSMoodles) do--our moodles
            if k == moodleName then
                break
            else
                if v.Level ~= 0 and player:getModData().LSMoodles[moodleName].Level ~= 0 then
                    y = y + 36;
                end
            end
        end

	local sandboxmoodlepriority = SandboxVars.Debug.MoodlePriority or false
	if sandboxmoodlepriority then
        local ModdedMoodles = player:getModData().Moodles--modded
        if ModdedMoodles then
            for k, v in pairs(player:getModData().Moodles) do
                if v.Level ~= 0 then
                    y = y + 36;
               end
			end
       end
	end

	--[[
	if (getActivatedMods():contains('DynamicTraits') or getActivatedMods():contains('DynamicTraits[RF4]') or getActivatedMods():contains('DynamicTraits[RF5]')) and isDTMoodleAPI then
		local MoodleAPI = require("MoodleAPI/MoodleAPIClient")
		if MoodleAPI and MoodleAPI.MoodleList and (#MoodleAPI.MoodleList > 0) then
			for _, moodleObj in pairs(MoodleAPI.MoodleList) do

				local lvl = moodleObj.getLevelFunc(moodleObj)
				if lvl > 0 then
					y = y + 36
				end
			end
		else
			isDTMoodleAPI = false
		end
	end
	]]--

        return x, y
		--end
    end

    ISMoodles.new = function(LSMoodleManager, width, height)
	--if MoodleManagerEnabled == false then return end
		if MoodleManagerEnabled == true and player:getModData().LSMoodles[moodleName] ~= nil then
        local x, y = ISMoodles:updateLSMoodles();
        
        local o = {};
        o = ISUIElement:new(x, y, width, height);
        setmetatable(o, LSMoodleManager);
        LSMoodleManager.__index = LSMoodleManager;
        o.borderColor = {r=0, g=0, b=0, a=0};
        o.backgroundColor = {r=0, g=0, b=0, a=0};

        return o;
		end
    end

    return ISMoodles:new(WIDTH, HEIGHT)
	end
end


--Util to add LSMoodleManager to UI manager on game start and removes on death, then adds it back on player creation 
LSMoodleManager.createType = function(player, moodleName)
	if not MoodleManagerPlayerSpawn then
	MoodleManagerPlayerSpawn = true
	player:getModData().LSMoodles = {}; 
	end
	MoodleManagerEnabled = true
    local moodleTypeUI = LSMoodleManager.newType(player, moodleName)

    moodleTypeUI:addToUIManager()
    --print("CreateType")

    --local onCreatePlayer = function(index, player, moodleName)
		--LSMoodleManager.init(player)
	--	MoodleManagerEnabled = true
		
	--	local player = getPlayer()
     --   local moodleTypeUI = LSMoodleManager.newType(player, moodleName)
        --print("onCreatePlayer")

     --   moodleTypeUI:addToUIManager()
   -- end

    local onPlayerDeath = function(player, moodleName)
        moodleTypeUI:removeFromUIManager()
		MoodleManagerEnabled = false
		--LSMoodleManager = {}
		--LSMoodleManager.LSMoodles = {}
		--MoodleManagerPlayerDeath = true
		--Events.OnPlayerDeath.Remove(onPlayerDeath)
    end
    
    --Events.OnCreatePlayer.Add(onCreatePlayer)
    Events.OnPlayerDeath.Add(onPlayerDeath)
end

function onPlayerDeathLSMoodle()
MoodleManagerPlayerSpawn = false
end

Events.OnPlayerDeath.Add(onPlayerDeathLSMoodle)

local onGameStartLSGeneralMoodles = function()

	local moodleProperties = require("Properties/MoodleProperties")
	--isDTMoodleAPI = true
	for k,v in pairs(moodleProperties) do
		if v.name then
			LSMoodleManager.createType(getPlayer(), v.name);
		end
	end

end
Events.OnCreatePlayer.Add(onGameStartLSGeneralMoodles);
--Events.OnGameStart.Add(onGameStartLSGeneralMoodles);

if getActivatedMods():contains('MoodleFramework') or getActivatedMods():contains('MoodleFramework[RF4]') or getActivatedMods():contains('MoodleFramework[RF5]') then
require "MF_ISMoodle"

function MF.ISMoodle:getXYPosition()

    local x = (getCore():getScreenWidth() - self:getWidth() * MF.scale) - 18;
    local y = 101;
    
	if self.disable then if MF.verbose then print("MF.ISMoodle:getXYPosition while disabled. "..self.name) end; return x,y; end
	
    if self:getLevel() ~= 0 then
        for i = 0, 23 do--vanilla moodles
            if self.char:getMoodles():getMoodleLevel(MoodleType.FromIndex(i)) ~= 0 then
                y = y + 36 * MF.scale;
            end
        end
        
        local aiteronMM = self.char:getModData().MoodleManager;--aiteron moodles
        if aiteronMM and aiteronMM.moodles then
            local nbMoodlesAiteron = 0
            for _, moodleObj in pairs(aiteronMM.moodles) do

                if moodleObj.getLevel then
                    local lvl = moodleObj:getLevel()
                    if lvl > 0 then
                        nbMoodlesAiteron = nbMoodlesAiteron + 1
                        y = y + 36 * MF.scale;
                    end
                end
            end
            
        else
            
        end
		local sandboxmoodlepriority = SandboxVars.Debug.MoodlePriority or false

		if self.char:getModData().LSMoodles and not sandboxmoodlepriority then
			for k, v in pairs(self.char:getModData().LSMoodles) do--our moodles
				if v.Level ~= 0 then
					y = y + 36;
				end
			end
		end
		for k, v in pairs(self.char:getModData().Moodles) do--modded moodles then
			if k == self.name then
				break
			elseif v.Level ~= 0 then
				y = y + 36 * MF.scale;
			end
		end

    end
		return x, y
end
end
