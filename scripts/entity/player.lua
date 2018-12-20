local image = nil;
local instance;
local animation;
local walkSpeed = 256;

local moving = false;

function init(entityInstance)
	instance = entityInstance;

	entityInstance:loadAnimation("anim_idle", "textures/entity/player/idle.png", 16, 16, 3)
	entityInstance:loadAnimation("anim_walk", "textures/entity/player/walk.png", 16, 16, 8);
	entityInstance:setAnimation("anim_idle");
end

function update()
	local delta = Timer.getDelta();

	if (Keyboard.isKeyDown("W")) then
		instance:setY(instance:getY() - (walkSpeed * delta));
		moving = true;
	elseif (Keyboard.isKeyDown("S")) then
		instance:setY(instance:getY() + (walkSpeed * delta));
		moving = true;
	elseif (Keyboard.isKeyDown("A")) then
		instance:setX(instance:getX() - (walkSpeed * delta));
		moving = true;
	elseif (Keyboard.isKeyDown("D")) then
		instance:setX(instance:getX() + (walkSpeed * delta));
		moving = true;
	else
		moving = false;
	end

	if (moving) then
		instance:setAnimation("anim_walk");
	else
		instance:setAnimation("anim_idle");
	end

	-- Map.setPosition(Display.getWidth() / 2 - instance:getX(), Display.getHeight() / 2 - instance:getY());
end

function draw()
	if (instance:hasAnimation()) then
		animation = instance:getCurrentAnimation();
		image = animation:getImage();
		
		Graphics.drawImage(image, instance:getX(), instance:getY(), image:getWidth() * 3, image:getHeight() * 3);
	end
end