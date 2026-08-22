require "SSRLoader"
require "ExamineUI/Examine"

if SSRLoader.QSystem then
    require "Scripting/Objects/Command"
    -- Syntax:
    -- #examine media/ui/Poster.png|"What the fuck is this?"
    -- #examine SuspiciousMagazine
    local command = Command:derive("examine")
    function command:execute(sender)
        self:debug()
        local function callback()
            sender.input.enable = true;
            sender:showNext();
        end
        local id = tostring(self.args[1]);
        -- try create examine panel for preloaded image
        if #self.args == 1 then
            local entry = ExamineEntries.getEntry(id);
            if entry then
                Examine.create(id, entry.images, entry.texts, callback);
                return -2;
            end
        end
        -- try create examine panel from path to image (single page item only)
        self.args[1] = self.args[1]:ssplit(',');
        local image = ExamineEntries.image2table(self.args[1][1]);
        if image then
            -- add text if specified
            if #self.args == 2 then
                Examine.create(tostring(self.args[1][1]), image, tostring(self.args[2]), callback);
            else
                Examine.create(tostring(self.args[1][1]), image, "", callback);
            end
        else
            return "Unable to load image '"..tostring(id).."' at line "..sender.script.index;
        end
        return -2;
    end

    table.insert(CommandList_a, command:new("examine", 1, 2, "DialoguePanel"))
end
