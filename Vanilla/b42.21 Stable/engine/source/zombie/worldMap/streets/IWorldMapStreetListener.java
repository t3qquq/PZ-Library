// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.worldMap.streets;

public interface IWorldMapStreetListener {
    void onAdd(WorldMapStreet arg0);

    void onBeforeRemove(WorldMapStreet arg0);

    void onAfterRemove(WorldMapStreet arg0);

    void onBeforeModifyStreet(WorldMapStreet arg0);

    void onAfterModifyStreet(WorldMapStreet arg0);
}
