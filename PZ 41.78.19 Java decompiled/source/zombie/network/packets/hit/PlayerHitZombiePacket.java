// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;

public class PlayerHitZombiePacket extends PlayerHitPacket implements INetworkPacket {
    protected final Zombie target = new Zombie();
    protected final WeaponHit hit = new WeaponHit();
    protected final Fall fall = new Fall();

    public PlayerHitZombiePacket() {
        super(HitCharacterPacket.HitType.PlayerHitZombie);
    }

    public void set(
        IsoPlayer player,
        IsoZombie zombie0,
        HandWeapon weapon,
        float float0,
        boolean boolean2,
        float float1,
        boolean boolean0,
        boolean boolean1,
        boolean boolean3
    ) {
        super.set(player, weapon, boolean0);
        this.target.set(zombie0, boolean1);
        this.hit.set(boolean2, float0, float1, zombie0.getHitForce(), zombie0.getHitDir().x, zombie0.getHitDir().y, boolean3);
        this.fall.set(zombie0.getHitReactionNetworkAI());
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.hit.parse(byteBuffer, udpConnection);
        this.fall.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.hit.write(byteBufferWriter);
        this.fall.write(byteBufferWriter);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.target.isConsistent() && this.hit.isConsistent();
    }

    @Override
    public String getDescription() {
        return super.getDescription()
            + "\n\tTarget "
            + this.target.getDescription()
            + "\n\tHit "
            + this.hit.getDescription()
            + "\n\tFall "
            + this.fall.getDescription();
    }

    @Override
    protected void preProcess() {
        super.preProcess();
        this.target.process();
    }

    @Override
    protected void process() {
        this.hit.process(this.wielder.getCharacter(), this.target.getCharacter(), this.weapon.getWeapon());
        this.fall.process(this.target.getCharacter());
    }

    @Override
    protected void postProcess() {
        super.postProcess();
        this.target.process();
    }

    @Override
    protected void react() {
        this.target.react(this.weapon.getWeapon());
    }

    @Override
    public boolean validate(UdpConnection udpConnection) {
        return !PacketValidator.checkLongDistance(udpConnection, this.wielder, this.target, PlayerHitZombiePacket.class.getSimpleName())
            ? false
            : PacketValidator.checkDamage(udpConnection, this.hit, PlayerHitZombiePacket.class.getSimpleName());
    }
}
