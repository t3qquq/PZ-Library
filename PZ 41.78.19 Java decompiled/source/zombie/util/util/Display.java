// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.util;

public class Display {
    private static final String displayChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"#\u00a4%&/()=?'@\u00a3${[]}+|^~*-_.:,;<>\\";

    public static String display(int int0) {
        return String.valueOf(int0);
    }

    static String hexChar(char char0) {
        String string = Integer.toHexString(char0);
        switch (string.length()) {
            case 1:
                return "\\u000" + string;
            case 2:
                return "\\u00" + string;
            case 3:
                return "\\u0" + string;
            case 4:
                return "\\u" + string;
            default:
                throw new RuntimeException("Internal error");
        }
    }
}
