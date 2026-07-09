// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class Random {
    private final Random.Xorshiro128 rnd;
    private static long seedHalf = 8020463840L;

    public static long newSeed() {
        synchronized (Random.class) {
            long long0 = seedHalf;
            long long1 = long0 * 3512401965023503517L;
            seedHalf = long1;
            return long1;
        }
    }

    public Random() {
        this(newSeed() ^ System.nanoTime());
    }

    public Random(long long0) {
        this.rnd = new Random.Xorshiro128(long0);
    }

    public float nextFloat() {
        return this.rnd.nextFloat();
    }

    public int nextInt(int int0) {
        return this.rnd.nextInt(int0);
    }

    private static final class Xorshiro128 {
        private static final float INT_TO_FLOAT = Float.intBitsToFloat(864026624);
        private long _s0;
        private long _s1;
        private long state;

        Xorshiro128(long long0) {
            this.state = long0;
            this._s0 = this.nextSplitMix64();
            this._s1 = this.nextSplitMix64();
        }

        private long nextSplitMix64() {
            long long0 = this.state += -7046029254386353131L;
            long0 = (long0 ^ long0 >>> 30) * -4658895280553007687L;
            long0 = (long0 ^ long0 >>> 27) * -7723592293110705685L;
            return long0 ^ long0 >>> 31;
        }

        final float nextFloat() {
            return (this.nextInt() >>> 8) * INT_TO_FLOAT;
        }

        private int nextInt() {
            long long0 = this._s0;
            long long1 = this._s1;
            long long2 = long0 + long1;
            long1 ^= long0;
            this.rotateLeft(long0, long1);
            return (int)(long2 & -1L);
        }

        private static long rotl_JDK4(long long0, int int0) {
            return long0 << int0 | long0 >>> 64 - int0;
        }

        private static long rotl_JDK5(long long0, int int0) {
            return Long.rotateLeft(long0, int0);
        }

        private static long rotl(long long0, int int0) {
            return Runtime.HAS_Long_rotateLeft ? rotl_JDK5(long0, int0) : rotl_JDK4(long0, int0);
        }

        private void rotateLeft(long long1, long long0) {
            this._s0 = rotl(long1, 55) ^ long0 ^ long0 << 14;
            this._s1 = rotl(long0, 36);
        }

        final int nextInt(int int0) {
            long long0 = this.nextInt() >>> 1;
            long0 = long0 * int0 >> 31;
            return (int)long0;
        }
    }
}
