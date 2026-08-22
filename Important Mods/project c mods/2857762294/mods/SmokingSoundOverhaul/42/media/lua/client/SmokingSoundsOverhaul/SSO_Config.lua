require "SmokingSoundsOverhaul/SSO_Schedule"
require "SmokingSoundsOverhaul/SSO_Playback"

SmokingSoundsOverhaul = SmokingSoundsOverhaul or {}
local SSO = SmokingSoundsOverhaul
SSO.Config = {}
local Cfg = SSO.Config

SSO.enableScheduler = true   -- another mod can set false to take over playback

local MODE_ASSEMBLED, MODE_PUFFS, MODE_CLASSIC = 1, 2, 3

-- combo values: 1 = Random, 2/3/4 = 1/2/3
local DEFAULTS = {
	mode = MODE_ASSEMBLED, minGap = 4.0, lidClose = true, pipes = true,
	zippoFlicks = 1, matchStrikes = 1, lighterSet = 1, matchesSet = 1,
	ignVol = 100, puffVol = 100,   -- percent
}

local opts

local function build()
	if not PZAPI or not PZAPI.ModOptions then return nil end
	local o = PZAPI.ModOptions:create("SmokingSoundsOverhaul", "Smoking Sounds Overhaul")

	o:addDescription("Sounds are built from separate parts, so no two smokes sound the same.")

	local mode = o:addComboBox("mode", "Sound mode",
		"Assembled: built from parts. Puffs only: no lighting sounds. Classic: the old single clip.")
	mode:addItem("Assembled", true)
	mode:addItem("Puffs only (experimental)")
	mode:addItem("Classic (single clip)")

	o:addSeparator()
	o:addTitle("Lighting")

	local zippo = o:addComboBox("zippoFlicks", "Zippo flicks",
		"How many times the zippo flicks before it catches. Only one take does 3.")
	zippo:addItem("Random", true)
	zippo:addItem("1"); zippo:addItem("2"); zippo:addItem("3")

	local strikes = o:addComboBox("matchStrikes", "Match strikes",
		"How many times the match scrapes before it catches. Only one take does 3.")
	strikes:addItem("Random", true)
	strikes:addItem("1"); strikes:addItem("2"); strikes:addItem("3")

	o:addDescription("Specific sound -- lock one exact take instead of rotating.")

	local ls = o:addComboBox("lighterSet", "Lighter sound",
		"Random rotates the 3 zippo takes; pick one to lock it.")
	ls:addItem("Random", true)
	ls:addItem("1"); ls:addItem("2"); ls:addItem("3")

	local ms = o:addComboBox("matchesSet", "Matches sound",
		"Random rotates the 3 match takes; pick one to lock it.")
	ms:addItem("Random", true)
	ms:addItem("1"); ms:addItem("2"); ms:addItem("3")

	local ignVol = o:addSlider("ignVol", "Ignition volume %%", 0, 150, 5, DEFAULTS.ignVol,
		"Volume of the lighter/match sounds.")

	local lidClose = o:addTickBox("lidClose", "Lighter lid close", DEFAULTS.lidClose,
		"Play the zippo lid closing after lighting. Matches have no lid.")

	o:addSeparator()
	o:addTitle("Puffs")
	o:addDescription("Puff spacing below only stretches on long smokes (cigars, pipes, longer-smoke mods) -- a normal cigarette is short.")

	local minGap = o:addSlider("minGap", "Min seconds between puffs", 2.0, 12.0, 0.5, DEFAULTS.minGap,
		"Shortest gap between puffs. A long draw pushes the next one later.")

	local puffVol = o:addSlider("puffVol", "Puff volume %%", 0, 150, 5, DEFAULTS.puffVol,
		"Volume of the draws and exhales.")

	o:addSeparator()
	o:addTitle("Other")

	local pipes = o:addTickBox("pipes", "Cover pipes", DEFAULTS.pipes,
		"Pipes are silent in vanilla. On, they use the cigarette sounds.")

	o:addButton("reset", "Reset to defaults", "Put every setting back to default.",
		function() Cfg.resetDefaults() end)

	o:addSeparator()
	o:addTitle("Preview")
	o:addDescription("Preview only works from the main menu -- in-game the menu mutes these sounds. Volume sliders apply in-game, not to the preview.")

	local nowPlaying = o:addTextEntry("nowPlaying", "Now playing", "",
		"Shows what the last preview played, including its flick count.")
	if nowPlaying.setEnabled then pcall(function() nowPlaying:setEnabled(false) end) end

	local bZippo = o:addButton("previewZippo", "Preview zippo", "Hear a zippo light with your Zippo flicks setting. Main menu only.",
		function() Cfg.preview("lighter") end)
	local bMatch = o:addButton("previewMatch", "Preview matches", "Hear a match light with your Match strikes setting. Main menu only.",
		function() Cfg.preview("matches") end)
	local bStop = o:addButton("previewStop", "Stop preview", "Stop the preview currently playing.",
		function() SSO.Playback.previewStop(); Cfg.setReadout("") end)

	-- grey out only what the current mode cannot use
	local function setEnabled(opt, on)
		if opt and opt.setEnabled then pcall(function() opt:setEnabled(on) end) end
	end
	local function applyMode(idx)
		local assembled = (idx == MODE_ASSEMBLED)
		local puffs     = (idx == MODE_PUFFS)
		local classic   = (idx == MODE_CLASSIC)
		setEnabled(minGap,   assembled or puffs)
		setEnabled(zippo,    assembled)
		setEnabled(strikes,  assembled)
		setEnabled(ls,       assembled or classic)
		setEnabled(ms,       assembled or classic)
		setEnabled(ignVol,   assembled or classic)
		setEnabled(puffVol,  assembled or puffs)
		setEnabled(lidClose, assembled)
	end
	applyMode(mode.getValue and mode:getValue() or MODE_ASSEMBLED)

	-- recompute grey-out from current values (used after the panel loads saved values)
	Cfg.refreshEnabled = function()
		applyMode(mode.getValue and mode:getValue() or MODE_ASSEMBLED)
	end

	-- live callbacks: mode re-greys; combo/tickbox changes stop preview (sliders skipped)
	Cfg.attachLive = function()
		local function stopPrev() SSO.Playback.previewStop(); Cfg.setReadout("") end
		local function wireCombo(opt, extra)
			local el = opt and opt.element
			if el and el.setOnChange then
				pcall(function() el:setOnChange(nil, function(_, cb)
					if extra then extra(cb) end
					stopPrev()
				end) end)
			end
		end
		local function wireTick(opt)
			local el = opt and opt.element
			if el then pcall(function() el.changeOptionMethod = stopPrev end) end
		end
		wireCombo(mode, function(cb) applyMode((cb and cb.selected) or MODE_ASSEMBLED) end)
		wireCombo(zippo); wireCombo(strikes); wireCombo(ls); wireCombo(ms)
		wireTick(lidClose); wireTick(pipes)

		-- lay the three preview buttons on one row, clear of the Now playing field
		local ez, em, es = bZippo.element, bMatch.element, bStop.element
		local np = nowPlaying and nowPlaying.element
		if ez and em and es and ez.getX then
			pcall(function()
				local y = ez:getY()
				if np and np.getY then y = math.max(y, np:getY() + np:getHeight() + 6) end
				local x = ez:getX() + ez:getWidth() + 8
				ez:setY(y)
				em:setX(x); em:setY(y)
				es:setX(x + em:getWidth() + 8); es:setY(y)
			end)
		end
	end

	return o
