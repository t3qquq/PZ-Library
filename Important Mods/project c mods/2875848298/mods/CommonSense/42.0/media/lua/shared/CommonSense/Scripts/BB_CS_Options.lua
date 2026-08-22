-- ************************************************************************
-- **        ██████  ██████   █████  ██    ██ ███████ ███    ██          **
-- **        ██   ██ ██   ██ ██   ██ ██    ██ ██      ████   ██          **
-- **        ██████  ██████  ███████ ██    ██ █████   ██ ██  ██          **
-- **        ██   ██ ██   ██ ██   ██  ██  ██  ██      ██  ██ ██          **
-- **        ██████  ██   ██ ██   ██   ████   ███████ ██   ████          **
-- ************************************************************************
-- ** All rights reserved. This content is protected by © Copyright law. **
-- ************************************************************************

CommonSense = {}
CommonSense.PryingTools = { "Base.Crowbar", "Base.CrowbarForged" }

--- Add an item (Equipable ONLY) to the list of items that can be used to pry stuff open.
---@param toolID string
CommonSense.AddPryingTool = function(toolID)
	table.insert(CommonSense.PryingTools, toolID)
	print("Added tool to list successfuly!")
end

--- Remove an item from the list of items that can be used to pry stuff open.
---@param toolID string
CommonSense.RemovePryingTool = function(toolID)

	  for k, v in pairs(CommonSense.PryingTools) do
		if v == toolID then
			table.remove(CommonSense.PryingTools, k)
			break
		end
  	end

	print("Removed tool from list successfuly!")
end