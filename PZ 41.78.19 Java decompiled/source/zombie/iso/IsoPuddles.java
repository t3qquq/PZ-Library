// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjglx.BufferUtils;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.opengl.SharedVertexBufferObjects;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.interfaces.ITexture;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameServer;

public final class IsoPuddles {
    public Shader Effect;
    private float PuddlesWindAngle;
    private float PuddlesWindIntensity;
    private float PuddlesTime;
    private final Vector2f PuddlesParamWindINT;
    public static boolean leakingPuddlesInTheRoom = false;
    private Texture texHM;
    private int apiId;
    private static IsoPuddles instance;
    private static boolean isShaderEnable = false;
    static final int BYTES_PER_FLOAT = 4;
    static final int FLOATS_PER_VERTEX = 7;
    static final int BYTES_PER_VERTEX = 28;
    static final int VERTICES_PER_SQUARE = 4;
    public static final SharedVertexBufferObjects VBOs = new SharedVertexBufferObjects(28);
    private final IsoPuddles.RenderData[][] renderData = new IsoPuddles.RenderData[3][4];
    private final Vector4f shaderOffset = new Vector4f();
    private final Vector4f shaderOffsetMain = new Vector4f();
    private FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
    public static final int BOOL_MAX = 0;
    public static final int FLOAT_RAIN = 0;
    public static final int FLOAT_WETGROUND = 1;
    public static final int FLOAT_MUDDYPUDDLES = 2;
    public static final int FLOAT_PUDDLESSIZE = 3;
    public static final int FLOAT_RAININTENSITY = 4;
    public static final int FLOAT_MAX = 5;
    private IsoPuddles.PuddlesFloat rain;
    private IsoPuddles.PuddlesFloat wetGround;
    private IsoPuddles.PuddlesFloat muddyPuddles;
    private IsoPuddles.PuddlesFloat puddlesSize;
    private IsoPuddles.PuddlesFloat rainIntensity;
    private final IsoPuddles.PuddlesFloat[] climateFloats = new IsoPuddles.PuddlesFloat[5];

    public static synchronized IsoPuddles getInstance() {
        if (instance == null) {
            instance = new IsoPuddles();
        }

        return instance;
    }

    public boolean getShaderEnable() {
        return isShaderEnable;
    }

    public IsoPuddles() {
        if (GameServer.bServer) {
            Core.getInstance().setPerfPuddles(3);
            this.applyPuddlesQuality();
            this.PuddlesParamWindINT = new Vector2f(0.0F);
            this.setup();
        } else {
            this.texHM = Texture.getSharedTexture("media/textures/puddles_hm.png");
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
            this.applyPuddlesQuality();
            this.PuddlesParamWindINT = new Vector2f(0.0F);

            for (int int0 = 0; int0 < this.renderData.length; int0++) {
                for (int int1 = 0; int1 < 4; int1++) {
                    this.renderData[int0][int1] = new IsoPuddles.RenderData();
                }
            }

            this.setup();
        }
    }

