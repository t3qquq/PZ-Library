// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import java.util.function.Consumer;
import org.lwjgl.opengl.GL11;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.input.Mouse;
import zombie.iso.IsoCamera;

public final class IsoCursor {
    private static IsoCursor instance = null;
    IsoCursor.IsoCursorShader m_shader = null;

    public static IsoCursor getInstance() {
        if (instance == null) {
            instance = new IsoCursor();
        }

        return instance;
    }

    private IsoCursor() {
        RenderThread.invokeOnRenderContext(this::createShader);
        if (this.m_shader != null) {
            this.m_shader.m_textureCursor = Texture.getSharedTexture("media/ui/isocursor.png");
        }
    }

    private void createShader() {
        this.m_shader = new IsoCursor.IsoCursorShader();
    }

    public void render(int int0) {
        if (Core.getInstance().getOffscreenBuffer() != null) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null && !player.isDead() && player.isAiming() && player.PlayerIndex == 0 && player.JoypadBind == -1) {
                if (!GameTime.isGamePaused()) {
                    if (this.m_shader != null && this.m_shader.isCompiled()) {
                        float float0 = 1.0F / Core.getInstance().getZoom(int0);
                        int int1 = (int)(this.m_shader.m_textureCursor.getWidth() * Core.TileScale / 2.0F * float0);
                        int int2 = (int)(this.m_shader.m_textureCursor.getHeight() * Core.TileScale / 2.0F * float0);
                        this.m_shader.m_screenX = Mouse.getXA() - int1 / 2;
                        this.m_shader.m_screenY = Mouse.getYA() - int2 / 2;
                        this.m_shader.width = int1;
                        this.m_shader.height = int2;
                        int int3 = IsoCamera.getScreenLeft(int0);
                        int int4 = IsoCamera.getScreenTop(int0);
                        int int5 = IsoCamera.getScreenWidth(int0);
                        int int6 = IsoCamera.getScreenHeight(int0);
                        SpriteRenderer.instance.StartShader(this.m_shader.getID(), int0);
                        SpriteRenderer.instance
                            .renderClamped(
                                this.m_shader.m_textureCursor,
                                this.m_shader.m_screenX,
                                this.m_shader.m_screenY,
                                int1,
                                int2,
                                int3,
                                int4,
                                int5,
                                int6,
                                1.0F,
                                1.0F,
                                1.0F,
                                1.0F,
                                this.m_shader
                            );
                        SpriteRenderer.instance.EndShader();
                    }
                }
            }
        }
    }

    private static class IsoCursorShader extends Shader implements Consumer<TextureDraw> {
        private float m_alpha = 1.0F;
        private Texture m_textureCursor;
        private Texture m_textureWorld;
        private int m_screenX;
        private int m_screenY;

        IsoCursorShader() {
            super("isocursor");
        }

        @Override
        public void startMainThread(TextureDraw var1, int int0) {
            this.m_alpha = this.calculateAlpha();
            this.m_textureWorld = Core.getInstance().OffscreenBuffer.getTexture(int0);
        }

        @Override
        public void startRenderThread(TextureDraw var1) {
            this.getProgram().setValue("u_alpha", this.m_alpha);
            this.getProgram().setValue("TextureCursor", this.m_textureCursor, 0);
            this.getProgram().setValue("TextureBackground", this.m_textureWorld, 1);
            SpriteRenderer.ringBuffer.shaderChangedTexture1();
            GL11.glEnable(3042);
        }

        public void accept(TextureDraw textureDraw) {
            byte byte0 = 0;
            int int0 = (int)textureDraw.x0 - this.m_screenX;
            int int1 = (int)textureDraw.y0 - this.m_screenY;
            int int2 = this.m_screenX + this.width - (int)textureDraw.x2;
            int int3 = this.m_screenY + this.height - (int)textureDraw.y2;
            this.m_screenX += int0;
            this.m_screenY += int1;
            this.width -= int0 + int2;
            this.height -= int1 + int3;
            float float0 = this.m_textureWorld.getWidthHW();
            float float1 = this.m_textureWorld.getHeightHW();
            float float2 = IsoCamera.getScreenTop(byte0) + IsoCamera.getScreenHeight(byte0) - (this.m_screenY + this.height);
            textureDraw.tex1 = this.m_textureWorld;
            textureDraw.tex1_u0 = this.m_screenX / float0;
            textureDraw.tex1_v3 = float2 / float1;
            textureDraw.tex1_u1 = (this.m_screenX + this.width) / float0;
            textureDraw.tex1_v2 = float2 / float1;
            textureDraw.tex1_u2 = (this.m_screenX + this.width) / float0;
            textureDraw.tex1_v1 = (float2 + this.height) / float1;
            textureDraw.tex1_u3 = this.m_screenX / float0;
            textureDraw.tex1_v0 = (float2 + this.height) / float1;
        }

        float calculateAlpha() {
            float float0 = 0.05F;
            switch (Core.getInstance().getIsoCursorVisibility()) {
                case 0:
                    float0 = 0.0F;
                    break;
                case 1:
                    float0 = 0.05F;
                    break;
                case 2:
                    float0 = 0.1F;
                    break;
                case 3:
                    float0 = 0.15F;
                    break;
                case 4:
                    float0 = 0.3F;
                    break;
                case 5:
                    float0 = 0.5F;
                    break;
                case 6:
                    float0 = 0.75F;
            }

            return float0;
        }
    }
}
