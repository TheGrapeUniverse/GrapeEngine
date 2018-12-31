package at.dalex.grape.graphics.shader;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import at.dalex.grape.graphics.font.CharMeta;
import at.dalex.grape.graphics.font.GrapeFont;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import at.dalex.grape.graphics.mesh.TexturedModel;
import at.dalex.grape.resource.FileContentReader;

public class FontShader extends ShaderProgram {

    private int position_projectionMatrix;
    private int position_atlas_offset;
    private int position_atlas_rows;

    public FontShader() {
        super(FileContentReader.readFile("/shaders/FontShader.vsh"), FileContentReader.readFile("/shaders/FontShader.fsh"));
    }

    @Override
    public void getAllUniformLocations() {
        position_projectionMatrix 	= getUniformLoader().getUniformLocation("projectionMatrix");
        position_atlas_offset 		= getUniformLoader().getUniformLocation("atlas_offset");
        position_atlas_rows 		= getUniformLoader().getUniformLocation("atlas_rows");
    }

    @Override
    public void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
    }

    public void drawFont(TexturedModel model, GrapeFont font, String text, Matrix4f projectionAndViewMatrix) {
        start();
        model.getBaseModel().getVao().bindVAO();
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //Funktioniert vl nicht, weil der Shader nicht

        getUniformLoader().loadMatrix(position_projectionMatrix, projectionAndViewMatrix);
        getUniformLoader().loadFloat(position_atlas_rows, 15);     //TODO: Change this when switching to unicode!

        for (int i = 0; i < text.length(); i++) {

            //Set UV-Offset
            CharMeta charInfo = font.getCharacterInfo(text.charAt(i));
            Vector2f UVOffset = new Vector2f(charInfo.getAtlasX(), charInfo.getAtlasY());
            getUniformLoader().load2DVector(position_atlas_offset, UVOffset);

            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTextureId());
            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getBaseModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        }

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        model.getBaseModel().getVao().unbindVAO();
        stop();
    }
}
