// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.markers;

import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.math.PZMath;
import zombie.core.textures.Texture;
import zombie.worldMap.UIWorldMap;

public final class WorldMapGridSquareMarker extends WorldMapMarker {
    Texture m_texture1 = Texture.getSharedTexture("media/textures/worldMap/circle_center.png");
    Texture m_texture2 = Texture.getSharedTexture("media/textures/worldMap/circle_only_highlight.png");
    float m_r = 1.0F;
    float m_g = 1.0F;
    float m_b = 1.0F;
    float m_a = 1.0F;
    int m_worldX;
    int m_worldY;
    int m_radius = 10;
    int m_minScreenRadius = 64;
    boolean m_blink = true;

    WorldMapGridSquareMarker init(int int0, int int1, int int2, float float0, float float1, float float2, float float3) {
        this.m_worldX = int0;
        this.m_worldY = int1;
        this.m_radius = int2;
        this.m_r = float0;
        this.m_g = float1;
        this.m_b = float2;
        this.m_a = float3;
        return this;
    }

    public void setBlink(boolean blink) {
        this.m_blink = blink;
    }

    public void setMinScreenRadius(int pixels) {
        this.m_minScreenRadius = pixels;
    }

    @Override
    void render(UIWorldMap uIWorldMap) {
        float float0 = PZMath.max((float)this.m_radius, this.m_minScreenRadius / uIWorldMap.getAPI().getWorldScale());
        float float1 = uIWorldMap.getAPI().worldToUIX(this.m_worldX - float0, this.m_worldY - float0);
        float float2 = uIWorldMap.getAPI().worldToUIY(this.m_worldX - float0, this.m_worldY - float0);
        float float3 = uIWorldMap.getAPI().worldToUIX(this.m_worldX + float0, this.m_worldY - float0);
        float float4 = uIWorldMap.getAPI().worldToUIY(this.m_worldX + float0, this.m_worldY - float0);
        float float5 = uIWorldMap.getAPI().worldToUIX(this.m_worldX + float0, this.m_worldY + float0);
        float float6 = uIWorldMap.getAPI().worldToUIY(this.m_worldX + float0, this.m_worldY + float0);
        float float7 = uIWorldMap.getAPI().worldToUIX(this.m_worldX - float0, this.m_worldY + float0);
        float float8 = uIWorldMap.getAPI().worldToUIY(this.m_worldX - float0, this.m_worldY + float0);
        float1 = (float)(float1 + uIWorldMap.getAbsoluteX());
        float2 = (float)(float2 + uIWorldMap.getAbsoluteY());
        float3 = (float)(float3 + uIWorldMap.getAbsoluteX());
        float4 = (float)(float4 + uIWorldMap.getAbsoluteY());
        float5 = (float)(float5 + uIWorldMap.getAbsoluteX());
        float6 = (float)(float6 + uIWorldMap.getAbsoluteY());
        float7 = (float)(float7 + uIWorldMap.getAbsoluteX());
        float8 = (float)(float8 + uIWorldMap.getAbsoluteY());
        float float9 = this.m_a * (this.m_blink ? Core.blinkAlpha : 1.0F);
        if (this.m_texture1 != null && this.m_texture1.isReady()) {
            SpriteRenderer.instance
                .render(this.m_texture1, float1, float2, float3, float4, float5, float6, float7, float8, this.m_r, this.m_g, this.m_b, float9, null);
        }

        if (this.m_texture2 != null && this.m_texture2.isReady()) {
            SpriteRenderer.instance
                .render(this.m_texture2, float1, float2, float3, float4, float5, float6, float7, float8, this.m_r, this.m_g, this.m_b, float9, null);
        }
    }
}
