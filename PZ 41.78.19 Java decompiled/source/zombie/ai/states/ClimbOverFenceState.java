// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import fmod.fmod.FMODManager;
import java.util.HashMap;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.ZomboidGlobals;
import zombie.ai.State;
import zombie.audio.parameters.ParameterCharacterMovementSpeed;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.MoveDeltaModifiers;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.debug.DebugOptions;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.util.StringUtils;
import zombie.util.Type;

public final class ClimbOverFenceState extends State {
    private static final ClimbOverFenceState _instance = new ClimbOverFenceState();
    static final Integer PARAM_START_X = 0;
    static final Integer PARAM_START_Y = 1;
    static final Integer PARAM_Z = 2;
    static final Integer PARAM_END_X = 3;
    static final Integer PARAM_END_Y = 4;
    static final Integer PARAM_DIR = 5;
    static final Integer PARAM_ZOMBIE_ON_FLOOR = 6;
    static final Integer PARAM_PREV_STATE = 7;
    static final Integer PARAM_SCRATCH = 8;
    static final Integer PARAM_COUNTER = 9;
    static final Integer PARAM_SOLID_FLOOR = 10;
    static final Integer PARAM_SHEET_ROPE = 11;
    static final Integer PARAM_RUN = 12;
    static final Integer PARAM_SPRINT = 13;
    static final Integer PARAM_COLLIDABLE = 14;
    static final int FENCE_TYPE_WOOD = 0;
    static final int FENCE_TYPE_METAL = 1;
    static final int FENCE_TYPE_SANDBAG = 2;
    static final int FENCE_TYPE_GRAVELBAG = 3;
    static final int FENCE_TYPE_BARBWIRE = 4;
    static final int FENCE_TYPE_ROADBLOCK = 5;
    static final int FENCE_TYPE_METAL_BARS = 6;
    static final int TRIP_WOOD = 0;
    static final int TRIP_METAL = 1;
    static final int TRIP_SANDBAG = 2;
    static final int TRIP_GRAVELBAG = 3;
    static final int TRIP_BARBWIRE = 4;
    public static final int TRIP_TREE = 5;
    public static final int TRIP_ZOMBIE = 6;
    public static final int COLLIDE_WITH_WALL = 7;
    public static final int TRIP_METAL_BARS = 8;
    public static final int TRIP_WINDOW = 9;

    public static ClimbOverFenceState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setVariable("FenceLungeX", 0.0F);
        owner.setVariable("FenceLungeY", 0.0F);
        HashMap hashMap = owner.getStateMachineParams(this);
        owner.setIgnoreMovement(true);
        if (hashMap.get(PARAM_RUN) == Boolean.TRUE) {
            owner.setVariable("VaultOverRun", true);
            Stats stats0 = owner.getStats();
            stats0.endurance = (float)(stats0.endurance - ZomboidGlobals.RunningEnduranceReduce * 300.0);
        } else if (hashMap.get(PARAM_SPRINT) == Boolean.TRUE) {
            owner.setVariable("VaultOverSprint", true);
            Stats stats1 = owner.getStats();
            stats1.endurance = (float)(stats1.endurance - ZomboidGlobals.RunningEnduranceReduce * 700.0);
        }

        boolean boolean0 = hashMap.get(PARAM_COUNTER) == Boolean.TRUE;
        owner.setVariable("ClimbingFence", true);
        owner.setVariable("ClimbFenceStarted", false);
        owner.setVariable("ClimbFenceFinished", false);
        owner.setVariable("ClimbFenceOutcome", boolean0 ? "obstacle" : "success");
        owner.clearVariable("ClimbFenceFlopped");
        if ((owner.getVariableBoolean("VaultOverRun") || owner.getVariableBoolean("VaultOverSprint")) && this.shouldFallAfterVaultOver(owner)) {
            owner.setVariable("ClimbFenceOutcome", "fall");
        }

        IsoZombie zombie0 = Type.tryCastTo(owner, IsoZombie.class);
        if (!boolean0 && zombie0 != null && zombie0.shouldDoFenceLunge()) {
            owner.setVariable("ClimbFenceOutcome", "lunge");
            this.setLungeXVars(zombie0);
        }

        if (hashMap.get(PARAM_SOLID_FLOOR) == Boolean.FALSE) {
            owner.setVariable("ClimbFenceOutcome", "falling");
        }

        if (!(owner instanceof IsoZombie) && hashMap.get(PARAM_SHEET_ROPE) == Boolean.TRUE) {
            owner.setVariable("ClimbFenceOutcome", "rope");
        }

