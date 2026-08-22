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

-- Inventions starting definitions

 -- player mod data only saves known inventions and current improvement levels if any and # of specials researched
 -- resources can be stored in nearby containers. Research UI checks both player inv and nearby containers (loot)

LSInventionDefs = {}

LSInventionDefs.Improvements = {}
--LSInventionDefs.Moveables = {}
LSInventionDefs.Items = {}
--LSInventionDefs.Weapons = {}
--LSInventionDefs.Clothing = {}

local params = {}

-- base resource costs, modified by sandbox
params.vvlow = 2
params.vlow = 5
params.low = 10
params.midlow = 15
params.mid = 20
params.midhigh = 30
params.high = 40
params.vhigh = 60
params.vvhigh = 90
params.ludicrous = 120
params.vludicrous = 200
params.vvludicrous = 300


-- machinery mixes elec, mechanics and sometimes metalwork
params[8] = { -- improvement level
	-- base skill and material required per improvement level
	skills = {
		electrical = {"Electricity",10,"Mechanics",8,"Maintenance",8},
		electricalHard = {"Electricity",10,"Mechanics",10,"Maintenance",10},
		
		machinery = {"Mechanics",10,"MetalWelding",8,"Electricity",8},
		machineryHard = {"Mechanics",10,"MetalWelding",10,"Electricity",10},
		
		metalwork = {"MetalWelding",10,"Mechanics",8},
		metalworkHard = {"MetalWelding",10,"Mechanics",10},

		plumbing = {"Maintenance",10,"Mechanics",8,"MetalWelding",8},
		plumbingHard = {"Maintenance",10,"Mechanics",10,"MetalWelding",10},
		
		woodwork = {"Woodwork",10,"Maintenance",8},
		woodworkHard = {"Woodwork",10,"Maintenance",10},
	},
	res = {
        electrical = {
            "Base.ElectronicsScrap", params.vhigh,
            "Base.ElectricWire", params.vvhigh,
            "Base.RadioReceiver", params.high,
            "Base.CarBattery2", params.midhigh,
            "Base.NoiseTrap", params.mid,
            "Base.MotionSensor", params.mid,
            "Base.TriggerCrafted", params.midlow,
            "Base.Remote", params.vlow
        },
        electricalHard = {
            "Base.ElectronicsScrap", params.vvhigh,
            "Base.ElectricWire", params.ludicrous,
            "Base.Glue", params.high,
            "Base.Amplifier", params.midhigh,
            "Base.CarBattery3", params.mid,
            "Base.MotionSensor", params.mid,
            "Base.NoiseTrap", params.mid,
            "Base.TimerCrafted", params.midlow,
            "Base.CordlessPhone", params.vlow
        },

        machinery = {
            "Base.ScrapMetal", params.vhigh,
            "Base.Screws", params.vvhigh,
            "Base.EngineParts", params.high,
            "Base.ModernSuspension3", params.midhigh,
            "Base.NormalBrake2", params.mid,
            "Base.Wire", params.mid,
            "Base.Hinge", params.midlow
        },
        machineryHard = {
            "Base.ScrapMetal", params.vvhigh,
            "Base.Screws", params.ludicrous,
            "Base.ElectronicsScrap", params.high,
            "Base.EngineParts", params.midhigh,
            "Base.ModernSuspension3", params.mid,
            "Base.DuctTape", params.midhigh,
            "Base.NormalBrake2", params.mid
        },

        metalwork = {
            "Base.MetalBar", params.vhigh,
            "Base.SmallSheetMetal", params.vvhigh,
            "Base.WeldingRods", params.high,
            "Base.SheetMetal", params.mid,
            "Base.ScrapMetal", params.midhigh,
            "Base.Hinge", params.mid,
            "Base.MetalPipe", params.midlow
        },
        metalworkHard = {
            "Base.MetalBar", params.vvhigh,
            "Base.SheetMetal", params.high,
            "Base.SmallSheetMetal", params.vvhigh,
            "Base.Hinge", params.midhigh,
            "Base.ScrapMetal", params.mid,
            "Base.MetalPipe", params.mid,
            "Base.MetalBar", params.midlow
        },

        plumbing = {
            "Base.MetalPipe", params.vhigh,
            "Base.DuctTape", params.high,
            "Base.MetalBar", params.midhigh,
            "Base.LeadPipe", params.mid,
            "Base.MetalPipe", params.mid,
            "Base.MetalBar", params.midlow
        },
        plumbingHard = {
            "Base.MetalPipe", params.vvhigh,
            "Base.MetalBar", params.high,
            "Base.DuctTape", params.high,
            "Base.LeadPipe", params.midhigh,
            "Base.MetalPipe", params.mid,
            "Base.MetalBar", params.midlow,
            "Base.LeadPipe", params.vlow
        },

        woodwork = {
            "Base.Plank", params.vhigh,
            "Base.Nails", params.vvhigh,
            "Base.Screws", params.high,
            "Base.Woodglue", params.midhigh,
            "Base.LeatherStrips", params.mid,
            "Base.Twine", params.mid,
            "Base.Wire", params.midlow
        },
        woodworkHard = {
            "Base.Plank", params.vvhigh,
            "Base.Nails", params.ludicrous,
            "Base.Woodglue", params.high,
            "Base.LeatherStrips", params.midhigh,
            "Base.Twine", params.mid,
            "Base.Screws", params.mid,
            "Base.Wire", params.midlow,
            "Base.CordlessPhone", params.vlow
        },
	},
}