end

local function option(id)
	if not opts then return nil end
	local ok, res = pcall(function() return opts:getOption(id) end)
	if ok then return res end
	return nil
end

local function value(id, fallback)
	local o = option(id)
	if not o or not o.getValue then return fallback end
	local ok, v = pcall(function() return o:getValue() end)
	if not ok or v == nil then return fallback end
	return v
end

-- live (unsaved) UI value of an option
local function liveValue(id, fallback)
	local o = option(id)
	local el = o and o.element
	if el then
		local ok, v = pcall(function()
			if el.getCurrentValue then return el:getCurrentValue() end
			if el.isSelected then return el:isSelected(1) end
			return el.selected
		end)
		if ok and v ~= nil then return v end
	end
	return value(id, fallback)
end

function Cfg.mode()     return value("mode", DEFAULTS.mode) end
function Cfg.minGap()   return value("minGap", DEFAULTS.minGap) end
function Cfg.lidClose() return value("lidClose", DEFAULTS.lidClose) == true end
function Cfg.pipes()    return value("pipes", DEFAULTS.pipes) == true end
function Cfg.ignVol()   return value("ignVol", DEFAULTS.ignVol) / 100.0 end
function Cfg.puffVol()  return value("puffVol", DEFAULTS.puffVol) / 100.0 end

-- nil (random) or 1/2/3, per family (Zippo flicks / Match strikes)
local function countFor(id, default)
	local i = value(id, default)
	if not i or i <= 1 then return nil end
	return i - 1
end
function Cfg.flicksFor(family)
	if family == "matches" then return countFor("matchStrikes", DEFAULTS.matchStrikes) end
	return countFor("zippoFlicks", DEFAULTS.zippoFlicks)
end

-- nil (random) or set number 1/2/3
local function setFor(id)
	local i = value(id, 1)
	if not i or i <= 1 then return nil end
	return i - 1
end
function Cfg.setNum(family)
	if family == "matches" then return setFor("matchesSet") end
	return setFor("lighterSet")
end

function Cfg.isClassic() return Cfg.mode() == MODE_CLASSIC end
function Cfg.isPuffsOnly() return Cfg.mode() == MODE_PUFFS end
function Cfg.modeName()
	local m = Cfg.mode()
	if m == MODE_CLASSIC then return "classic" end
	if m == MODE_PUFFS then return "puffs" end
	return "assembled"