        if (owner instanceof IsoPlayer && ((IsoPlayer)owner).isLocalPlayer()) {
            ((IsoPlayer)owner).dirtyRecalcGridStackTime = 20.0F;
        }
    }

    private void setLungeXVars(IsoZombie zombie0) {
        IsoMovingObject movingObject = zombie0.getTarget();
        if (movingObject != null) {
            zombie0.setVariable("FenceLungeX", 0.0F);
            zombie0.setVariable("FenceLungeY", 0.0F);
            float float0 = 0.0F;
            Vector2 vector = zombie0.getForwardDirection();
            PZMath.SideOfLine sideOfLine = PZMath.testSideOfLine(
                zombie0.x, zombie0.y, zombie0.x + vector.x, zombie0.y + vector.y, movingObject.x, movingObject.y
            );
            float float1 = (float)Math.acos(zombie0.getDotWithForwardDirection(movingObject.x, movingObject.y));
            float float2 = PZMath.clamp(PZMath.radToDeg(float1), 0.0F, 90.0F);
            switch (sideOfLine) {
                case Left:
                    float0 = -float2 / 90.0F;
                    break;
                case OnLine:
                    float0 = 0.0F;
                    break;
                case Right:
                    float0 = float2 / 90.0F;
            }

            zombie0.setVariable("FenceLungeX", float0);
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoDirections directions = Type.tryCastTo(hashMap.get(PARAM_DIR), IsoDirections.class);
        int int0 = (Integer)hashMap.get(PARAM_END_X);
        int int1 = (Integer)hashMap.get(PARAM_END_Y);
        owner.setAnimated(true);
        if (directions == IsoDirections.N) {
            owner.setDir(IsoDirections.N);
        } else if (directions == IsoDirections.S) {
            owner.setDir(IsoDirections.S);
        } else if (directions == IsoDirections.W) {
            owner.setDir(IsoDirections.W);
        } else if (directions == IsoDirections.E) {
            owner.setDir(IsoDirections.E);
        }

        String string = owner.getVariableString("ClimbFenceOutcome");
        if (!"lunge".equals(string)) {
            float float0 = 0.05F;
            if (directions == IsoDirections.N || directions == IsoDirections.S) {
                owner.x = owner.nx = PZMath.clamp(owner.x, int0 + float0, int0 + 1 - float0);
            } else if (directions == IsoDirections.W || directions == IsoDirections.E) {
                owner.y = owner.ny = PZMath.clamp(owner.y, int1 + float0, int1 + 1 - float0);
            }
        }

        if (owner.getVariableBoolean("ClimbFenceStarted")
            && !"back".equals(string)
            && !"fallback".equals(string)
            && !"lunge".equalsIgnoreCase(string)
            && !"obstacle".equals(string)
            && !"obstacleEnd".equals(string)) {
            float float1 = ((Integer)hashMap.get(PARAM_START_X)).intValue();
            float float2 = ((Integer)hashMap.get(PARAM_START_Y)).intValue();
            switch (directions) {
                case N:
                    float2 -= 0.1F;
                    break;
                case S:
                    float2++;
                    break;
                case W:
                    float1 -= 0.1F;
                    break;
                case E:
                    float1++;
            }

            if ((int)owner.x != (int)float1 && (directions == IsoDirections.W || directions == IsoDirections.E)) {
                this.slideX(owner, float1);
            }

            if ((int)owner.y != (int)float2 && (directions == IsoDirections.N || directions == IsoDirections.S)) {
                this.slideY(owner, float2);
            }
        }

        if (owner instanceof IsoZombie) {
            boolean boolean0 = hashMap.get(PARAM_ZOMBIE_ON_FLOOR) == Boolean.TRUE;
            owner.setOnFloor(boolean0);
            ((IsoZombie)owner).setKnockedDown(boolean0);
            owner.setFallOnFront(boolean0);
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (owner instanceof IsoPlayer && "fall".equals(owner.getVariableString("ClimbFenceOutcome"))) {
            owner.setSprinting(false);
        }

        owner.clearVariable("ClimbingFence");
        owner.clearVariable("ClimbFenceFinished");
        owner.clearVariable("ClimbFenceOutcome");
        owner.clearVariable("ClimbFenceStarted");
        owner.clearVariable("ClimbFenceFlopped");
        owner.ClearVariable("VaultOverSprint");
        owner.ClearVariable("VaultOverRun");
        owner.setIgnoreMovement(false);
        IsoZombie zombie0 = Type.tryCastTo(owner, IsoZombie.class);
        if (zombie0 != null) {
            zombie0.AllowRepathDelay = 0.0F;
            if (hashMap.get(PARAM_PREV_STATE) == PathFindState.instance()) {
                if (owner.getPathFindBehavior2().getTargetChar() == null) {
                    owner.setVariable("bPathfind", true);
                    owner.setVariable("bMoving", false);
                } else if (zombie0.isTargetLocationKnown()) {
                    owner.pathToCharacter(owner.getPathFindBehavior2().getTargetChar());
                } else if (zombie0.LastTargetSeenX != -1) {
                    owner.pathToLocation(zombie0.LastTargetSeenX, zombie0.LastTargetSeenY, zombie0.LastTargetSeenZ);
                }
            } else if (hashMap.get(PARAM_PREV_STATE) == WalkTowardState.instance() || hashMap.get(PARAM_PREV_STATE) == WalkTowardNetworkState.instance()) {
                owner.setVariable("bPathFind", false);
                owner.setVariable("bMoving", true);
            }
        }

        if (owner instanceof IsoZombie) {
            ((IsoZombie)owner).networkAI.isClimbing = false;
        }
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoZombie zombie0 = Type.tryCastTo(owner, IsoZombie.class);
        if (event.m_EventName.equalsIgnoreCase("CheckAttack") && zombie0 != null && zombie0.target instanceof IsoGameCharacter) {
            ((IsoGameCharacter)zombie0.target).attackFromWindowsLunge(zombie0);
        }

        if (event.m_EventName.equalsIgnoreCase("ActiveAnimFinishing")) {
        }

        if (event.m_EventName.equalsIgnoreCase("VaultSprintFallLanded")) {
            owner.dropHandItems();
            owner.fallenOnKnees();
        }

        if (event.m_EventName.equalsIgnoreCase("FallenOnKnees")) {
            owner.fallenOnKnees();
        }

        if (event.m_EventName.equalsIgnoreCase("OnFloor")) {
            hashMap.put(PARAM_ZOMBIE_ON_FLOOR, Boolean.parseBoolean(event.m_ParameterValue));
            if (Boolean.parseBoolean(event.m_ParameterValue)) {
                this.setLungeXVars((IsoZombie)owner);
                IsoObject object0 = this.getFence(owner);
                if (this.countZombiesClimbingOver(object0) >= 2) {
                    object0.Damage = (short)(object0.Damage - Rand.Next(7, 12) / (this.isMetalFence(object0) ? 2 : 1));
                    if (object0.Damage <= 0) {
                        IsoDirections directions = Type.tryCastTo(hashMap.get(PARAM_DIR), IsoDirections.class);
                        object0.destroyFence(directions);
                    }
                }

                owner.setVariable("ClimbFenceFlopped", true);
            }
        }

        if (event.m_EventName.equalsIgnoreCase("PlayFenceSound")) {
            if (!SoundManager.instance.isListenerInRange(owner.getX(), owner.getY(), 10.0F)) {
                return;
            }

            IsoObject object1 = this.getFence(owner);
            if (object1 == null) {
                return;
            }

            int int0 = this.getFenceType(object1);
            long long0 = owner.getEmitter().playSoundImpl(event.m_ParameterValue, null);
            if (owner instanceof IsoPlayer) {
                ParameterCharacterMovementSpeed parameterCharacterMovementSpeed0 = ((IsoPlayer)owner).getParameterCharacterMovementSpeed();
                owner.getEmitter()
                    .setParameterValue(
                        long0, parameterCharacterMovementSpeed0.getParameterDescription(), parameterCharacterMovementSpeed0.calculateCurrentValue()
                    );
            }

            owner.getEmitter().setParameterValue(long0, FMODManager.instance.getParameterDescription("FenceTypeLow"), int0);
        }

        if (event.m_EventName.equalsIgnoreCase("PlayTripSound")) {
            if (!SoundManager.instance.isListenerInRange(owner.getX(), owner.getY(), 10.0F)) {
                return;
            }

            IsoObject object2 = this.getFence(owner);
            if (object2 == null) {
                return;
            }

            int int1 = this.getTripType(object2);
            long long1 = owner.getEmitter().playSoundImpl(event.m_ParameterValue, null);
            ParameterCharacterMovementSpeed parameterCharacterMovementSpeed1 = ((IsoPlayer)owner).getParameterCharacterMovementSpeed();
            owner.getEmitter()
                .setParameterValue(long1, parameterCharacterMovementSpeed1.getParameterDescription(), parameterCharacterMovementSpeed1.calculateCurrentValue());
            owner.getEmitter().setParameterValue(long1, FMODManager.instance.getParameterDescription("TripObstacleType"), int1);
        }

        if (event.m_EventName.equalsIgnoreCase("SetCollidable")) {
            hashMap.put(PARAM_COLLIDABLE, Boolean.parseBoolean(event.m_ParameterValue));
        }

        if (event.m_EventName.equalsIgnoreCase("SetState")) {
            if (zombie0 == null) {
                return;
            }

            try {
                ParameterZombieState.State state = ParameterZombieState.State.valueOf(event.m_ParameterValue);
                zombie0.parameterZombieState.setState(state);
            } catch (IllegalArgumentException illegalArgumentException) {
            }
        }

        if (event.m_EventName.equalsIgnoreCase("VaultOverStarted")) {
            if (owner instanceof IsoPlayer && !((IsoPlayer)owner).isLocalPlayer()) {
                return;
            }

            if (owner.isVariable("ClimbFenceOutcome", "fall")) {
                owner.reportEvent("EventFallClimb");
                owner.setVariable("BumpDone", true);
                owner.setFallOnFront(true);
            }
        }
    }

    @Override
    public void getDeltaModifiers(IsoGameCharacter owner, MoveDeltaModifiers modifiers) {
        boolean boolean0 = owner.getPath2() != null;
        boolean boolean1 = owner instanceof IsoPlayer;
        if (boolean0 && boolean1) {
            modifiers.turnDelta = Math.max(modifiers.turnDelta, 10.0F);
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

    private void slideX(IsoGameCharacter character, float float1) {
        float float0 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
        float0 = float1 > character.x ? Math.min(float0, float1 - character.x) : Math.max(-float0, float1 - character.x);
        character.x += float0;
        character.nx = character.x;
    }

    private void slideY(IsoGameCharacter character, float float1) {
        float float0 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
        float0 = float1 > character.y ? Math.min(float0, float1 - character.y) : Math.max(-float0, float1 - character.y);
        character.y += float0;
        character.ny = character.y;
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
        return square0 != null && square1 != null ? square0.getHoppableTo(square1) : null;
    }

    private int getFenceType(IsoObject object) {
        if (object.getSprite() == null) {
            return 0;
        } else {
            PropertyContainer propertyContainer = object.getSprite().getProperties();
            String string = propertyContainer.Val("FenceTypeLow");
            if (string != null) {
                if ("Sandbag".equals(string) && object.getName() != null && StringUtils.containsIgnoreCase(object.getName(), "Gravel")) {
                    string = "Gravelbag";
                }
                return switch (string) {
                    case "Wood" -> 0;
                    case "Metal" -> 1;
                    case "Sandbag" -> 2;
                    case "Gravelbag" -> 3;
                    case "Barbwire" -> 4;
                    case "RoadBlock" -> 5;
                    case "MetalGate" -> 6;
                    default -> 0;
                };
            } else {
                return 0;
            }
        }
    }

    private int getTripType(IsoObject object) {
        if (object.getSprite() == null) {
            return 0;
        } else {
            PropertyContainer propertyContainer = object.getSprite().getProperties();
            String string = propertyContainer.Val("FenceTypeLow");
            if (string != null) {
                if ("Sandbag".equals(string) && object.getName() != null && StringUtils.containsIgnoreCase(object.getName(), "Gravel")) {
                    string = "Gravelbag";
                }
                return switch (string) {
                    case "Wood" -> 0;
                    case "Metal" -> 1;
                    case "Sandbag" -> 2;
                    case "Gravelbag" -> 3;
                    case "Barbwire" -> 4;
                    case "MetalGate" -> 8;
                    default -> 0;
                };
            } else {
                return 0;
            }
        }
    }

    private boolean shouldFallAfterVaultOver(IsoGameCharacter character) {
        if (character instanceof IsoPlayer && !((IsoPlayer)character).isLocalPlayer()) {
            return ((IsoPlayer)character).networkAI.climbFenceOutcomeFall;
        } else if (DebugOptions.instance.Character.Debug.AlwaysTripOverFence.getValue()) {
            return true;
        } else {
            float float0 = 0.0F;
            if (character.getVariableBoolean("VaultOverSprint")) {
                float0 = 10.0F;
            }

            if (character.getMoodles() != null) {
                float0 += character.getMoodles().getMoodleLevel(MoodleType.Endurance) * 10;
                float0 += character.getMoodles().getMoodleLevel(MoodleType.HeavyLoad) * 13;
                float0 += character.getMoodles().getMoodleLevel(MoodleType.Pain) * 5;
            }

            BodyPart bodyPart = character.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
            if (bodyPart.getAdditionalPain(true) > 20.0F) {
                float0 += (bodyPart.getAdditionalPain(true) - 20.0F) / 10.0F;
            }

            if (character.Traits.Clumsy.isSet()) {
                float0 += 10.0F;
            }

            if (character.Traits.Graceful.isSet()) {
                float0 -= 10.0F;
            }

            if (character.Traits.VeryUnderweight.isSet()) {
                float0 += 20.0F;
            }

            if (character.Traits.Underweight.isSet()) {
                float0 += 10.0F;
            }

            if (character.Traits.Obese.isSet()) {
                float0 += 20.0F;
            }

            if (character.Traits.Overweight.isSet()) {
                float0 += 10.0F;
            }

            float0 -= character.getPerkLevel(PerkFactory.Perks.Fitness);
            return Rand.Next(100) < float0;
        }
    }

    private int countZombiesClimbingOver(IsoObject object) {
        if (object != null && object.getSquare() != null) {
            int int0 = 0;
            IsoGridSquare square = object.getSquare();
            int0 += this.countZombiesClimbingOver(object, square);
            if (object.getProperties().Is(IsoFlagType.HoppableN)) {
                square = square.getAdjacentSquare(IsoDirections.N);
            } else {
                square = square.getAdjacentSquare(IsoDirections.W);
            }

            return int0 + this.countZombiesClimbingOver(object, square);
        } else {
            return 0;
        }
    }

    private int countZombiesClimbingOver(IsoObject object, IsoGridSquare square) {
        if (square == null) {
            return 0;
        } else {
            int int0 = 0;

            for (int int1 = 0; int1 < square.getMovingObjects().size(); int1++) {
                IsoZombie zombie0 = Type.tryCastTo(square.getMovingObjects().get(int1), IsoZombie.class);
                if (zombie0 != null && zombie0.target != null && zombie0.isCurrentState(this) && this.getFence(zombie0) == object) {
                    int0++;
                }
            }

            return int0;
        }
    }

    private boolean isMetalFence(IsoObject object) {
        if (object != null && object.getProperties() != null) {
            PropertyContainer propertyContainer = object.getProperties();
            String string0 = propertyContainer.Val("Material");
            String string1 = propertyContainer.Val("Material2");
            String string2 = propertyContainer.Val("Material3");
            if ("MetalBars".equals(string0) || "MetalBars".equals(string1) || "MetalBars".equals(string2)) {
                return true;
            } else if (!"MetalWire".equals(string0) && !"MetalWire".equals(string1) && !"MetalWire".equals(string2)) {
                if (object instanceof IsoThumpable && object.hasModData()) {
                    KahluaTableIterator kahluaTableIterator = object.getModData().iterator();

                    while (kahluaTableIterator.advance()) {
                        String string3 = Type.tryCastTo(kahluaTableIterator.getKey(), String.class);
                        if (string3 != null && string3.contains("MetalPipe")) {
                            return true;
                        }
                    }
                }

                return false;
            } else {
                return true;
            }
        } else {
            return false;
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

        IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, int2);
        boolean boolean0 = false;
        boolean boolean1 = square != null && square.Is(IsoFlagType.solidtrans);
        boolean boolean2 = square != null && square.TreatAsSolidFloor();
        boolean boolean3 = square != null && owner.canClimbDownSheetRope(square);
        hashMap.put(PARAM_START_X, int0);
        hashMap.put(PARAM_START_Y, int1);
        hashMap.put(PARAM_Z, int2);
        hashMap.put(PARAM_END_X, int3);
        hashMap.put(PARAM_END_Y, int4);
        hashMap.put(PARAM_DIR, dir);
        hashMap.put(PARAM_ZOMBIE_ON_FLOOR, Boolean.FALSE);
        hashMap.put(PARAM_PREV_STATE, owner.getCurrentState());
        hashMap.put(PARAM_SCRATCH, boolean0 ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_COUNTER, boolean1 ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_SOLID_FLOOR, boolean2 ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_SHEET_ROPE, boolean3 ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_RUN, owner.isRunning() ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_SPRINT, owner.isSprinting() ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_COLLIDABLE, Boolean.FALSE);
    }
}
