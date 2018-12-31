package at.dalex.grape.graphics;

import at.dalex.grape.GrapeEngine;
import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import at.dalex.grape.graphics.graphicsutil.TextureAtlas;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TrueTypeFont {

    private CharBounds[] charArray = new CharBounds[256];

    private boolean antiAlias;
    private int fontSize;
    private int fontHeight;

    private TextureAtlas charAtlas;
    private int textureWidth = 1024;
    private int textureHeight = 1024;

    private Font font;
    private FontMetrics fontMetrics;

    private class CharBounds {
        public int x;
        public int y;
        public int width;
        public int height;
    }

    public TrueTypeFont(Font font, boolean antiAlias) {
        this.font = font;
        this.fontSize = font.getSize();
        this.antiAlias = antiAlias;

        createImageSet();
    }

    private void createImageSet() {
        BufferedImage imgTemp = new BufferedImage(this.textureWidth, this.textureHeight, 2);
        Graphics2D g = (Graphics2D) imgTemp.getGraphics();

        g.setColor(new java.awt.Color(255, 255, 0, 100));   //TODO: Texte haben einen leicht schwarzen Hintergrund. ==> Tranparenz auf 1 gesetzt!
        g.fillRect(0, 0, this.textureWidth, this.textureHeight);

        int rowHeight = 0;
        int positionX = 0;
        int positionY = 0;

        for (int i = 0; i < 256; i++) {
            BufferedImage fontImage = getCharacterImage((char) i);
            CharBounds charBounds = new CharBounds();

            charBounds.width = 64;  //fontImage.getWidth();
            charBounds.height = 64; //fontImage.getHeight();

            if (positionX + charBounds.width >= this.textureWidth) {
                positionX = 0;
                positionY += rowHeight;
                rowHeight = 0;
            }

            charBounds.x = positionX;
            charBounds.y = positionY;

            if (charBounds.height > this.fontHeight) {
                this.fontHeight = charBounds.height;
            }

            if (charBounds.height > rowHeight) {
                rowHeight = charBounds.height;
            }

            g.drawImage(fontImage, positionX, positionY, null);

            positionX += 64;

            this.charArray[i] = charBounds;
        }

        Image charSetImage = ImageUtils.convertBufferedImage(imgTemp);
        this.charAtlas = new TextureAtlas(charSetImage.getTextureId(), charSetImage.getWidth(), 15 /* <-- = Math.sqrt(256 [Anzahl Zeichen]) */);
    }

    private BufferedImage getCharacterImage(char character) {
        //TODO: Wird hier ein neues BufferedImage und Graphics nur für die zeichenhöhe erstellt?
        //      Eine Überarbeitung wäre hier mal gut!

        BufferedImage tempFontImage = new BufferedImage(1, 1, 2);
        Graphics2D g = (Graphics2D) tempFontImage.getGraphics();
        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(this.font);
        this.fontMetrics = g.getFontMetrics();

        int charWidth = this.fontMetrics.charWidth(character);
        int charHeight = this.fontMetrics.getHeight();

        charWidth  = charWidth  <= 0 ? 1 : charWidth;
        charHeight = charHeight <= 0 ? this.fontSize : charHeight;

        BufferedImage charImage = new BufferedImage(charWidth, charHeight, 2);
        Graphics2D gt = (Graphics2D) charImage.getGraphics();
        if (antiAlias)
            gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        gt.setFont(this.font);
        gt.setColor(Color.WHITE);
        gt.drawString(String.valueOf(character), 0, this.fontMetrics.getAscent());

        return charImage;
    }

    public void drawString(int x, int y, String text) {
        this.drawString(x, y, text, Color.white, 0, text.length() - 1);
        Graphics.drawImage(charAtlas.getTextureId(), 0, 0, 512, 512, GrapeEngine.getEngine().getCamera().getProjectionAndViewMatrix());
    }

    public void drawString(int x, int y, String text, Color color, int startIndex, int endIndex) {
        int totalWidth = 0;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        for (int i = 0; i < text.length(); ++i) {
            char currentChar = text.charAt(i);
            CharBounds bounds = this.charArray[currentChar];
            if (bounds != null) {
                if (i <= startIndex || i <= endIndex) {
                    drawQuad(   x + totalWidth,                 y,                          //Quad Position on screen 1
                                x + totalWidth + bounds.width,  y + bounds.height,          //Quad Position on screen 2
                                bounds.x,                       bounds.y,                   //Quad Position on atlas 1
                                bounds.x + bounds.width,        bounds.y + bounds.height);  //Quad Position on atlas 2
                }

                totalWidth += bounds.width;
            }
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    private void drawQuad(int drawX, int drawY, int drawX2, int drawY2, int srcX, int srcY, int srcX2, int srcY2) {
        int width = drawX2 - drawX;
        int height = drawY2 - drawY;

        int cellX = srcX / charAtlas.getCellSize();
        int cellY = srcY / charAtlas.getCellSize();

        int cellId = charAtlas.getNumberOfRows() * cellY + cellX;

        //TODO: Conclusion: Atlas breite und höhe sind quadrate, die boxen der buchstaben
        //      sind aber meistens keine! ==> custom bounds im shader blah blah blah ...

        //Change matrix to orthographic screen matrix when moving camera! (If you're here again <3)
        Graphics.drawImageFromAtlas(charAtlas, cellId, drawX, drawY, height, height, GrapeEngine.getEngine().getCamera().getProjectionAndViewMatrix());
    }

    public int getWidth(String text) {
        int totalWidth = 0;
        CharBounds bounds;
        char currentChar = 0;
        for (int i = 0; i < text.length(); ++i) {
            currentChar = text.charAt(i);
            bounds = currentChar < 255 ? this.charArray[currentChar] : this.charArray[0]; //Cap characters
            if (bounds != null) totalWidth += bounds.width;
        }
        return totalWidth;
    }

    public int getHeight() {
        return this.fontHeight;
    }
}
