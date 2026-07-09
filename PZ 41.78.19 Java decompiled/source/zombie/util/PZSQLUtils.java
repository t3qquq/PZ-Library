// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import zombie.core.logger.ExceptionLogger;

public class PZSQLUtils {
    public static void init() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException classNotFoundException) {
            ExceptionLogger.logException(classNotFoundException);
            System.exit(1);
        }

        setupSqliteVariables();
    }

    private static void setupSqliteVariables() {
        if (!System.getProperty("os.name").contains("OS X")) {
            if (System.getProperty("os.name").startsWith("Win")) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    System.setProperty("org.sqlite.lib.path", searchPathForSqliteLib("sqlitejdbc64.dll"));
                    System.setProperty("org.sqlite.lib.name", "sqlitejdbc64.dll");
                }
            } else if (System.getProperty("sun.arch.data.model").equals("64")) {
            }
        }
    }

    private static String searchPathForSqliteLib(String string1) {
        for (String string0 : System.getProperty("java.library.path", "").split(File.pathSeparator)) {
            File file = new File(string0, string1);
            if (file.exists()) {
                return string0;
            }
        }

        return "";
    }

    public static Connection getConnection(String string) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + string);
    }
}
