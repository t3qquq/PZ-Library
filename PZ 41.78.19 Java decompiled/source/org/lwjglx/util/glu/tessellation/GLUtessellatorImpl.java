// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

import org.lwjglx.util.glu.GLUtessellator;
import org.lwjglx.util.glu.GLUtessellatorCallback;
import org.lwjglx.util.glu.GLUtessellatorCallbackAdapter;

public class GLUtessellatorImpl implements GLUtessellator {
    public static final int TESS_MAX_CACHE = 100;
    private int state;
    private GLUhalfEdge lastEdge;
    GLUmesh mesh;
    double[] normal = new double[3];
    double[] sUnit = new double[3];
    double[] tUnit = new double[3];
    private double relTolerance;
    int windingRule;
    boolean fatalError;
    Dict dict;
    PriorityQ pq;
    GLUvertex event;
    boolean flagBoundary;
    boolean boundaryOnly;
    GLUface lonelyTriList;
    private boolean flushCacheOnNextVertex;
    int cacheCount;
    CachedVertex[] cache = new CachedVertex[100];
    private Object polygonData;
    private GLUtessellatorCallback callBegin;
    private GLUtessellatorCallback callEdgeFlag;
    private GLUtessellatorCallback callVertex;
    private GLUtessellatorCallback callEnd;
    private GLUtessellatorCallback callError;
    private GLUtessellatorCallback callCombine;
    private GLUtessellatorCallback callBeginData;
    private GLUtessellatorCallback callEdgeFlagData;
    private GLUtessellatorCallback callVertexData;
    private GLUtessellatorCallback callEndData;
    private GLUtessellatorCallback callErrorData;
    private GLUtessellatorCallback callCombineData;
    private static final double GLU_TESS_DEFAULT_TOLERANCE = 0.0;
    private static GLUtessellatorCallback NULL_CB = new GLUtessellatorCallbackAdapter();

    public GLUtessellatorImpl() {
        this.state = 0;
        this.normal[0] = 0.0;
        this.normal[1] = 0.0;
        this.normal[2] = 0.0;
        this.relTolerance = 0.0;
        this.windingRule = 100130;
        this.flagBoundary = false;
        this.boundaryOnly = false;
        this.callBegin = NULL_CB;
        this.callEdgeFlag = NULL_CB;
        this.callVertex = NULL_CB;
        this.callEnd = NULL_CB;
        this.callError = NULL_CB;
        this.callCombine = NULL_CB;
        this.callBeginData = NULL_CB;
        this.callEdgeFlagData = NULL_CB;
        this.callVertexData = NULL_CB;
        this.callEndData = NULL_CB;
        this.callErrorData = NULL_CB;
        this.callCombineData = NULL_CB;
        this.polygonData = null;

        for (int int0 = 0; int0 < this.cache.length; int0++) {
            this.cache[int0] = new CachedVertex();
        }
    }

    public static GLUtessellator gluNewTess() {
        return new GLUtessellatorImpl();
    }

    private void makeDormant() {
        if (this.mesh != null) {
            Mesh.__gl_meshDeleteMesh(this.mesh);
        }

        this.state = 0;
        this.lastEdge = null;
        this.mesh = null;
    }

    private void requireState(int int0) {
        if (this.state != int0) {
            this.gotoState(int0);
        }
    }

    private void gotoState(int int0) {
        while (this.state != int0) {
            if (this.state < int0) {
                if (this.state == 0) {
                    this.callErrorOrErrorData(100151);
                    this.gluTessBeginPolygon(null);
                } else if (this.state == 1) {
                    this.callErrorOrErrorData(100152);
                    this.gluTessBeginContour();
                }
            } else if (this.state == 2) {
                this.callErrorOrErrorData(100154);
                this.gluTessEndContour();
            } else if (this.state == 1) {
                this.callErrorOrErrorData(100153);
                this.makeDormant();
            }
        }
    }

    @Override
    public void gluDeleteTess() {
        this.requireState(0);
    }

    @Override
    public void gluTessProperty(int int0, double double0) {
        switch (int0) {
            case 100140:
                int int1 = (int)double0;
                if (int1 != double0) {
                    break;
                }

                switch (int1) {
                    case 100130:
                    case 100131:
                    case 100132:
                    case 100133:
                    case 100134:
                        this.windingRule = int1;
                        return;
                }
            case 100141:
                this.boundaryOnly = double0 != 0.0;
                return;
            case 100142:
                if (!(double0 < 0.0) && !(double0 > 1.0)) {
                    this.relTolerance = double0;
                    return;
                }
                break;
            default:
                this.callErrorOrErrorData(100900);
                return;
        }

        this.callErrorOrErrorData(100901);
    }

