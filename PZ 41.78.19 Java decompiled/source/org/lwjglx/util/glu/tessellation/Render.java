// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class Render {
    private static final boolean USE_OPTIMIZED_CODE_PATH = false;
    private static final Render.RenderFan renderFan = new Render.RenderFan();
    private static final Render.RenderStrip renderStrip = new Render.RenderStrip();
    private static final Render.RenderTriangle renderTriangle = new Render.RenderTriangle();
    private static final int SIGN_INCONSISTENT = 2;

    private Render() {
    }

    public static void __gl_renderMesh(GLUtessellatorImpl gLUtessellatorImpl, GLUmesh gLUmesh) {
        gLUtessellatorImpl.lonelyTriList = null;

        for (GLUface gLUface0 = gLUmesh.fHead.next; gLUface0 != gLUmesh.fHead; gLUface0 = gLUface0.next) {
            gLUface0.marked = false;
        }

        for (GLUface gLUface1 = gLUmesh.fHead.next; gLUface1 != gLUmesh.fHead; gLUface1 = gLUface1.next) {
            if (gLUface1.inside && !gLUface1.marked) {
                RenderMaximumFaceGroup(gLUtessellatorImpl, gLUface1);

                assert gLUface1.marked;
            }
        }

        if (gLUtessellatorImpl.lonelyTriList != null) {
            RenderLonelyTriangles(gLUtessellatorImpl, gLUtessellatorImpl.lonelyTriList);
            gLUtessellatorImpl.lonelyTriList = null;
        }
    }

    static void RenderMaximumFaceGroup(GLUtessellatorImpl gLUtessellatorImpl, GLUface gLUface) {
        GLUhalfEdge gLUhalfEdge = gLUface.anEdge;
        Render.FaceCount faceCount0 = new Render.FaceCount();
        faceCount0.size = 1L;
        faceCount0.eStart = gLUhalfEdge;
        faceCount0.render = renderTriangle;
        if (!gLUtessellatorImpl.flagBoundary) {
            Render.FaceCount faceCount1 = MaximumFan(gLUhalfEdge);
            if (faceCount1.size > faceCount0.size) {
                faceCount0 = faceCount1;
            }

            faceCount1 = MaximumFan(gLUhalfEdge.Lnext);
            if (faceCount1.size > faceCount0.size) {
                faceCount0 = faceCount1;
            }

            faceCount1 = MaximumFan(gLUhalfEdge.Onext.Sym);
            if (faceCount1.size > faceCount0.size) {
                faceCount0 = faceCount1;
            }

            faceCount1 = MaximumStrip(gLUhalfEdge);
            if (faceCount1.size > faceCount0.size) {
                faceCount0 = faceCount1;
            }

            faceCount1 = MaximumStrip(gLUhalfEdge.Lnext);
            if (faceCount1.size > faceCount0.size) {
                faceCount0 = faceCount1;
            }

            faceCount1 = MaximumStrip(gLUhalfEdge.Onext.Sym);
            if (faceCount1.size > faceCount0.size) {
                faceCount0 = faceCount1;
            }
        }

        faceCount0.render.render(gLUtessellatorImpl, faceCount0.eStart, faceCount0.size);
    }

    private static boolean Marked(GLUface gLUface) {
        return !gLUface.inside || gLUface.marked;
    }

    private static GLUface AddToTrail(GLUface gLUface1, GLUface gLUface0) {
        gLUface1.trail = gLUface0;
        gLUface1.marked = true;
        return gLUface1;
    }

    private static void FreeTrail(GLUface gLUface) {
        while (gLUface != null) {
            gLUface.marked = false;
            gLUface = gLUface.trail;
        }
    }

    static Render.FaceCount MaximumFan(GLUhalfEdge gLUhalfEdge1) {
        Render.FaceCount faceCount = new Render.FaceCount(0L, null, renderFan);
        GLUface gLUface = null;

        for (GLUhalfEdge gLUhalfEdge0 = gLUhalfEdge1; !Marked(gLUhalfEdge0.Lface); gLUhalfEdge0 = gLUhalfEdge0.Onext) {
            gLUface = AddToTrail(gLUhalfEdge0.Lface, gLUface);
            faceCount.size++;
        }

        GLUhalfEdge gLUhalfEdge2;
        for (gLUhalfEdge2 = gLUhalfEdge1; !Marked(gLUhalfEdge2.Sym.Lface); gLUhalfEdge2 = gLUhalfEdge2.Sym.Lnext) {
            gLUface = AddToTrail(gLUhalfEdge2.Sym.Lface, gLUface);
            faceCount.size++;
        }

        faceCount.eStart = gLUhalfEdge2;
        FreeTrail(gLUface);
        return faceCount;
    }

    private static boolean IsEven(long long0) {
        return (long0 & 1L) == 0L;
    }

    static Render.FaceCount MaximumStrip(GLUhalfEdge gLUhalfEdge1) {
        Render.FaceCount faceCount = new Render.FaceCount(0L, null, renderStrip);
        long long0 = 0L;
        long long1 = 0L;
        GLUface gLUface = null;

        GLUhalfEdge gLUhalfEdge0;
        for (gLUhalfEdge0 = gLUhalfEdge1; !Marked(gLUhalfEdge0.Lface); gLUhalfEdge0 = gLUhalfEdge0.Onext) {
            gLUface = AddToTrail(gLUhalfEdge0.Lface, gLUface);
            long1++;
            gLUhalfEdge0 = gLUhalfEdge0.Lnext.Sym;
            if (Marked(gLUhalfEdge0.Lface)) {
                break;
            }

            gLUface = AddToTrail(gLUhalfEdge0.Lface, gLUface);
            long1++;
        }

        for (gLUhalfEdge0 = gLUhalfEdge1; !Marked(gLUhalfEdge0.Sym.Lface); gLUhalfEdge0 = gLUhalfEdge0.Sym.Onext.Sym) {
            gLUface = AddToTrail(gLUhalfEdge0.Sym.Lface, gLUface);
            long0++;
            gLUhalfEdge0 = gLUhalfEdge0.Sym.Lnext;
            if (Marked(gLUhalfEdge0.Sym.Lface)) {
                break;
            }

            gLUface = AddToTrail(gLUhalfEdge0.Sym.Lface, gLUface);
            long0++;
        }

        faceCount.size = long1 + long0;
        if (IsEven(long1)) {
            faceCount.eStart = gLUhalfEdge0.Sym;
        } else if (IsEven(long0)) {
            faceCount.eStart = gLUhalfEdge0;
        } else {
            faceCount.size--;
            faceCount.eStart = gLUhalfEdge0.Onext;
        }

        FreeTrail(gLUface);
        return faceCount;
    }

    static void RenderLonelyTriangles(GLUtessellatorImpl gLUtessellatorImpl, GLUface gLUface) {
        int int0 = -1;
        gLUtessellatorImpl.callBeginOrBeginData(4);

        while (gLUface != null) {
            GLUhalfEdge gLUhalfEdge = gLUface.anEdge;

            do {
                if (gLUtessellatorImpl.flagBoundary) {
                    int int1 = !gLUhalfEdge.Sym.Lface.inside ? 1 : 0;
                    if (int0 != int1) {
                        int0 = int1;
                        gLUtessellatorImpl.callEdgeFlagOrEdgeFlagData(int1 != 0);
                    }
                }

                gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
                gLUhalfEdge = gLUhalfEdge.Lnext;
            } while (gLUhalfEdge != gLUface.anEdge);

            gLUface = gLUface.trail;
        }

        gLUtessellatorImpl.callEndOrEndData();
    }

    public static void __gl_renderBoundary(GLUtessellatorImpl gLUtessellatorImpl, GLUmesh gLUmesh) {
        for (GLUface gLUface = gLUmesh.fHead.next; gLUface != gLUmesh.fHead; gLUface = gLUface.next) {
            if (gLUface.inside) {
                gLUtessellatorImpl.callBeginOrBeginData(2);
                GLUhalfEdge gLUhalfEdge = gLUface.anEdge;

                do {
                    gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
                    gLUhalfEdge = gLUhalfEdge.Lnext;
                } while (gLUhalfEdge != gLUface.anEdge);

                gLUtessellatorImpl.callEndOrEndData();
            }
        }
    }

    static int ComputeNormal(GLUtessellatorImpl gLUtessellatorImpl, double[] doubles1, boolean boolean0) {
        CachedVertex[] cachedVertexs = gLUtessellatorImpl.cache;
        int int0 = gLUtessellatorImpl.cacheCount;
        double[] doubles0 = new double[3];
        byte byte0 = 0;
        if (!boolean0) {
            doubles1[0] = doubles1[1] = doubles1[2] = 0.0;
        }

        int int1 = 1;
        double double0 = cachedVertexs[int1].coords[0] - cachedVertexs[0].coords[0];
        double double1 = cachedVertexs[int1].coords[1] - cachedVertexs[0].coords[1];
        double double2 = cachedVertexs[int1].coords[2] - cachedVertexs[0].coords[2];

        while (++int1 < int0) {
            double double3 = double0;
            double double4 = double1;
            double double5 = double2;
            double0 = cachedVertexs[int1].coords[0] - cachedVertexs[0].coords[0];
            double1 = cachedVertexs[int1].coords[1] - cachedVertexs[0].coords[1];
            double2 = cachedVertexs[int1].coords[2] - cachedVertexs[0].coords[2];
            doubles0[0] = double4 * double2 - double5 * double1;
            doubles0[1] = double5 * double0 - double3 * double2;
            doubles0[2] = double3 * double1 - double4 * double0;
            double double6 = doubles0[0] * doubles1[0] + doubles0[1] * doubles1[1] + doubles0[2] * doubles1[2];
            if (!boolean0) {
                if (double6 >= 0.0) {
                    doubles1[0] += doubles0[0];
                    doubles1[1] += doubles0[1];
                    doubles1[2] += doubles0[2];
                } else {
                    doubles1[0] -= doubles0[0];
                    doubles1[1] -= doubles0[1];
                    doubles1[2] -= doubles0[2];
                }
            } else if (double6 != 0.0) {
                if (double6 > 0.0) {
                    if (byte0 < 0) {
                        return 2;
                    }

                    byte0 = 1;
                } else {
                    if (byte0 > 0) {
                        return 2;
                    }

                    byte0 = -1;
                }
            }
        }

        return byte0;
    }

    public static boolean __gl_renderCache(GLUtessellatorImpl gLUtessellatorImpl) {
        CachedVertex[] cachedVertexs = gLUtessellatorImpl.cache;
        int int0 = gLUtessellatorImpl.cacheCount;
        double[] doubles = new double[3];
        if (gLUtessellatorImpl.cacheCount < 3) {
            return true;
        } else {
            doubles[0] = gLUtessellatorImpl.normal[0];
            doubles[1] = gLUtessellatorImpl.normal[1];
            doubles[2] = gLUtessellatorImpl.normal[2];
            if (doubles[0] == 0.0 && doubles[1] == 0.0 && doubles[2] == 0.0) {
                ComputeNormal(gLUtessellatorImpl, doubles, false);
            }

            int int1 = ComputeNormal(gLUtessellatorImpl, doubles, true);
            return int1 == 2 ? false : int1 == 0;
        }
    }

    private static class FaceCount {
        long size;
        GLUhalfEdge eStart;
        Render.renderCallBack render;

        private FaceCount() {
        }

        private FaceCount(long long0, GLUhalfEdge gLUhalfEdge, Render.renderCallBack renderCallBack) {
            this.size = long0;
            this.eStart = gLUhalfEdge;
            this.render = renderCallBack;
        }
    }

    private static class RenderFan implements Render.renderCallBack {
        @Override
        public void render(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, long long0) {
            gLUtessellatorImpl.callBeginOrBeginData(6);
            gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
            gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);

            while (!Render.Marked(gLUhalfEdge.Lface)) {
                gLUhalfEdge.Lface.marked = true;
                long0--;
                gLUhalfEdge = gLUhalfEdge.Onext;
                gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);
            }

            assert long0 == 0L;

            gLUtessellatorImpl.callEndOrEndData();
        }
    }

    private static class RenderStrip implements Render.renderCallBack {
        @Override
        public void render(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, long long0) {
            gLUtessellatorImpl.callBeginOrBeginData(5);
            gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
            gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);

            while (!Render.Marked(gLUhalfEdge.Lface)) {
                gLUhalfEdge.Lface.marked = true;
                long0--;
                gLUhalfEdge = gLUhalfEdge.Lnext.Sym;
                gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Org.data);
                if (Render.Marked(gLUhalfEdge.Lface)) {
                    break;
                }

                gLUhalfEdge.Lface.marked = true;
                long0--;
                gLUhalfEdge = gLUhalfEdge.Onext;
                gLUtessellatorImpl.callVertexOrVertexData(gLUhalfEdge.Sym.Org.data);
            }

            assert long0 == 0L;

            gLUtessellatorImpl.callEndOrEndData();
        }
    }

    private static class RenderTriangle implements Render.renderCallBack {
        @Override
        public void render(GLUtessellatorImpl gLUtessellatorImpl, GLUhalfEdge gLUhalfEdge, long long0) {
            assert long0 == 1L;

            gLUtessellatorImpl.lonelyTriList = Render.AddToTrail(gLUhalfEdge.Lface, gLUtessellatorImpl.lonelyTriList);
        }
    }

    private interface renderCallBack {
        void render(GLUtessellatorImpl var1, GLUhalfEdge var2, long var3);
    }
}
