// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import java.io.File;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;

public final class FileTask_Exists extends FileTask {
    String fileName;

    public FileTask_Exists(String string, IFileTaskCallback iFileTaskCallback, FileSystem fileSystem) {
        super(fileSystem, iFileTaskCallback);
        this.fileName = string;
    }

    @Override
    public void done() {
    }

    @Override
    public Object call() throws Exception {
        return new File(this.fileName).exists();
    }
}
