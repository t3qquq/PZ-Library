// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
