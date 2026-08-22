--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

require "ISUI/ISInventoryPane"

PerfumeContextMenu = {};

PerfumeContextMenu.doInventoryMenu = function(player, context, items, perfumeOrCologne)
	--print("PerfumeContextMenu: GETTING PLAYER")
    local thisPlayer = getSpecificPlayer(player)
	if not thisPlayer then return; end
	
	local playerData
	--print("PerfumeContextMenu: GETTING PLAYER MODDATA")
	if thisPlayer:hasModData() then
		playerData = thisPlayer:getModData()
	else
	return; end
	--print("PerfumeContextMenu: CHECKING LSMOODLES")
	if (not playerData.LSMoodles) or (not playerData.LSMoodles["SmellGood"]) then return; end
	
	local useDelta = 0.05
	local invItems = ISInventoryPane.getActualItems(items)
	--local item
	--for i,v in ipairs(invItems) do
	--	if ((v:getFullType() == 'Base.Perfume') or (v:getFullType() == 'Base.Cologne')) and (v:getUsedDelta() >= useDelta) then
	--		perfumeOrCologne = v
	--		break
	--	end
	--end

	if not perfumeOrCologne then
		--print("PERFUME NOT FOUND")
		return
	end

	----ApplyPerfume

	local doApplyOption = context:addOptionOnTop(getText("ContextMenu_H_PerfumeUse"),
	false,
	PerfumeContextMenu.onAction,
	thisPlayer,
	perfumeOrCologne,
	useDelta);

	local tooltipUse = ISToolTip:new();
	tooltipUse:initialise();
	tooltipUse:setVisible(false);

	description = getText("Tooltip_H_PerfumeUse");
	tooltipUse.description = description
	doApplyOption.toolTip = tooltipUse
	doApplyOption.iconTexture = getTexture('media/ui/perfume_icon.png')
	if playerData.LSMoodles["SmellGood"] and (playerData.LSMoodles["SmellGood"].Value >= 0.8) then
		doApplyOption.notAvailable = true;
		description = " <RED>" .. getText("Tooltip_H_PerfumeMax");
		tooltipUse.description = description
		doApplyOption.toolTip = tooltipUse
		doApplyOption.iconTexture = getTexture('media/ui/perfumeNo_icon.png')
	end
------
end

local function doPerfumeItemTransfer(player, PerfumeItem)

	if instanceof(PerfumeItem, "InventoryItem") then
		if luautils.haveToBeTransfered(player, PerfumeItem) then
			ISTimedActionQueue.add(ISInventoryTransferAction:new(player, PerfumeItem, PerfumeItem:getContainer(), player:getInventory()))
		end
		return true
	elseif instanceof(PerfumeItem, "ArrayList") then
		local items = PerfumeItem
		for i=1,items:size() do
			local item = items:get(i-1)
			if luautils.haveToBeTransfered(player, item) then
				ISTimedActionQueue.add(ISInventoryTransferAction:new(player, item, item:getContainer(), player:getInventory()))
			end
		end
		return true
	end

	return false

end

PerfumeContextMenu.onAction = function(worldobjects, player, Item, UseDelta)
	local LSApplyPerfumeAction = require "TimedActions/LSApplyPerfumeAction"
	if Item and doPerfumeItemTransfer(player, Item) then
		ISTimedActionQueue.add(LSApplyPerfumeAction:new(player, Item, UseDelta));
	end
end

--Events.OnFillInventoryObjectContextMenu.Add(PerfumeContextMenu.doInventoryMenu);