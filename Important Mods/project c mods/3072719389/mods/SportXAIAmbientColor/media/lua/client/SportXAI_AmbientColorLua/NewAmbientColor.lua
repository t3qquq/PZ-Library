local season_txt = {"summer","fall","winter","spring"};
local temp_txt = {"bright","normal","cloudy"};
local DuskColor_Outside = {1, 0.55, 0.3, 0.55} --R G B A
local DuskColor_Inside = {1, 0.55, 0.3, 0.15}
local DawnColor_Outside = {1, 0.7, 0.45, 0.45}
local DawnColor_Inside = {1, 0.7, 0.45, 0.25}
local NoMoon_Inside = Color.new(0.06, 0.06, 0.35, 0.1)
local NoMoon_Outside = Color.new(0.25, 0.25, 0.7, 0.2)
local sunshineE_Tex = getTexture("media/SportXAI_Sunshine/SunshineEast.png")
local sunshineW_Tex = getTexture("media/SportXAI_Sunshine/SunshineWest.png")
local sunshineE_Alpha = 0
local sunshineW_Alpha = 0
local screenWidth, screenHeight = getCore():getScreenWidth(), getCore():getScreenHeight()
--local Moon_Inside = Color.new(0.33, 0.33, 1, 0.2)
--local Moon_Outside = Color.new(0.12, 0.13, 0.4, 0.1)

local function NewAmbientColor()
	local climateManager = getClimateManager()
	for temp = 0, #temp_txt-1 do
		for season = 0, #season_txt-1 do

			if season ~= 2 then
				climateManager:setSeasonColorDawn(temp, season, DawnColor_Outside[1], DawnColor_Outside[2], DawnColor_Outside[3], DawnColor_Outside[4], true) --Outside
				climateManager:setSeasonColorDawn(temp, season, DawnColor_Inside[1], DawnColor_Inside[2], DawnColor_Inside[3], DawnColor_Inside[4], false) --Inside
				climateManager:setSeasonColorDusk(temp, season, DuskColor_Outside[1], DuskColor_Outside[2], DuskColor_Outside[3], DuskColor_Outside[4], true) --Outside
				climateManager:setSeasonColorDusk(temp, season, DuskColor_Inside[1], DuskColor_Inside[2], DuskColor_Inside[3], DuskColor_Inside[4], false) --Inside
			end

			climateManager:getColNightMoon():setExterior(NoMoon_Outside)
			climateManager:getColNightMoon():setInterior(NoMoon_Inside)
			climateManager:getColNightNoMoon():setExterior(NoMoon_Outside)
			climateManager:getColNightNoMoon():setInterior(NoMoon_Inside)
			climateManager:getColNight():setExterior(NoMoon_Outside)
			climateManager:getColNight():setInterior(NoMoon_Inside)
		end
	end
end
Events.OnLoad.Add(NewAmbientColor)

local function Sunshine()
	if not isIngameState() then return end

	local player = getSpecificPlayer(0)
	if player == nil then return end

	if not player:isOutside() then return end

	local speed = 0.003 * getGameSpeed()
	local climateManager = getClimateManager()
	local nowTime = getGameTime():getHour() + (getGameTime():getMinutes() / 60)
	local dawnTime,duskTime = climateManager:getSeason():getDawn(), climateManager:getSeason():getDusk() --Hour
	local intensityTable = {climateManager:getCloudIntensity(), climateManager:getFogIntensity(), 1 - climateManager:getAmbient()}
	local zoom = getCore():getZoom(player:getPlayerNum())

	zoom = PZMath.clampFloat(zoom, 0.0, 1.0)

	--Dawn time
	if nowTime >= dawnTime-1 and nowTime < dawnTime+3 then --6 am to 10 am
		sunshineE_Alpha = PZMath.lerp(sunshineE_Alpha, 1, speed)
	else
		sunshineE_Alpha = PZMath.lerp(sunshineE_Alpha, 0, speed)
	end

	--Dusk time
	if nowTime < duskTime+1 and nowTime > duskTime-3 then --6 pm to 8 pm
		sunshineW_Alpha = PZMath.lerp(sunshineW_Alpha, 1, speed)
	else
		sunshineW_Alpha = PZMath.lerp(sunshineW_Alpha, 0, speed)
	end

	UIManager.DrawTexture(sunshineW_Tex, 0, 0, screenWidth * zoom, screenHeight * zoom, sunshineW_Alpha - intensityTable[1] - intensityTable[2] - intensityTable[3])

	local offsetX = (screenWidth*zoom) - screenWidth
	local offsetY = (screenHeight*zoom) - screenHeight
	offsetX = math.abs(offsetX)
	offsetY = math.abs(offsetY)
	UIManager.DrawTexture(sunshineE_Tex, offsetX, offsetY, screenWidth * zoom, screenHeight * zoom, sunshineE_Alpha - intensityTable[1] - intensityTable[2] - intensityTable[3])
end
Events.OnPreUIDraw.Add(Sunshine)

local function SizeChange(n, n2, x, y)
	screenWidth = x
	screenHeight = y
end
Events.OnResolutionChange.Add(SizeChange)