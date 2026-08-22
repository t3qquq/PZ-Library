if getCore():isDedicated() then return end

-------------------------------------------------------------------------
-- global variables
SafeUserLogin_mod = SafeUserLogin or {};
SafeUserLogin_mod.Utils = {};

-------------------------------------------------------------------------
-- global functions

--[[ noise(message)
    prints noise
]]
function SafeUserLogin_mod.Utils.noise(message)
    print('SafeUserLogin: ' .. message);
end