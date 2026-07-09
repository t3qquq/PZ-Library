// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;
import zombie.IndieGL;
import zombie.SystemDisabler;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.DirectBufferAllocator;
import zombie.core.utils.WrappedBuffer;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.fileSystem.FileSystem;
import zombie.interfaces.IDestroyable;

public final class TextureID extends Asset implements IDestroyable, Serializable {
    private static final long serialVersionUID = 4409253583065563738L;
    public static long totalGraphicMemory = 0L;
    public static boolean UseFiltering = false;
    public static boolean bUseCompression = true;
    public static boolean bUseCompressionOption = true;
    public static float totalMemUsed = 0.0F;
    private static boolean FREE_MEMORY = true;
    private static final HashMap<Integer, String> TextureIDMap = new HashMap<>();
    protected String pathFileName;
    protected boolean solid;
    protected int width;
    protected int widthHW;
    protected int height;
    protected int heightHW;
    protected transient ImageData data;
    protected transient int id = -1;
    private int m_glMagFilter = -1;
    private int m_glMinFilter = -1;
    ArrayList<AlphaColorIndex> alphaList;
    int referenceCount = 0;
    BooleanGrid mask;
    protected int flags = 0;
    public TextureID.TextureIDAssetParams assetParams;
    public static final IntBuffer deleteTextureIDS = BufferUtils.createIntBuffer(20);
    public static final AssetType ASSET_TYPE = new AssetType("TextureID");

    public TextureID(AssetPath path, AssetManager manager, TextureID.TextureIDAssetParams params) {
        super(path, manager);
        this.assetParams = params;
        this.flags = params == null ? 0 : this.assetParams.flags;
    }

    protected TextureID() {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        this.onCreated(Asset.State.READY);
    }

    public TextureID(int _width, int _height, int _flags) {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = new TextureID.TextureIDAssetParams();
        this.assetParams.flags = _flags;
        if ((_flags & 16) != 0) {
            if ((_flags & 4) != 0) {
                DebugLog.General.warn("FBO incompatible with COMPRESS");
                this.assetParams.flags &= -5;
            }

            this.data = new ImageData(_width, _height, null);
        } else {
            this.data = new ImageData(_width, _height);
        }

        this.width = this.data.getWidth();
        this.height = this.data.getHeight();
        this.widthHW = this.data.getWidthHW();
        this.heightHW = this.data.getHeightHW();
        this.solid = this.data.isSolid();
        RenderThread.queueInvokeOnRenderContext(() -> this.createTexture(false));
        this.onCreated(Asset.State.READY);
    }

    public TextureID(ImageData image) {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        this.data = image;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(Asset.State.READY);
    }

    public TextureID(String pcx, String palette) {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        this.data = new ImageData(pcx, palette);
        this.pathFileName = pcx;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(Asset.State.READY);
    }

    public TextureID(String string, int[] ints) {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        this.data = new ImageData(string, ints);
        this.pathFileName = string;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(Asset.State.READY);
    }

    public TextureID(String path, int red, int green, int blue) throws Exception {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        int int0;
        while ((int0 = path.indexOf("\\")) != -1) {
            path = path.substring(0, int0) + "/" + path.substring(int0 + 1);
        }

        (this.data = new ImageData(path)).makeTransp((byte)red, (byte)green, (byte)blue);
        if (this.alphaList == null) {
            this.alphaList = new ArrayList<>();
        }

        this.alphaList.add(new AlphaColorIndex(red, green, blue, 0));
        this.pathFileName = path;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(Asset.State.READY);
    }

    public TextureID(String path) throws Exception {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        if (path.toLowerCase().contains(".pcx")) {
            this.data = new ImageData(path, path);
        } else {
            this.data = new ImageData(path);
        }

        if (this.data.getHeight() != -1) {
            this.pathFileName = path;
            RenderThread.invokeOnRenderContext(this::createTexture);
            this.onCreated(Asset.State.READY);
        }
    }

    public TextureID(BufferedInputStream b, String path, boolean bDoMask, Texture.PZFileformat format) {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        this.data = new ImageData(b, bDoMask, format);
        if (this.data.id != -1) {
            this.id = this.data.id;
            this.width = this.data.getWidth();
            this.height = this.data.getHeight();
            this.widthHW = this.data.getWidthHW();
            this.heightHW = this.data.getHeightHW();
            this.solid = this.data.isSolid();
        } else {
            if (bDoMask) {
                this.mask = this.data.mask;
                this.data.mask = null;
            }

            this.createTexture();
        }

        this.pathFileName = path;
        this.onCreated(Asset.State.READY);
    }

