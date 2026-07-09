// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import fmod.fmod.FMODManager;
import zombie.ai.State;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.util.Type;

public final class BumpedState extends State {
    private static final BumpedState _instance = new BumpedState();

    public static BumpedState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter character) {
        character.setBumpDone(false);
        character.setVariable("BumpFallAnimFinished", false);
        character.getAnimationPlayer().setTargetToAngle();
        character.getForwardDirection().setLengthAndDirection(character.getAnimationPlayer().getAngle(), 1.0F);
        this.setCharacterBlockMovement(character, true);
        if (character.getVariableBoolean("BumpFall")) {
            long long0 = character.playSound("TripOverObstacle");
            ParameterCharacterMovementSpeed parameterCharacterMovementSpeed = ((IsoPlayer)character).getParameterCharacterMovementSpeed();
            character.getEmitter()
                .setParameterValue(long0, parameterCharacterMovementSpeed.getParameterDescription(), parameterCharacterMovementSpeed.calculateCurrentValue());
            String string = character.getVariableString("TripObstacleType");
            if (string == null) {
                string = "zombie";
            }

            character.clearVariable("TripObstacleType");
            byte byte0 = -1;
            switch (string.hashCode()) {
                case 3568542:
                    if (string.equals("tree")) {
                        byte0 = 0;
                    }
                default:
                    byte byte1 = switch (byte0) {
                        case 0 -> 5;
                        default -> 6;
                    };
                    character.getEmitter().setParameterValue(long0, FMODManager.instance.getParameterDescription("TripObstacleType"), byte1);
            }
        }
    }

    @Override
    public void execute(IsoGameCharacter character) {
        boolean boolean0 = character.isBumpFall() || character.isBumpStaggered();
        this.setCharacterBlockMovement(character, boolean0);
    }

    private void setCharacterBlockMovement(IsoGameCharacter character, boolean boolean0) {
        IsoPlayer player = Type.tryCastTo(character, IsoPlayer.class);
        if (player != null) {
            player.setBlockMovement(boolean0);
        }
    }

    @Override
    public void exit(IsoGameCharacter character) {
        character.clearVariable("BumpFallType");
        character.clearVariable("BumpFallAnimFinished");
        character.clearVariable("BumpAnimFinished");
        character.setBumpType("");
        character.setBumpedChr(null);
        IsoPlayer player = Type.tryCastTo(character, IsoPlayer.class);
        if (player != null) {
            player.setInitiateAttack(false);
            player.attackStarted = false;
            player.setAttackType(null);
        }

        if (player != null && character.isBumpFall()) {
            character.fallenOnKnees();
        }

        character.setOnFloor(false);
        character.setBumpFall(false);
        this.setCharacterBlockMovement(character, false);
        if (character instanceof IsoZombie && ((IsoZombie)character).target != null) {
            character.pathToLocation(
                (int)((IsoZombie)character).target.getX(), (int)((IsoZombie)character).target.getY(), (int)((IsoZombie)character).target.getZ()
            );
        }
    }

    @Override
    public void animEvent(IsoGameCharacter character, AnimEvent animEvent) {
        if (animEvent.m_EventName.equalsIgnoreCase("FallOnFront")) {
            character.setFallOnFront(Boolean.parseBoolean(animEvent.m_ParameterValue));
            character.setOnFloor(character.isFallOnFront());
        }

        if (animEvent.m_EventName.equalsIgnoreCase("FallOnBack")) {
            character.setOnFloor(Boolean.parseBoolean(animEvent.m_ParameterValue));
        }
    }
}
