package org.com.nemitek.system;

import org.com.nemitek.render.Camera;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/9/13
 * Time: 11:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Core {
    private static Camera activeCamera = new Camera();

    public static Camera getActiveCamera() {
        return Core.activeCamera;
    }

    public static void setActiveCamera(Camera activeCamera) {
        Core.activeCamera = activeCamera;
    }
}
