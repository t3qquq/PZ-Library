// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public final class TexturePackPage {
    public static HashMap<String, Stack<String>> FoundTextures = new HashMap<>();
    public static final HashMap<String, Texture> subTextureMap = new HashMap<>();
    public static final HashMap<String, Texture> subTextureMap2 = new HashMap<>();
    public static final HashMap<String, TexturePackPage> texturePackPageMap = new HashMap<>();
    public static final HashMap<String, String> TexturePackPageNameMap = new HashMap<>();
    public final HashMap<String, Texture> subTextures = new HashMap<>();
    public Texture tex = null;
    static ByteBuffer SliceBuffer = null;
    static boolean bHasCache = false;
    static int percent = 0;
    public static int chl1 = 0;
    public static int chl2 = 0;
    public static int chl3 = 0;
    public static int chl4 = 0;
    static StringBuilder v = new StringBuilder(50);
    public static ArrayList<TexturePackPage.SubTextureInfo> TempSubTextureInfo = new ArrayList<>();
    public static ArrayList<String> tempFilenameCheck = new ArrayList<>();
    public static boolean bIgnoreWorldItemTextures = true;

    public static void LoadDir(String var0) throws URISyntaxException {
    }

    public static void searchFolders(File var0) {
    }

    public static Texture getTexture(String string) {
        if (string.contains(".png")) {
            return Texture.getSharedTexture(string);
        } else {
            return subTextureMap.containsKey(string) ? subTextureMap.get(string) : null;
        }
    }

    public static int readInt(InputStream inputStream) throws EOFException, IOException {
        int int0 = inputStream.read();
        int int1 = inputStream.read();
        int int2 = inputStream.read();
        int int3 = inputStream.read();
        chl1 = int0;
        chl2 = int1;
        chl3 = int2;
        chl4 = int3;
        if ((int0 | int1 | int2 | int3) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 0) + (int1 << 8) + (int2 << 16) + (int3 << 24);
        }
    }

    public static int readInt(ByteBuffer byteBuffer) throws EOFException, IOException {
        byte byte0 = byteBuffer.get();
        byte byte1 = byteBuffer.get();
        byte byte2 = byteBuffer.get();
        byte byte3 = byteBuffer.get();
        chl1 = byte0;
        chl2 = byte1;
        chl3 = byte2;
        chl4 = byte3;
        return (byte0 << 0) + (byte1 << 8) + (byte2 << 16) + (byte3 << 24);
    }

    public static int readIntByte(InputStream inputStream) throws EOFException, IOException {
        int int0 = chl2;
        int int1 = chl3;
        int int2 = chl4;
        int int3 = inputStream.read();
        chl1 = int0;
        chl2 = int1;
        chl3 = int2;
        chl4 = int3;
        if ((int0 | int1 | int2 | int3) < 0) {
            throw new EOFException();
        } else {
            return (int0 << 0) + (int1 << 8) + (int2 << 16) + (int3 << 24);
        }
    }

    public static String ReadString(InputStream inputStream) throws IOException {
        v.setLength(0);
        int int0 = readInt(inputStream);

        for (int int1 = 0; int1 < int0; int1++) {
            v.append((char)inputStream.read());
        }

        return v.toString();
    }

    public void loadFromPackFileDDS(BufferedInputStream bufferedInputStream) throws IOException {
        String string0 = ReadString(bufferedInputStream);
        tempFilenameCheck.add(string0);
        int int0 = readInt(bufferedInputStream);
        boolean boolean0 = readInt(bufferedInputStream) != 0;
        TempSubTextureInfo.clear();

        for (int int1 = 0; int1 < int0; int1++) {
            String string1 = ReadString(bufferedInputStream);
            int int2 = readInt(bufferedInputStream);
            int int3 = readInt(bufferedInputStream);
            int int4 = readInt(bufferedInputStream);
            int int5 = readInt(bufferedInputStream);
            int int6 = readInt(bufferedInputStream);
            int int7 = readInt(bufferedInputStream);
            int int8 = readInt(bufferedInputStream);
            int int9 = readInt(bufferedInputStream);
            if (string1.contains("ZombieWalk") && string1.contains("BobZ_")) {
                TempSubTextureInfo.add(new TexturePackPage.SubTextureInfo(int2, int3, int4, int5, int6, int7, int8, int9, string1));
            }
        }

        if (TempSubTextureInfo.isEmpty()) {
            int int10 = 0;

            do {
                int10 = readIntByte(bufferedInputStream);
            } while (int10 != -559038737);
        } else {
            Texture texture0 = new Texture(string0, bufferedInputStream, boolean0, Texture.PZFileformat.DDS);

            for (int int11 = 0; int11 < TempSubTextureInfo.size(); int11++) {
                TexturePackPage.SubTextureInfo subTextureInfo = TempSubTextureInfo.get(int11);
                Texture texture1 = texture0.split(subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
                texture1.copyMaskRegion(texture0, subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
                texture1.setName(subTextureInfo.name);
                this.subTextures.put(subTextureInfo.name, texture1);
                subTextureMap.put(subTextureInfo.name, texture1);
                texture1.offsetX = subTextureInfo.ox;
                texture1.offsetY = subTextureInfo.oy;
                texture1.widthOrig = subTextureInfo.fx;
                texture1.heightOrig = subTextureInfo.fy;
            }

            texture0.mask = null;
            texturePackPageMap.put(string0, this);
            int int12 = 0;

            do {
                int12 = readIntByte(bufferedInputStream);
            } while (int12 != -559038737);
        }
    }

    public void loadFromPackFile(BufferedInputStream bufferedInputStream) throws Exception {
        String string0 = ReadString(bufferedInputStream);
        tempFilenameCheck.add(string0);
        int int0 = readInt(bufferedInputStream);
        boolean boolean0 = readInt(bufferedInputStream) != 0;
        if (boolean0) {
            boolean boolean1 = false;
        }

        TempSubTextureInfo.clear();

        for (int int1 = 0; int1 < int0; int1++) {
            String string1 = ReadString(bufferedInputStream);
            int int2 = readInt(bufferedInputStream);
            int int3 = readInt(bufferedInputStream);
            int int4 = readInt(bufferedInputStream);
            int int5 = readInt(bufferedInputStream);
            int int6 = readInt(bufferedInputStream);
            int int7 = readInt(bufferedInputStream);
            int int8 = readInt(bufferedInputStream);
            int int9 = readInt(bufferedInputStream);
            if (!bIgnoreWorldItemTextures || !string1.startsWith("WItem_")) {
                TempSubTextureInfo.add(new TexturePackPage.SubTextureInfo(int2, int3, int4, int5, int6, int7, int8, int9, string1));
            }
        }

        Texture texture0 = new Texture(string0, bufferedInputStream, boolean0);

        for (int int10 = 0; int10 < TempSubTextureInfo.size(); int10++) {
            TexturePackPage.SubTextureInfo subTextureInfo = TempSubTextureInfo.get(int10);
            Texture texture1 = texture0.split(subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
            texture1.copyMaskRegion(texture0, subTextureInfo.x, subTextureInfo.y, subTextureInfo.w, subTextureInfo.h);
            texture1.setName(subTextureInfo.name);
            this.subTextures.put(subTextureInfo.name, texture1);
            subTextureMap.put(subTextureInfo.name, texture1);
            texture1.offsetX = subTextureInfo.ox;
            texture1.offsetY = subTextureInfo.oy;
            texture1.widthOrig = subTextureInfo.fx;
            texture1.heightOrig = subTextureInfo.fy;
        }

        texture0.mask = null;
        texturePackPageMap.put(string0, this);
        int int11 = 0;

        do {
            int11 = readIntByte(bufferedInputStream);
        } while (int11 != -559038737);
    }

    public static class SubTextureInfo {
        public int w;
        public int h;
        public int x;
        public int y;
        public int ox;
        public int oy;
        public int fx;
        public int fy;
        public String name;

        public SubTextureInfo(int _x, int _y, int _w, int _h, int _ox, int _oy, int _fx, int _fy, String _name) {
            this.x = _x;
            this.y = _y;
            this.w = _w;
            this.h = _h;
            this.ox = _ox;
            this.oy = _oy;
            this.fx = _fx;
            this.fy = _fy;
            this.name = _name;
        }
    }
}
