// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import java.util.LinkedList;
import zombie.GameTime;
import zombie.SystemDisabler;
import zombie.ai.states.CollideWithWallState;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.utils.UpdateTimer;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.iso.IsoDirections;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.packets.EventPacket;
import zombie.network.packets.PlayerPacket;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;

public class NetworkPlayerAI extends NetworkCharacterAI {
    public final LinkedList<EventPacket> events = new LinkedList<>();
    IsoPlayer player;
    private PathFindBehavior2 pfb2 = null;
    private final UpdateTimer timer = new UpdateTimer();
    private byte lastDirection = 0;
    private boolean needUpdate = false;
    private boolean blockUpdate = false;
    public boolean usePathFind = false;
    public float collidePointX;
    public float collidePointY;
    public float targetX = 0.0F;
    public float targetY = 0.0F;
    public int targetZ = 0;
    public boolean needToMovingUsingPathFinder = false;
    public boolean forcePathFinder = false;
    public Vector2 direction = new Vector2();
    public Vector2 distance = new Vector2();
    public boolean moving = false;
    public byte footstepSoundRadius = 0;
    public int lastBooleanVariables = 0;
    public float lastForwardDirection = 0.0F;
    public float lastPlayerMoveDirLen = 0.0F;
    private boolean pressedMovement = false;
    private boolean pressedCancelAction = false;
    public boolean climbFenceOutcomeFall = false;
    private long accessLevelTimestamp = 0L;
    boolean wasNonPvpZone = false;
    private Vector2 tempo = new Vector2();
    private static final int predictInterval = 1000;

    public NetworkPlayerAI(IsoGameCharacter character) {
        super(character);
        this.player = (IsoPlayer)character;
        this.pfb2 = this.player.getPathFindBehavior2();
        character.ulBeatenVehicle.Reset(200L);
        this.collidePointX = -1.0F;
        this.collidePointY = -1.0F;
        this.wasNonPvpZone = false;
    }

    public void needToUpdate() {
        this.needUpdate = true;
    }

    public void setBlockUpdate(boolean value) {
        this.blockUpdate = value;
    }

    public boolean isNeedToUpdate() {
        int int0 = NetworkPlayerVariables.getBooleanVariables(this.player);
        byte byte0 = (byte)(this.player.playerMoveDir.getDirection() * 10.0F);
        if ((!this.timer.check() && int0 == this.lastBooleanVariables && this.lastDirection == byte0 || this.blockUpdate) && !this.needUpdate) {
            return false;
        } else {
            this.lastDirection = byte0;
            this.needUpdate = false;
            return true;
        }
    }

    public void setUpdateTimer(float nextTimerValue) {
        this.timer.reset(PZMath.clamp((int)nextTimerValue, 200, 3800));
    }

    private void setUsingCollide(PlayerPacket playerPacket, int int0) {
        if (SystemDisabler.useNetworkCharacter) {
            this.player.networkCharacter.checkResetPlayer(int0);
        }

        playerPacket.x = this.player.getCurrentSquare().getX();
        playerPacket.y = this.player.getCurrentSquare().getY();
        playerPacket.z = (byte)this.player.getCurrentSquare().getZ();
        playerPacket.usePathFinder = false;
        playerPacket.moveType = NetworkVariables.PredictionTypes.Thump;
    }

    private void setUsingExtrapolation(PlayerPacket playerPacket, int int0, int int1) {
        Vector2 vector0 = this.player.dir.ToVector();
        if (SystemDisabler.useNetworkCharacter) {
            this.player.networkCharacter.checkResetPlayer(int0);
        }

        if (!this.player.isPlayerMoving()) {
            playerPacket.x = this.player.x;
            playerPacket.y = this.player.y;
            playerPacket.z = (byte)this.player.z;
            playerPacket.usePathFinder = false;
            playerPacket.moveType = NetworkVariables.PredictionTypes.Static;
        } else {
            Vector2 vector1 = this.tempo;
            if (SystemDisabler.useNetworkCharacter) {
                NetworkCharacter.Transform transform = this.player.networkCharacter.predict(int1, int0, this.player.x, this.player.y, vector0.x, vector0.y);
                vector1.x = transform.position.x;
                vector1.y = transform.position.y;
            } else {
                this.player.getDeferredMovement(vector1);
                vector1.x = this.player.x + vector1.x * 0.03F * int1;
                vector1.y = this.player.y + vector1.y * 0.03F * int1;
            }

            if (this.player.z == this.pfb2.getTargetZ()
                && !PolygonalMap2.instance.lineClearCollide(this.player.x, this.player.y, vector1.x, vector1.y, (int)this.player.z, null)) {
                playerPacket.x = vector1.x;
                playerPacket.y = vector1.y;
                playerPacket.z = (byte)this.pfb2.getTargetZ();
            } else {
                Vector2 vector2 = PolygonalMap2.instance.getCollidepoint(this.player.x, this.player.y, vector1.x, vector1.y, (int)this.player.z, null, 2);
                playerPacket.collidePointX = vector2.x;
                playerPacket.collidePointY = vector2.y;
                playerPacket.x = vector2.x
                    + (
                        this.player.dir != IsoDirections.N && this.player.dir != IsoDirections.S
                            ? (this.player.dir.index() >= IsoDirections.NW.index() && this.player.dir.index() <= IsoDirections.SW.index() ? -1.0F : 1.0F)
                            : 0.0F
                    );
                playerPacket.y = vector2.y
                    + (
                        this.player.dir != IsoDirections.W && this.player.dir != IsoDirections.E
                            ? (this.player.dir.index() >= IsoDirections.SW.index() && this.player.dir.index() <= IsoDirections.SE.index() ? 1.0F : -1.0F)
                            : 0.0F
                    );
                playerPacket.z = (byte)this.player.z;
            }

            playerPacket.usePathFinder = false;
            playerPacket.moveType = NetworkVariables.PredictionTypes.Moving;
        }
    }

