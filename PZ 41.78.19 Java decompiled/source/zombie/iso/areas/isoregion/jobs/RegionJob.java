// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.jobs;

public abstract class RegionJob {
    private final RegionJobType type;

    protected RegionJob(RegionJobType regionJobType) {
        this.type = regionJobType;
    }

    protected void reset() {
    }

    public RegionJobType getJobType() {
        return this.type;
    }
}
