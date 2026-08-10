// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import zombie.popman.ObjectPool;
import zombie.worldMap.styles.WorldMapStyleLayer;

public final class WorldMapRenderLayer {
    WorldMapStyleLayer m_styleLayer;
    final ArrayList<WorldMapFeature> m_features = new ArrayList<>();
    static ObjectPool<WorldMapRenderLayer> s_pool = new ObjectPool<>(WorldMapRenderLayer::new);
}
