--***********************************************************************
--         ██████  ██████   █████  ██    ██ ███████ ███    ██
--         ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██ 
--         ██████  ██████  ███████ ██    ██ █████   ██ ██  ██ 
--         ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██ 
--         ██████  ██   ██ ██   ██   ████   ███████ ██   ████          
--***********************************************************************
--**  A big thank you to "Batman-[FR]" for some of the code snippets.  **
--***********************************************************************

require "recipecode"

function Recipe.OnCreate.OpenJarredFood(items, result, player)
    local jar = items:get(0)
    local aged = jar:getAge() / jar:getOffAgeMax()

    result:setAge(result:getOffAgeMax() * aged)
    player:getInventory():AddItem("Base.EmptyJar")
    player:getInventory():AddItem("Base.JarLid")
  end

  function Recipe.GetItemTypes.Scissors(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SharpKnife"))
    scriptItems:addAll(getScriptManager():getItemsTag("Scissors"))
end

function Recipe.GetItemTypes.CanOpener(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("CanOpener"))
    Recipe.GetItemTypes.SharpKnife(scriptItems)
    Recipe.GetItemTypes.Screwdriver(scriptItems)
    Recipe.GetItemTypes.Spoon(scriptItems)
    Recipe.GetItemTypes.Fork(scriptItems)
end

local function searchForTinOpenerInBag(bagInv)
    local bagItems = bagInv:getItems()
    for x = 1, bagItems:size() do
        local bagItem = bagItems:get(x - 1)
        if not instanceof(bagItem, "InventoryContainer") then
            if bagItem:getType() == "TinOpener" then
                return true
            end
        end
    end

    return false
end

local function searchForTinOpener(item)
    local inventory = item:getOutermostContainer()
    if inventory then
        local invItems = inventory:getItems()
        for i = 1, invItems:size() do
            local invItem = invItems:get(i - 1)
            if not instanceof(invItem, "InventoryContainer") then
                if invItem:getType() == "TinOpener" then
                    return true
                end
            else
                local bagInv = invItem:getInventory()
                if bagInv then
                    if searchForTinOpenerInBag(bagInv) then
                        return true
                    end
                end
            end
        end
    end

    return false
end

function Recipe.OnTest.HasCanOpener(item, result)
    if searchForTinOpener(item) then
      local itemTags = item:getTags()
      for i = 1, itemTags:size() do
          local tag = itemTags:get(i - 1) or "None"
          if tag ~= "HasMetal" and tag ~= "CanOpener" then
            return false
        end
      end
  end

    return true
end