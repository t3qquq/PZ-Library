// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;

public final class FileTask_LoadWorldMapXML extends FileTask {
    WorldMapData m_worldMapData;
    String m_filename;

    public FileTask_LoadWorldMapXML(WorldMapData worldMapData, String string, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
        super(fileSystem, iFileTaskCallback);
        this.m_worldMapData = worldMapData;
        this.m_filename = string;
    }

    @Override
    public String getErrorMessage() {
        return this.m_filename;
    }

    @Override
    public void done() {
        this.m_worldMapData = null;
        this.m_filename = null;
    }

    @Override
    public Object call() throws Exception {
        WorldMapXML worldMapXML = new WorldMapXML();
        return worldMapXML.read(this.m_filename, this.m_worldMapData) ? Boolean.TRUE : Boolean.FALSE;
    }
}
