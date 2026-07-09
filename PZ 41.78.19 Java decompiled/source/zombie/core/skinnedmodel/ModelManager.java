// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjglx.opengl.Display;
import org.lwjglx.opengl.Util;
import zombie.DebugFileWatcher;
import zombie.GameWindow;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.asset.AssetPath;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.AttachedItems.AttachedItem;
import zombie.characters.AttachedItems.AttachedModels;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.PZGLUtil;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.advancedanimation.AdvancedAnimator;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.SoftwareSkinnedModelAnim;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.AnimationAssetManager;
import zombie.core.skinnedmodel.model.MeshAssetManager;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelAssetManager;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelInstanceTextureInitializer;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.VehicleModelInstance;
import zombie.core.skinnedmodel.model.VehicleSubModelInstance;
import zombie.core.skinnedmodel.population.PopTemplateManager;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponPart;
import zombie.iso.FireShader;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoPuddles;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWater;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.ParticlesFire;
import zombie.iso.PlayerCamera;
import zombie.iso.PuddlesShader;
import zombie.iso.SmokeShader;
import zombie.iso.Vector2;
import zombie.iso.WaterShader;
import zombie.iso.sprite.SkyBox;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.ObjectPool;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.AnimationsMesh;
import zombie.scripting.objects.ItemReplacement;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.ModelWeaponPart;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Lambda;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;
import zombie.vehicles.BaseVehicle;

/**
 * Created by LEMMYATI on 05/01/14.
 */
public final class ModelManager {
    public static boolean NoOpenGL = false;
    public static final ModelManager instance = new ModelManager();
    private final HashMap<String, Model> m_modelMap = new HashMap<>();
    public Model m_maleModel;
    public Model m_femaleModel;
    public Model m_skeletonMaleModel;
    public Model m_skeletonFemaleModel;
    public TextureFBO bitmap;
    private boolean m_bCreated = false;
    public boolean bDebugEnableModels = true;
    public boolean bCreateSoftwareMeshes = false;
    public final HashMap<String, SoftwareSkinnedModelAnim> SoftwareMeshAnims = new HashMap<>();
    private final ArrayList<ModelManager.ModelSlot> m_modelSlots = new ArrayList<>();
    private final ObjectPool<ModelInstance> m_modelInstancePool = new ObjectPool<>(ModelInstance::new);
    private final ArrayList<WeaponPart> m_tempWeaponPartList = new ArrayList<>();
    private ModelMesh m_animModel;
    private final HashMap<String, AnimationAsset> m_animationAssets = new HashMap<>();
    private final ModelManager.ModAnimations m_gameAnimations = new ModelManager.ModAnimations("game");
    private final HashMap<String, ModelManager.ModAnimations> m_modAnimations = new HashMap<>();
    private final ArrayList<StaticAnimation> m_cachedAnims = new ArrayList<>();
    private final HashSet<IsoGameCharacter> m_contains = new HashSet<>();
    private final ArrayList<IsoGameCharacter.TorchInfo> m_torches = new ArrayList<>();
    private final Stack<IsoLightSource> m_freeLights = new Stack<>();
    private final ArrayList<IsoLightSource> m_torchLights = new ArrayList<>();
    private final ArrayList<IsoGameCharacter> ToRemove = new ArrayList<>();
    private final ArrayList<IsoGameCharacter> ToResetNextFrame = new ArrayList<>();
    private final ArrayList<IsoGameCharacter> ToResetEquippedNextFrame = new ArrayList<>();
    private final ArrayList<ModelManager.ModelSlot> m_resetAfterRender = new ArrayList<>();
    private final Stack<IsoLightSource> m_lights = new Stack<>();
    private final Stack<IsoLightSource> m_lightsTemp = new Stack<>();
    private final Vector2 m_tempVec2 = new Vector2();
    private final Vector2 m_tempVec2_2 = new Vector2();
    private static final TreeMap<String, ModelManager.ModelMetaData> modelMetaData = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    static String basicEffect = "basicEffect";
    static String isStaticTrue = ";isStatic=true";
    static String shaderEquals = "shader=";
    static String texA = ";tex=";
    static String amp = "&";
    static HashMap<String, String> toLower = new HashMap<>();
    static HashMap<String, String> toLowerTex = new HashMap<>();
    static HashMap<String, String> toLowerKeyRoot = new HashMap<>();
    static StringBuilder builder = new StringBuilder();

    public boolean isCreated() {
        return this.m_bCreated;
    }

    public void create() {
        if (!this.m_bCreated) {
            if (!GameServer.bServer || ServerGUI.isCreated()) {
                Texture texture = new Texture(1024, 1024, 16);
                PerformanceSettings.UseFBOs = false;

                try {
                    this.bitmap = new TextureFBO(texture, false);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    PerformanceSettings.UseFBOs = false;
                    DebugLog.Animation.error("FBO not compatible with gfx card at this time.");
                    return;
                }
            }

            DebugLog.Animation.println("Loading 3D models");
            this.initAnimationMeshes(false);
            this.m_modAnimations.put(this.m_gameAnimations.m_modID, this.m_gameAnimations);
            AnimationsMesh animationsMesh = ScriptManager.instance.getAnimationsMesh("Human");
            ModelMesh modelMesh = animationsMesh.modelMesh;
            if (!NoOpenGL && this.bCreateSoftwareMeshes) {
                SoftwareSkinnedModelAnim softwareSkinnedModelAnim = new SoftwareSkinnedModelAnim(
                    this.m_cachedAnims.toArray(new StaticAnimation[0]), modelMesh.softwareMesh, modelMesh.skinningData
                );
                this.SoftwareMeshAnims.put(modelMesh.getPath().getPath(), softwareSkinnedModelAnim);
            }

            Model model0 = this.loadModel("skinned/malebody", null, modelMesh);
            Model model1 = this.loadModel("skinned/femalebody", null, modelMesh);
            Model model2 = this.loadModel("skinned/Male_Skeleton", null, modelMesh);
            Model model3 = this.loadModel("skinned/Female_Skeleton", null, modelMesh);
            this.m_animModel = modelMesh;
            this.loadModAnimations();
            model0.addDependency(this.getAnimationAssetRequired("bob/bob_idle"));
            model0.addDependency(this.getAnimationAssetRequired("bob/bob_walk"));
            model0.addDependency(this.getAnimationAssetRequired("bob/bob_run"));
            model1.addDependency(this.getAnimationAssetRequired("bob/bob_idle"));
            model1.addDependency(this.getAnimationAssetRequired("bob/bob_walk"));
            model1.addDependency(this.getAnimationAssetRequired("bob/bob_run"));
            this.m_maleModel = model0;
            this.m_femaleModel = model1;
            this.m_skeletonMaleModel = model2;
            this.m_skeletonFemaleModel = model3;
            this.m_bCreated = true;
            AdvancedAnimator.systemInit();
            PopTemplateManager.instance.init();
        }
    }

    public void loadAdditionalModel(String meshName, String tex, boolean bStatic, String shaderName) {
        boolean boolean0 = this.bCreateSoftwareMeshes;
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.debugln("createSoftwareMesh: %B, model: %s", boolean0, meshName);
        }

