// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;

public class Sphere extends Quadric {
    public void draw(float float3, int int1, int int0) {
        boolean boolean0 = super.normals != 100002;
        float float0;
        if (super.orientation == 100021) {
            float0 = -1.0F;
        } else {
            float0 = 1.0F;
        }

        float float1 = (float) Math.PI / int0;
        float float2 = (float) (Math.PI * 2) / int1;
        if (super.drawStyle == 100012) {
            if (!super.textureFlag) {
                GL11.glBegin(6);
                GL11.glNormal3f(0.0F, 0.0F, 1.0F);
                GL11.glVertex3f(0.0F, 0.0F, float0 * float3);

                for (int int2 = 0; int2 <= int1; int2++) {
                    float float4 = int2 == int1 ? 0.0F : int2 * float2;
                    float float5 = -this.sin(float4) * this.sin(float1);
                    float float6 = this.cos(float4) * this.sin(float1);
                    float float7 = float0 * this.cos(float1);
                    if (boolean0) {
                        GL11.glNormal3f(float5 * float0, float6 * float0, float7 * float0);
                    }

                    GL11.glVertex3f(float5 * float3, float6 * float3, float7 * float3);
                }

                GL11.glEnd();
            }

            float float8 = 1.0F / int1;
            float float9 = 1.0F / int0;
            float float10 = 1.0F;
            byte byte0;
            int int3;
            if (super.textureFlag) {
                byte0 = 0;
                int3 = int0;
            } else {
                byte0 = 1;
                int3 = int0 - 1;
            }

            for (int int4 = byte0; int4 < int3; int4++) {
                float float11 = int4 * float1;
                GL11.glBegin(8);
                float float12 = 0.0F;

                for (int int5 = 0; int5 <= int1; int5++) {
                    float float13 = int5 == int1 ? 0.0F : int5 * float2;
                    float float14 = -this.sin(float13) * this.sin(float11);
                    float float15 = this.cos(float13) * this.sin(float11);
                    float float16 = float0 * this.cos(float11);
                    if (boolean0) {
                        GL11.glNormal3f(float14 * float0, float15 * float0, float16 * float0);
                    }

                    this.TXTR_COORD(float12, float10);
                    GL11.glVertex3f(float14 * float3, float15 * float3, float16 * float3);
                    float14 = -this.sin(float13) * this.sin(float11 + float1);
                    float15 = this.cos(float13) * this.sin(float11 + float1);
                    float16 = float0 * this.cos(float11 + float1);
                    if (boolean0) {
                        GL11.glNormal3f(float14 * float0, float15 * float0, float16 * float0);
                    }

                    this.TXTR_COORD(float12, float10 - float9);
                    float12 += float8;
                    GL11.glVertex3f(float14 * float3, float15 * float3, float16 * float3);
                }

                GL11.glEnd();
                float10 -= float9;
            }

            if (!super.textureFlag) {
                GL11.glBegin(6);
                GL11.glNormal3f(0.0F, 0.0F, -1.0F);
                GL11.glVertex3f(0.0F, 0.0F, -float3 * float0);
                float float17 = (float) Math.PI - float1;
                float float18 = 1.0F;

                for (int int6 = int1; int6 >= 0; int6--) {
                    float float19 = int6 == int1 ? 0.0F : int6 * float2;
                    float float20 = -this.sin(float19) * this.sin(float17);
                    float float21 = this.cos(float19) * this.sin(float17);
                    float float22 = float0 * this.cos(float17);
                    if (boolean0) {
                        GL11.glNormal3f(float20 * float0, float21 * float0, float22 * float0);
                    }

                    float18 -= float8;
                    GL11.glVertex3f(float20 * float3, float21 * float3, float22 * float3);
                }

                GL11.glEnd();
            }
        } else if (super.drawStyle != 100011 && super.drawStyle != 100013) {
            if (super.drawStyle == 100010) {
                GL11.glBegin(0);
                if (boolean0) {
                    GL11.glNormal3f(0.0F, 0.0F, float0);
                }

                GL11.glVertex3f(0.0F, 0.0F, float3);
                if (boolean0) {
                    GL11.glNormal3f(0.0F, 0.0F, -float0);
                }

                GL11.glVertex3f(0.0F, 0.0F, -float3);

                for (int int7 = 1; int7 < int0 - 1; int7++) {
                    float float23 = int7 * float1;

                    for (int int8 = 0; int8 < int1; int8++) {
                        float float24 = int8 * float2;
                        float float25 = this.cos(float24) * this.sin(float23);
                        float float26 = this.sin(float24) * this.sin(float23);
                        float float27 = this.cos(float23);
                        if (boolean0) {
                            GL11.glNormal3f(float25 * float0, float26 * float0, float27 * float0);
                        }

                        GL11.glVertex3f(float25 * float3, float26 * float3, float27 * float3);
                    }
                }

                GL11.glEnd();
            }
        } else {
            for (int int9 = 1; int9 < int0; int9++) {
                float float28 = int9 * float1;
                GL11.glBegin(2);

                for (int int10 = 0; int10 < int1; int10++) {
                    float float29 = int10 * float2;
                    float float30 = this.cos(float29) * this.sin(float28);
                    float float31 = this.sin(float29) * this.sin(float28);
                    float float32 = this.cos(float28);
                    if (boolean0) {
                        GL11.glNormal3f(float30 * float0, float31 * float0, float32 * float0);
                    }

                    GL11.glVertex3f(float30 * float3, float31 * float3, float32 * float3);
                }

                GL11.glEnd();
            }

            for (int int11 = 0; int11 < int1; int11++) {
                float float33 = int11 * float2;
                GL11.glBegin(3);

                for (int int12 = 0; int12 <= int0; int12++) {
                    float float34 = int12 * float1;
                    float float35 = this.cos(float33) * this.sin(float34);
                    float float36 = this.sin(float33) * this.sin(float34);
                    float float37 = this.cos(float34);
                    if (boolean0) {
                        GL11.glNormal3f(float35 * float0, float36 * float0, float37 * float0);
                    }

                    GL11.glVertex3f(float35 * float3, float36 * float3, float37 * float3);
                }

                GL11.glEnd();
            }
        }
    }
}