    private void setUsingPathFindState(PlayerPacket playerPacket, int int0) {
        if (SystemDisabler.useNetworkCharacter) {
            this.player.networkCharacter.checkResetPlayer(int0);
        }

        playerPacket.x = this.pfb2.pathNextX;
        playerPacket.y = this.pfb2.pathNextY;
        playerPacket.z = (byte)this.player.z;
        playerPacket.usePathFinder = true;
        playerPacket.moveType = NetworkVariables.PredictionTypes.PathFind;
    }

    public boolean set(PlayerPacket packet) {
        int int0 = (int)(GameTime.getServerTime() / 1000000L);
        packet.realx = this.player.x;
        packet.realy = this.player.y;
        packet.realz = (byte)this.player.z;
        packet.realdir = (byte)this.player.dir.index();
        packet.realt = int0;
        if (this.player.vehicle == null) {
            packet.VehicleID = -1;
            packet.VehicleSeat = -1;
        } else {
            packet.VehicleID = this.player.vehicle.VehicleID;
            packet.VehicleSeat = (short)this.player.vehicle.getSeat(this.player);
        }

        boolean boolean0 = this.timer.check();
        packet.collidePointX = -1.0F;
        packet.collidePointY = -1.0F;
        if (boolean0) {
            this.setUpdateTimer(600.0F);
        }

        if (this.player.getCurrentState() == CollideWithWallState.instance()) {
            this.setUsingCollide(packet, int0);
        } else if (this.pfb2.isMovingUsingPathFind()) {
            this.setUsingPathFindState(packet, int0);
        } else {
            this.setUsingExtrapolation(packet, int0, 1000);
        }

        boolean boolean1 = this.player.playerMoveDir.getLength() < 0.01 && this.lastPlayerMoveDirLen > 0.01F;
        this.lastPlayerMoveDirLen = this.player.playerMoveDir.getLength();
        packet.booleanVariables = NetworkPlayerVariables.getBooleanVariables(this.player);
        boolean boolean2 = this.lastBooleanVariables != packet.booleanVariables;
        this.lastBooleanVariables = packet.booleanVariables;
        packet.direction = this.player.getForwardDirection().getDirection();
        boolean boolean3 = Math.abs(this.lastForwardDirection - packet.direction) > 0.2F;
        this.lastForwardDirection = packet.direction;
        packet.footstepSoundRadius = this.footstepSoundRadius;
        return boolean0 || boolean2 || boolean3 || this.player.isJustMoved() || boolean1;
    }

