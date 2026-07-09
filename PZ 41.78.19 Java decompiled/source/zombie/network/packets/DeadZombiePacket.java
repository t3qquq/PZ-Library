// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;

public class DeadZombiePacket extends DeadCharacterPacket implements INetworkPacket {
    private byte zombieFlags;
    private IsoZombie zombie;

    @Override
    public void set(IsoGameCharacter character) {
        super.set(character);
        this.zombie = (IsoZombie)character;
        this.zombieFlags = (byte)(this.zombieFlags | (byte)(this.zombie.isCrawling() ? 1 : 0));
    }

    @Override
    public void process() {
        if (this.zombie != null) {
            this.zombie.setCrawler((this.zombieFlags & 1) != 0);
            super.process();
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        if (GameServer.bServer) {
            this.zombie = ServerMap.instance.ZombieMap.get(this.id);
        } else if (GameClient.bClient) {
            this.zombie = GameClient.IDToZombieMap.get(this.id);
        }

        if (this.zombie != null) {
            this.character = this.zombie;
            if (!GameServer.bServer || !this.zombie.isReanimatedPlayer()) {
                this.parseCharacterInventory(byteBuffer);
                this.parseCharacterHumanVisuals(byteBuffer);
            }

            this.character.setHealth(0.0F);
            this.character.getHitReactionNetworkAI().process(this.x, this.y, this.z, this.angle);
            this.character.getNetworkCharacterAI().setDeadBody(this);
        } else {
            IsoDeadBody deadBody = this.getDeadBody();
            if (deadBody != null) {
                this.parseDeadBodyInventory(deadBody, byteBuffer);
                this.parseDeadBodyHumanVisuals(deadBody, byteBuffer);
            }
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.writeCharacterInventory(byteBufferWriter);
        this.writeCharacterHumanVisuals(byteBufferWriter);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + String.format(" | isCrawling=%b", (this.zombieFlags & 1) != 0);
    }

    public IsoZombie getZombie() {
        return this.zombie;
    }
}
