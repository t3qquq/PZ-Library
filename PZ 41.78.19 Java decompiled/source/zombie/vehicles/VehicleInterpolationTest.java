// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class VehicleInterpolationTest extends Assert {
    final VehicleInterpolation interpolation = new VehicleInterpolation();
    final float[] physics = new float[27];
    final float[] engineSound = new float[2];
    final ByteBuffer bb = ByteBuffer.allocateDirect(255);
    final int tick = 100;
    final int delay = 300;
    final int history = 200;
    final int bufferingIterations = 4;
    @Rule
    public TestRule watchman = new TestWatcher() {
        protected void failed(Throwable var1, Description var2) {
            System.out.println("interpolation.buffer:");
            System.out.print("TIME: ");

            for (VehicleInterpolationData vehicleInterpolationData0 : VehicleInterpolationTest.this.interpolation.buffer) {
                System.out.print(String.format(" %5d", vehicleInterpolationData0.time));
            }

            System.out.println();
            System.out.print("   X: ");

            for (VehicleInterpolationData vehicleInterpolationData1 : VehicleInterpolationTest.this.interpolation.buffer) {
                System.out.print(String.format(" %5.0f", vehicleInterpolationData1.x));
            }
        }
    };

    @Before
    public void setup() {
        this.interpolation.clear();
        this.interpolation.delay = 300;
        this.interpolation.history = 500;
        this.interpolation.reset();
    }

    @Test
    public void normalTest() {
        long long0 = 9223372036853775807L;

        for (int int0 = 1; int0 < 30; int0++) {
            this.bb.position(0);
            this.interpolation.interpolationDataAdd(this.bb, long0, int0 * 2, int0 * 2, 0.0F, long0);
            boolean boolean0 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, long0 - 298L);
            if (int0 < 4) {
                assertFalse(boolean0);
            } else {
                assertTrue(boolean0);
                assertEquals((int0 - 4 + 1) * 2.0F, this.physics[0], 0.2F);
            }

            boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, long0 - 298L + 50L);
            if (int0 < 4) {
                assertFalse(boolean0);
            } else {
                assertTrue(boolean0);
                assertEquals((int0 - 4 + 1) * 2.0F + 1.0F, this.physics[0], 0.2F);
            }

            long0 += 100L;
        }
    }

    @Test
    public void interpolationTest() {
        byte byte0 = 0;

        for (int int0 = 1; int0 < 30; int0++) {
            this.bb.position(0);
            if (int0 % 2 == 1) {
                this.interpolation.interpolationDataAdd(this.bb, byte0, int0, int0, 0.0F, byte0);
            }

            boolean boolean0 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, byte0 - 298);
            if (int0 < 4) {
                assertFalse(boolean0);
            } else {
                assertTrue(boolean0);
                assertEquals(int0 - 4 + 1, this.physics[0], 0.2F);
            }

            byte0 += 100;
        }
    }

    @Test
    public void interpolationMicroStepTest() {
        byte byte0 = 0;

        for (int int0 = 1; int0 < 30; int0++) {
            this.bb.position(0);
            this.interpolation.interpolationDataAdd(this.bb, byte0, int0, int0, 0.0F, byte0);
            boolean boolean0 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, byte0 - 298);
            if (int0 < 4) {
                assertFalse(boolean0);
            } else {
                assertTrue(boolean0);
                assertEquals(int0 - 4 + 1, this.physics[0], 0.2F);
            }

            byte0 += 100;
        }

        for (int int1 = 30; int1 < 35; int1++) {
            this.interpolation.interpolationDataAdd(this.bb, byte0, int1, int1, 0.0F, byte0);

            for (int int2 = 0; int2 < 100; int2++) {
                boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, byte0 - 300 + 100 * int2 / 100);
                assertTrue(boolean1);
                assertEquals(int1 - 4 + 1 + int2 / 100.0F, this.physics[0], 0.001F);
            }

            byte0 += 100;
        }
    }

    @Test
    public void interpolationMicroStepTest2() {
        long long0 = 0L;
        byte byte0 = 50;

        for (int int0 = 1; int0 < 30; int0++) {
            this.bb.position(0);
            this.interpolation.interpolationDataAdd(this.bb, long0, int0, int0, 0.0F, long0);
            boolean boolean0 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, long0 - 298L);
            System.out.println(int0 + "   " + long0 + " " + boolean0 + " " + this.physics[0]);
            long0 += byte0;
        }

        for (int int1 = 30; int1 < 35; int1++) {
            this.interpolation.interpolationDataAdd(this.bb, long0, int1, int1, 0.0F, long0);

            for (int int2 = 0; int2 < 10; int2++) {
                boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, long0 - 300L + byte0 * int2 / 10);
                System.out
                    .println(
                        int1 + "." + int2 + " " + (long0 + byte0 * int2 / 10) + " " + boolean1 + " " + this.physics[0] + " " + (int1 - 6.0F + int2 / 10.0F)
                    );
                assertTrue(boolean1);
                assertEquals(int1 - 6.0F + int2 / 10.0F, this.physics[0], 0.001F);
            }

            long0 += byte0;
        }
    }

    @Test
    public void testBufferRestoring() {
        int int0 = 0;

        for (int int1 = 1; int1 < 30; int1++) {
            this.bb.position(0);
            this.interpolation.interpolationDataAdd(this.bb, int0, int1, int1, 0.0F, int0);
            boolean boolean0 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, int0 - 298);
            System.out.println(int1 + " " + int0 + " " + boolean0 + " " + this.physics[0]);
            if (int1 >= 4 && (int1 <= 10 || int1 >= 14)) {
                assertTrue(boolean0);
                assertEquals(int1 - 4 + 1, this.physics[0], 0.2F);
            }

            if (int1 == 10) {
                int0 += 500;
            }

            int0 += 100;
        }
    }

    @Test
    public void normalTestBufferRestoring2() {
        byte byte0 = 0;

        for (int int0 = 1; int0 < 100; int0++) {
            this.bb.position(0);
            boolean boolean0 = int0 < 15 || int0 > 21;
            if (boolean0) {
                this.interpolation.interpolationDataAdd(this.bb, byte0, int0, 0.0F, 0.0F, byte0);
            }

            boolean boolean1 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, byte0 - 298);
            System.out.println(int0 + " " + boolean1 + " " + this.physics[0]);
            if (int0 >= 4 && (int0 <= 17 || int0 >= 25)) {
                assertTrue(boolean1);
                if (int0 >= 17 && int0 <= 21) {
                    assertEquals(14.0F, this.physics[0], 0.1F);
                } else {
                    assertEquals(int0 - 4 + 1, this.physics[0], 0.1F);
                }
            } else {
                assertFalse(boolean1);
            }

            byte0 += 100;
        }
    }

    @Test
    public void normalTestBufferRestoring3() {
        byte byte0 = 0;

        for (int int0 = 1; int0 < 40; int0++) {
            this.bb.position(0);
            if (int0 != 10 && int0 != 12 && int0 != 13 && int0 != 15 && int0 != 16) {
                this.interpolation.interpolationDataAdd(this.bb, byte0, int0, 0.0F, 0.0F, byte0);
            }

            if (int0 > 26 && int0 < 33) {
                this.interpolation.interpolationDataAdd(this.bb, byte0 + 50, int0 + 0.5F, 0.0F, 0.0F, byte0);
            }

            boolean boolean0 = this.interpolation.interpolationDataGet(this.physics, this.engineSound, byte0 - 298);
            System.out.println(int0 + " " + boolean0 + " " + this.physics[0]);
            if (int0 < 4) {
                assertFalse(boolean0);
            } else {
                assertTrue(boolean0);
                assertEquals(int0 - 4 + 1, this.physics[0], 0.1F);
            }

            byte0 += 100;
        }
    }
}
