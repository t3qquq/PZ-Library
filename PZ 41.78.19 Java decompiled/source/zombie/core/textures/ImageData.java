// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import com.evildevil.engines.bubble.texture.DDSLoader;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import zombie.ZomboidFileSystem;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.ImageUtils;
import zombie.core.utils.WrappedBuffer;
import zombie.core.znet.SteamFriends;
import zombie.debug.DebugOptions;
import zombie.util.list.PZArrayUtil;

public final class ImageData implements Serializable {
    private static final long serialVersionUID = -7893392091273534932L;
    /**
     * the data of image
     */
    public MipMapLevel data;
    private MipMapLevel[] mipMaps;
    private int height;
    private int heightHW;
    private boolean solid = true;
    private int width;
    private int widthHW;
    private int mipMapCount = -1;
    public boolean alphaPaddingDone = false;
    public boolean bPreserveTransparentColor = false;
    public BooleanGrid mask;
    private static final int BufferSize = 67108864;
    static final DDSLoader dds = new DDSLoader();
    public int id = -1;
    public static final int MIP_LEVEL_IDX_OFFSET = 0;
    private static final ThreadLocal<ImageData.L_generateMipMaps> TL_generateMipMaps = ThreadLocal.withInitial(ImageData.L_generateMipMaps::new);
    private static final ThreadLocal<ImageData.L_performAlphaPadding> TL_performAlphaPadding = ThreadLocal.withInitial(ImageData.L_performAlphaPadding::new);

    public ImageData(TextureID texture, WrappedBuffer bb) {
        this.data = new MipMapLevel(texture.widthHW, texture.heightHW, bb);
        this.width = texture.width;
        this.widthHW = texture.widthHW;
        this.height = texture.height;
        this.heightHW = texture.heightHW;
        this.solid = texture.solid;
    }

    public ImageData(String path) throws Exception {
        if (path.contains(".txt")) {
            path = path.replace(".txt", ".png");
        }

        path = Texture.processFilePath(path);
        path = ZomboidFileSystem.instance.getString(path);
        ZomboidFileSystem.instance.validatePrefix(path);

        try (
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            PNGDecoder pNGDecoder = new PNGDecoder(bufferedInputStream, false);
            this.width = pNGDecoder.getWidth();
            this.height = pNGDecoder.getHeight();
            this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
            this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
            this.data = new MipMapLevel(this.widthHW, this.heightHW);
            ByteBuffer byteBuffer = this.data.getBuffer();
            byteBuffer.rewind();
            int int0 = this.widthHW * 4;
            if (this.width != this.widthHW) {
                for (int int1 = this.width * 4; int1 < this.widthHW * 4; int1++) {
                    for (int int2 = 0; int2 < this.heightHW; int2++) {
                        byteBuffer.put(int1 + int2 * int0, (byte)0);
                    }
                }
            }

            if (this.height != this.heightHW) {
                for (int int3 = this.height; int3 < this.heightHW; int3++) {
                    for (int int4 = 0; int4 < this.width * 4; int4++) {
                        byteBuffer.put(int4 + int3 * int0, (byte)0);
                    }
                }
            }

            pNGDecoder.decode(this.data.getBuffer(), int0, PNGDecoder.Format.RGBA);
        } catch (Exception exception) {
            this.dispose();
            this.width = this.height = -1;
        }
    }

