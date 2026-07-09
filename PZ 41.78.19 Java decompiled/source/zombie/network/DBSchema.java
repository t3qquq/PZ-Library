// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTable;

public class DBSchema {
    private HashMap<String, HashMap<String, String>> schema = new HashMap<>();
    private KahluaTable fullTable;

    public DBSchema(Connection connection) {
        try {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            String[] strings = new String[]{"TABLE"};
            ResultSet resultSet0 = databaseMetaData.getTables(null, null, null, strings);

            while (resultSet0.next()) {
                String string0 = resultSet0.getString(3);
                if (!string0.startsWith("SQLITE_")) {
                    ResultSet resultSet1 = databaseMetaData.getColumns(null, null, string0, null);
                    HashMap hashMap = new HashMap();

                    while (resultSet1.next()) {
                        String string1 = resultSet1.getString(4);
                        if (!string1.equals("world")
                            && !string1.equals("moderator")
                            && !string1.equals("admin")
                            && !string1.equals("password")
                            && !string1.equals("encryptedPwd")
                            && !string1.equals("pwdEncryptType")
                            && !string1.equals("transactionID")) {
                            hashMap.put(string1, resultSet1.getString(6));
                        }
                    }

                    this.schema.put(string0, hashMap);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public KahluaTable getFullTable() {
        return this.fullTable;
    }

    public void setFullTable(KahluaTable table) {
        this.fullTable = table;
    }

    public HashMap<String, HashMap<String, String>> getSchema() {
        return this.schema;
    }
}