        Model model = this.loadModelInternal(meshName, tex, shaderName, this.m_animModel, bStatic);
        if (boolean0) {
            SoftwareSkinnedModelAnim softwareSkinnedModelAnim = new SoftwareSkinnedModelAnim(
                this.m_cachedAnims.toArray(new StaticAnimation[0]), model.softwareMesh, (SkinningData)model.Tag
            );
            this.SoftwareMeshAnims.put(meshName.toLowerCase(), softwareSkinnedModelAnim);
        }
    }

    public ModelInstance newAdditionalModelInstance(String meshName, String tex, IsoGameCharacter chr, AnimationPlayer animPlayer, String shaderName) {
        Model model = this.tryGetLoadedModel(meshName, tex, false, shaderName, false);
        if (model == null) {
            boolean boolean0 = false;
            instance.loadAdditionalModel(meshName, tex, boolean0, shaderName);
        }

        model = this.getLoadedModel(meshName, tex, false, shaderName);
        return this.newInstance(model, chr, animPlayer);
    }

    private void loadAnimsFromDir(String string, ModelMesh modelMesh) {
        File file = new File(ZomboidFileSystem.instance.base, string);
        this.loadAnimsFromDir(ZomboidFileSystem.instance.baseURI, ZomboidFileSystem.instance.getMediaRootFile().toURI(), file, modelMesh, this.m_gameAnimations);
    }

    private void loadAnimsFromDir(URI uri0, URI uri1, File file0, ModelMesh modelMesh, ModelManager.ModAnimations modAnimations) {
        if (!file0.exists()) {
            DebugLog.General.error("ERROR: %s", file0.getPath());

            for (File file1 = file0.getParentFile(); file1 != null; file1 = file1.getParentFile()) {
                DebugLog.General.error(" - Parent exists: %B, %s", file1.exists(), file1.getPath());
            }
        }

        if (file0.isDirectory()) {
            File[] files = file0.listFiles();
            if (files != null) {
                boolean boolean0 = false;

                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        this.loadAnimsFromDir(uri0, uri1, file2, modelMesh, modAnimations);
                    } else {
                        String string = ZomboidFileSystem.instance.getAnimName(uri1, file2);
                        this.loadAnim(string, modelMesh, modAnimations);
                        boolean0 = true;
                        if (!NoOpenGL && RenderThread.RenderThread == null) {
                            Display.processMessages();
                        }
                    }
                }

                if (boolean0) {
                    DebugFileWatcher.instance.add(new ModelManager.AnimDirReloader(uri0, uri1, file0.getPath(), modelMesh, modAnimations).GetFileWatcher());
                }
            }
        }
    }

    public void RenderSkyBox(TextureDraw texd, int shaderID, int userId, int apiId, int bufferId) {
        int int0 = TextureFBO.getCurrentID();
        switch (apiId) {
            case 1:
                GL30.glBindFramebuffer(36160, bufferId);
                break;
            case 2:
                ARBFramebufferObject.glBindFramebuffer(36160, bufferId);
                break;
            case 3:
                EXTFramebufferObject.glBindFramebufferEXT(36160, bufferId);
        }

        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, 1.0, 1.0, 0.0, -1.0, 1.0);
        GL11.glViewport(0, 0, 512, 512);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ARBShaderObjects.glUseProgramObjectARB(shaderID);
        if (Shader.ShaderMap.containsKey(shaderID)) {
            Shader.ShaderMap.get(shaderID).startRenderThread(texd);
        }

        GL11.glColor4f(0.13F, 0.96F, 0.13F, 1.0F);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex2f(0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex2f(0.0F, 1.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex2f(1.0F, 1.0F);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex2f(1.0F, 0.0F);
        GL11.glEnd();
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(userId);
        GL11.glViewport(0, 0, playerCamera.OffscreenWidth, playerCamera.OffscreenHeight);
        switch (apiId) {
            case 1:
                GL30.glBindFramebuffer(36160, int0);
                break;
            case 2:
                ARBFramebufferObject.glBindFramebuffer(36160, int0);
                break;
            case 3:
                EXTFramebufferObject.glBindFramebufferEXT(36160, int0);
        }

        SkyBox.getInstance().swapTextureFBO();
    }

    public void RenderWater(TextureDraw texd, int shaderID, int userId, boolean bShore) {
        try {
            Util.checkGLError();
        } catch (Throwable throwable) {
        }

        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        IsoWater.getInstance().waterProjection();
        PlayerCamera playerCamera = SpriteRenderer.instance.getRenderingPlayerCamera(userId);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ARBShaderObjects.glUseProgramObjectARB(shaderID);
        Shader shader = Shader.ShaderMap.get(shaderID);
        if (shader instanceof WaterShader) {
            ((WaterShader)shader).updateWaterParams(texd, userId);
        }

        IsoWater.getInstance().waterGeometry(bShore);
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        if (!PZGLUtil.checkGLError(true)) {
            DebugLog.General.println("DEBUG: EXCEPTION RenderWater");
            PZGLUtil.printGLState(DebugLog.General);
        }
    }

    public void RenderPuddles(int shaderID, int userId, int z) {
        PZGLUtil.checkGLError(true);
        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        IsoPuddles.getInstance().puddlesProjection();
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        ARBShaderObjects.glUseProgramObjectARB(shaderID);
        Shader shader = Shader.ShaderMap.get(shaderID);
        if (shader instanceof PuddlesShader) {
            ((PuddlesShader)shader).updatePuddlesParams(userId, z);
        }

        IsoPuddles.getInstance().puddlesGeometry(z);
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        if (!PZGLUtil.checkGLError(true)) {
            DebugLog.General.println("DEBUG: EXCEPTION RenderPuddles");
            PZGLUtil.printGLState(DebugLog.General);
        }
    }

    public void RenderParticles(TextureDraw texd, int userId, int va11) {
        int int0 = ParticlesFire.getInstance().getFireShaderID();
        int int1 = ParticlesFire.getInstance().getSmokeShaderID();
        int int2 = ParticlesFire.getInstance().getVapeShaderID();

        try {
            Util.checkGLError();
        } catch (Throwable throwable) {
        }

        GL11.glPushClientAttrib(-1);
        GL11.glPushAttrib(1048575);
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glViewport(
            0,
            0,
            SpriteRenderer.instance.getRenderingPlayerCamera(userId).OffscreenWidth,
            SpriteRenderer.instance.getRenderingPlayerCamera(userId).OffscreenHeight
        );
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        float float0 = ParticlesFire.getInstance().getShaderTime();
        GL11.glBlendFunc(770, 1);
        ARBShaderObjects.glUseProgramObjectARB(int0);
        Shader shader = Shader.ShaderMap.get(int0);
        if (shader instanceof FireShader) {
            ((FireShader)shader).updateFireParams(texd, userId, float0);
        }

        ParticlesFire.getInstance().getGeometryFire(va11);
        GL11.glBlendFunc(770, 771);
        ARBShaderObjects.glUseProgramObjectARB(int1);
        shader = Shader.ShaderMap.get(int1);
        if (shader instanceof SmokeShader) {
            ((SmokeShader)shader).updateSmokeParams(texd, userId, float0);
        }

        ParticlesFire.getInstance().getGeometry(va11);
        ARBShaderObjects.glUseProgramObjectARB(0);
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
        GL11.glPopClientAttrib();
        Texture.lastTextureID = -1;
        GL11.glViewport(
            0,
            0,
            SpriteRenderer.instance.getRenderingPlayerCamera(userId).OffscreenWidth,
            SpriteRenderer.instance.getRenderingPlayerCamera(userId).OffscreenHeight
        );
        if (!PZGLUtil.checkGLError(true)) {
            DebugLog.General.println("DEBUG: EXCEPTION RenderParticles");
            PZGLUtil.printGLState(DebugLog.General);
        }
    }

    /**
     * Reset  Resets the specified character.
     * 
     * @param chr the character to reset
     */
    public void Reset(IsoGameCharacter chr) {
        if (chr.legsSprite != null && chr.legsSprite.modelSlot != null) {
            ModelManager.ModelSlot modelSlot = chr.legsSprite.modelSlot;
            this.resetModelInstance(modelSlot.model, modelSlot);

            for (int int0 = 0; int0 < modelSlot.sub.size(); int0++) {
                ModelInstance modelInstance = modelSlot.sub.get(int0);
                if (modelInstance != chr.primaryHandModel && modelInstance != chr.secondaryHandModel && !modelSlot.attachedModels.contains(modelInstance)) {
                    this.resetModelInstanceRecurse(modelInstance, modelSlot);
                }
            }

            this.derefModelInstances(chr.getReadyModelData());
            chr.getReadyModelData().clear();
            this.dressInRandomOutfit(chr);
            Model model = this.getBodyModel(chr);
            modelSlot.model = this.newInstance(model, chr, chr.getAnimationPlayer());
            modelSlot.model.setOwner(modelSlot);
            modelSlot.model.m_modelScript = ScriptManager.instance.getModelScript(chr.isFemale() ? "FemaleBody" : "MaleBody");
            this.DoCharacterModelParts(chr, modelSlot);
        }
    }

    public void reloadAllOutfits() {
        for (IsoGameCharacter character : this.m_contains) {
            character.reloadOutfit();
        }
    }

    /**
     * Add the supplied character to the visible render list.
     */
    public void Add(IsoGameCharacter chr) {
        if (this.m_bCreated) {
            if (chr.isSceneCulled()) {
                if (this.ToRemove.contains(chr)) {
                    this.ToRemove.remove(chr);
                    chr.legsSprite.modelSlot.bRemove = false;
                } else {
                    ModelManager.ModelSlot modelSlot = this.getSlot(chr);
                    modelSlot.framesSinceStart = 0;
                    if (modelSlot.model != null) {
                        RenderThread.invokeOnRenderContext(modelSlot.model::destroySmartTextures);
                    }

                    this.dressInRandomOutfit(chr);
                    Model model = this.getBodyModel(chr);
                    modelSlot.model = this.newInstance(model, chr, chr.getAnimationPlayer());
                    modelSlot.model.setOwner(modelSlot);
                    modelSlot.model.m_modelScript = ScriptManager.instance.getModelScript(chr.isFemale() ? "FemaleBody" : "MaleBody");
                    this.DoCharacterModelParts(chr, modelSlot);
                    modelSlot.active = true;
                    modelSlot.character = chr;
                    modelSlot.model.character = chr;
                    modelSlot.model.object = chr;
                    modelSlot.model.SetForceDir(modelSlot.model.character.getForwardDirection());

                    for (int int0 = 0; int0 < modelSlot.sub.size(); int0++) {
                        ModelInstance modelInstance = modelSlot.sub.get(int0);
                        modelInstance.character = chr;
                        modelInstance.object = chr;
                    }

                    chr.legsSprite.modelSlot = modelSlot;
                    this.m_contains.add(chr);
                    chr.onCullStateChanged(this, false);
                    if (modelSlot.model.AnimPlayer != null && modelSlot.model.AnimPlayer.isBoneTransformsNeedFirstFrame()) {
                        try {
                            modelSlot.Update();
                        } catch (Throwable throwable) {
                            ExceptionLogger.logException(throwable);
                        }
                    }
                }
            }
        }
    }

    public void dressInRandomOutfit(IsoGameCharacter chr) {
        IsoZombie zombie0 = Type.tryCastTo(chr, IsoZombie.class);
        if (zombie0 != null && !zombie0.isReanimatedPlayer() && !zombie0.wasFakeDead()) {
            if (DebugOptions.instance.ZombieOutfitRandom.getValue() && !chr.isPersistentOutfitInit()) {
                zombie0.bDressInRandomOutfit = true;
            }

            if (zombie0.bDressInRandomOutfit) {
                zombie0.bDressInRandomOutfit = false;
                zombie0.dressInRandomOutfit();
            }

            if (!chr.isPersistentOutfitInit()) {
                zombie0.dressInPersistentOutfitID(chr.getPersistentOutfitID());
            }
        } else {
            if (GameClient.bClient && zombie0 != null && !chr.isPersistentOutfitInit() && chr.getPersistentOutfitID() != 0) {
                zombie0.dressInPersistentOutfitID(chr.getPersistentOutfitID());
            }
        }
    }

    public Model getBodyModel(IsoGameCharacter chr) {
        if (chr.isZombie() && ((IsoZombie)chr).isSkeleton()) {
            return chr.isFemale() ? this.m_skeletonFemaleModel : this.m_skeletonMaleModel;
        } else {
            return chr.isFemale() ? this.m_femaleModel : this.m_maleModel;
        }
    }

    /**
     * Returns TRUE if the character is currently in the visible render list, and has not been flagged for removal.
     */
    public boolean ContainsChar(IsoGameCharacter chr) {
        return this.m_contains.contains(chr) && !this.ToRemove.contains(chr);
    }

    public void ResetCharacterEquippedHands(IsoGameCharacter chr) {
        if (chr != null && chr.legsSprite != null && chr.legsSprite.modelSlot != null) {
            this.DoCharacterModelEquipped(chr, chr.legsSprite.modelSlot);
        }
    }

    private void DoCharacterModelEquipped(IsoGameCharacter character, ModelManager.ModelSlot modelSlot) {
        if (character.primaryHandModel != null) {
            character.clearVariable("RightHandMask");
            character.primaryHandModel.maskVariableValue = null;
            this.resetModelInstanceRecurse(character.primaryHandModel, modelSlot);
            modelSlot.sub.remove(character.primaryHandModel);
            modelSlot.model.sub.remove(character.primaryHandModel);
            character.primaryHandModel = null;
        }

        if (character.secondaryHandModel != null) {
            character.clearVariable("LeftHandMask");
            character.secondaryHandModel.maskVariableValue = null;
            this.resetModelInstanceRecurse(character.secondaryHandModel, modelSlot);
            modelSlot.sub.remove(character.secondaryHandModel);
            modelSlot.model.sub.remove(character.secondaryHandModel);
            character.secondaryHandModel = null;
        }

        for (int int0 = 0; int0 < modelSlot.attachedModels.size(); int0++) {
            ModelInstance modelInstance0 = modelSlot.attachedModels.get(int0);
            this.resetModelInstanceRecurse(modelInstance0, modelSlot);
            modelSlot.sub.remove(modelInstance0);
            modelSlot.model.sub.remove(modelInstance0);
        }

        modelSlot.attachedModels.clear();

        for (int int1 = 0; int1 < character.getAttachedItems().size(); int1++) {
            AttachedItem attachedItem = character.getAttachedItems().get(int1);
            String string0 = attachedItem.getItem().getStaticModel();
            if (!StringUtils.isNullOrWhitespace(string0)) {
                String string1 = character.getAttachedItems().getGroup().getLocation(attachedItem.getLocation()).getAttachmentName();
                ModelInstance modelInstance1 = this.addStatic(modelSlot.model, string0, string1, string1);
                if (modelInstance1 != null) {
                    modelInstance1.setOwner(modelSlot);
                    modelSlot.sub.add(modelInstance1);
                    HandWeapon weapon = Type.tryCastTo(attachedItem.getItem(), HandWeapon.class);
                    if (weapon != null) {
                        this.addWeaponPartModels(modelSlot, weapon, modelInstance1);
                        if (!Core.getInstance().getOptionSimpleWeaponTextures()) {
                            ModelInstanceTextureInitializer modelInstanceTextureInitializer = ModelInstanceTextureInitializer.alloc();
                            modelInstanceTextureInitializer.init(modelInstance1, weapon);
                            modelInstance1.setTextureInitializer(modelInstanceTextureInitializer);
                        }
                    }

                    modelSlot.attachedModels.add(modelInstance1);
                }
            }
        }

        if (character instanceof IsoZombie) {
        }

        InventoryItem item0 = character.getPrimaryHandItem();
        InventoryItem item1 = character.getSecondaryHandItem();
        if (character.isHideWeaponModel()) {
            item0 = null;
            item1 = null;
        }

        if (character instanceof IsoPlayer && character.forceNullOverride) {
            item0 = null;
            item1 = null;
            character.forceNullOverride = false;
        }

        boolean boolean0 = false;
        BaseAction baseAction = character.getCharacterActions().isEmpty() ? null : character.getCharacterActions().get(0);
        if (baseAction != null && baseAction.overrideHandModels) {
            boolean0 = true;
            item0 = null;
            if (baseAction.getPrimaryHandItem() != null) {
                item0 = baseAction.getPrimaryHandItem();
            } else if (baseAction.getPrimaryHandMdl() != null) {
                character.primaryHandModel = this.addStatic(modelSlot, baseAction.getPrimaryHandMdl(), "Bip01_Prop1");
            }

            item1 = null;
            if (baseAction.getSecondaryHandItem() != null) {
                item1 = baseAction.getSecondaryHandItem();
            } else if (baseAction.getSecondaryHandMdl() != null) {
                character.secondaryHandModel = this.addStatic(modelSlot, baseAction.getSecondaryHandMdl(), "Bip01_Prop2");
            }
        }

        if (!StringUtils.isNullOrEmpty(character.overridePrimaryHandModel)) {
            boolean0 = true;
            character.primaryHandModel = this.addStatic(modelSlot, character.overridePrimaryHandModel, "Bip01_Prop1");
        }

        if (!StringUtils.isNullOrEmpty(character.overrideSecondaryHandModel)) {
            boolean0 = true;
            character.secondaryHandModel = this.addStatic(modelSlot, character.overrideSecondaryHandModel, "Bip01_Prop2");
        }

        if (item0 != null) {
            ItemReplacement itemReplacement0 = item0.getItemReplacementPrimaryHand();
            character.primaryHandModel = this.addEquippedModelInstance(character, modelSlot, item0, "Bip01_Prop1", itemReplacement0, boolean0);
        }

        if (item1 != null && item0 != item1) {
            ItemReplacement itemReplacement1 = item1.getItemReplacementSecondHand();
            character.secondaryHandModel = this.addEquippedModelInstance(character, modelSlot, item1, "Bip01_Prop2", itemReplacement1, boolean0);
        }
    }

    private ModelInstance addEquippedModelInstance(
        IsoGameCharacter character, ModelManager.ModelSlot modelSlot, InventoryItem item, String string1, ItemReplacement itemReplacement, boolean boolean0
    ) {
        HandWeapon weapon = Type.tryCastTo(item, HandWeapon.class);
        if (weapon != null) {
            String string0 = weapon.getStaticModel();
            ModelInstance modelInstance = this.addStatic(modelSlot, string0, string1);
            this.addWeaponPartModels(modelSlot, weapon, modelInstance);
            if (Core.getInstance().getOptionSimpleWeaponTextures()) {
                return modelInstance;
            } else {
                ModelInstanceTextureInitializer modelInstanceTextureInitializer = ModelInstanceTextureInitializer.alloc();
                modelInstanceTextureInitializer.init(modelInstance, weapon);
                modelInstance.setTextureInitializer(modelInstanceTextureInitializer);
                return modelInstance;
            }
        } else {
            if (item != null) {
                if (itemReplacement != null
                    && !StringUtils.isNullOrEmpty(itemReplacement.maskVariableValue)
                    && (itemReplacement.clothingItem != null || !StringUtils.isNullOrWhitespace(item.getStaticModel()))) {
                    return this.addMaskingModel(
                        modelSlot, character, item, itemReplacement, itemReplacement.maskVariableValue, itemReplacement.attachment, string1
                    );
                }

                if (boolean0 && !StringUtils.isNullOrWhitespace(item.getStaticModel())) {
                    return this.addStatic(modelSlot, item.getStaticModel(), string1);
                }
            }

            return null;
        }
    }

    private ModelInstance addMaskingModel(
        ModelManager.ModelSlot modelSlot,
        IsoGameCharacter character,
        InventoryItem item,
        ItemReplacement itemReplacement,
        String string3,
        String string1,
        String string2
    ) {
        ModelInstance modelInstance = null;
        ItemVisual itemVisual = item.getVisual();
        if (itemReplacement.clothingItem != null && itemVisual != null) {
            modelInstance = PopTemplateManager.instance.addClothingItem(character, modelSlot, itemVisual, itemReplacement.clothingItem);
        } else {
            if (StringUtils.isNullOrWhitespace(item.getStaticModel())) {
                return null;
            }

            String string0 = null;
            if (itemVisual != null && item.getClothingItem() != null) {
                string0 = item.getClothingItem().getTextureChoices().get(itemVisual.getTextureChoice());
            }

            if (!StringUtils.isNullOrEmpty(string1)) {
                modelInstance = this.addStaticForcedTex(modelSlot.model, item.getStaticModel(), string1, string1, string0);
            } else {
                modelInstance = this.addStaticForcedTex(modelSlot, item.getStaticModel(), string2, string0);
            }

            modelInstance.maskVariableValue = string3;
            if (itemVisual != null) {
                modelInstance.tintR = itemVisual.m_Tint.r;
                modelInstance.tintG = itemVisual.m_Tint.g;
                modelInstance.tintB = itemVisual.m_Tint.b;
            }
        }

        if (!StringUtils.isNullOrEmpty(string3)) {
            character.setVariable(itemReplacement.maskVariableName, string3);
            character.bUpdateEquippedTextures = true;
        }

        return modelInstance;
    }

    private void addWeaponPartModels(ModelManager.ModelSlot modelSlot, HandWeapon weapon, ModelInstance modelInstance1) {
        ArrayList arrayList0 = weapon.getModelWeaponPart();
        if (arrayList0 != null) {
            ArrayList arrayList1 = weapon.getAllWeaponParts(this.m_tempWeaponPartList);

            for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                WeaponPart weaponPart = (WeaponPart)arrayList1.get(int0);

                for (int int1 = 0; int1 < arrayList0.size(); int1++) {
                    ModelWeaponPart modelWeaponPart = (ModelWeaponPart)arrayList0.get(int1);
                    if (weaponPart.getFullType().equals(modelWeaponPart.partType)) {
                        ModelInstance modelInstance0 = this.addStatic(
                            modelInstance1, modelWeaponPart.modelName, modelWeaponPart.attachmentNameSelf, modelWeaponPart.attachmentParent
                        );
                        modelInstance0.setOwner(modelSlot);
                    }
                }
            }
        }
    }

    public void resetModelInstance(ModelInstance modelInstance, Object expectedOwner) {
        if (modelInstance != null) {
            modelInstance.clearOwner(expectedOwner);
            if (modelInstance.isRendering()) {
                modelInstance.bResetAfterRender = true;
            } else {
                if (modelInstance instanceof VehicleModelInstance) {
                    return;
                }

                if (modelInstance instanceof VehicleSubModelInstance) {
                    return;
                }

                modelInstance.reset();
                this.m_modelInstancePool.release(modelInstance);
            }
        }
    }

    public void resetModelInstanceRecurse(ModelInstance modelInstance, Object expectedOwner) {
        if (modelInstance != null) {
            this.resetModelInstancesRecurse(modelInstance.sub, expectedOwner);
            this.resetModelInstance(modelInstance, expectedOwner);
        }
    }

    public void resetModelInstancesRecurse(ArrayList<ModelInstance> modelInstances, Object expectedOwner) {
        for (int int0 = 0; int0 < modelInstances.size(); int0++) {
            ModelInstance modelInstance = (ModelInstance)modelInstances.get(int0);
            this.resetModelInstance(modelInstance, expectedOwner);
        }
    }

    public void derefModelInstance(ModelInstance modelInstance) {
        if (modelInstance != null) {
            assert modelInstance.renderRefCount > 0;

            modelInstance.renderRefCount--;
            if (modelInstance.bResetAfterRender && !modelInstance.isRendering()) {
                assert modelInstance.getOwner() == null;

                if (modelInstance instanceof VehicleModelInstance) {
                    return;
                }

                if (modelInstance instanceof VehicleSubModelInstance) {
                    return;
                }

                modelInstance.reset();
                this.m_modelInstancePool.release(modelInstance);
            }
        }
    }

    public void derefModelInstances(ArrayList<ModelInstance> modelInstances) {
        for (int int0 = 0; int0 < modelInstances.size(); int0++) {
            ModelInstance modelInstance = (ModelInstance)modelInstances.get(int0);
            this.derefModelInstance(modelInstance);
        }
    }

    private void DoCharacterModelParts(IsoGameCharacter character, ModelManager.ModelSlot modelSlot) {
        if (modelSlot.isRendering()) {
            boolean boolean0 = false;
        }

        if (DebugLog.isEnabled(DebugType.Clothing)) {
            DebugLog.Clothing.debugln("Char: " + character + " Slot: " + modelSlot);
        }

        modelSlot.sub.clear();
        PopTemplateManager.instance.populateCharacterModelSlot(character, modelSlot);
        this.DoCharacterModelEquipped(character, modelSlot);
    }

    public void update() {
        for (int int0 = 0; int0 < this.ToResetNextFrame.size(); int0++) {
            IsoGameCharacter character0 = this.ToResetNextFrame.get(int0);
            this.Reset(character0);
        }

        this.ToResetNextFrame.clear();

        for (int int1 = 0; int1 < this.ToResetEquippedNextFrame.size(); int1++) {
            IsoGameCharacter character1 = this.ToResetEquippedNextFrame.get(int1);
            this.ResetCharacterEquippedHands(character1);
        }

        this.ToResetEquippedNextFrame.clear();

        for (int int2 = 0; int2 < this.ToRemove.size(); int2++) {
            IsoGameCharacter character2 = this.ToRemove.get(int2);
            this.DoRemove(character2);
        }

        this.ToRemove.clear();

        for (int int3 = 0; int3 < this.m_resetAfterRender.size(); int3++) {
            ModelManager.ModelSlot modelSlot = this.m_resetAfterRender.get(int3);
            if (!modelSlot.isRendering()) {
                modelSlot.reset();
                this.m_resetAfterRender.remove(int3--);
            }
        }

        this.m_lights.clear();
        if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
            this.m_lights.addAll(IsoWorld.instance.CurrentCell.getLamppostPositions());
            ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();

            for (int int4 = 0; int4 < arrayList.size(); int4++) {
                BaseVehicle vehicle = (BaseVehicle)arrayList.get(int4);
                if (vehicle.sprite != null && vehicle.sprite.hasActiveModel()) {
                    ((VehicleModelInstance)vehicle.sprite.modelSlot.model).UpdateLights();
                }
            }
        }

        this.m_freeLights.addAll(this.m_torchLights);
        this.m_torchLights.clear();
        this.m_torches.clear();
        LightingJNI.getTorches(this.m_torches);

        for (int int5 = 0; int5 < this.m_torches.size(); int5++) {
            IsoGameCharacter.TorchInfo torchInfo = this.m_torches.get(int5);
            IsoLightSource lightSource = this.m_freeLights.isEmpty() ? new IsoLightSource(0, 0, 0, 1.0F, 1.0F, 1.0F, 1) : this.m_freeLights.pop();
            lightSource.x = (int)torchInfo.x;
            lightSource.y = (int)torchInfo.y;
            lightSource.z = (int)torchInfo.z;
            lightSource.r = 1.0F;
            lightSource.g = 0.85F;
            lightSource.b = 0.6F;
            lightSource.radius = (int)Math.ceil(torchInfo.dist);
            this.m_torchLights.add(lightSource);
        }
    }

    private ModelManager.ModelSlot addNewSlot(IsoGameCharacter character) {
        ModelManager.ModelSlot modelSlot = new ModelManager.ModelSlot(this.m_modelSlots.size(), null, character);
        this.m_modelSlots.add(modelSlot);
        return modelSlot;
    }

    public ModelManager.ModelSlot getSlot(IsoGameCharacter chr) {
        for (int int0 = 0; int0 < this.m_modelSlots.size(); int0++) {
            ModelManager.ModelSlot modelSlot = this.m_modelSlots.get(int0);
            if (!modelSlot.bRemove && !modelSlot.isRendering() && !modelSlot.active) {
                return modelSlot;
            }
        }

        return this.addNewSlot(chr);
    }

    private boolean DoRemove(IsoGameCharacter character) {
        if (!this.m_contains.contains(character)) {
            return false;
        } else {
            boolean boolean0 = false;

            for (int int0 = 0; int0 < this.m_modelSlots.size(); int0++) {
                ModelManager.ModelSlot modelSlot = this.m_modelSlots.get(int0);
                if (modelSlot.character == character) {
                    character.legsSprite.modelSlot = null;
                    this.m_contains.remove(character);
                    if (!character.isSceneCulled()) {
                        character.onCullStateChanged(this, true);
                    }

                    if (!this.m_resetAfterRender.contains(modelSlot)) {
                        this.m_resetAfterRender.add(modelSlot);
                    }

                    boolean0 = true;
                }
            }

            return boolean0;
        }
    }

    public void Remove(IsoGameCharacter chr) {
        if (!chr.isSceneCulled()) {
            if (!this.ToRemove.contains(chr)) {
                chr.legsSprite.modelSlot.bRemove = true;
                this.ToRemove.add(chr);
                chr.onCullStateChanged(this, true);
            } else if (this.ContainsChar(chr)) {
                throw new IllegalStateException("IsoGameCharacter.isSceneCulled() = true inconsistent with ModelManager.ContainsChar() = true");
            }
        }
    }

    public void Remove(BaseVehicle vehicle) {
        if (vehicle.sprite != null && vehicle.sprite.modelSlot != null) {
            ModelManager.ModelSlot modelSlot = vehicle.sprite.modelSlot;
            if (!this.m_resetAfterRender.contains(modelSlot)) {
                this.m_resetAfterRender.add(modelSlot);
            }

            vehicle.sprite.modelSlot = null;
        }
    }

    public void ResetNextFrame(IsoGameCharacter isoGameCharacter) {
        if (!this.ToResetNextFrame.contains(isoGameCharacter)) {
            this.ToResetNextFrame.add(isoGameCharacter);
        }
    }

    public void ResetEquippedNextFrame(IsoGameCharacter isoGameCharacter) {
        if (!this.ToResetEquippedNextFrame.contains(isoGameCharacter)) {
            this.ToResetEquippedNextFrame.add(isoGameCharacter);
        }
    }

    public void Reset() {
        RenderThread.invokeOnRenderContext(() -> {
            for (IsoGameCharacter character0 : this.ToRemove) {
                this.DoRemove(character0);
            }

            this.ToRemove.clear();

            try {
                if (!this.m_contains.isEmpty()) {
                    IsoGameCharacter[] characters = this.m_contains.toArray(new IsoGameCharacter[0]);

                    for (IsoGameCharacter character1 : characters) {
                        this.DoRemove(character1);
                    }
                }

                this.m_modelSlots.clear();
            } catch (Exception exception) {
                DebugLog.Animation.error("Exception thrown removing Models.");
                exception.printStackTrace();
            }
        });
        this.m_lights.clear();
        this.m_lightsTemp.clear();
    }

    public void getClosestThreeLights(IsoMovingObject movingObject, IsoLightSource[] lightSources) {
        this.m_lightsTemp.clear();

        for (IsoLightSource lightSource : this.m_lights) {
            if (lightSource.bActive
                && lightSource.life != 0
                && (lightSource.localToBuilding == null || movingObject.getCurrentBuilding() == lightSource.localToBuilding)
                && !(IsoUtils.DistanceTo(movingObject.x, movingObject.y, lightSource.x + 0.5F, lightSource.y + 0.5F) >= lightSource.radius)
                && LosUtil.lineClear(
                        IsoWorld.instance.CurrentCell,
                        (int)movingObject.x,
                        (int)movingObject.y,
                        (int)movingObject.z,
                        lightSource.x,
                        lightSource.y,
                        lightSource.z,
                        false
                    )
                    != LosUtil.TestResults.Blocked) {
                this.m_lightsTemp.add(lightSource);
            }
        }

        if (movingObject instanceof BaseVehicle) {
            for (int int0 = 0; int0 < this.m_torches.size(); int0++) {
                IsoGameCharacter.TorchInfo torchInfo = this.m_torches.get(int0);
                if (!(IsoUtils.DistanceTo(movingObject.x, movingObject.y, torchInfo.x, torchInfo.y) >= torchInfo.dist)
                    && LosUtil.lineClear(
                            IsoWorld.instance.CurrentCell,
                            (int)movingObject.x,
                            (int)movingObject.y,
                            (int)movingObject.z,
                            (int)torchInfo.x,
                            (int)torchInfo.y,
                            (int)torchInfo.z,
                            false
                        )
                        != LosUtil.TestResults.Blocked) {
                    if (torchInfo.bCone) {
                        Vector2 vector0 = this.m_tempVec2;
                        vector0.x = torchInfo.x - movingObject.x;
                        vector0.y = torchInfo.y - movingObject.y;
                        vector0.normalize();
                        Vector2 vector1 = this.m_tempVec2_2;
                        vector1.x = torchInfo.angleX;
                        vector1.y = torchInfo.angleY;
                        vector1.normalize();
                        float float0 = vector0.dot(vector1);
                        if (float0 >= -0.92F) {
                            continue;
                        }
                    }

                    this.m_lightsTemp.add(this.m_torchLights.get(int0));
                }
            }
        }

        PZArrayUtil.sort(this.m_lightsTemp, Lambda.comparator(movingObject, (lightSource0, lightSource1, movingObjectx) -> {
            float float0x = movingObjectx.DistTo(lightSource0.x, lightSource0.y);
            float float1 = movingObjectx.DistTo(lightSource1.x, lightSource1.y);
            if (float0x > float1) {
                return 1;
            } else {
                return float0x < float1 ? -1 : 0;
            }
        }));
        lightSources[0] = lightSources[1] = lightSources[2] = null;
        if (this.m_lightsTemp.size() > 0) {
            lightSources[0] = this.m_lightsTemp.get(0);
        }

        if (this.m_lightsTemp.size() > 1) {
            lightSources[1] = this.m_lightsTemp.get(1);
        }

        if (this.m_lightsTemp.size() > 2) {
            lightSources[2] = this.m_lightsTemp.get(2);
        }
    }

    public void addVehicle(BaseVehicle vehicle) {
        if (this.m_bCreated) {
            if (!GameServer.bServer || ServerGUI.isCreated()) {
                if (vehicle != null && vehicle.getScript() != null) {
                    VehicleScript vehicleScript = vehicle.getScript();
                    String string = vehicle.getScript().getModel().file;
                    Model model0 = this.getLoadedModel(string);
                    if (model0 == null) {
                        DebugLog.Animation.error("Failed to find vehicle model: %s", string);
                    } else {
                        if (DebugLog.isEnabled(DebugType.Animation)) {
                            DebugLog.Animation.debugln("%s", string);
                        }

                        VehicleModelInstance vehicleModelInstance = new VehicleModelInstance();
                        vehicleModelInstance.init(model0, null, vehicle.getAnimationPlayer());
                        vehicleModelInstance.applyModelScriptScale(string);
                        vehicle.getSkin();
                        VehicleScript.Skin skin = vehicleScript.getTextures();
                        if (vehicle.getSkinIndex() >= 0 && vehicle.getSkinIndex() < vehicleScript.getSkinCount()) {
                            skin = vehicleScript.getSkin(vehicle.getSkinIndex());
                        }

                        vehicleModelInstance.LoadTexture(skin.texture);
                        vehicleModelInstance.tex = skin.textureData;
                        vehicleModelInstance.textureMask = skin.textureDataMask;
                        vehicleModelInstance.textureDamage1Overlay = skin.textureDataDamage1Overlay;
                        vehicleModelInstance.textureDamage1Shell = skin.textureDataDamage1Shell;
                        vehicleModelInstance.textureDamage2Overlay = skin.textureDataDamage2Overlay;
                        vehicleModelInstance.textureDamage2Shell = skin.textureDataDamage2Shell;
                        vehicleModelInstance.textureLights = skin.textureDataLights;
                        vehicleModelInstance.textureRust = skin.textureDataRust;
                        if (vehicleModelInstance.tex != null) {
                            vehicleModelInstance.tex.bindAlways = true;
                        } else {
                            DebugLog.Animation.error("texture not found:", vehicle.getSkin());
                        }

                        ModelManager.ModelSlot modelSlot = this.getSlot(null);
                        modelSlot.model = vehicleModelInstance;
                        vehicleModelInstance.setOwner(modelSlot);
                        vehicleModelInstance.object = vehicle;
                        modelSlot.sub.clear();

                        for (int int0 = 0; int0 < vehicle.models.size(); int0++) {
                            BaseVehicle.ModelInfo modelInfo = vehicle.models.get(int0);
                            Model model1 = this.getLoadedModel(modelInfo.scriptModel.file);
                            if (model1 == null) {
                                DebugLog.Animation.error("vehicle.models[%d] not found: %s", int0, modelInfo.scriptModel.file);
                            } else {
                                VehicleSubModelInstance vehicleSubModelInstance = new VehicleSubModelInstance();
                                vehicleSubModelInstance.init(model1, null, modelInfo.getAnimationPlayer());
                                vehicleSubModelInstance.setOwner(modelSlot);
                                vehicleSubModelInstance.applyModelScriptScale(modelInfo.scriptModel.file);
                                vehicleSubModelInstance.object = vehicle;
                                vehicleSubModelInstance.parent = vehicleModelInstance;
                                vehicleModelInstance.sub.add(vehicleSubModelInstance);
                                vehicleSubModelInstance.modelInfo = modelInfo;
                                if (vehicleSubModelInstance.tex == null) {
                                    vehicleSubModelInstance.tex = vehicleModelInstance.tex;
                                }

                                modelSlot.sub.add(vehicleSubModelInstance);
                                modelInfo.modelInstance = vehicleSubModelInstance;
                            }
                        }

                        modelSlot.active = true;
                        vehicle.sprite.modelSlot = modelSlot;
                    }
                }
            }
        }
    }

    public ModelInstance addStatic(ModelManager.ModelSlot slot, String meshName, String texName, String boneName, String shaderName) {
        ModelInstance modelInstance = this.newStaticInstance(slot, meshName, texName, boneName, shaderName);
        if (modelInstance == null) {
            return null;
        } else {
            slot.sub.add(modelInstance);
            modelInstance.setOwner(slot);
            slot.model.sub.add(modelInstance);
            return modelInstance;
        }
    }

    public ModelInstance newStaticInstance(ModelManager.ModelSlot slot, String meshName, String texName, String boneName, String shaderName) {
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.debugln("Adding Static Model:" + meshName);
        }

        Model model = this.tryGetLoadedModel(meshName, texName, true, shaderName, false);
        if (model == null && meshName != null) {
            this.loadStaticModel(meshName, texName, shaderName);
            model = this.getLoadedModel(meshName, texName, true, shaderName);
            if (model == null) {
                if (DebugLog.isEnabled(DebugType.Animation)) {
                    DebugLog.Animation.error("Model not found. model:" + meshName + " tex:" + texName);
                }

                return null;
            }
        }

        if (meshName == null) {
            model = this.tryGetLoadedModel("vehicles_wheel02", "vehicles/vehicle_wheel02", true, "vehiclewheel", false);
        }

        ModelInstance modelInstance = this.newInstance(model, slot.character, slot.model.AnimPlayer);
        modelInstance.parent = slot.model;
        if (slot.model.AnimPlayer != null) {
            modelInstance.parentBone = slot.model.AnimPlayer.getSkinningBoneIndex(boneName, modelInstance.parentBone);
            modelInstance.parentBoneName = boneName;
        }

        modelInstance.AnimPlayer = slot.model.AnimPlayer;
        return modelInstance;
    }

    private ModelInstance addStatic(ModelManager.ModelSlot modelSlot, String string0, String string1) {
        return this.addStaticForcedTex(modelSlot, string0, string1, null);
    }

    private ModelInstance addStaticForcedTex(ModelManager.ModelSlot modelSlot, String string1, String string5, String string4) {
        String string0 = ScriptManager.getItemName(string1);
        String string2 = ScriptManager.getItemName(string1);
        String string3 = null;
        ModelManager.ModelMetaData modelMetaDatax = modelMetaData.get(string1);
        if (modelMetaDatax != null) {
            if (!StringUtils.isNullOrWhitespace(modelMetaDatax.meshName)) {
                string0 = modelMetaDatax.meshName;
            }

            if (!StringUtils.isNullOrWhitespace(modelMetaDatax.textureName)) {
                string2 = modelMetaDatax.textureName;
            }

            if (!StringUtils.isNullOrWhitespace(modelMetaDatax.shaderName)) {
                string3 = modelMetaDatax.shaderName;
            }
        }

        if (!StringUtils.isNullOrEmpty(string4)) {
            string2 = string4;
        }

        ModelScript modelScript = ScriptManager.instance.getModelScript(string1);
        if (modelScript != null) {
            string0 = modelScript.getMeshName();
            string2 = modelScript.getTextureName();
            string3 = modelScript.getShaderName();
            ModelInstance modelInstance = this.addStatic(modelSlot, string0, string2, string5, string3);
            if (modelInstance != null) {
                modelInstance.applyModelScriptScale(string1);
            }

            return modelInstance;
        } else {
            return this.addStatic(modelSlot, string0, string2, string5, string3);
        }
    }

    public ModelInstance addStatic(ModelInstance parentInst, String modelName, String attachNameSelf, String attachNameParent) {
        return this.addStaticForcedTex(parentInst, modelName, attachNameSelf, attachNameParent, null);
    }

    public ModelInstance addStaticForcedTex(ModelInstance parentInst, String modelName, String attachNameSelf, String attachNameParent, String forcedTex) {
        String string0 = ScriptManager.getItemName(modelName);
        String string1 = ScriptManager.getItemName(modelName);
        String string2 = null;
        ModelScript modelScript = ScriptManager.instance.getModelScript(modelName);
        if (modelScript != null) {
            string0 = modelScript.getMeshName();
            string1 = modelScript.getTextureName();
            string2 = modelScript.getShaderName();
        }

        if (!StringUtils.isNullOrEmpty(forcedTex)) {
            string1 = forcedTex;
        }

        Model model = this.tryGetLoadedModel(string0, string1, true, string2, false);
        if (model == null && string0 != null) {
            this.loadStaticModel(string0, string1, string2);
            model = this.getLoadedModel(string0, string1, true, string2);
            if (model == null) {
                if (DebugLog.isEnabled(DebugType.Animation)) {
                    DebugLog.Animation.error("Model not found. model:" + string0 + " tex:" + string1);
                }

                return null;
            }
        }

        if (string0 == null) {
            model = this.tryGetLoadedModel("vehicles_wheel02", "vehicles/vehicle_wheel02", true, "vehiclewheel", false);
        }

        if (model == null) {
            return null;
        } else {
            ModelInstance modelInstance = this.m_modelInstancePool.alloc();
            if (parentInst != null) {
                modelInstance.init(model, parentInst.character, parentInst.AnimPlayer);
                modelInstance.parent = parentInst;
                parentInst.sub.add(modelInstance);
            } else {
                modelInstance.init(model, null, null);
            }

            if (modelScript != null) {
                modelInstance.applyModelScriptScale(modelName);
            }

            modelInstance.attachmentNameSelf = attachNameSelf;
            modelInstance.attachmentNameParent = attachNameParent;
            return modelInstance;
        }
    }

    private String modifyShaderName(String string) {
        if ((StringUtils.equals(string, "vehicle") || StringUtils.equals(string, "vehicle_multiuv") || StringUtils.equals(string, "vehicle_norandom_multiuv"))
            && !Core.getInstance().getPerfReflectionsOnLoad()) {
            string = string + "_noreflect";
        }

        return string;
    }

    private Model loadModelInternal(String string1, String string2, String string0, ModelMesh modelMesh, boolean boolean0) {
        string0 = this.modifyShaderName(string0);
        Model.ModelAssetParams modelAssetParams = new Model.ModelAssetParams();
        modelAssetParams.animationsModel = modelMesh;
        modelAssetParams.bStatic = boolean0;
        modelAssetParams.meshName = string1;
        modelAssetParams.shaderName = string0;
        modelAssetParams.textureName = string2;
        if (string0 != null && StringUtils.startsWithIgnoreCase(string0, "vehicle")) {
            modelAssetParams.textureFlags = TextureID.bUseCompression ? 4 : 0;
            modelAssetParams.textureFlags |= 256;
        } else {
            modelAssetParams.textureFlags = this.getTextureFlags();
        }

        String string3 = this.createModelKey(string1, string2, boolean0, string0);
        Model model = (Model)ModelAssetManager.instance.load(new AssetPath(string3), modelAssetParams);
        if (model != null) {
            this.putLoadedModel(string1, string2, boolean0, string0, model);
        }

        return model;
    }

    public int getTextureFlags() {
        int int0 = TextureID.bUseCompression ? 4 : 0;
        if (Core.OptionModelTextureMipmaps) {
        }

        return int0 | 128;
    }

    public void setModelMetaData(String meshName, String texName, String shaderName, boolean bStatic) {
        this.setModelMetaData(meshName, meshName, texName, shaderName, bStatic);
    }

    public void setModelMetaData(String modelId, String meshName, String texName, String shaderName, boolean bStatic) {
        ModelManager.ModelMetaData modelMetaDatax = new ModelManager.ModelMetaData();
        modelMetaDatax.meshName = meshName;
        modelMetaDatax.textureName = texName;
        modelMetaDatax.shaderName = shaderName;
        modelMetaDatax.bStatic = bStatic;
        modelMetaData.put(modelId, modelMetaDatax);
    }

    public Model loadStaticModel(String meshName, String tex, String shaderName) {
        String string = this.modifyShaderName(shaderName);
        return this.loadModelInternal(meshName, tex, string, null, true);
    }

    private Model loadModel(String string0, String string1, ModelMesh modelMesh) {
        return this.loadModelInternal(string0, string1, "basicEffect", modelMesh, false);
    }

    public Model getLoadedModel(String meshName) {
        ModelScript modelScript = ScriptManager.instance.getModelScript(meshName);
        if (modelScript != null) {
            if (modelScript.loadedModel != null) {
                return modelScript.loadedModel;
            } else {
                modelScript.shaderName = this.modifyShaderName(modelScript.shaderName);
                Model model0 = this.tryGetLoadedModel(
                    modelScript.getMeshName(), modelScript.getTextureName(), modelScript.bStatic, modelScript.getShaderName(), false
                );
                if (model0 != null) {
                    modelScript.loadedModel = model0;
                    return model0;
                } else {
                    AnimationsMesh animationsMesh = modelScript.animationsMesh == null
                        ? null
                        : ScriptManager.instance.getAnimationsMesh(modelScript.animationsMesh);
                    ModelMesh modelMesh = animationsMesh == null ? null : animationsMesh.modelMesh;
                    model0 = modelScript.bStatic
                        ? this.loadModelInternal(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.getShaderName(), null, true)
                        : this.loadModelInternal(modelScript.getMeshName(), modelScript.getTextureName(), modelScript.getShaderName(), modelMesh, false);
                    modelScript.loadedModel = model0;
                    return model0;
                }
            }
        } else {
            ModelManager.ModelMetaData modelMetaDatax = modelMetaData.get(meshName);
            if (modelMetaDatax != null) {
                modelMetaDatax.shaderName = this.modifyShaderName(modelMetaDatax.shaderName);
                Model model1 = this.tryGetLoadedModel(
                    modelMetaDatax.meshName, modelMetaDatax.textureName, modelMetaDatax.bStatic, modelMetaDatax.shaderName, false
                );
                if (model1 != null) {
                    return model1;
                } else {
                    return modelMetaDatax.bStatic
                        ? this.loadStaticModel(modelMetaDatax.meshName, modelMetaDatax.textureName, modelMetaDatax.shaderName)
                        : this.loadModel(modelMetaDatax.meshName, modelMetaDatax.textureName, this.m_animModel);
                }
            } else {
                Model model2 = this.tryGetLoadedModel(meshName, null, false, null, false);
                if (model2 != null) {
                    return model2;
                } else {
                    String string0 = meshName.toLowerCase().trim();

                    for (Entry entry : this.m_modelMap.entrySet()) {
                        String string1 = (String)entry.getKey();
                        if (string1.startsWith(string0)) {
                            Model model3 = (Model)entry.getValue();
                            if (model3 != null && (string1.length() == string0.length() || string1.charAt(string0.length()) == '&')) {
                                model2 = model3;
                                break;
                            }
                        }
                    }

                    if (model2 == null && DebugLog.isEnabled(DebugType.Animation)) {
                        DebugLog.Animation.error("ModelManager.getLoadedModel> Model missing for key=\"" + string0 + "\"");
                    }

                    return model2;
                }
            }
        }
    }

    public Model getLoadedModel(String meshName, String tex, boolean isStatic, String shaderName) {
        return this.tryGetLoadedModel(meshName, tex, isStatic, shaderName, true);
    }

    public Model tryGetLoadedModel(String meshName, String tex, boolean isStatic, String shaderName, boolean logError) {
        String string = this.createModelKey(meshName, tex, isStatic, shaderName);
        if (string == null) {
            return null;
        } else {
            Model model = this.m_modelMap.get(string);
            if (model == null && logError && DebugLog.isEnabled(DebugType.Animation)) {
                DebugLog.Animation.error("ModelManager.getLoadedModel> Model missing for key=\"" + string + "\"");
            }

            return model;
        }
    }

    public void putLoadedModel(String name, String tex, boolean isStatic, String shaderName, Model model) {
        String string = this.createModelKey(name, tex, isStatic, shaderName);
        if (string != null) {
            Model _model = this.m_modelMap.get(string);
            if (_model != model) {
                if (_model != null) {
                    DebugLog.Animation.debugln("Override key=\"%s\" old=%s new=%s", string, _model, model);
                } else {
                    DebugLog.Animation.debugln("key=\"%s\" model=%s", string, model);
                }

                this.m_modelMap.put(string, model);
                model.Name = string;
            }
        }
    }

    private String createModelKey(String string0, String string2, boolean boolean0, String string1) {
        builder.delete(0, builder.length());
        if (string0 == null) {
            return null;
        } else {
            if (!toLowerKeyRoot.containsKey(string0)) {
                toLowerKeyRoot.put(string0, string0.toLowerCase(Locale.ENGLISH).trim());
            }

            builder.append(toLowerKeyRoot.get(string0));
            builder.append(amp);
            if (StringUtils.isNullOrWhitespace(string1)) {
                string1 = basicEffect;
            }

            builder.append(shaderEquals);
            if (!toLower.containsKey(string1)) {
                toLower.put(string1, string1.toLowerCase().trim());
            }

            builder.append(toLower.get(string1));
            if (!StringUtils.isNullOrWhitespace(string2)) {
                builder.append(texA);
                if (!toLowerTex.containsKey(string2)) {
                    toLowerTex.put(string2, string2.toLowerCase().trim());
                }

                builder.append(toLowerTex.get(string2));
            }

            if (boolean0) {
                builder.append(isStaticTrue);
            }

            return builder.toString();
        }
    }

    private String createModelKey2(String string0, String string3, boolean boolean0, String string1) {
        if (string0 == null) {
            return null;
        } else {
            if (StringUtils.isNullOrWhitespace(string1)) {
                string1 = "basicEffect";
            }

            String string2 = "shader=" + string1.toLowerCase().trim();
            if (!StringUtils.isNullOrWhitespace(string3)) {
                string2 = string2 + ";tex=" + string3.toLowerCase().trim();
            }

            if (boolean0) {
                string2 = string2 + ";isStatic=true";
            }

            String string4 = string0.toLowerCase(Locale.ENGLISH).trim();
            return string4 + "&" + string2;
        }
    }

    private AnimationAsset loadAnim(String string, ModelMesh modelMesh, ModelManager.ModAnimations modAnimations) {
        DebugLog.Animation.debugln("Adding asset to queue: %s", string);
        AnimationAsset.AnimationAssetParams animationAssetParams = new AnimationAsset.AnimationAssetParams();
        animationAssetParams.animationsMesh = modelMesh;
        AnimationAsset animationAsset = (AnimationAsset)AnimationAssetManager.instance.load(new AssetPath(string), animationAssetParams);
        animationAsset.skinningData = modelMesh.skinningData;
        this.putAnimationAsset(string, animationAsset, modAnimations);
        return animationAsset;
    }

    private void putAnimationAsset(String string1, AnimationAsset animationAsset1, ModelManager.ModAnimations modAnimations) {
        String string0 = string1.toLowerCase();
        AnimationAsset animationAsset0 = modAnimations.m_animationAssetMap.getOrDefault(string0, null);
        if (animationAsset0 != null) {
            DebugLog.Animation.debugln("Overwriting asset: %s", this.animAssetToString(animationAsset0));
            DebugLog.Animation.debugln("New asset        : %s", this.animAssetToString(animationAsset1));
            modAnimations.m_animationAssetList.remove(animationAsset0);
        }

        animationAsset1.modelManagerKey = string0;
        animationAsset1.modAnimations = modAnimations;
        modAnimations.m_animationAssetMap.put(string0, animationAsset1);
        modAnimations.m_animationAssetList.add(animationAsset1);
    }

    private String animAssetToString(AnimationAsset animationAsset) {
        if (animationAsset == null) {
            return "null";
        } else {
            AssetPath assetPath = animationAsset.getPath();
            return assetPath == null ? "null-path" : String.valueOf(assetPath.getPath());
        }
    }

    private AnimationAsset getAnimationAsset(String string1) {
        String string0 = string1.toLowerCase(Locale.ENGLISH);
        return this.m_animationAssets.get(string0);
    }

    private AnimationAsset getAnimationAssetRequired(String string) {
        AnimationAsset animationAsset = this.getAnimationAsset(string);
        if (animationAsset == null) {
            throw new NullPointerException("Required Animation Asset not found: " + string);
        } else {
            return animationAsset;
        }
    }

    public void addAnimationClip(String name, AnimationClip clip) {
        this.m_animModel.skinningData.AnimationClips.put(name, clip);
    }

    public AnimationClip getAnimationClip(String name) {
        return this.m_animModel.skinningData.AnimationClips.get(name);
    }

    public Collection<AnimationClip> getAllAnimationClips() {
        return this.m_animModel.skinningData.AnimationClips.values();
    }

    public ModelInstance newInstance(Model model, IsoGameCharacter chr, AnimationPlayer player) {
        if (model == null) {
            System.err.println("ModelManager.newInstance> Model is null.");
            return null;
        } else {
            ModelInstance modelInstance = this.m_modelInstancePool.alloc();
            modelInstance.init(model, chr, player);
            return modelInstance;
        }
    }

    public boolean isLoadingAnimations() {
        for (AnimationAsset animationAsset : this.m_animationAssets.values()) {
            if (animationAsset.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void reloadModelsMatching(String meshName) {
        meshName = meshName.toLowerCase(Locale.ENGLISH);

        for (String string : this.m_modelMap.keySet()) {
            if (string.contains(meshName)) {
                Model model = this.m_modelMap.get(string);
                if (!model.isEmpty()) {
                    DebugLog.General.printf("reloading model %s\n", string);
                    ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
                    meshAssetParams.animationsMesh = null;
                    if (model.Mesh.vb == null) {
                        meshAssetParams.bStatic = string.contains(";isStatic=true");
                    } else {
                        meshAssetParams.bStatic = model.Mesh.vb.bStatic;
                    }

                    MeshAssetManager.instance.reload(model.Mesh, meshAssetParams);
                }
            }
        }
    }

    public void loadModAnimations() {
        for (ModelManager.ModAnimations modAnimations0 : this.m_modAnimations.values()) {
            modAnimations0.setPriority(modAnimations0 == this.m_gameAnimations ? 0 : -1);
        }

        ArrayList arrayList0 = ScriptManager.instance.getAllAnimationsMeshes();
        ArrayList arrayList1 = ZomboidFileSystem.instance.getModIDs();

        for (int int0 = 0; int0 < arrayList1.size(); int0++) {
            String string0 = (String)arrayList1.get(int0);
            ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
            if (mod != null && mod.animsXFile.isDirectory()) {
                ModelManager.ModAnimations modAnimations1 = this.m_modAnimations.get(string0);
                if (modAnimations1 != null) {
                    modAnimations1.setPriority(int0 + 1);
                } else {
                    modAnimations1 = new ModelManager.ModAnimations(string0);
                    modAnimations1.setPriority(int0 + 1);
                    this.m_modAnimations.put(string0, modAnimations1);

                    for (AnimationsMesh animationsMesh : arrayList0) {
                        for (String string1 : animationsMesh.animationDirectories) {
                            if (animationsMesh.modelMesh.isReady()) {
                                File file = new File(mod.animsXFile, string1);
                                if (file.exists()) {
                                    this.loadAnimsFromDir(mod.baseFile.toURI(), mod.mediaFile.toURI(), file, animationsMesh.modelMesh, modAnimations1);
                                }
                            }
                        }
                    }

                    this.loadHumanAnimations(mod, modAnimations1);
                }
            }
        }

        this.setActiveAnimations();
    }

    void setActiveAnimations() {
        this.m_animationAssets.clear();

        for (AnimationsMesh animationsMesh : ScriptManager.instance.getAllAnimationsMeshes()) {
            if (animationsMesh.modelMesh.isReady()) {
                animationsMesh.modelMesh.skinningData.AnimationClips.clear();
            }
        }

        for (ModelManager.ModAnimations modAnimations : this.m_modAnimations.values()) {
            if (modAnimations.isActive()) {
                for (AnimationAsset animationAsset0 : modAnimations.m_animationAssetList) {
                    AnimationAsset animationAsset1 = this.m_animationAssets.get(animationAsset0.modelManagerKey);
                    if (animationAsset1 == null || animationAsset1 == animationAsset0 || animationAsset1.modAnimations.m_priority <= modAnimations.m_priority) {
                        this.m_animationAssets.put(animationAsset0.modelManagerKey, animationAsset0);
                        if (animationAsset0.isReady()) {
                            animationAsset0.skinningData.AnimationClips.putAll(animationAsset0.AnimationClips);
                        }
                    }
                }
            }
        }
    }

    public void animationAssetLoaded(AnimationAsset animationAsset) {
        if (animationAsset.modAnimations.isActive()) {
            AnimationAsset _animationAsset = this.m_animationAssets.get(animationAsset.modelManagerKey);
            if (_animationAsset == null
                || _animationAsset == animationAsset
                || _animationAsset.modAnimations.m_priority <= animationAsset.modAnimations.m_priority) {
                this.m_animationAssets.put(animationAsset.modelManagerKey, animationAsset);
                animationAsset.skinningData.AnimationClips.putAll(animationAsset.AnimationClips);
            }
        }
    }

    public void initAnimationMeshes(boolean bReloading) {
        ArrayList arrayList = ScriptManager.instance.getAllAnimationsMeshes();

        for (AnimationsMesh animationsMesh0 : arrayList) {
            ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
            meshAssetParams.bStatic = false;
            meshAssetParams.animationsMesh = null;
            animationsMesh0.modelMesh = (ModelMesh)MeshAssetManager.instance.getAssetTable().get(animationsMesh0.meshFile);
            if (animationsMesh0.modelMesh == null) {
                animationsMesh0.modelMesh = (ModelMesh)MeshAssetManager.instance.load(new AssetPath(animationsMesh0.meshFile), meshAssetParams);
            }

            animationsMesh0.modelMesh.m_animationsMesh = animationsMesh0.modelMesh;
        }

        if (!bReloading) {
            while (this.isLoadingAnimationMeshes()) {
                GameWindow.fileSystem.updateAsyncTransactions();

                try {
                    Thread.sleep(10L);
                } catch (InterruptedException interruptedException) {
                }

                if (!GameServer.bServer) {
                    Core.getInstance().StartFrame();
                    Core.getInstance().EndFrame();
                    Core.getInstance().StartFrameUI();
                    Core.getInstance().EndFrameUI();
                }
            }

            for (AnimationsMesh animationsMesh1 : arrayList) {
                for (String string : animationsMesh1.animationDirectories) {
                    if (animationsMesh1.modelMesh.isReady()) {
                        File file = new File(ZomboidFileSystem.instance.base, "media/anims_X/" + string);
                        if (file.exists()) {
                            this.loadAnimsFromDir("media/anims_X/" + string, animationsMesh1.modelMesh);
                        }
                    }
                }
            }
        }
    }

    private boolean isLoadingAnimationMeshes() {
        for (AnimationsMesh animationsMesh : ScriptManager.instance.getAllAnimationsMeshes()) {
            if (!animationsMesh.modelMesh.isFailure() && !animationsMesh.modelMesh.isReady()) {
                return true;
            }
        }

        return false;
    }

    private void loadHumanAnimations(ChooseGameInfo.Mod mod, ModelManager.ModAnimations modAnimations) {
        AnimationsMesh animationsMesh = ScriptManager.instance.getAnimationsMesh("Human");
        if (animationsMesh != null && animationsMesh.modelMesh != null && animationsMesh.modelMesh.isReady()) {
            File[] files = mod.animsXFile.listFiles();
            if (files != null) {
                URI uri = mod.animsXFile.toURI();

                for (File file : files) {
                    if (file.isDirectory()) {
                        if (!this.isAnimationsMeshDirectory(file.getName())) {
                            this.loadAnimsFromDir(mod.baseFile.toURI(), mod.mediaFile.toURI(), file, animationsMesh.modelMesh, modAnimations);
                        }
                    } else {
                        String string = ZomboidFileSystem.instance.getAnimName(uri, file);
                        this.loadAnim(string, animationsMesh.modelMesh, modAnimations);
                    }
                }
            }
        }
    }

    private boolean isAnimationsMeshDirectory(String string) {
        for (AnimationsMesh animationsMesh : ScriptManager.instance.getAllAnimationsMeshes()) {
            if (animationsMesh.animationDirectories.contains(string)) {
                return true;
            }
        }

        return false;
    }

    class AnimDirReloader implements PredicatedFileWatcher.IPredicatedFileWatcherCallback {
        URI m_baseURI;
        URI m_mediaURI;
        String m_dir;
        String m_dirSecondary;
        String m_dirAbsolute;
        String m_dirSecondaryAbsolute;
        ModelMesh m_animationsModel;
        ModelManager.ModAnimations m_modAnimations;

        public AnimDirReloader(URI uri0, URI uri1, String string, ModelMesh modelMesh, ModelManager.ModAnimations modAnimations) {
            string = ZomboidFileSystem.instance.getRelativeFile(uri0, string);
            this.m_baseURI = uri0;
            this.m_mediaURI = uri1;
            this.m_dir = ZomboidFileSystem.instance.normalizeFolderPath(string);
            this.m_dirAbsolute = ZomboidFileSystem.instance.normalizeFolderPath(new File(new File(this.m_baseURI), this.m_dir).toString());
            if (this.m_dir.contains("/anims/")) {
                this.m_dirSecondary = this.m_dir.replace("/anims/", "/anims_X/");
                this.m_dirSecondaryAbsolute = ZomboidFileSystem.instance
                    .normalizeFolderPath(new File(new File(this.m_baseURI), this.m_dirSecondary).toString());
            }

            this.m_animationsModel = modelMesh;
            this.m_modAnimations = modAnimations;
        }

        private boolean IsInDir(String string) {
            string = ZomboidFileSystem.instance.normalizeFolderPath(string);

            try {
                return this.m_dirSecondary == null
                    ? string.startsWith(this.m_dirAbsolute)
                    : string.startsWith(this.m_dirAbsolute) || string.startsWith(this.m_dirSecondaryAbsolute);
            } catch (Exception exception) {
                exception.printStackTrace();
                return false;
            }
        }

        @Override
        public void call(String string1) {
            String string0 = string1.toLowerCase();
            if (string0.endsWith(".fbx") || string0.endsWith(".x") || string0.endsWith(".txt")) {
                String string2 = ZomboidFileSystem.instance.getAnimName(this.m_mediaURI, new File(string1));
                AnimationAsset animationAsset = ModelManager.this.getAnimationAsset(string2);
                if (animationAsset != null) {
                    if (!animationAsset.isEmpty()) {
                        DebugLog.General.debugln("Reloading animation: %s", ModelManager.this.animAssetToString(animationAsset));

                        assert animationAsset.getRefCount() == 1;

                        AnimationAsset.AnimationAssetParams animationAssetParams = new AnimationAsset.AnimationAssetParams();
                        animationAssetParams.animationsMesh = this.m_animationsModel;
                        AnimationAssetManager.instance.reload(animationAsset, animationAssetParams);
                    }
                } else {
                    ModelManager.this.loadAnim(string2, this.m_animationsModel, this.m_modAnimations);
                }
            }
        }

        public PredicatedFileWatcher GetFileWatcher() {
            return new PredicatedFileWatcher(this.m_dir, this::IsInDir, this);
        }
    }

    public static final class ModAnimations {
        public final String m_modID;
        public final ArrayList<AnimationAsset> m_animationAssetList = new ArrayList<>();
        public final HashMap<String, AnimationAsset> m_animationAssetMap = new HashMap<>();
        public int m_priority;

        public ModAnimations(String modID) {
            this.m_modID = modID;
        }

        public void setPriority(int priority) {
            assert priority >= -1;

            this.m_priority = priority;
        }

        public boolean isActive() {
            return this.m_priority != -1;
        }
    }

    private static final class ModelMetaData {
        String meshName;
        String textureName;
        String shaderName;
        boolean bStatic;
    }

    public static class ModelSlot {
        public int ID;
        public ModelInstance model;
        public IsoGameCharacter character;
        public final ArrayList<ModelInstance> sub = new ArrayList<>();
        protected final AttachedModels attachedModels = new AttachedModels();
        public boolean active;
        public boolean bRemove;
        public int renderRefCount = 0;
        public int framesSinceStart;

        public ModelSlot(int _ID, ModelInstance _model, IsoGameCharacter _character) {
            this.ID = _ID;
            this.model = _model;
            this.character = _character;
        }

        public void Update() {
            if (this.character != null && !this.bRemove) {
                this.framesSinceStart++;
                if (this != this.character.legsSprite.modelSlot) {
                    boolean boolean0 = false;
                }

                if (this.model.AnimPlayer != this.character.getAnimationPlayer()) {
                    this.model.AnimPlayer = this.character.getAnimationPlayer();
                }

                synchronized (this.model.m_lock) {
                    this.model.UpdateDir();
                    this.model.Update();

                    for (int int0 = 0; int0 < this.sub.size(); int0++) {
                        this.sub.get(int0).AnimPlayer = this.model.AnimPlayer;
                    }
                }
            }
        }

        public boolean isRendering() {
            return this.renderRefCount > 0;
        }

        public void reset() {
            ModelManager.instance.resetModelInstanceRecurse(this.model, this);
            if (this.character != null) {
                this.character.primaryHandModel = null;
                this.character.secondaryHandModel = null;
                ModelManager.instance.derefModelInstances(this.character.getReadyModelData());
                this.character.getReadyModelData().clear();
            }

            this.active = false;
            this.character = null;
            this.bRemove = false;
            this.renderRefCount = 0;
            this.model = null;
            this.sub.clear();
            this.attachedModels.clear();
        }
    }
}
