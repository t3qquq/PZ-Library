// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.savefile;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.util.PZSQLUtils;
import zombie.vehicles.VehicleDBHelper;

public final class PlayerDBHelper {
    public static Connection create() {
        Connection connection = null;
        String string = ZomboidFileSystem.instance.getCurrentSaveDir();
        File file0 = new File(string);
        if (!file0.exists()) {
            file0.mkdirs();
        }

        File file1 = new File(string + File.separator + "players.db");
        file1.setReadable(true, false);
        file1.setExecutable(true, false);
        file1.setWritable(true, false);
        if (!file1.exists()) {
            try {
                file1.createNewFile();
                connection = PZSQLUtils.getConnection(file1.getAbsolutePath());
                Statement statement0 = connection.createStatement();
                statement0.executeUpdate(
                    "CREATE TABLE localPlayers (id   INTEGER PRIMARY KEY NOT NULL,name STRING,wx    INTEGER,wy    INTEGER,x    FLOAT,y    FLOAT,z    FLOAT,worldversion    INTEGER,data BLOB,isDead BOOLEAN);"
                );
                statement0.executeUpdate(
                    "CREATE TABLE networkPlayers (id   INTEGER PRIMARY KEY NOT NULL,world TEXT,username TEXT,playerIndex   INTEGER,name STRING,steamid STRING,x    FLOAT,y    FLOAT,z    FLOAT,worldversion    INTEGER,data BLOB,isDead BOOLEAN);"
                );
                statement0.executeUpdate("CREATE INDEX inpusername ON networkPlayers (username);");
                statement0.close();
            } catch (Exception exception0) {
                ExceptionLogger.logException(exception0);
                DebugLog.log("failed to create players database");
                System.exit(1);
            }
        }

        if (connection == null) {
            try {
                connection = PZSQLUtils.getConnection(file1.getAbsolutePath());
            } catch (Exception exception1) {
                ExceptionLogger.logException(exception1);
                DebugLog.log("failed to create players database");
                System.exit(1);
            }
        }

        try {
            Statement statement1 = connection.createStatement();
            statement1.executeQuery("PRAGMA JOURNAL_MODE=TRUNCATE;");
            statement1.close();
        } catch (Exception exception2) {
            ExceptionLogger.logException(exception2);
            DebugLog.log("failed to config players.db");
            System.exit(1);
        }

        try {
            connection.setAutoCommit(false);
        } catch (SQLException sQLException) {
            DebugLog.log("failed to setAutoCommit for players.db");
        }

        return connection;
    }

    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException sQLException) {
                ExceptionLogger.logException(sQLException);
            }
        }
    }

    public static boolean isPlayerAlive(String string0, int int0) {
        if (Core.getInstance().isNoSave()) {
            return false;
        } else {
            File file0 = new File(string0 + File.separator + "map_p.bin");
            if (file0.exists()) {
                return true;
            } else if (VehicleDBHelper.isPlayerAlive(string0, int0)) {
                return true;
            } else if (int0 == -1) {
                return false;
            } else {
                try {
                    File file1 = new File(string0 + File.separator + "players.db");
                    if (!file1.exists()) {
                        return false;
                    } else {
                        file1.setReadable(true, false);

                        boolean boolean0;
                        try (Connection connection = PZSQLUtils.getConnection(file1.getAbsolutePath())) {
                            String string1 = "SELECT isDead FROM localPlayers WHERE id=?";

                            try (PreparedStatement preparedStatement = connection.prepareStatement(string1)) {
                                preparedStatement.setInt(1, int0);
                                ResultSet resultSet = preparedStatement.executeQuery();
                                if (!resultSet.next()) {
                                    return false;
                                }

                                boolean0 = !resultSet.getBoolean(1);
                            }
                        }

                        return boolean0;
                    }
                } catch (Throwable throwable2) {
                    ExceptionLogger.logException(throwable2);
                    return false;
                }
            }
        }
    }

    public static ArrayList<Object> getPlayers(String string0) throws SQLException {
        ArrayList arrayList = new ArrayList();
        if (Core.getInstance().isNoSave()) {
            return arrayList;
        } else {
            File file = new File(string0 + File.separator + "players.db");
            if (!file.exists()) {
                return arrayList;
            } else {
                file.setReadable(true, false);

                try (Connection connection = PZSQLUtils.getConnection(file.getAbsolutePath())) {
                    String string1 = "SELECT id, name, isDead FROM localPlayers";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(string1)) {
                        ResultSet resultSet = preparedStatement.executeQuery();

                        while (resultSet.next()) {
                            int int0 = resultSet.getInt(1);
                            String string2 = resultSet.getString(2);
                            boolean boolean0 = resultSet.getBoolean(3);
                            arrayList.add(BoxedStaticValues.toDouble(int0));
                            arrayList.add(string2);
                            arrayList.add(boolean0 ? Boolean.TRUE : Boolean.FALSE);
                        }
                    }
                }

                return arrayList;
            }
        }
    }

    public static void setPlayer1(String string0, int int0) throws SQLException {
        if (!Core.getInstance().isNoSave()) {
            if (int0 != 1) {
                File file = new File(string0 + File.separator + "players.db");
                if (file.exists()) {
                    file.setReadable(true, false);

                    try (Connection connection = PZSQLUtils.getConnection(file.getAbsolutePath())) {
                        boolean boolean0 = false;
                        boolean boolean1 = false;
                        int int1 = -1;
                        int int2 = -1;
                        String string1 = "SELECT id FROM localPlayers";

                        try (PreparedStatement preparedStatement0 = connection.prepareStatement(string1)) {
                            ResultSet resultSet = preparedStatement0.executeQuery();

                            while (resultSet.next()) {
                                int int3 = resultSet.getInt(1);
                                if (int3 == 1) {
                                    boolean0 = true;
                                } else if (int1 == -1 || int1 > int3) {
                                    int1 = int3;
                                }

                                if (int3 == int0) {
                                    boolean1 = true;
                                }

                                int2 = Math.max(int2, int3);
                            }
                        }

                        if (int0 <= 0) {
                            if (!boolean0) {
                                return;
                            }

                            string1 = "UPDATE localPlayers SET id=? WHERE id=?";

                            try (PreparedStatement preparedStatement1 = connection.prepareStatement(string1)) {
                                preparedStatement1.setInt(1, int2 + 1);
                                preparedStatement1.setInt(2, 1);
                                preparedStatement1.executeUpdate();
                                return;
                            }
                        }

                        if (!boolean1) {
                            return;
                        }

                        if (boolean0) {
                            string1 = "UPDATE localPlayers SET id=? WHERE id=?";

                            try (PreparedStatement preparedStatement2 = connection.prepareStatement(string1)) {
                                preparedStatement2.setInt(1, int2 + 1);
                                preparedStatement2.setInt(2, 1);
                                preparedStatement2.executeUpdate();
                                preparedStatement2.setInt(1, 1);
                                preparedStatement2.setInt(2, int0);
                                preparedStatement2.executeUpdate();
                                preparedStatement2.setInt(1, int0);
                                preparedStatement2.setInt(2, int2 + 1);
                                preparedStatement2.executeUpdate();
                            }
                        } else {
                            string1 = "UPDATE localPlayers SET id=? WHERE id=?";

                            try (PreparedStatement preparedStatement3 = connection.prepareStatement(string1)) {
                                preparedStatement3.setInt(1, 1);
                                preparedStatement3.setInt(2, int0);
                                preparedStatement3.executeUpdate();
                            }
                        }
                    }
                }
            }
        }
    }
}
