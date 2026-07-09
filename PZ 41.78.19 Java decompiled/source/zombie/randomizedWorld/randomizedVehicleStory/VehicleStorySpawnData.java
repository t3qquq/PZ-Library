// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import zombie.iso.IsoChunk;
import zombie.iso.IsoMetaGrid;

public final class VehicleStorySpawnData {
    public RandomizedVehicleStoryBase m_story;
    public IsoMetaGrid.Zone m_zone;
    public float m_spawnX;
    public float m_spawnY;
    public float m_direction;
    public int m_x1;
    public int m_y1;
    public int m_x2;
    public int m_y2;

    public VehicleStorySpawnData(
        RandomizedVehicleStoryBase story, IsoMetaGrid.Zone zone, float spawnX, float spawnY, float direction, int x1, int y1, int x2, int y2
    ) {
        this.m_story = story;
        this.m_zone = zone;
        this.m_spawnX = spawnX;
        this.m_spawnY = spawnY;
        this.m_direction = direction;
        this.m_x1 = x1;
        this.m_y1 = y1;
        this.m_x2 = x2;
        this.m_y2 = y2;
    }

    public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        if (zone != this.m_zone) {
            return false;
        } else if (!this.m_story.isFullyStreamedIn(this.m_x1, this.m_y1, this.m_x2, this.m_y2)) {
            return false;
        } else {
            chunk.setRandomVehicleStoryToSpawnLater(null);
            return this.m_story.isValid(zone, chunk, false);
        }
    }
}