params[6] = { -- improvement level
	-- base skill and material required per improvement level
	skills = {
		electrical = {"Electricity",8,"Mechanics",8,"Maintenance",6},
		electricalHard = {"Electricity",9,"Mechanics",8,"Maintenance",8},
		
		machinery = {"Mechanics",8,"MetalWelding",8,"Electricity",6},
		machineryHard = {"Mechanics",9,"MetalWelding",8,"Electricity",8},
		
		metalwork = {"MetalWelding",8,"Mechanics",6},
		metalworkHard = {"MetalWelding",9,"Mechanics",8},

		plumbing = {"Maintenance",8,"Mechanics",6,"MetalWelding",6},
		plumbingHard = {"Maintenance",9,"Mechanics",8,"MetalWelding",8},
		
		woodwork = {"Woodwork",8,"Maintenance",6},
		woodworkHard = {"Woodwork",9,"Maintenance",8},
	},
	res = {
        electrical = {
            "Base.ElectronicsScrap", params.midhigh,
            "Base.ElectricWire", params.high,
            "Base.RadioReceiver", params.mid,
            "Base.CarBattery1", params.midlow,
            "Base.TriggerCrafted", params.vlow,
            "Base.Remote", params.vlow
        },
        electricalHard = {
            "Base.ElectronicsScrap", params.high,
            "Base.ElectricWire", params.vhigh,
            "Base.Glue", params.midhigh,
            "Base.Amplifier", params.midlow,
            "Base.CarBattery2", params.mid,
            "Base.MotionSensor", params.vlow,
            "Base.NoiseTrap", params.vlow
        },

        machinery = {
            "Base.ScrapMetal", params.midhigh,
            "Base.Screws", params.high,
            "Base.EngineParts", params.mid,
            "Base.ModernSuspension2", params.mid,
            "Base.Wire", params.midlow,
            "Base.Hinge", params.vlow
        },
        machineryHard = {
            "Base.ScrapMetal", params.high,
            "Base.Screws", params.vhigh,
            "Base.ElectronicsScrap", params.midhigh,
            "Base.EngineParts", params.mid,
            "Base.NormalBrake2", params.midlow,
            "Base.ModernSuspension2", params.mid,
            "Base.DuctTape", params.midlow
        },

        metalwork = {
            "Base.MetalBar", params.midhigh,
            "Base.SmallSheetMetal", params.high,
            "Base.WeldingRods", params.mid,
            "Base.SheetMetal", params.midlow,
            "Base.ScrapMetal", params.midlow,
            "Base.Hinge", params.vlow
        },
        metalworkHard = {
            "Base.MetalBar", params.high,
            "Base.SheetMetal", params.midhigh,
            "Base.SmallSheetMetal", params.high,
            "Base.Hinge", params.mid,
            "Base.MetalPipe", params.midlow,
            "Base.ScrapMetal", params.mid
        },

        plumbing = {
            "Base.MetalPipe", params.midhigh,
            "Base.DuctTape", params.mid,
            "Base.MetalBar", params.mid,
            "Base.LeadPipe", params.midlow,
            "Base.MetalPipe", params.vlow
        },
        plumbingHard = {
            "Base.MetalPipe", params.high,
            "Base.MetalBar", params.midhigh,
            "Base.DuctTape", params.midhigh,
            "Base.LeadPipe", params.mid,
            "Base.MetalPipe", params.midlow,
            "Base.MetalBar", params.vlow
        },

        woodwork = {
            "Base.Plank", params.midhigh,
            "Base.Nails", params.high,
            "Base.Screws", params.mid,
            "Base.Woodglue", params.mid,
            "Base.LeatherStrips", params.midlow,
            "Base.Twine", params.midlow
        },
        woodworkHard = {
            "Base.Plank", params.high,
            "Base.Nails", params.vhigh,
            "Base.Woodglue", params.midhigh,
            "Base.LeatherStrips", params.mid,
            "Base.Twine", params.mid,
            "Base.Screws", params.midlow,
            "Base.Wire", params.vlow
        },
	},
}

