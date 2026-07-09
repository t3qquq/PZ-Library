// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.packets.INetworkPacket;

public class Square implements IPositional, INetworkPacket {
    protected float positionX;
    protected float positionY;
    protected float positionZ;
    protected IsoGridSquare square;

    public void set(IsoGameCharacter character) {
        this.square = character.getAttackTargetSquare();
        if (this.square != null) {
            this.positionX = this.square.getX();
            this.positionY = this.square.getY();
            this.positionZ = this.square.getZ();
        } else {
            this.positionX = 0.0F;
            this.positionY = 0.0F;
            this.positionZ = 0.0F;
        }
    }

    public void set(IsoGridSquare square0) {
        this.square = square0;
        if (this.square != null) {
            this.positionX = this.square.getX();
            this.positionY = this.square.getY();
            this.positionZ = this.square.getZ();
        } else {
            this.positionX = 0.0F;
            this.positionY = 0.0F;
            this.positionZ = 0.0F;
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.positionX = byteBuffer.getFloat();
        this.positionY = byteBuffer.getFloat();
        this.positionZ = byteBuffer.getFloat();
        if (GameServer.bServer) {
            this.square = ServerMap.instance.getGridSquare((int)Math.floor(this.positionX), (int)Math.floor(this.positionY), (int)Math.floor(this.positionZ));
        }

        if (GameClient.bClient) {
            this.square = IsoWorld.instance
                .CurrentCell
                .getGridSquare((int)Math.floor(this.positionX), (int)Math.floor(this.positionY), (int)Math.floor(this.positionZ));
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putFloat(this.positionX);
        byteBufferWriter.putFloat(this.positionY);
        byteBufferWriter.putFloat(this.positionZ);
    }

    @Override
    public String getDescription() {
        return "\n\tSquare [ pos=( " + this.positionX + " ; " + this.positionY + " ; " + this.positionZ + " ) ]";
    }

    void process(IsoGameCharacter character) {
        character.setAttackTargetSquare(character.getCell().getGridSquare((double)this.positionX, (double)this.positionY, (double)this.positionZ));
    }

    @Override
    public float getX() {
        return this.positionX;
    }

    @Override
    public float getY() {
        return this.positionY;
    }

    public float getZ() {
        return this.positionZ;
    }

    public IsoGridSquare getSquare() {
        return this.square;
    }

    @Override
    public boolean isConsistent() {
        return this.square != null;
    }
}
