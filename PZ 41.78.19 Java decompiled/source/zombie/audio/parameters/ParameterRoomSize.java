// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;

public final class ParameterRoomSize extends FMODGlobalParameter {
    public ParameterRoomSize() {
        super("RoomSize");
    }

    @Override
    public float calculateCurrentValue() {
        IsoGameCharacter character = this.getCharacter();
        if (character == null) {
            return 0.0F;
        } else {
            RoomDef roomDef = character.getCurrentRoomDef();
            if (roomDef != null) {
                return roomDef.getArea();
            } else {
                IsoGridSquare square = character.getCurrentSquare();
                return square != null && square.isInARoom() ? square.getRoomSize() : 0.0F;
            }
        }
    }

    private IsoGameCharacter getCharacter() {
        IsoPlayer player0 = null;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player1 = IsoPlayer.players[int0];
            if (player1 != null && (player0 == null || player0.isDead() && player1.isAlive() || player0.Traits.Deaf.isSet() && !player1.Traits.Deaf.isSet())) {
                player0 = player1;
            }
        }

        return player0;
    }
}
