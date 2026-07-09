// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import java.util.HashMap;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.PersistentOutfits;
import zombie.Lua.LuaManager;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import zombie.core.Rand;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.network.GameServer;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;

public final class ZombiesZoneDefinition {
    private static final ArrayList<ZombiesZoneDefinition.ZZDZone> s_zoneList = new ArrayList<>();
    private static final HashMap<String, ZombiesZoneDefinition.ZZDZone> s_zoneMap = new HashMap<>();
    public static boolean bDirty = true;
    private static final ZombiesZoneDefinition.PickDefinition pickDef = new ZombiesZoneDefinition.PickDefinition();
    private static final HashMap<String, ZombiesZoneDefinition.ZZDOutfit> s_customOutfitMap = new HashMap<>();

    private static void checkDirty() {
        if (bDirty) {
            bDirty = false;
            init();
        }
    }

    private static void init() {
        s_zoneList.clear();
        s_zoneMap.clear();
        KahluaTableImpl kahluaTableImpl0 = Type.tryCastTo(LuaManager.env.rawget("ZombiesZoneDefinition"), KahluaTableImpl.class);
        if (kahluaTableImpl0 != null) {
            KahluaTableIterator kahluaTableIterator = kahluaTableImpl0.iterator();

            while (kahluaTableIterator.advance()) {
                KahluaTableImpl kahluaTableImpl1 = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
                if (kahluaTableImpl1 != null) {
                    ZombiesZoneDefinition.ZZDZone zZDZone = initZone(kahluaTableIterator.getKey().toString(), kahluaTableImpl1);
                    if (zZDZone != null) {
                        s_zoneList.add(zZDZone);
                        s_zoneMap.put(zZDZone.name, zZDZone);
                    }
                }
            }
        }
    }

    private static ZombiesZoneDefinition.ZZDZone initZone(String string, KahluaTableImpl kahluaTableImpl0) {
        ZombiesZoneDefinition.ZZDZone zZDZone = new ZombiesZoneDefinition.ZZDZone();
        zZDZone.name = string;
        zZDZone.femaleChance = kahluaTableImpl0.rawgetInt("femaleChance");
        zZDZone.maleChance = kahluaTableImpl0.rawgetInt("maleChance");
        zZDZone.chanceToSpawn = kahluaTableImpl0.rawgetInt("chanceToSpawn");
        zZDZone.toSpawn = kahluaTableImpl0.rawgetInt("toSpawn");
        KahluaTableIterator kahluaTableIterator = kahluaTableImpl0.iterator();

        while (kahluaTableIterator.advance()) {
            KahluaTableImpl kahluaTableImpl1 = Type.tryCastTo(kahluaTableIterator.getValue(), KahluaTableImpl.class);
            if (kahluaTableImpl1 != null) {
                ZombiesZoneDefinition.ZZDOutfit zZDOutfit = initOutfit(kahluaTableImpl1);
                if (zZDOutfit != null) {
                    zZDOutfit.customName = "ZZD." + zZDZone.name + "." + zZDOutfit.name;
                    zZDZone.outfits.add(zZDOutfit);
                }
            }
        }

        return zZDZone;
    }

    private static ZombiesZoneDefinition.ZZDOutfit initOutfit(KahluaTableImpl kahluaTableImpl) {
        ZombiesZoneDefinition.ZZDOutfit zZDOutfit = new ZombiesZoneDefinition.ZZDOutfit();
        zZDOutfit.name = kahluaTableImpl.rawgetStr("name");
        zZDOutfit.chance = kahluaTableImpl.rawgetFloat("chance");
        zZDOutfit.gender = kahluaTableImpl.rawgetStr("gender");
        zZDOutfit.toSpawn = kahluaTableImpl.rawgetInt("toSpawn");
        zZDOutfit.mandatory = kahluaTableImpl.rawgetStr("mandatory");
        zZDOutfit.room = kahluaTableImpl.rawgetStr("room");
        zZDOutfit.femaleHairStyles = initStringChance(kahluaTableImpl.rawgetStr("femaleHairStyles"));
        zZDOutfit.maleHairStyles = initStringChance(kahluaTableImpl.rawgetStr("maleHairStyles"));
        zZDOutfit.beardStyles = initStringChance(kahluaTableImpl.rawgetStr("beardStyles"));
        return zZDOutfit;
    }

