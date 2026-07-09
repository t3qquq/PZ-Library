// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiBuiltInWrapperProvider;
import jassimp.AiMesh;
import jassimp.AiNode;
import jassimp.AiScene;
import java.util.HashMap;
import java.util.Map.Entry;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.math.PZMath;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.model.AnimationAsset;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.core.skinnedmodel.model.VertexBufferObject;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;

public final class ProcessedAiScene {
    private ImportedSkeleton skeleton;
    private ImportedSkinnedMesh skinnedMesh;
    private ImportedStaticMesh staticMesh;
    private Matrix4f transform = null;

    private ProcessedAiScene() {
    }

    public static ProcessedAiScene process(ProcessedAiSceneParams processedAiSceneParams) {
        ProcessedAiScene processedAiScene = new ProcessedAiScene();
        processedAiScene.processAiScene(processedAiSceneParams);
        return processedAiScene;
    }

    private void processAiScene(ProcessedAiSceneParams processedAiSceneParams) {
        AiScene aiScene = processedAiSceneParams.scene;
        JAssImpImporter.LoadMode loadMode = processedAiSceneParams.mode;
        String string = processedAiSceneParams.meshName;
        AiMesh aiMesh = this.findMesh(aiScene, string);
        if (aiMesh == null) {
            DebugLog.General.error("No such mesh \"%s\"", string);
        } else {
            if (loadMode != JAssImpImporter.LoadMode.StaticMesh && aiMesh.hasBones()) {
                ImportedSkeletonParams importedSkeletonParams = ImportedSkeletonParams.create(processedAiSceneParams, aiMesh);
                this.skeleton = ImportedSkeleton.process(importedSkeletonParams);
                if (loadMode != JAssImpImporter.LoadMode.AnimationOnly) {
                    this.skinnedMesh = new ImportedSkinnedMesh(this.skeleton, aiMesh);
                }
            } else {
                this.staticMesh = new ImportedStaticMesh(aiMesh);
            }

            if (this.staticMesh != null || this.skinnedMesh != null) {
                AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
                AiNode aiNode0 = aiScene.getSceneRoot(aiBuiltInWrapperProvider);
                AiNode aiNode1 = this.findParentNodeForMesh(aiScene.getMeshes().indexOf(aiMesh), aiNode0);
                if (aiNode1 != null) {
                    this.transform = JAssImpImporter.getMatrixFromAiMatrix(aiNode1.getTransform(aiBuiltInWrapperProvider));

                    for (AiNode aiNode2 = aiNode1.getParent(); aiNode2 != null; aiNode2 = aiNode2.getParent()) {
                        Matrix4f matrix4f = JAssImpImporter.getMatrixFromAiMatrix(aiNode2.getTransform(aiBuiltInWrapperProvider));
                        Matrix4f.mul(matrix4f, this.transform, this.transform);
                    }
                }
            }
        }
    }

    private AiMesh findMesh(AiScene aiScene, String string) {
        if (aiScene.getNumMeshes() == 0) {
            return null;
        } else if (StringUtils.isNullOrWhitespace(string)) {
            for (AiMesh aiMesh0 : aiScene.getMeshes()) {
                if (aiMesh0.hasBones()) {
                    return aiMesh0;
                }
            }

            return aiScene.getMeshes().get(0);
        } else {
            for (AiMesh aiMesh1 : aiScene.getMeshes()) {
                if (aiMesh1.getName().equalsIgnoreCase(string)) {
                    return aiMesh1;
                }
            }

            AiBuiltInWrapperProvider aiBuiltInWrapperProvider = new AiBuiltInWrapperProvider();
            AiNode aiNode0 = aiScene.getSceneRoot(aiBuiltInWrapperProvider);
            AiNode aiNode1 = JAssImpImporter.FindNode(string, aiNode0);
            if (aiNode1 != null && aiNode1.getNumMeshes() == 1) {
                int int0 = aiNode1.getMeshes()[0];
                return aiScene.getMeshes().get(int0);
            } else {
                return null;
            }
        }
    }

