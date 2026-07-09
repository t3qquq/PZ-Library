// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import zombie.core.Core;

public final class ChunkMapFilenames {
    public static ChunkMapFilenames instance = new ChunkMapFilenames();
    public final ConcurrentHashMap<Long, Object> Map = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, Object> HeaderMap = new ConcurrentHashMap<>();
    String prefix = "map_";
    private File dirFile;
    private String cacheDir;

    public void clear() {
        this.dirFile = null;
        this.cacheDir = null;
        this.Map.clear();
        this.HeaderMap.clear();
    }

    public File getFilename(int int1, int int0) {
        long long0 = (long)int1 << 32 | int0;
        if (this.Map.containsKey(long0)) {
            return (File)this.Map.get(long0);
        } else {
            if (this.cacheDir == null) {
                this.cacheDir = ZomboidFileSystem.instance.getGameModeCacheDir();
            }

            String string = this.cacheDir + File.separator + Core.GameSaveWorld + File.separator + this.prefix + int1 + "_" + int0 + ".bin";
            File file = new File(string);
            this.Map.put(long0, file);
            return file;
        }
    }

    public File getDir(String string) {
        if (this.cacheDir == null) {
            this.cacheDir = ZomboidFileSystem.instance.getGameModeCacheDir();
        }

        if (this.dirFile == null) {
            this.dirFile = new File(this.cacheDir + File.separator + string);
        }

        return this.dirFile;
    }

    public String getHeader(int int1, int int0) {
        long long0 = (long)int1 << 32 | int0;
        if (this.HeaderMap.containsKey(long0)) {
            return this.HeaderMap.get(long0).toString();
        } else {
            String string = int1 + "_" + int0 + ".lotheader";
            this.HeaderMap.put(long0, string);
            return string;
        }
    }
}
