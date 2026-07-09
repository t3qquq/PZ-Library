// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;

public class VehicleAuthorizationPacket implements INetworkPacket {
    short vehicleId = -1;
    BaseVehicle.Authorization authorization = BaseVehicle.Authorization.Server;
    short authorizationPlayer = -1;

    public void set(BaseVehicle vehicle, UdpConnection udpConnection) {
        BaseVehicle.ServerVehicleState serverVehicleState = vehicle.connectionState[udpConnection.index];
        serverVehicleState.setAuthorization(vehicle);
        this.authorization = vehicle.netPlayerAuthorization;
        this.authorizationPlayer = vehicle.netPlayerId;
        this.vehicleId = vehicle.getId();
    }

    public void process() {
        BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(this.vehicleId);
        if (vehicle != null) {
            DebugLog.Vehicle.trace("vehicle=%d netPlayerAuthorization=%s netPlayerId=%d", vehicle.getId(), this.authorization.name(), this.authorizationPlayer);
            vehicle.netPlayerFromServerUpdate(this.authorization, this.authorizationPlayer);
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.vehicleId = byteBuffer.getShort();
        this.authorization = BaseVehicle.Authorization.valueOf(byteBuffer.get());
        this.authorizationPlayer = byteBuffer.getShort();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.vehicleId);
        byteBufferWriter.putByte(this.authorization.index());
        byteBufferWriter.putShort(this.authorizationPlayer);
    }

    @Override
    public String getDescription() {
        return null;
    }
}