    /**
     * creates a new empty imageData
     * 
     * @param _width the width of imageData
     * @param _height the height of imageData
     */
    public ImageData(int _width, int _height) {
        this.width = _width;
        this.height = _height;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(_width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(_height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
    }

    public ImageData(int _width, int _height, WrappedBuffer _data) {
        this.width = _width;
        this.height = _height;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(_width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(_height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW, _data);
    }

    ImageData(String string0, String string1) {
        Pcx pcx = new Pcx(string0, string1);
        this.width = pcx.imageWidth;
        this.height = pcx.imageHeight;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
        this.setData(pcx);
        this.makeTransp((byte)pcx.palette[762], (byte)pcx.palette[763], (byte)pcx.palette[764], (byte)0);
    }

    ImageData(String string, int[] ints) {
        Pcx pcx = new Pcx(string, ints);
        this.width = pcx.imageWidth;
        this.height = pcx.imageHeight;
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
        this.setData(pcx);
        this.makeTransp((byte)pcx.palette[762], (byte)pcx.palette[763], (byte)pcx.palette[764], (byte)0);
    }

    public ImageData(BufferedInputStream b, boolean bDoMask, Texture.PZFileformat format) {
        if (format == Texture.PZFileformat.DDS) {
            RenderThread.invokeOnRenderContext(() -> this.id = dds.loadDDSFile(b));
            this.width = DDSLoader.lastWid;
            this.height = DDSLoader.lastHei;
            this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
            this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        }
    }

    public ImageData(InputStream b, boolean bDoMask) throws Exception {
        Object object = null;
        PNGDecoder pNGDecoder = new PNGDecoder(b, bDoMask);
        this.width = pNGDecoder.getWidth();
        this.height = pNGDecoder.getHeight();
        this.widthHW = ImageUtils.getNextPowerOfTwoHW(this.width);
        this.heightHW = ImageUtils.getNextPowerOfTwoHW(this.height);
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
        this.data.rewind();
        pNGDecoder.decode(this.data.getBuffer(), 4 * this.widthHW, PNGDecoder.Format.RGBA);
        if (bDoMask) {
            this.mask = pNGDecoder.mask;
        }
    }

    public static ImageData createSteamAvatar(long steamID) {
        WrappedBuffer wrappedBuffer = DirectBufferAllocator.allocate(65536);
        int int0 = SteamFriends.CreateSteamAvatar(steamID, wrappedBuffer.getBuffer());
        if (int0 <= 0) {
            return null;
        } else {
            int int1 = wrappedBuffer.getBuffer().position() / (int0 * 4);
            wrappedBuffer.getBuffer().flip();
            return new ImageData(int0, int1, wrappedBuffer);
        }
    }

    public MipMapLevel getData() {
        if (this.data == null) {
            this.data = new MipMapLevel(this.widthHW, this.heightHW, DirectBufferAllocator.allocate(67108864));
        }

        this.data.rewind();
        return this.data;
    }

    /**
     * make the image transparent
     * 
     * @param red the red value (0-255)
     * @param green the green value (0-255)
     * @param blue the blue value (0-255)
     */
    public void makeTransp(byte red, byte green, byte blue) {
        this.makeTransp(red, green, blue, (byte)0);
    }

    /**
     * make the image transparent
     * 
     * @param red the red value (0-255)
     * @param green the green value (0-255)
     * @param blue the blue value (0-255)
     * @param alpha the alpha value that will be setted (0-255)
     */
    public void makeTransp(byte red, byte green, byte blue, byte alpha) {
        this.solid = false;
        ByteBuffer byteBuffer = this.data.getBuffer();
        byteBuffer.rewind();
        int int0 = this.widthHW * 4;

        for (int int1 = 0; int1 < this.heightHW; int1++) {
            int int2 = byteBuffer.position();

            for (int int3 = 0; int3 < this.widthHW; int3++) {
                byte byte0 = byteBuffer.get();
                byte byte1 = byteBuffer.get();
                byte byte2 = byteBuffer.get();
                if (byte0 == red && byte1 == green && byte2 == blue) {
                    byteBuffer.put(alpha);
                } else {
                    byteBuffer.get();
                }

                if (int3 == this.width) {
                    byteBuffer.position(int2 + int0);
                    break;
                }
            }

            if (int1 == this.height) {
                break;
            }
        }

        byteBuffer.rewind();
    }

    public void setData(BufferedImage image) {
        if (image != null) {
            this.setData(image.getData());
        }
    }

    public void setData(Raster rasterData) {
        if (rasterData == null) {
            new Exception().printStackTrace();
        } else {
            this.width = rasterData.getWidth();
            this.height = rasterData.getHeight();
            if (this.width <= this.widthHW && this.height <= this.heightHW) {
                int[] ints = rasterData.getPixels(0, 0, this.width, this.height, (int[])null);
                ByteBuffer byteBuffer = this.data.getBuffer();
                byteBuffer.rewind();
                int int0 = 0;
                int int1 = byteBuffer.position();
                int int2 = this.widthHW * 4;

                for (int int3 = 0; int3 < ints.length; int3++) {
                    if (++int0 > this.width) {
                        byteBuffer.position(int1 + int2);
                        int1 = byteBuffer.position();
                        int0 = 1;
                    }

                    byteBuffer.put((byte)ints[int3]);
                    byteBuffer.put((byte)ints[++int3]);
                    byteBuffer.put((byte)ints[++int3]);
                    byteBuffer.put((byte)ints[++int3]);
                }

                byteBuffer.rewind();
                this.solid = false;
            } else {
                new Exception().printStackTrace();
            }
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.data = new MipMapLevel(this.widthHW, this.heightHW);
        ByteBuffer byteBuffer = this.data.getBuffer();

        for (int int0 = 0; int0 < this.widthHW * this.heightHW; int0++) {
            byteBuffer.put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte()).put(objectInputStream.readByte());
        }

        byteBuffer.flip();
    }

    private void setData(Pcx pcx) {
        this.width = pcx.imageWidth;
        this.height = pcx.imageHeight;
        if (this.width <= this.widthHW && this.height <= this.heightHW) {
            ByteBuffer byteBuffer = this.data.getBuffer();
            byteBuffer.rewind();
            int int0 = 0;
            int int1 = byteBuffer.position();
            int int2 = this.widthHW * 4;

            for (int int3 = 0; int3 < this.heightHW * this.widthHW * 3; int3++) {
                if (++int0 > this.width) {
                    int1 = byteBuffer.position();
                    int0 = 1;
                }

                byteBuffer.put(pcx.imageData[int3]);
                byteBuffer.put(pcx.imageData[++int3]);
                byteBuffer.put(pcx.imageData[++int3]);
                byteBuffer.put((byte)-1);
            }

            byteBuffer.rewind();
            this.solid = false;
        } else {
            new Exception().printStackTrace();
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        ByteBuffer byteBuffer = this.data.getBuffer();
        byteBuffer.rewind();

        for (int int0 = 0; int0 < this.widthHW * this.heightHW; int0++) {
            objectOutputStream.writeByte(byteBuffer.get());
            objectOutputStream.writeByte(byteBuffer.get());
            objectOutputStream.writeByte(byteBuffer.get());
            objectOutputStream.writeByte(byteBuffer.get());
        }
    }

    public int getHeight() {
        return this.height;
    }

    public int getHeightHW() {
        return this.heightHW;
    }

    public boolean isSolid() {
        return this.solid;
    }

    public int getWidth() {
        return this.width;
    }

    public int getWidthHW() {
        return this.widthHW;
    }

    public int getMipMapCount() {
        if (this.data == null) {
            return 0;
        } else {
            if (this.mipMapCount < 0) {
                this.mipMapCount = calculateNumMips(this.widthHW, this.heightHW);
            }

            return this.mipMapCount;
        }
    }

    public MipMapLevel getMipMapData(int idx) {
        if (this.data != null && !this.alphaPaddingDone) {
            this.performAlphaPadding();
        }

        if (idx == 0) {
            return this.getData();
        } else {
            if (this.mipMaps == null) {
                this.generateMipMaps();
            }

            int int0 = idx - 1;
            MipMapLevel mipMapLevel = this.mipMaps[int0];
            mipMapLevel.rewind();
            return mipMapLevel;
        }
    }

    public void initMipMaps() {
        int int0 = this.getMipMapCount();
        int int1 = PZMath.min(0, int0 - 1);
        int int2 = int0;

        for (int int3 = int1; int3 < int2; int3++) {
            MipMapLevel mipMapLevel = this.getMipMapData(int3);
        }
    }

    public void dispose() {
        if (this.data != null) {
            this.data.dispose();
            this.data = null;
        }

        if (this.mipMaps != null) {
            for (int int0 = 0; int0 < this.mipMaps.length; int0++) {
                this.mipMaps[int0].dispose();
                this.mipMaps[int0] = null;
            }

            this.mipMaps = null;
        }
    }

    private void generateMipMaps() {
        this.mipMapCount = calculateNumMips(this.widthHW, this.heightHW);
        int int0 = this.mipMapCount - 1;
        this.mipMaps = new MipMapLevel[int0];
        MipMapLevel mipMapLevel0 = this.getData();
        int int1 = this.widthHW;
        int int2 = this.heightHW;
        MipMapLevel mipMapLevel1 = mipMapLevel0;
        int int3 = getNextMipDimension(int1);
        int int4 = getNextMipDimension(int2);

        for (int int5 = 0; int5 < int0; int5++) {
            MipMapLevel mipMapLevel2 = new MipMapLevel(int3, int4);
            if (int5 < 2) {
                this.scaleMipLevelMaxAlpha(mipMapLevel1, mipMapLevel2, int5);
            } else {
                this.scaleMipLevelAverage(mipMapLevel1, mipMapLevel2, int5);
            }

            this.performAlphaPadding(mipMapLevel2);
            this.mipMaps[int5] = mipMapLevel2;
            mipMapLevel1 = mipMapLevel2;
            int3 = getNextMipDimension(int3);
            int4 = getNextMipDimension(int4);
        }
    }

    private void scaleMipLevelMaxAlpha(MipMapLevel mipMapLevel1, MipMapLevel mipMapLevel0, int int6) {
        ImageData.L_generateMipMaps l_generateMipMaps = TL_generateMipMaps.get();
        ByteBuffer byteBuffer0 = mipMapLevel0.getBuffer();
        byteBuffer0.rewind();
        int int0 = mipMapLevel1.width;
        int int1 = mipMapLevel1.height;
        ByteBuffer byteBuffer1 = mipMapLevel1.getBuffer();
        int int2 = mipMapLevel0.width;
        int int3 = mipMapLevel0.height;

        for (int int4 = 0; int4 < int3; int4++) {
            for (int int5 = 0; int5 < int2; int5++) {
                int[] ints0 = l_generateMipMaps.pixelBytes;
                int[] ints1 = l_generateMipMaps.originalPixel;
                int[] ints2 = l_generateMipMaps.resultPixelBytes;
                getPixelClamped(byteBuffer1, int0, int1, int5 * 2, int4 * 2, ints1);
                byte byte0;
                if (!this.bPreserveTransparentColor && ints1[3] <= 0) {
                    PZArrayUtil.arraySet(ints2, 0);
                    byte0 = 0;
                } else {
                    PZArrayUtil.arrayCopy(ints2, ints1, 0, 4);
                    byte0 = 1;
                }

                byte0 += this.sampleNeighborPixelDiscard(byteBuffer1, int0, int1, int5 * 2 + 1, int4 * 2, ints0, ints2);
                byte0 += this.sampleNeighborPixelDiscard(byteBuffer1, int0, int1, int5 * 2, int4 * 2 + 1, ints0, ints2);
                byte0 += this.sampleNeighborPixelDiscard(byteBuffer1, int0, int1, int5 * 2 + 1, int4 * 2 + 1, ints0, ints2);
                if (byte0 > 0) {
                    ints2[0] /= byte0;
                    ints2[1] /= byte0;
                    ints2[2] /= byte0;
                    ints2[3] /= byte0;
                    if (DebugOptions.instance.IsoSprite.WorldMipmapColors.getValue()) {
                        setMipmapDebugColors(int6, ints2);
                    }
                }

                setPixel(byteBuffer0, int2, int3, int5, int4, ints2);
            }
        }
    }

    private void scaleMipLevelAverage(MipMapLevel mipMapLevel1, MipMapLevel mipMapLevel0, int int7) {
        ImageData.L_generateMipMaps l_generateMipMaps = TL_generateMipMaps.get();
        ByteBuffer byteBuffer0 = mipMapLevel0.getBuffer();
        byteBuffer0.rewind();
        int int0 = mipMapLevel1.width;
        int int1 = mipMapLevel1.height;
        ByteBuffer byteBuffer1 = mipMapLevel1.getBuffer();
        int int2 = mipMapLevel0.width;
        int int3 = mipMapLevel0.height;

        for (int int4 = 0; int4 < int3; int4++) {
            for (int int5 = 0; int5 < int2; int5++) {
                int[] ints = l_generateMipMaps.resultPixelBytes;
                int int6 = 1;
                getPixelClamped(byteBuffer1, int0, int1, int5 * 2, int4 * 2, ints);
                int6 += getPixelDiscard(byteBuffer1, int0, int1, int5 * 2 + 1, int4 * 2, ints);
                int6 += getPixelDiscard(byteBuffer1, int0, int1, int5 * 2, int4 * 2 + 1, ints);
                int6 += getPixelDiscard(byteBuffer1, int0, int1, int5 * 2 + 1, int4 * 2 + 1, ints);
                ints[0] /= int6;
                ints[1] /= int6;
                ints[2] /= int6;
                ints[3] /= int6;
                if (ints[3] != 0 && DebugOptions.instance.IsoSprite.WorldMipmapColors.getValue()) {
                    setMipmapDebugColors(int7, ints);
                }

                setPixel(byteBuffer0, int2, int3, int5, int4, ints);
            }
        }
    }

    public static int calculateNumMips(int _widthHW, int _heightHW) {
        int int0 = calculateNumMips(_widthHW);
        int int1 = calculateNumMips(_heightHW);
        return PZMath.max(int0, int1);
    }

    private static int calculateNumMips(int int2) {
        int int0 = 0;

        for (int int1 = int2; int1 > 0; int0++) {
            int1 >>= 1;
        }

        return int0;
    }

    private void performAlphaPadding() {
        MipMapLevel mipMapLevel = this.data;
        if (mipMapLevel != null && mipMapLevel.data != null) {
            this.performAlphaPadding(mipMapLevel);
            this.alphaPaddingDone = true;
        }
    }

    private void performAlphaPadding(MipMapLevel mipMapLevel) {
        ImageData.L_performAlphaPadding l_performAlphaPadding = TL_performAlphaPadding.get();
        ByteBuffer byteBuffer = mipMapLevel.getBuffer();
        int int0 = mipMapLevel.width;
        int int1 = mipMapLevel.height;

        for (int int2 = 0; int2 < int1; int2++) {
            for (int int3 = 0; int3 < int0; int3++) {
                int int4 = (int2 * int0 + int3) * 4;
                int int5 = byteBuffer.get(int4 + 3) & 255;
                if (int5 != 255 && int5 == 0) {
                    int[] ints0 = getPixelClamped(byteBuffer, int0, int1, int3, int2, l_performAlphaPadding.pixelRGBA);
                    int[] ints1 = l_performAlphaPadding.newPixelRGBA;
                    PZArrayUtil.arraySet(ints1, 0);
                    ints1[3] = ints0[3];
                    int int6 = 0;
                    int6 += this.sampleNeighborPixelDiscard(byteBuffer, int0, int1, int3 - 1, int2, l_performAlphaPadding.pixelRGBA_neighbor, ints1);
                    int6 += this.sampleNeighborPixelDiscard(byteBuffer, int0, int1, int3, int2 - 1, l_performAlphaPadding.pixelRGBA_neighbor, ints1);
                    int6 += this.sampleNeighborPixelDiscard(byteBuffer, int0, int1, int3 - 1, int2 - 1, l_performAlphaPadding.pixelRGBA_neighbor, ints1);
                    int6 += this.sampleNeighborPixelDiscard(byteBuffer, int0, int1, int3 + 1, int2, l_performAlphaPadding.pixelRGBA_neighbor, ints1);
                    int6 += this.sampleNeighborPixelDiscard(byteBuffer, int0, int1, int3, int2 + 1, l_performAlphaPadding.pixelRGBA_neighbor, ints1);
                    int6 += this.sampleNeighborPixelDiscard(byteBuffer, int0, int1, int3 + 1, int2 + 1, l_performAlphaPadding.pixelRGBA_neighbor, ints1);
                    if (int6 > 0) {
                        ints1[0] /= int6;
                        ints1[1] /= int6;
                        ints1[2] /= int6;
                        ints1[3] = ints0[3];
                        setPixel(byteBuffer, int0, int1, int3, int2, ints1);
                    }
                }
            }
        }
    }

    private int sampleNeighborPixelDiscard(ByteBuffer byteBuffer, int int3, int int1, int int2, int int0, int[] ints0, int[] ints1) {
        if (int2 >= 0 && int2 < int3 && int0 >= 0 && int0 < int1) {
            getPixelClamped(byteBuffer, int3, int1, int2, int0, ints0);
            if (ints0[3] > 0) {
                ints1[0] += ints0[0];
                ints1[1] += ints0[1];
                ints1[2] += ints0[2];
                ints1[3] += ints0[3];
                return 1;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    public static int getPixelDiscard(ByteBuffer byteBuffer, int int3, int int1, int int2, int int0, int[] ints) {
        if (int2 >= 0 && int2 < int3 && int0 >= 0 && int0 < int1) {
            int int4 = (int2 + int0 * int3) * 4;
            ints[0] += byteBuffer.get(int4) & 255;
            ints[1] += byteBuffer.get(int4 + 1) & 255;
            ints[2] += byteBuffer.get(int4 + 2) & 255;
            ints[3] += byteBuffer.get(int4 + 3) & 255;
            return 1;
        } else {
            return 0;
        }
    }

    public static int[] getPixelClamped(ByteBuffer byteBuffer, int int1, int int3, int int0, int int2, int[] ints) {
        int0 = PZMath.clamp(int0, 0, int1 - 1);
        int2 = PZMath.clamp(int2, 0, int3 - 1);
        int int4 = (int0 + int2 * int1) * 4;
        ints[0] = byteBuffer.get(int4) & 255;
        ints[1] = byteBuffer.get(int4 + 1) & 255;
        ints[2] = byteBuffer.get(int4 + 2) & 255;
        ints[3] = byteBuffer.get(int4 + 3) & 255;
        return ints;
    }

    public static void setPixel(ByteBuffer byteBuffer, int int3, int var2, int int1, int int2, int[] ints) {
        int int0 = (int1 + int2 * int3) * 4;
        byteBuffer.put(int0, (byte)(ints[0] & 0xFF));
        byteBuffer.put(int0 + 1, (byte)(ints[1] & 0xFF));
        byteBuffer.put(int0 + 2, (byte)(ints[2] & 0xFF));
        byteBuffer.put(int0 + 3, (byte)(ints[3] & 0xFF));
    }

    public static int getNextMipDimension(int dim) {
        if (dim > 1) {
            dim >>= 1;
        }

        return dim;
    }

    private static void setMipmapDebugColors(int int0, int[] ints) {
        switch (int0) {
            case 0:
                ints[0] = 255;
                ints[1] = 0;
                ints[2] = 0;
                break;
            case 1:
                ints[0] = 0;
                ints[1] = 255;
                ints[2] = 0;
                break;
            case 2:
                ints[0] = 0;
                ints[1] = 0;
                ints[2] = 255;
                break;
            case 3:
                ints[0] = 255;
                ints[1] = 255;
                ints[2] = 0;
                break;
            case 4:
                ints[0] = 255;
                ints[1] = 0;
                ints[2] = 255;
                break;
            case 5:
                ints[0] = 0;
                ints[1] = 0;
                ints[2] = 0;
                break;
            case 6:
                ints[0] = 255;
                ints[1] = 255;
                ints[2] = 255;
                break;
            case 7:
                ints[0] = 128;
                ints[1] = 128;
                ints[2] = 128;
        }
    }

    private static final class L_generateMipMaps {
        final int[] pixelBytes = new int[4];
        final int[] originalPixel = new int[4];
        final int[] resultPixelBytes = new int[4];
    }

    static final class L_performAlphaPadding {
        final int[] pixelRGBA = new int[4];
        final int[] newPixelRGBA = new int[4];
        final int[] pixelRGBA_neighbor = new int[4];
    }
}
