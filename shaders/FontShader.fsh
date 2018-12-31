#version 330

in vec2 pass_textureCoordinates;
uniform sampler2D textureSampler;

out vec4 fragColor;

void main()
{
	fragColor = texture(textureSampler, pass_textureCoordinates);
}
