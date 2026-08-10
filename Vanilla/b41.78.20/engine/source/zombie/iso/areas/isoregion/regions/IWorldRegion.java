// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.regions;

import java.util.ArrayList;

/**
 * TurboTuTone.
 */
public interface IWorldRegion {
    ArrayList<IsoWorldRegion> getDebugConnectedNeighborCopy();

    ArrayList<IsoWorldRegion> getNeighbors();

    boolean isFogMask();

    boolean isPlayerRoom();

    boolean isFullyRoofed();

    int getRoofCnt();

    int getSquareSize();

    ArrayList<IsoChunkRegion> getDebugIsoChunkRegionCopy();
}