end

-- update the "Now playing" readout in the menu
function Cfg.setReadout(text)
	if not opts then return end
	local op = opts:getOption("nowPlaying")
	if op and op.setValue then pcall(function() op:setValue(text or "") end) end
end

-- put every setting back to its default
function Cfg.resetDefaults()
	if not opts then return end
	for id, v in pairs(DEFAULTS) do
		local op = opts:getOption(id)
		if op and op.setValue then
			pcall(function()
				op:setValue(v)
				local el = op.element   -- sliders keep a separate value label setValue misses
				if el and el.label and el.label.setName then el.label:setName(tostring(v)) end
			end)
		end
	end
	SSO.Playback.previewStop()
	Cfg.setReadout("")
	if Cfg.refreshEnabled then Cfg.refreshEnabled() end
end

local function familyLabel(f) return (f == "matches") and "Matches" or "Lighter" end

-- preview a lighter or matches light with its own settings (main menu only)
function Cfg.preview(family)
	if getSpecificPlayer(0) then return end   -- muted in-game; button is greyed there
	family = (family == "matches") and "matches" or "lighter"
	local gender = "m"

	local function combo(id) local i = liveValue(id, 1); if not i or i <= 1 then return nil end; return i - 1 end
	local modeIdx  = liveValue("mode", DEFAULTS.mode)
	local modeName = (modeIdx == MODE_CLASSIC and "classic") or (modeIdx == MODE_PUFFS and "puffs") or "assembled"

	local setNum = combo(family == "matches" and "matchesSet" or "lighterSet")
	local flicks = combo(family == "matches" and "matchStrikes" or "zippoFlicks")

	if modeName == "classic" then
		local set = SSO.Lib.legacy[family] and SSO.Lib.legacy[family][gender]
		if set and #set > 0 then
			local idx = setNum or (ZombRand(#set) + 1)
			SSO.Playback.previewEvents({ { at = 0, sound = set[idx] } })
			Cfg.setReadout(familyLabel(family) .. " take " .. idx .. " (classic)")
		end
		return
	end

	SSO.Schedule.lastIgnition = nil
	local ev = SSO.Schedule.build({
		family = family, gender = gender, duration = 20.0,
		minGap = liveValue("minGap", DEFAULTS.minGap),
		lidClose = liveValue("lidClose", DEFAULTS.lidClose) == true,
		mode = modeName, setNum = setNum, flicks = flicks,
	})
	SSO.Playback.previewEvents(ev)

	local ig = SSO.Schedule.lastIgnition
	if not ig then
		Cfg.setReadout("Puffs only")
	else
		local n = ig.flicks or 0
		local unit = (ig.family == "matches") and "strike" or "flick"
		Cfg.setReadout(familyLabel(ig.family) .. " take " .. (ig.n or "?") ..
			" -- " .. n .. " " .. unit .. (n == 1 and "" or "s"))
	end
end

-- build once, whichever of OnGameBoot / immediate runs first
local function ensureBuilt()
	if not opts and PZAPI and PZAPI.ModOptions then opts = build() end
end
Events.OnGameBoot.Add(ensureBuilt)
ensureBuilt()

-- grey out Preview / Stop while in-game (muted there)
local function setPreviewEnabled(on)
	if not opts then return end
	for _, id in ipairs({ "previewZippo", "previewMatch", "previewStop" }) do
		local op = opts:getOption(id)
		if op and op.setEnabled then pcall(function() op:setEnabled(on) end) end
	end
end
-- wire the panel from our own UI element instead of wrapping a vanilla function, so SSO
-- stays out of other mods' error traces
local wiredTo, sawOptions
local function pollPanel()
	if not opts then return end
	local m = option("mode")
	local el = m and m.element
	if el and el ~= wiredTo then
		wiredTo = el
		if Cfg.refreshEnabled then Cfg.refreshEnabled() end
		if Cfg.attachLive then Cfg.attachLive() end
		Cfg.setReadout("")
	end
	-- stop only on a real open -> closed transition
	local mo = MainOptions and MainOptions.instance
	local vis = (mo and mo.getIsVisible and mo:getIsVisible()) and true or false
	if vis then
		sawOptions = true
	elseif sawOptions then
		sawOptions = false
		if SSO.Playback.isPreviewing() then
			SSO.Playback.previewStop()
			Cfg.setReadout("")
		end
	end
end
SSO.Playback.onFrame = pollPanel

if Events.OnMainMenuEnter then
	Events.OnMainMenuEnter.Add(function()
		SSO.Playback.resetPumpUI(); setPreviewEnabled(true); Cfg.setReadout("")
	end)
end
if Events.OnCreatePlayer then Events.OnCreatePlayer.Add(function() setPreviewEnabled(false) end) end
Events.OnGameBoot.Add(function() SSO.Playback.ensurePumpUI() end)
