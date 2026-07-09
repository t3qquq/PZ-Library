// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class Sweep {
    private static final boolean TOLERANCE_NONZERO = false;
    private static final double SENTINEL_COORD = 4.0E150;

    private Sweep() {
    }

    private static void DebugEvent(GLUtessellatorImpl var0) {
    }

    private static void AddWinding(GLUhalfEdge gLUhalfEdge1, GLUhalfEdge gLUhalfEdge0) {
        gLUhalfEdge1.winding = gLUhalfEdge1.winding + gLUhalfEdge0.winding;
        gLUhalfEdge1.Sym.winding = gLUhalfEdge1.Sym.winding + gLUhalfEdge0.Sym.winding;
    }

    private static ActiveRegion RegionBelow(ActiveRegion activeRegion) {
        return (ActiveRegion)Dict.dictKey(Dict.dictPred(activeRegion.nodeUp));
    }

    private static ActiveRegion RegionAbove(ActiveRegion activeRegion) {
        return (ActiveRegion)Dict.dictKey(Dict.dictSucc(activeRegion.nodeUp));
    }

    static boolean EdgeLeq(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion0, ActiveRegion activeRegion1) {
        GLUvertex gLUvertex = gLUtessellatorImpl.event;
        GLUhalfEdge gLUhalfEdge0 = activeRegion0.eUp;
        GLUhalfEdge gLUhalfEdge1 = activeRegion1.eUp;
        if (gLUhalfEdge0.Sym.Org == gLUvertex) {
            if (gLUhalfEdge1.Sym.Org == gLUvertex) {
                return Geom.VertLeq(gLUhalfEdge0.Org, gLUhalfEdge1.Org)
                    ? Geom.EdgeSign(gLUhalfEdge1.Sym.Org, gLUhalfEdge0.Org, gLUhalfEdge1.Org) <= 0.0
                    : Geom.EdgeSign(gLUhalfEdge0.Sym.Org, gLUhalfEdge1.Org, gLUhalfEdge0.Org) >= 0.0;
            } else {
                return Geom.EdgeSign(gLUhalfEdge1.Sym.Org, gLUvertex, gLUhalfEdge1.Org) <= 0.0;
            }
        } else if (gLUhalfEdge1.Sym.Org == gLUvertex) {
            return Geom.EdgeSign(gLUhalfEdge0.Sym.Org, gLUvertex, gLUhalfEdge0.Org) >= 0.0;
        } else {
            double double0 = Geom.EdgeEval(gLUhalfEdge0.Sym.Org, gLUvertex, gLUhalfEdge0.Org);
            double double1 = Geom.EdgeEval(gLUhalfEdge1.Sym.Org, gLUvertex, gLUhalfEdge1.Org);
            return double0 >= double1;
        }
    }

    static void DeleteRegion(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
        assert !activeRegion.fixUpperEdge || activeRegion.eUp.winding == 0;

        activeRegion.eUp.activeRegion = null;
        Dict.dictDelete(gLUtessellatorImpl.dict, activeRegion.nodeUp);
    }

    static boolean FixUpperEdge(ActiveRegion activeRegion, GLUhalfEdge gLUhalfEdge) {
        assert activeRegion.fixUpperEdge;

        if (!Mesh.__gl_meshDelete(activeRegion.eUp)) {
            return false;
        } else {
            activeRegion.fixUpperEdge = false;
            activeRegion.eUp = gLUhalfEdge;
            gLUhalfEdge.activeRegion = activeRegion;
            return true;
        }
    }

    static ActiveRegion TopLeftRegion(ActiveRegion activeRegion) {
        GLUvertex gLUvertex = activeRegion.eUp.Org;

        do {
            activeRegion = RegionAbove(activeRegion);
        } while (activeRegion.eUp.Org == gLUvertex);

        if (activeRegion.fixUpperEdge) {
            GLUhalfEdge gLUhalfEdge = Mesh.__gl_meshConnect(RegionBelow(activeRegion).eUp.Sym, activeRegion.eUp.Lnext);
            if (gLUhalfEdge == null) {
                return null;
            }

            if (!FixUpperEdge(activeRegion, gLUhalfEdge)) {
                return null;
            }

            activeRegion = RegionAbove(activeRegion);
        }

        return activeRegion;
    }

    static ActiveRegion TopRightRegion(ActiveRegion activeRegion) {
        GLUvertex gLUvertex = activeRegion.eUp.Sym.Org;

        do {
            activeRegion = RegionAbove(activeRegion);
        } while (activeRegion.eUp.Sym.Org == gLUvertex);

        return activeRegion;
    }

    static ActiveRegion AddRegionBelow(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion1, GLUhalfEdge gLUhalfEdge) {
        ActiveRegion activeRegion0 = new ActiveRegion();
        activeRegion0.eUp = gLUhalfEdge;
        activeRegion0.nodeUp = Dict.dictInsertBefore(gLUtessellatorImpl.dict, activeRegion1.nodeUp, activeRegion0);
        if (activeRegion0.nodeUp == null) {
            throw new RuntimeException();
        } else {
            activeRegion0.fixUpperEdge = false;
            activeRegion0.sentinel = false;
            activeRegion0.dirty = false;
            gLUhalfEdge.activeRegion = activeRegion0;
            return activeRegion0;
        }
    }

    static boolean IsWindingInside(GLUtessellatorImpl gLUtessellatorImpl, int int0) {
        switch (gLUtessellatorImpl.windingRule) {
            case 100130:
                return (int0 & 1) != 0;
            case 100131:
                return int0 != 0;
            case 100132:
                return int0 > 0;
            case 100133:
                return int0 < 0;
            case 100134:
                return int0 >= 2 || int0 <= -2;
            default:
                throw new InternalError();
        }
    }

    static void ComputeWinding(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
        activeRegion.windingNumber = RegionAbove(activeRegion).windingNumber + activeRegion.eUp.winding;
        activeRegion.inside = IsWindingInside(gLUtessellatorImpl, activeRegion.windingNumber);
    }

    static void FinishRegion(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion) {
        GLUhalfEdge gLUhalfEdge = activeRegion.eUp;
        GLUface gLUface = gLUhalfEdge.Lface;
        gLUface.inside = activeRegion.inside;
        gLUface.anEdge = gLUhalfEdge;
        DeleteRegion(gLUtessellatorImpl, activeRegion);
    }

    static GLUhalfEdge FinishLeftRegions(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion1, ActiveRegion activeRegion2) {
        ActiveRegion activeRegion0 = activeRegion1;
        GLUhalfEdge gLUhalfEdge0 = activeRegion1.eUp;

        while (activeRegion0 != activeRegion2) {
            activeRegion0.fixUpperEdge = false;
            ActiveRegion activeRegion3 = RegionBelow(activeRegion0);
            GLUhalfEdge gLUhalfEdge1 = activeRegion3.eUp;
            if (gLUhalfEdge1.Org != gLUhalfEdge0.Org) {
                if (!activeRegion3.fixUpperEdge) {
                    FinishRegion(gLUtessellatorImpl, activeRegion0);
                    break;
                }

                gLUhalfEdge1 = Mesh.__gl_meshConnect(gLUhalfEdge0.Onext.Sym, gLUhalfEdge1.Sym);
                if (gLUhalfEdge1 == null) {
                    throw new RuntimeException();
                }

                if (!FixUpperEdge(activeRegion3, gLUhalfEdge1)) {
                    throw new RuntimeException();
                }
            }

            if (gLUhalfEdge0.Onext != gLUhalfEdge1) {
                if (!Mesh.__gl_meshSplice(gLUhalfEdge1.Sym.Lnext, gLUhalfEdge1)) {
                    throw new RuntimeException();
                }

                if (!Mesh.__gl_meshSplice(gLUhalfEdge0, gLUhalfEdge1)) {
                    throw new RuntimeException();
                }
            }

            FinishRegion(gLUtessellatorImpl, activeRegion0);
            gLUhalfEdge0 = activeRegion3.eUp;
            activeRegion0 = activeRegion3;
        }

        return gLUhalfEdge0;
    }

    static void AddRightEdges(
        GLUtessellatorImpl gLUtessellatorImpl,
        ActiveRegion activeRegion0,
        GLUhalfEdge gLUhalfEdge1,
        GLUhalfEdge gLUhalfEdge2,
        GLUhalfEdge gLUhalfEdge3,
        boolean boolean1
    ) {
        boolean boolean0 = true;
        GLUhalfEdge gLUhalfEdge0 = gLUhalfEdge1;

        while ($assertionsDisabled || Geom.VertLeq(gLUhalfEdge0.Org, gLUhalfEdge0.Sym.Org)) {
            AddRegionBelow(gLUtessellatorImpl, activeRegion0, gLUhalfEdge0.Sym);
            gLUhalfEdge0 = gLUhalfEdge0.Onext;
            if (gLUhalfEdge0 == gLUhalfEdge2) {
                if (gLUhalfEdge3 == null) {
                    gLUhalfEdge3 = RegionBelow(activeRegion0).eUp.Sym.Onext;
                }

                ActiveRegion activeRegion1 = activeRegion0;
                GLUhalfEdge gLUhalfEdge4 = gLUhalfEdge3;

                while (true) {
                    ActiveRegion activeRegion2 = RegionBelow(activeRegion1);
                    gLUhalfEdge0 = activeRegion2.eUp.Sym;
                    if (gLUhalfEdge0.Org != gLUhalfEdge4.Org) {
                        activeRegion1.dirty = true;

                        assert activeRegion1.windingNumber - gLUhalfEdge0.winding == activeRegion2.windingNumber;

                        if (boolean1) {
                            WalkDirtyRegions(gLUtessellatorImpl, activeRegion1);
                        }

                        return;
                    }

                    if (gLUhalfEdge0.Onext != gLUhalfEdge4) {
                        if (!Mesh.__gl_meshSplice(gLUhalfEdge0.Sym.Lnext, gLUhalfEdge0)) {
                            throw new RuntimeException();
                        }

                        if (!Mesh.__gl_meshSplice(gLUhalfEdge4.Sym.Lnext, gLUhalfEdge0)) {
                            throw new RuntimeException();
                        }
                    }

                    activeRegion2.windingNumber = activeRegion1.windingNumber - gLUhalfEdge0.winding;
                    activeRegion2.inside = IsWindingInside(gLUtessellatorImpl, activeRegion2.windingNumber);
                    activeRegion1.dirty = true;
                    if (!boolean0 && CheckForRightSplice(gLUtessellatorImpl, activeRegion1)) {
                        AddWinding(gLUhalfEdge0, gLUhalfEdge4);
                        DeleteRegion(gLUtessellatorImpl, activeRegion1);
                        if (!Mesh.__gl_meshDelete(gLUhalfEdge4)) {
                            throw new RuntimeException();
                        }
                    }

                    boolean0 = false;
                    activeRegion1 = activeRegion2;
                    gLUhalfEdge4 = gLUhalfEdge0;
                }
            }
        }

        throw new AssertionError();
    }

    static void CallCombine(GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex, Object[] objects1, float[] floats, boolean boolean0) {
        double[] doubles = new double[]{gLUvertex.coords[0], gLUvertex.coords[1], gLUvertex.coords[2]};
        Object[] objects0 = new Object[1];
        gLUtessellatorImpl.callCombineOrCombineData(doubles, objects1, floats, objects0);
        gLUvertex.data = objects0[0];
        if (gLUvertex.data == null) {
            if (!boolean0) {
                gLUvertex.data = objects1[0];
            } else if (!gLUtessellatorImpl.fatalError) {
                gLUtessellatorImpl.callErrorOrErrorData(100156);
                gLUtessellatorImpl.fatalError = true;
            }
        }
    }

    static void SpliceMergeVertices(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge0, GLUhalfEdge gLUhalfEdge1) {
        Object[] objects = new Object[4];
        float[] floats = new float[]{0.5F, 0.5F, 0.0F, 0.0F};
        objects[0] = gLUhalfEdge0.Org.data;
        objects[1] = gLUhalfEdge1.Org.data;
        CallCombine(gLUtessellatorImpl, gLUhalfEdge0.Org, objects, floats, false);
        if (!Mesh.__gl_meshSplice(gLUhalfEdge0, gLUhalfEdge1)) {
            throw new RuntimeException();
        }
    }

    static void VertexWeights(GLUvertex gLUvertex1, GLUvertex gLUvertex0, GLUvertex gLUvertex2, float[] floats) {
        double double0 = Geom.VertL1dist(gLUvertex0, gLUvertex1);
        double double1 = Geom.VertL1dist(gLUvertex2, gLUvertex1);
        floats[0] = (float)(0.5 * double1 / (double0 + double1));
        floats[1] = (float)(0.5 * double0 / (double0 + double1));
        gLUvertex1.coords[0] = gLUvertex1.coords[0] + (floats[0] * gLUvertex0.coords[0] + floats[1] * gLUvertex2.coords[0]);
        gLUvertex1.coords[1] = gLUvertex1.coords[1] + (floats[0] * gLUvertex0.coords[1] + floats[1] * gLUvertex2.coords[1]);
        gLUvertex1.coords[2] = gLUvertex1.coords[2] + (floats[0] * gLUvertex0.coords[2] + floats[1] * gLUvertex2.coords[2]);
    }

    static void GetIntersectData(
        GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex4, GLUvertex gLUvertex0, GLUvertex gLUvertex1, GLUvertex gLUvertex2, GLUvertex gLUvertex3
    ) {
        Object[] objects = new Object[4];
        float[] floats0 = new float[4];
        float[] floats1 = new float[2];
        float[] floats2 = new float[2];
        objects[0] = gLUvertex0.data;
        objects[1] = gLUvertex1.data;
        objects[2] = gLUvertex2.data;
        objects[3] = gLUvertex3.data;
        gLUvertex4.coords[0] = gLUvertex4.coords[1] = gLUvertex4.coords[2] = 0.0;
        VertexWeights(gLUvertex4, gLUvertex0, gLUvertex1, floats1);
        VertexWeights(gLUvertex4, gLUvertex2, gLUvertex3, floats2);
        System.arraycopy(floats1, 0, floats0, 0, 2);
        System.arraycopy(floats2, 0, floats0, 2, 2);
        CallCombine(gLUtessellatorImpl, gLUvertex4, objects, floats0, true);
    }

    static boolean CheckForRightSplice(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion1) {
        ActiveRegion activeRegion0 = RegionBelow(activeRegion1);
        GLUhalfEdge gLUhalfEdge0 = activeRegion1.eUp;
        GLUhalfEdge gLUhalfEdge1 = activeRegion0.eUp;
        if (Geom.VertLeq(gLUhalfEdge0.Org, gLUhalfEdge1.Org)) {
            if (Geom.EdgeSign(gLUhalfEdge1.Sym.Org, gLUhalfEdge0.Org, gLUhalfEdge1.Org) > 0.0) {
                return false;
            }

            if (!Geom.VertEq(gLUhalfEdge0.Org, gLUhalfEdge1.Org)) {
                if (Mesh.__gl_meshSplitEdge(gLUhalfEdge1.Sym) == null) {
                    throw new RuntimeException();
                }

                if (!Mesh.__gl_meshSplice(gLUhalfEdge0, gLUhalfEdge1.Sym.Lnext)) {
                    throw new RuntimeException();
                }

                activeRegion1.dirty = activeRegion0.dirty = true;
            } else if (gLUhalfEdge0.Org != gLUhalfEdge1.Org) {
                gLUtessellatorImpl.pq.pqDelete(gLUhalfEdge0.Org.pqHandle);
                SpliceMergeVertices(gLUtessellatorImpl, gLUhalfEdge1.Sym.Lnext, gLUhalfEdge0);
            }
        } else {
            if (Geom.EdgeSign(gLUhalfEdge0.Sym.Org, gLUhalfEdge1.Org, gLUhalfEdge0.Org) < 0.0) {
                return false;
            }

            RegionAbove(activeRegion1).dirty = activeRegion1.dirty = true;
            if (Mesh.__gl_meshSplitEdge(gLUhalfEdge0.Sym) == null) {
                throw new RuntimeException();
            }

            if (!Mesh.__gl_meshSplice(gLUhalfEdge1.Sym.Lnext, gLUhalfEdge0)) {
                throw new RuntimeException();
            }
        }

        return true;
    }

    static boolean CheckForLeftSplice(GLUtessellatorImpl var0, ActiveRegion activeRegion1) {
        ActiveRegion activeRegion0 = RegionBelow(activeRegion1);
        GLUhalfEdge gLUhalfEdge0 = activeRegion1.eUp;
        GLUhalfEdge gLUhalfEdge1 = activeRegion0.eUp;

        assert !Geom.VertEq(gLUhalfEdge0.Sym.Org, gLUhalfEdge1.Sym.Org);

        if (Geom.VertLeq(gLUhalfEdge0.Sym.Org, gLUhalfEdge1.Sym.Org)) {
            if (Geom.EdgeSign(gLUhalfEdge0.Sym.Org, gLUhalfEdge1.Sym.Org, gLUhalfEdge0.Org) < 0.0) {
                return false;
            }

            RegionAbove(activeRegion1).dirty = activeRegion1.dirty = true;
            GLUhalfEdge gLUhalfEdge2 = Mesh.__gl_meshSplitEdge(gLUhalfEdge0);
            if (gLUhalfEdge2 == null) {
                throw new RuntimeException();
            }

            if (!Mesh.__gl_meshSplice(gLUhalfEdge1.Sym, gLUhalfEdge2)) {
                throw new RuntimeException();
            }

            gLUhalfEdge2.Lface.inside = activeRegion1.inside;
        } else {
            if (Geom.EdgeSign(gLUhalfEdge1.Sym.Org, gLUhalfEdge0.Sym.Org, gLUhalfEdge1.Org) > 0.0) {
                return false;
            }

            activeRegion1.dirty = activeRegion0.dirty = true;
            GLUhalfEdge gLUhalfEdge3 = Mesh.__gl_meshSplitEdge(gLUhalfEdge1);
            if (gLUhalfEdge3 == null) {
                throw new RuntimeException();
            }

            if (!Mesh.__gl_meshSplice(gLUhalfEdge0.Lnext, gLUhalfEdge1.Sym)) {
                throw new RuntimeException();
            }

            gLUhalfEdge3.Sym.Lface.inside = activeRegion1.inside;
        }

        return true;
    }

    static boolean CheckForIntersect(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion1) {
        ActiveRegion activeRegion0 = RegionBelow(activeRegion1);
        GLUhalfEdge gLUhalfEdge0 = activeRegion1.eUp;
        GLUhalfEdge gLUhalfEdge1 = activeRegion0.eUp;
        GLUvertex gLUvertex0 = gLUhalfEdge0.Org;
        GLUvertex gLUvertex1 = gLUhalfEdge1.Org;
        GLUvertex gLUvertex2 = gLUhalfEdge0.Sym.Org;
        GLUvertex gLUvertex3 = gLUhalfEdge1.Sym.Org;
        GLUvertex gLUvertex4 = new GLUvertex();

        assert !Geom.VertEq(gLUvertex3, gLUvertex2);

        assert Geom.EdgeSign(gLUvertex2, gLUtessellatorImpl.event, gLUvertex0) <= 0.0;

        assert Geom.EdgeSign(gLUvertex3, gLUtessellatorImpl.event, gLUvertex1) >= 0.0;

        assert gLUvertex0 != gLUtessellatorImpl.event && gLUvertex1 != gLUtessellatorImpl.event;

        assert !activeRegion1.fixUpperEdge && !activeRegion0.fixUpperEdge;

        if (gLUvertex0 == gLUvertex1) {
            return false;
        } else {
            double double0 = Math.min(gLUvertex0.t, gLUvertex2.t);
            double double1 = Math.max(gLUvertex1.t, gLUvertex3.t);
            if (double0 > double1) {
                return false;
            } else {
                if (Geom.VertLeq(gLUvertex0, gLUvertex1)) {
                    if (Geom.EdgeSign(gLUvertex3, gLUvertex0, gLUvertex1) > 0.0) {
                        return false;
                    }
                } else if (Geom.EdgeSign(gLUvertex2, gLUvertex1, gLUvertex0) < 0.0) {
                    return false;
                }

                DebugEvent(gLUtessellatorImpl);
                Geom.EdgeIntersect(gLUvertex2, gLUvertex0, gLUvertex3, gLUvertex1, gLUvertex4);

                assert Math.min(gLUvertex0.t, gLUvertex2.t) <= gLUvertex4.t;

                assert gLUvertex4.t <= Math.max(gLUvertex1.t, gLUvertex3.t);

                assert Math.min(gLUvertex3.s, gLUvertex2.s) <= gLUvertex4.s;

                assert gLUvertex4.s <= Math.max(gLUvertex1.s, gLUvertex0.s);

                if (Geom.VertLeq(gLUvertex4, gLUtessellatorImpl.event)) {
                    gLUvertex4.s = gLUtessellatorImpl.event.s;
                    gLUvertex4.t = gLUtessellatorImpl.event.t;
                }

                GLUvertex gLUvertex5 = Geom.VertLeq(gLUvertex0, gLUvertex1) ? gLUvertex0 : gLUvertex1;
                if (Geom.VertLeq(gLUvertex5, gLUvertex4)) {
                    gLUvertex4.s = gLUvertex5.s;
                    gLUvertex4.t = gLUvertex5.t;
                }

                if (Geom.VertEq(gLUvertex4, gLUvertex0) || Geom.VertEq(gLUvertex4, gLUvertex1)) {
                    CheckForRightSplice(gLUtessellatorImpl, activeRegion1);
                    return false;
                } else if ((Geom.VertEq(gLUvertex2, gLUtessellatorImpl.event) || !(Geom.EdgeSign(gLUvertex2, gLUtessellatorImpl.event, gLUvertex4) >= 0.0))
                    && (Geom.VertEq(gLUvertex3, gLUtessellatorImpl.event) || !(Geom.EdgeSign(gLUvertex3, gLUtessellatorImpl.event, gLUvertex4) <= 0.0))) {
                    if (Mesh.__gl_meshSplitEdge(gLUhalfEdge0.Sym) == null) {
                        throw new RuntimeException();
                    } else if (Mesh.__gl_meshSplitEdge(gLUhalfEdge1.Sym) == null) {
                        throw new RuntimeException();
                    } else if (!Mesh.__gl_meshSplice(gLUhalfEdge1.Sym.Lnext, gLUhalfEdge0)) {
                        throw new RuntimeException();
                    } else {
                        gLUhalfEdge0.Org.s = gLUvertex4.s;
                        gLUhalfEdge0.Org.t = gLUvertex4.t;
                        gLUhalfEdge0.Org.pqHandle = gLUtessellatorImpl.pq.pqInsert(gLUhalfEdge0.Org);
                        if (gLUhalfEdge0.Org.pqHandle == Long.MAX_VALUE) {
                            gLUtessellatorImpl.pq.pqDeletePriorityQ();
                            gLUtessellatorImpl.pq = null;
                            throw new RuntimeException();
                        } else {
                            GetIntersectData(gLUtessellatorImpl, gLUhalfEdge0.Org, gLUvertex0, gLUvertex2, gLUvertex1, gLUvertex3);
                            RegionAbove(activeRegion1).dirty = activeRegion1.dirty = activeRegion0.dirty = true;
                            return false;
                        }
                    }
                } else if (gLUvertex3 == gLUtessellatorImpl.event) {
                    if (Mesh.__gl_meshSplitEdge(gLUhalfEdge0.Sym) == null) {
                        throw new RuntimeException();
                    } else if (!Mesh.__gl_meshSplice(gLUhalfEdge1.Sym, gLUhalfEdge0)) {
                        throw new RuntimeException();
                    } else {
                        activeRegion1 = TopLeftRegion(activeRegion1);
                        if (activeRegion1 == null) {
                            throw new RuntimeException();
                        } else {
                            gLUhalfEdge0 = RegionBelow(activeRegion1).eUp;
                            FinishLeftRegions(gLUtessellatorImpl, RegionBelow(activeRegion1), activeRegion0);
                            AddRightEdges(gLUtessellatorImpl, activeRegion1, gLUhalfEdge0.Sym.Lnext, gLUhalfEdge0, gLUhalfEdge0, true);
                            return true;
                        }
                    }
                } else if (gLUvertex2 == gLUtessellatorImpl.event) {
                    if (Mesh.__gl_meshSplitEdge(gLUhalfEdge1.Sym) == null) {
                        throw new RuntimeException();
                    } else if (!Mesh.__gl_meshSplice(gLUhalfEdge0.Lnext, gLUhalfEdge1.Sym.Lnext)) {
                        throw new RuntimeException();
                    } else {
                        ActiveRegion activeRegion2 = TopRightRegion(activeRegion1);
                        GLUhalfEdge gLUhalfEdge2 = RegionBelow(activeRegion2).eUp.Sym.Onext;
                        activeRegion1.eUp = gLUhalfEdge1.Sym.Lnext;
                        gLUhalfEdge1 = FinishLeftRegions(gLUtessellatorImpl, activeRegion1, null);
                        AddRightEdges(gLUtessellatorImpl, activeRegion2, gLUhalfEdge1.Onext, gLUhalfEdge0.Sym.Onext, gLUhalfEdge2, true);
                        return true;
                    }
                } else {
                    if (Geom.EdgeSign(gLUvertex2, gLUtessellatorImpl.event, gLUvertex4) >= 0.0) {
                        RegionAbove(activeRegion1).dirty = activeRegion1.dirty = true;
                        if (Mesh.__gl_meshSplitEdge(gLUhalfEdge0.Sym) == null) {
                            throw new RuntimeException();
                        }

                        gLUhalfEdge0.Org.s = gLUtessellatorImpl.event.s;
                        gLUhalfEdge0.Org.t = gLUtessellatorImpl.event.t;
                    }

                    if (Geom.EdgeSign(gLUvertex3, gLUtessellatorImpl.event, gLUvertex4) <= 0.0) {
                        activeRegion1.dirty = activeRegion0.dirty = true;
                        if (Mesh.__gl_meshSplitEdge(gLUhalfEdge1.Sym) == null) {
                            throw new RuntimeException();
                        }

                        gLUhalfEdge1.Org.s = gLUtessellatorImpl.event.s;
                        gLUhalfEdge1.Org.t = gLUtessellatorImpl.event.t;
                    }

                    return false;
                }
            }
        }
    }

    static void WalkDirtyRegions(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion1) {
        ActiveRegion activeRegion0 = RegionBelow(activeRegion1);

        while (true) {
            while (activeRegion0.dirty) {
                activeRegion1 = activeRegion0;
                activeRegion0 = RegionBelow(activeRegion0);
            }

            if (!activeRegion1.dirty) {
                activeRegion0 = activeRegion1;
                activeRegion1 = RegionAbove(activeRegion1);
                if (activeRegion1 == null || !activeRegion1.dirty) {
                    return;
                }
            }

            activeRegion1.dirty = false;
            GLUhalfEdge gLUhalfEdge0 = activeRegion1.eUp;
            GLUhalfEdge gLUhalfEdge1 = activeRegion0.eUp;
            if (gLUhalfEdge0.Sym.Org != gLUhalfEdge1.Sym.Org && CheckForLeftSplice(gLUtessellatorImpl, activeRegion1)) {
                if (activeRegion0.fixUpperEdge) {
                    DeleteRegion(gLUtessellatorImpl, activeRegion0);
                    if (!Mesh.__gl_meshDelete(gLUhalfEdge1)) {
                        throw new RuntimeException();
                    }

                    activeRegion0 = RegionBelow(activeRegion1);
                    gLUhalfEdge1 = activeRegion0.eUp;
                } else if (activeRegion1.fixUpperEdge) {
                    DeleteRegion(gLUtessellatorImpl, activeRegion1);
                    if (!Mesh.__gl_meshDelete(gLUhalfEdge0)) {
                        throw new RuntimeException();
                    }

                    activeRegion1 = RegionAbove(activeRegion0);
                    gLUhalfEdge0 = activeRegion1.eUp;
                }
            }

            if (gLUhalfEdge0.Org != gLUhalfEdge1.Org) {
                if (gLUhalfEdge0.Sym.Org != gLUhalfEdge1.Sym.Org
                    && !activeRegion1.fixUpperEdge
                    && !activeRegion0.fixUpperEdge
                    && (gLUhalfEdge0.Sym.Org == gLUtessellatorImpl.event || gLUhalfEdge1.Sym.Org == gLUtessellatorImpl.event)) {
                    if (CheckForIntersect(gLUtessellatorImpl, activeRegion1)) {
                        return;
                    }
                } else {
                    CheckForRightSplice(gLUtessellatorImpl, activeRegion1);
                }
            }

            if (gLUhalfEdge0.Org == gLUhalfEdge1.Org && gLUhalfEdge0.Sym.Org == gLUhalfEdge1.Sym.Org) {
                AddWinding(gLUhalfEdge1, gLUhalfEdge0);
                DeleteRegion(gLUtessellatorImpl, activeRegion1);
                if (!Mesh.__gl_meshDelete(gLUhalfEdge0)) {
                    throw new RuntimeException();
                }

                activeRegion1 = RegionAbove(activeRegion0);
            }
        }
    }

    static void ConnectRightVertex(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion1, GLUhalfEdge gLUhalfEdge1) {
        GLUhalfEdge gLUhalfEdge0 = gLUhalfEdge1.Onext;
        ActiveRegion activeRegion0 = RegionBelow(activeRegion1);
        GLUhalfEdge gLUhalfEdge2 = activeRegion1.eUp;
        GLUhalfEdge gLUhalfEdge3 = activeRegion0.eUp;
        boolean boolean0 = false;
        if (gLUhalfEdge2.Sym.Org != gLUhalfEdge3.Sym.Org) {
            CheckForIntersect(gLUtessellatorImpl, activeRegion1);
        }

        if (Geom.VertEq(gLUhalfEdge2.Org, gLUtessellatorImpl.event)) {
            if (!Mesh.__gl_meshSplice(gLUhalfEdge0.Sym.Lnext, gLUhalfEdge2)) {
                throw new RuntimeException();
            }

            activeRegion1 = TopLeftRegion(activeRegion1);
            if (activeRegion1 == null) {
                throw new RuntimeException();
            }

            gLUhalfEdge0 = RegionBelow(activeRegion1).eUp;
            FinishLeftRegions(gLUtessellatorImpl, RegionBelow(activeRegion1), activeRegion0);
            boolean0 = true;
        }

        if (Geom.VertEq(gLUhalfEdge3.Org, gLUtessellatorImpl.event)) {
            if (!Mesh.__gl_meshSplice(gLUhalfEdge1, gLUhalfEdge3.Sym.Lnext)) {
                throw new RuntimeException();
            }

            gLUhalfEdge1 = FinishLeftRegions(gLUtessellatorImpl, activeRegion0, null);
            boolean0 = true;
        }

        if (boolean0) {
            AddRightEdges(gLUtessellatorImpl, activeRegion1, gLUhalfEdge1.Onext, gLUhalfEdge0, gLUhalfEdge0, true);
        } else {
            GLUhalfEdge gLUhalfEdge4;
            if (Geom.VertLeq(gLUhalfEdge3.Org, gLUhalfEdge2.Org)) {
                gLUhalfEdge4 = gLUhalfEdge3.Sym.Lnext;
            } else {
                gLUhalfEdge4 = gLUhalfEdge2;
            }

            gLUhalfEdge4 = Mesh.__gl_meshConnect(gLUhalfEdge1.Onext.Sym, gLUhalfEdge4);
            if (gLUhalfEdge4 == null) {
                throw new RuntimeException();
            } else {
                AddRightEdges(gLUtessellatorImpl, activeRegion1, gLUhalfEdge4, gLUhalfEdge4.Onext, gLUhalfEdge4.Onext, false);
                gLUhalfEdge4.Sym.activeRegion.fixUpperEdge = true;
                WalkDirtyRegions(gLUtessellatorImpl, activeRegion1);
            }
        }
    }

    static void ConnectLeftDegenerate(GLUtessellatorImpl gLUtessellatorImpl, ActiveRegion activeRegion0, GLUvertex gLUvertex) {
        GLUhalfEdge gLUhalfEdge0 = activeRegion0.eUp;
        if (Geom.VertEq(gLUhalfEdge0.Org, gLUvertex)) {
            assert false;

            SpliceMergeVertices(gLUtessellatorImpl, gLUhalfEdge0, gLUvertex.anEdge);
        } else if (!Geom.VertEq(gLUhalfEdge0.Sym.Org, gLUvertex)) {
            if (Mesh.__gl_meshSplitEdge(gLUhalfEdge0.Sym) == null) {
                throw new RuntimeException();
            } else {
                if (activeRegion0.fixUpperEdge) {
                    if (!Mesh.__gl_meshDelete(gLUhalfEdge0.Onext)) {
                        throw new RuntimeException();
                    }

                    activeRegion0.fixUpperEdge = false;
                }

                if (!Mesh.__gl_meshSplice(gLUvertex.anEdge, gLUhalfEdge0)) {
                    throw new RuntimeException();
                } else {
                    SweepEvent(gLUtessellatorImpl, gLUvertex);
                }
            }
        } else {
            assert false;

            activeRegion0 = TopRightRegion(activeRegion0);
            ActiveRegion activeRegion1 = RegionBelow(activeRegion0);
            GLUhalfEdge gLUhalfEdge1 = activeRegion1.eUp.Sym;
            GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge1.Onext;
            GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge1.Onext;
            if (activeRegion1.fixUpperEdge) {
                assert gLUhalfEdge3 != gLUhalfEdge1;

                DeleteRegion(gLUtessellatorImpl, activeRegion1);
                if (!Mesh.__gl_meshDelete(gLUhalfEdge1)) {
                    throw new RuntimeException();
                }

                gLUhalfEdge1 = gLUhalfEdge3.Sym.Lnext;
            }

            if (!Mesh.__gl_meshSplice(gLUvertex.anEdge, gLUhalfEdge1)) {
                throw new RuntimeException();
            } else {
                if (!Geom.EdgeGoesLeft(gLUhalfEdge3)) {
                    gLUhalfEdge3 = null;
                }

                AddRightEdges(gLUtessellatorImpl, activeRegion0, gLUhalfEdge1.Onext, gLUhalfEdge2, gLUhalfEdge3, true);
            }
        }
    }

    static void ConnectLeftVertex(GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex) {
        ActiveRegion activeRegion0 = new ActiveRegion();
        activeRegion0.eUp = gLUvertex.anEdge.Sym;
        ActiveRegion activeRegion1 = (ActiveRegion)Dict.dictKey(Dict.dictSearch(gLUtessellatorImpl.dict, activeRegion0));
        ActiveRegion activeRegion2 = RegionBelow(activeRegion1);
        GLUhalfEdge gLUhalfEdge0 = activeRegion1.eUp;
        GLUhalfEdge gLUhalfEdge1 = activeRegion2.eUp;
        if (Geom.EdgeSign(gLUhalfEdge0.Sym.Org, gLUvertex, gLUhalfEdge0.Org) == 0.0) {
            ConnectLeftDegenerate(gLUtessellatorImpl, activeRegion1, gLUvertex);
        } else {
            ActiveRegion activeRegion3 = Geom.VertLeq(gLUhalfEdge1.Sym.Org, gLUhalfEdge0.Sym.Org) ? activeRegion1 : activeRegion2;
            if (!activeRegion1.inside && !activeRegion3.fixUpperEdge) {
                AddRightEdges(gLUtessellatorImpl, activeRegion1, gLUvertex.anEdge, gLUvertex.anEdge, null, true);
            } else {
                GLUhalfEdge gLUhalfEdge2;
                if (activeRegion3 == activeRegion1) {
                    gLUhalfEdge2 = Mesh.__gl_meshConnect(gLUvertex.anEdge.Sym, gLUhalfEdge0.Lnext);
                    if (gLUhalfEdge2 == null) {
                        throw new RuntimeException();
                    }
                } else {
                    GLUhalfEdge gLUhalfEdge3 = Mesh.__gl_meshConnect(gLUhalfEdge1.Sym.Onext.Sym, gLUvertex.anEdge);
                    if (gLUhalfEdge3 == null) {
                        throw new RuntimeException();
                    }

                    gLUhalfEdge2 = gLUhalfEdge3.Sym;
                }

                if (activeRegion3.fixUpperEdge) {
                    if (!FixUpperEdge(activeRegion3, gLUhalfEdge2)) {
                        throw new RuntimeException();
                    }
                } else {
                    ComputeWinding(gLUtessellatorImpl, AddRegionBelow(gLUtessellatorImpl, activeRegion1, gLUhalfEdge2));
                }

                SweepEvent(gLUtessellatorImpl, gLUvertex);
            }
        }
    }

    static void SweepEvent(GLUtessellatorImpl gLUtessellatorImpl, GLUvertex gLUvertex) {
        gLUtessellatorImpl.event = gLUvertex;
        DebugEvent(gLUtessellatorImpl);
        GLUhalfEdge gLUhalfEdge0 = gLUvertex.anEdge;

        while (gLUhalfEdge0.activeRegion == null) {
            gLUhalfEdge0 = gLUhalfEdge0.Onext;
            if (gLUhalfEdge0 == gLUvertex.anEdge) {
                ConnectLeftVertex(gLUtessellatorImpl, gLUvertex);
                return;
            }
        }

        ActiveRegion activeRegion0 = TopLeftRegion(gLUhalfEdge0.activeRegion);
        if (activeRegion0 == null) {
            throw new RuntimeException();
        } else {
            ActiveRegion activeRegion1 = RegionBelow(activeRegion0);
            GLUhalfEdge gLUhalfEdge1 = activeRegion1.eUp;
            GLUhalfEdge gLUhalfEdge2 = FinishLeftRegions(gLUtessellatorImpl, activeRegion1, null);
            if (gLUhalfEdge2.Onext == gLUhalfEdge1) {
                ConnectRightVertex(gLUtessellatorImpl, activeRegion0, gLUhalfEdge2);
            } else {
                AddRightEdges(gLUtessellatorImpl, activeRegion0, gLUhalfEdge2.Onext, gLUhalfEdge1, gLUhalfEdge1, true);
            }
        }
    }

    static void AddSentinel(GLUtessellatorImpl gLUtessellatorImpl, double double0) {
        ActiveRegion activeRegion = new ActiveRegion();
        GLUhalfEdge gLUhalfEdge = Mesh.__gl_meshMakeEdge(gLUtessellatorImpl.mesh);
        if (gLUhalfEdge == null) {
            throw new RuntimeException();
        } else {
            gLUhalfEdge.Org.s = 4.0E150;
            gLUhalfEdge.Org.t = double0;
            gLUhalfEdge.Sym.Org.s = -4.0E150;
            gLUhalfEdge.Sym.Org.t = double0;
            gLUtessellatorImpl.event = gLUhalfEdge.Sym.Org;
            activeRegion.eUp = gLUhalfEdge;
            activeRegion.windingNumber = 0;
            activeRegion.inside = false;
            activeRegion.fixUpperEdge = false;
            activeRegion.sentinel = true;
            activeRegion.dirty = false;
            activeRegion.nodeUp = Dict.dictInsert(gLUtessellatorImpl.dict, activeRegion);
            if (activeRegion.nodeUp == null) {
                throw new RuntimeException();
            }
        }
    }

    static void InitEdgeDict(final GLUtessellatorImpl gLUtessellatorImpl) {
        gLUtessellatorImpl.dict = Dict.dictNewDict(gLUtessellatorImpl, new Dict.DictLeq() {
            @Override
            public boolean leq(Object var1, Object object1, Object object0) {
                return Sweep.EdgeLeq(gLUtessellatorImpl, (ActiveRegion)object1, (ActiveRegion)object0);
            }
        });
        if (gLUtessellatorImpl.dict == null) {
            throw new RuntimeException();
        } else {
            AddSentinel(gLUtessellatorImpl, -4.0E150);
            AddSentinel(gLUtessellatorImpl, 4.0E150);
        }
    }

    static void DoneEdgeDict(GLUtessellatorImpl gLUtessellatorImpl) {
        int int0 = 0;

        ActiveRegion activeRegion;
        while ((activeRegion = (ActiveRegion)Dict.dictKey(Dict.dictMin(gLUtessellatorImpl.dict))) != null) {
            if (!activeRegion.sentinel) {
                assert activeRegion.fixUpperEdge;

                if (!$assertionsDisabled) {
                    if (++int0 != 1) {
                        throw new AssertionError();
                    }
                }
            }

            assert activeRegion.windingNumber == 0;

            DeleteRegion(gLUtessellatorImpl, activeRegion);
        }

        Dict.dictDeleteDict(gLUtessellatorImpl.dict);
    }

    static void RemoveDegenerateEdges(GLUtessellatorImpl gLUtessellatorImpl) {
        GLUhalfEdge gLUhalfEdge0 = gLUtessellatorImpl.mesh.eHead;
        GLUhalfEdge gLUhalfEdge1 = gLUhalfEdge0.next;

        while (gLUhalfEdge1 != gLUhalfEdge0) {
            GLUhalfEdge gLUhalfEdge2 = gLUhalfEdge1.next;
            GLUhalfEdge gLUhalfEdge3 = gLUhalfEdge1.Lnext;
            if (Geom.VertEq(gLUhalfEdge1.Org, gLUhalfEdge1.Sym.Org) && gLUhalfEdge1.Lnext.Lnext != gLUhalfEdge1) {
                SpliceMergeVertices(gLUtessellatorImpl, gLUhalfEdge3, gLUhalfEdge1);
                if (!Mesh.__gl_meshDelete(gLUhalfEdge1)) {
                    throw new RuntimeException();
                }

                gLUhalfEdge1 = gLUhalfEdge3;
                gLUhalfEdge3 = gLUhalfEdge3.Lnext;
            }

            if (gLUhalfEdge3.Lnext == gLUhalfEdge1) {
                if (gLUhalfEdge3 != gLUhalfEdge1) {
                    if (gLUhalfEdge3 == gLUhalfEdge2 || gLUhalfEdge3 == gLUhalfEdge2.Sym) {
                        gLUhalfEdge2 = gLUhalfEdge2.next;
                    }

                    if (!Mesh.__gl_meshDelete(gLUhalfEdge3)) {
                        throw new RuntimeException();
                    }
                }

                if (gLUhalfEdge1 == gLUhalfEdge2 || gLUhalfEdge1 == gLUhalfEdge2.Sym) {
                    gLUhalfEdge2 = gLUhalfEdge2.next;
                }

                if (!Mesh.__gl_meshDelete(gLUhalfEdge1)) {
                    throw new RuntimeException();
                }
            }

            gLUhalfEdge1 = gLUhalfEdge2;
        }
    }

    static boolean InitPriorityQ(GLUtessellatorImpl gLUtessellatorImpl) {
        PriorityQ priorityQ = gLUtessellatorImpl.pq = PriorityQ.pqNewPriorityQ(new PriorityQ.Leq() {
            @Override
            public boolean leq(Object object1, Object object0) {
                return Geom.VertLeq((GLUvertex)object1, (GLUvertex)object0);
            }
        });
        if (priorityQ == null) {
            return false;
        } else {
            GLUvertex gLUvertex0 = gLUtessellatorImpl.mesh.vHead;

            GLUvertex gLUvertex1;
            for (gLUvertex1 = gLUvertex0.next; gLUvertex1 != gLUvertex0; gLUvertex1 = gLUvertex1.next) {
                gLUvertex1.pqHandle = priorityQ.pqInsert(gLUvertex1);
                if (gLUvertex1.pqHandle == Long.MAX_VALUE) {
                    break;
                }
            }

            if (gLUvertex1 == gLUvertex0 && priorityQ.pqInit()) {
                return true;
            } else {
                gLUtessellatorImpl.pq.pqDeletePriorityQ();
                gLUtessellatorImpl.pq = null;
                return false;
            }
        }
    }

    static void DonePriorityQ(GLUtessellatorImpl gLUtessellatorImpl) {
        gLUtessellatorImpl.pq.pqDeletePriorityQ();
    }

    static boolean RemoveDegenerateFaces(GLUmesh gLUmesh) {
        GLUface gLUface0 = gLUmesh.fHead.next;

        while (gLUface0 != gLUmesh.fHead) {
            GLUface gLUface1 = gLUface0.next;
            GLUhalfEdge gLUhalfEdge = gLUface0.anEdge;

            assert gLUhalfEdge.Lnext != gLUhalfEdge;

            if (gLUhalfEdge.Lnext.Lnext == gLUhalfEdge) {
                AddWinding(gLUhalfEdge.Onext, gLUhalfEdge);
                if (!Mesh.__gl_meshDelete(gLUhalfEdge)) {
                    return false;
                }
            }

            gLUface0 = gLUface1;
        }

        return true;
    }

    public static boolean __gl_computeInterior(GLUtessellatorImpl gLUtessellatorImpl) {
        gLUtessellatorImpl.fatalError = false;
        RemoveDegenerateEdges(gLUtessellatorImpl);
        if (!InitPriorityQ(gLUtessellatorImpl)) {
            return false;
        } else {
            InitEdgeDict(gLUtessellatorImpl);

            GLUvertex gLUvertex0;
            while ((gLUvertex0 = (GLUvertex)gLUtessellatorImpl.pq.pqExtractMin()) != null) {
                GLUvertex gLUvertex1 = (GLUvertex)gLUtessellatorImpl.pq.pqMinimum();
                if (gLUvertex1 == null || !Geom.VertEq(gLUvertex1, gLUvertex0)) {
                    SweepEvent(gLUtessellatorImpl, gLUvertex0);
                } else {
                    gLUvertex1 = (GLUvertex)gLUtessellatorImpl.pq.pqExtractMin();
                    SpliceMergeVertices(gLUtessellatorImpl, gLUvertex0.anEdge, gLUvertex1.anEdge);
                }
            }

            gLUtessellatorImpl.event = ((ActiveRegion)Dict.dictKey(Dict.dictMin(gLUtessellatorImpl.dict))).eUp.Org;
            DebugEvent(gLUtessellatorImpl);
            DoneEdgeDict(gLUtessellatorImpl);
            DonePriorityQ(gLUtessellatorImpl);
            if (!RemoveDegenerateFaces(gLUtessellatorImpl.mesh)) {
                return false;
            } else {
                Mesh.__gl_meshCheckMesh(gLUtessellatorImpl.mesh);
                return true;
            }
        }
    }
}
