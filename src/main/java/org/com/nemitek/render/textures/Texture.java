package org.com.nemitek.render.textures;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/7/13
 * Time: 10:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Texture {
    private Integer textureUnit;
    private Integer textureId;
    private String textureName;

    public Texture(Integer textureUnit, Integer textureId, String textureName)
    {
        this.textureId = textureId;
        this.textureUnit = textureUnit;
        this.textureName = textureName;
    }

    public String getTextureName() {
        return textureName;
    }

    public void setTextureName(String textureName) {
        this.textureName = textureName;
    }

    public Integer getTextureUnit() {
        return textureUnit;
    }

    public void setTextureUnit(Integer textureUnit) {
        this.textureUnit = textureUnit;
    }

    public Integer getTextureId() {
        return textureId;
    }

    public void setTextureId(Integer textureId) {
        this.textureId = textureId;
    }

    public void close()
    {
        TextureManager.removeTexture(this.textureName);
        this.textureUnit = null;
        this.textureName = null;
        this.textureId = null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Texture texture = (Texture) o;

        if (!this.textureId.equals(texture.textureId)) return false;
        if (!this.textureName.equals(texture.textureName)) return false;
        if (!this.textureUnit.equals(texture.textureUnit)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (this.textureUnit == null ? 0 : this.textureUnit.hashCode());
        result = 31 * result + (this.textureId == null ? 0 : this.textureId.hashCode());
        result = 31 * result + (this.textureName == null ? 0: this.textureName.hashCode());
        return result;
    }
}
