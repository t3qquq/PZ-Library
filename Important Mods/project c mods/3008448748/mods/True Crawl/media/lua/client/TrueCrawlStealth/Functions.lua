local self = {}

self.getCoords = function(source)
    return {x = math.floor(source:getX()), y = math.floor(source:getY()), z = math.floor(source:getZ())}
end

return self