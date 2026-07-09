// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.IOException;
import zombie.core.logger.ExceptionLogger;

public final class DesktopBrowser {
    private static final String[] browsers = new String[]{
        "google-chrome", "firefox", "mozilla", "epiphany", "konqueror", "netscape", "opera", "links", "lynx", "chromium", "brave-browser"
    };

    public static boolean openURL(String string0) {
        try {
            if (System.getProperty("os.name").contains("OS X")) {
                Runtime.getRuntime().exec(String.format("open %s", string0));
                return true;
            }

            if (System.getProperty("os.name").startsWith("Win")) {
                Runtime.getRuntime().exec(String.format("rundll32 url.dll,FileProtocolHandler %s", string0));
                return true;
            }

            for (String string1 : browsers) {
                Process process = Runtime.getRuntime().exec(new String[]{"which", string1});
                if (process.getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{string1, string0});
                    return true;
                }
            }
        } catch (IOException iOException) {
            ExceptionLogger.logException(iOException);
        }

        return false;
    }
}
