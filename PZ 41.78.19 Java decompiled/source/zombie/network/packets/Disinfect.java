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
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Food;
import zombie.network.GameClient;
import zombie.network.PacketValidator;
import zombie.network.packets.hit.Player;
import zombie.network.packets.hit.PlayerBodyPart;
import zombie.network.packets.hit.PlayerItem;

public class Disinfect implements INetworkPacket {
    protected final Player wielder = new Player();
    protected final Player target = new Player();
    protected PlayerBodyPart bodyPart = new PlayerBodyPart();
    protected PlayerItem alcohol = new PlayerItem();

    public void set(IsoGameCharacter character0, IsoGameCharacter character1, BodyPart bodyPartx, InventoryItem item) {
        this.wielder.set(character0);
        this.target.set(character1);
        this.bodyPart.set(bodyPartx);
        this.alcohol.set(item);
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.wielder.parse(byteBuffer, udpConnection);
        this.wielder.parsePlayer(udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.target.parsePlayer(null);
        this.bodyPart.parse(byteBuffer, this.target.getCharacter());
        this.alcohol.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        this.wielder.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.bodyPart.write(byteBufferWriter);
        this.alcohol.write(byteBufferWriter);
    }

    public void process() {
        int int0 = this.wielder.getCharacter().getPerkLevel(PerkFactory.Perks.Doctor);
        if (!this.wielder.getPlayer().isAccessLevel("None")) {
            int0 = 10;
        }

        this.bodyPart.getBodyPart().setAlcoholLevel(this.bodyPart.getBodyPart().getAlcoholLevel() + this.alcohol.getItem().getAlcoholPower());
        float float0 = this.alcohol.getItem().getAlcoholPower() * 13.0F - int0 / 2;
        this.bodyPart.getBodyPart().setAdditionalPain(this.bodyPart.getBodyPart().getAdditionalPain() + float0);
        if (this.alcohol.getItem() instanceof Food) {
            Food food = (Food)this.alcohol.getItem();
            food.setThirstChange(food.getThirstChange() + 0.1F);
            if (food.getBaseHunger() < 0.0F) {
                food.setHungChange(food.getHungChange() + 0.1F);
            }
        }

        if (this.alcohol.getItem().getScriptItem().getThirstChange() > -0.01 || this.alcohol.getItem().getScriptItem().getHungerChange() > -0.01) {
            this.alcohol.getItem().Use();
        } else if (this.alcohol.getItem() instanceof DrainableComboItem) {
            this.alcohol.getItem().Use();
        }
    }

    @Override
    public boolean isConsistent() {
        return this.wielder.getCharacter() != null
            && this.wielder.getCharacter() instanceof IsoPlayer
            && this.target.getCharacter() != null
            && this.target.getCharacter() instanceof IsoPlayer
            && this.bodyPart.getBodyPart() != null
            && this.alcohol.getItem() != null;
    }

    public boolean validate(UdpConnection udpConnection) {
        if (GameClient.bClient && this.alcohol.getItem().getAlcoholPower() <= 0.0F) {
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
        return string + "alcohol=" + this.alcohol.getDescription() + "] ";
    }
}
