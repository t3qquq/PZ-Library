// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.VBO;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL15;

public final class GLBufferObject15 implements IGLBufferObject {
    @Override
    public int GL_ARRAY_BUFFER() {
        return 34962;
    }

    @Override
    public int GL_ELEMENT_ARRAY_BUFFER() {
        return 34963;
    }

    @Override
    public int GL_STATIC_DRAW() {
        return 35044;
    }

    @Override
    public int GL_STREAM_DRAW() {
        return 35040;
    }

    @Override
    public int GL_BUFFER_SIZE() {
        return 34660;
    }

    @Override
    public int GL_WRITE_ONLY() {
        return 35001;
    }

    @Override
    public int glGenBuffers() {
        return GL15.glGenBuffers();
    }

    @Override
    public void glBindBuffer(int int0, int int1) {
        GL15.glBindBuffer(int0, int1);
    }

    @Override
    public void glDeleteBuffers(int int0) {
        GL15.glDeleteBuffers(int0);
    }

    @Override
    public void glBufferData(int int0, ByteBuffer byteBuffer, int int1) {
        GL15.glBufferData(int0, byteBuffer, int1);
    }

    @Override
    public void glBufferData(int int0, long long0, int int1) {
        GL15.glBufferData(int0, long0, int1);
    }

    @Override
    public ByteBuffer glMapBuffer(int int0, int int1, long long0, ByteBuffer byteBuffer) {
        return GL15.glMapBuffer(int0, int1, long0, byteBuffer);
    }

    @Override
    public boolean glUnmapBuffer(int int0) {
        return GL15.glUnmapBuffer(int0);
    }

    @Override
    public void glGetBufferParameter(int int0, int int1, IntBuffer intBuffer) {
        GL15.glGetBufferParameteriv(int0, int1, intBuffer);
    }
}
