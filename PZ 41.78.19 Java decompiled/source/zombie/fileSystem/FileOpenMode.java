// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

public final class FileOpenMode {
    public static final int NONE = 0;
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int OPEN = 4;
    public static final int CREATE = 8;
    public static final int STREAM = 16;
    public static final int CREATE_AND_WRITE = 10;
    public static final int OPEN_AND_READ = 5;

    public static String toStringMode(int int0) {
        StringBuilder stringBuilder = new StringBuilder();
        if ((int0 & 1) != 0) {
            stringBuilder.append('r');
        }

        if ((int0 & 2) != 0) {
            stringBuilder.append('w');
        }

        return stringBuilder.toString();
    }
}
