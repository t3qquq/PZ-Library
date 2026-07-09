// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoWindow;
import zombie.network.packets.INetworkPacket;

public class HitInfo implements INetworkPacket {
    public MovingObject object;
    public NetObject window;
    public float x;
    public float y;
    public float z;
    public float dot;
    public float distSq;
    public int chance = 0;

    public HitInfo() {
        this.object = new MovingObject();
        this.window = new NetObject();
    }

    public HitInfo init(IsoMovingObject movingObject, float float3, float float4, float float0, float float1, float float2) {
        this.object = new MovingObject();
        this.window = new NetObject();
        this.object.setMovingObject(movingObject);
        this.window.setObject(null);
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.dot = float3;
        this.distSq = float4;
        return this;
    }

    public HitInfo init(IsoWindow windowx, float float0, float float1) {
        this.object = new MovingObject();
        this.window = new NetObject();
        this.object.setMovingObject(null);
        this.window.setObject(windowx);
        this.z = windowx.getZ();
        this.dot = float0;
        this.distSq = float1;
        return this;
    }

    public IsoMovingObject getObject() {
        return this.object.getMovingObject();
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.object.parse(byteBuffer, udpConnection);
        this.window.parse(byteBuffer, udpConnection);
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.getFloat();
        this.dot = byteBuffer.getFloat();
        this.distSq = byteBuffer.getFloat();
        this.chance = byteBuffer.getInt();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        this.object.write(byteBufferWriter);
        this.window.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putFloat(this.z);
        byteBufferWriter.putFloat(this.dot);
        byteBufferWriter.putFloat(this.distSq);
        byteBufferWriter.putInt(this.chance);
    }

    @Override
    public int getPacketSizeBytes() {
        return 24 + this.object.getPacketSizeBytes() + this.window.getPacketSizeBytes();
    }

    @Override
    public String getDescription() {
        return "\n\tHitInfo [ x="
            + this.x
            + " y="
            + this.y
            + " z="
            + this.z
            + " dot="
            + this.dot
            + " distSq="
            + this.distSq
            + " chance="
            + this.chance
            + "\n\t Object: "
            + this.object.getDescription()
            + "\n\t Window: "
            + this.window.getDescription()
            + " ]";
    }
}
