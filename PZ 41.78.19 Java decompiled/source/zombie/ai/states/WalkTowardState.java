// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import java.util.HashMap;
import org.joml.Vector3f;
import zombie.ai.State;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.gameStates.IngameState;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.util.Type;
import zombie.vehicles.PolygonalMap2;

public final class WalkTowardState extends State {
    private static final WalkTowardState _instance = new WalkTowardState();
    private static final Integer PARAM_IGNORE_OFFSET = 0;
    private static final Integer PARAM_IGNORE_TIME = 1;
    private static final Integer PARAM_TICK_COUNT = 2;
    private final Vector2 temp = new Vector2();
    private final Vector3f worldPos = new Vector3f();

    public static WalkTowardState instance() {
        return _instance;
    }

    @Override
    public void enter(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        if (hashMap.get(PARAM_IGNORE_OFFSET) == null) {
            hashMap.put(PARAM_IGNORE_OFFSET, Boolean.FALSE);
            hashMap.put(PARAM_IGNORE_TIME, 0L);
        }

        if (hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.TRUE && System.currentTimeMillis() - (Long)hashMap.get(PARAM_IGNORE_TIME) > 3000L) {
            hashMap.put(PARAM_IGNORE_OFFSET, Boolean.FALSE);
            hashMap.put(PARAM_IGNORE_TIME, 0L);
        }

        hashMap.put(PARAM_TICK_COUNT, IngameState.instance.numberTicks);
        if (((IsoZombie)owner).isUseless()) {
            owner.changeState(ZombieIdleState.instance());
        }

        owner.getPathFindBehavior2().walkingOnTheSpot.reset(owner.x, owner.y);
        ((IsoZombie)owner).networkAI.extraUpdate();
    }

    @Override
    public void execute(IsoGameCharacter owner) {
        HashMap hashMap = owner.getStateMachineParams(this);
        IsoZombie zombie0 = (IsoZombie)owner;
        if (!zombie0.bCrawling) {
            owner.setOnFloor(false);
        }

        IsoGameCharacter character = Type.tryCastTo(zombie0.target, IsoGameCharacter.class);
        if (zombie0.target != null) {
            if (zombie0.isTargetLocationKnown()) {
                if (character != null) {
                    zombie0.getPathFindBehavior2().pathToCharacter(character);
                    if (character.getVehicle() != null && zombie0.DistToSquared(zombie0.target) < 16.0F) {
                        Vector3f vector3f = character.getVehicle().chooseBestAttackPosition(character, zombie0, this.worldPos);
                        if (vector3f == null) {
                            zombie0.setVariable("bMoving", false);
                            return;
                        }

                        if (Math.abs(owner.x - zombie0.getPathFindBehavior2().getTargetX()) > 0.1F
                            || Math.abs(owner.y - zombie0.getPathFindBehavior2().getTargetY()) > 0.1F) {
                            zombie0.setVariable("bPathfind", true);
                            zombie0.setVariable("bMoving", false);
                            return;
                        }
                    }
                }
            } else if (zombie0.LastTargetSeenX != -1
                && !owner.getPathFindBehavior2().isTargetLocation(zombie0.LastTargetSeenX + 0.5F, zombie0.LastTargetSeenY + 0.5F, zombie0.LastTargetSeenZ)) {
                owner.pathToLocation(zombie0.LastTargetSeenX, zombie0.LastTargetSeenY, zombie0.LastTargetSeenZ);
            }
        }

        if (owner.getPathTargetX() == (int)owner.getX() && owner.getPathTargetY() == (int)owner.getY()) {
            if (zombie0.target == null) {
                zombie0.setVariable("bPathfind", false);
                zombie0.setVariable("bMoving", false);
                return;
            }

            if ((int)zombie0.target.getZ() != (int)owner.getZ()) {
                zombie0.setVariable("bPathfind", true);
                zombie0.setVariable("bMoving", false);
                return;
            }
        }

        boolean boolean0 = owner.isCollidedWithVehicle();
        if (character != null && character.getVehicle() != null && character.getVehicle().isCharacterAdjacentTo(owner)) {
            boolean0 = false;
        }

        boolean boolean1 = owner.isCollidedThisFrame();
        if (boolean1 && hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
            hashMap.put(PARAM_IGNORE_OFFSET, Boolean.TRUE);
            hashMap.put(PARAM_IGNORE_TIME, System.currentTimeMillis());
            float float0 = zombie0.getPathFindBehavior2().getTargetX();
            float float1 = zombie0.getPathFindBehavior2().getTargetY();
            float float2 = zombie0.z;
            boolean1 = !this.isPathClear(owner, float0, float1, float2);
        }

        if (!boolean1 && !boolean0) {
            this.temp.x = zombie0.getPathFindBehavior2().getTargetX();
            this.temp.y = zombie0.getPathFindBehavior2().getTargetY();
            this.temp.x = this.temp.x - zombie0.getX();
            this.temp.y = this.temp.y - zombie0.getY();
            float float3 = this.temp.getLength();
            if (float3 < 0.25F) {
                owner.x = zombie0.getPathFindBehavior2().getTargetX();
                owner.y = zombie0.getPathFindBehavior2().getTargetY();
                owner.nx = owner.x;
                owner.ny = owner.y;
                float3 = 0.0F;
            }

            if (float3 < 0.025F) {
                zombie0.setVariable("bPathfind", false);
                zombie0.setVariable("bMoving", false);
            } else {
                if (!GameServer.bServer && !zombie0.bCrawling && hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
                    float float4 = Math.min(float3 / 2.0F, 4.0F);
                    float float5 = (owner.getID() + zombie0.ZombieID) % 20 / 10.0F - 1.0F;
                    float float6 = (zombie0.getID() + zombie0.ZombieID) % 20 / 10.0F - 1.0F;
                    this.temp.x = this.temp.x + zombie0.getX();
                    this.temp.y = this.temp.y + zombie0.getY();
                    this.temp.x += float5 * float4;
                    this.temp.y += float6 * float4;
                    this.temp.x = this.temp.x - zombie0.getX();
                    this.temp.y = this.temp.y - zombie0.getY();
                }

                zombie0.bRunning = false;
                this.temp.normalize();
                if (zombie0.bCrawling) {
                    if (zombie0.getVariableString("TurnDirection").isEmpty()) {
                        zombie0.setForwardDirection(this.temp);
                    }
                } else {
                    zombie0.setDir(IsoDirections.fromAngle(this.temp));
                    zombie0.setForwardDirection(this.temp);
                }

                if (owner.getPathFindBehavior2().walkingOnTheSpot.check(owner.x, owner.y)) {
                    owner.setVariable("bMoving", false);
                }

                long long0 = (Long)hashMap.get(PARAM_TICK_COUNT);
                if (IngameState.instance.numberTicks - long0 == 2L) {
                    zombie0.parameterZombieState.setState(ParameterZombieState.State.Idle);
                }
            }
        } else {
            zombie0.AllowRepathDelay = 0.0F;
            zombie0.pathToLocation(owner.getPathTargetX(), owner.getPathTargetY(), owner.getPathTargetZ());
            if (!zombie0.getVariableBoolean("bPathfind")) {
                zombie0.setVariable("bPathfind", true);
                zombie0.setVariable("bMoving", false);
            }
        }
    }

