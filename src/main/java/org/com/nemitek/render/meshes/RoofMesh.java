package org.com.nemitek.render.meshes;

import org.com.nemitek.render.interfaces.MeshInterface;
import org.com.nemitek.render.primitives.VertexData;
import org.com.nemitek.render.textures.TextureManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/11/13
 * Time: 1:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class RoofMesh implements MeshInterface {
    private Integer indicesVBO;
    private Integer meshVAO;
    private Integer meshVBO;
    private Integer indicesCount = 0;
    private ByteBuffer verticesByteBuffer;
    private VertexData[] vertices;
    private String textureName;
    private Integer buildingBodyHeight;

    public Integer getBuildingBodyHeight() {
        return buildingBodyHeight;
    }

    public void setBuildingBodyHeight(Integer buildingBodyHeight) {
        this.close();
        this.buildingBodyHeight = buildingBodyHeight;
        this.init();
    }

    public RoofMesh(int height)
    {
        this.indicesVBO = 0;
        this.meshVAO = 0;
        this.meshVBO = 0;
        this.indicesCount = 0;
        this.verticesByteBuffer = null;
        this.textureName = "concrete1";
        this.buildingBodyHeight = height;

        init();
    }

    private void init() {
        // Load textures
        TextureManager.addTexture(getClass().getResourceAsStream("/assets/images/concrete1.png"), GL13.GL_TEXTURE0, "concrete1");

        // We'll define our quad using 4 vertices of the custom 'TexturedVertex' class
        VertexData v0 = new VertexData();
        v0.setXYZ(-0.5f, MeshManager.groundLevel + this.buildingBodyHeight, -0.5f); v0.setRGB(1, 0, 0); v0.setST(0, 0);
        VertexData v1 = new VertexData();
        v1.setXYZ(0.5f, MeshManager.groundLevel + this.buildingBodyHeight, -0.5f); v1.setRGB(0, 1, 0); v1.setST(1, 0);
        VertexData v2 = new VertexData();
        v2.setXYZ(-0.5f, MeshManager.groundLevel + this.buildingBodyHeight, 0.5f); v2.setRGB(0, 0, 1); v2.setST(0, 1);
        VertexData v3 = new VertexData();
        v3.setXYZ(0.5f, MeshManager.groundLevel + this.buildingBodyHeight, 0.5f); v3.setRGB(1, 1, 1); v3.setST(1, 1);

        this.vertices = new VertexData[] {v0, v1, v2, v3};

        // Put each 'Vertex' in one FloatBuffer
        this.verticesByteBuffer = BufferUtils.createByteBuffer(this.vertices.length *
                VertexData.stride);
        FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
        for (int i = 0; i < this.vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesFloatBuffer.put(this.vertices[i].getElements());
        }
        verticesFloatBuffer.flip();


        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = {
                0, 2, 3,
                3, 1, 0,
        };
        this.indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(this.indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();

        // Create a new Vertex Array Object in memory and select it (bind)
        this.meshVAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(this.meshVAO);

        // Create a new Vertex Buffer Object in memory and select it (bind)
        this.meshVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, this.meshVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);

        // Put the position coordinates in attribute list 0
        GL20.glVertexAttribPointer(0, VertexData.positionElementCount, GL11.GL_FLOAT,
                false, VertexData.stride, VertexData.positionByteOffset);
        // Put the color components in attribute list 1
        GL20.glVertexAttribPointer(1, VertexData.colorElementCount, GL11.GL_FLOAT,
                false, VertexData.stride, VertexData.colorByteOffset);
        // Put the texture coordinates in attribute list 2
        GL20.glVertexAttribPointer(2, VertexData.textureElementCount, GL11.GL_FLOAT,
                false, VertexData.stride, VertexData.textureByteOffset);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        // Deselect (bind to 0) the VAO
        GL30.glBindVertexArray(0);

        // Create a new VBO for the indices and select it (bind) - INDICES
        this.indicesVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indicesVBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer,
                GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    @Override
    public void render() {
        // Bind the texture
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, TextureManager.getTexture(this.textureName).getTextureId());

        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(this.meshVAO);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, this.indicesVBO);

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, this.indicesCount, GL11.GL_UNSIGNED_BYTE, 0);

        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }

    @Override
    public void close() {
        // Select the VAO
        GL30.glBindVertexArray(this.meshVAO);

        // Disable the VBO index from the VAO attributes list
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        // Delete the vertex VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(this.meshVBO);

        // Delete the index VBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(this.indicesVBO);

        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.meshVAO);
    }
}