    @Override
    public void gluGetTessProperty(int int0, double[] doubles, int int1) {
        switch (int0) {
            case 100140:
                assert this.windingRule == 100130
                    || this.windingRule == 100131
                    || this.windingRule == 100132
                    || this.windingRule == 100133
                    || this.windingRule == 100134;

                doubles[int1] = this.windingRule;
                break;
            case 100141:
                assert this.boundaryOnly || !this.boundaryOnly;

                doubles[int1] = this.boundaryOnly ? 1.0 : 0.0;
                break;
            case 100142:
                assert 0.0 <= this.relTolerance && this.relTolerance <= 1.0;

                doubles[int1] = this.relTolerance;
                break;
            default:
                doubles[int1] = 0.0;
                this.callErrorOrErrorData(100900);
        }
    }

    @Override
    public void gluTessNormal(double double0, double double1, double double2) {
        this.normal[0] = double0;
        this.normal[1] = double1;
        this.normal[2] = double2;
    }

    @Override
    public void gluTessCallback(int int0, GLUtessellatorCallback gLUtessellatorCallback) {
        switch (int0) {
            case 100100:
                this.callBegin = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100101:
                this.callVertex = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100102:
                this.callEnd = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100103:
                this.callError = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100104:
                this.callEdgeFlag = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                this.flagBoundary = gLUtessellatorCallback != null;
                return;
            case 100105:
                this.callCombine = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100106:
                this.callBeginData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100107:
                this.callVertexData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100108:
                this.callEndData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100109:
                this.callErrorData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            case 100110:
                this.callEdgeFlagData = this.callBegin = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                this.flagBoundary = gLUtessellatorCallback != null;
                return;
            case 100111:
                this.callCombineData = gLUtessellatorCallback == null ? NULL_CB : gLUtessellatorCallback;
                return;
            default:
                this.callErrorOrErrorData(100900);
        }
    }

    private boolean addVertex(double[] doubles, Object object) {
        GLUhalfEdge gLUhalfEdge = this.lastEdge;
        if (gLUhalfEdge == null) {
            gLUhalfEdge = Mesh.__gl_meshMakeEdge(this.mesh);
            if (gLUhalfEdge == null) {
                return false;
            }

            if (!Mesh.__gl_meshSplice(gLUhalfEdge, gLUhalfEdge.Sym)) {
                return false;
            }
        } else {
            if (Mesh.__gl_meshSplitEdge(gLUhalfEdge) == null) {
                return false;
            }

            gLUhalfEdge = gLUhalfEdge.Lnext;
        }

        gLUhalfEdge.Org.data = object;
        gLUhalfEdge.Org.coords[0] = doubles[0];
        gLUhalfEdge.Org.coords[1] = doubles[1];
        gLUhalfEdge.Org.coords[2] = doubles[2];
        gLUhalfEdge.winding = 1;
        gLUhalfEdge.Sym.winding = -1;
        this.lastEdge = gLUhalfEdge;
        return true;
    }

    private void cacheVertex(double[] doubles, Object object) {
        if (this.cache[this.cacheCount] == null) {
            this.cache[this.cacheCount] = new CachedVertex();
        }

        CachedVertex cachedVertex = this.cache[this.cacheCount];
        cachedVertex.data = object;
        cachedVertex.coords[0] = doubles[0];
        cachedVertex.coords[1] = doubles[1];
        cachedVertex.coords[2] = doubles[2];
        this.cacheCount++;
    }

    private boolean flushCache() {
        CachedVertex[] cachedVertexs = this.cache;
        this.mesh = Mesh.__gl_meshNewMesh();
        if (this.mesh == null) {
            return false;
        } else {
            for (int int0 = 0; int0 < this.cacheCount; int0++) {
                CachedVertex cachedVertex = cachedVertexs[int0];
                if (!this.addVertex(cachedVertex.coords, cachedVertex.data)) {
                    return false;
                }
            }

            this.cacheCount = 0;
            this.flushCacheOnNextVertex = false;
            return true;
        }
    }

    @Override
    public void gluTessVertex(double[] doubles1, int int1, Object object) {
        boolean boolean0 = false;
        double[] doubles0 = new double[3];
        this.requireState(2);
        if (this.flushCacheOnNextVertex) {
            if (!this.flushCache()) {
                this.callErrorOrErrorData(100902);
                return;
            }

            this.lastEdge = null;
        }

        for (int int0 = 0; int0 < 3; int0++) {
            double double0 = doubles1[int0 + int1];
            if (double0 < -1.0E150) {
                double0 = -1.0E150;
                boolean0 = true;
            }

            if (double0 > 1.0E150) {
                double0 = 1.0E150;
                boolean0 = true;
            }

            doubles0[int0] = double0;
        }

        if (boolean0) {
            this.callErrorOrErrorData(100155);
        }

        if (this.mesh == null) {
            if (this.cacheCount < 100) {
                this.cacheVertex(doubles0, object);
                return;
            }

            if (!this.flushCache()) {
                this.callErrorOrErrorData(100902);
                return;
            }
        }

        if (!this.addVertex(doubles0, object)) {
            this.callErrorOrErrorData(100902);
        }
    }

