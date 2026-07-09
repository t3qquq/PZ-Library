// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;

public class PartialDisk extends Quadric {
    private static final int CACHE_SIZE = 240;

    public void draw(float arg0, float arg1, int arg2, int arg3, float arg4, float arg5) {
        float[] floats0 = new float[240];
        float[] floats1 = new float[240];
        float float0 = 0.0F;
        float float1 = 0.0F;
        if (arg2 >= 240) {
            arg2 = 239;
        }

        if (arg2 >= 2 && arg3 >= 1 && !(arg1 <= 0.0F) && !(arg0 < 0.0F) && !(arg0 > arg1)) {
            if (arg5 < -360.0F) {
                arg5 = 360.0F;
            }

            if (arg5 > 360.0F) {
                arg5 = 360.0F;
            }

            if (arg5 < 0.0F) {
                arg4 += arg5;
                arg5 = -arg5;
            }

            int int0;
            if (arg5 == 360.0F) {
                int0 = arg2;
            } else {
                int0 = arg2 + 1;
            }

            float float2 = arg1 - arg0;
            float float3 = arg4 / 180.0F * (float) Math.PI;

            for (int int1 = 0; int1 <= arg2; int1++) {
                float float4 = float3 + (float) Math.PI * arg5 / 180.0F * int1 / arg2;
                floats0[int1] = this.sin(float4);
                floats1[int1] = this.cos(float4);
            }

            if (arg5 == 360.0F) {
                floats0[arg2] = floats0[0];
                floats1[arg2] = floats1[0];
            }

            switch (super.normals) {
                case 100000:
                case 100001:
                    if (super.orientation == 100020) {
                        GL11.glNormal3f(0.0F, 0.0F, 1.0F);
                    } else {
                        GL11.glNormal3f(0.0F, 0.0F, -1.0F);
                    }
                case 100002:
            }

            switch (super.drawStyle) {
                case 100010:
                    GL11.glBegin(0);

                    for (int int11 = 0; int11 < int0; int11++) {
                        float float13 = floats0[int11];
                        float float14 = floats1[int11];

                        for (int int12 = 0; int12 <= arg3; int12++) {
                            float float15 = arg1 - float2 * ((float)int12 / arg3);
                            if (super.textureFlag) {
                                float0 = float15 / arg1 / 2.0F;
                                GL11.glTexCoord2f(float0 * floats0[int11] + 0.5F, float0 * floats1[int11] + 0.5F);
                            }

                            GL11.glVertex3f(float15 * float13, float15 * float14, 0.0F);
                        }
                    }

                    GL11.glEnd();
                    break;
                case 100011:
                    if (arg0 == arg1) {
                        GL11.glBegin(3);

                        for (int int6 = 0; int6 <= arg2; int6++) {
                            if (super.textureFlag) {
                                GL11.glTexCoord2f(floats0[int6] / 2.0F + 0.5F, floats1[int6] / 2.0F + 0.5F);
                            }

                            GL11.glVertex3f(arg0 * floats0[int6], arg0 * floats1[int6], 0.0F);
                        }

                        GL11.glEnd();
                    } else {
                        for (int int7 = 0; int7 <= arg3; int7++) {
                            float float9 = arg1 - float2 * ((float)int7 / arg3);
                            if (super.textureFlag) {
                                float0 = float9 / arg1 / 2.0F;
                            }

                            GL11.glBegin(3);

                            for (int int8 = 0; int8 <= arg2; int8++) {
                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float0 * floats0[int8] + 0.5F, float0 * floats1[int8] + 0.5F);
                                }

                                GL11.glVertex3f(float9 * floats0[int8], float9 * floats1[int8], 0.0F);
                            }

                            GL11.glEnd();
                        }

                        for (int int9 = 0; int9 < int0; int9++) {
                            float float10 = floats0[int9];
                            float float11 = floats1[int9];
                            GL11.glBegin(3);

                            for (int int10 = 0; int10 <= arg3; int10++) {
                                float float12 = arg1 - float2 * ((float)int10 / arg3);
                                if (super.textureFlag) {
                                    float0 = float12 / arg1 / 2.0F;
                                }

                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float0 * floats0[int9] + 0.5F, float0 * floats1[int9] + 0.5F);
                                }

                                GL11.glVertex3f(float12 * float10, float12 * float11, 0.0F);
                            }

                            GL11.glEnd();
                        }
                    }
                    break;
                case 100012:
                    int int13;
                    if (arg0 != 0.0F) {
                        int13 = arg3;
                    } else {
                        int13 = arg3 - 1;
                        GL11.glBegin(6);
                        if (super.textureFlag) {
                            GL11.glTexCoord2f(0.5F, 0.5F);
                        }

                        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
                        float float16 = arg1 - float2 * ((float)(arg3 - 1) / arg3);
                        if (super.textureFlag) {
                            float0 = float16 / arg1 / 2.0F;
                        }

                        if (super.orientation == 100020) {
                            for (int int14 = arg2; int14 >= 0; int14--) {
                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float0 * floats0[int14] + 0.5F, float0 * floats1[int14] + 0.5F);
                                }

                                GL11.glVertex3f(float16 * floats0[int14], float16 * floats1[int14], 0.0F);
                            }
                        } else {
                            for (int int15 = 0; int15 <= arg2; int15++) {
                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float0 * floats0[int15] + 0.5F, float0 * floats1[int15] + 0.5F);
                                }

                                GL11.glVertex3f(float16 * floats0[int15], float16 * floats1[int15], 0.0F);
                            }
                        }

                        GL11.glEnd();
                    }

                    for (int int16 = 0; int16 < int13; int16++) {
                        float float17 = arg1 - float2 * ((float)int16 / arg3);
                        float float18 = arg1 - float2 * ((float)(int16 + 1) / arg3);
                        if (super.textureFlag) {
                            float0 = float17 / arg1 / 2.0F;
                            float1 = float18 / arg1 / 2.0F;
                        }

                        GL11.glBegin(8);

                        for (int int17 = 0; int17 <= arg2; int17++) {
                            if (super.orientation == 100020) {
                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float0 * floats0[int17] + 0.5F, float0 * floats1[int17] + 0.5F);
                                }

                                GL11.glVertex3f(float17 * floats0[int17], float17 * floats1[int17], 0.0F);
                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float1 * floats0[int17] + 0.5F, float1 * floats1[int17] + 0.5F);
                                }

                                GL11.glVertex3f(float18 * floats0[int17], float18 * floats1[int17], 0.0F);
                            } else {
                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float1 * floats0[int17] + 0.5F, float1 * floats1[int17] + 0.5F);
                                }

                                GL11.glVertex3f(float18 * floats0[int17], float18 * floats1[int17], 0.0F);
                                if (super.textureFlag) {
                                    GL11.glTexCoord2f(float0 * floats0[int17] + 0.5F, float0 * floats1[int17] + 0.5F);
                                }

                                GL11.glVertex3f(float17 * floats0[int17], float17 * floats1[int17], 0.0F);
                            }
                        }

                        GL11.glEnd();
                    }
                    break;
                case 100013:
                    if (arg5 < 360.0F) {
                        for (int int2 = 0; int2 <= arg2; int2 += arg2) {
                            float float5 = floats0[int2];
                            float float6 = floats1[int2];
                            GL11.glBegin(3);

                            for (int int3 = 0; int3 <= arg3; int3++) {
                                float float7 = arg1 - float2 * ((float)int3 / arg3);
                                if (super.textureFlag) {
                                    float0 = float7 / arg1 / 2.0F;
                                    GL11.glTexCoord2f(float0 * floats0[int2] + 0.5F, float0 * floats1[int2] + 0.5F);
                                }

                                GL11.glVertex3f(float7 * float5, float7 * float6, 0.0F);
                            }

                            GL11.glEnd();
                        }
                    }

                    for (int int4 = 0; int4 <= arg3; int4 += arg3) {
                        float float8 = arg1 - float2 * ((float)int4 / arg3);
                        if (super.textureFlag) {
                            float0 = float8 / arg1 / 2.0F;
                        }

                        GL11.glBegin(3);

                        for (int int5 = 0; int5 <= arg2; int5++) {
                            if (super.textureFlag) {
                                GL11.glTexCoord2f(float0 * floats0[int5] + 0.5F, float0 * floats1[int5] + 0.5F);
                            }

                            GL11.glVertex3f(float8 * floats0[int5], float8 * floats1[int5], 0.0F);
                        }

                        GL11.glEnd();
                        if (arg0 == arg1) {
                            break;
                        }
                    }
            }
        } else {
            System.err.println("PartialDisk: GLU_INVALID_VALUE");
        }
    }
}
