// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects.interfaces;

import zombie.characters.IsoGameCharacter;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoBarricade;

public interface BarricadeAble {
    boolean isBarricaded();

    boolean isBarricadeAllowed();

    IsoBarricade getBarricadeOnSameSquare();

    IsoBarricade getBarricadeOnOppositeSquare();

    IsoBarricade getBarricadeForCharacter(IsoGameCharacter chr);

    IsoBarricade getBarricadeOppositeCharacter(IsoGameCharacter chr);

    IsoGridSquare getSquare();

    IsoGridSquare getOppositeSquare();

    boolean getNorth();
}
