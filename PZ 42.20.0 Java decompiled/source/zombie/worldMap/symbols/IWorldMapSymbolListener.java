// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.worldMap.symbols;

public interface IWorldMapSymbolListener {
    void onAdd(WorldMapBaseSymbol arg0);

    void onBeforeRemove(WorldMapBaseSymbol arg0);

    void onAfterRemove(WorldMapBaseSymbol arg0);

    void onBeforeClear();

    void onAfterClear();
}
