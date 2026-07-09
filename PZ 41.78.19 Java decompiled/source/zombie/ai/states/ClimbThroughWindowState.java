// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import fmod.fmod.FMODManager;
import java.util.HashMap;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.MoveDeltaModifiers;
import zombie.characters.skills.PerkFactory;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.util.Type;

public final class ClimbThroughWindowState extends State {
    private static final ClimbThroughWindowState _instance = new ClimbThroughWindowState();
    static final Integer PARAM_START_X = 0;
    static final Integer PARAM_START_Y = 1;
    static final Integer PARAM_Z = 2;
    static final Integer PARAM_OPPOSITE_X = 3;
    static final Integer PARAM_OPPOSITE_Y = 4;
    static final Integer PARAM_DIR = 5;
    static final Integer PARAM_ZOMBIE_ON_FLOOR = 6;
    static final Integer PARAM_PREV_STATE = 7;
    static final Integer PARAM_SCRATCH = 8;
    static final Integer PARAM_COUNTER = 9;
    static final Integer PARAM_SOLID_FLOOR = 10;
    static final Integer PARAM_SHEET_ROPE = 11;
    static final Integer PARAM_END_X = 12;
    static final Integer PARAM_END_Y = 13;

    public static ClimbThroughWindowState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        owner.setIgnoreMovement(true);
        owner.setHideWeaponModel(true);
        HashMap hashMap = owner.getStateMachineParams(this);
        boolean boolean0 = hashMap.get(PARAM_COUNTER) == Boolean.TRUE;
        owner.setVariable("ClimbWindowStarted", false);
        owner.setVariable("ClimbWindowEnd", false);
        owner.setVariable("ClimbWindowFinished", false);
        owner.clearVariable("ClimbWindowGetUpBack");
        owner.clearVariable("ClimbWindowGetUpFront");
        owner.setVariable("ClimbWindowOutcome", boolean0 ? "obstacle" : "success");
        owner.clearVariable("ClimbWindowFlopped");
        IsoZombie zombie0 = Type.tryCastTo(owner, IsoZombie.class);
        if (!boolean0 && zombie0 != null && zombie0.shouldDoFenceLunge()) {
            this.setLungeXVars(zombie0);
            owner.setVariable("ClimbWindowOutcome", "lunge");
        }

        if (hashMap.get(PARAM_SOLID_FLOOR) == Boolean.FALSE) {
            owner.setVariable("ClimbWindowOutcome", "fall");
        }

        if (!(owner instanceof IsoZombie) && hashMap.get(PARAM_SHEET_ROPE) == Boolean.TRUE) {
            owner.setVariable("ClimbWindowOutcome", "rope");
        }

