// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.ImmutableColor;
import zombie.core.SpriteRenderer;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.population.ClothingItem;
import zombie.core.skinnedmodel.shader.Shader;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.IsoCamera;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ModelAttachment;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.util.StringUtils;

public final class WorldItemModelDrawer extends TextureDraw.GenericDrawer {
    private static final ObjectPool<WorldItemModelDrawer> s_modelDrawerPool = new ObjectPool<>(WorldItemModelDrawer::new);
    private static final ObjectPool<WorldItemModelDrawer.WeaponPartParams> s_weaponPartParamPool = new ObjectPool<>(WorldItemModelDrawer.WeaponPartParams::new);
    private static final ArrayList<WeaponPart> s_tempWeaponPartList = new ArrayList<>();
    private static final ColorInfo tempColorInfo = new ColorInfo();
    private static final Matrix4f s_attachmentXfrm = new Matrix4f();
    private static final ImmutableColor ROTTEN_FOOD_COLOR = new ImmutableColor(0.5F, 0.5F, 0.5F);
    public static boolean NEW_WAY = true;
    private Model m_model;
    private ArrayList<WorldItemModelDrawer.WeaponPartParams> m_weaponParts;
    private float m_hue;
    private float m_tintR;
    private float m_tintG;
    private float m_tintB;
    private float m_x;
    private float m_y;
    private float m_z;
    private final Vector3f m_angle = new Vector3f();
    private final Matrix4f m_transform = new Matrix4f();
    private float m_ambientR;
    private float m_ambientG;
    private float m_ambientB;
    private float alpha = 1.0F;
    static final Vector3f tempVector3f = new Vector3f(0.0F, 5.0F, -2.0F);

    public static boolean renderMain(InventoryItem item, IsoGridSquare square, float float0, float float1, float float2, float float3) {
        return renderMain(item, square, float0, float1, float2, float3, -1.0F);
    }

