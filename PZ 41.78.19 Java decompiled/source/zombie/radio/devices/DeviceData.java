// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.devices;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.WorldSoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.core.raknet.VoiceManager;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemUser;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Radio;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoGenerator;
import zombie.iso.objects.IsoWaveSignal;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.radio.ZomboidRadio;
import zombie.radio.media.MediaData;
import zombie.vehicles.VehiclePart;

/**
 * Turbo  Stores shared data for devices (used in iso and item)
 */
public final class DeviceData implements Cloneable {
    private static final float deviceSpeakerSoundMod = 0.4F;
    private static final float deviceButtonSoundVol = 0.05F;
    protected String deviceName = "WaveSignalDevice";
    protected boolean twoWay = false;
    protected int transmitRange = 1000;
    protected int micRange = 5;
    protected boolean micIsMuted = false;
    protected float baseVolumeRange = 15.0F;
    protected float deviceVolume = 1.0F;
    protected boolean isPortable = false;
    protected boolean isTelevision = false;
    protected boolean isHighTier = false;
    protected boolean isTurnedOn = false;
    protected int channel = 88000;
    protected int minChannelRange = 200;
    protected int maxChannelRange = 1000000;
    protected DevicePresets presets = null;
    protected boolean isBatteryPowered = true;
    protected boolean hasBattery = true;
    protected float powerDelta = 1.0F;
    protected float useDelta = 0.001F;
    protected int lastRecordedDistance = -1;
    protected int headphoneType = -1;
    protected WaveSignalDevice parent = null;
    protected GameTime gameTime = null;
    protected boolean channelChangedRecently = false;
    protected BaseSoundEmitter emitter = null;
    protected ArrayList<Long> soundIDs = new ArrayList<>();
    protected short mediaIndex = -1;
    protected byte mediaType = -1;
    protected String mediaItem = null;
    protected MediaData playingMedia = null;
    protected boolean isPlayingMedia = false;
    protected int mediaLineIndex = 0;
    protected float lineCounter = 0.0F;
    protected String currentMediaLine = null;
    protected Color currentMediaColor = null;
    protected boolean isStoppingMedia = false;
    protected float stopMediaCounter = 0.0F;
    protected boolean noTransmit = false;
    private float soundCounterStatic = 0.0F;
    protected long radioLoopSound = 0L;
    protected boolean doTriggerWorldSound = false;
    protected long lastMinuteStamp = -1L;
    protected int listenCnt = 0;
    float nextStaticSound = 0.0F;
    protected float voipCounter = 0.0F;
    protected float signalCounter = 0.0F;
    protected float soundCounter = 0.0F;
    float minmod = 1.5F;
    float maxmod = 5.0F;

    public DeviceData() {
        this(null);
    }

    public DeviceData(WaveSignalDevice _parent) {
        this.parent = _parent;
        this.presets = new DevicePresets();
        this.gameTime = GameTime.getInstance();
    }

