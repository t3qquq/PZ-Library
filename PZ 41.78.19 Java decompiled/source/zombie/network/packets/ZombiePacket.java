// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.NetworkVariables;

public class ZombiePacket implements INetworkPacket {
    private static final int PACKET_SIZE_BYTES = 55;
    public short id;
    public float x;
    public float y;
    public byte z;
    public int descriptorID;
    public NetworkVariables.PredictionTypes moveType;
    public short booleanVariables;
    public short target;
    public int timeSinceSeenFlesh;
    public int smParamTargetAngle;
    public short speedMod;
    public NetworkVariables.WalkType walkType;
    public float realX;
    public float realY;
    public byte realZ;
    public short realHealth;
    public NetworkVariables.ZombieState realState;
    public short reanimatedBodyID;
    public byte pfbType;
    public short pfbTarget;
    public float pfbTargetX;
    public float pfbTargetY;
    public byte pfbTargetZ;

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.get();
        this.descriptorID = byteBuffer.getInt();
        this.moveType = NetworkVariables.PredictionTypes.fromByte(byteBuffer.get());
        this.booleanVariables = byteBuffer.getShort();
        this.target = byteBuffer.getShort();
        this.timeSinceSeenFlesh = byteBuffer.getInt();
        this.smParamTargetAngle = byteBuffer.getInt();
        this.speedMod = byteBuffer.getShort();
        this.walkType = NetworkVariables.WalkType.fromByte(byteBuffer.get());
        this.realX = byteBuffer.getFloat();
        this.realY = byteBuffer.getFloat();
        this.realZ = byteBuffer.get();
        this.realHealth = byteBuffer.getShort();
        this.realState = NetworkVariables.ZombieState.fromByte(byteBuffer.get());
        this.reanimatedBodyID = byteBuffer.getShort();
        this.pfbType = byteBuffer.get();
        if (this.pfbType == 1) {
            this.pfbTarget = byteBuffer.getShort();
        } else if (this.pfbType > 1) {
            this.pfbTargetX = byteBuffer.getFloat();
            this.pfbTargetY = byteBuffer.getFloat();
            this.pfbTargetZ = byteBuffer.get();
        }
    }

    public void write(ByteBuffer byteBuffer) {
        byteBuffer.putShort(this.id);
        byteBuffer.putFloat(this.x);
        byteBuffer.putFloat(this.y);
        byteBuffer.put(this.z);
        byteBuffer.putInt(this.descriptorID);
        byteBuffer.put((byte)this.moveType.ordinal());
        byteBuffer.putShort(this.booleanVariables);
        byteBuffer.putShort(this.target);
        byteBuffer.putInt(this.timeSinceSeenFlesh);
        byteBuffer.putInt(this.smParamTargetAngle);
        byteBuffer.putShort(this.speedMod);
        byteBuffer.put((byte)this.walkType.ordinal());
        byteBuffer.putFloat(this.realX);
        byteBuffer.putFloat(this.realY);
        byteBuffer.put(this.realZ);
        byteBuffer.putShort(this.realHealth);
        byteBuffer.put((byte)this.realState.ordinal());
        byteBuffer.putShort(this.reanimatedBodyID);
        byteBuffer.put(this.pfbType);
        if (this.pfbType == 1) {
            byteBuffer.putShort(this.pfbTarget);
        } else if (this.pfbType > 1) {
            byteBuffer.putFloat(this.pfbTargetX);
            byteBuffer.putFloat(this.pfbTargetY);
            byteBuffer.put(this.pfbTargetZ);
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        this.write(byteBufferWriter.bb);
    }

    @Override
    public int getPacketSizeBytes() {
        return 55;
    }

    public void copy(ZombiePacket zombiePacket0) {
        this.id = zombiePacket0.id;
        this.x = zombiePacket0.x;
        this.y = zombiePacket0.y;
        this.z = zombiePacket0.z;
        this.descriptorID = zombiePacket0.descriptorID;
        this.moveType = zombiePacket0.moveType;
        this.booleanVariables = zombiePacket0.booleanVariables;
        this.target = zombiePacket0.target;
        this.timeSinceSeenFlesh = zombiePacket0.timeSinceSeenFlesh;
        this.smParamTargetAngle = zombiePacket0.smParamTargetAngle;
        this.speedMod = zombiePacket0.speedMod;
        this.walkType = zombiePacket0.walkType;
        this.realX = zombiePacket0.realX;
        this.realY = zombiePacket0.realY;
        this.realZ = zombiePacket0.realZ;
        this.realHealth = zombiePacket0.realHealth;
        this.reanimatedBodyID = zombiePacket0.reanimatedBodyID;
        this.realState = zombiePacket0.realState;
        this.pfbType = zombiePacket0.pfbType;
        this.pfbTarget = zombiePacket0.pfbTarget;
        this.pfbTargetX = zombiePacket0.pfbTargetX;
        this.pfbTargetY = zombiePacket0.pfbTargetY;
        this.pfbTargetZ = zombiePacket0.pfbTargetZ;
    }

    public void set(IsoZombie zombie0) {
        this.id = zombie0.OnlineID;
        this.descriptorID = zombie0.getPersistentOutfitID();
        zombie0.networkAI.set(this);
        zombie0.networkAI.mindSync.set(this);
        zombie0.thumpSent = true;
    }
}
