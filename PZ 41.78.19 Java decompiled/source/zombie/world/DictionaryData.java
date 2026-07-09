// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.world;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.StringConcatFactory;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.world.logger.Log;
import zombie.world.logger.WorldDictionaryLogger;

public class DictionaryData {
    protected final Map<Short, ItemInfo> itemIdToInfoMap = new HashMap<>();
    protected final Map<String, ItemInfo> itemTypeToInfoMap = new HashMap<>();
    protected final Map<String, Integer> spriteNameToIdMap = new HashMap<>();
    protected final Map<Integer, String> spriteIdToNameMap = new HashMap<>();
    protected final Map<String, Byte> objectNameToIdMap = new HashMap<>();
    protected final Map<Byte, String> objectIdToNameMap = new HashMap<>();
    protected final ArrayList<String> unsetObject = new ArrayList<>();
    protected final ArrayList<String> unsetSprites = new ArrayList<>();
    protected short NextItemID = 0;
    protected int NextSpriteNameID = 0;
    protected byte NextObjectNameID = 0;
    protected byte[] serverDataCache;
    private File dataBackupPath;

    protected boolean isClient() {
        return false;
    }

    protected void reset() {
        this.NextItemID = 0;
        this.NextSpriteNameID = 0;
        this.NextObjectNameID = 0;
        this.itemIdToInfoMap.clear();
        this.itemTypeToInfoMap.clear();
        this.objectIdToNameMap.clear();
        this.objectNameToIdMap.clear();
        this.spriteIdToNameMap.clear();
        this.spriteNameToIdMap.clear();
    }

    protected final ItemInfo getItemInfoFromType(String string) {
        return this.itemTypeToInfoMap.get(string);
    }

    protected final ItemInfo getItemInfoFromID(short short0) {
        return this.itemIdToInfoMap.get(short0);
    }

    protected final short getItemRegistryID(String string) {
        ItemInfo itemInfo = this.itemTypeToInfoMap.get(string);
        if (itemInfo != null) {
            return itemInfo.registryID;
        } else {
            if (Core.bDebug) {
                DebugLog.log("WARNING: Cannot get registry id for item: " + string);
            }

            return -1;
        }
    }

    protected final String getItemTypeFromID(short short0) {
        ItemInfo itemInfo = this.itemIdToInfoMap.get(short0);
        return itemInfo != null ? itemInfo.fullType : null;
    }

    protected final String getItemTypeDebugString(short short0) {
        String string = this.getItemTypeFromID(short0);
        if (string == null) {
            string = "Unknown";
        }

        return string;
    }

    protected final String getSpriteNameFromID(int int0) {
        if (int0 >= 0) {
            if (this.spriteIdToNameMap.containsKey(int0)) {
                return this.spriteIdToNameMap.get(int0);
            }

            IsoSprite sprite = IsoSprite.getSprite(IsoSpriteManager.instance, int0);
            if (sprite != null && sprite.name != null) {
                return sprite.name;
            }
        }

        DebugLog.log("WorldDictionary, Couldnt find sprite name for ID '" + int0 + "'.");
        return null;
    }

    protected final int getIdForSpriteName(String string) {
        if (string != null) {
            if (this.spriteNameToIdMap.containsKey(string)) {
                return this.spriteNameToIdMap.get(string);
            }

            IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
            if (sprite != null && sprite.ID >= 0 && sprite.ID != 20000000 && sprite.name.equals(string)) {
                return sprite.ID;
            }
        }

        return -1;
    }

    protected final String getObjectNameFromID(byte byte0) {
        if (byte0 >= 0) {
            if (this.objectIdToNameMap.containsKey(byte0)) {
                return this.objectIdToNameMap.get(byte0);
            }

            if (Core.bDebug) {
                DebugLog.log("WorldDictionary, Couldnt find object name for ID '" + byte0 + "'.");
            }
        }

        return null;
    }

    protected final byte getIdForObjectName(String string) {
        if (string != null) {
            if (this.objectNameToIdMap.containsKey(string)) {
                return this.objectNameToIdMap.get(string);
            }

            if (Core.bDebug) {
            }
        }

        return -1;
    }

    protected final void getItemMods(List<String> list0) {
        list0.clear();

        for (Entry entry : this.itemIdToInfoMap.entrySet()) {
            if (!list0.contains(((ItemInfo)entry.getValue()).modID)) {
                list0.add(((ItemInfo)entry.getValue()).modID);
            }

            if (((ItemInfo)entry.getValue()).modOverrides != null) {
                List list1 = ((ItemInfo)entry.getValue()).modOverrides;

                for (int int0 = 0; int0 < list1.size(); int0++) {
                    if (!list0.contains(list1.get(int0))) {
                        list0.add((String)list1.get(int0));
                    }
                }
            }
        }
    }

