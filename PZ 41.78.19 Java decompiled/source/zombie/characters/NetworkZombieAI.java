// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.GameTime;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverWallState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.LungeState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.ThumpState;
import zombie.ai.states.WalkTowardState;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.core.utils.UpdateTimer;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.packets.ZombiePacket;
import zombie.popman.NetworkZombieSimulator;
import zombie.vehicles.PathFindBehavior2;

public class NetworkZombieAI extends NetworkCharacterAI {
    private final UpdateTimer timer;
    private final PathFindBehavior2 pfb2;
    public final IsoZombie zombie;
    public boolean usePathFind = false;
    public float targetX = 0.0F;
    public float targetY = 0.0F;
    public int targetZ = 0;
    public boolean isClimbing;
    private byte flags;
    private byte direction;
    public final NetworkZombieMind mindSync;
    public short reanimatedBodyID;
    public boolean DebugInterfaceActive = false;

    public NetworkZombieAI(IsoGameCharacter character) {
        super(character);
        this.zombie = (IsoZombie)character;
        this.isClimbing = false;
        this.flags = 0;
        this.pfb2 = this.zombie.getPathFindBehavior2();
        this.timer = new UpdateTimer();
        this.mindSync = new NetworkZombieMind(this.zombie);
        character.ulBeatenVehicle.Reset(400L);
        this.reanimatedBodyID = -1;
    }

    @Override
    public void reset() {
        super.reset();
        this.usePathFind = true;
        this.targetX = this.zombie.getX();
        this.targetY = this.zombie.getY();
        this.targetZ = (byte)this.zombie.getZ();
        this.isClimbing = false;
        this.flags = 0;
        this.zombie.getHitDir().set(0.0F, 0.0F);
        this.reanimatedBodyID = -1;
    }

    public void extraUpdate() {
        NetworkZombieSimulator.getInstance().addExtraUpdate(this.zombie);
    }

    private long getUpdateTime() {
        return this.timer.getTime();
    }

    public void setUpdateTimer(float time) {
        this.timer.reset(PZMath.clamp((int)time, 200, 3800));
    }

    private void setUsingExtrapolation(ZombiePacket zombiePacket, int int0) {
        if (this.zombie.isMoving()) {
            Vector2 vector = this.zombie.dir.ToVector();
            this.zombie.networkCharacter.checkReset(int0);
            NetworkCharacter.Transform transform = this.zombie.networkCharacter.predict(500, int0, this.zombie.x, this.zombie.y, vector.x, vector.y);
            zombiePacket.x = transform.position.x;
            zombiePacket.y = transform.position.y;
            zombiePacket.z = (byte)this.zombie.z;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.Moving;
            this.setUpdateTimer(300.0F);
        } else {
            zombiePacket.x = this.zombie.x;
            zombiePacket.y = this.zombie.y;
            zombiePacket.z = (byte)this.zombie.z;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.Static;
            this.setUpdateTimer(2280.0F);
        }
    }

    private void setUsingThump(ZombiePacket zombiePacket) {
        zombiePacket.x = ((IsoObject)this.zombie.getThumpTarget()).getX();
        zombiePacket.y = ((IsoObject)this.zombie.getThumpTarget()).getY();
        zombiePacket.z = (byte)((IsoObject)this.zombie.getThumpTarget()).getZ();
        zombiePacket.moveType = NetworkVariables.PredictionTypes.Thump;
        this.setUpdateTimer(2280.0F);
    }

    private void setUsingClimb(ZombiePacket zombiePacket) {
        zombiePacket.x = this.zombie.getTarget().getX();
        zombiePacket.y = this.zombie.getTarget().getY();
        zombiePacket.z = (byte)this.zombie.getTarget().getZ();
        zombiePacket.moveType = NetworkVariables.PredictionTypes.Climb;
        this.setUpdateTimer(2280.0F);
    }

