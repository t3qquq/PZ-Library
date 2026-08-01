// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation.events;

import zombie.characters.IsoGameCharacter;

public interface IAnimEventListenerNoTrackEnum<E extends Enum<E>> {
    void animEvent(IsoGameCharacter var1, E var2);
}
