// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL21;
import org.lwjgl.system.MemoryUtil;
import zombie.GameWindow;
import zombie.IndieGL;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.bucket.BucketManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.BooleanGrid;
import zombie.core.utils.ImageUtils;
import zombie.core.utils.WrappedBuffer;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.fileSystem.FileSystem;
import zombie.interfaces.IDestroyable;
import zombie.interfaces.ITexture;
import zombie.iso.Vector2;
import zombie.iso.objects.ObjectRenderEffects;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.util.StringUtils;
import zombie.util.Type;

public class Texture extends Asset implements IDestroyable, ITexture, Serializable {
    public static final HashSet<String> nullTextures = new HashSet<>();
    private static final long serialVersionUID = 7472363451408935314L;
    private static final ObjectRenderEffects objRen = ObjectRenderEffects.alloc();
    /**
     * indicates if all the texture will auto create the masks on load  The auto creation works only with the getTexture() methods
     */
    public static int BindCount = 0;
    public static boolean bDoingQuad = false;
    public static float lr;
    public static float lg;
    public static float lb;
    public static float la;
    public static int lastlastTextureID = -2;
    public static int totalTextureID = 0;
    private static Texture white = null;
    private static Texture errorTexture = null;
    private static Texture mipmap = null;
    public static int lastTextureID = -1;
    public static boolean WarnFailFindTexture = true;
    private static final HashMap<String, Texture> textures = new HashMap<>();
    private static final HashMap<String, Texture> s_sharedTextureTable = new HashMap<>();
    private static final HashMap<Long, Texture> steamAvatarMap = new HashMap<>();
    public boolean flip = false;
    public float offsetX = 0.0F;
    public float offsetY = 0.0F;
    public boolean bindAlways = false;
    /**
     * internal texture coordinates  it's used to get the max border of texture...
     */
    public float xEnd = 1.0F;
    /**
     * internal texture coordinates  it's used to get the max border of texture...
     */
    public float yEnd = 1.0F;
    /**
     * internal texture coordinates  it's used to get the max border of texture...
     */
    public float xStart = 0.0F;
    /**
     * internal texture coordinates  it's used to get the max border of texture...
     */
    public float yStart = 0.0F;
    protected TextureID dataid;
    protected Mask mask;
    protected String name;
    protected boolean solid;
    protected int width;
    protected int height;
    protected int heightOrig;
    protected int widthOrig;
    private int realWidth = 0;
    private int realHeight = 0;
    private boolean destroyed = false;
    private Texture splitIconTex;
    private int splitX = -1;
    private int splitY;
    private int splitW;
    private int splitH;
    protected FileSystem.SubTexture subTexture;
    public Texture.TextureAssetParams assetParams;
    private static final ThreadLocal<PNGSize> pngSize = ThreadLocal.withInitial(PNGSize::new);
    public static final AssetType ASSET_TYPE = new AssetType("Texture");

    public Texture(AssetPath path, AssetManager manager, Texture.TextureAssetParams params) {
        super(path, manager);
        this.assetParams = params;
        this.name = path == null ? null : path.getPath();
        if (params != null && params.subTexture != null) {
            FileSystem.SubTexture subTexturex = params.subTexture;
            this.splitX = subTexturex.m_info.x;
            this.splitY = subTexturex.m_info.y;
            this.splitW = subTexturex.m_info.w;
            this.splitH = subTexturex.m_info.h;
            this.width = this.splitW;
            this.height = this.splitH;
            this.offsetX = subTexturex.m_info.ox;
            this.offsetY = subTexturex.m_info.oy;
            this.widthOrig = subTexturex.m_info.fx;
            this.heightOrig = subTexturex.m_info.fy;
            this.name = subTexturex.m_info.name;
            this.subTexture = subTexturex;
        }

        TextureID.TextureIDAssetParams textureIDAssetParams = new TextureID.TextureIDAssetParams();
        if (this.assetParams != null && this.assetParams.subTexture != null) {
            textureIDAssetParams.subTexture = this.assetParams.subTexture;
            String string0 = textureIDAssetParams.subTexture.m_pack_name;
            String string1 = textureIDAssetParams.subTexture.m_page_name;
            FileSystem fileSystem = this.getAssetManager().getOwner().getFileSystem();
            textureIDAssetParams.flags = fileSystem.getTexturePackFlags(string0);
            textureIDAssetParams.flags = textureIDAssetParams.flags | (fileSystem.getTexturePackAlpha(string0, string1) ? 8 : 0);
            AssetPath assetPath = new AssetPath("@pack@/" + string0 + "/" + string1);
            this.dataid = (TextureID)TextureIDAssetManager.instance.load(assetPath, textureIDAssetParams);
        } else {
            if (this.assetParams == null) {
                textureIDAssetParams.flags = textureIDAssetParams.flags | (TextureID.bUseCompressionOption ? 4 : 0);
            } else {
                textureIDAssetParams.flags = this.assetParams.flags;
            }

            this.dataid = (TextureID)this.getAssetManager().getOwner().get(TextureID.ASSET_TYPE).load(this.getPath(), textureIDAssetParams);
        }

        this.onCreated(Asset.State.EMPTY);
        if (this.dataid != null) {
            this.addDependency(this.dataid);
        }
    }

    public Texture(TextureID data, String _name) {
        super(null, TextureAssetManager.instance);
        this.dataid = data;
        this.dataid.referenceCount++;
        if (data.isReady()) {
            this.solid = this.dataid.solid;
            this.width = data.width;
            this.height = data.height;
            this.xEnd = (float)this.width / data.widthHW;
            this.yEnd = (float)this.height / data.heightHW;
        } else {
            assert false;
        }

        this.name = _name;
        this.assetParams = null;
        this.onCreated(data.getState());
        this.addDependency(data);
    }

    /**
     * LOADS and crete a texture from a file
     * 
     * @param file relative path
     */
    public Texture(String file) throws Exception {
        this(new TextureID(file), file);
        this.setUseAlphaChannel(true);
    }

    public Texture(String _name, BufferedInputStream b, boolean bDoMask, Texture.PZFileformat format) {
        this(new TextureID(b, _name, bDoMask, format), _name);
        if (bDoMask && this.dataid.mask != null) {
            this.createMask(this.dataid.mask);
            this.dataid.mask = null;
            this.dataid.data = null;
        }
    }

    public Texture(String _name, BufferedInputStream b, boolean bDoMask) throws Exception {
        this(new TextureID(b, _name, bDoMask), _name);
        if (bDoMask) {
            this.createMask(this.dataid.mask);
            this.dataid.mask = null;
            this.dataid.data = null;
        }
    }

