// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.nio.ByteBuffer;

public final class ClipperOffset {
    private final long address = this.newInstance();

    private native long newInstance();

    public native void clear();

    public native void addPath(int numPoints, ByteBuffer points, int joinType, int endType);

    public native void execute(double delta);

    public native int getPolygonCount();

    public native int getPolygon(int index, ByteBuffer vertices);

    public static enum EndType {
        etClosedPolygon,
        etClosedLine,
        etOpenButt,
        etOpenSquare,
        etOpenRound;
    }

    public static enum JoinType {
        jtSquare,
        jtRound,
        jtMiter;
    }
}
