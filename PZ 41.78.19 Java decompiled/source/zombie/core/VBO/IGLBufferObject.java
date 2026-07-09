// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.VBO;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public interface IGLBufferObject {
    int GL_ARRAY_BUFFER();

    int GL_ELEMENT_ARRAY_BUFFER();

    int GL_STATIC_DRAW();

    int GL_STREAM_DRAW();

    int GL_BUFFER_SIZE();

    int GL_WRITE_ONLY();

    int glGenBuffers();

    void glBindBuffer(int target, int buffer);

    void glDeleteBuffers(int buffers);

    void glBufferData(int target, ByteBuffer data, int usage);

    void glBufferData(int target, long data_size, int usage);

    ByteBuffer glMapBuffer(int target, int access, long length, ByteBuffer old_buffer);

    boolean glUnmapBuffer(int target);

    void glGetBufferParameter(int target, int pname, IntBuffer params);
}
