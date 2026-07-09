// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.MPStatisticClient;
import zombie.network.NetworkVariables;
import zombie.network.packets.PlayerPacket;

public class NetworkTeleport {
    public static boolean enable = true;
    public static boolean enableInstantTeleport = true;
    private NetworkTeleport.Type teleportType = NetworkTeleport.Type.none;
    private IsoGameCharacter character = null;
    private boolean setNewPos = false;
    private float nx = 0.0F;
    private float ny = 0.0F;
    private byte nz = 0;
    public float ndirection;
    private float tx = 0.0F;
    private float ty = 0.0F;
    private byte tz = 0;
    private long startTime;
    private long duration;

    public NetworkTeleport(IsoGameCharacter chr, NetworkTeleport.Type type, float x, float y, byte z, float _duration) {
        this.character = chr;
        this.setNewPos = false;
        this.nx = x;
        this.ny = y;
        this.nz = z;
        this.teleportType = type;
        this.startTime = System.currentTimeMillis();
        this.duration = (long)(1000.0 * _duration);
        chr.setTeleport(this);
        if (Core.bDebug && chr.getNetworkCharacterAI() != null && DebugOptions.instance.MultiplayerShowTeleport.getValue()) {
            chr.getNetworkCharacterAI()
                .setTeleportDebug(
                    new NetworkTeleport.NetworkTeleportDebug(chr.getOnlineID(), chr.x, chr.y, chr.z, x, y, z, chr.getNetworkCharacterAI().predictionType)
                );
        }
    }

