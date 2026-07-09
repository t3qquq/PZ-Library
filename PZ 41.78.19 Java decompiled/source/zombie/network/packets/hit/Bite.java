// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;

public class Bite implements INetworkPacket {
    protected short flags;
    protected float hitDirection;

    public void set(IsoZombie zombie0) {
        this.flags = 0;
        this.flags = (short)(this.flags | (short)(zombie0.getEatBodyTarget() != null ? 1 : 0));
        this.flags = (short)(this.flags | (short)(zombie0.getVariableBoolean("AttackDidDamage") ? 2 : 0));
        this.flags = (short)(this.flags | (short)("BiteDefended".equals(zombie0.getHitReaction()) ? 4 : 0));
        this.flags = (short)(this.flags | (short)(zombie0.scratch ? 8 : 0));
        this.flags = (short)(this.flags | (short)(zombie0.laceration ? 16 : 0));
        this.hitDirection = zombie0.getHitDir().getDirection();
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.flags = byteBuffer.getShort();
        this.hitDirection = byteBuffer.getFloat();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.flags);
        byteBufferWriter.putFloat(this.hitDirection);
    }

    @Override
    public String getDescription() {
        return "\n\tBite [ eatBodyTarget="
            + ((this.flags & 1) != 0)
            + " | attackDidDamage="
            + ((this.flags & 2) != 0)
            + " | biteDefended="
            + ((this.flags & 4) != 0)
            + " | scratch="
            + ((this.flags & 8) != 0)
            + " | laceration="
            + ((this.flags & 16) != 0)
            + " | hitDirection="
            + this.hitDirection
            + " ]";
    }

    void process(IsoZombie zombie0, IsoGameCharacter character) {
        if ((this.flags & 4) == 0) {
            character.setAttackedBy(zombie0);
            if ((this.flags & 1) != 0 || character.isDead()) {
                zombie0.setEatBodyTarget(character, true);
                zombie0.setTarget(null);
            }

            if (character.isAsleep()) {
                if (GameServer.bServer) {
                    character.sendObjectChange("wakeUp");
                } else {
                    character.forceAwake();
                }
            }

            if ((this.flags & 2) != 0) {
                character.reportEvent("washit");
                character.setVariable("hitpvp", false);
            }

            zombie0.scratch = (this.flags & 8) != 0;
            zombie0.laceration = (this.flags & 8) != 0;
        }

        zombie0.getHitDir().setLengthAndDirection(this.hitDirection, 1.0F);
    }
}
