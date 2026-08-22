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

require "ISUI/ISPanelJoypad"

LSMenuOverlay = ISPanelJoypad:derive("LSMenuOverlay");

local LSMainMenuOverlay
local LSMenuremoved = false
--[[
local LSMenuplay = 0
local LSMenuplay2 = 0
local LSScreenScaleX = getCore():getScreenWidth() / 1920
local LSScreenScaleY = getCore():getScreenHeight() / 1080
local LSMenuScreenshotImage
local LSMenuScreenshot2Image
local LSMenuScreenshotFrameImage
--local ScreenshotFrameTexture = getTexture("media/ui/frame.png")
local ScreenshotTexture1 = getTexture("media/ui/screenshots/music0.png")
local ScreenshotTexture2 = getTexture("media/ui/screenshots/disco0.png")
local MainMenuImageEventListenerNumberCount = 0
local MainMenuImageEventListenerNumberTotal = 200
local MainMenuImageEventListenerNonRepeatNumber = 0
local LSMenuOverlayImageImage
local LSMenuOverlayImage2Image
local LSMainMenuDigit = 0
local LSMainMenuLastDigitMusic = 0
local LSMainMenuLastDigitMusic1 = 0
local LSMainMenuLastDigitMusic2 = 0
local LSMainMenuLastDigitDisco = 0
local LSMainMenuLastDigitDisco1 = 0
local LSMainMenuLastDigitDisco2 = 0
local MainMenuOriginalVolume
]]--