    public TextureID(BufferedInputStream b, String path, boolean bDoMask) throws Exception {
        super(null, TextureIDAssetManager.instance);
        this.assetParams = null;
        this.data = new ImageData(b, bDoMask);
        if (bDoMask) {
            this.mask = this.data.mask;
            this.data.mask = null;
        }

        this.pathFileName = path;
        RenderThread.invokeOnRenderContext(this::createTexture);
        this.onCreated(Asset.State.READY);
    }

    public static TextureID createSteamAvatar(long steamID) {
        ImageData imageData = ImageData.createSteamAvatar(steamID);
        return imageData == null ? null : new TextureID(imageData);
    }

    public int getID() {
        return this.id;
    }

    /**
     * binds the current texture
     */
    public boolean bind() {
        if (this.id == -1 && this.data == null) {
            Texture.getErrorTexture().bind();
            return true;
        } else {
            this.debugBoundTexture();
            return this.id != -1 && this.id == Texture.lastTextureID ? false : this.bindalways();
        }
    }

    public boolean bindalways() {
        this.bindInternal();
        return true;
    }

    private void bindInternal() {
        if (this.id == -1) {
            this.generateHwId(this.data != null && this.data.data != null);
        }

        this.assignFilteringFlags();
        Texture.lastlastTextureID = Texture.lastTextureID;
        Texture.lastTextureID = this.id;
        Texture.BindCount++;
    }

    private void debugBoundTexture() {
        if (DebugOptions.instance.Checks.BoundTextures.getValue() && Texture.lastTextureID != -1) {
            int int0 = GL11.glGetInteger(34016);
            if (int0 == 33984) {
                int int1 = GL11.glGetInteger(32873);
                if (int1 != Texture.lastTextureID) {
                    String string = null;

                    for (Asset asset : TextureIDAssetManager.instance.getAssetTable().values()) {
                        TextureID textureID = (TextureID)asset;
                        if (textureID.id == Texture.lastTextureID) {
                            string = textureID.getPath().getPath();
                            break;
                        }
                    }

                    DebugLog.General.error("Texture.lastTextureID %d != GL_TEXTURE_BINDING_2D %d name=%s", Texture.lastTextureID, int1, string);
                }
            }
        }
    }

    /**
     * Description copied from interface: IDestroyable
     */
    @Override
    public void destroy() {
        assert Thread.currentThread() == RenderThread.RenderThread;

        if (this.id != -1) {
            if (deleteTextureIDS.position() == deleteTextureIDS.capacity()) {
                deleteTextureIDS.flip();
                GL11.glDeleteTextures(deleteTextureIDS);
                deleteTextureIDS.clear();
            }

            deleteTextureIDS.put(this.id);
            this.id = -1;
        }
    }

    /**
     * free memory space
     */
    public void freeMemory() {
        this.data = null;
    }

    public WrappedBuffer getData() {
        this.bind();
        WrappedBuffer wrappedBuffer = DirectBufferAllocator.allocate(this.heightHW * this.widthHW * 4);
        GL11.glGetTexImage(3553, 0, 6408, 5121, wrappedBuffer.getBuffer());
        Texture.lastTextureID = 0;
        GL11.glBindTexture(3553, 0);
        return wrappedBuffer;
    }

    /**
     * if the data is null will be free the memory from the RAM but not from the VRAM
     */
    public void setData(ByteBuffer bdata) {
        if (bdata == null) {
            this.freeMemory();
        } else {
            this.bind();
            GL11.glTexSubImage2D(3553, 0, 0, 0, this.widthHW, this.heightHW, 6408, 5121, bdata);
            if (this.data != null) {
                MipMapLevel mipMapLevel = this.data.getData();
                ByteBuffer byteBuffer = mipMapLevel.getBuffer();
                bdata.flip();
                byteBuffer.clear();
                byteBuffer.put(bdata);
                byteBuffer.flip();
            }
        }
    }

    public ImageData getImageData() {
        return this.data;
    }

