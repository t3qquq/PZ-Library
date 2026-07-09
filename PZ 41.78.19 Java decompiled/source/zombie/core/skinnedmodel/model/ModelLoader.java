// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.opengl.RenderThread;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.core.skinnedmodel.animation.StaticAnimation;
import zombie.util.SharedStrings;

public final class ModelLoader {
    public static final ModelLoader instance = new ModelLoader();
    private final ThreadLocal<SharedStrings> sharedStrings = ThreadLocal.withInitial(SharedStrings::new);

    protected ModelTxt loadTxt(String string0, boolean boolean0, boolean boolean1, SkinningData skinningData) throws IOException {
        ModelTxt modelTxt = new ModelTxt();
        modelTxt.bStatic = boolean0;
        modelTxt.bReverse = boolean1;
        VertexBufferObject.VertexFormat vertexFormat = new VertexBufferObject.VertexFormat(boolean0 ? 4 : 6);
        vertexFormat.setElement(0, VertexBufferObject.VertexType.VertexArray, 12);
        vertexFormat.setElement(1, VertexBufferObject.VertexType.NormalArray, 12);
        vertexFormat.setElement(2, VertexBufferObject.VertexType.TangentArray, 12);
        vertexFormat.setElement(3, VertexBufferObject.VertexType.TextureCoordArray, 8);
        if (!boolean0) {
            vertexFormat.setElement(4, VertexBufferObject.VertexType.BlendWeightArray, 16);
            vertexFormat.setElement(5, VertexBufferObject.VertexType.BlendIndexArray, 16);
        }

        vertexFormat.calculate();

        try (
            FileReader fileReader = new FileReader(string0);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
        ) {
            SharedStrings sharedStringsx = this.sharedStrings.get();
            ModelLoader.LoadMode loadMode = ModelLoader.LoadMode.Version;
            String string1 = null;
            int int0 = 0;
            int int1 = 0;
            int int2 = 0;
            int int3 = 0;
            int int4 = 0;
            boolean boolean2 = false;

            while ((string1 = bufferedReader.readLine()) != null) {
                if (string1.indexOf(35) != 0) {
                    if (string1.contains("Tangent")) {
                        if (boolean0) {
                            int0 += 2;
                        }

                        boolean2 = true;
                    }

                    if (int0 > 0) {
                        int0--;
                    } else {
                        switch (loadMode) {
                            case Version:
                                loadMode = ModelLoader.LoadMode.ModelName;
                                break;
                            case ModelName:
                                loadMode = ModelLoader.LoadMode.VertexStrideElementCount;
                                break;
                            case VertexStrideElementCount:
                                loadMode = ModelLoader.LoadMode.VertexCount;
                                if (boolean0) {
                                    int0 = 7;
                                } else {
                                    int0 = 13;
                                }
                                break;
                            case VertexCount:
                                int1 = Integer.parseInt(string1);
                                loadMode = ModelLoader.LoadMode.VertexBuffer;
                                modelTxt.vertices = new VertexBufferObject.VertexArray(vertexFormat, int1);
                                break;
                            case VertexBuffer:
                                int int5 = 0;

                                for (; int5 < int1; int5++) {
                                    String[] strings0 = string1.split(",");
                                    float float0 = Float.parseFloat(strings0[0].trim());
                                    float float1 = Float.parseFloat(strings0[1].trim());
                                    float float2 = Float.parseFloat(strings0[2].trim());
                                    string1 = bufferedReader.readLine();
                                    strings0 = string1.split(",");
                                    float float3 = Float.parseFloat(strings0[0].trim());
                                    float float4 = Float.parseFloat(strings0[1].trim());
                                    float float5 = Float.parseFloat(strings0[2].trim());
                                    float float6 = 0.0F;
                                    float float7 = 0.0F;
                                    float float8 = 0.0F;
                                    if (boolean2) {
                                        string1 = bufferedReader.readLine();
                                        strings0 = string1.split(",");
                                        float6 = Float.parseFloat(strings0[0].trim());
                                        float7 = Float.parseFloat(strings0[1].trim());
                                        float8 = Float.parseFloat(strings0[2].trim());
                                    }

                                    string1 = bufferedReader.readLine();
                                    strings0 = string1.split(",");
                                    float float9 = Float.parseFloat(strings0[0].trim());
                                    float float10 = Float.parseFloat(strings0[1].trim());
                                    float float11 = 0.0F;
                                    float float12 = 0.0F;
                                    float float13 = 0.0F;
                                    float float14 = 0.0F;
                                    int int6 = 0;
                                    int int7 = 0;
                                    int int8 = 0;
                                    int int9 = 0;
                                    if (!boolean0) {
                                        string1 = bufferedReader.readLine();
                                        strings0 = string1.split(",");
                                        float11 = Float.parseFloat(strings0[0].trim());
                                        float12 = Float.parseFloat(strings0[1].trim());
                                        float13 = Float.parseFloat(strings0[2].trim());
                                        float14 = Float.parseFloat(strings0[3].trim());
                                        string1 = bufferedReader.readLine();
                                        strings0 = string1.split(",");
                                        int6 = Integer.parseInt(strings0[0].trim());
                                        int7 = Integer.parseInt(strings0[1].trim());
                                        int8 = Integer.parseInt(strings0[2].trim());
                                        int9 = Integer.parseInt(strings0[3].trim());
                                    }

                                    string1 = bufferedReader.readLine();
                                    modelTxt.vertices.setElement(int5, 0, float0, float1, float2);
                                    modelTxt.vertices.setElement(int5, 1, float3, float4, float5);
                                    modelTxt.vertices.setElement(int5, 2, float6, float7, float8);
                                    modelTxt.vertices.setElement(int5, 3, float9, float10);
                                    if (!boolean0) {
                                        modelTxt.vertices.setElement(int5, 4, float11, float12, float13, float14);
                                        modelTxt.vertices.setElement(int5, 5, int6, int7, int8, int9);
                                    }
                                }

                                loadMode = ModelLoader.LoadMode.NumberOfFaces;
                                break;
                            case NumberOfFaces:
                                int2 = Integer.parseInt(string1);
                                modelTxt.elements = new int[int2 * 3];
                                loadMode = ModelLoader.LoadMode.FaceData;
                                break;
                            case FaceData:
                                for (int int19 = 0; int19 < int2; int19++) {
                                    String[] strings1 = string1.split(",");
                                    int int20 = Integer.parseInt(strings1[0].trim());
                                    int int21 = Integer.parseInt(strings1[1].trim());
                                    int int22 = Integer.parseInt(strings1[2].trim());
                                    if (boolean1) {
                                        modelTxt.elements[int19 * 3 + 2] = int20;
                                        modelTxt.elements[int19 * 3 + 1] = int21;
                                        modelTxt.elements[int19 * 3 + 0] = int22;
                                    } else {
                                        modelTxt.elements[int19 * 3 + 0] = int20;
                                        modelTxt.elements[int19 * 3 + 1] = int21;
                                        modelTxt.elements[int19 * 3 + 2] = int22;
                                    }

                                    string1 = bufferedReader.readLine();
                                }

                                loadMode = ModelLoader.LoadMode.NumberOfBones;
                                break;
                            case NumberOfBones:
                                int4 = Integer.parseInt(string1);
                                loadMode = ModelLoader.LoadMode.SkeletonHierarchy;
                                break;
                            case SkeletonHierarchy:
                                for (int int16 = 0; int16 < int4; int16++) {
                                    int int17 = Integer.parseInt(string1);
                                    string1 = bufferedReader.readLine();
                                    int int18 = Integer.parseInt(string1);
                                    string1 = bufferedReader.readLine();
                                    String string15 = sharedStringsx.get(string1);
                                    string1 = bufferedReader.readLine();
                                    modelTxt.SkeletonHierarchy.add(int18);
                                    modelTxt.boneIndices.put(string15, int17);
                                }

                                loadMode = ModelLoader.LoadMode.BindPose;
                                break;
                            case BindPose:
                                for (int int15 = 0; int15 < int4; int15++) {
                                    string1 = bufferedReader.readLine();
                                    String string12 = bufferedReader.readLine();
                                    String string13 = bufferedReader.readLine();
                                    String string14 = bufferedReader.readLine();
                                    modelTxt.bindPose.add(int15, this.getMatrix(string1, string12, string13, string14));
                                    string1 = bufferedReader.readLine();
                                }

                                loadMode = ModelLoader.LoadMode.InvBindPose;
                                break;
                            case InvBindPose:
                                for (int int14 = 0; int14 < int4; int14++) {
                                    string1 = bufferedReader.readLine();
                                    String string9 = bufferedReader.readLine();
                                    String string10 = bufferedReader.readLine();
                                    String string11 = bufferedReader.readLine();
                                    modelTxt.invBindPose.add(int14, this.getMatrix(string1, string9, string10, string11));
                                    string1 = bufferedReader.readLine();
                                }

                                loadMode = ModelLoader.LoadMode.SkinOffsetMatrices;
                                break;
                            case SkinOffsetMatrices:
                                for (int int13 = 0; int13 < int4; int13++) {
                                    string1 = bufferedReader.readLine();
                                    String string6 = bufferedReader.readLine();
                                    String string7 = bufferedReader.readLine();
                                    String string8 = bufferedReader.readLine();
                                    modelTxt.skinOffsetMatrices.add(int13, this.getMatrix(string1, string6, string7, string8));
                                    string1 = bufferedReader.readLine();
                                }

                                loadMode = ModelLoader.LoadMode.NumberOfAnims;
                                break;
                            case NumberOfAnims:
                                int3 = Integer.parseInt(string1);
                                loadMode = ModelLoader.LoadMode.Anim;
                                break;
                            case Anim:
                                ArrayList arrayList0 = new ArrayList();
                                string1 = bufferedReader.readLine();
                                float float15 = Float.parseFloat(string1);
                                string1 = bufferedReader.readLine();
                                int int10 = Integer.parseInt(string1);
                                string1 = bufferedReader.readLine();

                                for (int int11 = 0; int11 < int10; int11++) {
                                    Keyframe keyframe0 = new Keyframe();
                                    int int12 = Integer.parseInt(string1);
                                    String string2 = bufferedReader.readLine();
                                    String string3 = sharedStringsx.get(string2);
                                    String string4 = bufferedReader.readLine();
                                    float float16 = Float.parseFloat(string4);
                                    string1 = bufferedReader.readLine();
                                    String string5 = bufferedReader.readLine();
                                    Vector3f vector3f = this.getVector(string1);
                                    Quaternion quaternion = this.getQuaternion(string5);
                                    if (int11 < int10 - 1) {
                                        string1 = bufferedReader.readLine();
                                    }

                                    keyframe0.Bone = int12;
                                    keyframe0.BoneName = string3;
                                    keyframe0.Time = float16;
                                    keyframe0.Rotation = quaternion;
                                    keyframe0.Position = new Vector3f(vector3f);
                                    arrayList0.add(keyframe0);
                                }

                                AnimationClip animationClip0 = new AnimationClip(float15, arrayList0, string1, false);
                                arrayList0.clear();
                                if (ModelManager.instance.bCreateSoftwareMeshes) {
                                    animationClip0.staticClip = new StaticAnimation(animationClip0);
                                }

                                modelTxt.clips.put(string1, animationClip0);
                        }
                    }
                }
            }

            if (!boolean0 && skinningData != null) {
                try {
                    int[] ints = new int[modelTxt.boneIndices.size()];
                    ArrayList arrayList1 = modelTxt.SkeletonHierarchy;
                    HashMap hashMap0 = modelTxt.boneIndices;
                    HashMap hashMap1 = new HashMap<>(skinningData.BoneIndices);
                    ArrayList arrayList2 = new ArrayList<>(skinningData.SkeletonHierarchy);
                    hashMap0.forEach((string, integer) -> {
                        int int0x = hashMap1.getOrDefault(string, -1);
                        if (int0x == -1) {
                            int0x = hashMap1.size();
                            hashMap1.put(string, int0x);
                            int int1x = (Integer)arrayList1.get(integer);
                            if (int1x >= 0) {
                                arrayList2.add(ints[int1x]);
                            } else {
                                byte byte0 = -2;
                            }
                        }

                        ints[integer] = int0x;
                    });
                    modelTxt.boneIndices = hashMap1;
                    modelTxt.SkeletonHierarchy = arrayList2;

                    for (int int23 = 0; int23 < modelTxt.vertices.m_numVertices; int23++) {
                        int int24 = (int)modelTxt.vertices.getElementFloat(int23, 5, 0);
                        int int25 = (int)modelTxt.vertices.getElementFloat(int23, 5, 1);
                        int int26 = (int)modelTxt.vertices.getElementFloat(int23, 5, 2);
                        int int27 = (int)modelTxt.vertices.getElementFloat(int23, 5, 3);
                        if (int24 >= 0) {
                            int24 = ints[int24];
                        }

                        if (int25 >= 0) {
                            int25 = ints[int25];
                        }

                        if (int26 >= 0) {
                            int26 = ints[int26];
                        }

                        if (int27 >= 0) {
                            int27 = ints[int27];
                        }

                        modelTxt.vertices.setElement(int23, 5, int24, int25, int26, int27);
                    }

                    for (AnimationClip animationClip1 : modelTxt.clips.values()) {
                        for (Keyframe keyframe1 : animationClip1.getKeyframes()) {
                            keyframe1.Bone = ints[keyframe1.Bone];
                        }
                    }

                    modelTxt.skinOffsetMatrices = this.RemapMatrices(ints, modelTxt.skinOffsetMatrices, modelTxt.boneIndices.size());
                    modelTxt.bindPose = this.RemapMatrices(ints, modelTxt.bindPose, modelTxt.boneIndices.size());
                    modelTxt.invBindPose = this.RemapMatrices(ints, modelTxt.invBindPose, modelTxt.boneIndices.size());
                } catch (Exception exception) {
                    exception.toString();
                }
            }
        }

        return modelTxt;
    }

