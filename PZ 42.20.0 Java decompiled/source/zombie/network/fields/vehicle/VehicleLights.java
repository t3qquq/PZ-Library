// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.network.fields.vehicle;

import zombie.core.network.ByteBufferReader;
import zombie.core.network.ByteBufferWriter;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;
import zombie.network.IConnection;
import zombie.network.fields.INetworkPacketField;

public class VehicleLights extends VehicleField implements INetworkPacketField {
    public VehicleLights(VehicleID vehicleID) {
        super(vehicleID);
    }

    @Override
    public void parse(ByteBufferReader bb, IConnection connection) {
        try {
            this.getVehicle().setHeadlightsOn(bb.getBoolean());
            this.getVehicle().setStoplightsOn(bb.getBoolean());

            for (int i = 0; i < this.getVehicle().getLightCount(); i++) {
                this.getVehicle().getLightByIndex(i).getLight().setActive(bb.getBoolean());
            }
        } catch (Exception e) {
            DebugType.Multiplayer.printException(e, this.getClass().getSimpleName() + ": failed", LogSeverity.Error);
        }
    }

    @Override
    public void write(ByteBufferWriter b) {
        try {
            b.putBoolean(this.getVehicle().getHeadlightsOn());
            b.putBoolean(this.getVehicle().getStoplightsOn());

            for (int j = 0; j < this.getVehicle().getLightCount(); j++) {
                b.putBoolean(this.getVehicle().getLightByIndex(j).getLight().getActive());
            }
        } catch (Exception e) {
            DebugType.Multiplayer.printException(e, this.getClass().getSimpleName() + ": failed", LogSeverity.Error);
        }
    }
}
