--***********************************************************
--**         ORIGINAL SCRIPTS BY ONELINE/D.BOROVSKY        **
--***********************************************************
require "ISBaseObject"

ISExamineSetInfo = ISBaseObject:derive("ISExamineSetInfo");

function ISExamineSetInfo:initialise()

end

function ISExamineSetInfo:addPage(image, text)
	self.pageCount = self.pageCount + 1;
	self.pages[self.pageCount] = { image = image, text = text or "" };
end

function ISExamineSetInfo:applyPageToRichTextPanel(panel)
	if self.pages[self.currentPage] then
		panel.text = self.pages[self.currentPage].text or "";
		panel:paginate();
	end
end

function ISExamineSetInfo:applyPageToExaminePanel(panel)
	if self.pages[self.currentPage] then
		panel.image = self.pages[self.currentPage].image or "";
		panel:load();
	end
end

function ISExamineSetInfo:hasNext()
    return self.currentPage + 1 <= self.pageCount;
end

function ISExamineSetInfo:hasPrevious()
    return self.currentPage > 1;
end

function ISExamineSetInfo:update(examinePanel)

end

--************************************************************************--
--** ISExaminePageInfo:new
--**
--************************************************************************--
function ISExamineSetInfo:new ()
	local o = {}
    setmetatable(o, self)
    self.__index = self
	o.pages = {}
	o.pageCount = 0;
	o.currentPage = 1;
   return o
end

