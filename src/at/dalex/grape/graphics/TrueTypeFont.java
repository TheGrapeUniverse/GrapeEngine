package at.dalex.grape.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import at.dalex.grape.renderer.graphicsutil.Image;
import at.dalex.grape.renderer.graphicsutil.ImageUtils;

public class TrueTypeFont {

//	private static final SGL GL = Renderer.get();
	private IntObject[] charArray;
	private Map customChars;
	private boolean antiAlias;
	private int fontSize;
	private int fontHeight;
	private Image fontTexture;
	private int textureWidth;
	private int textureHeight;
	private java.awt.Font font;
	private FontMetrics fontMetrics;

	public TrueTypeFont(java.awt.Font font, boolean antiAlias, char[] additionalChars) {
		
		this.charArray = new IntObject[256];

		this.customChars = new HashMap();
		this.fontSize = 0;
		this.fontHeight = 0;
		this.textureWidth = 512;
		this.textureHeight = 512;

		this.font = font;
		this.fontSize = font.getSize();
		this.antiAlias = antiAlias;

		createSet(additionalChars);
	}

	public TrueTypeFont(java.awt.Font font, boolean antiAlias) {
		this(font, antiAlias, null);
	}

	private BufferedImage getFontImage(char ch) {
		BufferedImage tempfontImage = new BufferedImage(1, 1, 2);

		Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
		if (this.antiAlias == true) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		g.setFont(this.font);
		this.fontMetrics = g.getFontMetrics();
		int charwidth = this.fontMetrics.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = this.fontMetrics.getHeight();
		if (charheight <= 0) {
			charheight = this.fontSize;
		}

		BufferedImage fontImage = new BufferedImage(charwidth, charheight, 2);

		Graphics2D gt = (Graphics2D) fontImage.getGraphics();
		if (this.antiAlias == true) {
			gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		gt.setFont(this.font);

		gt.setColor(Color.WHITE);
		int charx = 0;
		int chary = 0;
		gt.drawString(String.valueOf(ch), charx, chary + this.fontMetrics.getAscent());

		return fontImage;
	}

	private void createSet(char[] customCharsArray) {
		if ((customCharsArray != null) && (customCharsArray.length > 0)) {
			this.textureWidth *= 2;
		}

		BufferedImage imgTemp = new BufferedImage(this.textureWidth, this.textureHeight, 2);
		Graphics2D g = (Graphics2D) imgTemp.getGraphics();

		g.setColor(new Color(255, 255, 255, 1));
		g.fillRect(0, 0, this.textureWidth, this.textureHeight);

		int rowHeight = 0;
		int positionX = 0;
		int positionY = 0;

		int customCharsLength = (customCharsArray != null) ? customCharsArray.length : 0;

		for (int i = 0; i < 256 + customCharsLength; ++i) {
			char ch = (i < 256) ? (char) i : customCharsArray[(i - 256)];

			BufferedImage fontImage = getFontImage(ch);

			IntObject newIntObject = new IntObject();

			newIntObject.width = fontImage.getWidth();
			newIntObject.height = fontImage.getHeight();

			if (positionX + newIntObject.width >= this.textureWidth) {
				positionX = 0;
				positionY += rowHeight;
				rowHeight = 0;
			}

			newIntObject.storedX = positionX;
			newIntObject.storedY = positionY;

			if (newIntObject.height > this.fontHeight) {
				this.fontHeight = newIntObject.height;
			}

			if (newIntObject.height > rowHeight) {
				rowHeight = newIntObject.height;
			}

			g.drawImage(fontImage, positionX, positionY, null);

			positionX += newIntObject.width;

			if (i < 256)
				this.charArray[i] = newIntObject;
			else {
				this.customChars.put(new Character(ch), newIntObject);
			}

			fontImage = null;
		}

		this.fontTexture = ImageUtils.convertBufferedImage(imgTemp);
	}

	private void drawQuad(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY, float srcX2,
			float srcY2) {
		float DrawWidth = drawX2 - drawX;
		float DrawHeight = drawY2 - drawY;
		float TextureSrcX = srcX / this.textureWidth;
		float TextureSrcY = srcY / this.textureHeight;
		float SrcWidth = srcX2 - srcX;
		float SrcHeight = srcY2 - srcY;
		float RenderWidth = SrcWidth / this.textureWidth;
		float RenderHeight = SrcHeight / this.textureHeight;

		GL11.glTexCoord2f(TextureSrcX, TextureSrcY);
		GL11.glVertex2f(drawX, drawY);
		GL11.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
		GL11.glVertex2f(drawX, drawY + DrawHeight);
		GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
		GL11.glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
		GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
		GL11.glVertex2f(drawX + DrawWidth, drawY);
	}

	public int getWidth(String whatchars) {
		int totalwidth = 0;
		IntObject intObject = null;
		int currentChar = 0;
		for (int i = 0; i < whatchars.length(); ++i) {
			currentChar = whatchars.charAt(i);
			if (currentChar < 256)
				intObject = this.charArray[currentChar];
			else {
				intObject = (IntObject) this.customChars.get(new Character((char) currentChar));
			}

			if (intObject != null)
				totalwidth += intObject.width;
		}
		return totalwidth;
	}

	public int getHeight() {
		return this.fontHeight;
	}

	public int getHeight(String HeightString) {
		return this.fontHeight;
	}

	public int getLineHeight() {
		return this.fontHeight;
	}

	public void drawString(float x, float y, String whatchars, Color color) {
		drawString(x, y, whatchars, color, 0, whatchars.length() - 1);
	}

	public void drawString(float x, float y, String whatchars, Color color, int startIndex, int endIndex) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTexture.getTextureId());

		IntObject intObject = null;

		GL11.glBegin(GL11.GL_QUADS);

		int totalwidth = 0;
		for (int i = 0; i < whatchars.length(); ++i) {
			int charCurrent = whatchars.charAt(i);
			if (charCurrent < 256)
				intObject = this.charArray[charCurrent];
			else {
				intObject = (IntObject) this.customChars.get(new Character((char) charCurrent));
			}

			if (intObject != null) {
				if ((i >= startIndex) || (i <= endIndex)) {
					drawQuad(x + totalwidth, y, x + totalwidth + intObject.width, y + intObject.height,
							intObject.storedX, intObject.storedY, intObject.storedX + intObject.width,
							intObject.storedY + intObject.height);
				}

				totalwidth += intObject.width;
			}
		}

		GL11.glEnd();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
	}

	public void drawString(float x, float y, String whatchars) {
		drawString(x, y, whatchars, Color.white);
	}

	private class IntObject {
		public int width;
		public int height;
		public int storedX;
		public int storedY;
	}
}
