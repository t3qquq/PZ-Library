

function Recipe.OnCreate.OpenMirror(items, result, player)
    local mirror  =Projectmirror:new(getCore():getScreenWidth()/2-300,getCore():getScreenHeight()/2-250,500,600,player);
    mirror:initialise();
    mirror:addToUIManager();
	
end