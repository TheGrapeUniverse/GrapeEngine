local image = nil;
local instance;
local animation;
local scale = 1;

local velocity = 5;

function init(entityInstance)
	instance = entityInstance;
	print("LuaEntity initialized!");
	print("Loading animation ...");
	
	entityInstance:loadAnimation("textures/entity/player/walk.png", 16, 16, 2);
end

function update(delta)
	if (Keyboard.isKeyDown("W")) then
		instance:setY(instance:getY() - velocity);
	elseif (Keyboard.isKeyDown("S")) then
		instance:setY(instance:getY() + velocity);
	elseif (Keyboard.isKeyDown("A")) then
		instance:setX(instance:getX() - velocity);
	elseif (Keyboard.isKeyDown("D")) then
		instance:setX(instance:getX() + velocity);
	end
	
	width = Display.getWidth();
	height = Display.getHeight();
	-- Map.setPosition(Display.getWidth() / 2 - instance:getX(), Display.getHeight() / 2 - instance:getY());
end

function draw()
	if (instance:hasAnimation()) then
		animation = instance:getAnimation();
		image = animation:getImage();
		
		-- Graphics.drawImage(image, instance:getX() + Map.getX(), instance:getY() + Map.getY(), image:getWidth() * Map.getScale(), image:getHeight() * Map.getScale());
	end
end