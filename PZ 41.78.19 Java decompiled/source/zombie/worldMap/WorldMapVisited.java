// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.characters.IsoPlayer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.ShaderProgram;
import zombie.core.textures.TextureID;
import zombie.core.utils.ImageUtils;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.Vector2;
import zombie.worldMap.styles.WorldMapStyleLayer;

public class WorldMapVisited {
    private static WorldMapVisited instance;
    private int m_minX;
    private int m_minY;
    private int m_maxX;
    private int m_maxY;
    byte[] m_visited;
    boolean m_changed = false;
    int m_changeX1 = 0;
    int m_changeY1 = 0;
    int m_changeX2 = 0;
    int m_changeY2 = 0;
    private final int[] m_updateMinX = new int[4];
    private final int[] m_updateMinY = new int[4];
    private final int[] m_updateMaxX = new int[4];
    private final int[] m_updateMaxY = new int[4];
    private static final int TEXTURE_BPP = 4;
    private TextureID m_textureID;
    private int m_textureW = 0;
    private int m_textureH = 0;
    private ByteBuffer m_textureBuffer;
    private boolean m_textureChanged = false;
    private final WorldMapStyleLayer.RGBAf m_color = new WorldMapStyleLayer.RGBAf().init(0.85882354F, 0.84313726F, 0.7529412F, 1.0F);
    private final WorldMapStyleLayer.RGBAf m_gridColor = new WorldMapStyleLayer.RGBAf()
        .init(this.m_color.r * 0.85F, this.m_color.g * 0.85F, this.m_color.b * 0.85F, 1.0F);
    private boolean m_mainMenu = false;
    private static ShaderProgram m_shaderProgram;
    private static ShaderProgram m_gridShaderProgram;
    static final int UNITS_PER_CELL = 10;
    static final int SQUARES_PER_CELL = 300;
    static final int SQUARES_PER_UNIT = 30;
    static final int TEXTURE_PAD = 1;
    static final int BIT_VISITED = 1;
    static final int BIT_KNOWN = 2;
    Vector2 m_vector2 = new Vector2();

    public WorldMapVisited() {
        Arrays.fill(this.m_updateMinX, -1);
        Arrays.fill(this.m_updateMinY, -1);
        Arrays.fill(this.m_updateMaxX, -1);
        Arrays.fill(this.m_updateMaxY, -1);
    }

    public void setBounds(int minX, int minY, int maxX, int maxY) {
        if (minX > maxX || minY > maxY) {
            maxY = 0;
            minY = 0;
            maxX = 0;
            minX = 0;
            this.m_mainMenu = true;
        }

        this.m_minX = minX;
        this.m_minY = minY;
        this.m_maxX = maxX;
        this.m_maxY = maxY;
        this.m_changed = true;
        this.m_changeX1 = 0;
        this.m_changeY1 = 0;
        this.m_changeX2 = this.getWidthInCells() * 10 - 1;
        this.m_changeY2 = this.getHeightInCells() * 10 - 1;
        this.m_visited = new byte[this.getWidthInCells() * 10 * this.getHeightInCells() * 10];
        this.m_textureW = this.calcTextureWidth();
        this.m_textureH = this.calcTextureHeight();
        this.m_textureBuffer = BufferUtils.createByteBuffer(this.m_textureW * this.m_textureH * 4);
        this.m_textureBuffer.limit(this.m_textureBuffer.capacity());
        int int0 = SandboxOptions.getInstance().Map.MapAllKnown.getValue() ? 0 : -1;
        byte byte0 = -1;
        byte byte1 = -1;
        byte byte2 = -1;

        for (byte byte3 = 0; byte3 < this.m_textureBuffer.limit(); byte3 += 4) {
            this.m_textureBuffer.put(byte3, (byte)int0);
            this.m_textureBuffer.put(byte3 + 1, byte0);
            this.m_textureBuffer.put(byte3 + 2, byte1);
            this.m_textureBuffer.put(byte3 + 3, byte2);
        }

        this.m_textureID = new TextureID(this.m_textureW, this.m_textureH, 0);
    }

    public int getMinX() {
        return this.m_minX;
    }

