// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.popman;

public enum ZombieStateFlag {
    Initialized(1),
    Crawling(2),
    CanWalk(4),
    FakeDead(8),
    CanCrawlUnderVehicle(16),
    ReanimatedForGrappleOnly(32);

    public final int flag;

    ZombieStateFlag(final int flag) {
        this.flag = flag;
    }
}
