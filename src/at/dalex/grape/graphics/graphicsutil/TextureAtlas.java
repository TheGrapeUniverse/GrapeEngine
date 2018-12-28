package at.dalex.grape.graphics.graphicsutil;

public class TextureAtlas extends Image {

    private int numberOfRows = 1;
    private int cellSize;

    /**
     * Creates a new TextureAtlas instance.
     *
     * A Texture Atlas is an Image which contains
     * numberOfRows^2 individual images.
     *
     * (The source image has to be a power of two texture!)
     *
     * Contains the width, height and texture id of a OpenGL texture
     * and information about the atlas itself.
     *
     * @param textureId The OpenGL id of the atlas texture
     * @param atlasWidth The width of the atlas source image
     * @param numberOfRows The number of separate images contained in one row
     */
    public TextureAtlas(int textureId, int atlasWidth, int numberOfRows ) {
        super(textureId, atlasWidth, atlasWidth);

        this.numberOfRows = numberOfRows;
        this.cellSize = atlasWidth / numberOfRows;
    }

    public float[] recalculateUVCoordinates(float[] uvs, int cellId) {
        int cellX = cellId % numberOfRows;
        int cellY = cellId / numberOfRows;

        for (int i = 0; i < uvs.length; i++) {
            uvs[i] /= numberOfRows;
            if (i % 2 == 0) {
                uvs[i] += cellX;
            }
            else uvs[i] += cellY;
        }

        return uvs;
    }

    public int getNumberOfRows() {
        return this.numberOfRows;
    }

    public int getCellSize() {
        return this.cellSize;
    }
}
