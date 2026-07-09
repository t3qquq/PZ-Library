// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.debug.DebugLog;
import zombie.network.GameServer;

public class SteamWorkshop implements ISteamWorkshopCallback {
    public static final SteamWorkshop instance = new SteamWorkshop();
    private ArrayList<SteamWorkshopItem> stagedItems = new ArrayList<>();
    private ArrayList<ISteamWorkshopCallback> callbacks = new ArrayList<>();

    public static void init() {
        if (SteamUtils.isSteamModeEnabled()) {
            instance.n_Init();
            ZomboidFileSystem.instance.resetModFolders();
        }

        if (!GameServer.bServer) {
            instance.initWorkshopFolder();
        }
    }

    public static void shutdown() {
        if (SteamUtils.isSteamModeEnabled()) {
            instance.n_Shutdown();
        }
    }

    private void copyFile(File file0, File file1) {
        try (
            FileInputStream fileInputStream = new FileInputStream(file0);
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
        ) {
            fileOutputStream.getChannel().transferFrom(fileInputStream.getChannel(), 0L, file0.length());
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    private void copyFileOrFolder(File file0, File file1) {
        if (file0.isDirectory()) {
            if (!file1.mkdirs()) {
                return;
            }

            String[] strings = file0.list();

            for (int int0 = 0; int0 < strings.length; int0++) {
                this.copyFileOrFolder(new File(file0, strings[int0]), new File(file1, strings[int0]));
            }
        } else {
            this.copyFile(file0, file1);
        }
    }

    private void initWorkshopFolder() {
        File file0 = new File(this.getWorkshopFolder());
        if (file0.exists() || file0.mkdirs()) {
            File file1 = new File("Workshop" + File.separator + "ModTemplate");
            File file2 = new File(this.getWorkshopFolder() + File.separator + "ModTemplate");
            if (file1.exists() && !file2.exists()) {
                this.copyFileOrFolder(file1, file2);
            }
        }
    }

    public ArrayList<SteamWorkshopItem> loadStagedItems() {
        this.stagedItems.clear();

        for (String string : this.getStageFolders()) {
            SteamWorkshopItem steamWorkshopItem = new SteamWorkshopItem(string);
            steamWorkshopItem.readWorkshopTxt();
            this.stagedItems.add(steamWorkshopItem);
        }

        return this.stagedItems;
    }

    public String getWorkshopFolder() {
        return ZomboidFileSystem.instance.getCacheDir() + File.separator + "Workshop";
    }

    public ArrayList<String> getStageFolders() {
        ArrayList arrayList = new ArrayList();
        Path path0 = FileSystems.getDefault().getPath(this.getWorkshopFolder());

        try {
            if (!Files.isDirectory(path0)) {
                Files.createDirectories(path0);
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
            return arrayList;
        }

        Filter filter = new Filter<Path>() {
            public boolean accept(Path path) throws IOException {
                return Files.isDirectory(path);
            }
        };

        try (DirectoryStream directoryStream = Files.newDirectoryStream(path0, filter)) {
            for (Path path1 : directoryStream) {
                String string = path1.toAbsolutePath().toString();
                arrayList.add(string);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return arrayList;
    }

    public boolean CreateWorkshopItem(SteamWorkshopItem steamWorkshopItem) {
        if (steamWorkshopItem.getID() != null) {
            throw new RuntimeException("can't recreate an existing item");
        } else {
            return this.n_CreateItem();
        }
    }

    public boolean SubmitWorkshopItem(SteamWorkshopItem steamWorkshopItem) {
        if (steamWorkshopItem.getID() != null && SteamUtils.isValidSteamID(steamWorkshopItem.getID())) {
            long long0 = SteamUtils.convertStringToSteamID(steamWorkshopItem.getID());
            if (!this.n_StartItemUpdate(long0)) {
                return false;
            } else if (!this.n_SetItemTitle(steamWorkshopItem.getTitle())) {
                return false;
            } else if (!this.n_SetItemDescription(steamWorkshopItem.getSubmitDescription())) {
                return false;
            } else {
                int int0 = steamWorkshopItem.getVisibilityInteger();
                if ("Mod Template".equals(steamWorkshopItem.getTitle())) {
                    int0 = 2;
                }

                if (!this.n_SetItemVisibility(int0)) {
                    return false;
                } else {
                    if (!this.n_SetItemTags(steamWorkshopItem.getSubmitTags())) {
                    }

                    if (!this.n_SetItemContent(steamWorkshopItem.getContentFolder())) {
                        return false;
                    } else {
                        return !this.n_SetItemPreview(steamWorkshopItem.getPreviewImage()) ? false : this.n_SubmitItemUpdate(steamWorkshopItem.getChangeNote());
                    }
                }
            }
        } else {
            throw new RuntimeException("workshop ID is required");
        }
    }

    public boolean GetItemUpdateProgress(long[] longs) {
        return this.n_GetItemUpdateProgress(longs);
    }

    public String[] GetInstalledItemFolders() {
        return GameServer.bServer ? GameServer.WorkshopInstallFolders : this.n_GetInstalledItemFolders();
    }

    public long GetItemState(long long0) {
        return this.n_GetItemState(long0);
    }

    public String GetItemInstallFolder(long long0) {
        return this.n_GetItemInstallFolder(long0);
    }

    public long GetItemInstallTimeStamp(long long0) {
        return this.n_GetItemInstallTimeStamp(long0);
    }

    public boolean SubscribeItem(long long0, ISteamWorkshopCallback iSteamWorkshopCallback) {
        if (!this.callbacks.contains(iSteamWorkshopCallback)) {
            this.callbacks.add(iSteamWorkshopCallback);
        }

        return this.n_SubscribeItem(long0);
    }

    public boolean DownloadItem(long long0, boolean boolean0, ISteamWorkshopCallback iSteamWorkshopCallback) {
        if (!this.callbacks.contains(iSteamWorkshopCallback)) {
            this.callbacks.add(iSteamWorkshopCallback);
        }

        return this.n_DownloadItem(long0, boolean0);
    }

    public boolean GetItemDownloadInfo(long long0, long[] longs) {
        return this.n_GetItemDownloadInfo(long0, longs);
    }

    public long CreateQueryUGCDetailsRequest(long[] longs, ISteamWorkshopCallback iSteamWorkshopCallback) {
        if (!this.callbacks.contains(iSteamWorkshopCallback)) {
            this.callbacks.add(iSteamWorkshopCallback);
        }

        return this.n_CreateQueryUGCDetailsRequest(longs);
    }

    public SteamUGCDetails GetQueryUGCResult(long long0, int int0) {
        return this.n_GetQueryUGCResult(long0, int0);
    }

    public long[] GetQueryUGCChildren(long long0, int int0) {
        return this.n_GetQueryUGCChildren(long0, int0);
    }

    public boolean ReleaseQueryUGCRequest(long long0) {
        return this.n_ReleaseQueryUGCRequest(long0);
    }

    public void RemoveCallback(ISteamWorkshopCallback iSteamWorkshopCallback) {
        this.callbacks.remove(iSteamWorkshopCallback);
    }

    public String getIDFromItemInstallFolder(String string0) {
        if (string0 != null && string0.replace("\\", "/").contains("/workshop/content/108600/")) {
            File file = new File(string0);
            String string1 = file.getName();
            if (SteamUtils.isValidSteamID(string1)) {
                return string1;
            }

            DebugLog.log("ERROR: " + string1 + " isn't a valid workshop item ID");
        }

        return null;
    }

    private native void n_Init();

    private native void n_Shutdown();

    private native boolean n_CreateItem();

    private native boolean n_StartItemUpdate(long var1);

    private native boolean n_SetItemTitle(String var1);

    private native boolean n_SetItemDescription(String var1);

    private native boolean n_SetItemVisibility(int var1);

    private native boolean n_SetItemTags(String[] var1);

    private native boolean n_SetItemContent(String var1);

    private native boolean n_SetItemPreview(String var1);

    private native boolean n_SubmitItemUpdate(String var1);

    private native boolean n_GetItemUpdateProgress(long[] var1);

    private native String[] n_GetInstalledItemFolders();

    private native long n_GetItemState(long var1);

    private native boolean n_SubscribeItem(long var1);

    private native boolean n_DownloadItem(long var1, boolean var3);

    private native String n_GetItemInstallFolder(long var1);

    private native long n_GetItemInstallTimeStamp(long var1);

    private native boolean n_GetItemDownloadInfo(long var1, long[] var3);

    private native long n_CreateQueryUGCDetailsRequest(long[] var1);

    private native SteamUGCDetails n_GetQueryUGCResult(long var1, int var3);

    private native long[] n_GetQueryUGCChildren(long var1, int var3);

    private native boolean n_ReleaseQueryUGCRequest(long var1);

    @Override
    public void onItemCreated(long long0, boolean boolean0) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemCreated", SteamUtils.convertSteamIDToString(long0), boolean0);
    }

    @Override
    public void onItemNotCreated(int int0) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemNotCreated", int0);
    }

    @Override
    public void onItemUpdated(boolean boolean0) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemUpdated", boolean0);
    }

    @Override
    public void onItemNotUpdated(int int0) {
        LuaEventManager.triggerEvent("OnSteamWorkshopItemNotUpdated", int0);
    }

    @Override
    public void onItemSubscribed(long long0) {
        for (int int0 = 0; int0 < this.callbacks.size(); int0++) {
            this.callbacks.get(int0).onItemSubscribed(long0);
        }
    }

    @Override
    public void onItemNotSubscribed(long long0, int int1) {
        for (int int0 = 0; int0 < this.callbacks.size(); int0++) {
            this.callbacks.get(int0).onItemNotSubscribed(long0, int1);
        }
    }

    @Override
    public void onItemDownloaded(long long0) {
        for (int int0 = 0; int0 < this.callbacks.size(); int0++) {
            this.callbacks.get(int0).onItemDownloaded(long0);
        }
    }

    @Override
    public void onItemNotDownloaded(long long0, int int1) {
        for (int int0 = 0; int0 < this.callbacks.size(); int0++) {
            this.callbacks.get(int0).onItemNotDownloaded(long0, int1);
        }
    }

    @Override
    public void onItemQueryCompleted(long long0, int int1) {
        for (int int0 = 0; int0 < this.callbacks.size(); int0++) {
            this.callbacks.get(int0).onItemQueryCompleted(long0, int1);
        }
    }

    @Override
    public void onItemQueryNotCompleted(long long0, int int1) {
        for (int int0 = 0; int0 < this.callbacks.size(); int0++) {
            this.callbacks.get(int0).onItemQueryNotCompleted(long0, int1);
        }
    }
}
