// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugLog;
import zombie.network.GameClient;

/**
 * TurboTuTone.
 */
public final class FishingState extends State {
    private static final FishingState _instance = new FishingState();
    float pauseTime = 0.0F;
    private String stage = null;

    public static FishingState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        DebugLog.log("FISHINGSTATE - ENTER");
        owner.setVariable("FishingFinished", false);
        this.pauseTime = Rand.Next(60.0F, 120.0F);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        if (GameClient.bClient && owner instanceof IsoPlayer && ((IsoPlayer)owner).isLocalPlayer()) {
            String string = owner.getVariableString("FishingStage");
            if (string != null && !string.equals(this.stage)) {
                this.stage = string;
                if (!string.equals("idle")) {
                    GameClient.sendEvent((IsoPlayer)owner, "EventFishing");
                }
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        DebugLog.log("FISHINGSTATE - EXIT");
        owner.clearVariable("FishingStage");
        owner.clearVariable("FishingFinished");
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
    }
}
