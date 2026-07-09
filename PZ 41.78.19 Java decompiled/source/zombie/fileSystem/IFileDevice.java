// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;

public interface IFileDevice {
    IFile createFile(IFile child);

    void destroyFile(IFile file);

    InputStream createStream(String path, InputStream child) throws IOException;

    void destroyStream(InputStream stream);

    String name();
}
