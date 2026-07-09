// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import org.joml.Vector3f;
import zombie.ZomboidFileSystem;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.advancedanimation.AnimBoneWeight;
import zombie.core.skinnedmodel.model.Model;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class ModelScript extends BaseScriptObject {
    public static final String DEFAULT_SHADER_NAME = "basicEffect";
    public String fileName;
    public String name;
    public String meshName;
    public String textureName;
    public String shaderName;
    public boolean bStatic = true;
    public float scale = 1.0F;
    public final ArrayList<ModelAttachment> m_attachments = new ArrayList<>();
    public boolean invertX = false;
    public Model loadedModel;
    public final ArrayList<AnimBoneWeight> boneWeights = new ArrayList<>();
    public String animationsMesh = null;
    private static final HashSet<String> reported = new HashSet<>();

    public void Load(String _name, String totalFile) {
        ScriptManager scriptManager = ScriptManager.instance;
        this.fileName = scriptManager.currentFileName;
        this.name = _name;
        ScriptParser.Block block0 = ScriptParser.parse(totalFile);
        block0 = block0.children.get(0);

        for (ScriptParser.Block block1 : block0.children) {
            if ("attachment".equals(block1.type)) {
                this.LoadAttachment(block1);
            }
        }

        for (ScriptParser.Value value : block0.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("mesh".equalsIgnoreCase(string0)) {
                this.meshName = string1;
            } else if ("scale".equalsIgnoreCase(string0)) {
                this.scale = Float.parseFloat(string1);
            } else if ("shader".equalsIgnoreCase(string0)) {
                this.shaderName = string1;
            } else if ("static".equalsIgnoreCase(string0)) {
                this.bStatic = Boolean.parseBoolean(string1);
            } else if ("texture".equalsIgnoreCase(string0)) {
                this.textureName = string1;
            } else if ("invertX".equalsIgnoreCase(string0)) {
                this.invertX = Boolean.parseBoolean(string1);
            } else if ("boneWeight".equalsIgnoreCase(string0)) {
                String[] strings = string1.split("\\s+");
                if (strings.length == 2) {
                    AnimBoneWeight animBoneWeight = new AnimBoneWeight(strings[0], PZMath.tryParseFloat(strings[1], 1.0F));
                    animBoneWeight.includeDescendants = false;
                    this.boneWeights.add(animBoneWeight);
                }
            } else if ("animationsMesh".equalsIgnoreCase(string0)) {
                this.animationsMesh = StringUtils.discardNullOrWhitespace(string1);
            }
        }
    }

    private ModelAttachment LoadAttachment(ScriptParser.Block block) {
        ModelAttachment modelAttachment = this.getAttachmentById(block.id);
        if (modelAttachment == null) {
            modelAttachment = new ModelAttachment(block.id);
            this.m_attachments.add(modelAttachment);
        }

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("bone".equals(string0)) {
                modelAttachment.setBone(string1);
            } else if ("offset".equals(string0)) {
                this.LoadVector3f(string1, modelAttachment.getOffset());
            } else if ("rotate".equals(string0)) {
                this.LoadVector3f(string1, modelAttachment.getRotate());
            }
        }

        return modelAttachment;
    }

    private void LoadVector3f(String string, Vector3f vector3f) {
        String[] strings = string.split(" ");
        vector3f.set(Float.parseFloat(strings[0]), Float.parseFloat(strings[1]), Float.parseFloat(strings[2]));
    }

    public String getName() {
        return this.name;
    }

    public String getFullType() {
        return this.module.name + "." + this.name;
    }

    public String getMeshName() {
        return this.meshName;
    }

    public String getTextureName() {
        return StringUtils.isNullOrWhitespace(this.textureName) ? this.meshName : this.textureName;
    }

    public String getTextureName(boolean allowNull) {
        return StringUtils.isNullOrWhitespace(this.textureName) && !allowNull ? this.meshName : this.textureName;
    }

    public String getShaderName() {
        return StringUtils.isNullOrWhitespace(this.shaderName) ? "basicEffect" : this.shaderName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public int getAttachmentCount() {
        return this.m_attachments.size();
    }

    public ModelAttachment getAttachment(int index) {
        return this.m_attachments.get(index);
    }

    public ModelAttachment getAttachmentById(String id) {
        for (int int0 = 0; int0 < this.m_attachments.size(); int0++) {
            ModelAttachment modelAttachment = this.m_attachments.get(int0);
            if (modelAttachment.getId().equals(id)) {
                return modelAttachment;
            }
        }

        return null;
    }

    public ModelAttachment addAttachment(ModelAttachment attach) {
        this.m_attachments.add(attach);
        return attach;
    }

    public ModelAttachment removeAttachment(ModelAttachment attach) {
        this.m_attachments.remove(attach);
        return attach;
    }

    public ModelAttachment addAttachmentAt(int index, ModelAttachment attach) {
        this.m_attachments.add(index, attach);
        return attach;
    }

    public ModelAttachment removeAttachment(int index) {
        return this.m_attachments.remove(index);
    }

    public void reset() {
        this.invertX = false;
        this.name = null;
        this.meshName = null;
        this.textureName = null;
        this.shaderName = null;
        this.bStatic = true;
        this.scale = 1.0F;
        this.boneWeights.clear();
    }

    private static void checkMesh(String string2, String string0) {
        if (!StringUtils.isNullOrWhitespace(string0)) {
            String string1 = string0.toLowerCase(Locale.ENGLISH);
            if (!ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/models_x/" + string1 + ".fbx")
                && !ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/models_x/" + string1 + ".x")
                && !ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/models/" + string1 + ".txt")) {
                reported.add(string0);
                DebugLog.Script.warn("no such mesh \"" + string0 + "\" for " + string2);
            }
        }
    }

    private static void checkTexture(String string2, String string0) {
        if (!GameServer.bServer) {
            if (!StringUtils.isNullOrWhitespace(string0)) {
                String string1 = string0.toLowerCase(Locale.ENGLISH);
                if (!ZomboidFileSystem.instance.ActiveFileMap.containsKey("media/textures/" + string1 + ".png")) {
                    reported.add(string0);
                    DebugLog.Script.warn("no such texture \"" + string0 + "\" for " + string2);
                }
            }
        }
    }

    private static void check(String string0, String string1) {
        check(string0, string1, null);
    }

    private static void check(String string1, String string0, String string2) {
        if (!StringUtils.isNullOrWhitespace(string0)) {
            if (!reported.contains(string0)) {
                ModelScript modelScript = ScriptManager.instance.getModelScript(string0);
                if (modelScript == null) {
                    reported.add(string0);
                    DebugLog.Script.warn("no such model \"" + string0 + "\" for " + string1);
                } else {
                    checkMesh(modelScript.getFullType(), modelScript.getMeshName());
                    if (StringUtils.isNullOrWhitespace(string2)) {
                        checkTexture(modelScript.getFullType(), modelScript.getTextureName());
                    }
                }
            }
        }
    }

    public static void ScriptsLoaded() {
        reported.clear();

        for (Item item : ScriptManager.instance.getAllItems()) {
            item.resolveModelScripts();
            check(item.getFullName(), item.getStaticModel());
            check(item.getFullName(), item.getWeaponSprite());
            check(item.getFullName(), item.worldStaticModel, item.getClothingItem());
        }

        for (Recipe recipe : ScriptManager.instance.getAllRecipes()) {
            if (recipe.getProp1() != null && !recipe.getProp1().startsWith("Source=")) {
                check(recipe.getFullType(), recipe.getProp1());
            }

            if (recipe.getProp2() != null && !recipe.getProp2().startsWith("Source=")) {
                check(recipe.getFullType(), recipe.getProp2());
            }
        }
    }
}
