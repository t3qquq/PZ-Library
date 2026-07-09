// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.world;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import zombie.scripting.ScriptManager;

public class DictionaryDataClient extends DictionaryData {
    @Override
    protected boolean isClient() {
        return true;
    }

    @Override
    protected void parseItemLoadList(Map<String, ItemInfo> var1) throws WorldDictionaryException {
    }

    @Override
    protected void parseCurrentItemSet() throws WorldDictionaryException {
        for (Entry entry : this.itemTypeToInfoMap.entrySet()) {
            ItemInfo itemInfo = (ItemInfo)entry.getValue();
            if (!itemInfo.removed && itemInfo.scriptItem == null) {
                itemInfo.scriptItem = ScriptManager.instance.getSpecificItem(itemInfo.fullType);
            }

            if (itemInfo.scriptItem != null) {
                itemInfo.scriptItem.setRegistry_id(itemInfo.registryID);
                itemInfo.scriptItem.setModID(itemInfo.modID);
                itemInfo.isLoaded = true;
            } else if (!itemInfo.removed) {
                throw new WorldDictionaryException("Warning client has no script for item " + itemInfo.fullType);
            }
        }
    }

    @Override
    protected void parseObjectNameLoadList(List<String> var1) throws WorldDictionaryException {
    }

    @Override
    protected void backupCurrentDataSet() throws IOException {
    }

    @Override
    protected void deleteBackupCurrentDataSet() throws IOException {
    }

    @Override
    protected void createErrorBackups() {
    }

    @Override
    protected void load() throws IOException, WorldDictionaryException {
    }

    @Override
    protected void save() throws IOException, WorldDictionaryException {
    }

    @Override
    protected void saveToByteBuffer(ByteBuffer var1) throws IOException {
    }
}
