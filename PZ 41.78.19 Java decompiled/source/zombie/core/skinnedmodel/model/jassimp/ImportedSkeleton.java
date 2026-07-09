// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model.jassimp;

import gnu.trove.list.array.TFloatArrayList;
import jassimp.AiAnimation;
import jassimp.AiBone;
import jassimp.AiBuiltInWrapperProvider;
import jassimp.AiMatrix4f;
import jassimp.AiMesh;
import jassimp.AiNode;
import jassimp.AiNodeAnim;
import jassimp.AiQuaternion;
import jassimp.AiScene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.core.skinnedmodel.model.SkinningData;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;

public final class ImportedSkeleton {
    final HashMap<String, Integer> boneIndices = new HashMap<>();
    final ArrayList<Integer> SkeletonHierarchy = new ArrayList<>();
    final ArrayList<Matrix4f> bindPose = new ArrayList<>();
    final ArrayList<Matrix4f> invBindPose = new ArrayList<>();
    final ArrayList<Matrix4f> skinOffsetMatrices = new ArrayList<>();
    AiNode rootBoneNode = null;
    final HashMap<String, AnimationClip> clips = new HashMap<>();
    final AiBuiltInWrapperProvider wrapper = new AiBuiltInWrapperProvider();
    final Quaternion end = new Quaternion();

    private ImportedSkeleton() {
    }

    public static ImportedSkeleton process(ImportedSkeletonParams importedSkeletonParams) {
        ImportedSkeleton importedSkeleton = new ImportedSkeleton();
        importedSkeleton.processAiScene(importedSkeletonParams);
        return importedSkeleton;
    }

    private void processAiScene(ImportedSkeletonParams importedSkeletonParams) {
        AiScene aiScene = importedSkeletonParams.scene;
        JAssImpImporter.LoadMode loadMode = importedSkeletonParams.mode;
        SkinningData skinningData = importedSkeletonParams.skinnedTo;
        float float0 = importedSkeletonParams.animBonesScaleModifier;
        Quaternion quaternion = importedSkeletonParams.animBonesRotateModifier;
        AiMesh aiMesh = importedSkeletonParams.mesh;
        AiNode aiNode0 = aiScene.getSceneRoot(this.wrapper);
        this.rootBoneNode = JAssImpImporter.FindNode("Dummy01", aiNode0);
        boolean boolean0;
        if (this.rootBoneNode == null) {
            this.rootBoneNode = JAssImpImporter.FindNode("VehicleSkeleton", aiNode0);
            boolean0 = true;
        } else {
            boolean0 = false;
        }

        while (this.rootBoneNode != null && this.rootBoneNode.getParent() != null && this.rootBoneNode.getParent() != aiNode0) {
            this.rootBoneNode = this.rootBoneNode.getParent();
        }

        if (this.rootBoneNode == null) {
            this.rootBoneNode = aiNode0;
        }

        ArrayList arrayList = new ArrayList();
        JAssImpImporter.CollectBoneNodes(arrayList, this.rootBoneNode);
        AiNode aiNode1 = JAssImpImporter.FindNode("Translation_Data", aiNode0);
        if (aiNode1 != null) {
            arrayList.add(aiNode1);

            for (AiNode aiNode2 = aiNode1.getParent(); aiNode2 != null && aiNode2 != aiNode0; aiNode2 = aiNode2.getParent()) {
                arrayList.add(aiNode2);
            }
        }

        if (skinningData != null) {
            this.boneIndices.putAll(skinningData.BoneIndices);
            this.SkeletonHierarchy.addAll(skinningData.SkeletonHierarchy);
        }

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            AiNode aiNode3 = (AiNode)arrayList.get(int0);
            String string0 = aiNode3.getName();
            if (!this.boneIndices.containsKey(string0)) {
                int int1 = this.boneIndices.size();
                this.boneIndices.put(string0, int1);
                if (aiNode3 == this.rootBoneNode) {
                    this.SkeletonHierarchy.add(-1);
                } else {
                    AiNode aiNode4 = aiNode3.getParent();

                    while (aiNode4 != null && !this.boneIndices.containsKey(aiNode4.getName())) {
                        aiNode4 = aiNode4.getParent();
                    }

                    if (aiNode4 != null) {
                        this.SkeletonHierarchy.add(this.boneIndices.get(aiNode4.getName()));
                    } else {
                        this.SkeletonHierarchy.add(0);
                    }
                }
            }
        }

