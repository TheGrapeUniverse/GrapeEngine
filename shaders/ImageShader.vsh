#version 400 core

uniform mat4 projectionMatrix;
attribute vec3 position;
in vec2 textureCoordinates;
out vec2 pass_textureCoordinates;

void main() {
	pass_textureCoordinates = textureCoordinates;
	gl_Position = projectionMatrix * vec4(position, 1);
}