--[[
function MainMenuMusicEventListener()

	if LSMenuplay ~= 0 and LSMenuremoved == false then 
	LSMenuOverlayImageImage.mouseover = true
	MainMenuImageEventListenerNumberCount = MainMenuImageEventListenerNumberCount + 1
		if MainMenuImageEventListenerNumberCount >= MainMenuImageEventListenerNumberTotal then
		
		local limit = 35--starts at 0 and ends at (last number - 1) - so if you have pictures from 0 up to 30 then limit should be set as 31
		
			MainMenuImageEventListenerNumberCount = 0
		
			LSMainMenuDigit = ZombRand(limit)--starts at 0 and ends at (last number - 1)
				
				if LSMainMenuDigit == LSMainMenuLastDigitMusic then
					if LSMainMenuDigit == (limit - 1) then--in case it's the last picture we pick the one before it
					LSMainMenuDigit = LSMainMenuDigit - 1
					else--else we pick the one after it
					LSMainMenuDigit = LSMainMenuDigit + 1
					end
				end
				if LSMainMenuDigit == LSMainMenuLastDigitMusic1 then
					if LSMainMenuDigit == (limit - 1) then
					LSMainMenuDigit = LSMainMenuDigit - 1
					else
					LSMainMenuDigit = LSMainMenuDigit + 1
					end
				end
				if LSMainMenuDigit == LSMainMenuLastDigitMusic2 then
					if LSMainMenuDigit == (limit - 1) then
					LSMainMenuDigit = LSMainMenuDigit - 1
					else
					LSMainMenuDigit = LSMainMenuDigit + 1
					end
				end
				
				if (MainMenuImageEventListenerNonRepeatNumber == 0) or (MainMenuImageEventListenerNonRepeatNumber > 2) then
					LSMainMenuLastDigitMusic = LSMainMenuDigit
					MainMenuImageEventListenerNonRepeatNumber = 0
				elseif MainMenuImageEventListenerNonRepeatNumber == 1 then
					LSMainMenuLastDigitMusic1 = LSMainMenuDigit
				elseif MainMenuImageEventListenerNonRepeatNumber == 2 then
					LSMainMenuLastDigitMusic2 = LSMainMenuDigit
				end
				MainMenuImageEventListenerNonRepeatNumber = MainMenuImageEventListenerNonRepeatNumber + 1
				
				LSMenuScreenshotImage.texture = getTexture("media/ui/screenshots/music".. LSMainMenuDigit ..".png")

		end

	else
	--MainMenuImageEventListenerNumberFrame = 0
	LSMenuOverlayImageImage.mouseover = false
	MainMenuImageEventListenerNumberCount = 0
	LSMenuScreenshotImage.texture = getTexture("media/ui/screenshots/music0.png")
	Events.OnFETick.Remove(MainMenuMusicEventListener)
	end
end

function MainMenuMusicEventListener2()

	if LSMenuplay2 ~= 0 and LSMenuremoved == false then 
	LSMenuOverlayImage2Image.mouseover = true
	MainMenuImageEventListenerNumberCount = MainMenuImageEventListenerNumberCount + 1

		if MainMenuImageEventListenerNumberCount >= MainMenuImageEventListenerNumberTotal then
		
		local limit = 9--starts at 0 and ends at (last number - 1) - so if you have pictures from 0 up to 30 then limit should be set as 31
		
			MainMenuImageEventListenerNumberCount = 0
		
			LSMainMenuDigit = ZombRand(limit)--starts at 0 and ends at (last number - 1)
				
				if LSMainMenuDigit == LSMainMenuLastDigitDisco then
					if LSMainMenuDigit == (limit - 1) then--in case it's the last picture we pick the one before it
					LSMainMenuDigit = LSMainMenuDigit - 1
					else--else we pick the one after it
					LSMainMenuDigit = LSMainMenuDigit + 1
					end
				end
				if LSMainMenuDigit == LSMainMenuLastDigitDisco1 then
					if LSMainMenuDigit == (limit - 1) then
					LSMainMenuDigit = LSMainMenuDigit - 1
					else
					LSMainMenuDigit = LSMainMenuDigit + 1
					end
				end
				if LSMainMenuDigit == LSMainMenuLastDigitDisco2 then
					if LSMainMenuDigit == (limit - 1) then
					LSMainMenuDigit = LSMainMenuDigit - 1
					else
					LSMainMenuDigit = LSMainMenuDigit + 1
					end
				end
				
				if (MainMenuImageEventListenerNonRepeatNumber == 0) or (MainMenuImageEventListenerNonRepeatNumber > 2) then
					LSMainMenuLastDigitDisco = LSMainMenuDigit
					MainMenuImageEventListenerNonRepeatNumber = 0
				elseif MainMenuImageEventListenerNonRepeatNumber == 1 then
					LSMainMenuLastDigitDisco1 = LSMainMenuDigit
				elseif MainMenuImageEventListenerNonRepeatNumber == 2 then
					LSMainMenuLastDigitDisco2 = LSMainMenuDigit
				end
				MainMenuImageEventListenerNonRepeatNumber = MainMenuImageEventListenerNonRepeatNumber + 1
				
				LSMenuScreenshot2Image.texture = getTexture("media/ui/screenshots/disco".. LSMainMenuDigit ..".png")

		end
		
			--LSMainMenuDigit = ZombRand(4)
		
			--if MainMenuImageEventListenerNumberFrame == 0 then
				--LSMenuScreenshot2Image.texture = getTexture("media/ui/screenshots/disco".. LSMainMenuDigit ..".png")
			--elseif MainMenuImageEventListenerNumberFrame == 1 then
				--LSMenuScreenshot2Image.texture = getTexture("media/ui/screenshots/disco2.png")
			--elseif MainMenuImageEventListenerNumberFrame == 2 then
				--LSMenuScreenshot2Image.texture = getTexture("media/ui/screenshots/disco3.png")
			--elseif MainMenuImageEventListenerNumberFrame == 3 then
				--LSMenuScreenshot2Image.texture = getTexture("media/ui/screenshots/disco4.png")
			
			
			--elseif MainMenuImageEventListenerNumberFrame == 5 then
				--LSMenuScreenshot2Image.texture = getTexture("media/ui/screenshots/disco1.png")
			
			
			--end
			
		--	if MainMenuImageEventListenerNumberFrame == 5 then
		--	MainMenuImageEventListenerNumberFrame = 0
		--	else
		--	MainMenuImageEventListenerNumberFrame = MainMenuImageEventListenerNumberFrame + 1
		--	end
		--end

	else
	--MainMenuImageEventListenerNumberFrame = 0
	LSMenuOverlayImage2Image.mouseover = false
	MainMenuImageEventListenerNumberCount = 0
	LSMenuScreenshot2Image.texture = getTexture("media/ui/screenshots/disco0.png")
	Events.OnFETick.Remove(MainMenuMusicEventListener2)
	end
end
]]--
--[[
function LSMenuOverlay.onMouseMoveImage(x, y)
	if LSMenuplay == 0 then
	LSMenuplay = getSoundManager():playUISound("GuitarAcoustic06MyOldKentuckyHomeLOOP")
	--LSMenuScreenshotImage.fade:setFadeIn(true)
	--LSMenuScreenshotImage.fade:reset()
	LSMenuScreenshotFrameImage:setVisible(true)
	LSMenuScreenshotImage:setVisible(true)
	MainMenuOriginalVolume = tonumber(getSoundManager():getMusicVolume())
	getSoundManager():setMusicVolume(0)
	Events.OnFETick.Add(MainMenuMusicEventListener)	
	end
--LSMenuScreenshotImage.fade:update()

end

function LSMenuOverlay.onMouseMoveOutsideImage(x, y)
	if LSMenuplay ~= 0 then
	getSoundManager():stopUISound(LSMenuplay)
	LSMenuplay = 0
	--LSMenuScreenshotImage.fade:setFadeIn(false)
	LSMenuScreenshotFrameImage:setVisible(false)
	LSMenuScreenshotImage:setVisible(false)
	getSoundManager():setMusicVolume(MainMenuOriginalVolume)
	end
end

function LSMenuOverlay.onMouseMoveImage2(x, y)
	if LSMenuplay2 == 0 then
	LSMenuplay2 = getSoundManager():playUISound("slow4LOOP")
	LSMenuScreenshotFrameImage:setVisible(true)
	LSMenuScreenshot2Image:setVisible(true)
	MainMenuOriginalVolume = tonumber(getSoundManager():getMusicVolume())
	getSoundManager():setMusicVolume(0)
	Events.OnFETick.Add(MainMenuMusicEventListener2)	
	end
end

function LSMenuOverlay.onMouseMoveOutsideImage2(x, y)
	if LSMenuplay2 ~= 0 then
	getSoundManager():stopUISound(LSMenuplay2)
	LSMenuplay2 = 0
	LSMenuScreenshotFrameImage:setVisible(false)
	LSMenuScreenshot2Image:setVisible(false)
	getSoundManager():setMusicVolume(MainMenuOriginalVolume)
	end
end
]]--

