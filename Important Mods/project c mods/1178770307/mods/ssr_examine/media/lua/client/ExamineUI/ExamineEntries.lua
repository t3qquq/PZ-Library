--***********************************************************
--**         ORIGINAL SCRIPTS BY ONELINE/D.BOROVSKY        **
--***********************************************************
require "Util/LuaList"

ExamineEntries = {}

ExamineEntries.list = LuaList:new();

ExamineEntries.image2table = function(image)
	local status, texture = pcall(getTexture, image);
	if status and texture then
		return { image, texture:getWidth(), texture:getHeight() }
	end
end

local function fixImagePath(path)
	path = path:trim();
	local splitter = string.find(path, ":");
	if splitter then
		path = string.sub(path, splitter+1);
		path = string.sub(path, 1, string.len(path) - 1);
	end
	return path;
end

ExamineEntries.addEntry = function (name, images, texts)
	if type(images) == "table" then
		for i=1, #images do
			local image = ExamineEntries.image2table(images[i]);
			if images[i] then
				images[i] = image;
			else
				print("[Examine] Problem with image "..tostring(images[i]));
				return;
			end
		end
	else
		local image = ExamineEntries.image2table(images);
		if image then
			images = image;
		else
			print("[Examine] Problem with image "..tostring(images));
			return;
		end
	end

    local entry = {name = name, images = images, texts = texts};
    ExamineEntries.list:add(entry);
end

ExamineEntries.getEntry = function(item)
	local num = nil;
	for i=0, ExamineEntries.list:size() -1 do
		if ExamineEntries.list:get(i).name == item then
			num = i;
			break;
		end
	end

	if num then
    	return ExamineEntries.list:get(num);
	end
end

ExamineEntries.getEntryCount = function()
    return ExamineEntries.list:size();
end

