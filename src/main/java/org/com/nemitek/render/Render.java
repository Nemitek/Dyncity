package org.com.nemitek.render;

import org.com.nemitek.input.Input;
import org.com.nemitek.math.MathHelper;
import org.com.nemitek.render.meshes.*;
import org.com.nemitek.render.textures.TextureManager;
import org.com.nemitek.system.Core;
import org.com.nemitek.system.Time;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.nio.FloatBuffer;

/**
 * User: Michael Kotlikov
 * Email: mkotlikov@gmail.com
 * Website: www.nemitek.com
 * Date: 9/7/13
 * Time: 2:39 PM
 */
public class Render {

    private static Camera mainCamera = new Camera();
    // Setup variables
    private static final String WINDOW_TITLE = "Dyncity";
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    // Shader variables
    private static int pId = 0;
    private static int vsId = 0;
    private static int fsId = 0;
    // Moving variables
    private static int projectionMatrixLocation = 0;
    private static int viewMatrixLocation = 0;
    private static int modelMatrixLocation = 0;
    private static Matrix4f projectionMatrix = null;
    private static Matrix4f viewMatrix = null;
    private static Matrix4f modelMatrix = null;
    private static Vector3f modelPos = null;
    private static Vector3f modelAngle = null;
    private static Vector3f modelScale = null;
    private static Vector3f cameraPos = null;
    private static FloatBuffer matrix44Buffer = null;

    public static void init()
    {
        setupOpenGL();
        MeshManager.buildingMesh = new BuildingMesh(3);
        MeshManager.groundMesh = new GroundMesh();
        setupShaders();
        setupMatrices();

        // Set the default quad rotation, scale and position values
        modelPos = new Vector3f(0, 0, 0);
        modelAngle = new Vector3f(0, 0, 0);
        modelScale = new Vector3f(1, 1, 1);
        cameraPos = new Vector3f(0, 0, -1);
    }

    public static void destroy()
    {
        destroyOpenGL();
    }

    private static void setupMatrices() {
        // Setup projection matrix
        projectionMatrix = new Matrix4f();
        float fieldOfView = 60f;
        float aspectRatio = (float)WIDTH / (float)HEIGHT;
        float near_plane = 0.1f;
        float far_plane = 100f;

        float y_scale = MathHelper.coTangent(MathHelper.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
        projectionMatrix.m33 = 0;

        // Setup view matrix
        viewMatrix = new Matrix4f();

        // Setup model matrix
        modelMatrix = new Matrix4f();

        // Create a FloatBuffer with the proper size to store our matrices later
        matrix44Buffer = BufferUtils.createFloatBuffer(16);
    }

    private static void setupOpenGL() {
        // Setup an OpenGL context with API version 3.2
        try {
            PixelFormat pixelFormat = new PixelFormat();
            ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
                    .withForwardCompatible(true)
                    .withProfileCore(true);

            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle(WINDOW_TITLE);
            Display.create(pixelFormat, contextAtrributes);

            GL11.glViewport(0, 0, WIDTH, HEIGHT);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Setup an XNA like background color
        GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);

        // Map the internal OpenGL coordinate system to the entire screen
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        GL11.glEnable(GL11.GL_CULL_FACE); // enables face culling
        GL11.glCullFace(GL11.GL_BACK); // tells OpenGL to cull back faces (the sane default setting)
        GL11.glFrontFace(GL11.GL_CCW); // tells OpenGL which faces are considered 'front' (use GL_CW or GL_CCW)
        GL11.glEnable(GL11.GL_DEPTH_TEST);   // Enables Depth Testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);

        // Due to LWJGL buffer check, you can't use smaller sized buffers (min_size = 16 for glGetFloat()).
        FloatBuffer max_a = BufferUtils.createFloatBuffer(16);
        max_a.rewind();

        // Grab the maximum anisotropic filter.
        GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max_a);

        // Set up the anisotropic filter.
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, max_a.get(0));