function LSMenuOverlay.onResetLua(reason)
	local self = LSMainMenuOverlay.initialise

	if LSMenuremoved == true then
	return
	end

	if reason ~= "optionsChangedApplied" then
		LSMainMenuOverlay:reset()
	end
end

function LSMenuOverlay:initialise()
	ISPanel.initialise(self);
	
	self.LSMenuScreenshotFrame = ISImage:new((self:getWidth() / 2) - 400, (self:getHeight() / 2) - 700, 0, 0, getTexture("media/ui/frame.png"))
	--LSMenuScreenshotFrameImage = self.LSMenuScreenshotFrame
	self.LSMenuScreenshotFrame:initialise()
	self:addChild(self.LSMenuScreenshotFrame)
	self.LSMenuScreenshotFrame:setVisible(false)
	
	self.LSMenuScreenshot = ISImage:new((self:getWidth() / 2) - 395, (self:getHeight() / 2) - 675, 0, 0, getTexture("media/ui/screenshots/music0.png"))
	--LSMenuScreenshotImage = self.LSMenuScreenshot
	self.LSMenuScreenshot.texture = getTexture("media/ui/screenshots/music0.png")
	self.LSMenuScreenshot:initialise()
	self:addChild(self.LSMenuScreenshot)
	self.LSMenuScreenshot:setVisible(false)

	self.LSMenuScreenshot2 = ISImage:new((self:getWidth() / 2) - 395, (self:getHeight() / 2) - 675, 0, 0, getTexture("media/ui/screenshots/disco0.png"))
	self.LSMenuScreenshot2.texture = getTexture("media/ui/screenshots/disco0.png")
	self.LSMenuScreenshot2:initialise()
	self:addChild(self.LSMenuScreenshot2)
	self.LSMenuScreenshot2:setVisible(false)
	
	self.LSMenuOverlayImage = ISImage:new((self:getWidth() / 2) + 30, (self:getHeight() / 2), 30, 30, getTexture("media/ui/lifestylemusicupdate_icon.png"))
	self.LSMenuOverlayImage:setMouseOverText(getText("UI_mainscreen_MusicUpdate"))
	self.LSMenuOverlayImage:initialise()
	self:addChild(self.LSMenuOverlayImage)

	self.LSMenuOverlayImage2 = ISImage:new((self:getWidth() / 2) + 30, (self:getHeight() / 2) - 33, 30, 30, getTexture("media/ui/lifestyledancingupdate_icon.png"))
	self.LSMenuOverlayImage2:setMouseOverText(getText("UI_mainscreen_DiscoUpdate"))
	self.LSMenuOverlayImage2:initialise()
	self:addChild(self.LSMenuOverlayImage2)

	self.LSMenuOverlayImage3 = ISImage:new((self:getWidth() / 2) - 40, (self:getHeight() / 2) - 31, 60, 60, getTexture("media/ui/lifestylehomeupdate_icon.png"))
	self.LSMenuOverlayImage3:setMouseOverText(getText("UI_mainscreen_HomeUpdate"))
	self.LSMenuOverlayImage3:initialise()
	self:addChild(self.LSMenuOverlayImage3)



