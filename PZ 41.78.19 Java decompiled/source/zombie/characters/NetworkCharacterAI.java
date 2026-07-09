// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import zombie.GameTime;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.core.utils.UpdateLimit;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.PacketValidator;
import zombie.network.packets.DeadCharacterPacket;
import zombie.network.packets.hit.IMovable;
import zombie.network.packets.hit.VehicleHitPacket;

public abstract class NetworkCharacterAI {
    private static final short VEHICLE_HIT_DELAY_MS = 500;
    private final NetworkCharacterAI.SpeedChecker speedChecker = new NetworkCharacterAI.SpeedChecker();
    public NetworkVariables.PredictionTypes predictionType;
    protected DeadCharacterPacket deadBody;
    protected VehicleHitPacket vehicleHit;
    protected float timestamp;
    protected BaseAction action;
    protected String performingAction;
    protected long noCollisionTime;
    protected boolean wasLocal;
    protected final HitReactionNetworkAI hitReaction;
    private final IsoGameCharacter character;
    public NetworkTeleport.NetworkTeleportDebug teleportDebug;
    public final HashMap<Integer, String> debugData = new LinkedHashMap<Integer, String>() {
        @Override
        protected boolean removeEldestEntry(Entry<Integer, String> var1) {
            return this.size() > 10;
        }
    };

    public NetworkCharacterAI(IsoGameCharacter _character) {
        this.character = _character;
        this.deadBody = null;
        this.wasLocal = false;
        this.vehicleHit = null;
        this.noCollisionTime = 0L;
        this.hitReaction = new HitReactionNetworkAI(_character);
        this.predictionType = NetworkVariables.PredictionTypes.None;
        this.clearTeleportDebug();
        this.speedChecker.reset();
    }

    public void reset() {
        this.deadBody = null;
        this.wasLocal = false;
        this.vehicleHit = null;
        this.noCollisionTime = 0L;
        this.hitReaction.finish();
        this.predictionType = NetworkVariables.PredictionTypes.None;
        this.clearTeleportDebug();
        this.speedChecker.reset();
    }

    public void setLocal(boolean _wasLocal) {
        this.wasLocal = _wasLocal;
    }

    public boolean wasLocal() {
        return this.wasLocal;
    }

    public NetworkTeleport.NetworkTeleportDebug getTeleportDebug() {
        return this.teleportDebug;
    }

    public void clearTeleportDebug() {
        this.teleportDebug = null;
        this.debugData.clear();
    }

