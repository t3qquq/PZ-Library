// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.NetworkVariables;

public class PlayerPacket implements INetworkPacket {
    public static final int PACKET_SIZE_BYTES = 43;
    public short id;
    public float x;
    public float y;
    public byte z;
    public float direction;
    public boolean usePathFinder;
    public NetworkVariables.PredictionTypes moveType;
    public short VehicleID;
    public short VehicleSeat;
    public int booleanVariables;
    public byte footstepSoundRadius;
    public byte bleedingLevel;
    public float realx;
    public float realy;
    public byte realz;
    public byte realdir;
    public int realt;
    public float collidePointX;
    public float collidePointY;
    public PlayerVariables variables = new PlayerVariables();

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.get();
        this.direction = byteBuffer.getFloat();
        this.usePathFinder = byteBuffer.get() == 1;
        this.moveType = NetworkVariables.PredictionTypes.fromByte(byteBuffer.get());
        this.VehicleID = byteBuffer.getShort();
        this.VehicleSeat = byteBuffer.getShort();
        this.booleanVariables = byteBuffer.getInt();
        this.footstepSoundRadius = byteBuffer.get();
        this.bleedingLevel = byteBuffer.get();
        this.realx = byteBuffer.getFloat();
        this.realy = byteBuffer.getFloat();
        this.realz = byteBuffer.get();
        this.realdir = byteBuffer.get();
        this.realt = byteBuffer.getInt();
        this.collidePointX = byteBuffer.getFloat();
        this.collidePointY = byteBuffer.getFloat();
        this.variables.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putByte(this.z);
        byteBufferWriter.putFloat(this.direction);
        byteBufferWriter.putBoolean(this.usePathFinder);
        byteBufferWriter.putByte((byte)this.moveType.ordinal());
        byteBufferWriter.putShort(this.VehicleID);
        byteBufferWriter.putShort(this.VehicleSeat);
        byteBufferWriter.putInt(this.booleanVariables);
        byteBufferWriter.putByte(this.footstepSoundRadius);
        byteBufferWriter.putByte(this.bleedingLevel);
        byteBufferWriter.putFloat(this.realx);
        byteBufferWriter.putFloat(this.realy);
        byteBufferWriter.putByte(this.realz);
        byteBufferWriter.putByte(this.realdir);
        byteBufferWriter.putInt(this.realt);
        byteBufferWriter.putFloat(this.collidePointX);
        byteBufferWriter.putFloat(this.collidePointY);
        this.variables.write(byteBufferWriter);
    }

    @Override
    public int getPacketSizeBytes() {
        return 43;
    }

    public boolean set(IsoPlayer player) {
        if (GameClient.bClient) {
            this.id = (short)player.getPlayerNum();
        } else {
            this.id = player.OnlineID;
        }

        this.bleedingLevel = player.bleedingLevel;
        this.variables.set(player);
        return player.networkAI.set(this);
    }

    public void copy(PlayerPacket playerPacket0) {
        this.id = playerPacket0.id;
        this.x = playerPacket0.x;
        this.y = playerPacket0.y;
        this.z = playerPacket0.z;
        this.direction = playerPacket0.direction;
        this.usePathFinder = playerPacket0.usePathFinder;
        this.moveType = playerPacket0.moveType;
        this.VehicleID = playerPacket0.VehicleID;
        this.VehicleSeat = playerPacket0.VehicleSeat;
        this.booleanVariables = playerPacket0.booleanVariables;
        this.footstepSoundRadius = playerPacket0.footstepSoundRadius;
        this.bleedingLevel = playerPacket0.bleedingLevel;
        this.realx = playerPacket0.realx;
        this.realy = playerPacket0.realy;
        this.realz = playerPacket0.realz;
        this.realdir = playerPacket0.realdir;
        this.realt = playerPacket0.realt;
        this.collidePointX = playerPacket0.collidePointX;
        this.collidePointY = playerPacket0.collidePointY;
        this.variables.copy(playerPacket0.variables);
    }

    public static class l_receive {
        public static PlayerPacket playerPacket = new PlayerPacket();
    }

    public static class l_send {
        public static PlayerPacket playerPacket = new PlayerPacket();
    }
}
