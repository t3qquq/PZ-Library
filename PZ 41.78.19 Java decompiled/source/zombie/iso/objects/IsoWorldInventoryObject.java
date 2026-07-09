// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.model.WorldItemModelDrawer;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemSoundManager;
import zombie.inventory.types.DrainableComboItem;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.PlayerCamera;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.Type;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;

public class IsoWorldInventoryObject extends IsoObject {
    public InventoryItem item;
    public float xoff;
    public float yoff;
    public float zoff;
    public boolean removeProcess = false;
    public double dropTime = -1.0;
    public boolean ignoreRemoveSandbox = false;

    public IsoWorldInventoryObject(InventoryItem _item, IsoGridSquare sq, float _xoff, float _yoff, float _zoff) {
        this.OutlineOnMouseover = true;
        if (_item.worldZRotation < 0) {
            _item.worldZRotation = Rand.Next(0, 360);
        }

        _item.setContainer(null);
        this.xoff = _xoff;
        this.yoff = _yoff;
        this.zoff = _zoff;
        if (this.xoff == 0.0F) {
            this.xoff = Rand.Next(1000) / 1000.0F;
        }

        if (this.yoff == 0.0F) {
            this.yoff = Rand.Next(1000) / 1000.0F;
        }

        this.item = _item;
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        this.updateSprite();
        this.square = sq;
        this.offsetY = 0.0F;
        this.offsetX = 0.0F;
        this.dropTime = GameTime.getInstance().getWorldAgeHours();
    }

    public IsoWorldInventoryObject(IsoCell cell) {
        super(cell);
        this.offsetY = 0.0F;
        this.offsetX = 0.0F;
    }

