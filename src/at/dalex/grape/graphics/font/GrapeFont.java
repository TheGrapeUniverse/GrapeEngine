package at.dalex.grape.graphics.font;


import at.dalex.grape.graphics.graphicsutil.Graphics;
import at.dalex.grape.graphics.shader.FontShader;
import at.dalex.grape.toolbox.ConvertionUtil;
import at.dalex.grape.toolbox.Dialog;
import at.dalex.grape.toolbox.IOUtil;
import at.dalex.grape.toolbox.MemoryManager;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static java.sql.Types.NULL;

public class GrapeFont {

    private final int BITMAP_WIDTH  = 2048;
    private final int BITMAP_HEIGHT = 2048;

    private Canvas canvas = new Canvas(); //Used to get FontMetrics
    private Font font;
    private FontMetrics fontMetrics;
    private int fontSize;

    private final STBTTAlignedQuad quad  = STBTTAlignedQuad.malloc();
    private STBTTPackedchar.Buffer charData;
    private int fontTexture;
    private FloatBuffer xBuffer = MemoryUtil.memAllocFloat(1);
    private FloatBuffer yBuffer = MemoryUtil.memAllocFloat(1);

    private static FontShader shader = new FontShader();
    private FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    private int vaoID;
    private int vboID;

    public GrapeFont(String fontPath, int fontSize) {
        this.fontSize = fontSize;

        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(12f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            Dialog.error("Font Error", "Could not create Font from file '" + fontPath + "'!");
        }

        this.fontMetrics = canvas.getFontMetrics(font);

        loadFont(fontPath);
        createVAO();
    }

    private void createVAO() {
        vaoID = GL30.glGenVertexArrays();
        MemoryManager.createdVAOs.add(vaoID);

        vboID = GL15.glGenBuffers();
        MemoryManager.createdVBOs.add(vboID);
        GL30.glBindVertexArray(vaoID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(24);
        floatBuffer.put(new float[24]);
        floatBuffer.flip();

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatBuffer, GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 4 * 4, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    private void loadFont(String fontPath) {
        this.fontTexture    = GL11.glGenTextures();
        this.charData       = STBTTPackedchar.malloc(6 * 128);

        try (STBTTPackContext packContext = STBTTPackContext.malloc()) {
            ByteBuffer trueTypeFont = IOUtil.ioResourceToByteBuffer(fontPath, 512 * 1024);
            ByteBuffer bitmapBuffer = BufferUtils.createByteBuffer(BITMAP_WIDTH * BITMAP_HEIGHT);

            STBTruetype.stbtt_PackBegin(packContext, bitmapBuffer, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, NULL);

            //When storing multiple font sizes at once, keep this code in a loop for all sizes
            int position = /* (i * 3) * <== Add this code when iterating ==> */ 128 + 32;
            charData.limit(position + 95);
            charData.position(position);

            STBTruetype.stbtt_PackSetOversampling(packContext, 2, 2);
            STBTruetype.stbtt_PackFontRange(packContext, trueTypeFont, 0, fontSize * 2, 32, charData); // <== 200 ==> FontSize
            //End of loop

            charData.clear();
            STBTruetype.stbtt_PackEnd(packContext);

            //Create font image
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTexture);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RED, BITMAP_WIDTH, BITMAP_HEIGHT, 0, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE, bitmapBuffer);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawString(String text, int x, int y, Color color, Matrix4f projectionAndOrViewMatrix) {
        xBuffer.put(0, x);
        yBuffer.put(0, y);
        charData.position(128);

        shader.start();

        GL30.glBindVertexArray(vaoID);
        GL20.glEnableVertexAttribArray(0);

        /* Uniforms */
        float[] textColor = ConvertionUtil.normalizeColors(color);
        projectionAndOrViewMatrix.get(matrixBuffer);
        GL20.glUniform3f(shader.position_text_color, textColor[0], textColor[1], textColor[2]);
        GL20.glUniformMatrix4fv(shader.position_projectionMatrix, false, matrixBuffer);

        /* Texture */
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTexture);
        at.dalex.grape.graphics.graphicsutil.Graphics.enableBlending(true);

        for (int i = 0; i < text.length(); i++) {
            STBTruetype.stbtt_GetPackedQuad(charData, BITMAP_WIDTH, BITMAP_HEIGHT, text.charAt(i), xBuffer, yBuffer,
                    quad, false); //false?

            int yOffset = fontMetrics.getAscent();

            float[] vertices = {
                    //Triangle 1
                    (quad.x0() / 2), (quad.y0() / 2) + yOffset, quad.s0(), quad.t0(),
                    (quad.x0() / 2), (quad.y1() / 2) + yOffset, quad.s0(), quad.t1(),
                    (quad.x1() / 2), (quad.y1() / 2) + yOffset, quad.s1(), quad.t1(),

                    //Triangle 2
                    (quad.x0() / 2), (quad.y0() / 2) + yOffset, quad.s0(), quad.t0(),
                    (quad.x1() / 2), (quad.y1() / 2) + yOffset, quad.s1(), quad.t1(),
                    (quad.x1() / 2), (quad.y0() / 2) + yOffset, quad.s1(), quad.t0()

            };

            System.out.println("height: " + fontMetrics.getHeight());
            System.out.println("descend: " + fontMetrics.getDescent());
            System.out.println("ascend: " + fontMetrics.getAscent());
            System.out.println("y: " + quad.y0());

            //Update vertex data
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(vertices.length);
            floatBuffer.put(vertices);
            floatBuffer.flip();

            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, floatBuffer);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }

        shader.stop();

        GL30.glBindVertexArray(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}
