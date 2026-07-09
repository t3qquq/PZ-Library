// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.util.ArrayList;
import java.util.HashMap;

public class DBResult {
    private HashMap<String, String> values = new HashMap<>();
    private ArrayList<String> columns = new ArrayList<>();
    private String type;
    private String tableName;

    public HashMap<String, String> getValues() {
        return this.values;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String _type) {
        this.type = _type;
    }

    public ArrayList<String> getColumns() {
        return this.columns;
    }

    public void setColumns(ArrayList<String> _columns) {
        this.columns = _columns;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String _tableName) {
        this.tableName = _tableName;
    }
}