    public static boolean renderMain(InventoryItem item, IsoGridSquare square, float float0, float float1, float float2, float float3, float float4) {
        if (item != null && square != null) {
            Core.getInstance();
            if (!Core.Option3DGroundItem) {
                return false;
            } else if (renderAtlasTexture(item, square, float0, float1, float2, float3, float4)) {
                return true;
            } else {
                if (!StringUtils.isNullOrEmpty(item.getWorldStaticItem())) {
                    ModelScript modelScript0 = ScriptManager.instance.getModelScript(item.getWorldStaticItem());
                    if (modelScript0 != null) {
                        String string0 = modelScript0.getMeshName();
                        String string1 = modelScript0.getTextureName();
                        String string2 = modelScript0.getShaderName();
                        ImmutableColor immutableColor0 = ImmutableColor.white;
                        float float5 = 1.0F;
                        if (item instanceof Food) {
                            if (((Food)item).isCooked()) {
                                ModelScript modelScript1 = ScriptManager.instance.getModelScript(item.getWorldStaticItem() + "Cooked");
                                if (modelScript1 != null) {
                                    string1 = modelScript1.getTextureName();
                                    string0 = modelScript1.getMeshName();
                                    string2 = modelScript1.getShaderName();
                                    modelScript0 = modelScript1;
                                }
                            }

                            if (((Food)item).isBurnt()) {
                                ModelScript modelScript2 = ScriptManager.instance.getModelScript(item.getWorldStaticItem() + "Burnt");
                                if (modelScript2 != null) {
                                    string1 = modelScript2.getTextureName();
                                    string0 = modelScript2.getMeshName();
                                    string2 = modelScript2.getShaderName();
                                    modelScript0 = modelScript2;
                                }
                            }

                            if (((Food)item).isRotten()) {
                                ModelScript modelScript3 = ScriptManager.instance.getModelScript(item.getWorldStaticItem() + "Rotten");
                                if (modelScript3 != null) {
                                    string1 = modelScript3.getTextureName();
                                    string0 = modelScript3.getMeshName();
                                    string2 = modelScript3.getShaderName();
                                    modelScript0 = modelScript3;
                                } else {
                                    immutableColor0 = ROTTEN_FOOD_COLOR;
                                }
                            }
                        }

                        if (item instanceof Clothing || item.getClothingItem() != null) {
                            string1 = modelScript0.getTextureName(true);
                            ItemVisual itemVisual0 = item.getVisual();
                            ClothingItem clothingItem0 = item.getClothingItem();
                            immutableColor0 = itemVisual0.getTint(clothingItem0);
                            if (string1 == null) {
                                if (clothingItem0.textureChoices.isEmpty()) {
                                    string1 = itemVisual0.getBaseTexture(clothingItem0);
                                } else {
                                    string1 = itemVisual0.getTextureChoice(clothingItem0);
                                }
                            }
                        }

                        boolean boolean0 = modelScript0.bStatic;
                        Model model0 = ModelManager.instance.tryGetLoadedModel(string0, string1, boolean0, string2, true);
                        if (model0 == null) {
                            ModelManager.instance.loadAdditionalModel(string0, string1, boolean0, string2);
                        }

                        model0 = ModelManager.instance.getLoadedModel(string0, string1, boolean0, string2);
                        if (model0 != null && model0.isReady() && model0.Mesh != null && model0.Mesh.isReady()) {
                            WorldItemModelDrawer worldItemModelDrawer0 = s_modelDrawerPool.alloc();
                            worldItemModelDrawer0.init(item, square, float0, float1, float2, model0, modelScript0, float5, immutableColor0, float3, false);
                            if (modelScript0.scale != 1.0F) {
                                worldItemModelDrawer0.m_transform.scale(modelScript0.scale);
                            }

                            if (item.worldScale != 1.0F) {
                                worldItemModelDrawer0.m_transform.scale(item.worldScale);
                            }

                            worldItemModelDrawer0.m_angle.x = 0.0F;
                            if (float4 < 0.0F) {
                                worldItemModelDrawer0.m_angle.y = item.worldZRotation;
                            } else {
                                worldItemModelDrawer0.m_angle.y = float4;
                            }

                            worldItemModelDrawer0.m_angle.z = 0.0F;
                            if (Core.bDebug) {
                            }

                            SpriteRenderer.instance.drawGeneric(worldItemModelDrawer0);
                            return true;
                        }
                    }
                } else if (item instanceof Clothing) {
                    ClothingItem clothingItem1 = item.getClothingItem();
                    ItemVisual itemVisual1 = item.getVisual();
                    if (clothingItem1 != null
                        && itemVisual1 != null
                        && "Bip01_Head".equalsIgnoreCase(clothingItem1.m_AttachBone)
                        && (!((Clothing)item).isCosmetic() || "Eyes".equals(item.getBodyLocation()))) {
                        boolean boolean1 = false;
                        String string3 = clothingItem1.getModel(boolean1);
                        if (!StringUtils.isNullOrWhitespace(string3)) {
                            String string4 = itemVisual1.getTextureChoice(clothingItem1);
                            boolean boolean2 = clothingItem1.m_Static;
                            String string5 = clothingItem1.m_Shader;
                            Model model1 = ModelManager.instance.tryGetLoadedModel(string3, string4, boolean2, string5, false);
                            if (model1 == null) {
                                ModelManager.instance.loadAdditionalModel(string3, string4, boolean2, string5);
                            }

                            model1 = ModelManager.instance.getLoadedModel(string3, string4, boolean2, string5);
                            if (model1 != null && model1.isReady() && model1.Mesh != null && model1.Mesh.isReady()) {
                                WorldItemModelDrawer worldItemModelDrawer1 = s_modelDrawerPool.alloc();
                                float float6 = itemVisual1.getHue(clothingItem1);
                                ImmutableColor immutableColor1 = itemVisual1.getTint(clothingItem1);
                                worldItemModelDrawer1.init(item, square, float0, float1, float2, model1, null, float6, immutableColor1, float3, false);
                                if (NEW_WAY) {
                                    worldItemModelDrawer1.m_angle.x = 180.0F + float3;
                                    if (float4 < 0.0F) {
                                        worldItemModelDrawer1.m_angle.y = item.worldZRotation;
                                    } else {
                                        worldItemModelDrawer1.m_angle.y = float4;
                                    }

                                    worldItemModelDrawer1.m_angle.z = -90.0F;
                                    if (Core.bDebug) {
                                    }

                                    worldItemModelDrawer1.m_transform.translate(-0.08F, 0.0F, 0.05F);
                                }

                                SpriteRenderer.instance.drawGeneric(worldItemModelDrawer1);
                                return true;
                            }
                        }
                    }
                }

                if (item instanceof HandWeapon) {
                    ModelScript modelScript4 = ScriptManager.instance.getModelScript(item.getStaticModel());
                    if (modelScript4 != null) {
                        String string6 = modelScript4.getMeshName();
                        String string7 = modelScript4.getTextureName();
                        String string8 = modelScript4.getShaderName();
                        boolean boolean3 = modelScript4.bStatic;
                        Model model2 = ModelManager.instance.tryGetLoadedModel(string6, string7, boolean3, string8, false);
                        if (model2 == null) {
                            ModelManager.instance.loadAdditionalModel(string6, string7, boolean3, string8);
                        }

                        model2 = ModelManager.instance.getLoadedModel(string6, string7, boolean3, string8);
                        if (model2 != null && model2.isReady() && model2.Mesh != null && model2.Mesh.isReady()) {
                            WorldItemModelDrawer worldItemModelDrawer2 = s_modelDrawerPool.alloc();
                            float float7 = 1.0F;
                            ImmutableColor immutableColor2 = ImmutableColor.white;
                            worldItemModelDrawer2.init(item, square, float0, float1, float2, model2, modelScript4, float7, immutableColor2, float3, true);
                            if (modelScript4.scale != 1.0F) {
                                worldItemModelDrawer2.m_transform.scale(modelScript4.scale);
                            }

                            if (item.worldScale != 1.0F) {
                                worldItemModelDrawer2.m_transform.scale(item.worldScale);
                            }

                            worldItemModelDrawer2.m_angle.x = 0.0F;
                            if (!NEW_WAY) {
                                worldItemModelDrawer2.m_angle.y = 180.0F;
                            }

                            if (float4 < 0.0F) {
                                worldItemModelDrawer2.m_angle.y = item.worldZRotation;
                            } else {
                                worldItemModelDrawer2.m_angle.y = float4;
                            }

                            if (!worldItemModelDrawer2.initWeaponParts((HandWeapon)item, modelScript4)) {
                                worldItemModelDrawer2.reset();
                                s_modelDrawerPool.release(worldItemModelDrawer2);
                                return false;
                            }

                            SpriteRenderer.instance.drawGeneric(worldItemModelDrawer2);
                            return true;
                        }
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }

    private static boolean renderAtlasTexture(InventoryItem item, IsoGridSquare square, float float3, float float4, float float5, float float0, float float1) {
        if (float0 > 0.0F) {
            return false;
        } else if (float1 >= 0.0F) {
            return false;
        } else {
            boolean boolean0 = !Core.bDebug || !GameKeyboard.isKeyDown(199);
            if (!boolean0) {
                return false;
            } else {
                if (item.atlasTexture != null && !item.atlasTexture.isStillValid(item)) {
                    item.atlasTexture = null;
                }

                if (item.atlasTexture == null) {
                    item.atlasTexture = WorldItemAtlas.instance.getItemTexture(item);
                }

                if (item.atlasTexture == null) {
                    return false;
                } else if (item.atlasTexture.isTooBig()) {
                    return false;
                } else {
                    if (IsoSprite.globalOffsetX == -1.0F) {
                        IsoSprite.globalOffsetX = -IsoCamera.frameState.OffX;
                        IsoSprite.globalOffsetY = -IsoCamera.frameState.OffY;
                    }

                    float float2 = IsoUtils.XToScreen(float3, float4, float5, 0);
                    float float6 = IsoUtils.YToScreen(float3, float4, float5, 0);
                    float2 += IsoSprite.globalOffsetX;
                    float6 += IsoSprite.globalOffsetY;
                    square.interpolateLight(tempColorInfo, float3 % 1.0F, float4 % 1.0F);
                    float float7 = IsoWorldInventoryObject.getSurfaceAlpha(square, float5 - (int)float5);
                    item.atlasTexture.render(float2, float6, tempColorInfo.r, tempColorInfo.g, tempColorInfo.b, float7);
                    WorldItemAtlas.instance.render();
                    return item.atlasTexture.isRenderMainOK();
                }
            }
        }
    }

    private void init(
        InventoryItem item,
        IsoGridSquare square,
        float float1,
        float float2,
        float float3,
        Model model,
        ModelScript modelScript,
        float float0,
        ImmutableColor immutableColor,
        float float4,
        boolean boolean0
    ) {
        this.m_model = model;
        if (this.m_weaponParts != null) {
            s_weaponPartParamPool.release(this.m_weaponParts);
            this.m_weaponParts.clear();
        }

        this.m_tintR = immutableColor.r;
        this.m_tintG = immutableColor.g;
        this.m_tintB = immutableColor.b;
        this.m_hue = float0;
        this.m_x = float1;
        this.m_y = float2;
        this.m_z = float3;
        this.m_transform.rotationZ((90.0F + float4) * (float) (Math.PI / 180.0));
        if (item instanceof Clothing) {
            float float5 = -0.08F;
            float float6 = 0.05F;
            this.m_transform.translate(float5, 0.0F, float6);
        }

        this.m_angle.x = 0.0F;
        this.m_angle.y = 525.0F;
        this.m_angle.z = 0.0F;
        if (NEW_WAY) {
            this.m_transform.identity();
            this.m_angle.y = 0.0F;
            if (boolean0) {
                this.m_transform.rotateXYZ(0.0F, (float) Math.PI, (float) (Math.PI / 2));
            }

            if (modelScript != null) {
                ModelAttachment modelAttachment = modelScript.getAttachmentById("world");
                if (modelAttachment != null) {
                    ModelInstanceRenderData.makeAttachmentTransform(modelAttachment, s_attachmentXfrm);
                    s_attachmentXfrm.invert();
                    this.m_transform.mul(s_attachmentXfrm);
                }
            }

            if (model.Mesh != null && model.Mesh.isReady() && model.Mesh.m_transform != null) {
                model.Mesh.m_transform.transpose();
                this.m_transform.mul(model.Mesh.m_transform);
                model.Mesh.m_transform.transpose();
            }
        }

        square.interpolateLight(tempColorInfo, this.m_x % 1.0F, this.m_y % 1.0F);
        if (GameServer.bServer && ServerGUI.isCreated()) {
            tempColorInfo.set(1.0F, 1.0F, 1.0F, 1.0F);
        }

        this.m_ambientR = tempColorInfo.r;
        this.m_ambientG = tempColorInfo.g;
        this.m_ambientB = tempColorInfo.b;
        this.alpha = IsoWorldInventoryObject.getSurfaceAlpha(square, float3 - (int)float3);
    }

    boolean initWeaponParts(HandWeapon weapon, ModelScript modelScript) {
        ArrayList arrayList0 = weapon.getModelWeaponPart();
        if (arrayList0 == null) {
            return true;
        } else {
            ArrayList arrayList1 = weapon.getAllWeaponParts(s_tempWeaponPartList);

            for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                WeaponPart weaponPart = (WeaponPart)arrayList1.get(int0);

                for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                    ModelWeaponPart modelWeaponPart = (ModelWeaponPart)arrayList0.get(int1);
                    if (weaponPart.getFullType().equals(modelWeaponPart.partType)) {
                        if (!this.initWeaponPart(modelWeaponPart, modelScript)) {
                            return false;
                        }
                        break;
                    }
                }
            }

            return true;
        }
    }

    boolean initWeaponPart(ModelWeaponPart modelWeaponPart, ModelScript modelScript1) {
        String string0 = StringUtils.discardNullOrWhitespace(modelWeaponPart.modelName);
        if (string0 == null) {
            return false;
        } else {
            ModelScript modelScript0 = ScriptManager.instance.getModelScript(string0);
            if (modelScript0 == null) {
                return false;
            } else {
                String string1 = modelScript0.getMeshName();
                String string2 = modelScript0.getTextureName();
                String string3 = modelScript0.getShaderName();
                boolean boolean0 = modelScript0.bStatic;
                Model model = ModelManager.instance.tryGetLoadedModel(string1, string2, boolean0, string3, false);
                if (model == null) {
                    ModelManager.instance.loadAdditionalModel(string1, string2, boolean0, string3);
                }

                model = ModelManager.instance.getLoadedModel(string1, string2, boolean0, string3);
                if (model != null && model.isReady() && model.Mesh != null && model.Mesh.isReady()) {
                    WorldItemModelDrawer.WeaponPartParams weaponPartParams = s_weaponPartParamPool.alloc();
                    weaponPartParams.m_model = model;
                    weaponPartParams.m_attachmentNameSelf = modelWeaponPart.attachmentNameSelf;
                    weaponPartParams.m_attachmentNameParent = modelWeaponPart.attachmentParent;
                    weaponPartParams.initTransform(modelScript1, modelScript0);
                    this.m_transform.mul(weaponPartParams.m_transform, weaponPartParams.m_transform);
                    if (this.m_weaponParts == null) {
                        this.m_weaponParts = new ArrayList<>();
                    }

                    this.m_weaponParts.add(weaponPartParams);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    @Override
    public void render() {
        GL11.glPushAttrib(1048575);
        GL11.glPushClientAttrib(-1);
        Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, 0.0F, false);
        GL11.glRotated(-180.0, 0.0, 1.0, 0.0);
        GL11.glRotated(this.m_angle.x, 1.0, 0.0, 0.0);
        GL11.glRotated(this.m_angle.y, 0.0, 1.0, 0.0);
        GL11.glRotated(this.m_angle.z, 0.0, 0.0, 1.0);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthFunc(513);
        GL11.glDepthMask(true);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glEnable(2929);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.renderModel(this.m_model, this.m_transform);
        if (this.m_weaponParts != null) {
            for (int int0 = 0; int0 < this.m_weaponParts.size(); int0++) {
                WorldItemModelDrawer.WeaponPartParams weaponPartParams = this.m_weaponParts.get(int0);
                this.renderModel(weaponPartParams.m_model, weaponPartParams.m_transform);
            }
        }

        if (Core.bDebug && DebugOptions.instance.ModelRenderAxis.getValue()) {
            Model.debugDrawAxis(0.0F, 0.0F, 0.0F, 0.5F, 1.0F);
        }

        Core.getInstance().DoPopIsoStuff();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        SpriteRenderer.ringBuffer.restoreBoundTextures = true;
        SpriteRenderer.ringBuffer.restoreVBOs = true;
    }

    void renderModel(Model model, Matrix4f matrix4f) {
        if (model.bStatic) {
            if (model.Effect == null) {
                model.CreateShader("basicEffect");
            }

            Shader shader = model.Effect;
            if (shader != null && model.Mesh != null && model.Mesh.isReady()) {
                shader.Start();
                if (model.tex != null) {
                    shader.setTexture(model.tex, "Texture", 0);
                }

                shader.setDepthBias(0.0F);
                shader.setAmbient(this.m_ambientR * 0.4F, this.m_ambientG * 0.4F, this.m_ambientB * 0.4F);
                shader.setLightingAmount(1.0F);
                shader.setHueShift(this.m_hue);
                shader.setTint(this.m_tintR, this.m_tintG, this.m_tintB);
                shader.setAlpha(this.alpha);

                for (int int0 = 0; int0 < 5; int0++) {
                    shader.setLight(int0, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Float.NaN, 0.0F, 0.0F, 0.0F, null);
                }

                Vector3f vector3f = tempVector3f;
                vector3f.x = 0.0F;
                vector3f.y = 5.0F;
                vector3f.z = -2.0F;
                vector3f.rotateY((float)Math.toRadians(this.m_angle.y));
                float float0 = 1.5F;
                shader.setLight(
                    4,
                    vector3f.x,
                    vector3f.z,
                    vector3f.y,
                    this.m_ambientR / 4.0F * float0,
                    this.m_ambientG / 4.0F * float0,
                    this.m_ambientB / 4.0F * float0,
                    5000.0F,
                    Float.NaN,
                    0.0F,
                    0.0F,
                    0.0F,
                    null
                );
                shader.setTransformMatrix(matrix4f, false);
                model.Mesh.Draw(shader);
                shader.End();
            }
        }
    }

    @Override
    public void postRender() {
        this.reset();
        s_modelDrawerPool.release(this);
    }

    void reset() {
        if (this.m_weaponParts != null) {
            s_weaponPartParamPool.release(this.m_weaponParts);
            this.m_weaponParts.clear();
        }
    }

    private static final class WeaponPartParams {
        Model m_model;
        String m_attachmentNameSelf;
        String m_attachmentNameParent;
        final Matrix4f m_transform = new Matrix4f();

        void initTransform(ModelScript modelScript0, ModelScript modelScript1) {
            this.m_transform.identity();
            Matrix4f matrix4f = WorldItemModelDrawer.s_attachmentXfrm;
            ModelAttachment modelAttachment0 = modelScript0.getAttachmentById(this.m_attachmentNameParent);
            if (modelAttachment0 != null) {
                ModelInstanceRenderData.makeAttachmentTransform(modelAttachment0, matrix4f);
                this.m_transform.mul(matrix4f);
            }

            ModelAttachment modelAttachment1 = modelScript1.getAttachmentById(this.m_attachmentNameSelf);
            if (modelAttachment1 != null) {
                ModelInstanceRenderData.makeAttachmentTransform(modelAttachment1, matrix4f);
                matrix4f.invert();
                this.m_transform.mul(matrix4f);
            }
        }
    }
}
