// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu.tessellation;

class GLUvertex {
    public GLUvertex next;
    public GLUvertex prev;
    public GLUhalfEdge anEdge;
    public Object data;
    public double[] coords = new double[3];
    public double s;
    public double t;
    public int pqHandle;
}
