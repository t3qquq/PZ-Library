// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;

public class ZomboidNetData implements IZomboidPacket {
    public PacketTypes.PacketType type;
    public short length;
    public ByteBuffer buffer;
    public long connection;
    public long time;

    public ZomboidNetData() {
        this.buffer = ByteBuffer.allocate(2048);
    }

    public ZomboidNetData(int int0) {
        this.buffer = ByteBuffer.allocate(int0);
    }

    public void reset() {
        this.type = null;
        this.length = 0;
        this.connection = 0L;
        this.buffer.clear();
    }

    public void read(short short0, ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.type = PacketTypes.packetTypes.get(short0);
        if (this.type == null) {
            DebugLog.Multiplayer.error("Received unknown packet id=%d", short0);
        }

        this.connection = udpConnection.getConnectedGUID();
        this.buffer.put(byteBuffer);
        this.buffer.flip();
    }

    @Override
    public boolean isConnect() {
        return false;
    }

    @Override
    public boolean isDisconnect() {
        return false;
    }
}
