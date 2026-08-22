require("CharacterCreationProfession")

MDFT = MDFT or {}
PZAPI = PZAPI or {}
CharacterCreationProfession = CharacterCreationProfession or {}
BaseGameCharacterDetails = BaseGameCharacterDetails or {}
forageSystem = forageSystem or {}
forageSkills = forageSystem.forageSkillDefinitions or  forageSkills or {}
TraitFactory = TraitFactory or {}
ProfessionFactory = ProfessionFactory or {}

CharacterCreationProfession.old_populateProfessionList = CharacterCreationProfession.populateProfessionList
CharacterCreationProfession.old_populateTraitList = CharacterCreationProfession.populateTraitList
CharacterCreationProfession.old_populateBadTraitList = CharacterCreationProfession.populateBadTraitList

MDFT.moreDescriptions = {}

MDFT.MoreDescription = MDFT.MoreDescription or {}

MDFT.debugMode = true

function MDFT.noise(message)
    if MDFT.debugMode then
        print(message)
    end
end

local function tableLength(t)
    local count = 0
    for k, v in pairs(t) do
        count = count + 1
    end
    return count
end

function MDFT.processProfessionOrTrait(professionOrTrait)
    MDFT.noise("Processing: " .. professionOrTrait:getType())
    if not MDFT.moreDescriptions[professionOrTrait:getType()] then
        MDFT.moreDescriptions[professionOrTrait:getType()] = {
            extraDescription = "",
            freeRecipes = "",
            foragingStats = ""
        }
    end
    MDFT.moreDescriptions[professionOrTrait:getType()].extraDescription = MDFT.processExtraDescription(professionOrTrait:getType())
    MDFT.moreDescriptions[professionOrTrait:getType()].freeRecipes = MDFT.processFreeRecipes(professionOrTrait)
    MDFT.moreDescriptions[professionOrTrait:getType()].foragingStats = MDFT.processForagingStats(professionOrTrait:getType())
end

function MDFT.processMoreDescriptionsForTraits()
    local professionList = ProfessionFactory.getProfessions();
    for i = 0, professionList:size() - 1 do
        MDFT.processProfessionOrTrait(professionList:get(i))
    end
    local traitList = TraitFactory.getTraits();
    for i = 0, traitList:size() - 1 do
        MDFT.processProfessionOrTrait(traitList:get(i))
    end
end

function MDFT.processExtraDescription(professionOrTraitType)
    local desc = ""
    if MDFT.MoreDescription[professionOrTraitType] and #MDFT.MoreDescription[professionOrTraitType] > 0 then
        desc = desc .. "\n   "
        for i = 1, #MDFT.MoreDescription[professionOrTraitType] do
            if MDFT.MoreDescription[professionOrTraitType][i].text then
                local value = MDFT.MoreDescription[professionOrTraitType][i].value
                if value then
                    desc = desc .. getText(MDFT.MoreDescription[professionOrTraitType][i].text, value) .."\n   "
                else 
                    desc = desc .. getText(MDFT.MoreDescription[professionOrTraitType][i].text) .."\n   "
                end
            end
        end
    end
    return desc
end


function MDFT.processForagingStats(professionOrTraitType)
    local desc = ""
    local table = forageSkills[professionOrTraitType];
    
    
    if table then
        local foragingHeader = getText("IGUI_perks_Foraging")
        if foragingHeader == "IGUI_perks_Foraging" then
            foragingHeader = "Foraging"
        end
        desc = desc .. "---" .. foragingHeader .. ": ---"
        
        if table.visionBonus and table.visionBonus~=0 then
            desc = desc .. "\n"
            local visionText = getText("IGUI_SearchMode_Vision_Effect_Radius")
            if visionText == "IGUI_SearchMode_Vision_Effect_Radius" then
                visionText = "Vision Radius"
            end
            if table.visionBonus>0 then
                desc = desc .. "+" .. table.visionBonus .. " " .. visionText
            else
                desc = desc .. table.visionBonus .. " " .. visionText
            end
        end
        if table.weatherEffect and table.weatherEffect~=0 then
            desc = desc .. "\n"
            local weatherText = getText("IGUI_SearchMode_Vision_Effect_Weather")
            if weatherText == "IGUI_SearchMode_Vision_Effect_Weather" then
                weatherText = "Weather Effect"
            end
            if table.weatherEffect>0 then
                desc = desc .. "-" .. table.weatherEffect .. "% " .. weatherText
            else
                desc = desc .. "+" ..  tostring(table.weatherEffect):sub(2) .. "% " .. weatherText
            end
        end
        if table.darknessEffect and table.darknessEffect~=0 then
            desc = desc .. "\n"
            local darknessText = getText("IGUI_SearchMode_Vision_Effect_Darkness")
            if darknessText == "IGUI_SearchMode_Vision_Effect_Darkness" then
                darknessText = "Darkness Effect"
            end
            if table.darknessEffect>0 then
                desc = desc .. "-" .. table.darknessEffect .. "% " .. darknessText
            else
                desc = desc .. "+" .. tostring(table.darknessEffect):sub(2) .. "% " .. darknessText
            end
        end
        if table.specialisations then
            for category, bonus in pairs(table.specialisations) do
                if bonus ~= 0 then
                    desc = desc .. "\n"
                    local categoryText = getText("IGUI_SearchMode_Categories_"..category)
                    if categoryText == "IGUI_SearchMode_Categories_"..category then
                        categoryText = category
                    end
                    if bonus > 0 then
                        desc = desc .."+" .. bonus .. "% ".. categoryText
                    else
                        desc = desc .. bonus .. "% ".." ".. categoryText
                    end
                end
            end
        end
    end
    return desc
