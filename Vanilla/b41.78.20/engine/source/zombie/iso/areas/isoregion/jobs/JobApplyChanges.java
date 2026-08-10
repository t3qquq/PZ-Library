// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.jobs;

public class JobApplyChanges extends RegionJob {
    protected boolean saveToDisk;

    protected JobApplyChanges() {
        super(RegionJobType.ApplyChanges);
    }

    @Override
    protected void reset() {
        this.saveToDisk = false;
    }

    public boolean isSaveToDisk() {
        return this.saveToDisk;
    }
}
