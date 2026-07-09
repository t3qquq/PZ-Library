// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.FishingState;
import zombie.ai.states.SmashWindowState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkPlayerVariables;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;

public class EventPacket implements INetworkPacket {
    public static final int MAX_PLAYER_EVENTS = 10;
    private static final long EVENT_TIMEOUT = 5000L;
    private static final short EVENT_FLAGS_VAULT_OVER_SPRINT = 1;
    private static final short EVENT_FLAGS_VAULT_OVER_RUN = 2;
    private static final short EVENT_FLAGS_BUMP_FALL = 4;
    private static final short EVENT_FLAGS_BUMP_STAGGERED = 8;
    private static final short EVENT_FLAGS_ACTIVATE_ITEM = 16;
    private static final short EVENT_FLAGS_CLIMB_SUCCESS = 32;
    private static final short EVENT_FLAGS_CLIMB_STRUGGLE = 64;
    private static final short EVENT_FLAGS_BUMP_FROM_BEHIND = 128;
    private static final short EVENT_FLAGS_BUMP_TARGET_TYPE = 256;
    private static final short EVENT_FLAGS_PRESSED_MOVEMENT = 512;
    private static final short EVENT_FLAGS_PRESSED_CANCEL_ACTION = 1024;
    private static final short EVENT_FLAGS_SMASH_CAR_WINDOW = 2048;
    private static final short EVENT_FLAGS_FITNESS_FINISHED = 4096;
    private short id;
    public float x;
    public float y;
    public float z;
    private byte eventID;
    private String type1;
    private String type2;
    private String type3;
    private String type4;
    private float strafeSpeed;
    private float walkSpeed;
    private float walkInjury;
    private int booleanVariables;
    private short flags;
    private IsoPlayer player;
    private EventPacket.EventType event;
    private long timestamp;

    @Override
    public String getDescription() {
        return "[ player="
            + this.id
            + " \""
            + (this.player == null ? "?" : this.player.getUsername())
            + "\" | name=\""
            + (this.event == null ? "?" : this.event.name())
            + "\" | pos=( "
            + this.x
            + " ; "
            + this.y
            + " ; "
            + this.z
            + " ) | type1=\""
            + this.type1
            + "\" | type2=\""
            + this.type2
            + "\" | type3=\""
            + this.type3
            + "\" | type4=\""
            + this.type4
            + "\" | flags="
            + this.flags
            + "\" | variables="
            + this.booleanVariables
            + " ]";
    }

    @Override
    public boolean isConsistent() {
        boolean boolean0 = this.player != null && this.event != null;
        if (!boolean0 && Core.bDebug) {
            DebugLog.log(DebugType.Multiplayer, "[Event] is not consistent " + this.getDescription());
        }

        return boolean0;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.id = byteBuffer.getShort();
        this.x = byteBuffer.getFloat();
        this.y = byteBuffer.getFloat();
        this.z = byteBuffer.getFloat();
        this.eventID = byteBuffer.get();
        this.type1 = GameWindow.ReadString(byteBuffer);
        this.type2 = GameWindow.ReadString(byteBuffer);
        this.type3 = GameWindow.ReadString(byteBuffer);
        this.type4 = GameWindow.ReadString(byteBuffer);
        this.strafeSpeed = byteBuffer.getFloat();
        this.walkSpeed = byteBuffer.getFloat();
        this.walkInjury = byteBuffer.getFloat();
        this.booleanVariables = byteBuffer.getInt();
        this.flags = byteBuffer.getShort();
        if (this.eventID >= 0 && this.eventID < EventPacket.EventType.values().length) {
            this.event = EventPacket.EventType.values()[this.eventID];
        } else {
            DebugLog.Multiplayer.warn("Unknown event=" + this.eventID);
            this.event = null;
        }

        if (GameServer.bServer) {
            this.player = GameServer.IDToPlayerMap.get(this.id);
        } else if (GameClient.bClient) {
            this.player = GameClient.IDToPlayerMap.get(this.id);
        } else {
            this.player = null;
        }

        this.timestamp = System.currentTimeMillis() + 5000L;
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putShort(this.id);
        byteBufferWriter.putFloat(this.x);
        byteBufferWriter.putFloat(this.y);
        byteBufferWriter.putFloat(this.z);
        byteBufferWriter.putByte(this.eventID);
        byteBufferWriter.putUTF(this.type1);
        byteBufferWriter.putUTF(this.type2);
        byteBufferWriter.putUTF(this.type3);
        byteBufferWriter.putUTF(this.type4);
        byteBufferWriter.putFloat(this.strafeSpeed);
        byteBufferWriter.putFloat(this.walkSpeed);
        byteBufferWriter.putFloat(this.walkInjury);
        byteBufferWriter.putInt(this.booleanVariables);
        byteBufferWriter.putShort(this.flags);
    }

