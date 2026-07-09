// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class GLUhalfEdge {
    public GLUhalfEdge next;
    public GLUhalfEdge Sym;
    public GLUhalfEdge Onext;
    public GLUhalfEdge Lnext;
    public GLUvertex Org;
    public GLUface Lface;
    public ActiveRegion activeRegion;
    public int winding;
    public boolean first;

    GLUhalfEdge(boolean boolean0) {
        this.first = boolean0;
    }
}
