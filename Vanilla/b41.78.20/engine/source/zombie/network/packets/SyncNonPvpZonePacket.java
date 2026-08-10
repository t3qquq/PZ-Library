// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.iso.IsoWorld;
import zombie.iso.areas.NonPvpZone;
import zombie.util.StringUtils;

public class SyncNonPvpZonePacket implements INetworkPacket {
    public final NonPvpZone zone = new NonPvpZone();
    public boolean doRemove;

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.zone.load(byteBuffer, IsoWorld.getWorldVersion());
        this.doRemove = byteBuffer.get() == 1;
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        this.zone.save(byteBufferWriter.bb);
        byteBufferWriter.putBoolean(this.doRemove);
    }

    @Override
    public boolean isConsistent() {
        return !StringUtils.isNullOrEmpty(this.zone.getTitle());
    }

    @Override
    public String getDescription() {
        return String.format(
            "\"%s\" remove=%b size=%d (%d;%d) (%d;%d)",
            this.zone.getTitle(),
            this.doRemove,
            this.zone.getSize(),
            this.zone.getX(),
            this.zone.getY(),
            this.zone.getX2(),
            this.zone.getY2()
        );
    }

    public void process() {
        if (this.doRemove) {
            NonPvpZone.getAllZones().removeIf(nonPvpZone -> nonPvpZone.getTitle().equals(this.zone.getTitle()));
        } else if (NonPvpZone.getZoneByTitle(this.zone.getTitle()) == null) {
            NonPvpZone.getAllZones().add(this.zone);
        }
    }
}
