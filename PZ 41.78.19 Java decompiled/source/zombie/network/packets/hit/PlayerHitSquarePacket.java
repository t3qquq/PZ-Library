// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.IsoPlayer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.types.HandWeapon;
import zombie.network.packets.INetworkPacket;

public class PlayerHitSquarePacket extends PlayerHitPacket implements INetworkPacket {
    protected final Square square = new Square();

    public PlayerHitSquarePacket() {
        super(HitCharacterPacket.HitType.PlayerHitSquare);
    }

    @Override
    public void set(IsoPlayer player, HandWeapon weapon, boolean boolean0) {
        super.set(player, weapon, boolean0);
        this.square.set(player);
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
        super.parse(byteBuffer, udpConnection);
        this.square.parse(byteBuffer, udpConnection);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        super.write(byteBufferWriter);
        this.square.write(byteBufferWriter);
    }

    @Override
    public boolean isRelevant(UdpConnection udpConnection) {
        return this.wielder.isRelevant(udpConnection);
    }

    @Override
    public boolean isConsistent() {
        return super.isConsistent() && this.square.isConsistent();
    }

    @Override
    public String getDescription() {
        return super.getDescription() + "\n\tSquare " + this.square.getDescription();
    }

    @Override
    protected void process() {
        this.square.process(this.wielder.getCharacter());
    }

    @Override
    public boolean validate(UdpConnection var1) {
        return true;
    }
}
