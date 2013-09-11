package org.com.nemitek.system;

import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;

/**
 * User: Michael Kotlikov
 * Email: mkotlikov@gmail.com
 * Website: www.nemitek.com
 * Date: 9/6/13
 * Time: 8:44 PM
 */
public class Time {
    private static Long lastFrame = 0L;
    private static Integer framesPerSecond = 0;
    private static Integer framesPerSecondCurrent = 0;
    private static Long lastFPS = getTime();
    private static Integer delta = 0;

    /**
     * updateDelta
     * Calculate how many milliseconds have passed
     * since last frame.
     */
    private static void updateDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;
        Time.delta = delta;
    }

    /**
     * getDelta
     * Returns the number of milliseconds since the last tick.
     *
     * @return milliseconds passed since last frame
     */
    public static Integer getDelta()
    {
        return Time.delta;
    }

    /**
     * getTime
     * Get the accurate system time
     *
     * @return The system time in milliseconds
     */
    public static Long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public static void tick()
    {
        Time.updateFPS();
        Time.updateDelta();
    }

    /**
     * updateFPS
     * Calculate the FPS and set it in the title bar
     */
    private static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            Display.setTitle("Dyncity - FPS: " + Time.getFramesPerSecond());
            framesPerSecond = framesPerSecondCurrent;
            framesPerSecondCurrent = 0;
            lastFPS += 1000;
        }

        framesPerSecondCurrent++;
    }

    /**
     * getFramesPerSecond
     * Gets the current FPS.
     *
     * @return The most recently calculated frames per second value
     */
    public static Integer getFramesPerSecond()
    {
        return framesPerSecond;
    }
}
