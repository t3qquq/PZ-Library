--***********************************************************
--**                    金喵球                             **
--***********************************************************

-- require "ISUI/ISCollapsableWindow"

Projectmirror = ISCollapsableWindow:derive("Projectmirror")

local FONT_HGT_SMALL = getTextManager():getFontHeight(UIFont.Small)

function Projectmirror:applycolor()

    local text = self.entry:getText()

    local texttable = string.split(text, ",")
    local tablecolor = {}

    if #texttable ~= 4 then return end
    local colorcount = 0

    for k,v in pairs(texttable) do

        if k == 1 then

            if tonumber(v) then
                tablecolor.r = tonumber(v)
                colorcount = colorcount+1
            end
            
        elseif k == 2 then
            if tonumber(v) then
                tablecolor.g = tonumber(v)
                colorcount = colorcount+1
            end
        elseif k == 3 then
            if tonumber(v) then
                tablecolor.b = tonumber(v)
                colorcount = colorcount+1
            end
        elseif k ==4 then
            
            if tonumber(v) then
                tablecolor.a = tonumber(v)
                colorcount = colorcount+1
            end
        end
    end
    if colorcount~=4 then return end

    self.backgroundColor = tablecolor
end

function Projectmirror:playaction()



    self.Isometric = not self.Isometric
    self.playermodel:setIsometric(self.Isometric)
end


function Projectmirror:onAnimSelected(combo)
    self.playermodel:reportEvent(combo:getOptionData(combo.selected))
end


function Projectmirror:createChildren()
    ISCollapsableWindow.createChildren(self);
    
    self.paneledge  = 10
    self.buttonwidth = 80
    self.buttonheight = 30


    self.playermodel = ISCharacterScreenAvatar:new(self.paneledge*2 +self.buttonwidth, self:titleBarHeight()+self.paneledge,self.width-3*self.paneledge-self.buttonwidth, self.height -self:titleBarHeight()-2* self.paneledge)
	self.playermodel:setVisible(true)
	self:addChild(self.playermodel)
	self.playermodel:setOutfitName("Foreman", false, false)
	self.playermodel:setState("idle")
	self.playermodel:setDirection(IsoDirections.S)
	self.playermodel:setIsometric(false)
    self.playermodel:setCharacter(self.playerz)
    

    self.lbl = ISLabel:new(self.paneledge, self:titleBarHeight()+self.paneledge, self.buttonheight, getText("IGUI_shuruyanse"), 1, 1, 1, 1.0, UIFont.Small, true);
    self.lbl:initialise();
    self.lbl:instantiate();
    self:addChild(self.lbl);

    local localheight = self:titleBarHeight()+self.paneledge  + self.buttonheight + self.paneledge

    self.entry = ISTextEntryBox:new("0,0,0,1",self.paneledge, localheight, self.buttonwidth,self.buttonheight);
    self.entry.font = UIFont.Medium
    self.entry:initialise();
    self.entry:instantiate();
    self:addChild(self.entry);

    localheight = localheight + self.buttonheight + self.paneledge

    self.buttoncolor = ISButton:new(self.paneledge, localheight,self.buttonwidth,self.buttonheight , getText("IGUI_yingyong"), self, self.applycolor);
    self.buttoncolor.anchorTop = false
    self.buttoncolor.anchorBottom = false
    self.buttoncolor:initialise();
    self.buttoncolor:instantiate();
    self.buttoncolor.borderColor = {r=1, g=1, b=1, a=1};
    self:addChild(self.buttoncolor);

    localheight = localheight + self.buttonheight + self.paneledge*2

    self.buttonaction = ISButton:new(self.paneledge, localheight,self.buttonwidth,self.buttonheight , getText("IGUI_qiehuanshijiao"), self, self.playaction);
    self.buttonaction.anchorTop = false
    self.buttonaction.anchorBottom = false
    self.buttonaction:initialise();
    self.buttonaction:instantiate();
    self.buttonaction.borderColor = {r=1, g=1, b=1, a=1};
    self:addChild(self.buttonaction);

    localheight = localheight + self.buttonheight + self.paneledge

    self.animCombo = ISComboBox:new(self.paneledge,localheight, self.buttonwidth, self.buttonheight, self, self.onAnimSelected)
	self.animCombo:initialise()
	self:addChild(self.animCombo)
	self.animCombo:addOptionWithData(getText("IGUI_anim_Idle"), "EventIdle")
	self.animCombo:addOptionWithData(getText("IGUI_anim_Walk"), "EventWalk")
	self.animCombo:addOptionWithData(getText("IGUI_anim_Run"), "EventRun")
	self.animCombo.selected = 1

    localheight = localheight + self.buttonheight + self.paneledge*2

    self.lbl2 = ISLabel:new(self.paneledge, localheight, self.buttonheight, getText("IGUI_suofang"), 1, 1, 1, 1.0, UIFont.Small, true);
    self.lbl2:initialise();
    self.lbl2:instantiate();
    self:addChild(self.lbl2);

    localheight = localheight + self.buttonheight + self.paneledge

    self.entry2 = ISTextEntryBox:new("0",self.paneledge, localheight, self.buttonwidth,self.buttonheight);
    self.entry2.font = UIFont.Medium
    self.entry2:initialise();
    self.entry2:instantiate();
    self.entry2:setOnlyNumbers(true)
    self:addChild(self.entry2);

    -- localheight = localheight + self.buttonheight + self.paneledge

end

function Projectmirror:render()
    ISCollapsableWindow.render(self);

    self.playermodel.javaObject:setWidth(self.width-3*self.paneledge-self.buttonwidth)
    self.playermodel.javaObject:setHeight(self.height -self:titleBarHeight()-2* self.paneledge)

    local text = self.entry2:getText()
    if text and tonumber(text) then
        self.playermodel:setZoom(tonumber(text))
    end

    self.playermodel:setCharacter(self.playerz)

    -- self.playermodel:setCharacter(getPlayer())
    



end

-- function Projectmirror:onMouseMove(dx, dy)
--     -- ISCollapsableWindow.onMouseMove(self,dx,dy);



-- 	self.mouseOver = true;

-- 	if self.moving then
-- 		self:setX(self.x + dx);
-- 		self:setY(self.y + dy);
-- 		self:bringToTop();
-- 		--ISMouseDrag.dragView = self;
-- 	end
--     if not isMouseButtonDown(0) and not isMouseButtonDown(1) and not isMouseButtonDown(2) then
--     	self:uncollapse();
--     end

--     -- print(self.moving)
--     -- print(dx,"-----",dy)
-- end

-- function Projectmirror:onMouseDown(x, y)

-- 	if not self:getIsVisible() then
-- 		return;
-- 	end

-- 	self.downX = x;
-- 	self.downY = y;
-- 	self.moving = true;
-- 	self:bringToTop();

--     -- print("onmousedown")
-- end

-- function Projectmirror:onMouseUp(x, y)
-- 	if not self:getIsVisible() then
-- 		return;
-- 	end

-- 	self.moving = false;
-- 	if ISMouseDrag.tabPanel then
-- 		ISMouseDrag.tabPanel:onMouseUp(x,y);
-- 	end

-- 	ISMouseDrag.dragView = nil;

--     print("onmouseup")
-- end


function Projectmirror:new(x, y, width, height,player)
	local o = ISCollapsableWindow:new(x, y, width, height)
	setmetatable(o, self)
	self.__index = self
	o.backgroundColor = {r=0, g=0, b=0, a=1.0}
	o.resizable = true
    o.playerz = player
	return o
end





