require "ISUI/ISToolTipInv"

local default = ISToolTipInv.render
local item = nil
local numRows = 0
local chargeText = nil
local charge = nil
local charge2 = nil
local triggerType = nil

function ISToolTipInv:render()
	numRows = 0
	if self.item ~= nil then
		item = self.item

		if (instanceof(item,"HandWeapon")) and (item:isAimedFirearm()) and (not isLauncher(item)) then
			------------------------------------------------------------------
			--	IF GAME DOES NOT GIVE MAG, BUT PRE-ATTACH UPGRADED MAG		--
			------------------------------------------------------------------
			if	item:isContainsClip() == false and item:getClip() ~= nil then
			--	DebugSay(2,"[ITEM] Insert Generated Mag...")
				item:setContainsClip(true)				-- INSERT ONE
			end
			------------------------------------------------------------------
			--	IF MAG IS INSERTED BUT CURRENT MAGTYPE SET DOES NOT MATCH	--
			------------------------------------------------------------------
			if	item:isContainsClip() and item:getClip() ~= nil then
				local	resetMag = nil
			--	if		(string.find(item:getClip():getType(), "Standard_Mag"))	and (item:getMagazineType() ~= nil) then
			--			resetMag = item:getMagazineType()
				if		(string.find(item:getClip():getType(), "Extended_Mag"))	and (item:getModData().ExtMagType ~= item:getMagazineType()) then
				--		DebugSay(2,"[ITEM] Extended Mag Detected...")
						resetMag = item:getModData().ExtMagType
				elseif	(string.find(item:getClip():getType(), "Drum_Mag"))		and (item:getModData().DrumMagType ~= item:getMagazineType()) then
				--		DebugSay(2,"[ITEM] Drum Mag Detected...")
						resetMag = item:getModData().DrumMagType
				end
				----------------------------------
				--	RESET IF NON-MATCH FOUND	--
				----------------------------------
				if resetMag ~= nil then
				--	DebugSay(2,"[ITEM] Update Mag Type...")
					item:setMagazineType(resetMag)	-- SET TO UPGRADED TYPE
					if		item:getMagazineType() ~= nil then
							item:setMaxAmmo(InventoryItemFactory.CreateItem(item:getMagazineType()):getMaxAmmo())
					else	item:setMaxAmmo(item:getMaxAmmo())
					end						
				end
			end
		end
		
		------------------------------------------
		--	WORKS BUT GOTTA BE A BETTER WAY		--
		------------------------------------------
		if	(instanceof(item,"HandWeapon")) and item:isAimedFirearm() and item:getModData().TriggerType ~= nil then
		--	if	not item:getFireModePossibilities():contains(item:getFireMode()) then
				Update_TriggerGroup(item)
		--		DebugSay(2,"Updating Trigger Group from ToolTip")
		--	end
		end
		
		getPlayer()
		if item and instanceof(item, "WeaponPart") and ( item:hasTag("Light") or item:hasTag("Laser") or item:hasTag("Multi_Laser") ) then		-- UNATTACHED PART
			charge2 = nil
			triggerType = nil
			if	item:getModData().Charge == nil then
				item:getModData().Charge = 0
			end
			charge = round(item:getModData().Charge,1)
		elseif item and instanceof(item, "HandWeapon") and item:isAimedFirearm() then 									-- ATTACHED PART
			triggerType = nil
			local stock = item:getStock()
			local sling = item:getSling()
			if stock and ( stock:hasTag("Light") or stock:hasTag("Laser") or stock:hasTag("Multi_Laser") ) then	-- NEEDS POWER
				if	stock:getModData().Charge == nil then														-- CAN SHOW EMPTY
					stock:getModData().Charge = 0
				end
				charge = round(stock:getModData().Charge,1)
				----------------------------------------------------------
				--	NEEDED FOR NON-EQUIPPED INVENTORY OR GUNS PLACED	--
				--	ATTACHMENT MODDATA NOT SAVED, ONLY GUN MODDATA		--
				----------------------------------------------------------
				if charge == 0 and item:getModData().Charge ~= nil then			-- FOR ITEMS NOT IN HAND
					charge = round(item:getModData().Charge,1)
					stock:getModData().Charge = charge
				end
				----------------------------------------------------------
			else charge = nil
			end
			
			if sling and ( sling:hasTag("Light") or sling:hasTag("Laser") or sling:hasTag("Multi_Laser") ) then	-- NEEDS POWER
				if	sling:getModData().Charge == nil then														-- CAN SHOW EMPTY
					sling:getModData().Charge = 0
				end
				charge2 = round(sling:getModData().Charge,1)
				----------------------------------------------------------
				--	NEEDED FOR NON-EQUIPPED INVENTORY OR GUNS PLACED	--
				--	ATTACHMENT MODDATA NOT SAVED, ONLY GUN MODDATA		--
				----------------------------------------------------------
				if charge2 == 0 and item:getModData().Charge2 ~= nil then		-- FOR ITEMS NOT IN HAND
					charge2 = round(item:getModData().Charge2,1)
					sling:getModData().Charge = charge2
				end
				----------------------------------------------------------
			else charge2 = nil
			end
		elseif item:hasTag("TriggerGroup") then		-- TRIGGER GROUP TYPE
			charge = nil
			if item:getModData().TriggerType ~= nil then
				triggerType = item:getModData().TriggerType
			end			
		else
			return default(self)
		end
		
		if		charge ~= nil and charge2 ~= nil then
				numRows = 1
				chargeText = getText("ContextMenu_AttachmentCharge").." : " .. charge .. " / " .. charge2
		elseif	charge ~= nil then
				numRows = 1
				chargeText = getText("ContextMenu_AttachmentCharge").." : " .. charge
		elseif	charge2 ~= nil then
				numRows = 1
				chargeText = getText("ContextMenu_AttachmentCharge").." : " .. charge2
		elseif	triggerType ~= nil then
				numRows = 1
				if		triggerType == 0 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_N/A")
				elseif	triggerType == 1 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_Single")
				elseif	triggerType == 2 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_Auto")
				elseif	triggerType == 3 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_Single") .. " / " .. getText("ContextMenu_FireMode_[2]Burst")
				elseif	triggerType == 4 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_Single") .. " / " .. getText("ContextMenu_FireMode_[3]Burst")
				elseif	triggerType == 5 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_Single") .. " / " .. getText("ContextMenu_FireMode_Auto")
				elseif	triggerType == 6 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_Single") .. " / " .. getText("ContextMenu_FireMode_[2]Burst") .. " / " .. getText("ContextMenu_FireMode_Auto")
				elseif	triggerType == 7 then
						chargeText = getText("ContextMenu_FireModes") .. " : " .. getText("ContextMenu_FireMode_Single") .. " / " .. getText("ContextMenu_FireMode_[3]Burst") .. " / " .. getText("ContextMenu_FireMode_Auto")
				end				
		end		
	end
	local default_y = 0
	local fontSize = 0
	local tooltipFontSize = 0
	local lineSpacing = self.tooltip:getLineSpacing()
	local default_setHeight = self.setHeight
	self.setHeight = function(self, num, ...)
		default_y = num
		num = num + numRows * lineSpacing
		return default_setHeight(self, num, ...)
	end
	local default_drawRectBorder = self.drawRectBorder
	self.drawRectBorder = function(self, ...)
		if numRows > 0 then
			local color = {0.0, 1.0, 0.0}
			local font = UIFont[getCore():getOptionTooltipFont()];
			self.tooltip:DrawText(font, chargeText, 5, default_y, color[1], color[2], color[3], 1);
		end
		return default_drawRectBorder(self, ...)
	end
	default(self)
	self.setHeight = default_setHeight
	self.drawRectBorder = default_drawRectBorder
end