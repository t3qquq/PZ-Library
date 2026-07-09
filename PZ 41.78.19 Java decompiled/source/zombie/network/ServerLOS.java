// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.util.ArrayList;
import zombie.characters.IsoPlayer;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoGridSquare;
import zombie.iso.LosUtil;

public class ServerLOS {
    public static ServerLOS instance;
    private ServerLOS.LOSThread thread;
    private ArrayList<ServerLOS.PlayerData> playersMain = new ArrayList<>();
    private ArrayList<ServerLOS.PlayerData> playersLOS = new ArrayList<>();
    private boolean bMapLoading = false;
    private boolean bSuspended = false;
    boolean bWasSuspended;

    private void noise(String var1) {
    }

    public static void init() {
        instance = new ServerLOS();
        instance.start();
    }

    public void start() {
        this.thread = new ServerLOS.LOSThread();
        this.thread.setName("LOS");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void addPlayer(IsoPlayer player) {
        synchronized (this.playersMain) {
            if (this.findData(player) == null) {
                ServerLOS.PlayerData playerData = new ServerLOS.PlayerData(player);
                this.playersMain.add(playerData);
                synchronized (this.thread.notifier) {
                    this.thread.notifier.notify();
                }
            }
        }
    }

    public void removePlayer(IsoPlayer player) {
        synchronized (this.playersMain) {
            ServerLOS.PlayerData playerData = this.findData(player);
            this.playersMain.remove(playerData);
            synchronized (this.thread.notifier) {
                this.thread.notifier.notify();
            }
        }
    }

    public boolean isCouldSee(IsoPlayer player, IsoGridSquare square) {
        ServerLOS.PlayerData playerData = this.findData(player);
        if (playerData != null) {
            int int0 = square.x - playerData.px + 50;
            int int1 = square.y - playerData.py + 50;
            if (int0 >= 0 && int0 < 100 && int1 >= 0 && int1 < 100) {
                return playerData.visible[int0][int1][square.z];
            }
        }

        return false;
    }

    public void doServerZombieLOS(IsoPlayer player) {
        if (ServerMap.instance.bUpdateLOSThisFrame) {
            ServerLOS.PlayerData playerData = this.findData(player);
            if (playerData != null) {
                if (playerData.status == ServerLOS.UpdateStatus.NeverDone) {
                    playerData.status = ServerLOS.UpdateStatus.ReadyInMain;
                }

                if (playerData.status == ServerLOS.UpdateStatus.ReadyInMain) {
                    playerData.status = ServerLOS.UpdateStatus.WaitingInLOS;
                    this.noise("WaitingInLOS playerID=" + player.OnlineID);
                    synchronized (this.thread.notifier) {
                        this.thread.notifier.notify();
                    }
                }
            }
        }
    }

    public void updateLOS(IsoPlayer player) {
        ServerLOS.PlayerData playerData = this.findData(player);
        if (playerData != null) {
            if (playerData.status == ServerLOS.UpdateStatus.ReadyInLOS || playerData.status == ServerLOS.UpdateStatus.ReadyInMain) {
                if (playerData.status == ServerLOS.UpdateStatus.ReadyInLOS) {
                    this.noise("BusyInMain playerID=" + player.OnlineID);
                }

                playerData.status = ServerLOS.UpdateStatus.BusyInMain;
                player.updateLOS();
                playerData.status = ServerLOS.UpdateStatus.ReadyInMain;
                synchronized (this.thread.notifier) {
                    this.thread.notifier.notify();
                }
            }
        }
    }

    private ServerLOS.PlayerData findData(IsoPlayer player) {
        for (int int0 = 0; int0 < this.playersMain.size(); int0++) {
            if (this.playersMain.get(int0).player == player) {
                return this.playersMain.get(int0);
            }
        }

        return null;
    }

    public void suspend() {
        this.bMapLoading = true;
        this.bWasSuspended = this.bSuspended;

        while (!this.bSuspended) {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException interruptedException) {
            }
        }

        if (!this.bWasSuspended) {
            this.noise("suspend **********");
        }
    }

    public void resume() {
        this.bMapLoading = false;
        synchronized (this.thread.notifier) {
            this.thread.notifier.notify();
        }

        if (!this.bWasSuspended) {
            this.noise("resume **********");
        }
    }

    private class LOSThread extends Thread {
        public Object notifier = new Object();

