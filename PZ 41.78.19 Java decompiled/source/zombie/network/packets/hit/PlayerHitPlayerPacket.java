// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;

public class PlayerHitPlayerPacket extends PlayerHitPacket implements INetworkPacket {
    protected final Player target = new Player();
    protected final WeaponHit hit = new WeaponHit();
    protected final Fall fall = new Fall();

    public PlayerHitPlayerPacket() {
        super(HitCharacterPacket.HitType.PlayerHitPlayer);
    }

    public void set(IsoPlayer player0, IsoPlayer player1, HandWeapon weapon, float float0, boolean boolean1, float float1, boolean boolean0, boolean boolean2) {
        super.set(player0, weapon, boolean0);
        this.target.set(player1, false);
        this.hit.set(boolean1, float0, float1, player1.getHitForce(), player1.getHitDir().x, player1.getHitDir().y, boolean2);
        this.fall.set(player1.getHitReactionNetworkAI());
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.target.parsePlayer(null);
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
    public boolean validate(UdpConnection udpConnection) {
        if (!PacketValidator.checkPVP(udpConnection, this.wielder, this.target, PlayerHitPlayerPacket.class.getSimpleName())) {
            return false;
        } else {
            return !PacketValidator.checkLongDistance(udpConnection, this.wielder, this.target, PlayerHitPlayerPacket.class.getSimpleName())
                ? false
                : PacketValidator.checkDamage(udpConnection, this.hit, PlayerHitPlayerPacket.class.getSimpleName());
        }
    }

    @Override
    protected void attack() {
        this.wielder.attack(this.weapon.getWeapon(), true);
    }

    @Override
    protected void react() {
        this.target.react();
    }
}
