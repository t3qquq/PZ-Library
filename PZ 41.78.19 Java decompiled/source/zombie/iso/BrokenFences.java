// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.MapCollisionData;
import zombie.SoundManager;
import zombie.core.Rand;
import zombie.core.properties.PropertyContainer;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.PolygonalMap2;

public class BrokenFences {
    private static final BrokenFences instance = new BrokenFences();
    private final THashMap<String, BrokenFences.Tile> s_unbrokenMap = new THashMap<>();
    private final THashMap<String, BrokenFences.Tile> s_brokenLeftMap = new THashMap<>();
    private final THashMap<String, BrokenFences.Tile> s_brokenRightMap = new THashMap<>();
    private final THashMap<String, BrokenFences.Tile> s_allMap = new THashMap<>();

    public static BrokenFences getInstance() {
        return instance;
    }

    private ArrayList<String> tableToTiles(KahluaTableImpl kahluaTableImpl) {
        if (kahluaTableImpl == null) {
            return null;
        } else {
            ArrayList arrayList = null;

            for (KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();
                kahluaTableIterator.advance();
                arrayList.add(kahluaTableIterator.getValue().toString())
            ) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
            }

            return arrayList;
        }
    }

    private ArrayList<String> tableToTiles(KahluaTable table, String string) {
        return this.tableToTiles((KahluaTableImpl)table.rawget(string));
    }

    public void addBrokenTiles(KahluaTableImpl tiles) {
        KahluaTableIterator kahluaTableIterator = tiles.iterator();

        while (kahluaTableIterator.advance()) {
            String string = kahluaTableIterator.getKey().toString();
            if (!"VERSION".equalsIgnoreCase(string)) {
                KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTableIterator.getValue();
                BrokenFences.Tile tile = new BrokenFences.Tile();
                tile.self = this.tableToTiles(kahluaTableImpl, "self");
                tile.left = this.tableToTiles(kahluaTableImpl, "left");
                tile.right = this.tableToTiles(kahluaTableImpl, "right");
                this.s_unbrokenMap.put(string, tile);
                PZArrayUtil.forEach(tile.left, stringx -> this.s_brokenLeftMap.put(stringx, tile));
                PZArrayUtil.forEach(tile.right, stringx -> this.s_brokenRightMap.put(stringx, tile));
            }
        }

        this.s_allMap.putAll(this.s_unbrokenMap);
        this.s_allMap.putAll(this.s_brokenLeftMap);
        this.s_allMap.putAll(this.s_brokenRightMap);
    }

    public void addDebrisTiles(KahluaTableImpl tiles) {
        KahluaTableIterator kahluaTableIterator = tiles.iterator();

        while (kahluaTableIterator.advance()) {
            String string = kahluaTableIterator.getKey().toString();
            if (!"VERSION".equalsIgnoreCase(string)) {
                KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTableIterator.getValue();
                BrokenFences.Tile tile = this.s_unbrokenMap.get(string);
                if (tile == null) {
                    throw new IllegalArgumentException("addDebrisTiles() with unknown tile");
                }

                tile.debrisN = this.tableToTiles(kahluaTableImpl, "north");
                tile.debrisS = this.tableToTiles(kahluaTableImpl, "south");
                tile.debrisW = this.tableToTiles(kahluaTableImpl, "west");
                tile.debrisE = this.tableToTiles(kahluaTableImpl, "east");
            }
        }
    }

    public void setDestroyed(IsoObject obj) {
        obj.RemoveAttachedAnims();
        obj.getSquare().removeBlood(false, true);
        this.updateSprite(obj, true, true);
    }

    public void setDamagedLeft(IsoObject obj) {
        this.updateSprite(obj, true, false);
    }

    public void setDamagedRight(IsoObject obj) {
        this.updateSprite(obj, false, true);
    }

    public void updateSprite(IsoObject obj, boolean brokenLeft, boolean brokenRight) {
        if (this.isBreakableObject(obj)) {
            BrokenFences.Tile tile = this.s_allMap.get(obj.sprite.name);
            String string = null;
            if (brokenLeft && brokenRight) {
                string = tile.pickRandom(tile.self);
            } else if (brokenLeft) {
                string = tile.pickRandom(tile.left);
            } else if (brokenRight) {
                string = tile.pickRandom(tile.right);
            }

            if (string != null) {
                IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
                sprite.name = string;
                obj.setSprite(sprite);
                obj.transmitUpdatedSprite();
                obj.getSquare().RecalcAllWithNeighbours(true);
                MapCollisionData.instance.squareChanged(obj.getSquare());
                PolygonalMap2.instance.squareChanged(obj.getSquare());
                IsoRegions.squareChanged(obj.getSquare());
            }
        }
    }

    private boolean isNW(IsoObject object) {
        PropertyContainer propertyContainer = object.getProperties();
        return propertyContainer.Is(IsoFlagType.collideN) && propertyContainer.Is(IsoFlagType.collideW);
    }

    private void damageAdjacent(IsoGridSquare square1, IsoDirections directions0, IsoDirections directions1) {
        IsoGridSquare square0 = square1.getAdjacentSquare(directions0);
        if (square0 != null) {
            boolean boolean0 = directions0 == IsoDirections.W || directions0 == IsoDirections.E;
            IsoObject object = this.getBreakableObject(square0, boolean0);
            if (object != null) {
                boolean boolean1 = directions0 == IsoDirections.N || directions0 == IsoDirections.E;
                boolean boolean2 = directions0 == IsoDirections.S || directions0 == IsoDirections.W;
                if (!this.isNW(object) || directions0 != IsoDirections.S && directions0 != IsoDirections.E) {
                    if (boolean1 && this.isBrokenRight(object)) {
                        this.destroyFence(object, directions1);
                    } else if (boolean2 && this.isBrokenLeft(object)) {
                        this.destroyFence(object, directions1);
                    } else {
                        this.updateSprite(object, boolean1, boolean2);
                    }
                }
            }
        }
    }

    public void destroyFence(IsoObject obj, IsoDirections dir) {
        if (this.isBreakableObject(obj)) {
            IsoGridSquare square = obj.getSquare();
            if (GameServer.bServer) {
                GameServer.PlayWorldSoundServer("BreakObject", false, square, 1.0F, 20.0F, 1.0F, true);
            } else {
                SoundManager.instance.PlayWorldSound("BreakObject", square, 1.0F, 20.0F, 1.0F, true);
            }

            boolean boolean0 = obj.getProperties().Is(IsoFlagType.collideN);
            boolean boolean1 = obj.getProperties().Is(IsoFlagType.collideW);
            if (obj instanceof IsoThumpable) {
                IsoObject object = IsoObject.getNew();
                object.setSquare(square);
                object.setSprite(obj.getSprite());
                int int0 = obj.getObjectIndex();
                square.transmitRemoveItemFromSquare(obj);
                square.transmitAddObjectToSquare(object, int0);
                obj = object;
            }

            this.addDebrisObject(obj, dir);
            this.setDestroyed(obj);
            if (boolean0 && boolean1) {
                this.damageAdjacent(square, IsoDirections.S, dir);
                this.damageAdjacent(square, IsoDirections.E, dir);
            } else if (boolean0) {
                this.damageAdjacent(square, IsoDirections.W, dir);
                this.damageAdjacent(square, IsoDirections.E, dir);
            } else if (boolean1) {
                this.damageAdjacent(square, IsoDirections.N, dir);
                this.damageAdjacent(square, IsoDirections.S, dir);
            }

            square.RecalcAllWithNeighbours(true);
            MapCollisionData.instance.squareChanged(square);
            PolygonalMap2.instance.squareChanged(square);
            IsoRegions.squareChanged(square);
        }
    }

    private boolean isUnbroken(IsoObject object) {
        return object != null && object.sprite != null && object.sprite.name != null ? this.s_unbrokenMap.contains(object.sprite.name) : false;
    }

    private boolean isBrokenLeft(IsoObject object) {
        return object != null && object.sprite != null && object.sprite.name != null ? this.s_brokenLeftMap.contains(object.sprite.name) : false;
    }

    private boolean isBrokenRight(IsoObject object) {
        return object != null && object.sprite != null && object.sprite.name != null ? this.s_brokenRightMap.contains(object.sprite.name) : false;
    }

    public boolean isBreakableObject(IsoObject obj) {
        return obj != null && obj.sprite != null && obj.sprite.name != null ? this.s_allMap.containsKey(obj.sprite.name) : false;
    }

    private IsoObject getBreakableObject(IsoGridSquare square, boolean boolean0) {
        for (int int0 = 0; int0 < square.Objects.size(); int0++) {
            IsoObject object = square.Objects.get(int0);
            if (this.isBreakableObject(object)
                && (boolean0 && object.getProperties().Is(IsoFlagType.collideN) || !boolean0 && object.getProperties().Is(IsoFlagType.collideW))) {
                return object;
            }
        }

        return null;
    }

    private void addItems(IsoObject object, IsoGridSquare square) {
        PropertyContainer propertyContainer = object.getProperties();
        if (propertyContainer != null) {
            String string0 = propertyContainer.Val("Material");
            String string1 = propertyContainer.Val("Material2");
            String string2 = propertyContainer.Val("Material3");
            if ("Wood".equals(string0) || "Wood".equals(string1) || "Wood".equals(string2)) {
                square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
                if (Rand.NextBool(5)) {
                    square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Plank"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
                }
            }

            if (("MetalBars".equals(string0) || "MetalBars".equals(string1) || "MetalBars".equals(string2)) && Rand.NextBool(2)) {
                square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.MetalBar"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
            }

            if (("MetalWire".equals(string0) || "MetalWire".equals(string1) || "MetalWire".equals(string2)) && Rand.NextBool(3)) {
                square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Wire"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
            }

            if (("Nails".equals(string0) || "Nails".equals(string1) || "Nails".equals(string2)) && Rand.NextBool(2)) {
                square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Nails"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
            }

            if (("Screws".equals(string0) || "Screws".equals(string1) || "Screws".equals(string2)) && Rand.NextBool(2)) {
                square.AddWorldInventoryItem(InventoryItemFactory.CreateItem("Base.Screws"), Rand.Next(0.0F, 0.5F), Rand.Next(0.0F, 0.5F), 0.0F);
            }
        }
    }

    private void addDebrisObject(IsoObject object0, IsoDirections directions) {
        if (this.isBreakableObject(object0)) {
            BrokenFences.Tile tile = this.s_allMap.get(object0.sprite.name);
            IsoGridSquare square = object0.getSquare();
            String string;
            switch (directions) {
                case N:
                    string = tile.pickRandom(tile.debrisN);
                    square = square.getAdjacentSquare(directions);
                    break;
                case S:
                    string = tile.pickRandom(tile.debrisS);
                    break;
                case W:
                    string = tile.pickRandom(tile.debrisW);
                    square = square.getAdjacentSquare(directions);
                    break;
                case E:
                    string = tile.pickRandom(tile.debrisE);
                    break;
                default:
                    throw new IllegalArgumentException("invalid direction");
            }

            if (string != null && square != null && square.TreatAsSolidFloor()) {
                IsoObject object1 = IsoObject.getNew(square, string, null, false);
                square.transmitAddObjectToSquare(object1, square == object0.getSquare() ? object0.getObjectIndex() : -1);
                this.addItems(object0, square);
            }
        }
    }

    public void Reset() {
        this.s_unbrokenMap.clear();
        this.s_brokenLeftMap.clear();
        this.s_brokenRightMap.clear();
        this.s_allMap.clear();
    }

    private static final class Tile {
        ArrayList<String> self = null;
        ArrayList<String> left = null;
        ArrayList<String> right = null;
        ArrayList<String> debrisN = null;
        ArrayList<String> debrisS = null;
        ArrayList<String> debrisW = null;
        ArrayList<String> debrisE = null;

        String pickRandom(ArrayList<String> arrayList) {
            return arrayList == null ? null : PZArrayUtil.pickRandom(arrayList);
        }
    }
}
