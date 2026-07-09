// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.GameTime;
import zombie.ai.states.PlayerFallDownState;
import zombie.ai.states.PlayerKnockedDown;
import zombie.ai.states.PlayerOnGroundState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieOnGroundState;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;

public class HitReactionNetworkAI {
    private static final float G = 2.0F;
    private static final float DURATION = 600.0F;
    public final Vector2 startPosition = new Vector2();
    public final Vector2 finalPosition = new Vector2();
    public byte finalPositionZ = 0;
    public final Vector2 startDirection = new Vector2();
    public final Vector2 finalDirection = new Vector2();
    private float startAngle;
    private float finalAngle;
    private final IsoGameCharacter character;
    private long startTime;

    public HitReactionNetworkAI(IsoGameCharacter _character) {
        this.character = _character;
        this.startTime = 0L;
    }

    public boolean isSetup() {
        return this.finalPosition.x != 0.0F && this.finalPosition.y != 0.0F;
    }

    public boolean isStarted() {
        return this.startTime > 0L;
    }

    public void start() {
        if (this.isSetup() && !this.isStarted()) {
            this.startTime = GameTime.getServerTimeMills();
            if (this.startPosition.x != this.character.x || this.startPosition.y != this.character.y) {
                DebugLog.Multiplayer.warn("HitReaction start shifted");
            }

            DebugLog.Damage
                .trace(
                    "id=%d: %s / %s => %s", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()
                );
        }
    }

    public void finish() {
        if (this.startTime != 0L) {
            DebugLog.Damage
                .trace(
                    "id=%d: %s / %s => %s", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()
                );
        }

        this.startTime = 0L;
        this.setup(0.0F, 0.0F, (byte)0, 0.0F);
    }

    public void setup(float dropPositionX, float dropPositionY, byte dropPositionZ, Float angle) {
        this.startPosition.set(this.character.x, this.character.y);
        this.finalPosition.set(dropPositionX, dropPositionY);
        this.finalPositionZ = dropPositionZ;
        this.startDirection.set(this.character.getForwardDirection());
        this.startAngle = this.character.getAnimAngleRadians();
        Vector2 vector = new Vector2().set(this.finalPosition.x - this.startPosition.x, this.finalPosition.y - this.startPosition.y);
        if (angle == null) {
            vector.normalize();
            angle = vector.dot(this.character.getForwardDirection());
            PZMath.lerp(this.finalDirection, vector, this.character.getForwardDirection(), Math.abs(angle));
            IsoMovingObject.getVectorFromDirection(this.finalDirection, IsoDirections.fromAngle(this.finalDirection));
        } else {
            this.finalDirection.setLengthAndDirection(angle, 1.0F);
        }

        this.finalAngle = angle;
        if (this.isSetup()) {
            DebugLog.Damage
                .trace(
                    "id=%d: %s / %s => %s", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription()
                );
        }
    }

    private void moveInternal(float float0, float float1, float float2, float float3) {
        this.character.nx = float0;
        this.character.ny = float1;
        this.character.setDir(IsoDirections.fromAngle(float2, float3));
        this.character.setForwardDirection(float2, float3);
        this.character.getAnimationPlayer().SetForceDir(this.character.getForwardDirection());
    }

    public void moveFinal() {
        this.moveInternal(this.finalPosition.x, this.finalPosition.y, this.finalDirection.x, this.finalDirection.y);
        this.character.lx = this.character.nx = this.character.x = this.finalPosition.x;
        this.character.ly = this.character.ny = this.character.y = this.finalPosition.y;
        this.character
            .setCurrent(
                IsoWorld.instance.CurrentCell.getGridSquare((double)((int)this.finalPosition.x), (double)((int)this.finalPosition.y), (double)this.character.z)
            );
        DebugLog.Damage
            .trace("id=%d: %s / %s => %s", this.character.getOnlineID(), this.getActualDescription(), this.getStartDescription(), this.getFinalDescription());
    }

    public void move() {
        if (this.finalPositionZ != (byte)this.character.z) {
            DebugLog.Damage
                .trace("HitReaction interrupt id=%d: z-final:%d z-current=%d", this.character.getOnlineID(), this.finalPositionZ, (byte)this.character.z);
            this.finish();
        } else {
            float float0 = Math.min(1.0F, Math.max(0.0F, (float)(GameTime.getServerTimeMills() - this.startTime) / 600.0F));
            if (this.startPosition.x == this.finalPosition.x && this.startPosition.y == this.finalPosition.y) {
                float0 = 1.0F;
            }

            if (float0 < 1.0F) {
                float0 = (PZMath.gain(float0 * 0.5F + 0.5F, 2.0F) - 0.5F) * 2.0F;
                this.moveInternal(
                    PZMath.lerp(this.startPosition.x, this.finalPosition.x, float0),
                    PZMath.lerp(this.startPosition.y, this.finalPosition.y, float0),
                    PZMath.lerp(this.startDirection.x, this.finalDirection.x, float0),
                    PZMath.lerp(this.startDirection.y, this.finalDirection.y, float0)
                );
            } else {
                this.moveFinal();
                this.finish();
            }
        }
    }

