// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import zombie.SystemDisabler;
import zombie.ai.State;
import zombie.ai.states.ZombieEatBodyState;
import zombie.ai.states.ZombieIdleState;
import zombie.ai.states.ZombieSittingState;
import zombie.ai.states.ZombieTurnAlerted;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.util.Type;

public class NetworkZombieManager {
    private static final NetworkZombieManager instance = new NetworkZombieManager();
    private final NetworkZombieList owns = new NetworkZombieList();
    private static final float NospottedDistanceSquared = 16.0F;

    public static NetworkZombieManager getInstance() {
        return instance;
    }

    public int getAuthorizedZombieCount(UdpConnection udpConnection) {
        return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter(zombie0 -> zombie0.authOwner == udpConnection).count();
    }

    public int getUnauthorizedZombieCount() {
        return (int)IsoWorld.instance.CurrentCell.getZombieList().stream().filter(zombie0 -> zombie0.authOwner == null).count();
    }

    public static boolean canSpotted(IsoZombie zombie0) {
        if (zombie0.isRemoteZombie()) {
            return false;
        } else if (zombie0.target != null && IsoUtils.DistanceToSquared(zombie0.x, zombie0.y, zombie0.target.x, zombie0.target.y) < 16.0F) {
            return false;
        } else {
            State state = zombie0.getCurrentState();
            return state == null
                || state == ZombieIdleState.instance()
                || state == ZombieEatBodyState.instance()
                || state == ZombieSittingState.instance()
                || state == ZombieTurnAlerted.instance();
        }
    }

