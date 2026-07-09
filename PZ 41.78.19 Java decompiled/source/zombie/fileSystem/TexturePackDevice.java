// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.fileSystem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.core.textures.TexturePackPage;

public final class TexturePackDevice implements IFileDevice {
    static final int VERSION1 = 1;
    static final int VERSION_LATEST = 1;
    String m_name;
    String m_filename;
    int m_version = -1;
    final ArrayList<TexturePackDevice.Page> m_pages = new ArrayList<>();
    final HashMap<String, TexturePackDevice.Page> m_pagemap = new HashMap<>();
    final HashMap<String, TexturePackDevice.SubTexture> m_submap = new HashMap<>();
    int m_textureFlags;

    private static long skipInput(InputStream inputStream, long long1) throws IOException {
        long long0 = 0L;

        while (long0 < long1) {
            long long2 = inputStream.skip(long1 - long0);
            if (long2 > 0L) {
                long0 += long2;
            }
        }

        return long0;
    }

    public TexturePackDevice(String string, int int0) {
        this.m_name = string;
        this.m_filename = ZomboidFileSystem.instance.getString("media/texturepacks/" + string + ".pack");
        this.m_textureFlags = int0;
    }

    @Override
    public IFile createFile(IFile var1) {
        return null;
    }

    @Override
    public void destroyFile(IFile var1) {
    }

    @Override
    public InputStream createStream(String string, InputStream var2) throws IOException {
        this.initMetaData();
        return new TexturePackDevice.TexturePackInputStream(string, this);
    }

    @Override
    public void destroyStream(InputStream inputStream) {
        if (inputStream instanceof TexturePackDevice.TexturePackInputStream) {
        }
    }

    @Override
    public String name() {
        return this.m_name;
    }

    public void getSubTextureInfo(FileSystem.TexturePackTextures texturePackTextures) throws IOException {
        this.initMetaData();

        for (TexturePackDevice.SubTexture subTexture0 : this.m_submap.values()) {
            FileSystem.SubTexture subTexture1 = new FileSystem.SubTexture(this.name(), subTexture0.m_page.m_name, subTexture0.m_info);
            texturePackTextures.put(subTexture0.m_info.name, subTexture1);
        }
    }

    private void initMetaData() throws IOException {
        if (this.m_pages.isEmpty()) {
            try (
                FileInputStream fileInputStream = new FileInputStream(this.m_filename);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                TexturePackDevice.PositionInputStream positionInputStream = new TexturePackDevice.PositionInputStream(bufferedInputStream);
            ) {
                positionInputStream.mark(4);
                int int0 = positionInputStream.read();
                int int1 = positionInputStream.read();
                int int2 = positionInputStream.read();
                int int3 = positionInputStream.read();
                if (int0 == 80 && int1 == 90 && int2 == 80 && int3 == 75) {
                    this.m_version = TexturePackPage.readInt(positionInputStream);
                    if (this.m_version < 1 || this.m_version > 1) {
                        throw new IOException("invalid .pack file version " + this.m_version);
                    }
                } else {
                    positionInputStream.reset();
                    this.m_version = 0;
                }

                int int4 = TexturePackPage.readInt(positionInputStream);

                for (int int5 = 0; int5 < int4; int5++) {
                    TexturePackDevice.Page page = this.readPage(positionInputStream);
                    this.m_pages.add(page);
                    this.m_pagemap.put(page.m_name, page);

                    for (TexturePackPage.SubTextureInfo subTextureInfo : page.m_sub) {
                        this.m_submap.put(subTextureInfo.name, new TexturePackDevice.SubTexture(page, subTextureInfo));
                    }
                }
            }
        }
    }

