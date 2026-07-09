// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import fmod.fmod.FMODManager;
import java.util.HashMap;
import zombie.GameTime;
import zombie.ZomboidGlobals;
import zombie.ai.State;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;

public final class ClimbOverWallState extends State {
    private static final ClimbOverWallState _instance = new ClimbOverWallState();
    static final Integer PARAM_START_X = 0;
    static final Integer PARAM_START_Y = 1;
    static final Integer PARAM_Z = 2;
    static final Integer PARAM_END_X = 3;
    static final Integer PARAM_END_Y = 4;
    static final Integer PARAM_DIR = 5;
    static final int FENCE_TYPE_WOOD = 0;
    static final int FENCE_TYPE_METAL = 1;
    static final int FENCE_TYPE_METAL_BARS = 2;

    public static ClimbOverWallState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        owner.setHideWeaponModel(true);
        HashMap hashMap = owner.getStateMachineParams(this);
        Stats stats = owner.getStats();
        stats.endurance = (float)(stats.endurance - ZomboidGlobals.RunningEnduranceReduce * 1200.0);
        IsoPlayer player = (IsoPlayer)owner;
        boolean boolean0 = player.isClimbOverWallStruggle();
        if (boolean0) {
            stats = owner.getStats();
            stats.endurance = (float)(stats.endurance - ZomboidGlobals.RunningEnduranceReduce * 500.0);
        }

        boolean boolean1 = player.isClimbOverWallSuccess();
        owner.setVariable("ClimbFenceFinished", false);
        owner.setVariable("ClimbFenceOutcome", boolean1 ? "success" : "fail");
        owner.setVariable("ClimbFenceStarted", false);
        owner.setVariable("ClimbFenceStruggle", boolean0);
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoDirections directions = (IsoDirections)hashMap.get(PARAM_DIR);
        owner.setAnimated(true);
        owner.setDir(directions);
        boolean boolean0 = owner.getVariableBoolean("ClimbFenceStarted");
        if (!boolean0) {
            int int0 = (Integer)hashMap.get(PARAM_START_X);
            int int1 = (Integer)hashMap.get(PARAM_START_Y);
            float float0 = 0.15F;
            float float1 = owner.getX();
            float float2 = owner.getY();
            switch (directions) {
                case N:
                    float2 = int1 + float0;
                    break;
                case S:
                    float2 = int1 + 1 - float0;
                    break;
                case W:
                    float1 = int0 + float0;
                    break;
                case E:
                    float1 = int0 + 1 - float0;
            }

            float float3 = GameTime.getInstance().getMultiplier() / 1.6F / 8.0F;
            owner.setX(owner.x + (float1 - owner.x) * float3);
            owner.setY(owner.y + (float2 - owner.y) * float3);
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.clearVariable("ClimbFenceFinished");
        owner.clearVariable("ClimbFenceOutcome");
        owner.clearVariable("ClimbFenceStarted");
        owner.clearVariable("ClimbFenceStruggle");
        owner.setIgnoreMovement(false);
        owner.setHideWeaponModel(false);
        if (owner instanceof IsoZombie) {
            ((IsoZombie)owner).networkAI.isClimbing = false;
        }
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        if (event.m_EventName.equalsIgnoreCase("PlayFenceSound")) {
            IsoObject object = this.getFence(owner);
            if (object == null) {
                return;
            }

            int int0 = this.getFenceType(object);
            long long0 = owner.getEmitter().playSoundImpl(event.m_ParameterValue, null);
            owner.getEmitter().setParameterValue(long0, FMODManager.instance.getParameterDescription("FenceTypeHigh"), int0);
        }
    }

    /**
     * Description copied from class: State
     */
    @Override
    public boolean isIgnoreCollide(IsoGameCharacter owner, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        HashMap hashMap = owner.getStateMachineParams(this);
        int int0 = (Integer)hashMap.get(PARAM_START_X);
        int int1 = (Integer)hashMap.get(PARAM_START_Y);
        int int2 = (Integer)hashMap.get(PARAM_END_X);
        int int3 = (Integer)hashMap.get(PARAM_END_Y);
        int int4 = (Integer)hashMap.get(PARAM_Z);
        if (int4 == fromZ && int4 == toZ) {
            int int5 = PZMath.min(int0, int2);
            int int6 = PZMath.min(int1, int3);
            int int7 = PZMath.max(int0, int2);
            int int8 = PZMath.max(int1, int3);
            int int9 = PZMath.min(fromX, toX);
            int int10 = PZMath.min(fromY, toY);
            int int11 = PZMath.max(fromX, toX);
            int int12 = PZMath.max(fromY, toY);
            return int5 <= int9 && int6 <= int10 && int7 >= int11 && int8 >= int12;
        } else {
            return false;
        }
    }