        if (owner instanceof IsoPlayer && ((IsoPlayer)owner).isLocalPlayer()) {
            ((IsoPlayer)owner).dirtyRecalcGridStackTime = 20.0F;
        }
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (!this.isWindowClosing(owner)) {
            IsoDirections directions = (IsoDirections)hashMap.get(PARAM_DIR);
            owner.setDir(directions);
            String string = owner.getVariableString("ClimbWindowOutcome");
            if (owner instanceof IsoZombie) {
                boolean boolean0 = hashMap.get(PARAM_ZOMBIE_ON_FLOOR) == Boolean.TRUE;
                if (!owner.isFallOnFront() && boolean0) {
                    int int0 = (Integer)hashMap.get(PARAM_OPPOSITE_X);
                    int int1 = (Integer)hashMap.get(PARAM_OPPOSITE_Y);
                    int int2 = (Integer)hashMap.get(PARAM_Z);
                    IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
                    if (square0 != null && square0.getBrokenGlass() != null) {
                        owner.addBlood(BloodBodyPartType.Head, true, true, true);
                        owner.addBlood(BloodBodyPartType.Head, true, true, true);
                        owner.addBlood(BloodBodyPartType.Head, true, true, true);
                        owner.addBlood(BloodBodyPartType.Head, true, true, true);
                        owner.addBlood(BloodBodyPartType.Head, true, true, true);
                        owner.addBlood(BloodBodyPartType.Neck, true, true, true);
                        owner.addBlood(BloodBodyPartType.Neck, true, true, true);
                        owner.addBlood(BloodBodyPartType.Neck, true, true, true);
                        owner.addBlood(BloodBodyPartType.Neck, true, true, true);
                        owner.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
                        owner.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
                        owner.addBlood(BloodBodyPartType.Torso_Upper, true, true, true);
                    }
                }

                owner.setOnFloor(boolean0);
                ((IsoZombie)owner).setKnockedDown(boolean0);
                owner.setFallOnFront(boolean0);
            }

            float float0 = ((Integer)hashMap.get(PARAM_START_X)).intValue() + 0.5F;
            float float1 = ((Integer)hashMap.get(PARAM_START_Y)).intValue() + 0.5F;
            if (!owner.getVariableBoolean("ClimbWindowStarted")) {
                if (owner.x != float0 && (directions == IsoDirections.N || directions == IsoDirections.S)) {
                    this.slideX(owner, float0);
                }

                if (owner.y != float1 && (directions == IsoDirections.W || directions == IsoDirections.E)) {
                    this.slideY(owner, float1);
                }
            }

            if (owner instanceof IsoPlayer && string.equalsIgnoreCase("obstacle")) {
                float float2 = ((Integer)hashMap.get(PARAM_END_X)).intValue() + 0.5F;
                float float3 = ((Integer)hashMap.get(PARAM_END_Y)).intValue() + 0.5F;
                if (owner.DistToSquared(float2, float3) < 0.5625F) {
                    owner.setVariable("ClimbWindowOutcome", "obstacleEnd");
                }
            }

            if (owner instanceof IsoPlayer
                && !owner.getVariableBoolean("ClimbWindowEnd")
                && !"fallfront".equals(string)
                && !"back".equals(string)
                && !"fallback".equals(string)) {
                int int3 = (Integer)hashMap.get(PARAM_OPPOSITE_X);
                int int4 = (Integer)hashMap.get(PARAM_OPPOSITE_Y);
                int int5 = (Integer)hashMap.get(PARAM_Z);
                IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, int5);
                if (square1 != null) {
                    this.checkForFallingBack(square1, owner);
                    if (square1 != owner.getSquare() && square1.TreatAsSolidFloor()) {
                        this.checkForFallingFront(owner.getSquare(), owner);
                    }
                }
            }

            if (owner.getVariableBoolean("ClimbWindowStarted")
                && !"back".equals(string)
                && !"fallback".equals(string)
                && !"lunge".equals(string)
                && !"obstacle".equals(string)
                && !"obstacleEnd".equals(string)) {
                float float4 = ((Integer)hashMap.get(PARAM_START_X)).intValue();
                float float5 = ((Integer)hashMap.get(PARAM_START_Y)).intValue();
                switch (directions) {
                    case N:
                        float5 -= 0.1F;
                        break;
                    case S:
                        float5++;
                        break;
                    case W:
                        float4 -= 0.1F;
                        break;
                    case E:
                        float4++;
                }

                if ((int)owner.x != (int)float4 && (directions == IsoDirections.W || directions == IsoDirections.E)) {
                    this.slideX(owner, float4);
                }

                if ((int)owner.y != (int)float5 && (directions == IsoDirections.N || directions == IsoDirections.S)) {
                    this.slideY(owner, float5);
                }
            }