    @Override
    public void exit(IsoGameCharacter owner) {
        owner.setVariable("bMoving", false);
        ((IsoZombie)owner).networkAI.extraUpdate();
    }

    @Override
    public void animEvent(IsoGameCharacter owner, AnimEvent event) {
    }

    /**
     * Return TRUE if the owner is currently moving.  Defaults to FALSE
     */
    @Override
    public boolean isMoving(IsoGameCharacter owner) {
        return true;
    }

    private boolean isPathClear(IsoGameCharacter character, float float0, float float1, float float2) {
        int int0 = (int)float0 / 10;
        int int1 = (int)float1 / 10;
        IsoChunk chunk = GameServer.bServer
            ? ServerMap.instance.getChunk(int0, int1)
            : IsoWorld.instance.CurrentCell.getChunkForGridSquare((int)float0, (int)float1, (int)float2);
        if (chunk != null) {
            int int2 = 1;
            int2 |= 2;
            return !PolygonalMap2.instance
                .lineClearCollide(character.getX(), character.getY(), float0, float1, (int)float2, character.getPathFindBehavior2().getTargetChar(), int2);
        } else {
            return false;
        }
    }

    public boolean calculateTargetLocation(IsoZombie zomb, Vector2 location) {
        assert zomb.isCurrentState(this);

        HashMap hashMap = zomb.getStateMachineParams(this);
        location.x = zomb.getPathFindBehavior2().getTargetX();
        location.y = zomb.getPathFindBehavior2().getTargetY();
        this.temp.set(location);
        this.temp.x = this.temp.x - zomb.getX();
        this.temp.y = this.temp.y - zomb.getY();
        float float0 = this.temp.getLength();
        if (float0 < 0.025F) {
            return false;
        } else if (!GameServer.bServer && !zomb.bCrawling && hashMap.get(PARAM_IGNORE_OFFSET) == Boolean.FALSE) {
            float float1 = Math.min(float0 / 2.0F, 4.0F);
            float float2 = (zomb.getID() + zomb.ZombieID) % 20 / 10.0F - 1.0F;
            float float3 = (zomb.getID() + zomb.ZombieID) % 20 / 10.0F - 1.0F;
            location.x += float2 * float1;
            location.y += float3 * float1;
            return true;
        } else {
            return false;
        }
    }
}
