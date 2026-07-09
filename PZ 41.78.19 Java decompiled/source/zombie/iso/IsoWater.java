// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import zombie.GameTime;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.interfaces.ITexture;
import zombie.iso.weather.ClimateManager;

public final class IsoWater {
    public Shader Effect;
    private float WaterTime;
    private float WaterWindAngle;
    private float WaterWindIntensity;
    private float WaterRainIntensity;
    private Vector2f WaterParamWindINT;
    private Texture texBottom;
    private int apiId;
    private static IsoWater instance;
    private static boolean isShaderEnable = false;
    private final IsoWater.RenderData[][] renderData = new IsoWater.RenderData[3][4];
    private final IsoWater.RenderData[][] renderDataShore = new IsoWater.RenderData[3][4];
    static final int BYTES_PER_FLOAT = 4;
    static final int FLOATS_PER_VERTEX = 7;
    static final int BYTES_PER_VERTEX = 28;
    static final int VERTICES_PER_SQUARE = 4;
    private final Vector4f shaderOffset = new Vector4f();

    public static synchronized IsoWater getInstance() {
        if (instance == null) {
            instance = new IsoWater();
        }

        return instance;
    }

    public boolean getShaderEnable() {
        return isShaderEnable;
    }

    public IsoWater() {
        this.texBottom = Texture.getSharedTexture("media/textures/river_bottom.png");
        RenderThread.invokeOnRenderContext(() -> {
            if (GL.getCapabilities().OpenGL30) {
                this.apiId = 1;
            }

            if (GL.getCapabilities().GL_ARB_framebuffer_object) {
                this.apiId = 2;
            }

            if (GL.getCapabilities().GL_EXT_framebuffer_object) {
                this.apiId = 3;
            }
        });

        for (int int0 = 0; int0 < this.renderData.length; int0++) {
            for (int int1 = 0; int1 < 4; int1++) {
                this.renderData[int0][int1] = new IsoWater.RenderData();
                this.renderDataShore[int0][int1] = new IsoWater.RenderData();
            }
        }

        this.applyWaterQuality();
        this.WaterParamWindINT = new Vector2f(0.0F);
    }

    public void applyWaterQuality() {
        if (PerformanceSettings.WaterQuality == 2) {
            isShaderEnable = false;
        }

        if (PerformanceSettings.WaterQuality == 1) {
            isShaderEnable = true;
            RenderThread.invokeOnRenderContext(() -> {
                ARBShaderObjects.glUseProgramObjectARB(0);
                this.Effect = new WaterShader("water");
                ARBShaderObjects.glUseProgramObjectARB(0);
            });
        }

        if (PerformanceSettings.WaterQuality == 0) {
            isShaderEnable = true;
            RenderThread.invokeOnRenderContext(() -> {
                this.Effect = new WaterShader("water_hq");
                this.Effect.Start();
                this.Effect.End();
            });
        }
    }

    public void render(ArrayList<IsoGridSquare> arrayList, boolean boolean0) {
        if (this.getShaderEnable()) {
            int int0 = IsoCamera.frameState.playerIndex;
            int int1 = SpriteRenderer.instance.getMainStateIndex();
            IsoWater.RenderData renderData0 = this.renderData[int1][int0];
            IsoWater.RenderData renderData1 = this.renderDataShore[int1][int0];
            if (boolean0) {
                if (renderData1.numSquares > 0) {
                    SpriteRenderer.instance.drawWater(this.Effect, int0, this.apiId, true);
                }
            } else {
                renderData0.clear();
                renderData1.clear();

                for (int int2 = 0; int2 < arrayList.size(); int2++) {
                    IsoGridSquare square = (IsoGridSquare)arrayList.get(int2);
                    if (square.chunk == null || !square.chunk.bLightingNeverDone[int0]) {
                        IsoWaterGeometry waterGeometry = square.getWater();
                        if (waterGeometry != null) {
                            if (waterGeometry.bShore) {
                                renderData1.addSquare(waterGeometry);
                            } else if (waterGeometry.hasWater) {
                                renderData0.addSquare(waterGeometry);
                            }
                        }
                    }
                }

                if (renderData0.numSquares != 0) {
                    SpriteRenderer.instance.drawWater(this.Effect, int0, this.apiId, false);
                }
            }
        }
    }