    public void generatePresets() {
        if (this.presets == null) {
            this.presets = new DevicePresets();
        }

        this.presets.clearPresets();
        if (this.isTelevision) {
            Map map0 = ZomboidRadio.getInstance().GetChannelList("Television");
            if (map0 != null) {
                for (Entry entry0 : map0.entrySet()) {
                    if ((Integer)entry0.getKey() >= this.minChannelRange && (Integer)entry0.getKey() <= this.maxChannelRange) {
                        this.presets.addPreset((String)entry0.getValue(), (Integer)entry0.getKey());
                    }
                }
            }
        } else {
            int int0 = this.twoWay ? 100 : 300;
            if (this.isHighTier) {
                int0 = 800;
            }

            Map map1 = ZomboidRadio.getInstance().GetChannelList("Emergency");
            if (map1 != null) {
                for (Entry entry1 : map1.entrySet()) {
                    if ((Integer)entry1.getKey() >= this.minChannelRange && (Integer)entry1.getKey() <= this.maxChannelRange && Rand.Next(1000) < int0) {
                        this.presets.addPreset((String)entry1.getValue(), (Integer)entry1.getKey());
                    }
                }
            }

            int0 = this.twoWay ? 100 : 800;
            map1 = ZomboidRadio.getInstance().GetChannelList("Radio");
            if (map1 != null) {
                for (Entry entry2 : map1.entrySet()) {
                    if ((Integer)entry2.getKey() >= this.minChannelRange && (Integer)entry2.getKey() <= this.maxChannelRange && Rand.Next(1000) < int0) {
                        this.presets.addPreset((String)entry2.getValue(), (Integer)entry2.getKey());
                    }
                }
            }

            if (this.twoWay) {
                map1 = ZomboidRadio.getInstance().GetChannelList("Amateur");
                if (map1 != null) {
                    for (Entry entry3 : map1.entrySet()) {
                        if ((Integer)entry3.getKey() >= this.minChannelRange && (Integer)entry3.getKey() <= this.maxChannelRange && Rand.Next(1000) < int0) {
                            this.presets.addPreset((String)entry3.getValue(), (Integer)entry3.getKey());
                        }
                    }
                }
            }

            if (this.isHighTier) {
                map1 = ZomboidRadio.getInstance().GetChannelList("Military");
                if (map1 != null) {
                    for (Entry entry4 : map1.entrySet()) {
                        if ((Integer)entry4.getKey() >= this.minChannelRange && (Integer)entry4.getKey() <= this.maxChannelRange && Rand.Next(1000) < 10) {
                            this.presets.addPreset((String)entry4.getValue(), (Integer)entry4.getKey());
                        }
                    }
                }
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        DeviceData deviceData0 = (DeviceData)super.clone();
        deviceData0.setDevicePresets((DevicePresets)this.presets.clone());
        deviceData0.setParent(null);
        return deviceData0;
    }

    public DeviceData getClone() {
        DeviceData deviceData0;
        try {
            deviceData0 = (DeviceData)this.clone();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            deviceData0 = new DeviceData();
        }

        return deviceData0;
    }

    public WaveSignalDevice getParent() {
        return this.parent;
    }

    public void setParent(WaveSignalDevice p) {
        this.parent = p;
    }

    public DevicePresets getDevicePresets() {
        return this.presets;
    }

    public void setDevicePresets(DevicePresets p) {
        if (p == null) {
            p = new DevicePresets();
        }

        this.presets = p;
    }

    public void cloneDevicePresets(DevicePresets p) throws CloneNotSupportedException {
        this.presets.clearPresets();
        if (p != null) {
            for (int int0 = 0; int0 < p.presets.size(); int0++) {
                PresetEntry presetEntry = p.presets.get(int0);
                this.presets.addPreset(presetEntry.name, presetEntry.frequency);
            }
        }
    }

    public int getMinChannelRange() {
        return this.minChannelRange;
    }

    public void setMinChannelRange(int i) {
        this.minChannelRange = i >= 200 && i <= 1000000 ? i : 200;
    }

    public int getMaxChannelRange() {
        return this.maxChannelRange;
    }

    public void setMaxChannelRange(int i) {
        this.maxChannelRange = i >= 200 && i <= 1000000 ? i : 1000000;
    }

    public boolean getIsHighTier() {
        return this.isHighTier;
    }

    public void setIsHighTier(boolean b) {
        this.isHighTier = b;
    }

    public boolean getIsBatteryPowered() {
        return this.isBatteryPowered;
    }

    public void setIsBatteryPowered(boolean b) {
        this.isBatteryPowered = b;
    }

    public boolean getHasBattery() {
        return this.hasBattery;
    }

    public void setHasBattery(boolean b) {
        this.hasBattery = b;
    }

    public void addBattery(DrainableComboItem bat) {
        if (!this.hasBattery && bat != null && bat.getFullType().equals("Base.Battery")) {
            ItemContainer container = bat.getContainer();
            if (container != null) {
                if (container.getType().equals("floor") && bat.getWorldItem() != null && bat.getWorldItem().getSquare() != null) {
                    bat.getWorldItem().getSquare().transmitRemoveItemFromSquare(bat.getWorldItem());
                    bat.getWorldItem().getSquare().getWorldObjects().remove(bat.getWorldItem());
                    bat.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
                    bat.getWorldItem().getSquare().getObjects().remove(bat.getWorldItem());
                    bat.setWorldItem(null);
                }

                this.powerDelta = bat.getDelta();
                container.DoRemoveItem(bat);
                this.hasBattery = true;
                this.transmitDeviceDataState((short)2);
            }
        }
    }

    public InventoryItem getBattery(ItemContainer inventory) {
        if (this.hasBattery) {
            DrainableComboItem drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.Battery");
            drainableComboItem.setDelta(this.powerDelta);
            this.powerDelta = 0.0F;
            inventory.AddItem(drainableComboItem);
            this.hasBattery = false;
            this.transmitDeviceDataState((short)2);
            return drainableComboItem;
        } else {
            return null;
        }
    }

    public void transmitBattryChange() {
        this.transmitDeviceDataState((short)2);
    }

    public void addHeadphones(InventoryItem headphones) {
        if (this.headphoneType < 0 && (headphones.getFullType().equals("Base.Headphones") || headphones.getFullType().equals("Base.Earbuds"))) {
            ItemContainer container = headphones.getContainer();
            if (container != null) {
                if (container.getType().equals("floor") && headphones.getWorldItem() != null && headphones.getWorldItem().getSquare() != null) {
                    headphones.getWorldItem().getSquare().transmitRemoveItemFromSquare(headphones.getWorldItem());
                    headphones.getWorldItem().getSquare().getWorldObjects().remove(headphones.getWorldItem());
                    headphones.getWorldItem().getSquare().chunk.recalcHashCodeObjects();
                    headphones.getWorldItem().getSquare().getObjects().remove(headphones.getWorldItem());
                    headphones.setWorldItem(null);
                }

                int int0 = headphones.getFullType().equals("Base.Headphones") ? 0 : 1;
                container.DoRemoveItem(headphones);
                this.setHeadphoneType(int0);
                this.transmitDeviceDataState((short)6);
            }
        }
    }

    public InventoryItem getHeadphones(ItemContainer inventory) {
        if (this.headphoneType >= 0) {
            InventoryItem item = null;
            if (this.headphoneType == 0) {
                item = InventoryItemFactory.CreateItem("Base.Headphones");
            } else if (this.headphoneType == 1) {
                item = InventoryItemFactory.CreateItem("Base.Earbuds");
            }

            if (item != null) {
                inventory.AddItem(item);
            }

            this.setHeadphoneType(-1);
            this.transmitDeviceDataState((short)6);
        }

        return null;
    }

    public int getMicRange() {
        return this.micRange;
    }

    public void setMicRange(int i) {
        this.micRange = i;
    }

    public boolean getMicIsMuted() {
        return this.micIsMuted;
    }

    public void setMicIsMuted(boolean b) {
        this.micIsMuted = b;
        if (this.getParent() != null
            && this.getParent() instanceof Radio
            && ((Radio)this.getParent()).getEquipParent() != null
            && ((Radio)this.getParent()).getEquipParent() instanceof IsoPlayer) {
            IsoPlayer player = (IsoPlayer)((Radio)this.getParent()).getEquipParent();
            player.updateEquippedRadioFreq();
        }
    }

    public int getHeadphoneType() {
        return this.headphoneType;
    }

    public void setHeadphoneType(int i) {
        this.headphoneType = i;
    }

    public float getBaseVolumeRange() {
        return this.baseVolumeRange;
    }

    public void setBaseVolumeRange(float f) {
        this.baseVolumeRange = f;
    }

    public float getDeviceVolume() {
        return this.deviceVolume;
    }

    public void setDeviceVolume(float f) {
        this.deviceVolume = f < 0.0F ? 0.0F : (f > 1.0F ? 1.0F : f);
        this.transmitDeviceDataState((short)4);
    }

    public void setDeviceVolumeRaw(float f) {
        this.deviceVolume = f < 0.0F ? 0.0F : (f > 1.0F ? 1.0F : f);
    }

    public boolean getIsTelevision() {
        return this.isTelevision;
    }

    public void setIsTelevision(boolean b) {
        this.isTelevision = b;
    }

    public String getDeviceName() {
        return this.deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public boolean getIsTwoWay() {
        return this.twoWay;
    }

    public void setIsTwoWay(boolean b) {
        this.twoWay = b;
    }

    public int getTransmitRange() {
        return this.transmitRange;
    }

    public void setTransmitRange(int range) {
        this.transmitRange = range > 0 ? range : 0;
    }

    public boolean getIsPortable() {
        return this.isPortable;
    }

    public void setIsPortable(boolean b) {
        this.isPortable = b;
    }

    public boolean getIsTurnedOn() {
        return this.isTurnedOn;
    }

    public void setIsTurnedOn(boolean b) {
        if (this.canBePoweredHere()) {
            if (this.isBatteryPowered && !(this.powerDelta > 0.0F)) {
                this.isTurnedOn = false;
            } else {
                this.isTurnedOn = b;
            }

            this.playSoundSend("RadioButton", false);
            this.transmitDeviceDataState((short)0);
        } else if (this.isTurnedOn) {
            this.isTurnedOn = false;
            this.playSoundSend("RadioButton", false);
            this.transmitDeviceDataState((short)0);
        }

        if (this.getParent() != null
            && this.getParent() instanceof Radio
            && ((Radio)this.getParent()).getEquipParent() != null
            && ((Radio)this.getParent()).getEquipParent() instanceof IsoPlayer) {
            IsoPlayer player = (IsoPlayer)((Radio)this.getParent()).getEquipParent();
            player.updateEquippedRadioFreq();
        }

        IsoGenerator.updateGenerator(this.getParent().getSquare());
    }

    public void setTurnedOnRaw(boolean b) {
        this.isTurnedOn = b;
        if (this.getParent() != null
            && this.getParent() instanceof Radio
            && ((Radio)this.getParent()).getEquipParent() != null
            && ((Radio)this.getParent()).getEquipParent() instanceof IsoPlayer) {
            IsoPlayer player = (IsoPlayer)((Radio)this.getParent()).getEquipParent();
            player.updateEquippedRadioFreq();
        }
    }

    public boolean canBePoweredHere() {
        if (this.isBatteryPowered) {
            return true;
        } else if (this.parent instanceof VehiclePart part) {
            return part.isInventoryItemUninstalled() ? false : part.hasDevicePower();
        } else {
            boolean boolean0 = false;
            if (IsoWorld.instance.isHydroPowerOn()) {
                boolean0 = true;
            }

            if (this.parent == null || this.parent.getSquare() == null) {
                boolean0 = false;
            } else if (this.parent.getSquare().haveElectricity()) {
                boolean0 = true;
            } else if (this.parent.getSquare().getRoom() == null) {
                boolean0 = false;
            }

            return boolean0;
        }
    }

    public void setRandomChannel() {
        if (this.presets != null && this.presets.getPresets().size() > 0) {
            int int0 = Rand.Next(0, this.presets.getPresets().size());
            this.channel = this.presets.getPresets().get(int0).getFrequency();
        } else {
            this.channel = Rand.Next(this.minChannelRange, this.maxChannelRange);
            this.channel = this.channel - this.channel % 200;
        }
    }

    public int getChannel() {
        return this.channel;
    }

    public void setChannel(int c) {
        this.setChannel(c, true);
    }

    public void setChannel(int chan, boolean setislistening) {
        if (chan >= this.minChannelRange && chan <= this.maxChannelRange) {
            this.channel = chan;
            this.playSoundSend("RadioButton", false);
            if (this.isTelevision) {
                this.playSoundSend("TelevisionZap", true);
            } else {
                this.playSoundSend("RadioZap", true);
            }

            if (this.radioLoopSound > 0L) {
                this.emitter.stopSound(this.radioLoopSound);
                this.radioLoopSound = 0L;
            }

            this.transmitDeviceDataState((short)1);
            if (setislistening) {
                this.TriggerPlayerListening(true);
            }
        }
    }

    public void setChannelRaw(int chan) {
        this.channel = chan;
    }

    public float getUseDelta() {
        return this.useDelta;
    }

    public void setUseDelta(float f) {
        this.useDelta = f / 60.0F;
    }

    public float getPower() {
        return this.powerDelta;
    }

    public void setPower(float p) {
        if (p > 1.0F) {
            p = 1.0F;
        }

        if (p < 0.0F) {
            p = 0.0F;
        }

        this.powerDelta = p;
    }

    public void setInitialPower() {
        this.lastMinuteStamp = this.gameTime.getMinutesStamp();
        this.setPower(this.powerDelta - this.useDelta * (float)this.lastMinuteStamp);
    }

    public void TriggerPlayerListening(boolean listening) {
        if (this.isTurnedOn) {
            ZomboidRadio.getInstance().PlayerListensChannel(this.channel, true, this.isTelevision);
        }
    }

    public void playSoundSend(String soundname, boolean useDeviceVolume) {
        this.playSound(soundname, useDeviceVolume ? this.deviceVolume * 0.4F : 0.05F, true);
    }

    public void playSoundLocal(String soundname, boolean useDeviceVolume) {
        this.playSound(soundname, useDeviceVolume ? this.deviceVolume * 0.4F : 0.05F, false);
    }

    public void playSound(String soundname, float volume, boolean transmit) {
        if (!GameServer.bServer) {
            this.setEmitterAndPos();
            if (this.emitter != null) {
                long long0 = transmit ? this.emitter.playSound(soundname) : this.emitter.playSoundImpl(soundname, (IsoObject)null);
                this.emitter.setVolume(long0, volume);
            }
        }
    }

    public void cleanSoundsAndEmitter() {
        if (this.emitter != null) {
            this.emitter.stopAll();
            IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
            this.emitter = null;
            this.radioLoopSound = 0L;
        }
    }

    protected void setEmitterAndPos() {
        Object object = null;
        if (this.parent != null && this.parent instanceof IsoObject) {
            object = (IsoObject)this.parent;
        } else if (this.parent != null && this.parent instanceof Radio) {
            object = IsoPlayer.getInstance();
        }

        if (object != null) {
            if (this.emitter == null) {
                this.emitter = IsoWorld.instance
                    .getFreeEmitter(((IsoObject)object).getX() + 0.5F, ((IsoObject)object).getY() + 0.5F, (int)((IsoObject)object).getZ());
                IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
            } else {
                this.emitter.setPos(((IsoObject)object).getX() + 0.5F, ((IsoObject)object).getY() + 0.5F, (int)((IsoObject)object).getZ());
            }

            if (this.radioLoopSound != 0L) {
                this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4F);
            }
        }
    }

    protected void updateEmitter() {
        if (!GameServer.bServer) {
            if (!this.isTurnedOn) {
                if (this.emitter != null && this.emitter.isPlaying("RadioButton")) {
                    if (this.radioLoopSound > 0L) {
                        this.emitter.stopSound(this.radioLoopSound);
                    }

                    this.setEmitterAndPos();
                    this.emitter.tick();
                } else {
                    this.cleanSoundsAndEmitter();
                }
            } else {
                this.setEmitterAndPos();
                if (this.emitter != null) {
                    if (this.signalCounter > 0.0F && !this.emitter.isPlaying("RadioTalk")) {
                        if (this.radioLoopSound > 0L) {
                            this.emitter.stopSound(this.radioLoopSound);
                        }

                        this.radioLoopSound = this.emitter.playSoundImpl("RadioTalk", (IsoObject)null);
                        this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4F);
                    }

                    String string = !this.isTelevision ? "RadioStatic" : "TelevisionTestBeep";
                    if (this.radioLoopSound == 0L || this.signalCounter <= 0.0F && !this.emitter.isPlaying(string)) {
                        if (this.radioLoopSound > 0L) {
                            this.emitter.stopSound(this.radioLoopSound);
                            if (this.isTelevision) {
                                this.playSoundLocal("TelevisionZap", true);
                            } else {
                                this.playSoundLocal("RadioZap", true);
                            }
                        }

                        this.radioLoopSound = this.emitter.playSoundImpl(string, (IsoObject)null);
                        this.emitter.setVolume(this.radioLoopSound, this.deviceVolume * 0.4F);
                    }

                    this.emitter.tick();
                }
            }
        }
    }

    public BaseSoundEmitter getEmitter() {
        return this.emitter;
    }

    public void update(boolean isIso, boolean playerInRange) {
        if (this.lastMinuteStamp == -1L) {
            this.lastMinuteStamp = this.gameTime.getMinutesStamp();
        }

        if (this.gameTime.getMinutesStamp() > this.lastMinuteStamp) {
            long long0 = this.gameTime.getMinutesStamp() - this.lastMinuteStamp;
            this.lastMinuteStamp = this.gameTime.getMinutesStamp();
            this.listenCnt = (int)(this.listenCnt + long0);
            if (this.listenCnt >= 10) {
                this.listenCnt = 0;
            }

            if (!GameServer.bServer && this.isTurnedOn && playerInRange && (this.listenCnt == 0 || this.listenCnt == 5)) {
                this.TriggerPlayerListening(true);
            }

            if (this.isTurnedOn && this.isBatteryPowered && this.powerDelta > 0.0F) {
                float float0 = this.powerDelta - this.powerDelta % 0.01F;
                this.setPower(this.powerDelta - this.useDelta * (float)long0);
                if (this.listenCnt == 0 || this.powerDelta == 0.0F || this.powerDelta < float0) {
                    if (isIso && GameServer.bServer) {
                        this.transmitDeviceDataStateServer((short)3, null);
                    } else if (!isIso && GameClient.bClient) {
                        this.transmitDeviceDataState((short)3);
                    }
                }
            }
        }

        if (this.isTurnedOn && (this.isBatteryPowered && this.powerDelta <= 0.0F || !this.canBePoweredHere())) {
            this.isTurnedOn = false;
            if (isIso && GameServer.bServer) {
                this.transmitDeviceDataStateServer((short)0, null);
            } else if (!isIso && GameClient.bClient) {
                this.transmitDeviceDataState((short)0);
            }
        }

        this.updateMediaPlaying();
        this.updateEmitter();
        this.updateSimple();
    }

    public void updateSimple() {
        if (this.voipCounter >= 0.0F) {
            this.voipCounter = this.voipCounter - 1.25F * GameTime.getInstance().getMultiplier();
        }

        if (this.signalCounter >= 0.0F) {
            this.signalCounter = this.signalCounter - 1.25F * GameTime.getInstance().getMultiplier();
        }

        if (this.soundCounter >= 0.0F) {
            this.soundCounter = (float)(this.soundCounter - 1.25 * GameTime.getInstance().getMultiplier());
        }

        if (this.signalCounter <= 0.0F && this.voipCounter <= 0.0F && this.lastRecordedDistance >= 0) {
            this.lastRecordedDistance = -1;
        }

        this.updateStaticSounds();
        if (GameClient.bClient) {
            this.updateEmitter();
        }

        if (this.doTriggerWorldSound && this.soundCounter <= 0.0F) {
            if (this.isTurnedOn
                && this.deviceVolume > 0.0F
                && (!this.isInventoryDevice() || this.headphoneType < 0)
                && (
                    !GameClient.bClient && !GameServer.bServer
                        || GameClient.bClient && this.isInventoryDevice()
                        || GameServer.bServer && !this.isInventoryDevice()
                )) {
                Object object = null;
                if (this.parent != null && this.parent instanceof IsoObject) {
                    object = (IsoObject)this.parent;
                } else if (this.parent != null && this.parent instanceof Radio) {
                    object = IsoPlayer.getInstance();
                } else if (this.parent instanceof VehiclePart) {
                    object = ((VehiclePart)this.parent).getVehicle();
                }

                if (object != null) {
                    int int0 = (int)(100.0F * this.deviceVolume);
                    int int1 = this.getDeviceSoundVolumeRange();
                    WorldSoundManager.instance
                        .addSoundRepeating(
                            object, (int)((IsoObject)object).getX(), (int)((IsoObject)object).getY(), (int)((IsoObject)object).getZ(), int1, int0, int0 > 50
                        );
                }
            }

            this.doTriggerWorldSound = false;
            this.soundCounter = 300 + Rand.Next(0, 300);
        }
    }

    private void updateStaticSounds() {
        if (this.isTurnedOn) {
            float float0 = GameTime.getInstance().getMultiplier();
            this.nextStaticSound -= float0;
            if (this.nextStaticSound <= 0.0F) {
                if (this.parent != null && this.signalCounter <= 0.0F && !this.isNoTransmit() && !this.isPlayingMedia()) {
                    this.parent.AddDeviceText(ZomboidRadio.getInstance().getRandomBzztFzzt(), 1.0F, 1.0F, 1.0F, null, null, -1);
                    this.doTriggerWorldSound = true;
                }

                this.setNextStaticSound();
            }
        }
    }

    private void setNextStaticSound() {
        this.nextStaticSound = Rand.Next(250.0F, 1500.0F);
    }

    public int getDeviceVolumeRange() {
        return 5 + (int)(this.baseVolumeRange * this.deviceVolume);
    }

    public int getDeviceSoundVolumeRange() {
        if (this.isInventoryDevice()) {
            Radio radio = (Radio)this.getParent();
            return radio.getPlayer() != null && radio.getPlayer().getSquare() != null && radio.getPlayer().getSquare().getRoom() != null
                ? 3 + (int)(this.baseVolumeRange * 0.4F * this.deviceVolume)
                : 5 + (int)(this.baseVolumeRange * this.deviceVolume);
        } else if (this.isIsoDevice()) {
            IsoWaveSignal waveSignal = (IsoWaveSignal)this.getParent();
            return waveSignal.getSquare() != null && waveSignal.getSquare().getRoom() != null
                ? 3 + (int)(this.baseVolumeRange * 0.5F * this.deviceVolume)
                : 5 + (int)(this.baseVolumeRange * 0.75F * this.deviceVolume);
        } else {
            return 5 + (int)(this.baseVolumeRange / 2.0F * this.deviceVolume);
        }
    }

    public void doReceiveSignal(int distance) {
        if (this.isTurnedOn) {
            this.lastRecordedDistance = distance;
            if (this.deviceVolume > 0.0F && (this.isIsoDevice() || this.headphoneType < 0)) {
                Object object = null;
                if (this.parent != null && this.parent instanceof IsoObject) {
                    object = (IsoObject)this.parent;
                } else if (this.parent != null && this.parent instanceof Radio) {
                    object = IsoPlayer.getInstance();
                } else if (this.parent instanceof VehiclePart) {
                    object = ((VehiclePart)this.parent).getVehicle();
                }

                if (object != null && this.soundCounter <= 0.0F) {
                    int int0 = (int)(100.0F * this.deviceVolume);
                    int int1 = this.getDeviceSoundVolumeRange();
                    WorldSoundManager.instance
                        .addSound(
                            object, (int)((IsoObject)object).getX(), (int)((IsoObject)object).getY(), (int)((IsoObject)object).getZ(), int1, int0, int0 > 50
                        );
                    this.soundCounter = 120.0F;
                }
            }

            this.signalCounter = 300.0F;
            this.doTriggerWorldSound = true;
            this.setNextStaticSound();
        }
    }

    public void doReceiveMPSignal(float distance) {
        this.lastRecordedDistance = (int)distance;
        this.voipCounter = 10.0F;
    }

    public boolean isReceivingSignal() {
        return this.signalCounter > 0.0F || this.voipCounter > 0.0F;
    }

    public int getLastRecordedDistance() {
        return this.lastRecordedDistance;
    }

    public boolean isIsoDevice() {
        return this.getParent() != null && this.getParent() instanceof IsoWaveSignal;
    }

    public boolean isInventoryDevice() {
        return this.getParent() != null && this.getParent() instanceof Radio;
    }

    public boolean isVehicleDevice() {
        return this.getParent() instanceof VehiclePart;
    }

    public void transmitPresets() {
        this.transmitDeviceDataState((short)5);
    }

    private void transmitDeviceDataState(short short0) {
        if (GameClient.bClient) {
            try {
                VoiceManager.getInstance().UpdateChannelsRoaming(GameClient.connection);
                this.sendDeviceDataStatePacket(GameClient.connection, short0);
            } catch (Exception exception) {
                System.out.print(exception.getMessage());
            }
        }
    }

    private void transmitDeviceDataStateServer(short short0, UdpConnection udpConnection1) {
        if (GameServer.bServer) {
            try {
                for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                    UdpConnection udpConnection0 = GameServer.udpEngine.connections.get(int0);
                    if (udpConnection1 == null || udpConnection1 != udpConnection0) {
                        this.sendDeviceDataStatePacket(udpConnection0, short0);
                    }
                }
            } catch (Exception exception) {
                System.out.print(exception.getMessage());
            }
        }
    }