    public void swapItem(InventoryItem newItem) {
        if (newItem != null) {
            if (this.getItem() != null) {
                IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.getItem());
                ItemSoundManager.removeItem(this.getItem());
                this.getItem().setWorldItem(null);
                newItem.setID(this.getItem().getID());
                newItem.worldScale = this.getItem().worldScale;
                newItem.worldZRotation = this.getItem().worldZRotation;
            }

            this.item = newItem;
            if (newItem.getWorldItem() != null) {
                throw new IllegalArgumentException("newItem.getWorldItem() != null");
            } else {
                this.getItem().setWorldItem(this);
                this.setKeyId(this.getItem().getKeyId());
                this.setName(this.getItem().getName());
                if (this.getItem().shouldUpdateInWorld()) {
                    IsoWorld.instance.CurrentCell.addToProcessWorldItems(this);
                }

                IsoWorld.instance.CurrentCell.addToProcessItems(newItem);
                this.updateSprite();
                LuaEventManager.triggerEvent("OnContainerUpdate");
                if (GameServer.bServer) {
                    this.sendObjectChange("swapItem");
                }
            }
        }
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("swapItem".equals(change)) {
            if (this.getItem() == null) {
                return;
            }

            try {
                this.getItem().saveWithSize(bb, false);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        } else {
            super.saveChange(change, tbl, bb);
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("swapItem".equals(change)) {
            try {
                InventoryItem itemx = InventoryItem.loadItem(bb, 195);
                if (itemx != null) {
                    this.swapItem(itemx);
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        } else {
            super.loadChange(change, bb);
        }
    }

    private boolean isWaterSource() {
        if (this.item == null) {
            return false;
        } else {
            if (this.item.isBroken()) {
            }

            if (!this.item.canStoreWater()) {
                return false;
            } else if (this.item.isWaterSource() && this.item instanceof DrainableComboItem) {
                return ((DrainableComboItem)this.item).getRainFactor() > 0.0F;
            } else {
                if (this.item.hasReplaceType("WaterSource")) {
                    Item itemx = ScriptManager.instance.getItem(this.item.getReplaceType("WaterSource"));
                    if (itemx != null && itemx.getType() == Item.Type.Drainable) {
                        return itemx.getCanStoreWater() && itemx.getRainFactor() > 0.0F;
                    }
                }

                return false;
            }
        }
    }

    @Override
    public int getWaterAmount() {
        if (this.isWaterSource()) {
            return this.item instanceof DrainableComboItem ? ((DrainableComboItem)this.item).getRemainingUses() : 0;
        } else {
            return 0;
        }
    }

    @Override
    public void setWaterAmount(int units) {
        if (this.isWaterSource()) {
            DrainableComboItem drainableComboItem = Type.tryCastTo(this.item, DrainableComboItem.class);
            if (drainableComboItem != null) {
                drainableComboItem.setUsedDelta(units * drainableComboItem.getUseDelta());
                if (units == 0 && drainableComboItem.getReplaceOnDeplete() != null) {
                    InventoryItem item0 = InventoryItemFactory.CreateItem(drainableComboItem.getReplaceOnDepleteFullType());
                    if (item0 != null) {
                        item0.setCondition(this.getItem().getCondition());
                        item0.setFavorite(this.getItem().isFavorite());
                        this.swapItem(item0);
                    }
                }
            } else if (units > 0 && this.getItem().hasReplaceType("WaterSource")) {
                InventoryItem item1 = InventoryItemFactory.CreateItem(this.getItem().getReplaceType("WaterSource"));
                if (item1 != null) {
                    item1.setCondition(this.getItem().getCondition());
                    item1.setFavorite(this.getItem().isFavorite());
                    item1.setTaintedWater(this.getItem().isTaintedWater());
                    drainableComboItem = Type.tryCastTo(item1, DrainableComboItem.class);
                    if (drainableComboItem != null) {
                        drainableComboItem.setUsedDelta(units * drainableComboItem.getUseDelta());
                    }

                    this.swapItem(item1);
                }
            }
        }
    }

    @Override
    public int getWaterMax() {
        if (this.isWaterSource()) {
            float float0;
            if (this.item instanceof DrainableComboItem) {
                float0 = 1.0F / ((DrainableComboItem)this.item).getUseDelta();
            } else {
                if (!this.getItem().hasReplaceType("WaterSource")) {
                    return 0;
                }

                Item itemx = ScriptManager.instance.getItem(this.getItem().getReplaceType("WaterSource"));
                if (itemx == null) {
                    return 0;
                }

                float0 = 1.0F / itemx.getUseDelta();
            }

            return float0 - (int)float0 > 0.99F ? (int)float0 + 1 : (int)float0;
        } else {
            return 0;
        }
    }

    @Override
    public boolean isTaintedWater() {
        return this.isWaterSource() ? this.getItem().isTaintedWater() : false;
    }

    @Override
    public void setTaintedWater(boolean tainted) {
        if (this.isWaterSource()) {
            this.getItem().setTaintedWater(tainted);
        }
    }

    @Override
    public void update() {
        IsoCell cell = IsoWorld.instance.getCell();
        if (!this.removeProcess && this.item != null && this.item.shouldUpdateInWorld()) {
            cell.addToProcessItems(this.item);
        }
    }

    public void updateSprite() {
        this.sprite.setTintMod(new ColorInfo(this.item.col.r, this.item.col.g, this.item.col.b, this.item.col.a));
        if (!GameServer.bServer || ServerGUI.isCreated()) {
            String string = this.item.getTex().getName();
            if (this.item.isUseWorldItem()) {
                string = this.item.getWorldTexture();
            }

            try {
                Texture texture0 = Texture.getSharedTexture(string);
                if (texture0 == null) {
                    string = this.item.getTex().getName();
                }
            } catch (Exception exception) {
                string = "media/inventory/world/WItem_Sack.png";
            }

            Texture texture1 = this.sprite.LoadFrameExplicit(string);
            if (this.item.getScriptItem() == null) {
                this.sprite.def.scaleAspect(texture1.getWidthOrig(), texture1.getHeightOrig(), 16 * Core.TileScale, 16 * Core.TileScale);
            } else {
                float float0 = Core.TileScale;
                float float1 = this.item.getScriptItem().ScaleWorldIcon * (float0 / 2.0F);
                this.sprite.def.setScale(float1, float1);
            }
        }
    }

    public boolean finishupdate() {
        return this.removeProcess || this.item == null || !this.item.shouldUpdateInWorld();
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        this.xoff = input.getFloat();
        this.yoff = input.getFloat();
        this.zoff = input.getFloat();
        float float0 = input.getFloat();
        float float1 = input.getFloat();
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        this.item = InventoryItem.loadItem(input, WorldVersion);
        if (this.item == null) {
            input.getDouble();
            if (WorldVersion >= 193) {
                BitHeaderRead bitHeaderRead0 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, input);
                bitHeaderRead0.release();
            }
        } else {
            this.item.setWorldItem(this);
            this.sprite.getTintMod().r = this.item.getR();
            this.sprite.getTintMod().g = this.item.getG();
            this.sprite.getTintMod().b = this.item.getB();
            if (WorldVersion >= 108) {
                this.dropTime = input.getDouble();
            } else {
                this.dropTime = GameTime.getInstance().getWorldAgeHours();
            }

            if (WorldVersion >= 193) {
                BitHeaderRead bitHeaderRead1 = BitHeader.allocRead(BitHeader.HeaderSize.Byte, input);
                this.ignoreRemoveSandbox = bitHeaderRead1.hasFlags(1);
                bitHeaderRead1.release();
            }

            if (!GameServer.bServer || ServerGUI.isCreated()) {
                String string = this.item.getTex().getName();
                if (this.item.isUseWorldItem()) {
                    string = this.item.getWorldTexture();
                }

                try {
                    Texture texture0 = Texture.getSharedTexture(string);
                    if (texture0 == null) {
                        string = this.item.getTex().getName();
                    }
                } catch (Exception exception) {
                    string = "media/inventory/world/WItem_Sack.png";
                }

                Texture texture1 = this.sprite.LoadFrameExplicit(string);
                if (texture1 != null) {
                    if (WorldVersion < 33) {
                        float0 -= texture1.getWidthOrig() / 2;
                        float1 -= texture1.getHeightOrig();
                    }

                    if (this.item.getScriptItem() == null) {
                        this.sprite.def.scaleAspect(texture1.getWidthOrig(), texture1.getHeightOrig(), 16 * Core.TileScale, 16 * Core.TileScale);
                    } else {
                        float float2 = Core.TileScale;
                        float float3 = this.item.getScriptItem().ScaleWorldIcon * (float2 / 2.0F);
                        this.sprite.def.setScale(float3, float3);
                    }
                }
            }
        }
    }

    @Override
    public boolean Serialize() {
        return true;
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        output.put((byte)(this.Serialize() ? 1 : 0));
        if (this.Serialize()) {
            output.put(IsoObject.factoryGetClassID(this.getObjectName()));
            output.putFloat(this.xoff);
            output.putFloat(this.yoff);
            output.putFloat(this.zoff);
            output.putFloat(this.offsetX);
            output.putFloat(this.offsetY);
            this.item.saveWithSize(output, false);
            output.putDouble(this.dropTime);
            BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, output);
            if (this.ignoreRemoveSandbox) {
                bitHeaderWrite.addFlags(1);
            }

            bitHeaderWrite.write();
            bitHeaderWrite.release();
        }
    }

    @Override
    public void softReset() {
        this.square.removeWorldObject(this);
    }

    @Override
    public String getObjectName() {
        return "WorldInventoryItem";
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI) {
        this.item.DoTooltip(tooltipUI);
    }

    @Override
    public boolean HasTooltip() {
        return false;
    }

    @Override
    public boolean onMouseLeftClick(int x, int y) {
        return false;
    }

    private void debugDrawLocation(float float0, float float1, float float2) {
        if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
            float0 += this.xoff;
            float1 += this.yoff;
            float2 += this.zoff;
            LineDrawer.DrawIsoLine(float0 - 0.25F, float1, float2, float0 + 0.25F, float1, float2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
            LineDrawer.DrawIsoLine(float0, float1 - 0.25F, float2, float0, float1 + 0.25F, float2, 1.0F, 1.0F, 1.0F, 0.5F, 1);
        }
    }

    private void debugHitTest() {
        int int0 = IsoCamera.frameState.playerIndex;
        float float0 = Core.getInstance().getZoom(int0);
        float float1 = Mouse.getXA();
        float float2 = Mouse.getYA();
        float1 -= IsoCamera.getScreenLeft(int0);
        float2 -= IsoCamera.getScreenTop(int0);
        float1 *= float0;
        float2 *= float0;
        float float3 = this.getScreenPosX(int0) * float0;
        float float4 = this.getScreenPosY(int0) * float0;
        float float5 = IsoUtils.DistanceTo2D(float3, float4, float1, float2);
        byte byte0 = 48;
        if (float5 < byte0) {
            LineDrawer.drawCircle(float3, float4, byte0, 16, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        if (Core.bDebug) {
        }

        if (this.getItem().getScriptItem().isWorldRender()) {
            if (WorldItemModelDrawer.renderMain(
                this.getItem(), this.getSquare(), this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0.0F
            )) {
                this.debugDrawLocation(x, y, z);
            } else if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
                Texture texture = this.sprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
                if (texture != null) {
                    float float0 = texture.getWidthOrig() * this.sprite.def.getScaleX() / 2.0F;
                    float float1 = texture.getHeightOrig() * this.sprite.def.getScaleY() * 3.0F / 4.0F;
                    int int0 = IsoCamera.frameState.playerIndex;
                    float float2 = this.getAlpha(int0);
                    float float3 = this.getTargetAlpha(int0);
                    float float4 = PZMath.min(getSurfaceAlpha(this.square, this.zoff), float2);
                    this.setAlphaAndTarget(int0, float4);
                    this.sprite.render(this, x + this.xoff, y + this.yoff, z + this.zoff, this.dir, this.offsetX + float0, this.offsetY + float1, col, true);
                    this.setAlpha(int0, float2);
                    this.setTargetAlpha(int0, float3);
                    this.debugDrawLocation(x, y, z);
                }
            }
        }
    }

    @Override
    public void renderObjectPicker(float x, float y, float z, ColorInfo lightInfo) {
        if (this.sprite != null) {
            if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
                Texture texture = this.sprite.CurrentAnim.Frames.get(0).getTexture(this.dir);
                if (texture != null) {
                    float float0 = texture.getWidthOrig() / 2;
                    float float1 = texture.getHeightOrig();
                    this.sprite.renderObjectPicker(this.sprite.def, this, this.dir);
                }
            }
        }
    }

    public InventoryItem getItem() {
        return this.item;
    }

    @Override
    public void addToWorld() {
        if (this.item != null && this.item.shouldUpdateInWorld() && !IsoWorld.instance.CurrentCell.getProcessWorldItems().contains(this)) {
            IsoWorld.instance.CurrentCell.getProcessWorldItems().add(this);
        }

        if (this.item instanceof InventoryContainer) {
            ItemContainer container = ((InventoryContainer)this.item).getInventory();
            if (container != null) {
                container.addItemsToProcessItems();
            }
        }

        super.addToWorld();
    }

    @Override
    public void removeFromWorld() {
        this.removeProcess = true;
        IsoWorld.instance.getCell().getProcessWorldItems().remove(this);
        if (this.item != null) {
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this.item);
            ItemSoundManager.removeItem(this.item);
            this.item.atlasTexture = null;
        }

        if (this.item instanceof InventoryContainer) {
            ItemContainer container = ((InventoryContainer)this.item).getInventory();
            if (container != null) {
                container.removeItemsFromProcessItems();
            }
        }

        super.removeFromWorld();
    }

