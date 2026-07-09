// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.jobs;

public class JobSquareUpdate extends RegionJob {
    protected int worldSquareX;
    protected int worldSquareY;
    protected int worldSquareZ;
    protected byte newSquareFlags;

    protected JobSquareUpdate() {
        super(RegionJobType.SquareUpdate);
    }

    public int getWorldSquareX() {
        return this.worldSquareX;
    }

    public int getWorldSquareY() {
        return this.worldSquareY;
    }

    public int getWorldSquareZ() {
        return this.worldSquareZ;
    }

    public byte getNewSquareFlags() {
        return this.newSquareFlags;
    }
}
