// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
