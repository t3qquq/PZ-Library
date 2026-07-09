// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.properties;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Collections.NonBlockingHashMap;
import zombie.iso.SpriteDetails.IsoFlagType;

public final class PropertyContainer {
    private long SpriteFlags1 = 0L;
    private long SpriteFlags2 = 0L;
    private final TIntIntHashMap Properties = new TIntIntHashMap();
    private int[] keyArray;
    public static NonBlockingHashMap<IsoFlagType, PropertyContainer.MostTested> test = new NonBlockingHashMap<>();
    public static List<Object> sorted = Collections.synchronizedList(new ArrayList<>());
    private byte Surface;
    private byte SurfaceFlags;
    private short StackReplaceTileOffset;
    private byte ItemHeight;
    private static final byte SURFACE_VALID = 1;
    private static final byte SURFACE_ISOFFSET = 2;
    private static final byte SURFACE_ISTABLE = 4;
    private static final byte SURFACE_ISTABLETOP = 8;

    public void CreateKeySet() {
        TIntSet tIntSet = this.Properties.keySet();
        this.keyArray = tIntSet.toArray();
    }

    public void AddProperties(PropertyContainer other) {
        if (other.keyArray != null) {
            for (int int0 = 0; int0 < other.keyArray.length; int0++) {
                int int1 = other.keyArray[int0];
                this.Properties.put(int1, other.Properties.get(int1));
            }

            this.SpriteFlags1 = this.SpriteFlags1 | other.SpriteFlags1;
            this.SpriteFlags2 = this.SpriteFlags2 | other.SpriteFlags2;
        }
    }

    public void Clear() {
        this.SpriteFlags1 = 0L;
        this.SpriteFlags2 = 0L;
        this.Properties.clear();
        this.SurfaceFlags &= -2;
    }

    public boolean Is(IsoFlagType flag) {
        long long0 = flag.index() / 64 == 0 ? this.SpriteFlags1 : this.SpriteFlags2;
        return (long0 & 1L << flag.index() % 64) != 0L;
    }

    public boolean Is(Double flag) {
        return this.Is(IsoFlagType.fromIndex(flag.intValue()));
    }

    public void Set(String propName, String propName2) {
        this.Set(propName, propName2, true);
    }

    public void Set(String propName, String propName2, boolean checkIsoFlagType) {
        if (propName != null) {
            if (checkIsoFlagType) {
                IsoFlagType flagType = IsoFlagType.FromString(propName);
                if (flagType != IsoFlagType.MAX) {
                    this.Set(flagType);
                    return;
                }
            }

            int int0 = TilePropertyAliasMap.instance.getIDFromPropertyName(propName);
            if (int0 != -1) {
                int int1 = TilePropertyAliasMap.instance.getIDFromPropertyValue(int0, propName2);
                this.SurfaceFlags &= -2;
                this.Properties.put(int0, int1);
            }
        }
    }

    public void Set(IsoFlagType flag) {
        if (flag.index() / 64 == 0) {
            this.SpriteFlags1 = this.SpriteFlags1 | 1L << flag.index() % 64;
        } else {
            this.SpriteFlags2 = this.SpriteFlags2 | 1L << flag.index() % 64;
        }
    }

    public void Set(IsoFlagType flag, String ignored) {
        this.Set(flag);
    }

    public void UnSet(String propName) {
        int int0 = TilePropertyAliasMap.instance.getIDFromPropertyName(propName);
        this.Properties.remove(int0);
    }

    public void UnSet(IsoFlagType flag) {
        if (flag.index() / 64 == 0) {
            this.SpriteFlags1 = this.SpriteFlags1 & ~(1L << flag.index() % 64);
        } else {
            this.SpriteFlags2 = this.SpriteFlags2 & ~(1L << flag.index() % 64);
        }
    }

    public String Val(String property) {
        int int0 = TilePropertyAliasMap.instance.getIDFromPropertyName(property);
        return !this.Properties.containsKey(int0) ? null : TilePropertyAliasMap.instance.getPropertyValueString(int0, this.Properties.get(int0));
    }