end


function LSMenuOverlay:destroy()
	if self ~= nil and self.LSMenuOverlayImage ~= nil and self.LSMenuOverlayImage2 ~= nil then
	UIManager.setShowPausedMessage(true);
	self:setVisible(false);
	self:removeFromUIManager();
	if UIManager.getSpeedControls() then
		UIManager.getSpeedControls():SetCurrentGameSpeed(1);
	end
	LSMenuremoved = true
	
	Events.OnMainMenuEnter.Remove(LSMenuOnEnter)	

	Events.OnResetLua.Remove(LSMenuOverlay.onResetLua);

	Events.OnResolutionChange.Remove(LSMenuOnDestroy)

	--Events.OnGameStart.Remove(LSMenuOnDestroy);

	Events.OnAcceptInvite.Remove(LSMenuOnDestroy);

	Events.OnSteamGameJoin.Remove(LSMenuOnDestroy);

	Events.OnJoypadBeforeDeactivate.Remove(LSMenuOnDestroy);

	Events.OnGameBoot.Remove(LSMenuOnGameBoot)
	
	end	
end

function LSMenuOverlay:reset()
	if self ~= nil and self.LSMenuOverlayImage ~= nil and self.LSMenuOverlayImage2 ~= nil then
	UIManager.setShowPausedMessage(true);
	self:setVisible(false);
	self:removeFromUIManager();
	if UIManager.getSpeedControls() then
		UIManager.getSpeedControls():SetCurrentGameSpeed(1);
	end
	LSMainMenuOverlay = nil
	LSMenuOnEnter()
	end	
end

function LSMenuOverlay:prerender()
	self:drawRect(0, 0, self.width, self.height, self.backgroundColor.a, self.backgroundColor.r, self.backgroundColor.g, self.backgroundColor.b);
	self:drawRectBorder(0, 0, self.width, self.height, self.borderColor.a, self.borderColor.r, self.borderColor.g, self.borderColor.b);

end

function LSMenuOverlay:render()

end

local function enableImgMenu(frame, img, sound, theme)
	local menuVol = tonumber(getSoundManager():getMusicVolume())
	frame:setVisible(true)
	img.texture = getTexture("media/ui/screenshots/"..theme.."0.png")
	img:setVisible(true)
	getSoundManager():setMusicVolume(0)
	return getSoundManager():playUISound(sound), menuVol
end

local function disableImgMenu(frame, imgs, sound, oV)
	getSoundManager():stopUISound(sound)
	frame:setVisible(false)
	imgs[1]:setVisible(false)
	imgs[2]:setVisible(false)
	getSoundManager():setMusicVolume(oV)
end

