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
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.PacketValidator;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.PlayerBodyPart;
import zombie.network.packets.hit.PlayerItem;

public class Stitch implements INetworkPacket {
    protected final Player wielder = new Player();
    protected final Player target = new Player();
    protected PlayerBodyPart bodyPart = new PlayerBodyPart();
    protected PlayerItem item = new PlayerItem();
    protected float stitchTime = 0.0F;
    protected boolean doIt = false;
    protected boolean infect = false;

    public void set(IsoGameCharacter character0, IsoGameCharacter character1, BodyPart bodyPartx, InventoryItem itemx, boolean boolean0) {
        this.wielder.set(character0);
        this.target.set(character1);
        this.bodyPart.set(bodyPartx);
        if (itemx != null) {
            this.item.set(itemx);
        }

        this.stitchTime = bodyPartx.getStitchTime();
        this.doIt = boolean0;
        this.infect = bodyPartx.isInfectedWound();
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.wielder.parse(byteBuffer, udpConnection);
        this.wielder.parsePlayer(udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.target.parsePlayer(null);
        this.bodyPart.parse(byteBuffer, this.target.getCharacter());
        this.item.parse(byteBuffer, udpConnection);
        this.stitchTime = byteBuffer.getFloat();
        this.doIt = byteBuffer.get() == 1;
        this.infect = byteBuffer.get() == 1;
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        this.wielder.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.bodyPart.write(byteBufferWriter);
        this.item.write(byteBufferWriter);
        byteBufferWriter.putFloat(this.stitchTime);
        byteBufferWriter.putBoolean(this.doIt);
        byteBufferWriter.putBoolean(this.infect);
    }

    public void process() {
        int int0 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
        if (!this.wielder.getPlayer().isAccessLevel("None")) {
            int0 = 10;
        }

        byte byte0 = 20;
        if (this.doIt) {
            if (this.wielder.getCharacter().getInventory().contains("SutureNeedleHolder") || this.item.getItem().getType().equals("SutureNeedle")) {
                byte0 = 10;
            }
        } else {
            byte0 = 5;
        }

        if (this.wielder.getCharacter().getCharacterTraits().Hemophobic.isSet()) {
            this.wielder.getCharacter().getStats().setPanic(this.wielder.getCharacter().getStats().getPanic() + 50.0F);
        }

        if (this.item.getItem() != null) {
            this.item.getItem().Use();
        }

        if (this.bodyPart.getBodyPart().isGetStitchXp()) {
            this.wielder.getCharacter().getXp().AddXP(PerkFactory.Perks.Doctor, 15.0F);
        }

        this.bodyPart.getBodyPart().setStitched(this.doIt);
        int int1 = byte0 - int0 * 1;
        if (int1 < 0) {
            int1 = 0;
        }

        if (!this.wielder.getPlayer().isAccessLevel("None")) {
            this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + int1);
        }

        if (this.doIt) {
            this.bodyPart.getBodyPart().setStitchTime(this.stitchTime);
        }

        if (this.infect) {
            this.bodyPart.getBodyPart().setInfectedWound(true);
        }
    }

    @Override
    public boolean isConsistent() {
        return this.wielder.getCharacter() != null
            && this.wielder.getCharacter() instanceof IsoPlayer
            && this.target.getCharacter() != null
            && this.target.getCharacter() instanceof IsoPlayer
            && this.bodyPart.getBodyPart() != null
            && this.stitchTime < 50.0F
            && this.stitchTime >= 0.0F;
    }

    public boolean validate(UdpConnection udpConnection) {
        if (!GameClient.bClient || !this.doIt || this.bodyPart.getBodyPart().isDeepWounded() && !this.bodyPart.getBodyPart().haveGlass()) {
            if (GameClient.bClient && !this.doIt && !this.bodyPart.getBodyPart().stitched()) {
                DebugLog.General.warn(this.getClass().getSimpleName() + ": Validate error: " + this.getDescription());
                return false;
            } else {
                return PacketValidator.checkShortDistance(udpConnection, this.wielder, this.target, this.getClass().getSimpleName());
            }
        } else {
            DebugLog.General.warn(this.getClass().getSimpleName() + ": Validate error: " + this.getDescription());
            return false;
        }
    }

    @Override
    public String getDescription() {
        String string = "\n\t" + this.getClass().getSimpleName() + " [";
        string = string + "wielder=" + this.wielder.getDescription() + " | ";
        string = string + "target=" + this.target.getDescription() + " | ";
        string = string + "bodyPart=" + this.bodyPart.getDescription() + " | ";
        string = string + "item=" + this.item.getDescription() + " | ";
        string = string + "stitchTime=" + this.stitchTime + " | ";
        string = string + "doIt=" + this.doIt + " | ";
        return string + "infect=" + this.infect + "] ";
    }
}
