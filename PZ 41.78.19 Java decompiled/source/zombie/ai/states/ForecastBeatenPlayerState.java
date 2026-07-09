// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.GameTime;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;

public final class ForecastBeatenPlayerState extends State {
    private static final ForecastBeatenPlayerState _instance = new ForecastBeatenPlayerState();

    public static ForecastBeatenPlayerState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        character.setIgnoreMovement(true);
        character.setReanimateTimer(30.0F);
    }

    @Override
    public void execute(IsoGameCharacter character) {
        if (character.getCurrentSquare() != null) {
            character.setReanimateTimer(character.getReanimateTimer() - GameTime.getInstance().getMultiplier() / 1.6F);
            if (character.getReanimateTimer() <= 0.0F) {
                character.setReanimateTimer(0.0F);
                character.setVariable("bKnockedDown", true);
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter character) {
        character.setIgnoreMovement(false);
    }
}
