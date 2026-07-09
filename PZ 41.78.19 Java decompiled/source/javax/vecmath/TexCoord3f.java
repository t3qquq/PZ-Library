// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class TexCoord3f extends Tuple3f implements Serializable {
    static final long serialVersionUID = -3517736544731446513L;

    public TexCoord3f(float float0, float float1, float float2) {
        super(float0, float1, float2);
    }

    public TexCoord3f(float[] floats) {
        super(floats);
    }

    public TexCoord3f(TexCoord3f texCoord3f1) {
        super(texCoord3f1);
    }

    public TexCoord3f(Tuple3f tuple3f) {
        super(tuple3f);
    }

    public TexCoord3f(Tuple3d tuple3d) {
        super(tuple3d);
    }

    public TexCoord3f() {
    }
}
