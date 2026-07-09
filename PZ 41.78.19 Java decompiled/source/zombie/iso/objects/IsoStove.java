// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.SystemDisabler;
import zombie.audio.BaseSoundEmitter;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.interfaces.Activatable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.Type;

public class IsoStove extends IsoObject implements Activatable {
    private static final ArrayList<IsoObject> s_tempObjects = new ArrayList<>();
    boolean activated = false;
    long soundInstance = -1L;
    private float maxTemperature = 0.0F;
    private double stopTime;
    private double startTime;
    private float currentTemperature = 0.0F;
    private int secondsTimer = -1;
    private boolean firstTurnOn = true;
    private boolean broken = false;
    private boolean hasMetal = false;

    public IsoStove(IsoCell cell, IsoGridSquare sq, IsoSprite gid) {
        super(cell, sq, gid);
    }

    @Override
    public String getObjectName() {
        return "Stove";
    }

    public IsoStove(IsoCell cell) {
        super(cell);
    }

    @Override
    public boolean Activated() {
        return this.activated;
    }

    @Override
    public void update() {
        if (this.Activated() && (this.container == null || !this.container.isPowered())) {
            this.setActivated(false);
            if (this.container != null) {
                this.container.addItemsToProcessItems();
            }
        }

        if (this.Activated() && this.isMicrowave() && this.stopTime > 0.0 && this.stopTime < GameTime.instance.getWorldAgeHours()) {
            this.setActivated(false);
        }

        boolean boolean0 = GameServer.bServer || !GameClient.bClient && !GameServer.bServer;
        if (boolean0 && this.Activated() && this.hasMetal && Rand.Next(Rand.AdjustForFramerate(200)) == 100) {
            IsoFireManager.StartFire(this.container.SourceGrid.getCell(), this.container.SourceGrid, true, 10000);
            this.setBroken(true);
            this.activated = false;
            this.stopTime = 0.0;
            this.startTime = 0.0;
            this.secondsTimer = -1;
        }

        if (!GameServer.bServer) {
            if (this.Activated()) {
                if (this.stopTime > 0.0 && this.stopTime < GameTime.instance.getWorldAgeHours()) {
                    if (!this.isMicrowave() && "stove".equals(this.container.getType()) && this.isSpriteGridOriginObject()) {
                        BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ());
                        baseSoundEmitter.playSoundImpl("StoveTimerExpired", this);
                    }

                    this.stopTime = 0.0;
                    this.startTime = 0.0;
                    this.secondsTimer = -1;
                }

                if (this.getMaxTemperature() > 0.0F && this.currentTemperature < this.getMaxTemperature()) {
                    float float0 = (this.getMaxTemperature() - this.currentTemperature) / 700.0F;
                    if (float0 < 0.05F) {
                        float0 = 0.05F;
                    }

                    this.currentTemperature = this.currentTemperature + float0 * GameTime.instance.getMultiplier();
                    if (this.currentTemperature > this.getMaxTemperature()) {
                        this.currentTemperature = this.getMaxTemperature();
                    }
                } else if (this.currentTemperature > this.getMaxTemperature()) {
                    this.currentTemperature = this.currentTemperature
                        - (this.currentTemperature - this.getMaxTemperature()) / 1000.0F * GameTime.instance.getMultiplier();
                    if (this.currentTemperature < 0.0F) {
                        this.currentTemperature = 0.0F;
                    }
                }
            } else if (this.currentTemperature > 0.0F) {
                this.currentTemperature = this.currentTemperature - 0.1F * GameTime.instance.getMultiplier();
                this.currentTemperature = Math.max(this.currentTemperature, 0.0F);
            }

            if (this.container != null && this.isMicrowave()) {
                if (this.Activated()) {
                    this.currentTemperature = this.getMaxTemperature();
                } else {
                    this.currentTemperature = 0.0F;
                }
            }

            if (this.isSpriteGridOriginObject() && this.emitter != null) {
                if (this.Activated() && this.secondsTimer > 0) {
                    if (!this.emitter.isPlaying("StoveTimer")) {
                        this.emitter.playSoundImpl("StoveTimer", this);
                    }
                } else if (this.emitter.isPlaying("StoveTimer")) {
                    this.emitter.stopSoundByName("StoveTimer");
                }
            }
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        if (WorldVersion >= 28) {
            this.activated = input.get() == 1;
        }

        if (WorldVersion >= 106) {
            this.secondsTimer = input.getInt();
            this.maxTemperature = input.getFloat();
            this.firstTurnOn = input.get() == 1;
            this.broken = input.get() == 1;
        }

        if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
            GameClient.instance.objectSyncReq.putRequestLoad(this.square);
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)(this.activated ? 1 : 0));
        output.putInt(this.secondsTimer);
        output.putFloat(this.maxTemperature);
        output.put((byte)(this.firstTurnOn ? 1 : 0));
        output.put((byte)(this.broken ? 1 : 0));
    }

    @Override
    public void addToWorld() {
        if (this.container != null) {
            IsoCell cell = this.getCell();
            cell.addToProcessIsoObject(this);
            this.container.addItemsToProcessItems();
            this.setActivated(this.activated);
        }
    }

    /**
     * Turn on or off the stove, if no electricity it won't work
     */
    @Override
    public void Toggle() {
        SoundManager.instance.PlayWorldSound(this.isMicrowave() ? "ToggleMicrowave" : "ToggleStove", this.getSquare(), 1.0F, 1.0F, 1.0F, false);
        this.setActivated(!this.activated);
        this.container.addItemsToProcessItems();
        IsoGenerator.updateGenerator(this.square);
        this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), null, null);
        this.syncSpriteGridObjects(true, true);
    }

    public void sync() {
        this.syncIsoObject(false, (byte)(this.activated ? 1 : 0), null, null);
    }

    private void doSound() {
        if (GameServer.bServer) {
            this.hasMetal();
        } else if (this.isSpriteGridOriginObject()) {
            if (this.isMicrowave()) {
                if (this.activated) {
                    if (this.emitter != null) {
                        if (this.soundInstance != -1L) {
                            this.emitter.stopSound(this.soundInstance);
                        }

                        this.emitter.stopSoundByName("StoveTimer");
                    }

                    this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ());
                    IsoWorld.instance.setEmitterOwner(this.emitter, this);
                    if (this.hasMetal()) {
                        this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveCookingMetal");
                    } else {
                        this.soundInstance = this.emitter.playSoundLoopedImpl("MicrowaveRunning");
                    }
                } else if (this.soundInstance != -1L) {
                    if (this.emitter != null) {
                        this.emitter.stopSound(this.soundInstance);
                        this.emitter.stopSoundByName("StoveTimer");
                        this.emitter = null;
                    }

                    this.soundInstance = -1L;
                    if (this.container != null && this.container.isPowered()) {
                        BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ());
                        baseSoundEmitter.playSoundImpl("MicrowaveTimerExpired", this);
                    }
                }
            } else if (this.getContainer() != null && "stove".equals(this.container.getType())) {
                if (this.Activated()) {
                    if (this.emitter == null) {
                        this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ());
                        IsoWorld.instance.setEmitterOwner(this.emitter, this);
                        this.soundInstance = this.emitter.playSoundLoopedImpl("StoveRunning");
                    } else if (!this.emitter.isPlaying("StoveRunning")) {
                        this.soundInstance = this.emitter.playSoundLoopedImpl("StoveRunning");
                    }
                } else if (this.soundInstance != -1L) {
                    if (this.emitter != null) {
                        this.emitter.stopSound(this.soundInstance);
                        this.emitter.stopSoundByName("StoveTimer");
                        this.emitter = null;
                    }

                    this.soundInstance = -1L;
                }
            }
        }
    }

    private boolean hasMetal() {
        int int0 = this.getContainer().getItems().size();

        for (int int1 = 0; int1 < int0; int1++) {
            InventoryItem item = this.getContainer().getItems().get(int1);
            if (item.getMetalValue() > 0.0F || item.hasTag("HasMetal")) {
                this.hasMetal = true;
                return true;
            }
        }

        this.hasMetal = false;
        return false;
    }

    @Override
    public String getActivatableType() {
        return "stove";
    }

    @Override
    public void syncIsoObjectSend(ByteBufferWriter b) {
        b.putInt(this.square.getX());
        b.putInt(this.square.getY());
        b.putInt(this.square.getZ());
        byte byte0 = (byte)this.square.getObjects().indexOf(this);
        b.putByte(byte0);
        b.putByte((byte)1);
        b.putByte((byte)(this.activated ? 1 : 0));
        b.putInt(this.secondsTimer);
        b.putFloat(this.maxTemperature);
    }

    @Override
    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source, ByteBuffer bb) {
        if (this.square == null) {
            System.out.println("ERROR: " + this.getClass().getSimpleName() + " square is null");
        } else if (this.getObjectIndex() == -1) {
            System.out
                .println(
                    "ERROR: "
                        + this.getClass().getSimpleName()
                        + " not found on square "
                        + this.square.getX()
                        + ","
                        + this.square.getY()
                        + ","
                        + this.square.getZ()
                );
        } else {
            if (GameClient.bClient && !bRemote) {
                ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter0);
                this.syncIsoObjectSend(byteBufferWriter0);
                PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
            } else if (bRemote) {
                boolean boolean0 = val == 1;
                this.secondsTimer = bb.getInt();
                this.maxTemperature = bb.getFloat();
                this.setActivated(boolean0);
                this.container.addItemsToProcessItems();
                if (GameServer.bServer) {
                    for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
                        if (source == null || udpConnection.getConnectedGUID() != source.getConnectedGUID()) {
                            ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                            PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter1);
                            this.syncIsoObjectSend(byteBufferWriter1);
                            PacketTypes.PacketType.SyncIsoObject.send(udpConnection);
                        }
                    }
                }
            }
        }
    }

    public void setActivated(boolean b) {
        if (!this.isBroken()) {
            this.activated = b;
            if (this.firstTurnOn && this.getMaxTemperature() == 0.0F) {
                if (this.isMicrowave() && this.secondsTimer < 0) {
                    this.maxTemperature = 100.0F;
                }

                if ("stove".equals(this.getContainer().getType()) && this.secondsTimer < 0) {
                    this.maxTemperature = 200.0F;
                }
            }

            if (this.firstTurnOn) {
                this.firstTurnOn = false;
            }

            if (this.activated) {
                if (this.isMicrowave() && this.secondsTimer < 0) {
                    this.secondsTimer = 3600;
                }

                if (this.secondsTimer > 0) {
                    this.startTime = GameTime.instance.getWorldAgeHours();
                    this.stopTime = this.startTime + this.secondsTimer / 3600.0;
                }
            } else {
                this.stopTime = 0.0;
                this.startTime = 0.0;
                this.hasMetal = false;
            }

            this.doSound();
            this.doOverlay();
        }
    }

    private void doOverlay() {
        if (this.Activated() && this.getOverlaySprite() == null) {
            String[] strings = this.getSprite().getName().split("_");
            String string = strings[0] + "_" + strings[1] + "_ON_" + strings[2] + "_" + strings[3];
            this.setOverlaySprite(string);
        } else if (!this.Activated()) {
            this.setOverlaySprite(null);
        }
    }

    public void setTimer(int seconds) {
        this.secondsTimer = seconds;
        if (this.activated && this.secondsTimer > 0) {
            this.startTime = GameTime.instance.getWorldAgeHours();
            this.stopTime = this.startTime + this.secondsTimer / 3600.0;
        }
    }

    public int getTimer() {
        return this.secondsTimer;
    }

    public float getMaxTemperature() {
        return this.maxTemperature;
    }

    public void setMaxTemperature(float _maxTemperature) {
        this.maxTemperature = _maxTemperature;
    }

    public boolean isMicrowave() {
        return this.getContainer() != null && this.getContainer().isMicrowave();
    }

    public int isRunningFor() {
        return this.startTime == 0.0 ? 0 : (int)((GameTime.instance.getWorldAgeHours() - this.startTime) * 3600.0);
    }

    public float getCurrentTemperature() {
        return this.currentTemperature + 100.0F;
    }

    public boolean isTemperatureChanging() {
        return this.currentTemperature != (this.activated ? this.maxTemperature : 0.0F);
    }

    public boolean isBroken() {
        return this.broken;
    }

    public void setBroken(boolean _broken) {
        this.broken = _broken;
    }

    private boolean isSpriteGridOriginObject() {
        IsoSprite sprite = this.getSprite();
        if (sprite == null) {
            return false;
        } else {
            IsoSpriteGrid spriteGrid = sprite.getSpriteGrid();
            if (spriteGrid == null) {
                return true;
            } else {
                int int0 = spriteGrid.getSpriteGridPosX(sprite);
                int int1 = spriteGrid.getSpriteGridPosY(sprite);
                return int0 == 0 && int1 == 0;
            }
        }
    }

    public void syncSpriteGridObjects(boolean toggle, boolean network) {
        this.getSpriteGridObjects(s_tempObjects);

        for (int int0 = s_tempObjects.size() - 1; int0 >= 0; int0--) {
            IsoStove stove = Type.tryCastTo(s_tempObjects.get(int0), IsoStove.class);
            if (stove != null && stove != this) {
                stove.activated = this.activated;
                stove.maxTemperature = this.maxTemperature;
                stove.firstTurnOn = this.firstTurnOn;
                stove.secondsTimer = this.secondsTimer;
                stove.startTime = this.startTime;
                stove.stopTime = this.stopTime;
                stove.hasMetal = this.hasMetal;
                stove.doOverlay();
                stove.doSound();
                if (toggle) {
                    if (stove.container != null) {
                        stove.container.addItemsToProcessItems();
                    }

                    IsoGenerator.updateGenerator(stove.square);
                }

                if (network) {
                    stove.sync();
                }
            }
        }
    }
}