local function checkForRepeats(limit,lastImgDigits)
	local isRepeat, digit = false, ZombRand(limit)--starts at 0 and ends at (last number - 1)	
	for n=1, #lastImgDigits do
		if lastImgDigits[n] == digit then isRepeat = true; break; end
	end
	if isRepeat then
		for n=1, (limit-1) do
			if (n ~= lastImgDigits[1]) and (n ~= lastImgDigits[2]) and (n ~= lastImgDigits[3]) then digit = n; break; end
		end
	end

	return digit
end

local function getNewDigitPos(digitPos)
	local newDigitPos = digitPos+1
	if newDigitPos > 3 then newDigitPos = 1; end
	return newDigitPos
end

local function getNewDigitTable(digit, digitPos, lastImgDigits)
	local n1,n2,n3 = false,false,false
	for n=1, 3 do
		local getDigit = digit
		if (n ~= digitPos) then
			getDigit = lastImgDigits[n]
		end
		if not n1 then n1 = getDigit;
		elseif not n2 then n2 = getDigit;
		elseif not n3 then n3 = getDigit;
		end
	end
	return {n1,n2,n3}
end

local function playNextImg(menuFocus, limit, menuTheme, lastImgDigits, digitPos)

	local digit = checkForRepeats(limit,lastImgDigits)
	local newDigitPos = getNewDigitPos(digitPos)
	local newDigitTable = getNewDigitTable(digit, newDigitPos, lastImgDigits)

	menuFocus.texture = getTexture("media/ui/screenshots/"..menuTheme..digit..".png")

	return newDigitTable, newDigitPos
end