    protected void applyToMesh(ModelTxt modelTxt, ModelMesh modelMesh, SkinningData skinningData) {
        if (modelTxt.bStatic) {
            if (!ModelManager.NoOpenGL) {
                modelMesh.m_bHasVBO = true;
                RenderThread.queueInvokeOnRenderContext(() -> {
                    modelMesh.SetVertexBuffer(new VertexBufferObject(modelTxt.vertices, modelTxt.elements));
                    if (ModelManager.instance.bCreateSoftwareMeshes) {
                        modelMesh.softwareMesh.vb = modelMesh.vb;
                    }
                });
            }
        } else {
            modelMesh.skinningData = new SkinningData(
                modelTxt.clips, modelTxt.bindPose, modelTxt.invBindPose, modelTxt.skinOffsetMatrices, modelTxt.SkeletonHierarchy, modelTxt.boneIndices
            );
            if (!ModelManager.NoOpenGL) {
                modelMesh.m_bHasVBO = true;
                RenderThread.queueInvokeOnRenderContext(() -> {
                    modelMesh.SetVertexBuffer(new VertexBufferObject(modelTxt.vertices, modelTxt.elements, modelTxt.bReverse));
                    if (ModelManager.instance.bCreateSoftwareMeshes) {
                    }
                });
            }
        }

        if (skinningData != null) {
            modelMesh.skinningData.AnimationClips = skinningData.AnimationClips;
        }
    }

