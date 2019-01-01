#version 330

uniform mat4 projectionMatrix;
layout (location = 0) in vec4 vertex; // vec2 pos, vec2 texPos

out vec2 pass_textureCoord;

void main()
{
	gl_Position = projectionMatrix * vec4(vertex.xy, 0.0, 1.0);
	pass_textureCoord = vertex.zw;
}