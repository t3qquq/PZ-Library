// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.markers;

import zombie.util.PooledObject;
import zombie.worldMap.UIWorldMap;

public abstract class WorldMapMarker extends PooledObject {
    abstract void render(UIWorldMap var1);
}
