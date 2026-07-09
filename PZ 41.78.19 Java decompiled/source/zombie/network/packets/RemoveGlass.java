// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.network.GameClient;
import zombie.network.PacketValidator;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.PlayerBodyPart;

public class RemoveGlass implements INetworkPacket {
    protected final Player wielder = new Player();
    protected final Player target = new Player();
    protected PlayerBodyPart bodyPart = new PlayerBodyPart();
    protected boolean handPain = false;

    public void set(IsoGameCharacter character0, IsoGameCharacter character1, BodyPart bodyPartx, boolean boolean0) {
        this.wielder.set(character0);
        this.target.set(character1);
        this.bodyPart.set(bodyPartx);
        this.handPain = boolean0;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.wielder.parse(byteBuffer, udpConnection);
        this.wielder.parsePlayer(udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.target.parsePlayer(null);
        this.bodyPart.parse(byteBuffer, this.target.getCharacter());
        byteBuffer.put((byte)(this.handPain ? 1 : 0));
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        this.wielder.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.bodyPart.write(byteBufferWriter);
        this.handPain = byteBufferWriter.bb.get() == 1;
    }

    public void process() {
        int int0 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
        if (!this.wielder.getPlayer().isAccessLevel("None")) {
            int0 = 10;
        }

        if (this.wielder.getCharacter().HasTrait("Hemophobic")) {
            this.wielder.getCharacter().getStats().setPanic(this.wielder.getCharacter().getStats().getPanic() + 50.0F);
        }

        this.wielder.getCharacter().getXp().AddXP(PerkFactory.Perks.Doctor, 15.0F);
        float float0 = 30 - int0;
        this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + float0);
        if (this.handPain) {
            this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + 30.0F);
        }

        this.bodyPart.getBodyPart().setHaveGlass(false);
    }

    @Override
    public boolean isConsistent() {
        return this.wielder.getCharacter() != null
            && this.wielder.getCharacter() instanceof IsoPlayer
            && this.target.getCharacter() != null
            && this.target.getCharacter() instanceof IsoPlayer
            && this.bodyPart.getBodyPart() != null;
    }

    public boolean validate(UdpConnection udpConnection) {
        if (GameClient.bClient && !this.bodyPart.getBodyPart().haveGlass()) {
            DebugLog.General.warn(this.getClass().getSimpleName() + ": Validate error: " + this.getDescription());
            return false;
        } else {
            return PacketValidator.checkShortDistance(udpConnection, this.wielder, this.target, this.getClass().getSimpleName());
        }
    }

    @Override
    public String getDescription() {
        String string = "\n\t" + this.getClass().getSimpleName() + " [";
        string = string + "wielder=" + this.wielder.getDescription() + " | ";
        string = string + "target=" + this.target.getDescription() + " | ";
        string = string + "bodyPart=" + this.bodyPart.getDescription() + " | ";
        return string + "handPain=" + this.handPain + "] ";
    }
}
