// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;

public class ZombieHitPlayerPacket extends HitCharacterPacket implements INetworkPacket {
    protected final Zombie wielder = new Zombie();
    protected final Player target = new Player();
    protected final Bite bite = new Bite();

    public ZombieHitPlayerPacket() {
        super(HitCharacterPacket.HitType.ZombieHitPlayer);
    }

    public void set(IsoZombie zombie0, IsoPlayer player) {
        this.wielder.set(zombie0, false);
        this.target.set(player, false);
        this.bite.set(zombie0);
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.wielder.parse(byteBuffer, udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.target.parsePlayer(udpConnection);
        this.bite.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.wielder.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.bite.write(byteBufferWriter);
    }

    @Override
    public boolean isRelevant(UdpConnection udpConnection) {
        return this.target.isRelevant(udpConnection);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.target.isConsistent() && this.wielder.isConsistent();
    }

    @Override
    public String getDescription() {
        return super.getDescription()
            + "\n\tWielder "
            + this.wielder.getDescription()
            + "\n\tTarget "
            + this.target.getDescription()
            + "\n\tBite "
            + this.bite.getDescription();
    }

    @Override
    protected void preProcess() {
        this.wielder.process();
        this.target.process();
    }

    @Override
    protected void process() {
        this.bite.process((IsoZombie)this.wielder.getCharacter(), this.target.getCharacter());
    }

    @Override
    protected void postProcess() {
        this.wielder.process();
        this.target.process();
    }

    @Override
    protected void attack() {
    }

    @Override
    protected void react() {
        this.wielder.react();
        this.target.react();
    }

    @Override
    public boolean validate(UdpConnection udpConnection) {
        if (!PacketValidator.checkShortDistance(udpConnection, this.wielder, this.target, ZombieHitPlayerPacket.class.getSimpleName())) {
            return false;
        } else {
            return !PacketValidator.checkOwner(udpConnection, this.wielder, ZombieHitPlayerPacket.class.getSimpleName())
                ? false
                : PacketValidator.checkTarget(udpConnection, this.target, ZombieHitPlayerPacket.class.getSimpleName());
        }
    }
}
