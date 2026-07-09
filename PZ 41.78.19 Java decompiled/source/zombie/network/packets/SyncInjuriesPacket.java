// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class SyncInjuriesPacket implements INetworkPacket {
    public short id;
    public float strafeSpeed;
    public float walkSpeed;
    public float walkInjury;
    public IsoPlayer player;

    public boolean set(IsoPlayer playerx) {
        if (GameClient.bClient) {
            this.id = (short)playerx.getPlayerNum();
        } else if (GameServer.bServer) {
            this.id = playerx.getOnlineID();
        }

        this.strafeSpeed = playerx.getVariableFloat("StrafeSpeed", 1.0F);
        this.walkSpeed = playerx.getVariableFloat("WalkSpeed", 1.0F);
        this.walkInjury = playerx.getVariableFloat("WalkInjury", 0.0F);
        this.player = playerx;
        return true;
    }

    public boolean process() {
        if (this.player != null && !this.player.isLocalPlayer()) {
            this.player.setVariable("StrafeSpeed", this.strafeSpeed);
            this.player.setVariable("WalkSpeed", this.walkSpeed);
            this.player.setVariable("WalkInjury", this.walkInjury);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        this.id = byteBuffer.getShort();
        this.strafeSpeed = byteBuffer.getFloat();
        this.walkSpeed = byteBuffer.getFloat();
        this.walkInjury = byteBuffer.getFloat();
        if (GameServer.bServer) {
            this.player = GameServer.getPlayerFromConnection(udpConnection, this.id);
        } else if (GameClient.bClient) {
            this.player = GameClient.IDToPlayerMap.get(this.id);
        } else {
            this.player = null;
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.strafeSpeed);
        byteBufferWriter.putFloat(this.walkSpeed);
        byteBufferWriter.putFloat(this.walkInjury);
    }

    @Override
    public int getPacketSizeBytes() {
        return 14;
    }

    @Override
    public String getDescription() {
        return "SyncInjuriesPacket: id=" + this.id + ", strafeSpeed=" + this.strafeSpeed + ", walkSpeed=" + this.walkSpeed + ", walkInjury=" + this.walkInjury;
    }
}