    public Texture(String file, boolean bDelete, boolean bUseAlpha) throws Exception {
        this(new TextureID(file), file);
        this.setUseAlphaChannel(bUseAlpha);
        if (bDelete) {
            this.dataid.data = null;
        }
    }

    public Texture(String _name, String palette) {
        this(new TextureID(_name, palette), _name);
        this.setUseAlphaChannel(true);
    }

    public Texture(String string, int[] ints) {
        this(new TextureID(string, ints), string);
        if (string.contains("drag")) {
            boolean boolean0 = false;
        }

        this.setUseAlphaChannel(true);
    }

    /**
     * LOADS and crete a texture from a file
     * 
     * @param file relative path
     * @param useAlphaChannel indicates if the image should use or not the alpha channel
     */
    public Texture(String file, boolean useAlphaChannel) throws Exception {
        this(new TextureID(file), file);
        this.setUseAlphaChannel(useAlphaChannel);
    }

    /**
     * create a new empty texture.
     * 
     * @param _width size of texture
     * @param _height size of texture
     * @param _name
     * @param flags
     */
    public Texture(int _width, int _height, String _name, int flags) {
        this(new TextureID(_width, _height, flags), _name);
    }

    /**
     * create a new empty texture.
     * 
     * @param _width size of texture
     * @param _height size of texture
     * @param flags
     */
    public Texture(int _width, int _height, int flags) {
        this(new TextureID(_width, _height, flags), null);
    }

    /**
     * loads and create a texture from a file and cretes as trasparent the section that have the color equal to the  RGB valued  spefied
     * 
     * @param file relative path
     * @param red red value to compare
     * @param green green value to compare
     * @param blue blue value to compare
     */
    public Texture(String file, int red, int green, int blue) throws Exception {
        this(new TextureID(file, red, green, blue), file);
    }

    /**
     * creates a copy of an existent texture
     * 
     * @param t original texture
     */
    public Texture(Texture t) {
        this(t.dataid, t.name + "(copy)");
        this.width = t.width;
        this.height = t.height;
        this.name = t.name;
        this.xStart = t.xStart;
        this.yStart = t.yStart;
        this.xEnd = t.xEnd;
        this.yEnd = t.yEnd;
        this.solid = t.solid;
    }

    /**
     * creates an emptiy texture and adds it to the game engine's texture list
     */
    public Texture() {
        super(null, TextureAssetManager.instance);
        this.assetParams = null;
        this.onCreated(Asset.State.EMPTY);
    }

    public static String processFilePath(String filePath) {
        return filePath.replaceAll("\\\\", "/");
    }

    public static void bindNone() {
        IndieGL.glDisable(3553);
        lastTextureID = -1;
        BindCount--;
    }

    public static Texture getWhite() {
        if (white == null) {
            white = new Texture(32, 32, "white", 0);
            RenderThread.invokeOnRenderContext(() -> {
                GL11.glBindTexture(3553, lastTextureID = white.getID());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                ByteBuffer byteBuffer = MemoryUtil.memAlloc(white.width * white.height * 4);

                for (int int0 = 0; int0 < white.width * white.height * 4; int0++) {
                    byteBuffer.put((byte)-1);
                }

                byteBuffer.flip();
                GL11.glTexImage2D(3553, 0, 6408, white.width, white.height, 0, 6408, 5121, byteBuffer);
                MemoryUtil.memFree(byteBuffer);
            });
            s_sharedTextureTable.put("white.png", white);
            s_sharedTextureTable.put("media/white.png", white);
            s_sharedTextureTable.put("media/ui/white.png", white);
        }

        return white;
    }

    public static Texture getErrorTexture() {
        if (errorTexture == null) {
            errorTexture = new Texture(32, 32, "EngineErrorTexture", 0);
            RenderThread.invokeOnRenderContext(() -> {
                GL11.glBindTexture(3553, lastTextureID = errorTexture.getID());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                byte byte0 = 4;
                ByteBuffer byteBuffer = MemoryUtil.memAlloc(errorTexture.width * errorTexture.height * byte0);
                byteBuffer.position(errorTexture.width * errorTexture.height * byte0);
                int int0 = errorTexture.width * byte0;
                boolean boolean0 = true;
                boolean boolean1 = boolean0;
                byte byte1 = 8;
                int int1 = errorTexture.width / byte1;

                for (int int2 = 0; int2 < byte1 * byte1; int2++) {
                    int int3 = int2 / byte1;
                    int int4 = int2 % byte1;
                    if (int3 > 0 && int4 == 0) {
                        boolean0 = !boolean0;
                        boolean1 = boolean0;
                    }

                    int int5 = boolean1 ? -16776961 : -1;
                    boolean1 = !boolean1;

                    for (int int6 = 0; int6 < int1; int6++) {
                        for (int int7 = 0; int7 < int1; int7++) {
                            byteBuffer.putInt((int3 * int1 + int6) * int0 + (int4 * int1 + int7) * byte0, int5);
                        }
                    }
                }

                byteBuffer.flip();
                GL11.glTexImage2D(3553, 0, 6408, errorTexture.width, errorTexture.height, 0, 6408, 5121, byteBuffer);
                MemoryUtil.memFree(byteBuffer);
            });
            s_sharedTextureTable.put("EngineErrorTexture.png", errorTexture);
        }

        return errorTexture;
    }

    private static void initEngineMipmapTextureLevel(int int7, int int0, int int1, int int3, int int4, int int5, int int6) {
        ByteBuffer byteBuffer = MemoryUtil.memAlloc(int0 * int1 * 4);
        MemoryUtil.memSet(byteBuffer, 255);

        for (int int2 = 0; int2 < int0 * int1; int2++) {
            byteBuffer.put((byte)(int3 & 0xFF));
            byteBuffer.put((byte)(int4 & 0xFF));
            byteBuffer.put((byte)(int5 & 0xFF));
            byteBuffer.put((byte)(int6 & 0xFF));
        }

        byteBuffer.flip();
        GL11.glTexImage2D(3553, int7, 6408, int0, int1, 0, 6408, 5121, byteBuffer);
        MemoryUtil.memFree(byteBuffer);
    }