    private AiNode findParentNodeForMesh(int int1, AiNode aiNode0) {
        for (int int0 = 0; int0 < aiNode0.getNumMeshes(); int0++) {
            if (aiNode0.getMeshes()[int0] == int1) {
                return aiNode0;
            }
        }

        for (AiNode aiNode1 : aiNode0.getChildren()) {
            AiNode aiNode2 = this.findParentNodeForMesh(int1, aiNode1);
            if (aiNode2 != null) {
                return aiNode2;
            }
        }

        return null;
    }

    public void applyToMesh(ModelMesh modelMesh, JAssImpImporter.LoadMode var2, boolean boolean0, SkinningData skinningData) {
        modelMesh.m_transform = null;
        if (this.transform != null) {
            modelMesh.m_transform = PZMath.convertMatrix(this.transform, new org.joml.Matrix4f());
        }

        if (this.staticMesh != null && !ModelManager.NoOpenGL) {
            modelMesh.minXYZ.set(this.staticMesh.minXYZ);
            modelMesh.maxXYZ.set(this.staticMesh.maxXYZ);
            modelMesh.m_bHasVBO = true;
            VertexBufferObject.VertexArray vertexArray0 = this.staticMesh.verticesUnskinned;
            int[] ints0 = this.staticMesh.elements;
            RenderThread.queueInvokeOnRenderContext(() -> {
                modelMesh.SetVertexBuffer(new VertexBufferObject(vertexArray0, ints0));
                if (ModelManager.instance.bCreateSoftwareMeshes) {
                    modelMesh.softwareMesh.vb = modelMesh.vb;
                }
            });
        }

        if (modelMesh.skinningData != null) {
            if (skinningData == null || modelMesh.skinningData.AnimationClips != skinningData.AnimationClips) {
                modelMesh.skinningData.AnimationClips.clear();
            }

            modelMesh.skinningData.InverseBindPose.clear();
            modelMesh.skinningData.BindPose.clear();
            modelMesh.skinningData.BoneOffset.clear();
            modelMesh.skinningData.BoneIndices.clear();
            modelMesh.skinningData.SkeletonHierarchy.clear();
            modelMesh.skinningData = null;
        }

        if (this.skeleton != null) {
            ImportedSkeleton importedSkeleton = this.skeleton;
            HashMap hashMap = importedSkeleton.clips;
            if (skinningData != null) {
                importedSkeleton.clips.clear();
                hashMap = skinningData.AnimationClips;
            }

            JAssImpImporter.replaceHashMapKeys(importedSkeleton.boneIndices, "SkinningData.boneIndices");
            modelMesh.skinningData = new SkinningData(
                hashMap,
                importedSkeleton.bindPose,
                importedSkeleton.invBindPose,
                importedSkeleton.skinOffsetMatrices,
                importedSkeleton.SkeletonHierarchy,
                importedSkeleton.boneIndices
            );
        }

        if (this.skinnedMesh != null && !ModelManager.NoOpenGL) {
            modelMesh.m_bHasVBO = true;
            VertexBufferObject.VertexArray vertexArray1 = this.skinnedMesh.vertices;
            int[] ints1 = this.skinnedMesh.elements;
            RenderThread.queueInvokeOnRenderContext(() -> {
                modelMesh.SetVertexBuffer(new VertexBufferObject(vertexArray1, ints1, boolean0));
                if (ModelManager.instance.bCreateSoftwareMeshes) {
                    modelMesh.softwareMesh.vb = modelMesh.vb;
                }
            });
        }

        this.skeleton = null;
        this.skinnedMesh = null;
        this.staticMesh = null;
    }

    public void applyToAnimation(AnimationAsset animationAsset) {
        for (Entry entry : this.skeleton.clips.entrySet()) {
            for (Keyframe keyframe : ((AnimationClip)entry.getValue()).getKeyframes()) {
                keyframe.BoneName = JAssImpImporter.getSharedString(keyframe.BoneName, "Keyframe.BoneName");
            }
        }

        animationAsset.AnimationClips = this.skeleton.clips;
        this.skeleton = null;
    }
}
