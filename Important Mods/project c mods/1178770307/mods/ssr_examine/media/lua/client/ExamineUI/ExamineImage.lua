require "ISUI/ISPanel"

ExamineImage = ISPanel:derive("ExamineImage");

function ExamineImage:initialise()
	ISPanel.initialise(self);
end

function ExamineImage:load()
	self.loaded = false;
	self.modified = false;

	useTextureFiltering(true);
	self.texture = getTexture(self.image);
	useTextureFiltering(false);

	local w = self.texture:getWidth();
	local h = self.texture:getHeight();

	if w > self.width or h > self.height then
		w = self.width;
		h = self.height;
	end

	local mx = (self.width / 2);
	local my = (self.height / 2);
	self.imageX = mx - (w/2);
	self.imageY = my - (h/2);
	self.imageW = w;
	self.imageH = h;

	self.loaded = true;
end

function ExamineImage:render()
	if not self.loaded then return end

	self:setStencilRect(0, 0, self.width, self.height);

	if self.modified then
		self:load();
	end

	if self.texture then
        self:drawTextureScaled(self.texture, self.imageX, self.imageY, self.imageW, self.imageH, 1, 1, 1, 1);
	end

	self:clearStencilRect()
end

function ExamineImage:onResize()
	self.width = self:getWidth();
	self.height = self:getHeight();
    self.modified = true;
end

-- Dragging

function ExamineImage:onMouseMove(dx, dy)
	self.mouseOver = true;
	if self.moving then
		self:setX(self.x + dx);
		self:setY(self.y + dy);
		self:bringToTop();
	end
end

function ExamineImage:onMouseUp(x, y)
	if not self:getIsVisible() then
		return;
	end

	self.moving = false;
	if ISMouseDrag.tabPanel then
		ISMouseDrag.tabPanel:onMouseUp(x,y);
	end

	ISMouseDrag.dragView = nil;
end

function ExamineImage:onMouseUpOutside(x, y)
	if not self:getIsVisible() then
		return;
	end

	self.moving = false;
	ISMouseDrag.dragView = nil;
end

function ExamineImage:onMouseDown(x, y)
	if self.dragging == false then return; end

	if not self:getIsVisible() then
		return;
	end

	self.downX = x;
	self.downY = y;
	self.moving = true;
	self:bringToTop();
end

function ExamineImage:new (x, y, width, height)
	local o = {}
	o = ISPanel:new(x, y, width, height);
	setmetatable(o, self);
    self.__index = self;
	o.x = x;
	o.y = y;
    o.backgroundColor = {r=0, g=0, b=0, a=0.0};
    o.borderColor = {r=0, g=0, b=0, a=0.0};
	o.noBackground = true;
	o.image = "";
	o.modified = false;
	return o;
end