    public static Texture getEngineMipmapTexture() {
        if (mipmap == null) {
            mipmap = new Texture(256, 256, "EngineMipmapTexture", 0);
            mipmap.dataid.setMinFilter(9984);
            RenderThread.invokeOnRenderContext(() -> {
                GL11.glBindTexture(3553, lastTextureID = mipmap.getID());
                GL11.glTexParameteri(3553, 10241, 9984);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexParameteri(3553, 33085, 6);
                initEngineMipmapTextureLevel(0, mipmap.width, mipmap.height, 255, 0, 0, 255);
                initEngineMipmapTextureLevel(1, mipmap.width / 2, mipmap.height / 2, 0, 255, 0, 255);
                initEngineMipmapTextureLevel(2, mipmap.width / 4, mipmap.height / 4, 0, 0, 255, 255);
                initEngineMipmapTextureLevel(3, mipmap.width / 8, mipmap.height / 8, 255, 255, 0, 255);
                initEngineMipmapTextureLevel(4, mipmap.width / 16, mipmap.height / 16, 255, 0, 255, 255);
                initEngineMipmapTextureLevel(5, mipmap.width / 32, mipmap.height / 32, 0, 0, 0, 255);
                initEngineMipmapTextureLevel(6, mipmap.width / 64, mipmap.height / 64, 255, 255, 255, 255);
            });
        }

        return mipmap;
    }

    public static void clearTextures() {
        textures.clear();
    }

    public static Texture getSharedTexture(String _name) {
        int int0 = 0;
        int0 |= TextureID.bUseCompression ? 4 : 0;
        return getSharedTexture(_name, int0);
    }

