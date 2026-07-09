// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

public final class LocationRNG {
    public static final LocationRNG instance = new LocationRNG();
    private static final float INT_TO_FLOAT = Float.intBitsToFloat(864026624);
    private long _s0;
    private long _s1;
    private long state;

    public void setSeed(long long0) {
        this.state = long0;
        this._s0 = this.nextSplitMix64();
        this._s1 = this.nextSplitMix64();
    }

    public long getSeed() {
        return this.state;
    }

    private long nextSplitMix64() {
        long long0 = this.state += -7046029254386353131L;
        long0 = (long0 ^ long0 >>> 30) * -4658895280553007687L;
        long0 = (long0 ^ long0 >>> 27) * -7723592293110705685L;
        return long0 ^ long0 >>> 31;
    }

    public float nextFloat() {
        return (this.nextInt() >>> 8) * INT_TO_FLOAT;
    }

    private int nextInt() {
        long long0 = this._s0;
        long long1 = this._s1;
        long long2 = long0 + long1;
        long1 ^= long0;
        this._s0 = Long.rotateLeft(long0, 55) ^ long1 ^ long1 << 14;
        this._s1 = Long.rotateLeft(long1, 36);
        return (int)(long2 & -1L);
    }

    public int nextInt(int int0) {
        long long0 = this.nextInt() >>> 1;
        long0 = long0 * int0 >> 31;
        return (int)long0;
    }

    public int nextInt(int int3, int int0, int int1, int int2) {
        this.setSeed((long)int2 << 16 | (long)int1 << 32 | int0);
        return this.nextInt(int3);
    }
}
