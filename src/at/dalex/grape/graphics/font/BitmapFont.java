package at.dalex.grape.graphics.font;

import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;
import at.dalex.grape.graphics.shader.FontShader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Font.MONOSPACED;
import static java.awt.Font.PLAIN;

public class BitmapFont {

    private final Map<Character, Glyph> glyphs;
    private Image texture;
    private int fontHeight;

    public BitmapFont() {
        this(new Font(Font.MONOSPACED, PLAIN, 16), true);
    }

    public BitmapFont(boolean antiAlias) {
        this(new Font(Font.MONOSPACED, PLAIN, 16), antiAlias);
    }

    public BitmapFont(int size, boolean antiAlias) {
        this(new Font(MONOSPACED, PLAIN, size), antiAlias);
    }

    public BitmapFont(InputStream in, int size) throws FontFormatException, IOException {
        this(in, size, true);
    }

    public BitmapFont(InputStream in, int size, boolean antiAlias) throws IOException, FontFormatException {
        this(Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(PLAIN, size), antiAlias);
    }

    public BitmapFont(Font font) {
        this(font, true);
    }

    public BitmapFont(Font font, boolean antiAlias) {
        glyphs = new HashMap<>();
        texture = createFontTexture(font, antiAlias);
    }

    private Image createFontTexture(Font font, boolean antiAlias) {
        /* Loop through the characters to get charWidth and charHeight */
        int width = 0;
        int height = 0;

        for (int i = 32; i < 256; i++) {
            if (i == 127) //DEL Control code
                continue;

            char character = (char) i;
            BufferedImage charImage = createCharImage(font, character, antiAlias);
            if (charImage == null) continue;

            width += charImage.getWidth();
            height = Math.max(height, charImage.getHeight());
        }

        fontHeight = height;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        int x = 0;

        for (int i = 32; i < 256; i++) {
            if (i == 127) //DEL Control code
                continue;

            char character = (char) i;
            BufferedImage charImage = createCharImage(font, character, antiAlias);
            if (charImage == null) continue;

            int charWidth = charImage.getWidth();
            int charHeight = charImage.getHeight();

            //Create glyph and draw char on image
            Glyph glyph = new Glyph(charWidth, charHeight, x, image.getHeight() - charHeight, 0f);
            g.drawImage(charImage, x, 0, null);
            x += glyph.width;
            glyphs.put(character, glyph);
        }

        //Flip image horizontal to get the origin to bottom left
        AffineTransform transform = AffineTransform.getScaleInstance(1f, -1f);
        transform.translate(0, -image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        image = operation.filter(image, null);

        return ImageUtils.convertBufferedImage(image);
    }

    private BufferedImage createCharImage(Font font, char c, boolean antiAlias) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics();
        g.dispose();

        int charWidth = metrics.charWidth(c);
        int charHeight = metrics.getHeight();

        if (charWidth == 0) return null;

        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        g = image.createGraphics();
        if (antiAlias)
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(font);
        g.setPaint(Color.WHITE);
        g.drawString(String.valueOf(c), 0, metrics.getAscent());
        g.dispose();
        return image;
    }

    public int getWidth(String text) {
        int width = 0;
        int lineWidth = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                width = Math.max(width, lineWidth);
                lineWidth = 0;
                continue;
            }
            if (c == '\r') continue;

            Glyph glyph = glyphs.get(c);
            lineWidth += glyph.width;
        }

        width = Math.max(width, lineWidth);
        return width;
    }

    public int getHeight(String text) {
        int height = 0;
        int lineHeight = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                height += lineHeight;
                lineHeight = 0;
                continue;
            }
            if (c == '\r') continue;

            Glyph glyph = glyphs.get(c);
            lineHeight = Math.max(lineHeight, glyph.height);
        }
        height += lineHeight;
        return height;
    }

    public void drawText(String text, int x, int y) {
        int textHeight = getHeight(text);

        int drawX = x;
        int drawY = y;
        if (textHeight > fontHeight) drawY += textHeight - fontHeight;


    }
}
