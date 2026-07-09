// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class TessMono {
    static boolean __gl_meshTessellateMonoRegion(GLUface gLUface) {
        GLUhalfEdge gLUhalfEdge0 = gLUface.anEdge;

        assert gLUhalfEdge0.Lnext != gLUhalfEdge0 && gLUhalfEdge0.Lnext.Lnext != gLUhalfEdge0;

        while (Geom.VertLeq(gLUhalfEdge0.Sym.Org, gLUhalfEdge0.Org)) {
            gLUhalfEdge0 = gLUhalfEdge0.Onext.Sym;
        }

        while (Geom.VertLeq(gLUhalfEdge0.Org, gLUhalfEdge0.Sym.Org)) {
            gLUhalfEdge0 = gLUhalfEdge0.Lnext;
        }

        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0.Onext.Sym;

        while (gLUhalfEdge0.Lnext != gLUhalfEdge1) {
            if (Geom.VertLeq(gLUhalfEdge0.Sym.Org, gLUhalfEdge1.Org)) {
                while (
                    gLUhalfEdge1.Lnext != gLUhalfEdge0
                        && (Geom.EdgeGoesLeft(gLUhalfEdge1.Lnext) || Geom.EdgeSign(gLUhalfEdge1.Org, gLUhalfEdge1.Sym.Org, gLUhalfEdge1.Lnext.Sym.Org) <= 0.0)
                ) {
                    GLUhalfEdge gLUhalfEdge2 = Mesh.__gl_meshConnect(gLUhalfEdge1.Lnext, gLUhalfEdge1);
                    if (gLUhalfEdge2 == null) {
                        return false;
                    }

                    gLUhalfEdge1 = gLUhalfEdge2.Sym;
                }

                gLUhalfEdge1 = gLUhalfEdge1.Onext.Sym;
            } else {
                while (
                    gLUhalfEdge1.Lnext != gLUhalfEdge0
                        && (
                            Geom.EdgeGoesRight(gLUhalfEdge0.Onext.Sym)
                                || Geom.EdgeSign(gLUhalfEdge0.Sym.Org, gLUhalfEdge0.Org, gLUhalfEdge0.Onext.Sym.Org) >= 0.0
                        )
                ) {
                    GLUhalfEdge gLUhalfEdge3 = Mesh.__gl_meshConnect(gLUhalfEdge0, gLUhalfEdge0.Onext.Sym);
                    if (gLUhalfEdge3 == null) {
                        return false;
                    }

                    gLUhalfEdge0 = gLUhalfEdge3.Sym;
                }

                gLUhalfEdge0 = gLUhalfEdge0.Lnext;
            }
        }

        assert gLUhalfEdge1.Lnext != gLUhalfEdge0;

        while (gLUhalfEdge1.Lnext.Lnext != gLUhalfEdge0) {
            GLUhalfEdge gLUhalfEdge4 = Mesh.__gl_meshConnect(gLUhalfEdge1.Lnext, gLUhalfEdge1);
            if (gLUhalfEdge4 == null) {
                return false;
            }

            gLUhalfEdge1 = gLUhalfEdge4.Sym;
        }

        return true;
    }

    public static boolean __gl_meshTessellateInterior(GLUmesh gLUmesh) {
        GLUface gLUface0 = gLUmesh.fHead.next;

        while (gLUface0 != gLUmesh.fHead) {
            GLUface gLUface1 = gLUface0.next;
            if (gLUface0.inside && !__gl_meshTessellateMonoRegion(gLUface0)) {
                return false;
            }

            gLUface0 = gLUface1;
        }

        return true;
    }

    public static void __gl_meshDiscardExterior(GLUmesh gLUmesh) {
        GLUface gLUface0 = gLUmesh.fHead.next;

        while (gLUface0 != gLUmesh.fHead) {
            GLUface gLUface1 = gLUface0.next;
            if (!gLUface0.inside) {
                Mesh.__gl_meshZapFace(gLUface0);
            }

            gLUface0 = gLUface1;
        }
    }

    public static boolean __gl_meshSetWindingNumber(GLUmesh gLUmesh, int int0, boolean boolean0) {
        GLUhalfEdge gLUhalfEdge0 = gLUmesh.eHead.next;

        while (gLUhalfEdge0 != gLUmesh.eHead) {
            GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0.next;
            if (gLUhalfEdge0.Sym.Lface.inside != gLUhalfEdge0.Lface.inside) {
                gLUhalfEdge0.winding = gLUhalfEdge0.Lface.inside ? int0 : -int0;
            } else if (!boolean0) {
                gLUhalfEdge0.winding = 0;
            } else if (!Mesh.__gl_meshDelete(gLUhalfEdge0)) {
                return false;
            }

            gLUhalfEdge0 = gLUhalfEdge1;
        }

        return true;
    }
}
