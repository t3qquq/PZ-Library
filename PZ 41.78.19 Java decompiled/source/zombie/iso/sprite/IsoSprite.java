// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.CharacterModelCamera;
import zombie.core.properties.PropertyContainer;
import zombie.core.skinnedmodel.ModelCameraRenderData;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Mask;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCamera;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoObjectPicker;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWater;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.WorldConverter;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleModelCamera;

public final class IsoSprite {
    public static int maxCount = 0;
    public static float alphaStep = 0.05F;
    public static float globalOffsetX = -1.0F;
    public static float globalOffsetY = -1.0F;
    private static final ColorInfo info = new ColorInfo();
    private static final HashMap<String, Object[]> AnimNameSet = new HashMap<>();
    public int firerequirement;
    public String burntTile;
    public boolean forceAmbient;
    public boolean solidfloor;
    public boolean canBeRemoved;
    public boolean attachedFloor;
    public boolean cutW;
    public boolean cutN;
    public boolean solid;
    public boolean solidTrans;
    public boolean invisible;
    public boolean alwaysDraw;
    public boolean forceRender;
    public boolean moveWithWind = false;
    public boolean isBush = false;
    public static final byte RL_DEFAULT = 0;
    public static final byte RL_FLOOR = 1;
    public byte renderLayer = 0;
    public int windType = 1;
    public boolean Animate = true;
    public IsoAnim CurrentAnim = null;
    public boolean DeleteWhenFinished = false;
    public boolean Loop = true;
    public short soffX = 0;
    public short soffY = 0;
    public final PropertyContainer Properties = new PropertyContainer();
    public final ColorInfo TintMod = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
    public final HashMap<String, IsoAnim> AnimMap = new HashMap<>(2);
    public final ArrayList<IsoAnim> AnimStack = new ArrayList<>(1);
    public String name;
    public int tileSheetIndex = 0;
    public int ID = 20000000;
    public IsoSpriteInstance def;
    public ModelManager.ModelSlot modelSlot;
    IsoSpriteManager parentManager;
    private IsoObjectType type = IsoObjectType.MAX;
    private String parentObjectName = null;
    private IsoSpriteGrid spriteGrid;
    public boolean treatAsWallOrder = false;
    private boolean hideForWaterRender = false;

    public void setHideForWaterRender() {
        this.hideForWaterRender = true;
    }

    public IsoSprite() {
        this.parentManager = IsoSpriteManager.instance;
        this.def = IsoSpriteInstance.get(this);
    }

    public IsoSprite(IsoSpriteManager manager) {
        this.parentManager = manager;
        this.def = IsoSpriteInstance.get(this);
    }

    public static IsoSprite CreateSprite(IsoSpriteManager manager) {
        return new IsoSprite(manager);
    }

    public static IsoSprite CreateSpriteUsingCache(String objectName, String animName, int numFrames) {
        IsoSprite sprite = CreateSprite(IsoSpriteManager.instance);
        return sprite.setFromCache(objectName, animName, numFrames);
    }

    public static IsoSprite getSprite(IsoSpriteManager manager, int id) {
        if (WorldConverter.instance.TilesetConversions != null
            && !WorldConverter.instance.TilesetConversions.isEmpty()
            && WorldConverter.instance.TilesetConversions.containsKey(id)) {
            id = WorldConverter.instance.TilesetConversions.get(id);
        }

        return manager.IntMap.containsKey(id) ? manager.IntMap.get(id) : null;
    }

    public static void setSpriteID(IsoSpriteManager manager, int id, IsoSprite spr) {
        if (manager.IntMap.containsKey(spr.ID)) {
            manager.IntMap.remove(spr.ID);
            spr.ID = id;
            manager.IntMap.put(id, spr);
        }
    }

    public static IsoSprite getSprite(IsoSpriteManager manager, IsoSprite spr, int offset) {
        if (spr.name.contains("_")) {
            String[] strings = spr.name.split("_");
            int int0 = Integer.parseInt(strings[strings.length - 1].trim());
            int0 += offset;
            return manager.NamedMap.get(spr.name.substring(0, spr.name.lastIndexOf("_")) + "_" + int0);
        } else {
            return null;
        }
    }

    public static IsoSprite getSprite(IsoSpriteManager manager, String _name, int offset) {
        IsoSprite sprite = manager.NamedMap.get(_name);
        String string0 = sprite.name.substring(0, sprite.name.lastIndexOf(95));
        String string1 = sprite.name.substring(sprite.name.lastIndexOf(95) + 1);
        if (sprite.name.contains("_")) {
            int int0 = Integer.parseInt(string1.trim());
            int0 += offset;
            return manager.getSprite(string0 + "_" + int0);
        } else {
            return null;
        }
    }

    public static void DisposeAll() {
        AnimNameSet.clear();
    }

