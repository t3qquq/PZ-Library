
MDFT = MDFT or {}

MDFT.MoreDescription = {
    Inventive = {
        {value = 1, text = "UI_trait_moredesc_levellower"},
    },
    Axeman = {
        {value = "25%", text = "UI_trait_moredesc_fasteraxeswing"},
        {value = "+50%", text = "UI_trait_moredesc_axetreedamage"},
    },
    Handy = {
        {value = "+100", text = "UI_trait_moredesc_HPtoallconstructionsexceptwalls"},
        {value = false, text = "UI_trait_moredesc_Fasterbuildingspeed"},
        {value = false, text = "UI_trait_moredesc_Fasterbarricadingspeed"},
    },
    SpeedDemon = {
        {value = "+100", text = "UI_trait_moredesc_gearswitchingspeed"},
        -- {value = "+15", text = "UI_trait_moredesc_acceleration"},
        {value = "+15", text = "UI_trait_moredesc_topspeed"},
        {value = "+200", text = "UI_trait_moredesc_engineloudnessreversing"},
        {value = false, text = "UI_trait_moredesc_increasedreversingspeed"},
        {value = "+15", text = "UI_trait_moredesc_grappleeffectiveness"},
    },
    SundayDriver = {
        {value = -40, text = "UI_trait_moredesc_acceleration"},
        {value = -30, text = "UI_trait_moredesc_reverseacceleration"},
        {value = "30", text = "UI_trait_moredesc_maxspeed"},
        {value = false, text = "UI_trait_moredesc_nochangetoengineloudness"},
    },
    PoorPassenger = {
        {value = "+30", text = "UI_trait_moredesc_motionsickness"},
    },
    Brave = {
        {value = -70, text = "UI_trait_moredesc_panicexceptnightterrors"},
        {value = -50, text = "UI_trait_moredesc_stressfromlootingcorpses"},
        {value = "+10", text = "UI_trait_moredesc_grappleeffectiveness"},
    },
    Cowardly = {
        {value = "+100", text = "UI_trait_moredesc_panicexceptnightterrors"},
        {value = "+100", text = "UI_trait_moredesc_stressfromlootingcorpses"},
        {value = -10, text = "UI_trait_moredesc_grappleeffectiveness"},
    },
    Clumsy = {
        {value = "+20", text = "UI_trait_moredesc_footstepsoundradius"},
        {value = "+10", text = "UI_trait_moredesc_chancetotripvaultingfence"},
        {value = false, text = "UI_trait_moredesc_increasedchanceoffallwhenbumping"},
        {value = false, text = "UI_trait_moredesc_increasedchanceofinjurywhenopeningcan"},
    },
    Graceful = {
        {value = "-40", text = "UI_trait_moredesc_footstepsoundradius"},
        {value = "-10", text = "UI_trait_moredesc_chancetotripvaultingfence"},
        {value = false, text = "UI_trait_moredesc_decreasedchancetotripfromlunge"},
        {value = false, text = "UI_trait_moredesc_decreasedchanceoffallwhenbumping"},
    },
    ShortSighted = {
        {value = false, text = "UI_trait_moredesc_alsogivesblurryvision"},
        {value = false, text = "UI_trait_moredesc_weaponsightsrangebonusatminimum"},
        {value = false, text = "UI_trait_moredesc_maluscanberemovedbywearingglasses"},
    },
    HardOfHearing = {
        {value = false, text = "UI_trait_moredesc_soundeffectsmuffled"},
        -- {value = false, text = "UI_trait_moredesc_zombiesbehindvisiblelater"}, 
    },
    Deaf = {
        {value = false, text = "UI_trait_moredesc_canthearsound"},
        {value = false, text = "UI_trait_moredesc_stillabletowatchtv"},
    },
    KeenHearing = {
        {value = "+50", text = "UI_trait_moredesc_perceptionradius"},
        {value = false, text = "UI_trait_moredesc_zombiesbehindvisibleearlier"},
    },
    EagleEyed = {
        {value = false, text = "UI_trait_moredesc_widerfieldofview"},
        {value = "+20", text = "UI_trait_moredesc_maxrangemodifieronweaponsights"},
    },
    HeartyAppitite = {
        {value = "+50", text = "UI_trait_moredesc_hunger"},
    },
    LightEater = {
        {value = -25, text = "UI_trait_moredesc_hunger"},
    },
    ThickSkinned = {
        {value = "+30", text = "UI_trait_moredesc_chanceofnotbeinginjuredbyzombies"},
        {value = -54, text = "UI_trait_moredesc_chanceofgettingscratchedcutbytrees"},
    },
    Unfit = {
        {value = 3, text = "UI_trait_moredesc_unfitlosecondition"},
        {value = 3, text = "UI_trait_moredesc_unfitgaincondition"},
    },
    ["Out of Shape"] = {
        {value = 5, text = "UI_trait_moredesc_outofshapelosecondition"},
        {value = 3, text = "UI_trait_moredesc_outofshapelosecondition2"},
        {value = 5, text = "UI_trait_moredesc_outofshapegaincondition"},
        {value = -1, text = "UI_trait_moredesc_speed"},
    },
    Fit = {
        {value = 5, text = "UI_trait_moredesc_fitlosecondition"},
        {value = 3, text = "UI_trait_moredesc_fitlosecondition2"},
        {value = 5, text = "UI_trait_moredesc_unfitgaincondition"},
    },
    Athletic = {
        {value = "+20", text = "UI_trait_moredesc_runningsprintingspeed"},
        {value = -20, text = "UI_trait_moredesc_runningsprintingenduranceloss"},
        {value = "+25", text = "UI_trait_moredesc_grappleeffectiveness"},
        {value = 9, text = "UI_trait_moredesc_athleticlosecondition"},
        {value = 9, text = "UI_trait_moredesc_athleticgaincondition"},
    },
    Nutritionist = {
        {value = false, text = "UI_trait_moredesc_includingnonpackagedandcookedfood"},
    },
    Nutritionist2 = {
        {value = false, text = "UI_trait_moredesc_includingnonpackagedandcookedfood"},
    },
    ["Weight Gain"] = {
        {value = false, text = "UI_trait_moredesc_currentlybugged"},
        {value = "< 90", text = "UI_trait_moredesc_highercalorythresholdtogainweight"},
        -- {value = 1280, text = "UI_trait_moredesc_currentlybugged"},

    },
    ["Weight Loss"] = {
        {value = "> 90", text = "UI_trait_moredesc_highercalorythresholdtogainweight"},
    },
    Overweight = {
        {value = "+10", text = "UI_trait_moredesc_chancetotripwhilerunvaulting"},
        {value = "+10", text = "UI_trait_moredesc_chancetotripfromlunge"},
        {value = false, text = "UI_trait_moredesc_increasedchanceoffallwhenbumping"},
        {value = false, text = "UI_trait_moredesc_increasedchancetofailatallfenceclimb"},
        {value = -30, text = "UI_trait_moredesc_enduranceregeneration"},
        {value = false, text = "UI_trait_moredesc_doubledendurancelosswhenrunning"},
        {value = "+20", text = "UI_trait_moredesc_falldamage"},
        {value = false, text = "UI_trait_moredesc_slowerropeclimbingspeed"},
        {value = "+10", text = "UI_trait_moredesc_grappleeffectiveness"},
        {value = 10, text = "UI_trait_moredesc_cannotgainfitnessxptowardslevel"},
        {value = false, text = ""},
        {value = 95, text = "UI_trait_moredesc_startingweight"},
        {value = 100, text = "UI_trait_moredesc_replacedbyobeseifweightgoesabove"},
        {value = 85, text = "UI_trait_moredesc_lostifweightgoesbelow"},
    },
    Underweight = {
        {value = -20, text = "UI_trait_moredesc_meleedamage"},
        {value = "+10", text = "UI_trait_moredesc_chancetotripfromlunge"},
        {value = false, text = "UI_trait_moredesc_increasedchanceoffallwhenbumping"},
        {value = false, text = "UI_trait_moredesc_increasedchancetofailatallfenceclimb"},
        {value = 10, text = "UI_trait_moredesc_cannotgainfitnessxptowardslevel"},
        {value = -10, text = "UI_trait_moredesc_grappleeffectiveness"},
        {value = false, text = ""},
        {value = 70, text = "UI_trait_moredesc_startingweight"},
        {value = 65, text = "UI_trait_moredesc_replacedbyveryunderweightifweightgoesbelow"},
        {value = 75, text = "UI_trait_moredesc_lostifweightgoesabove"},

    },
    Emaciated = {
        {value = -60, text = "UI_trait_moredesc_meleedamage"},
        {value = false, text = "UI_trait_moredesc_greatlyincreasedchancetofailatallfenceclimb"},

        {value = -70, text = "UI_trait_moredesc_enduranceregeneration"},
        {value = "+40", text = "UI_trait_moredesc_falldamage"},
        {value = false, text = "UI_trait_moredesc_cannotgainxptowardsfitnesslevel7orhigher"},
        {value = -60, text = "UI_trait_moredesc_grappleeffectiveness"},
        
        {value = false, text = ""},
        {value = 50, text = "UI_trait_moredesc_replacedbyveryunderweightifweightgoesabove"},
        {value = 35, text = "UI_trait_moredesc_characterwillstartlosinglife"},
    },
    ["Very Underweight"] = {
        {value = -40, text = "UI_trait_moredesc_meleedamage"},
        {value = "+20", text = "UI_trait_moredesc_chancetotripfromlunge"},
        {value = false, text = "UI_trait_moredesc_increasedchanceoffallwhenbumping"},
        {value = false, text = "UI_trait_moredesc_greatlyincreasedchancetofailatallfenceclimb"},
        {value = -30, text = "UI_trait_moredesc_enduranceregeneration"},
        {value = "+20", text = "UI_trait_moredesc_falldamage"},
        {value = false, text = "UI_trait_moredesc_cannotgainxptowardsfitnesslevel7orhigher"},
        {value = -40, text = "UI_trait_moredesc_grappleeffectiveness"},

        {value = false, text = ""},
        -- {value = 60, text = "UI_trait_moredesc_startingweight"},
        {value = 50, text = "UI_trait_moredesc_replacedbyemaciatedifweightgoesbelow"},
        {value = 65, text = "UI_trait_moredesc_replacedbyunderweightifweightgoesabove"},
    },
    Obese = {
        -- {value = 100, text = "UI_trait_moredesc_"},
        {value = "+20", text = "UI_trait_moredesc_chancetotripwhilerunvaulting"},
        {value = -10, text = "UI_trait_moredesc_chancetotripfromlunge"},
        {value = false, text = "UI_trait_moredesc_increasedchanceoffallwhenbumping"},
        {value = false, text = "UI_trait_moredesc_greatlyincreasedchancetofailatallfenceclimb"},
        {value = -60, text = "UI_trait_moredesc_enduranceregeneration"},
        {value = "+40", text = "UI_trait_moredesc_falldamage"},
        {value = -15, text = "UI_trait_moredesc_runningsprintingspeed"},
        {value = false, text = "UI_trait_moredesc_cannotgainxptowardsfitnesslevel7orhigher"},
        {value = "+5", text = "UI_trait_moredesc_grappleeffectiveness"},
        {value = false, text = ""},
        -- {value = 105, text = "UI_trait_moredesc_startingweight"},
        {value = 100, text = "UI_trait_moredesc_replacedbyoverweightifweightgoesbelow"},
    },
    Strong = {
        {value = "+40", text = "UI_trait_moredesc_knockbackpower"},
        {value = "+25", text = "UI_trait_moredesc_grappleeffectiveness"},
        
        {value = 9, text = "UI_trait_moredesc_canbegainedbytrainingstrengthtolevel"},
    },
    Stout = {
        {value = 6, text = "UI_trait_moredesc_canbegainedbytrainingstrengthtolevel"},
        {value = 9, text = "UI_trait_moredesc_replacedbystrongatlevel9strength"},
    },
    Weak = {
        {value = -40, text = "UI_trait_moredesc_knockbackpower"},
        {value = 2, text = "UI_trait_moredesc_replacedbyfeebleatlevel2strength"},
    },
    Feeble = {
        {value = 5, text = "UI_trait_moredesc_canbelostbytrainingstrengthtolevel5"},
    },
    Resilient = {
        {value = -25, text = "UI_trait_moredesc_corpsesickness"},
        {value = -55, text = "UI_trait_moredesc_chanceofcatchingacold"},
        {value = -20, text = "UI_trait_moredesc_coldstrength"},
        {value = -50, text = "UI_trait_moredesc_coldprogressionspeed"},
        {value = -25, text = "UI_trait_moredesc_zombificationspeed"},
    },
    ProneToIllness = {
        {value = "+25", text = "UI_trait_moredesc_corpsesickness"},
        {value = "+70", text = "UI_trait_moredesc_chanceofcatchingacold"},
        {value = "+20", text = "UI_trait_moredesc_coldstrength"},
        {value = "+50", text = "UI_trait_moredesc_coldprogressionspeed"},
        {value = "+25", text = "UI_trait_moredesc_zombificationspeed"},
    },
    Agoraphobic = {},
    Claustophobic = {
        {value = 70, text = "UI_trait_moredesc_claustrophobic"},
    },
    Marksman = {
        {value = -40, text = "UI_trait_moredesc_windpenaltywhenaimingwithguns"},
        {value = "+20", text = "UI_trait_moredesc_accuracywithguns"},
        {value = "+10", text = "UI_trait_moredesc_critchancewithguns"},
        {value = false, text = "UI_trait_moredesc_betteraimingdelay"},
    },
    NightOwl = {
        {value = "+40", text = "UI_trait_moredesc_tirednessrecoveryratewhensleeping"},
        {value = false, text = "UI_trait_moredesc_doesnotwakeupwhenreaching0tiredness"},

        {value = false, text = "UI_trait_moredesc_needtosetanalarmtotakefulladvantage"},
    },
    Outdoorsman = {
        {value = false, text = "UI_trait_moredesc_greatlydecreasedchanceofscratchbytrees"},
        {value = -75, text = "UI_trait_moredesc_chanceofcatchingacold"},
        {value = -33, text = "UI_trait_moredesc_chanceofbreakingfirekindling"},
        {value = "100", text = "UI_trait_moredesc_lightsfiresfaster"},
        {value = -33, text = "UI_trait_moredesc_gunaimweatherpenalty"},
    },
    FastHealer = {
        {value = -20, text = "UI_trait_moredesc_severityofvehicleinjuries"},
        {value = -40, text = "UI_trait_moredesc_fractureseverity"},
        {value = false, text = "UI_trait_moredesc_allwoundshealmuchfaster"},
        {value = false, text = "UI_trait_moredesc_noeffectonexercisefatigue"},
    },
    FastLearner = {
        {value = "+30", text = "UI_trait_moredesc_xpforallskillsexcept"},
    },
    FastReader = {
        {value = "+30", text = "UI_trait_moredesc_readingspeed"},
    },
    AdrenalineJunkie = {
        {value = "+0.2", text = "UI_trait_moredesc_basespeedatstrongpanic"},
        {value = "+0.25", text = "UI_trait_moredesc_basespeedatextremepanic"},
        {value = false, text = "UI_trait_moredesc_stillcantgoabovemovespeedcap"},
    },
    Inconspicuous = {
        {value = false, text = "UI_trait_moredesc_ifusingthenewstealthsystem"},
        {value = -20, text = "UI_trait_moredesc_chanceofbeingspottedbyazombie"},
        {value = false, text = ""},
        {value = false, text = "UI_trait_moredesc_ifusingtheoldstealthsystem"},
        {value = -50, text = "UI_trait_moredesc_chanceofbeingspottedbyazombie"},
    },
    NeedsLessSleep = {
        {value = -30, text = "UI_trait_moredesc_tirednesslossratewhileawake"},
        {value = "+25", text = "UI_trait_moredesc_tirednessrecoveryratewhensleeping"},
        {value = -25, text = "UI_trait_moredesc_sleepduration"},
    },
    NightVision = {
        {value = "+10", text = "UI_trait_moredesc_percent"},
        {value = false, text = "UI_trait_moredesc_increasedminimumanbiantlight"},
        {value = false, text = "UI_trait_moredesc_reducedvisoinconemalusindarkness"},
    },
    Organized = {
        {value = "+30", text = "UI_trait_moredesc_percent"},
    },
    LowThirst = {
        {value = -50, text = "UI_trait_moredesc_thirst"},
    },
    Burglar = {
        {value = false, text = "UI_trait_moredesc_decreasedchanceoffailingatallfenceclimb"},
        {value = false, text = "UI_trait_moredesc_slightlyincreasedropclimbingspeed"},
    },
    SlowHealer = {
        {value = "+20", text = "UI_trait_moredesc_severityofvehicleinjuries"},
        {value = "+80", text = "UI_trait_moredesc_fractureseverity"},
        {value = false, text = "UI_trait_moredesc_allwoundshealmuchslower"},
        {value = false, text = "UI_trait_moredesc_noeffectonexercisefatigue"},
    },
    SlowLearner = {
        {value = -30, text = "UI_trait_moredesc_xpforallskillsexcept"},
    },
    SlowReader = {
        {value = -30, text = "UI_trait_moredesc_readingspeed"},
    },
    NeedsMoreSleep = {
        {value = "+30", text = "UI_trait_moredesc_tirednesslossratewhileawake"},
        {value = "+18", text = "UI_trait_moredesc_tirednessrecoveryratewhensleeping"},
        {value = -18, text = "UI_trait_moredesc_sleepduration"},
    },
    Conspicuous = {
        {value = false, text = "UI_trait_moredesc_ifusingthenewstealthsystem"},
        {value = "+20", text = "UI_trait_moredesc_chanceofbeingspottedbyazombie"},
        {value = false, text = ""},
        {value = false, text = "UI_trait_moredesc_ifusingtheoldstealthsystem"},
        {value = "+100", text = "UI_trait_moredesc_chanceofbeingspottedbyazombie"},
    },
    Disorganized = {
        {value = -30, text = "UI_trait_moredesc_percent"},
        {value = false, text = "UI_trait_moredesc_aftercraftingdoesntreturnitems"}
    },
    HighThirst = {
        {value = "+100", text = "UI_trait_moredesc_thirst"},
    },
    Illiterate = {
        {value = false, text = "UI_trait_moredesc_includingtextonmapsandcaloryvaluesonfood"},
        -- {value = false, text = "UI_trait_moredesc_cannotwriteeither"},
    },
    Insomniac = {
        {value = false, text = "UI_trait_moredesc_hardertostartsleeping"},
        {value = -50, text = "UI_trait_moredesc_tirednessrecoveryratewhensleeping"},
    },
    Pacifist = {
        {value = -25, text = "UI_trait_moredesc_xpforweaponskillsandaimingskill"},
    },
    Thinskinned = {
        {value = -23, text = "UI_trait_moredesc_chanceofnotbeinginjuredbyzombies"},
        {value = "+100", text = "UI_trait_moredesc_chanceofgettingscratchedcutbytrees"},
    },
    Dextrous = {
        {value = -50, text = "UI_trait_moredesc_inventorytransferringtime"},
        {value = -20, text = "UI_trait_moredesc_aimingdelaywithguns"},
        {value = false, text = "UI_trait_moredesc_decreasedchanceofjammingguns"},
        {value = false, text = "UI_trait_moredesc_decreasedchancewoundwhenopeningacan"},
    },
    AllThumbs = {
        {value = "+100", text = "UI_trait_moredesc_inventorytransferringtime"},
        {value = "+20", text = "UI_trait_moredesc_aimingdelaywithguns"},
        {value = false, text = "UI_trait_moredesc_increasedchanceofjammingguns"},
        {value = false, text = "UI_trait_moredesc_increasedchancewoundwhenopeningacan"},
    },
    Desensitized = {
        {value = -85, text = "UI_trait_moredesc_panicexceptnightterrors"},
        {value = false, text = "UI_trait_moredesc_doesntpanicfromzombiereanimating"},
        {value = false, text = "UI_trait_moredesc_nostressfromlootingzombies"},
    },
    WeakStomach = {
        {value = "+100", text = "UI_trait_moredesc_foodillnesschance"},
        {value = "+45", text = "UI_trait_moredesc_foodillnessduration"},
        {value = "+50", text = "UI_trait_moredesc_taintedwatermorepoisonous"},
    },
    IronGut = {
        {value = -50, text = "UI_trait_moredesc_foodillnesschance"},
        {value = -55, text = "UI_trait_moredesc_foodillnessduration"},
        {value = -50, text = "UI_trait_moredesc_taintedwatermorepoisonous"},
        {value = -50, text = "UI_trait_moredesc_raweggsnevercausefoodillness"},
    },
    Hemophobic = {
        {value = false, text = "UI_trait_moredesc_inventorytransferbloodyitemsfasterandstressfull"},
    },
    Asthmatic = {
        {value = "+42", text = "UI_trait_moredesc_endurancelosswhenrunningsprintingcarryingdragging"},
        {value = "+20", text = "UI_trait_moredesc_endurancelosswhenswinging"},
    },
    Gymnast = {
        {value = false, text = "UI_trait_moredesc_decreasedchanceoffailingatallfenceclimb"},
        {value = false, text = "UI_trait_moredesc_slightlyincreasedropclimbingspeed"},
    },
    -- lucky and unlucky have been disabled for some reason in b42?
    Lucky = {
        {value = "+10", text = "UI_trait_moredesc_loot"},
        {value = -5, text = "UI_trait_moredesc_chanceoffailingitemrepairs"},
        {value = false, text = "UI_trait_moredesc_decreasedchanceofinjurywhenopeningcan"},
        {value = "+10", text = "UI_trait_moredesc_grappleeffectiveness"},
    },
    Unlucky = {
        {value = -10, text = "UI_trait_moredesc_loot"},
        {value = "+5", text = "UI_trait_moredesc_chanceoffailingitemrepairs"},
        {value = false, text = "UI_trait_moredesc_increasedchanceofinjurywhenopeningcan"},
    },
    -- FirstAid = {},
    -- Fishing = {},
    -- Gardener = { },
    -- Jogger = {},
    -- Smoker = {},
    -- Tailor = {},
    -- Blacksmith = {},
    -- Blacksmith2 = {},
    -- Cook = {},
    -- Cook2 = {},
    -- Herbalist = {},
    -- WildernessKnowledge = {},
    -- Brawler = {},
    -- Formerscout = {},
    -- BaseballPlayer = {},
    -- Hiker = {},
    -- Hunter = {},
    -- Mechanics = {},
    -- Whittler = {},

    -- PROFESSIONS
    fireofficer = {
        {value = "10-20", text = "UI_prof_moredesc_startingregularityexercise"},
    },
    parkranger = {
        {value = "+30", text = "UI_prof_moredesc_movespeedintrees"},
    },
    securityguard = {
        {value = "7-12", text = "UI_prof_moredesc_startingregularityexercise"},
    },
    lumberjack = {
        {value = "+15", text = "UI_prof_moredesc_movespeedintrees"},
    },
    fitnessInstructor = {
        {value = "40-60", text = "UI_prof_moredesc_startingregularityexercise"},
    },
}



