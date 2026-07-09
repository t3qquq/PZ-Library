// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.opengl.Shader;
import zombie.core.raknet.UdpConnection;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;

public class IsoCarBatteryCharger extends IsoObject {
    protected InventoryItem item;
    protected InventoryItem battery;
    protected boolean activated;
    protected float lastUpdate = -1.0F;
    protected float chargeRate = 0.16666667F;
    protected IsoSprite chargerSprite;
    protected IsoSprite batterySprite;
    protected long sound = 0L;

    public IsoCarBatteryCharger(IsoCell cell) {
        super(cell);
    }

    public IsoCarBatteryCharger(InventoryItem _item, IsoCell cell, IsoGridSquare square) {
        super(cell, square, (IsoSprite)null);
        if (_item == null) {
            throw new NullPointerException("item is null");
        } else {
            this.item = _item;
        }
    }

    @Override
    public String getObjectName() {
        return "IsoCarBatteryCharger";
    }

    @Override
    public void load(ByteBuffer bb, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(bb, WorldVersion, IS_DEBUG_SAVE);
        if (bb.get() == 1) {
            try {
                this.item = InventoryItem.loadItem(bb, WorldVersion);
            } catch (Exception exception0) {
                exception0.printStackTrace();
            }
        }

        if (bb.get() == 1) {
            try {
                this.battery = InventoryItem.loadItem(bb, WorldVersion);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        this.activated = bb.get() == 1;
        this.lastUpdate = bb.getFloat();
        this.chargeRate = bb.getFloat();
    }

    @Override
    public void save(ByteBuffer bb, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(bb, IS_DEBUG_SAVE);
        if (this.item == null) {
            assert false;

            bb.put((byte)0);
        } else {
            bb.put((byte)1);
            this.item.saveWithSize(bb, false);
        }

        if (this.battery == null) {
            bb.put((byte)0);
        } else {
            bb.put((byte)1);
            this.battery.saveWithSize(bb, false);
        }

        bb.put((byte)(this.activated ? 1 : 0));
        bb.putFloat(this.lastUpdate);
        bb.putFloat(this.chargeRate);
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.getCell().addToProcessIsoObject(this);
    }

    @Override
    public void removeFromWorld() {
        this.stopChargingSound();
        super.removeFromWorld();
    }

    @Override
    public void update() {
        super.update();
        if (!(this.battery instanceof DrainableComboItem)) {
            this.battery = null;
        }

        if (this.battery == null) {
            this.lastUpdate = -1.0F;
            this.activated = false;
            this.stopChargingSound();
        } else {
            boolean boolean0 = this.square != null && (this.square.haveElectricity() || IsoWorld.instance.isHydroPowerOn() && this.square.getRoom() != null);
            if (!boolean0) {
                this.activated = false;
            }

            if (!this.activated) {
                this.lastUpdate = -1.0F;
                this.stopChargingSound();
            } else {
                this.startChargingSound();
                DrainableComboItem drainableComboItem = (DrainableComboItem)this.battery;
                if (!(drainableComboItem.getUsedDelta() >= 1.0F)) {
                    float float0 = (float)GameTime.getInstance().getWorldAgeHours();
                    if (this.lastUpdate < 0.0F) {
                        this.lastUpdate = float0;
                    }

                    if (this.lastUpdate > float0) {
                        this.lastUpdate = float0;
                    }

                    float float1 = float0 - this.lastUpdate;
                    if (float1 > 0.0F) {
                        drainableComboItem.setUsedDelta(Math.min(1.0F, drainableComboItem.getUsedDelta() + this.chargeRate * float1));
                        this.lastUpdate = float0;
                    }
                }
            }
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        this.chargerSprite = this.configureSprite(this.item, this.chargerSprite);
        if (this.chargerSprite.CurrentAnim != null && !this.chargerSprite.CurrentAnim.Frames.isEmpty()) {
            Texture texture = this.chargerSprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
            if (texture != null) {
                float float0 = texture.getWidthOrig() * this.chargerSprite.def.getScaleX() / 2.0F;
                float float1 = texture.getHeightOrig() * this.chargerSprite.def.getScaleY() * 3.0F / 4.0F;
                this.offsetX = this.offsetY = 0.0F;
                this.setAlpha(IsoCamera.frameState.playerIndex, 1.0F);
                float float2 = 0.5F;
                float float3 = 0.5F;
                float float4 = 0.0F;
                this.sx = 0.0F;
                this.item.setWorldZRotation(315);
                if (!WorldItemModelDrawer.renderMain(this.getItem(), this.getSquare(), this.getX() + float2, this.getY() + float3, this.getZ() + float4, -1.0F)
                    )
                 {
                    this.chargerSprite
                        .render(
                            this,
                            x + float2,
                            y + float3,
                            z + float4,
                            this.dir,
                            this.offsetX + float0 + 8 * Core.TileScale,
                            this.offsetY + float1 + 4 * Core.TileScale,
                            col,
                            true
                        );
                }

                if (this.battery != null) {
                    this.batterySprite = this.configureSprite(this.battery, this.batterySprite);
                    if (this.batterySprite != null && this.batterySprite.CurrentAnim != null && !this.batterySprite.CurrentAnim.Frames.isEmpty()) {
                        this.sx = 0.0F;
                        this.getBattery().setWorldZRotation(90);
                        if (!WorldItemModelDrawer.renderMain(
                            this.getBattery(), this.getSquare(), this.getX() + 0.75F, this.getY() + 0.75F, this.getZ() + float4, -1.0F
                        )) {
                            this.batterySprite
                                .render(
                                    this,
                                    x + float2,
                                    y + float3,
                                    z + float4,
                                    this.dir,
                                    this.offsetX + float0 - 8.0F + Core.TileScale,
                                    this.offsetY + float1 - 4 * Core.TileScale,
                                    col,
                                    true
                                );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void renderObjectPicker(float x, float y, float z, ColorInfo lightInfo) {
    }

    private IsoSprite configureSprite(InventoryItem itemx, IsoSprite sprite) {
        String string = itemx.getWorldTexture();

        try {
            Texture texture0 = Texture.getSharedTexture(string);
            if (texture0 == null) {
                string = itemx.getTex().getName();
            }
        } catch (Exception exception) {
            string = "media/inventory/world/WItem_Sack.png";
        }

        Texture texture1 = Texture.getSharedTexture(string);
        boolean boolean0 = false;
        if (sprite == null) {
            sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        }

        if (sprite.CurrentAnim == null) {
            sprite.LoadFramesNoDirPageSimple(string);
            sprite.CurrentAnim.name = string;
            boolean0 = true;
        } else if (!string.equals(sprite.CurrentAnim.name)) {
            sprite.ReplaceCurrentAnimFrames(string);
            sprite.CurrentAnim.name = string;
            boolean0 = true;
        }

        if (boolean0) {
            if (itemx.getScriptItem() == null) {
                sprite.def.scaleAspect(texture1.getWidthOrig(), texture1.getHeightOrig(), 16 * Core.TileScale, 16 * Core.TileScale);
            } else if (this.battery != null && this.battery.getScriptItem() != null) {
                float float0 = Core.TileScale;
                float float1 = this.battery.getScriptItem().ScaleWorldIcon * (float0 / 2.0F);
                sprite.def.setScale(float1, float1);
            }
        }

        return sprite;
    }

    @Override
    public void syncIsoObjectSend(ByteBufferWriter b) {
        byte byte0 = (byte)this.getObjectIndex();
        b.putInt(this.square.getX());
        b.putInt(this.square.getY());
        b.putInt(this.square.getZ());
        b.putByte(byte0);
        b.putByte((byte)1);
        b.putByte((byte)0);
        if (this.battery == null) {
            b.putByte((byte)0);
        } else {
            b.putByte((byte)1);

            try {
                this.battery.saveWithSize(b.bb, false);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }

        b.putBoolean(this.activated);
        b.putFloat(this.chargeRate);
    }

    @Override
    public void syncIsoObject(boolean bRemote, byte val, UdpConnection source, ByteBuffer bb) {
        if (GameClient.bClient && !bRemote) {
            ByteBufferWriter byteBufferWriter0 = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter0);
            this.syncIsoObjectSend(byteBufferWriter0);
            PacketTypes.PacketType.SyncIsoObject.send(GameClient.connection);
        } else if (GameServer.bServer && !bRemote) {
            for (UdpConnection udpConnection0 : GameServer.udpEngine.connections) {
                ByteBufferWriter byteBufferWriter1 = udpConnection0.startPacket();
                PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter1);
                this.syncIsoObjectSend(byteBufferWriter1);
                PacketTypes.PacketType.SyncIsoObject.send(udpConnection0);
            }
        } else if (bRemote) {
            if (bb.get() == 1) {
                try {
                    this.battery = InventoryItem.loadItem(bb, 195);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            } else {
                this.battery = null;
            }

            this.activated = bb.get() == 1;
            this.chargeRate = bb.getFloat();
            if (GameServer.bServer) {
                for (UdpConnection udpConnection1 : GameServer.udpEngine.connections) {
                    if (source != null && udpConnection1 != source) {
                        ByteBufferWriter byteBufferWriter2 = udpConnection1.startPacket();
                        PacketTypes.PacketType.SyncIsoObject.doPacket(byteBufferWriter2);
                        this.syncIsoObjectSend(byteBufferWriter2);
                        PacketTypes.PacketType.SyncIsoObject.send(udpConnection1);
                    }
                }
            }
        }
    }

    public void sync() {
        this.syncIsoObject(false, (byte)0, null, null);
    }

    public InventoryItem getItem() {
        return this.item;
    }

    public InventoryItem getBattery() {
        return this.battery;
    }

    public void setBattery(InventoryItem _battery) {
        if (_battery != null) {
            if (!(_battery instanceof DrainableComboItem)) {
                throw new IllegalArgumentException("battery isn't DrainableComboItem");
            }

            if (this.battery != null) {
                throw new IllegalStateException("battery already inserted");
            }
        }

        this.battery = _battery;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(boolean _activated) {
        this.activated = _activated;
    }

    public float getChargeRate() {
        return this.chargeRate;
    }

    public void setChargeRate(float _chargeRate) {
        if (_chargeRate <= 0.0F) {
            throw new IllegalArgumentException("chargeRate <= 0.0f");
        } else {
            this.chargeRate = _chargeRate;
        }
    }

    private void startChargingSound() {
        if (!GameServer.bServer) {
            if (this.getObjectIndex() != -1) {
                if (this.sound != -1L) {
                    if (this.emitter == null) {
                        this.emitter = IsoWorld.instance.getFreeEmitter(this.square.x + 0.5F, this.square.y + 0.5F, this.square.z);
                        IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
                    }

                    if (!this.emitter.isPlaying(this.sound)) {
                        this.sound = this.emitter.playSound("CarBatteryChargerRunning");
                        if (this.sound == 0L) {
                            this.sound = -1L;
                        }
                    }

                    this.emitter.tick();
                }
            }
        }
    }

    private void stopChargingSound() {
        if (!GameServer.bServer) {
            if (this.emitter != null) {
                this.emitter.stopOrTriggerSound(this.sound);
                this.sound = 0L;
                IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
                this.emitter = null;
            }
        }
    }
}
