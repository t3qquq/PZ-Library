// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.spnetwork;

import java.nio.ByteBuffer;
import zombie.network.IZomboidPacket;

public final class ZomboidNetData implements IZomboidPacket {
    public short type;
    public short length;
    public ByteBuffer buffer;
    public UdpConnection connection;

    public ZomboidNetData() {
        this.buffer = ByteBuffer.allocate(2048);
    }

    public ZomboidNetData(int int0) {
        this.buffer = ByteBuffer.allocate(int0);
    }

    public void reset() {
        this.type = 0;
        this.length = 0;
        this.buffer.clear();
        this.connection = null;
    }

    public void read(short short0, ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.type = short0;
        this.connection = udpConnection;
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
