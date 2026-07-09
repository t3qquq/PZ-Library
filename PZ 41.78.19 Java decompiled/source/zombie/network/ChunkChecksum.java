// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import gnu.trove.map.hash.TIntLongHashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import zombie.ZomboidFileSystem;
import zombie.core.Core;

public class ChunkChecksum {
    private static final TIntLongHashMap checksumCache = new TIntLongHashMap();
    private static final StringBuilder stringBuilder = new StringBuilder(128);
    private static final CRC32 crc32 = new CRC32();
    private static final byte[] bytes = new byte[1024];

    private static void noise(String var0) {
        if (Core.bDebug) {
        }
    }

    public static long getChecksum(int int1, int int2) throws IOException {
        MPStatistic.getInstance().ChunkChecksum.Start();
        long long0 = 0L;
        synchronized (checksumCache) {
            int int0 = int1 + int2 * 30 * 1000;
            if (checksumCache.containsKey(int0)) {
                noise(int1 + "," + int2 + " found in cache crc=" + checksumCache.get(int0));
                long0 = checksumCache.get(int0);
            } else {
                stringBuilder.setLength(0);
                stringBuilder.append(ZomboidFileSystem.instance.getGameModeCacheDir());
                stringBuilder.append(File.separator);
                stringBuilder.append(Core.GameSaveWorld);
                stringBuilder.append(File.separator);
                stringBuilder.append("map_");
                stringBuilder.append(int1);
                stringBuilder.append("_");
                stringBuilder.append(int2);
                stringBuilder.append(".bin");
                long0 = createChecksum(stringBuilder.toString());
                checksumCache.put(int0, long0);
                noise(int1 + "," + int2 + " read from disk crc=" + long0);
            }
        }

        MPStatistic.getInstance().ChunkChecksum.End();
        return long0;
    }

    public static long getChecksumIfExists(int int1, int int2) throws IOException {
        long long0 = 0L;
        MPStatistic.getInstance().ChunkChecksum.Start();
        synchronized (checksumCache) {
            int int0 = int1 + int2 * 30 * 1000;
            if (checksumCache.containsKey(int0)) {
                long0 = checksumCache.get(int0);
            }
        }

        MPStatistic.getInstance().ChunkChecksum.End();
        return long0;
    }

    public static void setChecksum(int int1, int int2, long long0) {
        MPStatistic.getInstance().ChunkChecksum.Start();
        synchronized (checksumCache) {
            int int0 = int1 + int2 * 30 * 1000;
            checksumCache.put(int0, long0);
            noise(int1 + "," + int2 + " set crc=" + long0);
        }

        MPStatistic.getInstance().ChunkChecksum.End();
    }

    public static long createChecksum(String string) throws IOException {
        MPStatistic.getInstance().ChunkChecksum.Start();
        File file = new File(string);
        if (!file.exists()) {
            MPStatistic.getInstance().ChunkChecksum.End();
            return 0L;
        } else {
            long long0;
            try (FileInputStream fileInputStream = new FileInputStream(string)) {
                crc32.reset();

                int int0;
                while ((int0 = fileInputStream.read(bytes)) != -1) {
                    crc32.update(bytes, 0, int0);
                }

                long long1 = crc32.getValue();
                MPStatistic.getInstance().ChunkChecksum.End();
                long0 = long1;
            }

            return long0;
        }
    }

    public static void Reset() {
        MPStatistic.getInstance().ChunkChecksum.Start();
        checksumCache.clear();
        MPStatistic.getInstance().ChunkChecksum.End();
    }
}
