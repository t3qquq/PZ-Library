// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.math.PZMath;
import zombie.worldMap.UIWorldMap;

public abstract class WorldMapBaseSymbol {
    public static float DEFAULT_SCALE = 0.666F;
    WorldMapSymbols m_owner;
    float m_x;
    float m_y;
    float m_width;
    float m_height;
    float m_anchorX = 0.0F;
    float m_anchorY = 0.0F;
    float m_scale = DEFAULT_SCALE;
    float m_r;
    float m_g;
    float m_b;
    float m_a;
    boolean m_collide = false;
    boolean m_collided = false;
    float m_layoutX;
    float m_layoutY;
    boolean m_visible = true;

    public WorldMapBaseSymbol(WorldMapSymbols owner) {
        this.m_owner = owner;
    }

    public abstract WorldMapSymbols.WorldMapSymbolType getType();

    public void setAnchor(float x, float y) {
        this.m_anchorX = PZMath.clamp(x, 0.0F, 1.0F);
        this.m_anchorY = PZMath.clamp(y, 0.0F, 1.0F);
    }

    public void setPosition(float x, float y) {
        this.m_x = x;
        this.m_y = y;
    }

    public void setCollide(boolean collide) {
        this.m_collide = collide;
    }

    public void setRGBA(float r, float g, float b, float a) {
        this.m_r = PZMath.clamp_01(r);
        this.m_g = PZMath.clamp_01(g);
        this.m_b = PZMath.clamp_01(b);
        this.m_a = PZMath.clamp_01(a);
    }

    public void setScale(float scale) {
        this.m_scale = scale;
    }

    public float getDisplayScale(UIWorldMap ui) {
        if (this.m_scale <= 0.0F) {
            return this.m_scale;
        } else {
            return this.m_owner.getMiniMapSymbols() ? PZMath.min(this.m_owner.getLayoutWorldScale(), 1.0F) : this.m_owner.getLayoutWorldScale() * this.m_scale;
        }
    }

    public void layout(UIWorldMap ui, WorldMapSymbolCollisions collisions, float rox, float roy) {
        float float0 = ui.getAPI().worldToUIX(this.m_x, this.m_y) - rox;
        float float1 = ui.getAPI().worldToUIY(this.m_x, this.m_y) - roy;
        this.m_layoutX = float0 - this.widthScaled(ui) * this.m_anchorX;
        this.m_layoutY = float1 - this.heightScaled(ui) * this.m_anchorY;
        this.m_collided = collisions.addBox(this.m_layoutX, this.m_layoutY, this.widthScaled(ui), this.heightScaled(ui), this.m_collide);
        if (this.m_collided) {
        }
    }

    public float widthScaled(UIWorldMap ui) {
        return this.m_scale <= 0.0F ? this.m_width : this.m_width * this.getDisplayScale(ui);
    }

    public float heightScaled(UIWorldMap ui) {
        return this.m_scale <= 0.0F ? this.m_height : this.m_height * this.getDisplayScale(ui);
    }

    public void setVisible(boolean visible) {
        this.m_visible = visible;
    }

    public boolean isVisible() {
        return this.m_visible;
    }

    public void save(ByteBuffer output) throws IOException {
        output.putFloat(this.m_x);
        output.putFloat(this.m_y);
        output.putFloat(this.m_anchorX);
        output.putFloat(this.m_anchorY);
        output.putFloat(this.m_scale);
        output.put((byte)(this.m_r * 255.0F));
        output.put((byte)(this.m_g * 255.0F));
        output.put((byte)(this.m_b * 255.0F));
        output.put((byte)(this.m_a * 255.0F));
        output.put((byte)(this.m_collide ? 1 : 0));
    }

    public void load(ByteBuffer input, int WorldVersion, int SymbolsVersion) throws IOException {
        this.m_x = input.getFloat();
        this.m_y = input.getFloat();
        this.m_anchorX = input.getFloat();
        this.m_anchorY = input.getFloat();
        this.m_scale = input.getFloat();
        this.m_r = (input.get() & 255) / 255.0F;
        this.m_g = (input.get() & 255) / 255.0F;
        this.m_b = (input.get() & 255) / 255.0F;
        this.m_a = (input.get() & 255) / 255.0F;
        this.m_collide = input.get() == 1;
    }

    public abstract void render(UIWorldMap ui, float rox, float roy);

    void renderCollided(UIWorldMap uIWorldMap, float float1, float float3) {
        float float0 = float1 + this.m_layoutX + this.widthScaled(uIWorldMap) / 2.0F;
        float float2 = float3 + this.m_layoutY + this.heightScaled(uIWorldMap) / 2.0F;
        uIWorldMap.DrawTextureScaledCol(null, float0 - 3.0F, float2 - 3.0F, 6.0, 6.0, this.m_r, this.m_g, this.m_b, this.m_a);
    }

    public abstract void release();
}
