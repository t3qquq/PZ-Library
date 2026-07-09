// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.commands;

public final class PlayerType {
    public static final byte deprecated = 0;
    public static final byte fromServerOnly = 0;
    public static final byte player = 1;
    public static final byte observer = 2;
    public static final byte gm = 4;
    public static final byte overseer = 8;
    public static final byte moderator = 16;
    public static final byte admin = 32;
    public static final byte all = 63;
    public static final byte allExceptPlayer = 62;

    private PlayerType() {
    }

    public static String toString(byte byte0) {
        switch (byte0) {
            case 0:
                return "from-server-only";
            case 1:
                return "";
            case 2:
                return "observer";
            case 4:
                return "gm";
            case 8:
                return "overseer";
            case 16:
                return "moderator";
            case 32:
                return "admin";
            default:
                return "";
        }
    }

    public static byte fromString(String string) {
        string = string.trim().toLowerCase();
        if ("".equals(string) || "player".equals(string) || "none".equals(string)) {
            return 1;
        } else if ("observer".equals(string)) {
            return 2;
        } else if ("gm".equals(string)) {
            return 4;
        } else if ("overseer".equals(string)) {
            return 8;
        } else if ("moderator".equals(string)) {
            return 16;
        } else {
            return (byte)("admin".equals(string) ? 32 : 0);
        }
    }

    public static boolean isPrivileged(byte byte0) {
        return (byte0 & 62) != 0;
    }
}
