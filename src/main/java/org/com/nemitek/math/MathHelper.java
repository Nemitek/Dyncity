package org.com.nemitek.math;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/9/13
 * Time: 12:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class MathHelper {
    public static final double PI = 3.14159265358979323846;

    public static float coTangent(float angle) {
        return (float)(1f / Math.tan(angle));
    }

    public static float degreesToRadians(float degrees) {
        return degrees * (float)(PI / 180d);
    }
}
