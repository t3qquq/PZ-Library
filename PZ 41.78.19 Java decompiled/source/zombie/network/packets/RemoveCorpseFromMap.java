// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.objects.IsoDeadBody;

public class RemoveCorpseFromMap implements INetworkPacket {
    private short id;
    private IsoDeadBody deadBody = null;

    public void set(IsoDeadBody deadBodyx) {
        this.id = deadBodyx.getObjectID();
        this.deadBody = deadBodyx;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.id = byteBuffer.getShort();
        this.deadBody = IsoDeadBody.getDeadBody(this.id);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
    }

    public void process() {
        if (this.isConsistent()) {
            IsoDeadBody.removeDeadBody(this.id);
        }
    }

    @Override
    public String getDescription() {
        return String.format(this.getClass().getSimpleName() + " id=%d", this.id);
    }

    @Override
    public boolean isConsistent() {
        return this.deadBody != null && this.deadBody.getSquare() != null;
    }

    public boolean isRelevant(UdpConnection udpConnection) {
        return udpConnection.RelevantTo(this.deadBody.getX(), this.deadBody.getY());
    }
}
