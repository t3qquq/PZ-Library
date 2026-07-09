// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects.interfaces;

import zombie.characters.IsoGameCharacter;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoMovingObject;

public interface Thumpable {
    boolean isDestroyed();

    void Thump(IsoMovingObject thumper);

    void WeaponHit(IsoGameCharacter chr, HandWeapon weapon);

    Thumpable getThumpableFor(IsoGameCharacter chr);

    float getThumpCondition();
}