    public boolean isRelevant(UdpConnection udpConnection) {
        return udpConnection.RelevantTo(this.x, this.y);
    }

    public boolean isMovableEvent() {
        return !this.isConsistent()
            ? false
            : EventPacket.EventType.EventClimbFence.equals(this.event) || EventPacket.EventType.EventFallClimb.equals(this.event);
    }

    private boolean requireNonMoving() {
        return this.isConsistent()
            && (
                EventPacket.EventType.EventClimbWindow.equals(this.event)
                    || EventPacket.EventType.EventClimbFence.equals(this.event)
                    || EventPacket.EventType.EventClimbDownRope.equals(this.event)
                    || EventPacket.EventType.EventClimbRope.equals(this.event)
                    || EventPacket.EventType.EventClimbWall.equals(this.event)
            );
    }

    private IsoWindow getWindow(IsoPlayer playerx) {
        for (IsoDirections directions : IsoDirections.values()) {
            IsoObject object = playerx.getContextDoorOrWindowOrWindowFrame(directions);
            if (object instanceof IsoWindow) {
                return (IsoWindow)object;
            }
        }

        return null;
    }

    private IsoObject getObject(IsoPlayer playerx) {
        for (IsoDirections directions : IsoDirections.values()) {
            IsoObject object = playerx.getContextDoorOrWindowOrWindowFrame(directions);
            if (object instanceof IsoWindow || object instanceof IsoThumpable || IsoWindowFrame.isWindowFrame(object)) {
                return object;
            }
        }

        return null;
    }

    private IsoDirections checkCurrentIsEventGridSquareFence(IsoPlayer playerx) {
        IsoGridSquare square0 = playerx.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
        IsoGridSquare square1 = playerx.getCell().getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
        IsoGridSquare square2 = playerx.getCell().getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
        IsoDirections directions;
        if (square0 != null && square0.Is(IsoFlagType.HoppableN)) {
            directions = IsoDirections.N;
        } else if (square0 != null && square0.Is(IsoFlagType.HoppableW)) {
            directions = IsoDirections.W;
        } else if (square1 != null && square1.Is(IsoFlagType.HoppableN)) {
            directions = IsoDirections.S;
        } else if (square2 != null && square2.Is(IsoFlagType.HoppableW)) {
            directions = IsoDirections.E;
        } else {
            directions = IsoDirections.Max;
        }

        return directions;
    }

    public boolean isTimeout() {
        return System.currentTimeMillis() > this.timestamp;
    }

    public void tryProcess() {
        if (this.isConsistent()) {
            if (this.player.networkAI.events.size() < 10) {
                this.player.networkAI.events.add(this);
            } else {
                DebugLog.Multiplayer.warn("Event skipped: " + this.getDescription());
            }
        }
    }

