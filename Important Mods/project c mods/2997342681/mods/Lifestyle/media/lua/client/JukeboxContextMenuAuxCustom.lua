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
--DEPRECATED
--[[
require "JukeboxContextMenu"
require "JukeboxContextMenuAux"
if getActivatedMods():contains('True Music_v.2.15') or getActivatedMods():contains('truemusic') or getActivatedMods():contains('truemusic[RF4]') or getActivatedMods():contains('truemusic[RF5]') then
	require "TCMusicDefenitions"
end

JukeboxMenu = JukeboxMenu or {}

local function getJukebox(worldobjects)
	local Jukebox, spriteName

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
						end
					
						if properties:Is("GroupName") then
							groupName = properties:Val("GroupName")
						end
					
						if properties:Is("CustomName") then
							customName = properties:Val("CustomName")
						end

						if customName == "Jukebox" then
							Jukebox = thisObject;
							spriteName = thisSpriteName;
						end
					end
				end
			end
		end
	end

	return Jukebox
end

local function LSJCMACgetUID()

	local UID, numb1, numb2, numb3, numb4

	numb1 = ZombRand(10,1000)
	numb2 = ZombRand(10,1000)
	numb3 = ZombRand(10,1000)
	numb4 = ZombRand(10,1000)

	UID = tostring(numb1.."-"..numb2.."-"..numb3.."-"..numb4)
	print("LSJCMACgetUID is "..UID)
	return UID
end

local function getAvailableGenres()
	local availableGenres = {{name="Disco",trait="disco"}, {name="RB",trait="rbsoul"}, {name="Metal",trait="metal"}, {name="Salsa",trait="salsa"}, {name="Pop",trait="pop"}, {name="Beach",trait="beach"},
	{name="Classical",trait="classical"}, {name="Country",trait="country"}, {name="Holiday",trait="holiday"}, {name="Jazz",trait="jazz"}, {name="Muzak",trait="muzak"}, {name="Rap",trait="rap"},
	{name="Reggae",trait="reggae"}, {name="Rock",trait="rock"}, {name="World",trait="world"}}

	return availableGenres
end

local function getLSPlayerPlaylist()
	return {
	{name="Playlist1",songs={}},
	{name="Playlist2",songs={}},
	{name="Playlist3",songs={}},
	{name="Playlist4",songs={}}
	}
end

JukeboxMenu.doBuildMenuCustom = function(player, context, worldobjects)

    if getCore():getGameMode()=="LastStand" then
        return;
    end
 
    local thisPlayer = getSpecificPlayer(player)

    if thisPlayer:getVehicle() then return; end
    
	local Jukebox = getJukebox(worldobjects)
	if not Jukebox then return end
	
	if not ((SandboxVars.ElecShutModifier > -1 and
	GameTime:getInstance():getNightsSurvived() < SandboxVars.ElecShutModifier) or
	Jukebox:getSquare():haveElectricity()) then return; end

	local switch = {"JukeboxSwitch01","JukeboxSwitch02","JukeboxSwitch03"}
	local idx = ZombRand(#switch) + 1
	local soundFile = switch[idx]

    local JukeboxData = Jukebox:getModData()
    if not JukeboxData.JukeboxID then
	JukeboxData.JukeboxID = {(tostring(Jukebox:getX()) .. "," .. tostring(Jukebox:getY()) .. "," .. tostring(Jukebox:getZ()))};
	Jukebox:transmitModData();
	end
    if not JukeboxData.OnOff then 
	JukeboxData.OnOff = {"off"};
	Jukebox:transmitModData();
	end

	if JukeboxData.OnOff == "on" then

---------------Custom/Addons
	local PlayJukeboxCustomTracks = require("TimedActions/PlayJukeboxCustomTracks")
				
	if #PlayJukeboxCustomTracks > 0 or ((getActivatedMods():contains('True Music_v.2.15') or getActivatedMods():contains('truemusic') or getActivatedMods():contains('truemusic[RF4]') or getActivatedMods():contains('truemusic[RF5]')) and GlobalMusic) then
			
		local buildOptionCustom = context:addOptionOnTop(getText("ContextMenu_Play_Group_StylesCustom"));
		buildOptionCustom.iconTexture = getTexture('media/ui/addon_icon.png')
		local parentMenuCustom = ISContextMenu:getNew(context);
		context:addSubMenu(buildOptionCustom, parentMenuCustom)

---------------genreCustomOptions
		local AvailableCustomTracks
		local PlayCustomOption
		local availableGenres = getAvailableGenres()
		for k,v in ipairs(availableGenres) do
			AvailableCustomTracks = require("JukeboxTracksCustom/"..v.name)
			if AvailableCustomTracks and (#AvailableCustomTracks) > 0 then
				local randomCustomNumber = ZombRand(#AvailableCustomTracks) + 1
				local randomCustomTrack = AvailableCustomTracks[randomCustomNumber]
				PlayCustomOption = parentMenuCustom:addOption(getText("ContextMenu_Play_Style_"..v.name), worldobjects, JukeboxMenu.onPlay, getSpecificPlayer(player), Jukebox, soundFile,  
				randomCustomTrack.sound, randomCustomTrack.length, randomCustomTrack.genre)	
				PlayCustomOption.iconTexture = getTexture('media/ui/Traits/trait_'..v.trait..'.png')
			end
		end

---------------CustomPlaylists
		local tooltipManage = ISToolTip:new(); tooltipManage:initialise(); tooltipManage:setVisible(false);
		local tooltipImport = ISToolTip:new(); tooltipImport:initialise(); tooltipImport:setVisible(false);
		local buildOptionPlaylistSubCustom = context:addOptionOnTop(getText("ContextMenu_PlayerPlaylist_Main"));
		buildOptionPlaylistSubCustom.iconTexture = getTexture('media/ui/myplaylists_icon.png')
		local subParentMenuCustom = ISContextMenu:getNew(context);
		context:addSubMenu(buildOptionPlaylistSubCustom, subParentMenuCustom)

		if not thisPlayer:getModData().LSJukeboxCustomPlaylist then thisPlayer:getModData().LSJukeboxCustomPlaylist = getLSPlayerPlaylist(); end
		--if not thisPlayer:getModData().LSJukeboxCustomPlaylistUniqueID then thisPlayer:getModData().LSJukeboxCustomPlaylistUniqueID = LSJCMACgetUID(); end	
	
		local ManagePlaylistCustomOption = subParentMenuCustom:addOption(getText("ContextMenu_PlayerPlaylist_Manage"),
		worldobjects,
		JukeboxMenu.onManagePlaylist,
		getSpecificPlayer(player),
		Jukebox,
		"manage");
		tooltipManage.description = getText("Tooltip_LSCPM_Manage"); ManagePlaylistCustomOption.toolTip = tooltipManage;
		ManagePlaylistCustomOption.iconTexture = getTexture('media/ui/gears_icon.png')

		local buildOptionPlaylistImportSubCustom = subParentMenuCustom:addOption(getText("ContextMenu_PlayerPlaylist_Import")); buildOptionPlaylistImportSubCustom.iconTexture = getTexture('media/ui/playlistimport_icon.png');
		local subPlaylistImportMenuCustom = subParentMenuCustom:getNew(subParentMenuCustom); context:addSubMenu(buildOptionPlaylistImportSubCustom, subPlaylistImportMenuCustom);
		tooltipImport.description = getText("Tooltip_LSCPM_Import"); buildOptionPlaylistImportSubCustom.toolTip = tooltipImport;
		local buildOptionPlaylistPlaySubCustom = subParentMenuCustom:addOption(getText("ContextMenu_PlayerPlaylist_Play")); buildOptionPlaylistPlaySubCustom.iconTexture = getTexture('media/ui/djbooth_icon.png');
		local subPlaylistPlayMenuCustom = subParentMenuCustom:getNew(subParentMenuCustom); context:addSubMenu(buildOptionPlaylistPlaySubCustom, subPlaylistPlayMenuCustom);

		local playlistIdx = 0
		local noPlaylist = true
		for k,v in ipairs(thisPlayer:getModData().LSJukeboxCustomPlaylist) do
			playlistIdx = playlistIdx+1
			if Jukebox:getModData().customPlaylist and (#Jukebox:getModData().customPlaylist > 1) then
				local SharePlaylistCustomOption = subPlaylistImportMenuCustom:addOption(getText("ContextMenu_PlayerPlaylist_ImportTo")..v.name, worldobjects, JukeboxMenu.onManagePlaylist, getSpecificPlayer(player), Jukebox, "share", playlistIdx)
				SharePlaylistCustomOption.iconTexture = getTexture('media/ui/playlist'..playlistIdx..'_icon.png')
			end
			if v.songs and (#v.songs) > 1 then
				noPlaylist = false
				local randomCustomNumber = ZombRand(#v.songs) + 1
				local randomCustomTrack = v.songs[randomCustomNumber]
				PlayCustomOption = subPlaylistPlayMenuCustom:addOption(v.name, worldobjects, JukeboxMenu.onPlay, getSpecificPlayer(player), Jukebox, soundFile,  
				randomCustomTrack, 0, "customPlaylist", v.songs)	
				PlayCustomOption.iconTexture = getTexture('media/ui/playlist'..playlistIdx..'_icon.png')
			end
		end
		
		if noPlaylist then buildOptionPlaylistPlaySubCustom.notAvailable = true; local tooltipNoPlaylist = ISToolTip:new(); tooltipNoPlaylist:initialise(); tooltipNoPlaylist:setVisible(false);
		tooltipNoPlaylist.description = " <RED>"..getText("Tooltip_LSCPM_NoPlaylists"); buildOptionPlaylistPlaySubCustom.toolTip = tooltipNoPlaylist; buildOptionPlaylistPlaySubCustom.iconTexture = getTexture('media/ui/djboothNo_icon.png') end
		
		-- ignore below
		if #thisPlayer:getModData().LSJukeboxCustomPlaylist > 1 then
			local randomPlaylistCustomTrack = thisPlayer:getModData().LSJukeboxCustomPlaylist[ZombRand(#thisPlayer:getModData().LSJukeboxCustomPlaylist)+1]
			local PlayPlaylistCustomOption = subParentMenuCustom:addOption(getText("ContextMenu_PlayerPlaylist_Play"),
			worldobjects,
			JukeboxMenu.onPlay,
			getSpecificPlayer(player),
			Jukebox,
			soundFile,  
			randomPlaylistCustomTrack.sound,
			0,
			"customPlaylist");	
			PlayPlaylistCustomOption.iconTexture = getTexture('media/ui/Traits/trait_world.png')
		end

---------------TMCustom
		
		if (getActivatedMods():contains('True Music_v.2.15') or getActivatedMods():contains('truemusic') or getActivatedMods():contains('truemusic[RF4]') or getActivatedMods():contains('truemusic[RF5]')) and GlobalMusic then

			local AvailableTMCustomTracks = {}

			for k,v in pairs(GlobalMusic) do
				AvailableTMCustomTracks[#AvailableTMCustomTracks + 1] = k
			end

			if #AvailableTMCustomTracks > 0 then

				local randomTMCustomNumber = ZombRand(#AvailableTMCustomTracks) + 1
				local randomTMCustomTrack = AvailableTMCustomTracks[randomTMCustomNumber]

				local PlayTMCustomOption = parentMenuCustom:addOption(getText("ContextMenu_Play_Style_TM"),
					worldobjects,
					JukeboxMenu.onPlay,
					getSpecificPlayer(player),
					Jukebox,
					soundFile,  
					randomworldCustomTrack,
					0,
					"tm");	
					PlayTMCustomOption.iconTexture = getTexture('media/ui/Traits/trait_world.png')
			end
		end
	end--custom

	
	end
end

Events.OnFillWorldObjectContextMenu.Add(JukeboxMenu.doBuildMenuCustom);
]]--