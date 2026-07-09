// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjglx.BufferUtils;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.Vector3;
import zombie.core.textures.Texture;
import zombie.creative.creativerects.OpenSimplexNoise;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;

public final class HeightTerrain {
    private final ByteBuffer buffer;
    public VertexBufferObject vb;
    public static float isoAngle = 62.65607F;
    public static float scale = 0.047085002F;
    OpenSimplexNoise noise = new OpenSimplexNoise(Rand.Next(10000000));
    static float[] lightAmbient = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static float[] lightDiffuse = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static float[] lightPosition = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static float[] specular = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static float[] shininess = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static float[] emission = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static float[] ambient = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static float[] diffuse = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
    static ByteBuffer temp = ByteBuffer.allocateDirect(16);

    public HeightTerrain(int int1, int int2) {
        ArrayList arrayList0 = new ArrayList();
        int int0 = int1 * int2;
        int int3 = int1;
        int int4 = int2;
        ArrayList arrayList1 = new ArrayList();
        Vector2 vector = new Vector2(2.0F, 0.0F);
        int int5 = 0;

        for (int int6 = 0; int6 < int3; int6++) {
            for (int int7 = 0; int7 < int4; int7++) {
                float float0 = (float)this.calc(int6, int7);
                float0 *= 1.0F;
                float0++;
                VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin0 = null;
                vertexPositionNormalTangentTextureSkin0 = new VertexPositionNormalTangentTextureSkin();
                vertexPositionNormalTangentTextureSkin0.Position = new Vector3();
                vertexPositionNormalTangentTextureSkin0.Position.set(-int6, float0 * 30.0F, -int7);
                vertexPositionNormalTangentTextureSkin0.Normal = new Vector3();
                vertexPositionNormalTangentTextureSkin0.Normal.set(0.0F, 1.0F, 0.0F);
                vertexPositionNormalTangentTextureSkin0.Normal.normalize();
                vertexPositionNormalTangentTextureSkin0.TextureCoordinates = new Vector2();
                vertexPositionNormalTangentTextureSkin0.TextureCoordinates = new Vector2((float)int6 / (int3 - 1) * 16.0F, (float)int7 / (int4 - 1) * 16.0F);
                arrayList0.add(vertexPositionNormalTangentTextureSkin0);
            }
        }

        int5 = 0;

        for (int int8 = 0; int8 < int3; int8++) {
            for (int int9 = 0; int9 < int4; int9++) {
                float float1 = (float)this.calc(int8, int9);
                float1 *= 1.0F;
                float1 = Math.max(0.0F, ++float1);
                float1 = Math.min(1.0F, float1);
                Object object = null;
                object = (VertexPositionNormalTangentTextureSkin)arrayList0.get(int5);
                Vector3 vector30 = new Vector3();
                Vector3 vector31 = new Vector3();
                float float2 = (float)this.calc(int8 + 1, int9);
                float2 *= 1.0F;
                float2++;
                float float3 = (float)this.calc(int8 - 1, int9);
                float3 *= 1.0F;
                float3++;
                float float4 = (float)this.calc(int8, int9 + 1);
                float4 *= 1.0F;
                float4++;
                float float5 = (float)this.calc(int8, int9 - 1);
                float5 *= 1.0F;
                float5++;
                float float6 = float2 * 700.0F;
                float float7 = float3 * 700.0F;
                float float8 = float4 * 700.0F;
                float float9 = float5 * 700.0F;
                vector30.set(vector.x, vector.y, float6 - float7);
                vector31.set(vector.y, vector.x, float8 - float9);
                vector30.normalize();
                vector31.normalize();
                Vector3 vector32 = vector30.cross(vector31);
                ((VertexPositionNormalTangentTextureSkin)object).Normal.x(vector32.x());
                ((VertexPositionNormalTangentTextureSkin)object).Normal.y(vector32.z());
                ((VertexPositionNormalTangentTextureSkin)object).Normal.z(vector32.y());
                ((VertexPositionNormalTangentTextureSkin)object).Normal.normalize();
                System.out
                    .println(
                        ((VertexPositionNormalTangentTextureSkin)object).Normal.x()
                            + " , "
                            + ((VertexPositionNormalTangentTextureSkin)object).Normal.y()
                            + ", "
                            + ((VertexPositionNormalTangentTextureSkin)object).Normal.z()
                    );
                ((VertexPositionNormalTangentTextureSkin)object).Normal.normalize();
                int5++;
            }
        }

        int5 = 0;

        for (int int10 = 0; int10 < int4 - 1; int10++) {
            if ((int10 & 1) == 0) {
                for (int int11 = 0; int11 < int3; int11++) {
                    arrayList1.add(int11 + (int10 + 1) * int3);
                    arrayList1.add(int11 + int10 * int3);
                    int5++;
                    int5++;
                }
            } else {
                for (int int12 = int3 - 1; int12 > 0; int12--) {
                    arrayList1.add(int12 - 1 + int10 * int3);
                    arrayList1.add(int12 + (int10 + 1) * int3);
                    int5++;
                    int5++;
                }
            }
        }

        if ((int3 & 1) > 0 && int4 > 2) {
            arrayList1.add((int4 - 1) * int3);
            int5++;
        }

        this.vb = new VertexBufferObject();
        ByteBuffer byteBuffer = BufferUtils.createByteBuffer(arrayList0.size() * 36);

        for (int int13 = 0; int13 < arrayList0.size(); int13++) {
            VertexPositionNormalTangentTextureSkin vertexPositionNormalTangentTextureSkin1 = (VertexPositionNormalTangentTextureSkin)arrayList0.get(int13);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.Position.x());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.Position.y());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.Position.z());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.Normal.x());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.Normal.y());
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.Normal.z());
            byte byte0 = -1;
            byteBuffer.putInt(byte0);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.TextureCoordinates.x);
            byteBuffer.putFloat(vertexPositionNormalTangentTextureSkin1.TextureCoordinates.y);
        }

        byteBuffer.flip();
        int[] ints = new int[arrayList1.size()];

        for (int int14 = 0; int14 < arrayList1.size(); int14++) {
            Integer integer = (Integer)arrayList1.get(arrayList1.size() - 1 - int14);
            ints[int14] = integer;
        }

        this.vb._handle = this.vb.LoadSoftwareVBO(byteBuffer, this.vb._handle, ints);
        this.buffer = byteBuffer;
    }

    double calcTerrain(float float0, float float1) {
        float0 *= 10.0F;
        float1 *= 10.0F;
        double double0 = this.noise.eval(float0 / 900.0F, float1 / 600.0F, 0.0);
        double0 += this.noise.eval(float0 / 600.0F, float1 / 600.0F, 0.0) / 4.0;
        double0 += (this.noise.eval(float0 / 300.0F, float1 / 300.0F, 0.0) + 1.0) / 8.0;
        double0 += (this.noise.eval(float0 / 150.0F, float1 / 150.0F, 0.0) + 1.0) / 16.0;
        double0 += (this.noise.eval(float0 / 75.0F, float1 / 75.0F, 0.0) + 1.0) / 32.0;
        double double1 = (this.noise.eval(float0, float1, 0.0) + 1.0) / 2.0;
        double1 *= (this.noise.eval(float0, float1, 0.0) + 1.0) / 2.0;
        return double0;
    }

    double calc(float float0, float float1) {
        return this.calcTerrain(float0, float1);
    }

    public void pushView(int var1, int var2, int var3) {
        GL11.glDepthMask(false);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        float float0 = 0.6F;
        byte byte0 = 0;
        byte byte1 = 0;
        int int0 = byte0 + IsoCamera.getOffscreenWidth(IsoPlayer.getPlayerIndex());
        int int1 = byte1 + IsoCamera.getOffscreenHeight(IsoPlayer.getPlayerIndex());
        double double0 = IsoUtils.XToIso(byte0, byte1, 0.0F);
        double double1 = IsoUtils.YToIso(0.0F, 0.0F, 0.0F);
        double double2 = IsoUtils.XToIso(Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()), 0.0F, 0.0F);
        double double3 = IsoUtils.YToIso(int0, byte1, 0.0F);
        double double4 = IsoUtils.XToIso(int0, int1, 0.0F);
        double double5 = IsoUtils.YToIso(
            Core.getInstance().getOffscreenWidth(IsoPlayer.getPlayerIndex()), Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()), 6.0F
        );
        double double6 = IsoUtils.XToIso(-128.0F, Core.getInstance().getOffscreenHeight(IsoPlayer.getPlayerIndex()), 6.0F);
        double double7 = IsoUtils.YToIso(byte0, int1, 0.0F);
        double double8 = double4 - double0;
        double double9 = double7 - double3;
        double8 = Math.abs(Core.getInstance().getOffscreenWidth(0)) / 1920.0F;
        double9 = Math.abs(Core.getInstance().getOffscreenHeight(0)) / 1080.0F;
        GL11.glLoadIdentity();
        GL11.glOrtho(-double8 / 2.0, double8 / 2.0, -double9 / 2.0, double9 / 2.0, -10.0, 10.0);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glScaled(scale, scale, scale);
        GL11.glRotatef(isoAngle, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslated(IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2, 0.0, IsoWorld.instance.CurrentCell.ChunkMap[0].getWidthInTiles() / 2);
        GL11.glDepthRange(-100.0, 100.0);
    }

    public void popView() {
        GL11.glEnable(3008);
        GL11.glDepthFunc(519);
        GL11.glDepthMask(false);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
    }

    public void render() {
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glDisable(2884);
        GL11.glEnable(2929);
        GL11.glDepthFunc(519);
        GL11.glColorMask(true, true, true, true);
        GL11.glAlphaFunc(519, 0.0F);
        GL11.glDepthFunc(519);
        GL11.glDepthRange(-10.0, 10.0);
        GL11.glEnable(2903);
        GL11.glEnable(2896);
        GL11.glEnable(16384);
        GL11.glEnable(16385);
        GL11.glEnable(2929);
        GL11.glDisable(3008);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(3008);
        GL11.glAlphaFunc(519, 0.0F);
        GL11.glDisable(3089);
        this.doLighting();
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glCullFace(1029);
        this.pushView(
            IsoPlayer.getInstance().getCurrentSquare().getChunk().wx / 30 * 300, IsoPlayer.getInstance().getCurrentSquare().getChunk().wy / 30 * 300, 0
        );
        Texture.getSharedTexture("media/textures/grass.png").bind();
        this.vb.DrawStrip(null);
        this.popView();
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glDisable(2929);
        GL11.glEnable(6144);
        if (PerformanceSettings.ModelLighting) {
            GL11.glDisable(2903);
            GL11.glDisable(2896);
            GL11.glDisable(16384);
            GL11.glDisable(16385);
        }

        GL11.glDepthRange(0.0, 100.0);
        SpriteRenderer.ringBuffer.restoreVBOs = true;
        GL11.glEnable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0F);
        GL11.glEnable(3553);
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
    }

    private void doLighting() {
        temp.order(ByteOrder.nativeOrder());
        temp.clear();
        GL11.glColorMaterial(1032, 5634);
        GL11.glDisable(2903);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2896);
        GL11.glEnable(16384);
        GL11.glDisable(16385);
        lightAmbient[0] = 0.7F;
        lightAmbient[1] = 0.7F;
        lightAmbient[2] = 0.7F;
        lightAmbient[3] = 0.5F;
        lightDiffuse[0] = 0.5F;
        lightDiffuse[1] = 0.5F;
        lightDiffuse[2] = 0.5F;
        lightDiffuse[3] = 1.0F;
        Vector3 vector3 = new Vector3(1.0F, 1.0F, 1.0F);
        vector3.normalize();
        lightPosition[0] = -vector3.x();
        lightPosition[1] = vector3.y();
        lightPosition[2] = -vector3.z();
        lightPosition[3] = 0.0F;
        GL11.glLightfv(16384, 4608, temp.asFloatBuffer().put(lightAmbient).flip());
        GL11.glLightfv(16384, 4609, temp.asFloatBuffer().put(lightDiffuse).flip());
        GL11.glLightfv(16384, 4611, temp.asFloatBuffer().put(lightPosition).flip());
        GL11.glLightf(16384, 4615, 0.0F);
        GL11.glLightf(16384, 4616, 0.0F);
        GL11.glLightf(16384, 4617, 0.0F);
        specular[0] = 0.0F;
        specular[1] = 0.0F;
        specular[2] = 0.0F;
        specular[3] = 0.0F;
        GL11.glMaterialfv(1032, 4610, temp.asFloatBuffer().put(specular).flip());
        GL11.glMaterialfv(1032, 5633, temp.asFloatBuffer().put(specular).flip());
        GL11.glMaterialfv(1032, 5632, temp.asFloatBuffer().put(specular).flip());
        ambient[0] = 0.6F;
        ambient[1] = 0.6F;
        ambient[2] = 0.6F;
        ambient[3] = 1.0F;
        diffuse[0] = 0.6F;
        diffuse[1] = 0.6F;
        diffuse[2] = 0.6F;
        diffuse[3] = 0.6F;
        GL11.glMaterialfv(1032, 4608, temp.asFloatBuffer().put(ambient).flip());
        GL11.glMaterialfv(1032, 4609, temp.asFloatBuffer().put(diffuse).flip());
    }
}
