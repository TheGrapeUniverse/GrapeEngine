package at.dalex.grape.graphics;

import at.dalex.grape.map.chunk.Chunk;
import at.dalex.grape.map.chunk.ChunkWorld;
import org.joml.Matrix4f;

public class ChunkWorldRenderer {

    private BatchRenderer chunkRenderer;
    private Tileset tileset;
    private ChunkWorld world;

    public ChunkWorldRenderer(Tileset tileset, ChunkWorld world) {
        this.chunkRenderer = new BatchRenderer(
                tileset.getRawTextureImage(),
                tileset.getNumberOfRows(),
                tileset.getNumberOfColumns());
        this.tileset = tileset;
        this.world = world;
    }

    public void drawChunkAt(int xPos, int yPos, Matrix4f projectionAndViewMatrix) {
        Chunk chunk  = world.getChunkAt(xPos, yPos);
        if (chunk != null) {
            for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
                for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                    int size = tileset.getTileSize();
                    chunkRenderer.queueRender(
                            (xPos * Chunk.CHUNK_SIZE * size) + x * size,
                            (yPos * Chunk.CHUNK_SIZE * size) + y * size,
                            size, size, chunk.getTileAt(x, y).getId());
                }
            }
        }
        chunkRenderer.drawQueue(projectionAndViewMatrix);
        chunkRenderer.flush();
    }
}
