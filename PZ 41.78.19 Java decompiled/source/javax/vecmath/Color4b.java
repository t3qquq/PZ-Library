// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color4b extends Tuple4b implements Serializable {
    static final long serialVersionUID = -105080578052502155L;

    public Color4b(byte byte0, byte byte1, byte byte2, byte byte3) {
        super(byte0, byte1, byte2, byte3);
    }

    public Color4b(byte[] bytes) {
        super(bytes);
    }

    public Color4b(Color4b color4b1) {
        super(color4b1);
    }

    public Color4b(Tuple4b tuple4b) {
        super(tuple4b);
    }

    public Color4b(Color color) {
        super((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue(), (byte)color.getAlpha());
    }

    public Color4b() {
    }

    public final void set(Color color) {
        this.x = (byte)color.getRed();
        this.y = (byte)color.getGreen();
        this.z = (byte)color.getBlue();
        this.w = (byte)color.getAlpha();
    }

    public final Color get() {
        int int0 = this.x & 255;
        int int1 = this.y & 255;
        int int2 = this.z & 255;
        int int3 = this.w & 255;
        return new Color(int0, int1, int2, int3);
    }
}