params[4] = { -- improvement level
	-- base skill and material required per improvement level
	skills = {
		electrical = {"Electricity",7,"Mechanics",6,"Maintenance",4},
		electricalHard = {"Electricity",8,"Mechanics",7,"Maintenance",6},
		
		machinery = {"Mechanics",7,"MetalWelding",6,"Electricity",4},
		machineryHard = {"Mechanics",8,"MetalWelding",7,"Electricity",7},
		
		metalwork = {"MetalWelding",7,"Mechanics",5},
		metalworkHard = {"MetalWelding",8,"Mechanics",7},

		plumbing = {"Maintenance",7,"Mechanics",6,"MetalWelding",4},
		plumbingHard = {"Maintenance",8,"Mechanics",7,"MetalWelding",6},
		
		woodwork = {"Woodwork",7,"Maintenance",4},
		woodworkHard = {"Woodwork",8,"Maintenance",7},
	},
	res = {
        electrical = {
            "Base.ElectronicsScrap", params.mid,
            "Base.ElectricWire", params.midhigh,
            "Base.LightBulbGreen", params.midlow,
            "Base.RadioReceiver", params.midlow,
            "Base.Amplifier", params.vlow
        },
        electricalHard = {
            "Base.ElectronicsScrap", params.midhigh,
            "Base.ElectricWire", params.high,
            "Base.Glue", params.mid,
            "Base.RadioTransmitter", params.midlow,
            "Base.MotionSensor", params.vlow,
            "Base.CordlessPhone", params.vlow
        },

        machinery = {
            "Base.ScrapMetal", params.mid,
            "Base.Screws", params.midhigh,
            "Base.EngineParts", params.mid,
            "Base.ModernSuspension1", params.midlow,
            "Base.Wire", params.vlow
        },
        machineryHard = {
            "Base.ScrapMetal", params.midhigh,
            "Base.Screws", params.high,
            "Base.ElectronicsScrap", params.midhigh,
            "Base.EngineParts", params.mid,
            "Base.ModernSuspension1", params.midlow,
            "Base.NormalBrake2", params.vlow
        },

        metalwork = {
            "Base.MetalBar", params.mid,
            "Base.SmallSheetMetal", params.midhigh,
            "Base.WeldingRods", params.midlow,
            "Base.SheetMetal", params.vlow,
            "Base.ScrapMetal", params.vlow
        },
        metalworkHard = {
            "Base.MetalBar", params.midhigh,
            "Base.SheetMetal", params.mid,
            "Base.SmallSheetMetal", params.midhigh,
            "Base.Hinge", params.midlow,
            "Base.MetalPipe", params.vlow
        },

        plumbing = {
            "Base.MetalPipe", params.mid,
            "Base.DuctTape", params.midlow,
            "Base.MetalBar", params.midlow,
            "Base.LeadPipe", params.vlow
        },
        plumbingHard = {
            "Base.MetalPipe", params.midhigh,
            "Base.MetalBar", params.mid,
            "Base.DuctTape", params.mid,
            "Base.LeadPipe", params.midlow,
            "Base.MetalPipe", params.vlow
        },

        woodwork = {
            "Base.Plank", params.mid,
            "Base.Nails", params.midhigh,
            "Base.Screws", params.midlow,
            "Base.Woodglue", params.midlow,
            "Base.Twine", params.vlow
        },
        woodworkHard = {
            "Base.Plank", params.midhigh,
            "Base.Nails", params.high,
            "Base.Woodglue", params.mid,
            "Base.LeatherStrips", params.midlow,
            "Base.Twine", params.midlow,
            "Base.Screws", params.vlow
        },
	},
}