    protected final void getModuleList(List<String> list) {
        for (Entry entry : this.itemIdToInfoMap.entrySet()) {
            if (!list.contains(((ItemInfo)entry.getValue()).moduleName)) {
                list.add(((ItemInfo)entry.getValue()).moduleName);
            }
        }
    }

    protected void parseItemLoadList(Map<String, ItemInfo> map) throws WorldDictionaryException {
        for (Entry entry : map.entrySet()) {
            ItemInfo itemInfo0 = (ItemInfo)entry.getValue();
            ItemInfo itemInfo1 = this.itemTypeToInfoMap.get(itemInfo0.fullType);
            if (itemInfo1 == null) {
                if (!itemInfo0.obsolete) {
                    if (this.NextItemID >= 32767) {
                        throw new WorldDictionaryException("Max item ID value reached for WorldDictionary!");
                    }

                    itemInfo0.registryID = this.NextItemID++;
                    itemInfo0.isLoaded = true;
                    this.itemTypeToInfoMap.put(itemInfo0.fullType, itemInfo0);
                    this.itemIdToInfoMap.put(itemInfo0.registryID, itemInfo0);
                    WorldDictionaryLogger.log(new Log.RegisterItem(itemInfo0.copy()));
                }
            } else {
                if (itemInfo1.removed && !itemInfo0.obsolete) {
                    itemInfo1.removed = false;
                    WorldDictionaryLogger.log(new Log.ReinstateItem(itemInfo1.copy()));
                }

                if (!itemInfo1.modID.equals(itemInfo0.modID)) {
                    String string = itemInfo1.modID;
                    itemInfo1.modID = itemInfo0.modID;
                    itemInfo1.isModded = !itemInfo0.modID.equals("pz-vanilla");
                    WorldDictionaryLogger.log(new Log.ModIDChangedItem(itemInfo1.copy(), string, itemInfo1.modID));
                }

                if (itemInfo0.obsolete && (!itemInfo1.obsolete || !itemInfo1.removed)) {
                    itemInfo1.obsolete = true;
                    itemInfo1.removed = true;
                    WorldDictionaryLogger.log(new Log.ObsoleteItem(itemInfo1.copy()));
                }

                itemInfo1.isLoaded = true;
            }
        }
    }

    protected void parseCurrentItemSet() throws WorldDictionaryException {
        for (Entry entry : this.itemTypeToInfoMap.entrySet()) {
            ItemInfo itemInfo = (ItemInfo)entry.getValue();
            if (!itemInfo.isLoaded) {
                itemInfo.removed = true;
                WorldDictionaryLogger.log(new Log.RemovedItem(itemInfo.copy(), false));
            }

            if (itemInfo.scriptItem == null) {
                itemInfo.scriptItem = ScriptManager.instance.getSpecificItem(itemInfo.fullType);
            }

            if (itemInfo.scriptItem != null) {
                itemInfo.scriptItem.setRegistry_id(itemInfo.registryID);
            } else {
                itemInfo.removed = true;
                WorldDictionaryLogger.log(new Log.RemovedItem(itemInfo.copy(), true));
            }
        }
    }

    protected void parseObjectNameLoadList(List<String> list) throws WorldDictionaryException {
        for (int int0 = 0; int0 < list.size(); int0++) {
            String string = (String)list.get(int0);
            if (!this.objectNameToIdMap.containsKey(string)) {
                if (this.NextObjectNameID >= 127) {
                    WorldDictionaryLogger.log("Max value for object names reached.");
                    if (Core.bDebug) {
                        throw new WorldDictionaryException("Max value for object names reached.");
                    }
                } else {
                    byte byte0 = this.NextObjectNameID++;
                    this.objectIdToNameMap.put(byte0, string);
                    this.objectNameToIdMap.put(string, byte0);
                    WorldDictionaryLogger.log(new Log.RegisterObject(string, byte0));
                }
            }
        }
    }

    protected void backupCurrentDataSet() throws IOException {
        this.dataBackupPath = null;
        if (!Core.getInstance().isNoSave()) {
            File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionary.bin"));
            if (file.exists()) {
                long long0 = Instant.now().getEpochSecond();
                this.dataBackupPath = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionary_" + long0 + ".bak"));
                Files.copy(file, this.dataBackupPath);
            }
        }
    }

