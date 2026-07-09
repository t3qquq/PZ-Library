// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import zombie.debug.DebugLog;

public final class ConfigFile {
    protected ArrayList<ConfigOption> options;
    protected int version;

    private void fileError(String string1, int int0, String string0) {
        DebugLog.log(string1 + ":" + int0 + " " + string0);
    }

    public boolean read(String string0) {
        this.options = new ArrayList<>();
        this.version = 0;
        File file = new File(string0);
        if (!file.exists()) {
            return false;
        } else {
            DebugLog.log("reading " + string0);

            try (
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
            ) {
                int int0 = 0;

                while (true) {
                    String string1 = bufferedReader.readLine();
                    if (string1 == null) {
                        return true;
                    }

                    int0++;
                    string1 = string1.trim();
                    if (!string1.isEmpty() && !string1.startsWith("#")) {
                        if (!string1.contains("=")) {
                            this.fileError(string0, int0, string1);
                        } else {
                            String[] strings = string1.split("=");
                            if ("Version".equals(strings[0])) {
                                try {
                                    this.version = Integer.parseInt(strings[1]);
                                } catch (NumberFormatException numberFormatException) {
                                    this.fileError(string0, int0, "expected version number, got \"" + strings[1] + "\"");
                                }
                            } else {
                                StringConfigOption stringConfigOption = new StringConfigOption(strings[0], strings.length > 1 ? strings[1] : "", -1);
                                this.options.add(stringConfigOption);
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }
    }

    public boolean write(String string0, int int0, ArrayList<? extends ConfigOption> arrayList) {
        File file = new File(string0);
        DebugLog.log("writing " + string0);

        try {
            try (FileWriter fileWriter = new FileWriter(file, false)) {
                if (int0 != 0) {
                    fileWriter.write("Version=" + int0 + System.lineSeparator());
                }

                for (int int1 = 0; int1 < arrayList.size(); int1++) {
                    ConfigOption configOption = (ConfigOption)arrayList.get(int1);
                    String string1 = configOption.getTooltip();
                    if (string1 != null) {
                        string1 = string1.replaceAll("\n", System.lineSeparator() + "# ");
                        fileWriter.write("# " + string1 + System.lineSeparator());
                    }

                    fileWriter.write(
                        configOption.getName()
                            + "="
                            + configOption.getValueAsString()
                            + (int1 < arrayList.size() - 1 ? System.lineSeparator() + System.lineSeparator() : "")
                    );
                }
            }

            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public ArrayList<ConfigOption> getOptions() {
        return this.options;
    }

    public int getVersion() {
        return this.version;
    }
}