    public void setImageData(ImageData _data) {
        _data = this.limitMaxSize(_data);
        this.data = _data;
        this.width = _data.getWidth();
        this.height = _data.getHeight();
        this.widthHW = _data.getWidthHW();
        this.heightHW = _data.getHeightHW();
        if (_data.mask != null) {
            this.mask = _data.mask;
            _data.mask = null;
        }

        RenderThread.queueInvokeOnRenderContext(this::createTexture);
    }

    private ImageData limitMaxSize(ImageData imageData0) {
        if (this.assetParams == null) {
            return imageData0;
        } else {
            int int0 = this.assetParams.flags;
            short short0 = 384;
            if ((int0 & short0) == 0) {
                return imageData0;
            } else {
                int int1 = Core.getInstance().getMaxTextureSizeFromFlags(int0);
                if (imageData0.getWidth() <= int1 && imageData0.getHeight() <= int1) {
                    return imageData0;
                } else {
                    imageData0.bPreserveTransparentColor = true;
                    MipMapLevel mipMapLevel = imageData0.getData();
                    int int2 = 0;

                    while (mipMapLevel.width > int1 || mipMapLevel.height > int1) {
                        mipMapLevel = imageData0.getMipMapData(++int2);
                    }

                    WrappedBuffer wrappedBuffer = DirectBufferAllocator.allocate(mipMapLevel.getBuffer().capacity());
                    mipMapLevel.getBuffer().rewind();
                    wrappedBuffer.getBuffer().put(mipMapLevel.getBuffer());
                    wrappedBuffer.getBuffer().rewind();
                    ImageData imageData1 = new ImageData(imageData0.getWidth() >> int2, imageData0.getHeight() >> int2, wrappedBuffer);
                    imageData1.alphaPaddingDone = true;
                    if (FREE_MEMORY) {
                        imageData0.dispose();
                    }

                    return imageData1;
                }
            }
        }
    }

    public String getPathFileName() {
        return this.pathFileName;
    }

    /**
     * Description copied from interface: IDestroyable
     */
    @Override
    public boolean isDestroyed() {
        return this.id == -1;
    }

    public boolean isSolid() {
        return this.solid;
    }

    private void createTexture() {
        if (this.data != null) {
            this.createTexture(true);
        }
    }

    private void createTexture(boolean boolean0) {
        if (this.id == -1) {
            this.width = this.data.getWidth();
            this.height = this.data.getHeight();
            this.widthHW = this.data.getWidthHW();
            this.heightHW = this.data.getHeightHW();
            this.solid = this.data.isSolid();
            this.generateHwId(boolean0);
        }
    }