    public boolean process(IsoPlayer playerx) {
        boolean boolean0 = false;
        if (this.isConsistent()) {
            playerx.overridePrimaryHandModel = null;
            playerx.overrideSecondaryHandModel = null;
            if (playerx.getCurrentSquare() == playerx.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z) && !playerx.isPlayerMoving()
                || !this.requireNonMoving()) {
                switch (this.event) {
                    case EventSetActivatedPrimary:
                        if (playerx.getPrimaryHandItem() != null && playerx.getPrimaryHandItem().canEmitLight()) {
                            playerx.getPrimaryHandItem().setActivatedRemote((this.flags & 16) != 0);
                            boolean0 = true;
                        }
                        break;
                    case EventSetActivatedSecondary:
                        if (playerx.getSecondaryHandItem() != null && playerx.getSecondaryHandItem().canEmitLight()) {
                            playerx.getSecondaryHandItem().setActivatedRemote((this.flags & 16) != 0);
                            boolean0 = true;
                        }
                        break;
                    case EventFallClimb:
                        playerx.setVariable("ClimbFenceOutcome", "fall");
                        playerx.setVariable("BumpDone", true);
                        playerx.setFallOnFront(true);
                        boolean0 = true;
                        break;
                    case collideWithWall:
                        playerx.setCollideType(this.type1);
                        playerx.actionContext.reportEvent("collideWithWall");
                        boolean0 = true;
                        break;
                    case EventFishing:
                        playerx.setVariable("FishingStage", this.type1);
                        if (!FishingState.instance().equals(playerx.getCurrentState())) {
                            playerx.setVariable("forceGetUp", true);
                            playerx.actionContext.reportEvent("EventFishing");
                        }

                        boolean0 = true;
                        break;
                    case EventFitness:
                        playerx.setVariable("ExerciseType", this.type1);
                        playerx.setVariable("FitnessFinished", false);
                        playerx.actionContext.reportEvent("EventFitness");
                        boolean0 = true;
                        break;
                    case EventUpdateFitness:
                        playerx.clearVariable("ExerciseHand");
                        playerx.setVariable("ExerciseType", this.type2);
                        if (!StringUtils.isNullOrEmpty(this.type1)) {
                            playerx.setVariable("ExerciseHand", this.type1);
                        }

                        playerx.setFitnessSpeed();
                        if ((this.flags & 4096) != 0) {
                            playerx.setVariable("ExerciseStarted", false);
                            playerx.setVariable("ExerciseEnded", true);
                        }

                        playerx.setPrimaryHandItem(null);
                        playerx.setSecondaryHandItem(null);
                        playerx.overridePrimaryHandModel = null;
                        playerx.overrideSecondaryHandModel = null;
                        playerx.overridePrimaryHandModel = this.type3;
                        playerx.overrideSecondaryHandModel = this.type4;
                        playerx.resetModelNextFrame();
                        boolean0 = true;
                        break;
                    case EventEmote:
                        playerx.setVariable("emote", this.type1);
                        playerx.actionContext.reportEvent("EventEmote");
                        boolean0 = true;
                        break;
                    case EventSitOnGround:
                        playerx.actionContext.reportEvent("EventSitOnGround");
                        boolean0 = true;
                        break;
                    case EventClimbRope:
                        playerx.climbSheetRope();
                        boolean0 = true;
                        break;
                    case EventClimbDownRope:
                        playerx.climbDownSheetRope();
                        boolean0 = true;
                        break;
                    case EventClimbFence:
                        IsoDirections directions0 = this.checkCurrentIsEventGridSquareFence(playerx);
                        if (directions0 != IsoDirections.Max) {
                            playerx.climbOverFence(directions0);
                            if (playerx.isSprinting()) {
                                playerx.setVariable("VaultOverSprint", true);
                            }

                            if (playerx.isRunning()) {
                                playerx.setVariable("VaultOverRun", true);
                            }

                            boolean0 = true;
                        }
                        break;
                    case EventClimbWall:
                        playerx.setClimbOverWallStruggle((this.flags & 64) != 0);
                        playerx.setClimbOverWallSuccess((this.flags & 32) != 0);

                        for (IsoDirections directions1 : IsoDirections.values()) {
                            if (playerx.climbOverWall(directions1)) {
                                return true;
                            }
                        }
                        break;
                    case EventClimbWindow:
                        IsoObject object = this.getObject(playerx);
                        if (object instanceof IsoWindow) {
                            playerx.climbThroughWindow((IsoWindow)object);
                            boolean0 = true;
                        } else if (object instanceof IsoThumpable) {
                            playerx.climbThroughWindow((IsoThumpable)object);
                            boolean0 = true;
                        }

                        if (IsoWindowFrame.isWindowFrame(object)) {
                            playerx.climbThroughWindowFrame(object);
                            boolean0 = true;
                        }
                        break;
                    case EventOpenWindow:
                        IsoWindow window2 = this.getWindow(playerx);
                        if (window2 != null) {
                            playerx.openWindow(window2);
                            boolean0 = true;
                        }
                        break;
                    case EventCloseWindow:
                        IsoWindow window1 = this.getWindow(playerx);
                        if (window1 != null) {
                            playerx.closeWindow(window1);
                            boolean0 = true;
                        }
                        break;
                    case EventSmashWindow:
                        if ((this.flags & 2048) != 0) {
                            BaseVehicle vehicle = VehicleManager.instance.getVehicleByID(Short.parseShort(this.type1));
                            if (vehicle != null) {
                                VehiclePart part = vehicle.getPartById(this.type2);
                                if (part != null) {
                                    VehicleWindow vehicleWindow = part.getWindow();
                                    if (vehicleWindow != null) {
                                        playerx.smashCarWindow(part);
                                        boolean0 = true;
                                    }
                                }
                            }
                        } else {
                            IsoWindow window0 = this.getWindow(playerx);
                            if (window0 != null) {
                                playerx.smashWindow(window0);
                                boolean0 = true;
                            }
                        }
                        break;
                    case wasBumped:
                        playerx.setBumpDone(false);
                        playerx.setVariable("BumpFallAnimFinished", false);
                        playerx.setBumpType(this.type1);
                        playerx.setBumpFallType(this.type2);
                        playerx.setBumpFall((this.flags & 4) != 0);
                        playerx.setBumpStaggered((this.flags & 8) != 0);
                        playerx.reportEvent("wasBumped");
                        if (!StringUtils.isNullOrEmpty(this.type3) && !StringUtils.isNullOrEmpty(this.type4)) {
                            IsoGameCharacter character = null;
                            if ((this.flags & 256) != 0) {
                                character = GameClient.IDToZombieMap.get(Short.parseShort(this.type3));
                            } else {
                                character = GameClient.IDToPlayerMap.get(Short.parseShort(this.type3));
                            }

                            if (character != null) {
                                character.setBumpType(this.type4);
                                character.setHitFromBehind((this.flags & 128) != 0);
                            }
                        }

                        boolean0 = true;
                        break;
                    case EventOverrideItem:
                        if (playerx.getNetworkCharacterAI().getAction() != null) {
                            playerx.getNetworkCharacterAI().setOverride(true, this.type1, this.type2);
                        }

                        boolean0 = true;
                        break;
                    case ChargeSpearConnect:
                        boolean0 = true;
                        break;
                    case Update:
                        playerx.networkAI.setPressedMovement((this.flags & 512) != 0);
                        playerx.networkAI.setPressedCancelAction((this.flags & 1024) != 0);
                        boolean0 = true;
                        break;
                    case Unknown:
                    default:
                        DebugLog.Multiplayer.warn("[Event] unknown: " + this.getDescription());
                        boolean0 = true;
                }
            }
        }

