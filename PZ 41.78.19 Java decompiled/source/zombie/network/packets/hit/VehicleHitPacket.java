// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;

public abstract class VehicleHitPacket extends HitCharacterPacket implements INetworkPacket {
    protected final Player wielder = new Player();
    protected final Vehicle vehicle = new Vehicle();

    public VehicleHitPacket(HitCharacterPacket.HitType hitType) {
        super(hitType);
    }

    public void set(IsoPlayer player, BaseVehicle vehiclex, boolean boolean0) {
        this.wielder.set(player, boolean0);
        this.vehicle.set(vehiclex);
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.wielder.parse(byteBuffer, udpConnection);
        this.wielder.parsePlayer(null);
        this.vehicle.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.wielder.write(byteBufferWriter);
        this.vehicle.write(byteBufferWriter);
    }

    @Override
    public boolean isRelevant(UdpConnection udpConnection) {
        return this.wielder.isRelevant(udpConnection);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.wielder.isConsistent() && this.vehicle.isConsistent();
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tWielder " + this.wielder.getDescription() + "\n\tVehicle " + this.vehicle.getDescription();
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
    }
}
