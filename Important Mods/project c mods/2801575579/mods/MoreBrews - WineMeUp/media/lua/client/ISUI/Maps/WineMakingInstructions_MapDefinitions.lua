require 'Maps/ISMapDefinitions'

local function overlayPNG(mapUI, x, y, scale, layerName, tex, alpha)
	local texture = getTexture(tex)
	if not texture then return end
	local mapAPI = mapUI.javaObject:getAPIv1()
	local styleAPI = mapAPI:getStyleAPI()
	local layer = styleAPI:newTextureLayer(layerName)
	layer:setMinZoom(0)
	layer:addFill(0, 255, 255, 255, (alpha or 1.0) * 255)
	layer:addTexture(0, tex)
	layer:setBoundsInSquares(x, y, x + texture:getWidth() * scale, y + texture:getHeight() * scale)
end

LootMaps.Init.WineMakingInstructions = function(mapUI)

	local mapAPI = mapUI.javaObject:getAPIv1()
	MapUtils.initDirectoryMapData(mapUI, 'media/maps/Muldraugh, KY')
	mapAPI:setBoundsInSquares(17399, 0, 19799, 1499)
	overlayPNG(mapUI, 17399, 0, 1.0, "lootMapPNG", "media/ui/LootableMaps/WineMakingInstructions.png", 1.0)
end