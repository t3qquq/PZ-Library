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

public class RemoveBullet implements INetworkPacket {
    protected final Player wielder = new Player();
    protected final Player target = new Player();
    protected PlayerBodyPart bodyPart = new PlayerBodyPart();

    public void set(IsoGameCharacter character0, IsoGameCharacter character1, BodyPart bodyPartx) {
        this.wielder.set(character0);
        this.target.set(character1);
        this.bodyPart.set(bodyPartx);
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.wielder.parse(byteBuffer, udpConnection);
        this.wielder.parsePlayer(udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.target.parsePlayer(null);
        this.bodyPart.parse(byteBuffer, this.target.getCharacter());
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        this.wielder.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.bodyPart.write(byteBufferWriter);
    }

    public void process() {
        int int0 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
        if (!this.wielder.getPlayer().isAccessLevel("None")) {
            int0 = 10;
        }

        if (this.wielder.getCharacter().HasTrait("Hemophobic")) {
            this.wielder.getCharacter().getStats().setPanic(this.wielder.getCharacter().getStats().getPanic() + 50.0F);
        }

        this.wielder.getCharacter().getXp().AddXP(PerkFactory.Perks.Doctor, 20.0F);
        float float0 = 80 - int0;
        this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + float0);
        this.bodyPart.getBodyPart().setHaveBullet(false, int0);
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
        if (GameClient.bClient && !this.bodyPart.getBodyPart().haveBullet()) {
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
        return string + "bodyPart=" + this.bodyPart.getDescription() + "] ";
    }
}
