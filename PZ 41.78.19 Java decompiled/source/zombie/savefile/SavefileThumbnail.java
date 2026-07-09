// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.savefile;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.glu.GLU;
import zombie.IndieGL;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderSettings;
import zombie.core.sprite.SpriteRenderState;
import zombie.core.textures.MultiTextureFBO2;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.interfaces.ITexture;
import zombie.iso.IsoCamera;
import zombie.iso.IsoWorld;
import zombie.iso.PlayerCamera;
import zombie.iso.sprite.IsoSprite;
import zombie.ui.UIManager;

public final class SavefileThumbnail {
    private static final int WIDTH = 256;
    private static final int HEIGHT = 256;

    public static void create() {
        int int0 = -1;

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            if (IsoPlayer.players[int1] != null) {
                int0 = int1;
                break;
            }
        }

        if (int0 != -1) {
            create(int0);
        }
    }

    public static void create(int int0) {
        Core core = Core.getInstance();
        MultiTextureFBO2 multiTextureFBO2 = core.OffscreenBuffer;
        float float0 = multiTextureFBO2.zoom[int0];
        float float1 = multiTextureFBO2.targetZoom[int0];
        setZoom(int0, 1.0F, 1.0F);
        IsoCamera.cameras[int0].center();
        renderWorld(int0, true, true);
        SpriteRenderer.instance.drawGeneric(new SavefileThumbnail.TakeScreenShotDrawer(int0));
        setZoom(int0, float0, float1);
        IsoCamera.cameras[int0].center();

        for (int int1 = 0; int1 < IsoPlayer.numPlayers; int1++) {
            IsoPlayer player = IsoPlayer.players[int1];
            if (player != null) {
                renderWorld(int1, false, int1 == int0);
            }
        }

        core.RenderOffScreenBuffer();
        if (core.StartFrameUI()) {
            UIManager.render();
        }

        core.EndFrameUI();
    }

    private static void renderWorld(int int0, boolean boolean0, boolean boolean1) {
        IsoPlayer.setInstance(IsoPlayer.players[int0]);
        IsoCamera.CamCharacter = IsoPlayer.players[int0];
        IsoSprite.globalOffsetX = -1.0F;
        Core.getInstance().StartFrame(int0, boolean0);
        if (boolean1) {
            SpriteRenderer.instance.drawGeneric(new SavefileThumbnail.FixCameraDrawer(int0));
        }

        IsoCamera.frameState.set(int0);
        IndieGL.glDisable(2929);
        IsoWorld.instance.render();
        RenderSettings.getInstance().legacyPostRender(int0);
        Core.getInstance().EndFrame(int0);
    }

    private static void setZoom(int int0, float float0, float float1) {
        Core.getInstance().OffscreenBuffer.zoom[int0] = float0;
        Core.getInstance().OffscreenBuffer.targetZoom[int0] = float1;
        IsoCamera.cameras[int0].zoom = float0;
        IsoCamera.cameras[int0].OffscreenWidth = IsoCamera.getOffscreenWidth(int0);
        IsoCamera.cameras[int0].OffscreenHeight = IsoCamera.getOffscreenHeight(int0);
    }

    private static void createWithRenderShader(int int2) {
        short short0 = 256;
        short short1 = 256;
        Texture texture = new Texture(short0, short1, 16);
        TextureFBO textureFBO = new TextureFBO(texture, false);
        GL11.glPushAttrib(1048575);

        try {
            textureFBO.startDrawing(true, false);
            GL11.glViewport(0, 0, short0, short1);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GLU.gluOrtho2D(0.0F, short0, short1, 0.0F);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            Core core = Core.getInstance();
            core.RenderShader.Start();
            GL11.glDisable(3089);
            GL11.glDisable(2960);
            GL11.glDisable(3042);
            GL11.glDisable(3008);
            GL11.glDisable(2929);
            GL11.glDisable(2884);

            for (int int0 = 8; int0 > 1; int0--) {
                GL13.glActiveTexture(33984 + int0 - 1);
                GL11.glDisable(3553);
            }

            GL13.glActiveTexture(33984);
            GL11.glEnable(3553);
            ITexture iTexture = core.getOffscreenBuffer().getTexture();
            iTexture.bind();
            int int1 = IsoCamera.getScreenLeft(int2) + IsoCamera.getScreenWidth(int2) / 2 - short0 / 2;
            int int3 = IsoCamera.getScreenTop(int2) + IsoCamera.getScreenHeight(int2) / 2 - short1 / 2;
            int int4 = core.getOffscreenBuffer().getTexture().getWidthHW();
            int int5 = core.getOffscreenBuffer().getTexture().getHeightHW();
            float float0 = (float)int1 / int4;
            float float1 = (float)(int1 + short0) / int4;
            float float2 = (float)int3 / int5;
            float float3 = (float)(int3 + short1) / int5;
            GL11.glBegin(7);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTexCoord2f(float0, float3);
            GL11.glVertex2d(0.0, 0.0);
            GL11.glTexCoord2f(float0, float2);
            GL11.glVertex2d(0.0, short1);
            GL11.glTexCoord2f(float1, float2);
            GL11.glVertex2d(short0, short1);
            GL11.glTexCoord2f(float1, float3);
            GL11.glVertex2d(short0, 0.0);
            GL11.glEnd();
            core.RenderShader.End();
            core.TakeScreenshot(0, 0, short0, short1, TextureFBO.getFuncs().GL_COLOR_ATTACHMENT0());
            textureFBO.endDrawing();
        } finally {
            textureFBO.destroy();
            GL11.glPopAttrib();
        }
    }

    private static final class FixCameraDrawer extends TextureDraw.GenericDrawer {
        int m_playerIndex;
        float m_zoom;
        int m_offscreenWidth;
        int m_offscreenHeight;

        FixCameraDrawer(int int0) {
            PlayerCamera playerCamera = IsoCamera.cameras[int0];
            this.m_playerIndex = int0;
            this.m_zoom = playerCamera.zoom;
            this.m_offscreenWidth = playerCamera.OffscreenWidth;
            this.m_offscreenHeight = playerCamera.OffscreenHeight;
        }

        @Override
        public void render() {
            SpriteRenderState spriteRenderState = SpriteRenderer.instance.getRenderingState();
            spriteRenderState.playerCamera[this.m_playerIndex].zoom = this.m_zoom;
            spriteRenderState.playerCamera[this.m_playerIndex].OffscreenWidth = this.m_offscreenWidth;
            spriteRenderState.playerCamera[this.m_playerIndex].OffscreenHeight = this.m_offscreenHeight;
            spriteRenderState.zoomLevel[this.m_playerIndex] = this.m_zoom;
        }
    }

    private static final class TakeScreenShotDrawer extends TextureDraw.GenericDrawer {
        int m_playerIndex;

        TakeScreenShotDrawer(int int0) {
            this.m_playerIndex = int0;
        }

        @Override
        public void render() {
            Core core = Core.getInstance();
            MultiTextureFBO2 multiTextureFBO2 = core.OffscreenBuffer;
            if (multiTextureFBO2.Current == null) {
                Core.getInstance().TakeScreenshot(256, 256, 1029);
            } else if (core.RenderShader == null) {
                Core.getInstance().getOffscreenBuffer().startDrawing(false, false);
                Core.getInstance().TakeScreenshot(256, 256, TextureFBO.getFuncs().GL_COLOR_ATTACHMENT0());
                Core.getInstance().getOffscreenBuffer().endDrawing();
            } else {
                SavefileThumbnail.createWithRenderShader(this.m_playerIndex);
            }
        }
    }
}
