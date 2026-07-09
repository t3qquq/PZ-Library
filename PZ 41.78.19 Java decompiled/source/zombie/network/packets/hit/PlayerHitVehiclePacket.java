// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.PacketValidator;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;

public class PlayerHitVehiclePacket extends PlayerHitPacket implements INetworkPacket {
    protected final Vehicle vehicle = new Vehicle();

    public PlayerHitVehiclePacket() {
        super(HitCharacterPacket.HitType.PlayerHitVehicle);
    }

    public void set(IsoPlayer player, BaseVehicle vehiclex, HandWeapon weapon, boolean boolean0) {
        super.set(player, weapon, boolean0);
        this.vehicle.set(vehiclex);
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.vehicle.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.vehicle.write(byteBufferWriter);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.vehicle.isConsistent();
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tVehicle " + this.vehicle.getDescription();
    }

    @Override
    protected void process() {
        this.vehicle.process(this.wielder.getCharacter(), this.weapon.getWeapon());
    }

    @Override
    public boolean validate(UdpConnection udpConnection) {
        return PacketValidator.checkLongDistance(udpConnection, this.wielder, this.vehicle, PlayerHitVehiclePacket.class.getSimpleName());
    }
}
