#version 330

in vec2 pass_textureCoord;
out vec4 fragColor;

uniform sampler2D sampler;
uniform vec3 textColor;

void main()
{
	vec4 sampled = vec4(1.0, 1.0, 1.0, texture(sampler, pass_textureCoord).r);
	fragColor = vec4(textColor, 1.0) * sampled;
}
