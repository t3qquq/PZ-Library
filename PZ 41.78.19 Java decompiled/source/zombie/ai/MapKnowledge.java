// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai;

import java.util.ArrayList;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;

public final class MapKnowledge {
    private final ArrayList<KnownBlockedEdges> knownBlockedEdges = new ArrayList<>();

    public ArrayList<KnownBlockedEdges> getKnownBlockedEdges() {
        return this.knownBlockedEdges;
    }

    public KnownBlockedEdges getKnownBlockedEdges(int x, int y, int z) {
        for (int int0 = 0; int0 < this.knownBlockedEdges.size(); int0++) {
            KnownBlockedEdges knownBlockedEdgesx = this.knownBlockedEdges.get(int0);
            if (knownBlockedEdgesx.x == x && knownBlockedEdgesx.y == y && knownBlockedEdgesx.z == z) {
                return knownBlockedEdgesx;
            }
        }

        return null;
    }

    private KnownBlockedEdges createKnownBlockedEdges(int int0, int int1, int int2) {
        assert this.getKnownBlockedEdges(int0, int1, int2) == null;

        KnownBlockedEdges knownBlockedEdgesx = KnownBlockedEdges.alloc();
        knownBlockedEdgesx.init(int0, int1, int2);
        this.knownBlockedEdges.add(knownBlockedEdgesx);
        return knownBlockedEdgesx;
    }

    public KnownBlockedEdges getOrCreateKnownBlockedEdges(int x, int y, int z) {
        KnownBlockedEdges knownBlockedEdgesx = this.getKnownBlockedEdges(x, y, z);
        if (knownBlockedEdgesx == null) {
            knownBlockedEdgesx = this.createKnownBlockedEdges(x, y, z);
        }

        return knownBlockedEdgesx;
    }

    private void releaseIfEmpty(KnownBlockedEdges knownBlockedEdgesx) {
        if (!knownBlockedEdgesx.n && !knownBlockedEdgesx.w) {
            this.knownBlockedEdges.remove(knownBlockedEdgesx);
            knownBlockedEdgesx.release();
        }
    }

    public void setKnownBlockedEdgeW(int x, int y, int z, boolean blocked) {
        KnownBlockedEdges knownBlockedEdgesx = this.getOrCreateKnownBlockedEdges(x, y, z);
        knownBlockedEdgesx.w = blocked;
        this.releaseIfEmpty(knownBlockedEdgesx);
    }

    public void setKnownBlockedEdgeN(int x, int y, int z, boolean blocked) {
        KnownBlockedEdges knownBlockedEdgesx = this.getOrCreateKnownBlockedEdges(x, y, z);
        knownBlockedEdgesx.n = blocked;
        this.releaseIfEmpty(knownBlockedEdgesx);
    }

    public void setKnownBlockedDoor(IsoDoor object, boolean blocked) {
        IsoGridSquare square = object.getSquare();
        if (object.getNorth()) {
            this.setKnownBlockedEdgeN(square.x, square.y, square.z, blocked);
        } else {
            this.setKnownBlockedEdgeW(square.x, square.y, square.z, blocked);
        }
    }

    public void setKnownBlockedDoor(IsoThumpable object, boolean blocked) {
        if (object.isDoor()) {
            IsoGridSquare square = object.getSquare();
            if (object.getNorth()) {
                this.setKnownBlockedEdgeN(square.x, square.y, square.z, blocked);
            } else {
                this.setKnownBlockedEdgeW(square.x, square.y, square.z, blocked);
            }
        }
    }

    public void setKnownBlockedWindow(IsoWindow object, boolean blocked) {
        IsoGridSquare square = object.getSquare();
        if (object.getNorth()) {
            this.setKnownBlockedEdgeN(square.x, square.y, square.z, blocked);
        } else {
            this.setKnownBlockedEdgeW(square.x, square.y, square.z, blocked);
        }
    }

    public void setKnownBlockedWindowFrame(IsoObject object, boolean blocked) {
        IsoGridSquare square = object.getSquare();
        if (IsoWindowFrame.isWindowFrame(object, true)) {
            this.setKnownBlockedEdgeN(square.x, square.y, square.z, blocked);
        } else if (IsoWindowFrame.isWindowFrame(object, false)) {
            this.setKnownBlockedEdgeW(square.x, square.y, square.z, blocked);
        }
    }

    public void forget() {
        KnownBlockedEdges.releaseAll(this.knownBlockedEdges);
        this.knownBlockedEdges.clear();
    }
}
