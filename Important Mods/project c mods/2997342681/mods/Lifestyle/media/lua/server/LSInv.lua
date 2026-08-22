
local function adjustPowerAxe(character, item) --!
	LSUtil.debugPrint("---- LS - called lfunc adjustPowerAxe ----")
	local treeDmg = 20 -- 10 is stone axe 55 is wood axe
	local invData = LSUtil.getInventionItemData(item)
	if not invData then LSUtil.setItemVal(item, 'getTreeDamage', 'setTreeDamage', treeDmg) return; end
	if item:isBroken() then LSUtil.drainInventionIem(item, invData); LSUtil.setItemVal(item, 'getTreeDamage', 'setTreeDamage', treeDmg); return end
	LSUtil.useInventionItem(item, invData)
	LSUtil.playSoundCharacter(character, "Impact_Small_", 4, false, true, false, false)
	if not LSUtil.inventionItemHasUses(item, invData) then LSUtil.setItemVal(item, 'getTreeDamage', 'setTreeDamage', treeDmg); end
end

local function onWeaponHitTree(character, item) --!
	if not LSUtil.isValidInvItem(item) or not item:IsWeapon() then return; end
	if item:getType() == "LSPoweredAxe" then adjustPowerAxe(character, item); end
end

Events.OnWeaponHitTree.Add(onWeaponHitTree) --!