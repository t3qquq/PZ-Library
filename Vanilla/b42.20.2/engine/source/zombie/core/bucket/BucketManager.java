// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.core.bucket;

public final class BucketManager {
    static final Bucket SharedBucket = new Bucket();

    public static Bucket Active() {
        return SharedBucket;
    }

    public static Bucket Shared() {
        return SharedBucket;
    }
}
