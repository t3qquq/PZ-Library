DST = DST or {}

DST.SkillTooltipDefinitions = DST.SkillTooltipDefinitions or {}
DST.AttractFishNumber = 100;
forageDefs = forageDefs or {}
function dump(o)
    if type(o) == 'table' then
       local s = '{ '
       for k,v in pairs(o) do
          if type(k) ~= 'number' then k = '"'..k..'"' end
          s = s .. '['..k..'] = ' .. dump(v) .. ','
       end
       return s .. '} '
    else
       return tostring(o)
    end
 end
 

function DST.getCarryCapacityForStrength(strengthLevel)
    if strengthLevel == 0 then return 6;
    elseif strengthLevel == 1 then return 7;
    elseif strengthLevel == 2 then return 8;
    elseif strengthLevel == 3 then return 9;
    elseif strengthLevel == 4 then return 11;
    elseif strengthLevel == 5 then return 12;
    elseif strengthLevel == 6 then return 14;
    elseif strengthLevel == 7 then return 15;
    elseif strengthLevel == 8 then return 16;
    elseif strengthLevel == 9 then return 18;
    elseif strengthLevel == 10 then return 20;
    end
end
function DST.getFatigueGainForFitness(level)
    -- -x% fatigue
    if level == 0 then return 0;
    elseif level == 1 then return 5;
    elseif level == 2 then return 8;
    elseif level == 3 then return 11;
    elseif level == 4 then return 13;
    elseif level == 5 then return 15;
    elseif level == 6 then return 17;
    elseif level == 7 then return 19;
    elseif level == 8 then return 21;
    elseif level == 9 then return 23;
    elseif level == 10 then return 25;
    else return nil;
    end
end
function DST.getEnduranceLossForFitness(level)
    -- - x% endurance lost
    if level == 0 then return 90;
    elseif level == 1 then return 80;
    elseif level == 2 then return 75;
    elseif level == 3 then return 70;
    elseif level == 4 then return 65;
    elseif level == 5 then return 60;
    elseif level == 6 then return 57;
    elseif level == 7 then return 53;
    elseif level == 8 then return 49;
    elseif level == 9 then return 46;
    elseif level == 10 then return 43;
    else return nil;
    end
end
function DST.getEnduranceRecoveryForFitness(level)
    -- + x% endurance recovery
    if level == 0 then return 70;
    elseif level == 1 then return 80;
    elseif level == 2 then return 90;
    elseif level == 3 then return 100;
    elseif level == 4 then return 110;
    elseif level == 5 then return 120;
    elseif level == 6 then return 130;
    elseif level == 7 then return 140;
    elseif level == 8 then return 150;
    elseif level == 9 then return 155;
    elseif level == 10 then return 160;
    else return nil;
    end
end
function DST.getFootstepsSoundRadiusForLightfooted(level)
    -- - x% footstep sound radius
    if level == 0 then return 1;
    elseif level == 1 then return 10;
    elseif level == 2 then return 21;
    elseif level == 3 then return 29;
    elseif level == 4 then return 35;
    elseif level == 5 then return 41;
    elseif level == 6 then return 48;
    elseif level == 7 then return 55;
    elseif level == 8 then return 63;
    elseif level == 9 then return 70;
    elseif level == 10 then return 80;
    else return nil;
    end
end
function DST.getCombatWalkSpeedOrFootstepSoundRadiusForNimble(level)
    -- +x % combat walk speed
    -- -x% footstep sound radius
    if level == 0 then return 0;
    elseif level == 1 then return 10;
    elseif level == 2 then return 14;
    elseif level == 3 then return 18;
    elseif level == 4 then return 22;
    elseif level == 5 then return 26;
    elseif level == 6 then return 30;
    elseif level == 7 then return 34;
    elseif level == 8 then return 38;
    elseif level == 9 then return 42;
    elseif level == 10 then return 50;
    else return nil;
    end