params[2] = { -- improvement level
	-- base skill and material required per improvement level
	skills = {
		electrical = {"Electricity",4,"Mechanics",3,"Maintenance",2},
		electricalHard = {"Electricity",7,"Mechanics",6,"Maintenance",5},
		
		machinery = {"Mechanics",4,"MetalWelding",3,"Electricity",2},
		machineryHard = {"Mechanics",7,"MetalWelding",6,"Electricity",6},
		
		metalwork = {"MetalWelding",4,"Mechanics",2},
		metalworkHard = {"MetalWelding",7,"Mechanics",6},

		plumbing = {"Maintenance",4,"Mechanics",3,"MetalWelding",2},
		plumbingHard = {"Maintenance",7,"Mechanics",5,"MetalWelding",5},
		
		woodwork = {"Woodwork",4,"Maintenance",2},
		woodworkHard = {"Woodwork",7,"Maintenance",5},
	},
	res = {
        electrical = {
            "Base.ElectronicsScrap", params.midlow,
            "Base.ElectricWire", params.low,
            "Base.LightBulb", params.vlow,
            "Base.Receiver", params.vlow
        },
        electricalHard = {
            "Base.ElectronicsScrap", params.mid,
            "Base.ElectricWire", params.midlow,
            "Base.Glue", params.midlow,
            "Base.RadioReceiver", params.vlow,
            "Base.DuctTape", params.vlow
        },

        machinery = {
            "Base.ScrapMetal", params.midlow,
            "Base.Screws", params.mid,
            "Base.ElectronicsScrap", params.midlow,
            "Base.EngineParts", params.vlow
        },
        machineryHard = {
            "Base.ScrapMetal", params.mid,
            "Base.Screws", params.midhigh,
            "Base.ElectronicsScrap", params.mid,
            "Base.EngineParts", params.midlow,
            "Base.DuctTape", params.vlow
        },

        metalwork = {
            "Base.MetalBar", params.midlow,
            "Base.SmallSheetMetal", params.midlow,
            "Base.WeldingRods", params.vlow
        },
        metalworkHard = {
            "Base.MetalBar", params.mid,
            "Base.SheetMetal", params.midlow,
            "Base.SmallSheetMetal", params.mid,
            "Base.Hinge", params.vlow
        },

        plumbing = {
            "Base.MetalPipe", params.midlow,
            "Base.DuctTape", params.low,
            "Base.LeadPipe", params.vlow
        },
        plumbingHard = {
            "Base.MetalPipe", params.mid,
            "Base.MetalBar", params.midlow,
            "Base.DuctTape", params.midlow,
            "Base.LeadPipe", params.vlow
        },

        woodwork = {
            "Base.Plank", params.midlow,
            "Base.Nails", params.mid,
            "Base.Screws", params.vlow
        },
        woodworkHard = {
            "Base.Plank", params.mid,
            "Base.Nails", params.midhigh,
            "Base.Woodglue", params.midlow,
            "Base.LeatherStrips", params.vlow
        },
	},
}

