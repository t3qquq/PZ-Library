// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import java.io.InputStream;
import zombie.core.textures.ImageData;
import zombie.core.textures.TextureIDAssetManager;
import zombie.fileSystem.DeviceList;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;

public final class FileTask_LoadPackImage extends FileTask {
    String m_pack_name;
    String m_image_name;
    boolean bMask;
    int m_flags;

    public FileTask_LoadPackImage(String string0, String string1, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
        super(fileSystem, iFileTaskCallback);
        this.m_pack_name = string0;
        this.m_image_name = string1;
        this.bMask = fileSystem.getTexturePackAlpha(string0, string1);
        this.m_flags = fileSystem.getTexturePackFlags(string0);
    }

    @Override
    public void done() {
    }

    @Override
    public Object call() throws Exception {
        TextureIDAssetManager.instance.waitFileTask();
        DeviceList deviceList = this.m_file_system.getTexturePackDevice(this.m_pack_name);

        ImageData imageData0;
        try (InputStream inputStream = this.m_file_system.openStream(deviceList, this.m_image_name)) {
            ImageData imageData1 = new ImageData(inputStream, this.bMask);
            if ((this.m_flags & 64) != 0) {
                imageData1.initMipMaps();
            }

            imageData0 = imageData1;
        }

        return imageData0;
    }
}
