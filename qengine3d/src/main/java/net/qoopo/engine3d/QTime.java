package net.qoopo.engine3d;

/**
 *
 * @author Alberto
 */
public class QTime {

    public static int FPS;
    public static long delta;

    private static long frameStartTime = System.nanoTime();
    private static long frameLastTime = System.nanoTime();
    private static int framesCount;

    public static void update() {
        long currentFrameTime = System.nanoTime();
        if (currentFrameTime - frameStartTime >= 1000000000) {
            FPS = framesCount;
            framesCount = 0;
            frameStartTime = currentFrameTime;
        } else {
            framesCount++;
        }
        delta = currentFrameTime - frameLastTime;
        frameLastTime = currentFrameTime;
    }

}
