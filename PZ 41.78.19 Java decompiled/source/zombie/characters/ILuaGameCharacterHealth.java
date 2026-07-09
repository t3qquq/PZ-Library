// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.inventory.InventoryItem;

public interface ILuaGameCharacterHealth {
    void setSleepingTabletEffect(float var1);

    float getSleepingTabletEffect();

    float getFatigueMod();

    boolean Eat(InventoryItem var1, float var2);

    boolean Eat(InventoryItem var1);

    float getTemperature();

    void setTemperature(float var1);

    float getReduceInfectionPower();

    void setReduceInfectionPower(float var1);

    int getLastHourSleeped();

    void setLastHourSleeped(int var1);

    void setTimeOfSleep(float var1);
}
