// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;

public final class DeviceList {
    private final IFileDevice[] m_devices = new IFileDevice[8];

    public void add(IFileDevice device) {
        for (int int0 = 0; int0 < this.m_devices.length; int0++) {
            if (this.m_devices[int0] == null) {
                this.m_devices[int0] = device;
                break;
            }
        }
    }

    public IFile createFile() {
        IFile iFile = null;

        for (int int0 = 0; int0 < this.m_devices.length && this.m_devices[int0] != null; int0++) {
            iFile = this.m_devices[int0].createFile(iFile);
        }

        return iFile;
    }

    public InputStream createStream(String path) throws IOException {
        InputStream inputStream = null;

        for (int int0 = 0; int0 < this.m_devices.length && this.m_devices[int0] != null; int0++) {
            inputStream = this.m_devices[int0].createStream(path, inputStream);
        }

        return inputStream;
    }
}
