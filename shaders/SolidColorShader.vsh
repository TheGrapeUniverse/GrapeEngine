#version 400 core
uniform mat4 projectionMatrix;
attribute vec3 vertices;

void main() {

    gl_Position = projectionMatrix * vec4(vertices, 1);
}