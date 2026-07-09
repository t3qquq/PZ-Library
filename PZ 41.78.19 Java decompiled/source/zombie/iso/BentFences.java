// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.MapCollisionData;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.vehicles.PolygonalMap2;

public class BentFences {
    private static final BentFences instance = new BentFences();
    private final ArrayList<BentFences.Entry> m_entries = new ArrayList<>();
    private final HashMap<String, ArrayList<BentFences.Entry>> m_bentMap = new HashMap<>();
    private final HashMap<String, ArrayList<BentFences.Entry>> m_unbentMap = new HashMap<>();

    public static BentFences getInstance() {
        return instance;
    }

    private void tableToTiles(KahluaTableImpl kahluaTableImpl, ArrayList<String> arrayList) {
        if (kahluaTableImpl != null) {
            KahluaTableIterator kahluaTableIterator = kahluaTableImpl.iterator();

            while (kahluaTableIterator.advance()) {
                arrayList.add(kahluaTableIterator.getValue().toString());
            }
        }
    }

    private void tableToTiles(KahluaTable table, ArrayList<String> arrayList, String string) {
        this.tableToTiles((KahluaTableImpl)table.rawget(string), arrayList);
    }

    public void addFenceTiles(int VERSION, KahluaTableImpl tiles) {
        KahluaTableIterator kahluaTableIterator = tiles.iterator();

        while (kahluaTableIterator.advance()) {
            KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)kahluaTableIterator.getValue();
            BentFences.Entry entry = new BentFences.Entry();
            entry.dir = IsoDirections.valueOf(kahluaTableImpl.rawgetStr("dir"));
            this.tableToTiles(kahluaTableImpl, entry.unbent, "unbent");
            this.tableToTiles(kahluaTableImpl, entry.bent, "bent");
            if (!entry.unbent.isEmpty() && entry.unbent.size() == entry.bent.size()) {
                this.m_entries.add(entry);

                for (String string0 : entry.unbent) {
                    ArrayList arrayList0 = this.m_unbentMap.get(string0);
                    if (arrayList0 == null) {
                        arrayList0 = new ArrayList();
                        this.m_unbentMap.put(string0, arrayList0);
                    }

                    arrayList0.add(entry);
                }

                for (String string1 : entry.bent) {
                    ArrayList arrayList1 = this.m_bentMap.get(string1);
                    if (arrayList1 == null) {
                        arrayList1 = new ArrayList();
                        this.m_bentMap.put(string1, arrayList1);
                    }

                    arrayList1.add(entry);
                }
            }
        }
    }

    public boolean isBentObject(IsoObject obj) {
        return this.getEntryForObject(obj, null) != null;
    }

    public boolean isUnbentObject(IsoObject obj) {
        return this.getEntryForObject(obj, IsoDirections.Max) != null;
    }

    private BentFences.Entry getEntryForObject(IsoObject object, IsoDirections directions) {
        if (object != null && object.sprite != null && object.sprite.name != null) {
            boolean boolean0 = directions != null;
            ArrayList arrayList = boolean0 ? this.m_unbentMap.get(object.sprite.name) : this.m_bentMap.get(object.sprite.name);
            if (arrayList != null) {
                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    BentFences.Entry entry = (BentFences.Entry)arrayList.get(int0);
                    if ((!boolean0 || directions == IsoDirections.Max || directions == entry.dir) && this.isValidObject(object, entry, boolean0)) {
                        return entry;
                    }
                }
            }

            return null;
        } else {
            return null;
        }
    }

    private boolean isValidObject(IsoObject object, BentFences.Entry entry, boolean boolean0) {
        IsoCell cell = IsoWorld.instance.CurrentCell;
        ArrayList arrayList = boolean0 ? entry.unbent : entry.bent;
        int int0 = ((String)arrayList.get(2)).equals(object.sprite.name) ? 2 : (((String)arrayList.get(3)).equals(object.sprite.name) ? 3 : -1);
        if (int0 == -1) {
            return false;
        } else {
            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                int int2 = object.square.x + (entry.isNorth() ? int1 - int0 : 0);
                int int3 = object.square.y + (entry.isNorth() ? 0 : int1 - int0);
                IsoGridSquare square = cell.getGridSquare(int2, int3, object.square.z);
                if (square == null) {
                    return false;
                }

                if (int0 != int1 && this.getObjectForEntry(square, arrayList, int1) == null) {
                    return false;
                }
            }

            return true;
        }
    }

    IsoObject getObjectForEntry(IsoGridSquare square, ArrayList<String> arrayList, int int1) {
        for (int int0 = 0; int0 < square.getObjects().size(); int0++) {
            IsoObject object = square.getObjects().get(int0);
            if (object.sprite != null && object.sprite.name != null && ((String)arrayList.get(int1)).equals(object.sprite.name)) {
                return object;
            }
        }

        return null;
    }

    public void swapTiles(IsoObject obj, IsoDirections dir) {
        boolean boolean0 = dir != null;
        BentFences.Entry entry = this.getEntryForObject(obj, dir);
        if (entry != null) {
            if (boolean0) {
                if (entry.isNorth() && dir != IsoDirections.N && dir != IsoDirections.S) {
                    return;
                }

                if (!entry.isNorth() && dir != IsoDirections.W && dir != IsoDirections.E) {
                    return;
                }
            }

            IsoCell cell = IsoWorld.instance.CurrentCell;
            ArrayList arrayList = boolean0 ? entry.unbent : entry.bent;
            int int0 = ((String)arrayList.get(2)).equals(obj.sprite.name) ? 2 : (((String)arrayList.get(3)).equals(obj.sprite.name) ? 3 : -1);

            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                int int2 = obj.square.x + (entry.isNorth() ? int1 - int0 : 0);
                int int3 = obj.square.y + (entry.isNorth() ? 0 : int1 - int0);
                IsoGridSquare square = cell.getGridSquare(int2, int3, obj.square.z);
                if (square != null) {
                    IsoObject object = this.getObjectForEntry(square, arrayList, int1);
                    if (object != null) {
                        String string = boolean0 ? entry.bent.get(int1) : entry.unbent.get(int1);
                        IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
                        sprite.name = string;
                        object.setSprite(sprite);
                        object.transmitUpdatedSprite();
                        square.RecalcAllWithNeighbours(true);
                        MapCollisionData.instance.squareChanged(square);
                        PolygonalMap2.instance.squareChanged(square);
                        IsoRegions.squareChanged(square);
                    }
                }
            }
        }
    }

    public void bendFence(IsoObject obj, IsoDirections dir) {
        this.swapTiles(obj, dir);
    }

    public void unbendFence(IsoObject obj) {
        this.swapTiles(obj, null);
    }

    public void Reset() {
        this.m_entries.clear();
        this.m_bentMap.clear();
        this.m_unbentMap.clear();
    }

    private static final class Entry {
        IsoDirections dir = IsoDirections.Max;
        final ArrayList<String> unbent = new ArrayList<>();
        final ArrayList<String> bent = new ArrayList<>();

        boolean isNorth() {
            return this.dir == IsoDirections.N || this.dir == IsoDirections.S;
        }
    }
}