    private void sendDeviceDataStatePacket(UdpConnection udpConnection, short short0) {
        ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
        PacketTypes.PacketType.RadioDeviceDataState.doPacket(byteBufferWriter);
        boolean boolean0 = false;
        if (this.isIsoDevice()) {
            IsoWaveSignal waveSignal = (IsoWaveSignal)this.getParent();
            IsoGridSquare square = waveSignal.getSquare();
            if (square != null) {
                byteBufferWriter.putByte((byte)1);
                byteBufferWriter.putInt(square.getX());
                byteBufferWriter.putInt(square.getY());
                byteBufferWriter.putInt(square.getZ());
                byteBufferWriter.putInt(square.getObjects().indexOf(waveSignal));
                boolean0 = true;
            }
        } else if (this.isInventoryDevice()) {
            Radio radio = (Radio)this.getParent();
            IsoPlayer player = null;
            if (radio.getEquipParent() != null && radio.getEquipParent() instanceof IsoPlayer) {
                player = (IsoPlayer)radio.getEquipParent();
            }

            if (player != null) {
                byteBufferWriter.putByte((byte)0);
                if (GameServer.bServer) {
                    byteBufferWriter.putShort(player != null ? player.OnlineID : -1);
                } else {
                    byteBufferWriter.putByte((byte)player.PlayerIndex);
                }

                if (player.getPrimaryHandItem() == radio) {
                    byteBufferWriter.putByte((byte)1);
                } else if (player.getSecondaryHandItem() == radio) {
                    byteBufferWriter.putByte((byte)2);
                } else {
                    byteBufferWriter.putByte((byte)0);
                }

                boolean0 = true;
            }
        } else if (this.isVehicleDevice()) {
            VehiclePart part = (VehiclePart)this.getParent();
            byteBufferWriter.putByte((byte)2);
            byteBufferWriter.putShort(part.getVehicle().VehicleID);
            byteBufferWriter.putShort((short)part.getIndex());
            boolean0 = true;
        }

        if (boolean0) {
            byteBufferWriter.putShort(short0);
            switch (short0) {
                case 0:
                    byteBufferWriter.putByte((byte)(this.isTurnedOn ? 1 : 0));
                    break;
                case 1:
                    byteBufferWriter.putInt(this.channel);
                    break;
                case 2:
                    byteBufferWriter.putByte((byte)(this.hasBattery ? 1 : 0));
                    byteBufferWriter.putFloat(this.powerDelta);
                    break;
                case 3:
                    byteBufferWriter.putFloat(this.powerDelta);
                    break;
                case 4:
                    byteBufferWriter.putFloat(this.deviceVolume);
                    break;
                case 5:
                    byteBufferWriter.putInt(this.presets.getPresets().size());

                    for (PresetEntry presetEntry : this.presets.getPresets()) {
                        GameWindow.WriteString(byteBufferWriter.bb, presetEntry.getName());
                        byteBufferWriter.putInt(presetEntry.getFrequency());
                    }
                    break;
                case 6:
                    byteBufferWriter.putInt(this.headphoneType);
                    break;
                case 7:
                    byteBufferWriter.putShort(this.mediaIndex);
                    byteBufferWriter.putByte((byte)(this.mediaItem != null ? 1 : 0));
                    if (this.mediaItem != null) {
                        GameWindow.WriteString(byteBufferWriter.bb, this.mediaItem);
                    }
                    break;
                case 8:
                    if (GameServer.bServer) {
                        byteBufferWriter.putShort(this.mediaIndex);
                        byteBufferWriter.putByte((byte)(this.mediaItem != null ? 1 : 0));
                        if (this.mediaItem != null) {
                            GameWindow.WriteString(byteBufferWriter.bb, this.mediaItem);
                        }
                    }
                case 9:
                default:
                    break;
                case 10:
                    if (GameServer.bServer) {
                        byteBufferWriter.putShort(this.mediaIndex);
                        byteBufferWriter.putInt(this.mediaLineIndex);
                    }
            }

            PacketTypes.PacketType.RadioDeviceDataState.send(udpConnection);
        } else {
            udpConnection.cancelPacket();
        }
    }

