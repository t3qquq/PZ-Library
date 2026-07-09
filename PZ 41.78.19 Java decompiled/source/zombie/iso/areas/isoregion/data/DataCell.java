// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * TurboTuTone.
 */
public final class DataCell {
    public final DataRoot dataRoot;
    protected final Map<Integer, DataChunk> dataChunks = new HashMap<>();

    protected DataCell(DataRoot dataRootx, int var2, int var3, int var4) {
        this.dataRoot = dataRootx;
    }

    protected DataRoot getDataRoot() {
        return this.dataRoot;
    }

    protected DataChunk getChunk(int int0) {
        return this.dataChunks.get(int0);
    }

    protected DataChunk addChunk(int int0, int int1, int int2) {
        DataChunk dataChunk = new DataChunk(int0, int1, this, int2);
        this.dataChunks.put(int2, dataChunk);
        return dataChunk;
    }

    protected void setChunk(DataChunk dataChunk) {
        this.dataChunks.put(dataChunk.getHashId(), dataChunk);
    }

    protected void getAllChunks(List<DataChunk> list) {
        for (Entry entry : this.dataChunks.entrySet()) {
            list.add((DataChunk)entry.getValue());
        }
    }
}