    public int getMinY() {
        return this.m_minY;
    }

    private int getWidthInCells() {
        return this.m_maxX - this.m_minX + 1;
    }

    private int getHeightInCells() {
        return this.m_maxY - this.m_minY + 1;
    }

    private int calcTextureWidth() {
        return ImageUtils.getNextPowerOfTwo(this.getWidthInCells() * 10 + 2);
    }

    private int calcTextureHeight() {
        return ImageUtils.getNextPowerOfTwo(this.getHeightInCells() * 10 + 2);
    }

    public void setKnownInCells(int minX, int minY, int maxX, int maxY) {
        this.setFlags(minX * 300, minY * 300, (maxX + 1) * 300, (maxY + 1) * 300, 2);
    }

    public void clearKnownInCells(int minX, int minY, int maxX, int maxY) {
        this.clearFlags(minX * 300, minY * 300, (maxX + 1) * 300, (maxY + 1) * 300, 2);
    }

    public void setVisitedInCells(int minX, int minY, int maxX, int maxY) {
        this.setFlags(minX * 300, minY * 300, maxX * 300, maxY * 300, 1);
    }

    public void clearVisitedInCells(int minX, int minY, int maxX, int maxY) {
        this.clearFlags(minX * 300, minY * 300, maxX * 300, maxY * 300, 1);
    }

    public void setKnownInSquares(int minX, int minY, int maxX, int maxY) {
        this.setFlags(minX, minY, maxX, maxY, 2);
    }

    public void clearKnownInSquares(int minX, int minY, int maxX, int maxY) {
        this.clearFlags(minX, minY, maxX, maxY, 2);
    }

    public void setVisitedInSquares(int minX, int minY, int maxX, int maxY) {
        this.setFlags(minX, minY, maxX, maxY, 1);
    }

    public void clearVisitedInSquares(int minX, int minY, int maxX, int maxY) {
        this.clearFlags(minX, minY, maxX, maxY, 1);
    }

    private void updateVisitedTexture() {
        this.m_textureID.bind();
        GL11.glTexImage2D(3553, 0, 6408, this.m_textureW, this.m_textureH, 0, 6408, 5121, this.m_textureBuffer);
    }

    public void renderMain() {
        this.m_textureChanged = this.m_textureChanged | this.updateTextureData(this.m_textureBuffer, this.m_textureW);
    }

    private void initShader() {
        m_shaderProgram = ShaderProgram.createShaderProgram("worldMapVisited", false, true);
        if (m_shaderProgram.isCompiled()) {
        }
    }

    public void render(float renderX, float renderY, int minX, int minY, int maxX, int maxY, float worldScale, boolean blur) {
        if (!this.m_mainMenu) {
            GL13.glActiveTexture(33984);
            GL13.glClientActiveTexture(33984);
            GL11.glEnable(3553);
            if (this.m_textureChanged) {
                this.m_textureChanged = false;
                this.updateVisitedTexture();
            }

            this.m_textureID.bind();
            int int0 = blur ? 9729 : 9728;
            GL11.glTexParameteri(3553, 10241, int0);
            GL11.glTexParameteri(3553, 10240, int0);
            GL11.glEnable(3042);
            GL11.glTexEnvi(8960, 8704, 8448);
            GL11.glColor4f(this.m_color.r, this.m_color.g, this.m_color.b, this.m_color.a);
            if (m_shaderProgram == null) {
                this.initShader();
            }

            if (m_shaderProgram.isCompiled()) {
                m_shaderProgram.Start();
                float float0 = (float)(1 + (minX - this.m_minX) * 10) / this.m_textureW;
                float float1 = (float)(1 + (minY - this.m_minY) * 10) / this.m_textureH;
                float float2 = (float)(1 + (maxX + 1 - this.m_minX) * 10) / this.m_textureW;
                float float3 = (float)(1 + (maxY + 1 - this.m_minY) * 10) / this.m_textureH;
                float float4 = (minX - this.m_minX) * 300 * worldScale;
                float float5 = (minY - this.m_minY) * 300 * worldScale;
                float float6 = (maxX + 1 - this.m_minX) * 300 * worldScale;
                float float7 = (maxY + 1 - this.m_minY) * 300 * worldScale;
                GL11.glBegin(7);
                GL11.glTexCoord2f(float0, float1);
                GL11.glVertex2f(renderX + float4, renderY + float5);
                GL11.glTexCoord2f(float2, float1);
                GL11.glVertex2f(renderX + float6, renderY + float5);
                GL11.glTexCoord2f(float2, float3);
                GL11.glVertex2f(renderX + float6, renderY + float7);
                GL11.glTexCoord2f(float0, float3);
                GL11.glVertex2f(renderX + float4, renderY + float7);
                GL11.glEnd();
                m_shaderProgram.End();
            }
        }
    }