    public void process(int playerIndex) {
        if (!enable) {
            this.character.setX(this.nx);
            this.character.setY(this.ny);
            this.character.setZ(this.nz);
            this.character.ensureOnTile();
            this.character.setTeleport(null);
            this.character = null;
        } else {
            boolean boolean0 = this.character.getCurrentSquare().isCanSee(playerIndex);
            float float0 = Math.min(1.0F, (float)(System.currentTimeMillis() - this.startTime) / (float)this.duration);
            switch (this.teleportType) {
                case disappearing:
                    if (float0 < 0.99F) {
                        this.character.setAlpha(playerIndex, Math.min(this.character.getAlpha(playerIndex), 1.0F - float0));
                    } else {
                        this.stop(playerIndex);
                    }
                    break;
                case teleportation:
                    if (float0 < 0.5F) {
                        if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
                            this.character.setAlpha(playerIndex, Math.min(this.character.getAlpha(playerIndex), 1.0F - float0 * 2.0F));
                        }
                    } else if (float0 < 0.99F) {
                        if (!this.setNewPos) {
                            this.setNewPos = true;
                            this.character.setX(this.nx);
                            this.character.setY(this.ny);
                            this.character.setZ(this.nz);
                            this.character.ensureOnTile();
                            this.character.getNetworkCharacterAI().resetSpeedLimiter();
                        }

                        if (this.character.isoPlayer == null || this.character.isoPlayer != null && this.character.isoPlayer.spottedByPlayer) {
                            this.character.setAlpha(playerIndex, Math.min(this.character.getTargetAlpha(playerIndex), (float0 - 0.5F) * 2.0F));
                        }
                    } else {
                        this.stop(playerIndex);
                    }
                    break;
                case materialization:
                    if (float0 < 0.99F) {
                        this.character.setAlpha(playerIndex, Math.min(this.character.getTargetAlpha(playerIndex), float0));
                    } else {
                        this.stop(playerIndex);
                    }
            }
        }
    }

    public void stop(int playerIndex) {
        this.character.setTeleport(null);
        switch (this.teleportType) {
            case disappearing:
                this.character.setAlpha(playerIndex, Math.min(this.character.getAlpha(playerIndex), 0.0F));
            default:
                this.character = null;
        }
    }

    public static boolean teleport(IsoGameCharacter chr, NetworkTeleport.Type type, float x, float y, byte z, float _duration) {
        if (!enable) {
            return false;
        } else {
            if (chr.getCurrentSquare() != null && enableInstantTeleport) {
                boolean boolean0 = false;

                for (int int0 = 0; int0 < 4; int0++) {
                    if (chr.getCurrentSquare().isCanSee(int0)) {
                        boolean0 = true;
                        break;
                    }
                }

                IsoGridSquare square = LuaManager.GlobalObject.getCell().getGridSquare((int)x, (int)y, z);
                if (square != null) {
                    for (int int1 = 0; int1 < 4; int1++) {
                        if (square.isCanSee(int1)) {
                            boolean0 = true;
                            break;
                        }
                    }
                }

                if (!boolean0) {
                    chr.setX(x);
                    chr.setY(y);
                    chr.setZ(z);
                    chr.ensureOnTile();
                    return false;
                }
            }

            if (!chr.isTeleporting()) {
                if (chr instanceof IsoZombie) {
                    MPStatisticClient.getInstance().incrementZombiesTeleports();
                } else {
                    MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
                }

                new NetworkTeleport(chr, type, x, y, z, _duration);
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean teleport(IsoGameCharacter chr, PlayerPacket packet, float _duration) {
        if (!enable) {
            return false;
        } else {
            if (LuaManager.GlobalObject.getCell().getGridSquare((int)packet.x, (int)packet.y, packet.z) == null) {
                chr.setX(packet.x);
                chr.setY(packet.y);
                chr.setZ(packet.z);
                chr.realx = packet.realx;
                chr.realy = packet.realy;
                chr.realz = packet.realz;
                chr.realdir = IsoDirections.fromIndex(packet.realdir);
                chr.ensureOnTile();
            }

            if (chr.getCurrentSquare() != null && enableInstantTeleport) {
                boolean boolean0 = false;

                for (int int0 = 0; int0 < 4; int0++) {
                    if (chr.getCurrentSquare().isCanSee(int0)) {
                        boolean0 = true;
                        break;
                    }
                }

                IsoGridSquare square0 = LuaManager.GlobalObject.getCell().getGridSquare((int)packet.x, (int)packet.y, packet.z);
                if (square0 != null) {
                    for (int int1 = 0; int1 < 4; int1++) {
                        if (square0.isCanSee(int1)) {
                            boolean0 = true;
                            break;
                        }
                    }
                }

                if (!boolean0) {
                    chr.setX(packet.x);
                    chr.setY(packet.y);
                    chr.setZ(packet.z);
                    chr.ensureOnTile();
                    return false;
                }
            }

            if (!chr.isTeleporting()) {
                if (chr instanceof IsoZombie) {
                    MPStatisticClient.getInstance().incrementZombiesTeleports();
                } else {
                    MPStatisticClient.getInstance().incrementRemotePlayersTeleports();
                }

                IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare((double)chr.x, (double)chr.y, (double)chr.z);
                if (square1 == null) {
                    IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)packet.realx, (double)packet.realy, (double)packet.realz);
                    chr.setAlphaAndTarget(0.0F);
                    chr.setX(packet.realx);
                    chr.setY(packet.realy);
                    chr.setZ(packet.realz);
                    chr.ensureOnTile();
                    float float0 = 0.5F;
                    NetworkTeleport networkTeleport0 = new NetworkTeleport(
                        chr,
                        NetworkTeleport.Type.materialization,
                        float0 * packet.x + (1.0F - float0) * packet.realx,
                        float0 * packet.y + (1.0F - float0) * packet.realy,
                        (byte)(float0 * packet.z + (1.0F - float0) * packet.realz),
                        _duration
                    );
                    networkTeleport0.ndirection = packet.direction;
                    networkTeleport0.tx = packet.x;
                    networkTeleport0.ty = packet.y;
                    networkTeleport0.tz = packet.z;
                    return true;
                } else {
                    float float1 = 0.5F;
                    NetworkTeleport networkTeleport1 = new NetworkTeleport(
                        chr,
                        NetworkTeleport.Type.teleportation,
                        float1 * packet.x + (1.0F - float1) * packet.realx,
                        float1 * packet.y + (1.0F - float1) * packet.realy,
                        (byte)(float1 * packet.z + (1.0F - float1) * packet.realz),
                        _duration
                    );
                    networkTeleport1.ndirection = packet.direction;
                    networkTeleport1.tx = packet.x;
                    networkTeleport1.ty = packet.y;
                    networkTeleport1.tz = packet.z;
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public static void update(IsoGameCharacter chr, PlayerPacket packet) {
        if (chr.isTeleporting()) {
            NetworkTeleport networkTeleport = chr.getTeleport();
            if (networkTeleport.teleportType == NetworkTeleport.Type.teleportation) {
                float float0 = Math.min(1.0F, (float)(System.currentTimeMillis() - networkTeleport.startTime) / (float)networkTeleport.duration);
                if (float0 < 0.5F) {
                    float float1 = 0.5F;
                    networkTeleport.nx = float1 * packet.x + (1.0F - float1) * packet.realx;
                    networkTeleport.ny = float1 * packet.y + (1.0F - float1) * packet.realy;
                    networkTeleport.nz = (byte)(float1 * packet.z + (1.0F - float1) * packet.realz);
                }

                networkTeleport.ndirection = packet.direction;
                networkTeleport.tx = packet.x;
                networkTeleport.ty = packet.y;
                networkTeleport.tz = packet.z;
            }
        }
    }

    public static class NetworkTeleportDebug {
        short id;
        float nx;
        float ny;
        float nz;
        float lx;
        float ly;
        float lz;
        NetworkVariables.PredictionTypes type;

        public NetworkTeleportDebug(
            short short0, float float3, float float4, float float5, float float0, float float1, float float2, NetworkVariables.PredictionTypes predictionTypes
        ) {
            this.id = short0;
            this.nx = float0;
            this.ny = float1;
            this.nz = float2;
            this.lx = float3;
            this.ly = float4;
            this.lz = float5;
            this.type = predictionTypes;
        }

        public float getDistance() {
            return IsoUtils.DistanceTo(this.lx, this.ly, this.lz, this.nx, this.ny, this.nz);
        }
    }

    public static enum Type {
        none,
        disappearing,
        teleportation,
        materialization;
    }
}
