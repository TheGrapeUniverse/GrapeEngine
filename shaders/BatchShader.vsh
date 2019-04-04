#version 330

//Per vertex variables
in vec2 vertex;
in vec2 uvCoordinates;

//Instanced variables
in mat4 viewMatrix;
in mat4 transformationMatrix;

out vec2 pass_textureCoord;

void main()
{
    gl_Position = viewMatrix * transformationMatrix * vec4(vertex.x, vertex.y, 0.0f, 1.0f);
	pass_textureCoord = uvCoordinates;
}