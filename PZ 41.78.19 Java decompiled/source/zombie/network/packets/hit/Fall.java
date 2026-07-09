// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.HitReactionNetworkAI;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;

public class Fall implements INetworkPacket {
    protected float dropPositionX;
    protected float dropPositionY;
    protected byte dropPositionZ;
    protected float dropDirection;

    public void set(HitReactionNetworkAI hitReactionNetworkAI) {
        this.dropPositionX = hitReactionNetworkAI.finalPosition.x;
        this.dropPositionY = hitReactionNetworkAI.finalPosition.y;
        this.dropPositionZ = hitReactionNetworkAI.finalPositionZ;
        this.dropDirection = hitReactionNetworkAI.finalDirection.getDirection();
    }

    public void set(float float0, float float1, byte byte0, float float2) {
        this.dropPositionX = float0;
        this.dropPositionY = float1;
        this.dropPositionZ = byte0;
        this.dropDirection = float2;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.dropPositionX = byteBuffer.getFloat();
        this.dropPositionY = byteBuffer.getFloat();
        this.dropPositionZ = byteBuffer.get();
        this.dropDirection = byteBuffer.getFloat();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putFloat(this.dropPositionX);
        byteBufferWriter.putFloat(this.dropPositionY);
        byteBufferWriter.putByte(this.dropPositionZ);
        byteBufferWriter.putFloat(this.dropDirection);
    }

    @Override
    public String getDescription() {
        return "\n\tFall [ direction="
            + this.dropDirection
            + " | position=( "
            + this.dropPositionX
            + " ; "
            + this.dropPositionY
            + " ; "
            + this.dropPositionZ
            + " ) ]";
    }

    public void process(IsoGameCharacter character) {
        if (this.isSetup() && character.getHitReactionNetworkAI() != null) {
            character.getHitReactionNetworkAI().process(this.dropPositionX, this.dropPositionY, this.dropPositionZ, this.dropDirection);
        }
    }

    boolean isSetup() {
        return this.dropPositionX != 0.0F && this.dropPositionY != 0.0F;
    }
}
