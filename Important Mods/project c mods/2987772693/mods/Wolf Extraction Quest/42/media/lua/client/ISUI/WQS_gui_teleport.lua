-- Minimal service GUI to teleport to any WQS_ExtractionPointsData location.
-- Client-only file: no risk of loading ISUI classes on the server.
print("WQS: Loading WQS_gui_teleport.lua ...")
--rw
require "ISUI/ISCollapsableWindow"

if not ISCollapsableWindow then
    print("WQS ERROR: ISCollapsableWindow not found after require!")
    return
end

WQS_TeleportWindow = ISCollapsableWindow:derive("WQS_TeleportWindow")
print("WQS: WQS_gui_teleport.lua loaded successfully")

function WQS_TeleportWindow:initialise()
    ISCollapsableWindow.initialise(self)
end

---Create a new WQS_TeleportWindow instance.
---@param x number window x position
---@param y number window y position
---@param width number window width
---@param height number window height
---@return table WQS_TeleportWindow instance
function WQS_TeleportWindow:new(x, y, width, height)
    local o = ISCollapsableWindow:new(x, y, width, height)
    setmetatable(o, self)
    self.__index = self
    o.title = "WQS Teleport"
    o.pin = true
    o.resizable = false
    o.backgroundColor = { r = 0, g = 0, b = 0, a = 0.8 }
    return o
end

---Build child widgets: one button per extraction location.
function WQS_TeleportWindow:createChildren()
    ISCollapsableWindow.createChildren(self)

    local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)
    local pad = 10
    local btnHgt = math.max(22, FONT_HGT_SMALL + 6)
    local winW = self:getWidth()
    local innerW = winW - pad * 2

    -- Collect valid extraction points (skip "random" placeholder)
    self.teleportEntries = {}
    local data = WQS_ExtractionPointsData
    if data then
        for i = 1, #data do
            local entry = data[i]
            if entry and entry.MapItem ~= "random" then
                table.insert(self.teleportEntries, {
                    x = entry.MapCenterAreaX,
                    y = entry.MapCenterAreaY,
                    z = entry.MapCenterAreaZ,
                    label = entry.MapGuiLabel or entry.MapItem or ("Location " .. tostring(i)),
                })
            end
        end
    end

    -- Create one button per location, stacked vertically
    local y = 30 + pad
    self.buttons = {}
    for _, entry in ipairs(self.teleportEntries) do
        local btn = ISButton:new(pad, y, innerW, btnHgt, entry.label, self,
            WQS_TeleportWindow.onTeleportClick)
        btn:initialise()
        btn:instantiate()
        btn.borderColor = WQS_Shared.BorderColor
        btn.backgroundColor = WQS_Shared.BGColor
        btn.backgroundColorMouseOver = WQS_Shared.BGColorMouseOver
        btn.entry = entry
        self:addChild(btn)
        table.insert(self.buttons, btn)
        y = y + btnHgt + 2
    end

    -- Resize window to fit content
    local totalH = y + pad
    self:setHeight(totalH)
end

---Teleport the player to the location of the clicked button.
function WQS_TeleportWindow:onTeleportClick(button)
    local entry = button.entry
    if entry then
        print("WQS Teleport -> " .. tostring(entry.label) ..
            " (" .. entry.x .. "," .. entry.y .. "," .. entry.z .. ")")
        WQS_Shared.teleport(entry.x, entry.y, entry.z)
    end
end

function WQS_TeleportWindow:close()
    self:setVisible(false)
    self:removeFromUIManager()
    WQS_TeleportWindow.instance = nil
end

---Opens a minimal GUI to teleport to any WQS_ExtractionPointsData location.
function WQS_OpenTeleportGui()
    print("WQS: WQS_OpenTeleportGui called")
    -- Close existing instance if open
    if WQS_TeleportWindow.instance then
        WQS_TeleportWindow.instance:close()
    end

    local winW = 260
    local winH = 200 -- will be auto-resized in createChildren
    local x = 50
    local y = 100

    local win = WQS_TeleportWindow:new(x, y, winW, winH)
    win:addToUIManager()
    win:setVisible(true)
    WQS_TeleportWindow.instance = win
end

-- Also expose globally to ensure console access
_G["WQS_OpenTeleportGui"] = WQS_OpenTeleportGui
