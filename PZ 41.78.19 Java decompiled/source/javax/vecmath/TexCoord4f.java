// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class TexCoord4f extends Tuple4f implements Serializable {
    static final long serialVersionUID = -3517736544731446513L;

    public TexCoord4f(float float0, float float1, float float2, float float3) {
        super(float0, float1, float2, float3);
    }

    public TexCoord4f(float[] floats) {
        super(floats);
    }

    public TexCoord4f(TexCoord4f texCoord4f1) {
        super(texCoord4f1);
    }

    public TexCoord4f(Tuple4f tuple4f) {
        super(tuple4f);
    }

    public TexCoord4f(Tuple4d tuple4d) {
        super(tuple4d);
    }

    public TexCoord4f() {
    }
}
