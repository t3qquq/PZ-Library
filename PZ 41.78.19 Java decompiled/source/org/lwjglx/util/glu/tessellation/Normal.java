// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class Normal {
    static boolean SLANTED_SWEEP;
    static double S_UNIT_X;
    static double S_UNIT_Y;
    private static final boolean TRUE_PROJECT = false;

    private Normal() {
    }

    private static double Dot(double[] doubles1, double[] doubles0) {
        return doubles1[0] * doubles0[0] + doubles1[1] * doubles0[1] + doubles1[2] * doubles0[2];
    }

    static void Normalize(double[] doubles) {
        double double0 = doubles[0] * doubles[0] + doubles[1] * doubles[1] + doubles[2] * doubles[2];

        assert double0 > 0.0;

        double0 = Math.sqrt(double0);
        doubles[0] /= double0;
        doubles[1] /= double0;
        doubles[2] /= double0;
    }

    static int LongAxis(double[] doubles) {
        byte byte0 = 0;
        if (Math.abs(doubles[1]) > Math.abs(doubles[0])) {
            byte0 = 1;
        }

        if (Math.abs(doubles[2]) > Math.abs(doubles[byte0])) {
            byte0 = 2;
        }

        return byte0;
    }

    static void ComputeNormal(GLUtessellatorImpl gLUtessellatorImpl, double[] doubles5) {
        GLUvertex gLUvertex0 = gLUtessellatorImpl.mesh.vHead;
        double[] doubles0 = new double[3];
        double[] doubles1 = new double[3];
        GLUvertex[] gLUvertexs0 = new GLUvertex[3];
        GLUvertex[] gLUvertexs1 = new GLUvertex[3];
        double[] doubles2 = new double[3];
        double[] doubles3 = new double[3];
        double[] doubles4 = new double[3];
        doubles0[0] = doubles0[1] = doubles0[2] = -2.0E150;
        doubles1[0] = doubles1[1] = doubles1[2] = 2.0E150;

        for (GLUvertex gLUvertex1 = gLUvertex0.next; gLUvertex1 != gLUvertex0; gLUvertex1 = gLUvertex1.next) {
            for (int int0 = 0; int0 < 3; int0++) {
                double double0 = gLUvertex1.coords[int0];
                if (double0 < doubles1[int0]) {
                    doubles1[int0] = double0;
                    gLUvertexs0[int0] = gLUvertex1;
                }

                if (double0 > doubles0[int0]) {
                    doubles0[int0] = double0;
                    gLUvertexs1[int0] = gLUvertex1;
                }
            }
        }

        byte byte0 = 0;
        if (doubles0[1] - doubles1[1] > doubles0[0] - doubles1[0]) {
            byte0 = 1;
        }

        if (doubles0[2] - doubles1[2] > doubles0[byte0] - doubles1[byte0]) {
            byte0 = 2;
        }

        if (doubles1[byte0] >= doubles0[byte0]) {
            doubles5[0] = 0.0;
            doubles5[1] = 0.0;
            doubles5[2] = 1.0;
        } else {
            double double1 = 0.0;
            GLUvertex gLUvertex2 = gLUvertexs0[byte0];
            GLUvertex gLUvertex3 = gLUvertexs1[byte0];
            doubles2[0] = gLUvertex2.coords[0] - gLUvertex3.coords[0];
            doubles2[1] = gLUvertex2.coords[1] - gLUvertex3.coords[1];
            doubles2[2] = gLUvertex2.coords[2] - gLUvertex3.coords[2];

            for (GLUvertex gLUvertex4 = gLUvertex0.next; gLUvertex4 != gLUvertex0; gLUvertex4 = gLUvertex4.next) {
                doubles3[0] = gLUvertex4.coords[0] - gLUvertex3.coords[0];
                doubles3[1] = gLUvertex4.coords[1] - gLUvertex3.coords[1];
                doubles3[2] = gLUvertex4.coords[2] - gLUvertex3.coords[2];
                doubles4[0] = doubles2[1] * doubles3[2] - doubles2[2] * doubles3[1];
                doubles4[1] = doubles2[2] * doubles3[0] - doubles2[0] * doubles3[2];
                doubles4[2] = doubles2[0] * doubles3[1] - doubles2[1] * doubles3[0];
                double double2 = doubles4[0] * doubles4[0] + doubles4[1] * doubles4[1] + doubles4[2] * doubles4[2];
                if (double2 > double1) {
                    double1 = double2;
                    doubles5[0] = doubles4[0];
                    doubles5[1] = doubles4[1];
                    doubles5[2] = doubles4[2];
                }
            }

            if (double1 <= 0.0) {
                doubles5[0] = doubles5[1] = doubles5[2] = 0.0;
                doubles5[LongAxis(doubles2)] = 1.0;
            }
        }
    }

    static void CheckOrientation(GLUtessellatorImpl gLUtessellatorImpl) {
        GLUface gLUface0 = gLUtessellatorImpl.mesh.fHead;
        GLUvertex gLUvertex0 = gLUtessellatorImpl.mesh.vHead;
        double double0 = 0.0;

        for (GLUface gLUface1 = gLUface0.next; gLUface1 != gLUface0; gLUface1 = gLUface1.next) {
            GLUhalfEdge gLUhalfEdge = gLUface1.anEdge;
            if (gLUhalfEdge.winding > 0) {
                while (true) {
                    double0 += (gLUhalfEdge.Org.s - gLUhalfEdge.Sym.Org.s) * (gLUhalfEdge.Org.t + gLUhalfEdge.Sym.Org.t);
                    gLUhalfEdge = gLUhalfEdge.Lnext;
                    if (gLUhalfEdge == gLUface1.anEdge) {
                        break;
                    }
                }
            }
        }

        if (double0 < 0.0) {
            for (GLUvertex gLUvertex1 = gLUvertex0.next; gLUvertex1 != gLUvertex0; gLUvertex1 = gLUvertex1.next) {
                gLUvertex1.t = -gLUvertex1.t;
            }

            gLUtessellatorImpl.tUnit[0] = -gLUtessellatorImpl.tUnit[0];
            gLUtessellatorImpl.tUnit[1] = -gLUtessellatorImpl.tUnit[1];
            gLUtessellatorImpl.tUnit[2] = -gLUtessellatorImpl.tUnit[2];
        }
    }

    public static void __gl_projectPolygon(GLUtessellatorImpl gLUtessellatorImpl) {
        GLUvertex gLUvertex0 = gLUtessellatorImpl.mesh.vHead;
        double[] doubles0 = new double[3];
        boolean boolean0 = false;
        doubles0[0] = gLUtessellatorImpl.normal[0];
        doubles0[1] = gLUtessellatorImpl.normal[1];
        doubles0[2] = gLUtessellatorImpl.normal[2];
        if (doubles0[0] == 0.0 && doubles0[1] == 0.0 && doubles0[2] == 0.0) {
            ComputeNormal(gLUtessellatorImpl, doubles0);
            boolean0 = true;
        }

        double[] doubles1 = gLUtessellatorImpl.sUnit;
        double[] doubles2 = gLUtessellatorImpl.tUnit;
        int int0 = LongAxis(doubles0);
        doubles1[int0] = 0.0;
        doubles1[(int0 + 1) % 3] = S_UNIT_X;
        doubles1[(int0 + 2) % 3] = S_UNIT_Y;
        doubles2[int0] = 0.0;
        doubles2[(int0 + 1) % 3] = doubles0[int0] > 0.0 ? -S_UNIT_Y : S_UNIT_Y;
        doubles2[(int0 + 2) % 3] = doubles0[int0] > 0.0 ? S_UNIT_X : -S_UNIT_X;

        for (GLUvertex gLUvertex1 = gLUvertex0.next; gLUvertex1 != gLUvertex0; gLUvertex1 = gLUvertex1.next) {
            gLUvertex1.s = Dot(gLUvertex1.coords, doubles1);
            gLUvertex1.t = Dot(gLUvertex1.coords, doubles2);
        }

        if (boolean0) {
            CheckOrientation(gLUtessellatorImpl);
        }
    }

    static {
        if (SLANTED_SWEEP) {
            S_UNIT_X = 0.5094153956495538;
            S_UNIT_Y = 0.8605207462201063;
        } else {
            S_UNIT_X = 1.0;
            S_UNIT_Y = 0.0;
        }
    }
}
