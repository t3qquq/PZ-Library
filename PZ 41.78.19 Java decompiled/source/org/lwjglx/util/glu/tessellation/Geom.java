// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class Geom {
    private Geom() {
    }

    static double EdgeEval(GLUvertex gLUvertex2, GLUvertex gLUvertex0, GLUvertex gLUvertex1) {
        assert VertLeq(gLUvertex2, gLUvertex0) && VertLeq(gLUvertex0, gLUvertex1);

        double double0 = gLUvertex0.s - gLUvertex2.s;
        double double1 = gLUvertex1.s - gLUvertex0.s;
        if (!(double0 + double1 > 0.0)) {
            return 0.0;
        } else {
            return double0 < double1
                ? gLUvertex0.t - gLUvertex2.t + (gLUvertex2.t - gLUvertex1.t) * (double0 / (double0 + double1))
                : gLUvertex0.t - gLUvertex1.t + (gLUvertex1.t - gLUvertex2.t) * (double1 / (double0 + double1));
        }
    }

    static double EdgeSign(GLUvertex gLUvertex2, GLUvertex gLUvertex0, GLUvertex gLUvertex1) {
        assert VertLeq(gLUvertex2, gLUvertex0) && VertLeq(gLUvertex0, gLUvertex1);

        double double0 = gLUvertex0.s - gLUvertex2.s;
        double double1 = gLUvertex1.s - gLUvertex0.s;
        return double0 + double1 > 0.0 ? (gLUvertex0.t - gLUvertex1.t) * double0 + (gLUvertex0.t - gLUvertex2.t) * double1 : 0.0;
    }

    static double TransEval(GLUvertex gLUvertex2, GLUvertex gLUvertex0, GLUvertex gLUvertex1) {
        assert TransLeq(gLUvertex2, gLUvertex0) && TransLeq(gLUvertex0, gLUvertex1);

        double double0 = gLUvertex0.t - gLUvertex2.t;
        double double1 = gLUvertex1.t - gLUvertex0.t;
        if (!(double0 + double1 > 0.0)) {
            return 0.0;
        } else {
            return double0 < double1
                ? gLUvertex0.s - gLUvertex2.s + (gLUvertex2.s - gLUvertex1.s) * (double0 / (double0 + double1))
                : gLUvertex0.s - gLUvertex1.s + (gLUvertex1.s - gLUvertex2.s) * (double1 / (double0 + double1));
        }
    }

    static double TransSign(GLUvertex gLUvertex2, GLUvertex gLUvertex0, GLUvertex gLUvertex1) {
        assert TransLeq(gLUvertex2, gLUvertex0) && TransLeq(gLUvertex0, gLUvertex1);

        double double0 = gLUvertex0.t - gLUvertex2.t;
        double double1 = gLUvertex1.t - gLUvertex0.t;
        return double0 + double1 > 0.0 ? (gLUvertex0.s - gLUvertex1.s) * double0 + (gLUvertex0.s - gLUvertex2.s) * double1 : 0.0;
    }

    static boolean VertCCW(GLUvertex gLUvertex1, GLUvertex gLUvertex0, GLUvertex gLUvertex2) {
        return gLUvertex1.s * (gLUvertex0.t - gLUvertex2.t) + gLUvertex0.s * (gLUvertex2.t - gLUvertex1.t) + gLUvertex2.s * (gLUvertex1.t - gLUvertex0.t)
            >= 0.0;
    }

    static double Interpolate(double double0, double double2, double double1, double double3) {
        double0 = double0 < 0.0 ? 0.0 : double0;
        double1 = double1 < 0.0 ? 0.0 : double1;
        if (double0 <= double1) {
            return double1 == 0.0 ? (double2 + double3) / 2.0 : double2 + (double3 - double2) * (double0 / (double0 + double1));
        } else {
            return double3 + (double2 - double3) * (double1 / (double0 + double1));
        }
    }

    static void EdgeIntersect(GLUvertex gLUvertex0, GLUvertex gLUvertex1, GLUvertex gLUvertex3, GLUvertex gLUvertex4, GLUvertex gLUvertex7) {
        if (!VertLeq(gLUvertex0, gLUvertex1)) {
            GLUvertex gLUvertex2 = gLUvertex0;
            gLUvertex0 = gLUvertex1;
            gLUvertex1 = gLUvertex2;
        }

        if (!VertLeq(gLUvertex3, gLUvertex4)) {
            GLUvertex gLUvertex5 = gLUvertex3;
            gLUvertex3 = gLUvertex4;
            gLUvertex4 = gLUvertex5;
        }

        if (!VertLeq(gLUvertex0, gLUvertex3)) {
            GLUvertex gLUvertex6 = gLUvertex0;
            gLUvertex0 = gLUvertex3;
            gLUvertex3 = gLUvertex6;
            gLUvertex6 = gLUvertex1;
            gLUvertex1 = gLUvertex4;
            gLUvertex4 = gLUvertex6;
        }

        if (!VertLeq(gLUvertex3, gLUvertex1)) {
            gLUvertex7.s = (gLUvertex3.s + gLUvertex1.s) / 2.0;
        } else if (VertLeq(gLUvertex1, gLUvertex4)) {
            double double0 = EdgeEval(gLUvertex0, gLUvertex3, gLUvertex1);
            double double1 = EdgeEval(gLUvertex3, gLUvertex1, gLUvertex4);
            if (double0 + double1 < 0.0) {
                double0 = -double0;
                double1 = -double1;
            }

            gLUvertex7.s = Interpolate(double0, gLUvertex3.s, double1, gLUvertex1.s);
        } else {
            double double2 = EdgeSign(gLUvertex0, gLUvertex3, gLUvertex1);
            double double3 = -EdgeSign(gLUvertex0, gLUvertex4, gLUvertex1);
            if (double2 + double3 < 0.0) {
                double2 = -double2;
                double3 = -double3;
            }

            gLUvertex7.s = Interpolate(double2, gLUvertex3.s, double3, gLUvertex4.s);
        }

        if (!TransLeq(gLUvertex0, gLUvertex1)) {
            GLUvertex gLUvertex8 = gLUvertex0;
            gLUvertex0 = gLUvertex1;
            gLUvertex1 = gLUvertex8;
        }

        if (!TransLeq(gLUvertex3, gLUvertex4)) {
            GLUvertex gLUvertex9 = gLUvertex3;
            gLUvertex3 = gLUvertex4;
            gLUvertex4 = gLUvertex9;
        }

        if (!TransLeq(gLUvertex0, gLUvertex3)) {
            GLUvertex gLUvertex10 = gLUvertex3;
            gLUvertex3 = gLUvertex0;
            gLUvertex0 = gLUvertex10;
            gLUvertex10 = gLUvertex4;
            gLUvertex4 = gLUvertex1;
            gLUvertex1 = gLUvertex10;
        }

        if (!TransLeq(gLUvertex3, gLUvertex1)) {
            gLUvertex7.t = (gLUvertex3.t + gLUvertex1.t) / 2.0;
        } else if (TransLeq(gLUvertex1, gLUvertex4)) {
            double double4 = TransEval(gLUvertex0, gLUvertex3, gLUvertex1);
            double double5 = TransEval(gLUvertex3, gLUvertex1, gLUvertex4);
            if (double4 + double5 < 0.0) {
                double4 = -double4;
                double5 = -double5;
            }

            gLUvertex7.t = Interpolate(double4, gLUvertex3.t, double5, gLUvertex1.t);
        } else {
            double double6 = TransSign(gLUvertex0, gLUvertex3, gLUvertex1);
            double double7 = -TransSign(gLUvertex0, gLUvertex4, gLUvertex1);
            if (double6 + double7 < 0.0) {
                double6 = -double6;
                double7 = -double7;
            }

            gLUvertex7.t = Interpolate(double6, gLUvertex3.t, double7, gLUvertex4.t);
        }
    }

    static boolean VertEq(GLUvertex gLUvertex1, GLUvertex gLUvertex0) {
        return gLUvertex1.s == gLUvertex0.s && gLUvertex1.t == gLUvertex0.t;
    }

    static boolean VertLeq(GLUvertex gLUvertex1, GLUvertex gLUvertex0) {
        return gLUvertex1.s < gLUvertex0.s || gLUvertex1.s == gLUvertex0.s && gLUvertex1.t <= gLUvertex0.t;
    }

    static boolean TransLeq(GLUvertex gLUvertex1, GLUvertex gLUvertex0) {
        return gLUvertex1.t < gLUvertex0.t || gLUvertex1.t == gLUvertex0.t && gLUvertex1.s <= gLUvertex0.s;
    }

    static boolean EdgeGoesLeft(GLUhalfEdge gLUhalfEdge) {
        return VertLeq(gLUhalfEdge.Sym.Org, gLUhalfEdge.Org);
    }

    static boolean EdgeGoesRight(GLUhalfEdge gLUhalfEdge) {
        return VertLeq(gLUhalfEdge.Org, gLUhalfEdge.Sym.Org);
    }

    static double VertL1dist(GLUvertex gLUvertex1, GLUvertex gLUvertex0) {
        return Math.abs(gLUvertex1.s - gLUvertex0.s) + Math.abs(gLUvertex1.t - gLUvertex0.t);
    }
}
