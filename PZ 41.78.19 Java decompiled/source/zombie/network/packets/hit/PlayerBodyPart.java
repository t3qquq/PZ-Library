// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.packets.INetworkPacket;

public class PlayerBodyPart implements INetworkPacket {
    protected byte bodyPartIndex;
    protected BodyPart bodyPart;

    public void set(BodyPart bodyPartx) {
        if (bodyPartx == null) {
            this.bodyPartIndex = -1;
        } else {
            this.bodyPartIndex = (byte)bodyPartx.getIndex();
        }

        this.bodyPart = bodyPartx;
    }

    public void parse(ByteBuffer byteBuffer, IsoGameCharacter character) {
        boolean boolean0 = byteBuffer.get() == 1;
        if (boolean0) {
            this.bodyPartIndex = byteBuffer.get();
            if (character == null) {
                this.bodyPart = null;
            } else {
                this.bodyPart = character.getBodyDamage().getBodyPart(BodyPartType.FromIndex(this.bodyPartIndex));
            }
        } else {
            this.bodyPart = null;
        }
    }

    @Override
    public void parse(ByteBuffer var1, UdpConnection var2) {
        DebugLog.Multiplayer.error("PlayerBodyPart.parse is not implemented");
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        if (this.bodyPart == null) {
            byteBufferWriter.putByte((byte)0);
        } else {
            byteBufferWriter.putByte((byte)1);
            byteBufferWriter.putByte((byte)this.bodyPart.getIndex());
        }
    }

    @Override
    public String getDescription() {
        return "\n\tPlayerBodyPart [ Item=" + (this.bodyPart == null ? "?" : "\"" + this.bodyPart.getType().name() + "\"") + " ]";
    }

    public BodyPart getBodyPart() {
        return this.bodyPart;
    }
}