    public static Texture getSharedTexture(String _name, int flags) {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return null;
        } else {
            try {
                return getSharedTextureInternal(_name, flags);
            } catch (Exception exception) {
                return null;
            }
        }
    }

    public static Texture trygetTexture(String _name) {
        if (!StringUtils.containsDoubleDot(_name) && (!GameServer.bServer || ServerGUI.isCreated())) {
            Texture texture = getSharedTexture(_name);
            if (texture == null) {
                String string0 = "media/textures/" + _name;
                if (!_name.endsWith(".png")) {
                    string0 = string0 + ".png";
                }

                texture = s_sharedTextureTable.get(string0);
                if (texture != null) {
                    return texture;
                }

                String string1 = ZomboidFileSystem.instance.getString(string0);
                if (!string1.equals(string0)) {
                    int int0 = 0;
                    int0 |= TextureID.bUseCompression ? 4 : 0;
                    Texture.TextureAssetParams textureAssetParams = new Texture.TextureAssetParams();
                    textureAssetParams.flags = int0;
                    texture = (Texture)TextureAssetManager.instance.load(new AssetPath(string1), textureAssetParams);
                    BucketManager.Shared().AddTexture(string0, texture);
                    setSharedTextureInternal(string0, texture);
                }
            }

            return texture;
        } else {
            return null;
        }
    }

    private static void onTextureFileChanged(String string) {
        DebugLog.General.println("Texture.onTextureFileChanged> " + string);
    }

    public static void onTexturePacksChanged() {
        nullTextures.clear();
        s_sharedTextureTable.clear();
    }

    private static void setSharedTextureInternal(String string, Texture texture) {
        s_sharedTextureTable.put(string, texture);
    }

    private static Texture getSharedTextureInternal(String string0, int int0) {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return null;
        } else if (nullTextures.contains(string0)) {
            return null;
        } else {
            Texture texture0 = s_sharedTextureTable.get(string0);
            if (texture0 != null) {
                return texture0;
            } else {
                if (!string0.endsWith(".txt")) {
                    String string1 = string0;
                    if (string0.endsWith(".pcx") || string0.endsWith(".png")) {
                        string1 = string0.substring(0, string0.lastIndexOf("."));
                    }

                    string1 = string1.substring(string0.lastIndexOf("/") + 1);
                    Texture texture1 = TexturePackPage.getTexture(string1);
                    if (texture1 != null) {
                        setSharedTextureInternal(string0, texture1);
                        return texture1;
                    }

                    FileSystem.SubTexture subTexture0 = GameWindow.texturePackTextures.get(string1);
                    if (subTexture0 != null) {
                        Texture.TextureAssetParams textureAssetParams0 = new Texture.TextureAssetParams();
                        textureAssetParams0.subTexture = subTexture0;
                        String string2 = "@pack/" + subTexture0.m_pack_name + "/" + subTexture0.m_page_name + "/" + subTexture0.m_info.name;
                        Texture texture2 = (Texture)TextureAssetManager.instance.load(new AssetPath(string2), textureAssetParams0);
                        if (texture2 == null) {
                            nullTextures.add(string0);
                        } else {
                            setSharedTextureInternal(string0, texture2);
                        }

                        return texture2;
                    }
                }

                if (TexturePackPage.subTextureMap.containsKey(string0)) {
                    return TexturePackPage.subTextureMap.get(string0);
                } else {
                    FileSystem.SubTexture subTexture1 = GameWindow.texturePackTextures.get(string0);
                    if (subTexture1 != null) {
                        Texture.TextureAssetParams textureAssetParams1 = new Texture.TextureAssetParams();
                        textureAssetParams1.subTexture = subTexture1;
                        String string3 = "@pack/" + subTexture1.m_pack_name + "/" + subTexture1.m_page_name + "/" + subTexture1.m_info.name;
                        Texture texture3 = (Texture)TextureAssetManager.instance.load(new AssetPath(string3), textureAssetParams1);
                        if (texture3 == null) {
                            nullTextures.add(string0);
                        } else {
                            setSharedTextureInternal(string0, texture3);
                        }

                        return texture3;
                    } else if (BucketManager.Shared().HasTexture(string0)) {
                        Texture texture4 = BucketManager.Shared().getTexture(string0);
                        setSharedTextureInternal(string0, texture4);
                        return texture4;
                    } else if (StringUtils.endsWithIgnoreCase(string0, ".pcx")) {
                        nullTextures.add(string0);
                        return null;
                    } else if (string0.lastIndexOf(46) == -1) {
                        nullTextures.add(string0);
                        return null;
                    } else {
                        String string4 = ZomboidFileSystem.instance.getString(string0);
                        ZomboidFileSystem.instance.validatePrefix(string4);
                        boolean boolean0 = string4 != string0;
                        if (!boolean0 && !new File(string4).exists()) {
                            nullTextures.add(string0);
                            return null;
                        } else {
                            Texture.TextureAssetParams textureAssetParams2 = new Texture.TextureAssetParams();
                            textureAssetParams2.flags = int0;
                            Texture texture5 = (Texture)TextureAssetManager.instance.load(new AssetPath(string4), textureAssetParams2);
                            BucketManager.Shared().AddTexture(string0, texture5);
                            setSharedTextureInternal(string0, texture5);
                            return texture5;
                        }
                    }
                }
            }
        }
    }

    public static Texture getSharedTexture(String _name, String palette) {
        if (BucketManager.Shared().HasTexture(_name + palette)) {
            return BucketManager.Shared().getTexture(_name + palette);
        } else {
            Texture texture = new Texture(_name, palette);
            BucketManager.Shared().AddTexture(_name + palette, texture);
            return texture;
        }
    }

    public static Texture getSharedTexture(String string0, int[] ints, String string1) {
        if (BucketManager.Shared().HasTexture(string0 + string1)) {
            return BucketManager.Shared().getTexture(string0 + string1);
        } else {
            Texture texture = new Texture(string0, ints);
            BucketManager.Shared().AddTexture(string0 + string1, texture);
            return texture;
        }
    }

    /**
     * gets a texture from it's name; If the texture isn't already loaded this method will load it.
     * 
     * @param _name the name of texture
     * @return returns the texture from the given name
     */
    public static Texture getTexture(String _name) {
        if (!_name.contains(".txt")) {
            String string = _name.replace(".png", "");
            string = string.replace(".pcx", "");
            string = string.substring(_name.lastIndexOf("/") + 1);
            Texture texture0 = TexturePackPage.getTexture(string);
            if (texture0 != null) {
                return texture0;
            }
        }

        if (BucketManager.Active().HasTexture(_name)) {
            return BucketManager.Active().getTexture(_name);
        } else {
            try {
                Texture texture1 = new Texture(_name);
                BucketManager.Active().AddTexture(_name, texture1);
                return texture1;
            } catch (Exception exception) {
                return null;
            }
        }
    }

    public static Texture getSteamAvatar(long steamID) {
        if (steamAvatarMap.containsKey(steamID)) {
            return steamAvatarMap.get(steamID);
        } else {
            TextureID textureID = TextureID.createSteamAvatar(steamID);
            if (textureID == null) {
                return null;
            } else {
                Texture texture = new Texture(textureID, "SteamAvatar" + SteamUtils.convertSteamIDToString(steamID));
                steamAvatarMap.put(steamID, texture);
                return texture;
            }
        }
    }

    public static void steamAvatarChanged(long steamID) {
        Texture texture = steamAvatarMap.get(steamID);
        if (texture != null) {
            steamAvatarMap.remove(steamID);
        }
    }

    public static void forgetTexture(String _name) {
        BucketManager.Shared().forgetTexture(_name);
        s_sharedTextureTable.remove(_name);
    }

    public static void reload(String _name) {
        if (_name != null && !_name.isEmpty()) {
            Texture texture = s_sharedTextureTable.get(_name);
            if (texture == null) {
                texture = Type.tryCastTo(TextureAssetManager.instance.getAssetTable().get(_name), Texture.class);
                if (texture == null) {
                    return;
                }
            }

            texture.reloadFromFile(_name);
        }
    }

    public static int[] flipPixels(int[] ints1, int int0, int int1) {
        int[] ints0 = null;
        if (ints1 != null) {
            ints0 = new int[int0 * int1];

            for (int int2 = 0; int2 < int1; int2++) {
                for (int int3 = 0; int3 < int0; int3++) {
                    ints0[(int1 - int2 - 1) * int0 + int3] = ints1[int2 * int0 + int3];
                }
            }
        }

        return ints0;
    }

    public void reloadFromFile(String _name) {
        if (this.dataid != null) {
            TextureID.TextureIDAssetParams textureIDAssetParams = new TextureID.TextureIDAssetParams();
            textureIDAssetParams.flags = this.dataid.flags;
            this.dataid.getAssetManager().reload(this.dataid, textureIDAssetParams);
        } else if (_name != null && !_name.isEmpty()) {
            File file = new File(_name);
            if (file.exists()) {
                try {
                    ImageData imageData = new ImageData(file.getAbsolutePath());
                    if (imageData.getWidthHW() != this.getWidthHW() || imageData.getHeightHW() != this.getHeightHW()) {
                        return;
                    }

                    RenderThread.invokeOnRenderContext(imageData, imageDatax -> {
                        GL11.glBindTexture(3553, lastTextureID = this.dataid.id);
                        short short0 = 6408;
                        GL11.glTexImage2D(3553, 0, short0, this.getWidthHW(), this.getHeightHW(), 0, 6408, 5121, imageDatax.getData().getBuffer());
                    });
                } catch (Throwable throwable) {
                    ExceptionLogger.logException(throwable, _name);
                }
            }
        }
    }

    /**
     * Blinds the image
     */
    @Override
    public void bind() {
        this.bind(3553);
    }

    /**
     * Description copied from interface: ITexture
     * 
     * @param unit the texture unit in witch the current TextureObject will be binded
     */
    @Override
    public void bind(int unit) {
        if (!this.isDestroyed() && this.isValid() && this.isReady()) {
            if (this.bindAlways) {
                this.dataid.bindalways();
            } else {
                this.dataid.bind();
            }
        } else {
            getErrorTexture().bind(unit);
        }
    }

    public void copyMaskRegion(Texture from, int x, int y, int _width, int _height) {
        if (from.getMask() != null) {
            new Mask(from, this, x, y, _width, _height);
        }
    }

    /**
     * creates the mask of collisions
     */
    public void createMask() {
        new Mask(this);
    }

    public void createMask(boolean[] booleans) {
        new Mask(this, booleans);
    }

    public void createMask(BooleanGrid _mask) {
        new Mask(this, _mask);
    }

    public void createMask(WrappedBuffer buf) {
        new Mask(this, buf);
    }

    /**
     * destroys the image and release all resources
     */
    @Override
    public void destroy() {
        if (!this.destroyed) {
            if (this.dataid != null && --this.dataid.referenceCount == 0) {
                if (lastTextureID == this.dataid.id) {
                    lastTextureID = -1;
                }

                this.dataid.destroy();
            }

            this.destroyed = true;
        }
    }

    public boolean equals(Texture other) {
        return other.xStart == this.xStart
            && other.xEnd == this.xEnd
            && other.yStart == this.yStart
            && other.yEnd == this.yEnd
            && other.width == this.width
            && other.height == this.height
            && other.solid == this.solid
            && (
                this.dataid == null
                    || other.dataid == null
                    || other.dataid.pathFileName == null
                    || this.dataid.pathFileName == null
                    || other.dataid.pathFileName.equals(this.dataid.pathFileName)
            );
    }

    /**
     * returns the texture's pixel in a ByteBuffer
     */
    @Override
    public WrappedBuffer getData() {
        return this.dataid.getData();
    }

    /**
     * sets the texture's pixel from a ByteBuffer
     * 
     * @param data texture's pixel data
     */
    @Override
    public void setData(ByteBuffer data) {
        this.dataid.setData(data);
    }

    /**
     * Description copied from interface: ITexture
     * @return the height of image
     */
    @Override
    public int getHeight() {
        if (!this.isReady() && this.height <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }

        return this.height;
    }

    public void setHeight(int _height) {
        this.height = _height;
    }

    /**
     * Description copied from interface: ITexture
     */
    @Override
    public int getHeightHW() {
        if (!this.isReady() && this.height <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }

        return this.dataid.heightHW;
    }

    public int getHeightOrig() {
        return this.heightOrig == 0 ? this.getHeight() : this.heightOrig;
    }

    /**
     * Description copied from interface: ITexture
     * @return the ID of image in the Vram
     */
    @Override
    public int getID() {
        return this.dataid.id;
    }

    /**
     * returns the mask of collisions
     * @return mask of collisions
     */
    @Override
    public Mask getMask() {
        return this.mask;
    }

    /**
     * sets the mask of collisions
     * 
     * @param _mask the mask of collisions to set
     */
    @Override
    public void setMask(Mask _mask) {
        this.mask = _mask;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String _name) {
        if (_name != null) {
            if (_name.equals(this.name)) {
                if (!textures.containsKey(_name)) {
                    textures.put(_name, this);
                }
            } else {
                if (textures.containsKey(_name)) {
                }

                if (textures.containsKey(this.name)) {
                    textures.remove(this.name);
                }

                this.name = _name;
                textures.put(_name, this);
            }
        }
    }

    public TextureID getTextureId() {
        return this.dataid;
    }

    /**
     * indicates if the image will use the alpha channel or note
     * @return if the image will use the alpha channel or note
     */
    public boolean getUseAlphaChannel() {
        return !this.solid;
    }

    /**
     * indicates if the texture contains the alpha channel or not
     * 
     * @param value if true, the image will use the alpha channel
     */
    public void setUseAlphaChannel(boolean value) {
        this.dataid.solid = this.solid = !value;
    }

    /**
     * Description copied from interface: ITexture
     * @return the width of image
     */
    @Override
    public int getWidth() {
        if (!this.isReady() && this.width <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }

        return this.width;
    }

    public void setWidth(int _width) {
        this.width = _width;
    }

    /**
     * Description copied from interface: ITexture
     */
    @Override
    public int getWidthHW() {
        if (!this.isReady() && this.width <= 0 && !(this instanceof SmartTexture)) {
            this.syncReadSize();
        }

        return this.dataid.widthHW;
    }

    public int getWidthOrig() {
        return this.widthOrig == 0 ? this.getWidth() : this.widthOrig;
    }

    /**
     * Description copied from interface: ITexture
     * @return the end X-coordinate
     */
    @Override
    public float getXEnd() {
        return this.xEnd;
    }

    /**
     * Description copied from interface: ITexture
     * @return the start X-coordinate
     */
    @Override
    public float getXStart() {
        return this.xStart;
    }

    /**
     * Description copied from interface: ITexture
     * @return the end Y-coordinate
     */
    @Override
    public float getYEnd() {
        return this.yEnd;
    }

    /**
     * Description copied from interface: ITexture
     * @return the start Y-coordinate
     */
    @Override
    public float getYStart() {
        return this.yStart;
    }

    public float getOffsetX() {
        return this.offsetX;
    }

    public void setOffsetX(int offset) {
        this.offsetX = offset;
    }

    public float getOffsetY() {
        return this.offsetY;
    }

    public void setOffsetY(int offset) {
        this.offsetY = offset;
    }

    /**
     * indicates if the texture has a mask of collisions or not
     */
    public boolean isCollisionable() {
        return this.mask != null;
    }

    /**
     * returns if the texture is destroyed or not
     */
    @Override
    public boolean isDestroyed() {
        return this.destroyed;
    }

    /**
     * Description copied from interface: ITexture
     * @return if the texture is solid or not.
     */
    @Override
    public boolean isSolid() {
        return this.solid;
    }

    public boolean isValid() {
        return this.dataid != null;
    }

    /**
     * Description copied from interface: ITexture
     * 
     * @param red color used in the test
     * @param green color used in the test
     * @param blue color used in the test
     */
    @Override
    public void makeTransp(int red, int green, int blue) {
        this.setAlphaForeach(red, green, blue, 0);
    }

    public void render(float x, float y, float _width, float _height) {
        this.render(x, y, _width, _height, 1.0F, 1.0F, 1.0F, 1.0F, null);
    }

    public void render(float x, float y) {
        this.render(x, y, this.width, this.height, 1.0F, 1.0F, 1.0F, 1.0F, null);
    }

    public void render(float x, float y, float _width, float _height, float r, float g, float b, float a, Consumer<TextureDraw> texdModifier) {
        x += this.offsetX;
        y += this.offsetY;
        SpriteRenderer.instance.render(this, x, y, _width, _height, r, g, b, a, texdModifier);
    }

    public void render(
        ObjectRenderEffects dr, float x, float y, float _width, float _height, float r, float g, float b, float a, Consumer<TextureDraw> texdModifier
    ) {
        float float0 = this.offsetX + x;
        float float1 = this.offsetY + y;
        objRen.x1 = float0 + dr.x1 * _width;
        objRen.y1 = float1 + dr.y1 * _height;
        objRen.x2 = float0 + _width + dr.x2 * _width;
        objRen.y2 = float1 + dr.y2 * _height;
        objRen.x3 = float0 + _width + dr.x3 * _width;
        objRen.y3 = float1 + _height + dr.y3 * _height;
        objRen.x4 = float0 + dr.x4 * _width;
        objRen.y4 = float1 + _height + dr.y4 * _height;
        SpriteRenderer.instance.render(this, objRen.x1, objRen.y1, objRen.x2, objRen.y2, objRen.x3, objRen.y3, objRen.x4, objRen.y4, r, g, b, a, texdModifier);
    }

    public void rendershader2(
        float x, float y, float _width, float _height, int texx, int texy, int texWidth, int texHeight, float r, float g, float b, float a
    ) {
        if (a != 0.0F) {
            float float0 = (float)texx / this.getWidthHW();
            float float1 = (float)texy / this.getHeightHW();
            float float2 = (float)(texx + texWidth) / this.getWidthHW();
            float float3 = (float)(texy + texHeight) / this.getHeightHW();
            if (this.flip) {
                float float4 = float2;
                float2 = float0;
                float0 = float4;
                x += this.widthOrig - this.offsetX - this.width;
                y += this.offsetY;
            } else {
                x += this.offsetX;
                y += this.offsetY;
            }

            if (r > 1.0F) {
                r = 1.0F;
            }

            if (g > 1.0F) {
                g = 1.0F;
            }

            if (b > 1.0F) {
                b = 1.0F;
            }

            if (a > 1.0F) {
                a = 1.0F;
            }

            if (r < 0.0F) {
                r = 0.0F;
            }

            if (g < 0.0F) {
                g = 0.0F;
            }

            if (b < 0.0F) {
                b = 0.0F;
            }

            if (a < 0.0F) {
                a = 0.0F;
            }

            if (!(x + _width <= 0.0F)) {
                if (!(y + _height <= 0.0F)) {
                    if (!(x >= Core.getInstance().getScreenWidth())) {
                        if (!(y >= Core.getInstance().getScreenHeight())) {
                            lr = r;
                            lg = g;
                            lb = b;
                            la = a;
                            SpriteRenderer.instance
                                .render(this, x, y, _width, _height, r, g, b, a, float0, float3, float2, float3, float2, float1, float0, float1);
                        }
                    }
                }
            }
        }
    }

    public void renderdiamond(float x, float y, float _width, float _height, int l, int u, int r, int d) {
        SpriteRenderer.instance.render(null, x, y, x + _width / 2.0F, y - _height / 2.0F, x + _width, y, x + _width / 2.0F, y + _height / 2.0F, l, u, r, d);
    }

    public void renderwallnw(float x, float y, float _width, float _height, int u, int d, int u2, int d2, int r, int r2) {
        lr = -1.0F;
        lg = -1.0F;
        lb = -1.0F;
        la = -1.0F;
        if (this.flip) {
            x += this.widthOrig - this.offsetX - this.width;
            y += this.offsetY;
        } else {
            x += this.offsetX;
            y += this.offsetY;
        }

        int int0 = Core.TileScale;
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            d2 = -65536;
            d = -65536;
            u2 = -65536;
            u = -65536;
        }

        float float0 = x - _width / 2.0F - 0.0F;
        float float1 = y - 96 * int0 + _height / 2.0F - 1.0F - 0.0F;
        float float2 = x + 0.0F;
        float float3 = y - 96 * int0 - 2.0F - 0.0F;
        float float4 = x + 0.0F;
        float float5 = y + 4.0F + 0.0F;
        float float6 = x - _width / 2.0F - 0.0F;
        float float7 = y + _height / 2.0F + 4.0F + 0.0F;
        SpriteRenderer.instance.render(this, float0, float1, float2, float3, float4, float5, float6, float7, d2, u2, u, d);
        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            r2 = -256;
            r = -256;
            u2 = -256;
            u = -256;
        }

        float0 = x - 0.0F;
        float1 = y - 96 * int0 - 0.0F;
        float2 = x + _width / 2.0F + 0.0F;
        float3 = y - 96 * int0 + _height / 2.0F - 0.0F;
        float4 = x + _width / 2.0F + 0.0F;
        float5 = y + _height / 2.0F + 5.0F + 0.0F;
        float6 = x - 0.0F;
        float7 = y + 5.0F + 0.0F;
        SpriteRenderer.instance.render(this, float0, float1, float2, float3, float4, float5, float6, float7, u2, r2, r, u);
    }

    public void renderwallw(float x, float y, float _width, float _height, int u, int d, int u2, int d2) {
        lr = -1.0F;
        lg = -1.0F;
        lb = -1.0F;
        la = -1.0F;
        if (this.flip) {
            x += this.widthOrig - this.offsetX - this.width;
            y += this.offsetY;
        } else {
            x += this.offsetX;
            y += this.offsetY;
        }

        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            d = -16711936;
            u = -16711936;
            d2 = -16728064;
            u2 = -16728064;
        }

        int int0 = Core.TileScale;
        float float0 = x - _width / 2.0F - 0.0F;
        float float1 = y - 96 * int0 + _height / 2.0F - 1.0F - 0.0F;
        float float2 = x + int0 + 0.0F;
        float float3 = y - 96 * int0 - 3.0F - 0.0F;
        float float4 = x + int0 + 0.0F;
        float float5 = y + 3.0F + 0.0F;
        float float6 = x - _width / 2.0F - 0.0F;
        float float7 = y + _height / 2.0F + 4.0F + 0.0F;
        SpriteRenderer.instance.render(this, float0, float1, float2, float3, float4, float5, float6, float7, d2, u2, u, d);
    }

    public void renderwalln(float x, float y, float _width, float _height, int u, int d, int u2, int d2) {
        lr = -1.0F;
        lg = -1.0F;
        lb = -1.0F;
        la = -1.0F;
        if (this.flip) {
            x += this.widthOrig - this.offsetX - this.width;
            y += this.offsetY;
        } else {
            x += this.offsetX;
            y += this.offsetY;
        }

        if (DebugOptions.instance.Terrain.RenderTiles.IsoGridSquare.Walls.LightingOldDebug.getValue()) {
            d = -16776961;
            u = -16776961;
            d2 = -16777024;
            u2 = -16777024;
        }

        int int0 = Core.TileScale;
        float float0 = x - 6.0F - 0.0F;
        float float1 = y - 96 * int0 - 3.0F - 0.0F;
        float float2 = x + _width / 2.0F + 0.0F;
        float float3 = y - 96 * int0 + _height / 2.0F - 0.0F;
        float float4 = x + _width / 2.0F + 0.0F;
        float float5 = y + _height / 2.0F + 5.0F + 0.0F;
        float float6 = x - 6.0F - 0.0F;
        float float7 = y + 2.0F + 0.0F;
        SpriteRenderer.instance.render(this, float0, float1, float2, float3, float4, float5, float6, float7, u2, d2, d, u);
    }

    public void renderstrip(int x, int y, int _width, int _height, float r, float g, float b, float a, Consumer<TextureDraw> texdModifier) {
        try {
            if (a <= 0.0F) {
                return;
            }

            if (r > 1.0F) {
                r = 1.0F;
            }

            if (g > 1.0F) {
                g = 1.0F;
            }

            if (b > 1.0F) {
                b = 1.0F;
            }

            if (a > 1.0F) {
                a = 1.0F;
            }

            if (r < 0.0F) {
                r = 0.0F;
            }

            if (g < 0.0F) {
                g = 0.0F;
            }

            if (b < 0.0F) {
                b = 0.0F;
            }

            if (a < 0.0F) {
                a = 0.0F;
            }

            float float0 = this.getXStart();
            float float1 = this.getYStart();
            float float2 = this.getXEnd();
            float float3 = this.getYEnd();
            if (this.flip) {
                x = (int)(x + (this.widthOrig - this.offsetX - this.width));
                y = (int)(y + this.offsetY);
            } else {
                x = (int)(x + this.offsetX);
                y = (int)(y + this.offsetY);
            }

            SpriteRenderer.instance.renderi(this, x, y, _width, _height, r, g, b, a, texdModifier);
        } catch (Exception exception) {
            bDoingQuad = false;
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
        }
    }

    /**
     * Description copied from interface: ITexture
     * 
     * @param red color used in the test
     * @param green color used in the test
     * @param blue color used in the test
     * @param alpha the alpha color that will be setted to the pixel that pass the test
     */
    @Override
    public void setAlphaForeach(int red, int green, int blue, int alpha) {
        ImageData imageData = this.getTextureId().getImageData();
        if (imageData != null) {
            imageData.makeTransp((byte)red, (byte)green, (byte)blue, (byte)alpha);
        } else {
            WrappedBuffer wrappedBuffer = this.getData();
            this.setData(ImageUtils.makeTransp(wrappedBuffer.getBuffer(), red, green, blue, alpha, this.getWidthHW(), this.getHeightHW()));
            wrappedBuffer.dispose();
        }

        AlphaColorIndex alphaColorIndex = new AlphaColorIndex(red, green, blue, alpha);
        if (this.dataid.alphaList == null) {
            this.dataid.alphaList = new ArrayList<>();
        }

        if (!this.dataid.alphaList.contains(alphaColorIndex)) {
            this.dataid.alphaList.add(alphaColorIndex);
        }
    }

    public void setCustomizedTexture() {
        this.dataid.pathFileName = null;
    }

    public void setNameOnly(String _name) {
        this.name = _name;
    }

    /**
     * Description copied from interface: ITexture
     * 
     * @param x xstart position
     * @param y ystart position
     * @param _width width of the region
     * @param _height height of the region
     */
    @Override
    public void setRegion(int x, int y, int _width, int _height) {
        if (x >= 0 && x <= this.getWidthHW()) {
            if (y >= 0 && y <= this.getHeightHW()) {
                if (_width > 0) {
                    if (_height > 0) {
                        if (_width + x > this.getWidthHW()) {
                            _width = this.getWidthHW() - x;
                        }

                        if (_height > this.getHeightHW()) {
                            _height = this.getHeightHW() - y;
                        }

                        this.xStart = (float)x / this.getWidthHW();
                        this.yStart = (float)y / this.getHeightHW();
                        this.xEnd = (float)(x + _width) / this.getWidthHW();
                        this.yEnd = (float)(y + _height) / this.getHeightHW();
                        this.width = _width;
                        this.height = _height;
                    }
                }
            }
        }
    }

    public Texture splitIcon() {
        if (this.splitIconTex == null) {
            if (!this.dataid.isReady()) {
                this.splitIconTex = new Texture();
                this.splitIconTex.name = this.name + "_Icon";
                this.splitIconTex.dataid = this.dataid;
                this.splitIconTex.dataid.referenceCount++;
                this.splitIconTex.splitX = this.splitX;
                this.splitIconTex.splitY = this.splitY;
                this.splitIconTex.splitW = this.splitW;
                this.splitIconTex.splitH = this.splitH;
                this.splitIconTex.width = this.width;
                this.splitIconTex.height = this.height;
                this.splitIconTex.offsetX = 0.0F;
                this.splitIconTex.offsetY = 0.0F;
                this.splitIconTex.widthOrig = 0;
                this.splitIconTex.heightOrig = 0;
                this.splitIconTex.addDependency(this.dataid);
                setSharedTextureInternal(this.splitIconTex.name, this.splitIconTex);
                return this.splitIconTex;
            }

            this.splitIconTex = new Texture(this.getTextureId(), this.name + "_Icon");
            float float0 = this.xStart * this.getWidthHW();
            float float1 = this.yStart * this.getHeightHW();
            float float2 = this.xEnd * this.getWidthHW() - float0;
            float float3 = this.yEnd * this.getHeightHW() - float1;
            this.splitIconTex.setRegion((int)float0, (int)float1, (int)float2, (int)float3);
            this.splitIconTex.offsetX = 0.0F;
            this.splitIconTex.offsetY = 0.0F;
            setSharedTextureInternal(this.name + "_Icon", this.splitIconTex);
        }

        return this.splitIconTex;
    }

    public Texture split(int xOffset, int yOffset, int _width, int _height) {
        Texture texture = new Texture(this.getTextureId(), this.name + "_" + xOffset + "_" + yOffset);
        this.splitX = xOffset;
        this.splitY = yOffset;
        this.splitW = _width;
        this.splitH = _height;
        if (this.getTextureId().isReady()) {
            texture.setRegion(xOffset, yOffset, _width, _height);
        } else {
            assert false;
        }

        return texture;
    }

    public Texture split(String _name, int xOffset, int yOffset, int _width, int _height) {
        Texture texture = new Texture(this.getTextureId(), _name);
        texture.setRegion(xOffset, yOffset, _width, _height);
        return texture;
    }

    public Texture[] split(int xOffset, int yOffset, int row, int coloumn, int _width, int _height, int spaceX, int spaceY) {
        Texture[] texturesx = new Texture[row * coloumn];

        for (int int0 = 0; int0 < row; int0++) {
            for (int int1 = 0; int1 < coloumn; int1++) {
                texturesx[int1 + int0 * coloumn] = new Texture(this.getTextureId(), this.name + "_" + row + "_" + coloumn);
                texturesx[int1 + int0 * coloumn].setRegion(xOffset + int1 * _width + spaceX * int1, yOffset + int0 * _height + spaceY * int0, _width, _height);
                texturesx[int1 + int0 * coloumn]
                    .copyMaskRegion(this, xOffset + int1 * _width + spaceX * int1, yOffset + int0 * _height + spaceY * int0, _width, _height);
            }
        }

        return texturesx;
    }

    public Texture[][] split2D(int[] ints1, int[] ints0) {
        if (ints1 != null && ints0 != null) {
            Texture[][] texturesx = new Texture[ints1.length][ints0.length];
            float float0 = 0.0F;
            float float1 = 0.0F;
            float float2 = 0.0F;

            for (int int0 = 0; int0 < ints0.length; int0++) {
                float1 += float0;
                float0 = (float)ints0[int0] / this.getHeightHW();
                float2 = 0.0F;

                for (int int1 = 0; int1 < ints1.length; int1++) {
                    float float3 = (float)ints1[int1] / this.getWidthHW();
                    Texture texture1 = texturesx[int1][int0] = new Texture(this);
                    texture1.width = ints1[int1];
                    texture1.height = ints0[int0];
                    texture1.xStart = float2;
                    texture1.xEnd = float2 += float3;
                    texture1.yStart = float1;
                    texture1.yEnd = float1 + float0;
                }
            }

            return texturesx;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ name:\"" + this.name + "\", w:" + this.getWidth() + ", h:" + this.getHeight() + " }";
    }

    public void saveMask(String _name) {
        this.mask.save(_name);
    }

    public void saveToZomboidDirectory(String filename) {
        if (!StringUtils.containsDoubleDot(filename)) {
            String string = ZomboidFileSystem.instance.getCacheDirSub(filename);
            RenderThread.invokeOnRenderContext(() -> this.saveOnRenderThread(string));
        }
    }

    public void saveToCurrentSavefileDirectory(String filename) {
        if (!StringUtils.containsDoubleDot(filename)) {
            String string = ZomboidFileSystem.instance.getFileNameInCurrentSave(filename);
            RenderThread.invokeOnRenderContext(() -> this.saveOnRenderThread(string));
        }
    }

    public void saveOnRenderThread(String filename) {
        if (this.getID() == -1) {
            throw new IllegalStateException("texture hasn't been uploaded to the GPU");
        } else {
            GL11.glPixelStorei(3333, 1);
            GL13.glActiveTexture(33984);
            GL11.glEnable(3553);
            this.bind();
            int int0 = this.getWidth();
            int int1 = this.getHeight();
            int int2 = this.getWidthHW();
            int int3 = this.getHeightHW();
            byte byte0 = 4;
            ByteBuffer byteBuffer = MemoryUtil.memAlloc(int2 * int3 * byte0);
            GL21.glGetTexImage(3553, 0, 6408, 5121, byteBuffer);
            int[] ints = new int[int0 * int1];
            int int4 = (int)PZMath.floor(this.getXStart() * int2);
            int int5 = (int)PZMath.floor(this.getYStart() * int3);

            for (int int6 = 0; int6 < ints.length; int6++) {
                int int7 = int4 + int6 % int0;
                int int8 = int5 + int6 / int0;
                int int9 = (int7 + int8 * int2) * byte0;
                ints[int6] = (byteBuffer.get(int9 + 3) & 255) << 24
                    | (byteBuffer.get(int9) & 255) << 16
                    | (byteBuffer.get(int9 + 1) & 255) << 8
                    | (byteBuffer.get(int9 + 2) & 255) << 0;
            }

            MemoryUtil.memFree(byteBuffer);
            BufferedImage bufferedImage = new BufferedImage(int0, int1, 2);
            bufferedImage.setRGB(0, 0, int0, int1, ints, 0, int0);

            try {
                ZomboidFileSystem.instance.validatePrefix(filename);
                File file = new File(filename);
                file.getParentFile().mkdirs();
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException iOException) {
                ExceptionLogger.logException(iOException);
            }

            SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        }
    }

    public void loadMaskRegion(ByteBuffer cache) {
        if (cache != null) {
            this.mask = new Mask();
            this.mask.mask = new BooleanGrid(this.width, this.height);
            this.mask.mask.LoadFromByteBuffer(cache);
        }
    }

    public void saveMaskRegion(ByteBuffer cache) {
        if (cache != null) {
            this.mask.mask.PutToByteBuffer(cache);
        }
    }

    public int getRealWidth() {
        return this.realWidth;
    }

    public void setRealWidth(int _realWidth) {
        this.realWidth = _realWidth;
    }

    public int getRealHeight() {
        return this.realHeight;
    }

    public void setRealHeight(int _realHeight) {
        this.realHeight = _realHeight;
    }

    public Vector2 getUVScale(Vector2 uvScale) {
        uvScale.set(1.0F, 1.0F);
        if (this.dataid == null) {
            return uvScale;
        } else {
            if (this.dataid.heightHW != this.dataid.height || this.dataid.widthHW != this.dataid.width) {
                uvScale.x = (float)this.dataid.width / this.dataid.widthHW;
                uvScale.y = (float)this.dataid.height / this.dataid.heightHW;
            }

            return uvScale;
        }
    }

    private void syncReadSize() {
        PNGSize pNGSize = pngSize.get();
        ZomboidFileSystem.instance.validatePrefix(this.name);
        pNGSize.readSize(this.name);
        this.width = pNGSize.width;
        this.height = pNGSize.height;
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }

    @Override
    public void onBeforeReady() {
        if (this.assetParams != null) {
            this.assetParams.subTexture = null;
            this.assetParams = null;
        }

        this.solid = this.dataid.solid;
        if (this.splitX == -1) {
            this.width = this.dataid.width;
            this.height = this.dataid.height;
            this.xEnd = (float)this.width / this.dataid.widthHW;
            this.yEnd = (float)this.height / this.dataid.heightHW;
            if (this.dataid.mask != null) {
                this.createMask(this.dataid.mask);
            }
        } else {
            this.setRegion(this.splitX, this.splitY, this.splitW, this.splitH);
            if (this.dataid.mask != null) {
                this.mask = new Mask(this.dataid.mask, this.dataid.width, this.dataid.height, this.splitX, this.splitY, this.splitW, this.splitH);
            }
        }
    }

    public static void collectAllIcons(HashMap<String, String> map, HashMap<String, String> mapFull) {
        for (Entry entry : s_sharedTextureTable.entrySet()) {
            if (((String)entry.getKey()).startsWith("media/ui/Container_") || ((String)entry.getKey()).startsWith("Item_")) {
                String string0 = "";
                if (((String)entry.getKey()).startsWith("Item_")) {
                    string0 = ((String)entry.getKey()).replaceFirst("Item_", "");
                } else if (((String)entry.getKey()).startsWith("media/ui/Container_")) {
                    String string1 = ((String)entry.getKey()).replaceFirst("media/ui/Container_", "");
                    string0 = string1.replaceAll("\\.png", "");
                    DebugLog.log("Adding " + string0.toLowerCase() + ", value = " + (String)entry.getKey());
                }

                map.put(string0.toLowerCase(), string0);
                mapFull.put(string0.toLowerCase(), (String)entry.getKey());
            }
        }
    }

    public static enum PZFileformat {
        PNG,
        DDS;
    }

    public static final class TextureAssetParams extends AssetManager.AssetParams {
        int flags = 0;
        FileSystem.SubTexture subTexture;
    }
}
