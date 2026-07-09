// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.debug.DebugLog;

public final class ZomboidGlobals {
    public static double RunningEnduranceReduce = 0.0;
    public static double SprintingEnduranceReduce = 0.0;
    public static double ImobileEnduranceReduce = 0.0;
    public static double SittingEnduranceMultiplier = 5.0;
    public static double ThirstIncrease = 0.0;
    public static double ThirstSleepingIncrease = 0.0;
    public static double ThirstLevelToAutoDrink = 0.0;
    public static double ThirstLevelReductionOnAutoDrink = 0.0;
    public static double HungerIncrease = 0.0;
    public static double HungerIncreaseWhenWellFed = 0.0;
    public static double HungerIncreaseWhileAsleep = 0.0;
    public static double HungerIncreaseWhenExercise = 0.0;
    public static double FatigueIncrease = 0.0;
    public static double StressReduction = 0.0;
    public static double BoredomIncreaseRate = 0.0;
    public static double BoredomDecreaseRate = 0.0;
    public static double UnhappinessIncrease = 0.0;
    public static double StressFromSoundsMultiplier = 0.0;
    public static double StressFromBiteOrScratch = 0.0;
    public static double StressFromHemophobic = 0.0;
    public static double AngerDecrease = 0.0;
    public static double BroodingAngerDecreaseMultiplier = 0.0;
    public static double SleepFatigueReduction = 0.0;
    public static double WetnessIncrease = 0.0;
    public static double WetnessDecrease = 0.0;
    public static double CatchAColdIncreaseRate = 0.0;
    public static double CatchAColdDecreaseRate = 0.0;
    public static double PoisonLevelDecrease = 0.0;
    public static double PoisonHealthReduction = 0.0;
    public static double FoodSicknessDecrease = 0.0;

    public static void Load() {
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("ZomboidGlobals");
        SprintingEnduranceReduce = (Double)table.rawget("SprintingEnduranceReduce");
        RunningEnduranceReduce = (Double)table.rawget("RunningEnduranceReduce");
        ImobileEnduranceReduce = (Double)table.rawget("ImobileEnduranceIncrease");
        ThirstIncrease = (Double)table.rawget("ThirstIncrease");
        ThirstSleepingIncrease = (Double)table.rawget("ThirstSleepingIncrease");
        ThirstLevelToAutoDrink = (Double)table.rawget("ThirstLevelToAutoDrink");
        ThirstLevelReductionOnAutoDrink = (Double)table.rawget("ThirstLevelReductionOnAutoDrink");
        HungerIncrease = (Double)table.rawget("HungerIncrease");
        HungerIncreaseWhenWellFed = (Double)table.rawget("HungerIncreaseWhenWellFed");
        HungerIncreaseWhileAsleep = (Double)table.rawget("HungerIncreaseWhileAsleep");
        HungerIncreaseWhenExercise = (Double)table.rawget("HungerIncreaseWhenExercise");
        FatigueIncrease = (Double)table.rawget("FatigueIncrease");
        StressReduction = (Double)table.rawget("StressDecrease");
        BoredomIncreaseRate = (Double)table.rawget("BoredomIncrease");
        BoredomDecreaseRate = (Double)table.rawget("BoredomDecrease");
        UnhappinessIncrease = (Double)table.rawget("UnhappinessIncrease");
        StressFromSoundsMultiplier = (Double)table.rawget("StressFromSoundsMultiplier");
        StressFromBiteOrScratch = (Double)table.rawget("StressFromBiteOrScratch");
        StressFromHemophobic = (Double)table.rawget("StressFromHemophobic");
        AngerDecrease = (Double)table.rawget("AngerDecrease");
        BroodingAngerDecreaseMultiplier = (Double)table.rawget("BroodingAngerDecreaseMultiplier");
        SleepFatigueReduction = (Double)table.rawget("SleepFatigueReduction");
        WetnessIncrease = (Double)table.rawget("WetnessIncrease");
        WetnessDecrease = (Double)table.rawget("WetnessDecrease");
        CatchAColdIncreaseRate = (Double)table.rawget("CatchAColdIncreaseRate");
        CatchAColdDecreaseRate = (Double)table.rawget("CatchAColdDecreaseRate");
        PoisonLevelDecrease = (Double)table.rawget("PoisonLevelDecrease");
        PoisonHealthReduction = (Double)table.rawget("PoisonHealthReduction");
        FoodSicknessDecrease = (Double)table.rawget("FoodSicknessDecrease");
    }

    public static void toLua() {
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("ZomboidGlobals");
        if (table == null) {
            DebugLog.log("ERROR: ZomboidGlobals table undefined in Lua");
        } else {
            double double0 = 1.0;
            if (SandboxOptions.instance.getFoodLootModifier() == 1) {
                double0 = 0.0;
            } else if (SandboxOptions.instance.getFoodLootModifier() == 2) {
                double0 = 0.05;
            } else if (SandboxOptions.instance.getFoodLootModifier() == 3) {
                double0 = 0.2;
            } else if (SandboxOptions.instance.getFoodLootModifier() == 4) {
                double0 = 0.6;
            } else if (SandboxOptions.instance.getFoodLootModifier() == 5) {
                double0 = 1.0;
            } else if (SandboxOptions.instance.getFoodLootModifier() == 6) {
                double0 = 2.0;
            } else if (SandboxOptions.instance.getFoodLootModifier() == 7) {
                double0 = 4.0;
            }

            table.rawset("FoodLootModifier", double0);
            double double1 = 1.0;
            if (SandboxOptions.instance.getWeaponLootModifier() == 1) {
                double1 = 0.0;
            } else if (SandboxOptions.instance.getWeaponLootModifier() == 2) {
                double1 = 0.05;
            } else if (SandboxOptions.instance.getWeaponLootModifier() == 3) {
                double1 = 0.2;
            } else if (SandboxOptions.instance.getWeaponLootModifier() == 4) {
                double1 = 0.6;
            } else if (SandboxOptions.instance.getWeaponLootModifier() == 5) {
                double1 = 1.0;
            } else if (SandboxOptions.instance.getWeaponLootModifier() == 6) {
                double1 = 2.0;
            } else if (SandboxOptions.instance.getWeaponLootModifier() == 7) {
                double1 = 4.0;
            }

            table.rawset("WeaponLootModifier", double1);
            double double2 = 1.0;
            if (SandboxOptions.instance.getOtherLootModifier() == 1) {
                double2 = 0.0;
            } else if (SandboxOptions.instance.getOtherLootModifier() == 2) {
                double2 = 0.05;
            } else if (SandboxOptions.instance.getOtherLootModifier() == 3) {
                double2 = 0.2;
            } else if (SandboxOptions.instance.getOtherLootModifier() == 4) {
                double2 = 0.6;
            } else if (SandboxOptions.instance.getOtherLootModifier() == 5) {
                double2 = 1.0;
            } else if (SandboxOptions.instance.getOtherLootModifier() == 6) {
                double2 = 2.0;
            } else if (SandboxOptions.instance.getOtherLootModifier() == 7) {
                double2 = 4.0;
            }

            table.rawset("OtherLootModifier", double2);
        }
    }
}
