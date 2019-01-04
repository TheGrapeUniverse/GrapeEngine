package at.dalex.grape.graphics;

import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.shader.BatchShader;
import at.dalex.grape.toolbox.MemoryManager;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class BatchRenderer {

    private static BatchShader shader = new BatchShader();
    private ArrayList<BatchInfo> batchQueue = new ArrayList<>();

    private int vaoId;
    private int vboId;
    private FloatBuffer matrixBuffer;

    public BatchRenderer() {
        vaoId = GL30.glGenVertexArrays();
        MemoryManager.createdVAOs.add(vaoId);

        vboId = GL15.glGenBuffers();
        MemoryManager.createdVBOs.add(vboId);

        /* Create VAO and VBO */
        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, createFloatBuffer(new float[6 * 4]), GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 4 * 4, 0);
        GL20.glDisableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        /* Create FloatBuffer for projection-matrix */
        matrixBuffer = BufferUtils.createFloatBuffer(16);
    }

    private FloatBuffer createFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public void queueRender(Image image, int x, int y, int width, int height) {
        queueRender(image.getTextureId(), x, y, width, height);
    }

    public void queueRender(int textureId, int x, int y, int width, int height) {
        queueRender(textureId, x, y, width, height, 0, 0, 1, 1);
    }

    public void queueRender(Image image, int x, int y, int width, int height, int u1, int v1, int v2, int u2) {
        queueRender(image.getTextureId(), x, y, width, height, u1, v1, u2, v2);
    }

    public void queueRender(int textureId, int x, int y, int width, int height, int u1, int v1, int v2, int u2) {
        batchQueue.add(new BatchInfo(textureId, x, y, width, height, u1, v1, u2, v2));
    }

    public void drawQueue(Matrix4f projection) {
        shader.start();

        //Prepare projection matrix
        projection.get(matrixBuffer);
        GL20.glUniformMatrix4fv(shader.position_projectionMatrix, false, matrixBuffer);
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL30.glBindVertexArray(vaoId);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL20.glEnableVertexAttribArray(0);

        for (BatchInfo info : batchQueue) {
            //Prepare Texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, info.textureId);

            //Calculate Vertex-Data
            float[] vertices = {
                    //Triangle 1
                    info.x,                 info.y,                 info.u1, info.v1,
                    info.x,                 info.y + info.height,   info.u1, info.v2,
                    info.x + info.width,    info.y + info.height,   info.u2, info.v2,

                    //Triangle 2
                    info.x,                 info.y,                 info.u1, info.v1,
                    info.x + info.width,    info.y + info.height,   info.u2, info.v2,
                    info.x + info.width,    info.y,                 info.u2, info.v1
            };

            //Update vertex data
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, createFloatBuffer(vertices));

            //Draw
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }

        GL20.glDisableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        shader.stop();
    }

    /**
     * Flushes all queued elements.
     */
    public void flush() {
        batchQueue.clear();
    }

    /**
     * @return The amount of currently queued elements
     */
    public int queueSize() {
        return this.batchQueue.size();
    }

    /**
     * Represents all information which is needed to draw
     * a single rectangle.
     *
     * UV-Coordinates are also stored.
     */
    class BatchInfo {

        private int textureId;
        public int x;
        public int y;
        public int width;
        public int height;
        public float u1, v1, u2, v2;

        BatchInfo(int textureId, int x, int y, int width, int height, int u1, int v1, int u2, int v2) {
            this.textureId = textureId;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }
    }
}