params[1] = { -- improvement level
	-- base skill and material required per improvement level
	skills = {
		electrical = {"Electricity",2,"Mechanics",1},
		electricalHard = {"Electricity",5,"Mechanics",4,"Maintenance",3},
		
		machinery = {"Mechanics",2,"MetalWelding",1},
		machineryHard = {"Mechanics",5,"MetalWelding",3,"Electricity",3},
		
		metalwork = {"MetalWelding",2},
		metalworkHard = {"MetalWelding",5,"Mechanics",4},

		plumbing = {"Maintenance",2,"Mechanics",1},
		plumbingHard = {"Maintenance",5,"Mechanics",3,"MetalWelding",3},
		
		woodwork = {"Woodwork",2},
		woodworkHard = {"Woodwork",5,"Maintenance",3},
	},
	res = {
		electrical = {
			"Base.ElectronicsScrap",params.low,
			"Base.ElectricWire",params.vlow
		},
		electricalHard = {
			"Base.ElectronicsScrap",params.mid,
			"Base.ElectricWire",params.midlow,
			"Base.Glue",params.low
		},
		machinery = {
			"Base.ScrapMetal",params.low,
			"Base.Screws",params.midlow,
			"Base.ElectronicsScrap",params.low
		},
		machineryHard = {
			"Base.ScrapMetal",params.mid,
			"Base.Screws",params.midhigh,
			"Base.ElectronicsScrap",params.midlow,
			"Base.DuctTape",params.low
		},
		metalwork = {
			"Base.MetalBar",params.vlow,
			"Base.SmallSheetMetal",params.low
		},
		metalworkHard = {
			"Base.MetalBar",params.midlow,
			"Base.SheetMetal",params.vlow,
			"Base.SmallSheetMeta",params.midlow
		},
		plumbing = {
			"Base.MetalPipe",params.vlow,
			"Base.DuctTape",params.vvlow
		},
		plumbingHard = {
			"Base.MetalPipe",params.midlow,
			"Base.MetalBar",params.vlow,
			"Base.DuctTape",params.low
		},
		woodwork = {
			"Base.Plank",params.low,
			"Base.Nails",params.midlow
		},
		woodworkHard = {
			"Base.Plank",params.mid,
			"Base.Nails",params.midhigh,
			"Base.Woodglue",params.vlow
		},
	},
}

--res list
--electrical - Base.ElectronicsScrap Base.ElectricWire Base.Receiver Base.DuctTape Base.LightBulb Base.RadioReceiver Base.RadioTransmitter Base.CarBattery1 Base.CarBattery2 Base.CarBattery3 Base.Amplifier Base.NoiseTrap Base.TriggerCrafted 
-- Base.MotionSensor Base.TimerCrafted Base.Aluminum Base.LightBulb Base.LightBulbGreen Base.Screws Base.TinCanEmpty Base.Wire Base.MetalPipe Base.CordlessPhone Base.Remote Base.Glue
--machinery - Base.ElectronicsScrap Base.ElectricWire Base.DuctTape Base.ScrapMetal Base.MetalPipe Base.Hinge Base.Screws Base.Wire Base.EngineParts Base.ModernSuspension1 Base.ModernSuspension2 Base.ModernSuspension3 Base.NormalBrake2
--metalwork - Base.MetalPipe Base.MetalBar Base.SheetMetal Base.SmallSheetMetal Base.WeldingRods Base.Hinge Base.ScrapMetal
--plumbing - Base.DuctTape Base.MetalPipe Base.LeadPipe Base.MetalBar
--woodwork - Base.Woodglue Base.Nails Base.Plank Base.LeatherStrips Base.Twine Base.Screws Base.Wire


params[3] = params[2]; params[5] = params[4]; params[7] = params[6]; params[9] = params[8]; params[10] = params[8]; params[11] = params[8];

---- INVENTIONS

---- HYGIENATOR

LSInventionDefs.Items.Hygienator = {
	-- data for moveables is copied over to item moveable data when created (after checking for player-made improvements)
	-- essential
	enabled = true, -- functional, discoverable and craftable
	discover = {"Art",4,"Mechanics",3,"MetalWelding",2,"Electricity",2,"Maintenance",1}, -- minimum skill to be able to invent this item, false means no skill req, must be at minimum the skill req for production - div is 2
	isMoveable = true, -- moveables item data is not persistent, so is added to moveables data
	costDefs = {{"machineryHard","plumbing"},2,1.5}, -- item and skill groups, skill div, production div (repair is half)
	costPenalty = 1, -- cost is multiplied by this number, increases with total number of improvements, improvement type and level / updated when produced
	-- common non-improvements
	isBroken = false, -- broken
	cooldown = false, -- current cooldown / in game hours
	-- specific non-improvements
	hygieneMax = 30,	
	-- repeatable improvements
	-- common
	efficiency = 0.1, -- improved hygiene and visual cleaning values
	cooldownTime = 48, -- cooldown between uses / in game hours
	waterUsage = {100, 50}, -- water used, cleanining liquid used / in units
	costDecrease = 1, -- penalty is divided by this number
	durability = {50,75}, -- reduces breakdown and failure (and puddle) chance
	standardization = 1, -- piece standardization, production cost and time are divided by this number
	-- special
	isPerfumed = false,
	-- non repeatable improvements
	-- common
	-- special
	noPlumbing = false, -- no water req
	selfPowered = false, -- no energy req
	isHeated = false, -- removes cold water debuff, adds hot water buff
	hasDryJet = false, -- won't make worn clothes and character wet
	hasHighPressureJet = false, -- clean worn clothes
	-- indirect improvements - multiplied by efficiency improvement
	efficiencyBase = {70,1,1},
	efficiencyMult = {7,0.1,0.1},
	-- cheats
	neverBreak = false,
	neverSpill = false,
	noCooldown = false,
}

