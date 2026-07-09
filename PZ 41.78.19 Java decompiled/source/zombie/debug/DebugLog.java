// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import zombie.GameTime;
import zombie.ZomboidFileSystem;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.UIDebugConsole;
import zombie.util.StringUtils;

/**
 * Created by LEMMYPC on 31/12/13.
 */
public final class DebugLog {
    private static final boolean[] m_enabledDebugTypes = new boolean[DebugType.values().length];
    private static final HashMap<DebugType, LogSeverity> logLevels = new HashMap<>();
    private static boolean s_initialized = false;
    public static boolean printServerTime = false;
    private static final DebugLog.OutputStreamWrapper s_stdout = new DebugLog.OutputStreamWrapper(System.out);
    private static final DebugLog.OutputStreamWrapper s_stderr = new DebugLog.OutputStreamWrapper(System.err);
    private static final PrintStream m_originalOut = new PrintStream(s_stdout, true);
    private static final PrintStream m_originalErr = new PrintStream(s_stderr, true);
    private static final PrintStream GeneralErr = new DebugLogStream(m_originalErr, m_originalErr, m_originalErr, new GeneralErrorDebugLogFormatter());
    private static ZLogger s_logFileLogger;
    public static final DebugLogStream ActionSystem = createDebugLogStream(DebugType.ActionSystem);
    public static final DebugLogStream Animation = createDebugLogStream(DebugType.Animation);
    public static final DebugLogStream Asset = createDebugLogStream(DebugType.Asset);
    public static final DebugLogStream Clothing = createDebugLogStream(DebugType.Clothing);
    public static final DebugLogStream Combat = createDebugLogStream(DebugType.Combat);
    public static final DebugLogStream Damage = createDebugLogStream(DebugType.Damage);
    public static final DebugLogStream Death = createDebugLogStream(DebugType.Death);
    public static final DebugLogStream FileIO = createDebugLogStream(DebugType.FileIO);
    public static final DebugLogStream Fireplace = createDebugLogStream(DebugType.Fireplace);
    public static final DebugLogStream General = createDebugLogStream(DebugType.General);
    public static final DebugLogStream Input = createDebugLogStream(DebugType.Input);
    public static final DebugLogStream IsoRegion = createDebugLogStream(DebugType.IsoRegion);
    public static final DebugLogStream Lua = createDebugLogStream(DebugType.Lua);
    public static final DebugLogStream MapLoading = createDebugLogStream(DebugType.MapLoading);
    public static final DebugLogStream Mod = createDebugLogStream(DebugType.Mod);
    public static final DebugLogStream Multiplayer = createDebugLogStream(DebugType.Multiplayer);
    public static final DebugLogStream Network = createDebugLogStream(DebugType.Network);
    public static final DebugLogStream NetworkFileDebug = createDebugLogStream(DebugType.NetworkFileDebug);
    public static final DebugLogStream Packet = createDebugLogStream(DebugType.Packet);
    public static final DebugLogStream Objects = createDebugLogStream(DebugType.Objects);
    public static final DebugLogStream Radio = createDebugLogStream(DebugType.Radio);
    public static final DebugLogStream Recipe = createDebugLogStream(DebugType.Recipe);
    public static final DebugLogStream Script = createDebugLogStream(DebugType.Script);
    public static final DebugLogStream Shader = createDebugLogStream(DebugType.Shader);
    public static final DebugLogStream Sound = createDebugLogStream(DebugType.Sound);
    public static final DebugLogStream Statistic = createDebugLogStream(DebugType.Statistic);
    public static final DebugLogStream UnitTests = createDebugLogStream(DebugType.UnitTests);
    public static final DebugLogStream Vehicle = createDebugLogStream(DebugType.Vehicle);
    public static final DebugLogStream Voice = createDebugLogStream(DebugType.Voice);
    public static final DebugLogStream Zombie = createDebugLogStream(DebugType.Zombie);
    public static final int VERSION = 1;

    private static DebugLogStream createDebugLogStream(DebugType debugType) {
        return new DebugLogStream(m_originalOut, m_originalOut, m_originalErr, new GenericDebugLogFormatter(debugType));
    }

    public static void enableLog(DebugType type, LogSeverity severity) {
        setLogEnabled(type, true);
        logLevels.put(type, severity);
    }