    private IsoObject getClimbableWallN(IsoGridSquare square) {
        IsoObject[] objects = square.getObjects().getElements();
        int int0 = 0;

        for (int int1 = square.getObjects().size(); int0 < int1; int0++) {
            IsoObject object = objects[int0];
            PropertyContainer propertyContainer = object.getProperties();
            if (propertyContainer != null
                && !propertyContainer.Is(IsoFlagType.CantClimb)
                && object.getType() == IsoObjectType.wall
                && propertyContainer.Is(IsoFlagType.collideN)
                && !propertyContainer.Is(IsoFlagType.HoppableN)) {
                return object;
            }
        }

        return null;
    }

    private IsoObject getClimbableWallW(IsoGridSquare square) {
        IsoObject[] objects = square.getObjects().getElements();
        int int0 = 0;

        for (int int1 = square.getObjects().size(); int0 < int1; int0++) {
            IsoObject object = objects[int0];
            PropertyContainer propertyContainer = object.getProperties();
            if (propertyContainer != null
                && !propertyContainer.Is(IsoFlagType.CantClimb)
                && object.getType() == IsoObjectType.wall
                && propertyContainer.Is(IsoFlagType.collideW)
                && !propertyContainer.Is(IsoFlagType.HoppableW)) {
                return object;
            }
        }

        return null;
    }

    private IsoObject getFence(IsoGameCharacter character) {
        HashMap hashMap = character.getStateMachineParams(this);
        int int0 = (Integer)hashMap.get(PARAM_START_X);
        int int1 = (Integer)hashMap.get(PARAM_START_Y);
        int int2 = (Integer)hashMap.get(PARAM_Z);
        IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
        int int3 = (Integer)hashMap.get(PARAM_END_X);
        int int4 = (Integer)hashMap.get(PARAM_END_Y);
        IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, int2);
        if (square0 != null && square1 != null) {
            IsoDirections directions = (IsoDirections)hashMap.get(PARAM_DIR);

            return switch (directions) {
                case N -> this.getClimbableWallN(square0);
                case S -> this.getClimbableWallN(square1);
                case W -> this.getClimbableWallW(square0);
                case E -> this.getClimbableWallW(square1);
                default -> null;
            };
        } else {
            return null;
        }
    }

    private int getFenceType(IsoObject object) {
        if (object.getSprite() == null) {
            return 0;
        } else {
            PropertyContainer propertyContainer = object.getSprite().getProperties();
            String string = propertyContainer.Val("FenceTypeHigh");
            if (string != null) {
                return switch (string) {
                    case "Wood" -> 0;
                    case "Metal" -> 1;
                    case "MetalGate" -> 2;
                    default -> 0;
                };
            } else {
                return 0;
            }
        }
    }

    public void setParams(IsoGameCharacter owner, IsoDirections dir) {
        HashMap hashMap = owner.getStateMachineParams(this);
        int int0 = owner.getSquare().getX();
        int int1 = owner.getSquare().getY();
        int int2 = owner.getSquare().getZ();
        int int3 = int0;
        int int4 = int1;
        switch (dir) {
            case N:
                int4 = int1 - 1;
                break;
            case S:
                int4 = int1 + 1;
                break;
            case W:
                int3 = int0 - 1;
                break;
            case E:
                int3 = int0 + 1;
                break;
            default:
                throw new IllegalArgumentException("invalid direction");
        }

        hashMap.put(PARAM_START_X, int0);
        hashMap.put(PARAM_START_Y, int1);
        hashMap.put(PARAM_Z, int2);
        hashMap.put(PARAM_END_X, int3);
        hashMap.put(PARAM_END_Y, int4);
        hashMap.put(PARAM_DIR, dir);
        IsoPlayer player = (IsoPlayer)owner;
        if (player.isLocalPlayer()) {
            int int5 = 20;
            int5 += owner.getPerkLevel(PerkFactory.Perks.Fitness) * 2;
            int5 += owner.getPerkLevel(PerkFactory.Perks.Strength) * 2;
            int5 -= owner.getMoodles().getMoodleLevel(MoodleType.Endurance) * 5;
            int5 -= owner.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 8;
            if (owner.getTraits().contains("Emaciated") || owner.Traits.Obese.isSet() || owner.getTraits().contains("Very Underweight")) {
                int5 -= 25;
            }

            if (owner.getTraits().contains("Underweight") || owner.getTraits().contains("Overweight")) {
                int5 -= 15;
            }

            IsoGridSquare square = owner.getCurrentSquare();
            if (square != null) {
                for (int int6 = 0; int6 < square.getMovingObjects().size(); int6++) {
                    IsoMovingObject movingObject = square.getMovingObjects().get(int6);
                    if (movingObject instanceof IsoZombie) {
                        if (((IsoZombie)movingObject).target == owner && ((IsoZombie)movingObject).getCurrentState() == AttackState.instance()) {
                            int5 -= 25;
                        } else {
                            int5 -= 7;
                        }
                    }
                }
            }

            int5 = Math.max(0, int5);
            boolean boolean0 = Rand.NextBool(int5 / 2);
            if ("Tutorial".equals(Core.GameMode)) {
                boolean0 = false;
            }

            boolean boolean1 = !Rand.NextBool(int5);
            player.setClimbOverWallStruggle(boolean0);
            player.setClimbOverWallSuccess(boolean1);
        }
    }
}
