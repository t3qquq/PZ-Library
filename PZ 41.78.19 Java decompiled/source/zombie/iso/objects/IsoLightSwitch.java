// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.SystemDisabler;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.Moveable;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoRoomLight;
import zombie.iso.IsoWorld;
import zombie.iso.areas.IsoRoom;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public class IsoLightSwitch extends IsoObject {
    boolean Activated = false;
    public final ArrayList<IsoLightSource> lights = new ArrayList<>();
    public boolean lightRoom = false;
    public int RoomID = -1;
    public boolean bStreetLight = false;
    private boolean canBeModified = false;
    private boolean useBattery = false;
    private boolean hasBattery = false;
    private String bulbItem = "Base.LightBulb";
    private float power = 0.0F;
    private float delta = 2.5E-4F;
    private float primaryR = 1.0F;
    private float primaryG = 1.0F;
    private float primaryB = 1.0F;
    protected long lastMinuteStamp = -1L;
    protected int bulbBurnMinutes = -1;
    protected int lastMin = 0;
    protected int nextBreakUpdate = 60;

    @Override
    public String getObjectName() {
        return "LightSwitch";
    }

    public IsoLightSwitch(IsoCell cell) {
        super(cell);
    }

    public IsoLightSwitch(IsoCell cell, IsoGridSquare sq, IsoSprite gid, int _RoomID) {
        super(cell, sq, gid);
        this.RoomID = _RoomID;
        if (gid != null && gid.getProperties().Is("lightR")) {
            if (gid.getProperties().Is("IsMoveAble")) {
                this.canBeModified = true;
            }

            this.primaryR = Float.parseFloat(gid.getProperties().Val("lightR")) / 255.0F;
            this.primaryG = Float.parseFloat(gid.getProperties().Val("lightG")) / 255.0F;
            this.primaryB = Float.parseFloat(gid.getProperties().Val("lightB")) / 255.0F;
        } else {
            this.lightRoom = true;
        }

        this.bStreetLight = gid != null && gid.getProperties().Is("streetlight");
        IsoRoom room = this.square.getRoom();
        if (room != null && this.lightRoom) {
            if (!sq.haveElectricity() && !IsoWorld.instance.isHydroPowerOn()) {
                room.def.bLightsActive = false;
            }

            this.Activated = room.def.bLightsActive;
            room.lightSwitches.add(this);
        } else {
            this.Activated = true;
        }
    }

    public void addLightSourceFromSprite() {
        if (this.sprite != null && this.sprite.getProperties().Is("lightR")) {
            float float0 = Float.parseFloat(this.sprite.getProperties().Val("lightR")) / 255.0F;
            float float1 = Float.parseFloat(this.sprite.getProperties().Val("lightG")) / 255.0F;
            float float2 = Float.parseFloat(this.sprite.getProperties().Val("lightB")) / 255.0F;
            this.Activated = false;
            this.setActive(true, true);
            int int0 = 10;
            if (this.sprite.getProperties().Is("LightRadius") && Integer.parseInt(this.sprite.getProperties().Val("LightRadius")) > 0) {
                int0 = Integer.parseInt(this.sprite.getProperties().Val("LightRadius"));
            }

            IsoLightSource lightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), float0, float1, float2, int0);
            lightSource.bActive = this.Activated;
            lightSource.bHydroPowered = true;
            lightSource.switches.add(this);
            this.lights.add(lightSource);
        }
    }

    public boolean getCanBeModified() {
        return this.canBeModified;
    }

    public float getPower() {
        return this.power;
    }

    public void setPower(float _power) {
        this.power = _power;
    }

    public void setDelta(float _delta) {
        this.delta = _delta;
    }

    public float getDelta() {
        return this.delta;
    }

    public void setUseBattery(boolean b) {
        this.setActive(false);
        this.useBattery = b;
        if (GameClient.bClient) {
            this.syncCustomizedSettings(null);
        }
    }

    public boolean getUseBattery() {
        return this.useBattery;
    }

    public boolean getHasBattery() {
        return this.hasBattery;
    }

    public void setHasBatteryRaw(boolean b) {
        this.hasBattery = b;
    }

    public void addBattery(IsoGameCharacter chr, InventoryItem battery) {
        if (this.canBeModified && this.useBattery && !this.hasBattery && battery != null && battery.getFullType().equals("Base.Battery")) {
            this.power = ((DrainableComboItem)battery).getUsedDelta();
            this.hasBattery = true;
            chr.removeFromHands(battery);
            chr.getInventory().Remove(battery);
            if (GameClient.bClient) {
                this.syncCustomizedSettings(null);
            }
        }
    }

    public DrainableComboItem removeBattery(IsoGameCharacter chr) {
        if (this.canBeModified && this.useBattery && this.hasBattery) {
            DrainableComboItem drainableComboItem = (DrainableComboItem)InventoryItemFactory.CreateItem("Base.Battery");
            if (drainableComboItem != null) {
                this.hasBattery = false;
                drainableComboItem.setUsedDelta(this.power >= 0.0F ? this.power : 0.0F);
                this.power = 0.0F;
                this.setActive(false, false, true);
                chr.getInventory().AddItem(drainableComboItem);
                if (GameClient.bClient) {
                    this.syncCustomizedSettings(null);
                }

                return drainableComboItem;
            }
        }

        return null;
    }

    public boolean hasLightBulb() {
        return this.bulbItem != null;
    }

    public String getBulbItem() {
        return this.bulbItem;
    }

    public void setBulbItemRaw(String item) {
        this.bulbItem = item;
    }

    public void addLightBulb(IsoGameCharacter chr, InventoryItem bulb) {
        if (!this.hasLightBulb() && bulb != null && bulb.getType().startsWith("LightBulb")) {
            IsoLightSource lightSource = this.getPrimaryLight();
            if (lightSource != null) {
                this.setPrimaryR(bulb.getColorRed());
                this.setPrimaryG(bulb.getColorGreen());
                this.setPrimaryB(bulb.getColorBlue());
                this.bulbItem = bulb.getFullType();
                chr.removeFromHands(bulb);
                chr.getInventory().Remove(bulb);
                if (GameClient.bClient) {
                    this.syncCustomizedSettings(null);
                }
            }
        }
    }

    public InventoryItem removeLightBulb(IsoGameCharacter chr) {
        IsoLightSource lightSource = this.getPrimaryLight();
        if (lightSource != null && this.hasLightBulb()) {
            InventoryItem item = InventoryItemFactory.CreateItem(this.bulbItem);
            if (item != null) {
                item.setColorRed(this.getPrimaryR());
                item.setColorGreen(this.getPrimaryG());
                item.setColorBlue(this.getPrimaryB());
                item.setColor(new Color(lightSource.r, lightSource.g, lightSource.b));
                this.bulbItem = null;
                chr.getInventory().AddItem(item);
                this.setActive(false, false, true);
                if (GameClient.bClient) {
                    this.syncCustomizedSettings(null);
                }

                return item;
            }
        }

        return null;
    }

    private IsoLightSource getPrimaryLight() {
        return this.lights.size() > 0 ? this.lights.get(0) : null;
    }

    public float getPrimaryR() {
        return this.getPrimaryLight() != null ? this.getPrimaryLight().r : this.primaryR;
    }

    public float getPrimaryG() {
        return this.getPrimaryLight() != null ? this.getPrimaryLight().g : this.primaryG;
    }

    public float getPrimaryB() {
        return this.getPrimaryLight() != null ? this.getPrimaryLight().b : this.primaryB;
    }

    public void setPrimaryR(float r) {
        this.primaryR = r;
        if (this.getPrimaryLight() != null) {
            this.getPrimaryLight().r = r;
        }
    }

    public void setPrimaryG(float g) {
        this.primaryG = g;
        if (this.getPrimaryLight() != null) {
            this.getPrimaryLight().g = g;
        }
    }

    public void setPrimaryB(float b) {
        this.primaryB = b;
        if (this.getPrimaryLight() != null) {
            this.getPrimaryLight().b = b;
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.lightRoom = input.get() == 1;
        this.RoomID = input.getInt();
        this.Activated = input.get() == 1;
        if (WorldVersion >= 76) {
            this.canBeModified = input.get() == 1;
            if (this.canBeModified) {
                this.useBattery = input.get() == 1;
                this.hasBattery = input.get() == 1;
                if (input.get() == 1) {
                    this.bulbItem = GameWindow.ReadString(input);
                } else {
                    this.bulbItem = null;
                }

                this.power = input.getFloat();
                this.delta = input.getFloat();
                this.setPrimaryR(input.getFloat());
                this.setPrimaryG(input.getFloat());
                this.setPrimaryB(input.getFloat());
            }
        }

        if (WorldVersion >= 79) {
            this.lastMinuteStamp = input.getLong();
            this.bulbBurnMinutes = input.getInt();
        }

        this.bStreetLight = this.sprite != null && this.sprite.getProperties().Is("streetlight");
        if (this.square != null) {
            IsoRoom room = this.square.getRoom();
            if (room != null && this.lightRoom) {
                this.Activated = room.def.bLightsActive;
                room.lightSwitches.add(this);
            } else {
                float float0 = 0.9F;
                float float1 = 0.8F;
                float float2 = 0.7F;
                if (this.sprite != null && this.sprite.getProperties().Is("lightR")) {
                    if (WorldVersion >= 76 && this.canBeModified) {
                        float0 = this.primaryR;
                        float1 = this.primaryG;
                        float2 = this.primaryB;
                    } else {
                        float0 = Float.parseFloat(this.sprite.getProperties().Val("lightR")) / 255.0F;
                        float1 = Float.parseFloat(this.sprite.getProperties().Val("lightG")) / 255.0F;
                        float2 = Float.parseFloat(this.sprite.getProperties().Val("lightB")) / 255.0F;
                        this.primaryR = float0;
                        this.primaryG = float1;
                        this.primaryB = float2;
                    }
                }

                int int0 = 8;
                if (this.sprite.getProperties().Is("LightRadius") && Integer.parseInt(this.sprite.getProperties().Val("LightRadius")) > 0) {
                    int0 = Integer.parseInt(this.sprite.getProperties().Val("LightRadius"));
                }

                IsoLightSource lightSource = new IsoLightSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), float0, float1, float2, int0);
                lightSource.bActive = this.Activated;
                lightSource.bWasActive = lightSource.bActive;
                lightSource.bHydroPowered = true;
                lightSource.switches.add(this);
                this.lights.add(lightSource);
            }

            if (SystemDisabler.doObjectStateSyncEnable && GameClient.bClient) {
                GameClient.instance.objectSyncReq.putRequestLoad(this.square);
            }
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)(this.lightRoom ? 1 : 0));
        output.putInt(this.RoomID);
        output.put((byte)(this.Activated ? 1 : 0));
        output.put((byte)(this.canBeModified ? 1 : 0));
        if (this.canBeModified) {
            output.put((byte)(this.useBattery ? 1 : 0));
            output.put((byte)(this.hasBattery ? 1 : 0));
            output.put((byte)(this.hasLightBulb() ? 1 : 0));
            if (this.hasLightBulb()) {
                GameWindow.WriteString(output, this.bulbItem);
            }

            output.putFloat(this.power);
            output.putFloat(this.delta);
            output.putFloat(this.getPrimaryR());
            output.putFloat(this.getPrimaryG());
            output.putFloat(this.getPrimaryB());
        }

        output.putLong(this.lastMinuteStamp);
        output.putInt(this.bulbBurnMinutes);
    }

    @Override
    public boolean onMouseLeftClick(int x, int y) {
        return false;
    }

    public boolean canSwitchLight() {
        if (this.bulbItem != null) {
            boolean boolean0 = IsoWorld.instance.isHydroPowerOn();
            boolean boolean1 = boolean0 ? this.square.getRoom() != null || this.bStreetLight : this.square.haveElectricity();
            if (!boolean1 && this.getCell() != null) {
                for (int int0 = 0; int0 >= (this.getZ() >= 1.0F ? -1 : 0); int0--) {
                    for (int int1 = -1; int1 < 2; int1++) {
                        for (int int2 = -1; int2 < 2; int2++) {
                            if (int1 != 0 || int2 != 0 || int0 != 0) {
                                IsoGridSquare square = this.getCell()
                                    .getGridSquare((double)(this.getX() + int1), (double)(this.getY() + int2), (double)(this.getZ() + int0));
                                if (square != null && (boolean0 && square.getRoom() != null || square.haveElectricity())) {
                                    boolean1 = true;
                                    break;
                                }
                            }
                        }

                        if (boolean1) {
                            break;
                        }
                    }
                }
            }

            if (!this.useBattery && boolean1 || this.canBeModified && this.useBattery && this.hasBattery && this.power > 0.0F) {
                return true;
            }
        }

        return false;
    }

    public boolean setActive(boolean active) {
        return this.setActive(active, false, false);
    }

    public boolean setActive(boolean active, boolean setActiveBoolOnly) {
        return this.setActive(active, setActiveBoolOnly, false);
    }

    public boolean setActive(boolean active, boolean setActiveBoolOnly, boolean ignoreSwitchCheck) {
        if (this.bulbItem == null) {
            active = false;
        }

        if (active == this.Activated) {
            return this.Activated;
        } else if (this.square.getRoom() == null && !this.canBeModified) {
            return this.Activated;
        } else {
            if (ignoreSwitchCheck || this.canSwitchLight()) {
                this.Activated = active;
                if (!setActiveBoolOnly) {
                    IsoWorld.instance.getFreeEmitter().playSound("LightSwitch", this.square);
                    this.switchLight(this.Activated);
                    this.syncIsoObject(false, (byte)(this.Activated ? 1 : 0), null);
                }
            }

            return this.Activated;
        }
    }

    public boolean toggle() {
        return this.setActive(!this.Activated);
    }

    public void switchLight(boolean _Activated) {
        if (this.lightRoom && this.square.getRoom() != null) {
            this.square.getRoom().def.bLightsActive = _Activated;

            for (int int0 = 0; int0 < this.square.getRoom().lightSwitches.size(); int0++) {
                this.square.getRoom().lightSwitches.get(int0).Activated = _Activated;
            }

            if (GameServer.bServer) {
                int int1 = this.square.getX() / 300;
                int int2 = this.square.getY() / 300;
                int int3 = this.square.getRoom().def.ID;
                GameServer.sendMetaGrid(int1, int2, int3);
            }
        }

        for (int int4 = 0; int4 < this.lights.size(); int4++) {
            IsoLightSource lightSource = this.lights.get(int4);
            lightSource.bActive = _Activated;
        }

        IsoGridSquare.RecalcLightTime = -1;
        GameTime.instance.lightSourceUpdate = 100.0F;
        IsoGenerator.updateGenerator(this.getSquare());
    }

    public void getCustomSettingsFromItem(InventoryItem item) {
        if (item instanceof Moveable moveable && moveable.isLight()) {
            this.useBattery = moveable.isLightUseBattery();
            this.hasBattery = moveable.isLightHasBattery();
            this.bulbItem = moveable.getLightBulbItem();
            this.power = moveable.getLightPower();
            this.delta = moveable.getLightDelta();
            this.setPrimaryR(moveable.getLightR());
            this.setPrimaryG(moveable.getLightG());
            this.setPrimaryB(moveable.getLightB());
        }
    }

    public void setCustomSettingsToItem(InventoryItem item) {
        if (item instanceof Moveable moveable) {
            moveable.setLightUseBattery(this.useBattery);
            moveable.setLightHasBattery(this.hasBattery);
            moveable.setLightBulbItem(this.bulbItem);
            moveable.setLightPower(this.power);
            moveable.setLightDelta(this.delta);
            moveable.setLightR(this.primaryR);
            moveable.setLightG(this.primaryG);
            moveable.setLightB(this.primaryB);
        }
    }

    public void syncCustomizedSettings(UdpConnection source) {
        if (GameClient.bClient) {
            this.writeCustomizedSettingsPacket(GameClient.connection);
        } else if (GameServer.bServer) {
            for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
                if (source == null || udpConnection.getConnectedGUID() != source.getConnectedGUID()) {
                    this.writeCustomizedSettingsPacket(udpConnection);
                }
            }
        }
    }

    private void writeCustomizedSettingsPacket(UdpConnection udpConnection) {
        if (udpConnection != null) {
            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
            PacketTypes.PacketType.SyncCustomLightSettings.doPacket(byteBufferWriter);
            this.writeLightSwitchObjectHeader(byteBufferWriter, (byte)(this.Activated ? 1 : 0));
            byteBufferWriter.putBoolean(this.canBeModified);
            byteBufferWriter.putBoolean(this.useBattery);
            byteBufferWriter.putBoolean(this.hasBattery);
            byteBufferWriter.putByte((byte)(this.bulbItem != null ? 1 : 0));
            if (this.bulbItem != null) {
                GameWindow.WriteString(byteBufferWriter.bb, this.bulbItem);
            }

            byteBufferWriter.putFloat(this.power);
            byteBufferWriter.putFloat(this.delta);
            byteBufferWriter.putFloat(this.primaryR);
            byteBufferWriter.putFloat(this.primaryG);
            byteBufferWriter.putFloat(this.primaryB);
            PacketTypes.PacketType.SyncCustomLightSettings.send(udpConnection);
        }
    }

    private void readCustomizedSettingsPacket(ByteBuffer byteBuffer) {
        this.Activated = byteBuffer.get() == 1;
        this.canBeModified = byteBuffer.get() == 1;
        this.useBattery = byteBuffer.get() == 1;
        this.hasBattery = byteBuffer.get() == 1;
        if (byteBuffer.get() == 1) {
            this.bulbItem = GameWindow.ReadString(byteBuffer);
        } else {
            this.bulbItem = null;
        }

        this.power = byteBuffer.getFloat();
        this.delta = byteBuffer.getFloat();
        this.setPrimaryR(byteBuffer.getFloat());
        this.setPrimaryG(byteBuffer.getFloat());
        this.setPrimaryB(byteBuffer.getFloat());
    }

    public void receiveSyncCustomizedSettings(ByteBuffer bb, UdpConnection connection) {
        if (GameClient.bClient) {
            this.readCustomizedSettingsPacket(bb);
        } else if (GameServer.bServer) {
            this.readCustomizedSettingsPacket(bb);
            this.syncCustomizedSettings(connection);
        }

        this.switchLight(this.Activated);
    }

    private void writeLightSwitchObjectHeader(ByteBufferWriter byteBufferWriter, byte byte0) {
        byteBufferWriter.putInt(this.square.getX());
        byteBufferWriter.putInt(this.square.getY());
        byteBufferWriter.putInt(this.square.getZ());
        byteBufferWriter.putByte((byte)this.square.getObjects().indexOf(this));
        byteBufferWriter.putByte(byte0);
    }

    @Override
    public void syncIsoObjectSend(ByteBufferWriter b) {
        b.putInt(this.square.getX());
        b.putInt(this.square.getY());
        b.putInt(this.square.getZ());
        byte byte0 = (byte)this.square.getObjects().indexOf(this);
        b.putByte(byte0);
        b.putByte((byte)1);
        b.putByte((byte)(this.Activated ? 1 : 0));
    }

    @Override
    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source, ByteBuffer bb) {
        this.syncIsoObject(bRemote, val, source);
    }

    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source) {
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
                if (val == 1) {
                    this.switchLight(true);
                    this.Activated = true;
                } else {
                    this.switchLight(false);
                    this.Activated = false;
                }

                if (GameServer.bServer) {
                    for (UdpConnection udpConnection : GameServer.udpEngine.connections) {
                        if (source != null) {
                            if (udpConnection.getConnectedGUID() != source.getConnectedGUID()) {
                                ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
                                PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter1);
                                this.syncIsoObjectSend(byteBufferWriter1);
                                PacketTypes.PacketType.SyncIsoObject.send(udpConnection);
                            }
                        } else if (udpConnection.RelevantTo(this.square.x, this.square.y)) {
                            ByteBufferWriter byteBufferWriter2 = udpConnection.startPacket();
                            PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter2);
                            byteBufferWriter2.putInt(this.square.getX());
                            byteBufferWriter2.putInt(this.square.getY());
                            byteBufferWriter2.putInt(this.square.getZ());
                            byte byte0 = (byte)this.square.getObjects().indexOf(this);
                            if (byte0 != -1) {
                                byteBufferWriter2.putByte(byte0);
                            } else {
                                byteBufferWriter2.putByte((byte)this.square.getObjects().size());
                            }

                            byteBufferWriter2.putByte((byte)1);
                            byteBufferWriter2.putByte((byte)(this.Activated ? 1 : 0));
                            PacketTypes.PacketType.SyncIsoObject.send(udpConnection);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void update() {
        if (!GameServer.bServer && !GameClient.bClient || GameServer.bServer) {
            boolean boolean0 = false;
            if (!this.Activated) {
                this.lastMinuteStamp = -1L;
            }

            if (!this.lightRoom && this.canBeModified && this.Activated) {
                if (this.lastMinuteStamp == -1L) {
                    this.lastMinuteStamp = GameTime.instance.getMinutesStamp();
                }

                if (GameTime.instance.getMinutesStamp() > this.lastMinuteStamp) {
                    if (this.bulbBurnMinutes == -1) {
                        int int0 = SandboxOptions.instance.getElecShutModifier() * 24 * 60;
                        if (this.lastMinuteStamp < int0) {
                            this.bulbBurnMinutes = (int)this.lastMinuteStamp;
                        } else {
                            this.bulbBurnMinutes = int0;
                        }
                    }

                    long long0 = GameTime.instance.getMinutesStamp() - this.lastMinuteStamp;
                    this.lastMinuteStamp = GameTime.instance.getMinutesStamp();
                    boolean boolean1 = false;
                    boolean boolean2 = IsoWorld.instance.isHydroPowerOn();
                    boolean boolean3 = boolean2 ? this.square.getRoom() != null : this.square.haveElectricity();
                    boolean boolean4 = this.useBattery && this.hasBattery && this.power > 0.0F;
                    if (boolean4 || !this.useBattery && boolean3) {
                        boolean1 = true;
                    }

                    double double0 = SandboxOptions.instance.LightBulbLifespan.getValue();
                    if (double0 <= 0.0) {
                        boolean1 = false;
                    }

                    if (this.Activated && this.hasLightBulb() && boolean1) {
                        this.bulbBurnMinutes = (int)(this.bulbBurnMinutes + long0);
                    }

                    this.nextBreakUpdate = (int)(this.nextBreakUpdate - long0);
                    if (this.nextBreakUpdate <= 0) {
                        if (this.Activated && this.hasLightBulb() && boolean1) {
                            int int1 = (int)(1000.0 * double0);
                            if (int1 < 1) {
                                int1 = 1;
                            }

                            int int2 = Rand.Next(0, int1);
                            int int3 = this.bulbBurnMinutes / 10000;
                            if (int2 < int3) {
                                this.bulbBurnMinutes = 0;
                                this.setActive(false, true, true);
                                this.bulbItem = null;
                                IsoWorld.instance.getFreeEmitter().playSound("LightbulbBurnedOut", this.square);
                                boolean0 = true;
                                if (Core.bDebug) {
                                    System.out.println("broke bulb at x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ());
                                }
                            }
                        }

                        this.nextBreakUpdate = 60;
                    }

                    if (this.Activated && boolean4 && this.hasLightBulb()) {
                        float float0 = this.power - this.power % 0.01F;
                        this.power = this.power - this.delta * (float)long0;
                        if (this.power < 0.0F) {
                            this.power = 0.0F;
                        }

                        if (long0 == 1L || this.power < float0) {
                            boolean0 = true;
                        }
                    }
                }

                if (this.useBattery && this.Activated && (this.power <= 0.0F || !this.hasBattery)) {
                    this.power = 0.0F;
                    this.setActive(false, true, true);
                    boolean0 = true;
                }
            }

            if (this.Activated && !this.hasLightBulb()) {
                this.setActive(false, true, true);
                boolean0 = true;
            }

            if (boolean0 && GameServer.bServer) {
                this.syncCustomizedSettings(null);
            }
        }
    }

    public boolean isActivated() {
        return this.Activated;
    }

    @Override
    public void addToWorld() {
        if (!this.Activated) {
            this.lastMinuteStamp = -1L;
        }

        if (!this.lightRoom && !this.lights.isEmpty()) {
            for (int int0 = 0; int0 < this.lights.size(); int0++) {
                IsoWorld.instance.CurrentCell.getLamppostPositions().add(this.lights.get(int0));
            }
        }

        if (this.getCell() != null && this.canBeModified && !this.lightRoom && (!GameServer.bServer && !GameClient.bClient || GameServer.bServer)) {
            this.getCell().addToStaticUpdaterObjectList(this);
        }

        this.checkAmbientSound();
    }

    @Override
    public void removeFromWorld() {
        if (!this.lightRoom && !this.lights.isEmpty()) {
            for (int int0 = 0; int0 < this.lights.size(); int0++) {
                this.lights.get(int0).setActive(false);
                IsoWorld.instance.CurrentCell.removeLamppost(this.lights.get(int0));
            }

            this.lights.clear();
        }

        if (this.square != null && this.lightRoom) {
            IsoRoom room = this.square.getRoom();
            if (room != null) {
                room.lightSwitches.remove(this);
            }
        }

        super.removeFromWorld();
    }

    public static void chunkLoaded(IsoChunk chunk) {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < 10; int0++) {
            for (int int1 = 0; int1 < 10; int1++) {
                for (int int2 = 0; int2 < 8; int2++) {
                    IsoGridSquare square = chunk.getGridSquare(int0, int1, int2);
                    if (square != null) {
                        IsoRoom room0 = square.getRoom();
                        if (room0 != null && room0.hasLightSwitches() && !arrayList.contains(room0)) {
                            arrayList.add(room0);
                        }
                    }
                }
            }
        }

        for (int int3 = 0; int3 < arrayList.size(); int3++) {
            IsoRoom room1 = (IsoRoom)arrayList.get(int3);
            room1.createLights(room1.def.bLightsActive);

            for (int int4 = 0; int4 < room1.roomLights.size(); int4++) {
                IsoRoomLight roomLight = room1.roomLights.get(int4);
                if (!chunk.roomLights.contains(roomLight)) {
                    chunk.roomLights.add(roomLight);
                }
            }
        }
    }

    public ArrayList<IsoLightSource> getLights() {
        return this.lights;
    }
}
