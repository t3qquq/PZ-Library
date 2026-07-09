// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.jobs;

import zombie.core.raknet.UdpConnection;

public class JobServerSendFullData extends RegionJob {
    protected UdpConnection targetConn;

    protected JobServerSendFullData() {
        super(RegionJobType.ServerSendFullData);
    }

    @Override
    protected void reset() {
        this.targetConn = null;
    }

    public UdpConnection getTargetConn() {
        return this.targetConn;
    }
}
