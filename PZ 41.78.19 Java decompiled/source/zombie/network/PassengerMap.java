// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import org.joml.Vector3f;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.vehicles.BaseVehicle;

public final class PassengerMap {
    private static final int CHUNKS = 7;
    private static final int MAX_PASSENGERS = 16;
    private static final PassengerMap.PassengerLocal[] perPlayerPngr = new PassengerMap.PassengerLocal[4];
    private static final PassengerMap.DriverLocal[] perPlayerDriver = new PassengerMap.DriverLocal[4];

    public static void updatePassenger(IsoPlayer player) {
        if (player != null && player.getVehicle() != null && !player.getVehicle().isDriver(player)) {
            IsoGameCharacter character = player.getVehicle().getDriver();
            if (character instanceof IsoPlayer && !((IsoPlayer)character).isLocalPlayer()) {
                PassengerMap.PassengerLocal passengerLocal = perPlayerPngr[player.PlayerIndex];
                passengerLocal.chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[player.PlayerIndex];
                passengerLocal.updateLoaded();
            }
        }
    }

    public static void serverReceivePacket(ByteBuffer byteBuffer, UdpConnection udpConnection0) {
        byte byte0 = byteBuffer.get();
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        long long0 = byteBuffer.getLong();
        IsoPlayer player = udpConnection0.players[byte0];
        if (player != null && player.getVehicle() != null) {
            IsoGameCharacter character = player.getVehicle().getDriver();
            if (character instanceof IsoPlayer && character != player) {
                UdpConnection udpConnection1 = GameServer.getConnectionFromPlayer((IsoPlayer)character);
                if (udpConnection1 != null) {
                    ByteBufferWriter byteBufferWriter = udpConnection1.startPacket();
                    PacketTypes.PacketType.PassengerMap.doPacket(byteBufferWriter);
                    byteBufferWriter.putShort(player.getVehicle().VehicleID);
                    byteBufferWriter.putByte((byte)player.getVehicle().getSeat(player));
                    byteBufferWriter.putInt(int0);
                    byteBufferWriter.putInt(int1);
                    byteBufferWriter.putLong(long0);
                    PacketTypes.PacketType.PassengerMap.send(udpConnection1);
                }
            }
        }
    }

    public static void clientReceivePacket(ByteBuffer byteBuffer) {
        short short0 = byteBuffer.getShort();
        byte byte0 = byteBuffer.get();
        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        long long0 = byteBuffer.getLong();

        for (int int2 = 0; int2 < IsoPlayer.numPlayers; int2++) {
            IsoPlayer player = IsoPlayer.players[int2];
            if (player != null && player.getVehicle() != null) {
                BaseVehicle vehicle = player.getVehicle();
                if (vehicle.VehicleID == short0 && vehicle.isDriver(player)) {
                    PassengerMap.DriverLocal driverLocal = perPlayerDriver[int2];
                    PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[byte0];
                    if (passengerRemote == null) {
                        passengerRemote = driverLocal.passengers[byte0] = new PassengerMap.PassengerRemote();
                    }

                    passengerRemote.setLoaded(int0, int1, long0);
                }
            }
        }
    }

