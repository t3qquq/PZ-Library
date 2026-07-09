// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;

public class MipMap extends Util {
    public static int gluBuild2DMipmaps(int int11, int int12, int int1, int int0, int int3, int int4, ByteBuffer byteBuffer1) {
        if (int1 >= 1 && int0 >= 1) {
            int int2 = bytesPerPixel(int3, int4);
            if (int2 == 0) {
                return 100900;
            } else {
                int int5 = GL11.glGetInteger(3379);
                int int6 = nearestPower(int1);
                if (int6 > int5) {
                    int6 = int5;
                }

                int int7 = nearestPower(int0);
                if (int7 > int5) {
                    int7 = int5;
                }

                PixelStoreState pixelStoreState = new PixelStoreState();
                GL11.glPixelStorei(3330, 0);
                GL11.glPixelStorei(3333, 1);
                GL11.glPixelStorei(3331, 0);
                GL11.glPixelStorei(3332, 0);
                int int8 = 0;
                boolean boolean0 = false;
                ByteBuffer byteBuffer0;
                if (int6 == int1 && int7 == int0) {
                    byteBuffer0 = byteBuffer1;
                } else {
                    byteBuffer0 = BufferUtils.createByteBuffer((int6 + 4) * int7 * int2);
                    int int9 = gluScaleImage(int3, int1, int0, int4, byteBuffer1, int6, int7, int4, byteBuffer0);
                    if (int9 != 0) {
                        int8 = int9;
                        boolean0 = true;
                    }

                    GL11.glPixelStorei(3314, 0);
                    GL11.glPixelStorei(3317, 1);
                    GL11.glPixelStorei(3315, 0);
                    GL11.glPixelStorei(3316, 0);
                }

                ByteBuffer byteBuffer2 = null;
                ByteBuffer byteBuffer3 = null;

                for (int int10 = 0; !boolean0; int10++) {
                    if (byteBuffer0 != byteBuffer1) {
                        GL11.glPixelStorei(3314, 0);
                        GL11.glPixelStorei(3317, 1);
                        GL11.glPixelStorei(3315, 0);
                        GL11.glPixelStorei(3316, 0);
                    }

                    GL11.glTexImage2D(int11, int10, int12, int6, int7, 0, int3, int4, byteBuffer0);
                    if (int6 == 1 && int7 == 1) {
                        break;
                    }

                    int int13 = int6 < 2 ? 1 : int6 >> 1;
                    int int14 = int7 < 2 ? 1 : int7 >> 1;
                    ByteBuffer byteBuffer4;
                    if (byteBuffer2 == null) {
                        byteBuffer4 = byteBuffer2 = BufferUtils.createByteBuffer((int13 + 4) * int14 * int2);
                    } else if (byteBuffer3 == null) {
                        byteBuffer4 = byteBuffer3 = BufferUtils.createByteBuffer((int13 + 4) * int14 * int2);
                    } else {
                        byteBuffer4 = byteBuffer3;
                    }

                    int int15 = gluScaleImage(int3, int6, int7, int4, byteBuffer0, int13, int14, int4, byteBuffer4);
                    if (int15 != 0) {
                        int8 = int15;
                        boolean0 = true;
                    }

                    byteBuffer0 = byteBuffer4;
                    if (byteBuffer3 != null) {
                        byteBuffer3 = byteBuffer2;
                    }

                    int6 = int13;
                    int7 = int14;
                }

                pixelStoreState.save();
                return int8;
            }
        } else {
            return 100901;
        }
    }

