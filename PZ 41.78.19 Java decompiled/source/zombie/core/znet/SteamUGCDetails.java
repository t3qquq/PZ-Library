// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import zombie.debug.DebugLog;

public class SteamUGCDetails {
    private long ID;
    private String title;
    private long timeCreated;
    private long timeUpdated;
    private int fileSize;
    private long[] childIDs;

    public SteamUGCDetails(long long0, String string, long long1, long long2, int int0, long[] longs) {
        this.ID = long0;
        this.title = string;
        this.timeCreated = long1;
        this.timeUpdated = long2;
        this.fileSize = int0;
        this.childIDs = longs;
    }

    public long getID() {
        return this.ID;
    }

    public String getIDString() {
        return SteamUtils.convertSteamIDToString(this.ID);
    }

    public String getTitle() {
        return this.title;
    }

    public long getTimeCreated() {
        return this.timeCreated;
    }

    public long getTimeUpdated() {
        return this.timeUpdated;
    }

    public int getFileSize() {
        return this.fileSize;
    }

    public long[] getChildren() {
        return this.childIDs;
    }

    public int getNumChildren() {
        return this.childIDs == null ? 0 : this.childIDs.length;
    }

    public long getChildID(int index) {
        if (index >= 0 && index < this.getNumChildren()) {
            return this.childIDs[index];
        } else {
            throw new IndexOutOfBoundsException("invalid child index");
        }
    }

    public String getState() {
        long long0 = SteamWorkshop.instance.GetItemState(this.ID);
        if (!SteamWorkshopItem.ItemState.Subscribed.and(long0)) {
            return "NotSubscribed";
        } else if (SteamWorkshopItem.ItemState.DownloadPending.and(long0)) {
            DebugLog.log(SteamWorkshopItem.ItemState.toString(long0) + " ID=" + this.ID);
            return "Downloading";
        } else if (SteamWorkshopItem.ItemState.NeedsUpdate.and(long0)) {
            return "NeedsUpdate";
        } else {
            return SteamWorkshopItem.ItemState.Installed.and(long0) ? "Installed" : "Error";
        }
    }
}
