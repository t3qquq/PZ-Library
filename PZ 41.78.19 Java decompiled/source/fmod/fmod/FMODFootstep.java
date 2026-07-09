// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;

public class FMODFootstep {
    public String wood;
    public String concrete;
    public String grass;
    public String upstairs;
    public String woodCreak;

    public FMODFootstep(String arg0, String arg1, String arg2, String arg3) {
        this.grass = arg0;
        this.wood = arg1;
        this.concrete = arg2;
        this.upstairs = arg3;
        this.woodCreak = "HumanFootstepFloorCreaking";
    }

    public boolean isUpstairs(IsoGameCharacter arg0) {
        IsoGridSquare square = IsoPlayer.getInstance().getCurrentSquare();
        return square.getZ() < arg0.getCurrentSquare().getZ();
    }

    public String getSoundToPlay(IsoGameCharacter arg0) {
        if (FMODManager.instance.getNumListeners() == 1) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null && player != arg0 && !player.Traits.Deaf.isSet()) {
                    if ((int)player.getZ() < (int)arg0.getZ()) {
                        return this.upstairs;
                    }
                    break;
                }
            }
        }

        IsoObject object = arg0.getCurrentSquare().getFloor();
        if (object != null && object.getSprite() != null && object.getSprite().getName() != null) {
            String string = object.getSprite().getName();
            if (string.startsWith("blends_natural_01")) {
                return this.grass;
            } else if (string.startsWith("floors_interior_tilesandwood_01_")) {
                int int1 = Integer.parseInt(string.replaceFirst("floors_interior_tilesandwood_01_", ""));
                return int1 > 40 && int1 < 48 ? this.wood : this.concrete;
            } else if (string.startsWith("carpentry_02_")) {
                return this.wood;
            } else {
                return string.startsWith("floors_interior_carpet_") ? this.wood : this.concrete;
            }
        } else {
            return this.concrete;
        }
    }
}
