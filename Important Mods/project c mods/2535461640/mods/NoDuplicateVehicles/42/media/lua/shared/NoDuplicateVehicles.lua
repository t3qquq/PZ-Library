if VehicleZoneDistribution then -- check if the table exists for backwards compatibility
    -- Wrangler

    if getActivatedMods():contains("\\92jeepYJ") then

        VehicleZoneDistribution.medium.vehicles["Base.fr_je_wrangler_92"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_je_wrangler_92_offroad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.good.vehicles["Base.fr_je_wrangler_92"] = {index = -1, spawnChance = nil}
        VehicleZoneDistribution.good.vehicles["Base.fr_je_wrangler_92_offroad"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.good.vehicles["Base.fr_je_wrangler_92_jurassic"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_je_wrangler_92"] = {index = -1, spawnChance = 0}
    end

    -- Cherokee

    if getActivatedMods():contains("\\84jeepXJ") then
        VehicleZoneDistribution.medium.vehicles["Base.fr_je_cherokee_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_je_cherokee_93_offroad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.good.vehicles["Base.fr_je_cherokee_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.good.vehicles["Base.fr_je_cherokee_93_offroad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_je_cherokee_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_je_cherokee_93_offroad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.police.vehicles["Base.fr_je_cherokee_93_police"] = {index = -1, spawnChance = 0}
    end

    -- 85 Crown Vic

    if getActivatedMods():contains("\\91fordLTD") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_crownvic_85"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_crownvic_85_coupe"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_crownvic_85_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_crownvic_92"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_crownvic_85_taxi"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_crownvic_85_wagtaxi"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_crownvic_85"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_crownvic_85_coupe"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_crownvic_85_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_crownvic_85"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_crownvic_85_coupe"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_crownvic_85_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_crownvic_85"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_crownvic_85_coupe"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_crownvic_85_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_crownvic_85"] = {index = -1, spawnChance = nil}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_crownvic_85_coupe"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_crownvic_85_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_crownvic_85_taxi"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_crownvic_85_wagtaxi"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_crownvic_85"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_crownvic_85_coupe"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_crownvic_85_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_crownvic_85_taxi"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_crownvic_85_wagtaxi"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.police.vehicles["Base.fr_fo_crownvic_85_police"] = {index = -1, spawnChance = 0}
    end

    -- 92 Crown Vic

    if getActivatedMods():contains("\\92fordCVPI") then
        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_crownvic_92"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_crownvic_92"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.good.vehicles["Base.fr_fo_crownvic_92"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_crownvic_92"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.police.vehicles["Base.fr_fo_crownvic_92_police"] = {index = -1, spawnChance = 0}
    end

    -- Beetle

    if getActivatedMods():contains("\\63beetle") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_vw_beetle_72"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_vw_beetle_72"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_vw_beetle_72"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_vw_beetle_72"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_vw_beetle_72"] = {index = -1, spawnChance = 0}
    end

    -- Caprice

    if getActivatedMods():contains("\\85chevyCaprice") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_caprice_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_caprice_87_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_caprice_87_taxi"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_caprice_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_caprice_87_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_caprice_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_caprice_87_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_caprice_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_caprice_87_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.good.vehicles["Base.fr_ch_caprice_87"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_ch_caprice_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_ch_caprice_87_taxi"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_caprice_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_caprice_87_wag"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.police.vehicles["Base.fr_ch_caprice_87_police"] = {index = -1, spawnChance = 0}
    end

    -- S10

    if getActivatedMods():contains("\\88chevyS10") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_s10_91_ext_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_s10_91_ext_sb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_s10_91_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_s10_91_sb"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_ext_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_ext_offroadlb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_ext_offroadsb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_ext_sb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_offroadlb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_offroadsb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_s10_91_sb"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_ext_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_ext_offroadlb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_ext_offroadsb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_ext_sb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_offroadlb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_offroadsb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_s10_91_sb"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_ext_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_ext_offroadlb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_ext_offroadsb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_ext_sb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_offroadlb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_offroadsb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_s10_91_sb"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_ch_s10_91_ext_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_ch_s10_91_ext_sb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_ch_s10_91_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_ch_s10_91_sb"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_s10_91_ext_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_s10_91_ext_sb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_s10_91_lb"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_s10_91_sb"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.ranger.vehicles["Base.fr_ch_s10_91_ext_lb_ranger"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.ranger.vehicles["Base.fr_ch_s10_91_lb_ranger"] = {index = -1, spawnChance = 0}
    end

    -- F350

    if getActivatedMods():contains("\\93fordF350") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_f350_80"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_f350_80_quad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_f350_80"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_f350_80_offroad"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_f350_80_offroadquad"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_f350_80_quad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_f350_80"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_f350_80_offroad"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_f350_80_offroadquad"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_f350_80_quad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_f350_80"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_f350_80_offroad"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_f350_80_offroadquad"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_f350_80_quad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_f350_80"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_f350_80_quad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_f350_80"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_f350_80_quad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.farm.vehicles["Base.fr_fo_f350_80"] = {index = -1, spawnChance = 0}
    end

    -- F350 Ambulance

    if getActivatedMods():contains("\\90fordF350ambulance") then
        VehicleZoneDistribution.ambulance.vehicles["Base.fr_fo_f350_ambulance_80"] = {index = -1, spawnChance = 0}
    end

    -- Firebird Transam

    if getActivatedMods():contains("\\77firebird") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_po_transam_77"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_po_transam_77_ttop"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_po_transam_77"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_po_transam_77_ttop"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_po_transam_77_bandit"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_po_transam_77"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_po_transam_77_ttop"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_po_transam_77"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_po_transam_77_ttop"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.sport.vehicles["Base.fr_po_transam_77"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.sport.vehicles["Base.fr_po_transam_77_ttop"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_po_transam_77"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_po_transam_77_ttop"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_po_transam_77"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_po_transam_77_ttop"] = {index = -1, spawnChance = 0}
    end

    -- Volvo

    if getActivatedMods():contains("\\89volvo200") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_vo_240_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_vo_240_93_wagon"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_vo_240_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_vo_240_93_wagon"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_vo_240_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_vo_240_93_wagon"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_vo_240_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_vo_240_93_wagon"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_vo_240_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_vo_240_93_wagon"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_vo_240_93"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_vo_240_93_wagon"] = {index = -1, spawnChance = 0}
    end

    -- FordB700

    if getActivatedMods():contains("\\87fordB700") then
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_b700_schoolshort"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_b700_schoollong"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_b700_schoolshort"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_b700_schoollong"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_b700_schoolshort"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_b700_prisonlong"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_b700_schoollong"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_b700_schoolshort"] = {index = -1, spawnChance = 0}
    end

    -- Fire Truck

    if getActivatedMods():contains("\\90pierceArrow") then
        VehicleZoneDistribution.fire.vehicles["Base.fr_pi_engine_90_fire"] = {index = -1, spawnChance = 0}
    end

    -- Econoline Van

    if getActivatedMods():contains("\\86fordE150") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_econoline_86"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_fo_econoline_86_florist"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_fo_econoline_86"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_fo_econoline_86"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_econoline_86"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_fo_econoline_86_florist"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_fo_econoline_86"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_fo_econoline_86"] = {index = -1, spawnChance = 0}
    end

    -- Hmmvt M998

    if getActivatedMods():contains("\\92amgeneralM998") then
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ag_hmmwv_92_2d_mil"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ag_hmmwv_92_4d_mil"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.military.vehicles["Base.fr_ag_hmmwv_92_2d_mil"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.military.vehicles["Base.fr_ag_hmmwv_92_4d_mil"] = {index = -1, spawnChance = 0}
    end

    -- M35A2 Bed

    if getActivatedMods():contains("\\78amgeneralM35A2") then
        VehicleZoneDistribution.military.vehicles["Base.fr_ag_m35_88_mil"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ag_m35_88_mil"] = {index = -1, spawnChance = 0}
    end

    -- M49A2 Tanker

    if getActivatedMods():contains("\\78amgeneralM49A2C") then
        VehicleZoneDistribution.military.vehicles["Base.fr_ag_m49_88_mil"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ag_m49_88_mil"] = {index = -1, spawnChance = 0}
    end

    -- Blazer

    if getActivatedMods():contains("\\86chevyCUCV") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_ch_blazer_87"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_blazer_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trailerpark.vehicles["Base.fr_ch_blazer_87_offroad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_blazer_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.bad.vehicles["Base.fr_ch_blazer_87_offroad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_blazer_87"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.medium.vehicles["Base.fr_ch_blazer_87_offroad"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.junkyard.vehicles["Base.fr_ch_blazer_87"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_blazer_87"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ch_blazer_87_mil"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.police.vehicles["Base.fr_ch_blazer_87_police"] = {index = -1, spawnChance = 0}

        VehicleZoneDistribution.military.vehicles["Base.fr_ch_blazer_87_mil"] = {index = -1, spawnChance = 0}
    end

    -- Hmmwv

    if getActivatedMods():contains("\\92amgeneralM998") then
        VehicleZoneDistribution.military.vehicles["Base.fr_ag_hmmwv_92_4d_mil"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.military.vehicles["Base.fr_ag_hmmwv_92_2d_mil"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ag_hmmwv_92_4d_mil"] = {index = -1, spawnChance = 0}
        VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_ag_hmmwv_92_2d_mil"] = {index = -1, spawnChance = 0}
    end


    -- Hilux

    if getActivatedMods():contains("\\88toyotaHilux") then
        VehicleZoneDistribution.parkingstall.vehicles["Base.fr_to_hilux_83_lb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.parkingstall.vehicles["Base.fr_to_hilux_83_sb"] = {index = -1, spawnChance = 0}

	VehicleZoneDistribution.trailerpark.vehicles["Base.fr_to_hilux_83_lb"] = {index = -1, spawnChance = 0};
	VehicleZoneDistribution.trailerpark.vehicles["Base.fr_to_hilux_83_offroadlb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.trailerpark.vehicles["Base.fr_to_hilux_83_offroadsb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.trailerpark.vehicles["Base.fr_to_hilux_83_sb"] = {index = -1, spawnChance = 0}

	VehicleZoneDistribution.bad.vehicles["Base.fr_to_hilux_83_lb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.bad.vehicles["Base.fr_to_hilux_83_offroadlb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.bad.vehicles["Base.fr_to_hilux_83_offroadsb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.bad.vehicles["Base.fr_to_hilux_83_sb"] = {index = -1, spawnChance = 0}

	VehicleZoneDistribution.medium.vehicles["Base.fr_to_hilux_83_lb"] = {index = -1, spawnChance = 0};
	VehicleZoneDistribution.medium.vehicles["Base.fr_to_hilux_83_offroadlb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.medium.vehicles["Base.fr_to_hilux_83_offroadsb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.medium.vehicles["Base.fr_to_hilux_83_sb"] = {index = -1, spawnChance = 0}

	VehicleZoneDistribution.junkyard.vehicles["Base.fr_to_hilux_83_lb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.junkyard.vehicles["Base.fr_to_hilux_83_sb"] = {index = -1, spawnChance = 0}

	VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_to_hilux_83_lb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.trafficjamw.vehicles["Base.fr_to_hilux_83_sb"] = {index = -1, spawnChance = 0}

	VehicleZoneDistribution.farm.vehicles["Base.fr_to_hilux_83_lb"] = {index = -1, spawnChance = 0}
	VehicleZoneDistribution.farm.vehicles["Base.fr_to_hilux_83_sb"] = {index = -1, spawnChance = 0}
    end
end