    public void receiveDeviceDataStatePacket(ByteBuffer bb, UdpConnection ignoreConnection) throws IOException {
        if (GameClient.bClient || GameServer.bServer) {
            boolean boolean0 = GameServer.bServer;
            boolean boolean1 = this.isIsoDevice() || this.isVehicleDevice();
            short short0 = bb.getShort();
            switch (short0) {
                case 0:
                    if (boolean0 && boolean1) {
                        this.setIsTurnedOn(bb.get() == 1);
                    } else {
                        this.isTurnedOn = bb.get() == 1;
                    }

                    if (boolean0) {
                        this.transmitDeviceDataStateServer(short0, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 1:
                    int int1 = bb.getInt();
                    if (boolean0 && boolean1) {
                        this.setChannel(int1);
                    } else {
                        this.channel = int1;
                    }

                    if (boolean0) {
                        this.transmitDeviceDataStateServer(short0, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 2:
                    boolean boolean2 = bb.get() == 1;
                    float float0 = bb.getFloat();
                    if (boolean0 && boolean1) {
                        this.hasBattery = boolean2;
                        this.setPower(float0);
                    } else {
                        this.hasBattery = boolean2;
                        this.powerDelta = float0;
                    }

                    if (boolean0) {
                        this.transmitDeviceDataStateServer(short0, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 3:
                    float float1 = bb.getFloat();
                    if (boolean0 && boolean1) {
                        this.setPower(float1);
                    } else {
                        this.powerDelta = float1;
                    }

                    if (boolean0) {
                        this.transmitDeviceDataStateServer(short0, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 4:
                    float float2 = bb.getFloat();
                    if (boolean0 && boolean1) {
                        this.setDeviceVolume(float2);
                    } else {
                        this.deviceVolume = float2;
                    }

                    if (boolean0) {
                        this.transmitDeviceDataStateServer(short0, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 5:
                    int int2 = bb.getInt();

                    for (int int3 = 0; int3 < int2; int3++) {
                        String string3 = GameWindow.ReadString(bb);
                        int int4 = bb.getInt();
                        if (int3 < this.presets.getPresets().size()) {
                            PresetEntry presetEntry = this.presets.getPresets().get(int3);
                            if (!presetEntry.getName().equals(string3) || presetEntry.getFrequency() != int4) {
                                presetEntry.setName(string3);
                                presetEntry.setFrequency(int4);
                            }
                        } else {
                            this.presets.addPreset(string3, int4);
                        }
                    }

                    if (boolean0) {
                        this.transmitDeviceDataStateServer((short)5, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 6:
                    this.headphoneType = bb.getInt();
                    if (boolean0) {
                        this.transmitDeviceDataStateServer(short0, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 7:
                    this.mediaIndex = bb.getShort();
                    if (bb.get() == 1) {
                        this.mediaItem = GameWindow.ReadString(bb);
                    }

                    if (boolean0) {
                        this.transmitDeviceDataStateServer(short0, !boolean1 ? ignoreConnection : null);
                    }
                    break;
                case 8:
                    if (GameServer.bServer) {
                        this.StartPlayMedia();
                    } else {
                        this.mediaIndex = bb.getShort();
                        if (bb.get() == 1) {
                            this.mediaItem = GameWindow.ReadString(bb);
                        }

                        this.isPlayingMedia = true;
                        this.televisionMediaSwitch();
                    }
                    break;
                case 9:
                    if (GameServer.bServer) {
                        this.StopPlayMedia();
                    } else {
                        this.isPlayingMedia = false;
                        this.televisionMediaSwitch();
                    }
                    break;
                case 10:
                    if (GameClient.bClient) {
                        this.mediaIndex = bb.getShort();
                        int int0 = bb.getInt();
                        MediaData mediaData = this.getMediaData();
                        if (mediaData != null && int0 >= 0 && int0 < mediaData.getLineCount()) {
                            MediaData.MediaLineData mediaLineData = mediaData.getLine(int0);
                            String string0 = mediaLineData.getTranslatedText();
                            Color color = mediaLineData.getColor();
                            String string1 = mediaLineData.getTextGuid();
                            String string2 = mediaLineData.getCodes();
                            this.parent.AddDeviceText(string0, color.r, color.g, color.b, string1, string2, 0);
                        }
                    }
            }
        }
    }

    public void save(ByteBuffer output, boolean net) throws IOException {
        GameWindow.WriteString(output, this.deviceName);
        output.put((byte)(this.twoWay ? 1 : 0));
        output.putInt(this.transmitRange);
        output.putInt(this.micRange);
        output.put((byte)(this.micIsMuted ? 1 : 0));
        output.putFloat(this.baseVolumeRange);
        output.putFloat(this.deviceVolume);
        output.put((byte)(this.isPortable ? 1 : 0));
        output.put((byte)(this.isTelevision ? 1 : 0));
        output.put((byte)(this.isHighTier ? 1 : 0));
        output.put((byte)(this.isTurnedOn ? 1 : 0));
        output.putInt(this.channel);
        output.putInt(this.minChannelRange);
        output.putInt(this.maxChannelRange);
        output.put((byte)(this.isBatteryPowered ? 1 : 0));
        output.put((byte)(this.hasBattery ? 1 : 0));
        output.putFloat(this.powerDelta);
        output.putFloat(this.useDelta);
        output.putInt(this.headphoneType);
        if (this.presets != null) {
            output.put((byte)1);
            this.presets.save(output, net);
        } else {
            output.put((byte)0);
        }

        output.putShort(this.mediaIndex);
        output.put(this.mediaType);
        output.put((byte)(this.mediaItem != null ? 1 : 0));
        if (this.mediaItem != null) {
            GameWindow.WriteString(output, this.mediaItem);
        }

        output.put((byte)(this.noTransmit ? 1 : 0));
    }

    public void load(ByteBuffer input, int WorldVersion, boolean net) throws IOException {
        if (this.presets == null) {
            this.presets = new DevicePresets();
        }

        if (WorldVersion >= 69) {
            this.deviceName = GameWindow.ReadString(input);
            this.twoWay = input.get() == 1;
            this.transmitRange = input.getInt();
            this.micRange = input.getInt();
            this.micIsMuted = input.get() == 1;
            this.baseVolumeRange = input.getFloat();
            this.deviceVolume = input.getFloat();
            this.isPortable = input.get() == 1;
            this.isTelevision = input.get() == 1;
            this.isHighTier = input.get() == 1;
            this.isTurnedOn = input.get() == 1;
            this.channel = input.getInt();
            this.minChannelRange = input.getInt();
            this.maxChannelRange = input.getInt();
            this.isBatteryPowered = input.get() == 1;
            this.hasBattery = input.get() == 1;
            this.powerDelta = input.getFloat();
            this.useDelta = input.getFloat();
            this.headphoneType = input.getInt();
            if (input.get() == 1) {
                this.presets.load(input, WorldVersion, net);
            }
        }

        if (WorldVersion >= 181) {
            this.mediaIndex = input.getShort();
            this.mediaType = input.get();
            if (input.get() == 1) {
                this.mediaItem = GameWindow.ReadString(input);
            }

            this.noTransmit = input.get() == 1;
        }
    }

    public boolean hasMedia() {
        return this.mediaIndex >= 0;
    }

    public short getMediaIndex() {
        return this.mediaIndex;
    }

    public void setMediaIndex(short _mediaIndex) {
        this.mediaIndex = _mediaIndex;
    }

    public byte getMediaType() {
        return this.mediaType;
    }

    public void setMediaType(byte _mediaType) {
        this.mediaType = _mediaType;
    }

    public void addMediaItem(InventoryItem media) {
        if (this.mediaIndex < 0 && media.isRecordedMedia() && media.getMediaType() == this.mediaType) {
            ItemContainer container = media.getContainer();
            if (container != null) {
                this.mediaIndex = media.getRecordedMediaIndex();
                this.mediaItem = media.getFullType();
                ItemUser.RemoveItem(media);
                this.transmitDeviceDataState((short)7);
            }
        }
    }

    public InventoryItem removeMediaItem(ItemContainer inventory) {
        if (this.hasMedia()) {
            InventoryItem item = InventoryItemFactory.CreateItem(this.mediaItem);
            item.setRecordedMediaIndex(this.mediaIndex);
            inventory.AddItem(item);
            this.mediaIndex = -1;
            this.mediaItem = null;
            if (this.isPlayingMedia()) {
                this.StopPlayMedia();
            }

            this.transmitDeviceDataState((short)7);
            return item;
        } else {
            return null;
        }
    }

    public boolean isPlayingMedia() {
        return this.isPlayingMedia;
    }

    public void StartPlayMedia() {
        if (GameClient.bClient) {
            this.transmitDeviceDataState((short)8);
        } else if (!this.isPlayingMedia() && this.getIsTurnedOn() && this.hasMedia()) {
            this.playingMedia = ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.mediaIndex);
            if (this.playingMedia != null) {
                this.isPlayingMedia = true;
                this.mediaLineIndex = 0;
                this.prePlayingMedia();
                if (GameServer.bServer) {
                    this.transmitDeviceDataStateServer((short)8, null);
                }
            }
        }
    }

    private void prePlayingMedia() {
        this.lineCounter = 60.0F * this.maxmod * 0.5F;
        this.televisionMediaSwitch();
    }

    private void postPlayingMedia() {
        this.isStoppingMedia = true;
        this.stopMediaCounter = 60.0F * this.maxmod * 0.5F;
        this.televisionMediaSwitch();
    }

    private void televisionMediaSwitch() {
        if (this.mediaType == 1) {
            ZomboidRadio.getInstance().getRandomBzztFzzt();
            this.parent.AddDeviceText(ZomboidRadio.getInstance().getRandomBzztFzzt(), 0.5F, 0.5F, 0.5F, null, null, 0);
            this.playSoundLocal("TelevisionZap", true);
        }
    }

    public void StopPlayMedia() {
        if (GameClient.bClient) {
            this.transmitDeviceDataState((short)9);
        } else {
            this.playingMedia = null;
            this.postPlayingMedia();
            if (GameServer.bServer) {
                this.transmitDeviceDataStateServer((short)9, null);
            }
        }
    }

    public void updateMediaPlaying() {
        if (!GameClient.bClient) {
            if (this.isStoppingMedia) {
                this.stopMediaCounter = this.stopMediaCounter - 1.25F * GameTime.getInstance().getMultiplier();
                if (this.stopMediaCounter <= 0.0F) {
                    this.isPlayingMedia = false;
                    this.isStoppingMedia = false;
                }
            } else {
                if (this.hasMedia() && this.isPlayingMedia()) {
                    if (!this.getIsTurnedOn()) {
                        this.StopPlayMedia();
                        return;
                    }

                    if (this.playingMedia != null) {
                        this.lineCounter = this.lineCounter - 1.25F * GameTime.getInstance().getMultiplier();
                        if (this.lineCounter <= 0.0F) {
                            MediaData.MediaLineData mediaLineData = this.playingMedia.getLine(this.mediaLineIndex);
                            if (mediaLineData != null) {
                                String string0 = mediaLineData.getTranslatedText();
                                Color color = mediaLineData.getColor();
                                this.lineCounter = string0.length() / 10.0F * 60.0F;
                                if (this.lineCounter < 60.0F * this.minmod) {
                                    this.lineCounter = 60.0F * this.minmod;
                                } else if (this.lineCounter > 60.0F * this.maxmod) {
                                    this.lineCounter = 60.0F * this.maxmod;
                                }

                                if (GameServer.bServer) {
                                    this.currentMediaLine = string0;
                                    this.currentMediaColor = color;
                                    this.transmitDeviceDataStateServer((short)10, null);
                                } else {
                                    String string1 = mediaLineData.getTextGuid();
                                    String string2 = mediaLineData.getCodes();
                                    this.parent.AddDeviceText(string0, color.r, color.g, color.b, string1, string2, 0);
                                }

                                this.mediaLineIndex++;
                            } else {
                                this.StopPlayMedia();
                            }
                        }
                    }
                }
            }
        }
    }

    public MediaData getMediaData() {
        return this.mediaIndex >= 0 ? ZomboidRadio.getInstance().getRecordedMedia().getMediaDataFromIndex(this.mediaIndex) : null;
    }

    public boolean isNoTransmit() {
        return this.noTransmit;
    }

    public void setNoTransmit(boolean _noTransmit) {
        this.noTransmit = _noTransmit;
    }
}
