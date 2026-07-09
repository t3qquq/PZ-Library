// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
