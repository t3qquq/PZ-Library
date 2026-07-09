// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import zombie.debug.DebugLog;
import zombie.network.GameServer;

public class TimeDebugger {
    ArrayList<Long> records = new ArrayList<>();
    ArrayList<String> recordStrings = new ArrayList<>();
    String name = "";

    public TimeDebugger(String string) {
        this.name = string;
    }

    public void clear() {
        if (GameServer.bServer) {
            this.records.clear();
            this.recordStrings.clear();
        }
    }

    public void start() {
        if (GameServer.bServer) {
            this.records.clear();
            this.recordStrings.clear();
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add("Start");
        }
    }

    public void record() {
        if (GameServer.bServer) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add(String.valueOf(this.records.size()));
        }
    }

    public void record(String string) {
        if (GameServer.bServer) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add(string);
        }
    }

    public void recordTO(String string, int int0) {
        if (GameServer.bServer && this.records.get(this.records.size() - 1) - this.records.get(this.records.size() - 2) > int0) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add(string);
        }
    }

    public void add(TimeDebugger timeDebugger0) {
        if (GameServer.bServer) {
            String string = timeDebugger0.name;

            for (int int0 = 0; int0 < timeDebugger0.records.size(); int0++) {
                this.records.add(timeDebugger0.records.get(int0));
                this.recordStrings.add(string + "|" + timeDebugger0.recordStrings.get(int0));
            }

            timeDebugger0.clear();
        }
    }

    public void print() {
        if (GameServer.bServer) {
            this.records.add(System.currentTimeMillis());
            this.recordStrings.add("END");
            if (this.records.size() > 1) {
                DebugLog.log("=== DBG " + this.name + " ===");
                long long0 = this.records.get(0);

                for (int int0 = 1; int0 < this.records.size(); int0++) {
                    long long1 = this.records.get(int0 - 1);
                    long long2 = this.records.get(int0);
                    String string = this.recordStrings.get(int0);
                    DebugLog.log("RECORD " + int0 + " " + string + " A:" + (long2 - long0) + " D:" + (long2 - long1));
                }

                DebugLog.log("=== END " + this.name + " (" + (this.records.get(this.records.size() - 1) - long0) + ") ===");
            } else {
                DebugLog.log("<<< DBG " + this.name + " ERROR >>>");
            }
        }
    }

    public long getExecTime() {
        return this.records.size() == 0 ? 0L : System.currentTimeMillis() - this.records.get(0);
    }
}
