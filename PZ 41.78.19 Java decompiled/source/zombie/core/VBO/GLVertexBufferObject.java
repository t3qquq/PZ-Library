// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.VBO;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.lwjgl.opengl.ARBMapBufferRange;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjglx.opengl.OpenGLException;
import zombie.core.skinnedmodel.model.VertexBufferObject;

/**
 * Vertex buffer object wrapper
 */
public class GLVertexBufferObject {
    public static IGLBufferObject funcs;
    private long size;
    private final int type;
    private final int usage;
    private transient int id;
    private transient boolean mapped;
    private transient boolean cleared;
    private transient ByteBuffer buffer;
    private int m_vertexAttribArray = -1;

    public static void init() {
        if (GL.getCapabilities().OpenGL15) {
            System.out.println("OpenGL 1.5 buffer objects supported");
            funcs = new GLBufferObject15();
        } else {
            if (!GL.getCapabilities().GL_ARB_vertex_buffer_object) {
                throw new RuntimeException("Neither OpenGL 1.5 nor GL_ARB_vertex_buffer_object supported");
            }

            System.out.println("GL_ARB_vertex_buffer_object supported");
            funcs = new GLBufferObjectARB();
        }

        VertexBufferObject.funcs = funcs;
    }

    /**
     * C'tor
     */
    public GLVertexBufferObject(long _size, int _type, int _usage) {
        this.size = _size;
        this.type = _type;
        this.usage = _usage;
    }

    /**
     * C'tor
     */
    public GLVertexBufferObject(int _type, int _usage) {
        this.size = 0L;
        this.type = _type;
        this.usage = _usage;
    }

    public void create() {
        this.id = funcs.glGenBuffers();
    }

    /**
     * Tells the driver we don't care about the data in our buffer any more (may improve performance before mapping)
     */
    public void clear() {
        if (!this.cleared) {
            funcs.glBufferData(this.type, this.size, this.usage);
            this.cleared = true;
        }
    }

    protected void doDestroy() {
        if (this.id != 0) {
            this.unmap();
            funcs.glDeleteBuffers(this.id);
            this.id = 0;
        }
    }

    public ByteBuffer map(int _size) {
        if (!this.mapped) {
            if (this.size != _size) {
                this.size = _size;
                this.clear();
            }

            if (this.buffer != null && this.buffer.capacity() < _size) {
                this.buffer = null;
            }

            ByteBuffer byteBuffer = this.buffer;
            if (GL.getCapabilities().OpenGL30) {
                byte byte0 = 34;
                this.buffer = GL30.glMapBufferRange(this.type, 0L, _size, byte0, this.buffer);
            } else if (GL.getCapabilities().GL_ARB_map_buffer_range) {
                byte byte1 = 34;
                this.buffer = ARBMapBufferRange.glMapBufferRange(this.type, 0L, _size, byte1, this.buffer);
            } else {
                this.buffer = funcs.glMapBuffer(this.type, funcs.GL_WRITE_ONLY(), _size, this.buffer);
            }

            if (this.buffer == null) {
                throw new OpenGLException("Failed to map buffer " + this);
            }

            if (this.buffer != byteBuffer && byteBuffer != null) {
            }

            this.buffer.order(ByteOrder.nativeOrder()).clear().limit(_size);
            this.mapped = true;
            this.cleared = false;
        }

        return this.buffer;
    }

    public ByteBuffer map() {
        if (!this.mapped) {
            assert this.size > 0L;

            this.clear();
            ByteBuffer byteBuffer = this.buffer;
            if (GL.getCapabilities().OpenGL30) {
                byte byte0 = 34;
                this.buffer = GL30.glMapBufferRange(this.type, 0L, this.size, byte0, this.buffer);
            } else if (GL.getCapabilities().GL_ARB_map_buffer_range) {
                byte byte1 = 34;
                this.buffer = ARBMapBufferRange.glMapBufferRange(this.type, 0L, this.size, byte1, this.buffer);
            } else {
                this.buffer = funcs.glMapBuffer(this.type, funcs.GL_WRITE_ONLY(), this.size, this.buffer);
            }

            if (this.buffer == null) {
                throw new OpenGLException("Failed to map a buffer " + this.size + " bytes long");
            }

            if (this.buffer != byteBuffer && byteBuffer != null) {
            }

            this.buffer.order(ByteOrder.nativeOrder()).clear().limit((int)this.size);
            this.mapped = true;
            this.cleared = false;
        }

        return this.buffer;
    }

    public void orphan() {
        funcs.glMapBuffer(this.type, this.usage, this.size, null);
    }

    public boolean unmap() {
        if (this.mapped) {
            this.mapped = false;
            return funcs.glUnmapBuffer(this.type);
        } else {
            return true;
        }
    }

    public boolean isMapped() {
        return this.mapped;
    }

    public void bufferData(ByteBuffer data) {
        funcs.glBufferData(this.type, data, this.usage);
    }

    @Override
    public String toString() {
        return "GLVertexBufferObject[" + this.id + ", " + this.size + "]";
    }

    public void bind() {
        funcs.glBindBuffer(this.type, this.id);
    }

    public void bindNone() {
        funcs.glBindBuffer(this.type, 0);
    }

    public int getID() {
        return this.id;
    }

    public void enableVertexAttribArray(int index) {
        if (this.m_vertexAttribArray != index) {
            this.disableVertexAttribArray();
            if (index >= 0) {
                GL20.glEnableVertexAttribArray(index);
            }

            this.m_vertexAttribArray = index >= 0 ? index : -1;
        }
    }

    public void disableVertexAttribArray() {
        if (this.m_vertexAttribArray != -1) {
            GL20.glDisableVertexAttribArray(this.m_vertexAttribArray);
            this.m_vertexAttribArray = -1;
        }
    }
}
