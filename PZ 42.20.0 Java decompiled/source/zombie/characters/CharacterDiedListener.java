// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.characters;

import zombie.iso.objects.IsoDeadBody;

public interface CharacterDiedListener {
    void onDied(IsoGameCharacter var1, IsoDeadBody var2);
}