    @Override
    public void removeFromSquare() {
        if (this.square != null) {
            this.square.getWorldObjects().remove(this);
            this.square.chunk.recalcHashCodeObjects();
        }

        super.removeFromSquare();
    }

    public float getScreenPosX(int playerIndex) {
        float float0 = IsoUtils.XToScreen(this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0);
        PlayerCamera playerCamera = IsoCamera.cameras[playerIndex];
        return (float0 - playerCamera.getOffX()) / Core.getInstance().getZoom(playerIndex);
    }

    public float getScreenPosY(int playerIndex) {
        Texture texture = this.sprite == null ? null : this.sprite.getTextureForCurrentFrame(this.dir);
        float float0 = texture == null ? 0.0F : texture.getHeightOrig() * this.sprite.def.getScaleY() * 1.0F / 4.0F;
        float float1 = IsoUtils.YToScreen(this.getX() + this.xoff, this.getY() + this.yoff, this.getZ() + this.zoff, 0);
        PlayerCamera playerCamera = IsoCamera.cameras[playerIndex];
        return (float1 - playerCamera.getOffY() - float0) / Core.getInstance().getZoom(playerIndex);
    }

    public void setIgnoreRemoveSandbox(boolean b) {
        this.ignoreRemoveSandbox = b;
    }

    public boolean isIgnoreRemoveSandbox() {
        return this.ignoreRemoveSandbox;
    }

    public float getWorldPosX() {
        return this.getX() + this.xoff;
    }

    public float getWorldPosY() {
        return this.getY() + this.yoff;
    }

    public float getWorldPosZ() {
        return this.getZ() + this.zoff;
    }

    public static float getSurfaceAlpha(IsoGridSquare square, float _zoff) {
        if (square == null) {
            return 1.0F;
        } else {
            int int0 = IsoCamera.frameState.playerIndex;
            float float0 = 1.0F;
            if (_zoff > 0.01F) {
                boolean boolean0 = false;

                for (int int1 = 0; int1 < square.getObjects().size(); int1++) {
                    IsoObject object = square.getObjects().get(int1);
                    if (object.getSurfaceOffsetNoTable() > 0.0F) {
                        if (!boolean0) {
                            boolean0 = true;
                            float0 = 0.0F;
                        }

                        float0 = PZMath.max(float0, object.getAlpha(int0));
                    }
                }
            }

            return float0;
        }
    }
}