    public boolean Is(String isoPropertyType) {
        int int0 = TilePropertyAliasMap.instance.getIDFromPropertyName(isoPropertyType);
        return this.Properties.containsKey(int0);
    }

    public ArrayList<IsoFlagType> getFlagsList() {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < 64; int0++) {
            if ((this.SpriteFlags1 & 1L << int0) != 0L) {
                arrayList.add(IsoFlagType.fromIndex(int0));
            }
        }

        for (int int1 = 0; int1 < 64; int1++) {
            if ((this.SpriteFlags2 & 1L << int1) != 0L) {
                arrayList.add(IsoFlagType.fromIndex(64 + int1));
            }
        }

        return arrayList;
    }

    public ArrayList<String> getPropertyNames() {
        ArrayList arrayList = new ArrayList();
        TIntSet tIntSet = this.Properties.keySet();
        tIntSet.forEach(int0 -> {
            arrayList.add(TilePropertyAliasMap.instance.Properties.get(int0).propertyName);
            return true;
        });
        Collections.sort(arrayList);
        return arrayList;
    }

    private void initSurface() {
        if ((this.SurfaceFlags & 1) == 0) {
            this.Surface = 0;
            this.StackReplaceTileOffset = 0;
            this.SurfaceFlags = 1;
            this.ItemHeight = 0;
            this.Properties.forEachEntry((int0, int1) -> {
                TilePropertyAliasMap.TileProperty tileProperty = TilePropertyAliasMap.instance.Properties.get(int0);
                String string0 = tileProperty.propertyName;
                String string1 = tileProperty.possibleValues.get(int1);
                if ("Surface".equals(string0) && string1 != null) {
                    try {
                        int int2 = Integer.parseInt(string1);
                        if (int2 >= 0 && int2 <= 127) {
                            this.Surface = (byte)int2;
                        }
                    } catch (NumberFormatException numberFormatException0) {
                    }
                } else if ("IsSurfaceOffset".equals(string0)) {
                    this.SurfaceFlags = (byte)(this.SurfaceFlags | 2);
                } else if ("IsTable".equals(string0)) {
                    this.SurfaceFlags = (byte)(this.SurfaceFlags | 4);
                } else if ("IsTableTop".equals(string0)) {
                    this.SurfaceFlags = (byte)(this.SurfaceFlags | 8);
                } else if ("StackReplaceTileOffset".equals(string0)) {
                    try {
                        this.StackReplaceTileOffset = (short)Integer.parseInt(string1);
                    } catch (NumberFormatException numberFormatException1) {
                    }
                } else if ("ItemHeight".equals(string0)) {
                    try {
                        int int3 = Integer.parseInt(string1);
                        if (int3 >= 0 && int3 <= 127) {
                            this.ItemHeight = (byte)int3;
                        }
                    } catch (NumberFormatException numberFormatException2) {
                    }
                }

                return true;
            });
        }
    }

    public int getSurface() {
        this.initSurface();
        return this.Surface;
    }

    public boolean isSurfaceOffset() {
        this.initSurface();
        return (this.SurfaceFlags & 2) != 0;
    }

    public boolean isTable() {
        this.initSurface();
        return (this.SurfaceFlags & 4) != 0;
    }

    public boolean isTableTop() {
        this.initSurface();
        return (this.SurfaceFlags & 8) != 0;
    }

    public int getStackReplaceTileOffset() {
        this.initSurface();
        return this.StackReplaceTileOffset;
    }

    public int getItemHeight() {
        this.initSurface();
        return this.ItemHeight;
    }

    public static class MostTested {
        public IsoFlagType flag;
        public int count;
    }

    private static class ProfileEntryComparitor implements Comparator<Object> {
        public ProfileEntryComparitor() {
        }

        @Override
        public int compare(Object object0, Object object1) {
            double double0 = ((PropertyContainer.MostTested)object0).count;
            double double1 = ((PropertyContainer.MostTested)object1).count;
            if (double0 > double1) {
                return -1;
            } else {
                return double1 > double0 ? 1 : 0;
            }
        }
    }
}
