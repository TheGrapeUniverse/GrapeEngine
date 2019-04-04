#version 330

in vec2 pass_textureCoord;
out vec4 fragColor;

uniform sampler2D batchTexture;

void main()
{
    fragColor = texture(batchTexture, pass_textureCoord);
    //fragColor = vec4(0.0f, 1.0f, 1.0f, 1.0f);
}