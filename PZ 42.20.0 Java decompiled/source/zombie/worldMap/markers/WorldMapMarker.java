// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.worldMap.markers;

import zombie.util.PooledObject;
import zombie.worldMap.UIWorldMap;

public abstract class WorldMapMarker extends PooledObject {
    abstract void render(UIWorldMap arg0);
}
