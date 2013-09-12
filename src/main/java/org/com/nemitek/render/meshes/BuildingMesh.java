package org.com.nemitek.render.meshes;

import org.com.nemitek.render.interfaces.MeshInterface;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Kotlikov
 * Date: 9/12/13
 * Time: 2:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class BuildingMesh implements MeshInterface {
    private RoofMesh roof;
    private BuildingBodyMesh body;
    private Integer height;

    public RoofMesh getRoof() {
        return roof;
    }

    public void setRoof(RoofMesh roof) {
        this.roof = roof;
    }

    public BuildingBodyMesh getBody() {
        return body;
    }

    public void setBody(BuildingBodyMesh body) {
        this.body = body;
    }

    public BuildingMesh(Integer height)
    {
        this.roof = new RoofMesh(height);
        this.body = new BuildingBodyMesh(height);
        this.height = height;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.roof.setBuildingBodyHeight(height);
        this.body.setBuildingBodyHeight(height);
        this.height = height;
    }

    @Override
    public void render()
    {
        this.roof.render();
        this.body.render();
    }

    @Override
    public void close()
    {
        this.roof.close();
        this.body.close();
    }
}