        @Override
        public void run() {
            while (true) {
                try {
                    this.runInner();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        private void runInner() {
            MPStatistic.getInstance().ServerLOS.Start();
            synchronized (ServerLOS.this.playersMain) {
                ServerLOS.this.playersLOS.clear();
                ServerLOS.this.playersLOS.addAll(ServerLOS.this.playersMain);
            }

            for (int int0 = 0; int0 < ServerLOS.this.playersLOS.size(); int0++) {
                ServerLOS.PlayerData playerData = ServerLOS.this.playersLOS.get(int0);
                if (playerData.status == ServerLOS.UpdateStatus.WaitingInLOS) {
                    playerData.status = ServerLOS.UpdateStatus.BusyInLOS;
                    ServerLOS.this.noise("BusyInLOS playerID=" + playerData.player.OnlineID);
                    this.calcLOS(playerData);
                    playerData.status = ServerLOS.UpdateStatus.ReadyInLOS;
                }

                if (ServerLOS.this.bMapLoading) {
                    break;
                }
            }

            MPStatistic.getInstance().ServerLOS.End();

            while (this.shouldWait()) {
                ServerLOS.this.bSuspended = true;
                synchronized (this.notifier) {
                    try {
                        this.notifier.wait();
                    } catch (InterruptedException interruptedException) {
                    }
                }
            }

            ServerLOS.this.bSuspended = false;
        }

        private void calcLOS(ServerLOS.PlayerData playerData) {
            boolean boolean0 = false;
            if (playerData.px == (int)playerData.player.getX()
                && playerData.py == (int)playerData.player.getY()
                && playerData.pz == (int)playerData.player.getZ()) {
                boolean0 = true;
            }

            playerData.px = (int)playerData.player.getX();
            playerData.py = (int)playerData.player.getY();
            playerData.pz = (int)playerData.player.getZ();
            playerData.player.initLightInfo2();
            if (!boolean0) {
                byte byte0 = 0;

                for (int int0 = 0; int0 < LosUtil.XSIZE; int0++) {
                    for (int int1 = 0; int1 < LosUtil.YSIZE; int1++) {
                        for (int int2 = 0; int2 < LosUtil.ZSIZE; int2++) {
                            LosUtil.cachedresults[int0][int1][int2][byte0] = 0;
                        }
                    }
                }

                try {
                    IsoPlayer.players[byte0] = playerData.player;
                    int int3 = playerData.px;
                    int int4 = playerData.py;

                    for (int int5 = -50; int5 < 50; int5++) {
                        for (int int6 = -50; int6 < 50; int6++) {
                            for (int int7 = 0; int7 < 8; int7++) {
                                IsoGridSquare square = ServerMap.instance.getGridSquare(int5 + int3, int6 + int4, int7);
                                if (square != null) {
                                    square.CalcVisibility(byte0);
                                    playerData.visible[int5 + 50][int6 + 50][int7] = square.isCouldSee(byte0);
                                    square.checkRoomSeen(byte0);
                                }
                            }
                        }
                    }
                } finally {
                    IsoPlayer.players[byte0] = null;
                }
            }
        }

        private boolean shouldWait() {
            if (ServerLOS.this.bMapLoading) {
                return true;
            } else {
                for (int int0 = 0; int0 < ServerLOS.this.playersLOS.size(); int0++) {
                    ServerLOS.PlayerData playerData = ServerLOS.this.playersLOS.get(int0);
                    if (playerData.status == ServerLOS.UpdateStatus.WaitingInLOS) {
                        return false;
                    }
                }

                synchronized (ServerLOS.this.playersMain) {
                    return ServerLOS.this.playersLOS.size() == ServerLOS.this.playersMain.size();
                }
            }
        }
    }

    private class PlayerData {
        public IsoPlayer player;
        public ServerLOS.UpdateStatus status = ServerLOS.UpdateStatus.NeverDone;
        public int px;
        public int py;
        public int pz;
        public boolean[][][] visible = new boolean[100][100][8];

        public PlayerData(IsoPlayer playerx) {
            this.player = playerx;
        }
    }

    public static final class ServerLighting implements IsoGridSquare.ILighting {
        private static final byte LOS_SEEN = 1;
        private static final byte LOS_COULD_SEE = 2;
        private static final byte LOS_CAN_SEE = 4;
        private static ColorInfo lightInfo = new ColorInfo();
        private byte los;

        @Override
        public int lightverts(int var1) {
            return 0;
        }

        @Override
        public float lampostTotalR() {
            return 0.0F;
        }

        @Override
        public float lampostTotalG() {
            return 0.0F;
        }

        @Override
        public float lampostTotalB() {
            return 0.0F;
        }

        @Override
        public boolean bSeen() {
            return (this.los & 1) != 0;
        }

        @Override
        public boolean bCanSee() {
            return (this.los & 4) != 0;
        }

        @Override
        public boolean bCouldSee() {
            return (this.los & 2) != 0;
        }

        @Override
        public float darkMulti() {
            return 0.0F;
        }

        @Override
        public float targetDarkMulti() {
            return 0.0F;
        }

        @Override
        public ColorInfo lightInfo() {
            lightInfo.r = 1.0F;
            lightInfo.g = 1.0F;
            lightInfo.b = 1.0F;
            return lightInfo;
        }

        @Override
        public void lightverts(int var1, int var2) {
        }

        @Override
        public void lampostTotalR(float var1) {
        }

        @Override
        public void lampostTotalG(float var1) {
        }

        @Override
        public void lampostTotalB(float var1) {
        }

        @Override
        public void bSeen(boolean boolean0) {
            if (boolean0) {
                this.los = (byte)(this.los | 1);
            } else {
                this.los &= -2;
            }
        }

        @Override
        public void bCanSee(boolean boolean0) {
            if (boolean0) {
                this.los = (byte)(this.los | 4);
            } else {
                this.los &= -5;
            }
        }

        @Override
        public void bCouldSee(boolean boolean0) {
            if (boolean0) {
                this.los = (byte)(this.los | 2);
            } else {
                this.los &= -3;
            }
        }

        @Override
        public void darkMulti(float var1) {
        }

        @Override
        public void targetDarkMulti(float var1) {
        }

        @Override
        public int resultLightCount() {
            return 0;
        }

        @Override
        public IsoGridSquare.ResultLight getResultLight(int var1) {
            return null;
        }

        @Override
        public void reset() {
            this.los = 0;
        }
    }

    static enum UpdateStatus {
        NeverDone,
        WaitingInLOS,
        BusyInLOS,
        ReadyInLOS,
        BusyInMain,
        ReadyInMain;
    }
}
