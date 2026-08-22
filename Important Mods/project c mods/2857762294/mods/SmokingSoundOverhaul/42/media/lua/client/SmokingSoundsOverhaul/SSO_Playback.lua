require "SmokingSoundsOverhaul/SSO_Schedule"

SmokingSoundsOverhaul = SmokingSoundsOverhaul or {}
local SSO = SmokingSoundsOverhaul
SSO.Playback = {}
local PB = SSO.Playback

local MAX_FRAME = 0.10   -- clamp per-frame elapsed

local function now() return getTimestampMs() end

local function advance(st)
	local ms = now()
	local dt = (ms - (st.lastMs or ms)) / 1000.0
	st.lastMs = ms
	if dt < 0 then dt = 0 end
	if dt > MAX_FRAME then dt = MAX_FRAME end
	st.elapsed = st.elapsed + dt
end

-- draws/exhales use puff volume; everything else uses ignition volume
local function isPuffSound(name)
	return name:find("draw", 1, true) or name:find("exh", 1, true) or name:find("puff", 1, true)
end

local function fireDue(st, character)
	local em = character:getEmitter()
	if not em then return end
	while st.next <= #st.events and st.events[st.next].at <= st.elapsed do
		local name = st.events[st.next].sound
		local h = em:playSound(name)
		if h and h ~= 0 then
			st.handles[#st.handles + 1] = h
			local vol = isPuffSound(name) and st.puffVol or st.ignVol
			if vol and vol ~= 1.0 then
				pcall(function() em:setVolume(h, vol) end)
			end
		end
		st.next = st.next + 1
	end
end

-- estimate the action's total length in seconds from job progress
local function estimateTotal(action, st)
	if not action.getJobDelta then return nil end
	local ok, d = pcall(function() return action:getJobDelta() end)
	if not ok or not d or d <= 0.05 or st.elapsed < 0.30 then return nil end
	return st.elapsed / d
end

----------------------------------------------------------------------

function PB.start(action, cfg)
	local st = {
		events = {}, next = 1, elapsed = 0, lastMs = now(), handles = {},
		gender = cfg.gender, minGap = cfg.minGap, puffAt = nil, done = false,
		ignVol = cfg.ignVol or 1.0, puffVol = cfg.puffVol or 1.0,
	}
	action.sso = st

	local lit = 0
	if cfg.mode ~= "puffs" then
		local ev, l = SSO.Schedule.buildIgnition(cfg.family, cfg.gender, cfg)
		if ev then st.events, lit = ev, l end
	end
	st.puffAt = lit + SSO.FIRST_DRAW_GAP

	table.sort(st.events, function(a, b) return a.at < b.at end)
	fireDue(st, action.character)
end

function PB.tick(action)
	local st = action.sso
	if not st then return end
	advance(st)

	-- schedule the next puff lazily, when it comes due
	if st.puffAt and not st.done and st.elapsed >= st.puffAt then
		local p = SSO.Schedule.nextPuff(st.gender, st.puffAt, st.minGap, estimateTotal(action, st))
		if p then
			st.events[#st.events + 1] = { at = p.drawAt, sound = p.draw }
			st.events[#st.events + 1] = { at = p.exAt,   sound = p.exhale }
			table.sort(st.events, function(a, b) return a.at < b.at end)
			st.puffAt = p.nextAt
		else
			st.done = true   -- no room left
		end
	end

	fireDue(st, action.character)
end

function PB.stop(action)
	local st = action.sso
	if not st then return end
	local em = action.character and action.character:getEmitter()
	if em then
		for _, h in ipairs(st.handles) do
			if em:isPlaying(h) then action.character:stopOrTriggerSound(h) end
		end
	end
	action.sso = nil
end

-- Preview for the settings menu: plays 2D UI sounds (works with no player).
local preview = nil
local pumpUI = nil
local liveHandles = {}

local function stopLiveHandles()
	local sm = getSoundManager and getSoundManager()
	if sm and sm.stopUISound then
		for _, h in ipairs(liveHandles) do
			if h and h ~= 0 then pcall(function() sm:stopUISound(h) end) end
		end
	end
	liveHandles = {}
end

-- invisible UI element that pumps the preview each frame the menu is drawn
function PB.ensurePumpUI()
	if pumpUI or not ISUIElement then return end
	local el = ISUIElement:new(0, 0, 0, 0)
	el:initialise()
	el.prerender = function()
		if PB.onFrame then pcall(PB.onFrame) end
		PB.pump()
	end
	el.update = function() PB.pump() end
	el.render = function() end
	el:setVisible(true)
	el:addToUIManager()
	pumpUI = el
end

-- recreate it: the UI manager drops elements across state changes
function PB.resetPumpUI()
	if pumpUI then pcall(function() pumpUI:removeFromUIManager() end) end
	pumpUI = nil
	PB.ensurePumpUI()
end

function PB.previewEvents(events)
	PB.previewStop()
	if not events or #events == 0 then return end
	local sm = getSoundManager and getSoundManager()
	if not sm or not sm.playUISound then return end
	PB.resetPumpUI()
	preview = { events = events, next = 1, elapsed = 0, lastMs = now(), sm = sm }
end

function PB.isPreviewing() return preview ~= nil end

-- stops the preview and cuts any of its sounds still playing
function PB.previewStop()
	preview = nil
	stopLiveHandles()
end

function PB.pump()
	if not preview then return end
	advance(preview)
	while preview.next <= #preview.events and preview.events[preview.next].at <= preview.elapsed do
		local name = preview.events[preview.next].sound
		local h
		pcall(function() h = preview.sm:playUISound(name) end)
		if h and h ~= 0 then liveHandles[#liveHandles + 1] = h end
		preview.next = preview.next + 1
	end
	if preview.next > #preview.events then preview = nil end
end
