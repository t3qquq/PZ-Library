// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;

public abstract class Instance implements INetworkPacket {
    protected short ID;

    public void set(short short0) {
        this.ID = short0;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.ID = byteBuffer.getShort();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.ID);
    }

    @Override
    public boolean isConsistent() {
        return this.ID != -1;
    }

    @Override
    public String getDescription() {
        return "ID=" + this.ID;
    }
}
