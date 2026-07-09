// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import org.junit.Assert;
import org.junit.Test;
import zombie.iso.Vector2;

public class TestZombieInterpolate extends Assert {
    @Test
    public void test_predictor_stay() {
        NetworkCharacter networkCharacter = new NetworkCharacter();
        short short0 = 10000;
        short short1 = 250;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 1.0F;
        float float3 = -1.0F;
        networkCharacter.predict(short1, short0, float0, float1, float2, float3);

        for (int int0 = 0; int0 < 10; int0++) {
            NetworkCharacter.Transform transform0 = networkCharacter.predict(short1, short0, float0, float1, float2, float3);
            assertEquals(100.0F, transform0.position.x, 0.01F);
            assertEquals(200.0F, transform0.position.y, 0.01F);
        }

        for (int int1 = 0; int1 < 10; int1++) {
            short0 += short1;
            NetworkCharacter.Transform transform1 = networkCharacter.predict(short1, short0, float0, float1, float2, float3);
            assertEquals(100.0F, transform1.position.x, 0.01F);
            assertEquals(200.0F, transform1.position.y, 0.01F);
        }
    }

    @Test
    public void test_predictor_normal_go() {
        NetworkCharacter networkCharacter = new NetworkCharacter();
        short short0 = 10000;
        short short1 = 250;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 1.0F;
        float float3 = -1.0F;
        NetworkCharacter.Transform transform = networkCharacter.predict(short1, short0, float0, float1, float2, float3);
        assertEquals(100.0F, transform.position.x, 0.01F);
        assertEquals(200.0F, transform.position.y, 0.01F);

        for (int int0 = 0; int0 < 30; int0++) {
            short0 += short1;
            float0 += 10.0F;
            float1 -= 2.5F;
            transform = networkCharacter.predict(short1, short0, float0, float1, float2, float3);
            assertEquals(float0 + 10.0F, transform.position.x, 0.01F);
            assertEquals(float1 - 2.5F, transform.position.y, 0.01F);
        }
    }

    @Test
    public void test_predictor() {
        NetworkCharacter networkCharacter = new NetworkCharacter();
        int int0 = 10000;
        short short0 = 200;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 1.0F;
        float float3 = -1.0F;
        NetworkCharacter.Transform transform = networkCharacter.predict(short0, int0, float0, float1, float2, float3);
        assertEquals(100.0F, transform.position.x, 0.01F);
        assertEquals(200.0F, transform.position.y, 0.01F);
        int0 += short0;
        float0 += 200.0F;
        float1 += 100.0F;
        transform = networkCharacter.predict(short0, int0, float0, float1, float2, float3);
        assertEquals(500.0F, transform.position.x, 0.01F);
        assertEquals(400.0F, transform.position.y, 0.01F);
        int0 += 10000;
        float0 = 500.0F;
        float1 = 500.0F;
        transform = networkCharacter.predict(short0, int0, float0, float1, float2, float3);
        assertEquals(500.0F, transform.position.x, 0.01F);
        assertEquals(500.0F, transform.position.y, 0.01F);
        int0 += short0;
        float0 = 400.0F;
        float1 = 300.0F;
        transform = networkCharacter.predict(short0, int0, float0, float1, float2, float3);
        assertEquals(300.0F, transform.position.x, 0.01F);
        assertEquals(100.0F, transform.position.y, 0.01F);
    }

    @Test
    public void test_predictor_normal_rotate() {
        NetworkCharacter networkCharacter = new NetworkCharacter();
        short short0 = 10000;
        short short1 = 250;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 1.0F;
        float float3 = -1.0F;
        NetworkCharacter.Transform transform = networkCharacter.predict(short1, short0, float0, float1, float2, float3);
        assertEquals(100.0F, transform.position.x, 0.01F);
        assertEquals(200.0F, transform.position.y, 0.01F);

        for (int int0 = 0; int0 < 10; int0++) {
            short0 += short1;
            float0 += 10.0F;
            float1 -= 2.5F;
            transform = networkCharacter.predict(short1, short0, float0, float1, float2, float3);
            assertEquals(float0 + 10.0F, transform.position.x, 0.01F);
            assertEquals(float1 - 2.5F, transform.position.y, 0.01F);
        }

        for (int int1 = 0; int1 < 10; int1++) {
            short0 += short1;
            float0 -= 10.0F;
            float1 += 2.5F;
            transform = networkCharacter.predict(short1, short0, float0, float1, float2, float3);
            assertEquals(float0 - 10.0F, transform.position.x, 0.01F);
            assertEquals(float1 + 2.5F, transform.position.y, 0.01F);
        }
    }