    private static ArrayList<ZombiesZoneDefinition.StringChance> initStringChance(String string0) {
        if (StringUtils.isNullOrWhitespace(string0)) {
            return null;
        } else {
            ArrayList arrayList = new ArrayList();
            String[] strings0 = string0.split(";");

            for (String string1 : strings0) {
                String[] strings1 = string1.split(":");
                ZombiesZoneDefinition.StringChance stringChance = new ZombiesZoneDefinition.StringChance();
                stringChance.str = strings1[0];
                stringChance.chance = Float.parseFloat(strings1[1]);
                arrayList.add(stringChance);
            }

            return arrayList;
        }
    }

    public static void dressInRandomOutfit(IsoZombie zombie0) {
        if (!zombie0.isSkeleton()) {
            IsoGridSquare square = zombie0.getCurrentSquare();
            if (square != null) {
                ZombiesZoneDefinition.PickDefinition pickDefinition = pickDefinition(square.x, square.y, square.z, zombie0.isFemale());
                if (pickDefinition == null) {
                    String string = square.getRoom() == null ? null : square.getRoom().getName();
                    Outfit outfit = getRandomDefaultOutfit(zombie0.isFemale(), string);
                    zombie0.dressInPersistentOutfit(outfit.m_Name);
                    UnderwearDefinition.addRandomUnderwear(zombie0);
                } else {
                    applyDefinition(zombie0, pickDefinition.zone, pickDefinition.table, pickDefinition.bFemale);
                    UnderwearDefinition.addRandomUnderwear(zombie0);
                }
            }
        }
    }

    public static IsoMetaGrid.Zone getDefinitionZoneAt(int int0, int int1, int int2) {
        ArrayList arrayList = IsoWorld.instance.MetaGrid.getZonesAt(int0, int1, int2);

        for (int int3 = arrayList.size() - 1; int3 >= 0; int3--) {
            IsoMetaGrid.Zone zone = (IsoMetaGrid.Zone)arrayList.get(int3);
            if ("ZombiesType".equalsIgnoreCase(zone.type) || s_zoneMap.containsKey(zone.type)) {
                return zone;
            }
        }

        return null;
    }

    public static ZombiesZoneDefinition.PickDefinition pickDefinition(int int0, int int1, int int2, boolean boolean0) {
        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        if (square == null) {
            return null;
        } else {
            String string0 = square.getRoom() == null ? null : square.getRoom().getName();
            checkDirty();
            IsoMetaGrid.Zone zone = getDefinitionZoneAt(int0, int1, int2);
            if (zone == null) {
                return null;
            } else if (zone.spawnSpecialZombies == Boolean.FALSE) {
                return null;
            } else {
                String string1 = StringUtils.isNullOrEmpty(zone.name) ? zone.type : zone.name;
                ZombiesZoneDefinition.ZZDZone zZDZone = s_zoneMap.get(string1);
                if (zZDZone == null) {
                    return null;
                } else {
                    if (zZDZone.chanceToSpawn != -1) {
                        int int3 = zZDZone.chanceToSpawn;
                        int int4 = zZDZone.toSpawn;
                        ArrayList arrayList0 = IsoWorld.instance.getSpawnedZombieZone().get(zone.getName());
                        if (arrayList0 == null) {
                            arrayList0 = new ArrayList();
                            IsoWorld.instance.getSpawnedZombieZone().put(zone.getName(), arrayList0);
                        }

                        if (arrayList0.contains(zone.id)) {
                            zone.spawnSpecialZombies = true;
                        }

                        if (int4 == -1 || zone.spawnSpecialZombies == null && arrayList0.size() < int4) {
                            if (Rand.Next(100) < int3) {
                                zone.spawnSpecialZombies = true;
                                arrayList0.add(zone.id);
                            } else {
                                zone.spawnSpecialZombies = false;
                                zone = null;
                            }
                        }
                    }

                    if (zone == null) {
                        return null;
                    } else {
                        ArrayList arrayList1 = new ArrayList();
                        ArrayList arrayList2 = new ArrayList();
                        int int5 = zZDZone.maleChance;
                        int int6 = zZDZone.femaleChance;
                        if (int5 > 0 && Rand.Next(100) < int5) {
                            boolean0 = false;
                        }

                        if (int6 > 0 && Rand.Next(100) < int6) {
                            boolean0 = true;
                        }

                        for (int int7 = 0; int7 < zZDZone.outfits.size(); int7++) {
                            ZombiesZoneDefinition.ZZDOutfit zZDOutfit0 = zZDZone.outfits.get(int7);
                            String string2 = zZDOutfit0.gender;
                            String string3 = zZDOutfit0.room;
                            if ((string3 == null || string0 != null && string3.contains(string0))
                                && (!"male".equalsIgnoreCase(string2) || !boolean0)
                                && (!"female".equalsIgnoreCase(string2) || boolean0)) {
                                String string4 = zZDOutfit0.name;
                                boolean boolean1 = Boolean.parseBoolean(zZDOutfit0.mandatory);
                                if (boolean1) {
                                    Integer integer = zone.spawnedZombies.get(string4);
                                    if (integer == null) {
                                        integer = 0;
                                    }

                                    if (integer < zZDOutfit0.toSpawn) {
                                        arrayList1.add(zZDOutfit0);
                                    }
                                } else {
                                    arrayList2.add(zZDOutfit0);
                                }
                            }
                        }

                        ZombiesZoneDefinition.ZZDOutfit zZDOutfit1;
                        if (!arrayList1.isEmpty()) {
                            zZDOutfit1 = PZArrayUtil.pickRandom(arrayList1);
                        } else {
                            zZDOutfit1 = getRandomOutfitInSetList(arrayList2, true);
                        }

                        if (zZDOutfit1 == null) {
                            return null;
                        } else {
                            pickDef.table = zZDOutfit1;
                            pickDef.bFemale = boolean0;
                            pickDef.zone = zone;
                            return pickDef;
                        }
                    }
                }
            }
        }
    }

