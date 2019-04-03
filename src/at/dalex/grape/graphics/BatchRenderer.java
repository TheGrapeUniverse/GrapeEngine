package at.dalex.grape.graphics;

import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.shader.BatchShader;
import at.dalex.grape.toolbox.MemoryManager;
import at.dalex.grape.toolbox.Toolbox;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.BufferUtils.createFloatBuffer;

public class BatchRenderer {

    private static BatchShader shader = new BatchShader();
    private ArrayList<BatchInfo> batchQueue = new ArrayList<>();

    private int textureId;
    private int vaoId;
    private int vVBOId; //Vertex Vertex-Buffer-Object lmao xd
    private int texVBOId;

    private int instanceVBOId;
    private int instanceDataPos = 0;

    //Maximum amount of instances to be drawn at once
    private final int MAX_INSTANCES = 10000;

    // 4 for viewMat, 4 for transformMat, 4 for UVs
    // (Matrices = 4 float per row and column)
    // [4*4 + 4*4 = 32]
    private final int INSTANCE_DATA_LENGTH = 32;

    private FloatBuffer projectionMatrixBuffer;
    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;
    private FloatBuffer instanceBuffer;

    public BatchRenderer(Image atlasImage) {
        this(atlasImage.getTextureId());
    }

    public BatchRenderer(int atlasTextureId) {
        this.textureId = atlasTextureId;

        vaoId = GL30.glGenVertexArrays();
        MemoryManager.createdVAOs.add(vaoId);

        vVBOId = GL15.glGenBuffers();
        texVBOId = GL15.glGenBuffers();
        MemoryManager.createdVBOs.add(vVBOId);
        MemoryManager.createdVBOs.add(texVBOId);

        /* Create FloatBuffers */
        projectionMatrixBuffer = createFloatBuffer(16);
        vertexBuffer = createFloatBuffer(24);
        uvBuffer = createFloatBuffer(4 * 2); //2 UVs for each vertex = 8 floats
        instanceBuffer = createFloatBuffer(MAX_INSTANCES * INSTANCE_DATA_LENGTH);

        //Create VBO used for instanced rendering
        instanceVBOId = createInstanceVBO(INSTANCE_DATA_LENGTH * MAX_INSTANCES);

        //Calculate Vertex-Data
        //If you want to change these, rather use a transformation matrix
        //for each instance in the vertex shader. Thank you.
        float[] vertices = { 0, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 0 };
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
        /* Fill UV-Buffer */
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texVBOId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvBuffer, GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(9); //We're using attribute 9, all from 1-8 are already used by instancing
        GL20.glVertexAttribPointer(9, 2, GL11.GL_FLOAT, false, 0, 0); //2 floats for each vertex = 8 bytes (UV)
        GL20.glDisableVertexAttribArray(9);
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
        buffer.clear();
        buffer.put(data);
        buffer.flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * 4, usage);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void updateInstanceVBO(int vboId, float[] data, FloatBuffer buffer) {
        updateVBO(vboId, data, buffer, GL15.GL_STREAM_DRAW);
    }

    private void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset) {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL30.glBindVertexArray(vao);
        //Multiplied by 4 (4 bytes for each float)
        GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
        GL33.glVertexAttribDivisor(attribute, 1); //Mark VBO to be updated every single instance
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void storeMatrixInFloat(Matrix4f matrix, float[] vboData) {
        vboData[instanceDataPos++] = matrix.m00();
        vboData[instanceDataPos++] = matrix.m01();
        vboData[instanceDataPos++] = matrix.m02();
        vboData[instanceDataPos++] = matrix.m03();
        vboData[instanceDataPos++] = matrix.m10();
        vboData[instanceDataPos++] = matrix.m11();
        vboData[instanceDataPos++] = matrix.m12();
        vboData[instanceDataPos++] = matrix.m13();
        vboData[instanceDataPos++] = matrix.m20();
        vboData[instanceDataPos++] = matrix.m21();
        vboData[instanceDataPos++] = matrix.m22();
        vboData[instanceDataPos++] = matrix.m23();
        vboData[instanceDataPos++] = matrix.m30();
        vboData[instanceDataPos++] = matrix.m31();
        vboData[instanceDataPos++] = matrix.m32();
        vboData[instanceDataPos++] = matrix.m33();
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
        float[] uvs = new float[batchQueue.size() * 8];

        instanceDataPos = 0;
        for (int i = 0; i < batchQueue.size(); i++) {
            //Combine all data into one float array
            BatchInfo currentBatch = batchQueue.get(i);
            storeMatrixInFloat(projection, instanceVBOData);
            storeMatrixInFloat(currentBatch.transformationMatrix, instanceVBOData);
            /* Update UVs */
            uvs[i + 0]  = 0;   //UV1
            uvs[i + 1]  = 0;
            uvs[i + 2]  = 0;   //UV2
            uvs[i + 3]  = 0;
            uvs[i + 4]  = 1;   //UV3
            uvs[i + 5]  = 1;
            uvs[i + 6]  = 0;   //UV4
            uvs[i + 7]  = 1;
        }
        updateInstanceVBO(instanceVBOId, instanceVBOData, instanceBuffer);  //Update Instance VBO
        updateVBO(texVBOId, uvs, BufferUtils.createFloatBuffer(uvs.length), GL15.GL_DYNAMIC_DRAW); //Update UV-VBO

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
        batchQueue.add(new BatchInfo(x, y, width, height, u1, v1, u2, v2));
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
        public float u1, v1, u2, v2;

        BatchInfo(int x, int y, int width, int height, float u1, float v1, float u2, float v2) {
            Vector3f translation = new Vector3f(x, y, 0);
            transformationMatrix = Toolbox.createTransformationMatrix(translation, 0.0f, 0.0f, 0.0f, 1.0f);
            transformationMatrix = transformationMatrix.scale((float) width, (float) height, 1.0f);
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }
    }
}