    @Test
    public void test_reconstructor_stay() {
        NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
        NetworkCharacter.Transform transform = networkCharacter.transform;
        short short0 = 10000;
        short short1 = 250;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 1.0F;
        float float3 = -1.0F;
        networkCharacter.updateInterpolationPoint(short0, float0, float1, float2, float3);

        for (int int0 = 0; int0 < 10; int0++) {
            networkCharacter.updateInterpolationPoint(short0, float0, float1, float2, float3);
            transform = networkCharacter.reconstruct(short0, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            assertEquals(100.0F, transform.position.x, 0.01F);
            assertEquals(200.0F, transform.position.y, 0.01F);
        }

        for (int int1 = 0; int1 < 10; int1++) {
            short0 += short1;
            networkCharacter.updateInterpolationPoint(short0, float0, float1, float2, float3);
            transform = networkCharacter.reconstruct(short0, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            if (Float.isNaN(transform.position.x)) {
                assertEquals(100.0F, transform.position.x, 0.01F);
            }

            assertEquals(200.0F, transform.position.y, 0.01F);
        }
    }

    @Test
    public void test_reconstructor_normal_go() {
        NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
        NetworkCharacter.Transform transform = networkCharacter.transform;
        short short0 = 10000;
        int int0 = short0;
        short short1 = 250;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 4.0F;
        float float3 = -1.0F;
        networkCharacter.updateInterpolationPoint(short0, float0, float1, float2, float3);

        for (int int1 = 0; int1 < 30; int1++) {
            short0 += short1;
            float0 += 10.0F;
            float1 -= 2.5F;
            networkCharacter.updateInterpolationPoint(short0, float0, float1, float2, float3);
            transform = networkCharacter.reconstruct(int0, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);

            for (int int2 = 0; int2 < 5; int2++) {
                int0 += short1 / 5;
                transform = networkCharacter.reconstruct(int0, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out
                    .print(
                        "transform:("
                            + transform.position.x
                            + ", "
                            + transform.position.y
                            + ") rotation:("
                            + transform.rotation.x
                            + ", "
                            + transform.rotation.y
                            + ") t:"
                            + int0
                            + "\n"
                    );
                assertEquals(float0 + (int2 + 1) * 2.0F - 10.0F, transform.position.x, 0.9F);
                assertEquals(float1 - (int2 + 1) * 0.5F + 2.5F, transform.position.y, 0.9F);
            }
        }
    }

    @Test
    public void test_reconstructor_unnormal_go() {
        NetworkCharacter.Transform transform0 = new NetworkCharacter.Transform();
        transform0.position = new Vector2();
        transform0.rotation = new Vector2();
        NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
        NetworkCharacter.Transform transform1 = networkCharacter.transform;
        short short0 = 10000;
        int int0 = short0;
        short short1 = 250;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 4.0F;
        float float3 = -1.0F;
        System.out.print("update x:" + float0 + " y:" + float1 + " t:" + short0 + "\n");
        networkCharacter.updateInterpolationPoint(short0, float0, float1, float2, float3);
        int int1 = short0 + short1;
        float0 += 10.0F;
        float1 -= 2.5F;
        System.out.print("update x:" + float0 + " y:" + float1 + " t:" + int1 + "\n");
        networkCharacter.updateInterpolationPoint(int1, float0, float1, float2, float3);
        transform1 = networkCharacter.reconstruct(short0, transform1.position.x, transform1.position.y, transform1.rotation.x, transform1.rotation.y);

        for (int int2 = 0; int2 < 5; int2++) {
            int0 += short1 / 5;
            transform1 = networkCharacter.reconstruct(int0, transform1.position.x, transform1.position.y, transform1.rotation.x, transform1.rotation.y);
            System.out.print("transform:(" + transform1.position.x + ", " + transform1.position.y + ")\n");
            assertEquals(float0 + (int2 + 1) * 2.0F - 10.0F, transform1.position.x, 1.9F);
            assertEquals(float1 - (int2 + 1) * 0.5F + 2.5F, transform1.position.y, 1.5F);
        }

        for (int int3 = 0; int3 < 30; int3++) {
            int1 += short1;
            float0 += 10.0F;
            float1 -= 2.5F;
            System.out.print("update x:" + float0 + " y:" + float1 + " t:" + int1 + "\n");
            networkCharacter.updateInterpolationPoint(int1, float0, float1, float2, float3);

            for (int int4 = 0; int4 < 5; int4++) {
                int0 += short1 / 5;
                transform1 = networkCharacter.reconstruct(int0, transform1.position.x, transform1.position.y, transform1.rotation.x, transform1.rotation.y);
                System.out.print("transform:(" + transform1.position.x + ", " + transform1.position.y + ")\n");
                assertEquals(float0 + (int4 + 1) * 2.0F - 10.0F, transform1.position.x, 1.1F);
                assertEquals(float1 - (int4 + 1) * 0.5F + 2.5F, transform1.position.y, 1.1F);
                transform0.position.set(transform1.position);
                transform0.rotation.set(transform1.rotation);
            }
        }
    }

    @Test
    public void test_all() {
        NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
        NetworkCharacter.Transform transform = networkCharacter.transform;
        int int0 = 10000;
        int int1 = int0;
        short short0 = 250;
        float float0 = 100.0F;
        float float1 = 200.0F;
        float float2 = 0.04F;
        float float3 = -0.01F;
        System.out.print("update x:" + float0 + " y:" + float1 + " t:" + int0 + "\n");
        networkCharacter.updateInterpolationPoint(int0, float0, float1, float2, float3);
        System.out.print("Normal interpolate\n");

        for (int int2 = 0; int2 < 10; int2++) {
            int0 += short0;
            float0 += 10.0F;
            float1 -= 2.5F;
            System.out.print("update x:" + float0 + " y:" + float1 + " t:" + int0 + "\n");
            networkCharacter.updateInterpolationPoint(int0, float0, float1, float2, float3);

            for (int int3 = 0; int3 < 5; int3++) {
                int1 += short0 / 5;
                transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out
                    .print(
                        "transform:("
                            + transform.position.x
                            + ", "
                            + transform.position.y
                            + ") rotation:("
                            + transform.rotation.x
                            + ", "
                            + transform.rotation.y
                            + ") t:"
                            + int1
                            + "\n"
                    );
            }
        }

        System.out.print("Extrapolate\n");

        for (int int4 = 0; int4 < 20; int4++) {
            int1 += short0 / 5;
            transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            System.out
                .print(
                    "transform:("
                        + transform.position.x
                        + ", "
                        + transform.position.y
                        + ") rotation:("
                        + transform.rotation.x
                        + ", "
                        + transform.rotation.y
                        + ") t:"
                        + int1
                        + "\n"
                );
        }

        System.out.print("Teleport\n");
        int0 += short0 * 10;
        float0 += 100.0F;
        float1 -= 25.0F;
        System.out.print("update x:" + float0 + " y:" + float1 + " t:" + int0 + "\n");
        networkCharacter.updateInterpolationPoint(int0, float0, float1, float2, float3);

        for (int int5 = 0; int5 < 30; int5++) {
            int1 += short0 / 5;
            transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            System.out
                .print(
                    "transform:("
                        + transform.position.x
                        + ", "
                        + transform.position.y
                        + ") rotation:("
                        + transform.rotation.x
                        + ", "
                        + transform.rotation.y
                        + ") t:"
                        + int1
                        + "\n"
                );
        }

        System.out.print("Normal interpolate\n");

        for (int int6 = 0; int6 < 10; int6++) {
            int0 += short0;
            float0 += 10.0F;
            float1 -= 2.5F;
            System.out.print("update x:" + float0 + " y:" + float1 + " t:" + int0 + "\n");
            networkCharacter.updateInterpolationPoint(int0, float0, float1, float2, float3);

            for (int int7 = 0; int7 < 5; int7++) {
                int1 += short0 / 5;
                transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out
                    .print(
                        "transform:("
                            + transform.position.x
                            + ", "
                            + transform.position.y
                            + ") rotation:("
                            + transform.rotation.x
                            + ", "
                            + transform.rotation.y
                            + ") t:"
                            + int1
                            + "\n"
                    );
            }
        }

        System.out.print("Extrapolate\n");

        for (int int8 = 0; int8 < 20; int8++) {
            int1 += short0;
            transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
            System.out
                .print(
                    "transform:("
                        + transform.position.x
                        + ", "
                        + transform.position.y
                        + ") rotation:("
                        + transform.rotation.x
                        + ", "
                        + transform.rotation.y
                        + ") t:"
                        + int1
                        + "\n"
                );
        }

        int0 += short0 * 20;
        float0 += 200.0F;
        float1 -= 50.0F;
        System.out.print("Normal interpolate\n");

        for (int int9 = 0; int9 < 10; int9++) {
            int0 += short0;
            float0 += 10.0F;
            float1 -= 2.5F;
            System.out.print("update x:" + float0 + " y:" + float1 + " t:" + int0 + "\n");
            networkCharacter.updateInterpolationPoint(int0, float0, float1, float2, float3);

            for (int int10 = 0; int10 < 5; int10++) {
                int1 += short0 / 5;
                transform = networkCharacter.reconstruct(int1, transform.position.x, transform.position.y, transform.rotation.x, transform.rotation.y);
                System.out
                    .print(
                        "transform:("
                            + transform.position.x
                            + ", "
                            + transform.position.y
                            + ") rotation:("
                            + transform.rotation.x
                            + ", "
                            + transform.rotation.y
                            + ") t:"
                            + int1
                            + "\n"
                    );
            }
        }
    }

    @Test
    public void test_case1() {
        NetworkCharacter.Transform transform0 = new NetworkCharacter.Transform();
        transform0.position = new Vector2();
        transform0.rotation = new Vector2();
        long[] longs = new long[]{
            982999607L,
            982999623L,
            982999640L,
            982999656L,
            982999674L,
            982999690L,
            982999706L,
            982999723L,
            982999740L,
            982999756L,
            982999773L,
            982999791L,
            982999807L,
            982999823L,
            982999840L,
            982999856L,
            982999872L
        };
        NetworkCharacter networkCharacter = new NetworkCharacter(0.0F, 100.0F, 0L);
        NetworkCharacter.Transform transform1 = networkCharacter.transform;
        System.out.print("update x:10593.158 y:9952.486 t:982998656\n");
        System.out.print("update x:10593.23 y:9950.746 t:982999872\n");
        networkCharacter.updateInterpolationPoint(982998656, 10593.158F, 9952.486F, 0.0F, -0.0014706347F);
        networkCharacter.updateInterpolationPoint(982999872, 10593.23F, 9950.746F, 0.0F, -0.0014323471F);
        int int0 = (int)longs[0];

        for (long long0 : longs) {
            transform1 = networkCharacter.reconstruct((int)long0, transform1.position.x, transform1.position.y, transform1.rotation.x, transform1.rotation.y);
            System.out
                .print(
                    "transform:("
                        + transform1.position.x
                        + ", "
                        + transform1.position.y
                        + ") rotation:("
                        + transform1.rotation.x
                        + ", "
                        + transform1.rotation.y
                        + ") t:"
                        + long0
                        + " t':"
                        + (long0 - int0)
                        + "\n"
                );
            if (long0 > longs[0]) {
            }

            transform0.position.set(transform1.position);
            transform0.rotation.set(transform1.rotation);
            int0 = (int)long0;
        }
    }
}
