// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.nio.ByteBuffer;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.WrappedBuffer;

public final class MipMapLevel {
    public final int width;
    public final int height;
    public final WrappedBuffer data;

    public MipMapLevel(int _width, int _height) {
        this.width = _width;
        this.height = _height;
        this.data = DirectBufferAllocator.allocate(_width * _height * 4);
    }

    public MipMapLevel(int _width, int _height, WrappedBuffer _data) {
        this.width = _width;
        this.height = _height;
        this.data = _data;
    }

    public void dispose() {
        if (this.data != null) {
            this.data.dispose();
        }
    }

    public boolean isDisposed() {
        return this.data != null && this.data.isDisposed();
    }

    public void rewind() {
        if (this.data != null) {
            this.data.getBuffer().rewind();
        }
    }

    public ByteBuffer getBuffer() {
        return this.data == null ? null : this.data.getBuffer();
    }

    public int getDataSize() {
        return this.width * this.height * 4;
    }
}
