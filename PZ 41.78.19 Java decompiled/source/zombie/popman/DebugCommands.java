// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import zombie.core.network.ByteBufferWriter;
import zombie.network.GameClient;
import zombie.network.PacketTypes;

final class DebugCommands {
    protected static final byte PKT_LOADED = 1;
    protected static final byte PKT_REPOP = 2;
    protected static final byte PKT_SPAWN_TIME_TO_ZERO = 3;
    protected static final byte PKT_CLEAR_ZOMBIES = 4;
    protected static final byte PKT_SPAWN_NOW = 5;

    private static native void n_debugCommand(int var0, int var1, int var2);

    public void SpawnTimeToZero(int int0, int int1) {
        if (ZombiePopulationManager.instance.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)3);
            byteBufferWriter.bb.putShort((short)int0);
            byteBufferWriter.bb.putShort((short)int1);
            PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
        } else {
            n_debugCommand(3, int0, int1);
        }
    }

    public void ClearZombies(int int0, int int1) {
        if (ZombiePopulationManager.instance.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)4);
            byteBufferWriter.bb.putShort((short)int0);
            byteBufferWriter.bb.putShort((short)int1);
            PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
        } else {
            n_debugCommand(4, int0, int1);
        }
    }

    public void SpawnNow(int int0, int int1) {
        if (ZombiePopulationManager.instance.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.KeepAlive.doPacket(byteBufferWriter);
            byteBufferWriter.bb.put((byte)5);
            byteBufferWriter.bb.putShort((short)int0);
            byteBufferWriter.bb.putShort((short)int1);
            PacketTypes.PacketType.KeepAlive.send(GameClient.connection);
        } else {
            n_debugCommand(5, int0, int1);
        }
    }
}
