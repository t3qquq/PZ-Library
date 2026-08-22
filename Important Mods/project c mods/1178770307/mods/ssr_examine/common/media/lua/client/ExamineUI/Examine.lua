--***********************************************************
--**         ORIGINAL SCRIPTS BY ONELINE/D.BOROVSKY        **
--***********************************************************
require "ExamineUI/ExaminePanel"
require "ISUI/ISModalDialogMod"

Examine = {};
Examine.panel = nil;
Examine.restricted = false -- check player's age?

SPageTable = {}; -- Single page items
MPageTable = {}; -- Multi page items
RestrictedTable = {}; -- NSFW items

function Examine.create(item_type, image, text, callback)
	Examine.panel = ExaminePanel:new(item_type, image, text, callback);
	Examine.panel:initialise();
	Examine.panel:addToUIManager();
end

local function config_validate(line)
	local arr = line:ssplit("="); -- splits the string into several parts

	if #arr < 1 then -- if one part is missing, parameter is invalid
		return false
	elseif #arr > 1 then -- otherwise continue
		local value = line:substring(arr[1]:len() + 2);  -- make local var with value part

		if value:indexOf('"') == 1 then -- check, if this is supposed to be string
			local count = value:count('"');
			if count ~= 2 or value:lastIndexOf('"') ~= value:len() - 1 then -- make sure it has only two " instances
				return false
			end
		end

		if value ~= "true" and value ~= "false" then -- if this isn't boolean parameter, check, if it's items list
			arr = value:ssplit(";"); -- split items

			for i=1,#arr do -- check every item
				if arr[i]:count(".") ~= 1 then -- item must have single dot
					return false
				end

				local item = arr[i]:ssplit("."); -- split item into two parts

				local Module = ScriptManager.instance:getModule(item[1]) -- 1st parts must be a module
				if Module:getName() ~= item[1] then -- if module doesn't exist it will have 'Base' value. not yours
					return false
				end

				local Item = Module:getItem(item[2]) -- 2nd part must be a name of item, that exists in our module
				if not Item then
					return false
				end
			end
		end
	end

	return true -- if everything is cool, return true
end

local function config_load(mod, file)
	local params = {}
	local reader = getModFileReader(mod, file, false);

	if reader then
		local line = reader:readLine();
		while line do
			if config_validate(line) then
				local n = (line:ssplit("="))[1];
				local v = line:substring(n:len() + 2);
				table.insert(params, {name = n, value = v});
			end

			line = reader:readLine();
		end
		reader:close();
	else
		print("File '"..tostring(file).."' doesn't exist in the directory of mod '"..tostring(mod).."'");
	end

	return params
end

Examine.initialize = function(mod_id)
	local params = config_load(mod_id, "config.ini");

	if params then -- only if 
		-- "params" is table of format:
		-- { {name = "param1", value = "value1"}, ... }
		for i=1, #params do
			local parameter = params[i];
			if parameter.name == "ageCheck" then
				if not Examine.restricted then -- if at least one of mods has 'true', it can't be overwritten
					if parameter.value == "true" then
						Examine.restricted = true;
					elseif parameter.value == "false" then
						Examine.restricted = false;
						--break;
					end
				end
			elseif parameter.name == "restrictedItems" then
				local items = parameter.value:ssplit(";");
				for j=1, #items do
					table.insert(RestrictedTable, items[j]);
				end
			end
		end

		if Examine.restricted then
			ExamineModal.create();
		end
	end
end

function Examine.close(key)
	if key == 1 and Examine.panel then
		if Examine.panel.callback then
			Examine.panel.callback();
		end
		Examine.panel:removeFromUIManager();
		Examine.panel = nil;
	else
		ToggleEscapeMenu(key);
	end
end

-- Don't open pause menu, when examining the item
Events.OnKeyPressed.Remove(ToggleEscapeMenu);
Events.OnKeyPressed.Add(Examine.close)

