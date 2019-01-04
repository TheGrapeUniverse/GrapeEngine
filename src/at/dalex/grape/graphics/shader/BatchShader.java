package at.dalex.grape.graphics.shader;

import at.dalex.grape.resource.FileContentReader;

public class BatchShader extends ShaderProgram {

    public int position_projectionMatrix;

    public BatchShader() {
        super(FileContentReader.readFile("/shaders/BatchShader.vsh"), FileContentReader.readFile("/shaders/BatchShader.fsh"));
    }

    @Override
    public void getAllUniformLocations() {
        this.position_projectionMatrix = getUniformLoader().getUniformLocation("projectionMatrix");
    }

    @Override
    public void bindAttributes() {
        super.bindAttribute(0, "vertex");
    }
}
