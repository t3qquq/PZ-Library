// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;

public class Cylinder extends Quadric {
    public void draw(float arg0, float arg1, float arg2, int arg3, int arg4) {
        float float0;
        if (super.orientation == 100021) {
            float0 = -1.0F;
        } else {
            float0 = 1.0F;
        }

        float float1 = (float) (Math.PI * 2) / arg3;
        float float2 = (arg1 - arg0) / arg4;
        float float3 = arg2 / arg4;
        float float4 = (arg0 - arg1) / arg2;
        if (super.drawStyle == 100010) {
            GL11.glBegin(0);

            for (int int0 = 0; int0 < arg3; int0++) {
                float float5 = this.cos(int0 * float1);
                float float6 = this.sin(int0 * float1);
                this.normal3f(float5 * float0, float6 * float0, float4 * float0);
                float float7 = 0.0F;
                float float8 = arg0;

                for (int int1 = 0; int1 <= arg4; int1++) {
                    GL11.glVertex3f(float5 * float8, float6 * float8, float7);
                    float7 += float3;
                    float8 += float2;
                }
            }

            GL11.glEnd();
        } else if (super.drawStyle != 100011 && super.drawStyle != 100013) {
            if (super.drawStyle == 100012) {
                float float9 = 1.0F / arg3;
                float float10 = 1.0F / arg4;
                float float11 = 0.0F;
                float float12 = 0.0F;
                float float13 = arg0;

                for (int int2 = 0; int2 < arg4; int2++) {
                    float float14 = 0.0F;
                    GL11.glBegin(8);

                    for (int int3 = 0; int3 <= arg3; int3++) {
                        float float15;
                        float float16;
                        if (int3 == arg3) {
                            float15 = this.sin(0.0F);
                            float16 = this.cos(0.0F);
                        } else {
                            float15 = this.sin(int3 * float1);
                            float16 = this.cos(int3 * float1);
                        }

                        if (float0 == 1.0F) {
                            this.normal3f(float15 * float0, float16 * float0, float4 * float0);
                            this.TXTR_COORD(float14, float11);
                            GL11.glVertex3f(float15 * float13, float16 * float13, float12);
                            this.normal3f(float15 * float0, float16 * float0, float4 * float0);
                            this.TXTR_COORD(float14, float11 + float10);
                            GL11.glVertex3f(float15 * (float13 + float2), float16 * (float13 + float2), float12 + float3);
                        } else {
                            this.normal3f(float15 * float0, float16 * float0, float4 * float0);
                            this.TXTR_COORD(float14, float11);
                            GL11.glVertex3f(float15 * float13, float16 * float13, float12);
                            this.normal3f(float15 * float0, float16 * float0, float4 * float0);
                            this.TXTR_COORD(float14, float11 + float10);
                            GL11.glVertex3f(float15 * (float13 + float2), float16 * (float13 + float2), float12 + float3);
                        }

                        float14 += float9;
                    }

                    GL11.glEnd();
                    float13 += float2;
                    float11 += float10;
                    float12 += float3;
                }
            }
        } else {
            if (super.drawStyle == 100011) {
                float float17 = 0.0F;
                float float18 = arg0;

                for (int int4 = 0; int4 <= arg4; int4++) {
                    GL11.glBegin(2);

                    for (int int5 = 0; int5 < arg3; int5++) {
                        float float19 = this.cos(int5 * float1);
                        float float20 = this.sin(int5 * float1);
                        this.normal3f(float19 * float0, float20 * float0, float4 * float0);
                        GL11.glVertex3f(float19 * float18, float20 * float18, float17);
                    }

                    GL11.glEnd();
                    float17 += float3;
                    float18 += float2;
                }
            } else if (arg0 != 0.0) {
                GL11.glBegin(2);

                for (int int6 = 0; int6 < arg3; int6++) {
                    float float21 = this.cos(int6 * float1);
                    float float22 = this.sin(int6 * float1);
                    this.normal3f(float21 * float0, float22 * float0, float4 * float0);
                    GL11.glVertex3f(float21 * arg0, float22 * arg0, 0.0F);
                }

                GL11.glEnd();
                GL11.glBegin(2);

                for (int int7 = 0; int7 < arg3; int7++) {
                    float float23 = this.cos(int7 * float1);
                    float float24 = this.sin(int7 * float1);
                    this.normal3f(float23 * float0, float24 * float0, float4 * float0);
                    GL11.glVertex3f(float23 * arg1, float24 * arg1, arg2);
                }

                GL11.glEnd();
            }

            GL11.glBegin(1);

            for (int int8 = 0; int8 < arg3; int8++) {
                float float25 = this.cos(int8 * float1);
                float float26 = this.sin(int8 * float1);
                this.normal3f(float25 * float0, float26 * float0, float4 * float0);
                GL11.glVertex3f(float25 * arg0, float26 * arg0, 0.0F);
                GL11.glVertex3f(float25 * arg1, float26 * arg1, arg2);
            }

            GL11.glEnd();
        }
    }
}
