// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class Vector4 {
    public float x;
    public float y;
    public float z;
    public float w;

    public Vector4() {
        this(0.0F, 0.0F, 0.0F, 0.0F);
    }

    public Vector4(float _x, float _y, float _z, float _w) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.w = _w;
    }

    public Vector4(Vector4 vec) {
        this.set(vec);
    }

    public Vector4 set(float _x, float _y, float _z, float _w) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        this.w = _w;
        return this;
    }

    public Vector4 set(Vector4 vec) {
        return this.set(vec.x, vec.y, vec.z, vec.w);
    }
}