        exitOnGLError("setupOpenGL");
    }

    private static void setupShaders() {
        // Load the vertex shader
        vsId = loadShader(Render.class.getResourceAsStream("/assets/shaders/vertex_shader.glsl"), GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        fsId = loadShader(Render.class.getResourceAsStream("/assets/shaders/fragment_shader.glsl"), GL20.GL_FRAGMENT_SHADER);

        // Create a new shader program that links both shaders
        pId = GL20.glCreateProgram();
        GL20.glAttachShader(pId, vsId);
        GL20.glAttachShader(pId, fsId);

        // Position information will be attribute 0
        GL20.glBindAttribLocation(pId, 0, "in_Position");
        // Color information will be attribute 1
        GL20.glBindAttribLocation(pId, 1, "in_Color");
        // Textute information will be attribute 2
        GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");

        GL20.glLinkProgram(pId);
        GL20.glValidateProgram(pId);

        // Get matrices uniform locations
        projectionMatrixLocation = GL20.glGetUniformLocation(pId,"projectionMatrix");
        viewMatrixLocation = GL20.glGetUniformLocation(pId, "viewMatrix");
        modelMatrixLocation = GL20.glGetUniformLocation(pId, "modelMatrix");

        exitOnGLError("setupShaders");
    }

    private static void logicCycle() {
        //-- Input processing
        float rotationDelta = 15f;
        float scaleDelta = 0.1f;
        float posDelta = 0.1f;
        Vector3f scaleAddResolution = new Vector3f(scaleDelta, scaleDelta, scaleDelta);
        Vector3f scaleMinusResolution = new Vector3f(-scaleDelta, -scaleDelta,
                -scaleDelta);

        Input.KeyboardInput();

        //-- Update matrices
        // Reset view and model matrices
        //viewMatrix = new Matrix4f();
        viewMatrix = Core.getActiveCamera().getCameraMatrix();
        modelMatrix = new Matrix4f();

        // Translate camera
        //Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);

        // Scale, translate and rotate model
        Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
        Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
        Matrix4f.rotate(MathHelper.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1),
                modelMatrix, modelMatrix);
        Matrix4f.rotate(MathHelper.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0),
                modelMatrix, modelMatrix);
        Matrix4f.rotate(MathHelper.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0),
                modelMatrix, modelMatrix);

        // Upload matrices to the uniform variables
        GL20.glUseProgram(pId);

        projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
        viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
        modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);

        GL20.glUseProgram(0);

        exitOnGLError("logicCycle");
    }

    private static void renderCycle() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL20.glUseProgram(pId);

        MeshManager.buildingMesh.render();
        MeshManager.groundMesh.render();

        GL20.glUseProgram(0);

        exitOnGLError("renderCycle");
    }

    public static void loopCycle() {
        // Update logic
        logicCycle();
        // Update rendered frame
        renderCycle();

        exitOnGLError("loopCycle");
    }

    private static void destroyOpenGL() {
        TextureManager.clearAllTextures();

        // Delete the shaders
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(pId);

        MeshManager.buildingMesh.close();
        MeshManager.groundMesh.close();

        exitOnGLError("destroyOpenGL");

        Display.destroy();
    }

    private static int loadShader(InputStream inputStream, int type) {
        StringBuilder shaderSource = new StringBuilder();
        int shaderID = 0;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));;
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }

        shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);

        if (GL20.glGetShader(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }

        exitOnGLError("loadShader");

        return shaderID;
    }

    private static void exitOnGLError(String errorMessage) {
        int errorValue = GL11.glGetError();

        if (errorValue != GL11.GL_NO_ERROR) {
            String errorString = GLU.gluErrorString(errorValue);
            System.err.println("ERROR - " + errorMessage + ": " + errorString);

            if (Display.isCreated()) Display.destroy();
            System.exit(-1);
        }
    }
}
