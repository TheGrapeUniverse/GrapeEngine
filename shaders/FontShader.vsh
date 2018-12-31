#version 330

uniform mat4 projectionMatrix;

layout (location = 0) in vec3 position;

uniform vec2  atlas_offset;
uniform float atlas_rows;

in  vec2 textureCoordinates;
out vec2 pass_textureCoordinates;

void main()
{
	pass_textureCoordinates = textureCoordinates / atlas_rows;
	pass_textureCoordinates += atlas_offset;

	gl_Position = projectionMatrix * vec4(position, 1);
}