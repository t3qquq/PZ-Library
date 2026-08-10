// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.util.vector.Matrix4f;
import zombie.core.skinnedmodel.animation.AnimationClip;

public final class ModelTxt {
    boolean bStatic;
    boolean bReverse;
    VertexBufferObject.VertexArray vertices;
    int[] elements;
    HashMap<String, Integer> boneIndices = new HashMap<>();
    ArrayList<Integer> SkeletonHierarchy = new ArrayList<>();
    ArrayList<Matrix4f> bindPose = new ArrayList<>();
    ArrayList<Matrix4f> skinOffsetMatrices = new ArrayList<>();
    ArrayList<Matrix4f> invBindPose = new ArrayList<>();
    HashMap<String, AnimationClip> clips = new HashMap<>();
}