            if (owner.getVariableBoolean("ClimbWindowStarted") && hashMap.get(PARAM_SCRATCH) == Boolean.TRUE) {
                hashMap.put(PARAM_SCRATCH, Boolean.FALSE);
                owner.getBodyDamage().setScratchedWindow();
            }
        }
    }

    private void checkForFallingBack(IsoGridSquare square, IsoGameCharacter character) {
        for (int int0 = 0; int0 < square.getMovingObjects().size(); int0++) {
            IsoMovingObject movingObject = square.getMovingObjects().get(int0);
            IsoZombie zombie0 = Type.tryCastTo(movingObject, IsoZombie.class);
            if (zombie0 != null && !zombie0.isOnFloor() && !zombie0.isSitAgainstWall()) {
                if (!zombie0.isVariable("AttackOutcome", "success") && Rand.Next(5 + character.getPerkLevel(PerkFactory.Perks.Fitness)) != 0) {
                    zombie0.playHurtSound();
                    character.setVariable("ClimbWindowOutcome", "back");
                } else {
                    zombie0.playHurtSound();
                    character.setVariable("ClimbWindowOutcome", "fallback");
                }
            }
        }
    }

    private void checkForFallingFront(IsoGridSquare square, IsoGameCharacter character) {
        for (int int0 = 0; int0 < square.getMovingObjects().size(); int0++) {
            IsoMovingObject movingObject = square.getMovingObjects().get(int0);
            IsoZombie zombie0 = Type.tryCastTo(movingObject, IsoZombie.class);
            if (zombie0 != null && !zombie0.isOnFloor() && !zombie0.isSitAgainstWall() && zombie0.isVariable("AttackOutcome", "success")) {
                zombie0.playHurtSound();
                character.setVariable("ClimbWindowOutcome", "fallfront");
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setIgnoreMovement(false);
        owner.setHideWeaponModel(false);
        HashMap hashMap = owner.getStateMachineParams(this);
        if (owner.isVariable("ClimbWindowOutcome", "fall")
            || owner.isVariable("ClimbWindowOutcome", "fallback")
            || owner.isVariable("ClimbWindowOutcome", "fallfront")) {
            owner.setHitReaction("");
        }

        owner.clearVariable("ClimbWindowFinished");
        owner.clearVariable("ClimbWindowOutcome");
        owner.clearVariable("ClimbWindowStarted");
        owner.clearVariable("ClimbWindowFlopped");
        if (owner instanceof IsoZombie) {
            owner.setOnFloor(false);
            ((IsoZombie)owner).setKnockedDown(false);
        }

        IsoZombie zombie0 = Type.tryCastTo(owner, IsoZombie.class);
        if (zombie0 != null) {
            zombie0.AllowRepathDelay = 0.0F;
            if (hashMap.get(PARAM_PREV_STATE) == PathFindState.instance()) {
                if (owner.getPathFindBehavior2().getTargetChar() == null) {
                    owner.setVariable("bPathFind", true);
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

    public void slideX(IsoGameCharacter owner, float x) {
        float float0 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
        float0 = x > owner.x ? Math.min(float0, x - owner.x) : Math.max(-float0, x - owner.x);
        owner.x += float0;
        owner.nx = owner.x;
    }

    public void slideY(IsoGameCharacter owner, float y) {
        float float0 = 0.05F * GameTime.getInstance().getMultiplier() / 1.6F;
        float0 = y > owner.y ? Math.min(float0, y - owner.y) : Math.max(-float0, y - owner.y);
        owner.y += float0;
        owner.ny = owner.y;
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoZombie zombie0 = Type.tryCastTo(owner, IsoZombie.class);
        if (event.m_EventName.equalsIgnoreCase("CheckAttack") && zombie0 != null && zombie0.target instanceof IsoGameCharacter) {
            ((IsoGameCharacter)zombie0.target).attackFromWindowsLunge(zombie0);
        }

        if (event.m_EventName.equalsIgnoreCase("OnFloor") && zombie0 != null) {
            boolean boolean0 = Boolean.parseBoolean(event.m_ParameterValue);
            hashMap.put(PARAM_ZOMBIE_ON_FLOOR, boolean0);
            if (boolean0) {
                this.setLungeXVars(zombie0);
                IsoThumpable thumpable = Type.tryCastTo(this.getWindow(owner), IsoThumpable.class);
                if (thumpable != null && thumpable.getSquare() != null && zombie0.target != null) {
                    thumpable.Health = thumpable.Health - Rand.Next(10, 20);
                    if (thumpable.Health <= 0) {
                        thumpable.destroy();
                    }
                }

                owner.setVariable("ClimbWindowFlopped", true);
            }
        }

        if (event.m_EventName.equalsIgnoreCase("PlayWindowSound")) {
            if (!SoundManager.instance.isListenerInRange(owner.getX(), owner.getY(), 10.0F)) {
                return;
            }

            long long0 = owner.getEmitter().playSoundImpl(event.m_ParameterValue, null);
            owner.getEmitter().setParameterValue(long0, FMODManager.instance.getParameterDescription("TripObstacleType"), 9.0F);
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

    public IsoObject getWindow(IsoGameCharacter owner) {
        if (!owner.isCurrentState(this)) {
            return null;
        } else {
            HashMap hashMap = owner.getStateMachineParams(this);
            int int0 = (Integer)hashMap.get(PARAM_START_X);
            int int1 = (Integer)hashMap.get(PARAM_START_Y);
            int int2 = (Integer)hashMap.get(PARAM_Z);
            IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            int int3 = (Integer)hashMap.get(PARAM_END_X);
            int int4 = (Integer)hashMap.get(PARAM_END_Y);
            IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(int3, int4, int2);
            if (square0 != null && square1 != null) {
                Object object = square0.getWindowTo(square1);
                if (object == null) {
                    object = square0.getWindowThumpableTo(square1);
                }

                if (object == null) {
                    object = square0.getHoppableTo(square1);
                }

                return (IsoObject)object;
            } else {
                return null;
            }
        }
    }

    public boolean isWindowClosing(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (owner.getVariableBoolean("ClimbWindowStarted")) {
            return false;
        } else {
            int int0 = (Integer)hashMap.get(PARAM_START_X);
            int int1 = (Integer)hashMap.get(PARAM_START_Y);
            int int2 = (Integer)hashMap.get(PARAM_Z);
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            if (owner.getCurrentSquare() != square) {
                return false;
            } else {
                IsoWindow window = Type.tryCastTo(this.getWindow(owner), IsoWindow.class);
                if (window == null) {
                    return false;
                } else {
                    IsoGameCharacter character = window.getFirstCharacterClosing();
                    if (character != null && character.isVariable("CloseWindowOutcome", "success")) {
                        if (owner.isZombie()) {
                            owner.setHitReaction("HeadLeft");
                        } else {
                            owner.setVariable("ClimbWindowFinished", true);
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
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

        if (boolean1 && owner.getVariableBoolean("isTurning")) {
            modifiers.turnDelta = Math.max(modifiers.turnDelta, 5.0F);
        }
    }

    private boolean isFreeSquare(IsoGridSquare square) {
        return square != null && square.TreatAsSolidFloor() && !square.Is(IsoFlagType.solid) && !square.Is(IsoFlagType.solidtrans);
    }

    private boolean isObstacleSquare(IsoGridSquare square) {
        return square != null
            && square.TreatAsSolidFloor()
            && !square.Is(IsoFlagType.solid)
            && square.Is(IsoFlagType.solidtrans)
            && !square.Is(IsoFlagType.water);
    }

    private IsoGridSquare getFreeSquareAfterObstacles(IsoGridSquare square1, IsoDirections directions) {
        while (true) {
            IsoGridSquare square0 = square1.getAdjacentSquare(directions);
            if (square0 == null || square1.isSomethingTo(square0) || square1.getWindowFrameTo(square0) != null || square1.getWindowThumpableTo(square0) != null
                )
             {
                return null;
            }

            if (this.isFreeSquare(square0)) {
                return square0;
            }

            if (!this.isObstacleSquare(square0)) {
                return null;
            }

            square1 = square0;
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

    public boolean isPastInnerEdgeOfSquare(IsoGameCharacter owner, int x, int y, IsoDirections moveDir) {
        if (moveDir == IsoDirections.N) {
            return owner.y < y + 1 - 0.3F;
        } else if (moveDir == IsoDirections.S) {
            return owner.y > y + 0.3F;
        } else if (moveDir == IsoDirections.W) {
            return owner.x < x + 1 - 0.3F;
        } else if (moveDir == IsoDirections.E) {
            return owner.x > x + 0.3F;
        } else {
            throw new IllegalArgumentException("unhandled direction");
        }
    }

    public boolean isPastOuterEdgeOfSquare(IsoGameCharacter owner, int x, int y, IsoDirections moveDir) {
        if (moveDir == IsoDirections.N) {
            return owner.y < y - 0.3F;
        } else if (moveDir == IsoDirections.S) {
            return owner.y > y + 1 + 0.3F;
        } else if (moveDir == IsoDirections.W) {
            return owner.x < x - 0.3F;
        } else if (moveDir == IsoDirections.E) {
            return owner.x > x + 1 + 0.3F;
        } else {
            throw new IllegalArgumentException("unhandled direction");
        }
    }

    public void setParams(IsoGameCharacter owner, IsoObject obj) {
        HashMap hashMap = owner.getStateMachineParams(this);
        hashMap.clear();
        boolean boolean0 = false;
        boolean boolean1;
        if (obj instanceof IsoWindow window) {
            boolean1 = window.north;
            if (owner instanceof IsoPlayer && window.isDestroyed() && !window.isGlassRemoved() && Rand.Next(2) == 0) {
                boolean0 = true;
            }
        } else if (obj instanceof IsoThumpable thumpable) {
            boolean1 = thumpable.north;
            if (owner instanceof IsoPlayer && thumpable.getName().equals("Barbed Fence") && Rand.Next(101) > 75) {
                boolean0 = true;
            }
        } else {
            if (!IsoWindowFrame.isWindowFrame(obj)) {
                throw new IllegalArgumentException("expected thumpable, window, or window-frame");
            }

            boolean1 = IsoWindowFrame.isWindowFrame(obj, true);
        }

        int int0 = obj.getSquare().getX();
        int int1 = obj.getSquare().getY();
        int int2 = obj.getSquare().getZ();
        int int3 = int0;
        int int4 = int1;
        int int5 = int0;
        int int6 = int1;
        IsoDirections directions;
        if (boolean1) {
            if (int1 < owner.getY()) {
                int6 = int1 - 1;
                directions = IsoDirections.N;
            } else {
                int4 = int1 - 1;
                directions = IsoDirections.S;
            }
        } else if (int0 < owner.getX()) {
            int5 = int0 - 1;
            directions = IsoDirections.W;
        } else {
            int3 = int0 - 1;
            directions = IsoDirections.E;
        }

        IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(int5, int6, int2);
        boolean boolean2 = square0 != null && square0.Is(IsoFlagType.solidtrans);
        boolean boolean3 = square0 != null && square0.TreatAsSolidFloor();
        boolean boolean4 = square0 != null && owner.canClimbDownSheetRope(square0);
        int int7 = int5;
        int int8 = int6;
        if (boolean2 && owner.isZombie()) {
            IsoGridSquare square1 = square0.getAdjacentSquare(directions);
            if (this.isFreeSquare(square1)
                && !square0.isSomethingTo(square1)
                && square0.getWindowFrameTo(square1) == null
                && square0.getWindowThumpableTo(square1) == null) {
                int7 = square1.x;
                int8 = square1.y;
            } else {
                boolean2 = false;
            }
        }

        if (boolean2 && !owner.isZombie()) {
            IsoGridSquare square2 = this.getFreeSquareAfterObstacles(square0, directions);
            if (square2 == null) {
                boolean2 = false;
            } else {
                int7 = square2.x;
                int8 = square2.y;
            }
        }

        hashMap.put(PARAM_START_X, int3);
        hashMap.put(PARAM_START_Y, int4);
        hashMap.put(PARAM_Z, int2);
        hashMap.put(PARAM_OPPOSITE_X, int5);
        hashMap.put(PARAM_OPPOSITE_Y, int6);
        hashMap.put(PARAM_END_X, int7);
        hashMap.put(PARAM_END_Y, int8);
        hashMap.put(PARAM_DIR, directions);
        hashMap.put(PARAM_ZOMBIE_ON_FLOOR, Boolean.FALSE);
        hashMap.put(PARAM_PREV_STATE, owner.getCurrentState());
        hashMap.put(PARAM_SCRATCH, boolean0 ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_COUNTER, boolean2 ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_SOLID_FLOOR, boolean3 ? Boolean.TRUE : Boolean.FALSE);
        hashMap.put(PARAM_SHEET_ROPE, boolean4 ? Boolean.TRUE : Boolean.FALSE);
    }
}
