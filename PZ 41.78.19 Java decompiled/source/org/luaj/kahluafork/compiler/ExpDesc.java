// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.luaj.kahluafork.compiler;

public class ExpDesc {
    int k;
    int info;
    int aux;
    private double _nval;
    private boolean has_nval;
    int t;
    int f;

    public void setNval(double double0) {
        this._nval = double0;
        this.has_nval = true;
    }

    public double nval() {
        return this.has_nval ? this._nval : this.info;
    }

    void init(int int0, int int1) {
        this.f = -1;
        this.t = -1;
        this.k = int0;
        this.info = int1;
    }

    boolean hasjumps() {
        return this.t != this.f;
    }

    boolean isnumeral() {
        return this.k == 5 && this.t == -1 && this.f == -1;
    }

    public void setvalue(ExpDesc expDesc0) {
        this.k = expDesc0.k;
        this._nval = expDesc0._nval;
        this.has_nval = expDesc0.has_nval;
        this.info = expDesc0.info;
        this.aux = expDesc0.aux;
        this.t = expDesc0.t;
        this.f = expDesc0.f;
    }
}
