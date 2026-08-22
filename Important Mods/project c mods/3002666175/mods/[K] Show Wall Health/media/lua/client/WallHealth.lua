-- require "shared/WallHealthSettings"

KAMER_WallHealth = KAMER_WallHealth or {}
KAMER_WallHealth.trashcan = {
    menu = nil,
}
---------------------------------------------------------------------------------------------------------------------------------
--                                  Start Checking For Object/Send Data to Server.
---------------------------------------------------------------------------------------------------------------------------------

function KAMER_WallHealth.Check(PlayerIN, context, worldObjects, test)
    if not context or context == nil then return end;
    local saveK = {};
    local d = 0;
	local k_sandbox = SandboxVars.Kamer_ShowWallHealth
	
    for _, k in ipairs(worldObjects) do
        local walldata = k:getModData()
        if ((instanceof(k, "IsoThumpable") or instanceof(k, "IsoDoor")) and walldata.wallType ~= "doorframe") and saveK[k] == nil then
			-- Modular Show Status Menu
			if k_sandbox.visibility == 3 then
			elseif k_sandbox.visibility == 2 and (isAdmin() or isDebugEnabled()) then
				if d == 0 then
					d = d + 1
					local SubMenuOption = nil;

					KAMER_WallHealth.trashcan.menu = ISContextMenu:getNew(context);
					-- Modular position
					if k_sandbox.position == 1 then
						SubMenuOption = context:addOptionOnTop(getText("ContextMenu_Kamer_ShowWallHealth_Check_Status"), worldObjects, nil)
					elseif k_sandbox.position == 2 then
						SubMenuOption = context:addOption(getText("ContextMenu_Kamer_ShowWallHealth_Check_Status"), worldObjects, nil)
					elseif k_sandbox.position == 3 then
						SubMenuOption = context:addOptionOnTop(getText("ContextMenu_Kamer_ShowWallHealth_Check_Status"), worldObjects, nil)
					end
					--
					-- Modular add icon
					if k_sandbox.showIcons then
						SubMenuOption.iconTexture = getTexture("media/ui/infoIcon.png");
					end
					--
					context:addSubMenu(SubMenuOption, KAMER_WallHealth.trashcan.menu);
				end
			elseif k_sandbox.visibility == 1 then
				if d == 0 then
					d = d + 1
					local SubMenuOption = nil;

					KAMER_WallHealth.trashcan.menu = ISContextMenu:getNew(context);
						-- Modular position
						if k_sandbox.position == 1 then
							SubMenuOption = context:addOptionOnTop(getText("ContextMenu_Kamer_ShowWallHealth_Check_Status"), worldObjects, nil)
						elseif k_sandbox.position == 2 then
							SubMenuOption = context:allocOption(getText("ContextMenu_Kamer_ShowWallHealth_Check_Status"), worldObjects, nil)
						elseif k_sandbox.position == 3 then
							SubMenuOption = context:addOptionOnTop(getText("ContextMenu_Kamer_ShowWallHealth_Check_Status"), worldObjects, nil)
						end
						--
						-- Modular add icon
					if k_sandbox.showIcons then
						SubMenuOption.iconTexture = getTexture("media/ui/infoIcon.png");
					end
					--
					context:addSubMenu(SubMenuOption, KAMER_WallHealth.trashcan.menu);
				end	
			end	
			--
            saveK[k] = true
            local square = k:getSquare()
            local o = {}
            o.playerIndex = PlayerIN
            o.index = k:getObjectIndex()
            o.health = k:getHealth()
            o.newHealth = 0
            o.x = square:getX()
            o.y = square:getY()
            o.z = square:getZ()
            if isClient() then
                sendClientCommand("KAMER_WallHealth_Client", "updateHealth", o);
            else
                KAMER_WallHealth.HandleClientRequestsK("KAMER_WallHealth_Server", "updateHealth", o)
            end
        end
    end
end

Events.OnFillWorldObjectContextMenu.Add(KAMER_WallHealth.Check)

---------------------------------------------------------------------------------------------------------------------------------
--                                  Handling Data From Server.
---------------------------------------------------------------------------------------------------------------------------------

-- local function immersiveHealth(newHealth, objMaxHealth, callback)

