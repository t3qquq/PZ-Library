CharacterCreationProfession = CharacterCreationProfession

local old_CharacterCreationProfession_drawXpBoostMap = CharacterCreationProfession.drawXpBoostMap
function CharacterCreationProfession:drawXpBoostMap(y, item, alt)
	local result = old_CharacterCreationProfession_drawXpBoostMap(self, y, item, alt)
	
  local dy = (self.itemheight - self.fontHgt) / 2
  local levelString = ' ('..item.item.level..')';
  self:drawText(item.text..levelString, 16, y + dy, 0, 1, 0, 1, UIFont.Small);

	return result
end