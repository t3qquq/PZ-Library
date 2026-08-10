// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
