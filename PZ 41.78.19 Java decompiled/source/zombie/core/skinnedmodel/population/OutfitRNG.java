// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.util.List;
import zombie.core.Color;
import zombie.core.ImmutableColor;
import zombie.util.LocationRNG;

public final class OutfitRNG {
    private static final ThreadLocal<LocationRNG> RNG = ThreadLocal.withInitial(LocationRNG::new);

    public static void setSeed(long long0) {
        RNG.get().setSeed(long0);
    }

    public static long getSeed() {
        return RNG.get().getSeed();
    }

    public static int Next(int int0) {
        return RNG.get().nextInt(int0);
    }

    public static int Next(int int1, int int0) {
        if (int0 == int1) {
            return int1;
        } else {
            if (int1 > int0) {
                int int2 = int1;
                int1 = int0;
                int0 = int2;
            }

            int int3 = RNG.get().nextInt(int0 - int1);
            return int3 + int1;
        }
    }

    public static float Next(float float1, float float0) {
        if (float0 == float1) {
            return float1;
        } else {
            if (float1 > float0) {
                float float2 = float1;
                float1 = float0;
                float0 = float2;
            }

            return float1 + RNG.get().nextFloat() * (float0 - float1);
        }
    }

    public static boolean NextBool(int int0) {
        return Next(int0) == 0;
    }

    public static <E> E pickRandom(List<E> list) {
        if (list.isEmpty()) {
            return null;
        } else if (list.size() == 1) {
            return (E)list.get(0);
        } else {
            int int0 = Next(list.size());
            return (E)list.get(int0);
        }
    }

    public static ImmutableColor randomImmutableColor() {
        float float0 = Next(0.0F, 1.0F);
        float float1 = Next(0.0F, 0.6F);
        float float2 = Next(0.0F, 0.9F);
        Color color = Color.HSBtoRGB(float0, float1, float2);
        return new ImmutableColor(color);
    }
}
