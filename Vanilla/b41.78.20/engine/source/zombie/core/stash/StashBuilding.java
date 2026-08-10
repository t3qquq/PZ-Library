// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.stash;

public final class StashBuilding {
    public int buildingX;
    public int buildingY;
    public String stashName;

    public StashBuilding(String _stashName, int _buildingX, int _buildingY) {
        this.stashName = _stashName;
        this.buildingX = _buildingX;
        this.buildingY = _buildingY;
    }

    public String getName() {
        return this.stashName;
    }
}
