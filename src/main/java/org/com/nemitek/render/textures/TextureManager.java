package org.com.nemitek.render.textures;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/7/13
 * Time: 9:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextureManager {
    private static Map<String, Texture> textures = new HashMap<String, Texture>();

    public static void addTexture(InputStream inputStream, int textureUnit, String textureName)
    {
        Integer textureId = loadPNGTexture(inputStream, textureUnit);
        Texture texture = new Texture(textureUnit, textureId, textureName);

        // Store the texture
        textures.put(textureName, texture);
    }

    public static void removeTexture(String textureName)
    {
        if(textures.containsKey(textureName))
        {
            GL11.glDeleteTextures(textures.get(textureName).getTextureId());
        }

        textures.remove(textureName);
    }

    /**
     * clearAllTextures
     * Deletes all stored textures.
     */
    public static void clearAllTextures()
    {
        // Delete all textures in OpenGL
        for(Texture texture : textures.values())
        {
            // Delete the texture
            GL11.glDeleteTextures(texture.getTextureId());
        }

        // Delete all textures in the HashMap
        textures.clear();
    }

    public static Texture getTexture(String textureName)
    {
        return textures.get(textureName);
    }

    private static int loadPNGTexture(InputStream inputStream, int textureUnit) {
        ByteBuffer buf = null;
        int tWidth = 0;
        int tHeight = 0;

        try {
            // Link the PNG decoder to this stream
            PNGDecoder decoder = new PNGDecoder(inputStream);

            // Get the width and height of the texture
            tWidth = decoder.getWidth();
            tHeight = decoder.getHeight();


            // Decode the PNG file in a ByteBuffer
            buf = ByteBuffer.allocateDirect(
                    4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();

            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // Create a new texture object in memory and bind it
        int texId = GL11.glGenTextures();
        GL13.glActiveTexture(textureUnit);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        // All RGB bytes are aligned to each other and each component is 1 byte
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Upload the texture data and generate mip maps (for scaling)
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

        // Setup the ST coordinate system
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        // Setup what to do when the texture has to be scaled
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
                GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
                GL11.GL_LINEAR_MIPMAP_LINEAR);

        return texId;
    }
}
