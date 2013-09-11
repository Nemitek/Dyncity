package org.com.nemitek.render;

import org.com.nemitek.math.MathHelper;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/9/13
 * Time: 12:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class Camera {
    private Double xLoc;
    private Double yLoc;
    private Double zLoc;
    private static final Double fullCircleRadsPositive = 2D * MathHelper.PI;
    private static final Double fullCircleRadsNegative = 2D * MathHelper.PI * -1D;
    private Matrix4f cameraMatrix;

    public Camera()
    {
        xLoc = 0D;
        yLoc = 0D;
        zLoc = 0D;
        cameraMatrix = new Matrix4f();
        Matrix4f.translate(new Vector3f(0, 0, -3), cameraMatrix, cameraMatrix);
    }

    public Matrix4f getCameraMatrix() {
        return cameraMatrix;
    }

    public void setCameraMatrix(Matrix4f cameraMatrix) {
        this.cameraMatrix = cameraMatrix;
    }

    /**
     * doMoveX
     * Move the camera along the X-Axis.
     *
     * @param moveX
     */
    public void doMoveX(Double moveX)
    {
        this.xLoc = this.xLoc + moveX;
        Matrix4f matrix = new Matrix4f();
        Matrix4f.translate(new Vector3f(-moveX.floatValue(), 0, 0), matrix, matrix);
        Matrix4f.mul(matrix, cameraMatrix, cameraMatrix);
    }

    /**
     * doMoveY
     * Move the camera along the Y-Axis.
     *
     * @param moveY
     */
    public void doMoveY(Double moveY)
    {
        this.yLoc = this.yLoc + moveY;
        Matrix4f matrix = new Matrix4f();
        Matrix4f.translate(new Vector3f(0, -moveY.floatValue(), 0), matrix, matrix);
        Matrix4f.mul(matrix, cameraMatrix, cameraMatrix);
    }

    /**
     * doMoveZ
     * Move the camera along the Z-Axis.
     *
     * @param moveZ
     */
    public void doMoveZ(Double moveZ)
    {
        this.zLoc = this.zLoc + moveZ;
        Matrix4f matrix = new Matrix4f();
        Matrix4f.translate(new Vector3f(0, 0, -moveZ.floatValue()), matrix, matrix);
        Matrix4f.mul(matrix, cameraMatrix, cameraMatrix);
    }

    /**
     * doRoll
     * Rotate the camera around the Z-Axis.
     *
     * @param angle in radians
     */
    public void doRoll(Double angle)
    {
        Matrix4f matrix = new Matrix4f();
        Matrix4f.rotate(angle.floatValue(), new Vector3f(0, 0, 1), matrix, matrix);
        Matrix4f.mul(matrix, cameraMatrix, cameraMatrix);
    }

    /**
     * doPitch
     * Rotate the camera around the X-Axis.
     *
     * @param angle in radians
     */
    public void doPitch(Double angle)
    {
        Matrix4f matrix = new Matrix4f();
        Matrix4f.rotate(angle.floatValue(), new Vector3f(1, 0, 0), matrix, matrix);
        Matrix4f.mul(matrix, cameraMatrix, cameraMatrix);
    }

    /**
     * doYaw
     * Rotate the camera around the Y-Axis.
     *
     * @param angle in radians
     */
    public void doYaw(Double angle)
    {
        Matrix4f matrix = new Matrix4f();
        Matrix4f.rotate(angle.floatValue(), new Vector3f(0, 1, 0), matrix, matrix);
        Matrix4f.mul(matrix, cameraMatrix, cameraMatrix);
    }

    /**
     * rotationBoundary
     * Creates a loop around boundary for any angle greater than 2PI or less than -2PI.
     * If it is greater than 2PI then subtract 2PI, if it is less than -2PI then add
     * 2PI.
     *
     * @param angle in radians
     * @return
     */
    private Double rotationBoundary(Double angle)
    {
        if(angle >= fullCircleRadsPositive)
        {
            Integer multiplier = (int)(angle / fullCircleRadsPositive);
            return angle - fullCircleRadsPositive * multiplier;
        }
        else if(angle <= fullCircleRadsNegative)
        {
            Integer multiplier = (int)(-angle / fullCircleRadsPositive);
            return angle + fullCircleRadsPositive * multiplier;
        }
        else
        {
            return angle;
        }
    }
}
