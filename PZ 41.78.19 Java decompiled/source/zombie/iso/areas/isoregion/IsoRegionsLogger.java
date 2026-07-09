// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Color;
import zombie.core.Core;
import zombie.debug.DebugLog;

/**
 * TurboTuTone.
 */
public class IsoRegionsLogger {
    private final ConcurrentLinkedQueue<IsoRegionsLogger.IsoRegionLog> pool = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<IsoRegionsLogger.IsoRegionLog> loggerQueue = new ConcurrentLinkedQueue<>();
    private final boolean consolePrint;
    private final ArrayList<IsoRegionsLogger.IsoRegionLog> logs = new ArrayList<>();
    private final int maxLogs = 100;
    private boolean isDirtyUI = false;

    public IsoRegionsLogger(boolean doConsolePrint) {
        this.consolePrint = doConsolePrint;
    }

    public ArrayList<IsoRegionsLogger.IsoRegionLog> getLogs() {
        return this.logs;
    }

    public boolean isDirtyUI() {
        return this.isDirtyUI;
    }

    public void unsetDirtyUI() {
        this.isDirtyUI = false;
    }

    private IsoRegionsLogger.IsoRegionLog getLog() {
        IsoRegionsLogger.IsoRegionLog regionLog = this.pool.poll();
        if (regionLog == null) {
            regionLog = new IsoRegionsLogger.IsoRegionLog();
        }

        return regionLog;
    }

    protected void log(String string) {
        this.log(string, null);
    }

    protected void log(String string, Color color) {
        if (Core.bDebug) {
            if (this.consolePrint) {
                DebugLog.IsoRegion.println(string);
            }

            IsoRegionsLogger.IsoRegionLog regionLog = this.getLog();
            regionLog.str = string;
            regionLog.type = IsoRegionLogType.Normal;
            regionLog.col = color;
            this.loggerQueue.offer(regionLog);
        }
    }

    protected void warn(String string) {
        DebugLog.IsoRegion.warn(string);
        if (Core.bDebug) {
            IsoRegionsLogger.IsoRegionLog regionLog = this.getLog();
            regionLog.str = string;
            regionLog.type = IsoRegionLogType.Warn;
            this.loggerQueue.offer(regionLog);
        }
    }

    protected void update() {
        if (Core.bDebug) {
            for (IsoRegionsLogger.IsoRegionLog regionLog0 = this.loggerQueue.poll(); regionLog0 != null; regionLog0 = this.loggerQueue.poll()) {
                if (this.logs.size() >= 100) {
                    IsoRegionsLogger.IsoRegionLog regionLog1 = this.logs.remove(0);
                    regionLog1.col = null;
                    this.pool.offer(regionLog1);
                }

                this.logs.add(regionLog0);
                this.isDirtyUI = true;
            }
        }
    }

    public static class IsoRegionLog {
        private String str;
        private IsoRegionLogType type;
        private Color col;

        public String getStr() {
            return this.str;
        }

        public IsoRegionLogType getType() {
            return this.type;
        }

        public Color getColor() {
            if (this.col != null) {
                return this.col;
            } else {
                return this.type == IsoRegionLogType.Warn ? Color.red : Color.white;
            }
        }
    }
}
