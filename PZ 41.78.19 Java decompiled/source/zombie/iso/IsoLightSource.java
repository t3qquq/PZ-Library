// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.opengl.RenderSettings;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.objects.IsoLightSwitch;

public class IsoLightSource {
    public static int NextID = 1;
    public int ID;
    public int x;
    public int y;
    public int z;
    public float r;
    public float g;
    public float b;
    public float rJNI;
    public float gJNI;
    public float bJNI;
    public int radius;
    public boolean bActive;
    public boolean bWasActive;
    public boolean bActiveJNI;
    public int life = -1;
    public int startlife = -1;
    public IsoBuilding localToBuilding;
    public boolean bHydroPowered = false;
    public ArrayList<IsoLightSwitch> switches = new ArrayList<>(0);
    public IsoChunk chunk;
    public Object lightMap;

    public IsoLightSource(int _x, int _y, int _z, float _r, float _g, float _b, int _radius) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.r = _r;
        this.g = _g;
        this.b = _b;
        this.radius = _radius;
        this.bActive = true;
    }

    public IsoLightSource(int _x, int _y, int _z, float _r, float _g, float _b, int _radius, IsoBuilding building) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.r = _r;
        this.g = _g;
        this.b = _b;
        this.radius = _radius;
        this.bActive = true;
        this.localToBuilding = building;
    }

    public IsoLightSource(int _x, int _y, int _z, float _r, float _g, float _b, int _radius, int _life) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.r = _r;
        this.g = _g;
        this.b = _b;
        this.radius = _radius;
        this.bActive = true;
        this.startlife = this.life = _life;
    }

    public void update() {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(this.x, this.y, this.z);
        if (!this.bHydroPowered || IsoWorld.instance.isHydroPowerOn() || square != null && square.haveElectricity()) {
            if (this.bActive) {
                if (this.localToBuilding != null) {
                    this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                    this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                    this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.7F;
                }

                if (this.life > 0) {
                    this.life--;
                }

                if (this.localToBuilding != null && square != null) {
                    this.r = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.rmod * 0.7F;
                    this.g = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.gmod * 0.7F;
                    this.b = RenderSettings.getInstance().getAmbientForPlayer(IsoPlayer.getPlayerIndex()) * 0.8F * IsoGridSquare.bmod * 0.7F;
                }

                for (int int0 = this.x - this.radius; int0 < this.x + this.radius; int0++) {
                    for (int int1 = this.y - this.radius; int1 < this.y + this.radius; int1++) {
                        for (int int2 = 0; int2 < 8; int2++) {
                            square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
                            if (square != null && (this.localToBuilding == null || this.localToBuilding == square.getBuilding())) {
                                LosUtil.TestResults testResults = LosUtil.lineClear(
                                    square.getCell(), this.x, this.y, this.z, square.getX(), square.getY(), square.getZ(), false
                                );
                                if (square.getX() == this.x && square.getY() == this.y && square.getZ() == this.z || testResults != LosUtil.TestResults.Blocked
                                    )
                                 {
                                    float float0 = 0.0F;
                                    float float1;
                                    if (Math.abs(square.getZ() - this.z) <= 1) {
                                        float1 = IsoUtils.DistanceTo(this.x, this.y, 0.0F, square.getX(), square.getY(), 0.0F);
                                    } else {
                                        float1 = IsoUtils.DistanceTo(this.x, this.y, this.z, square.getX(), square.getY(), square.getZ());
                                    }

                                    if (!(float1 > this.radius)) {
                                        float0 = float1 / this.radius;
                                        float0 = 1.0F - float0;
                                        float0 *= float0;
                                        if (this.life > -1) {
                                            float0 *= (float)this.life / this.startlife;
                                        }

                                        float float2 = float0 * this.r * 2.0F;
                                        float float3 = float0 * this.g * 2.0F;
                                        float float4 = float0 * this.b * 2.0F;
                                        square.setLampostTotalR(square.getLampostTotalR() + float2);
                                        square.setLampostTotalG(square.getLampostTotalG() + float3);
                                        square.setLampostTotalB(square.getLampostTotalB() + float4);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            this.bActive = false;
        }
    }

    /**
     * @return the x
     */
    public int getX() {
        return this.x;
    }

    /**
     * 
     * @param _x the x to set
     */
    public void setX(int _x) {
        this.x = _x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return this.y;
    }

    /**
     * 
     * @param _y the y to set
     */
    public void setY(int _y) {
        this.y = _y;
    }

    /**
     * @return the z
     */
    public int getZ() {
        return this.z;
    }

    /**
     * 
     * @param _z the z to set
     */
    public void setZ(int _z) {
        this.z = _z;
    }

    /**
     * @return the r
     */
    public float getR() {
        return this.r;
    }

    /**
     * 
     * @param _r the r to set
     */
    public void setR(float _r) {
        this.r = _r;
    }

    /**
     * @return the g
     */
    public float getG() {
        return this.g;
    }

    /**
     * 
     * @param _g the g to set
     */
    public void setG(float _g) {
        this.g = _g;
    }

    /**
     * @return the b
     */
    public float getB() {
        return this.b;
    }

    /**
     * 
     * @param _b the b to set
     */
    public void setB(float _b) {
        this.b = _b;
    }

    /**
     * @return the radius
     */
    public int getRadius() {
        return this.radius;
    }

    /**
     * 
     * @param _radius the radius to set
     */
    public void setRadius(int _radius) {
        this.radius = _radius;
    }

    /**
     * @return the bActive
     */
    public boolean isActive() {
        return this.bActive;
    }

    /**
     * 
     * @param _bActive the bActive to set
     */
    public void setActive(boolean _bActive) {
        this.bActive = _bActive;
    }

    /**
     * @return the bWasActive
     */
    public boolean wasActive() {
        return this.bWasActive;
    }

    /**
     * 
     * @param _bWasActive the bWasActive to set
     */
    public void setWasActive(boolean _bWasActive) {
        this.bWasActive = _bWasActive;
    }

    /**
     * @return the switches
     */
    public ArrayList<IsoLightSwitch> getSwitches() {
        return this.switches;
    }

    /**
     * 
     * @param _switches the switches to set
     */
    public void setSwitches(ArrayList<IsoLightSwitch> _switches) {
        this.switches = _switches;
    }

    public void clearInfluence() {
        for (int int0 = this.x - this.radius; int0 < this.x + this.radius; int0++) {
            for (int int1 = this.y - this.radius; int1 < this.y + this.radius; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
                    if (square != null) {
                        square.setLampostTotalR(0.0F);
                        square.setLampostTotalG(0.0F);
                        square.setLampostTotalB(0.0F);
                    }
                }
            }
        }
    }

    public boolean isInBounds(int minX, int minY, int maxX, int maxY) {
        return this.x >= minX && this.x < maxX && this.y >= minY && this.y < maxY;
    }

    public boolean isInBounds() {
        IsoChunkMap[] chunkMaps = IsoWorld.instance.CurrentCell.ChunkMap;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            if (!chunkMaps[int0].ignore) {
                int int1 = chunkMaps[int0].getWorldXMinTiles();
                int int2 = chunkMaps[int0].getWorldXMaxTiles();
                int int3 = chunkMaps[int0].getWorldYMinTiles();
                int int4 = chunkMaps[int0].getWorldYMaxTiles();
                if (this.isInBounds(int1, int3, int2, int4)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isHydroPowered() {
        return this.bHydroPowered;
    }

    public IsoBuilding getLocalToBuilding() {
        return this.localToBuilding;
    }
}
