// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class MemoryFileDevice implements IFileDevice {
    @Override
    public IFile createFile(IFile iFile) {
        return new MemoryFileDevice.MemoryFile(iFile, this);
    }

    @Override
    public void destroyFile(IFile var1) {
    }

    @Override
    public InputStream createStream(String var1, InputStream var2) throws IOException {
        return null;
    }

    @Override
    public void destroyStream(InputStream var1) {
    }

    @Override
    public String name() {
        return "memory";
    }

    private static class MemoryFile implements IFile {
        final MemoryFileDevice m_device;
        byte[] m_buffer;
        long m_size;
        long m_pos;
        IFile m_file;
        boolean m_write;

        MemoryFile(IFile iFile, MemoryFileDevice memoryFileDevice) {
            this.m_device = memoryFileDevice;
            this.m_buffer = null;
            this.m_size = 0L;
            this.m_pos = 0L;
            this.m_file = iFile;
            this.m_write = false;
        }

        @Override
        public boolean open(String string, int int0) {
            assert this.m_buffer == null;

            this.m_write = (int0 & 2) != 0;
            if (this.m_file != null) {
                if (this.m_file.open(string, int0)) {
                    if ((int0 & 1) != 0) {
                        this.m_size = this.m_file.size();
                        this.m_buffer = new byte[(int)this.m_size];
                        this.m_file.read(this.m_buffer, this.m_size);
                        this.m_pos = 0L;
                    }

                    return true;
                }
            } else if ((int0 & 2) != 0) {
                return true;
            }

            return false;
        }

        @Override
        public void close() {
            if (this.m_file != null) {
                if (this.m_write) {
                    this.m_file.seek(FileSeekMode.BEGIN, 0L);
                    this.m_file.write(this.m_buffer, this.m_size);
                }

                this.m_file.close();
            }

            this.m_buffer = null;
        }

        @Override
        public boolean read(byte[] bytes, long long1) {
            long long0 = this.m_pos + long1 < this.m_size ? long1 : this.m_size - this.m_pos;
            System.arraycopy(this.m_buffer, (int)this.m_pos, bytes, 0, (int)long0);
            this.m_pos += long0;
            return false;
        }

        @Override
        public boolean write(byte[] bytes, long long3) {
            long long0 = this.m_pos;
            long long1 = this.m_buffer.length;
            long long2 = this.m_size;
            if (long0 + long3 > long1) {
                long long4 = Math.max(long1 * 2L, long0 + long3);
                this.m_buffer = Arrays.copyOf(this.m_buffer, (int)long4);
            }

            System.arraycopy(bytes, 0, this.m_buffer, (int)long0, (int)long3);
            this.m_pos += long3;
            this.m_size = long0 + long3 > long2 ? long0 + long3 : long2;
            return true;
        }

        @Override
        public byte[] getBuffer() {
            return this.m_buffer;
        }

        @Override
        public long size() {
            return this.m_size;
        }

        @Override
        public boolean seek(FileSeekMode fileSeekMode, long long0) {
            switch (fileSeekMode) {
                case BEGIN:
                    assert long0 <= this.m_size;

                    this.m_pos = long0;
                    break;
                case CURRENT:
                    assert 0L <= this.m_pos + long0 && this.m_pos + long0 <= this.m_size;

                    this.m_pos += long0;
                    break;
                case END:
                    assert long0 <= this.m_size;

                    this.m_pos = this.m_size - long0;
            }

            boolean boolean0 = this.m_pos <= this.m_size;
            this.m_pos = Math.min(this.m_pos, this.m_size);
            return boolean0;
        }

        @Override
        public long pos() {
            return this.m_pos;
        }

        @Override
        public InputStream getInputStream() {
            return this.m_file != null ? this.m_file.getInputStream() : null;
        }

        @Override
        public IFileDevice getDevice() {
            return this.m_device;
        }

        @Override
        public void release() {
            this.m_buffer = null;
        }
    }
}