    public void renderGrid(float renderX, float renderY, int minX, int minY, int maxX, int maxY, float worldScale, float zoomF) {
        if (!(zoomF < 11.0F)) {
            if (m_gridShaderProgram == null) {
                m_gridShaderProgram = ShaderProgram.createShaderProgram("worldMapGrid", false, true);
            }

            if (m_gridShaderProgram.isCompiled()) {
                m_gridShaderProgram.Start();
                float float0 = renderX + (minX * 300 - this.m_minX * 300) * worldScale;
                float float1 = renderY + (minY * 300 - this.m_minY * 300) * worldScale;
                float float2 = float0 + (maxX - minX + 1) * 300 * worldScale;
                float float3 = float1 + (maxY - minY + 1) * 300 * worldScale;
                VBOLinesUV vBOLinesUV = WorldMapRenderer.m_vboLinesUV;
                vBOLinesUV.setMode(1);
                vBOLinesUV.setLineWidth(0.5F);
                vBOLinesUV.startRun(this.m_textureID);
                float float4 = this.m_gridColor.r;
                float float5 = this.m_gridColor.g;
                float float6 = this.m_gridColor.b;
                float float7 = this.m_gridColor.a;
                byte byte0 = 1;
                if (zoomF < 13.0F) {
                    byte0 = 8;
                } else if (zoomF < 14.0F) {
                    byte0 = 4;
                } else if (zoomF < 15.0F) {
                    byte0 = 2;
                }

                m_gridShaderProgram.setValue("UVOffset", this.m_vector2.set(0.5F / this.m_textureW, 0.0F));

                for (int int0 = minX * 10; int0 <= (maxX + 1) * 10; int0 += byte0) {
                    vBOLinesUV.reserve(2);
                    vBOLinesUV.addElement(
                        renderX + (int0 * 30 - this.m_minX * 300) * worldScale,
                        float1,
                        0.0F,
                        (float)(1 + int0 - this.m_minX * 10) / this.m_textureW,
                        1.0F / this.m_textureH,
                        float4,
                        float5,
                        float6,
                        float7
                    );
                    vBOLinesUV.addElement(
                        renderX + (int0 * 30 - this.m_minX * 300) * worldScale,
                        float3,
                        0.0F,
                        (float)(1 + int0 - this.m_minX * 10) / this.m_textureW,
                        (float)(1 + this.getHeightInCells() * 10) / this.m_textureH,
                        float4,
                        float5,
                        float6,
                        float7
                    );
                }

                m_gridShaderProgram.setValue("UVOffset", this.m_vector2.set(0.0F, 0.5F / this.m_textureH));

                for (int int1 = minY * 10; int1 <= (maxY + 1) * 10; int1 += byte0) {
                    vBOLinesUV.reserve(2);
                    vBOLinesUV.addElement(
                        float0,
                        renderY + (int1 * 30 - this.m_minY * 300) * worldScale,
                        0.0F,
                        1.0F / this.m_textureW,
                        (float)(1 + int1 - this.m_minY * 10) / this.m_textureH,
                        float4,
                        float5,
                        float6,
                        float7
                    );
                    vBOLinesUV.addElement(
                        float2,
                        renderY + (int1 * 30 - this.m_minY * 300) * worldScale,
                        0.0F,
                        (float)(1 + this.getWidthInCells() * 10) / this.m_textureW,
                        (float)(1 + int1 - this.m_minY * 10) / this.m_textureH,
                        float4,
                        float5,
                        float6,
                        float7
                    );
                }

                vBOLinesUV.flush();
                m_gridShaderProgram.End();
            }
        }
    }

