
entities = {
	player = nil;
}

local font = nil;

function init()
	-- Map.changeMap("maps/test.tmx");
	-- Map.setScale(3);
  
  	entities.player = Entity.create(0, 10, LivingType.NEUTRAL, 200, "scripts/entity/player.lua");
  
	font = Font.createFont("Arial", 32, true);
end

function draw()
	font:drawString(0, 0, "FPS: " .. Timer.getFPS());
	font:drawString(0, 36, "UPS: " .. Timer.getUPS());	
end

function update()

end
