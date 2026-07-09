// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.world.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.network.GameClient;

public class WorldDictionaryLogger {
    private static final ArrayList<Log.BaseLog> _logItems = new ArrayList<>();

    public static void reset() {
        _logItems.clear();
    }

    public static void startLogging() {
        reset();
    }

    public static void log(Log.BaseLog baseLog) {
        if (!GameClient.bClient) {
            _logItems.add(baseLog);
        }
    }

    public static void log(String string) {
        log(string, true);
    }

    public static void log(String string, boolean boolean0) {
        if (!GameClient.bClient) {
            if (boolean0) {
                DebugLog.log("WorldDictionary: " + string);
            }

            _logItems.add(new Log.Comment(string));
        }
    }

    public static void saveLog(String string1) throws IOException {
        if (!GameClient.bClient) {
            boolean boolean0 = false;

            for (int int0 = 0; int0 < _logItems.size(); int0++) {
                Log.BaseLog baseLog0 = _logItems.get(int0);
                if (!baseLog0.isIgnoreSaveCheck()) {
                    boolean0 = true;
                    break;
                }
            }

            if (boolean0) {
                File file0 = new File(ZomboidFileSystem.instance.getCurrentSaveDir() + File.separator);
                if (file0.exists() && file0.isDirectory()) {
                    String string0 = ZomboidFileSystem.instance.getFileNameInCurrentSave(string1);
                    File file1 = new File(string0);

                    try (FileWriter fileWriter = new FileWriter(file1, true)) {
                        fileWriter.write("log = log or {};" + System.lineSeparator());
                        fileWriter.write("table.insert(log, {" + System.lineSeparator());

                        for (int int1 = 0; int1 < _logItems.size(); int1++) {
                            Log.BaseLog baseLog1 = _logItems.get(int1);
                            baseLog1.saveAsText(fileWriter, "\t");
                        }

                        fileWriter.write("};" + System.lineSeparator());
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        throw new IOException("Error saving WorldDictionary log.");
                    }
                }
            }
        }
    }
}