    public static boolean HasCache(String string) {
        return AnimNameSet.containsKey(string);
    }

    public IsoSpriteInstance newInstance() {
        return IsoSpriteInstance.get(this);
    }

    /**
     * @return the Properties
     */
    public PropertyContainer getProperties() {
        return this.Properties;
    }

    public String getParentObjectName() {
        return this.parentObjectName;
    }

    public void setParentObjectName(String val) {
        this.parentObjectName = val;
    }

    public void save(DataOutputStream output) throws IOException {
        GameWindow.WriteString(output, this.name);
    }

    public void load(DataInputStream input) throws IOException {
        this.name = GameWindow.ReadString(input);
        this.LoadFramesNoDirPageSimple(this.name);
    }

    public void Dispose() {
        for (IsoAnim anim : this.AnimMap.values()) {
            anim.Dispose();
        }

        this.AnimMap.clear();
        this.AnimStack.clear();
        this.CurrentAnim = null;
    }

    public boolean isMaskClicked(IsoDirections dir, int x, int y) {
        try {
            Texture texture = this.CurrentAnim.Frames.get((int)this.def.Frame).directions[dir.index()];
            if (texture == null) {
                return false;
            } else {
                Mask mask = texture.getMask();
                if (mask == null) {
                    return false;
                } else {
                    x = (int)(x - texture.offsetX);
                    y = (int)(y - texture.offsetY);
                    return mask.get(x, y);
                }
            }
        } catch (Exception exception) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
            return true;
        }
    }

    public boolean isMaskClicked(IsoDirections dir, int x, int y, boolean flip) {
        if (this.CurrentAnim == null) {
            return false;
        } else {
            this.initSpriteInstance();

            try {
                if (this.CurrentAnim != null && this.CurrentAnim.Frames != null && !(this.def.Frame >= this.CurrentAnim.Frames.size())) {
                    Texture texture = this.CurrentAnim.Frames.get((int)this.def.Frame).directions[dir.index()];
                    if (texture == null) {
                        return false;
                    } else {
                        Mask mask = texture.getMask();
                        if (mask == null) {
                            return false;
                        } else {
                            if (flip) {
                                x = (int)(x - (texture.getWidthOrig() - texture.getWidth() - texture.offsetX));
                                y = (int)(y - texture.offsetY);
                                x = texture.getWidth() - x;
                            } else {
                                x = (int)(x - texture.offsetX);
                                y = (int)(y - texture.offsetY);
                            }

                            return x >= 0 && y >= 0 && x <= texture.getWidth() && y <= texture.getHeight() ? mask.get(x, y) : false;
                        }
                    }
                } else {
                    return false;
                }
            } catch (Exception exception) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
                return true;
            }
        }
    }

    public float getMaskClickedY(IsoDirections dir, int x, int y, boolean flip) {
        try {
            Texture texture = this.CurrentAnim.Frames.get((int)this.def.Frame).directions[dir.index()];
            if (texture == null) {
                return 10000.0F;
            } else {
                Mask mask = texture.getMask();
                if (mask == null) {
                    return 10000.0F;
                } else {
                    if (flip) {
                        x = (int)(x - (texture.getWidthOrig() - texture.getWidth() - texture.offsetX));
                        y = (int)(y - texture.offsetY);
                        x = texture.getWidth() - x;
                    } else {
                        x = (int)(x - texture.offsetX);
                        y = (int)(y - texture.offsetY);
                        x = texture.getWidth() - x;
                    }

                    return y;
                }
            }
        } catch (Exception exception) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
            return 10000.0F;
        }
    }

    public Texture LoadFrameExplicit(String ObjectName) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put("default", this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        return this.CurrentAnim.LoadFrameExplicit(ObjectName);
    }

    public void LoadFrames(String ObjectName, String AnimName, int nFrames) {
        if (!this.AnimMap.containsKey(AnimName)) {
            this.CurrentAnim = new IsoAnim();
            this.AnimMap.put(AnimName, this.CurrentAnim);
            this.CurrentAnim.ID = this.AnimStack.size();
            this.AnimStack.add(this.CurrentAnim);
            this.CurrentAnim.LoadFrames(ObjectName, AnimName, nFrames);
        }
    }

    public void LoadFramesReverseAltName(String ObjectName, String AnimName, String AltName, int nFrames) {
        if (!this.AnimMap.containsKey(AltName)) {
            this.CurrentAnim = new IsoAnim();
            this.AnimMap.put(AltName, this.CurrentAnim);
            this.CurrentAnim.ID = this.AnimStack.size();
            this.AnimStack.add(this.CurrentAnim);
            this.CurrentAnim.LoadFramesReverseAltName(ObjectName, AnimName, AltName, nFrames);
        }
    }

    public void LoadFramesNoDirPage(String ObjectName, String AnimName, int nFrames) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put(AnimName, this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesNoDirPage(ObjectName, AnimName, nFrames);
    }

    public void LoadFramesNoDirPageDirect(String ObjectName, String AnimName, int nFrames) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put(AnimName, this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesNoDirPageDirect(ObjectName, AnimName, nFrames);
    }

    public void LoadFramesNoDirPageSimple(String ObjectName) {
        if (this.AnimMap.containsKey("default")) {
            IsoAnim anim = this.AnimMap.get("default");
            this.AnimStack.remove(anim);
            this.AnimMap.remove("default");
        }

        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put("default", this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesNoDirPage(ObjectName);
    }

    public void ReplaceCurrentAnimFrames(String ObjectName) {
        if (this.CurrentAnim != null) {
            this.CurrentAnim.Frames.clear();
            this.CurrentAnim.LoadFramesNoDirPage(ObjectName);
        }
    }

    public void LoadFramesPageSimple(String NObjectName, String SObjectName, String EObjectName, String WObjectName) {
        this.CurrentAnim = new IsoAnim();
        this.AnimMap.put("default", this.CurrentAnim);
        this.CurrentAnim.ID = this.AnimStack.size();
        this.AnimStack.add(this.CurrentAnim);
        this.CurrentAnim.LoadFramesPageSimple(NObjectName, SObjectName, EObjectName, WObjectName);
    }

    public void LoadFramesPcx(String ObjectName, String AnimName, int nFrames) {
        if (!this.AnimMap.containsKey(AnimName)) {
            this.CurrentAnim = new IsoAnim();
            this.AnimMap.put(AnimName, this.CurrentAnim);
            this.CurrentAnim.ID = this.AnimStack.size();
            this.AnimStack.add(this.CurrentAnim);
            this.CurrentAnim.LoadFramesPcx(ObjectName, AnimName, nFrames);
        }
    }

    public void PlayAnim(IsoAnim anim) {
        if (this.CurrentAnim == null || this.CurrentAnim != anim) {
            this.CurrentAnim = anim;
        }
    }

    public void PlayAnim(String _name) {
        if ((this.CurrentAnim == null || !this.CurrentAnim.name.equals(_name)) && this.AnimMap.containsKey(_name)) {
            this.CurrentAnim = this.AnimMap.get(_name);
        }
    }

    public void PlayAnimUnlooped(String _name) {
        if (this.AnimMap.containsKey(_name)) {
            if (this.CurrentAnim == null || !this.CurrentAnim.name.equals(_name)) {
                this.CurrentAnim = this.AnimMap.get(_name);
            }

            this.CurrentAnim.looped = false;
        }
    }

    public void ChangeTintMod(ColorInfo NewTintMod) {
        this.TintMod.r = NewTintMod.r;
        this.TintMod.g = NewTintMod.g;
        this.TintMod.b = NewTintMod.b;
        this.TintMod.a = NewTintMod.a;
    }

    public void RenderGhostTile(int x, int y, int z) {
        IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(this);
        spriteInstance.alpha = spriteInstance.targetAlpha = 0.6F;
        this.render(spriteInstance, null, x, y, z, IsoDirections.N, 32 * Core.TileScale, 96 * Core.TileScale, IsoGridSquare.getDefColorInfo(), true);
    }

    public void RenderGhostTileRed(int x, int y, int z) {
        IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(this);
        spriteInstance.tintr = 0.65F;
        spriteInstance.tintg = 0.2F;
        spriteInstance.tintb = 0.2F;
        spriteInstance.alpha = spriteInstance.targetAlpha = 0.6F;
        this.render(spriteInstance, null, x, y, z, IsoDirections.N, 32 * Core.TileScale, 96 * Core.TileScale, IsoGridSquare.getDefColorInfo(), true);
    }

    public void RenderGhostTileColor(int x, int y, int z, float r, float g, float b, float a) {
        this.RenderGhostTileColor(x, y, z, 0.0F, 0.0F, r, g, b, a);
    }

    public void RenderGhostTileColor(int x, int y, int z, float offsetX, float offsetY, float r, float g, float b, float a) {
        IsoSpriteInstance spriteInstance = IsoSpriteInstance.get(this);
        spriteInstance.tintr = r;
        spriteInstance.tintg = g;
        spriteInstance.tintb = b;
        spriteInstance.alpha = spriteInstance.targetAlpha = a;
        IsoGridSquare.getDefColorInfo().r = IsoGridSquare.getDefColorInfo().g = IsoGridSquare.getDefColorInfo().b = IsoGridSquare.getDefColorInfo().a = 1.0F;
        int int0 = Core.TileScale;
        this.render(spriteInstance, null, x, y, z, IsoDirections.N, 32 * int0 + offsetX, 96 * int0 + offsetY, IsoGridSquare.getDefColorInfo(), true);
    }

    public boolean hasActiveModel() {
        if (!ModelManager.instance.bDebugEnableModels) {
            return false;
        } else {
            return !ModelManager.instance.isCreated() ? false : this.modelSlot != null && this.modelSlot.active;
        }
    }

    public void renderVehicle(
        IsoSpriteInstance inst, IsoObject obj, float x, float y, float z, float offsetX, float offsetY, ColorInfo info2, boolean bDoRenderPrep
    ) {
        if (inst != null) {
            if (this.hasActiveModel()) {
                SpriteRenderer.instance.drawGeneric(ModelCameraRenderData.s_pool.alloc().init(VehicleModelCamera.instance, this.modelSlot));
                SpriteRenderer.instance.drawModel(this.modelSlot);
                if (!BaseVehicle.RENDER_TO_TEXTURE) {
                    return;
                }
            }

            info.r = info2.r;
            info.g = info2.g;
            info.b = info2.b;
            info.a = info2.a;

            try {
                if (bDoRenderPrep) {
                    inst.renderprep(obj);
                }

                float float0 = 0.0F;
                float float1 = 0.0F;
                if (globalOffsetX == -1.0F) {
                    globalOffsetX = -IsoCamera.frameState.OffX;
                    globalOffsetY = -IsoCamera.frameState.OffY;
                }

                if (obj == null || obj.sx == 0.0F || obj instanceof IsoMovingObject) {
                    float0 = IsoUtils.XToScreen(x + inst.offX, y + inst.offY, z + inst.offZ, 0);
                    float1 = IsoUtils.YToScreen(x + inst.offX, y + inst.offY, z + inst.offZ, 0);
                    float0 -= offsetX;
                    float1 -= offsetY;
                    if (obj != null) {
                        obj.sx = float0;
                        obj.sy = float1;
                    }
                }

                if (obj != null) {
                    float0 = obj.sx + globalOffsetX;
                    float1 = obj.sy + globalOffsetY;
                    float0 += this.soffX;
                    float1 += this.soffY;
                } else {
                    float0 += globalOffsetX;
                    float1 += globalOffsetY;
                    float0 += this.soffX;
                    float1 += this.soffY;
                }

                if (bDoRenderPrep) {
                    if (inst.tintr != 1.0F || inst.tintg != 1.0F || inst.tintb != 1.0F) {
                        info.r = info.r * inst.tintr;
                        info.g = info.g * inst.tintg;
                        info.b = info.b * inst.tintb;
                    }

                    info.a = inst.alpha;
                }

                if (!this.hasActiveModel() && (this.TintMod.r != 1.0F || this.TintMod.g != 1.0F || this.TintMod.b != 1.0F)) {
                    info.r = info.r * this.TintMod.r;
                    info.g = info.g * this.TintMod.g;
                    info.b = info.b * this.TintMod.b;
                }

                if (this.hasActiveModel()) {
                    float float2 = inst.getScaleX() * Core.TileScale;
                    float float3 = -inst.getScaleY() * Core.TileScale;
                    float float4 = 0.666F;
                    float2 /= 4.0F * float4;
                    float3 /= 4.0F * float4;
                    int int0 = ModelManager.instance.bitmap.getTexture().getWidth();
                    int int1 = ModelManager.instance.bitmap.getTexture().getHeight();
                    float0 -= int0 * float2 / 2.0F;
                    float1 -= int1 * float3 / 2.0F;
                    float float5 = ((BaseVehicle)obj).jniTransform.origin.y / 2.46F;
                    float1 += 96.0F * float5 / float3 / float4;
                    float1 += 27.84F / float3 / float4;
                    if (Core.getInstance().RenderShader != null && Core.getInstance().getOffscreenBuffer() != null) {
                        SpriteRenderer.instance
                            .render(
                                (Texture)ModelManager.instance.bitmap.getTexture(),
                                float0,
                                float1,
                                int0 * float2,
                                int1 * float3,
                                1.0F,
                                1.0F,
                                1.0F,
                                info.a,
                                null
                            );
                    } else {
                        SpriteRenderer.instance
                            .render(
                                (Texture)ModelManager.instance.bitmap.getTexture(),
                                float0,
                                float1,
                                int0 * float2,
                                int1 * float3,
                                info.r,
                                info.g,
                                info.b,
                                info.a,
                                null
                            );
                    }

                    if (Core.bDebug && DebugOptions.instance.ModelRenderBounds.getValue()) {
                        LineDrawer.drawRect(float0, float1, int0 * float2, int1 * float3, 1.0F, 1.0F, 1.0F, 1.0F, 1);
                    }
                }

                info.r = 1.0F;
                info.g = 1.0F;
                info.b = 1.0F;
            } catch (Exception exception) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
    }

    private IsoSpriteInstance getSpriteInstance() {
        this.initSpriteInstance();
        return this.def;
    }

    private void initSpriteInstance() {
        if (this.def == null) {
            this.def = IsoSpriteInstance.get(this);
        }
    }

    public final void render(IsoObject obj, float x, float y, float z, IsoDirections dir, float offsetX, float offsetY, ColorInfo info2, boolean bDoRenderPrep) {
        this.render(obj, x, y, z, dir, offsetX, offsetY, info2, bDoRenderPrep, null);
    }

    public final void render(
        IsoObject obj,
        float x,
        float y,
        float z,
        IsoDirections dir,
        float offsetX,
        float offsetY,
        ColorInfo info2,
        boolean bDoRenderPrep,
        Consumer<TextureDraw> texdModifier
    ) {
        this.render(this.getSpriteInstance(), obj, x, y, z, dir, offsetX, offsetY, info2, bDoRenderPrep, texdModifier);
    }

    public final void render(
        IsoSpriteInstance inst,
        IsoObject obj,
        float x,
        float y,
        float z,
        IsoDirections dir,
        float offsetX,
        float offsetY,
        ColorInfo info2,
        boolean bDoRenderPrep
    ) {
        this.render(inst, obj, x, y, z, dir, offsetX, offsetY, info2, bDoRenderPrep, null);
    }

    public void render(
        IsoSpriteInstance inst,
        IsoObject obj,
        float x,
        float y,
        float z,
        IsoDirections dir,
        float offsetX,
        float offsetY,
        ColorInfo info2,
        boolean bDoRenderPrep,
        Consumer<TextureDraw> texdModifier
    ) {
        if (this.hasActiveModel()) {
            this.renderActiveModel();
        } else {
            this.renderCurrentAnim(inst, obj, x, y, z, dir, offsetX, offsetY, info2, bDoRenderPrep, texdModifier);
        }
    }

    public void renderCurrentAnim(
        IsoSpriteInstance inst,
        IsoObject obj,
        float x,
        float y,
        float z,
        IsoDirections dir,
        float offsetX,
        float offsetY,
        ColorInfo col,
        boolean bDoRenderPrep,
        Consumer<TextureDraw> texdModifier
    ) {
        if (DebugOptions.instance.IsoSprite.RenderSprites.getValue()) {
            if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
                float float0 = this.getCurrentSpriteFrame(inst);
                info.set(col);
                Vector3 vector0 = IsoSprite.l_renderCurrentAnim.colorInfoBackup.set(info.r, info.g, info.b);
                Vector2 vector1 = IsoSprite.l_renderCurrentAnim.spritePos.set(0.0F, 0.0F);
                this.prepareToRenderSprite(inst, obj, x, y, z, dir, offsetX, offsetY, bDoRenderPrep, (int)float0, vector1);
                this.performRenderFrame(inst, obj, dir, (int)float0, vector1.x, vector1.y, texdModifier);
                info.r = vector0.x;
                info.g = vector0.y;
                info.b = vector0.z;
            }
        }
    }

    private float getCurrentSpriteFrame(IsoSpriteInstance spriteInstance) {
        if (this.CurrentAnim.FramesArray == null) {
            this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(new IsoDirectionFrame[0]);
        }

        if (this.CurrentAnim.FramesArray.length != this.CurrentAnim.Frames.size()) {
            this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
        }

        float float0;
        if (spriteInstance.Frame >= this.CurrentAnim.Frames.size()) {
            float0 = this.CurrentAnim.FramesArray.length - 1;
        } else if (spriteInstance.Frame < 0.0F) {
            spriteInstance.Frame = 0.0F;
            float0 = 0.0F;
        } else {
            float0 = spriteInstance.Frame;
        }

        return float0;
    }

    private void prepareToRenderSprite(
        IsoSpriteInstance spriteInstance,
        IsoObject object,
        float float4,
        float float3,
        float float2,
        IsoDirections directions,
        float float5,
        float float6,
        boolean boolean0,
        int int0,
        Vector2 vector
    ) {
        if (boolean0) {
            spriteInstance.renderprep(object);
        }

        float float0 = 0.0F;
        float float1 = 0.0F;
        if (globalOffsetX == -1.0F) {
            globalOffsetX = -IsoCamera.frameState.OffX;
            globalOffsetY = -IsoCamera.frameState.OffY;
        }

        if (object == null || object.sx == 0.0F || object instanceof IsoMovingObject) {
            float0 = IsoUtils.XToScreen(float4 + spriteInstance.offX, float3 + spriteInstance.offY, float2 + spriteInstance.offZ, 0);
            float1 = IsoUtils.YToScreen(float4 + spriteInstance.offX, float3 + spriteInstance.offY, float2 + spriteInstance.offZ, 0);
            float0 -= float5;
            float1 -= float6;
            if (object != null) {
                object.sx = float0;
                object.sy = float1;
            }

            float0 += globalOffsetX;
            float1 += globalOffsetY;
            float0 += this.soffX;
            float1 += this.soffY;
        } else if (object != null) {
            float0 = object.sx + globalOffsetX;
            float1 = object.sy + globalOffsetY;
            float0 += this.soffX;
            float1 += this.soffY;
        } else {
            float0 += globalOffsetX;
            float1 += globalOffsetY;
            float0 += this.soffX;
            float1 += this.soffY;
        }

        if (object instanceof IsoMovingObject && this.CurrentAnim != null && this.CurrentAnim.FramesArray[int0].getTexture(directions) != null) {
            float0 -= this.CurrentAnim.FramesArray[int0].getTexture(directions).getWidthOrig() / 2 * spriteInstance.getScaleX();
            float1 -= this.CurrentAnim.FramesArray[int0].getTexture(directions).getHeightOrig() * spriteInstance.getScaleY();
        }

        if (boolean0) {
            if (spriteInstance.tintr != 1.0F || spriteInstance.tintg != 1.0F || spriteInstance.tintb != 1.0F) {
                info.r = info.r * spriteInstance.tintr;
                info.g = info.g * spriteInstance.tintg;
                info.b = info.b * spriteInstance.tintb;
            }

            info.a = spriteInstance.alpha;
            if (spriteInstance.bMultiplyObjectAlpha && object != null) {
                info.a = info.a * object.getAlpha(IsoCamera.frameState.playerIndex);
            }
        }

        if (this.TintMod.r != 1.0F || this.TintMod.g != 1.0F || this.TintMod.b != 1.0F) {
            info.r = info.r * this.TintMod.r;
            info.g = info.g * this.TintMod.g;
            info.b = info.b * this.TintMod.b;
        }

        vector.set(float0, float1);
    }

    private void performRenderFrame(
        IsoSpriteInstance spriteInstance, IsoObject object, IsoDirections directions, int int0, float float4, float float5, Consumer<TextureDraw> consumer
    ) {
        if (int0 < this.CurrentAnim.FramesArray.length) {
            IsoDirectionFrame directionFrame = this.CurrentAnim.FramesArray[int0];
            Texture texture = directionFrame.getTexture(directions);
            if (texture != null) {
                if (Core.TileScale == 2 && texture.getWidthOrig() == 64 && texture.getHeightOrig() == 128) {
                    spriteInstance.setScale(2.0F, 2.0F);
                }

                if (Core.TileScale == 2
                    && spriteInstance.scaleX == 2.0F
                    && spriteInstance.scaleY == 2.0F
                    && texture.getWidthOrig() == 128
                    && texture.getHeightOrig() == 256) {
                    spriteInstance.setScale(1.0F, 1.0F);
                }

                if (!(spriteInstance.scaleX <= 0.0F) && !(spriteInstance.scaleY <= 0.0F)) {
                    float float0 = texture.getWidth();
                    float float1 = texture.getHeight();
                    float float2 = spriteInstance.scaleX;
                    float float3 = spriteInstance.scaleY;
                    if (float2 != 1.0F) {
                        float4 += texture.getOffsetX() * (float2 - 1.0F);
                        float0 *= float2;
                    }

                    if (float3 != 1.0F) {
                        float5 += texture.getOffsetY() * (float3 - 1.0F);
                        float1 *= float3;
                    }

                    if (DebugOptions.instance.IsoSprite.MovingObjectEdges.getValue() && object instanceof IsoMovingObject) {
                        this.renderSpriteOutline(float4, float5, texture, float2, float3);
                    }

                    if (DebugOptions.instance.IsoSprite.DropShadowEdges.getValue() && StringUtils.equals(texture.getName(), "dropshadow")) {
                        this.renderSpriteOutline(float4, float5, texture, float2, float3);
                    }

                    if (!this.hideForWaterRender || !IsoWater.getInstance().getShaderEnable()) {
                        if (object != null && object.getObjectRenderEffectsToApply() != null) {
                            directionFrame.render(
                                object.getObjectRenderEffectsToApply(), float4, float5, float0, float1, directions, info, spriteInstance.Flip, consumer
                            );
                        } else {
                            directionFrame.render(float4, float5, float0, float1, directions, info, spriteInstance.Flip, consumer);
                        }
                    }

                    if (int0 < this.CurrentAnim.FramesArray.length
                        && IsoObjectPicker.Instance.wasDirty
                        && IsoCamera.frameState.playerIndex == 0
                        && object != null) {
                        boolean boolean0 = directions == IsoDirections.W || directions == IsoDirections.SW || directions == IsoDirections.S;
                        if (spriteInstance.Flip) {
                            boolean0 = !boolean0;
                        }

                        float4 = object.sx + globalOffsetX;
                        float5 = object.sy + globalOffsetY;
                        if (object instanceof IsoMovingObject) {
                            float4 -= texture.getWidthOrig() / 2 * float2;
                            float5 -= texture.getHeightOrig() * float3;
                        }

                        IsoObjectPicker.Instance
                            .Add(
                                (int)float4,
                                (int)float5,
                                (int)(texture.getWidthOrig() * float2),
                                (int)(texture.getHeightOrig() * float3),
                                object.square,
                                object,
                                boolean0,
                                float2,
                                float3
                            );
                    }
                }
            }
        }
    }

    private void renderSpriteOutline(float float0, float float1, Texture texture, float float3, float float2) {
        LineDrawer.drawRect(float0, float1, texture.getWidthOrig() * float3, texture.getHeightOrig() * float2, 1.0F, 1.0F, 1.0F, 1.0F, 1);
        LineDrawer.drawRect(
            float0 + texture.getOffsetX() * float3,
            float1 + texture.getOffsetY() * float2,
            texture.getWidth() * float3,
            texture.getHeight() * float2,
            1.0F,
            1.0F,
            1.0F,
            1.0F,
            1
        );
    }

    public void renderActiveModel() {
        if (DebugOptions.instance.IsoSprite.RenderModels.getValue()) {
            this.modelSlot.model.updateLights();
            SpriteRenderer.instance.drawGeneric(ModelCameraRenderData.s_pool.alloc().init(CharacterModelCamera.instance, this.modelSlot));
            SpriteRenderer.instance.drawModel(this.modelSlot);
        }
    }

    public void renderBloodSplat(float x, float y, float z, ColorInfo info2) {
        if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
            byte byte0 = -8;
            short short0 = -228;
            byte0 = 0;
            byte byte1 = 0;

            try {
                if (globalOffsetX == -1.0F) {
                    globalOffsetX = -IsoCamera.frameState.OffX;
                    globalOffsetY = -IsoCamera.frameState.OffY;
                }

                float float0 = IsoUtils.XToScreen(x, y, z, 0);
                float float1 = IsoUtils.YToScreen(x, y, z, 0);
                float0 = (int)float0;
                float1 = (int)float1;
                float0 -= byte0;
                float1 -= byte1;
                float0 += globalOffsetX;
                float1 += globalOffsetY;
                if (float0 >= IsoCamera.frameState.OffscreenWidth || float0 + 64.0F <= 0.0F) {
                    return;
                }

                if (float1 >= IsoCamera.frameState.OffscreenHeight || float1 + 64.0F <= 0.0F) {
                    return;
                }

                info.r = info2.r;
                info.g = info2.g;
                info.b = info2.b;
                info.a = info2.a;
                this.CurrentAnim.Frames.get(0).render(float0, float1, IsoDirections.N, info, false, null);
            } catch (Exception exception) {
                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, exception);
            }
        }
    }

    public void renderObjectPicker(IsoSpriteInstance _def, IsoObject obj, IsoDirections dir) {
        if (this.CurrentAnim != null) {
            if (_def != null) {
                if (IsoPlayer.getInstance() == IsoPlayer.players[0]) {
                    if (!this.CurrentAnim.Frames.isEmpty()) {
                        if (_def.Frame >= this.CurrentAnim.Frames.size()) {
                            _def.Frame = 0.0F;
                        }

                        if (this.CurrentAnim.Frames.get((int)_def.Frame).getTexture(dir) != null) {
                            float float0 = obj.sx + globalOffsetX;
                            float float1 = obj.sy + globalOffsetY;
                            if (obj instanceof IsoMovingObject) {
                                float0 -= this.CurrentAnim.Frames.get((int)_def.Frame).getTexture(dir).getWidthOrig() / 2 * _def.getScaleX();
                                float1 -= this.CurrentAnim.Frames.get((int)_def.Frame).getTexture(dir).getHeightOrig() * _def.getScaleY();
                            }

                            if (_def.Frame < this.CurrentAnim.Frames.size() && IsoObjectPicker.Instance.wasDirty && IsoCamera.frameState.playerIndex == 0) {
                                Texture texture = this.CurrentAnim.Frames.get((int)_def.Frame).getTexture(dir);
                                boolean boolean0 = dir == IsoDirections.W || dir == IsoDirections.SW || dir == IsoDirections.S;
                                if (_def.Flip) {
                                    boolean0 = !boolean0;
                                }

                                IsoObjectPicker.Instance
                                    .Add(
                                        (int)float0,
                                        (int)float1,
                                        (int)(texture.getWidthOrig() * _def.getScaleX()),
                                        (int)(texture.getHeightOrig() * _def.getScaleY()),
                                        obj.square,
                                        obj,
                                        boolean0,
                                        _def.getScaleX(),
                                        _def.getScaleY()
                                    );
                            }
                        }
                    }
                }
            }
        }
    }

    public Texture getTextureForFrame(int frame, IsoDirections dir) {
        if (this.CurrentAnim != null && !this.CurrentAnim.Frames.isEmpty()) {
            if (this.CurrentAnim.FramesArray == null) {
                this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(new IsoDirectionFrame[0]);
            }

            if (this.CurrentAnim.FramesArray.length != this.CurrentAnim.Frames.size()) {
                this.CurrentAnim.FramesArray = this.CurrentAnim.Frames.toArray(this.CurrentAnim.FramesArray);
            }

            if (frame >= this.CurrentAnim.FramesArray.length) {
                frame = this.CurrentAnim.FramesArray.length - 1;
            }

            if (frame < 0) {
                frame = 0;
            }

            return this.CurrentAnim.FramesArray[frame].getTexture(dir);
        } else {
            return null;
        }
    }

    public Texture getTextureForCurrentFrame(IsoDirections dir) {
        this.initSpriteInstance();
        return this.getTextureForFrame((int)this.def.Frame, dir);
    }

    public void update() {
        this.update(this.def);
    }

    public void update(IsoSpriteInstance _def) {
        if (_def == null) {
            _def = IsoSpriteInstance.get(this);
        }

        if (this.CurrentAnim != null) {
            if (this.Animate && !_def.Finished) {
                float float0 = _def.Frame;
                if (!GameTime.isGamePaused()) {
                    _def.Frame = _def.Frame + _def.AnimFrameIncrease * (GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F);
                }

                if ((int)_def.Frame >= this.CurrentAnim.Frames.size() && this.Loop && _def.Looped) {
                    _def.Frame = 0.0F;
                }

                if ((int)float0 != (int)_def.Frame) {
                    _def.NextFrame = true;
                }

                if ((int)_def.Frame >= this.CurrentAnim.Frames.size() && (!this.Loop || !_def.Looped)) {
                    _def.Finished = true;
                    _def.Frame = this.CurrentAnim.FinishUnloopedOnFrame;
                    if (this.DeleteWhenFinished) {
                        this.Dispose();
                        this.Animate = false;
                    }
                }
            }
        }
    }

    public void CacheAnims(String key) {
        this.name = key;
        Stack stack = new Stack();

        for (int int0 = 0; int0 < this.AnimStack.size(); int0++) {
            IsoAnim anim = this.AnimStack.get(int0);
            String string = key + anim.name;
            stack.add(string);
            if (!IsoAnim.GlobalAnimMap.containsKey(string)) {
                IsoAnim.GlobalAnimMap.put(string, anim);
            }
        }

        AnimNameSet.put(key, stack.toArray());
    }

    public void LoadCache(String string) {
        Object[] objects = AnimNameSet.get(string);
        this.name = string;

        for (int int0 = 0; int0 < objects.length; int0++) {
            String _string = (String)objects[int0];
            IsoAnim anim = IsoAnim.GlobalAnimMap.get(_string);
            this.AnimMap.put(anim.name, anim);
            this.AnimStack.add(anim);
            this.CurrentAnim = anim;
        }
    }

    public IsoSprite setFromCache(String objectName, String animName, int numFrames) {
        String string = objectName + animName;
        if (HasCache(string)) {
            this.LoadCache(string);
        } else {
            this.LoadFramesNoDirPage(objectName, animName, numFrames);
            this.CacheAnims(string);
        }

        return this;
    }

    public IsoObjectType getType() {
        return this.type;
    }

    public void setType(IsoObjectType ntype) {
        this.type = ntype;
    }

    public void AddProperties(IsoSprite sprite) {
        this.getProperties().AddProperties(sprite.getProperties());
    }

    public int getID() {
        return this.ID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String string) {
        this.name = string;
    }

    public ColorInfo getTintMod() {
        return this.TintMod;
    }

    public void setTintMod(ColorInfo _info) {
        this.TintMod.set(_info);
    }

    public void setAnimate(boolean animate) {
        this.Animate = animate;
    }

    public IsoSpriteGrid getSpriteGrid() {
        return this.spriteGrid;
    }

    public void setSpriteGrid(IsoSpriteGrid sGrid) {
        this.spriteGrid = sGrid;
    }

    public boolean isMoveWithWind() {
        return this.moveWithWind;
    }

    public int getSheetGridIdFromName() {
        return this.name != null ? getSheetGridIdFromName(this.name) : -1;
    }

    public static int getSheetGridIdFromName(String _name) {
        if (_name != null) {
            int int0 = _name.lastIndexOf(95);
            if (int0 > 0 && int0 + 1 < _name.length()) {
                return Integer.parseInt(_name.substring(int0 + 1));
            }
        }

        return -1;
    }

    private static class l_renderCurrentAnim {
        static final Vector3 colorInfoBackup = new Vector3();
        static final Vector2 spritePos = new Vector2();
    }
}
