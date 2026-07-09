// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import zombie.ZomboidFileSystem;

public class IndieFileLoader {
    public static InputStreamReader getStreamReader(String string) throws FileNotFoundException {
        return getStreamReader(string, false);
    }

    public static InputStreamReader getStreamReader(String string, boolean boolean0) throws FileNotFoundException {
        InputStreamReader inputStreamReader = null;
        Object object = null;
        if (object != null && !boolean0) {
            inputStreamReader = new InputStreamReader((InputStream)object);
        } else {
            try {
                FileInputStream fileInputStream0 = new FileInputStream(ZomboidFileSystem.instance.getString(string));
                inputStreamReader = new InputStreamReader(fileInputStream0, "UTF-8");
            } catch (Exception exception) {
                FileInputStream fileInputStream1 = new FileInputStream(Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + string);
                inputStreamReader = new InputStreamReader(fileInputStream1);
            }
        }

        return inputStreamReader;
    }
}
