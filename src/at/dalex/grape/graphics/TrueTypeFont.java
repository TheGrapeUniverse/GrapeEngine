package at.dalex.grape.graphics;

import at.dalex.grape.graphics.graphicsutil.Image;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TrueTypeFont {

    private CharBounds[] charArray = new CharBounds[256];

    private boolean antiAlias;
    private int fontSize;
    private int fontHeight;

    private Image fontTexture;
    private int textureWidth = 512;
    private int textureHeight = 512;

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
            }
        }
        GL11.glDisable(GL11.GL_TEXTURE_2D);
    }

    private void drawQuad(int drawX, int drawY, int drawX2, int drawY2, int srcX, int srcY, int srcX2, int srcY2) {
        int width = drawX2 - drawX;
        int height = drawY2 - drawY;

        //TODO: Update drawing engine to allow a texture atlas. thank you.
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
