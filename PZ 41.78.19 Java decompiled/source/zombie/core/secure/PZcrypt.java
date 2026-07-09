// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.secure;

import org.mindrot.jbcrypt.BCrypt;

public class PZcrypt {
    static String salt = "$2a$12$O/BFHoDFPrfFaNPAACmWpu";

    public static String hash(String string, boolean boolean0) {
        return boolean0 && string.isEmpty() ? string : BCrypt.hashpw(string, salt);
    }

    public static String hash(String string) {
        return hash(string, true);
    }

    public static String hashSalt(String string) {
        return BCrypt.hashpw(string, BCrypt.gensalt(12));
    }

    public static boolean checkHashSalt(String string1, String string0) {
        return BCrypt.checkpw(string0, string1);
    }
}
