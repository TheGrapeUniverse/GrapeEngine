
entities = {
	player = nil;
}

--local debugFont;

function init()
	Map.changeMap("maps/simpleMap.tmx", "textures/tileset.png", 16);
	Map.setScale(3);

  	entities.player = Entity.create(0, 10, LivingType.NEUTRAL, 200, "scripts/entity/player.lua");

	-- debugFont = Font.createFont("Arial", 48, true);
end

function draw()
    entities.player:draw();
	-- debugFont:drawString(0, 0, "GrapeEngine");
    -- debugFont:drawString(0, 12, "Version 0.8");
	-- debugFont:drawString(0, 24 , "Using Lua-Implementation [ALPHA]");
	-- debugFont:drawString(0, 48, "FPS: " .. Timer.getFPS());
	-- debugFont:drawString(0, 60, "UPS: " .. Timer.getUPS());
	-- debugFont:drawString(0, 84, "DeltaTime: " .. Timer.getDelta());
	-- debugFont:drawString(0, 96, "Uptime: " .. string.format("%d Seconds", Timer.getTime()));

end

function update()
	-- Update calls going here
end
