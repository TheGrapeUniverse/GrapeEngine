package at.dalex.grape.graphics;

import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.TextureAtlas;
import at.dalex.grape.graphics.shader.BatchShader;
import at.dalex.grape.info.Logger;
import at.dalex.grape.toolbox.MemoryManager;
import at.dalex.grape.toolbox.Toolbox;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;

import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.BufferUtils.createFloatBuffer;

public class BatchRenderer {

    private static BatchShader shader = new BatchShader();
    private ArrayList<BatchInfo> batchQueue = new ArrayList<>();

    private int textureId;
    private int vaoId;
    private int vVBOId; //Vertex Vertex-Buffer-Object lmao xd

    private int instanceVBOId;
    private int instanceDataPos = 0;

    //Maximum amount of instances to be drawn at once
    private final int MAX_INSTANCES = 100000;

    // 4 for viewMat, 4 for transformMat, 2 for UV-Offset
    // (Matrices = 4 float per row and column)
    // [4*4 + 4*4 + 2*4= 32]
    private final int INSTANCE_DATA_LENGTH = 40;

    private FloatBuffer projectionMatrixBuffer;
    private FloatBuffer vertexBuffer;
    private FloatBuffer instanceBuffer;

    private TextureAtlas textureAtlas;

    public BatchRenderer(Image atlasImage) {
        this(atlasImage, 1, 1);
    }

    /**
     * Creates a new {@link BatchRenderer}, which is able to draw
     * textured rectangles at incredible amounts in a short period of time.
     *
     * This renderer uses a technique called caching,
     * which stores all draw informations until everything should be rendered.
     * This system makes use of instancing!
     *
     * Internally, this system uses a texture atlas to avoid switching textures.
     *
     * @param atlasImage The source image of the texture atlas
     * @param atlasRows The height in cells of the texture atlas
     * @param atlasCols The width in cells of the texture atlas.
     */
    public BatchRenderer(Image atlasImage, int atlasRows, int atlasCols) {
        this.textureAtlas = new TextureAtlas(atlasImage.getTextureId(), atlasImage.getWidth(), atlasImage.getHeight(),
                atlasRows, atlasCols);

        vaoId = GL30.glGenVertexArrays();
        MemoryManager.createdVAOs.add(vaoId);

        vVBOId = GL15.glGenBuffers();
        MemoryManager.createdVBOs.add(vVBOId);

        /* Create FloatBuffers */
        projectionMatrixBuffer = createFloatBuffer(16);
        vertexBuffer = createFloatBuffer(24);
        instanceBuffer = createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

        //Create VBO used for instanced rendering
        instanceVBOId = createInstanceVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);

        //Calculate Vertex-Data
        //If you want to change these, rather use a transformation matrix
        //for each instance in the vertex shader. Thank you.
        float[] vertices = {
                0, 0,     /* UVs */   0, 0,
                0, 1,                 0, 1,
                1, 1,                 1, 1,
                0, 0,                 0, 0,
                1, 1,                 1, 1,
                1, 0,                 1, 0
        };
        vertexBuffer.put(vertices);
        vertexBuffer.flip();

