// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.util.PZSQLUtils;

public final class VehicleDBHelper {
    public static boolean isPlayerAlive(String string0, int int0) {
        File file0 = new File(string0 + File.separator + "map_p.bin");
        if (file0.exists()) {
            return true;
        } else if (int0 == -1) {
            return false;
        } else {
            Connection connection = null;
            File file1 = new File(string0 + File.separator + "vehicles.db");
            file1.setReadable(true, false);
            if (!file1.exists()) {
                return false;
            } else {
                try {
                    connection = PZSQLUtils.getConnection(file1.getAbsolutePath());
                } catch (Exception exception) {
                    DebugLog.log("failed to create vehicles database");
                    ExceptionLogger.logException(exception);
                    System.exit(1);
                }

                boolean boolean0 = false;
                String string1 = "SELECT isDead FROM localPlayers WHERE id=?";
                PreparedStatement preparedStatement = null;

                boolean boolean1;
                try {
                    preparedStatement = connection.prepareStatement(string1);
                    preparedStatement.setInt(1, int0);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        boolean0 = !resultSet.getBoolean(1);
                    }

                    return boolean0;
                } catch (SQLException sQLException0) {
                    boolean1 = false;
                } finally {
                    try {
                        if (preparedStatement != null) {
                            preparedStatement.close();
                        }

                        connection.close();
                    } catch (SQLException sQLException1) {
                        System.out.println(sQLException1.getMessage());
                    }
                }

                return boolean1;
            }
        }
    }
}
