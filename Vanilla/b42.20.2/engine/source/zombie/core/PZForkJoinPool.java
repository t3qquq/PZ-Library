// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.core;

import java.util.concurrent.ForkJoinPool;

public final class PZForkJoinPool {
    private static ForkJoinPool common;

    public static ForkJoinPool commonPool() {
        int parallelism = Runtime.getRuntime().availableProcessors() - 1;
        if (common == null) {
            common = new ForkJoinPool(parallelism);
        }

        return common;
    }
}