    public boolean isDoSkipMovement() {
        if (this.character instanceof IsoZombie) {
            return this.character.isCurrentState(ZombieFallDownState.instance()) || this.character.isCurrentState(ZombieOnGroundState.instance());
        } else {
            return !(this.character instanceof IsoPlayer)
                ? false
                : this.character.isCurrentState(PlayerFallDownState.instance())
                    || this.character.isCurrentState(PlayerKnockedDown.instance())
                    || this.character.isCurrentState(PlayerOnGroundState.instance());
        }
    }

    private String getStartDescription() {
        return String.format(
            "start=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]",
            this.startPosition.x,
            this.startPosition.y,
            this.startDirection.x,
            this.startDirection.y,
            this.startAngle
        );
    }

    private String getFinalDescription() {
        return String.format(
            "final=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]",
            this.finalPosition.x,
            this.finalPosition.y,
            this.finalDirection.x,
            this.finalDirection.y,
            this.finalAngle
        );
    }

    private String getActualDescription() {
        return String.format(
            "actual=[ pos=( %f ; %f ) dir=( %f ; %f ) angle=%f ]",
            this.character.x,
            this.character.y,
            this.character.getForwardDirection().getX(),
            this.character.getForwardDirection().getY(),
            this.character.getAnimAngleRadians()
        );
    }

    public String getDescription() {
        return String.format(
            "start=%d | (x=%f,y=%f;a=%f;l=%f)",
            this.startTime,
            this.finalPosition.x,
            this.finalPosition.y,
            this.finalAngle,
            IsoUtils.DistanceTo(this.startPosition.x, this.startPosition.y, this.finalPosition.x, this.finalPosition.y)
        );
    }

    public static void CalcHitReactionWeapon(IsoGameCharacter wielder, IsoGameCharacter target, HandWeapon weapon) {
        HitReactionNetworkAI hitReactionNetworkAI = target.getHitReactionNetworkAI();
        if (target.isOnFloor()) {
            hitReactionNetworkAI.setup(target.x, target.y, (byte)target.z, target.getAnimAngleRadians());
        } else {
            Vector2 vector = new Vector2();
            Float float0 = target.calcHitDir(wielder, weapon, vector);
            if (target instanceof IsoPlayer) {
                vector.x = (vector.x + target.x + ((IsoPlayer)target).networkAI.targetX) * 0.5F;
                vector.y = (vector.y + target.y + ((IsoPlayer)target).networkAI.targetY) * 0.5F;
            } else {
                vector.x = vector.x + target.x;
                vector.y = vector.y + target.y;
            }

            vector.x = PZMath.roundFromEdges(vector.x);
            vector.y = PZMath.roundFromEdges(vector.y);
            if (PolygonalMap2.instance.lineClearCollide(target.x, target.y, vector.x, vector.y, (int)target.z, null, false, true)) {
                vector.x = target.x;
                vector.y = target.y;
            }

            hitReactionNetworkAI.setup(vector.x, vector.y, (byte)target.z, float0);
        }

        if (hitReactionNetworkAI.isSetup()) {
            hitReactionNetworkAI.start();
        }
    }

    public static void CalcHitReactionVehicle(IsoGameCharacter target, BaseVehicle vehicle) {
        HitReactionNetworkAI hitReactionNetworkAI = target.getHitReactionNetworkAI();
        if (!hitReactionNetworkAI.isStarted()) {
            if (target.isOnFloor()) {
                hitReactionNetworkAI.setup(target.x, target.y, (byte)target.z, target.getAnimAngleRadians());
            } else {
                Vector2 vector = new Vector2();
                target.calcHitDir(vector);
                if (target instanceof IsoPlayer) {
                    vector.x = (vector.x + target.x + ((IsoPlayer)target).networkAI.targetX) * 0.5F;
                    vector.y = (vector.y + target.y + ((IsoPlayer)target).networkAI.targetY) * 0.5F;
                } else {
                    vector.x = vector.x + target.x;
                    vector.y = vector.y + target.y;
                }

                vector.x = PZMath.roundFromEdges(vector.x);
                vector.y = PZMath.roundFromEdges(vector.y);
                if (PolygonalMap2.instance.lineClearCollide(target.x, target.y, vector.x, vector.y, (int)target.z, vehicle, false, true)) {
                    vector.x = target.x;
                    vector.y = target.y;
                }

                hitReactionNetworkAI.setup(vector.x, vector.y, (byte)target.z, null);
            }
        }

        if (hitReactionNetworkAI.isSetup()) {
            hitReactionNetworkAI.start();
        }
    }

    public void process(float dropPositionX, float dropPositionY, float dropPositionZ, float dropDirection) {
        this.setup(dropPositionX, dropPositionY, (byte)dropPositionZ, dropDirection);
        this.start();
        if (GameServer.bServer) {
            this.moveFinal();
            this.finish();
        }
    }
}
