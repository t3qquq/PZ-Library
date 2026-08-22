-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

local compatify = false
local exceptionIDs = { "Advanced_trajectory", "TheStar", "Arsenal(26)GunFighter", "Arsenal(26)GunFighter[MAIN MOD 2.0]" }
local ammoTexture = getTexture("Item_PistolAmmo")
local UIAssets = {}
local UIVisible = true
local ammoList = {}
local ammoWordBlacklist = {
    "Box",
    "Carton",
    "Clip",
    "Mold",
}

local function setAssetState(newState)
    for _, asset in ipairs(UIAssets) do
        asset:setVisible(newState)
    end

    UIVisible = newState
end

local function updateAmmoTexture(gun)
    if not UIAssets[1] then return end

    local ammoType = gun:getAmmoType() or ""
    UIAssets[1].texture = nil

    for _, ammo in ipairs(ammoList) do
        if ammo.type == ammoType then
            UIAssets[1].texture = ammo.texture
            break
        end
    end

    if UIAssets[1].texture == nil then
        UIAssets[1].texture = ammoTexture
    end
end

local function updateAmmoStyle(gun)
    if not UIAssets[2] then return end

    local ammoCount = gun:getCurrentAmmoCount() or 0
    if gun:isRoundChambered() then ammoCount = ammoCount + 1 end
    UIAssets[2].name = tostring(ammoCount)

    local maxAmmoCount = gun:getMaxAmmo() or 0
    local ammoPercentage = ammoCount / maxAmmoCount

    if ammoPercentage > 0.5 then
        UIAssets[2].r = 1
        UIAssets[2].g = 1
        UIAssets[2].b = 1
    elseif ammoPercentage > 0.25 then
        UIAssets[2].r = 1
        UIAssets[2].g = 1
        UIAssets[2].b = 0
    elseif ammoPercentage > 0 then
        UIAssets[2].r = 1
        UIAssets[2].g = 0
        UIAssets[2].b = 0
    else
        UIAssets[2].r = 0.7
        UIAssets[2].g = 0
        UIAssets[2].b = 0
    end
end

local leftUIBarRender = ISEquippedItem.render

function ISEquippedItem:render()
    leftUIBarRender(self)
    if not SandboxVars.CommonSense.GunStats then return end
    if compatify then return end
    if #UIAssets < 2 then return end

    local primaryItem = self.chr:getPrimaryHandItem()
    if primaryItem and primaryItem:IsWeapon() and primaryItem:isRanged() and primaryItem:getAmmoType() then

        updateAmmoTexture(primaryItem)
        updateAmmoStyle(primaryItem)

        if not UIVisible then
            setAssetState(true)
        end

    elseif UIVisible then
        setAssetState(false)
    end
end

local leftUIBarInit = ISEquippedItem.initialise

function ISEquippedItem:initialise()

	local menu = leftUIBarInit(self)

    if SandboxVars.CommonSense.GunStats and not compatify then
        local scaleFactor = 3
        local alpha = 0.5

        local ammoImage = ISImage:new(
        self.mainHand:getX() + 48,
        self.mainHand:getY() + 8,
        self.mainHand:getWidth() / scaleFactor,
        self.mainHand:getHeight() / scaleFactor,
        nil)

        ammoImage.backgroundColor = { r = 1, g = 1, b = 1, a = alpha }
        ammoImage:initialise()
        ammoImage.parent = self
        self:addChild(ammoImage)

        local ammoText = ISLabel:new(ammoImage:getX() + 40, ammoImage:getY() - 16, 64, "", 1, 1, 1, alpha, UIFont.Medium, true)
        ammoText:initialise()
        ammoText.parent = self
        self:addChild(ammoText)

        table.insert(UIAssets, ammoImage)
        table.insert(UIAssets, ammoText)

        local primaryItem = getPlayer():getPrimaryHandItem()
        if primaryItem and primaryItem:IsWeapon() and primaryItem:isRanged() and primaryItem:getAmmoType() then
            updateAmmoTexture(primaryItem)
        end
    end

	return menu
end

local function fetchAllAmmoTypes()
    local allScriptItems = getScriptManager():getAllItems()

    for i = 1, allScriptItems:size() do
        local scriptItem = allScriptItems:get(i - 1)
        local itemName = scriptItem:getFullName()

        if scriptItem:getType() == Type.Normal and scriptItem:getDisplayCategory() == "Ammo" then
            local invalid = false
            for _, blockedWord in ipairs(ammoWordBlacklist) do
                if string.find(itemName, blockedWord) then
                    invalid = true
                end
            end

            if not invalid then
                local textureData = scriptItem:getNormalTexture()
                local texture = nil

                if textureData then
                  local texName = textureData.name or ""
                  if texName then
                    local textureString = tostring(textureData)
                    local pattern = "name:\"(.*)\""
                    local match = string.match(textureString, pattern)
                    if match then
                      texName = match:sub(1, -1)
                    end
                  end

                  texture = getTexture(texName)
                end

                local ammo = { type = itemName, texture = texture }
                print(itemName)
                table.insert(ammoList, ammo)
            end
        end
    end
end

local function onEarliestStart()
    if not SandboxVars.CommonSense.GunStats then return end
    if compatify then return end

    local activatedMods = getActivatedMods()
    if activatedMods then
        for _, modID in ipairs(exceptionIDs) do
            if activatedMods:contains(modID) then
                compatify = true
                return
            end
        end
    end

    fetchAllAmmoTypes()
end

Events.OnInitGlobalModData.Add(onEarliestStart)