    public static void applyDefinition(IsoZombie zombie0, IsoMetaGrid.Zone zone, ZombiesZoneDefinition.ZZDOutfit zZDOutfit, boolean boolean0) {
        zombie0.setFemaleEtc(boolean0);
        Outfit outfit = null;
        if (!boolean0) {
            outfit = OutfitManager.instance.FindMaleOutfit(zZDOutfit.name);
        } else {
            outfit = OutfitManager.instance.FindFemaleOutfit(zZDOutfit.name);
        }

        String string = zZDOutfit.customName;
        if (outfit == null) {
            outfit = OutfitManager.instance.GetRandomOutfit(boolean0);
            string = outfit.m_Name;
        } else if (zone != null) {
            Integer integer = zone.spawnedZombies.get(outfit.m_Name);
            if (integer == null) {
                integer = 1;
            }

            zone.spawnedZombies.put(outfit.m_Name, integer + 1);
        }

        if (outfit != null) {
            zombie0.dressInPersistentOutfit(outfit.m_Name);
        }

        ModelManager.instance.ResetNextFrame(zombie0);
        zombie0.advancedAnimator.OnAnimDataChanged(false);
    }

    public static Outfit getRandomDefaultOutfit(boolean boolean0, String string2) {
        ArrayList arrayList = new ArrayList();
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("ZombiesZoneDefinition");
        ZombiesZoneDefinition.ZZDZone zZDZone = s_zoneMap.get("Default");

        for (int int0 = 0; int0 < zZDZone.outfits.size(); int0++) {
            ZombiesZoneDefinition.ZZDOutfit zZDOutfit0 = zZDZone.outfits.get(int0);
            String string0 = zZDOutfit0.gender;
            String string1 = zZDOutfit0.room;
            if ((string1 == null || string2 != null && string1.contains(string2))
                && (string0 == null || "male".equalsIgnoreCase(string0) && !boolean0 || "female".equalsIgnoreCase(string0) && boolean0)) {
                arrayList.add(zZDOutfit0);
            }
        }

        ZombiesZoneDefinition.ZZDOutfit zZDOutfit1 = getRandomOutfitInSetList(arrayList, false);
        Outfit outfit = null;
        if (zZDOutfit1 != null) {
            if (boolean0) {
                outfit = OutfitManager.instance.FindFemaleOutfit(zZDOutfit1.name);
            } else {
                outfit = OutfitManager.instance.FindMaleOutfit(zZDOutfit1.name);
            }
        }

        if (outfit == null) {
            outfit = OutfitManager.instance.GetRandomOutfit(boolean0);
        }

        return outfit;
    }

    public static ZombiesZoneDefinition.ZZDOutfit getRandomOutfitInSetList(ArrayList<ZombiesZoneDefinition.ZZDOutfit> arrayList, boolean boolean0) {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            ZombiesZoneDefinition.ZZDOutfit zZDOutfit0 = (ZombiesZoneDefinition.ZZDOutfit)arrayList.get(int0);
            float0 += zZDOutfit0.chance;
        }

        float float1 = Rand.Next(0.0F, 100.0F);
        if (!boolean0 || float0 > 100.0F) {
            float1 = Rand.Next(0.0F, float0);
        }

