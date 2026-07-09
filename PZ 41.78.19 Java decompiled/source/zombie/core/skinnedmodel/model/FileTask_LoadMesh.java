// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import jassimp.AiPostProcessSteps;
import jassimp.AiScene;
import jassimp.Jassimp;
import java.io.IOException;
import java.util.EnumSet;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector4f;
import zombie.ZomboidFileSystem;
import zombie.core.skinnedmodel.model.jassimp.JAssImpImporter;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiSceneParams;
import zombie.debug.DebugLog;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFileTaskCallback;

public class FileTask_LoadMesh extends FileTask_AbstractLoadModel {
    ModelMesh mesh;

    public FileTask_LoadMesh(ModelMesh modelMesh, FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
        super(fileSystem, iFileTaskCallback, "media/models", "media/models_x");
        this.mesh = modelMesh;
    }

    @Override
    public String getErrorMessage() {
        return this.m_fileName;
    }

    @Override
    public void done() {
        MeshAssetManager.instance.addWatchedFile(this.m_fileName);
        this.mesh.m_fullPath = this.m_fileName;
        this.m_fileName = null;
        this.mesh = null;
    }

    @Override
    public String getRawFileName() {
        String string = this.mesh.getPath().getPath();
        int int0 = string.indexOf(124);
        return int0 != -1 ? string.substring(0, int0) : string;
    }

    private String getMeshName() {
        String string = this.mesh.getPath().getPath();
        int int0 = string.indexOf(124);
        return int0 != -1 ? string.substring(int0 + 1) : null;
    }

    @Override
    public ProcessedAiScene loadX() throws IOException {
        EnumSet enumSet = EnumSet.of(
            AiPostProcessSteps.FIND_INSTANCES,
            AiPostProcessSteps.MAKE_LEFT_HANDED,
            AiPostProcessSteps.LIMIT_BONE_WEIGHTS,
            AiPostProcessSteps.TRIANGULATE,
            AiPostProcessSteps.OPTIMIZE_MESHES,
            AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS,
            AiPostProcessSteps.JOIN_IDENTICAL_VERTICES
        );
        ZomboidFileSystem.instance.validatePrefix(this.m_fileName);
        AiScene aiScene = Jassimp.importFile(this.m_fileName, enumSet);
        JAssImpImporter.LoadMode loadMode = this.mesh.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
        ModelMesh modelMesh = this.mesh.assetParams.animationsMesh;
        SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
        ProcessedAiSceneParams processedAiSceneParams = ProcessedAiSceneParams.create();
        processedAiSceneParams.scene = aiScene;
        processedAiSceneParams.mode = loadMode;
        processedAiSceneParams.skinnedTo = skinningData;
        processedAiSceneParams.meshName = this.getMeshName();
        ProcessedAiScene processedAiScene = ProcessedAiScene.process(processedAiSceneParams);
        JAssImpImporter.takeOutTheTrash(aiScene);
        return processedAiScene;
    }

    @Override
    public ProcessedAiScene loadFBX() throws IOException {
        DebugLog.Animation.debugln("Loading: %s", this.m_fileName);
        EnumSet enumSet = EnumSet.of(
            AiPostProcessSteps.FIND_INSTANCES,
            AiPostProcessSteps.MAKE_LEFT_HANDED,
            AiPostProcessSteps.LIMIT_BONE_WEIGHTS,
            AiPostProcessSteps.TRIANGULATE,
            AiPostProcessSteps.OPTIMIZE_MESHES,
            AiPostProcessSteps.REMOVE_REDUNDANT_MATERIALS,
            AiPostProcessSteps.JOIN_IDENTICAL_VERTICES
        );
        ZomboidFileSystem.instance.validatePrefix(this.m_fileName);
        AiScene aiScene = Jassimp.importFile(this.m_fileName, enumSet);
        JAssImpImporter.LoadMode loadMode = this.mesh.assetParams.bStatic ? JAssImpImporter.LoadMode.StaticMesh : JAssImpImporter.LoadMode.Normal;
        ModelMesh modelMesh = this.mesh.assetParams.animationsMesh;
        SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
        Quaternion quaternion = new Quaternion();
        Vector4f vector4f = new Vector4f(1.0F, 0.0F, 0.0F, (float) (-Math.PI / 2));
        quaternion.setFromAxisAngle(vector4f);
        ProcessedAiSceneParams processedAiSceneParams = ProcessedAiSceneParams.create();
        processedAiSceneParams.scene = aiScene;
        processedAiSceneParams.mode = loadMode;
        processedAiSceneParams.skinnedTo = skinningData;
        processedAiSceneParams.meshName = this.getMeshName();
        processedAiSceneParams.animBonesScaleModifier = 0.01F;
        processedAiSceneParams.animBonesRotateModifier = quaternion;
        ProcessedAiScene processedAiScene = ProcessedAiScene.process(processedAiSceneParams);
        JAssImpImporter.takeOutTheTrash(aiScene);
        return processedAiScene;
    }

    @Override
    public ModelTxt loadTxt() throws IOException {
        boolean boolean0 = this.mesh.assetParams.bStatic;
        boolean boolean1 = false;
        ModelMesh modelMesh = this.mesh.assetParams.animationsMesh;
        SkinningData skinningData = modelMesh == null ? null : modelMesh.skinningData;
        return ModelLoader.instance.loadTxt(this.m_fileName, boolean0, boolean1, skinningData);
    }

    static enum LoadMode {
        Assimp,
        Txt,
        Missing;
    }
}
