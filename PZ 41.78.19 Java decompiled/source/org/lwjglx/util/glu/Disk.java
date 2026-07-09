// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;

public class Disk extends Quadric {
    public void draw(float float3, float float2, int int0, int int1) {
        if (super.normals != 100002) {
            if (super.orientation == 100020) {
                GL11.glNormal3f(0.0F, 0.0F, 1.0F);
            } else {
                GL11.glNormal3f(0.0F, 0.0F, -1.0F);
            }
        }

        float float0 = (float) (Math.PI * 2) / int0;
        float float1 = (float2 - float3) / int1;
        switch (super.drawStyle) {
            case 100010:
                GL11.glBegin(0);

                for (int int2 = 0; int2 < int0; int2++) {
                    float float10 = int2 * float0;
                    float float11 = this.sin(float10);
                    float float12 = this.cos(float10);

                    for (int int3 = 0; int3 <= int1; int3++) {
                        float float13 = float3 * int3 * float1;
                        GL11.glVertex2f(float13 * float11, float13 * float12);
                    }
                }

                GL11.glEnd();
                break;
            case 100011:
                for (int int4 = 0; int4 <= int1; int4++) {
                    float float14 = float3 + int4 * float1;
                    GL11.glBegin(2);

                    for (int int5 = 0; int5 < int0; int5++) {
                        float float15 = int5 * float0;
                        GL11.glVertex2f(float14 * this.sin(float15), float14 * this.cos(float15));
                    }

                    GL11.glEnd();
                }

                for (int int6 = 0; int6 < int0; int6++) {
                    float float16 = int6 * float0;
                    float float17 = this.sin(float16);
                    float float18 = this.cos(float16);
                    GL11.glBegin(3);

                    for (int int7 = 0; int7 <= int1; int7++) {
                        float float19 = float3 + int7 * float1;
                        GL11.glVertex2f(float19 * float17, float19 * float18);
                    }

                    GL11.glEnd();
                }
                break;
            case 100012:
                float float20 = 2.0F * float2;
                float float21 = float3;

                for (int int8 = 0; int8 < int1; int8++) {
                    float float22 = float21 + float1;
                    if (super.orientation == 100020) {
                        GL11.glBegin(8);

                        for (int int9 = 0; int9 <= int0; int9++) {
                            float float23;
                            if (int9 == int0) {
                                float23 = 0.0F;
                            } else {
                                float23 = int9 * float0;
                            }

                            float float24 = this.sin(float23);
                            float float25 = this.cos(float23);
                            this.TXTR_COORD(0.5F + float24 * float22 / float20, 0.5F + float25 * float22 / float20);
                            GL11.glVertex2f(float22 * float24, float22 * float25);
                            this.TXTR_COORD(0.5F + float24 * float21 / float20, 0.5F + float25 * float21 / float20);
                            GL11.glVertex2f(float21 * float24, float21 * float25);
                        }

                        GL11.glEnd();
                    } else {
                        GL11.glBegin(8);

                        for (int int10 = int0; int10 >= 0; int10--) {
                            float float26;
                            if (int10 == int0) {
                                float26 = 0.0F;
                            } else {
                                float26 = int10 * float0;
                            }

                            float float27 = this.sin(float26);
                            float float28 = this.cos(float26);
                            this.TXTR_COORD(0.5F - float27 * float22 / float20, 0.5F + float28 * float22 / float20);
                            GL11.glVertex2f(float22 * float27, float22 * float28);
                            this.TXTR_COORD(0.5F - float27 * float21 / float20, 0.5F + float28 * float21 / float20);
                            GL11.glVertex2f(float21 * float27, float21 * float28);
                        }

                        GL11.glEnd();
                    }

                    float21 = float22;
                }
                break;
            case 100013:
                if (float3 != 0.0) {
                    GL11.glBegin(2);

                    for (float float4 = 0.0F; float4 < (float) (Math.PI * 2); float4 += float0) {
                        float float5 = float3 * this.sin(float4);
                        float float6 = float3 * this.cos(float4);
                        GL11.glVertex2f(float5, float6);
                    }

                    GL11.glEnd();
                }

                GL11.glBegin(2);

                for (float float7 = 0.0F; float7 < (float) (Math.PI * 2); float7 += float0) {
                    float float8 = float2 * this.sin(float7);
                    float float9 = float2 * this.cos(float7);
                    GL11.glVertex2f(float8, float9);
                }

                GL11.glEnd();
                break;
            default:
                return;
        }
    }
}
