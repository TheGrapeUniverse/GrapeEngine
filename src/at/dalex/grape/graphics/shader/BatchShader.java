package at.dalex.grape.graphics.shader;

import at.dalex.grape.resource.FileContentReader;

public class BatchShader extends ShaderProgram {

    protected int position_atlasSize;

    public BatchShader() {
        super(FileContentReader.readFile("/shaders/BatchShader.vsh"), FileContentReader.readFile("/shaders/BatchShader.fsh"));
    }

    @Override
    public void getAllUniformLocations() {
        position_atlasSize = getUniformLoader().getUniformLocation("atlasSize");
    }

    @Override
    public void bindAttributes() {
        super.bindAttribute(0, "vertex");
        super.bindAttribute(1, "viewMatrix");
        super.bindAttribute(5, "transformationMatrix");
        super.bindAttribute(9, "uvOffset");
    }
}
