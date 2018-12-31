package at.dalex.grape.graphics.font;

import java.awt.image.BufferedImage;

public class CharMeta {

    private int atlasX;
    private int atlasY;
    private int width;
    private int height;

    private BufferedImage characterImage;

    public CharMeta(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getAtlasX() {
        return this.atlasX;
    }

    public void setAtlasX(int atlasX) {
        this.atlasX = atlasX;
    }

    public int getAtlasY() {
        return this.atlasY;
    }

    public void setAtlasY(int atlasY) {
        this.atlasY = atlasY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage getCharacterImage() {
        return characterImage;
    }

    public void setCharacterImage(BufferedImage characterImage) {
        this.characterImage = characterImage;
    }
}
