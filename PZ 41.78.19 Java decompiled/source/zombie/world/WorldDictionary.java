// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.world;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zombie.GameWindow;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionRegions;
import zombie.erosion.categories.ErosionCategory;
import zombie.gameStates.ChooseGameInfo;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.objects.Item;
import zombie.world.logger.Log;
import zombie.world.logger.WorldDictionaryLogger;

public class WorldDictionary {
    public static final String SAVE_FILE_READABLE = "WorldDictionaryReadable.lua";
    public static final String SAVE_FILE_LOG = "WorldDictionaryLog.lua";
    public static final String SAVE_FILE = "WorldDictionary";
    public static final String SAVE_EXT = ".bin";
    public static final boolean logUnset = false;
    public static final boolean logMissingObjectID = false;
    private static final Map<String, ItemInfo> itemLoadList = new HashMap<>();
    private static final List<String> objNameLoadList = new ArrayList<>();
    private static DictionaryData data;
    private static boolean isNewGame = true;
    private static boolean allowScriptItemLoading = false;
    private static final String netValidator = "DICTIONARY_PACKET_END";
    private static byte[] clientRemoteData;

    protected static void log(String string) {
        log(string, true);
    }

    protected static void log(String string, boolean boolean0) {
        if (boolean0) {
            DebugLog.log("WorldDictionary: " + string);
        }
    }

    public static void setIsNewGame(boolean boolean0) {
        isNewGame = boolean0;
    }

    public static boolean isIsNewGame() {
        return isNewGame;
    }

    public static void StartScriptLoading() {
        allowScriptItemLoading = true;
        itemLoadList.clear();
    }

    public static void ScriptsLoaded() {
        allowScriptItemLoading = false;
    }

    public static void onLoadItem(Item item) {
        if (!GameClient.bClient) {
            if (!allowScriptItemLoading) {
                log("Warning script item loaded after WorldDictionary is initialised");
                if (Core.bDebug) {
                    throw new RuntimeException("This shouldn't be happening.");
                }
            }

            ItemInfo itemInfo = itemLoadList.get(item.getFullName());
            if (itemInfo == null) {
                itemInfo = new ItemInfo();
                itemInfo.itemName = item.getName();
                itemInfo.moduleName = item.getModuleName();
                itemInfo.fullType = item.getFullName();
                itemLoadList.put(item.getFullName(), itemInfo);
            }

            if (itemInfo.modID != null && !item.getModID().equals(itemInfo.modID)) {
                if (itemInfo.modOverrides == null) {
                    itemInfo.modOverrides = new ArrayList<>();
                }

                if (!itemInfo.modOverrides.contains(itemInfo.modID)) {
                    itemInfo.modOverrides.add(itemInfo.modID);
                } else {
                    log("modOverrides for item '" + itemInfo.fullType + "' already contains mod id: " + itemInfo.modID);
                }
            }

            itemInfo.modID = item.getModID();
            if (itemInfo.modID.equals("pz-vanilla")) {
                itemInfo.existsAsVanilla = true;
            }

            itemInfo.isModded = !itemInfo.modID.equals("pz-vanilla");
            itemInfo.obsolete = item.getObsolete();
            itemInfo.scriptItem = item;
        }
    }

    private static void collectObjectNames() {
        objNameLoadList.clear();
        if (!GameClient.bClient) {
            ArrayList arrayList = new ArrayList();

            for (int int0 = 0; int0 < ErosionRegions.regions.size(); int0++) {
                for (int int1 = 0; int1 < ErosionRegions.regions.get(int0).categories.size(); int1++) {
                    ErosionCategory erosionCategory = ErosionRegions.regions.get(int0).categories.get(int1);
                    arrayList.clear();
                    erosionCategory.getObjectNames(arrayList);

                    for (String string : arrayList) {
                        if (!objNameLoadList.contains(string)) {
                            objNameLoadList.add(string);
                        }
                    }
                }
            }
        }
    }

    public static void loadDataFromServer(ByteBuffer byteBuffer) throws IOException {
        if (GameClient.bClient) {
            int int0 = byteBuffer.getInt();
            clientRemoteData = new byte[int0];
            byteBuffer.get(clientRemoteData, 0, clientRemoteData.length);
        }
    }

    public static void saveDataForClient(ByteBuffer byteBuffer) throws IOException {
        if (GameServer.bServer) {
            int int0 = byteBuffer.position();
            byteBuffer.putInt(0);
            int int1 = byteBuffer.position();
            if (data.serverDataCache != null) {
                byteBuffer.put(data.serverDataCache);
            } else {
                if (Core.bDebug) {
                    throw new RuntimeException("Should be sending data from the serverDataCache here.");
                }

                data.saveToByteBuffer(byteBuffer);
            }

            GameWindow.WriteString(byteBuffer, "DICTIONARY_PACKET_END");
            int int2 = byteBuffer.position();
            byteBuffer.position(int0);
            byteBuffer.putInt(int2 - int1);
            byteBuffer.position(int2);
        }
    }

