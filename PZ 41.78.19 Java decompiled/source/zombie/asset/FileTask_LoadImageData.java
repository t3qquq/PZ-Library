// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import zombie.core.textures.ImageData;
import zombie.core.textures.TextureIDAssetManager;
import zombie.debug.DebugOptions;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;

public final class FileTask_LoadImageData extends FileTask {
    String m_image_name;
    boolean bMask = false;

    public FileTask_LoadImageData(String string, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
        super(fileSystem, iFileTaskCallback);
        this.m_image_name = string;
    }

    @Override
    public String getErrorMessage() {
        return this.m_image_name;
    }

    @Override
    public void done() {
    }

    @Override
    public Object call() throws Exception {
        TextureIDAssetManager.instance.waitFileTask();
        if (DebugOptions.instance.AssetSlowLoad.getValue()) {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException interruptedException) {
            }
        }

        ImageData imageData;
        try (
            FileInputStream fileInputStream = new FileInputStream(this.m_image_name);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            imageData = new ImageData(bufferedInputStream, this.bMask);
        }

        return imageData;
    }
}
