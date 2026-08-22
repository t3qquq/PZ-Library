--***********************************************************
--**         ORIGINAL SCRIPTS BY ONELINE/D.BOROVSKY        **
--***********************************************************
require "ExamineUI/ExamineEntries"
require "ExamineUI/ExaminePageInfo"
require "ExamineUI/ExamineImage"
require "ISUI/ISLabelMod"
require "ISUI/ISRichTextPanel"
require "ISUI/ISButton"

ExaminePanel = ISPanel:derive("ExaminePanel");

function ExaminePanel:initialise()
	ISPanel.initialise(self);
end

function ExaminePanel:update()
	if (self.tooltip:getIsVisible() or self.info:getIsVisible()) and self.panel.moving then
		self.info:setVisible(false);
		self.tooltip:setVisible(false);
	end
end

function ExaminePanel:createChildren()
	self.panel = ExamineImage:new(0, 0, 0, 0);
	self.panel:initialise();

	self:reload(1);

	self.panel:setAnchorBottom(true);
	self.panel:setAnchorRight(true);

	self:addChild(self.panel);

	self.panel:setCapture(true);

	local half_screen_width = getCore():getScreenWidth() / 2;
	local text_disclaimer = getText("UI_Text_Disclaimer");
	local text_tooltip = getText("UI_Text_ExamineTooltip");

	local disclaimer = ISLabelMod:new(half_screen_width - (getTextManager():MeasureStringX(UIFont.NewSmall, text_disclaimer) / 2), getCore():getScreenHeight() - 25, 500, 15, text_disclaimer, 0.5, 0.5, 0.5, 1, UIFont.NewSmall);
	disclaimer:initialise();
	disclaimer:setAlwaysOnTop(true);
	self:addChild(disclaimer);
	self.disclaimer = disclaimer;

	local tooltip = ISLabelMod:new(half_screen_width - (getTextManager():MeasureStringX(UIFont.NewSmall, text_tooltip) / 2), self.panel.y - 30, 250, 15, text_tooltip, 1, 1, 0.7, 1, UIFont.NewSmall);
	tooltip:initialise();
	tooltip:setAlwaysOnTop(true);
	self:addChild(tooltip);
	self.tooltip = tooltip;

	--** INFO LABEL **--
	local info = ISRichTextPanel:new(half_screen_width - 300, (self.panel:getY() + self.panel:getHeight() + 5), 600, 15);
	info:initialise();
	info.backgroundColor = {r=0, g=0, b=0, a=0.0};
	info.borderColor = {r=0, g=0, b=0, a=0.0};
	self:addChild(info);
	self.info = info;
	self.info:setAlwaysOnTop(true);

	if self.tableMode then
		--** PREVIOUS BUTTON **--
		local previousButton = ISButton:new(-40, (self.panel.height / 2) - 10, 20, 20, "", self, ExaminePanel.previousPage);
		previousButton:initialise();
		previousButton:setVisible(false);
		previousButton:setImage(getTexture("media/ui/sGuidePrevBtn.png"));
		self.panel:addChild(previousButton);
		self.previous = previousButton;

		--** NEXT BUTTON **--
		local nextButton = ISButton:new(self.panel.width + 23, (self.panel.height / 2) - 10, 20, 20, "", self, ExaminePanel.nextPage);
		nextButton:initialise();
		nextButton:setVisible(#self.image > 1);
		nextButton:setImage(getTexture("media/ui/sGuideNextBtn.png"));
		self.panel:addChild(nextButton);
		self.next = nextButton;

		--** PAGES SUPPORT **--
		self.paginator = ISExamineSetInfo:new();
		self.paginator.currentPage = 1;

		local i, r = 1, 0;
		while i <= #self.image do
			if type(self.image[i]) == "table" then
				self.paginator:addPage(self.image[i][1], type(self.text) == "table" and self.text[i] and (" <SIZE:small> <CENTRE> "..self.text[i]) or "");
			else
				print(string.format("[Examine] (Error) Problem with image for item '%s' at index %s", self.item_type, tostring(i+r)));
				table.remove(self.image, i);
				i = i - 1; r = r + 1;
			end
			i = i + 1;
		end

		self.paginator:applyPageToExaminePanel(self.panel);
		self.paginator:applyPageToRichTextPanel(self.info);
	else
		if self.image[1] then
			self.panel.image = self.image[1];
			self.panel:load();

			if self.text ~= nil then
				self.info.text = " <SIZE:small> <CENTRE> "..self.text;
			else
				self.info.text = "";
			end
			self.info:paginate();
		else
			print(string.format("[Examine] (Error) Problem with image for item '%s'", self.item_type));
		end
	end

	self:bringToTop();
end

function ExaminePanel:reloadBtns()
	if self.paginator:hasNext() then
		self.next:setVisible(true);
    else
        self.next:setVisible(false);
	end

	if self.paginator:hasPrevious() then
		self.previous:setVisible(true);
    else
        self.previous:setVisible(false);
	end
end

function ExaminePanel:reload(index)
	local width;
	local height;

	if self.tableMode then
		if type(self.image[index]) == "table" then
			width = self.image[index][2];
			height = self.image[index][3];
		else
			print(string.format("[Examine] (Error) Unable to load image for item '%s' at index %s", self.item_type, tostring(index)));
			return;
		end
	else
		if type(self.image) == "table" then
			width = self.image[2];
			height = self.image[3];
		else
			print(string.format("[Examine] (Error) Unable to load image for item '%s'", self.item_type));
			return;
		end
	end

	local screen_width = getCore():getScreenWidth();
	local screen_height = getCore():getScreenHeight();

	if width > height then
		local percent = width / height
		width = math.ceil(screen_width * 0.6076);
		height = math.ceil(width / percent);
	elseif height > width then
		local percent = height / width
		height = math.ceil(screen_height * 0.7161);
		width = math.ceil(height / percent);
	end
	width = width + 10;
	--height = height + 51;

	self.panel:setWidth(width);
	self.panel:setHeight(height);

	local loaded = self.next or self.previous;
	if self.tableMode and loaded then
		self.next:setX(self.panel.width + 23);
		self.next:setY((self.panel.height / 2) - 10);
		self.previous:setX(-40);
		self.previous:setY((self.panel.height / 2) - 10);
	end

	self.panel:setX((screen_width / 2) - (self.panel.width / 2));
	self.panel:setY((screen_height / 2) - (self.panel.height / 2));
	return true;
end

function ExaminePanel:previousPage(button)
	if not self.paginator:hasPrevious() then return end
	local index = self.paginator.currentPage - 1;
	if self:reload(index) then
		self.paginator.currentPage = index;
		self.paginator:applyPageToExaminePanel(self.panel);
    	self.paginator:applyPageToRichTextPanel(self.info);
	end

	self:reloadBtns();

	self:playSound(-1)
end

function ExaminePanel:nextPage(button)
	if not self.paginator:hasNext() then return end
	local index = self.paginator.currentPage + 1;
	if self:reload(index) then
		self.paginator.currentPage = index;
		self.paginator:applyPageToExaminePanel(self.panel);
    	self.paginator:applyPageToRichTextPanel(self.info);
	end

	self:reloadBtns();

	self:playSound(1)
end

function ExaminePanel:playSound(dir)
	if type(self.image) == "table" then
		if #self.image > 2 then
			if (self.paginator.currentPage == 2 and dir == 1) or (self.paginator.currentPage == 1 and dir == -1) then
				getSoundManager():PlaySound("magazineOpen", false, 1.0);
			else
				if dir == 1 then
					getSoundManager():PlaySound("magazineFlip1", false, 1.0);
				elseif dir == -1 then
					getSoundManager():PlaySound("magazineFlip2", false, 1.0);
				end
			end
		end
	else
		-- do nothing
	end
end

function ExaminePanel:new(item_type, image, text, callback)
	local o = {}
	o = ISPanel:new(0, 0, getCore():getScreenWidth(), getCore():getScreenHeight());
	setmetatable(o, self)
    self.__index = self
	o.backgroundColor = {r=0, g=0, b=0, a=0.7};
	o.borderColor = {r=0, g=0, b=0, a=0.0};

	o.width = getCore():getScreenWidth();
	o.height = getCore():getScreenHeight();

	o.item_type = item_type;
	o.image = image;
	o.callback = callback;

	if type(text) == "table" then
		o.text = text;
		o.tableMode = true;
	else
		o.text = text;
		o.tableMode = false;
	end

	o:setAlwaysOnTop(true);
   return o
end