    private void destroy() {
        if (this.m_textureID != null) {
            RenderThread.invokeOnRenderContext(this.m_textureID::destroy);
        }

        this.m_textureBuffer = null;
        this.m_visited = null;
    }

    public void save(ByteBuffer output) {
        output.putInt(this.m_minX);
        output.putInt(this.m_minY);
        output.putInt(this.m_maxX);
        output.putInt(this.m_maxY);
        output.putInt(10);
        output.put(this.m_visited);
    }

    public void load(ByteBuffer input, int WorldVersion) {
        int int0 = input.getInt();
        int int1 = input.getInt();
        int int2 = input.getInt();
        int int3 = input.getInt();
        int int4 = input.getInt();
        if (int0 == this.m_minX && int1 == this.m_minY && int2 == this.m_maxX && int3 == this.m_maxY && int4 == 10) {
            input.get(this.m_visited);
        } else {
            byte[] bytes = new byte[(int2 - int0 + 1) * int4 * (int3 - int1 + 1) * int4];
            input.get(bytes);
            int int5 = 300 / int4;
            int int6 = (int2 - int0 + 1) * int4;

            for (int int7 = int1 * int4; int7 <= int3 * int4; int7++) {
                int int8 = int7 - int1 * int4;

                for (int int9 = int0 * int4; int9 <= int2 * int4; int9++) {
                    int int10 = int9 - int0 * int4;
                    this.setFlags(int9 * int5, int7 * int5, int9 * int5 + int5 - 1, int7 * int5 + int5 - 1, bytes[int10 + int8 * int6]);
                }
            }
        }
    }

    public void save() throws IOException {
        ByteBuffer byteBuffer = SliceY.SliceBuffer;
        byteBuffer.clear();
        byteBuffer.putInt(195);
        this.save(byteBuffer);
        File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("map_visited.bin"));

