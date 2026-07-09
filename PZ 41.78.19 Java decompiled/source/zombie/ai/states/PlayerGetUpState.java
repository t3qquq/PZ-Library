// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.Moodles.MoodleType;
import zombie.network.GameClient;

public final class PlayerGetUpState extends State {
    private static final PlayerGetUpState _instance = new PlayerGetUpState();

    public static PlayerGetUpState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        owner.setIgnoreMovement(true);
        IsoPlayer player = (IsoPlayer)owner;
        player.setInitiateAttack(false);
        player.attackStarted = false;
        player.setAttackType(null);
        player.setBlockMovement(true);
        player.setForceRun(false);
        player.setForceSprint(false);
        owner.setVariable("getUpQuick", owner.getVariableBoolean("pressedRunButton"));
        if (owner.getMoodles().getMoodleLevel(MoodleType.Panic) > 1) {
            owner.setVariable("getUpQuick", true);
        }

        if (owner.getVariableBoolean("pressedMovement")) {
            owner.setVariable("getUpWalk", true);
        }

        if (GameClient.bClient) {
            owner.setKnockedDown(false);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.clearVariable("getUpWalk");
        if (owner.getVariableBoolean("sitonground")) {
            owner.setHideWeaponModel(false);
        }

        owner.setIgnoreMovement(false);
        owner.setFallOnFront(false);
        owner.setOnFloor(false);
        ((IsoPlayer)owner).setBlockMovement(false);
        owner.setSitOnGround(false);
    }
}