    private void setUsingLungeState(ZombiePacket zombiePacket, long long0) {
        if (this.zombie.target == null) {
            this.setUsingExtrapolation(zombiePacket, (int)long0);
        } else {
            float float0 = IsoUtils.DistanceTo(this.zombie.target.x, this.zombie.target.y, this.zombie.x, this.zombie.y);
            if (float0 > 5.0F) {
                zombiePacket.x = (this.zombie.x + this.zombie.target.x) * 0.5F;
                zombiePacket.y = (this.zombie.y + this.zombie.target.y) * 0.5F;
                zombiePacket.z = (byte)this.zombie.target.z;
                float float1 = float0 * 0.5F / 5.0E-4F * this.zombie.speedMod;
                zombiePacket.moveType = NetworkVariables.PredictionTypes.LungeHalf;
                this.setUpdateTimer(float1 * 0.6F);
            } else {
                zombiePacket.x = this.zombie.target.x;
                zombiePacket.y = this.zombie.target.y;
                zombiePacket.z = (byte)this.zombie.target.z;
                float float2 = float0 / 5.0E-4F * this.zombie.speedMod;
                zombiePacket.moveType = NetworkVariables.PredictionTypes.Lunge;
                this.setUpdateTimer(float2 * 0.6F);
            }
        }
    }

    private void setUsingWalkTowardState(ZombiePacket zombiePacket) {
        float float0;
        if (this.zombie.getPath2() == null) {
            float float1 = this.pfb2.getPathLength();
            if (float1 > 5.0F) {
                zombiePacket.x = (this.zombie.x + this.pfb2.getTargetX()) * 0.5F;
                zombiePacket.y = (this.zombie.y + this.pfb2.getTargetY()) * 0.5F;
                zombiePacket.z = (byte)this.pfb2.getTargetZ();
                float0 = float1 * 0.5F / 5.0E-4F * this.zombie.speedMod;
                zombiePacket.moveType = NetworkVariables.PredictionTypes.WalkHalf;
            } else {
                zombiePacket.x = this.pfb2.getTargetX();
                zombiePacket.y = this.pfb2.getTargetY();
                zombiePacket.z = (byte)this.pfb2.getTargetZ();
                float0 = float1 / 5.0E-4F * this.zombie.speedMod;
                zombiePacket.moveType = NetworkVariables.PredictionTypes.Walk;
            }
        } else {
            zombiePacket.x = this.pfb2.pathNextX;
            zombiePacket.y = this.pfb2.pathNextY;
            zombiePacket.z = (byte)this.zombie.z;
            float0 = IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, this.pfb2.pathNextX, this.pfb2.pathNextY) / 5.0E-4F * this.zombie.speedMod;
            zombiePacket.moveType = NetworkVariables.PredictionTypes.Walk;
        }