    private TexturePackDevice.Page readPage(TexturePackDevice.PositionInputStream positionInputStream) throws IOException {
        TexturePackDevice.Page page = new TexturePackDevice.Page();
        String string0 = TexturePackPage.ReadString(positionInputStream);
        int int0 = TexturePackPage.readInt(positionInputStream);
        boolean boolean0 = TexturePackPage.readInt(positionInputStream) != 0;
        page.m_name = string0;
        page.m_has_alpha = boolean0;

        for (int int1 = 0; int1 < int0; int1++) {
            String string1 = TexturePackPage.ReadString(positionInputStream);
            int int2 = TexturePackPage.readInt(positionInputStream);
            int int3 = TexturePackPage.readInt(positionInputStream);
            int int4 = TexturePackPage.readInt(positionInputStream);
            int int5 = TexturePackPage.readInt(positionInputStream);
            int int6 = TexturePackPage.readInt(positionInputStream);
            int int7 = TexturePackPage.readInt(positionInputStream);
            int int8 = TexturePackPage.readInt(positionInputStream);
            int int9 = TexturePackPage.readInt(positionInputStream);
            page.m_sub.add(new TexturePackPage.SubTextureInfo(int2, int3, int4, int5, int6, int7, int8, int9, string1));
        }

        page.m_png_start = positionInputStream.getPosition();
        if (this.m_version == 0) {
            int int10 = 0;

            do {
                int10 = TexturePackPage.readIntByte(positionInputStream);
            } while (int10 != -559038737);
        } else {
            int int11 = TexturePackPage.readInt(positionInputStream);
            skipInput(positionInputStream, int11);
        }

        return page;
    }

    public boolean isAlpha(String string) {
        TexturePackDevice.Page page = this.m_pagemap.get(string);
        return page.m_has_alpha;
    }

    public int getTextureFlags() {
        return this.m_textureFlags;
    }

    static final class Page {
        String m_name;
        boolean m_has_alpha = false;
        long m_png_start = -1L;
        final ArrayList<TexturePackPage.SubTextureInfo> m_sub = new ArrayList<>();
    }

    public final class PositionInputStream extends FilterInputStream {
        private long pos = 0L;
        private long mark = 0L;

        public PositionInputStream(InputStream inputStream) {
            super(inputStream);
        }

        public synchronized long getPosition() {
            return this.pos;
        }

        @Override
        public synchronized int read() throws IOException {
            int int0 = super.read();
            if (int0 >= 0) {
                this.pos++;
            }

            return int0;
        }

        @Override
        public synchronized int read(byte[] bytes, int int1, int int2) throws IOException {
            int int0 = super.read(bytes, int1, int2);
            if (int0 > 0) {
                this.pos += int0;
            }

            return int0;
        }

        @Override
        public synchronized long skip(long long1) throws IOException {
            long long0 = super.skip(long1);
            if (long0 > 0L) {
                this.pos += long0;
            }

            return long0;
        }

        @Override
        public synchronized void mark(int int0) {
            super.mark(int0);
            this.mark = this.pos;
        }

        @Override
        public synchronized void reset() throws IOException {
            if (!this.markSupported()) {
                throw new IOException("Mark not supported.");
            } else {
                super.reset();
                this.pos = this.mark;
            }
        }
    }

    static final class SubTexture {
        final TexturePackDevice.Page m_page;
        final TexturePackPage.SubTextureInfo m_info;

        SubTexture(TexturePackDevice.Page page, TexturePackPage.SubTextureInfo subTextureInfo) {
            this.m_page = page;
            this.m_info = subTextureInfo;
        }
    }

    static class TexturePackInputStream extends FileInputStream {
        TexturePackDevice m_device;

        TexturePackInputStream(String string, TexturePackDevice texturePackDevice) throws IOException {
            super(texturePackDevice.m_filename);
            this.m_device = texturePackDevice;
            TexturePackDevice.Page page = this.m_device.m_pagemap.get(string);
            if (page == null) {
                throw new FileNotFoundException();
            } else {
                TexturePackDevice.skipInput(this, page.m_png_start);
                if (texturePackDevice.m_version >= 1) {
                    int int0 = TexturePackPage.readInt(this);
                }
            }
        }
    }
}
