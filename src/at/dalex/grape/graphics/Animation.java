package at.dalex.grape.graphics;

import java.awt.image.BufferedImage;

import at.dalex.grape.renderer.graphicsutil.Image;
import at.dalex.grape.renderer.graphicsutil.ImageUtils;

/**
 * Created by Da vid on 14.01.2018.
 */

/**
 * This class takes in an array of images.
 * Calling <code>getImage()</code> gives you the appropriate
 * image in the animation cycle.
 *
 * @author dalex
 */
public class Animation {

    private Image[] frames;
    private int currentFrame;
    private int numFrames;

    private int count;
    private int delay;

    private int timesPlayed;

    public Animation() {
        timesPlayed = 0;
        currentFrame = 0;
    }

    public void setFrames(Image[] frames) {
        this.frames = frames;
        currentFrame = 0;
        count = 0;
        timesPlayed = 0;
        delay = 2;
        numFrames = frames.length;
    }

    public void setDelay(int i) { delay = i; }
    public void setFrame(int i) { currentFrame = i; }
    public void setNumFrames(int i) { numFrames = i; }

    public void update() {

        if(delay == -1) return;

        count++;

        if(count == delay) {
            currentFrame++;
            count = 0;
        }
        if(currentFrame == numFrames) {
            currentFrame = 0;
            timesPlayed++;
        }
    }

    public int getFrame() { return currentFrame; }
    public int getCount() { return count; }
    public int getFrameCount() { return frames.length; }
    public Image getImage() { return frames[currentFrame]; }
    public boolean hasPlayedOnce() { return timesPlayed > 0; }
    public boolean hasPlayed(int i) { return timesPlayed == i; }
    public void setPlayed(int i) { timesPlayed = i; }

    public static Animation loadAnimation(BufferedImage animationAtlas, int frameWidth, int frameHeight, int delay) {

        Animation animation = new Animation();
        Image[] frames = new Image[animationAtlas.getWidth() / frameWidth];

        for (int index = 0; index < (animationAtlas.getWidth() / frameWidth); index++) {
            frames[index] = ImageUtils.convertBufferedImage(animationAtlas.getSubimage(index * frameWidth, 0, frameWidth, frameHeight));
        }

        animation.setFrames(frames);
        animation.setDelay(delay);

        return animation;
    }
}