package at.dalex.grape.graphics.shader;

import at.dalex.grape.resource.FileContentReader;

public class FontShader extends ShaderProgram {

    public int position_projectionMatrix;
    public int position_text_color;

    public FontShader() {
        super(FileContentReader.readFile("/shaders/FontShader.vsh"), FileContentReader.readFile("/shaders/FontShader.fsh"));
    }

    @Override
    public void getAllUniformLocations() {
        position_projectionMatrix = getUniformLoader().getUniformLocation("projectionMatrix");
        position_text_color       = getUniformLoader().getUniformLocation("textColor");
    }

    @Override
    public void bindAttributes() {
        super.bindAttribute(0, "vertex");
    }
}
