// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.styles;

import java.util.ArrayList;
import zombie.core.math.PZMath;
import zombie.worldMap.WorldMapFeature;

public class WorldMapLineStyleLayer extends WorldMapStyleLayer {
    public final ArrayList<WorldMapStyleLayer.ColorStop> m_fill = new ArrayList<>();
    public final ArrayList<WorldMapStyleLayer.FloatStop> m_lineWidth = new ArrayList<>();

    public WorldMapLineStyleLayer(String string) {
        super(string);
    }

    @Override
    public String getTypeString() {
        return "Line";
    }

    @Override
    public void render(WorldMapFeature worldMapFeature, WorldMapStyleLayer.RenderArgs renderArgs) {
        WorldMapStyleLayer.RGBAf rGBAf = this.evalColor(renderArgs, this.m_fill);
        if (!(rGBAf.a < 0.01F)) {
            float float0;
            if (worldMapFeature.m_properties.containsKey("width")) {
                float0 = PZMath.tryParseFloat(worldMapFeature.m_properties.get("width"), 1.0F) * renderArgs.drawer.getWorldScale();
            } else {
                float0 = this.evalFloat(renderArgs, this.m_lineWidth);
            }

            renderArgs.drawer.drawLineString(renderArgs, worldMapFeature, rGBAf, float0);
            WorldMapStyleLayer.RGBAf.s_pool.release(rGBAf);
        }
    }
}