    protected void applyToAnimation(ModelTxt modelTxt, AnimationAsset animationAsset) {
        animationAsset.AnimationClips = modelTxt.clips;
        animationAsset.assetParams.animationsMesh.skinningData.AnimationClips.putAll(modelTxt.clips);
    }

    private ArrayList<Matrix4f> RemapMatrices(int[] ints, ArrayList<Matrix4f> arrayList1, int int0) {
        ArrayList arrayList0 = new ArrayList(int0);
        Matrix4f matrix4f = new Matrix4f();

        for (int int1 = 0; int1 < int0; int1++) {
            arrayList0.add(matrix4f);
        }

        for (int int2 = 0; int2 < ints.length; int2++) {
            arrayList0.set(ints[int2], (Matrix4f)arrayList1.get(int2));
        }

        return arrayList0;
    }

    private Vector3f getVector(String string) {
        Vector3f vector3f = new Vector3f();
        String[] strings = string.split(",");
        vector3f.x = Float.parseFloat(strings[0]);
        vector3f.y = Float.parseFloat(strings[1]);
        vector3f.z = Float.parseFloat(strings[2]);
        return vector3f;
    }

    private Quaternion getQuaternion(String string) {
        Quaternion quaternion = new Quaternion();
        String[] strings = string.split(",");
        quaternion.x = Float.parseFloat(strings[0]);
        quaternion.y = Float.parseFloat(strings[1]);
        quaternion.z = Float.parseFloat(strings[2]);
        quaternion.w = Float.parseFloat(strings[3]);
        return quaternion;
    }