    public static int gluScaleImage(int int1, int int2, int int3, int int6, ByteBuffer byteBuffer0, int int4, int int5, int int7, ByteBuffer byteBuffer1) {
        int int0 = compPerPix(int1);
        if (int0 == -1) {
            return 100900;
        } else {
            float[] floats0 = new float[int2 * int3 * int0];
            float[] floats1 = new float[int4 * int5 * int0];
            byte byte0;
            switch (int6) {
                case 5121:
                    byte0 = 1;
                    break;
                case 5126:
                    byte0 = 4;
                    break;
                default:
                    return 1280;
            }

            byte byte1;
            switch (int7) {
                case 5121:
                    byte1 = 1;
                    break;
                case 5126:
                    byte1 = 4;
                    break;
                default:
                    return 1280;
            }

            PixelStoreState pixelStoreState = new PixelStoreState();
            int int8;
            if (pixelStoreState.unpackRowLength > 0) {
                int8 = pixelStoreState.unpackRowLength;
            } else {
                int8 = int2;
            }

            int int9;
            if (byte0 >= pixelStoreState.unpackAlignment) {
                int9 = int0 * int8;
            } else {
                int9 = pixelStoreState.unpackAlignment / byte0 * ceil(int0 * int8 * byte0, pixelStoreState.unpackAlignment);
            }

            switch (int6) {
                case 5121:
                    int int14 = 0;
                    byteBuffer0.rewind();

                    for (int int15 = 0; int15 < int3; int15++) {
                        int int16 = int15 * int9 + pixelStoreState.unpackSkipRows * int9 + pixelStoreState.unpackSkipPixels * int0;

                        for (int int17 = 0; int17 < int2 * int0; int17++) {
                            floats0[int14++] = byteBuffer0.get(int16++) & 255;
                        }
                    }
                    break;
                case 5126:
                    int int10 = 0;
                    byteBuffer0.rewind();

                    for (int int11 = 0; int11 < int3; int11++) {
                        int int12 = 4 * (int11 * int9 + pixelStoreState.unpackSkipRows * int9 + pixelStoreState.unpackSkipPixels * int0);

                        for (int int13 = 0; int13 < int2 * int0; int13++) {
                            floats0[int10++] = byteBuffer0.getFloat(int12);
                            int12 += 4;
                        }
                    }
                    break;
                default:
                    return 100900;
            }

            float float0 = (float)int2 / int4;
            float float1 = (float)int3 / int5;
            float[] floats2 = new float[int0];

            for (int int18 = 0; int18 < int5; int18++) {
                for (int int19 = 0; int19 < int4; int19++) {
                    int int20 = (int)(int19 * float0);
                    int int21 = (int)((int19 + 1) * float0);
                    int int22 = (int)(int18 * float1);
                    int int23 = (int)((int18 + 1) * float1);
                    int int24 = 0;

                    for (int int25 = 0; int25 < int0; int25++) {
                        floats2[int25] = 0.0F;
                    }

                    for (int int26 = int20; int26 < int21; int26++) {
                        for (int int27 = int22; int27 < int23; int27++) {
                            int int28 = (int27 * int2 + int26) * int0;

                            for (int int29 = 0; int29 < int0; int29++) {
                                floats2[int29] += floats0[int28 + int29];
                            }

                            int24++;
                        }
                    }

                    int int30 = (int18 * int4 + int19) * int0;
                    if (int24 == 0) {
                        int int31 = (int22 * int2 + int20) * int0;

                        for (int int32 = 0; int32 < int0; int32++) {
                            floats1[int30++] = floats0[int31 + int32];
                        }
                    } else {
                        for (int int33 = 0; int33 < int0; int33++) {
                            floats1[int30++] = floats2[int33] / int24;
                        }
                    }
                }
            }

            if (pixelStoreState.packRowLength > 0) {
                int8 = pixelStoreState.packRowLength;
            } else {
                int8 = int4;
            }

            if (byte1 >= pixelStoreState.packAlignment) {
                int9 = int0 * int8;
            } else {
                int9 = pixelStoreState.packAlignment / byte1 * ceil(int0 * int8 * byte1, pixelStoreState.packAlignment);
            }

            switch (int7) {
                case 5121:
                    int int38 = 0;

                    for (int int39 = 0; int39 < int5; int39++) {
                        int int40 = int39 * int9 + pixelStoreState.packSkipRows * int9 + pixelStoreState.packSkipPixels * int0;

                        for (int int41 = 0; int41 < int4 * int0; int41++) {
                            byteBuffer1.put(int40++, (byte)floats1[int38++]);
                        }
                    }
                    break;
                case 5126:
                    int int34 = 0;

                    for (int int35 = 0; int35 < int5; int35++) {
                        int int36 = 4 * (int35 * int9 + pixelStoreState.unpackSkipRows * int9 + pixelStoreState.unpackSkipPixels * int0);

                        for (int int37 = 0; int37 < int4 * int0; int37++) {
                            byteBuffer1.putFloat(int36, floats1[int34++]);
                            int36 += 4;
                        }
                    }
                    break;
                default:
                    return 100900;
            }

            return 0;
        }
    }
}
