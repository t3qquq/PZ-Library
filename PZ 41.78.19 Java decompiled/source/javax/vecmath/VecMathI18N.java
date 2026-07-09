// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

class VecMathI18N {
    static String getString(String string1) {
        String string0;
        try {
            string0 = ResourceBundle.getBundle("javax.vecmath.ExceptionStrings").getString(string1);
        } catch (MissingResourceException missingResourceException) {
            System.err.println("VecMathI18N: Error looking up: " + string1);
            string0 = string1;
        }

        return string0;
    }
}