    public void parse(PlayerPacket packet) {
        if (!this.player.isTeleporting()) {
            this.targetX = PZMath.roundFromEdges(packet.x);
            this.targetY = PZMath.roundFromEdges(packet.y);
            this.targetZ = packet.z;
            this.predictionType = packet.moveType;
            this.needToMovingUsingPathFinder = packet.usePathFinder;
            this.direction.set((float)Math.cos(packet.direction), (float)Math.sin(packet.direction));
            this.distance.set(packet.x - this.player.x, packet.y - this.player.y);
            if (this.usePathFind) {
                this.pfb2.pathToLocationF(packet.x, packet.y, packet.z);
                this.pfb2.walkingOnTheSpot.reset(this.player.x, this.player.y);
            }

            BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(packet.VehicleID);
            NetworkPlayerVariables.setBooleanVariables(this.player, packet.booleanVariables);
            this.player.setbSeenThisFrame(false);
            this.player.setbCouldBeSeenThisFrame(false);
            this.player.TimeSinceLastNetData = 0;
            this.player.ensureOnTile();
            this.player.realx = packet.realx;
            this.player.realy = packet.realy;
            this.player.realz = packet.realz;
            this.player.realdir = IsoDirections.fromIndex(packet.realdir);
            if (GameServer.bServer) {
                this.player.setForwardDirection(this.direction);
            }

            this.collidePointX = packet.collidePointX;
            this.collidePointY = packet.collidePointY;
            packet.variables.apply(this.player);
            this.footstepSoundRadius = packet.footstepSoundRadius;
            if (this.player.getVehicle() == null) {
                if (vehicle != null) {
                    if (packet.VehicleSeat >= 0 && packet.VehicleSeat < vehicle.getMaxPassengers()) {
                        IsoGameCharacter character0 = vehicle.getCharacter(packet.VehicleSeat);
                        if (character0 == null) {
                            if (GameServer.bDebug) {
                                DebugLog.log(this.player.getUsername() + " got in vehicle " + vehicle.VehicleID + " seat " + packet.VehicleSeat);
                            }

                            vehicle.enterRSync(packet.VehicleSeat, this.player, vehicle);
                        } else if (character0 != this.player) {
                            DebugLog.log(this.player.getUsername() + " got in same seat as " + ((IsoPlayer)character0).getUsername());
                            this.player.sendObjectChange("exitVehicle");
                        }
                    } else {
                        DebugLog.log(this.player.getUsername() + " invalid seat vehicle " + vehicle.VehicleID + " seat " + packet.VehicleSeat);
                    }
                }
            } else if (vehicle != null) {
                if (vehicle == this.player.getVehicle() && this.player.getVehicle().getSeat(this.player) != -1) {
                    IsoGameCharacter character1 = vehicle.getCharacter(packet.VehicleSeat);
                    if (character1 == null) {
                        if (vehicle.getSeat(this.player) != packet.VehicleSeat) {
                            vehicle.switchSeat(this.player, packet.VehicleSeat);
                        }
                    } else if (character1 != this.player) {
                        DebugLog.log(this.player.getUsername() + " switched to same seat as " + ((IsoPlayer)character1).getUsername());
                        this.player.sendObjectChange("exitVehicle");
                    }
                } else {
                    DebugLog.log(
                        this.player.getUsername()
                            + " vehicle/seat remote "
                            + vehicle.VehicleID
                            + "/"
                            + packet.VehicleSeat
                            + " local "
                            + this.player.getVehicle().VehicleID
                            + "/"
                            + this.player.getVehicle().getSeat(this.player)
                    );
                    this.player.sendObjectChange("exitVehicle");
                }
            } else {
                this.player.getVehicle().exitRSync(this.player);
                this.player.setVehicle(null);
            }

            this.setPressedMovement(false);
            this.setPressedCancelAction(false);
        }
    }

    public boolean isPressedMovement() {
        return this.pressedMovement;
    }

    public void setPressedMovement(boolean _pressedMovement) {
        boolean boolean0 = !this.pressedMovement && _pressedMovement;
        this.pressedMovement = _pressedMovement;
        if (this.player.isLocal() && boolean0) {
            GameClient.sendEvent(this.player, "Update");
        }
    }

    public boolean isPressedCancelAction() {
        return this.pressedCancelAction;
    }

    public void setPressedCancelAction(boolean _pressedCancelAction) {
        boolean boolean0 = !this.pressedCancelAction && _pressedCancelAction;
        this.pressedCancelAction = _pressedCancelAction;
        if (this.player.isLocal() && boolean0) {
            GameClient.sendEvent(this.player, "Update");
        }
    }

    public void setCheckAccessLevelDelay(long delay) {
        this.accessLevelTimestamp = System.currentTimeMillis() + delay;
    }

    public boolean doCheckAccessLevel() {
        if (this.accessLevelTimestamp == 0L) {
            return true;
        } else if (System.currentTimeMillis() > this.accessLevelTimestamp) {
            this.accessLevelTimestamp = 0L;
            return true;
        } else {
            return false;
        }
    }

    public void update() {
        if (DebugOptions.instance.MultiplayerHotKey.getValue() && GameKeyboard.isKeyPressed(45) && GameKeyboard.isKeyDown(56)) {
            DebugLog.Multiplayer.noise("multiplayer hot key pressed");
            ArrayList arrayList = ScriptManager.instance.getAllRecipes();
            Recipe recipe = (Recipe)arrayList.get(Rand.Next(arrayList.size()));
            recipe.TimeToMake = Rand.Next(32767);
            DebugLog.Multiplayer.debugln("Failed recipe \"%s\"", recipe.getOriginalname());
        }
    }

    public boolean isDismantleAllowed() {
        return true;
    }
}
