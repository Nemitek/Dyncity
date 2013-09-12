package org.com.nemitek.input;

import org.com.nemitek.math.MathHelper;
import org.com.nemitek.render.Camera;
import org.com.nemitek.render.meshes.MeshManager;
import org.com.nemitek.system.Core;
import org.com.nemitek.system.Time;
import org.lwjgl.input.Keyboard;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/7/13
 * Time: 8:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class Input {
    public static void KeyboardInput()
    {
        Camera mainCamera = Core.getActiveCamera();
        Integer delta = Time.getDelta();

        // Turn velocity - 180 degrees per second
        Double turnVelocity = MathHelper.PI / 1000;

        if(Keyboard.isKeyDown(Keyboard.KEY_W))
        {
            mainCamera.doPitch(turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_S))
        {
            mainCamera.doPitch(-turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_D))
        {
            mainCamera.doRoll(turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_A))
        {
            mainCamera.doRoll(-turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_E))
        {
            mainCamera.doYaw(turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_Q))
        {
            mainCamera.doYaw(-turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_UP))
        {
            mainCamera.doMoveZ(-turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))
        {
            mainCamera.doMoveZ(turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
        {
            mainCamera.doMoveX(turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))
        {
            mainCamera.doMoveX(-turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
        {
            mainCamera.doMoveY(turnVelocity * delta.doubleValue());
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
        {
            mainCamera.doMoveY(-turnVelocity * delta.doubleValue());
        }

        while(Keyboard.next()) {
            // Only listen to events where the key was pressed (down event)
            if (!Keyboard.getEventKeyState()) continue;

            // Switch textures depending on the key released
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_1:
                    MeshManager.buildingMesh.getBody().setTextureName("building1_body");
                    break;
                case Keyboard.KEY_2:
                    MeshManager.buildingMesh.getBody().setTextureName("stGrid2");
                    break;
            }
        }
    }

}