    public static boolean isChunkLoaded(BaseVehicle vehicle, int int1, int int0) {
        if (!GameClient.bClient) {
            return false;
        } else if (vehicle != null && int1 >= 0 && int0 >= 0) {
            IsoGameCharacter character0 = vehicle.getDriver();
            if (character0 instanceof IsoPlayer && ((IsoPlayer)character0).isLocalPlayer()) {
                int int2 = ((IsoPlayer)character0).PlayerIndex;
                PassengerMap.DriverLocal driverLocal = perPlayerDriver[int2];

                for (int int3 = 1; int3 < vehicle.getMaxPassengers(); int3++) {
                    PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[int3];
                    if (passengerRemote != null && passengerRemote.wx != -1) {
                        IsoGameCharacter character1 = vehicle.getCharacter(int3);
                        if (character1 instanceof IsoPlayer && !((IsoPlayer)character1).isLocalPlayer()) {
                            int int4 = passengerRemote.wx - 3;
                            int int5 = passengerRemote.wy - 3;
                            if (int1 >= int4
                                && int0 >= int5
                                && int1 < int4 + 7
                                && int0 < int5 + 7
                                && (passengerRemote.loaded & 1L << int1 - int4 + (int0 - int5) * 7) == 0L) {
                                return false;
                            }
                        } else {
                            passengerRemote.wx = -1;
                        }
                    }
                }

                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static void render(int int0) {
        if (GameClient.bClient) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null && player.getVehicle() != null) {
                BaseVehicle vehicle = player.getVehicle();
                int int1 = Core.TileScale;
                byte byte0 = 10;
                float float0 = 0.1F;
                float float1 = 0.1F;
                float float2 = 0.1F;
                float float3 = 0.75F;
                float float4 = 0.0F;
                PassengerMap.DriverLocal driverLocal = perPlayerDriver[int0];

                for (int int2 = 1; int2 < vehicle.getMaxPassengers(); int2++) {
                    PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[int2];
                    if (passengerRemote != null && passengerRemote.wx != -1) {
                        IsoGameCharacter character = vehicle.getCharacter(int2);
                        if (character instanceof IsoPlayer && !((IsoPlayer)character).isLocalPlayer()) {
                            for (int int3 = 0; int3 < 7; int3++) {
                                for (int int4 = 0; int4 < 7; int4++) {
                                    boolean boolean0 = (passengerRemote.loaded & 1L << int4 + int3 * 7) != 0L;
                                    if (!boolean0) {
                                        float float5 = (passengerRemote.wx - 3 + int4) * byte0;
                                        float float6 = (passengerRemote.wy - 3 + int3) * byte0;
                                        float float7 = IsoUtils.XToScreenExact(float5, float6 + byte0, float4, 0);
                                        float float8 = IsoUtils.YToScreenExact(float5, float6 + byte0, float4, 0);
                                        SpriteRenderer.instance
                                            .renderPoly(
                                                (int)float7,
                                                (int)float8,
                                                (int)(float7 + byte0 * 64 / 2 * int1),
                                                (int)(float8 - byte0 * 32 / 2 * int1),
                                                (int)(float7 + byte0 * 64 * int1),
                                                (int)float8,
                                                (int)(float7 + byte0 * 64 / 2 * int1),
                                                (int)(float8 + byte0 * 32 / 2 * int1),
                                                float0,
                                                float1,
                                                float2,
                                                float3
                                            );
                                    }
                                }
                            }
                        } else {
                            passengerRemote.wx = -1;
                        }
                    }
                }
            }
        }
    }

    public static void Reset() {
        for (int int0 = 0; int0 < 4; int0++) {
            PassengerMap.PassengerLocal passengerLocal = perPlayerPngr[int0];
            passengerLocal.wx = -1;
            PassengerMap.DriverLocal driverLocal = perPlayerDriver[int0];

            for (int int1 = 0; int1 < 16; int1++) {
                PassengerMap.PassengerRemote passengerRemote = driverLocal.passengers[int1];
                if (passengerRemote != null) {
                    passengerRemote.wx = -1;
                }
            }
        }
    }

    static {
        for (int int0 = 0; int0 < 4; int0++) {
            perPlayerPngr[int0] = new PassengerMap.PassengerLocal(int0);
            perPlayerDriver[int0] = new PassengerMap.DriverLocal();
        }
    }

    private static final class DriverLocal {
        final PassengerMap.PassengerRemote[] passengers = new PassengerMap.PassengerRemote[16];
    }

    private static final class PassengerLocal {
        final int playerIndex;
        IsoChunkMap chunkMap;
        int wx = -1;
        int wy = -1;
        long loaded = 0L;

        PassengerLocal(int int0) {
            this.playerIndex = int0;
        }

        boolean setLoaded() {
            int int0 = this.chunkMap.WorldX;
            int int1 = this.chunkMap.WorldY;
            Vector3f vector3f = IsoPlayer.players[this.playerIndex].getVehicle().jniLinearVelocity;
            float float0 = Math.abs(vector3f.x);
            float float1 = Math.abs(vector3f.z);
            boolean boolean0 = vector3f.x < 0.0F && float0 > float1;
            boolean boolean1 = vector3f.x > 0.0F && float0 > float1;
            boolean boolean2 = vector3f.z < 0.0F && float1 > float0;
            boolean boolean3 = vector3f.z > 0.0F && float1 > float0;
            if (boolean1) {
                int0++;
            } else if (boolean0) {
                int0--;
            } else if (boolean2) {
                int1--;
            } else if (boolean3) {
                int1++;
            }

            long long0 = 0L;

            for (int int2 = 0; int2 < 7; int2++) {
                for (int int3 = 0; int3 < 7; int3++) {
                    IsoChunk chunk = this.chunkMap.getChunk(IsoChunkMap.ChunkGridWidth / 2 - 3 + int3, IsoChunkMap.ChunkGridWidth / 2 - 3 + int2);
                    if (chunk != null && chunk.bLoaded) {
                        long0 |= 1L << int3 + int2 * 7;
                    }
                }
            }

            boolean boolean4 = int0 != this.wx || int1 != this.wy || long0 != this.loaded;
            if (boolean4) {
                this.wx = int0;
                this.wy = int1;
                this.loaded = long0;
            }

            return boolean4;
        }

        void updateLoaded() {
            if (this.setLoaded()) {
                this.clientSendPacket(GameClient.connection);
            }
        }

        void clientSendPacket(UdpConnection udpConnection) {
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.PassengerMap.doPacket(byteBufferWriter);
            byteBufferWriter.putByte((byte)this.playerIndex);
            byteBufferWriter.putInt(this.wx);
            byteBufferWriter.putInt(this.wy);
            byteBufferWriter.putLong(this.loaded);
            PacketTypes.PacketType.PassengerMap.send(udpConnection);
        }
    }

    private static final class PassengerRemote {
        int wx = -1;
        int wy = -1;
        long loaded = 0L;

        void setLoaded(int int0, int int1, long long0) {
            this.wx = int0;
            this.wy = int1;
            this.loaded = long0;
        }
    }
}