    public void updateAuth(IsoZombie zombie0) {
        if (GameServer.bServer) {
            if (System.currentTimeMillis() - zombie0.lastChangeOwner >= 2000L || zombie0.authOwner == null) {
                if (SystemDisabler.zombiesSwitchOwnershipEachUpdate && GameServer.getPlayerCount() > 1) {
                    if (zombie0.authOwner == null) {
                        for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                            UdpConnection udpConnection0 = GameServer.udpEngine.connections.get(int0);
                            if (udpConnection0 != null) {
                                this.moveZombie(zombie0, udpConnection0, null);
                                break;
                            }
                        }
                    } else {
                        int int1 = GameServer.udpEngine.connections.indexOf(zombie0.authOwner) + 1;

                        for (int int2 = 0; int2 < GameServer.udpEngine.connections.size(); int2++) {
                            UdpConnection udpConnection1 = GameServer.udpEngine.connections.get((int2 + int1) % GameServer.udpEngine.connections.size());
                            if (udpConnection1 != null) {
                                this.moveZombie(zombie0, udpConnection1, null);
                                break;
                            }
                        }
                    }
                } else {
                    if (zombie0.target instanceof IsoPlayer) {
                        UdpConnection udpConnection2 = GameServer.getConnectionFromPlayer((IsoPlayer)zombie0.target);
                        if (udpConnection2 != null && udpConnection2.isFullyConnected()) {
                            float float0 = ((IsoPlayer)zombie0.target).getRelevantAndDistance(zombie0.x, zombie0.y, udpConnection2.ReleventRange - 2);
                            if (!Float.isInfinite(float0)) {
                                this.moveZombie(zombie0, udpConnection2, (IsoPlayer)zombie0.target);
                                if (Core.bDebug) {
                                    DebugLog.log(
                                        DebugType.Ownership,
                                        String.format(
                                            "Zombie (%d) owner (\"%s\"): zombie has target", zombie0.getOnlineID(), ((IsoPlayer)zombie0.target).getUsername()
                                        )
                                    );
                                }

                                return;
                            }
                        }
                    }

                    UdpConnection udpConnection3 = zombie0.authOwner;
                    IsoPlayer player0 = zombie0.authOwnerPlayer;
                    float float1 = Float.POSITIVE_INFINITY;
                    if (udpConnection3 != null) {
                        float1 = udpConnection3.getRelevantAndDistance(zombie0.x, zombie0.y, zombie0.z);
                    }

                    for (int int3 = 0; int3 < GameServer.udpEngine.connections.size(); int3++) {
                        UdpConnection udpConnection4 = GameServer.udpEngine.connections.get(int3);
                        if (udpConnection4 != udpConnection3) {
                            for (IsoPlayer player1 : udpConnection4.players) {
                                if (player1 != null && player1.isAlive()) {
                                    float float2 = player1.getRelevantAndDistance(zombie0.x, zombie0.y, udpConnection4.ReleventRange - 2);
                                    if (!Float.isInfinite(float2) && (udpConnection3 == null || float1 > float2 * 1.618034F)) {
                                        udpConnection3 = udpConnection4;
                                        float1 = float2;
                                        player0 = player1;
                                    }
                                }
                            }
                        }
                    }

                    if (Core.bDebug && player0 != null && player0 != zombie0.authOwnerPlayer) {
                        DebugLog.log(
                            DebugType.Ownership, String.format("Zombie (%d) owner (\"%s\"): zombie is closer", zombie0.getOnlineID(), player0.getUsername())
                        );
                    }

                    if (udpConnection3 == null && zombie0.isReanimatedPlayer()) {
                        for (int int4 = 0; int4 < GameServer.udpEngine.connections.size(); int4++) {
                            UdpConnection udpConnection5 = GameServer.udpEngine.connections.get(int4);
                            if (udpConnection5 != udpConnection3) {
                                for (IsoPlayer player2 : udpConnection5.players) {
                                    if (player2 != null && player2.isDead() && player2.ReanimatedCorpse == zombie0) {
                                        udpConnection3 = udpConnection5;
                                        player0 = player2;
                                        if (Core.bDebug) {
                                            DebugLog.log(
                                                DebugType.Ownership,
                                                String.format("Zombie (%d) owner (\"%s\"): zombie is reanimated", zombie0.getOnlineID(), player2.getUsername())
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (udpConnection3 != null && !udpConnection3.RelevantTo(zombie0.x, zombie0.y, (udpConnection3.ReleventRange - 2) * 10)) {
                        udpConnection3 = null;
                    }

                    this.moveZombie(zombie0, udpConnection3, player0);
                }
            }
        }
    }

    public void moveZombie(IsoZombie zombie0, UdpConnection udpConnection, IsoPlayer player) {
        if (zombie0.isDead()) {
            if (zombie0.authOwner == null && zombie0.authOwnerPlayer == null) {
                zombie0.becomeCorpse();
            } else {
                synchronized (this.owns.lock) {
                    zombie0.authOwner = null;
                    zombie0.authOwnerPlayer = null;
                    zombie0.getNetworkCharacterAI().resetSpeedLimiter();
                }

                NetworkZombiePacker.getInstance().setExtraUpdate();
            }

            if (Core.bDebug) {
                DebugLog.log(
                    DebugType.Ownership,
                    String.format("Zombie (%d) owner (\"%s\" / null): zombie is dead", zombie0.getOnlineID(), player == null ? "" : player.getUsername())
                );
            }
        } else {
            if (player != null
                && player.getVehicle() != null
                && player.getVehicle().getSpeed2D() > 2.0F
                && player.getVehicle().getDriver() != player
                && player.getVehicle().getDriver() instanceof IsoPlayer) {
                player = (IsoPlayer)player.getVehicle().getDriver();
                udpConnection = GameServer.getConnectionFromPlayer(player);
                if (Core.bDebug) {
                    DebugLog.log(
                        DebugType.Ownership,
                        String.format("Zombie (%d) owner (\"%s\"): zombie owner is driver", zombie0.getOnlineID(), player == null ? "" : player.getUsername())
                    );
                }
            }

            if (zombie0.authOwner != udpConnection) {
                synchronized (this.owns.lock) {
                    if (zombie0.authOwner != null) {
                        NetworkZombieList.NetworkZombie networkZombie0 = this.owns.getNetworkZombie(zombie0.authOwner);
                        if (networkZombie0 != null && !networkZombie0.zombies.remove(zombie0)) {
                            DebugLog.log("moveZombie: There are no zombies in nz.zombies.");
                        }
                    }

                    if (udpConnection != null) {
                        NetworkZombieList.NetworkZombie networkZombie1 = this.owns.getNetworkZombie(udpConnection);
                        if (networkZombie1 != null) {
                            networkZombie1.zombies.add(zombie0);
                            zombie0.authOwner = udpConnection;
                            zombie0.authOwnerPlayer = player;
                            zombie0.getNetworkCharacterAI().resetSpeedLimiter();
                            udpConnection.timerSendZombie.reset(0L);
                        }
                    } else {
                        zombie0.authOwner = null;
                        zombie0.authOwnerPlayer = null;
                        zombie0.getNetworkCharacterAI().resetSpeedLimiter();
                    }
                }

                zombie0.lastChangeOwner = System.currentTimeMillis();
                NetworkZombiePacker.getInstance().setExtraUpdate();
            }
        }
    }

    public void getZombieAuth(UdpConnection udpConnection, ByteBuffer byteBuffer) {
        NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
        int int0 = networkZombie.zombies.size();
        int int1 = 0;
        int int2 = byteBuffer.position();
        byteBuffer.putShort((short)int0);
        synchronized (this.owns.lock) {
            networkZombie.zombies.removeIf(zombie0x -> zombie0x.OnlineID == -1);

            for (IsoZombie zombie0 : networkZombie.zombies) {
                if (zombie0.OnlineID != -1) {
                    byteBuffer.putShort(zombie0.OnlineID);
                    int1++;
                } else {
                    DebugLog.General.error("getZombieAuth: zombie.OnlineID == -1");
                }
            }
        }

        if (int1 < int0) {
            int int3 = byteBuffer.position();
            byteBuffer.position(int2);
            byteBuffer.putShort((short)int1);
            byteBuffer.position(int3);
        }
    }

    public LinkedList<IsoZombie> getZombieList(UdpConnection udpConnection) {
        NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
        return networkZombie.zombies;
    }

    public void clearTargetAuth(UdpConnection udpConnection, IsoPlayer player) {
        if (Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, "Clear zombies target and auth for player id=" + player.getOnlineID());
        }

        if (GameServer.bServer) {
            for (IsoZombie zombie0 : IsoWorld.instance.CurrentCell.getZombieList()) {
                if (zombie0.target == player) {
                    zombie0.setTarget(null);
                }

                if (zombie0.authOwner == udpConnection) {
                    zombie0.authOwner = null;
                    zombie0.authOwnerPlayer = null;
                    zombie0.getNetworkCharacterAI().resetSpeedLimiter();
                    getInstance().updateAuth(zombie0);
                }
            }
        }
    }

    public static void removeZombies(UdpConnection udpConnection) {
        int int0 = (IsoChunkMap.ChunkGridWidth / 2 + 2) * 10;

        for (IsoPlayer player : udpConnection.players) {
            if (player != null) {
                int int1 = (int)player.getX();
                int int2 = (int)player.getY();

                for (int int3 = 0; int3 < 8; int3++) {
                    for (int int4 = int2 - int0; int4 <= int2 + int0; int4++) {
                        for (int int5 = int1 - int0; int5 <= int1 + int0; int5++) {
                            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int5, int4, int3);
                            if (square != null && !square.getMovingObjects().isEmpty()) {
                                for (int int6 = square.getMovingObjects().size() - 1; int6 >= 0; int6--) {
                                    IsoZombie zombie0 = Type.tryCastTo(square.getMovingObjects().get(int6), IsoZombie.class);
                                    if (zombie0 != null) {
                                        NetworkZombiePacker.getInstance().deleteZombie(zombie0);
                                        zombie0.removeFromWorld();
                                        zombie0.removeFromSquare();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void recheck(UdpConnection udpConnection) {
        synchronized (this.owns.lock) {
            NetworkZombieList.NetworkZombie networkZombie = this.owns.getNetworkZombie(udpConnection);
            if (networkZombie != null) {
                networkZombie.zombies.removeIf(zombie0 -> zombie0.authOwner != udpConnection);
            }
        }
    }
}
