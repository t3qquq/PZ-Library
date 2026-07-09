// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.packets.INetworkPacket;
import zombie.vehicles.BaseVehicle;

public class VehicleHit extends Hit implements IMovable, INetworkPacket {
    public int vehicleDamage;
    public float vehicleSpeed;
    public boolean isVehicleHitFromBehind;
    public boolean isTargetHitFromBehind;

    public void set(boolean boolean0, float float0, float float1, float float2, float float3, int int0, float float4, boolean boolean1, boolean boolean2) {
        super.set(boolean0, float0, float1, float2, float3);
        this.vehicleDamage = int0;
        this.vehicleSpeed = float4;
        this.isVehicleHitFromBehind = boolean1;
        this.isTargetHitFromBehind = boolean2;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.vehicleDamage = byteBuffer.getInt();
        this.vehicleSpeed = byteBuffer.getFloat();
        this.isVehicleHitFromBehind = byteBuffer.get() != 0;
        this.isTargetHitFromBehind = byteBuffer.get() != 0;
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putInt(this.vehicleDamage);
        byteBufferWriter.putFloat(this.vehicleSpeed);
        byteBufferWriter.putBoolean(this.isVehicleHitFromBehind);
        byteBufferWriter.putBoolean(this.isTargetHitFromBehind);
    }

    @Override
    public String getDescription() {
        return super.getDescription()
            + "\n\tVehicle [ speed="
            + this.vehicleSpeed
            + " | damage="
            + this.vehicleDamage
            + " | target-hit="
            + (this.isTargetHitFromBehind ? "FRONT" : "BEHIND")
            + " | vehicle-hit="
            + (this.isVehicleHitFromBehind ? "FRONT" : "REAR")
            + " ]";
    }

    void process(IsoGameCharacter character0, IsoGameCharacter character1, BaseVehicle vehicle) {
        super.process(character0, character1);
        if (GameServer.bServer) {
            if (this.vehicleDamage != 0) {
                if (this.isVehicleHitFromBehind) {
                    vehicle.addDamageFrontHitAChr(this.vehicleDamage);
                } else {
                    vehicle.addDamageRearHitAChr(this.vehicleDamage);
                }

                vehicle.transmitBlood();
            }
        } else if (GameClient.bClient) {
            if (character1 instanceof IsoZombie) {
                ((IsoZombie)character1).applyDamageFromVehicle(this.vehicleSpeed, this.damage);
            } else if (character1 instanceof IsoPlayer) {
                ((IsoPlayer)character1).getDamageFromHitByACar(this.vehicleSpeed);
                ((IsoPlayer)character1).actionContext.reportEvent("washit");
                character1.setVariable("hitpvp", false);
            }
        }
    }

    @Override
    public float getSpeed() {
        return this.vehicleSpeed;
    }

    @Override
    public boolean isVehicle() {
        return true;
    }
}