--     local procentageofHealth = math.floor((newHealth/objMaxHealth) * 100)
--     for tIndex, tValue in pairs(KAMER_WallHealthSettings.Text) do
--         if procentageofHealth < tIndex then
--             callback = tValue
--             break
--         end
--     end

--     return callback;
-- end

function KAMER_WallHealth.HandleClientRequestsK(module, command, args)

    if module ~= "KAMER_WallHealth_Server" then return end;

    if command == "updateHealth" then
        local Player = getSpecificPlayer(args.playerIndex)
        if not Player or Player == nil then return end;
        local GetSquare = Player:getCell():getGridSquare(args.x, args.y, args.z)
        if not GetSquare or GetSquare == nil then return end;
        local GetObjects = GetSquare:getObjects()
        local ob = GetObjects:get(args.index)
        local obwalldata = ob:getModData()
        if ob and ((instanceof(ob, "IsoThumpable") or instanceof(ob, "IsoDoor")) and obwalldata.wallType ~= "doorframe") then
            local objName = ob:getName() or ob:getProperties():Val("CustomName")
            if objName then
                local prepareName = "ContextMenu_" .. string.gsub(objName, " ", "_")
                objName = getText(prepareName)
                if objName == prepareName then
                    objName = ob:getProperties():Val("CustomName") or ob:getName() or "Unknown Object (Unnamed)"
                end
            else
                objName = "Unknown Object (Unnamed)"
            end

            if isClient() then
                ob:setHealth(args.newHealth)
            else
                args.newHealth = ob:getHealth()
            end
            
            if args.newHealth == 0 then return end

            local objMaxHealth = ob:getMaxHealth()
            if objMaxHealth and args.newHealth > objMaxHealth then
                if ob.setMaxHealth then
                    ob:setMaxHealth(args.newHealth)
                end
            end
    
            local Object = nil;
            if not KAMER_WallHealth.trashcan.menu or KAMER_WallHealth.trashcan.menu == nil or KAMER_WallHealth.trashcan.menu == "null" then return end;
            Object = KAMER_WallHealth.trashcan.menu:addOption(getText("ContextMenu_Kamer_ShowWallHealth_Check", objName), ob, KAMER_WallHealth.onObjectInfo, ob, Player)
            if Player:DistToSquared(args.x + 0.5, args.y + 0.5) < 2 * 2 then
                local tooltip = ISToolTip:new()
                local newobjMaxHealth = ob:getMaxHealth()

                tooltip.description = getText("ContextMenu_Kamer_ShowWallHealth_Health", args.newHealth, newobjMaxHealth);
                -- if KAMER_WallHealthSettings.immersive then
                --     local k_getThis = immersiveHealth(args.newHealth, newobjMaxHealth)
                --     local ConText = getText("ContextMenu_Kamer_ShowWallHealth_Condition")
                --     local tx = (getTextManager():MeasureStringX(tooltip.font, getText(k_getThis[1])) / 2) - (getTextManager():MeasureStringX(tooltip.font, ConText) / 2)

                --     tooltip.description = string.format("<SETX:%d>%s <SETX:0><LINE>%s%s", tx, ConText, getText(k_getThis[3]), getText(k_getThis[1]))
                --     tooltip.KamerWallHealth = true
                --     tooltip.KamerWallHealth_text = getText(k_getThis[1])
                --     Player:Say("Test")
                -- else
                -- end

                tooltip:setName(objName);
                Object.toolTip = tooltip;
            end
        end
    end
end

Events.OnServerCommand.Add(KAMER_WallHealth.HandleClientRequestsK)

---------------------------------------------------------------------------------------------------------------------------------
--                                  Pass on.
---------------------------------------------------------------------------------------------------------------------------------

function KAMER_WallHealth.onObjectInfo(self, k, Player)
    if luautils.walkAdjWindowOrDoor(Player, k:getSquare(), k) then
	ISTimedActionQueue.add(Kamer_WallHeathAction:new(Player, k))
    end
end



-- local kamer_oldTooltip = ISToolTip.doLayout
-- function ISToolTip:doLayout()
--     kamer_oldTooltip(self)

--     if self.KamerWallHealth then

-- 		local tx = getTextManager():MeasureStringX(self.font, self.KamerWallHealth_text) + 20
--         self:setWidth(tx)
--         self:setHeight(self.height)
--     end
-- end

