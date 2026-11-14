// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.characters;

import zombie.UsedFromLua;

@UsedFromLua
public abstract class CharacterInputBindingSetEntry {
    abstract void apply();

    abstract boolean isValid();
}