        this.setUpdateTimer(float0 * 0.6F);
    }

    private void setUsingPathFindState(ZombiePacket zombiePacket) {
        zombiePacket.x = this.pfb2.pathNextX;
        zombiePacket.y = this.pfb2.pathNextY;
        zombiePacket.z = (byte)this.zombie.z;
        float float0 = IsoUtils.DistanceTo(this.zombie.x, this.zombie.y, this.pfb2.pathNextX, this.pfb2.pathNextY) / 5.0E-4F * this.zombie.speedMod;
        zombiePacket.moveType = NetworkVariables.PredictionTypes.PathFind;
        this.setUpdateTimer(float0 * 0.6F);
    }

    public void set(ZombiePacket packet) {
        int int0 = (int)(GameTime.getServerTime() / 1000000L);
        packet.booleanVariables = NetworkZombieVariables.getBooleanVariables(this.zombie);
        packet.realHealth = (short)NetworkZombieVariables.getInt(this.zombie, (short)0);
        packet.target = (short)NetworkZombieVariables.getInt(this.zombie, (short)1);
        packet.speedMod = (short)NetworkZombieVariables.getInt(this.zombie, (short)2);
        packet.timeSinceSeenFlesh = NetworkZombieVariables.getInt(this.zombie, (short)3);
        packet.smParamTargetAngle = NetworkZombieVariables.getInt(this.zombie, (short)4);
        packet.walkType = NetworkVariables.WalkType.fromString(this.zombie.getVariableString("zombieWalkType"));
        packet.realX = this.zombie.x;
        packet.realY = this.zombie.y;
        packet.realZ = (byte)this.zombie.z;
        this.zombie.realState = NetworkVariables.ZombieState.fromString(this.zombie.getAdvancedAnimator().getCurrentStateName());
        packet.realState = this.zombie.realState;
        packet.reanimatedBodyID = this.reanimatedBodyID;
        if (this.zombie.getThumpTarget() != null && this.zombie.getCurrentState() == ThumpState.instance()) {
            this.setUsingThump(packet);
        } else if (this.zombie.getTarget() == null
            || this.isClimbing
            || this.zombie.getCurrentState() != ClimbOverFenceState.instance()
                && this.zombie.getCurrentState() != ClimbOverWallState.instance()
                && this.zombie.getCurrentState() != ClimbThroughWindowState.instance()) {
            if (this.zombie.getCurrentState() == WalkTowardState.instance()) {
                this.setUsingWalkTowardState(packet);
            } else if (this.zombie.getCurrentState() == LungeState.instance()) {
                this.setUsingLungeState(packet, int0);
            } else if (this.zombie.getCurrentState() == PathFindState.instance() && this.zombie.isMoving()) {
                this.setUsingPathFindState(packet);
            } else {
                this.setUsingExtrapolation(packet, int0);
            }
        } else {
            this.setUsingClimb(packet);
            this.isClimbing = true;
        }

        Vector2 vector = this.zombie.dir.ToVector();
        this.zombie.networkCharacter.updateExtrapolationPoint(int0, this.zombie.x, this.zombie.y, vector.x, vector.y);
        if (DebugOptions.instance.MultiplayerLogPrediction.getValue() && Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, getPredictionDebug(this.zombie, packet, int0, this.getUpdateTime()));
        }
    }

    public void parse(ZombiePacket packet) {
        int int0 = (int)(GameTime.getServerTime() / 1000000L);
        if (DebugOptions.instance.MultiplayerLogPrediction.getValue()) {
            this.zombie.getNetworkCharacterAI().addTeleportData(int0, getPredictionDebug(this.zombie, packet, int0, int0));
        }

        if (this.usePathFind) {
            this.pfb2.pathToLocationF(packet.x, packet.y, packet.z);
            this.pfb2.walkingOnTheSpot.reset(this.zombie.x, this.zombie.y);
        }

        this.targetX = packet.x;
        this.targetY = packet.y;
        this.targetZ = packet.z;
        this.predictionType = packet.moveType;
        NetworkZombieVariables.setInt(this.zombie, (short)1, packet.target);
        NetworkZombieVariables.setInt(this.zombie, (short)3, packet.timeSinceSeenFlesh);
        if (this.zombie.isRemoteZombie()) {
            NetworkZombieVariables.setInt(this.zombie, (short)2, packet.speedMod);
            NetworkZombieVariables.setInt(this.zombie, (short)4, packet.smParamTargetAngle);
            NetworkZombieVariables.setBooleanVariables(this.zombie, packet.booleanVariables);
            this.zombie.setWalkType(packet.walkType.toString());
            this.zombie.realState = packet.realState;
        }

        this.zombie.realx = packet.realX;
        this.zombie.realy = packet.realY;
        this.zombie.realz = packet.realZ;
        if ((IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, this.zombie.realx, this.zombie.realy) > 9.0F || this.zombie.z != this.zombie.realz)
            && (
                this.zombie.isRemoteZombie()
                    || IsoPlayer.getInstance() != null
                        && IsoUtils.DistanceToSquared(this.zombie.x, this.zombie.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y) > 2.0F
            )) {
            NetworkTeleport.teleport(this.zombie, NetworkTeleport.Type.teleportation, this.zombie.realx, this.zombie.realy, this.zombie.realz, 1.0F);
        }
    }

    public void preupdate() {
        if (GameClient.bClient) {
            if (this.zombie.target != null) {
                this.zombie.setTargetSeenTime(this.zombie.getTargetSeenTime() + GameTime.getInstance().getRealworldSecondsSinceLastUpdate());
            }
        } else if (GameServer.bServer) {
            byte byte0 = (byte)((this.zombie.getVariableBoolean("bMoving") ? 1 : 0) | (this.zombie.getVariableBoolean("bPathfind") ? 2 : 0));
            if (this.flags != byte0) {
                this.flags = byte0;
                this.extraUpdate();
            }

            byte byte1 = (byte)IsoDirections.fromAngleActual(this.zombie.getForwardDirection()).index();
            if (this.direction != byte1) {
                this.direction = byte1;
                this.extraUpdate();
            }
        }
    }

    public static String getPredictionDebug(IsoGameCharacter character, ZombiePacket packet, int currentTime, long next) {
        return String.format(
            "Prediction Z_%d [type=%s, distance=%f], time [current=%d, next=%d], states [current=%s, previous=%s]",
            packet.id,
            packet.moveType.toString(),
            IsoUtils.DistanceTo(character.x, character.y, packet.x, packet.y),
            currentTime,
            next - currentTime,
            character.getCurrentStateName(),
            character.getPreviousStateName()
        );
    }
}
