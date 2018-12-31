package at.dalex.grape.graphics.font;

import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import at.dalex.grape.graphics.shader.FontShader;
import org.joml.Matrix4f;

import java.awt.image.BufferedImage;

import java.awt.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class GrapeFont {

    private Font font;
    private boolean antiAlias;
    private int size;
    private CharMeta[] characterInfos = new CharMeta[255];
    private Canvas fontCanvas = new Canvas();  //Used to get the font-metrics

    private Image atlasImage;

    public GrapeFont(Font font, boolean antiAlias) {
        this.font = font;
        this.antiAlias = antiAlias;

        size = font.getSize();

        for (int i = 0; i < 255; i++)
            characterInfos[i] = getCharacterMeta((char) i, antiAlias);

        createAtlasImage();
    }

    private void createAtlasImage() {
        int fontSize = font.getSize();
        // 15 == sqrt(255)
        int atlas_size = 15 * size;
        BufferedImage charAtlas = new BufferedImage(atlas_size, atlas_size, TYPE_INT_ARGB);

        Graphics2D g = (Graphics2D) charAtlas.getGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, atlas_size, atlas_size);

        g.setColor(Color.WHITE);
        g.setFont(font);

        char currentChar = (char) 0;

        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                g.drawString(String.valueOf(currentChar), y * size, x * size);
                characterInfos[currentChar].setAtlasX(y * size);
                characterInfos[currentChar].setAtlasY(x * size);
                currentChar++;
            }
        }

        this.atlasImage = ImageUtils.convertBufferedImage(charAtlas);
    }

    private CharMeta getCharacterMeta(char character, boolean antiAlias) {
        FontMetrics fontMetrics = fontCanvas.getFontMetrics(font);
        int charWidth = fontMetrics.charWidth(character);
        int charHeight = fontMetrics.getHeight();

        charWidth  = charWidth  <= 0 ? 1 : charWidth;
        charHeight = charHeight <= 0 ? 1 : charHeight;

        //Check if cell-size needs to be bigger
        if (charHeight > size)
            size = charHeight;

        if (charWidth > size)
            size = charWidth;

        BufferedImage charImage = new BufferedImage(charWidth, charHeight, TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) charImage.getGraphics();

        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(font);
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(character), 0, fontMetrics.getAscent());

        //Create CharMeta
        CharMeta meta = new CharMeta(charWidth, charHeight);
        meta.setCharacterImage(charImage);
        return meta;
    }

    public void drawString(String text, int x, int y, Matrix4f projectionAndViewMatrix) {
        Graphics.drawText(this, text, x, y, projectionAndViewMatrix);
    }

    public CharMeta getCharacterInfo(char character) {
        return characterInfos[character];
    }

    public Image getAtlasImage() {
        return this.atlasImage;
    }

    public boolean isAntiAliased() {
        return this.antiAlias;
    }
}
