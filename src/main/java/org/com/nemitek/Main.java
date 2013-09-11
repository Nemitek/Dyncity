package org.com.nemitek;

import org.com.nemitek.render.Render;
import org.com.nemitek.system.Time;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;


/**
 * User: Michael Kotlikov
 * Email: mkotlikov@gmail.com
 * Website: www.nemitek.com
 * Date: 9/6/13
 * Time: 7:45 PM
 */
public class Main {
    public static void main(String [] args)
    {

        // init OpenGL here
        Render.init();

        while (!Display.isCloseRequested()) {

            // Update all time related properties
            Time.tick();

            //pollInput();
            Render.loopCycle();

            // Force a maximum FPS of about 60
            //Display.sync(60);
            Display.update();
        }

        Render.destroy();
    }

    public static void pollInput() {

        if (Mouse.isButtonDown(0)) {
            int x = Mouse.getX();
            int y = Mouse.getY();

            System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            System.out.println("SPACE KEY IS DOWN");
        }

        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_A) {
                    System.out.println("A Key Pressed");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_S) {
                    System.out.println("S Key Pressed");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_D) {
                    System.out.println("D Key Pressed");
                }
            } else {
                if (Keyboard.getEventKey() == Keyboard.KEY_A) {
                    System.out.println("A Key Released");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_S) {
                    System.out.println("S Key Released");
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_D) {
                    System.out.println("D Key Released");
                }
            }
        }
    }
}
