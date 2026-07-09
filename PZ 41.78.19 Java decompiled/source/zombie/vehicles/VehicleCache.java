// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import java.util.LinkedList;
import java.util.List;

public final class VehicleCache {
    public short id;
    float x;
    float y;
    float z;
    private static TShortObjectHashMap<VehicleCache> mapId = new TShortObjectHashMap<>();
    private static TIntObjectHashMap<List<VehicleCache>> mapXY = new TIntObjectHashMap<>();

    public static void vehicleUpdate(short short0, float float0, float float1, float float2) {
        VehicleCache vehicleCache0 = mapId.get(short0);
        if (vehicleCache0 != null) {
            int int0 = (int)(vehicleCache0.x / 10.0F);
            int int1 = (int)(vehicleCache0.y / 10.0F);
            int int2 = (int)(float0 / 10.0F);
            int int3 = (int)(float1 / 10.0F);
            if (int0 != int2 || int1 != int3) {
                mapXY.get(int0 * 65536 + int1).remove(vehicleCache0);
                if (mapXY.get(int2 * 65536 + int3) == null) {
                    mapXY.put(int2 * 65536 + int3, new LinkedList<>());
                }

                mapXY.get(int2 * 65536 + int3).add(vehicleCache0);
            }

            vehicleCache0.x = float0;
            vehicleCache0.y = float1;
            vehicleCache0.z = float2;
        } else {
            VehicleCache vehicleCache1 = new VehicleCache();
            vehicleCache1.id = short0;
            vehicleCache1.x = float0;
            vehicleCache1.y = float1;
            vehicleCache1.z = float2;
            mapId.put(short0, vehicleCache1);
            int int4 = (int)(float0 / 10.0F);
            int int5 = (int)(float1 / 10.0F);
            if (mapXY.get(int4 * 65536 + int5) == null) {
                mapXY.put(int4 * 65536 + int5, new LinkedList<>());
            }

            mapXY.get(int4 * 65536 + int5).add(vehicleCache1);
        }
    }

    public static List<VehicleCache> vehicleGet(float float0, float float1) {
        int int0 = (int)(float0 / 10.0F);
        int int1 = (int)(float1 / 10.0F);
        return mapXY.get(int0 * 65536 + int1);
    }

    public static List<VehicleCache> vehicleGet(int int1, int int0) {
        return mapXY.get(int1 * 65536 + int0);
    }

    public static void remove(short short0) {
        VehicleCache vehicleCache = mapId.get(short0);
        if (vehicleCache != null) {
            mapId.remove(short0);
            int int0 = (int)(vehicleCache.x / 10.0F);
            int int1 = (int)(vehicleCache.y / 10.0F);
            int int2 = int0 * 65536 + int1;

            assert mapXY.containsKey(int2);

            assert mapXY.get(int2).contains(vehicleCache);

            mapXY.get(int2).remove(vehicleCache);
        }
    }

    public static void Reset() {
        mapId.clear();
        mapXY.clear();
    }

    static {
        mapId.setAutoCompactionFactor(0.0F);
        mapXY.setAutoCompactionFactor(0.0F);
    }
}
