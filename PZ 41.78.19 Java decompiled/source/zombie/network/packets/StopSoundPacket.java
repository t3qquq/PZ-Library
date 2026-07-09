// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoMovingObject;
import zombie.network.packets.hit.MovingObject;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;

public class StopSoundPacket implements INetworkPacket {
    MovingObject object = new MovingObject();
    String name;
    boolean trigger;

    public void set(IsoMovingObject movingObject, String string, boolean boolean0) {
        this.object.setMovingObject(movingObject);
        this.name = string;
        this.trigger = boolean0;
    }

    public void process() {
        IsoMovingObject movingObject = this.object.getMovingObject();
        IsoGameCharacter character = Type.tryCastTo(movingObject, IsoGameCharacter.class);
        if (character != null) {
            if (this.trigger) {
                character.getEmitter().stopOrTriggerSoundByName(this.name);
            } else {
                character.getEmitter().stopSoundByName(this.name);
            }
        } else {
            BaseVehicle vehicle = Type.tryCastTo(movingObject, BaseVehicle.class);
            if (vehicle != null) {
                if (this.trigger) {
                    vehicle.getEmitter().stopOrTriggerSoundByName(this.name);
                } else {
                    vehicle.getEmitter().stopSoundByName(this.name);
                }
            }
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.trigger = byteBuffer.get() == 1;
        this.object.parse(byteBuffer, udpConnection);
        this.name = GameWindow.ReadString(byteBuffer);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte((byte)(this.trigger ? 1 : 0));
        this.object.write(byteBufferWriter);
        byteBufferWriter.putUTF(this.name);
    }

    @Override
    public int getPacketSizeBytes() {
        return this.object.getPacketSizeBytes() + 2 + this.name.length();
    }

    @Override
    public String getDescription() {
        return "\n\tStopSoundPacket [name=" + this.name + " | object=" + this.object.getDescription() + "]";
    }
}
