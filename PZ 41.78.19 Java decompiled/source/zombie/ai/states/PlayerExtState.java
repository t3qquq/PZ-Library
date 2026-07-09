// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;

public final class PlayerExtState extends State {
    private static final PlayerExtState _instance = new PlayerExtState();

    public static PlayerExtState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setVariable("ExtPlaying", true);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.clearVariable("ExtPlaying");
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if ("ExtFinishing".equalsIgnoreCase(event.m_EventName)) {
            owner.setVariable("ExtPlaying", false);
        }
    }
}
