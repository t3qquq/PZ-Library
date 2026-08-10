// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.markers;

import zombie.util.PooledObject;
import zombie.worldMap.UIWorldMap;

public abstract class WorldMapMarker extends PooledObject {
    abstract void render(UIWorldMap var1);
}
