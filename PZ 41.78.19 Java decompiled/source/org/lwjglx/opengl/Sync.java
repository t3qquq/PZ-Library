// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.opengl;

import org.lwjglx.Sys;

class Sync {
    private static final long NANOS_IN_SECOND = 1000000000L;
    private static long nextFrame = 0L;
    private static boolean initialised = false;
    private static Sync.RunningAvg sleepDurations = new Sync.RunningAvg(10);
    private static Sync.RunningAvg yieldDurations = new Sync.RunningAvg(10);

    public static void sync(int int0) {
        if (int0 > 0) {
            if (!initialised) {
                initialise();
            }

            try {
                long long0 = getTime();

                while (nextFrame - long0 > sleepDurations.avg()) {
                    Thread.sleep(1L);
                    long long1;
                    sleepDurations.add((long1 = getTime()) - long0);
                    long0 = long1;
                }

                sleepDurations.dampenForLowResTicker();
                long0 = getTime();

                while (nextFrame - long0 > yieldDurations.avg()) {
                    Thread.yield();
                    long long2;
                    yieldDurations.add((long2 = getTime()) - long0);
                    long0 = long2;
                }
            } catch (InterruptedException interruptedException) {
            }

            nextFrame = Math.max(nextFrame + 1000000000L / int0, getTime());
        }
    }

    private static void initialise() {
        initialised = true;
        sleepDurations.init(1000000L);
        yieldDurations.init((int)(-(getTime() - getTime()) * 1.333));
        nextFrame = getTime();
        String string = System.getProperty("os.name");
        if (string.startsWith("Win")) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (Exception exception) {
                    }
                }
            });
            thread.setName("LWJGL Timer");
            thread.setDaemon(true);
            thread.start();
        }
    }

    private static long getTime() {
        return Sys.getTime() * 1000000000L / Sys.getTimerResolution();
    }

    private static class RunningAvg {
        private final long[] slots;
        private int offset;
        private static final long DAMPEN_THRESHOLD = 10000000L;
        private static final float DAMPEN_FACTOR = 0.9F;

        public RunningAvg(int int0) {
            this.slots = new long[int0];
            this.offset = 0;
        }

        public void init(long long0) {
            while (this.offset < this.slots.length) {
                this.slots[this.offset++] = long0;
            }
        }

        public void add(long long0) {
            this.slots[this.offset++ % this.slots.length] = long0;
            this.offset = this.offset % this.slots.length;
        }

        public long avg() {
            long long0 = 0L;

            for (int int0 = 0; int0 < this.slots.length; int0++) {
                long0 += this.slots[int0];
            }

            return long0 / this.slots.length;
        }

        public void dampenForLowResTicker() {
            if (this.avg() > 10000000L) {
                for (int int0 = 0; int0 < this.slots.length; int0++) {
                    this.slots[int0] = (long)((float)this.slots[int0] * 0.9F);
                }
            }
        }
    }
}
