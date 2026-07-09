// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;

public abstract class PlayerHitPacket extends HitCharacterPacket implements INetworkPacket {
    protected final Player wielder = new Player();
    protected final Weapon weapon = new Weapon();

    public PlayerHitPacket(HitCharacterPacket.HitType hitType) {
        super(hitType);
    }

    public void set(IsoPlayer player, HandWeapon weaponx, boolean boolean0) {
        this.wielder.set(player, boolean0);
        this.weapon.set(weaponx);
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.wielder.parse(byteBuffer, udpConnection);
        this.wielder.parsePlayer(udpConnection);
        this.weapon.parse(byteBuffer, (IsoLivingCharacter)this.wielder.getCharacter());
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.wielder.write(byteBufferWriter);
        this.weapon.write(byteBufferWriter);
    }

    @Override
    public boolean isRelevant(UdpConnection udpConnection) {
        return this.wielder.isRelevant(udpConnection);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.weapon.isConsistent() && this.wielder.isConsistent();
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tWielder " + this.wielder.getDescription() + "\n\tWeapon " + this.weapon.getDescription();
    }

    @Override
    protected void preProcess() {
        this.wielder.process();
    }

    @Override
    protected void postProcess() {
        this.wielder.process();
    }

    @Override
    protected void attack() {
        this.wielder.attack(this.weapon.getWeapon(), false);
    }

    @Override
    protected void react() {
    }
}