        /* Create VAO and VBO */
        GL30.glBindVertexArray(vaoId);
        /* Fill Vertex-Buffer */
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vVBOId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0); //2 coords for each vertex * sizeof(float) = 8
        GL20.glDisableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        //View matrix
        addInstancedAttribute(vaoId, instanceVBOId, 1, 4, INSTANCE_DATA_LENGTH, 0);
        addInstancedAttribute(vaoId, instanceVBOId, 2, 4, INSTANCE_DATA_LENGTH, 4);
        addInstancedAttribute(vaoId, instanceVBOId, 3, 4, INSTANCE_DATA_LENGTH, 8);
        addInstancedAttribute(vaoId, instanceVBOId, 4, 4, INSTANCE_DATA_LENGTH, 12);
        //Transformation matrix
        addInstancedAttribute(vaoId, instanceVBOId, 5, 4, INSTANCE_DATA_LENGTH, 16);
        addInstancedAttribute(vaoId, instanceVBOId, 6, 4, INSTANCE_DATA_LENGTH, 20);
        addInstancedAttribute(vaoId, instanceVBOId, 7, 4, INSTANCE_DATA_LENGTH, 24);
        addInstancedAttribute(vaoId, instanceVBOId, 8, 4, INSTANCE_DATA_LENGTH, 28);
        //UV-Offset
        addInstancedAttribute(vaoId, instanceVBOId, 9, 2, INSTANCE_DATA_LENGTH, 32);
        GL30.glBindVertexArray(0);
    }

    private int createInstanceVBO(int floatCount) {
        int vbo = GL15.glGenBuffers();
        MemoryManager.createdVBOs.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);    //Num of floats * bytes per float (4)
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        return vbo;
    }

    private void updateVBO(int vboId, float[] data, FloatBuffer buffer, int usage) {
        try {
            buffer.clear();
            buffer.put(data);
            buffer.flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, usage);
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        } catch (BufferOverflowException e) {
            Logger.error("BatchRenderer query size cannot exceed " + MAX_INSTANCES + " entries!");
        }
    }

    private void updateInstanceVBO(int vboId, float[] data, FloatBuffer buffer) {
        updateVBO(vboId, data, buffer, GL15.GL_STREAM_DRAW);
    }

    private void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL30.glBindVertexArray(vao);
        //Multiplied by 4 (4 bytes for each float)
        GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
        GL33.glVertexAttribDivisor(attribute, 1); //Mark VBO to be updated every single instance step
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void storeMatrixInFloat(Matrix4f matrix, float[] vboData) {
        float[] data = Toolbox.convertMatrixToArray(matrix);
        System.arraycopy(data, 0, vboData, instanceDataPos, data.length);
        instanceDataPos += data.length;
    }

    public void drawQueue(Matrix4f projection) {
        shader.start();
        //Prepare projection matrix
        projection.get(projectionMatrixBuffer);

        GL30.glBindVertexArray(vaoId);
        for (int i = 0; i <= 9; i++)
            GL20.glEnableVertexAttribArray(i);

        //Bind renderer's atlas texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.textureId);

        //Update the vbo
        float[] instanceVBOData = new float[batchQueue.size() * INSTANCE_DATA_LENGTH];
        float[] uvs = new float[batchQueue.size() * 12];

        instanceDataPos = 0;
        for (int i = 0; i < batchQueue.size(); i++) {
            //Combine all data into one float array
            BatchInfo currentBatch = batchQueue.get(i);
            storeMatrixInFloat(projection, instanceVBOData);
            storeMatrixInFloat(currentBatch.transformationMatrix, instanceVBOData);
            instanceVBOData[instanceDataPos++] = textureAtlas.calculateXOffset();
            instanceVBOData[instanceDataPos++] = textureAtlas.calculateYOffset();
        }
        updateInstanceVBO(instanceVBOId, instanceVBOData, instanceBuffer);  //Update Instance VBO

        //Draw vertices
        GL31.glDrawArraysInstanced(GL11.GL_TRIANGLES, 0, 6, batchQueue.size());
        MemoryManager.drawCallsAmount++;

        for (int i = 0; i <= 9; i++)
            GL20.glDisableVertexAttribArray(i);

        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void queueRender(int x, int y, int width, int height) {
        queueRender(x, y, width, height, 0, 0, 1, 1);
    }

    public void queueRender(int x, int y, int width, int height, float u1, float v1, float v2, float u2) {
        float[] uvs = {
                u1, v1,
                u1, v2,
                u2, v2,
                u1, v1,
                u2, v2,
                u2, v1,
        };
        batchQueue.add(new BatchInfo(x, y, width, height, uvs));
    }

    public void queueRender(int x, int y, int width, int height, float[] uvs) {
        batchQueue.add(new BatchInfo(x, y, width, height, uvs));
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

        public Matrix4f transformationMatrix;
        public float[] uvs;

        BatchInfo(int x, int y, int width, int height, float[] uvs) {
            Vector3f translation = new Vector3f(x, y, 0);
            transformationMatrix = Toolbox.createTransformationMatrix(translation, 0.0f, 0.0f, 0.0f, 1.0f);
            transformationMatrix = transformationMatrix.scale((float) width, (float) height, 1.0f);
            this.uvs = uvs;
        }
    }
}
