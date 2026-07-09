// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

public interface GLUtessellator {
    void gluDeleteTess();

    void gluTessProperty(int var1, double var2);

    void gluGetTessProperty(int var1, double[] var2, int var3);

    void gluTessNormal(double var1, double var3, double var5);

    void gluTessCallback(int var1, GLUtessellatorCallback var2);

    void gluTessVertex(double[] var1, int var2, Object var3);

    void gluTessBeginPolygon(Object var1);

    void gluTessBeginContour();

    void gluTessEndContour();

    void gluTessEndPolygon();

    void gluBeginPolygon();

    void gluNextContour(int var1);

    void gluEndPolygon();
}
