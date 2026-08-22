		----------------------------------------------------------------------------------------------------------------------
		--									True Crawl Animation Manager Script by iLusioN									--
		--					Do Not Copy Any Of This! But if you do anyway, at least give me the CREDITS!					--
		----------------------------------------------------------------------------------------------------------------------
		
require "TimedActions/ISBaseTimedAction"
require 'TC_CrawlUnder_Logic'
require 'TC_Logic'
require 'Shared_time'

ISTC_CrawlUnder = ISBaseTimedAction:derive("ISTC_CrawlUnder")

function ISTC_CrawlUnder:isValidStart()
    return true
end

function ISTC_CrawlUnder:isValid()
    return not self.isInvalid
end


function ISTC_CrawlUnder:update()
    if not self.isUnderCar then
        if self.vehicle:isInBounds(self.character:getX(),self.character:getY()) then
            self.isUnderCar = true;
        end
    else
        if not self.vehicle:isInBounds(self.character:getX(),self.character:getY()) then
                self.isInvalid = true
				self.isUnderCar = false
                self.lastUpdateTime = nil
        end
    end

end

function ISTC_CrawlUnder:start()

	local primary = self.character:getPrimaryHandItem()
	
    self.action:setUseProgressBar(false)
	self.character:setNoClip(true)
    self.character:setBlockMovement(true)
	self.character:removeFromHands(primary)
    self:setActionAnim("Bob_CrawlUnder")
	
	IsUnderVehicle = true

	
end

function ISTC_CrawlUnder:stop()

    ISBaseTimedAction.stop(self)
    self.character:setBlockMovement(false)
	self.character:setNoClip(false)
	
	IsUnderVehicle = false

end

function ISTC_CrawlUnder:perform(character)

    ISBaseTimedAction.perform(self)
    self.character:setBlockMovement(false)
	
end



function ISTC_CrawlUnder:new(character, vehicle)
    local o = {}
    setmetatable(o, self)
    self.__index = self
    o.character = character;
    o.vehicle = vehicle;
    o.stopOnWalk = false;
    o.stopOnRun = false;
    o.stopOnAim = false;
    o.isInvalid = false;
    o.isUnderCar = false;
    o.lastUpdateTime = nil
    o.maxTime = -1;
    return o
end