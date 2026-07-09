// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import org.uncommons.maths.random.CellularAutomatonRNG;
import org.uncommons.maths.random.SecureRandomSeedGenerator;
import org.uncommons.maths.random.SeedException;
import org.uncommons.maths.random.SeedGenerator;
import zombie.network.GameServer;

public final class Rand {
    public static CellularAutomatonRNG rand;
    public static CellularAutomatonRNG randlua;
    public static int id = 0;

    public static void init() {
        try {
            rand = new CellularAutomatonRNG(new Rand.PZSeedGenerator());
            randlua = new CellularAutomatonRNG(new Rand.PZSeedGenerator());
        } catch (SeedException seedException) {
            seedException.printStackTrace();
        }
    }

    private static int Next(int int0, CellularAutomatonRNG cellularAutomatonRNG) {
        if (int0 <= 0) {
            return 0;
        } else {
            id++;
            if (id >= 10000) {
                id = 0;
            }

            return cellularAutomatonRNG.nextInt(int0);
        }
    }

    public static int Next(int int0) {
        return Next(int0, rand);
    }

    public static long Next(long long0, CellularAutomatonRNG cellularAutomatonRNG) {
        return Next((int)long0, cellularAutomatonRNG);
    }

    public static long Next(long long0) {
        return Next(long0, rand);
    }

    public static int Next(int int1, int int0, CellularAutomatonRNG cellularAutomatonRNG) {
        if (int0 == int1) {
            return int1;
        } else {
            if (int1 > int0) {
                int int2 = int1;
                int1 = int0;
                int0 = int2;
            }

            id++;
            if (id >= 10000) {
                id = 0;
            }

            int int3 = cellularAutomatonRNG.nextInt(int0 - int1);
            return int3 + int1;
        }
    }

    public static int Next(int int0, int int1) {
        return Next(int0, int1, rand);
    }

    public static long Next(long long1, long long0, CellularAutomatonRNG cellularAutomatonRNG) {
        if (long0 == long1) {
            return long1;
        } else {
            if (long1 > long0) {
                long long2 = long1;
                long1 = long0;
                long0 = long2;
            }

            id++;
            if (id >= 10000) {
                id = 0;
            }

            int int0 = cellularAutomatonRNG.nextInt((int)(long0 - long1));
            return int0 + long1;
        }
    }

    public static long Next(long long0, long long1) {
        return Next(long0, long1, rand);
    }

    public static float Next(float float1, float float0, CellularAutomatonRNG cellularAutomatonRNG) {
        if (float0 == float1) {
            return float1;
        } else {
            if (float1 > float0) {
                float float2 = float1;
                float1 = float0;
                float0 = float2;
            }

            id++;
            if (id >= 10000) {
                id = 0;
            }

            return float1 + cellularAutomatonRNG.nextFloat() * (float0 - float1);
        }
    }

    public static float Next(float float0, float float1) {
        return Next(float0, float1, rand);
    }

    public static boolean NextBool(int int0) {
        return Next(int0) == 0;
    }

    public static int AdjustForFramerate(int int0) {
        if (GameServer.bServer) {
            int0 = (int)(int0 * 0.33333334F);
        } else {
            int0 = (int)(int0 * (PerformanceSettings.getLockFPS() / 30.0F));
        }

        return int0;
    }

    public static final class PZSeedGenerator implements SeedGenerator {
        private static final SeedGenerator[] GENERATORS = new SeedGenerator[]{new SecureRandomSeedGenerator()};

        private PZSeedGenerator() {
        }

        @Override
        public byte[] generateSeed(int int0) {
            for (SeedGenerator seedGenerator : GENERATORS) {
                try {
                    return seedGenerator.generateSeed(int0);
                } catch (SeedException seedException) {
                }
            }

            throw new IllegalStateException("All available seed generation strategies failed.");
        }
    }
}
