// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.iso.IsoUtils;
import zombie.network.GameServer;

public final class CorpseFlies {
    private static Texture TEXTURE;
    private static final int FRAME_WIDTH = 128;
    private static final int FRAME_HEIGHT = 128;
    private static final int COLUMNS = 8;
    private static final int ROWS = 7;
    private static final int NUM_FRAMES = 56;
    private static float COUNTER = 0.0F;
    private static int FRAME = 0;

    public static void render(int int1, int int2, int int5) {
        if (TEXTURE == null) {
            TEXTURE = Texture.getSharedTexture("media/textures/CorpseFlies.png");
        }

        if (TEXTURE != null && TEXTURE.isReady()) {
            int int0 = (FRAME + int1 + int2) % 56;
            int int3 = int0 % 8;
            int int4 = int0 / 8;
            float float0 = (float)(int3 * 128) / TEXTURE.getWidth();
            float float1 = (float)(int4 * 128) / TEXTURE.getHeight();
            float float2 = (float)((int3 + 1) * 128) / TEXTURE.getWidth();
            float float3 = (float)((int4 + 1) * 128) / TEXTURE.getHeight();
            float float4 = IsoUtils.XToScreen(int1 + 0.5F, int2 + 0.5F, int5, 0) + IsoSprite.globalOffsetX;
            float float5 = IsoUtils.YToScreen(int1 + 0.5F, int2 + 0.5F, int5, 0) + IsoSprite.globalOffsetY;
            byte byte0 = 64;
            int int6 = byte0 * Core.TileScale;
            float4 -= int6 / 2;
            float5 -= int6 + 16 * Core.TileScale;
            if (Core.bDebug) {
            }

            SpriteRenderer.instance
                .render(TEXTURE, float4, float5, int6, int6, 1.0F, 1.0F, 1.0F, 1.0F, float0, float1, float2, float1, float2, float3, float0, float3);
        }
    }

    public static void update() {
        if (!GameServer.bServer) {
            COUNTER = COUNTER + GameTime.getInstance().getRealworldSecondsSinceLastUpdate() * 1000.0F;
            float float0 = 20.0F;
            if (COUNTER > 1000.0F / float0) {
                COUNTER %= 1000.0F / float0;
                FRAME++;
                FRAME %= 56;
            }
        }
    }

    public static void Reset() {
        TEXTURE = null;
    }
}