function Examine.onResolutionChange()
	if Examine.panel then
		local item_type = Examine.panel.item_type;
		local image = Examine.panel.image;
		local text = Examine.panel.text;

		Examine.panel:removeFromUIManager()
		Examine.panel = nil;

		Examine.create(item_type, image, text);
	end
end

Events.OnResolutionChange.Add(Examine.onResolutionChange);



ExamineModal = {};
ExamineModal.instance = nil;
ExamineModal.explicit = nil;

function ExamineModal.onConfirmModalClick(sender, button)
	if button.internal == "YES" then
		ExamineModal.explicit = true
	else
		ExamineModal.explicit = false
	end
end

function ExamineModal.create()
	if ExamineModal.instance then return end

	local width = 500
	local height = 400

	local x = ((getCore():getScreenWidth() / 2) - (width / 2));
	local y = ((getCore():getScreenHeight() / 2) - (height / 2))

	ExamineModal.instance = ISModalDialogMod:new(x, y, width, height, true, nil, ExamineModal.onConfirmModalClick);
	ExamineModal.instance:initialise()
	ExamineModal.instance:setCapture(true)
	ExamineModal.instance:setAlwaysOnTop(true)
	ExamineModal.instance:addToUIManager()

	ExamineModal.instance.text = getText("UI_Text_isAdult");
	ExamineModal.instance:paginate();
	ISLayoutManager.RegisterWindow('explicitmodal', ExamineModal, ExamineModal.instance)
end

function ExamineModal.RestoreLayout(sender, name, layout)
	if layout.visible == 'true' then
		ExamineModal.instance:setVisible(false)
		ExamineModal.explicit = true;
    elseif layout.visible == 'false' then
		ExamineModal.instance:setVisible(false)
        ExamineModal.explicit = false;
    end

	print("EXPLICIT CONTENT IS "..tostring(ExamineModal.explicit))
end

function ExamineModal.SaveLayout(sender, name, layout)
    if ExamineModal.explicit ~= nil then
		if ExamineModal.explicit then
			layout.visible = 'true';
		elseif not ExamineModal.explicit then
			layout.visible = 'false';
		end
	end
    ISLayoutManager.SaveWindowVisibleSSR(ExamineModal.explicit, layout);
end

ExamineModal.onKeyPressed = function(key)
	if key == 67 then -- F9
		if ExamineModal.instance then
			ExamineModal.instance:setVisible(true);
		end
	end
end

Events.OnKeyPressed.Add(ExamineModal.onKeyPressed)



ExamineContext = {}

ExamineContext.isValid = function(item, list)
	for j=1,#list do
		if item:getModule().."."..item:getType() == list[j] and not Examine.panel then
			return true;
		end
	end

	return false;
end

ExamineContext.createMenu = function(player, context, items)

    local playerObj = getSpecificPlayer(player)

	for i,item in ipairs(items) do

        if not instanceof(item, "InventoryItem") then
            item = item.items[1];
        end

		if Examine.restricted then
			if ExamineContext.isValid(item, RestrictedTable) and not ExamineModal.explicit then
				return
			end
		end

		if ExamineContext.isValid(item, SPageTable) then
			context:addOption(getText("ContextMenu_Examine"), item, ExamineContext.onExamine, playerObj, true);
			break
		end

		if ExamineContext.isValid(item, MPageTable) then
			context:addOption(getText("ContextMenu_Examine"), item, ExamineContext.onExamine, playerObj, false);
			break
		end

	end

end

ExamineContext.onExamine = function(item, player, singlePage)
	local item_type = item:getType();
	local entry = ExamineEntries.getEntry(item_type);
	if entry then
		Examine.create(item_type, entry.images, entry.texts);
	else
		print(string.format("[Examine] (Error) Problem with image for item '%s'", item_type));
	end
end

Events.OnFillInventoryObjectContextMenu.Add(ExamineContext.createMenu);