        Matrix4f matrix4f0 = new Matrix4f();

        for (int int2 = 0; int2 < this.boneIndices.size(); int2++) {
            this.bindPose.add(matrix4f0);
            this.skinOffsetMatrices.add(matrix4f0);
        }

        List list0 = aiMesh.getBones();

        for (int int3 = 0; int3 < arrayList.size(); int3++) {
            AiNode aiNode5 = (AiNode)arrayList.get(int3);
            String string1 = aiNode5.getName();
            AiBone aiBone0 = JAssImpImporter.FindAiBone(string1, list0);
            if (aiBone0 != null) {
                AiMatrix4f aiMatrix4f0 = aiBone0.getOffsetMatrix(this.wrapper);
                if (aiMatrix4f0 != null) {
                    Matrix4f matrix4f1 = JAssImpImporter.getMatrixFromAiMatrix(aiMatrix4f0);
                    Matrix4f matrix4f2 = new Matrix4f(matrix4f1);
                    matrix4f2.invert();
                    Matrix4f matrix4f3 = new Matrix4f();
                    matrix4f3.setIdentity();
                    String string2 = aiNode5.getParent().getName();
                    AiBone aiBone1 = JAssImpImporter.FindAiBone(string2, list0);
                    if (aiBone1 != null) {
                        AiMatrix4f aiMatrix4f1 = aiBone1.getOffsetMatrix(this.wrapper);
                        if (aiMatrix4f1 != null) {
                            JAssImpImporter.getMatrixFromAiMatrix(aiMatrix4f1, matrix4f3);
                        }
                    }

                    Matrix4f matrix4f4 = new Matrix4f(matrix4f3);
                    matrix4f4.invert();
                    Matrix4f matrix4f5 = new Matrix4f();
                    Matrix4f.mul(matrix4f2, matrix4f4, matrix4f5);
                    matrix4f5.invert();
                    int int4 = this.boneIndices.get(string1);
                    this.bindPose.set(int4, matrix4f5);
                    this.skinOffsetMatrices.set(int4, matrix4f1);
                }
            }
        }

        int int5 = this.bindPose.size();

        for (int int6 = 0; int6 < int5; int6++) {
            Matrix4f matrix4f6 = new Matrix4f(this.bindPose.get(int6));
            matrix4f6.invert();
            this.invBindPose.add(int6, matrix4f6);
        }

