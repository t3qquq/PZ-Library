// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODGlobalParameter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;

public final class ParameterMoodlePanic extends FMODGlobalParameter {
    public ParameterMoodlePanic() {
        super("MoodlePanic");
    }

    @Override
    public float calculateCurrentValue() {
        IsoGameCharacter character = this.getCharacter();
        return character == null ? 0.0F : character.getMoodles().getMoodleLevel(MoodleType.Panic) / 4.0F;
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