    private Matrix4f getMatrix(String string0, String string1, String string2, String string3) {
        Matrix4f matrix4f = new Matrix4f();
        boolean boolean0 = false;
        String[] strings = string0.split(",");
        matrix4f.m00 = Float.parseFloat(strings[0]);
        matrix4f.m01 = Float.parseFloat(strings[1]);
        matrix4f.m02 = Float.parseFloat(strings[2]);
        matrix4f.m03 = Float.parseFloat(strings[3]);
        strings = string1.split(",");
        matrix4f.m10 = Float.parseFloat(strings[0]);
        matrix4f.m11 = Float.parseFloat(strings[1]);
        matrix4f.m12 = Float.parseFloat(strings[2]);
        matrix4f.m13 = Float.parseFloat(strings[3]);
        strings = string2.split(",");
        matrix4f.m20 = Float.parseFloat(strings[0]);
        matrix4f.m21 = Float.parseFloat(strings[1]);
        matrix4f.m22 = Float.parseFloat(strings[2]);
        matrix4f.m23 = Float.parseFloat(strings[3]);
        strings = string3.split(",");
        matrix4f.m30 = Float.parseFloat(strings[0]);
        matrix4f.m31 = Float.parseFloat(strings[1]);
        matrix4f.m32 = Float.parseFloat(strings[2]);
        matrix4f.m33 = Float.parseFloat(strings[3]);
        return matrix4f;
    }

    public static enum LoadMode {
        Version,
        ModelName,
        VertexStrideElementCount,
        VertexStrideSize,
        VertexStrideData,
        VertexCount,
        VertexBuffer,
        NumberOfFaces,
        FaceData,
        NumberOfBones,
        SkeletonHierarchy,
        BindPose,
        InvBindPose,
        SkinOffsetMatrices,
        NumberOfAnims,
        Anim;
    }
}
