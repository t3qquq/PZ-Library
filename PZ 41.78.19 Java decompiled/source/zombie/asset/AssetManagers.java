// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import gnu.trove.map.hash.TLongObjectHashMap;
import zombie.fileSystem.FileSystem;

public final class AssetManagers {
    private final AssetManagers.AssetManagerTable m_managers = new AssetManagers.AssetManagerTable();
    private final FileSystem m_file_system;

    public AssetManagers(FileSystem fileSystem) {
        this.m_file_system = fileSystem;
    }

    public AssetManager get(AssetType type) {
        return this.m_managers.get(type.type);
    }

    public void add(AssetType type, AssetManager rm) {
        this.m_managers.put(type.type, rm);
    }

    public FileSystem getFileSystem() {
        return this.m_file_system;
    }

    public static final class AssetManagerTable extends TLongObjectHashMap<AssetManager> {
    }
}
