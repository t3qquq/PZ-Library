// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas;

import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Mapping0;
import java.io.FileInputStream;

public class IsoArea {
    public static String version = "0a2a0q";
    public static boolean Doobo;

    public static byte[] asasa(String string) throws Exception {
        new FileInputStream(string);
        return new byte[1024];
    }

    public static String Ardo(String string0) throws Exception {
        byte[] bytes = asasa(string0);
        String string1 = "";

        for (int int0 = 0; int0 < bytes.length; int0++) {
            string1 = Block.asdsadsa(string1, bytes, int0);
        }

        return string1;
    }

    public static boolean Thigglewhat2(String string1, String string2) {
        String string0 = "";

        try {
            string0 = Ardo(string1);
            if (!string0.equals(string2)) {
                return false;
            }
        } catch (Exception exception1) {
            string0 = "";

            try {
                string0 = Ardo(IsoRoomExit.ThiggleQ + string1);
            } catch (Exception exception0) {
                return false;
            }
        }

        return string0.equals(string2);
    }

    public static String Thigglewhat22(String string1) {
        String string0 = "";

        try {
            string0 = Ardo(string1);
        } catch (Exception exception1) {
            string0 = "";

            try {
                string0 = Ardo(IsoRoomExit.ThiggleQ + string1);
            } catch (Exception exception0) {
                return "";
            }
        }

        return string0;
    }

    public static boolean Thigglewhat() {
        String string = "";
        string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleA + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
        string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleB + Mapping0.ThiggleBB + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
        string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleC + Mapping0.ThiggleCC + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
        string = string
            + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleD + Mapping0.ThiggleDA + Mapping0.ThiggleDB + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
        string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleE + Mapping0.ThiggleEA + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
        string = string + Thigglewhat22(Mapping0.ThiggleAQQ2 + Mapping0.ThiggleF + Mapping0.ThiggleFA + Mapping0.ThiggleAQ + Mapping0.ThiggleAQ2);
        string = string
            + Thigglewhat22(
                Mapping0.ThiggleAQQ2
                    + Mapping0.ThiggleG
                    + Mapping0.ThiggleGA
                    + Mapping0.ThiggleGB
                    + Mapping0.ThiggleGC
                    + Mapping0.ThiggleAQ
                    + Mapping0.ThiggleAQ2
            );
        string = string.toUpperCase();
        return true;
    }
}