end
function DST.getFootstepsSoundRadiusForSneaking(level)
    -- - x% footstep sound radius
    if level == 0 then return 0;
    elseif level == 1 then return 25;
    elseif level == 2 then return 33.3;
    elseif level == 3 then return 37.5;
    elseif level == 4 then return 41.7;
    elseif level == 5 then return 45.8;
    elseif level == 6 then return 50;
    elseif level == 7 then return 54.2;
    elseif level == 8 then return 58.3;
    elseif level == 9 then return 62.5;
    elseif level == 10 then return 66.7;
    else return nil;
    end
end
function DST.getDetectionChanceForSneaking(level)
    -- - x% detection chance (every tick)
    if level == 0 then return 0;
    elseif level == 1 then return 5.2;
    elseif level == 2 then return 15.8;
    elseif level == 3 then return 21;
    elseif level == 4 then return 26.3;
    elseif level == 5 then return 31.6;
    elseif level == 6 then return 36.8;
    elseif level == 7 then return 42.1;
    elseif level == 8 then return 47.4;
    elseif level == 9 then return 52.6;
    elseif level == 10 then return 57.9
    else return nil;
    end
end

function DST.getInjuryChanceForWeaponSkill(level)
    if level == 0 then return -5;
    elseif level == 1 then return -2;
    elseif level == 2 then return 0;
    elseif level == 3 then return 1;
    elseif level == 4 then return 2;
    elseif level == 5 then return 3;
    elseif level == 6 then return 4;
    elseif level == 7 then return 5;
    elseif level == 8 then return 5;
    elseif level == 9 then return 6;
    elseif level == 10 then return 7
    else return nil;
    end
end
DST = DST or {}
function DST.round(x)
    return x>=0 and math.floor(x*100+0.5)/100 or math.ceil(x*100-0.5)/100
end
function DST.round3(x)
    return x>=0 and math.floor(x*1000+0.5)/1000 or math.ceil(x*1000-0.5)/1000
end
  

function DST.calculateCurrentAttractFishNumber()
    SandboxVars = SandboxVars or {}
    AttractFishNumber = 100;
    if SandboxVars.NatureAbundance == 1 then -- very poor
        AttractFishNumber = 140;
    elseif SandboxVars.NatureAbundance == 2 then -- poor
        AttractFishNumber = 120;
    elseif SandboxVars.NatureAbundance == 4 then -- abundant
        AttractFishNumber = 80;
    elseif SandboxVars.NatureAbundance == 5 then -- very abundant
        AttractFishNumber = 60;
    end
    local currentHour = math.floor(math.floor(GameTime:getInstance():getTimeOfDay() * 3600) / 3600);
    if (currentHour >= 4 and currentHour <= 6) or (currentHour >= 18 and currentHour <= 20) then
        AttractFishNumber = AttractFishNumber - 10;
    end
    if (getGameTime():getMonth() + 1) >= 11 or (getGameTime():getMonth() + 1) <= 2 then
        AttractFishNumber = AttractFishNumber + 20;
    end
end