    protected void deleteBackupCurrentDataSet() throws IOException {
        if (Core.getInstance().isNoSave()) {
            this.dataBackupPath = null;
        } else {
            if (this.dataBackupPath != null) {
                this.dataBackupPath.delete();
            }

            this.dataBackupPath = null;
        }
    }

    protected void createErrorBackups() {
        if (!Core.getInstance().isNoSave()) {
            try {
                WorldDictionary.log("Attempting to copy WorldDictionary backups...");
                long long0 = Instant.now().getEpochSecond();
                String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("WD_ERROR_" + long0) + File.separator;
                WorldDictionary.log("path = " + string);
                File file0 = new File(string);
                boolean boolean0 = true;
                if (!file0.exists()) {
                    boolean0 = file0.mkdir();
                }

                if (!boolean0) {
                    WorldDictionary.log("Could not create backup folder folder.");
                    return;
                }

                if (this.dataBackupPath != null) {
                    File file1 = new File(string + "WorldDictionary_backup.bin");
                    if (this.dataBackupPath.exists()) {
                        Files.copy(this.dataBackupPath, file1);
                    }
                }

                File file2 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionaryLog.lua"));
                File file3 = new File(string + "WorldDictionaryLog.lua");
                if (file2.exists()) {
                    Files.copy(file2, file3);
                }

                File file4 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionaryReadable.lua"));
                File file5 = new File(string + "WorldDictionaryReadable.lua");
                if (file4.exists()) {
                    Files.copy(file4, file5);
                }

                File file6 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionary.bin"));
                File file7 = new File(string + "WorldDictionary.bin");
                if (file6.exists()) {
                    Files.copy(file6, file7);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    protected void load() throws IOException, WorldDictionaryException {
        if (!Core.getInstance().isNoSave()) {
            String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionary.bin");
            File file = new File(string);
            if (!file.exists()) {
                if (!WorldDictionary.isIsNewGame()) {
                    throw new WorldDictionaryException("WorldDictionary data file is missing from world folder.");
                }
            } else {
                try {
                    try (FileInputStream fileInputStream = new FileInputStream(file)) {
                        DebugLog.log("Loading WorldDictionary:" + string);
                        ByteBuffer byteBuffer = ByteBuffer.allocate((int)file.length());
                        byteBuffer.clear();
                        int int0 = fileInputStream.read(byteBuffer.array());
                        byteBuffer.limit(int0);
                        this.loadFromByteBuffer(byteBuffer);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw new WorldDictionaryException("Error loading WorldDictionary.", exception);
                }
            }
        }
    }

    protected void loadFromByteBuffer(ByteBuffer byteBuffer) throws IOException {
        this.NextItemID = byteBuffer.getShort();
        this.NextObjectNameID = byteBuffer.get();
        this.NextSpriteNameID = byteBuffer.getInt();
        ArrayList arrayList0 = new ArrayList();
        int int0 = byteBuffer.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            arrayList0.add(GameWindow.ReadString(byteBuffer));
        }

        ArrayList arrayList1 = new ArrayList();
        int int2 = byteBuffer.getInt();

        for (int int3 = 0; int3 < int2; int3++) {
            arrayList1.add(GameWindow.ReadString(byteBuffer));
        }

        int int4 = byteBuffer.getInt();

        for (int int5 = 0; int5 < int4; int5++) {
            ItemInfo itemInfo = new ItemInfo();
            itemInfo.load(byteBuffer, 195, arrayList0, arrayList1);
            this.itemIdToInfoMap.put(itemInfo.registryID, itemInfo);
            this.itemTypeToInfoMap.put(itemInfo.fullType, itemInfo);
        }

        int int6 = byteBuffer.getInt();

        for (int int7 = 0; int7 < int6; int7++) {
            byte byte0 = byteBuffer.get();
            String string0 = GameWindow.ReadString(byteBuffer);
            this.objectIdToNameMap.put(byte0, string0);
            this.objectNameToIdMap.put(string0, byte0);
        }

        int int8 = byteBuffer.getInt();

        for (int int9 = 0; int9 < int8; int9++) {
            int int10 = byteBuffer.getInt();
            String string1 = GameWindow.ReadString(byteBuffer);
            this.spriteIdToNameMap.put(int10, string1);
            this.spriteNameToIdMap.put(string1, int10);
        }
    }

    protected void save() throws IOException, WorldDictionaryException {
        if (!Core.getInstance().isNoSave()) {
            try {
                byte[] bytes = new byte[5242880];
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                this.saveToByteBuffer(byteBuffer);
                byteBuffer.flip();
                if (GameServer.bServer) {
                    bytes = new byte[byteBuffer.limit()];
                    byteBuffer.get(bytes, 0, bytes.length);
                    this.serverDataCache = bytes;
                }

                File file0 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionary.tmp"));
                FileOutputStream fileOutputStream = new FileOutputStream(file0);
                fileOutputStream.getChannel().truncate(0L);
                fileOutputStream.write(byteBuffer.array(), 0, byteBuffer.limit());
                fileOutputStream.flush();
                fileOutputStream.close();
                File file1 = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("WorldDictionary.bin"));
                Files.copy(file0, file1);
                file0.delete();
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new WorldDictionaryException("Error saving WorldDictionary.", exception);
            }
        }
    }

    protected void saveToByteBuffer(ByteBuffer byteBuffer) throws IOException {
        byteBuffer.putShort(this.NextItemID);
        byteBuffer.put(this.NextObjectNameID);
        byteBuffer.putInt(this.NextSpriteNameID);
        ArrayList arrayList0 = new ArrayList();
        this.getItemMods(arrayList0);
        byteBuffer.putInt(arrayList0.size());

        for (String string0 : arrayList0) {
            GameWindow.WriteString(byteBuffer, string0);
        }

        ArrayList arrayList1 = new ArrayList();
        this.getModuleList(arrayList1);
        byteBuffer.putInt(arrayList1.size());

        for (String string1 : arrayList1) {
            GameWindow.WriteString(byteBuffer, string1);
        }

        byteBuffer.putInt(this.itemIdToInfoMap.size());

        for (Entry entry0 : this.itemIdToInfoMap.entrySet()) {
            ItemInfo itemInfo = (ItemInfo)entry0.getValue();
            itemInfo.save(byteBuffer, arrayList0, arrayList1);
        }

        byteBuffer.putInt(this.objectIdToNameMap.size());

        for (Entry entry1 : this.objectIdToNameMap.entrySet()) {
            byteBuffer.put((Byte)entry1.getKey());
            GameWindow.WriteString(byteBuffer, (String)entry1.getValue());
        }

        byteBuffer.putInt(this.spriteIdToNameMap.size());

        for (Entry entry2 : this.spriteIdToNameMap.entrySet()) {
            byteBuffer.putInt((Integer)entry2.getKey());
            GameWindow.WriteString(byteBuffer, (String)entry2.getValue());
        }
    }

    protected void saveAsText(String string1) throws IOException, WorldDictionaryException {
        if (!Core.getInstance().isNoSave()) {
            File file0 = new File(ZomboidFileSystem.instance.getCurrentSaveDir() + File.separator);
            if (file0.exists() && file0.isDirectory()) {
                String string0 = ZomboidFileSystem.instance.getFileNameInCurrentSave(string1);
                File file1 = new File(string0);

                try (FileWriter fileWriter = new FileWriter(file1, false)) {
                    fileWriter.write("--[[ ---- ITEMS ---- --]]" + System.lineSeparator());
                    fileWriter.write("items = {" + System.lineSeparator());

                    for (Entry entry0 : this.itemIdToInfoMap.entrySet()) {
                        fileWriter.write("\t{" + System.lineSeparator());
                        ((ItemInfo)entry0.getValue()).saveAsText(fileWriter, "\t\t");
                        fileWriter.write("\t}," + System.lineSeparator());
                    }

                    fileWriter.write("}" + System.lineSeparator());
                    fileWriter.write(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(System.lineSeparator()));
                    fileWriter.write("--[[ ---- OBJECTS ---- --]]" + System.lineSeparator());
                    fileWriter.write("objects = {" + System.lineSeparator());

                    for (Entry entry1 : this.objectIdToNameMap.entrySet()) {
                        fileWriter.write("\t" + entry1.getKey() + " = \"" + (String)entry1.getValue() + "\"," + System.lineSeparator());
                    }

                    fileWriter.write("}" + System.lineSeparator());
                    fileWriter.write(StringConcatFactory.makeConcatWithConstants<"makeConcatWithConstants","\u0001">(System.lineSeparator()));
                    fileWriter.write("--[[ ---- SPRITES ---- --]]" + System.lineSeparator());
                    fileWriter.write("sprites = {" + System.lineSeparator());

                    for (Entry entry2 : this.spriteIdToNameMap.entrySet()) {
                        fileWriter.write("\t" + entry2.getKey() + " = \"" + (String)entry2.getValue() + "\"," + System.lineSeparator());
                    }

                    fileWriter.write("}" + System.lineSeparator());
                } catch (Exception exception) {
                    exception.printStackTrace();
                    throw new WorldDictionaryException("Error saving WorldDictionary as text.", exception);
                }
            }
        }
    }
}
