// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.nio.ByteBuffer;
import zombie.debug.DebugLog;

public class Clipper {
    private long address;
    final ByteBuffer bb = ByteBuffer.allocateDirect(64);

    public static void init() {
        String string = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.clipper"))) {
            DebugLog.log("***** Loading debug version of PZClipper");
            string = "d";
        }

        if (System.getProperty("os.name").contains("OS X")) {
            System.loadLibrary("PZClipper");
        } else if (System.getProperty("sun.arch.data.model").equals("64")) {
            System.loadLibrary("PZClipper64" + string);
        } else {
            System.loadLibrary("PZClipper32" + string);
        }

        n_init();
    }

    public Clipper() {
        this.newInstance();
    }

    private native void newInstance();

    public native void clear();

    public native void addPath(int numPoints, ByteBuffer points, boolean bClip);

    public native void addLine(float x1, float y1, float x2, float y2);

    public native void addAABB(float x1, float y1, float x2, float y2);

    public void addAABBBevel(float x1, float y1, float x2, float y2, float RADIUS) {
        this.bb.clear();
        this.bb.putFloat(x1 + RADIUS);
        this.bb.putFloat(y1);
        this.bb.putFloat(x2 - RADIUS);
        this.bb.putFloat(y1);
        this.bb.putFloat(x2);
        this.bb.putFloat(y1 + RADIUS);
        this.bb.putFloat(x2);
        this.bb.putFloat(y2 - RADIUS);
        this.bb.putFloat(x2 - RADIUS);
        this.bb.putFloat(y2);
        this.bb.putFloat(x1 + RADIUS);
        this.bb.putFloat(y2);
        this.bb.putFloat(x1);
        this.bb.putFloat(y2 - RADIUS);
        this.bb.putFloat(x1);
        this.bb.putFloat(y1 + RADIUS);
        this.addPath(this.bb.position() / 4 / 2, this.bb, false);
    }

    public native void addPolygon(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4);

    public native void clipAABB(float x1, float y1, float x2, float y2);

    public int generatePolygons() {
        return this.generatePolygons(0.0);
    }

    public native int generatePolygons(double delta);

    public native int getPolygon(int index, ByteBuffer vertices);

    public native int generateTriangulatePolygons(int wx, int wy);

    public native int triangulate(int index, ByteBuffer vertices);

    public static native void n_init();

    private static void writeToStdErr(String string) {
        System.err.println(string);
    }
}
