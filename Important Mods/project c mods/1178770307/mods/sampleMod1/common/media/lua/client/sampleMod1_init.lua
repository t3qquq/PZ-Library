--***********************************************************
--**         ORIGINAL SCRIPTS BY ONELINE/D.BOROVSKY        **
--***********************************************************
require "ExamineUI/Examine"
require "ExamineUI/ExamineEntries"

local mod_id = "ssr-example1"; -- this mod's id

local function bind()
    local SuspiciousMagazine_pages = {
        "media/ui/sample/01.png",
        "media/ui/sample/02.png",
        "media/ui/sample/03.png",
        "media/ui/sample/04.png",
        "media/ui/sample/05.png",
        "media/ui/sample/06.png",
        "media/ui/sample/07.png",
        "media/ui/sample/08.png"
    };

    local SuspiciousMagazine_texts = {}
    SuspiciousMagazine_texts[1] = getText("UI_Text_SuspiciousMagazine_01")
    SuspiciousMagazine_texts[8] = getText("UI_Text_SuspiciousMagazine_08")

    ExamineEntries.addEntry("SuspiciousMagazine", SuspiciousMagazine_pages, SuspiciousMagazine_texts);

    ExamineEntries.addEntry("Poster", "media/ui/Poster.png", getText("UI_Text_Poster"));

    table.insert(SPageTable, "ssr.Poster");
    table.insert(MPageTable, "ssr.SuspiciousMagazine");
end

Events.OnGameStart.Add(function ()
    if Examine then
        bind(); -- binds images to items
        Examine.initialize(mod_id); -- loads config file from your mod directory
    end
end);