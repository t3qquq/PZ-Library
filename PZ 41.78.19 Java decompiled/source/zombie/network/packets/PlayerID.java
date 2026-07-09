// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.packets.hit.Instance;

public class PlayerID extends Instance implements INetworkPacket {
    protected IsoPlayer player;
    protected byte playerIndex;

    public void set(IsoPlayer playerx) {
        super.set(playerx.OnlineID);
        this.playerIndex = playerx.isLocal() ? (byte)playerx.getPlayerNum() : -1;
        this.player = playerx;
    }

    public void parsePlayer(UdpConnection udpConnection) {
        if (GameServer.bServer) {
            if (udpConnection != null && this.playerIndex != -1) {
                this.player = GameServer.getPlayerFromConnection(udpConnection, this.playerIndex);
            } else {
                this.player = GameServer.IDToPlayerMap.get(this.ID);
            }
        } else if (GameClient.bClient) {
            this.player = GameClient.IDToPlayerMap.get(this.ID);
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.playerIndex = byteBuffer.get();
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        byteBufferWriter.putByte(this.playerIndex);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.getCharacter() != null;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tPlayer [ player " + (this.player == null ? "?" : "\"" + this.player.getUsername() + "\"") + " ]";
    }

    public IsoPlayer getCharacter() {
        return this.player;
    }
}
