package at.dalex.grape.graphics;

import java.awt.image.BufferedImage;
import at.dalex.grape.graphics.graphicsutil.Image;
import at.dalex.grape.graphics.graphicsutil.ImageUtils;

/**
 * This class takes in an image (atlas) an converts it into an animation cycle.
 *
 * Calling <code>getImage()</code> will give you the appropriate
 * image in the animation cycle.
 *
 * @author dalex
 */
public class Animation {

    private Image[] frames;             //Array of images
    private double currentFrame = 0;    //Current frame index
    private int numFrames;              //Count of total frames
    private int framesPerSecond = 2;    //Amount of frames per second.

    public void setFrames(Image[] frames) {
        this.frames = frames;
        numFrames = frames.length;
    }

    public void update(double delta) {
        currentFrame += (framesPerSecond * delta);
        if ((int) currentFrame >= numFrames) currentFrame = 0;
    }
    public int getFramesPerSecond() {
        return this.framesPerSecond;
    }

    public void setFramesPerSecond(int framesPerSecond) {
        this.framesPerSecond = framesPerSecond;
    }

    public int getCurrentFrame() {
        return (int) currentFrame;
    }

    public void setCurrentFrame(int index) {
        currentFrame = index;
    }

    public int getFrameCount() {
        return frames.length;
    }

    public Image getImage() {
        return frames[(int) currentFrame];
    }

    public static Animation loadAnimation(BufferedImage animationAtlas, int frameWidth, int frameHeight, int framesPerSecond) {
        Animation animation = new Animation();
        Image[] frames = new Image[animationAtlas.getWidth() / frameWidth];

        for (int index = 0; index < (animationAtlas.getWidth() / frameWidth); index++) {
            frames[index] = ImageUtils.convertBufferedImage(animationAtlas.getSubimage(index * frameWidth, 0, frameWidth, frameHeight));
        }

        animation.setFrames(frames);
        animation.setFramesPerSecond(framesPerSecond);

        return animation;
    }
}