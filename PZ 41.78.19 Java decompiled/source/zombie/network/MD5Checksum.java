// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.CRC32;

public class MD5Checksum {
    public static long createChecksum(String string) throws Exception {
        File file = new File(string);
        if (!file.exists()) {
            return 0L;
        } else {
            FileInputStream fileInputStream = new FileInputStream(string);
            CRC32 crc32 = new CRC32();
            byte[] bytes = new byte[1024];

            int int0;
            while ((int0 = fileInputStream.read(bytes)) != -1) {
                crc32.update(bytes, 0, int0);
            }

            long long0 = crc32.getValue();
            fileInputStream.close();
            return long0;
        }
    }

    public static void main(String[] var0) {
    }
}
