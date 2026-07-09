// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.nio.IntBuffer;
import org.lwjglx.BufferUtils;

/**
 * Created by LEMMY on 3/17/2016.
 */
public final class Vbo {
    public IntBuffer b = BufferUtils.createIntBuffer(4);
    public int VboID;
    public int EboID;
    public int NumElements;
    public int VertexStride;
    public boolean FaceDataOnly;
}