function DST.onAddForageDefs(forageSystem)


    
    local skillLevel = 0;
    DST.setupBaseTooltipDefinitions()

    for skill, tooltipLinesTable in pairs(DST.SkillTooltipDefinitions) do
        skillLevel = 0;
        for levelKey, line in pairs(DST.SkillTooltipDefinitions[skill]) do
            skillLevel = skillLevel + 1;
            
            -- table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")

            if skill == "Strength" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.getCarryCapacityForStrength(skillLevel).." "..getText("IGUI_skilltooltip_basecarrycapacity"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*2).."% "..getText("IGUI_skilltooltip_blockchance"))

                if skillLevel < 5 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.round(-25 + skillLevel*5).."% "..getText("IGUI_skilltooltip_meleedamage"))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.round(-25 + skillLevel*5).."% "..getText("IGUI_skilltooltip_pushknockdown"))
                elseif skillLevel > 5 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(-25 + skillLevel*5).."% "..getText("IGUI_skilltooltip_meleedamage"))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(-25 + skillLevel*5).."% "..getText("IGUI_skilltooltip_pushknockdown"))
                end

                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*2).."% "..getText("IGUI_skilltooltip_chancetoclimbtallfences"))
                
                if skillLevel >= 9 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_strong").." "..getText("IGUI_skilltooltip_trait"))
                elseif skillLevel >= 6 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_stout").." "..getText("IGUI_skilltooltip_trait"))
                elseif skillLevel <= 1 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_weak").." "..getText("IGUI_skilltooltip_trait"))
                elseif skillLevel <= 4 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_feeble").." "..getText("IGUI_skilltooltip_trait"))
                end
            elseif skill == "Fitness" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*2).."% "..getText("IGUI_skilltooltip_blockchance"))
                if skillLevel>0 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.getFatigueGainForFitness(skillLevel).."% "..getText("Tooltip_food_Fatigue"))
                end
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.getEnduranceLossForFitness(skillLevel).."% "..getText("IGUI_skilltooltip_enduranceloss"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.getEnduranceRecoveryForFitness(skillLevel).."% "..getText("IGUI_skilltooltip_endurancerecovery"))
                
                if skillLevel>0 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*2).."% "..getText("IGUI_skilltooltip_attackspeed"))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round(skillLevel*2).."% "..getText("IGUI_skilltooltip_tripchance"))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*2).."% "..getText("IGUI_skilltooltip_chancetoclimbtallfences"))
                end

                
                if skillLevel >= 9 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_athletic").." "..getText("IGUI_skilltooltip_trait"))
                elseif skillLevel >= 6 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_fit").." "..getText("IGUI_skilltooltip_trait"))
                elseif skillLevel <= 1 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_unfit").." "..getText("IGUI_skilltooltip_trait"))
                elseif skillLevel <= 4 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+ "..getText("UI_trait_outofshape").." "..getText("IGUI_skilltooltip_trait"))
                end

            elseif skill == "Sprinting" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*5).."% "..getText("IGUI_skilltooltip_runningspeed"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*5).."% "..getText("IGUI_skilltooltip_sprintingspeed"))
            elseif skill == "Lightfoot" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.getFootstepsSoundRadiusForLightfooted(skillLevel).."% "..getText("IGUI_skilltooltip_footstepssoundradius"))
            elseif skill == "Nimble" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.getCombatWalkSpeedOrFootstepSoundRadiusForNimble(skillLevel).."% "..getText("IGUI_skilltooltip_footstepssoundradius"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.getCombatWalkSpeedOrFootstepSoundRadiusForNimble(skillLevel).."% "..getText("IGUI_skilltooltip_combatwalkspeed"))
            elseif skill == "Sneak" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.getFootstepsSoundRadiusForSneaking(skillLevel).."% "..getText("IGUI_skilltooltip_footstepssoundradiuswhilesneaking"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.getDetectionChanceForSneaking(skillLevel).."% "..getText("IGUI_skilltooltip_detectionchance"))
                
            elseif DST.WeaponSkills[skill] then -- weapon skills
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.round(0.3+0.1*skillLevel).."x "..getText("IGUI_skilltooltip_damage"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(3*skillLevel).."% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_critchance"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(3*skillLevel).."% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_attackspeed"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(3*skillLevel).."% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_blockchance"))

                if skillLevel <= 2 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.getInjuryChanceForWeaponSkill(skillLevel).."% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_injurychance"))
                else
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.getInjuryChanceForWeaponSkill(skillLevel).."% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_injurychance"))
                end
                if DST.BruiserWeaponSkills[skill] then
                    if skillLevel >= 8 then
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+20% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_extradamage"))
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+20% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_attackrange"))
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-10% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_endurancecost"))
                    elseif skillLevel == 7 then
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+20% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_extradamage"))
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+20% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_attackrange"))
                    elseif skillLevel >= 3 then
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+10% "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_extradamage"))
                    end
                elseif skill == "SmallBlade" then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*3).."% "..getText("IGUI_skilltooltip_chanceofjawstab"))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_decreasedchanceofshortbladegettingstuckinazombie"))
                end
                if skillLevel == 2 or skillLevel == 4 or skillLevel == 6 or skillLevel == 8 or skillLevel == 10 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Slightincreasein").." "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_durability"))
                end

                if getActivatedMods():contains("WeaponModifiersReforge") then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round3(skillLevel*0.005).." "..getText("IGUI_perks_"..skill).." "..getText("IGUI_skilltooltip_tinkeringraritytweaker"))
                end

            elseif skill == "Maintenance" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Increaseddurabilityofallweapons"))
                if getActivatedMods():contains("WeaponModifiersReforge") then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round3(skillLevel*0.01).." "..getText("IGUI_skilltooltip_tinkeringraritytweaker"))
                end

            elseif skill == "Woodwork" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 1, "-"..DST.round(skillLevel*5).."% "..getText("IGUI_skilltooltip_barricadingtimewood"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 2, "+"..DST.round(skillLevel*50).." "..getText("IGUI_skilltooltip_healthforwoodenconstructions"))
                if skillLevel <=3 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 3, "+"..DST.round(skillLevel).." "..getText("IGUI_skilltooltip_craftedspeardurability"))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 4, "")
                else
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 3, "")
                end

            elseif skill == "Cooking" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round(skillLevel*3).."% "..getText("IGUI_skilltooltip_ingredientsused"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*6.7).."% "..getText("IGUI_skilltooltip_morecalories"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*2.5).."% "..getText("IGUI_skilltooltip_cookingspeed"))
                if skillLevel > 8 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Canuserottenfood", 10))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Cantellpoison"))
                elseif skillLevel > 6 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Canuserottenfood", 5))
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Cantellpoison"))
                end
            elseif skill == "Farming" then
                -- already done
            elseif skill == "Doctor" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*15).."% "..getText("IGUI_skilltooltip_bandagelife"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], DST.round(1+skillLevel).." x "..getText("IGUI_skilltooltip_fracturehealingspeed"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*20).."% "..getText("IGUI_skilltooltip_movespeedwithafracture"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_fastermedicalactions"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_fastermedicalchecks"))

            elseif skill == "Electricity" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 1, "+"..DST.round(skillLevel*0.5).."% "..getText("IGUI_skilltooltip_generatorconditionrepaired"))

            elseif skill == "MetalWelding" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 1, "-"..DST.round(skillLevel*3).."% "..getText("IGUI_skilltooltip_barricadingtimemetal"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 1, "-"..DST.round(skillLevel*2.5).."% "..getText("IGUI_skilltooltip_vehiclesalvagingtime"))
                
            elseif skill == "Mechanics" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 1, "-"..DST.round(skillLevel*100/15).."% "..getText("IGUI_skilltooltip_hoodopeningtime"))
                
            elseif skill == "Tailoring" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Sheetpaddingsprovide").." "..math.max(DST.round(skillLevel/2), 1).."% "..getText("IGUI_skilltooltip_scratchdefense"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Denimpaddingsprovide").." "..math.max(DST.round(skillLevel), 1).."% "..getText("IGUI_skilltooltip_scratchdefense")..", "..math.max(DST.round(skillLevel/2), 1).."% "..getText("IGUI_skilltooltip_bitedefense"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Leatherpaddingsprovide").." "..math.max(DST.round(skillLevel*2), 1).."% "..getText("IGUI_skilltooltip_scratchdefense")..", "..math.max(DST.round(skillLevel), 1).."% "..getText("IGUI_skilltooltip_bitedefense"))

                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..math.min(DST.round(12.5+12.5*skillLevel), 100).."% "..getText("IGUI_skilltooltip_chancetogetthread"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..DST.round(10 + 5*skillLevel).."% "..getText("IGUI_skilltooltip_chanceofgettingpaddingback"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Rippingclothesyieldsmorefabric"))
                if skillLevel >=8 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Canrepairclothes"))
                end
                

            elseif skill == "Aiming" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_increasegunstats"))
                if skillLevel >= 5 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-63% "..getText("IGUI_skilltooltip_experience"))
                end
                if getActivatedMods():contains("WeaponModifiersReforge") then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round3(skillLevel*2*0.005/3).." "..getText("IGUI_skilltooltip_rangedweapon").." "..getText("IGUI_skilltooltip_tinkeringraritytweaker"))
                end

            elseif skill == "Reloading" then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Insertsandremovesmagazinesfaster"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Loadsunloadsmagazinesfaster"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Loadsweaponsfaster"))
                if skillLevel >= 5 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-63% "..getText("IGUI_skilltooltip_experience"))
                end
                if getActivatedMods():contains("WeaponModifiersReforge") then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round3(skillLevel*0.005/3).." "..getText("IGUI_skilltooltip_rangedweapon").." "..getText("IGUI_skilltooltip_tinkeringraritytweaker"))
                end

            elseif skill == "Fishing" then
                
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..skillLevel.."% "..getText("IGUI_skilltooltip_chancetobreakrod"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")

            elseif skill == "Trapping" then


            elseif skill == "PlantScavenging" then
                
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..(skillLevel*forageSystem.levelBonus).." "..getText("IGUI_SearchMode_Vision_Effect_Radius"))
                
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Better").." "..getText("IGUI_SearchMode_Tip_SearchFocus_Title"))
                if skillLevel >= 3 and skillLevel <= 8 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_morepreciseinformation"))
                end
                local spaceCheck = false
                for _, catDef in pairs(forageSystem.catDefs) do
                    if (not catDef.categoryHidden) and skillLevel == catDef.identifyCategoryLevel then
                        local exactCategory = getTextOrNull("IGUI_SearchMode_Categories_"..catDef.name);
                        if exactCategory then
                            if not spaceCheck then
                                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                                spaceCheck = true
                            end
                            table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_Enablesfocusing").." "..exactCategory)
                            
                        end;
                    end;
                end;

            elseif skill == "Woodcutting" and Woodcutting and Woodcutting.Settings then -- Woodcutting skill mod

                local caloriesUsed = DST.round(math.floor(skillLevel * Woodcutting.Settings.caloriesSavedModifierPerLevel) * 100/8);

                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..(skillLevel*6).."% "..getText("IGUI_skilltooltip_removingbushtime"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..DST.round(skillLevel*Woodcutting.Settings.bonusAxeTreeDamagePerLevel).." "..getText("IGUI_skilltooltip_bonusaxetreedamage"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round(skillLevel*Woodcutting.Settings.enduranceSavedPerPerkLevel*100).."% "..getText("IGUI_skilltooltip_enduranceused"))

                if caloriesUsed > 0 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..caloriesUsed.."% "..getText("IGUI_skilltooltip_caloriedused"))
                end
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..getText("IGUI_skilltooltip_increasedchanceofmaterials"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..getText("IGUI_skilltooltip_loweredchancetoloseaxecondition"))
                
                if skillLevel < Woodcutting.Settings.cumulatedForagingAndWoodcuttingSkillLevelForFruit then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "   "..getText("IGUI_skilltooltip_foodcondition", Woodcutting.Settings.cumulatedForagingAndWoodcuttingSkillLevelForFruit - skillLevel))
                else
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "   "..getText("IGUI_skilltooltip_foodcondition2", Woodcutting.Settings.cumulatedForagingAndWoodcuttingSkillLevelForFruit - skillLevel))
                end

                if skillLevel >= Woodcutting.Settings.skillLevelForNoSevereExhaustion then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "   "..getText("IGUI_skilltooltip_nolongergetsseverelyexhausted"))
                end
                

            elseif skill == "Tinkering" and SandboxVars and SandboxVars.WeaponModifiers then  -- Weapon Modifiers - Reforge mod

                local player = getPlayer()
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "+"..(skillLevel*3).."% "..getText("IGUI_skilltooltip_chancenottoconsumetinkeringitem"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..(skillLevel*3).."% "..getText("IGUI_skilltooltip_tinkeringtime"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "-"..DST.round3(skillLevel*0.03).." "..getText("IGUI_skilltooltip_tinkeringraritytweaker"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_tinkeringraritytweakerexplanation"))

                
                if skillLevel > 2 then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..getText("IGUI_skilltooltip_cantinkerweaponscondition"))
                else
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..getText("IGUI_skilltooltip_cantinkerweaponscondition2"))

                end

                if SandboxVars and SandboxVars.WeaponModifiers and SandboxVars.WeaponModifiers.DynamicTinkerer then
                    table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                    if skillLevel < 8 then
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..getText("IGUI_skilltooltip_dynamictinkerercondition", 8 - skillLevel))
                    else
                        table.insert(DST.SkillTooltipDefinitions[skill][levelKey], ""..getText("IGUI_skilltooltip_dynamictinkerercondition2"))
                    end
                end
                

            elseif skill == "Scavenging" and SandboxVars.ScavengerSkill then -- Scavenging skill mod
                local baseChance = SandboxVars.ScavengerSkill["Level"..skillLevel.."FindChance"]
                local modifier = SandboxVars.ScavengerSkill["Level"..skillLevel.."BonusLoot"]

                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_scavengingbenefits", baseChance, modifier))

            elseif skill == "LGRHunting" then -- Snake's 'Le gourmet revolution' Hunting skill
                -- +10 animal tracking max chance /level
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_animaltrackingmaxchance", 10*skillLevel))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_successfulcapturechance", 2*skillLevel))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_successfulanimalcallchance", DST.round((100/(15-skillLevel))-6.67)))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_watchtreetime", 8*skillLevel))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_huntinghitchance", 3.75*skillLevel))

            elseif skill == "AMReloading" then -- Snake's 'Ammo maker' Artillery skill
                -- -5% weapon dismantiling time
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], 1, getText("IGUI_skilltooltip_weapondismantlingtime", 5*skillLevel))
                -- On level 5 : remove 'uninterested in weapon recycling trait' or add 'ammo recicler trait'
                -- On level 8 : + 'ammo recycler' trait if the player doesn't have it

            elseif skill == "Driving" and DriveSkillUtils and SandboxVars.DrivingSkill then -- Driving skill mod
                local DS = SandboxVars.DrivingSkill
                local brakeForceMult = DS["Level"..skillLevel.."brakeForceMult"]
                local engineQualityMult = DS["Level"..skillLevel.."engineQualityMult"]
                local engineLoudnessMult = DS["Level"..skillLevel.."engineLoudnessMult"]
                local enginePowerMult = DS["Level"..skillLevel.."enginePowerMult"]
                local maxSpeedMult = DS["Level"..skillLevel.."maxSpeedMult"]
                local damageMitigate = DS["Level"..skillLevel.."damageMitigate"]
                local avoidDamage = DS["Level"..skillLevel.."avoidDamage"]
                
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_brakeForceMult", brakeForceMult-100))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_engineQualityMult", engineQualityMult-100))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_engineLoudnessMult", engineLoudnessMult - 100))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_enginePowerMult", enginePowerMult-100))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_maxSpeedMult", maxSpeedMult-100))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_damageMitigate", damageMitigate, avoidDamage))

            elseif skill == "Lockpicking"  then -- Lockpicking skill mod

                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_increasedGreenYellowZoneWhenForcingALockWithACrowbar"))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_wideranglebobbipin"))


            elseif skill == "Archery"  then -- Archery skill mod
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_reloadspeedwithbows", DST.round((100/(0.85^skillLevel))-100)))
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_arrowdeviation", DST.round((100/(1.35^skillLevel))-100)))
                    
            elseif skill == "Reading" and SandboxVars.Literacy then -- Literacy skill
                local readingLevel = skillLevel - 5
                readingLevel = readingLevel * 0.2
                if readingLevel > 0 then
                    readingLevel = readingLevel * SandboxVars.Literacy.SpeedMultiplier
                end
                readingLevel = readingLevel + 1
                local readingSpeed = readingLevel * 100
                if readingSpeed % 10 ~= 0 then
                    readingSpeed = math.floor(readingSpeed) + 1
                end
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText('IGUI_skilltooltip_levelxreadingspeed', skillLevel, readingSpeed))

                
            elseif skill == "Gunsmith" and SandboxVars.GSmith then -- Gunsmith skill

                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_gunrepairandscraptime", DST.round(skillLevel * 25/4)))

                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_gunrepairandscarp", skillLevel * 8))

            end
        end
    end


    local tooltipsForagingDefs = {}
    -- print("for forageSystem.itemDefs")
    local itemNamesDone = {}
    for k, itemDef in pairs(forageSystem.itemDefs) do

        if itemDef.skill and itemDef.skill > 0 and itemDef.perks then

            if not tooltipsForagingDefs[itemDef.perks[1]] then
                tooltipsForagingDefs[itemDef.perks[1]] = {}
            end
            
            if not tooltipsForagingDefs[itemDef.perks[1]]["level_"..itemDef.skill] then
                tooltipsForagingDefs[itemDef.perks[1]]["level_"..itemDef.skill] = {}
            end

            if not tooltipsForagingDefs[itemDef.perks[1]]["level_"..itemDef.skill][itemDef.categories[1]] then
                tooltipsForagingDefs[itemDef.perks[1]]["level_"..itemDef.skill][itemDef.categories[1]] = {}
            end
            if not itemNamesDone[getItemNameFromFullType(itemDef.type)] then
                table.insert(tooltipsForagingDefs[itemDef.perks[1]]["level_"..itemDef.skill][itemDef.categories[1]], getItemNameFromFullType(itemDef.type))
                itemNamesDone[getItemNameFromFullType(itemDef.type)] = true
            end
        end
    end

    local catsDone = {}
    for perkKey, perkTable in pairs(tooltipsForagingDefs) do

        for levelKey, levelTable in pairs(tooltipsForagingDefs[perkKey]) do
            local spaceDone = false
            
            for categoryKey, categoryTable in pairs(tooltipsForagingDefs[perkKey][levelKey]) do
                if not spaceDone then
                    table.insert(DST.SkillTooltipDefinitions[perkKey][levelKey], "")
                    spaceDone = true
                end
                if #categoryTable > 9 then
                    if not catsDone[categoryKey] then
                        table.insert(DST.SkillTooltipDefinitions[perkKey][levelKey], getText("IGUI_skilltooltip_Canfind").." "..getText("IGUI_SearchMode_Categories_"..categoryKey))
                    else
                        table.insert(DST.SkillTooltipDefinitions[perkKey][levelKey], getText("IGUI_skilltooltip_Canfindmore").." "..getText("IGUI_SearchMode_Categories_"..categoryKey))
                    end
                    catsDone[categoryKey] = true
                else
                    local line = getText("IGUI_skilltooltip_Canfind").." "
                    for i=1, #categoryTable do
                        line = line..categoryTable[i]
                        if i < #categoryTable then
                            line = line..", "
                        end
                        if i == 5 and categoryTable[6] then
                            line = line.." <LINE>   "
                        end
                    end
                    table.insert(DST.SkillTooltipDefinitions[perkKey][levelKey], line)
                    catsDone[categoryKey] = true
                end
            end
        end
    end


    
	local recipes = getScriptManager():getAllRecipes()
    local recipeNamesAdded = {}

    for i=0, recipes:size()-1 do
        local recipe = recipes:get(i);
        -- local skillsRequired = recipe:getRequiredSkills()
        -- if skillsRequired and skillsRequired:size() == 1 then
            -- for j=0, skillsRequired:size()-1 do

            local requiredSkill = recipe:getRequiredSkill(0)
            local requiredSkill2 = recipe:getRequiredSkill(1)
            if requiredSkill and requiredSkill:getPerk() and recipe:getRequiredSkillCount() < 3 then
                local perkName = requiredSkill:getPerk():getType():toString();
                local levelRequired = requiredSkill:getLevel();

                if not DST.SkillTooltipDefinitions[perkName] then
                    DST.SkillTooltipDefinitions[perkName] = {
                        level_1 = {},
                        level_2 = {},
                        level_3 = {},
                        level_4 = {},
                        level_5 = {},
                        level_6 = {},
                        level_7 = {},
                        level_8 = {},
                        level_9 = {},
                        level_10 = {},
                    }
                elseif not DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired] then
                    DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired] = {}
                end
                local skillTooltipLine = getText("IGUI_skilltooltip_can").." "..recipe:getName()
                if requiredSkill2 then
                    skillTooltipLine = skillTooltipLine.." ("..getText("IGUI_skilltooltip_with").." "..requiredSkill2:getPerk():getType():toString().." = "..requiredSkill2:getLevel()..")";
                end
                if (recipe:needToBeLearn()) then
                    skillTooltipLine = skillTooltipLine.." (*)"
                    DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].requiresKnowledge = true;
                end
                if not recipeNamesAdded[skillTooltipLine] then
                    if not DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount or DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount < 1 then
                        table.insert(DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired], "")
                    end
                    if not DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount or DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount and DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount < 10 then
                        table.insert(DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired], skillTooltipLine)
                        recipeNamesAdded[skillTooltipLine] = skillTooltipLine
                        if not DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount then
                            DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount = 1;
                        else
                            DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount = DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount + 1;
                        end
                    else
                        if not DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].tooManyRecipes then
                            table.insert(DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired], getText("IGUI_skilltooltip_Andmore"))
                            DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].tooManyRecipes = true
                        end
                    end
                    
                    
                end

                if requiredSkill2 and requiredSkill2:getPerk() then
                    local perkName2 = requiredSkill2:getPerk():getType():toString();
                    local levelRequired2 = requiredSkill2:getLevel();
                
                    if not DST.SkillTooltipDefinitions[perkName2] then
                        DST.SkillTooltipDefinitions[perkName2] = {
                            level_1 = {},
                            level_2 = {},
                            level_3 = {},
                            level_4 = {},
                            level_5 = {},
                            level_6 = {},
                            level_7 = {},
                            level_8 = {},
                            level_9 = {},
                            level_10 = {},
                        }
                    elseif not DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2] then
                        DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2] = {}
                    end
                    if DST.SkillTooltipDefinitions[perkName2] and DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2] then
                        local skillTooltipLine = getText("IGUI_skilltooltip_can").." "..recipe:getName()
                        skillTooltipLine = skillTooltipLine.." ("..getText("IGUI_skilltooltip_with").." "..getText("IGUI_perks_"..perkName).." = "..levelRequired..")";
                        if (recipe:needToBeLearn()) then
                            skillTooltipLine = skillTooltipLine.." (*)"
                            
                            DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].requiresKnowledge = true;
                        end
    
                        if not recipeNamesAdded[skillTooltipLine] then
                            if not DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].recipesCount or DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].recipesCount < 1 then
                                table.insert(DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2], "")
                            end
                            if  not DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount or DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount and DST.SkillTooltipDefinitions[perkName]["level_"..levelRequired].recipesCount < 10  then
                                table.insert(DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2], skillTooltipLine)
                                recipeNamesAdded[skillTooltipLine] = skillTooltipLine
                                if not DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].recipesCount then
                                    DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].recipesCount = 1;
                                else
                                    DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].recipesCount = DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].recipesCount + 1;
                                end
                            else
                                if not DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].tooManyRecipes then
                                    table.insert(DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2], getText("IGUI_skilltooltip_andmore"))
                                    DST.SkillTooltipDefinitions[perkName2]["level_"..levelRequired2].tooManyRecipes = true
                                end
                            end
                        end
                    end
                    
                end
            end     
    end




    
    for skill, tooltipLinesTable in pairs(DST.SkillTooltipDefinitions) do
        for levelKey, line in pairs(DST.SkillTooltipDefinitions[skill]) do
            if DST.SkillTooltipDefinitions[skill][levelKey].requiresKnowledge then
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], "")
                table.insert(DST.SkillTooltipDefinitions[skill][levelKey], getText("IGUI_skilltooltip_requiresknowledge"))
            end
        end
    end
    


end


function DST.updateDSTs()
    forageSystem = forageSystem or {}
    DST.onAddForageDefs(forageSystem)
end

Events.onAddForageDefs.Add(DST.onAddForageDefs)
Events.EveryHours.Add(DST.updateDSTs)
Events.EveryTenMinutes.Add(DST.calculateCurrentAttractFishNumber)


return DST