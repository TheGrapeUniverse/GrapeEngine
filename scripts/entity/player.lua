local image = nil;
local instance;
local animation;
local walkSpeed = 256;

function init(entityInstance)
	instance = entityInstance;
	print("LuaEntity initialized!");
	print("Loading animation ...");
	
	entityInstance:loadAnimation("textures/entity/player/walk.png", 16, 16, 8);
end

function update()
	local delta = Timer.getDelta();

	if (Keyboard.isKeyDown("W")) then
		instance:setY(instance:getY() - (walkSpeed * delta));
	elseif (Keyboard.isKeyDown("S")) then
		instance:setY(instance:getY() + (walkSpeed * delta));
	elseif (Keyboard.isKeyDown("A")) then
		instance:setX(instance:getX() - (walkSpeed * delta));
	elseif (Keyboard.isKeyDown("D")) then
		instance:setX(instance:getX() + (walkSpeed * delta));
	end

	-- Map.setPosition(Display.getWidth() / 2 - instance:getX(), Display.getHeight() / 2 - instance:getY());
end

function draw()
	if (instance:hasAnimation()) then
		animation = instance:getAnimation();
		image = animation:getImage();
		
		Graphics.drawImage(image, instance:getX(), instance:getY(), image:getWidth() * 3, image:getHeight() * 3);
	end
end