// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;

public class PlayerDataRequestPacket implements INetworkPacket {
    short playerId = -1;

    public void set(short short0) {
        this.playerId = short0;
    }

    public void process(UdpConnection udpConnection) {
        IsoPlayer player = GameServer.IDToPlayerMap.get(this.playerId);
        if (udpConnection.RelevantTo(player.x, player.y) && !player.isInvisible() || udpConnection.accessLevel >= 1) {
            GameServer.sendPlayerConnect(player, udpConnection);
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.playerId = byteBuffer.getShort();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.playerId);
    }
}
