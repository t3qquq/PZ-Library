// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.logger;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.debug.DebugLog;

public final class LoggerManager {
    private static boolean s_isInitialized = false;
    private static final HashMap<String, ZLogger> s_loggers = new HashMap<>();

    public static synchronized ZLogger getLogger(String string) {
        if (!s_loggers.containsKey(string)) {
            createLogger(string, false);
        }

        return s_loggers.get(string);
    }

    public static synchronized void init() {
        if (!s_isInitialized) {
            DebugLog.General.debugln("Initializing...");
            s_isInitialized = true;
            backupOldLogFiles();
        }
    }

    private static void backupOldLogFiles() {
        try {
            File file0 = new File(getLogsDir());
            String[] strings = file0.list();
            if (strings == null || strings.length == 0) {
                return;
            }

            Calendar calendar = getLogFileLastModifiedTime(strings[0]);
            String string = "logs_";
            if (calendar.get(5) < 9) {
                string = string + "0" + calendar.get(5);
            } else {
                string = string + calendar.get(5);
            }

            if (calendar.get(2) < 9) {
                string = string + "-0" + (calendar.get(2) + 1);
            } else {
                string = string + "-" + (calendar.get(2) + 1);
            }

            File file1 = new File(getLogsDir() + File.separator + string);
            if (!file1.exists()) {
                file1.mkdir();
            }

            for (int int0 = 0; int0 < strings.length; int0++) {
                string = strings[int0];
                File file2 = new File(getLogsDir() + File.separator + string);
                if (file2.isFile()) {
                    file2.renameTo(new File(file1.getAbsolutePath() + File.separator + file2.getName()));
                    file2.delete();
                }
            }
        } catch (Exception exception) {
            DebugLog.General.error("Exception thrown trying to initialize LoggerManager, trying to copy old log files.");
            DebugLog.General.error("Exception: ");
            DebugLog.General.error(exception);
            exception.printStackTrace();
        }
    }

    private static Calendar getLogFileLastModifiedTime(String string) {
        File file = new File(getLogsDir() + File.separator + string);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(file.lastModified());
        return calendar;
    }

    public static synchronized void createLogger(String string, boolean boolean0) {
        init();
        s_loggers.put(string, new ZLogger(string, boolean0));
    }

    public static String getLogsDir() {
        String string = ZomboidFileSystem.instance.getCacheDirSub("Logs");
        ZomboidFileSystem.ensureFolderExists(string);
        File file = new File(string);
        return file.getAbsolutePath();
    }

    public static String getPlayerCoords(IsoPlayer player) {
        return "(" + (int)player.getX() + "," + (int)player.getY() + "," + (int)player.getZ() + ")";
    }
}