--repeatable - table, each key represents a level, improvements is set to key value upon research
--result - research will always set improvement to result value and is treated as level 10 for research. repeatable must be nil
--special - how many researched improvements are required to enable this improvement {# of improvements, min level in each} and improvement is added to special list (limited research)
--customRes - table, custom resource followed by number
--customSkill - perk string, level is same as improvement level

LSInventionDefs.Improvements.Hygienator = {
	-- list of improvements and its research args / last arg of repeatables is omitted for players without the relevant ambition (for improvements with >2 repetitions)
	costDecrease = {repeatable={1.1,1.3,1.4,1.7,1.9,2.2,2.4,3,3.3,3.6}, defs="machinery"},
	efficiency = {repeatable={0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1}, defs="plumbing"},
	standardization = {repeatable={1.2,1.5,1.7,2.2,2.5,3}, defs="metalworkHard"},
	durability = {repeatable={{45,70}, {40,60}, {35,50}, {30,40}, {25,30}, {20,20}, {15,15}, {10,10}, {5,5}, {0,0}}, defs="metalwork", customSkill="Maintenance"},
	cooldownTime = {repeatable={44, 36, 30, 24, 18, 12, 8, 4}, defs="electrical"},
	waterUsage = {repeatable={{75,40}, {60,30}, {40,20}, {20,10}, {10,5}}, defs="plumbing"},
	isPerfumed = {special={2,3},repeatable={{level=0.2,buff=10}, {level=0.2,buff=25}, {level=0.6,buff=40}, {level=0.8,buff=60}}, defs="machineryHard", customRes={"Base.Perfume",5,"Base.GardeningSprayEmpty",1,"Base.TriggerCrafted",1}},
	-- non-repeatables use value from result when research is finished; they use improvement level 10 for skill and res defs
	noPlumbing = {special={3,5}, result=true, defs="plumbingHard", customRes={"Base.Charcoal",3}},
	selfPowered = {special={3,5}, result=true, defs="electricalHard", customRes={"Base.MotionSensor",1,"Base.Battery",2,"Base.CarBatteryCharger",1}},
	isHeated = {special={2,3}, result=true, defs="electricalHard", customRes={"Base.Hairspray",2,"Base.Lighter",5,"Base.Sparklers",1}},
	hasDryJet = {special={2,3}, result=true, defs="plumbingHard"},
	hasHighPressureJet = {special={3,5}, result=true, defs="plumbingHard"},
}

---- HARVESTER

LSInventionDefs.Items.Harvester = { --!
	-- copied over to item data when created (after checking for player-made improvements)
	-- essential
	enabled = true, -- functional, discoverable and craftable
	discover = {"Art",6,"Electricity",4,"Mechanics",3,"Maintenance",3,"MetalWelding",2}, -- minimum skill to be able to invent this item, false means no skill req - div is 1.4
	costDefs = {{"electricalHard","metalwork","machinery"},1.4,1}, -- item and skill groups, skill div, production div (repair is half)
	costPenalty = 1, -- cost is multiplied by this number, increases with total number of improvements, improvement type and level / updated when produced
	-- common non-improvements
	--isBroken = false, -- broken
	cooldown = false, -- current cooldown / in game hours
	-- specific non-improvements

	-- repeatable improvements
	-- common
	efficiency = 0.1, -- more humph improves work speed at the cost of fuel consumption
	sensors = {1,2}, -- better sensors improve range and maximum harvest number
	cooldownTime = 36, -- cooldown if machine overheats / in game hours
	fuelConsumption = 1, -- reduces fuel consumption / in units
	fuelContainer = {100,0}, -- how much fuel it can hold, increases weight / in units
	costDecrease = 1, -- penalty is divided by this number
	durability = {50,75}, -- reduces breakdown and overheat chance (rolls when stopping and completing)
	weight = 30, -- lighter materials reduce item encumbrance
	standardization = 1, -- piece standardization, production cost and time are divided by this number
	-- special
	-- non repeatable improvements
	-- common
	-- special
	silent = false, -- muffled vaccuum sound and won't draw nearby zombies

	-- indirect improvements - multiplied by efficiency improvement (removes base_)

}

LSInventionDefs.Improvements.Harvester = {
	-- list of improvements and its research args / last arg of repeatables is omitted for players without the relevant ambition (for improvements with >2 repetitions)
	costDecrease = {repeatable={1.1,1.3,1.4,1.7,1.9,2.2,2.4,3,3.3,3.6}, defs="metalwork"},
	efficiency = {repeatable={0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1}, defs="machinery"},
	standardization = {repeatable={1.2,1.5,1.7,2.2,2.5,3}, defs="metalworkHard"},
	sensors = {repeatable={{2,3}, {3,4}, {4,6}, {5,8}, {6,10}, {7,12}, {7,15}, {8,18}, {8,20}, {9,25}}, defs="electrical", customRes={"Base.EngineParts",2}},
	durability = {repeatable={{45,70}, {40,60}, {35,50}, {30,40}, {25,30}, {20,20}, {15,15}, {10,10}, {5,5}, {0,0}}, defs="metalwork", customSkill="Maintenance"},
	cooldownTime = {repeatable={32, 28, 24, 20, 16, 12, 8, 4, 2}, defs="machinery"},
	fuelConsumption = {repeatable={1.1,1.3,1.4,1.7,1.9,2.2,2.4,3,3.3,3.6}, defs="machinery"},
	fuelContainer = {repeatable={{45,3}, {40,5}, {35,7}, {30,10}, {25,15}, {20,18}}, defs="machineryHard", customRes={"Base.BigGasTank2",1}},
	weight = {repeatable={25,22,18,15,13,10,8,5}, defs="metalwork"},
	-- non-repeatables use value from result when research is finished; they use improvement level 10 for skill and res defs
	silent = {special={3,3}, result=true, defs="machineryHard", customRes={"Base.Pillow",3,"Base.ModernCarMuffler2",1}},
}

----

function getInventionDefinitions(level, def)
	if not level or not def or not params[level] or not params[level].skills[def] then return false; end
	local t = {
		reqSkills = params[level].skills[def],
		reqRes = params[level].res[def],
	}
	return t
end

function getInventionDefinitionsMult(level, defs, data, isRepair, isResearch)
	if not level or not defs or not params[level] then return false; end
	local t = {reqSkills={},reqRes={}}

	for n=1,#defs do
		local def = defs[n]
		for j=1,#params[level].skills[def] do
			local param = params[level].skills[def][j]
			if type(param) ~= "number" then
				local value = params[level].skills[def][j+1]
				local div = 1
				if not isResearch then
					local divMult = data['costDefs'][2]
					div = div*divMult
				end
				value = math.ceil(value/div)
				if not t.reqSkills[param] then t.reqSkills[param] = 0; end
				t.reqSkills[param] = math.max(t.reqSkills[param], value)
			end
		end
		for j=1,#params[level].res[def] do
			local param = params[level].res[def][j]
			if type(param) ~= "number" then
				local value = params[level].res[def][j+1]
				local div, mult = 1, data['costPenalty']/data['costDecrease']
				if not isResearch then
					div = data['standardization']
					local divMult = data['costDefs'][3]
					if isRepair then divMult = divMult*2; end
					div = div*divMult
				end
				value = math.ceil((value*mult)/div)
				if not t.reqRes[param] then t.reqRes[param] = 0; end
				t.reqRes[param] = math.ceil(t.reqRes[param]+value)
			end
		end
	end

	return t
end

function updateClientInventionDefs()
	local lsData = ModData.getOrCreate("LSDATA")
	if lsData and lsData["INVT"] then
		if lsData["INVT"].Items then
			for k, v in pairs(lsData["INVT"].Items) do
				if v and LSInventionDefs.Items[k] then
					for j, i in pairs(v) do
						LSInventionDefs.Items[k][j] = i;
					end
				end
			end
		end
		if lsData["INVT"].Improvements then
			for k, v in pairs(lsData["INVT"].Improvements) do
				if v and LSInventionDefs.Improvements[k] then
					for j, i in pairs(v) do
						LSInventionDefs.Improvements[k][j] = i;
					end
				end
			end
		end
	end	
end

Events.OnGameStart.Add(updateDefs)
