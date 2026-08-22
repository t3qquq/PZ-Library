require "SmokingSoundsOverhaul/SSO_Library"

SmokingSoundsOverhaul = SmokingSoundsOverhaul or {}
local SSO = SmokingSoundsOverhaul
SSO.Schedule = {}
local Sched = SSO.Schedule

SSO.LOOP = 3.75

SSO.FIRST_DRAW_GAP = 0.15
SSO.HOLD           = 0.15
SSO.PUFF_MARGIN    = 0.05
SSO.END_MARGIN     = 0.50

-- random pick, avoiding an immediate repeat per pool
local lastPick = {}

local function pick(pool, key)
	if not pool or #pool == 0 then return nil end
	if #pool == 1 then return pool[1] end
	local n = ZombRand(#pool) + 1
	if n == lastPick[key] then n = (n % #pool) + 1 end
	lastPick[key] = n
	return pool[n]
end

-- ignition item -> "lighter" / "matches" / nil
SSO.lighterTypes = {
	["Base.Lighter"] = true, ["Base.Lighter_Battery"] = true,
	["Base.LighterDisposable"] = true, ["Base.LighterBBQ"] = true,
	["SM.SMFoil_Lighter"] = true,
}
SSO.matchTypes = {
	["Base.Matches"] = true, ["Base.Matchbox"] = true, ["SM.Matches"] = true,
}

function Sched.familyOf(fullType)
	if not fullType then return nil end
	if SSO.lighterTypes[fullType] then return "lighter" end
	if SSO.matchTypes[fullType] then return "matches" end
	return nil
end

-- separable = one file per scrape (trimmable); fused = fewer files than scrapes
local function separable(s) return #s.files == s.flicks end

-- pick open / strike-seq / close; also returns playCount and audible scrape count
local function selectIgnition(family, gender, opts)
	local L = SSO.Lib
	local seqs = L.strikeSeqs[family]
	if not seqs or #seqs == 0 then return nil end

	local pool = {}
	for _, s in ipairs(seqs) do
		if not s.g or s.g == gender then pool[#pool + 1] = s end
	end
	if #pool == 0 then pool = seqs end

	local setNum = opts and opts.setNum
	if setNum then
		local locked = {}
		for _, s in ipairs(pool) do if s.n == setNum then locked[#locked + 1] = s end end
		if #locked > 0 then pool = locked end
	end

	local want = opts and type(opts.flicks) == "number" and opts.flicks or nil
	if want then
		-- prefer sequences that can produce exactly `want` (trimmed or naturally)
		local exact = {}
		for _, s in ipairs(pool) do
			if (separable(s) and want <= #s.files) or (not separable(s) and want == s.flicks) then
				exact[#exact + 1] = s
			end
		end
		if #exact > 0 then pool = exact end
		-- else keep pool: a pinned sound that cannot reach `want` is clamped below
	end

	local seq = pick(pool, "seq_" .. family)

	local playCount, audible
	if want and separable(seq) then
		playCount = math.min(want, #seq.files)   -- trim, clamped to what exists
		audible = playCount
	else
		playCount = #seq.files                    -- fused or random: whole sequence
		audible = seq.flicks
	end

	local function byNum(list, n)
		if not list then return nil end
		if n then
			local m = {}
			for _, e in ipairs(list) do if e.n == n then m[#m + 1] = e end end
			if #m > 0 then return m end
		end
		return list
	end
	local open  = pick(byNum(L.opens[family], setNum), "open_" .. family)
	local close = L.closes[family] and pick(byNum(L.closes[family], setNum), "close_" .. family)
	return open, seq, close, playCount, audible
end

function Sched.buildIgnition(family, gender, opts)
	local L = SSO.Lib
	if not L or not L.opens[family] or not L.strikeSeqs[family] then return nil, 0 end

	local wantClose = opts == nil or opts.lidClose ~= false
	local open, seq, close, playCount, audible = selectIgnition(family, gender, opts or {})
	if not open or not seq then return nil, 0 end
	playCount = playCount or #seq.files

	Sched.lastIgnition = { family = family, n = seq.n, flicks = audible or seq.flicks }

	local ev = { { at = 0, sound = open.s } }
	local t, lastEnd, litSound = open.d + seq.firstOff, 0, nil
	for i = 1, playCount do
		local f = seq.files[i]
		if i > 1 then t = t + (seq.gaps[i - 1] or 0.4) end
		ev[#ev + 1] = { at = t, sound = f.s }
		lastEnd, litSound = t + f.d, f.s
	end

	if wantClose and seq.closeAfter and close then
		ev[#ev + 1] = { at = lastEnd + seq.closeAfter, sound = close.s }
	end

	SSO_last_lighting_sound = litSound or ""   -- read by True Smoking via getLightingSound()
	return ev, lastEnd
end

-- draws are not gendered, so pool every take; only exhales are gendered
local function allDraws(L)
	if not L._allDraws then
		local a = {}
		for _, g in ipairs({ "m", "f" }) do
			for _, d in ipairs(L.draws[g] or {}) do a[#a + 1] = d end
		end
		L._allDraws = a
	end
	return L._allDraws
end

-- one puff (draw + exhale); returns nil if none fits in the time left
function Sched.nextPuff(gender, at, minGap, total)
	local L = SSO.Lib
	local draws, exhales = allDraws(L), L.exhales[gender]
	if not draws or #draws == 0 or not exhales or #exhales == 0 then return nil end

	local function shortest(pool)
		local best = pool[1]
		for _, e in ipairs(pool) do if e.d < best.d then best = e end end
		return best
	end

	local dr = pick(draws, "draw")
	local ex = pick(exhales, "exh_" .. gender)
	local exAt = at + dr.d + SSO.HOLD

	-- if the pick is too long to fit, try the shortest pair before giving up
	if total and (exAt + ex.d + SSO.END_MARGIN > total) then
		dr, ex = shortest(draws), shortest(exhales)
		exAt = at + dr.d + SSO.HOLD
		if exAt + ex.d + SSO.END_MARGIN > total then return nil end
	end

	-- next puff on the minGap grid, slid past this exhale so they never overlap
	local earliest = exAt + ex.d + SSO.PUFF_MARGIN
	local nextAt = at + minGap
	local guard = 0
	while nextAt < earliest and guard < 64 do
		nextAt = nextAt + minGap
		guard = guard + 1
	end

	return { drawAt = at, draw = dr.s, exAt = exAt, exhale = ex.s, nextAt = nextAt }
end

-- whole sequence for a known duration (used by Preview)
function Sched.build(opts)
	local ev, lit = {}, 0
	if opts.mode ~= "puffs" then
		local ie, il = Sched.buildIgnition(opts.family, opts.gender, opts)
		if ie then ev, lit = ie, il end
	end

	local at, guard = lit + SSO.FIRST_DRAW_GAP, 0
	while at < opts.duration and guard < 128 do
		guard = guard + 1
		local p = Sched.nextPuff(opts.gender, at, opts.minGap, opts.duration)
		if not p then break end
		ev[#ev + 1] = { at = p.drawAt, sound = p.draw }
		ev[#ev + 1] = { at = p.exAt,   sound = p.exhale }
		at = p.nextAt
	end

	table.sort(ev, function(a, b) return a.at < b.at end)
	return ev
end