    @Override
    public void gluTessBeginPolygon(Object object) {
        this.requireState(0);
        this.state = 1;
        this.cacheCount = 0;
        this.flushCacheOnNextVertex = false;
        this.mesh = null;
        this.polygonData = object;
    }

    @Override
    public void gluTessBeginContour() {
        this.requireState(1);
        this.state = 2;
        this.lastEdge = null;
        if (this.cacheCount > 0) {
            this.flushCacheOnNextVertex = true;
        }
    }

    @Override
    public void gluTessEndContour() {
        this.requireState(2);
        this.state = 1;
    }

    @Override
    public void gluTessEndPolygon() {
        try {
            this.requireState(1);
            this.state = 0;
            if (this.mesh == null) {
                if (!this.flagBoundary && Render.__gl_renderCache(this)) {
                    this.polygonData = null;
                    return;
                }

                if (!this.flushCache()) {
                    throw new RuntimeException();
                }
            }

            Normal.__gl_projectPolygon(this);
            if (!Sweep.__gl_computeInterior(this)) {
                throw new RuntimeException();
            }

            GLUmesh gLUmesh = this.mesh;
            if (!this.fatalError) {
                boolean boolean0 = true;
                if (this.boundaryOnly) {
                    boolean0 = TessMono.__gl_meshSetWindingNumber(gLUmesh, 1, true);
                } else {
                    boolean0 = TessMono.__gl_meshTessellateInterior(gLUmesh);
                }

                if (!boolean0) {
                    throw new RuntimeException();
                }

                Mesh.__gl_meshCheckMesh(gLUmesh);
                if (this.callBegin != NULL_CB
                    || this.callEnd != NULL_CB
                    || this.callVertex != NULL_CB
                    || this.callEdgeFlag != NULL_CB
                    || this.callBeginData != NULL_CB
                    || this.callEndData != NULL_CB
                    || this.callVertexData != NULL_CB
                    || this.callEdgeFlagData != NULL_CB) {
                    if (this.boundaryOnly) {
                        Render.__gl_renderBoundary(this, gLUmesh);
                    } else {
                        Render.__gl_renderMesh(this, gLUmesh);
                    }
                }
            }

            Mesh.__gl_meshDeleteMesh(gLUmesh);
            this.polygonData = null;
            gLUmesh = null;
        } catch (Exception exception) {
            exception.printStackTrace();
            this.callErrorOrErrorData(100902);
        }
    }

    @Override
    public void gluBeginPolygon() {
        this.gluTessBeginPolygon(null);
        this.gluTessBeginContour();
    }

    @Override
    public void gluNextContour(int var1) {
        this.gluTessEndContour();
        this.gluTessBeginContour();
    }

    @Override
    public void gluEndPolygon() {
        this.gluTessEndContour();
        this.gluTessEndPolygon();
    }

    void callBeginOrBeginData(int int0) {
        if (this.callBeginData != NULL_CB) {
            this.callBeginData.beginData(int0, this.polygonData);
        } else {
            this.callBegin.begin(int0);
        }
    }

    void callVertexOrVertexData(Object object) {
        if (this.callVertexData != NULL_CB) {
            this.callVertexData.vertexData(object, this.polygonData);
        } else {
            this.callVertex.vertex(object);
        }
    }

    void callEdgeFlagOrEdgeFlagData(boolean boolean0) {
        if (this.callEdgeFlagData != NULL_CB) {
            this.callEdgeFlagData.edgeFlagData(boolean0, this.polygonData);
        } else {
            this.callEdgeFlag.edgeFlag(boolean0);
        }
    }

    void callEndOrEndData() {
        if (this.callEndData != NULL_CB) {
            this.callEndData.endData(this.polygonData);
        } else {
            this.callEnd.end();
        }
    }

    void callCombineOrCombineData(double[] doubles, Object[] objects0, float[] floats, Object[] objects1) {
        if (this.callCombineData != NULL_CB) {
            this.callCombineData.combineData(doubles, objects0, floats, objects1, this.polygonData);
        } else {
            this.callCombine.combine(doubles, objects0, floats, objects1);
        }
    }

    void callErrorOrErrorData(int int0) {
        if (this.callErrorData != NULL_CB) {
            this.callErrorData.errorData(int0, this.polygonData);
        } else {
            this.callError.error(int0);
        }
    }
}
