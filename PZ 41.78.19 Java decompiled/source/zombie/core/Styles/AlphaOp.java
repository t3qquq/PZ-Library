// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.Styles;

import java.nio.FloatBuffer;
import org.lwjgl.util.ReadableColor;

/**
 * What to do with the alpha to the colours of a sprite
 */
public enum AlphaOp {
    PREMULTIPLY {
        @Override
        protected int calc(ReadableColor readableColor, int int0) {
            float float0 = readableColor.getAlpha() * int0 * 0.003921569F;
            float float1 = float0 * 0.003921569F;
            return (int)(readableColor.getRed() * float1) << 0
                | (int)(readableColor.getGreen() * float1) << 8
                | (int)(readableColor.getBlue() * float1) << 16
                | (int)float0 << 24;
        }
    },
    KEEP {
        @Override
        protected int calc(ReadableColor readableColor, int var2) {
            return readableColor.getRed() << 0 | readableColor.getGreen() << 8 | readableColor.getBlue() << 16 | readableColor.getAlpha() << 24;
        }
    },
    ZERO {
        @Override
        protected int calc(ReadableColor readableColor, int int0) {
            float float0 = readableColor.getAlpha() * int0 * 0.003921569F;
            float float1 = float0 * 0.003921569F;
            return (int)(readableColor.getRed() * float1) << 0 | (int)(readableColor.getGreen() * float1) << 8 | (int)(readableColor.getBlue() * float1) << 16;
        }
    };

    private static final float PREMULT_ALPHA = 0.003921569F;

    public final void op(ReadableColor c, int alpha, FloatBuffer dest) {
        dest.put(Float.intBitsToFloat(this.calc(c, alpha)));
    }

    public final void op(int c, int alpha, FloatBuffer dest) {
        dest.put(Float.intBitsToFloat(c));
    }

    protected abstract int calc(ReadableColor var1, int var2);
}
