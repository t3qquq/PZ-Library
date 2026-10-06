// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.entity.components.crafting;

import zombie.UsedFromLua;

@UsedFromLua
public enum OutputFlag {
    HandcraftOnly,
    AutomationOnly,
    IsEmpty,
    ForceEmpty,
    AlwaysFill,
    RespectCapacity,
    IsBlunt,
    HasOneUse,
    HasNoUses,
    DontInheritCondition,
    EquipSecondary,
    SetActivated;
}
