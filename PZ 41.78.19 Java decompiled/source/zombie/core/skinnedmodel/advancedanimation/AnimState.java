// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import zombie.ZomboidFileSystem;
import zombie.asset.AssetPath;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.util.StringUtils;

public final class AnimState {
    public String m_Name = "";
    public final List<AnimNode> m_Nodes = new ArrayList<>();
    public int m_DefaultIndex = 0;
    public AnimationSet m_Set = null;
    private static final boolean s_bDebugLog_NodeConditions = false;

    public List<AnimNode> getAnimNodes(IAnimationVariableSource varSource, List<AnimNode> nodes) {
        nodes.clear();
        if (this.m_Nodes.size() <= 0) {
            return nodes;
        } else if (DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue()
            && varSource.getVariableBoolean("dbgForceAnim")
            && varSource.isVariable("dbgForceAnimStateName", this.m_Name)) {
            String string = varSource.getVariableString("dbgForceAnimNodeName");
            int int0 = 0;

            for (int int1 = this.m_Nodes.size(); int0 < int1; int0++) {
                AnimNode animNode0 = this.m_Nodes.get(int0);
                if (StringUtils.equalsIgnoreCase(animNode0.m_Name, string)) {
                    nodes.add(animNode0);
                    break;
                }
            }

            return nodes;
        } else {
            int int2 = -1;
            int int3 = 0;

            for (int int4 = this.m_Nodes.size(); int3 < int4; int3++) {
                AnimNode animNode1 = this.m_Nodes.get(int3);
                if (!animNode1.isAbstract() && animNode1.m_Conditions.size() >= int2 && animNode1.checkConditions(varSource)) {
                    if (int2 < animNode1.m_Conditions.size()) {
                        nodes.clear();
                        int2 = animNode1.m_Conditions.size();
                    }

                    nodes.add(animNode1);
                }
            }

            if (!nodes.isEmpty()) {
            }

            return nodes;
        }
    }

    public static AnimState Parse(String name, String statePath) {
        boolean boolean0 = DebugLog.isEnabled(DebugType.Animation);
        AnimState animState = new AnimState();
        animState.m_Name = name;
        if (boolean0) {
            DebugLog.Animation.println("Loading AnimState: " + name);
        }

        String[] strings = ZomboidFileSystem.instance.resolveAllFiles(statePath, filex -> filex.getName().endsWith(".xml"), true);

        for (String string0 : strings) {
            File file = new File(string0);
            String string1 = file.getName().split(".xml")[0].toLowerCase();
            if (boolean0) {
                DebugLog.Animation.println(name + " -> AnimNode: " + string1);
            }

            String string2 = ZomboidFileSystem.instance.resolveFileOrGUID(string0);
            AnimNodeAsset animNodeAsset = (AnimNodeAsset)AnimNodeAssetManager.instance.load(new AssetPath(string2));
            if (animNodeAsset.isReady()) {
                AnimNode animNode = animNodeAsset.m_animNode;
                animNode.m_State = animState;
                animState.m_Nodes.add(animNode);
            }
        }

        return animState;
    }

    @Override
    public String toString() {
        return "AnimState{" + this.m_Name + ", NodeCount:" + this.m_Nodes.size() + ", DefaultIndex:" + this.m_DefaultIndex + "}";
    }

    /**
     * Null-safe function that returns a given state's name.  If null, returns a null
     */
    public static String getStateName(AnimState state) {
        return state != null ? state.m_Name : null;
    }

    protected void clear() {
        this.m_Nodes.clear();
        this.m_Set = null;
    }
}