    public static void init() throws WorldDictionaryException {
        boolean boolean0 = true;
        collectObjectNames();
        WorldDictionaryLogger.startLogging();
        WorldDictionaryLogger.log("-------------------------------------------------------", false);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        WorldDictionaryLogger.log("Time: " + simpleDateFormat.format(new Date()), false);
        log("Checking dictionary...");
        Log.Info info = null;

        try {
            if (!GameClient.bClient) {
                if (data == null || data.isClient()) {
                    data = new DictionaryData();
                }
            } else if (data == null || !data.isClient()) {
                data = new DictionaryDataClient();
            }

            data.reset();
            if (GameClient.bClient) {
                if (clientRemoteData == null) {
                    throw new WorldDictionaryException("WorldDictionary data not received from server.");
                }

                ByteBuffer byteBuffer = ByteBuffer.wrap(clientRemoteData);
                data.loadFromByteBuffer(byteBuffer);
                String string = GameWindow.ReadString(byteBuffer);
                if (!string.equals("DICTIONARY_PACKET_END")) {
                    throw new WorldDictionaryException("WorldDictionary data received from server is corrupt.");
                }

                clientRemoteData = null;
            }

            data.backupCurrentDataSet();
            data.load();
            ArrayList arrayList = new ArrayList();
            info = new Log.Info(simpleDateFormat.format(new Date()), Core.GameSaveWorld, 195, arrayList);
            WorldDictionaryLogger.log(info);
            data.parseItemLoadList(itemLoadList);
            data.parseCurrentItemSet();
            itemLoadList.clear();
            data.parseObjectNameLoadList(objNameLoadList);
            objNameLoadList.clear();
            data.getItemMods(arrayList);
            data.saveAsText("WorldDictionaryReadable.lua");
            data.save();
            data.deleteBackupCurrentDataSet();
        } catch (Exception exception0) {
            boolean0 = false;
            exception0.printStackTrace();
            log("Warning: error occurred loading dictionary!");
            if (info != null) {
                info.HasErrored = true;
            }

            if (data != null) {
                data.createErrorBackups();
            }
        }

        try {
            WorldDictionaryLogger.saveLog("WorldDictionaryLog.lua");
            WorldDictionaryLogger.reset();
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }

        if (!boolean0) {
            throw new WorldDictionaryException("WorldDictionary: Cannot load world due to WorldDictionary error.");
        }
    }

    public static void onWorldLoaded() {
    }

    public static ItemInfo getItemInfoFromType(String string) {
        return data.getItemInfoFromType(string);
    }

    public static ItemInfo getItemInfoFromID(short short0) {
        return data.getItemInfoFromID(short0);
    }

    public static short getItemRegistryID(String string) {
        return data.getItemRegistryID(string);
    }

    public static String getItemTypeFromID(short short0) {
        return data.getItemTypeFromID(short0);
    }

    public static String getItemTypeDebugString(short short0) {
        return data.getItemTypeDebugString(short0);
    }

    public static String getSpriteNameFromID(int int0) {
        return data.getSpriteNameFromID(int0);
    }

    public static int getIdForSpriteName(String string) {
        return data.getIdForSpriteName(string);
    }

    public static String getObjectNameFromID(byte byte0) {
        return data.getObjectNameFromID(byte0);
    }

    public static byte getIdForObjectName(String string) {
        return data.getIdForObjectName(string);
    }

    public static String getItemModID(short short0) {
        ItemInfo itemInfo = getItemInfoFromID(short0);
        return itemInfo != null ? itemInfo.modID : null;
    }

    public static String getItemModID(String string) {
        ItemInfo itemInfo = getItemInfoFromType(string);
        return itemInfo != null ? itemInfo.modID : null;
    }

    public static String getModNameFromID(String string) {
        if (string != null) {
            if (string.equals("pz-vanilla")) {
                return "Project Zomboid";
            }

            ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string);
            if (mod != null && mod.getName() != null) {
                return mod.getName();
            }
        }

        return "Unknown mod";
    }

    public static void DebugPrintItem(InventoryItem item1) {
        Item item0 = item1.getScriptItem();
        if (item0 != null) {
            DebugPrintItem(item0);
        } else {
            String string = item1.getFullType();
            ItemInfo itemInfo = null;
            if (string != null) {
                itemInfo = getItemInfoFromType(string);
            }

            if (itemInfo == null && item1.getRegistry_id() >= 0) {
                itemInfo = getItemInfoFromID(item1.getRegistry_id());
            }

            if (itemInfo != null) {
                itemInfo.DebugPrint();
            } else {
                DebugLog.log("WorldDictionary: Cannot debug print item: " + (string != null ? string : "unknown"));
            }
        }
    }

    public static void DebugPrintItem(Item item) {
        String string = item.getFullName();
        ItemInfo itemInfo = null;
        if (string != null) {
            itemInfo = getItemInfoFromType(string);
        }

        if (itemInfo == null && item.getRegistry_id() >= 0) {
            itemInfo = getItemInfoFromID(item.getRegistry_id());
        }

        if (itemInfo != null) {
            itemInfo.DebugPrint();
        } else {
            DebugLog.log("WorldDictionary: Cannot debug print item: " + (string != null ? string : "unknown"));
        }
    }

    public static void DebugPrintItem(String string) {
        ItemInfo itemInfo = getItemInfoFromType(string);
        if (itemInfo != null) {
            itemInfo.DebugPrint();
        } else {
            DebugLog.log("WorldDictionary: Cannot debug print item: " + string);
        }
    }

    public static void DebugPrintItem(short short0) {
        ItemInfo itemInfo = getItemInfoFromID(short0);
        if (itemInfo != null) {
            itemInfo.DebugPrint();
        } else {
            DebugLog.log("WorldDictionary: Cannot debug print item id: " + short0);
        }
    }
}
