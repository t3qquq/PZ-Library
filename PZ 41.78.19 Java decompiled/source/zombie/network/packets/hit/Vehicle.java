// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;

public class Vehicle extends Instance implements IPositional, INetworkPacket {
    protected BaseVehicle vehicle;

    public void set(BaseVehicle vehicle1) {
        super.set(vehicle1.getId());
        this.vehicle = vehicle1;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.vehicle = VehicleManager.instance.getVehicleByID(this.ID);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.vehicle != null;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tVehicle [ vehicle=" + (this.vehicle == null ? "?" : "\"" + this.vehicle.getScriptName() + "\"") + " ]";
    }

    void process(IsoGameCharacter character, HandWeapon weapon) {
        if (GameServer.bServer) {
            this.vehicle.hitVehicle(character, weapon);
        }
    }

    BaseVehicle getVehicle() {
        return this.vehicle;
    }

    @Override
    public float getX() {
        return this.vehicle.getX();
    }

    @Override
    public float getY() {
        return this.vehicle.getY();
    }
}
