WQS_Recipe = WQS_Recipe or {} 

--- Draws the extraction map and adds it to player inventory
--- Uses sendClientCommand to ensure proper synchronization in multiplayer
---@param craftRecipeData any
---@param player IsoPlayer
function WQS_Recipe.DrawExtractionMap(craftRecipeData, player) --b42
    -- function WQS_Recipe.DrawExtractionMap(recipe, result, player)
    --player:Say("DrawExtractionMap");
    --print("DrawExtractionMap player=" .. tostring(player))
    -- local CurrentMapId = string.lower(tostring(WQS.getCurretExtractionMap()))
    -- local is_modded_str="#modded_map#"
    -- local is_modded_map = CurrentMapId:find(is_modded_str, 1, true)

    if not (WQS_Shared.IsCurretExtractionOnModdedMap()) then
        --print("DrawExtractionMap: not modded map")
        local cmap = WQS.getExtractionData(WQS.getCurretExtractionMap())
        if cmap then
            --print("DrawExtractionMap: cmap=" .. tostring(cmap))
            local args = {
                mapId = tostring(WQS.getCurretExtractionMap()),
                mapLabel = cmap.MapGuiLabel
            }
            --print("DrawExtractionMap:SendClientCommand args=" .. tostring(args))
            WQS_Shared.SendClientCommand(player, "WQS_ServerSideModule", "WQS_AddExtractionMap", args)
            --sendClientCommand(player, "WQS_ServerSideModule", "WQS_AddExtractionMap", args)
        end
    else
        player:Say("I can't orient myself, a strange map to draw!")
    end
end

--- Generates a target repeater location and adds the map to player inventory
--- Uses sendClientCommand to ensure proper synchronization in multiplayer
---@param craftRecipeData any
---@param player any
function WQS_Recipe.GenerateTargetRepeater(craftRecipeData, player) --b42
    -- function WQS_Recipe.GenerateTargetRepeater(recipe, result, player) --b41
    print("GenerateTargetRepeater player=" .. tostring(player))
    if WQS_Shared.IsActiveRepeatersMode() then
        if WQSAntenna.CanAddMoreTargetRepeaters(player) then
            WQSAntenna.GenerateTargetRepeaterList(true, false, 1, player)
            player:Say(getText("IGUI_WQS_TargetAntennaRepeaterLocationAdded"))
        else
            player:Say(getText("IGUI_WQS_HaveAllTargetAntennaRepeaterLocations"))
        end

        -- if not (WQS_Shared.HaveItemInInventory(WQS_Shared.getRepeaterDynamicMapItemId(true), player)) then
        --     local args = {
        --         mapId = tostring(WQS_Shared.getRepeaterDynamicMapItemId(true)),
        --     }
        --     WQS_Shared.SendClientCommand(player, "WQS_ServerSideModule", "WQS_AddRepeaterMap", args)
        --     --sendClientCommand(player, "WQS_ServerSideModule", "WQS_AddRepeaterMap", args)
        -- end
    end
end

--[[ require "ISUI/ISCollapsableWindow"
require "ISUI/ISCollapsableWindow"


MyUI = ISCollapsableWindow:derive("MyUI");

function MyUI:initialise()
    ISCollapsableWindow.initialise(self);
    self:create();
end

function MyUI:prerender() -- Call before render, it's for harder stuff that need init, ect
    ISCollapsableWindow.prerender(self);
    self:drawText("Hello world",0,0,1,1,1,1, UIFont.Small); -- You can put it in render() too
end

function MyUI:render() -- Use to render text and other
end

function MyUI:create() -- Use to make the elements
end

function MyUI:new(x, y, width, height)
    local o = {};
    o = ISCollapsableWindow:new(x, y, width, height);
    setmetatable(o, self);
    self.__index = self;

    return o;
end

local myUI = MyUI:new(100, 100, 200, 200);
myUI:initialise();
 ]]


--[[
require "StashDescriptions/StashUtil";

WQS_Recipe = {}



function WQS_Recipe.RequestExtraction(recipe, result, player)
    --player:getInventory():AddItems("Base.BaseballBat", 1)
    --myUI:setVisible(true);
    --myUI:addToUIManager();


    local CurretExtractionMap=WQS.getCurretExtractionMap()
	local MapData=WQS.getExtractionData(CurretExtractionMap)

	if WQS.ThereIsErrorInExtractionData(MapData) then
		print(" #### WQS ERROR MAP INIT: "..MapItemName)
		return false
	end

    local distance = WQS_Shared.getDistance(player:getX(), player:getY(), MapData.MapCenterAreaX, MapData.MapCenterAreaY);

    if (distance>MapData.AreaRadiusFromCenter) then
        --player:Say("Too far! "..tostring(distance).." from "..CurretExtractionMap );
        player:Say("Too far from extraction zone! ("..tostring(distance)..")");
    end

    if (not(player:getZ()==MapData.MapCenterAreaZ) and (distance<MapData.AreaRadiusFromCenter*4) )then
        player:Say("The height level is wrong! "..tostring(player:getZ()).." -> "..MapData.MapCenterAreaZ );
    end

    local hours_left=WQS.getHoursLeftToDeadline()
    local in_time=true
    if (hours_left~=false) then
        if (hours_left>=0) then
            local hls=string.format("%.2f", hours_left)
            player:Say(tostring(hls).." hours left to reach extraction zone");
            in_time=true
        else
            player:Say("Too late baby, too late. ")
            in_time=false
        end
    end

    if ( (distance<=MapData.AreaRadiusFromCenter) and (player:getZ()==MapData.MapCenterAreaZ) and (in_time) )then
        player:Say("OK! Requesting Extraction "..tostring(distance).." -> "..CurretExtractionMap );
        testHelicopter();--dovrei fare una funzione simile mia invece di usare quella vanilla?
    end

    --per spawn item segnaposto cerca local newSprite = (IsoObject.new(getCell(), square, sprite_type));	


end

function WQS_Recipe.AskInstructionsExtraction(recipe, result, player)
    player:Say("ask extraction instructions here");


end

function WQS_Recipe.ReadInstructionsExtraction(recipe, result, player)
    player:Say("read extraction instructions here");
    --print(WQS.getExtractionData("WQS_item_list.wqs_map_louisville1").MapItem)
end




 ]]
