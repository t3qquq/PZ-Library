// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.physics;

import gnu.trove.list.array.TFloatArrayList;
import org.lwjgl.opengl.GL11;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.opengl.VBOLines;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.popman.ObjectPool;

public final class PhysicsDebugRenderer extends TextureDraw.GenericDrawer {
    private static final ObjectPool<PhysicsDebugRenderer> POOL = new ObjectPool<>(PhysicsDebugRenderer::new);
    private static final VBOLines vboLines = new VBOLines();
    private float camOffX;
    private float camOffY;
    private float deferredX;
    private float deferredY;
    private int drawOffsetX;
    private int drawOffsetY;
    private int playerIndex;
    private float playerX;
    private float playerY;
    private float playerZ;
    private float offscreenWidth;
    private float offscreenHeight;
    private final TFloatArrayList elements = new TFloatArrayList();

    public static PhysicsDebugRenderer alloc() {
        return POOL.alloc();
    }

    public void release() {
        POOL.release(this);
    }

    public void init(IsoPlayer player) {
        this.playerIndex = player.getPlayerNum();
        this.camOffX = IsoCamera.getRightClickOffX() + IsoCamera.PLAYER_OFFSET_X;
        this.camOffY = IsoCamera.getRightClickOffY() + IsoCamera.PLAYER_OFFSET_Y;
        this.camOffX = this.camOffX + this.XToScreenExact(player.x - (int)player.x, player.y - (int)player.y, 0.0F, 0);
        this.camOffY = this.camOffY + this.YToScreenExact(player.x - (int)player.x, player.y - (int)player.y, 0.0F, 0);
        this.deferredX = IsoCamera.cameras[this.playerIndex].DeferedX;
        this.deferredY = IsoCamera.cameras[this.playerIndex].DeferedY;
        this.drawOffsetX = (int)player.x;
        this.drawOffsetY = (int)player.y;
        this.playerX = player.x;
        this.playerY = player.y;
        this.playerZ = player.z;
        this.offscreenWidth = Core.getInstance().getOffscreenWidth(this.playerIndex);
        this.offscreenHeight = Core.getInstance().getOffscreenHeight(this.playerIndex);
        this.elements.clear();
        int int0 = (int)WorldSimulation.instance.offsetX - this.drawOffsetX;
        int int1 = (int)WorldSimulation.instance.offsetY - this.drawOffsetY;
        this.n_debugDrawWorld(int0, int1);
    }

    @Override
    public void render() {
        GL11.glPushAttrib(1048575);
        GL11.glDisable(3553);
        GL11.glDisable(3042);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, this.offscreenWidth, this.offscreenHeight, 0.0, 10000.0, -10000.0);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        int int0 = -this.drawOffsetX;
        int int1 = -this.drawOffsetY;
        float float0 = this.deferredX;
        float float1 = this.deferredY;
        GL11.glTranslatef(this.offscreenWidth / 2.0F, this.offscreenHeight / 2.0F, 0.0F);
        float float2 = this.XToScreenExact(float0, float1, this.playerZ, 0);
        float float3 = this.YToScreenExact(float0, float1, this.playerZ, 0);
        float2 += this.camOffX;
        float3 += this.camOffY;
        GL11.glTranslatef(-float2, -float3, 0.0F);
        int0 = (int)(int0 + WorldSimulation.instance.offsetX);
        int1 = (int)(int1 + WorldSimulation.instance.offsetY);
        int int2 = 32 * Core.TileScale;
        float float4 = (float)Math.sqrt(int2 * int2 + int2 * int2);
        GL11.glScalef(float4, float4, float4);
        GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
        vboLines.setLineWidth(1.0F);
        int int3 = 0;

        while (int3 < this.elements.size()) {
            float float5 = this.elements.getQuick(int3++);
            float float6 = this.elements.getQuick(int3++);
            float float7 = this.elements.getQuick(int3++);
            float float8 = this.elements.getQuick(int3++);
            float float9 = this.elements.getQuick(int3++);
            float float10 = this.elements.getQuick(int3++);
            float float11 = this.elements.getQuick(int3++);
            float float12 = this.elements.getQuick(int3++);
            float float13 = this.elements.getQuick(int3++);
            float float14 = this.elements.getQuick(int3++);
            float float15 = this.elements.getQuick(int3++);
            float float16 = this.elements.getQuick(int3++);
            vboLines.addLine(float5, float6, float7, float8, float9, float10, float11, float12, float13, 1.0F, float14, float15, float16, 1.0F);
        }

        vboLines.flush();
        GL11.glLineWidth(1.0F);
        GL11.glBegin(1);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(1.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 1.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 0.0);
        GL11.glVertex3d(0.0, 0.0, 1.0);
        GL11.glEnd();
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glEnable(3042);
        GL11.glEnable(3553);
        GL11.glPopAttrib();
        Texture.lastTextureID = -1;
    }

    @Override
    public void postRender() {
        this.release();
    }

    public float YToScreenExact(float objectX, float objectY, float objectZ, int screenZ) {
        return IsoUtils.YToScreen(objectX, objectY, objectZ, screenZ);
    }

    public float XToScreenExact(float objectX, float objectY, float objectZ, int screenZ) {
        return IsoUtils.XToScreen(objectX, objectY, objectZ, screenZ);
    }

    public void drawLine(
        float fromX, float fromY, float fromZ, float toX, float toY, float toZ, float fromR, float fromG, float fromB, float toR, float toG, float toB
    ) {
        if (!(fromX < -1000.0F) && !(fromX > 1000.0F) && !(fromY < -1000.0F) && !(fromY > 1000.0F)) {
            this.elements.add(fromX);
            this.elements.add(fromY);
            this.elements.add(fromZ);
            this.elements.add(toX);
            this.elements.add(toY);
            this.elements.add(toZ);
            this.elements.add(fromR);
            this.elements.add(fromG);
            this.elements.add(fromB);
            this.elements.add(toR);
            this.elements.add(toG);
            this.elements.add(toB);
        }
    }

    public void drawSphere(float pX, float pY, float pZ, float radius, float r, float g, float b) {
    }

    public void drawTriangle(float aX, float aY, float aZ, float bX, float bY, float bZ, float cX, float cY, float cZ, float r, float g, float b, float alpha) {
    }

    public void drawContactPoint(
        float pointOnBX,
        float pointOnBY,
        float pointOnBZ,
        float normalOnBX,
        float normalOnBY,
        float normalOnBZ,
        float distance,
        int lifeTime,
        float r,
        float g,
        float b
    ) {
    }

    public native void n_debugDrawWorld(int offsetX, int offsetY);
}