end

function MDFT.processFreeRecipes(professionOrTrait)
    local desc = ""
    local freeRecipesTable = professionOrTrait:getFreeRecipes();
    
    -- Configuration: Maximum individual recipes to show per category before summarizing
    -- Change this number to control how many individual recipes are shown
    -- before adding "(and X more Category recipes)" summary
    local maxIndividualRecipes = 5
    
    if freeRecipesTable and not freeRecipesTable:isEmpty() then
        local knownRecipesText = getText('UI_trait_knownrecipes')
        if knownRecipesText == 'UI_trait_knownrecipes' then
            knownRecipesText = "Known Recipes"
        end
        desc = desc .. "---"..knownRecipesText.." ---\n"

        -- Initialize category tracking
        local categoryRecipes = {}
        local knowledgeRecipes = {}
        local scriptManager = getScriptManager()
        
        -- First pass: categorize all recipes
        for i=0, freeRecipesTable:size()-1 do
            local recipeId = freeRecipesTable:get(i)
            local recipeIdLower = string.lower(recipeId)
            
            -- Skip obsolete recipes entirely
            if not string.find(string.upper(recipeId), getText("UI_moredesc_OBSOLETE")) then
                local category = nil
                local isKnowledge = false
                local recipeExists = false
                
                -- Check for knowledge-based recipes first (these are handled separately)
                if (string.find(recipeIdLower, "herbal") and string.find(recipeIdLower, "remed")) or
                   string.find(recipeIdLower, "herbalremed") or string.find(recipeIdLower, "herbal_remed") then
                    local herbalText = getText("Recipe_Herbalist")
                    knowledgeRecipes[#knowledgeRecipes + 1] = herbalText
                    isKnowledge = true
                    recipeExists = true
                elseif (string.find(recipeIdLower, "generator") and string.find(recipeIdLower, "maintenance")) or
                       string.find(recipeIdLower, "generatormaintenance") or 
                       string.find(recipeIdLower, "generator_maintenance") then
                    local generatorText = getText("Recipe_Generator")
                    knowledgeRecipes[#knowledgeRecipes + 1] = generatorText
                    isKnowledge = true
                    recipeExists = true
                elseif string.find(recipeIdLower, "basic") and string.find(recipeIdLower, "mechanic") then
                    local basicMechText = getText("UI_moredesc_BasicMechanics")
                    knowledgeRecipes[#knowledgeRecipes + 1] = basicMechText
                    isKnowledge = true
                    recipeExists = true
                elseif string.find(recipeIdLower, "intermediate") and string.find(recipeIdLower, "mechanic") then
                    local intermediateMechText = getText("UI_moredesc_IntermediateMechanics")
                    knowledgeRecipes[#knowledgeRecipes + 1] = intermediateMechText
                    isKnowledge = true
                    recipeExists = true
                elseif string.find(recipeIdLower, "advanced") and string.find(recipeIdLower, "mechanic") then
                    local advancedMechText = getText("UI_moredesc_AdvancedMechanics")
                    knowledgeRecipes[#knowledgeRecipes + 1] = advancedMechText
                    isKnowledge = true
                    recipeExists = true
                end
                
                if not isKnowledge then
                    -- Handle special grouped categories with priority order
                    if string.sub(recipeId, -#"Growing Season") == "Growing Season" then
                        category = getText("UI_moredesc_GrowingSeasons")
                        recipeExists = true
                    elseif string.sub(recipeId, 1, #"MakeJarof") == "MakeJarof" then
                        category = getText("UI_moredesc_Pickling")
                        recipeExists = true
                    elseif string.sub(recipeId, 1, #"Knit") == "Knit" then
                        category = getText("UI_moredesc_Knitting")
                        recipeExists = true
                    elseif string.sub(recipeId, 1, #"Sew") == "Sew" then
                        category = getText("UI_moredesc_Sewing")
                        recipeExists = true
                    elseif string.sub(recipeId, 1, #"Sharpen") == "Sharpen" or string.find(recipeIdLower, "sharpen") then
                        category = getText("UI_moredesc_Sharpening")
                        recipeExists = true
                    else
                        -- Check construction by both recipe ID and display name
                        local displayName = getRecipeDisplayName(recipeId) or ""
                        local displayNameLower = string.lower(displayName)
                        if string.find(recipeIdLower, "construct") or 
                           string.find(recipeIdLower, "fence") or string.find(recipeIdLower, "container") or string.find(recipeIdLower, "wall") or
                           string.sub(recipeId, 1, #"Construct") == "Construct" or
                           string.find(displayNameLower, "construct") or 
                           -- More specific patterns to avoid false positives
                           (string.find(recipeIdLower, "roof") and not string.find(recipeIdLower, "proof")) or
                           (string.find(recipeIdLower, "build") and not string.find(recipeIdLower, "building")) or
                           (string.find(displayNameLower, "build ") or string.match(displayNameLower, "^build")) then
                            category = getText("UI_moredesc_Construction")
                            recipeExists = true
                            MDFT.noise("Construction override: " .. recipeId)
                        end
                    end
                    
                    -- Only try script category detection if no special category was assigned
                    if not category then
                        -- First, try to get the category from the actual recipe script
                        -- Try new CraftRecipe objects first, then old Recipe objects
                        local recipe = scriptManager:getCraftRecipe(recipeId)
                        
                        if not recipe then
                            recipe = scriptManager:getRecipe(recipeId)
                        end
                        
                        if recipe then
                            recipeExists = true
                            local scriptCategory = recipe:getCategory()
                            
                            -- Priority override: Construction items should always be categorized as Construction
                            -- regardless of what the script says
                            local displayName = getRecipeDisplayName(recipeId) or ""
                            local displayNameLower = string.lower(displayName)
                            if string.find(recipeIdLower, "construct") or 
                               string.find(recipeIdLower, "fence") or string.find(recipeIdLower, "container") or string.find(recipeIdLower, "wall") or
                               string.sub(recipeId, 1, #"Construct") == "Construct" or
                               string.find(displayNameLower, "construct") or 
                               -- More specific patterns to avoid false positives
                               (string.find(recipeIdLower, "roof") and not string.find(recipeIdLower, "proof")) or
                               (string.find(recipeIdLower, "build") and not string.find(recipeIdLower, "building")) or
                               (string.find(displayNameLower, "build ") or string.match(displayNameLower, "^build")) then
                                local constructionText = getText("UI_moredesc_Construction")
                                category = constructionText
                                MDFT.noise("Construction override: " .. recipeId)
                            elseif scriptCategory and scriptCategory ~= "" and 
                               scriptCategory ~= getText("UI_moredesc_General") and scriptCategory ~= getText("UI_moredesc_Miscellaneous") then
                                -- Remap Tailoring to Sewing (remove Tailoring category)
                                if scriptCategory == getText("UI_moredesc_Tailoring") then
                                    -- Check if recipe involves sewing or knitting
                                    if string.find(recipeIdLower, "sew") or string.find(recipeIdLower, "knit") then
                                        if string.find(recipeIdLower, "knit") then
                                            category = getText("UI_moredesc_Knitting")
                                        else
                                            category = getText("UI_moredesc_Sewing")
                                        end
                                    else
                                        category = scriptCategory
                                    end
                                elseif scriptCategory == getText("UI_moredesc_Blade") then
                                    category = getText("UI_moredesc_Blades")
                                    MDFT.noise("Blades: " .. recipeId)
                                else
                                    category = scriptCategory
                                end
                            end
                        end
                        
                        -- If no valid script category found, try evolved recipes
                        if not category then
                            local evolvedRecipe = scriptManager:getEvolvedRecipe(recipeId)
                            if evolvedRecipe then
                                category = getText("UI_moredesc_Cooking")
                                recipeExists = true
                            end
                        end
                        
                        -- If still no category and recipe exists, fall back to name-based categorization
                        if not category and recipeExists then
                            -- Prioritize Construction category first (must come before metalworking)
                            local displayName = getRecipeDisplayName(recipeId) or ""
                            local displayNameLower = string.lower(displayName)
                            if string.find(recipeIdLower, "construct") or 
                               string.find(recipeIdLower, "fence") or string.find(recipeIdLower, "container") or string.find(recipeIdLower, "wall") or
                               string.find(displayNameLower, "construct") or 
                               -- More specific patterns to avoid false positives
                               (string.find(recipeIdLower, "roof") and not string.find(recipeIdLower, "proof")) or
                               (string.find(recipeIdLower, "build") and not string.find(recipeIdLower, "building")) or
                               (string.find(displayNameLower, "build ") or string.match(displayNameLower, "^build")) then
                                category = getText("UI_moredesc_Construction")
                                MDFT.noise("Construction fallback: " .. recipeId)
                            -- Prioritize Blades category (must come before metalworking and weaponry)
                            elseif string.find(recipeIdLower, "blade") or string.find(recipeIdLower, "sword") or string.find(displayNameLower, "blade") then
                                category = getText("UI_moredesc_Blades")
                                MDFT.noise("Blades fallback: " .. recipeId)
                            -- Prioritize Weaponry category
                            elseif string.find(recipeIdLower, "weapon") then
                                category = getText("UI_moredesc_Weaponry")
                            -- Prioritize Cookware category (saucepan, kettle, etc.)
                            elseif string.find(recipeIdLower, "saucepan") or string.find(recipeIdLower, "kettle") or 
                                   string.find(recipeIdLower, "pot") or string.find(recipeIdLower, "pan") then
                                category = getText("UI_moredesc_Cookware")
                            -- Prioritize Assembling category
                            elseif string.find(recipeIdLower, "assemble") then
                                category = getText("UI_moredesc_Assembling")
                            -- Check for metalworking patterns (including Make Metal) - but exclude construction items and blades
                            elseif (string.find(recipeIdLower, "makemetal") or string.find(recipeIdLower, "make metal") or 
                                   (string.sub(recipeId, 1, #"Forge") == "Forge" and not string.match(recipeId, "Construct")) or
                                   (string.find(recipeIdLower, "forge") and not string.find(recipeIdLower, "construct") and not string.find(recipeIdLower, "blade")) or 
                                   string.find(recipeIdLower, "smelt") or string.find(recipeIdLower, "weld")) and
                                   not string.find(recipeIdLower, "construct") and not string.find(recipeIdLower, "blade") and
                                   not string.find(recipeIdLower, "barbed") and not string.find(recipeIdLower, "barbedwire") then
                                category = getText("UI_moredesc_Metalworking")
                            elseif string.find(recipeIdLower, "weapon") or string.find(recipeIdLower, "spear") or string.find(recipeIdLower, "knife") then
                                category = getText("UI_moredesc_Weaponry")
                            elseif string.find(recipeIdLower, "assemble") or string.find(recipeIdLower, "assembly") then
                                category = getText("UI_moredesc_Assembling")
                            elseif string.find(recipeIdLower, "trap") or string.find(recipeIdLower, "snare") then
                                category = getText("UI_moredesc_Trapping")
                            elseif string.find(recipeIdLower, "saw") or string.find(recipeIdLower, "chop") then
                                category = getText("UI_moredesc_Carpentry")
                            elseif string.find(recipeIdLower, "grow") or string.find(recipeIdLower, "plant") or string.find(recipeIdLower, "seed") or string.find(recipeIdLower, "farm") then
                                category = getText("UI_moredesc_Farming")
                            elseif string.find(recipeIdLower, "cook") or string.find(recipeIdLower, "bake") or string.find(recipeIdLower, "slice") or string.find(recipeIdLower, "prepare") then
                                category = getText("UI_moredesc_Cooking")
                            elseif string.find(recipeIdLower, "fish") or string.find(recipeIdLower, "bait") then
                                category = getText("UI_moredesc_Fishing")
                            -- Be more specific about electrical - avoid barbed wire weapons etc.
                            elseif (string.find(recipeIdLower, "electric") or string.find(recipeIdLower, "radio") or 
                                   (string.find(recipeIdLower, "wire") and not string.find(recipeIdLower, "weapon") and 
                                    not string.find(recipeIdLower, "barbed") and not string.find(recipeIdLower, "barbedwire"))) then
                                category = getText("UI_moredesc_Electrical")
                            elseif string.find(recipeIdLower, "medical") or string.find(recipeIdLower, "bandage") or string.find(recipeIdLower, "splint") then
                                category = getText("UI_moredesc_Medical")
                            else
                                category = getText("UI_moredesc_Miscellaneous")
                            end
                        end
                        
                        -- If recipe doesn't exist anywhere, skip it entirely
                        if not recipeExists then
                            MDFT.noise("Skipping non-existent recipe: " .. recipeId)
                        else
                            -- Ensure we have a category (but not for knowledge recipes)
                            if not category or category == "" then
                                category = getText("UI_moredesc_General")
                            end
                            
                            -- Get display name for category (try translation first)
                            local categoryDisplayName = getText("IGUI_CraftCategory_" .. category)
                            if categoryDisplayName == "IGUI_CraftCategory_" .. category then
                                categoryDisplayName = category
                            end
                            
                            -- Initialize category if needed
                            if not categoryRecipes[categoryDisplayName] then
                                categoryRecipes[categoryDisplayName] = {
                                    recipes = {},
                                    count = 0
                                }
                            end
                            
                            -- Add recipe to category
                            categoryRecipes[categoryDisplayName].recipes[#categoryRecipes[categoryDisplayName].recipes + 1] = recipeId
                            categoryRecipes[categoryDisplayName].count = categoryRecipes[categoryDisplayName].count + 1
                        end
                    end
                end
            end
        end
        
        -- Display knowledge recipes in a single line (if any)
        if #knowledgeRecipes > 0 then
            local knowledgeText = getText("UI_moredesc_Knowledge")
            desc = desc .. " - " .. knowledgeText .. ": "
            for i, knowledge in ipairs(knowledgeRecipes) do
                desc = desc .. knowledge
                if i < #knowledgeRecipes then
                    desc = desc .. ", "
                end
            end
            desc = desc .. ".\n"
        end
        
        -- Display categorized recipes
        for categoryName, categoryData in pairs(categoryRecipes) do
            if categoryData.count > 0 then
                desc = desc .. " - " .. categoryName .. ": "
                
                -- Smart recipe showing logic:
                -- If there's only 1 more recipe than the limit, show it instead of summary
                -- Only use summary when there are 2+ more recipes
                local recipesToShow = maxIndividualRecipes
                local useParenthesisSummary = false
                
                if categoryData.count <= maxIndividualRecipes then
                    -- Show all recipes if within limit
                    recipesToShow = categoryData.count
                elseif categoryData.count == maxIndividualRecipes + 1 then
                    -- Show all recipes if only 1 more than limit
                    recipesToShow = categoryData.count
                else
                    -- Use summary for 2+ additional recipes
                    recipesToShow = maxIndividualRecipes
                    useParenthesisSummary = true
                end
                
                local recipeNames = {}
                
                for i = 1, recipesToShow do
                    local recipeId = categoryData.recipes[i]
                    local displayName = nil
                    
                    -- Get translated category names for comparison
                    local growingSeasonsText = getText("UI_moredesc_GrowingSeasons")
                    if growingSeasonsText == "UI_moredesc_GrowingSeasons" then
                        growingSeasonsText = "Growing Seasons"
                    end
                    local knittingText = getText("UI_moredesc_Knitting")
                    if knittingText == "UI_moredesc_Knitting" then
                        knittingText = "Knitting"
                    end
                    local sewingText = getText("UI_moredesc_Sewing")
                    if sewingText == "UI_moredesc_Sewing" then
                        sewingText = "Sewing"
                    end
                    local sharpeningText = getText("UI_moredesc_Sharpening")
                    if sharpeningText == "UI_moredesc_Sharpening" then
                        sharpeningText = "Sharpening"
                    end
                    local constructionText = getText("UI_moredesc_Construction")
                    if constructionText == "UI_moredesc_Construction" then
                        constructionText = "Construction"
                    end
                    local assemblingText = getText("UI_moredesc_Assembling")
                    if assemblingText == "UI_moredesc_Assembling" then
                        assemblingText = "Assembling"
                    end
                    local carvingText = getText("UI_moredesc_Carving")
                    if carvingText == "UI_moredesc_Carving" then
                        carvingText = "Carving"
                    end
                    local knappingText = getText("UI_moredesc_Knapping")
                    if knappingText == "UI_moredesc_Knapping" then
                        knappingText = "Knapping"
                    end
                    local carpentryText = getText("UI_moredesc_Carpentry")
                    if carpentryText == "UI_moredesc_Carpentry" then
                        carpentryText = "Carpentry"
                    end
                    local metalworkingText = getText("UI_moredesc_Metalworking")
                    if metalworkingText == "UI_moredesc_Metalworking" then
                        metalworkingText = "Metalworking"
                    end
                    local picklingText = getText("UI_moredesc_Pickling")
                    if picklingText == "UI_moredesc_Pickling" then
                        picklingText = "Pickling"
                    end
                    local bladesText = getText("UI_moredesc_Blades")
                    if bladesText == "UI_moredesc_Blades" then
                        bladesText = "Blades"
                    end
                    local cookwareText = getText("UI_moredesc_Cookware")
                    if cookwareText == "UI_moredesc_Cookware" then
                        cookwareText = "Cookware"
                    end
                    
                    -- Special handling for different recipe types
                    if categoryName == "Growing Seasons" then
                        local strippedName = string.gsub(getRecipeDisplayName(recipeId), getText("UI_moredesc_GrowingSeason"), "")
                        displayName = strippedName
                        -- Special case for farmer/gardener
                        if (professionOrTrait:getType() == "farmer" or professionOrTrait:getType() == "Gardener") and categoryData.count > 1 then
                            displayName = getText("UI_moredesc_AllExceptHemp")
                            recipeNames[#recipeNames + 1] = displayName
                            break -- Don't show individual ones for farmer/gardener
                        end
                    elseif categoryName == "Knitting" then
                        displayName = string.gsub(getRecipeDisplayName(recipeId), getText("UI_moredesc_Knit"), "")
                    elseif categoryName == "Sewing" then
                        -- Remove both 'Sew' prefix and any remaining 'Sew' text
                        displayName = getRecipeDisplayName(recipeId)
                        displayName = string.gsub(displayName, getText("UI_moredesc_Sew"), "")
                        displayName = string.gsub(displayName, "^Sew ", "")
                        displayName = string.gsub(displayName, "Sew", "")
                    elseif categoryName == "Sharpening" then
                        displayName = getRecipeDisplayName(recipeId)
                        displayName = string.gsub(displayName, "^Sharpen ", "")
                        displayName = string.gsub(displayName, "Sharpen", "")
                    elseif categoryName == "Construction" then
                        displayName = getRecipeDisplayName(recipeId)
                        displayName = string.gsub(displayName, getText("UI_moredesc_Construct"), "")
                        displayName = string.gsub(displayName, "^Construct ", "")
                        displayName = string.gsub(displayName, "Construct", "")
                    elseif categoryName == "Assembling" then
                        displayName = getRecipeDisplayName(recipeId)
                        displayName = string.gsub(displayName, "^Assemble ", "")
                        displayName = string.gsub(displayName, "Assemble", "")
                    elseif categoryName == "Carving" then
                        displayName = getRecipeDisplayName(recipeId)
                        displayName = string.gsub(displayName, "^Carve ", "")
                        displayName = string.gsub(displayName, "Carve", "")
                    elseif categoryName == "Knapping" then
                        displayName = getRecipeDisplayName(recipeId)
                        displayName = string.gsub(displayName, "^Knap ", "")
                        displayName = string.gsub(displayName, "Knap", "")
                    elseif categoryName == "Carpentry" and string.find(recipeId, "Construct") then
                        displayName = string.gsub(getRecipeDisplayName(recipeId), getText("UI_moredesc_Construct"), "")
                    elseif categoryName == "Metalworking" and (string.find(recipeId, "Forge") or string.find(string.lower(recipeId), "makemetal") or string.find(string.lower(recipeId), "make metal")) then
                        displayName = getRecipeDisplayName(recipeId)
                        if string.find(recipeId, "Forge") then
                            displayName = string.gsub(displayName, getText("UI_moredesc_Forge"), "")
                        end
                        -- Handle item lookup for forge recipes
                        if string.find(recipeId, "Forge") then
                            -- Try to get the actual item name by extracting from recipe ID
                            local itemId = string.sub(recipeId, 6) -- Remove "Forge_" prefix
                            if itemId and itemId ~= "" then
                                local item = instanceItem(itemId)
                                if item then
                                    displayName = item:getDisplayName()
                                end
                            end
                        end
                    elseif categoryName == "Pickling" then
                        displayName = string.gsub(getRecipeDisplayName(recipeId), getText("UI_moredesc_MakeJarOf"), "")
                    elseif categoryName == "Blades" then
                        displayName = getRecipeDisplayName(recipeId)
                        -- Handle forge blade recipes specially
                        if string.find(recipeId, "Forge") then
                            displayName = string.gsub(displayName, getText("UI_moredesc_Forge"), "")
                            -- Try to get the actual item name by extracting from recipe ID
                            local itemId = string.sub(recipeId, 6) -- Remove "Forge_" prefix
                            if itemId and itemId ~= "" then
                                local item = instanceItem(itemId)
                                if item then
                                    displayName = item:getDisplayName()
                                end
                            end
                        end
                    elseif categoryName == "Cookware" then
                        displayName = getRecipeDisplayName(recipeId)
                        -- Handle forge cookware recipes specially
                        if string.find(recipeId, "Forge") then
                            displayName = string.gsub(displayName, getText("UI_moredesc_Forge"), "")
                            -- Try to get the actual item name by extracting from recipe ID
                            local itemId = string.sub(recipeId, 6) -- Remove "Forge_" prefix
                            if itemId and itemId ~= "" then
                                local item = instanceItem(itemId)
                                if item then
                                    displayName = item:getDisplayName()
                                end
                            end
                        end
                        -- Remove common prefixes for cookware
                        displayName = string.gsub(displayName, "^Make ", "")
                        displayName = string.gsub(displayName, "^Craft ", "")
                    else
                        displayName = getRecipeDisplayName(recipeId)
                        if displayName and not string.match(displayName, getText("UI_moredesc_OBSOLETE")) then
                            -- Remove common prefixes
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Make") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Craft") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Prepare") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Sew") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Knit") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Sharpen") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Construct") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Assemble") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Carve") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Knap") .. " ", "")
                            displayName = string.gsub(displayName, "^" .. getText("UI_moredesc_Forge") .. " ", "")
                        end
                    end
                    
                    if displayName and displayName ~= "" then
                        -- Final cleanup - trim whitespace
                        displayName = string.gsub(displayName, "^%s*(.-)%s*$", "%1")
                        
                        -- Special formatting for specific items
                        if string.find(displayName, "_Charcoal_Pit") or string.find(displayName, "Charcoal_Pit") then
                            local charcoalPitText = getText("UI_moredesc_CharcoalPit")
                            displayName = string.gsub(displayName, "_?Charcoal_Pit", charcoalPitText)
                        elseif string.find(displayName, "_Dome_Kiln") or string.find(displayName, "Dome_Kiln") then
                            local domeKilnText = getText("UI_moredesc_DomeKiln")
                            displayName = string.gsub(displayName, "_?Dome_Kiln", domeKilnText)
                        end
                        
                        
                        
                        if displayName ~= "" then
                            recipeNames[#recipeNames + 1] = displayName
                        end
                    end
                end
                
                -- Join recipe names
                for i, name in ipairs(recipeNames) do
                    desc = desc .. name
                    if i < #recipeNames then
                        desc = desc .. ", "
                    end
                end
                
                -- Add summary only if there are 2+ more recipes than shown
                if useParenthesisSummary then
                    local remaining = categoryData.count - maxIndividualRecipes
                    if #recipeNames > 0 then
                        -- Special case for Growing Seasons with "All, except Hemp"
                        if categoryName == getText("UI_moredesc_GrowingSeasons") and #recipeNames == 1 and 
                           recipeNames[1] == getText("UI_moredesc_AllExceptHemp") then
                            desc = desc .. " (" .. getText("UI_moredesc_GrowingSeasonsTotal", categoryData.count) .. ")"
                        else
                            desc = desc .. " (" .. getText("UI_moredesc_AndXMoreRecipes", remaining, categoryName) .. ")"
                        end
                    else
                        desc = desc .. getText("UI_moredesc_XRecipes", remaining, categoryName)
                    end
                end
                
                desc = desc .. ".\n"
            end
        end
    end
    
    return desc
end

function MDFT.addDescriptionToTooltip(professionOrTrait, desc)
    local professionOrTraitType = professionOrTrait:getType()
    
    -- Check if mod options exist, if not use defaults
    local showMoreDescription = true
    local showFreeRecipes = true
    local showForagingStats = true
    
    -- Try to get mod options if PZAPI is available
    if PZAPI and PZAPI.ModOptions then
        local options = PZAPI.ModOptions:getOptions("MoreDescriptionForTraits")
        if options then
            showMoreDescription = options:getOption("ShowMoreDescription"):getValue()
            showFreeRecipes = options:getOption("ShowFreeRecipes"):getValue()
            showForagingStats = options:getOption("ShowForagingStats"):getValue()
        end
    elseif MDFT.OPTIONS then
        -- Fallback to direct options if available
        showMoreDescription = MDFT.OPTIONS.ShowMoreDescription and MDFT.OPTIONS.ShowMoreDescription:getValue() or true
        showFreeRecipes = MDFT.OPTIONS.ShowFreeRecipes and MDFT.OPTIONS.ShowFreeRecipes:getValue() or true
        showForagingStats = MDFT.OPTIONS.ShowForagingStats and MDFT.OPTIONS.ShowForagingStats:getValue() or true
    end
    
    -- Ensure the data exists for this profession/trait
    if not MDFT.moreDescriptions[professionOrTraitType] then
        MDFT.processProfessionOrTrait(professionOrTrait)
    end
    
    local additionalDesc = ""
    
    if showMoreDescription and MDFT.moreDescriptions[professionOrTraitType].extraDescription ~= "" then
        additionalDesc = additionalDesc .. MDFT.moreDescriptions[professionOrTraitType].extraDescription
    end
    if showFreeRecipes and MDFT.moreDescriptions[professionOrTraitType].freeRecipes ~= "" then
        if additionalDesc ~= "" then
            additionalDesc = additionalDesc .. "\n"
        else
            additionalDesc = additionalDesc .. "\n\n"
        end
        additionalDesc = additionalDesc .. MDFT.moreDescriptions[professionOrTraitType].freeRecipes
    end
    if showForagingStats and MDFT.moreDescriptions[professionOrTraitType].foragingStats ~= "" then
        if additionalDesc ~= "" then
            additionalDesc = additionalDesc .. "\n"
        else
            additionalDesc = additionalDesc .. "\n\n"
        end
        additionalDesc = additionalDesc .. MDFT.moreDescriptions[professionOrTraitType].foragingStats
    end
    
    return desc .. additionalDesc
end

CharacterCreationProfession.populateProfessionList = function(self, list)
    local professionList = ProfessionFactory.getProfessions();
    for i = 0, professionList:size() - 1 do
        local prof = professionList:get(i)
        local newitem = list:addItem(i, prof);
        newitem.tooltip = prof:getDescription();
        newitem.tooltip = MDFT.addDescriptionToTooltip(prof, newitem.tooltip)
    end
end

CharacterCreationProfession.populateTraitList = function(self, list)
    list:clear()
    local traitList = TraitFactory.getTraits();
    for i = 0, traitList:size() - 1 do
        local trait = traitList:get(i);
        if not trait:isFree() and trait:getCost() > 0 and not trait:isRemoveInMP() and not self:isTraitExcluded(trait) then
            local traitDescription = trait:getDescription()
            traitDescription = MDFT.addDescriptionToTooltip(trait, traitDescription)
            list:addItem(trait:getLabel(), trait, traitDescription);
        end
    end
end

CharacterCreationProfession.populateBadTraitList = function(self, list)
    list:clear()
    local traitList = TraitFactory.getTraits();
    for i = 0, traitList:size() - 1 do
        local trait = traitList:get(i);
        if not trait:isFree() and trait:getCost() < 0 and not trait:isRemoveInMP() and not self:isTraitExcluded(trait) then
            local traitDescription = trait:getDescription()
            traitDescription = MDFT.addDescriptionToTooltip(trait, traitDescription)
            list:addItem(trait:getLabel(), trait, traitDescription);
        end
    end
end

CharacterCreationProfession.addTrait = function(self, trait)
    if not self.listboxTraitSelected:contains(trait:getLabel()) then
        self.pointToSpend = self.pointToSpend - trait:getCost();
        local traitDescription = trait:getDescription()
        traitDescription = MDFT.addDescriptionToTooltip(trait, traitDescription)
        self.listboxTraitSelected:addUniqueItem(trait:getLabel(), trait, traitDescription);
    end

    if not trait:isFree() then
        self.listboxTrait:removeMatchingItems(trait:getLabel());
        self.listboxBadTrait:removeMatchingItems(trait:getLabel());
    end

    for i = 0, trait:getFreeTraits():size() - 1 do
        local freeTrait = TraitFactory.getTrait(trait:getFreeTraits():get(i));
        self.freeTraits:add(freeTrait:getLabel())
        local freeTraitDescription = freeTrait:getDescription()
        freeTraitDescription = MDFT.addDescriptionToTooltip(freeTrait, freeTraitDescription)
        self.listboxTraitSelected:addUniqueItem(freeTrait:getLabel(), freeTrait, freeTraitDescription);
        self:doTestForMutuallyExclusiveTraits(freeTrait, false);
    end

    self:doTestForMutuallyExclusiveTraits(trait, false);
    self:repopulateTraitLists()
end

Events.OnGameBoot.Add(function()
    Events.OnGameBoot.Add(MDFT.processMoreDescriptionsForTraits);
    Events.OnResetLua.Add(MDFT.processMoreDescriptionsForTraits);
end) 