    public void setTeleportDebug(NetworkTeleport.NetworkTeleportDebug _teleportDebug) {
        this.teleportDebug = _teleportDebug;
        this.debugData.entrySet().stream().sorted(Entry.comparingByKey(Comparator.naturalOrder())).forEach(entry -> {
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "==> " + entry.getValue());
            }
        });
        if (Core.bDebug) {
            DebugLog.log(
                DebugType.Multiplayer,
                String.format(
                    "NetworkTeleport %s id=%d distance=%.3f prediction=%s",
                    this.character.getClass().getSimpleName(),
                    this.character.getOnlineID(),
                    _teleportDebug.getDistance(),
                    this.predictionType
                )
            );
        }
    }

    public void addTeleportData(int time, String prediction) {
        this.debugData.put(time, prediction);
    }

    public void processDeadBody() {
        if (this.isSetDeadBody() && !this.hitReaction.isSetup() && !this.hitReaction.isStarted()) {
            this.deadBody.process();
            this.setDeadBody(null);
        }
    }

    public void setDeadBody(DeadCharacterPacket packet) {
        this.deadBody = packet;
        DebugLog.Death.trace(packet == null ? "processed" : "postpone");
    }

    public boolean isSetDeadBody() {
        return this.deadBody != null && this.deadBody.isConsistent();
    }

    public void setPerformingAction(String animation) {
        this.performingAction = animation;
    }

    public String getPerformingAction() {
        return this.performingAction;
    }

    public void setAction(BaseAction _action) {
        this.action = _action;
    }

    public BaseAction getAction() {
        return this.action;
    }

    public void startAction() {
        if (this.action != null) {
            this.action.start();
        }
    }

    public void stopAction() {
        if (this.action != null) {
            this.setOverride(false, null, null);
            this.action.stop();
        }
    }

    public void setOverride(boolean override, String primaryHandModel, String secondaryHandModel) {
        if (this.action != null) {
            this.action.chr.forceNullOverride = override;
            this.action.chr.overridePrimaryHandModel = primaryHandModel;
            this.action.chr.overrideSecondaryHandModel = secondaryHandModel;
            this.action.chr.resetModelNextFrame();
        }
    }

    public void processVehicleHit() {
        this.vehicleHit.tryProcessInternal();
        this.setVehicleHit(null);
    }

    public void setVehicleHit(VehicleHitPacket packet) {
        this.vehicleHit = packet;
        this.timestamp = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime());
        DebugLog.Damage.noise(packet == null ? "processed" : "postpone");
    }

    public boolean isSetVehicleHit() {
        return this.vehicleHit != null && this.vehicleHit.isConsistent();
    }

    public void resetVehicleHitTimeout() {
        this.timestamp = (float)(TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - 500L);
        if (this.vehicleHit == null) {
            DebugLog.Damage.noise("VehicleHit is not set");
        }
    }

    public boolean isVehicleHitTimeout() {
        boolean boolean0 = (float)TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()) - this.timestamp >= 500.0F;
        if (boolean0) {
            DebugLog.Damage.noise("VehicleHit timeout");
        }

        return boolean0;
    }

    public void updateHitVehicle() {
        if (this.isSetVehicleHit() && this.isVehicleHitTimeout()) {
            this.processVehicleHit();
        }
    }

    public boolean isCollisionEnabled() {
        return this.noCollisionTime == 0L;
    }

    public boolean isNoCollisionTimeout() {
        boolean boolean0 = GameTime.getServerTimeMills() > this.noCollisionTime;
        if (boolean0) {
            this.setNoCollision(0L);
        }

        return boolean0;
    }

    public void setNoCollision(long interval) {
        if (interval == 0L) {
            this.noCollisionTime = 0L;
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "SetNoCollision: disabled");
            }
        } else {
            this.noCollisionTime = GameTime.getServerTimeMills() + interval;
            if (Core.bDebug) {
                DebugLog.log(DebugType.Multiplayer, "SetNoCollision: enabled for " + interval + " ms");
            }
        }
    }

    public boolean checkPosition(UdpConnection connection, IsoGameCharacter _character, float x, float y) {
        if (GameServer.bServer && _character.isAlive()) {
            this.speedChecker.set(x, y, _character.isSeatedInVehicle());
            boolean boolean0 = PacketValidator.checkSpeed(
                connection, this.speedChecker, _character.getClass().getSimpleName() + NetworkCharacterAI.SpeedChecker.class.getSimpleName()
            );
            if (32 == connection.accessLevel) {
                boolean0 = true;
            }

            return boolean0;
        } else {
            return true;
        }
    }

    public void resetSpeedLimiter() {
        this.speedChecker.reset();
    }

    private static class SpeedChecker implements IMovable {
        private static final int checkDelay = 5000;
        private static final int checkInterval = 1000;
        private final UpdateLimit updateLimit = new UpdateLimit(5000L);
        private final Vector2 position = new Vector2();
        private boolean isInVehicle;
        private float speed;

        @Override
        public float getSpeed() {
            return this.speed;
        }

        @Override
        public boolean isVehicle() {
            return this.isInVehicle;
        }

        private void set(float arg0, float arg1, boolean arg2) {
            if (this.updateLimit.Check()) {
                if (5000L == this.updateLimit.getDelay()) {
                    this.updateLimit.Reset(1000L);
                    this.position.set(0.0F, 0.0F);
                    this.speed = 0.0F;
                }

                this.isInVehicle = arg2;
                if (this.position.getLength() != 0.0F) {
                    this.speed = IsoUtils.DistanceTo(this.position.x, this.position.y, arg0, arg1);
                }

                this.position.set(arg0, arg1);
            }
        }

        private void reset() {
            this.updateLimit.Reset(5000L);
            this.isInVehicle = false;
            this.position.set(0.0F, 0.0F);
            this.speed = 0.0F;
        }

        public String getDescription() {
            return "SpeedChecker: speed=" + this.speed + " x=" + this.position.x + " y=" + this.position.y + " vehicle=" + this.isInVehicle;
        }
    }
}
