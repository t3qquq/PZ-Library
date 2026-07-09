// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.DrainableComboItem;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class IsoBarbecue extends IsoObject {
    boolean bHasPropaneTank = false;
    int FuelAmount = 0;
    boolean bLit = false;
    boolean bIsSmouldering = false;
    protected float LastUpdateTime = -1.0F;
    protected float MinuteAccumulator = 0.0F;
    protected int MinutesSinceExtinguished = -1;
    IsoSprite normalSprite = null;
    IsoSprite noTankSprite = null;
    private IsoHeatSource heatSource;
    private long soundInstance = 0L;
    private static int SMOULDER_MINUTES = 10;

    public IsoBarbecue(IsoCell cell) {
        super(cell);
    }

    public IsoBarbecue(IsoCell cell, IsoGridSquare sq, IsoSprite gid) {
        super(cell, sq, gid);
        this.container = new ItemContainer("barbecue", sq, this);
        this.container.setExplored(true);
        if (isSpriteWithPropaneTank(this.sprite)) {
            this.bHasPropaneTank = true;
            this.FuelAmount = 1200;
            byte byte0 = 8;
            this.normalSprite = this.sprite;
            this.noTankSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.sprite, byte0);
        } else if (isSpriteWithoutPropaneTank(this.sprite)) {
            byte byte1 = -8;
            this.normalSprite = IsoSprite.getSprite(IsoSpriteManager.instance, this.sprite, byte1);
            this.noTankSprite = this.sprite;
        }
    }

    @Override
    public String getObjectName() {
        return "Barbecue";
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.bHasPropaneTank = input.get() == 1;
        this.FuelAmount = input.getInt();
        this.bLit = input.get() == 1;
        this.LastUpdateTime = input.getFloat();
        this.MinutesSinceExtinguished = input.getInt();
        if (input.get() == 1) {
            this.normalSprite = IsoSprite.getSprite(IsoSpriteManager.instance, input.getInt());
        }

        if (input.get() == 1) {
            this.noTankSprite = IsoSprite.getSprite(IsoSpriteManager.instance, input.getInt());
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.put((byte)(this.bHasPropaneTank ? 1 : 0));
        output.putInt(this.FuelAmount);
        output.put((byte)(this.bLit ? 1 : 0));
        output.putFloat(this.LastUpdateTime);
        output.putInt(this.MinutesSinceExtinguished);
        if (this.normalSprite != null) {
            output.put((byte)1);
            output.putInt(this.normalSprite.ID);
        } else {
            output.put((byte)0);
        }

        if (this.noTankSprite != null) {
            output.put((byte)1);
            output.putInt(this.noTankSprite.ID);
        } else {
            output.put((byte)0);
        }
    }

    public void setFuelAmount(int units) {
        units = Math.max(0, units);
        int int0 = this.getFuelAmount();
        if (units != int0) {
            this.FuelAmount = units;
        }
    }

    public int getFuelAmount() {
        return this.FuelAmount;
    }

    public void addFuel(int units) {
        this.setFuelAmount(this.getFuelAmount() + units);
    }

    public int useFuel(int amount) {
        int int0 = this.getFuelAmount();
        int int1 = 0;
        if (int0 >= amount) {
            int1 = amount;
        } else {
            int1 = int0;
        }

        this.setFuelAmount(int0 - int1);
        return int1;
    }

    public boolean hasFuel() {
        return this.getFuelAmount() > 0;
    }

    public boolean hasPropaneTank() {
        return this.isPropaneBBQ() && this.bHasPropaneTank;
    }

    public boolean isPropaneBBQ() {
        return this.getSprite() != null && this.getProperties().Is("propaneTank");
    }

    public static boolean isSpriteWithPropaneTank(IsoSprite sprite) {
        if (sprite != null && sprite.getProperties().Is("propaneTank")) {
            byte byte0 = 8;
            IsoSprite _sprite = IsoSprite.getSprite(IsoSpriteManager.instance, sprite, byte0);
            return _sprite != null && _sprite.getProperties().Is("propaneTank");
        } else {
            return false;
        }
    }

    public static boolean isSpriteWithoutPropaneTank(IsoSprite sprite) {
        if (sprite != null && sprite.getProperties().Is("propaneTank")) {
            byte byte0 = -8;
            IsoSprite _sprite = IsoSprite.getSprite(IsoSpriteManager.instance, sprite, byte0);
            return _sprite != null && _sprite.getProperties().Is("propaneTank");
        } else {
            return false;
        }
    }

    public void setPropaneTank(InventoryItem tank) {
        if (tank.getFullType().equals("Base.PropaneTank")) {
            this.bHasPropaneTank = true;
            this.FuelAmount = 1200;
            if (tank instanceof DrainableComboItem) {
                this.FuelAmount = (int)(this.FuelAmount * ((DrainableComboItem)tank).getUsedDelta());
            }
        }
    }

    public InventoryItem removePropaneTank() {
        if (!this.bHasPropaneTank) {
            return null;
        } else {
            this.bHasPropaneTank = false;
            this.bLit = false;
            InventoryItem item = InventoryItemFactory.CreateItem("Base.PropaneTank");
            if (item instanceof DrainableComboItem) {
                ((DrainableComboItem)item).setUsedDelta(this.getFuelAmount() / 1200.0F);
            }

            this.FuelAmount = 0;
            return item;
        }
    }

    public void setLit(boolean lit) {
        this.bLit = lit;
    }

    public boolean isLit() {
        return this.bLit;
    }

    public boolean isSmouldering() {
        return this.bIsSmouldering;
    }

    public void turnOn() {
        if (!this.isLit()) {
            this.setLit(true);
        }
    }

    public void turnOff() {
        if (this.isLit()) {
            this.setLit(false);
        }
    }

    public void toggle() {
        this.setLit(!this.isLit());
    }

    public void extinguish() {
        if (this.isLit()) {
            this.setLit(false);
            if (this.hasFuel() && !this.isPropaneBBQ()) {
                this.MinutesSinceExtinguished = 0;
            }
        }
    }

    public float getTemperature() {
        return this.isLit() ? 1.8F : 1.0F;
    }

    private void updateSprite() {
        if (this.isPropaneBBQ()) {
            if (this.hasPropaneTank()) {
                this.sprite = this.normalSprite;
            } else {
                this.sprite = this.noTankSprite;
            }
        }
    }

    private void updateHeatSource() {
        if (this.isLit()) {
            if (this.heatSource == null) {
                this.heatSource = new IsoHeatSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), 3, 25);
                IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
            }
        } else if (this.heatSource != null) {
            IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
            this.heatSource = null;
        }
    }

    private void updateSound() {
        if (!GameServer.bServer) {
            if (this.isLit()) {
                if (this.emitter == null) {
                    this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, (int)this.getZ());
                    IsoWorld.instance.setEmitterOwner(this.emitter, this);
                }

                String string = this.isPropaneBBQ() ? "BBQPropaneRunning" : "BBQRegularRunning";
                if (!this.emitter.isPlaying(string)) {
                    this.soundInstance = this.emitter.playSoundLoopedImpl(string);
                }
            } else if (this.emitter != null && this.soundInstance != 0L) {
                this.emitter.stopOrTriggerSound(this.soundInstance);
                this.emitter = null;
                this.soundInstance = 0L;
            }
        }
    }

    @Override
    public void update() {
        if (!GameClient.bClient) {
            boolean boolean0 = this.hasFuel();
            boolean boolean1 = this.isLit();
            float float0 = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.LastUpdateTime < 0.0F) {
                this.LastUpdateTime = float0;
            } else if (this.LastUpdateTime > float0) {
                this.LastUpdateTime = float0;
            }

            if (float0 > this.LastUpdateTime) {
                this.MinuteAccumulator = this.MinuteAccumulator + (float0 - this.LastUpdateTime) * 60.0F;
                int int0 = (int)Math.floor(this.MinuteAccumulator);
                if (int0 > 0) {
                    if (this.isLit()) {
                        DebugLog.log(DebugType.Fireplace, "IsoBarbecue burned " + int0 + " minutes (" + this.getFuelAmount() + " remaining)");
                        this.useFuel(int0);
                        if (!this.hasFuel()) {
                            this.extinguish();
                        }
                    } else if (this.MinutesSinceExtinguished != -1) {
                        int int1 = Math.min(int0, SMOULDER_MINUTES - this.MinutesSinceExtinguished);
                        DebugLog.log(DebugType.Fireplace, "IsoBarbecue smoldered " + int1 + " minutes (" + this.getFuelAmount() + " remaining)");
                        this.MinutesSinceExtinguished += int0;
                        this.bIsSmouldering = true;
                        this.useFuel(int1);
                        if (!this.hasFuel() || this.MinutesSinceExtinguished >= SMOULDER_MINUTES) {
                            this.MinutesSinceExtinguished = -1;
                            this.bIsSmouldering = false;
                        }
                    }

                    this.MinuteAccumulator -= int0;
                }
            }

            this.LastUpdateTime = float0;
            if (GameServer.bServer) {
                if (boolean0 != this.hasFuel() || boolean1 != this.isLit()) {
                    this.sendObjectChange("state");
                }

                return;
            }
        }

        this.updateSprite();
        this.updateHeatSource();
        if (!this.isLit() || this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
            if (!this.isLit() && this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
                this.RemoveAttachedAnims();
            }
        } else {
            ColorInfo colorInfo = new ColorInfo(0.95F, 0.95F, 0.85F, 1.0F);
            this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -14, 58, true, 0, false, 0.7F, colorInfo);
            this.AttachedAnimSprite.get(0).alpha = this.AttachedAnimSprite.get(0).targetAlpha = 0.55F;
            this.AttachedAnimSprite.get(0).bCopyTargetAlpha = false;
        }

        if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
            int int2 = this.AttachedAnimSprite.size();

            for (int int3 = 0; int3 < int2; int3++) {
                IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int3);
                IsoSprite sprite = spriteInstance.parentSprite;
                spriteInstance.update();
                float float1 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
                spriteInstance.Frame = spriteInstance.Frame + spriteInstance.AnimFrameIncrease * float1;
                if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
                    spriteInstance.Frame = 0.0F;
                }
            }
        }

        this.updateSound();
    }

    /**
     * 
     * @param newsprite the sprite to set
     */
    @Override
    public void setSprite(IsoSprite newsprite) {
        if (isSpriteWithPropaneTank(newsprite)) {
            byte byte0 = 8;
            this.normalSprite = newsprite;
            this.noTankSprite = IsoSprite.getSprite(IsoSpriteManager.instance, newsprite, byte0);
        } else if (isSpriteWithoutPropaneTank(newsprite)) {
            byte byte1 = -8;
            this.normalSprite = IsoSprite.getSprite(IsoSpriteManager.instance, newsprite, byte1);
            this.noTankSprite = newsprite;
        }
    }

    @Override
    public void addToWorld() {
        IsoCell cell = this.getCell();
        this.getCell().addToProcessIsoObject(this);
        this.container.addItemsToProcessItems();
    }

    @Override
    public void removeFromWorld() {
        if (this.heatSource != null) {
            IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
            this.heatSource = null;
        }

        super.removeFromWorld();
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        if (this.AttachedAnimSprite != null) {
            int int0 = Core.TileScale;

            for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); int1++) {
                IsoSprite sprite = this.AttachedAnimSprite.get(int1).parentSprite;
                sprite.soffX = (short)(14 * int0);
                sprite.soffY = (short)(-58 * int0);
                this.AttachedAnimSprite.get(int1).setScale(int0, int0);
            }
        }

        super.render(x, y, z, col, bDoChild, bWallLightingPass, shader);
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("state".equals(change)) {
            bb.putInt(this.getFuelAmount());
            bb.put((byte)(this.isLit() ? 1 : 0));
            bb.put((byte)(this.hasPropaneTank() ? 1 : 0));
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("state".equals(change)) {
            this.setFuelAmount(bb.getInt());
            this.setLit(bb.get() == 1);
            this.bHasPropaneTank = bb.get() == 1;
        }
    }
}
