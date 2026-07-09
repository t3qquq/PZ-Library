// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.util.concurrent.Callable;

public abstract class FileTask implements Callable<Object> {
    protected final FileSystem m_file_system;
    protected final IFileTaskCallback m_cb;
    protected int m_priority = 5;

    public FileTask(FileSystem fileSystem) {
        this.m_file_system = fileSystem;
        this.m_cb = null;
    }

    public FileTask(FileSystem fileSystem, IFileTaskCallback cb) {
        this.m_file_system = fileSystem;
        this.m_cb = cb;
    }

    public void handleResult(Object result) {
        if (this.m_cb != null) {
            this.m_cb.onFileTaskFinished(result);
        }
    }

    public void setPriority(int priority) {
        this.m_priority = priority;
    }

    public abstract void done();

    public String getErrorMessage() {
        return null;
    }
}
