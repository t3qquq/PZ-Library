// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;

public final class ZLogger {
    private final String name;
    private final ZLogger.OutputStreams outputStreams = new ZLogger.OutputStreams();
    private File file = null;
    private static final SimpleDateFormat s_fileNameSdf = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
    private static final SimpleDateFormat s_logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");
    private static final long s_maxSizeKo = 10000L;

    /**
     * Write logs into file and console.
     * 
     * @param _name filename
     * @param useConsole if true then write logs into console also
     */
    public ZLogger(String _name, boolean useConsole) {
        this.name = _name;

        try {
            this.file = new File(LoggerManager.getLogsDir() + File.separator + getLoggerName(_name) + ".txt");
            this.outputStreams.file = new PrintStream(this.file);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

        if (useConsole) {
            this.outputStreams.console = System.out;
        }
    }

    private static String getLoggerName(String string) {
        return s_fileNameSdf.format(Calendar.getInstance().getTime()) + "_" + string;
    }

    public void write(String logs) {
        this.write(logs, null);
    }

    public void write(String logs, String level) {
        this.write(logs, level, false);
    }

    public void write(String string0, String string1, boolean boolean0) {
        try {
            this.writeUnsafe(string0, string1, boolean0);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public synchronized void writeUnsafe(String string1, String string0, boolean boolean0) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        if (!boolean0) {
            stringBuilder.append("[").append(s_logSdf.format(Calendar.getInstance().getTime())).append("]");
        }

        if (!StringUtils.isNullOrEmpty(string0)) {
            stringBuilder.append("[").append(string0).append("]");
        }

        int int0 = string1.length();
        if (string1.lastIndexOf(10) == string1.length() - 1) {
            int0--;
        }

        if (!boolean0) {
            stringBuilder.append(" ").append(string1, 0, int0).append(".");
        } else {
            stringBuilder.append(string1, 0, int0);
        }

        this.outputStreams.println(stringBuilder.toString());
        this.checkSizeUnsafe();
    }

    public synchronized void write(Exception ex) {
        ex.printStackTrace(this.outputStreams.file);
        this.checkSize();
    }

    private synchronized void checkSize() {
        try {
            this.checkSizeUnsafe();
        } catch (Exception exception) {
            DebugLog.General.error("Exception thrown checking log file size.");
            DebugLog.General.error(exception);
            exception.printStackTrace();
        }
    }

    private synchronized void checkSizeUnsafe() throws Exception {
        long long0 = this.file.length() / 1024L;
        if (long0 > 10000L) {
            this.outputStreams.file.close();
            this.file = new File(LoggerManager.getLogsDir() + File.separator + getLoggerName(this.name) + ".txt");
            this.outputStreams.file = new PrintStream(this.file);
        }
    }

    private static class OutputStreams {
        public PrintStream file;
        public PrintStream console;

        public void println(String arg0) {
            if (this.file != null) {
                this.file.println(arg0);
                this.file.flush();
            }

            if (this.console != null) {
                this.console.println(arg0);
            }
        }
    }
}
