// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;

public class VehicleHitPlayerPacket extends VehicleHitPacket implements INetworkPacket {
    protected final Player target = new Player();
    protected final VehicleHit vehicleHit = new VehicleHit();
    protected final Fall fall = new Fall();

    public VehicleHitPlayerPacket() {
        super(HitCharacterPacket.HitType.VehicleHitPlayer);
    }

    public void set(IsoPlayer player0, IsoPlayer player1, BaseVehicle vehicle, float float0, boolean boolean1, int int0, float float1, boolean boolean0) {
        super.set(player0, vehicle, false);
        this.target.set(player1, false);
        this.vehicleHit.set(false, float0, player1.getHitForce(), player1.getHitDir().x, player1.getHitDir().y, int0, float1, boolean0, boolean1);
        this.fall.set(player1.getHitReactionNetworkAI());
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.target.parse(byteBuffer, udpConnection);
        this.target.parsePlayer(udpConnection);
        this.vehicleHit.parse(byteBuffer, udpConnection);
        this.fall.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.target.write(byteBufferWriter);
        this.vehicleHit.write(byteBufferWriter);
        this.fall.write(byteBufferWriter);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.target.isConsistent() && this.vehicleHit.isConsistent();
    }

    @Override
    public String getDescription() {
        return super.getDescription()
            + "\n\tTarget "
            + this.target.getDescription()
            + "\n\tVehicleHit "
            + this.vehicleHit.getDescription()
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
        this.vehicleHit.process(this.wielder.getCharacter(), this.target.getCharacter(), this.vehicle.getVehicle());
        this.fall.process(this.target.getCharacter());
    }

    @Override
    protected void postProcess() {
        super.postProcess();
        this.target.process();
    }

    @Override
    protected void react() {
        this.target.react();
    }

    @Override
    protected void postpone() {
        this.target.getCharacter().getNetworkCharacterAI().setVehicleHit(this);
    }

    @Override
    public boolean validate(UdpConnection udpConnection) {
        if (!PacketValidator.checkPVP(udpConnection, this.wielder, this.target, VehicleHitPlayerPacket.class.getSimpleName())) {
            return false;
        } else if (!PacketValidator.checkSpeed(udpConnection, this.vehicleHit, VehicleHitPlayerPacket.class.getSimpleName())) {
            return false;
        } else {
            return !PacketValidator.checkShortDistance(udpConnection, this.wielder, this.target, VehicleHitPlayerPacket.class.getSimpleName())
                ? false
                : PacketValidator.checkDamage(udpConnection, this.vehicleHit, VehicleHitPlayerPacket.class.getSimpleName());
        }
    }
}
