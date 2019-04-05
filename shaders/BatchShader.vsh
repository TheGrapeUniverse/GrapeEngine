#version 330

uniform vec2 atlasSize;

//Per vertex variables
in vec4 vertex;

//Instanced variables
in mat4 viewMatrix;
in mat4 transformationMatrix;
in vec2 uvOffset;

out vec2 pass_textureCoord;

void main()
{
    gl_Position = viewMatrix * transformationMatrix * vec4(vertex.x, vertex.y, 0.0f, 1.0f);
    vec2 uvCoords = vertex.zw;
    vec2 normalizedUVs = uvCoords / atlasSize;
	pass_textureCoord = normalizedUVs + uvOffset;
}