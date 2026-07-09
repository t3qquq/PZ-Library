// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class Mesh {
    private Mesh() {
    }

    static GLUhalfEdge MakeEdge(GLUhalfEdge gLUhalfEdge2) {
        GLUhalfEdge gLUhalfEdge0 = new GLUhalfEdge(true);
        GLUhalfEdge gLUhalfEdge1 = new GLUhalfEdge(false);
        if (!gLUhalfEdge2.first) {
            gLUhalfEdge2 = gLUhalfEdge2.Sym;
        }

        GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge2.Sym.next;
        gLUhalfEdge1.next = gLUhalfEdge3;
        gLUhalfEdge3.Sym.next = gLUhalfEdge0;
        gLUhalfEdge0.next = gLUhalfEdge2;
        gLUhalfEdge2.Sym.next = gLUhalfEdge1;
        gLUhalfEdge0.Sym = gLUhalfEdge1;
        gLUhalfEdge0.Onext = gLUhalfEdge0;
        gLUhalfEdge0.Lnext = gLUhalfEdge1;
        gLUhalfEdge0.Org = null;
        gLUhalfEdge0.Lface = null;
        gLUhalfEdge0.winding = 0;
        gLUhalfEdge0.activeRegion = null;
        gLUhalfEdge1.Sym = gLUhalfEdge0;
        gLUhalfEdge1.Onext = gLUhalfEdge1;
        gLUhalfEdge1.Lnext = gLUhalfEdge0;
        gLUhalfEdge1.Org = null;
        gLUhalfEdge1.Lface = null;
        gLUhalfEdge1.winding = 0;
        gLUhalfEdge1.activeRegion = null;
        return gLUhalfEdge0;
    }

    static void Splice(GLUhalfEdge gLUhalfEdge1, GLUhalfEdge gLUhalfEdge3) {
        GLUhalfEdge gLUhalfEdge0 = gLUhalfEdge1.Onext;
        GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge3.Onext;
        gLUhalfEdge0.Sym.Lnext = gLUhalfEdge3;
        gLUhalfEdge2.Sym.Lnext = gLUhalfEdge1;
        gLUhalfEdge1.Onext = gLUhalfEdge2;
        gLUhalfEdge3.Onext = gLUhalfEdge0;
    }

    static void MakeVertex(GLUvertex gLUvertex1, GLUhalfEdge gLUhalfEdge0, GLUvertex gLUvertex3) {
        GLUvertex gLUvertex0 = gLUvertex1;

        assert gLUvertex1 != null;

        GLUvertex gLUvertex2 = gLUvertex3.prev;
        gLUvertex1.prev = gLUvertex2;
        gLUvertex2.next = gLUvertex1;
        gLUvertex1.next = gLUvertex3;
        gLUvertex3.prev = gLUvertex1;
        gLUvertex1.anEdge = gLUhalfEdge0;
        gLUvertex1.data = null;
        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0;

        do {
            gLUhalfEdge1.Org = gLUvertex0;
            gLUhalfEdge1 = gLUhalfEdge1.Onext;
        } while (gLUhalfEdge1 != gLUhalfEdge0);
    }

    static void MakeFace(GLUface gLUface1, GLUhalfEdge gLUhalfEdge0, GLUface gLUface3) {
        GLUface gLUface0 = gLUface1;

        assert gLUface1 != null;

        GLUface gLUface2 = gLUface3.prev;
        gLUface1.prev = gLUface2;
        gLUface2.next = gLUface1;
        gLUface1.next = gLUface3;
        gLUface3.prev = gLUface1;
        gLUface1.anEdge = gLUhalfEdge0;
        gLUface1.data = null;
        gLUface1.trail = null;
        gLUface1.marked = false;
        gLUface1.inside = gLUface3.inside;
        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0;

        do {
            gLUhalfEdge1.Lface = gLUface0;
            gLUhalfEdge1 = gLUhalfEdge1.Lnext;
        } while (gLUhalfEdge1 != gLUhalfEdge0);
    }

    static void KillEdge(GLUhalfEdge gLUhalfEdge0) {
        if (!gLUhalfEdge0.first) {
            gLUhalfEdge0 = gLUhalfEdge0.Sym;
        }

        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0.next;
        GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge0.Sym.next;
        gLUhalfEdge1.Sym.next = gLUhalfEdge2;
        gLUhalfEdge2.Sym.next = gLUhalfEdge1;
    }

    static void KillVertex(GLUvertex gLUvertex0, GLUvertex gLUvertex1) {
        GLUhalfEdge gLUhalfEdge0 = gLUvertex0.anEdge;
        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0;

        do {
            gLUhalfEdge1.Org = gLUvertex1;
            gLUhalfEdge1 = gLUhalfEdge1.Onext;
        } while (gLUhalfEdge1 != gLUhalfEdge0);

        GLUvertex gLUvertex2 = gLUvertex0.prev;
        GLUvertex gLUvertex3 = gLUvertex0.next;
        gLUvertex3.prev = gLUvertex2;
        gLUvertex2.next = gLUvertex3;
    }

    static void KillFace(GLUface gLUface0, GLUface gLUface1) {
        GLUhalfEdge gLUhalfEdge0 = gLUface0.anEdge;
        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0;

        do {
            gLUhalfEdge1.Lface = gLUface1;
            gLUhalfEdge1 = gLUhalfEdge1.Lnext;
        } while (gLUhalfEdge1 != gLUhalfEdge0);

        GLUface gLUface2 = gLUface0.prev;
        GLUface gLUface3 = gLUface0.next;
        gLUface3.prev = gLUface2;
        gLUface2.next = gLUface3;
    }

    public static GLUhalfEdge __gl_meshMakeEdge(GLUmesh gLUmesh) {
        GLUvertex gLUvertex0 = new GLUvertex();
        GLUvertex gLUvertex1 = new GLUvertex();
        GLUface gLUface = new GLUface();
        GLUhalfEdge gLUhalfEdge = MakeEdge(gLUmesh.eHead);
        if (gLUhalfEdge == null) {
            return null;
        } else {
            MakeVertex(gLUvertex0, gLUhalfEdge, gLUmesh.vHead);
            MakeVertex(gLUvertex1, gLUhalfEdge.Sym, gLUmesh.vHead);
            MakeFace(gLUface, gLUhalfEdge, gLUmesh.fHead);
            return gLUhalfEdge;
        }
    }

    public static boolean __gl_meshSplice(GLUhalfEdge gLUhalfEdge0, GLUhalfEdge gLUhalfEdge1) {
        boolean boolean0 = false;
        boolean boolean1 = false;
        if (gLUhalfEdge0 == gLUhalfEdge1) {
            return true;
        } else {
            if (gLUhalfEdge1.Org != gLUhalfEdge0.Org) {
                boolean1 = true;
                KillVertex(gLUhalfEdge1.Org, gLUhalfEdge0.Org);
            }

            if (gLUhalfEdge1.Lface != gLUhalfEdge0.Lface) {
                boolean0 = true;
                KillFace(gLUhalfEdge1.Lface, gLUhalfEdge0.Lface);
            }

            Splice(gLUhalfEdge1, gLUhalfEdge0);
            if (!boolean1) {
                GLUvertex gLUvertex = new GLUvertex();
                MakeVertex(gLUvertex, gLUhalfEdge1, gLUhalfEdge0.Org);
                gLUhalfEdge0.Org.anEdge = gLUhalfEdge0;
            }

            if (!boolean0) {
                GLUface gLUface = new GLUface();
                MakeFace(gLUface, gLUhalfEdge1, gLUhalfEdge0.Lface);
                gLUhalfEdge0.Lface.anEdge = gLUhalfEdge0;
            }

            return true;
        }
    }

    static boolean __gl_meshDelete(GLUhalfEdge gLUhalfEdge1) {
        GLUhalfEdge gLUhalfEdge0 = gLUhalfEdge1.Sym;
        boolean boolean0 = false;
        if (gLUhalfEdge1.Lface != gLUhalfEdge1.Sym.Lface) {
            boolean0 = true;
            KillFace(gLUhalfEdge1.Lface, gLUhalfEdge1.Sym.Lface);
        }

        if (gLUhalfEdge1.Onext == gLUhalfEdge1) {
            KillVertex(gLUhalfEdge1.Org, null);
        } else {
            gLUhalfEdge1.Sym.Lface.anEdge = gLUhalfEdge1.Sym.Lnext;
            gLUhalfEdge1.Org.anEdge = gLUhalfEdge1.Onext;
            Splice(gLUhalfEdge1, gLUhalfEdge1.Sym.Lnext);
            if (!boolean0) {
                GLUface gLUface = new GLUface();
                MakeFace(gLUface, gLUhalfEdge1, gLUhalfEdge1.Lface);
            }
        }

        if (gLUhalfEdge0.Onext == gLUhalfEdge0) {
            KillVertex(gLUhalfEdge0.Org, null);
            KillFace(gLUhalfEdge0.Lface, null);
        } else {
            gLUhalfEdge1.Lface.anEdge = gLUhalfEdge0.Sym.Lnext;
            gLUhalfEdge0.Org.anEdge = gLUhalfEdge0.Onext;
            Splice(gLUhalfEdge0, gLUhalfEdge0.Sym.Lnext);
        }

        KillEdge(gLUhalfEdge1);
        return true;
    }

    static GLUhalfEdge __gl_meshAddEdgeVertex(GLUhalfEdge gLUhalfEdge1) {
        GLUhalfEdge gLUhalfEdge0 = MakeEdge(gLUhalfEdge1);
        GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge0.Sym;
        Splice(gLUhalfEdge0, gLUhalfEdge1.Lnext);
        gLUhalfEdge0.Org = gLUhalfEdge1.Sym.Org;
        GLUvertex gLUvertex = new GLUvertex();
        MakeVertex(gLUvertex, gLUhalfEdge2, gLUhalfEdge0.Org);
        gLUhalfEdge0.Lface = gLUhalfEdge2.Lface = gLUhalfEdge1.Lface;
        return gLUhalfEdge0;
    }

    public static GLUhalfEdge __gl_meshSplitEdge(GLUhalfEdge gLUhalfEdge1) {
        GLUhalfEdge gLUhalfEdge0 = __gl_meshAddEdgeVertex(gLUhalfEdge1);
        GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge0.Sym;
        Splice(gLUhalfEdge1.Sym, gLUhalfEdge1.Sym.Sym.Lnext);
        Splice(gLUhalfEdge1.Sym, gLUhalfEdge2);
        gLUhalfEdge1.Sym.Org = gLUhalfEdge2.Org;
        gLUhalfEdge2.Sym.Org.anEdge = gLUhalfEdge2.Sym;
        gLUhalfEdge2.Sym.Lface = gLUhalfEdge1.Sym.Lface;
        gLUhalfEdge2.winding = gLUhalfEdge1.winding;
        gLUhalfEdge2.Sym.winding = gLUhalfEdge1.Sym.winding;
        return gLUhalfEdge2;
    }

    static GLUhalfEdge __gl_meshConnect(GLUhalfEdge gLUhalfEdge1, GLUhalfEdge gLUhalfEdge3) {
        boolean boolean0 = false;
        GLUhalfEdge gLUhalfEdge0 = MakeEdge(gLUhalfEdge1);
        GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge0.Sym;
        if (gLUhalfEdge3.Lface != gLUhalfEdge1.Lface) {
            boolean0 = true;
            KillFace(gLUhalfEdge3.Lface, gLUhalfEdge1.Lface);
        }

        Splice(gLUhalfEdge0, gLUhalfEdge1.Lnext);
        Splice(gLUhalfEdge2, gLUhalfEdge3);
        gLUhalfEdge0.Org = gLUhalfEdge1.Sym.Org;
        gLUhalfEdge2.Org = gLUhalfEdge3.Org;
        gLUhalfEdge0.Lface = gLUhalfEdge2.Lface = gLUhalfEdge1.Lface;
        gLUhalfEdge1.Lface.anEdge = gLUhalfEdge2;
        if (!boolean0) {
            GLUface gLUface = new GLUface();
            MakeFace(gLUface, gLUhalfEdge0, gLUhalfEdge1.Lface);
        }

        return gLUhalfEdge0;
    }

    static void __gl_meshZapFace(GLUface gLUface0) {
        GLUhalfEdge gLUhalfEdge0 = gLUface0.anEdge;
        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0.Lnext;

        GLUhalfEdge gLUhalfEdge2;
        do {
            gLUhalfEdge2 = gLUhalfEdge1;
            gLUhalfEdge1 = gLUhalfEdge1.Lnext;
            gLUhalfEdge2.Lface = null;
            if (gLUhalfEdge2.Sym.Lface == null) {
                if (gLUhalfEdge2.Onext == gLUhalfEdge2) {
                    KillVertex(gLUhalfEdge2.Org, null);
                } else {
                    gLUhalfEdge2.Org.anEdge = gLUhalfEdge2.Onext;
                    Splice(gLUhalfEdge2, gLUhalfEdge2.Sym.Lnext);
                }

                GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge2.Sym;
                if (gLUhalfEdge3.Onext == gLUhalfEdge3) {
                    KillVertex(gLUhalfEdge3.Org, null);
                } else {
                    gLUhalfEdge3.Org.anEdge = gLUhalfEdge3.Onext;
                    Splice(gLUhalfEdge3, gLUhalfEdge3.Sym.Lnext);
                }

                KillEdge(gLUhalfEdge2);
            }
        } while (gLUhalfEdge2 != gLUhalfEdge0);

        GLUface gLUface1 = gLUface0.prev;
        GLUface gLUface2 = gLUface0.next;
        gLUface2.prev = gLUface1;
        gLUface1.next = gLUface2;
    }

    public static GLUmesh __gl_meshNewMesh() {
        GLUmesh gLUmesh = new GLUmesh();
        GLUvertex gLUvertex = gLUmesh.vHead;
        GLUface gLUface = gLUmesh.fHead;
        GLUhalfEdge gLUhalfEdge0 = gLUmesh.eHead;
        GLUhalfEdge gLUhalfEdge1 = gLUmesh.eHeadSym;
        gLUvertex.next = gLUvertex.prev = gLUvertex;
        gLUvertex.anEdge = null;
        gLUvertex.data = null;
        gLUface.next = gLUface.prev = gLUface;
        gLUface.anEdge = null;
        gLUface.data = null;
        gLUface.trail = null;
        gLUface.marked = false;
        gLUface.inside = false;
        gLUhalfEdge0.next = gLUhalfEdge0;
        gLUhalfEdge0.Sym = gLUhalfEdge1;
        gLUhalfEdge0.Onext = null;
        gLUhalfEdge0.Lnext = null;
        gLUhalfEdge0.Org = null;
        gLUhalfEdge0.Lface = null;
        gLUhalfEdge0.winding = 0;
        gLUhalfEdge0.activeRegion = null;
        gLUhalfEdge1.next = gLUhalfEdge1;
        gLUhalfEdge1.Sym = gLUhalfEdge0;
        gLUhalfEdge1.Onext = null;
        gLUhalfEdge1.Lnext = null;
        gLUhalfEdge1.Org = null;
        gLUhalfEdge1.Lface = null;
        gLUhalfEdge1.winding = 0;
        gLUhalfEdge1.activeRegion = null;
        return gLUmesh;
    }

    static GLUmesh __gl_meshUnion(GLUmesh gLUmesh0, GLUmesh gLUmesh1) {
        GLUface gLUface0 = gLUmesh0.fHead;
        GLUvertex gLUvertex0 = gLUmesh0.vHead;
        GLUhalfEdge gLUhalfEdge0 = gLUmesh0.eHead;
        GLUface gLUface1 = gLUmesh1.fHead;
        GLUvertex gLUvertex1 = gLUmesh1.vHead;
        GLUhalfEdge gLUhalfEdge1 = gLUmesh1.eHead;
        if (gLUface1.next != gLUface1) {
            gLUface0.prev.next = gLUface1.next;
            gLUface1.next.prev = gLUface0.prev;
            gLUface1.prev.next = gLUface0;
            gLUface0.prev = gLUface1.prev;
        }

        if (gLUvertex1.next != gLUvertex1) {
            gLUvertex0.prev.next = gLUvertex1.next;
            gLUvertex1.next.prev = gLUvertex0.prev;
            gLUvertex1.prev.next = gLUvertex0;
            gLUvertex0.prev = gLUvertex1.prev;
        }

        if (gLUhalfEdge1.next != gLUhalfEdge1) {
            gLUhalfEdge0.Sym.next.Sym.next = gLUhalfEdge1.next;
            gLUhalfEdge1.next.Sym.next = gLUhalfEdge0.Sym.next;
            gLUhalfEdge1.Sym.next.Sym.next = gLUhalfEdge0;
            gLUhalfEdge0.Sym.next = gLUhalfEdge1.Sym.next;
        }

        return gLUmesh0;
    }

    static void __gl_meshDeleteMeshZap(GLUmesh gLUmesh) {
        GLUface gLUface = gLUmesh.fHead;

        while (gLUface.next != gLUface) {
            __gl_meshZapFace(gLUface.next);
        }

        assert gLUmesh.vHead.next == gLUmesh.vHead;
    }

    public static void __gl_meshDeleteMesh(GLUmesh gLUmesh) {
        GLUface gLUface0 = gLUmesh.fHead.next;

        while (gLUface0 != gLUmesh.fHead) {
            GLUface gLUface1 = gLUface0.next;
            gLUface0 = gLUface1;
        }

        GLUvertex gLUvertex0 = gLUmesh.vHead.next;

        while (gLUvertex0 != gLUmesh.vHead) {
            GLUvertex gLUvertex1 = gLUvertex0.next;
            gLUvertex0 = gLUvertex1;
        }

        GLUhalfEdge gLUhalfEdge0 = gLUmesh.eHead.next;

        while (gLUhalfEdge0 != gLUmesh.eHead) {
            GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0.next;
            gLUhalfEdge0 = gLUhalfEdge1;
        }
    }

    public static void __gl_meshCheckMesh(GLUmesh gLUmesh) {
        GLUface gLUface0 = gLUmesh.fHead;
        GLUvertex gLUvertex0 = gLUmesh.vHead;
        GLUhalfEdge gLUhalfEdge0 = gLUmesh.eHead;
        GLUface gLUface1 = gLUface0;

        label211:
        while (true) {
            GLUface gLUface2 = gLUface1.next;
            if (gLUface1.next == gLUface0) {
                if ($assertionsDisabled || gLUface2.prev == gLUface1 && gLUface2.anEdge == null && gLUface2.data == null) {
                    GLUvertex gLUvertex1 = gLUvertex0;

                    label185:
                    while (true) {
                        GLUvertex gLUvertex2 = gLUvertex1.next;
                        if (gLUvertex1.next == gLUvertex0) {
                            if ($assertionsDisabled || gLUvertex2.prev == gLUvertex1 && gLUvertex2.anEdge == null && gLUvertex2.data == null) {
                                GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0;

                                while (true) {
                                    GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge1.next;
                                    if (gLUhalfEdge1.next == gLUhalfEdge0) {
                                        if ($assertionsDisabled
                                            || gLUhalfEdge2.Sym.next == gLUhalfEdge1.Sym
                                                && gLUhalfEdge2.Sym == gLUmesh.eHeadSym
                                                && gLUhalfEdge2.Sym.Sym == gLUhalfEdge2
                                                && gLUhalfEdge2.Org == null
                                                && gLUhalfEdge2.Sym.Org == null
                                                && gLUhalfEdge2.Lface == null
                                                && gLUhalfEdge2.Sym.Lface == null) {
                                            return;
                                        }

                                        throw new AssertionError();
                                    }

                                    assert gLUhalfEdge2.Sym.next == gLUhalfEdge1.Sym;

                                    assert gLUhalfEdge2.Sym != gLUhalfEdge2;

                                    assert gLUhalfEdge2.Sym.Sym == gLUhalfEdge2;

                                    assert gLUhalfEdge2.Org != null;

                                    assert gLUhalfEdge2.Sym.Org != null;

                                    assert gLUhalfEdge2.Lnext.Onext.Sym == gLUhalfEdge2;

                                    assert gLUhalfEdge2.Onext.Sym.Lnext == gLUhalfEdge2;

                                    gLUhalfEdge1 = gLUhalfEdge2;
                                }
                            }

                            throw new AssertionError();
                        }

                        assert gLUvertex2.prev == gLUvertex1;

                        GLUhalfEdge gLUhalfEdge3 = gLUvertex2.anEdge;

                        while ($assertionsDisabled || gLUhalfEdge3.Sym != gLUhalfEdge3) {
                            assert gLUhalfEdge3.Sym.Sym == gLUhalfEdge3;

                            assert gLUhalfEdge3.Lnext.Onext.Sym == gLUhalfEdge3;

                            assert gLUhalfEdge3.Onext.Sym.Lnext == gLUhalfEdge3;

                            assert gLUhalfEdge3.Org == gLUvertex2;

                            gLUhalfEdge3 = gLUhalfEdge3.Onext;
                            if (gLUhalfEdge3 == gLUvertex2.anEdge) {
                                gLUvertex1 = gLUvertex2;
                                continue label185;
                            }
                        }

                        throw new AssertionError();
                    }
                }

                throw new AssertionError();
            }

            assert gLUface2.prev == gLUface1;

            GLUhalfEdge gLUhalfEdge4 = gLUface2.anEdge;

            while ($assertionsDisabled || gLUhalfEdge4.Sym != gLUhalfEdge4) {
                assert gLUhalfEdge4.Sym.Sym == gLUhalfEdge4;

                assert gLUhalfEdge4.Lnext.Onext.Sym == gLUhalfEdge4;

                assert gLUhalfEdge4.Onext.Sym.Lnext == gLUhalfEdge4;

                assert gLUhalfEdge4.Lface == gLUface2;

                gLUhalfEdge4 = gLUhalfEdge4.Lnext;
                if (gLUhalfEdge4 == gLUface2.anEdge) {
                    gLUface1 = gLUface2;
                    continue label211;
                }
            }

            throw new AssertionError();
        }
    }
}
