// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import zombie.characters.IsoPlayer;

public class IsoHeatSource {
    private int x;
    private int y;
    private int z;
    private int radius;
    private int temperature;

    public IsoHeatSource(int _x, int _y, int _z, int _radius, int _temperature) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.radius = _radius;
        this.temperature = _temperature;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int _radius) {
        this.radius = _radius;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public void setTemperature(int _temperature) {
        this.temperature = _temperature;
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
}
