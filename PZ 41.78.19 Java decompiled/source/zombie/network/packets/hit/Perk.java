// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets.hit;

import java.nio.ByteBuffer;
import zombie.characters.skills.PerkFactory;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.packets.INetworkPacket;

public class Perk implements INetworkPacket {
    protected PerkFactory.Perk perk;
    protected byte perkIndex;

    public void set(PerkFactory.Perk perk0) {
        this.perk = perk0;
        if (this.perk == null) {
            this.perkIndex = -1;
        } else {
            this.perkIndex = (byte)this.perk.index();
        }
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.perkIndex = byteBuffer.get();
        if (this.perkIndex >= 0 && this.perkIndex <= PerkFactory.Perks.getMaxIndex()) {
            this.perk = PerkFactory.Perks.fromIndex(this.perkIndex);
        }
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putByte(this.perkIndex);
    }

    @Override
    public String getDescription() {
        return "\n\t" + this.getClass().getSimpleName() + " [ perk=( " + this.perkIndex + " )" + (this.perk == null ? "null" : this.perk.name) + " ]";
    }

    @Override
    public boolean isConsistent() {
        return this.perk != null;
    }

    public PerkFactory.Perk getPerk() {
        return this.perk;
    }
}