    public void applyPuddlesQuality() {
        leakingPuddlesInTheRoom = Core.getInstance().getPerfPuddles() == 0;
        if (Core.getInstance().getPerfPuddles() == 3) {
            isShaderEnable = false;
        } else {
            isShaderEnable = true;
            if (PerformanceSettings.PuddlesQuality == 2) {
                RenderThread.invokeOnRenderContext(() -> {
                    this.Effect = new PuddlesShader("puddles_lq");
                    this.Effect.Start();
                    this.Effect.End();
                });
            }

            if (PerformanceSettings.PuddlesQuality == 1) {
                RenderThread.invokeOnRenderContext(() -> {
                    this.Effect = new PuddlesShader("puddles_mq");
                    this.Effect.Start();
                    this.Effect.End();
                });
            }

            if (PerformanceSettings.PuddlesQuality == 0) {
                RenderThread.invokeOnRenderContext(() -> {
                    this.Effect = new PuddlesShader("puddles_hq");
                    this.Effect.Start();
                    this.Effect.End();
                });
            }
        }
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

    public Vector4f getShaderOffsetMain() {
        int int0 = IsoCamera.frameState.playerIndex;
        PlayerCamera playerCamera = IsoCamera.cameras[int0];
        return this.shaderOffsetMain
            .set(
                playerCamera.getOffX() - IsoCamera.getOffscreenLeft(int0) * playerCamera.zoom,
                playerCamera.getOffY() + IsoCamera.getOffscreenTop(int0) * playerCamera.zoom,
                (float)IsoCamera.getOffscreenWidth(int0),
                (float)IsoCamera.getOffscreenHeight(int0)
            );
    }

    public void render(ArrayList<IsoGridSquare> grid, int z) {
        if (DebugOptions.instance.Weather.WaterPuddles.getValue()) {
            int int0 = SpriteRenderer.instance.getMainStateIndex();
            int int1 = IsoCamera.frameState.playerIndex;
            IsoPuddles.RenderData renderDatax = this.renderData[int0][int1];
            if (z == 0) {
                renderDatax.clear();
            }

            if (!grid.isEmpty()) {
                if (this.getShaderEnable()) {
                    if (Core.getInstance().getUseShaders()) {
                        if (Core.getInstance().getPerfPuddles() != 3) {
                            if (z <= 0 || Core.getInstance().getPerfPuddles() <= 0) {
                                if (this.wetGround.getFinalValue() != 0.0 || this.puddlesSize.getFinalValue() != 0.0) {
                                    for (int int2 = 0; int2 < grid.size(); int2++) {
                                        IsoPuddlesGeometry puddlesGeometry = ((IsoGridSquare)grid.get(int2)).getPuddles();
                                        if (puddlesGeometry != null && puddlesGeometry.shouldRender()) {
                                            puddlesGeometry.updateLighting(int1);
                                            renderDatax.addSquare(z, puddlesGeometry);
                                        }
                                    }

                                    if (renderDatax.squaresPerLevel[z] > 0) {
                                        SpriteRenderer.instance.drawPuddles(this.Effect, int1, this.apiId, z);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void puddlesProjection() {
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

    public void puddlesGeometry(int z) {
        int int0 = SpriteRenderer.instance.getRenderStateIndex();
        int int1 = SpriteRenderer.instance.getRenderingPlayerIndex();
        IsoPuddles.RenderData renderDatax = this.renderData[int0][int1];
        int int2 = 0;

        for (int int3 = 0; int3 < z; int3++) {
            int2 += renderDatax.squaresPerLevel[int3];
        }

        int int4 = renderDatax.squaresPerLevel[z];

        while (int4 > 0) {
            int int5 = this.renderSome(int2, int4);
            int2 += int5;
            int4 -= int5;
        }

        SpriteRenderer.ringBuffer.restoreVBOs = true;
    }

    private int renderSome(int int4, int int3) {
        VBOs.next();
        FloatBuffer floatBufferx = VBOs.vertices;
        ShortBuffer shortBuffer = VBOs.indices;
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
        IsoPuddles.RenderData renderDatax = this.renderData[int0][int1];
        int int2 = Math.min(int3 * 4, VBOs.bufferSizeVertices);
        floatBufferx.put(renderDatax.data, int4 * 4 * 7, int2 * 7);
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

        VBOs.unmap();
        byte byte2 = 0;
        byte byte3 = 0;
        GL12.glDrawRangeElements(4, byte2, byte2 + byte0, byte1 - byte3, 5123, byte3 * 2);
        return int2 / 4;
    }

    public void update(ClimateManager cm) {
        this.PuddlesWindAngle = cm.getCorrectedWindAngleIntensity();
        this.PuddlesWindIntensity = cm.getWindIntensity();
        this.rain.setFinalValue(cm.getRainIntensity());
        float float0 = GameTime.getInstance().getMultiplier() / 1.6F;
        float float1 = 2.0E-5F * float0 * cm.getTemperature();
        float float2 = 2.0E-5F * float0;
        float float3 = 2.0E-4F * float0;
        float float4 = this.rain.getFinalValue();
        float4 = float4 * float4 * 0.05F * float0;
        this.rainIntensity.setFinalValue(this.rain.getFinalValue() * 2.0F);
        this.wetGround.addFinalValue(float4);
        this.muddyPuddles.addFinalValue(float4 * 2.0F);
        this.puddlesSize.addFinalValueForMax(float4 * 0.01F, 0.7F);
        if (float4 == 0.0) {
            this.wetGround.addFinalValue(-float1);
            this.muddyPuddles.addFinalValue(-float3);
        }

        if (this.wetGround.getFinalValue() == 0.0) {
            this.puddlesSize.addFinalValue(-float2);
        }

        this.PuddlesTime = this.PuddlesTime + 0.0166F * GameTime.getInstance().getMultiplier();
        this.PuddlesParamWindINT
            .add(
                (float)Math.sin(this.PuddlesWindAngle * 6.0F) * this.PuddlesWindIntensity * 0.05F,
                (float)Math.cos(this.PuddlesWindAngle * 6.0F) * this.PuddlesWindIntensity * 0.05F
            );
    }

    public float getShaderTime() {
        return this.PuddlesTime;
    }

    public float getPuddlesSize() {
        return this.puddlesSize.getFinalValue();
    }

    public ITexture getHMTexture() {
        return this.texHM;
    }

    public FloatBuffer getPuddlesParams(int z) {
        this.floatBuffer.clear();
        this.floatBuffer.put(this.PuddlesParamWindINT.x);
        this.floatBuffer.put(this.muddyPuddles.getFinalValue());
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(this.PuddlesParamWindINT.y);
        this.floatBuffer.put(this.wetGround.getFinalValue());
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(this.PuddlesWindIntensity * 1.0F);
        this.floatBuffer.put(this.puddlesSize.getFinalValue());
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(z);
        this.floatBuffer.put(this.rainIntensity.getFinalValue());
        this.floatBuffer.put(0.0F);
        this.floatBuffer.put(0.0F);
        this.floatBuffer.flip();
        return this.floatBuffer;
    }

    public float getRainIntensity() {
        return this.rainIntensity.getFinalValue();
    }

    public int getFloatMax() {
        return 5;
    }

    public int getBoolMax() {
        return 0;
    }

    public IsoPuddles.PuddlesFloat getPuddlesFloat(int id) {
        if (id >= 0 && id < 5) {
            return this.climateFloats[id];
        } else {
            DebugLog.log("ERROR: Climate: cannot get float override id.");
            return null;
        }
    }

    private IsoPuddles.PuddlesFloat initClimateFloat(int int0, String string) {
        if (int0 >= 0 && int0 < 5) {
            return this.climateFloats[int0].init(int0, string);
        } else {
            DebugLog.log("ERROR: Climate: cannot get float override id.");
            return null;
        }
    }

    private void setup() {
        for (int int0 = 0; int0 < this.climateFloats.length; int0++) {
            this.climateFloats[int0] = new IsoPuddles.PuddlesFloat();
        }

        this.rain = this.initClimateFloat(0, "INPUT: RAIN");
        this.wetGround = this.initClimateFloat(1, "Wet Ground");
        this.muddyPuddles = this.initClimateFloat(2, "Muddy Puddles");
        this.puddlesSize = this.initClimateFloat(3, "Puddles Size");
        this.rainIntensity = this.initClimateFloat(4, "Rain Intensity");
    }

    public static class PuddlesFloat {
        protected float finalValue;
        private boolean isAdminOverride = false;
        private float adminValue;
        private float min = 0.0F;
        private float max = 1.0F;
        private float delta = 0.01F;
        private int ID;
        private String name;

        public IsoPuddles.PuddlesFloat init(int id, String _name) {
            this.ID = id;
            this.name = _name;
            return this;
        }

        public int getID() {
            return this.ID;
        }

        public String getName() {
            return this.name;
        }

        public float getMin() {
            return this.min;
        }

        public float getMax() {
            return this.max;
        }

        public void setEnableAdmin(boolean b) {
            this.isAdminOverride = b;
        }

        public boolean isEnableAdmin() {
            return this.isAdminOverride;
        }

        public void setAdminValue(float f) {
            this.adminValue = Math.max(this.min, Math.min(this.max, f));
        }

        public float getAdminValue() {
            return this.adminValue;
        }

        public void setFinalValue(float f) {
            this.finalValue = Math.max(this.min, Math.min(this.max, f));
        }

        public void addFinalValue(float f) {
            this.finalValue = Math.max(this.min, Math.min(this.max, this.finalValue + f));
        }

        public void addFinalValueForMax(float f, float maximum) {
            this.finalValue = Math.max(this.min, Math.min(maximum, this.finalValue + f));
        }

        public float getFinalValue() {
            return this.isAdminOverride ? this.adminValue : this.finalValue;
        }

        public void interpolateFinalValue(float f) {
            if (Math.abs(this.finalValue - f) < this.delta) {
                this.finalValue = f;
            } else if (f > this.finalValue) {
                this.finalValue = this.finalValue + this.delta;
            } else {
                this.finalValue = this.finalValue - this.delta;
            }
        }

        private void calculate() {
            if (this.isAdminOverride) {
                this.finalValue = this.adminValue;
            }
        }
    }

    private static final class RenderData {
        final int[] squaresPerLevel = new int[8];
        int numSquares;
        int capacity = 512;
        float[] data;

        RenderData() {
        }

        void clear() {
            this.numSquares = 0;
            Arrays.fill(this.squaresPerLevel, 0);
        }

        void addSquare(int int2, IsoPuddlesGeometry puddlesGeometry) {
            byte byte0 = 4;
            if (this.data == null) {
                this.data = new float[this.capacity * byte0 * 7];
            }

            if (this.numSquares + 1 > this.capacity) {
                this.capacity += 128;
                this.data = Arrays.copyOf(this.data, this.capacity * byte0 * 7);
            }

            int int0 = this.numSquares * byte0 * 7;

            for (int int1 = 0; int1 < 4; int1++) {
                this.data[int0++] = puddlesGeometry.pdne[int1];
                this.data[int0++] = puddlesGeometry.pdnw[int1];
                this.data[int0++] = puddlesGeometry.pda[int1];
                this.data[int0++] = puddlesGeometry.pnon[int1];
                this.data[int0++] = puddlesGeometry.x[int1];
                this.data[int0++] = puddlesGeometry.y[int1];
                this.data[int0++] = Float.intBitsToFloat(puddlesGeometry.color[int1]);
            }

            this.numSquares++;
            this.squaresPerLevel[int2]++;
        }
    }
}
