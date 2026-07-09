// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import zombie.core.logger.ExceptionLogger;
import zombie.core.znet.ISteamWorkshopCallback;
import zombie.core.znet.SteamUGCDetails;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.core.znet.SteamWorkshopItem;
import zombie.debug.DebugLog;

public class GameServerWorkshopItems {
    private static void noise(String string) {
        DebugLog.log("Workshop: " + string);
    }

    public static boolean Install(ArrayList<Long> arrayList0) {
        if (!GameServer.bServer) {
            return false;
        } else if (arrayList0.isEmpty()) {
            return true;
        } else {
            ArrayList arrayList1 = new ArrayList();

            for (long long0 : arrayList0) {
                GameServerWorkshopItems.WorkshopItem workshopItem0 = new GameServerWorkshopItems.WorkshopItem(long0);
                arrayList1.add(workshopItem0);
            }

            if (!QueryItemDetails(arrayList1)) {
                return false;
            } else {
                while (true) {
                    SteamUtils.runLoop();
                    boolean boolean0 = false;

                    for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                        GameServerWorkshopItems.WorkshopItem workshopItem1 = (GameServerWorkshopItems.WorkshopItem)arrayList1.get(int0);
                        workshopItem1.update();
                        if (workshopItem1.state == GameServerWorkshopItems.WorkshopInstallState.Fail) {
                            return false;
                        }

                        if (workshopItem1.state != GameServerWorkshopItems.WorkshopInstallState.Ready) {
                            boolean0 = true;
                            break;
                        }
                    }

                    if (!boolean0) {
                        GameServer.WorkshopInstallFolders = new String[arrayList0.size()];
                        GameServer.WorkshopTimeStamps = new long[arrayList0.size()];

                        for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                            long long1 = (Long)arrayList0.get(int1);
                            String string = SteamWorkshop.instance.GetItemInstallFolder(long1);
                            if (string == null) {
                                noise("GetItemInstallFolder() failed ID=" + long1);
                                return false;
                            }

                            noise(long1 + " installed to " + string);
                            GameServer.WorkshopInstallFolders[int1] = string;
                            GameServer.WorkshopTimeStamps[int1] = SteamWorkshop.instance.GetItemInstallTimeStamp(long1);
                        }

                        return true;
                    }

                    try {
                        Thread.sleep(33L);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    private static boolean QueryItemDetails(ArrayList<GameServerWorkshopItems.WorkshopItem> arrayList) {
        long[] longs = new long[arrayList.size()];

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            GameServerWorkshopItems.WorkshopItem workshopItem0 = (GameServerWorkshopItems.WorkshopItem)arrayList.get(int0);
            longs[int0] = workshopItem0.ID;
        }

        GameServerWorkshopItems.ItemQuery itemQuery = new GameServerWorkshopItems.ItemQuery();
        itemQuery.handle = SteamWorkshop.instance.CreateQueryUGCDetailsRequest(longs, itemQuery);
        if (itemQuery.handle == 0L) {
            return false;
        } else {
            while (true) {
                SteamUtils.runLoop();
                if (itemQuery.isCompleted()) {
                    for (SteamUGCDetails steamUGCDetails : itemQuery.details) {
                        for (GameServerWorkshopItems.WorkshopItem workshopItem1 : arrayList) {
                            if (workshopItem1.ID == steamUGCDetails.getID()) {
                                workshopItem1.details = steamUGCDetails;
                                break;
                            }
                        }
                    }

                    return true;
                }

                if (itemQuery.isNotCompleted()) {
                    return false;
                }

                try {
                    Thread.sleep(33L);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private static final class ItemQuery implements ISteamWorkshopCallback {
        long handle;
        ArrayList<SteamUGCDetails> details;
        boolean bCompleted;
        boolean bNotCompleted;

        public boolean isCompleted() {
            return this.bCompleted;
        }

        public boolean isNotCompleted() {
            return this.bNotCompleted;
        }

        @Override
        public void onItemCreated(long var1, boolean var3) {
        }

        @Override
        public void onItemNotCreated(int var1) {
        }

        @Override
        public void onItemUpdated(boolean var1) {
        }

        @Override
        public void onItemNotUpdated(int var1) {
        }

        @Override
        public void onItemSubscribed(long var1) {
        }

        @Override
        public void onItemNotSubscribed(long var1, int var3) {
        }

        @Override
        public void onItemDownloaded(long var1) {
        }

        @Override
        public void onItemNotDownloaded(long var1, int var3) {
        }

        @Override
        public void onItemQueryCompleted(long long0, int int0) {
            GameServerWorkshopItems.noise("onItemQueryCompleted handle=" + long0 + " numResult=" + int0);
            if (long0 == this.handle) {
                SteamWorkshop.instance.RemoveCallback(this);
                ArrayList arrayList = new ArrayList();

                for (int int1 = 0; int1 < int0; int1++) {
                    SteamUGCDetails steamUGCDetails = SteamWorkshop.instance.GetQueryUGCResult(long0, int1);
                    if (steamUGCDetails != null) {
                        arrayList.add(steamUGCDetails);
                    }
                }

                this.details = arrayList;
                SteamWorkshop.instance.ReleaseQueryUGCRequest(long0);
                this.bCompleted = true;
            }
        }

        @Override
        public void onItemQueryNotCompleted(long long0, int int0) {
            GameServerWorkshopItems.noise("onItemQueryNotCompleted handle=" + long0 + " result=" + int0);
            if (long0 == this.handle) {
                SteamWorkshop.instance.RemoveCallback(this);
                SteamWorkshop.instance.ReleaseQueryUGCRequest(long0);
                this.bNotCompleted = true;
            }
        }
    }

    private static enum WorkshopInstallState {
        CheckItemState,
        DownloadPending,
        Ready,
        Fail;
    }

    private static class WorkshopItem implements ISteamWorkshopCallback {
        long ID;
        GameServerWorkshopItems.WorkshopInstallState state = GameServerWorkshopItems.WorkshopInstallState.CheckItemState;
        long downloadStartTime;
        long downloadQueryTime;
        String error;
        SteamUGCDetails details;

        WorkshopItem(long long0) {
            this.ID = long0;
        }

        void update() {
            switch (this.state) {
                case CheckItemState:
                    this.CheckItemState();
                    break;
                case DownloadPending:
                    this.DownloadPending();
                case Ready:
            }
        }

        void setState(GameServerWorkshopItems.WorkshopInstallState workshopInstallState) {
            GameServerWorkshopItems.noise("item state " + this.state + " -> " + workshopInstallState + " ID=" + this.ID);
            this.state = workshopInstallState;
        }

        void CheckItemState() {
            long long0 = SteamWorkshop.instance.GetItemState(this.ID);
            GameServerWorkshopItems.noise("GetItemState()=" + SteamWorkshopItem.ItemState.toString(long0) + " ID=" + this.ID);
            if (SteamWorkshopItem.ItemState.Installed.and(long0)
                && this.details != null
                && this.details.getTimeCreated() != 0L
                && this.details.getTimeUpdated() != SteamWorkshop.instance.GetItemInstallTimeStamp(this.ID)) {
                GameServerWorkshopItems.noise("Installed status but timeUpdated doesn't match!!!");
                this.RemoveFolderForReinstall();
                long0 |= SteamWorkshopItem.ItemState.NeedsUpdate.getValue();
            }

            if (long0 != SteamWorkshopItem.ItemState.None.getValue() && !SteamWorkshopItem.ItemState.NeedsUpdate.and(long0)) {
                if (SteamWorkshopItem.ItemState.Installed.and(long0)) {
                    this.setState(GameServerWorkshopItems.WorkshopInstallState.Ready);
                } else {
                    this.error = "UnknownItemState";
                    this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
                }
            } else if (SteamWorkshop.instance.DownloadItem(this.ID, true, this)) {
                this.setState(GameServerWorkshopItems.WorkshopInstallState.DownloadPending);
                this.downloadStartTime = System.currentTimeMillis();
            } else {
                this.error = "DownloadItemFalse";
                this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
            }
        }

        void RemoveFolderForReinstall() {
            String string = SteamWorkshop.instance.GetItemInstallFolder(this.ID);
            if (string == null) {
                GameServerWorkshopItems.noise("not removing install folder because GetItemInstallFolder() failed ID=" + this.ID);
            } else {
                Path path = Paths.get(string);
                if (!Files.exists(path)) {
                    GameServerWorkshopItems.noise("not removing install folder because it does not exist : \"" + string + "\"");
                } else {
                    try {
                        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                            public FileVisitResult visitFile(Path path, BasicFileAttributes var2) throws IOException {
                                Files.delete(path);
                                return FileVisitResult.CONTINUE;
                            }

                            public FileVisitResult postVisitDirectory(Path path, IOException var2) throws IOException {
                                Files.delete(path);
                                return FileVisitResult.CONTINUE;
                            }
                        });
                    } catch (Exception exception) {
                        ExceptionLogger.logException(exception);
                    }
                }
            }
        }

        void DownloadPending() {
            long long0 = System.currentTimeMillis();
            if (this.downloadQueryTime + 100L <= long0) {
                this.downloadQueryTime = long0;
                long long1 = SteamWorkshop.instance.GetItemState(this.ID);
                GameServerWorkshopItems.noise("DownloadPending GetItemState()=" + SteamWorkshopItem.ItemState.toString(long1) + " ID=" + this.ID);
                if (SteamWorkshopItem.ItemState.NeedsUpdate.and(long1)) {
                    long[] longs = new long[2];
                    if (SteamWorkshop.instance.GetItemDownloadInfo(this.ID, longs)) {
                        GameServerWorkshopItems.noise("download " + longs[0] + "/" + longs[1] + " ID=" + this.ID);
                    }
                }
            }
        }

        @Override
        public void onItemCreated(long var1, boolean var3) {
        }

        @Override
        public void onItemNotCreated(int var1) {
        }

        @Override
        public void onItemUpdated(boolean var1) {
        }

        @Override
        public void onItemNotUpdated(int var1) {
        }

        @Override
        public void onItemSubscribed(long long0) {
            GameServerWorkshopItems.noise("onItemSubscribed itemID=" + long0);
        }

        @Override
        public void onItemNotSubscribed(long long0, int int0) {
            GameServerWorkshopItems.noise("onItemNotSubscribed itemID=" + long0 + " result=" + int0);
        }

        @Override
        public void onItemDownloaded(long long0) {
            GameServerWorkshopItems.noise("onItemDownloaded itemID=" + long0 + " time=" + (System.currentTimeMillis() - this.downloadStartTime) + " ms");
            if (long0 == this.ID) {
                SteamWorkshop.instance.RemoveCallback(this);
                this.setState(GameServerWorkshopItems.WorkshopInstallState.CheckItemState);
            }
        }

        @Override
        public void onItemNotDownloaded(long long0, int int0) {
            GameServerWorkshopItems.noise("onItemNotDownloaded itemID=" + long0 + " result=" + int0);
            if (long0 == this.ID) {
                SteamWorkshop.instance.RemoveCallback(this);
                this.error = "ItemNotDownloaded";
                this.setState(GameServerWorkshopItems.WorkshopInstallState.Fail);
            }
        }

        @Override
        public void onItemQueryCompleted(long long0, int int0) {
            GameServerWorkshopItems.noise("onItemQueryCompleted handle=" + long0 + " numResult=" + int0);
        }

        @Override
        public void onItemQueryNotCompleted(long long0, int int0) {
            GameServerWorkshopItems.noise("onItemQueryNotCompleted handle=" + long0 + " result=" + int0);
        }
    }
}
