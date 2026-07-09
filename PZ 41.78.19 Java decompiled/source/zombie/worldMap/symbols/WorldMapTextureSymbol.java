// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.symbols;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.worldMap.UIWorldMap;

public final class WorldMapTextureSymbol extends WorldMapBaseSymbol {
    private String m_symbolID;
    Texture m_texture;

    public WorldMapTextureSymbol(WorldMapSymbols owner) {
        super(owner);
    }

    public void setSymbolID(String symbolID) {
        this.m_symbolID = symbolID;
    }

    public String getSymbolID() {
        return this.m_symbolID;
    }

    public void checkTexture() {
        if (this.m_texture == null) {
            MapSymbolDefinitions.MapSymbolDefinition mapSymbolDefinition = MapSymbolDefinitions.getInstance().getSymbolById(this.getSymbolID());
            if (mapSymbolDefinition == null) {
                this.m_width = 18.0F;
                this.m_height = 18.0F;
            } else {
                this.m_texture = Texture.getSharedTexture(mapSymbolDefinition.getTexturePath());
                this.m_width = mapSymbolDefinition.getWidth();
                this.m_height = mapSymbolDefinition.getHeight();
            }

            if (this.m_texture == null) {
                this.m_texture = Texture.getErrorTexture();
            }
        }
    }

    @Override
    public WorldMapSymbols.WorldMapSymbolType getType() {
        return WorldMapSymbols.WorldMapSymbolType.Texture;
    }

    @Override
    public void layout(UIWorldMap ui, WorldMapSymbolCollisions collisions, float rox, float roy) {
        this.checkTexture();
        super.layout(ui, collisions, rox, roy);
    }

    @Override
    public void save(ByteBuffer output) throws IOException {
        super.save(output);
        GameWindow.WriteString(output, this.m_symbolID);
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, int SymbolsVersion) throws IOException {
        super.load(input, WorldVersion, SymbolsVersion);
        this.m_symbolID = GameWindow.ReadString(input);
    }

    @Override
    public void render(UIWorldMap ui, float rox, float roy) {
        if (this.m_collided) {
            this.renderCollided(ui, rox, roy);
        } else {
            this.checkTexture();
            float float0 = rox + this.m_layoutX;
            float float1 = roy + this.m_layoutY;
            if (this.m_scale > 0.0F) {
                float float2 = this.getDisplayScale(ui);
                SpriteRenderer.instance
                    .m_states
                    .getPopulatingActiveState()
                    .render(
                        this.m_texture,
                        ui.getAbsoluteX().intValue() + float0,
                        ui.getAbsoluteY().intValue() + float1,
                        this.m_texture.getWidth() * float2,
                        this.m_texture.getHeight() * float2,
                        this.m_r,
                        this.m_g,
                        this.m_b,
                        this.m_a,
                        null
                    );
            } else {
                ui.DrawTextureColor(this.m_texture, float0, float1, this.m_r, this.m_g, this.m_b, this.m_a);
            }
        }
    }

    @Override
    public void release() {
        this.m_symbolID = null;
        this.m_texture = null;
    }
}
