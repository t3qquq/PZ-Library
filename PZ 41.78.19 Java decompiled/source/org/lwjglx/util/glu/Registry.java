// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

public class Registry extends Util {
    private static final String versionString = "1.3";
    private static final String extensionString = "GLU_EXT_nurbs_tessellator GLU_EXT_object_space_tess ";

    public static String gluGetString(int int0) {
        if (int0 == 100800) {
            return "1.3";
        } else {
            return int0 == 100801 ? "GLU_EXT_nurbs_tessellator GLU_EXT_object_space_tess " : null;
        }
    }

    public static boolean gluCheckExtension(String string1, String string0) {
        return string0 != null && string1 != null ? string0.indexOf(string1) != -1 : false;
    }
}
