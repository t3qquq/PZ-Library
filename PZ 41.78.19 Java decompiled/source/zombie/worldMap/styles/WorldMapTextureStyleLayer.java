// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.textures.Texture;
import zombie.worldMap.WorldMapFeature;

public class WorldMapTextureStyleLayer extends WorldMapStyleLayer {
    public int m_worldX1;
    public int m_worldY1;
    public int m_worldX2;
    public int m_worldY2;
    public boolean m_useWorldBounds = false;
    public final ArrayList<WorldMapStyleLayer.ColorStop> m_fill = new ArrayList<>();
    public final ArrayList<WorldMapStyleLayer.TextureStop> m_texture = new ArrayList<>();
    public boolean m_tile = false;

    public WorldMapTextureStyleLayer(String string) {
        super(string);
    }

    @Override
    public String getTypeString() {
        return "Texture";
    }

    @Override
    public boolean filter(WorldMapFeature var1, WorldMapStyleLayer.FilterArgs var2) {
        return false;
    }

    @Override
    public void render(WorldMapFeature var1, WorldMapStyleLayer.RenderArgs var2) {
    }

    @Override
    public void renderCell(WorldMapStyleLayer.RenderArgs renderArgs) {
        if (this.m_useWorldBounds) {
            this.m_worldX1 = renderArgs.renderer.getWorldMap().getMinXInSquares();
            this.m_worldY1 = renderArgs.renderer.getWorldMap().getMinYInSquares();
            this.m_worldX2 = renderArgs.renderer.getWorldMap().getMaxXInSquares() + 1;
            this.m_worldY2 = renderArgs.renderer.getWorldMap().getMaxYInSquares() + 1;
        }

        WorldMapStyleLayer.RGBAf rGBAf = this.evalColor(renderArgs, this.m_fill);
        if (rGBAf.a < 0.01F) {
            WorldMapStyleLayer.RGBAf.s_pool.release(rGBAf);
        } else {
            Texture texture = this.evalTexture(renderArgs, this.m_texture);
            if (texture == null) {
                WorldMapStyleLayer.RGBAf.s_pool.release(rGBAf);
            } else {
                if (this.m_tile) {
                    renderArgs.drawer
                        .drawTextureTiled(texture, rGBAf, this.m_worldX1, this.m_worldY1, this.m_worldX2, this.m_worldY2, renderArgs.cellX, renderArgs.cellY);
                } else {
                    renderArgs.drawer.drawTexture(texture, rGBAf, this.m_worldX1, this.m_worldY1, this.m_worldX2, this.m_worldY2);
                }

                WorldMapStyleLayer.RGBAf.s_pool.release(rGBAf);
            }
        }
    }
}
