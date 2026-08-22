local CustomHealthActions = require("CustomHealthActions")

if MiniHealthTreatment then

    local originalDoBodyPartMenu = MiniHealthTreatment.doBodyPartContextMenu
    function MiniHealthTreatment:doBodyPartContextMenu(bodyPart, context)
        originalDoBodyPartMenu (self, bodyPart, context)

        local replaceBandage = CustomHealthActions.HReplaceBandage:new(self, bodyPart, mhpHandle.player)
        replaceBandage:checkItems()
        replaceBandage:addToMenu(context)

        --ReplaceBandage.createAndAddAction(self, CustomHealthActions.HReplaceBandage, bodyPart, context, mhpHandle.player)


        local replaceAndDisinfect = CustomHealthActions.HReplaceAndDisinfectBandage:new(self, bodyPart, mhpHandle.player)
        replaceAndDisinfect:checkItems()
        replaceAndDisinfect:addToMenu(context)


    end
end