local function getPianoSong(oldSound)
	local pianoSongs = {"Piano05AboutStrange","Piano05Etude","Piano10ChildrensCorner","Piano09PreludeInF","Piano06WaltzInBbMajor","Piano06Waltz","Piano07PineAppleRag"}
	if oldSound then
		for n=1, #pianoSongs do
			if pianoSongs[n] == oldSound then table.remove(pianoSongs, n); break; end
		end
	end
	local getSongIdx = ZombRand(#pianoSongs)+1

	return pianoSongs[getSongIdx]
end

function LSMenuOverlay:update()

	if self.LSMenuOverlayImage:isMouseOver() and (self.menuPlay == 0) then
		self.menuTheme = "music"
		self.menuPlay, self.menuOV = enableImgMenu(self.LSMenuScreenshotFrame,self.LSMenuScreenshot,"GuitarAcoustic06MyOldKentuckyHomeLOOP",self.menuTheme)
	elseif self.LSMenuOverlayImage2:isMouseOver() and (self.menuPlay == 0) then
		self.menuTheme = "disco"
		self.menuPlay, self.menuOV = enableImgMenu(self.LSMenuScreenshotFrame,self.LSMenuScreenshot,"slow4LOOP",self.menuTheme)--
	elseif self.LSMenuOverlayImage3:isMouseOver() and (self.menuPlay == 0) then
		self.menuTheme = "home"
		self.soundName = getPianoSong(self.soundName)
		self.menuPlay, self.menuOV = enableImgMenu(self.LSMenuScreenshotFrame,self.LSMenuScreenshot, self.soundName,self.menuTheme)--	
	
	elseif (self.menuPlay ~= 0) and (((not self.LSMenuOverlayImage:isMouseOver()) and (not self.LSMenuOverlayImage2:isMouseOver()) and (not self.LSMenuOverlayImage3:isMouseOver())) or
	(self.LSMenuOverlayImage:isMouseOver() and (self.menuTheme~="music")) or (self.LSMenuOverlayImage2:isMouseOver() and (self.menuTheme~="disco")) or (self.LSMenuOverlayImage3:isMouseOver() and (self.menuTheme~="home"))) then
		disableImgMenu(self.LSMenuScreenshotFrame,{self.LSMenuScreenshot,self.LSMenuScreenshot2},self.menuPlay,self.menuOV)
		self.menuPlay, self.delayCount, self.lastImgDigits, self.digitPos = 0, 0, {0,0,0}, 0
	elseif (self.menuPlay ~= 0) then
		if self.delayCount > self.delayTotal then
			self.delayCount = 0
			local menuFocus, limit
			--limit starts at 0 and ends at (last number - 1) - so if you have pictures from 0 up to 30 then limit should be set as 31
			if self.LSMenuOverlayImage:isMouseOver() then menuFocus, limit = self.LSMenuScreenshot, self.musicLimit
			elseif self.LSMenuOverlayImage2:isMouseOver() then menuFocus, limit = self.LSMenuScreenshot, self.discoLimit--
			elseif self.LSMenuOverlayImage3:isMouseOver() then menuFocus, limit = self.LSMenuScreenshot, self.homeLimit
			end
			if menuFocus and limit then self.lastImgDigits, self.digitPos = playNextImg(menuFocus, limit, self.menuTheme, self.lastImgDigits, self.digitPos); end
		else
			self.delayCount=self.delayCount+1
		end
	end
 
	if (self.menuTheme == "home") and self.LSMenuOverlayImage3:isMouseOver() and (self.menuPlay ~= 0) and (not getSoundManager():isPlayingUISound(self.menuPlay)) then
		self.soundName = getPianoSong(self.soundName)
		self.menuPlay = getSoundManager():playUISound(self.soundName)
	end


	ISPanelJoypad.update(self)
end

function LSMenuOverlay:new()
    local ScaleX = getCore():getScreenWidth() / 1920
	local ScaleY = getCore():getScreenHeight() / 1080
	local x = ScaleX * 1450
	local y = ScaleY * 990
	local width = 200
	local height = 80
	local o = {}
	o = ISPanelJoypad:new(x, y, width, height);
	setmetatable(o, self)
    self.__index = self
	o.name = nil;
    o.backgroundColor = {r=0, g=0, b=0, a=0};
    o.borderColor = {r=0, g=0, b=0, a=0};
	o.y = y
	o.x = x
	o.width = width;
	o.height = height;
	o.anchorLeft = true;
	o.anchorRight = true;
	o.anchorTop = true;
	o.anchorBottom = true;
	o.target = target;
	o.DJSoundboardTexture = getTexture("DJBooth_Overlay0.png")
	o:noBackground()
	o.menuPlay = 0
	o.menuOV = 0
	o.menuTheme = "music"
	o.delayCount = 0
	o.delayTotal = 40
	o.musicLimit = 46
	o.discoLimit = 29
	o.homeLimit = 37
	o.lastImgDigits = {0,0,0}
	o.digitPos = 0
	o.soundName = false
    o.new = new;
    return o;
end


function LSMenuOnEnter()
	if LSMenuremoved == true then
	return
	end
	if LSMainMenuOverlay ~= nil then
		LSMainMenuOverlay:reset()
	end
	if not LSMainMenuOverlay then
		LSMainMenuOverlay = LSMenuOverlay:new();
		LSMainMenuOverlay:initialise();
		LSMainMenuOverlay:addToUIManager();
	end
end

function LSMenuOnDestroy()
	if LSMenuremoved == true then
	return
	end
	if LSMainMenuOverlay then
		LSMainMenuOverlay:destroy()
	end

end

function LSMenuOnGameBoot()

	if LSMenuremoved == true then
	return
	end

Events.OnMainMenuEnter.Add(LSMenuOnEnter)	

Events.OnResetLua.Add(LSMenuOverlay.onResetLua);
--Events.OnMouseUp.Add(LSMenuOverlay.onResetLua);

Events.OnResolutionChange.Add(LSMenuOnDestroy)

--Events.OnMainMenuEnter.Add(LSMenuOverlay.destroy);

Events.OnGameStart.Add(LSMenuOnDestroy);

--Events.OnKeyPressed.Add(LSMenuOverlay.destroy);

--Events.OnKeyStartPressed.Add(LSMenuOverlay.destroy);

--Events.OnResetLua.Add(LSMenuOnDestroy);

Events.OnAcceptInvite.Add(LSMenuOnDestroy);

Events.OnSteamGameJoin.Add(LSMenuOnDestroy);

Events.OnJoypadBeforeDeactivate.Add(LSMenuOnDestroy);

end


Events.OnGameBoot.Add(LSMenuOnGameBoot)

