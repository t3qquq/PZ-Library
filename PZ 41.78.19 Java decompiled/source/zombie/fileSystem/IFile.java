// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.io.InputStream;

public interface IFile {
    boolean open(String path, int mode);

    void close();

    boolean read(byte[] var1, long var2);

    boolean write(byte[] var1, long var2);

    byte[] getBuffer();

    long size();

    boolean seek(FileSeekMode mode, long pos);

    long pos();

    InputStream getInputStream();

    IFileDevice getDevice();

    void release();
}