    public void waterProjection() {
        int int0 = SpriteRenderer.instance.getRenderingPlayerIndex();
        PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int0);
        GL11.glOrtho(
            playerCamera.getOffX(),
            playerCamera.getOffX() + playerCamera.OffscreenWidth,
            playerCamera.getOffY() + playerCamera.OffscreenHeight,
            playerCamera.getOffY(),
            -1.0,
            1.0
        );
    }

    public void waterGeometry(boolean boolean0) {
        long long0 = System.nanoTime();
        int int0 = SpriteRenderer.instance.getRenderStateIndex();
        int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
        IsoWater.RenderData renderDatax = boolean0 ? this.renderDataShore[int0][int1] : this.renderData[int0][int1];
        int int2 = 0;
        int int3 = renderDatax.numSquares;

        while (int3 > 0) {
            int int4 = this.renderSome(int2, int3, boolean0);
            int2 += int4;
            int3 -= int4;
        }

        long long1 = System.nanoTime();
        SpriteRenderer.ringBuffer.restoreVBOs = true;
    }

    private int renderSome(int int4, int int3, boolean boolean0) {
        IsoPuddles.VBOs.next();
        FloatBuffer floatBuffer = IsoPuddles.VBOs.vertices;
        ShortBuffer shortBuffer = IsoPuddles.VBOs.indices;
        GL13.glActiveTexture(33985);
        GL13.glClientActiveTexture(33985);
        GL11.glTexCoordPointer(2, 5126, 28, 8L);
        GL11.glEnableClientState(32888);
        GL13.glActiveTexture(33984);
        GL13.glClientActiveTexture(33984);
        GL11.glTexCoordPointer(2, 5126, 28, 0L);
        GL11.glColorPointer(4, 5121, 28, 24L);
        GL11.glVertexPointer(2, 5126, 28, 16L);
        int int0 = SpriteRenderer.instance.getRenderStateIndex();
        int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
        IsoWater.RenderData renderDatax = boolean0 ? this.renderDataShore[int0][int1] : this.renderData[int0][int1];
        int int2 = Math.min(int3 * 4, IsoPuddles.VBOs.bufferSizeVertices);
        floatBuffer.put(renderDatax.data, int4 * 4 * 7, int2 * 7);
        byte byte0 = 0;
        byte byte1 = 0;

        for (int int5 = 0; int5 < int2 / 4; int5++) {
            shortBuffer.put(byte0);
            shortBuffer.put((short)(byte0 + 1));
            shortBuffer.put((short)(byte0 + 2));
            shortBuffer.put(byte0);
            shortBuffer.put((short)(byte0 + 2));
            shortBuffer.put((short)(byte0 + 3));
            byte0 += 4;
            byte1 += 6;
        }

        IsoPuddles.VBOs.unmap();
        byte byte2 = 0;
        byte byte3 = 0;
        GL12.glDrawRangeElements(4, byte2, byte2 + byte0, byte1 - byte3, 5123, byte3 * 2);
        return int2 / 4;
    }

    public ITexture getTextureBottom() {
        return this.texBottom;
    }

    public float getShaderTime() {
        return this.WaterTime;
    }

    public float getRainIntensity() {
        return this.WaterRainIntensity;
    }

    public void update(ClimateManager climateManager) {
        this.WaterWindAngle = climateManager.getCorrectedWindAngleIntensity();
        this.WaterWindIntensity = climateManager.getWindIntensity() * 5.0F;
        this.WaterRainIntensity = climateManager.getRainIntensity();
        float float0 = GameTime.getInstance().getMultiplier();
        this.WaterTime += 0.0166F * float0;
        this.WaterParamWindINT
            .add(
                (float)Math.sin(this.WaterWindAngle * 6.0F) * this.WaterWindIntensity * 0.05F * (float0 / 1.6F),
                (float)Math.cos(this.WaterWindAngle * 6.0F) * this.WaterWindIntensity * 0.15F * (float0 / 1.6F)
            );
    }

    public float getWaterWindX() {
        return this.WaterParamWindINT.x;
    }

    public float getWaterWindY() {
        return this.WaterParamWindINT.y;
    }

    public float getWaterWindSpeed() {
        return this.WaterWindIntensity * 2.0F;
    }

    public Vector4f getShaderOffset() {
        int int0 = SpriteRenderer.instance.getRenderingPlayerIndex();
        PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(int0);
        return this.shaderOffset
            .set(
                playerCamera.getOffX() - IsoCamera.getOffscreenLeft(int0) * playerCamera.zoom,
                playerCamera.getOffY() + IsoCamera.getOffscreenTop(int0) * playerCamera.zoom,
                (float)playerCamera.OffscreenWidth,
                (float)playerCamera.OffscreenHeight
            );
    }

    public void FBOStart() {
        int int0 = IsoCamera.frameState.playerIndex;
    }

    public void FBOEnd() {
        int int0 = IsoCamera.frameState.playerIndex;
    }

    private static final class RenderData {
        int numSquares;
        int capacity = 512;
        float[] data;

        void clear() {
            this.numSquares = 0;
        }

        void addSquare(IsoWaterGeometry waterGeometry) {
            int int0 = IsoCamera.frameState.playerIndex;
            byte byte0 = 4;
            if (this.data == null) {
                this.data = new float[this.capacity * byte0 * 7];
            }

            if (this.numSquares + 1 > this.capacity) {
                this.capacity += 128;
                this.data = Arrays.copyOf(this.data, this.capacity * byte0 * 7);
            }

            int int1 = this.numSquares * byte0 * 7;

            for (int int2 = 0; int2 < 4; int2++) {
                this.data[int1++] = waterGeometry.depth[int2];
                this.data[int1++] = waterGeometry.flow[int2];
                this.data[int1++] = waterGeometry.speed[int2];
                this.data[int1++] = waterGeometry.IsExternal;
                this.data[int1++] = waterGeometry.x[int2];
                this.data[int1++] = waterGeometry.y[int2];
                if (waterGeometry.square != null) {
                    int int3 = waterGeometry.square.getVertLight((4 - int2) % 4, int0);
                    this.data[int1++] = Float.intBitsToFloat(int3);
                } else {
                    int1++;
                }
            }

            this.numSquares++;
        }
    }
}