        return boolean0;
    }

    public boolean set(IsoPlayer playerx, String string) {
        boolean boolean0 = false;
        this.player = playerx;
        this.id = playerx.getOnlineID();
        this.x = playerx.getX();
        this.y = playerx.getY();
        this.z = playerx.getZ();
        this.type1 = null;
        this.type2 = null;
        this.type3 = null;
        this.type4 = null;
        this.booleanVariables = NetworkPlayerVariables.getBooleanVariables(playerx);
        this.strafeSpeed = playerx.getVariableFloat("StrafeSpeed", 1.0F);
        this.walkSpeed = playerx.getVariableFloat("WalkSpeed", 1.0F);
        this.walkInjury = playerx.getVariableFloat("WalkInjury", 0.0F);
        this.flags = 0;

        for (EventPacket.EventType eventType : EventPacket.EventType.values()) {
            if (eventType.name().equals(string)) {
                this.event = eventType;
                this.eventID = (byte)eventType.ordinal();
                switch (eventType) {
                    case EventSetActivatedPrimary:
                        this.flags = (short)(this.flags | (playerx.getPrimaryHandItem().isActivated() ? 16 : 0));
                        break;
                    case EventSetActivatedSecondary:
                        this.flags = (short)(this.flags | (playerx.getSecondaryHandItem().isActivated() ? 16 : 0));
                    case EventFallClimb:
                    case EventSitOnGround:
                    case EventClimbRope:
                    case EventClimbDownRope:
                    case EventClimbWindow:
                    case EventOpenWindow:
                    case EventCloseWindow:
                    case ChargeSpearConnect:
                        break;
                    case collideWithWall:
                        this.type1 = playerx.getCollideType();
                        break;
                    case EventFishing:
                        this.type1 = playerx.getVariableString("FishingStage");
                        break;
                    case EventFitness:
                        this.type1 = playerx.getVariableString("ExerciseType");
                        break;
                    case EventUpdateFitness:
                        this.type1 = playerx.getVariableString("ExerciseHand");
                        this.type2 = playerx.getVariableString("ExerciseType");
                        if (playerx.getPrimaryHandItem() != null) {
                            this.type3 = playerx.getPrimaryHandItem().getStaticModel();
                        }

                        if (playerx.getSecondaryHandItem() != null && playerx.getSecondaryHandItem() != playerx.getPrimaryHandItem()) {
                            this.type4 = playerx.getSecondaryHandItem().getStaticModel();
                        }

                        this.flags = (short)(this.flags | (playerx.getVariableBoolean("FitnessFinished") ? 4096 : 0));
                        break;
                    case EventEmote:
                        this.type1 = playerx.getVariableString("emote");
                        break;
                    case EventClimbFence:
                        if (playerx.getVariableBoolean("VaultOverRun")) {
                            this.flags = (short)(this.flags | 2);
                        }

                        if (playerx.getVariableBoolean("VaultOverSprint")) {
                            this.flags = (short)(this.flags | 1);
                        }
                        break;
                    case EventClimbWall:
                        this.flags = (short)(this.flags | (playerx.isClimbOverWallSuccess() ? 32 : 0));
                        this.flags = (short)(this.flags | (playerx.isClimbOverWallStruggle() ? 64 : 0));
                        break;
                    case EventSmashWindow:
                        HashMap hashMap = playerx.getStateMachineParams(SmashWindowState.instance());
                        if (hashMap.get(1) instanceof BaseVehicle && hashMap.get(2) instanceof VehiclePart) {
                            BaseVehicle vehicle = (BaseVehicle)hashMap.get(1);
                            VehiclePart part = (VehiclePart)hashMap.get(2);
                            this.flags = (short)(this.flags | 2048);
                            this.type1 = String.valueOf(vehicle.getId());
                            this.type2 = part.getId();
                        }
                        break;
                    case wasBumped:
                        this.type1 = playerx.getBumpType();
                        this.type2 = playerx.getBumpFallType();
                        this.flags = (short)(this.flags | (playerx.isBumpFall() ? 4 : 0));
                        this.flags = (short)(this.flags | (playerx.isBumpStaggered() ? 8 : 0));
                        if (playerx.getBumpedChr() != null) {
                            this.type3 = String.valueOf(playerx.getBumpedChr().getOnlineID());
                            this.type4 = playerx.getBumpedChr().getBumpType();
                            this.flags = (short)(this.flags | (playerx.isHitFromBehind() ? 128 : 0));
                            if (playerx.getBumpedChr() instanceof IsoZombie) {
                                this.flags = (short)(this.flags | 256);
                            }
                        }
                        break;
                    case EventOverrideItem:
                        if (playerx.getNetworkCharacterAI().getAction() == null) {
                            return false;
                        }

                        BaseAction baseAction = playerx.getNetworkCharacterAI().getAction();
                        this.type1 = baseAction.getPrimaryHandItem() == null
                            ? baseAction.getPrimaryHandMdl()
                            : baseAction.getPrimaryHandItem().getStaticModel();
                        this.type2 = baseAction.getSecondaryHandItem() == null
                            ? baseAction.getSecondaryHandMdl()
                            : baseAction.getSecondaryHandItem().getStaticModel();
                        break;
                    case Update:
                        this.flags = (short)(this.flags | (playerx.networkAI.isPressedMovement() ? 512 : 0));
                        this.flags = (short)(this.flags | (playerx.networkAI.isPressedCancelAction() ? 1024 : 0));
                        break;
                    default:
                        DebugLog.Multiplayer.warn("[Event] unknown " + this.getDescription());
                        return false;
                }

                boolean0 = !ClimbDownSheetRopeState.instance().equals(playerx.getCurrentState())
                    && !ClimbSheetRopeState.instance().equals(playerx.getCurrentState());
            }
        }

        return boolean0;
    }

    public static enum EventType {
        EventSetActivatedPrimary,
        EventSetActivatedSecondary,
        EventFishing,
        EventFitness,
        EventEmote,
        EventClimbFence,
        EventClimbDownRope,
        EventClimbRope,
        EventClimbWall,
        EventClimbWindow,
        EventOpenWindow,
        EventCloseWindow,
        EventSmashWindow,
        EventSitOnGround,
        wasBumped,
        collideWithWall,
        EventUpdateFitness,
        EventFallClimb,
        EventOverrideItem,
        ChargeSpearConnect,
        Update,
        Unknown;
    }
}