    private void generateHwId(boolean boolean4) {
        this.id = GL11.glGenTextures();
        Texture.totalTextureID++;
        GL11.glBindTexture(3553, Texture.lastTextureID = this.id);
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        int int0;
        if (this.assetParams == null) {
            int0 = bUseCompressionOption ? 4 : 0;
        } else {
            int0 = this.assetParams.flags;
        }

        boolean boolean0 = (int0 & 1) != 0;
        boolean boolean1 = (int0 & 2) != 0;
        boolean boolean2 = (int0 & 16) != 0;
        boolean boolean3 = (int0 & 64) != 0 && !boolean2 && boolean4;
        boolean boolean5 = (int0 & 4) != 0;
        char char0;
        if (boolean5 && GL.getCapabilities().GL_ARB_texture_compression) {
            char0 = '\u84ee';
        } else {
            char0 = 6408;
        }

        this.m_glMagFilter = boolean1 ? 9728 : 9729;
        this.m_glMinFilter = boolean3 ? 9987 : (boolean0 ? 9728 : 9729);
        GL11.glTexParameteri(3553, 10241, this.m_glMinFilter);
        GL11.glTexParameteri(3553, 10240, this.m_glMagFilter);
        if ((int0 & 32) != 0) {
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }

        if (boolean4) {
            if (boolean3) {
                PZGLUtil.checkGLErrorThrow("TextureID.mipMaps.start");
                int int1 = this.data.getMipMapCount();
                int int2 = PZMath.min(0, int1 - 1);
                int int3 = int1;

                for (int int4 = int2; int4 < int3; int4++) {
                    MipMapLevel mipMapLevel = this.data.getMipMapData(int4);
                    int int5 = mipMapLevel.width;
                    int int6 = mipMapLevel.height;
                    totalMemUsed = totalMemUsed + mipMapLevel.getDataSize();
                    GL11.glTexImage2D(3553, int4 - int2, char0, int5, int6, 0, 6408, 5121, mipMapLevel.getBuffer());
                    PZGLUtil.checkGLErrorThrow("TextureID.mipMaps[%d].end", int4);
                }

                PZGLUtil.checkGLErrorThrow("TextureID.mipMaps.end");
            } else {
                PZGLUtil.checkGLErrorThrow("TextureID.noMips.start");
                totalMemUsed = totalMemUsed + this.widthHW * this.heightHW * 4;
                GL11.glTexImage2D(3553, 0, char0, this.widthHW, this.heightHW, 0, 6408, 5121, this.data.getData().getBuffer());
                PZGLUtil.checkGLErrorThrow("TextureID.noMips.end");
            }
        } else {
            GL11.glTexImage2D(3553, 0, char0, this.widthHW, this.heightHW, 0, 6408, 5121, (ByteBuffer)null);
            totalMemUsed = totalMemUsed + this.widthHW * this.heightHW * 4;
        }

        if (FREE_MEMORY) {
            if (this.data != null) {
                this.data.dispose();
            }

            this.data = null;
            if (this.assetParams != null) {
                this.assetParams.subTexture = null;
                this.assetParams = null;
            }
        }

        TextureIDMap.put(this.id, this.pathFileName);
        if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
            PZGLUtil.checkGLErrorThrow("generateHwId id:%d pathFileName:%s", this.id, this.pathFileName);
        }
    }

    private void assignFilteringFlags() {
        GL11.glBindTexture(3553, this.id);
        if (this.width == 1 && this.height == 1) {
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        } else {
            GL11.glTexParameteri(3553, 10241, this.m_glMinFilter);
            GL11.glTexParameteri(3553, 10240, this.m_glMagFilter);
            if ((this.flags & 64) != 0
                && DebugOptions.instance.IsoSprite.NearestMagFilterAtMinZoom.getValue()
                && this.isMinZoomLevel()
                && this.m_glMagFilter != 9728) {
                GL11.glTexParameteri(3553, 10240, 9728);
            }

            if (DebugOptions.instance.IsoSprite.ForceLinearMagFilter.getValue() && this.m_glMagFilter != 9729) {
                GL11.glTexParameteri(3553, 10240, 9729);
            }

            if (DebugOptions.instance.IsoSprite.ForceNearestMagFilter.getValue() && this.m_glMagFilter != 9728) {
                GL11.glTexParameteri(3553, 10240, 9728);
            }

            if (DebugOptions.instance.IsoSprite.ForceNearestMipMapping.getValue() && this.m_glMinFilter == 9987) {
                GL11.glTexParameteri(3553, 10241, 9986);
            }

            if (DebugOptions.instance.IsoSprite.TextureWrapClampToEdge.getValue()) {
                GL11.glTexParameteri(3553, 10242, 33071);
                GL11.glTexParameteri(3553, 10243, 33071);
            }

            if (DebugOptions.instance.IsoSprite.TextureWrapRepeat.getValue()) {
                GL11.glTexParameteri(3553, 10242, 10497);
                GL11.glTexParameteri(3553, 10243, 10497);
            }

            if (SystemDisabler.doEnableDetectOpenGLErrorsInTexture) {
                PZGLUtil.checkGLErrorThrow("assignFilteringFlags id:%d pathFileName:%s", this.id, this.pathFileName);
            }
        }
    }

    public void setMagFilter(int filter) {
        this.m_glMagFilter = filter;
    }

    public void setMinFilter(int filter) {
        this.m_glMinFilter = filter;
    }

    public boolean hasMipMaps() {
        return this.m_glMinFilter == 9987;
    }

    private boolean isMaxZoomLevel() {
        return IndieGL.isMaxZoomLevel();
    }

    private boolean isMinZoomLevel() {
        return IndieGL.isMinZoomLevel();
    }

    @Override
    public void setAssetParams(AssetManager.AssetParams params) {
        this.assetParams = (TextureID.TextureIDAssetParams)params;
        this.flags = this.assetParams == null ? 0 : this.assetParams.flags;
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }

    public static final class TextureIDAssetParams extends AssetManager.AssetParams {
        FileSystem.SubTexture subTexture;
        int flags = 0;
    }
}
