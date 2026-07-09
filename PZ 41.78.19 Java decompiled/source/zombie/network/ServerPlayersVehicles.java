// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import zombie.core.logger.ExceptionLogger;
import zombie.savefile.ServerPlayerDB;
import zombie.vehicles.VehiclesDB2;

public class ServerPlayersVehicles {
    public static final ServerPlayersVehicles instance = new ServerPlayersVehicles();
    private ServerPlayersVehicles.SPVThread m_thread = null;

    public void init() {
        this.m_thread = new ServerPlayersVehicles.SPVThread();
        this.m_thread.setName("ServerPlayersVehicles");
        this.m_thread.setDaemon(true);
        this.m_thread.start();
    }

    public void stop() {
        if (this.m_thread != null) {
            this.m_thread.m_bStop = true;

            while (this.m_thread.isAlive()) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException interruptedException) {
                }
            }

            this.m_thread = null;
        }
    }

    private static final class SPVThread extends Thread {
        boolean m_bStop = false;

        @Override
        public void run() {
            while (!this.m_bStop) {
                try {
                    this.runInner();
                } catch (Throwable throwable) {
                    ExceptionLogger.logException(throwable);
                }
            }
        }

        void runInner() {
            ServerPlayerDB.getInstance().process();
            VehiclesDB2.instance.updateWorldStreamer();

            try {
                Thread.sleep(500L);
            } catch (InterruptedException interruptedException) {
            }
        }
    }
}
