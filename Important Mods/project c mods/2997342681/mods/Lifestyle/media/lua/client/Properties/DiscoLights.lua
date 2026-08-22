--------------------------------------------------------------------------------------------------
--		----	  |			  |			|		 |				|    --    |      ----			--
--		----	  |			  |			|		 |				|    --	   |      ----			--
--		----	  |		-------	   -----|	 ---------		-----          -      ----	   -------
--		----	  |			---			|		 -----		------        --      ----			--
--		----	  |			---			|		 -----		-------	 	 ---      ----			--
--		----	  |		-------	   ----------	 -----		-------		 ---      ----	   -------
--			|	  |		-------			|		 -----		-------		 ---		  |			--
--			|	  |		-------			|	 	 -----		-------		 ---		  |			--
--------------------------------------------------------------------------------------------------

--Disco Lights
DiscoBallProps = {}

DiscoBallProps.default = function(stage)
	if stage == 1 then return 200, 200, 200, 3;
	elseif stage == 2 then return 0, 200, 255, 2;
	elseif stage == 3 then return 200, 200, 200, 3; end
	return 255, 0, 255, 2
end

DiscoBallProps.circles = function(stage)
	if stage == 1 then return 255, 165, 0, 2;
	elseif stage == 2 then return 255, 255, 0, 1;
	elseif stage == 3 then return 0, 255, 0, 2; end
	return 255, 0, 0, 1
end

DiscoBallProps.spots = function(stage)
	if stage == 1 then return 255, 105, 180, 2;
	elseif stage == 2 then return 128, 0, 0, 1;
	elseif stage == 3 then return 0, 191, 255, 2; end
	return 255, 20, 147, 1
end

DiscoBallProps.rainbow = function(stage)
	if stage == 1 then return 143, 0, 255, 2;
	elseif stage == 2 then return 255, 255, 0, 1;
	elseif stage == 3 then return 255, 127, 0, 2; end
	return 75, 0, 130, 1
end

DiscoBallProps.gold = function(stage)
	if stage == 1 then return 255, 215, 0, 2;
	elseif stage == 2 then return 255, 69, 0, 1;
	elseif stage == 3 then return 255, 215, 0, 2; end
	return 255, 69, 0, 1
end

DiscoBallProps.default = function(stage)
	if stage == 1 then return 200, 200, 200, 3;
	elseif stage == 2 then return 0, 200, 255, 2;
	elseif stage == 3 then return 200, 200, 200, 3; end
	return 255, 0, 255, 2
end

DiscoBallProps.valentine = function(stage)
	if stage == 1 then return 200, 0, 110, 3;
	elseif stage == 2 then return 200, 0, 110, 2;
	elseif stage == 3 then return 200, 0, 110, 3; end
	return 200, 0, 110, 2
end

DiscoBallProps.random = function(stage)
	local c = 1
	if (stage == 1) or (stage == 3) then c = 2; end
	local t = {}
	for i=1, 3 do
		local argRdm = ZombRand(200)+1
		if (i == 2) and (t[1] > 100) then argRdm = ZombRand(50)+1;
		elseif (i == 3) and (t[2] > 100) then argRdm = ZombRand(50)+1; end
		table.insert(t, argRdm)
	end
	return t[1], t[2], t[3], c
end


--mode is a string
DiscoBallProps.get = function(mode, stage)
	local r, g, b, c = DiscoBallProps[mode](stage)
	return r, g, b, c
end