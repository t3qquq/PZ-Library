// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;
import zombie.util.PZXmlUtil;

public final class FileTask_ParseXML extends FileTask {
    Class<? extends Object> m_class;
    String m_filename;

    public FileTask_ParseXML(Class<? extends Object> clazz, String string, IFileTaskCallback iFileTaskCallback, FileSystem fileSystem) {
        super(fileSystem, iFileTaskCallback);
        this.m_class = clazz;
        this.m_filename = string;
    }

    @Override
    public String getErrorMessage() {
        return this.m_filename;
    }

    @Override
    public void done() {
        this.m_class = null;
        this.m_filename = null;
    }

    @Override
    public Object call() throws Exception {
        return PZXmlUtil.parse(this.m_class, this.m_filename);
    }
}
