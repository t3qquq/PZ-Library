---------------------------------
---     Coded by Sioyth       ---
--- https://github.com/Sioyth ---
---------------------------------

local BaseHandler = require("BaseHandler")
local HReplaceBandage = BaseHandler:derive("HReplaceBandage")

function HReplaceBandage:new(panel, bodyPart, playerId)
    local o = BaseHandler.new(self, panel, bodyPart, playerId)
    o.items.BANDAGES = {}
    return o
end

function HReplaceBandage:checkItem(item)
    if item:getBandagePower() > 0 then
        self:addItem(self.items.BANDAGES, item)
    end
end
function HReplaceBandage:addToMenu(context)

    -- Make sure we have bandages before adding it to menu
    local types = self:getAllItemTypes(self.items.BANDAGES)

    if #types > 0 and self.bodyPart:bandaged() then
        local option = context:addOption(getText("IGUI_Replace_Bandage"), nil)
        local subMenu = context:getNew(context)
        local bandageType = self.bodyPart:getBandageType()
        if bandageType then
            option.itemForTexture = instanceItem(bandageType)
        end

        context:addSubMenu(option, subMenu)
        for i=1,#types do
            local item = self:getItemOfType(self.items.BANDAGES, types[i])
            local subMenuOption = subMenu:addOption(item:getName(), self, self.onMenuOptionSelected, types[i])
            subMenuOption.itemForTexture = item
        end
    end
end

function HReplaceBandage:dropItems(items)
    local types = self:getAllItemTypes(items)
    if #self.items.BANDAGES > 0 and #types == 1 and self.bodyPart:damaged() then
        self:onMenuOptionSelected(types[1])
        return true
    end
    return false
end

function HReplaceBandage:isValid(itemType)
    self:checkItems()
    return self:getItemOfType(self.items.BANDAGES, itemType) and self.bodyPart:bandaged()
end

function HReplaceBandage:perform(previousAction, itemType)
    local removeBandage = ISApplyBandage:new(self:getDoctor(), self:getPatient(), nil, self.bodyPart, false)
    ISTimedActionQueue.addAfter(previousAction, removeBandage)

    local item = self:getItemOfType(self.items.BANDAGES, itemType)
    previousAction = self:toPlayerInventory(item, previousAction)
    local addBandage = ISApplyBandage:new(self:getDoctor(), self:getPatient(), item, self.bodyPart, true)
    ISTimedActionQueue.addAfter(removeBandage, addBandage)
end

---------------------------------
--- Replace & Disinfect Bandage
---------------------------------

local HReplaceAndDisinfectBandage = BaseHandler:derive("HReplaceAndDisinfectBandage")

function HReplaceAndDisinfectBandage:new(panel, bodyPart, playerId)
    local o = BaseHandler.new(self, panel, bodyPart, playerId)
    o.items.BANDAGES = {}
    o.items.DISINFECTANT = {}
    return o
end

function HReplaceAndDisinfectBandage:checkItem(item)

    if item:getBandagePower() > 0 then
        self:addItem(self.items.BANDAGES, item)
    end

    if item:hasComponent(ComponentType.FluidContainer) then
        local fluidContainer = item:getFluidContainer()
            if (fluidContainer:getProperties():getAlcohol() / fluidContainer:getAmount() + 0.001 >= 0.4) and (fluidContainer:getAmount() > 0.15) then
                self:addItem(self.items.DISINFECTANT, item)
            end
    elseif item:IsDrainable() and (item:getAlcoholPower() == 4.0) then
        self:addItem(self.items.DISINFECTANT, item)
    end
end

function HReplaceAndDisinfectBandage:addToMenu(context)
    local types = self:getAllItemTypes(self.items.BANDAGES)

    if #types > 0 and self.bodyPart:bandaged() and #self.items.DISINFECTANT > 0 then
        local option = context:addOption(getText("IGUI_Replace_&_Disinfect"), nil)
        local subMenu = context:getNew(context)
        context:addSubMenu(option, subMenu)

        local disinfectant = ReplaceBandage:GetBestDisinfectant(self.items.DISINFECTANT)
        option.itemForTexture = disinfectant

        for i=1,#types do
            local bandage = self:getItemOfType(self.items.BANDAGES, types[i])

            local subMenuOption = subMenu:addOption(bandage:getName(), self, self.onMenuOptionSelected, types[i])
            subMenuOption.itemForTexture = bandage
        end
    end
end

function HReplaceAndDisinfectBandage:dropItems(items)
    local types = self:getAllItemTypes(items)
    if #self.items.BANDAGES > 0 and #types == 1 and self.bodyPart:damaged() then
        self:onMenuOptionSelected(types[1])
        return true
    end
    return false
end

function HReplaceAndDisinfectBandage:isValid(itemType)

    self:checkItems()
    return self:getItemOfType(self.items.BANDAGES, itemType) and self.bodyPart:bandaged() and #self.items.DISINFECTANT > 0
end

function HReplaceAndDisinfectBandage:perform(previousAction, itemType)
    local removeBandage = ISApplyBandage:new(self:getDoctor(), self:getPatient(), nil, self.bodyPart, false)
    ISTimedActionQueue.addAfter(previousAction, removeBandage)

    local disinfectant = ReplaceBandage:GetBestDisinfectant(self.items.DISINFECTANT)
    local getItem = self:toPlayerInventory(disinfectant, removeBandage)

    local disinfect = ISDisinfect:new(self:getDoctor(), self:getPatient(), disinfectant, self.bodyPart)
    ISTimedActionQueue.addAfter(getItem, disinfect)

    local item = self:getItemOfType(self.items.BANDAGES, itemType)
    previousAction = self:toPlayerInventory(item, previousAction)
    local addBandage = ISApplyBandage:new(self:getDoctor(), self:getPatient(), item, self.bodyPart, true)
    ISTimedActionQueue.addAfter(disinfect, addBandage)
end

return
{
    HReplaceBandage = HReplaceBandage,
    HReplaceAndDisinfectBandage = HReplaceAndDisinfectBandage,
}