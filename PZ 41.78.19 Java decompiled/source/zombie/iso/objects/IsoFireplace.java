// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.core.Core;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.ItemContainer;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class IsoFireplace extends IsoObject {
    int FuelAmount = 0;
    boolean bLit = false;
    boolean bSmouldering = false;
    protected float LastUpdateTime = -1.0F;
    protected float MinuteAccumulator = 0.0F;
    protected int MinutesSinceExtinguished = -1;
    protected IsoSprite FuelSprite = null;
    protected int FuelSpriteIndex = -1;
    protected int FireSpriteIndex = -1;
    protected IsoLightSource LightSource = null;
    protected IsoHeatSource heatSource = null;
    private long soundInstance = 0L;
    private static int SMOULDER_MINUTES = 10;

    public IsoFireplace(IsoCell cell) {
        super(cell);
    }

    public IsoFireplace(IsoCell cell, IsoGridSquare sq, IsoSprite gid) {
        super(cell, sq, gid);
        String string = gid != null && gid.getProperties().Is(IsoFlagType.container) ? gid.getProperties().Val("container") : "fireplace";
        this.container = new ItemContainer(string, sq, this);
        this.container.setExplored(true);
    }

    @Override
    public String getObjectName() {
        return "Fireplace";
    }

    @Override
    public Vector2 getFacingPosition(Vector2 pos) {
        if (this.square == null) {
            return pos.set(0.0F, 0.0F);
        } else {
            return this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideN)
                ? pos.set(this.getX() + 0.5F, this.getY())
                : pos.set(this.getX(), this.getY() + 0.5F);
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.FuelAmount = input.getInt();
        this.bLit = input.get() == 1;
        this.LastUpdateTime = input.getFloat();
        this.MinutesSinceExtinguished = input.getInt();
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        super.save(output, IS_DEBUG_SAVE);
        output.putInt(this.FuelAmount);
        output.put((byte)(this.bLit ? 1 : 0));
        output.putFloat(this.LastUpdateTime);
        output.putInt(this.MinutesSinceExtinguished);
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

    public void setLit(boolean lit) {
        this.bLit = lit;
    }

    public boolean isLit() {
        return this.bLit;
    }

    public boolean isSmouldering() {
        return this.bSmouldering;
    }

    public void extinguish() {
        if (this.isLit()) {
            this.setLit(false);
            if (this.hasFuel()) {
                this.MinutesSinceExtinguished = 0;
            }
        }
    }

    public float getTemperature() {
        return this.isLit() ? 1.8F : 1.0F;
    }

    private void updateFuelSprite() {
        if (this.container == null || !"woodstove".equals(this.container.getType())) {
            if (this.hasFuel()) {
                if (this.FuelSprite == null) {
                    this.FuelSprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
                    Texture texture = this.FuelSprite.LoadFrameExplicit("Item_Logs");
                }

                if (this.FuelSpriteIndex == -1) {
                    DebugLog.log(DebugType.Fireplace, "fireplace: added fuel sprite");
                    this.FuelSpriteIndex = this.AttachedAnimSprite != null ? this.AttachedAnimSprite.size() : 0;
                    if (this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideW)) {
                        this.AttachExistingAnim(this.FuelSprite, -10 * Core.TileScale, -90 * Core.TileScale, false, 0, false, 0.0F);
                    } else {
                        this.AttachExistingAnim(this.FuelSprite, -35 * Core.TileScale, -90 * Core.TileScale, false, 0, false, 0.0F);
                    }

                    if (Core.TileScale == 1) {
                        this.AttachedAnimSprite.get(this.FuelSpriteIndex).setScale(0.5F, 0.5F);
                    }
                }
            } else if (this.FuelSpriteIndex != -1) {
                DebugLog.log(DebugType.Fireplace, "fireplace: removed fuel sprite");
                this.AttachedAnimSprite.remove(this.FuelSpriteIndex);
                if (this.FireSpriteIndex > this.FuelSpriteIndex) {
                    this.FireSpriteIndex--;
                }

                this.FuelSpriteIndex = -1;
            }
        }
    }

    private void updateFireSprite() {
        if (this.container == null || !"woodstove".equals(this.container.getType())) {
            if (this.isLit()) {
                if (this.FireSpriteIndex == -1) {
                    DebugLog.log(DebugType.Fireplace, "fireplace: added fire sprite");
                    this.FireSpriteIndex = this.AttachedAnimSprite != null ? this.AttachedAnimSprite.size() : 0;
                    if (this.getProperties() != null && this.getProperties().Is(IsoFlagType.collideW)) {
                        this.AttachAnim(
                            "Fire",
                            "01",
                            4,
                            IsoFireManager.FireAnimDelay,
                            -11 * Core.TileScale,
                            -84 * Core.TileScale,
                            true,
                            0,
                            false,
                            0.7F,
                            IsoFireManager.FireTintMod
                        );
                    } else {
                        this.AttachAnim(
                            "Fire",
                            "01",
                            4,
                            IsoFireManager.FireAnimDelay,
                            -35 * Core.TileScale,
                            -84 * Core.TileScale,
                            true,
                            0,
                            false,
                            0.7F,
                            IsoFireManager.FireTintMod
                        );
                    }

                    if (Core.TileScale == 1) {
                        this.AttachedAnimSprite.get(this.FireSpriteIndex).setScale(0.5F, 0.5F);
                    }
                }
            } else if (this.FireSpriteIndex != -1) {
                DebugLog.log(DebugType.Fireplace, "fireplace: removed fire sprite");
                this.AttachedAnimSprite.remove(this.FireSpriteIndex);
                if (this.FuelSpriteIndex > this.FireSpriteIndex) {
                    this.FuelSpriteIndex--;
                }

                this.FireSpriteIndex = -1;
            }
        }
    }

    private int calcLightRadius() {
        return (int)GameTime.instance.Lerp(1.0F, 8.0F, Math.min(this.getFuelAmount(), 60) / 60.0F);
    }

    private void updateLightSource() {
        if (this.isLit()) {
            int int0 = this.calcLightRadius();
            if (this.LightSource != null && this.LightSource.getRadius() != int0) {
                this.LightSource.life = 0;
                this.LightSource = null;
            }

            if (this.LightSource == null) {
                this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 1.0F, 0.1F, 0.1F, int0);
                IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
                IsoGridSquare.RecalcLightTime = -1;
                GameTime.instance.lightSourceUpdate = 100.0F;
            }
        } else if (this.LightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
            this.LightSource = null;
        }
    }

    private void updateHeatSource() {
        if (this.isLit()) {
            int int0 = this.calcLightRadius();
            if (this.heatSource == null) {
                this.heatSource = new IsoHeatSource((int)this.getX(), (int)this.getY(), (int)this.getZ(), int0, 35);
                IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
            } else if (int0 != this.heatSource.getRadius()) {
                this.heatSource.setRadius(int0);
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

                String string = "FireplaceRunning";
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
            int int0 = this.calcLightRadius();
            float float0 = (float)GameTime.getInstance().getWorldAgeHours();
            if (this.LastUpdateTime < 0.0F) {
                this.LastUpdateTime = float0;
            } else if (this.LastUpdateTime > float0) {
                this.LastUpdateTime = float0;
            }

            if (float0 > this.LastUpdateTime) {
                this.MinuteAccumulator = this.MinuteAccumulator + (float0 - this.LastUpdateTime) * 60.0F;
                int int1 = (int)Math.floor(this.MinuteAccumulator);
                if (int1 > 0) {
                    if (this.isLit()) {
                        DebugLog.log(DebugType.Fireplace, "IsoFireplace burned " + int1 + " minutes (" + this.getFuelAmount() + " remaining)");
                        this.useFuel(int1);
                        if (!this.hasFuel()) {
                            this.extinguish();
                        }
                    } else if (this.MinutesSinceExtinguished != -1) {
                        int int2 = Math.min(int1, SMOULDER_MINUTES - this.MinutesSinceExtinguished);
                        DebugLog.log(DebugType.Fireplace, "IsoFireplace smoldered " + int2 + " minutes (" + this.getFuelAmount() + " remaining)");
                        this.MinutesSinceExtinguished += int1;
                        this.useFuel(int2);
                        this.bSmouldering = true;
                        if (!this.hasFuel() || this.MinutesSinceExtinguished >= SMOULDER_MINUTES) {
                            this.MinutesSinceExtinguished = -1;
                            this.bSmouldering = false;
                        }
                    }

                    this.MinuteAccumulator -= int1;
                }
            }

            this.LastUpdateTime = float0;
            if (GameServer.bServer) {
                if (boolean0 != this.hasFuel() || boolean1 != this.isLit() || int0 != this.calcLightRadius()) {
                    this.sendObjectChange("state");
                }

                return;
            }
        }

        this.updateFuelSprite();
        this.updateFireSprite();
        this.updateLightSource();
        this.updateHeatSource();
        this.updateSound();
        if (this.AttachedAnimSprite != null && !this.AttachedAnimSprite.isEmpty()) {
            int int3 = this.AttachedAnimSprite.size();

            for (int int4 = 0; int4 < int3; int4++) {
                IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int4);
                IsoSprite sprite = spriteInstance.parentSprite;
                spriteInstance.update();
                float float1 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
                spriteInstance.Frame = spriteInstance.Frame + spriteInstance.AnimFrameIncrease * float1;
                if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
                    spriteInstance.Frame = 0.0F;
                }
            }
        }
    }

    @Override
    public void addToWorld() {
        IsoCell cell = this.getCell();
        cell.addToProcessIsoObject(this);
        this.container.addItemsToProcessItems();
    }

    @Override
    public void removeFromWorld() {
        if (this.LightSource != null) {
            IsoWorld.instance.CurrentCell.removeLamppost(this.LightSource);
            this.LightSource = null;
        }

        if (this.heatSource != null) {
            IsoWorld.instance.CurrentCell.removeHeatSource(this.heatSource);
            this.heatSource = null;
        }

        super.removeFromWorld();
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        super.render(x, y, z, col, false, bWallLightingPass, shader);
        if (this.AttachedAnimSprite != null) {
            for (int int0 = 0; int0 < this.AttachedAnimSprite.size(); int0++) {
                IsoSpriteInstance spriteInstance = this.AttachedAnimSprite.get(int0);
                spriteInstance.getParentSprite().render(spriteInstance, this, x, y, z, this.dir, this.offsetX, this.offsetY, col, true);
            }
        }
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("state".equals(change)) {
            bb.putInt(this.getFuelAmount());
            bb.put((byte)(this.isLit() ? 1 : 0));
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("state".equals(change)) {
            this.setFuelAmount(bb.getInt());
            this.setLit(bb.get() == 1);
        }
    }
}
