// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;

public class Color3b extends Tuple3b implements Serializable {
    static final long serialVersionUID = 6632576088353444794L;

    public Color3b(byte byte0, byte byte1, byte byte2) {
        super(byte0, byte1, byte2);
    }

    public Color3b(byte[] bytes) {
        super(bytes);
    }

    public Color3b(Color3b color3b1) {
        super(color3b1);
    }

    public Color3b(Tuple3b tuple3b) {
        super(tuple3b);
    }

    public Color3b(Color color) {
        super((byte)color.getRed(), (byte)color.getGreen(), (byte)color.getBlue());
    }

    public Color3b() {
    }

    public final void set(Color color) {
        this.x = (byte)color.getRed();
        this.y = (byte)color.getGreen();
        this.z = (byte)color.getBlue();
    }

    public final Color get() {
        int int0 = this.x & 255;
        int int1 = this.y & 255;
        int int2 = this.z & 255;
        return new Color(int0, int1, int2);
    }
}
