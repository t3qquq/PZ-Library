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

function Recipe.GetItemTypes.Scissors(scriptItems)
    scriptItems:addAll(getScriptManager():getItemsTag("SharpKnife"))
    scriptItems:addAll(getScriptManager():getItemsTag("Scissors"))
end