    public static LogSeverity getLogLevel(DebugType type) {
        return logLevels.get(type);
    }

    public static boolean isLogEnabled(DebugType type, LogSeverity logSeverity) {
        return logSeverity.ordinal() >= logLevels.getOrDefault(type, LogSeverity.Warning).ordinal();
    }

    public static boolean isLogEnabled(LogSeverity logSeverity, DebugType type) {
        return logSeverity.ordinal() >= LogSeverity.Warning.ordinal() || isEnabled(type);
    }

    public static String formatString(DebugType type, LogSeverity logSeverity, String prefix, Object affix, String formatNoParams) {
        return isLogEnabled(logSeverity, type) ? formatStringVarArgs(type, logSeverity, prefix, affix, "%s", formatNoParams) : null;
    }

    public static String formatString(DebugType type, LogSeverity logSeverity, String prefix, Object affix, String format, Object param0) {
        return isLogEnabled(logSeverity, type) ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0) : null;
    }

    public static String formatString(DebugType type, LogSeverity logSeverity, String prefix, Object affix, String format, Object param0, Object param1) {
        return isLogEnabled(logSeverity, type) ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1) : null;
    }

    public static String formatString(
        DebugType type, LogSeverity logSeverity, String prefix, Object affix, String format, Object param0, Object param1, Object param2
    ) {
        return isLogEnabled(logSeverity, type) ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1, param2) : null;
    }

    public static String formatString(
        DebugType type, LogSeverity logSeverity, String prefix, Object affix, String format, Object param0, Object param1, Object param2, Object param3
    ) {
        return isLogEnabled(logSeverity, type) ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1, param2, param3) : null;
    }

    public static String formatString(
        DebugType type,
        LogSeverity logSeverity,
        String prefix,
        Object affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4
    ) {
        return isLogEnabled(logSeverity, type) ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1, param2, param3, param4) : null;
    }

    public static String formatString(
        DebugType type,
        LogSeverity logSeverity,
        String prefix,
        Object affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5
    ) {
        return isLogEnabled(logSeverity, type)
            ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1, param2, param3, param4, param5)
            : null;
    }

    public static String formatString(
        DebugType type,
        LogSeverity logSeverity,
        String prefix,
        Object affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5,
        Object param6
    ) {
        return isLogEnabled(logSeverity, type)
            ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1, param2, param3, param4, param5, param6)
            : null;
    }

    public static String formatString(
        DebugType type,
        LogSeverity logSeverity,
        String prefix,
        Object affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5,
        Object param6,
        Object param7
    ) {
        return isLogEnabled(logSeverity, type)
            ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1, param2, param3, param4, param5, param6, param7)
            : null;
    }

    public static String formatString(
        DebugType type,
        LogSeverity logSeverity,
        String prefix,
        Object affix,
        String format,
        Object param0,
        Object param1,
        Object param2,
        Object param3,
        Object param4,
        Object param5,
        Object param6,
        Object param7,
        Object param8
    ) {
        return isLogEnabled(logSeverity, type)
            ? formatStringVarArgs(type, logSeverity, prefix, affix, format, param0, param1, param2, param3, param4, param5, param6, param7, param8)
            : null;
    }

    public static String formatStringVarArgs(DebugType type, LogSeverity logSeverity, String prefix, Object affix, String format, Object... params) {
        if (!isLogEnabled(logSeverity, type)) {
            return null;
        } else {
            String string0 = String.valueOf(System.currentTimeMillis());
            if (GameServer.bServer || GameClient.bClient || printServerTime) {
                string0 = string0 + "> " + NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()));
            }

            String string1 = prefix + StringUtils.leftJustify(type.toString(), 12) + ", " + string0 + "> " + affix + String.format(format, params);
            echoToLogFile(string1);
            return string1;
        }
    }

    private static void echoToLogFile(String string) {
        if (s_logFileLogger == null) {
            if (s_initialized) {
                return;
            }

            s_logFileLogger = new ZLogger(GameServer.bServer ? "DebugLog-server" : "DebugLog", false);
        }

        try {
            s_logFileLogger.writeUnsafe(string, null, false);
        } catch (Exception exception) {
            m_originalErr.println("Exception thrown writing to log file.");
            m_originalErr.println(exception);
            exception.printStackTrace(m_originalErr);
        }
    }

    public static boolean isEnabled(DebugType type) {
        return m_enabledDebugTypes[type.ordinal()];
    }

    public static void log(DebugType type, String str) {
        String string = formatString(type, LogSeverity.General, "LOG  : ", "", "%s", str);
        if (string != null) {
            m_originalOut.println(string);
        }
    }

    public static void setLogEnabled(DebugType type, boolean bEnabled) {
        m_enabledDebugTypes[type.ordinal()] = bEnabled;
    }

    public static void log(Object o) {
        log(DebugType.General, String.valueOf(o));
    }

    public static void log(String str) {
        log(DebugType.General, str);
    }

    public static ArrayList<DebugType> getDebugTypes() {
        ArrayList arrayList = new ArrayList<>(Arrays.asList(DebugType.values()));
        arrayList.sort((debugType1, debugType0) -> String.CASE_INSENSITIVE_ORDER.compare(debugType1.name(), debugType0.name()));
        return arrayList;
    }

    public static void save() {
        ArrayList arrayList = new ArrayList();

        for (DebugType debugType : DebugType.values()) {
            BooleanConfigOption booleanConfigOption = new BooleanConfigOption(debugType.name(), false);
            booleanConfigOption.setValue(isEnabled(debugType));
            arrayList.add(booleanConfigOption);
        }

        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "debuglog.ini";
        ConfigFile configFile = new ConfigFile();
        configFile.write(string, 1, arrayList);
    }

    public static void load() {
        String string = ZomboidFileSystem.instance.getCacheDir() + File.separator + "debuglog.ini";
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(string)) {
            for (int int0 = 0; int0 < configFile.getOptions().size(); int0++) {
                ConfigOption configOption = configFile.getOptions().get(int0);

                try {
                    setLogEnabled(DebugType.valueOf(configOption.getName()), StringUtils.tryParseBoolean(configOption.getValueAsString()));
                } catch (Exception exception) {
                }
            }
        }
    }

    public static void setStdOut(OutputStream out) {
        s_stdout.setStream(out);
    }

    public static void setStdErr(OutputStream out) {
        s_stderr.setStream(out);
    }

    public static void init() {
        if (!s_initialized) {
            s_initialized = true;
            setStdOut(System.out);
            setStdErr(System.err);
            System.setOut(General);
            System.setErr(GeneralErr);
            if (!GameServer.bServer) {
                load();
            }

            s_logFileLogger = LoggerManager.getLogger(GameServer.bServer ? "DebugLog-server" : "DebugLog");
        }
    }

    static {
        enableLog(DebugType.Checksum, LogSeverity.Debug);
        enableLog(DebugType.Combat, LogSeverity.Debug);
        enableLog(DebugType.General, LogSeverity.Debug);
        enableLog(DebugType.IsoRegion, LogSeverity.Debug);
        enableLog(DebugType.Lua, LogSeverity.Debug);
        enableLog(DebugType.Mod, LogSeverity.Debug);
        enableLog(DebugType.Multiplayer, LogSeverity.Debug);
        enableLog(DebugType.Network, LogSeverity.Debug);
        enableLog(DebugType.Vehicle, LogSeverity.Debug);
        enableLog(DebugType.Voice, LogSeverity.Debug);
        if (GameServer.bServer) {
            enableLog(DebugType.Damage, LogSeverity.Debug);
            enableLog(DebugType.Death, LogSeverity.Debug);
            enableLog(DebugType.Statistic, LogSeverity.Debug);
        }
    }

    private static final class OutputStreamWrapper extends FilterOutputStream {
        public OutputStreamWrapper(OutputStream arg0) {
            super(arg0);
        }

        @Override
        public void write(byte[] bytes, int int0, int int1) throws IOException {
            this.out.write(bytes, int0, int1);
            if (Core.bDebug && UIDebugConsole.instance != null && DebugOptions.instance.UIDebugConsoleDebugLog.getValue()) {
                UIDebugConsole.instance.addOutput(bytes, int0, int1);
            }
        }

        public void setStream(OutputStream arg0) {
            this.out = arg0;
        }
    }
}
