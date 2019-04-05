#version 330

//Per vertex variables
layout (location = 0) in vec4 vertex;

//Instanced variables
in mat4 viewMatrix;
in mat4 transformationMatrix;
in vec2 uvOffset;
in vec2 uvScale;

out vec2 pass_textureCoord;

void main()
{
	//Calculate vertex position
    gl_Position = viewMatrix * transformationMatrix * vec4(vertex.x, vertex.y, 0.0f, 1.0f);

	//Transform uv coordinates
	vec2 uvCoords = vec2(vertex.xy);
	uvCoords *= uvScale;
	pass_textureCoord = uvCoords + uvOffset;
}