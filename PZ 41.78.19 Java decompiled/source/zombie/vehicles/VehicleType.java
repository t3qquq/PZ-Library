// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.Lua.LuaManager;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.VehicleScript;
import zombie.util.list.PZArrayUtil;

/**
 * Contains all car model with their associated skin index
 */
public final class VehicleType {
    public final ArrayList<VehicleType.VehicleTypeDefinition> vehiclesDefinition = new ArrayList<>();
    public int chanceToSpawnNormal = 80;
    public int chanceToSpawnBurnt = 0;
    public int spawnRate = 16;
    public int chanceOfOverCar = 0;
    public boolean randomAngle = false;
    public float baseVehicleQuality = 1.0F;
    public String name = "";
    private int chanceToSpawnKey = 70;
    public int chanceToPartDamage = 0;
    public boolean isSpecialCar = false;
    public boolean isBurntCar = false;
    public int chanceToSpawnSpecial = 5;
    public static final HashMap<String, VehicleType> vehicles = new HashMap<>();
    public static final ArrayList<VehicleType> specialVehicles = new ArrayList<>();

    public VehicleType(String _name) {
        this.name = _name;
    }

    public static void init() {
        initNormal();
        validate(vehicles.values());
        validate(specialVehicles);
    }

    private static void validate(Collection<VehicleType> var0) {
    }

    private static void initNormal() {
        boolean boolean0 = DebugLog.isEnabled(DebugType.Lua);
        KahluaTableImpl kahluaTableImpl0 = (KahluaTableImpl)LuaManager.env.rawget("VehicleZoneDistribution");

        for (Entry entry0 : kahluaTableImpl0.delegate.entrySet()) {
            String string0 = entry0.getKey().toString();
            VehicleType vehicleType0 = new VehicleType(string0);
            ArrayList arrayList = vehicleType0.vehiclesDefinition;
            KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)entry0.getValue();
            KahluaTableImpl kahluaTableImpl2 = (KahluaTableImpl)kahluaTableImpl1.rawget("vehicles");

            for (Entry entry1 : kahluaTableImpl2.delegate.entrySet()) {
                String string1 = entry1.getKey().toString();
                VehicleScript vehicleScript0 = ScriptManager.instance.getVehicle(string1);
                if (vehicleScript0 == null) {
                    DebugLog.General.warn("vehicle type \"" + string1 + "\" doesn't exist");
                }

                KahluaTableImpl kahluaTableImpl3 = (KahluaTableImpl)entry1.getValue();
                arrayList.add(new VehicleType.VehicleTypeDefinition(string1, kahluaTableImpl3.rawgetInt("index"), kahluaTableImpl3.rawgetFloat("spawnChance")));
            }

            float float0 = 0.0F;

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                float0 += ((VehicleType.VehicleTypeDefinition)arrayList.get(int0)).spawnChance;
            }