        float float2 = 0.0F;

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            ZombiesZoneDefinition.ZZDOutfit zZDOutfit1 = (ZombiesZoneDefinition.ZZDOutfit)arrayList.get(int1);
            float2 += zZDOutfit1.chance;
            if (float1 < float2) {
                return zZDOutfit1;
            }
        }

        return null;
    }

    private static String getRandomHairOrBeard(ArrayList<ZombiesZoneDefinition.StringChance> arrayList) {
        float float0 = OutfitRNG.Next(0.0F, 100.0F);
        float float1 = 0.0F;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            ZombiesZoneDefinition.StringChance stringChance = (ZombiesZoneDefinition.StringChance)arrayList.get(int0);
            float1 += stringChance.chance;
            if (float0 < float1) {
                if ("null".equalsIgnoreCase(stringChance.str)) {
                    return "";
                }

                return stringChance.str;
            }
        }

        return null;
    }

    public static void registerCustomOutfits() {
        checkDirty();
        s_customOutfitMap.clear();

        for (ZombiesZoneDefinition.ZZDZone zZDZone : s_zoneList) {
            for (ZombiesZoneDefinition.ZZDOutfit zZDOutfit : zZDZone.outfits) {
                PersistentOutfits.instance.registerOutfitter(zZDOutfit.customName, true, ZombiesZoneDefinition::ApplyCustomOutfit);
                s_customOutfitMap.put(zZDOutfit.customName, zZDOutfit);
            }
        }
    }

    private static void ApplyCustomOutfit(int int0, String string, IsoGameCharacter character) {
        ZombiesZoneDefinition.ZZDOutfit zZDOutfit = s_customOutfitMap.get(string);
        boolean boolean0 = (int0 & -2147483648) != 0;
        IsoZombie zombie0 = Type.tryCastTo(character, IsoZombie.class);
        if (zombie0 != null) {
            zombie0.setFemaleEtc(boolean0);
        }

        character.dressInNamedOutfit(zZDOutfit.name);
        if (zombie0 == null) {
            PersistentOutfits.instance.removeFallenHat(int0, character);
        } else {
            AttachedWeaponDefinitions.instance.addRandomAttachedWeapon(zombie0);
            zombie0.addRandomBloodDirtHolesEtc();
            boolean boolean1 = character.isFemale();
            if (boolean1 && zZDOutfit.femaleHairStyles != null) {
                zombie0.getHumanVisual().setHairModel(getRandomHairOrBeard(zZDOutfit.femaleHairStyles));
            }

            if (!boolean1 && zZDOutfit.maleHairStyles != null) {
                zombie0.getHumanVisual().setHairModel(getRandomHairOrBeard(zZDOutfit.maleHairStyles));
            }

            if (!boolean1 && zZDOutfit.beardStyles != null) {
                zombie0.getHumanVisual().setBeardModel(getRandomHairOrBeard(zZDOutfit.beardStyles));
            }

            PersistentOutfits.instance.removeFallenHat(int0, character);
        }
    }

    public static int pickPersistentOutfit(IsoGridSquare square) {
        if (!GameServer.bServer) {
            return 0;
        } else {
            boolean boolean0 = Rand.Next(2) == 0;
            ZombiesZoneDefinition.PickDefinition pickDefinition = pickDefinition(square.x, square.y, square.z, boolean0);
            Outfit outfit;
            if (pickDefinition == null) {
                String string0 = square.getRoom() == null ? null : square.getRoom().getName();
                outfit = getRandomDefaultOutfit(boolean0, string0);
            } else {
                boolean0 = pickDefinition.bFemale;
                String string1 = pickDefinition.table.name;
                if (boolean0) {
                    outfit = OutfitManager.instance.FindFemaleOutfit(string1);
                } else {
                    outfit = OutfitManager.instance.FindMaleOutfit(string1);
                }
            }

            if (outfit == null) {
                boolean boolean1 = true;
            } else {
                int int0 = PersistentOutfits.instance.pickOutfit(outfit.m_Name, boolean0);
                if (int0 != 0) {
                    return int0;
                }

                boolean boolean2 = true;
            }

            return 0;
        }
    }

    public static final class PickDefinition {
        IsoMetaGrid.Zone zone;
        ZombiesZoneDefinition.ZZDOutfit table;
        boolean bFemale;
    }

    private static final class StringChance {
        String str;
        float chance;
    }

    private static final class ZZDOutfit {
        String name;
        String customName;
        float chance;
        int toSpawn;
        String gender;
        String mandatory;
        String room;
        ArrayList<ZombiesZoneDefinition.StringChance> femaleHairStyles;
        ArrayList<ZombiesZoneDefinition.StringChance> maleHairStyles;
        ArrayList<ZombiesZoneDefinition.StringChance> beardStyles;
    }

    private static final class ZZDZone {
        String name;
        int femaleChance;
        int maleChance;
        int chanceToSpawn;
        int toSpawn;
        final ArrayList<ZombiesZoneDefinition.ZZDOutfit> outfits = new ArrayList<>();
    }
}