        if (loadMode == JAssImpImporter.LoadMode.AnimationOnly || skinningData == null) {
            int int7 = aiScene.getNumAnimations();
            if (int7 > 0) {
                List list1 = aiScene.getAnimations();

                for (int int8 = 0; int8 < int7; int8++) {
                    AiAnimation aiAnimation = (AiAnimation)list1.get(int8);
                    if (boolean0) {
                        this.processAnimation(aiAnimation, boolean0, 1.0F, null);
                    } else {
                        this.processAnimation(aiAnimation, boolean0, float0, quaternion);
                    }
                }
            }
        }
    }

    @Deprecated
    void processAnimationOld(AiAnimation aiAnimation, boolean boolean0) {
        ArrayList arrayList0 = new ArrayList();
        float float0 = (float)aiAnimation.getDuration();
        float float1 = float0 / (float)aiAnimation.getTicksPerSecond();
        ArrayList arrayList1 = new ArrayList();
        List list = aiAnimation.getChannels();

        for (int int0 = 0; int0 < list.size(); int0++) {
            AiNodeAnim aiNodeAnim0 = (AiNodeAnim)list.get(int0);

            for (int int1 = 0; int1 < aiNodeAnim0.getNumPosKeys(); int1++) {
                float float2 = (float)aiNodeAnim0.getPosKeyTime(int1);
                if (!arrayList1.contains(float2)) {
                    arrayList1.add(float2);
                }
            }

            for (int int2 = 0; int2 < aiNodeAnim0.getNumRotKeys(); int2++) {
                float float3 = (float)aiNodeAnim0.getRotKeyTime(int2);
                if (!arrayList1.contains(float3)) {
                    arrayList1.add(float3);
                }
            }

            for (int int3 = 0; int3 < aiNodeAnim0.getNumScaleKeys(); int3++) {
                float float4 = (float)aiNodeAnim0.getScaleKeyTime(int3);
                if (!arrayList1.contains(float4)) {
                    arrayList1.add(float4);
                }
            }
        }

        Collections.sort(arrayList1);

        for (int int4 = 0; int4 < arrayList1.size(); int4++) {
            for (int int5 = 0; int5 < list.size(); int5++) {
                AiNodeAnim aiNodeAnim1 = (AiNodeAnim)list.get(int5);
                Keyframe keyframe = new Keyframe();
                keyframe.clear();
                keyframe.BoneName = aiNodeAnim1.getNodeName();
                Integer integer = this.boneIndices.get(keyframe.BoneName);
                if (integer == null) {
                    DebugLog.General.error("Could not find bone index for node name: \"%s\"", keyframe.BoneName);
                } else {
                    keyframe.Bone = integer;
                    keyframe.Time = (Float)arrayList1.get(int4) / (float)aiAnimation.getTicksPerSecond();
                    if (!boolean0) {
                        keyframe.Position = JAssImpImporter.GetKeyFramePosition(aiNodeAnim1, (Float)arrayList1.get(int4));
                        keyframe.Rotation = JAssImpImporter.GetKeyFrameRotation(aiNodeAnim1, (Float)arrayList1.get(int4));
                        keyframe.Scale = JAssImpImporter.GetKeyFrameScale(aiNodeAnim1, (Float)arrayList1.get(int4));
                    } else {
                        keyframe.Position = this.GetKeyFramePosition(aiNodeAnim1, (Float)arrayList1.get(int4), aiAnimation.getDuration());
                        keyframe.Rotation = this.GetKeyFrameRotation(aiNodeAnim1, (Float)arrayList1.get(int4), aiAnimation.getDuration());
                        keyframe.Scale = this.GetKeyFrameScale(aiNodeAnim1, (Float)arrayList1.get(int4), aiAnimation.getDuration());
                    }

                    if (keyframe.Bone >= 0) {
                        arrayList0.add(keyframe);
                    }
                }
            }
        }

        String string = aiAnimation.getName();
        int int6 = string.indexOf(124);
        if (int6 > 0) {
            string = string.substring(int6 + 1);
        }

        AnimationClip animationClip = new AnimationClip(float1, arrayList0, string, true);
        arrayList0.clear();
        if (ModelManager.instance.bCreateSoftwareMeshes) {
            animationClip.staticClip = new StaticAnimation(animationClip);
        }

        this.clips.put(string, animationClip);
    }

    private void processAnimation(AiAnimation aiAnimation, boolean boolean2, float float4, Quaternion quaternion1) {
        ArrayList arrayList0 = new ArrayList();
        float float0 = (float)aiAnimation.getDuration();
        float float1 = float0 / (float)aiAnimation.getTicksPerSecond();
        TFloatArrayList[] tFloatArrayLists = new TFloatArrayList[this.boneIndices.size()];
        Arrays.fill(tFloatArrayLists, null);
        ArrayList arrayList1 = new ArrayList(this.boneIndices.size());

        for (int int0 = 0; int0 < this.boneIndices.size(); int0++) {
            arrayList1.add(null);
        }

        this.collectBoneFrames(aiAnimation, tFloatArrayLists, arrayList1);
        Quaternion quaternion0 = null;
        boolean boolean0 = quaternion1 != null;
        if (boolean0) {
            quaternion0 = new Quaternion();
            Quaternion.mulInverse(quaternion0, quaternion1, quaternion0);
        }

        for (int int1 = 0; int1 < this.boneIndices.size(); int1++) {
            ArrayList arrayList2 = (ArrayList)arrayList1.get(int1);
            if (arrayList2 == null) {
                if (int1 == 0 && quaternion1 != null) {
                    Quaternion quaternion2 = new Quaternion();
                    quaternion2.set(quaternion1);
                    this.addDefaultAnimTrack("RootNode", int1, quaternion2, new Vector3f(0.0F, 0.0F, 0.0F), arrayList0, float1);
                }
            } else {
                TFloatArrayList tFloatArrayList = tFloatArrayLists[int1];
                if (tFloatArrayList != null) {
                    tFloatArrayList.sort();
                    int int2 = this.getParentBoneIdx(int1);
                    boolean boolean1 = boolean0 && (int2 == 0 || this.doesParentBoneHaveAnimFrames(tFloatArrayLists, arrayList1, int1));

                    for (int int3 = 0; int3 < tFloatArrayList.size(); int3++) {
                        float float2 = tFloatArrayList.get(int3);
                        float float3 = float2 / (float)aiAnimation.getTicksPerSecond();

                        for (int int4 = 0; int4 < arrayList2.size(); int4++) {
                            AiNodeAnim aiNodeAnim = (AiNodeAnim)arrayList2.get(int4);
                            Keyframe keyframe = new Keyframe();
                            keyframe.clear();
                            keyframe.BoneName = aiNodeAnim.getNodeName();
                            keyframe.Bone = int1;
                            keyframe.Time = float3;
                            if (!boolean2) {
                                keyframe.Position = JAssImpImporter.GetKeyFramePosition(aiNodeAnim, float2);
                                keyframe.Rotation = JAssImpImporter.GetKeyFrameRotation(aiNodeAnim, float2);
                                keyframe.Scale = JAssImpImporter.GetKeyFrameScale(aiNodeAnim, float2);
                            } else {
                                keyframe.Position = this.GetKeyFramePosition(aiNodeAnim, float2, float0);
                                keyframe.Rotation = this.GetKeyFrameRotation(aiNodeAnim, float2, float0);
                                keyframe.Scale = this.GetKeyFrameScale(aiNodeAnim, float2, float0);
                            }

                            keyframe.Position.x *= float4;
                            keyframe.Position.y *= float4;
                            keyframe.Position.z *= float4;
                            if (boolean0) {
                                if (boolean1) {
                                    Quaternion.mul(quaternion0, keyframe.Rotation, keyframe.Rotation);
                                    boolean boolean3 = StringUtils.startsWithIgnoreCase(keyframe.BoneName, "Translation_Data");
                                    if (!boolean3) {
                                        HelperFunctions.transform(quaternion0, keyframe.Position, keyframe.Position);
                                    }
                                }

                                Quaternion.mul(keyframe.Rotation, quaternion1, keyframe.Rotation);
                            }

                            arrayList0.add(keyframe);
                        }
                    }
                }
            }
        }

        String string = aiAnimation.getName();
        int int5 = string.indexOf(124);
        if (int5 > 0) {
            string = string.substring(int5 + 1);
        }

        string = string.trim();
        AnimationClip animationClip = new AnimationClip(float1, arrayList0, string, true);
        arrayList0.clear();
        if (ModelManager.instance.bCreateSoftwareMeshes) {
            animationClip.staticClip = new StaticAnimation(animationClip);
        }

        this.clips.put(string, animationClip);
    }

    private void addDefaultAnimTrack(String string, int int0, Quaternion quaternion, Vector3f vector3f1, ArrayList<Keyframe> arrayList, float float0) {
        Vector3f vector3f0 = new Vector3f(1.0F, 1.0F, 1.0F);
        Keyframe keyframe0 = new Keyframe();
        keyframe0.clear();
        keyframe0.BoneName = string;
        keyframe0.Bone = int0;
        keyframe0.Time = 0.0F;
        keyframe0.Position = vector3f1;
        keyframe0.Rotation = quaternion;
        keyframe0.Scale = vector3f0;
        arrayList.add(keyframe0);
        Keyframe keyframe1 = new Keyframe();
        keyframe1.clear();
        keyframe1.BoneName = string;
        keyframe1.Bone = int0;
        keyframe1.Time = float0;
        keyframe1.Position = vector3f1;
        keyframe1.Rotation = quaternion;
        keyframe1.Scale = vector3f0;
        arrayList.add(keyframe1);
    }

    private boolean doesParentBoneHaveAnimFrames(TFloatArrayList[] tFloatArrayLists, ArrayList<ArrayList<AiNodeAnim>> arrayList, int int1) {
        int int0 = this.getParentBoneIdx(int1);
        return int0 < 0 ? false : this.doesBoneHaveAnimFrames(tFloatArrayLists, arrayList, int0);
    }

    private boolean doesBoneHaveAnimFrames(TFloatArrayList[] tFloatArrayLists, ArrayList<ArrayList<AiNodeAnim>> arrayList1, int int0) {
        TFloatArrayList tFloatArrayList = tFloatArrayLists[int0];
        if (tFloatArrayList != null && tFloatArrayList.size() > 0) {
            ArrayList arrayList0 = (ArrayList)arrayList1.get(int0);
            return arrayList0.size() > 0;
        } else {
            return false;
        }
    }

    private void collectBoneFrames(AiAnimation aiAnimation, TFloatArrayList[] tFloatArrayLists, ArrayList<ArrayList<AiNodeAnim>> arrayList1) {
        List list = aiAnimation.getChannels();

        for (int int0 = 0; int0 < list.size(); int0++) {
            AiNodeAnim aiNodeAnim = (AiNodeAnim)list.get(int0);
            String string = aiNodeAnim.getNodeName();
            Integer integer = this.boneIndices.get(string);
            if (integer == null) {
                DebugLog.General.error("Could not find bone index for node name: \"%s\"", string);
            } else {
                ArrayList arrayList0 = (ArrayList)arrayList1.get(integer);
                if (arrayList0 == null) {
                    arrayList0 = new ArrayList();
                    arrayList1.set(integer, arrayList0);
                }

                arrayList0.add(aiNodeAnim);
                TFloatArrayList tFloatArrayList = tFloatArrayLists[integer];
                if (tFloatArrayList == null) {
                    tFloatArrayList = new TFloatArrayList();
                    tFloatArrayLists[integer] = tFloatArrayList;
                }

                for (int int1 = 0; int1 < aiNodeAnim.getNumPosKeys(); int1++) {
                    float float0 = (float)aiNodeAnim.getPosKeyTime(int1);
                    if (!tFloatArrayList.contains(float0)) {
                        tFloatArrayList.add(float0);
                    }
                }

                for (int int2 = 0; int2 < aiNodeAnim.getNumRotKeys(); int2++) {
                    float float1 = (float)aiNodeAnim.getRotKeyTime(int2);
                    if (!tFloatArrayList.contains(float1)) {
                        tFloatArrayList.add(float1);
                    }
                }

                for (int int3 = 0; int3 < aiNodeAnim.getNumScaleKeys(); int3++) {
                    float float2 = (float)aiNodeAnim.getScaleKeyTime(int3);
                    if (!tFloatArrayList.contains(float2)) {
                        tFloatArrayList.add(float2);
                    }
                }
            }
        }
    }

    private int getParentBoneIdx(int int0) {
        return int0 > -1 ? this.SkeletonHierarchy.get(int0) : -1;
    }

    public int getNumBoneAncestors(int int2) {
        int int0 = 0;

        for (int int1 = this.getParentBoneIdx(int2); int1 > -1; int1 = this.getParentBoneIdx(int1)) {
            int0++;
        }

        return int0;
    }

    private Vector3f GetKeyFramePosition(AiNodeAnim aiNodeAnim, float float0, double double0) {
        Vector3f vector3f = new Vector3f();
        if (aiNodeAnim.getNumPosKeys() == 0) {
            return vector3f;
        } else {
            int int0 = 0;

            while (int0 < aiNodeAnim.getNumPosKeys() - 1 && !(float0 < aiNodeAnim.getPosKeyTime(int0 + 1))) {
                int0++;
            }

            int int1 = (int0 + 1) % aiNodeAnim.getNumPosKeys();
            float float1 = (float)aiNodeAnim.getPosKeyTime(int0);
            float float2 = (float)aiNodeAnim.getPosKeyTime(int1);
            float float3 = float2 - float1;
            if (float3 < 0.0F) {
                float3 = (float)(float3 + double0);
            }

            if (float3 > 0.0F) {
                float float4 = float2 - float1;
                float float5 = float0 - float1;
                float5 /= float4;
                float float6 = aiNodeAnim.getPosKeyX(int0);
                float float7 = aiNodeAnim.getPosKeyX(int1);
                float float8 = float6 + float5 * (float7 - float6);
                float float9 = aiNodeAnim.getPosKeyY(int0);
                float float10 = aiNodeAnim.getPosKeyY(int1);
                float float11 = float9 + float5 * (float10 - float9);
                float float12 = aiNodeAnim.getPosKeyZ(int0);
                float float13 = aiNodeAnim.getPosKeyZ(int1);
                float float14 = float12 + float5 * (float13 - float12);
                vector3f.set(float8, float11, float14);
            } else {
                vector3f.set(aiNodeAnim.getPosKeyX(int0), aiNodeAnim.getPosKeyY(int0), aiNodeAnim.getPosKeyZ(int0));
            }

            return vector3f;
        }
    }

    private Quaternion GetKeyFrameRotation(AiNodeAnim aiNodeAnim, float float0, double double0) {
        Quaternion quaternion = new Quaternion();
        if (aiNodeAnim.getNumRotKeys() == 0) {
            return quaternion;
        } else {
            int int0 = 0;

            while (int0 < aiNodeAnim.getNumRotKeys() - 1 && !(float0 < aiNodeAnim.getRotKeyTime(int0 + 1))) {
                int0++;
            }

            int int1 = (int0 + 1) % aiNodeAnim.getNumRotKeys();
            float float1 = (float)aiNodeAnim.getRotKeyTime(int0);
            float float2 = (float)aiNodeAnim.getRotKeyTime(int1);
            float float3 = float2 - float1;
            if (float3 < 0.0F) {
                float3 = (float)(float3 + double0);
            }

            if (float3 > 0.0F) {
                float float4 = (float0 - float1) / float3;
                AiQuaternion aiQuaternion0 = aiNodeAnim.getRotKeyQuaternion(int0, this.wrapper);
                AiQuaternion aiQuaternion1 = aiNodeAnim.getRotKeyQuaternion(int1, this.wrapper);
                double double1 = aiQuaternion0.getX() * aiQuaternion1.getX()
                    + aiQuaternion0.getY() * aiQuaternion1.getY()
                    + aiQuaternion0.getZ() * aiQuaternion1.getZ()
                    + aiQuaternion0.getW() * aiQuaternion1.getW();
                this.end.set(aiQuaternion1.getX(), aiQuaternion1.getY(), aiQuaternion1.getZ(), aiQuaternion1.getW());
                if (double1 < 0.0) {
                    double1 *= -1.0;
                    this.end.setX(-this.end.getX());
                    this.end.setY(-this.end.getY());
                    this.end.setZ(-this.end.getZ());
                    this.end.setW(-this.end.getW());
                }

                double double2;
                double double3;
                if (1.0 - double1 > 1.0E-4) {
                    double double4 = Math.acos(double1);
                    double double5 = Math.sin(double4);
                    double2 = Math.sin((1.0 - float4) * double4) / double5;
                    double3 = Math.sin(float4 * double4) / double5;
                } else {
                    double2 = 1.0 - float4;
                    double3 = float4;
                }

                quaternion.set(
                    (float)(double2 * aiQuaternion0.getX() + double3 * this.end.getX()),
                    (float)(double2 * aiQuaternion0.getY() + double3 * this.end.getY()),
                    (float)(double2 * aiQuaternion0.getZ() + double3 * this.end.getZ()),
                    (float)(double2 * aiQuaternion0.getW() + double3 * this.end.getW())
                );
            } else {
                float float5 = aiNodeAnim.getRotKeyX(int0);
                float float6 = aiNodeAnim.getRotKeyY(int0);
                float float7 = aiNodeAnim.getRotKeyZ(int0);
                float float8 = aiNodeAnim.getRotKeyW(int0);
                quaternion.set(float5, float6, float7, float8);
            }

            return quaternion;
        }
    }

    private Vector3f GetKeyFrameScale(AiNodeAnim aiNodeAnim, float float0, double var3) {
        Vector3f vector3f = new Vector3f(1.0F, 1.0F, 1.0F);
        if (aiNodeAnim.getNumScaleKeys() == 0) {
            return vector3f;
        } else {
            int int0 = 0;

            while (int0 < aiNodeAnim.getNumScaleKeys() - 1 && !(float0 < aiNodeAnim.getScaleKeyTime(int0 + 1))) {
                int0++;
            }

            vector3f.set(aiNodeAnim.getScaleKeyX(int0), aiNodeAnim.getScaleKeyY(int0), aiNodeAnim.getScaleKeyZ(int0));
            return vector3f;
        }
    }
}
