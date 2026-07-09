// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

public interface GLUtessellatorCallback {
    void begin(int var1);

    void beginData(int var1, Object var2);

    void edgeFlag(boolean var1);

    void edgeFlagData(boolean var1, Object var2);

    void vertex(Object var1);

    void vertexData(Object var1, Object var2);

    void end();

    void endData(Object var1);

    void combine(double[] var1, Object[] var2, float[] var3, Object[] var4);

    void combineData(double[] var1, Object[] var2, float[] var3, Object[] var4, Object var5);

    void error(int var1);

    void errorData(int var1, Object var2);
}
