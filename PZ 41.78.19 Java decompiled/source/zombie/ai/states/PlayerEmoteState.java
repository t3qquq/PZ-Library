// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class PlayerEmoteState extends State {
    private static final PlayerEmoteState _instance = new PlayerEmoteState();

    public static PlayerEmoteState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setVariable("EmotePlaying", true);
        owner.resetModelNextFrame();
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        IsoPlayer player = (IsoPlayer)owner;
        if (player.pressedCancelAction()) {
            owner.setVariable("EmotePlaying", false);
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.clearVariable("EmotePlaying");
        owner.resetModelNextFrame();
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if ("EmoteFinishing".equalsIgnoreCase(event.m_EventName)) {
            owner.setVariable("EmotePlaying", false);
        }

        if ("EmoteLooped".equalsIgnoreCase(event.m_EventName)) {
        }
    }

    /**
     * @return TRUE if this state handles the "Cancel Action" key or the B controller button.
     */
    @Override
    public boolean isDoingActionThatCanBeCancelled() {
        return true;
    }
}
