// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
