// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItems;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.DeadBodyAtlas;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.advancedanimation.AnimNode;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.AnimatedModel;
import zombie.core.skinnedmodel.advancedanimation.AnimationSet;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugLog;
import zombie.gameStates.GameLoadingState;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.InventoryContainer;
import zombie.inventory.types.Moveable;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaCell;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.MannequinScript;
import zombie.scripting.objects.ModelScript;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

public class IsoMannequin extends IsoObject implements IHumanVisual {
    private static final ColorInfo inf = new ColorInfo();
    private boolean bInit = false;
    private boolean bFemale = false;
    private boolean bZombie = false;
    private boolean bSkeleton = false;
    private String mannequinScriptName = null;
    private String modelScriptName = null;
    private String textureName = null;
    private String animSet = null;
    private String animState = null;
    private String pose = null;
    private String outfit = null;
    private final HumanVisual humanVisual = new HumanVisual(this);
    private final ItemVisuals itemVisuals = new ItemVisuals();
    private final WornItems wornItems;
    private MannequinScript mannequinScript = null;
    private ModelScript modelScript = null;
    private final IsoMannequin.PerPlayer[] perPlayer = new IsoMannequin.PerPlayer[4];
    private boolean bAnimate = false;
    private AnimatedModel animatedModel = null;
    private IsoMannequin.Drawer[] drawers = null;
    private float screenX;
    private float screenY;
    private static final IsoMannequin.StaticPerPlayer[] staticPerPlayer = new IsoMannequin.StaticPerPlayer[4];