            float0 = 100.0F / float0;
            if (boolean0) {
                DebugLog.Lua.println("Vehicle spawn rate:");
            }

            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                ((VehicleType.VehicleTypeDefinition)arrayList.get(int1)).spawnChance *= float0;
                if (boolean0) {
                    DebugLog.Lua
                        .println(
                            string0
                                + ": "
                                + ((VehicleType.VehicleTypeDefinition)arrayList.get(int1)).vehicleType
                                + " "
                                + ((VehicleType.VehicleTypeDefinition)arrayList.get(int1)).spawnChance
                                + "%"
                        );
                }
            }

            if (kahluaTableImpl1.delegate.containsKey("chanceToPartDamage")) {
                vehicleType0.chanceToPartDamage = kahluaTableImpl1.rawgetInt("chanceToPartDamage");
            }

            if (kahluaTableImpl1.delegate.containsKey("chanceToSpawnNormal")) {
                vehicleType0.chanceToSpawnNormal = kahluaTableImpl1.rawgetInt("chanceToSpawnNormal");
            }

            if (kahluaTableImpl1.delegate.containsKey("chanceToSpawnSpecial")) {
                vehicleType0.chanceToSpawnSpecial = kahluaTableImpl1.rawgetInt("chanceToSpawnSpecial");
            }

            if (kahluaTableImpl1.delegate.containsKey("specialCar")) {
                vehicleType0.isSpecialCar = kahluaTableImpl1.rawgetBool("specialCar");
            }

            if (kahluaTableImpl1.delegate.containsKey("burntCar")) {
                vehicleType0.isBurntCar = kahluaTableImpl1.rawgetBool("burntCar");
            }

            if (kahluaTableImpl1.delegate.containsKey("baseVehicleQuality")) {
                vehicleType0.baseVehicleQuality = kahluaTableImpl1.rawgetFloat("baseVehicleQuality");
            }

            if (kahluaTableImpl1.delegate.containsKey("chanceOfOverCar")) {
                vehicleType0.chanceOfOverCar = kahluaTableImpl1.rawgetInt("chanceOfOverCar");
            }

            if (kahluaTableImpl1.delegate.containsKey("randomAngle")) {
                vehicleType0.randomAngle = kahluaTableImpl1.rawgetBool("randomAngle");
            }

            if (kahluaTableImpl1.delegate.containsKey("spawnRate")) {
                vehicleType0.spawnRate = kahluaTableImpl1.rawgetInt("spawnRate");
            }

            if (kahluaTableImpl1.delegate.containsKey("chanceToSpawnKey")) {
                vehicleType0.chanceToSpawnKey = kahluaTableImpl1.rawgetInt("chanceToSpawnKey");
            }

            if (kahluaTableImpl1.delegate.containsKey("chanceToSpawnBurnt")) {
                vehicleType0.chanceToSpawnBurnt = kahluaTableImpl1.rawgetInt("chanceToSpawnBurnt");
            }

            vehicles.put(string0, vehicleType0);
            if (vehicleType0.isSpecialCar) {
                specialVehicles.add(vehicleType0);
            }
        }

        HashSet hashSet = new HashSet();

        for (VehicleType vehicleType1 : vehicles.values()) {
            for (VehicleType.VehicleTypeDefinition vehicleTypeDefinition : vehicleType1.vehiclesDefinition) {
                hashSet.add(vehicleTypeDefinition.vehicleType);
            }
        }

        for (VehicleScript vehicleScript1 : ScriptManager.instance.getAllVehicleScripts()) {
            if (!hashSet.contains(vehicleScript1.getFullName())) {
                DebugLog.General.warn("vehicle type \"" + vehicleScript1.getFullName() + "\" isn't in VehicleZoneDistribution");
            }
        }
    }

    public static boolean hasTypeForZone(String zoneName) {
        if (vehicles.isEmpty()) {
            init();
        }

        zoneName = zoneName.toLowerCase();
        return vehicles.containsKey(zoneName);
    }

    public static VehicleType getRandomVehicleType(String zoneName) {
        return getRandomVehicleType(zoneName, true);
    }

    public static VehicleType getRandomVehicleType(String zoneName, Boolean doNormalWhenSpecific) {
        if (vehicles.isEmpty()) {
            init();
        }

        zoneName = zoneName.toLowerCase();
        VehicleType vehicleType = vehicles.get(zoneName);
        if (vehicleType == null) {
            DebugLog.log(zoneName + " Don't exist in VehicleZoneDistribution");
            return null;
        } else if (Rand.Next(100) < vehicleType.chanceToSpawnBurnt) {
            if (Rand.Next(100) < 80) {
                vehicleType = vehicles.get("normalburnt");
            } else {
                vehicleType = vehicles.get("specialburnt");
            }

            return vehicleType;
        } else {
            if (doNormalWhenSpecific && vehicleType.isSpecialCar && Rand.Next(100) < vehicleType.chanceToSpawnNormal) {
                vehicleType = vehicles.get("parkingstall");
            }

            if (!vehicleType.isBurntCar && !vehicleType.isSpecialCar && Rand.Next(100) < vehicleType.chanceToSpawnSpecial) {
                vehicleType = PZArrayUtil.pickRandom(specialVehicles);
            }

            if (vehicleType.isBurntCar) {
                if (Rand.Next(100) < 80) {
                    vehicleType = vehicles.get("normalburnt");
                } else {
                    vehicleType = vehicles.get("specialburnt");
                }
            }

            return vehicleType;
        }
    }

    public static VehicleType getTypeFromName(String _name) {
        if (vehicles.isEmpty()) {
            init();
        }

        return vehicles.get(_name);
    }

    public float getBaseVehicleQuality() {
        return this.baseVehicleQuality;
    }

    public float getRandomBaseVehicleQuality() {
        return Rand.Next(this.baseVehicleQuality - 0.1F, this.baseVehicleQuality + 0.1F);
    }

    public int getChanceToSpawnKey() {
        return this.chanceToSpawnKey;
    }

    public void setChanceToSpawnKey(int _chanceToSpawnKey) {
        this.chanceToSpawnKey = _chanceToSpawnKey;
    }

    public static void Reset() {
        vehicles.clear();
        specialVehicles.clear();
    }

    public static class VehicleTypeDefinition {
        public String vehicleType;
        public int index = -1;
        public float spawnChance = 0.0F;

        public VehicleTypeDefinition(String string, int int0, float float0) {
            this.vehicleType = string;
            this.index = int0;
            this.spawnChance = float0;
        }
    }
}