        try (
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        ) {
            bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
        }
    }

    public void load() throws IOException {
        File file = new File(ZomboidFileSystem.instance.getFileNameInCurrentSave("map_visited.bin"));

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        ) {
            ByteBuffer byteBuffer = SliceY.SliceBuffer;
            byteBuffer.clear();
            int int0 = bufferedInputStream.read(byteBuffer.array());
            byteBuffer.limit(int0);
            int int1 = byteBuffer.getInt();
            this.load(byteBuffer, int1);
        } catch (FileNotFoundException fileNotFoundException) {
        }
    }

    private void setFlags(int int0, int int1, int int2, int int3, int int13) {
        int0 -= this.m_minX * 300;
        int1 -= this.m_minY * 300;
        int2 -= this.m_minX * 300;
        int3 -= this.m_minY * 300;
        int int4 = this.getWidthInCells();
        int int5 = this.getHeightInCells();
        int0 = PZMath.clamp(int0, 0, int4 * 300 - 1);
        int1 = PZMath.clamp(int1, 0, int5 * 300 - 1);
        int2 = PZMath.clamp(int2, 0, int4 * 300 - 1);
        int3 = PZMath.clamp(int3, 0, int5 * 300 - 1);
        if (int0 != int2 && int1 != int3) {
            int int6 = int0 / 30;
            int int7 = int2 / 30;
            int int8 = int1 / 30;
            int int9 = int3 / 30;
            if (int2 % 30 == 0) {
                int7--;
            }

            if (int3 % 30 == 0) {
                int9--;
            }

            boolean boolean0 = false;
            int int10 = int4 * 10;

            for (int int11 = int8; int11 <= int9; int11++) {
                for (int int12 = int6; int12 <= int7; int12++) {
                    byte byte0 = this.m_visited[int12 + int11 * int10];
                    if ((byte0 & int13) != int13) {
                        this.m_visited[int12 + int11 * int10] = (byte)(byte0 | int13);
                        boolean0 = true;
                    }
                }
            }

            if (boolean0) {
                this.m_changed = true;
                this.m_changeX1 = PZMath.min(this.m_changeX1, int6);
                this.m_changeY1 = PZMath.min(this.m_changeY1, int8);
                this.m_changeX2 = PZMath.max(this.m_changeX2, int7);
                this.m_changeY2 = PZMath.max(this.m_changeY2, int9);
            }
        }
    }

    private void clearFlags(int int0, int int1, int int2, int int3, int int13) {
        int0 -= this.m_minX * 300;
        int1 -= this.m_minY * 300;
        int2 -= this.m_minX * 300;
        int3 -= this.m_minY * 300;
        int int4 = this.getWidthInCells();
        int int5 = this.getHeightInCells();
        int0 = PZMath.clamp(int0, 0, int4 * 300 - 1);
        int1 = PZMath.clamp(int1, 0, int5 * 300 - 1);
        int2 = PZMath.clamp(int2, 0, int4 * 300 - 1);
        int3 = PZMath.clamp(int3, 0, int5 * 300 - 1);
        if (int0 != int2 && int1 != int3) {
            int int6 = int0 / 30;
            int int7 = int2 / 30;
            int int8 = int1 / 30;
            int int9 = int3 / 30;
            if (int2 % 30 == 0) {
                int7--;
            }

            if (int3 % 30 == 0) {
                int9--;
            }

            boolean boolean0 = false;
            int int10 = int4 * 10;

            for (int int11 = int8; int11 <= int9; int11++) {
                for (int int12 = int6; int12 <= int7; int12++) {
                    byte byte0 = this.m_visited[int12 + int11 * int10];
                    if ((byte0 & int13) != 0) {
                        this.m_visited[int12 + int11 * int10] = (byte)(byte0 & ~int13);
                        boolean0 = true;
                    }
                }
            }

            if (boolean0) {
                this.m_changed = true;
                this.m_changeX1 = PZMath.min(this.m_changeX1, int6);
                this.m_changeY1 = PZMath.min(this.m_changeY1, int8);
                this.m_changeX2 = PZMath.max(this.m_changeX2, int7);
                this.m_changeY2 = PZMath.max(this.m_changeY2, int9);
            }
        }
    }

    private boolean updateTextureData(ByteBuffer byteBuffer, int int2) {
        if (!this.m_changed) {
            return false;
        } else {
            this.m_changed = false;
            byte byte0 = 4;
            int int0 = this.getWidthInCells() * 10;

            for (int int1 = this.m_changeY1; int1 <= this.m_changeY2; int1++) {
                byteBuffer.position((1 + this.m_changeX1) * byte0 + (1 + int1) * int2 * byte0);

                for (int int3 = this.m_changeX1; int3 <= this.m_changeX2; int3++) {
                    byte byte1 = this.m_visited[int3 + int1 * int0];
                    byteBuffer.put((byte)((byte1 & 2) != 0 ? 0 : -1));
                    byteBuffer.put((byte)((byte1 & 1) != 0 ? 0 : -1));
                    byteBuffer.put((byte)-1);
                    byteBuffer.put((byte)-1);
                }
            }

            byteBuffer.position(0);
            this.m_changeX1 = Integer.MAX_VALUE;
            this.m_changeY1 = Integer.MAX_VALUE;
            this.m_changeX2 = Integer.MIN_VALUE;
            this.m_changeY2 = Integer.MIN_VALUE;
            return true;
        }
    }

    void setUnvisitedRGBA(float float0, float float1, float float2, float float3) {
        this.m_color.init(float0, float1, float2, float3);
    }

    void setUnvisitedGridRGBA(float float0, float float1, float float2, float float3) {
        this.m_gridColor.init(float0, float1, float2, float3);
    }

    boolean hasFlags(int int0, int int1, int int2, int int3, int int13, boolean boolean0) {
        int0 -= this.m_minX * 300;
        int1 -= this.m_minY * 300;
        int2 -= this.m_minX * 300;
        int3 -= this.m_minY * 300;
        int int4 = this.getWidthInCells();
        int int5 = this.getHeightInCells();
        int0 = PZMath.clamp(int0, 0, int4 * 300 - 1);
        int1 = PZMath.clamp(int1, 0, int5 * 300 - 1);
        int2 = PZMath.clamp(int2, 0, int4 * 300 - 1);
        int3 = PZMath.clamp(int3, 0, int5 * 300 - 1);
        if (int0 != int2 && int1 != int3) {
            int int6 = int0 / 30;
            int int7 = int2 / 30;
            int int8 = int1 / 30;
            int int9 = int3 / 30;
            if (int2 % 30 == 0) {
                int7--;
            }

            if (int3 % 30 == 0) {
                int9--;
            }

            int int10 = int4 * 10;

            for (int int11 = int8; int11 <= int9; int11++) {
                for (int int12 = int6; int12 <= int7; int12++) {
                    byte byte0 = this.m_visited[int12 + int11 * int10];
                    if (boolean0) {
                        if ((byte0 & int13) != 0) {
                            return true;
                        }
                    } else if ((byte0 & int13) != int13) {
                        return false;
                    }
                }
            }

            return !boolean0;
        } else {
            return false;
        }
    }

    boolean isCellVisible(int int1, int int0) {
        return this.hasFlags(int1 * 300, int0 * 300, (int1 + 1) * 300, (int0 + 1) * 300, 3, true);
    }

    public static WorldMapVisited getInstance() {
        IsoMetaGrid metaGrid = IsoWorld.instance.getMetaGrid();
        if (metaGrid == null) {
            throw new NullPointerException("IsoWorld.instance.MetaGrid is null");
        } else {
            if (instance == null) {
                instance = new WorldMapVisited();
                instance.setBounds(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());

                try {
                    instance.load();
                    if (SandboxOptions.getInstance().Map.MapAllKnown.getValue()) {
                        instance.setKnownInCells(metaGrid.getMinX(), metaGrid.getMinY(), metaGrid.getMaxX(), metaGrid.getMaxY());
                    }
                } catch (Throwable throwable) {
                    ExceptionLogger.logException(throwable);
                }
            }

            return instance;
        }
    }

    public static void update() {
        if (IsoWorld.instance != null) {
            WorldMapVisited worldMapVisited = getInstance();
            if (worldMapVisited != null) {
                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null && !player.isDead()) {
                        byte byte0 = 25;
                        int int1 = ((int)player.x - byte0) / 30;
                        int int2 = ((int)player.y - byte0) / 30;
                        int int3 = ((int)player.x + byte0) / 30;
                        int int4 = ((int)player.y + byte0) / 30;
                        if (((int)player.x + byte0) % 30 == 0) {
                            int3--;
                        }

                        if (((int)player.y + byte0) % 30 == 0) {
                            int4--;
                        }

                        if (int1 != worldMapVisited.m_updateMinX[int0]
                            || int2 != worldMapVisited.m_updateMinY[int0]
                            || int3 != worldMapVisited.m_updateMaxX[int0]
                            || int4 != worldMapVisited.m_updateMaxY[int0]) {
                            worldMapVisited.m_updateMinX[int0] = int1;
                            worldMapVisited.m_updateMinY[int0] = int2;
                            worldMapVisited.m_updateMaxX[int0] = int3;
                            worldMapVisited.m_updateMaxY[int0] = int4;
                            worldMapVisited.setFlags((int)player.x - byte0, (int)player.y - byte0, (int)player.x + byte0, (int)player.y + byte0, 3);
                        }
                    }
                }
            }
        }
    }

    public void forget() {
        this.clearKnownInCells(this.m_minX, this.m_minY, this.m_maxX, this.m_maxY);
        this.clearVisitedInCells(this.m_minX, this.m_minY, this.m_maxX, this.m_maxY);
        Arrays.fill(this.m_updateMinX, -1);
        Arrays.fill(this.m_updateMinY, -1);
        Arrays.fill(this.m_updateMaxX, -1);
        Arrays.fill(this.m_updateMaxY, -1);
    }

    public static void SaveAll() {
        WorldMapVisited worldMapVisited = instance;
        if (worldMapVisited != null) {
            try {
                worldMapVisited.save();
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }
    }

    public static void Reset() {
        WorldMapVisited worldMapVisited = instance;
        if (worldMapVisited != null) {
            worldMapVisited.destroy();
            instance = null;
        }
    }
}