    public IsoMannequin(IsoCell cell) {
        super(cell);
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));

        for (int int0 = 0; int0 < 4; int0++) {
            this.perPlayer[int0] = new IsoMannequin.PerPlayer();
        }
    }

    public IsoMannequin(IsoCell cell, IsoGridSquare square, IsoSprite sprite) {
        super(cell, square, sprite);
        this.wornItems = new WornItems(BodyLocations.getGroup("Human"));

        for (int int0 = 0; int0 < 4; int0++) {
            this.perPlayer[int0] = new IsoMannequin.PerPlayer();
        }
    }

    @Override
    public String getObjectName() {
        return "Mannequin";
    }

    @Override
    public HumanVisual getHumanVisual() {
        return this.humanVisual;
    }

    @Override
    public void getItemVisuals(ItemVisuals _itemVisuals) {
        this.wornItems.getItemVisuals(_itemVisuals);
    }

    @Override
    public boolean isFemale() {
        return this.bFemale;
    }

    @Override
    public boolean isZombie() {
        return this.bZombie;
    }

    @Override
    public boolean isSkeleton() {
        return this.bSkeleton;
    }

    @Override
    public boolean isItemAllowedInContainer(ItemContainer container, InventoryItem item) {
        return item instanceof Clothing && !StringUtils.isNullOrWhitespace(((Clothing)item).getBodyLocation())
            ? true
            : item instanceof InventoryContainer && !StringUtils.isNullOrWhitespace(((InventoryContainer)item).canBeEquipped());
    }

    public String getMannequinScriptName() {
        return this.mannequinScriptName;
    }

    public void setMannequinScriptName(String name) {
        if (!StringUtils.isNullOrWhitespace(name)) {
            if (ScriptManager.instance.getMannequinScript(name) != null) {
                this.mannequinScriptName = name;
                this.bInit = true;
                this.mannequinScript = null;
                this.textureName = null;
                this.animSet = null;
                this.animState = null;
                this.pose = null;
                this.outfit = null;
                this.humanVisual.clear();
                this.itemVisuals.clear();
                this.wornItems.clear();
                this.initMannequinScript();
                this.initModelScript();
                if (this.outfit == null) {
                    Outfit outfitx = OutfitManager.instance.GetRandomNonProfessionalOutfit(this.bFemale);
                    this.humanVisual.dressInNamedOutfit(outfitx.m_Name, this.itemVisuals);
                } else if (!"none".equalsIgnoreCase(this.outfit)) {
                    this.humanVisual.dressInNamedOutfit(this.outfit, this.itemVisuals);
                }

                this.humanVisual.setHairModel("");
                this.humanVisual.setBeardModel("");
                this.createInventory(this.itemVisuals);
                this.validateSkinTexture();
                this.validatePose();
                this.syncModel();
            }
        }
    }

    public String getPose() {
        return this.pose;
    }

    public void setRenderDirection(IsoDirections newDir) {
        int int0 = IsoCamera.frameState.playerIndex;
        if (newDir != this.perPlayer[int0].renderDirection) {
            this.perPlayer[int0].renderDirection = newDir;
        }
    }

    public void rotate(IsoDirections newDir) {
        if (newDir != null && newDir != IsoDirections.Max) {
            this.dir = newDir;

            for (int int0 = 0; int0 < 4; int0++) {
                this.perPlayer[int0].atlasTex = null;
            }

            if (GameServer.bServer) {
                this.sendObjectChange("rotate");
            }
        }
    }

    @Override
    public void saveChange(String change, KahluaTable tbl, ByteBuffer bb) {
        if ("rotate".equals(change)) {
            bb.put((byte)this.dir.index());
        } else {
            super.saveChange(change, tbl, bb);
        }
    }

    @Override
    public void loadChange(String change, ByteBuffer bb) {
        if ("rotate".equals(change)) {
            byte byte0 = bb.get();
            this.rotate(IsoDirections.fromIndex(byte0));
        } else {
            super.loadChange(change, bb);
        }
    }

    public void getVariables(Map<String, String> vars) {
        vars.put("Female", this.bFemale ? "true" : "false");
        vars.put("Pose", this.getPose());
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion, boolean IS_DEBUG_SAVE) throws IOException {
        super.load(input, WorldVersion, IS_DEBUG_SAVE);
        this.dir = IsoDirections.fromIndex(input.get());
        this.bInit = input.get() == 1;
        this.bFemale = input.get() == 1;
        this.bZombie = input.get() == 1;
        this.bSkeleton = input.get() == 1;
        if (WorldVersion >= 191) {
            this.mannequinScriptName = GameWindow.ReadString(input);
        }

        this.pose = GameWindow.ReadString(input);
        this.humanVisual.load(input, WorldVersion);
        this.textureName = this.humanVisual.getSkinTexture();
        this.wornItems.clear();
        if (this.container == null) {
            this.container = new ItemContainer("mannequin", this.getSquare(), this);
            this.container.setExplored(true);
        }

        this.container.clear();
        if (input.get() == 1) {
            try {
                this.container.ID = input.getInt();
                ArrayList arrayList = this.container.load(input, WorldVersion);
                byte byte0 = input.get();

                for (int int0 = 0; int0 < byte0; int0++) {
                    String string = GameWindow.ReadString(input);
                    short short0 = input.getShort();
                    if (short0 >= 0 && short0 < arrayList.size() && this.wornItems.getBodyLocationGroup().getLocation(string) != null) {
                        this.wornItems.setItem(string, (InventoryItem)arrayList.get(short0));
                    }
                }
            } catch (Exception exception) {
                if (this.container != null) {
                    DebugLog.log("Failed to stream in container ID: " + this.container.ID);
                }
            }
        }
    }

    @Override
    public void save(ByteBuffer output, boolean IS_DEBUG_SAVE) throws IOException {
        ItemContainer container = this.container;
        this.container = null;
        super.save(output, IS_DEBUG_SAVE);
        this.container = container;
        output.put((byte)this.dir.index());
        output.put((byte)(this.bInit ? 1 : 0));
        output.put((byte)(this.bFemale ? 1 : 0));
        output.put((byte)(this.bZombie ? 1 : 0));
        output.put((byte)(this.bSkeleton ? 1 : 0));
        GameWindow.WriteString(output, this.mannequinScriptName);
        GameWindow.WriteString(output, this.pose);
        this.humanVisual.save(output);
        if (container != null) {
            output.put((byte)1);
            output.putInt(container.ID);
            ArrayList arrayList = container.save(output);
            if (this.wornItems.size() > 127) {
                throw new RuntimeException("too many worn items");
            }

            output.put((byte)this.wornItems.size());
            this.wornItems.forEach(wornItem -> {
                GameWindow.WriteString(output, wornItem.getLocation());
                output.putShort((short)arrayList.indexOf(wornItem.getItem()));
            });
        } else {
            output.put((byte)0);
        }
    }

    @Override
    public void saveState(ByteBuffer output) throws IOException {
        if (!this.bInit) {
            this.initOutfit();
        }

        this.save(output);
    }

    @Override
    public void loadState(ByteBuffer input) throws IOException {
        input.get();
        input.get();
        this.load(input, 195);
        this.initOutfit();
        this.validateSkinTexture();
        this.validatePose();
        this.syncModel();
    }

    @Override
    public void addToWorld() {
        super.addToWorld();
        this.initOutfit();
        this.validateSkinTexture();
        this.validatePose();
        this.syncModel();
    }

    private void initMannequinScript() {
        if (!StringUtils.isNullOrWhitespace(this.mannequinScriptName)) {
            this.mannequinScript = ScriptManager.instance.getMannequinScript(this.mannequinScriptName);
        }

        if (this.mannequinScript == null) {
            this.modelScriptName = this.bFemale ? "FemaleBody" : "MaleBody";
            this.textureName = this.bFemale ? "F_Mannequin_White" : "M_Mannequin_White";
            this.animSet = "mannequin";
            this.animState = this.bFemale ? "female" : "male";
            this.outfit = null;
        } else {
            this.bFemale = this.mannequinScript.isFemale();
            this.modelScriptName = this.mannequinScript.getModelScriptName();
            if (this.textureName == null) {
                this.textureName = this.mannequinScript.getTexture();
            }

            this.animSet = this.mannequinScript.getAnimSet();
            this.animState = this.mannequinScript.getAnimState();
            if (this.pose == null) {
                this.pose = this.mannequinScript.getPose();
            }

            if (this.outfit == null) {
                this.outfit = this.mannequinScript.getOutfit();
            }
        }
    }

    private void initModelScript() {
        if (!StringUtils.isNullOrWhitespace(this.modelScriptName)) {
            this.modelScript = ScriptManager.instance.getModelScript(this.modelScriptName);
        }
    }

    private void validateSkinTexture() {
    }

    private void validatePose() {
        AnimationSet animationSet = AnimationSet.GetAnimationSet(this.animSet, false);
        if (animationSet == null) {
            DebugLog.General.warn("ERROR: mannequin AnimSet \"%s\" doesn't exist", this.animSet);
            this.pose = "Invalid";
        } else {
            AnimState animStatex = animationSet.GetState(this.animState);
            if (animStatex == null) {
                DebugLog.General.warn("ERROR: mannequin AnimSet \"%s\" state \"%s\" doesn't exist", this.animSet, this.animState);
                this.pose = "Invalid";
            } else {
                for (AnimNode animNode0 : animStatex.m_Nodes) {
                    if (animNode0.m_Name.equalsIgnoreCase(this.pose)) {
                        return;
                    }
                }

                if (animStatex.m_Nodes == null) {
                    DebugLog.General.warn("ERROR: mannequin AnimSet \"%s\" state \"%s\" node \"%s\" doesn't exist", this.animSet, this.animState, this.pose);
                    this.pose = "Invalid";
                } else {
                    AnimNode animNode1 = PZArrayUtil.pickRandom(animStatex.m_Nodes);
                    this.pose = animNode1.m_Name;
                }
            }
        }
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoChild, boolean bWallLightingPass, Shader shader) {
        int int0 = IsoCamera.frameState.playerIndex;
        x += 0.5F;
        y += 0.5F;
        this.calcScreenPos(x, y, z);
        this.renderShadow(x, y, z);
        if (this.bAnimate) {
            this.animatedModel.update();
            IsoMannequin.Drawer drawer = this.drawers[SpriteRenderer.instance.getMainStateIndex()];
            drawer.init(x, y, z);
            SpriteRenderer.instance.drawGeneric(drawer);
        } else {
            IsoDirections directions = this.dir;
            IsoMannequin.PerPlayer perPlayerx = this.perPlayer[int0];
            if (perPlayerx.renderDirection != null && perPlayerx.renderDirection != IsoDirections.Max) {
                this.dir = perPlayerx.renderDirection;
                perPlayerx.renderDirection = null;
                perPlayerx.bWasRenderDirection = true;
                perPlayerx.atlasTex = null;
            } else if (perPlayerx.bWasRenderDirection) {
                perPlayerx.bWasRenderDirection = false;
                perPlayerx.atlasTex = null;
            }

            if (perPlayerx.atlasTex == null) {
                perPlayerx.atlasTex = DeadBodyAtlas.instance.getBodyTexture(this);
                DeadBodyAtlas.instance.render();
            }

            this.dir = directions;
            if (perPlayerx.atlasTex != null) {
                if (this.isHighlighted()) {
                    inf.r = this.getHighlightColor().r;
                    inf.g = this.getHighlightColor().g;
                    inf.b = this.getHighlightColor().b;
                    inf.a = this.getHighlightColor().a;
                } else {
                    inf.r = col.r;
                    inf.g = col.g;
                    inf.b = col.b;
                    inf.a = col.a;
                }

                col = inf;
                if (!this.isHighlighted() && PerformanceSettings.LightingFrameSkip < 3) {
                    this.square.interpolateLight(col, x - this.square.getX(), y - this.square.getY());
                }

                perPlayerx.atlasTex.render((int)this.screenX, (int)this.screenY, col.r, col.g, col.b, this.getAlpha(int0));
                if (Core.bDebug) {
                }
            }
        }
    }

    @Override
    public void renderFxMask(float x, float y, float z, boolean bDoAttached) {
    }

    private void calcScreenPos(float float0, float float1, float float2) {
        if (IsoSprite.globalOffsetX == -1.0F) {
            IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
            IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
        }

        this.screenX = IsoUtils.XToScreen(float0, float1, float2, 0);
        this.screenY = IsoUtils.YToScreen(float0, float1, float2, 0);
        this.sx = this.screenX;
        this.sy = this.screenY;
        this.screenX = this.sx + IsoSprite.globalOffsetX;
        this.screenY = this.sy + IsoSprite.globalOffsetY;
        IsoObject[] objects = this.square.getObjects().getElements();

        for (int int0 = 0; int0 < this.square.getObjects().size(); int0++) {
            IsoObject object = objects[int0];
            if (object.isTableSurface()) {
                this.screenY = this.screenY - (object.getSurfaceOffset() + 1.0F) * Core.TileScale;
            }
        }
    }

    private void renderShadow(float var1, float var2, float var3) {
        Texture texture = Texture.getSharedTexture("dropshadow");
        int int0 = IsoCamera.frameState.playerIndex;
        float float0 = 0.8F * this.getAlpha(int0);
        ColorInfo colorInfo = this.square.lighting[int0].lightInfo();
        float0 *= (colorInfo.r + colorInfo.g + colorInfo.b) / 3.0F;
        float0 *= 0.8F;
        float float1 = this.screenX - texture.getWidth() / 2.0F * Core.TileScale;
        float float2 = this.screenY - texture.getHeight() / 2.0F * Core.TileScale;
        SpriteRenderer.instance
            .render(
                texture,
                float1,
                float2,
                (float)texture.getWidth() * Core.TileScale,
                (float)texture.getHeight() * Core.TileScale,
                1.0F,
                1.0F,
                1.0F,
                float0,
                null
            );
    }

    private void initOutfit() {
        if (this.bInit) {
            this.initMannequinScript();
            this.initModelScript();
        } else {
            this.bInit = true;
            this.getPropertiesFromSprite();
            this.getPropertiesFromZone();
            this.initMannequinScript();
            this.initModelScript();
            if (this.outfit == null) {
                Outfit outfitx = OutfitManager.instance.GetRandomNonProfessionalOutfit(this.bFemale);
                this.humanVisual.dressInNamedOutfit(outfitx.m_Name, this.itemVisuals);
            } else if (!"none".equalsIgnoreCase(this.outfit)) {
                this.humanVisual.dressInNamedOutfit(this.outfit, this.itemVisuals);
            }

            this.humanVisual.setHairModel("");
            this.humanVisual.setBeardModel("");
            this.createInventory(this.itemVisuals);
        }
    }

    private void getPropertiesFromSprite() {
        String string = this.sprite.name;
        switch (string) {
            case "location_shop_mall_01_65":
                this.mannequinScriptName = "FemaleWhite01";
                this.dir = IsoDirections.SE;
                break;
            case "location_shop_mall_01_66":
                this.mannequinScriptName = "FemaleWhite02";
                this.dir = IsoDirections.S;
                break;
            case "location_shop_mall_01_67":
                this.mannequinScriptName = "FemaleWhite03";
                this.dir = IsoDirections.SE;
                break;
            case "location_shop_mall_01_68":
                this.mannequinScriptName = "MaleWhite01";
                this.dir = IsoDirections.SE;
                break;
            case "location_shop_mall_01_69":
                this.mannequinScriptName = "MaleWhite02";
                this.dir = IsoDirections.S;
                break;
            case "location_shop_mall_01_70":
                this.mannequinScriptName = "MaleWhite03";
                this.dir = IsoDirections.SE;
                break;
            case "location_shop_mall_01_73":
                this.mannequinScriptName = "FemaleBlack01";
                this.dir = IsoDirections.SE;
                break;
            case "location_shop_mall_01_74":
                this.mannequinScriptName = "FemaleBlack02";
                this.dir = IsoDirections.S;
                break;
            case "location_shop_mall_01_75":
                this.mannequinScriptName = "FemaleBlack03";
                this.dir = IsoDirections.SE;
                break;
            case "location_shop_mall_01_76":
                this.mannequinScriptName = "MaleBlack01";
                this.dir = IsoDirections.SE;
                break;
            case "location_shop_mall_01_77":
                this.mannequinScriptName = "MaleBlack02";
                this.dir = IsoDirections.S;
                break;
            case "location_shop_mall_01_78":
                this.mannequinScriptName = "MaleBlack03";
                this.dir = IsoDirections.SE;
        }
    }

    private void getPropertiesFromZone() {
        if (this.getObjectIndex() != -1) {
            IsoMetaCell metaCell = IsoWorld.instance.getMetaGrid().getCellData(this.square.x / 300, this.square.y / 300);
            if (metaCell != null && metaCell.mannequinZones != null) {
                ArrayList arrayList = metaCell.mannequinZones;
                IsoMannequin.MannequinZone mannequinZone = null;

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    mannequinZone = (IsoMannequin.MannequinZone)arrayList.get(int0);
                    if (mannequinZone.contains(this.square.x, this.square.y, this.square.z)) {
                        break;
                    }

                    mannequinZone = null;
                }

                if (mannequinZone != null) {
                    if (mannequinZone.bFemale != -1) {
                        this.bFemale = mannequinZone.bFemale == 1;
                    }

                    if (mannequinZone.dir != IsoDirections.Max) {
                        this.dir = mannequinZone.dir;
                    }

                    if (mannequinZone.mannequinScript != null) {
                        this.mannequinScriptName = mannequinZone.mannequinScript;
                    }

                    if (mannequinZone.skin != null) {
                        this.textureName = mannequinZone.skin;
                    }

                    if (mannequinZone.pose != null) {
                        this.pose = mannequinZone.pose;
                    }

                    if (mannequinZone.outfit != null) {
                        this.outfit = mannequinZone.outfit;
                    }
                }
            }
        }
    }

    private void syncModel() {
        this.humanVisual.setForceModelScript(this.modelScriptName);
        String string = this.modelScriptName;
        switch (string) {
            case "FemaleBody":
                this.humanVisual.setForceModel(ModelManager.instance.m_femaleModel);
                break;
            case "MaleBody":
                this.humanVisual.setForceModel(ModelManager.instance.m_maleModel);
                break;
            default:
                this.humanVisual.setForceModel(ModelManager.instance.getLoadedModel(this.modelScriptName));
        }

        this.humanVisual.setSkinTextureName(this.textureName);
        this.wornItems.getItemVisuals(this.itemVisuals);

        for (int int0 = 0; int0 < 4; int0++) {
            this.perPlayer[int0].atlasTex = null;
        }

        if (this.bAnimate) {
            if (this.animatedModel == null) {
                this.animatedModel = new AnimatedModel();
                this.drawers = new IsoMannequin.Drawer[3];

                for (int int1 = 0; int1 < this.drawers.length; int1++) {
                    this.drawers[int1] = new IsoMannequin.Drawer();
                }
            }

            this.animatedModel.setAnimSetName(this.getAnimSetName());
            this.animatedModel.setState(this.getAnimStateName());
            this.animatedModel.setVariable("Female", this.bFemale);
            this.animatedModel.setVariable("Pose", this.getPose());
            this.animatedModel.setAngle(this.dir.ToVector());
            this.animatedModel.setModelData(this.humanVisual, this.itemVisuals);
        }
    }

    private void createInventory(ItemVisuals itemVisualsx) {
        if (this.container == null) {
            this.container = new ItemContainer("mannequin", this.getSquare(), this);
            this.container.setExplored(true);
        }

        this.container.clear();
        this.wornItems.setFromItemVisuals(itemVisualsx);
        this.wornItems.addItemsToItemContainer(this.container);
    }

    public void wearItem(InventoryItem item, IsoGameCharacter chr) {
        if (this.container.contains(item)) {
            ItemVisual itemVisual = item.getVisual();
            if (itemVisual != null) {
                if (item instanceof Clothing && !StringUtils.isNullOrWhitespace(((Clothing)item).getBodyLocation())) {
                    this.wornItems.setItem(((Clothing)item).getBodyLocation(), item);
                } else {
                    if (!(item instanceof InventoryContainer) || StringUtils.isNullOrWhitespace(((InventoryContainer)item).canBeEquipped())) {
                        return;
                    }

                    this.wornItems.setItem(((InventoryContainer)item).canBeEquipped(), item);
                }

                if (chr != null) {
                    ArrayList arrayList = this.container.getItems();

                    for (int int0 = 0; int0 < arrayList.size(); int0++) {
                        InventoryItem _item = (InventoryItem)arrayList.get(int0);
                        if (!this.wornItems.contains(_item)) {
                            this.container.removeItemOnServer(_item);
                            this.container.Remove(_item);
                            chr.getInventory().AddItem(_item);
                            int0--;
                        }
                    }
                }

                this.syncModel();
            }
        }
    }

    public void checkClothing(InventoryItem removedItem) {
        for (int int0 = 0; int0 < this.wornItems.size(); int0++) {
            InventoryItem item = this.wornItems.getItemByIndex(int0);
            if (this.container == null || this.container.getItems().indexOf(item) == -1) {
                this.wornItems.remove(item);
                this.syncModel();
                int0--;
            }
        }
    }

    public String getAnimSetName() {
        return this.animSet;
    }

    public String getAnimStateName() {
        return this.animState;
    }

    public void getCustomSettingsFromItem(InventoryItem item) throws IOException {
        if (item instanceof Moveable) {
            ByteBuffer byteBuffer = item.getByteData();
            if (byteBuffer == null) {
                return;
            }

            byteBuffer.rewind();
            int int0 = byteBuffer.getInt();
            byteBuffer.get();
            byteBuffer.get();
            this.load(byteBuffer, int0);
        }
    }

    public void setCustomSettingsToItem(InventoryItem item) throws IOException {
        if (item instanceof Moveable) {
            synchronized (SliceY.SliceBufferLock) {
                ByteBuffer byteBuffer = SliceY.SliceBuffer;
                byteBuffer.clear();
                byteBuffer.putInt(195);
                this.save(byteBuffer);
                byteBuffer.flip();
                item.byteData = ByteBuffer.allocate(byteBuffer.limit());
                item.byteData.put(byteBuffer);
            }

            if (this.container != null) {
                item.setActualWeight(item.getActualWeight() + this.container.getContentsWeight());
            }
        }
    }

    public static boolean isMannequinSprite(IsoSprite sprite) {
        return "Mannequin".equals(sprite.getProperties().Val("CustomName"));
    }

    private void resetMannequin() {
        this.bInit = false;
        this.bFemale = false;
        this.bZombie = false;
        this.bSkeleton = false;
        this.mannequinScriptName = null;
        this.modelScriptName = null;
        this.textureName = null;
        this.animSet = null;
        this.animState = null;
        this.pose = null;
        this.outfit = null;
        this.humanVisual.clear();
        this.itemVisuals.clear();
        this.wornItems.clear();
        this.mannequinScript = null;
        this.modelScript = null;
        this.bAnimate = false;
    }

    public static void renderMoveableItem(Moveable item, int x, int y, int z, IsoDirections dir) {
        int int0 = IsoCamera.frameState.playerIndex;
        IsoMannequin.StaticPerPlayer staticPerPlayerx = staticPerPlayer[int0];
        if (staticPerPlayerx == null) {
            staticPerPlayerx = staticPerPlayer[int0] = new IsoMannequin.StaticPerPlayer(int0);
        }

        staticPerPlayerx.renderMoveableItem(item, x, y, z, dir);
    }

    public static void renderMoveableObject(IsoMannequin mannequin, int x, int y, int z, IsoDirections dir) {
        mannequin.setRenderDirection(dir);
    }

    public static IsoDirections getDirectionFromItem(Moveable item, int playerIndex) {
        IsoMannequin.StaticPerPlayer staticPerPlayerx = staticPerPlayer[playerIndex];
        if (staticPerPlayerx == null) {
            staticPerPlayerx = staticPerPlayer[playerIndex] = new IsoMannequin.StaticPerPlayer(playerIndex);
        }

        return staticPerPlayerx.getDirectionFromItem(item);
    }

    private final class Drawer extends TextureDraw.GenericDrawer {
        float x;
        float y;
        float z;
        float m_animPlayerAngle;
        boolean bRendered;

        public void init(float arg0, float arg1, float arg2) {
            this.x = arg0;
            this.y = arg1;
            this.z = arg2;
            this.bRendered = false;
            IsoMannequin.this.animatedModel.renderMain();
            this.m_animPlayerAngle = IsoMannequin.this.animatedModel.getAnimationPlayer().getRenderedAngle();
        }

        @Override
        public void render() {
            IsoMannequin.this.animatedModel.DoRenderToWorld(this.x, this.y, this.z, this.m_animPlayerAngle);
            this.bRendered = true;
        }

        @Override
        public void postRender() {
            IsoMannequin.this.animatedModel.postRender(this.bRendered);
        }
    }

    public static final class MannequinZone extends IsoMetaGrid.Zone {
        public int bFemale = -1;
        public IsoDirections dir = IsoDirections.Max;
        public String mannequinScript = null;
        public String pose = null;
        public String skin = null;
        public String outfit = null;

        public MannequinZone(String string0, String string1, int int0, int int1, int int2, int int3, int int4, KahluaTable table) {
            super(string0, string1, int0, int1, int2, int3, int4);
            if (table != null) {
                Object object = table.rawget("Female");
                if (object instanceof Boolean) {
                    this.bFemale = object == Boolean.TRUE ? 1 : 0;
                }

                object = table.rawget("Direction");
                if (object instanceof String) {
                    this.dir = IsoDirections.valueOf((String)object);
                }

                object = table.rawget("Outfit");
                if (object instanceof String) {
                    this.outfit = (String)object;
                }

                object = table.rawget("Script");
                if (object instanceof String) {
                    this.mannequinScript = (String)object;
                }

                object = table.rawget("Skin");
                if (object instanceof String) {
                    this.skin = (String)object;
                }

                object = table.rawget("Pose");
                if (object instanceof String) {
                    this.pose = (String)object;
                }
            }
        }
    }

    private static final class PerPlayer {
        private DeadBodyAtlas.BodyTexture atlasTex = null;
        IsoDirections renderDirection = null;
        boolean bWasRenderDirection = false;
    }

    private static final class StaticPerPlayer {
        final int playerIndex;
        Moveable _moveable = null;
        Moveable _failedItem = null;
        IsoMannequin _mannequin = null;

        StaticPerPlayer(int int0) {
            this.playerIndex = int0;
        }

        void renderMoveableItem(Moveable moveable, int int0, int int1, int int2, IsoDirections directions) {
            if (this.checkItem(moveable)) {
                if (this._moveable != moveable) {
                    this._moveable = moveable;

                    try {
                        this._mannequin.getCustomSettingsFromItem(this._moveable);
                    } catch (IOException iOException) {
                    }

                    this._mannequin.initOutfit();
                    this._mannequin.validateSkinTexture();
                    this._mannequin.validatePose();
                    this._mannequin.syncModel();
                    this._mannequin.perPlayer[this.playerIndex].atlasTex = null;
                }

                this._mannequin.square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
                if (this._mannequin.square != null) {
                    this._mannequin.perPlayer[this.playerIndex].renderDirection = directions;
                    IsoMannequin.inf.set(1.0F, 1.0F, 1.0F, 1.0F);
                    this._mannequin.render(int0, int1, int2, IsoMannequin.inf, false, false, null);
                }
            }
        }

        IsoDirections getDirectionFromItem(Moveable moveable) {
            if (!this.checkItem(moveable)) {
                return IsoDirections.S;
            } else {
                this._moveable = null;

                try {
                    this._mannequin.getCustomSettingsFromItem(moveable);
                    return this._mannequin.getDir();
                } catch (Exception exception) {
                    return IsoDirections.S;
                }
            }
        }

        boolean checkItem(Moveable moveable) {
            if (moveable == null) {
                return false;
            } else {
                String string = moveable.getWorldSprite();
                IsoSprite sprite = IsoSpriteManager.instance.getSprite(string);
                if (sprite == null || !IsoMannequin.isMannequinSprite(sprite)) {
                    return false;
                } else if (moveable.getByteData() == null) {
                    Thread thread = Thread.currentThread();
                    if (thread != GameWindow.GameThread && thread != GameLoadingState.loader && thread == GameServer.MainThread) {
                        return false;
                    } else {
                        if (this._mannequin == null || this._mannequin.getCell() != IsoWorld.instance.CurrentCell) {
                            this._mannequin = new IsoMannequin(IsoWorld.instance.CurrentCell);
                        }

                        if (this._failedItem == moveable) {
                            return false;
                        } else {
                            try {
                                this._mannequin.resetMannequin();
                                this._mannequin.sprite = sprite;
                                this._mannequin.initOutfit();
                                this._mannequin.validateSkinTexture();
                                this._mannequin.validatePose();
                                this._mannequin.syncModel();
                                this._mannequin.setCustomSettingsToItem(moveable);
                                return true;
                            } catch (IOException iOException) {
                                this._failedItem = moveable;
                                return false;
                            }
                        }
                    }
                } else {
                    if (this._mannequin == null || this._mannequin.getCell() != IsoWorld.instance.CurrentCell) {
                        this._mannequin = new IsoMannequin(IsoWorld.instance.CurrentCell);
                    }

                    return true;
                }
            }
        }
    }
}
