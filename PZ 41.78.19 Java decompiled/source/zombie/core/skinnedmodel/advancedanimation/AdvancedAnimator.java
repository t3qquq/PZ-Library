// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.w3c.dom.Element;
import zombie.DebugFileWatcher;
import zombie.GameProfiler;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.CharacterActionAnims;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.util.Lambda;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.list.PZArrayList;
import zombie.util.list.PZArrayUtil;

/**
 * Created by LEMMYMAIN on 26/01/2015.
 */
public final class AdvancedAnimator implements IAnimEventCallback {
    private IAnimatable character;
    public AnimationSet animSet;
    public final ArrayList<IAnimEventCallback> animCallbackHandlers = new ArrayList<>();
    private AnimLayer m_rootLayer = null;
    private final List<AdvancedAnimator.SubLayerSlot> m_subLayers = new ArrayList<>();
    public static float s_MotionScale = 0.76F;
    public static float s_RotationScale = 0.76F;
    private AnimatorDebugMonitor debugMonitor;
    private static long animSetModificationTime = -1L;
    private static long actionGroupModificationTime = -1L;
    private AnimationPlayerRecorder m_recorder = null;

    public static void systemInit() {
        DebugFileWatcher.instance
            .add(new PredicatedFileWatcher("media/AnimSets", AdvancedAnimator::isAnimSetFilePath, AdvancedAnimator::onAnimSetsRefreshTriggered));
        DebugFileWatcher.instance
            .add(new PredicatedFileWatcher("media/actiongroups", AdvancedAnimator::isActionGroupFilePath, AdvancedAnimator::onActionGroupsRefreshTriggered));
        LoadDefaults();
    }

    private static boolean isAnimSetFilePath(String string0) {
        if (string0 == null) {
            return false;
        } else if (!string0.endsWith(".xml")) {
            return false;
        } else {
            ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                String string1 = (String)arrayList.get(int0);
                ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string1);
                if (mod != null && mod.animSetsFile != null && string0.startsWith(mod.animSetsFile.getPath())) {
                    return true;
                }
            }

            String string2 = ZomboidFileSystem.instance.getAnimSetsPath();
            return string0.startsWith(string2);
        }
    }

    private static boolean isActionGroupFilePath(String string0) {
        if (string0 == null) {
            return false;
        } else if (!string0.endsWith(".xml")) {
            return false;
        } else {
            ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                String string1 = (String)arrayList.get(int0);
                ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string1);
                if (mod != null && mod.actionGroupsFile != null && string0.startsWith(mod.actionGroupsFile.getPath())) {
                    return true;
                }
            }

            String string2 = ZomboidFileSystem.instance.getActionGroupsPath();
            return string0.startsWith(string2);
        }
    }

    private static void onActionGroupsRefreshTriggered(String string) {
        DebugLog.General.println("DebugFileWatcher Hit. ActionGroups: " + string);
        actionGroupModificationTime = System.currentTimeMillis() + 1000L;
    }

    private static void onAnimSetsRefreshTriggered(String string) {
        DebugLog.General.println("DebugFileWatcher Hit. AnimSets: " + string);
        animSetModificationTime = System.currentTimeMillis() + 1000L;
    }

    public static void checkModifiedFiles() {
        if (animSetModificationTime != -1L && animSetModificationTime < System.currentTimeMillis()) {
            DebugLog.General.println("Refreshing AnimSets.");
            animSetModificationTime = -1L;
            LoadDefaults();
            LuaManager.GlobalObject.refreshAnimSets(true);
        }

        if (actionGroupModificationTime != -1L && actionGroupModificationTime < System.currentTimeMillis()) {
            DebugLog.General.println("Refreshing action groups.");
            actionGroupModificationTime = -1L;
            LuaManager.GlobalObject.reloadActionGroups();
        }
    }

    private static void LoadDefaults() {
        try {
            Element element = PZXmlUtil.parseXml("media/AnimSets/Defaults.xml");
            String string0 = element.getElementsByTagName("MotionScale").item(0).getTextContent();
            s_MotionScale = Float.parseFloat(string0);
            String string1 = element.getElementsByTagName("RotationScale").item(0).getTextContent();
            s_RotationScale = Float.parseFloat(string1);
        } catch (PZXmlParserException pZXmlParserException) {
            DebugLog.General.error("Exception thrown: " + pZXmlParserException);
            pZXmlParserException.printStackTrace();
        }
    }

    public String GetDebug() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("GameState: ");
        if (this.character instanceof IsoGameCharacter characterx) {
            stringBuilder.append(characterx.getCurrentState() == null ? "null" : characterx.getCurrentState().getClass().getSimpleName()).append("\n");
        }

        if (this.m_rootLayer != null) {
            stringBuilder.append("Layer: ").append(0).append("\n");
            stringBuilder.append(this.m_rootLayer.GetDebugString()).append("\n");
        }

        stringBuilder.append("Variables:\n");
        stringBuilder.append("Weapon: ").append(this.character.getVariableString("weapon")).append("\n");
        stringBuilder.append("Aim: ").append(this.character.getVariableString("aim")).append("\n");

        for (IAnimationVariableSlot iAnimationVariableSlot : this.character.getGameVariables()) {
            stringBuilder.append("  ").append(iAnimationVariableSlot.getKey()).append(" : ").append(iAnimationVariableSlot.getValueString()).append("\n");
        }

        return stringBuilder.toString();
    }

    public void OnAnimDataChanged(boolean reload) {
        if (reload && this.character instanceof IsoGameCharacter characterx) {
            characterx.getStateMachine().activeStateChanged++;
            characterx.setDefaultState();
            if (characterx instanceof IsoZombie) {
                characterx.setOnFloor(false);
            }

            characterx.getStateMachine().activeStateChanged--;
        }

        this.SetAnimSet(AnimationSet.GetAnimationSet(this.character.GetAnimSetName(), false));
        if (this.character.getAnimationPlayer() != null) {
            this.character.getAnimationPlayer().reset();
        }

        if (this.m_rootLayer != null) {
            this.m_rootLayer.Reset();
        }

        for (int int0 = 0; int0 < this.m_subLayers.size(); int0++) {
            AdvancedAnimator.SubLayerSlot subLayerSlot = this.m_subLayers.get(int0);
            subLayerSlot.animLayer.Reset();
        }
    }

    public void Reload() {
    }

    public void init(IAnimatable _character) {
        this.character = _character;
        this.m_rootLayer = new AnimLayer(_character, this);
    }

    public void SetAnimSet(AnimationSet aset) {
        this.animSet = aset;
    }

    @Override
    public void OnAnimEvent(AnimLayer sender, AnimEvent event) {
        for (int int0 = 0; int0 < this.animCallbackHandlers.size(); int0++) {
            IAnimEventCallback iAnimEventCallback = this.animCallbackHandlers.get(int0);
            iAnimEventCallback.OnAnimEvent(sender, event);
        }
    }

    public String getCurrentStateName() {
        return this.m_rootLayer == null ? null : this.m_rootLayer.getCurrentStateName();
    }

    public boolean containsState(String stateName) {
        return this.animSet != null && this.animSet.containsState(stateName);
    }

    public void SetState(String stateName) {
        this.SetState(stateName, PZArrayList.emptyList());
    }

    public void SetState(String stateName, List<String> subStateNames) {
        if (this.animSet == null) {
            DebugLog.Animation.error("(" + stateName + ") Cannot set state. AnimSet is null.");
        } else {
            if (!this.animSet.containsState(stateName)) {
                DebugLog.Animation.error("State not found: " + stateName);
            }

            this.m_rootLayer.TransitionTo(this.animSet.GetState(stateName), false);
            PZArrayUtil.forEach(this.m_subLayers, subLayerSlot -> subLayerSlot.shouldBeActive = false);
            Lambda.forEachFrom(PZArrayUtil::forEach, subStateNames, this, (string, advancedAnimator) -> {
                AdvancedAnimator.SubLayerSlot subLayerSlot = advancedAnimator.getOrCreateSlot(string);
                subLayerSlot.transitionTo(advancedAnimator.animSet.GetState(string), false);
            });
            PZArrayUtil.forEach(this.m_subLayers, AdvancedAnimator.SubLayerSlot::applyTransition);
        }
    }

    protected AdvancedAnimator.SubLayerSlot getOrCreateSlot(String string) {
        AdvancedAnimator.SubLayerSlot subLayerSlot0 = null;
        int int0 = 0;

        for (int int1 = this.m_subLayers.size(); int0 < int1; int0++) {
            AdvancedAnimator.SubLayerSlot subLayerSlot1 = this.m_subLayers.get(int0);
            if (subLayerSlot1.animLayer.isCurrentState(string)) {
                subLayerSlot0 = subLayerSlot1;
                break;
            }
        }

        if (subLayerSlot0 != null) {
            return subLayerSlot0;
        } else {
            int0 = 0;

            for (int int2 = this.m_subLayers.size(); int0 < int2; int0++) {
                AdvancedAnimator.SubLayerSlot subLayerSlot2 = this.m_subLayers.get(int0);
                if (subLayerSlot2.animLayer.isStateless()) {
                    subLayerSlot0 = subLayerSlot2;
                    break;
                }
            }

            if (subLayerSlot0 != null) {
                return subLayerSlot0;
            } else {
                AdvancedAnimator.SubLayerSlot subLayerSlot3 = new AdvancedAnimator.SubLayerSlot(this.m_rootLayer, this.character, this);
                this.m_subLayers.add(subLayerSlot3);
                return subLayerSlot3;
            }
        }
    }

    public void update() {
        GameProfiler.getInstance().invokeAndMeasure("AdvancedAnimator.Update", this, AdvancedAnimator::updateInternal);
    }

    private void updateInternal() {
        if (this.character.getAnimationPlayer() != null) {
            if (this.character.getAnimationPlayer().isReady()) {
                if (this.animSet != null) {
                    if (!this.m_rootLayer.hasState()) {
                        this.m_rootLayer.TransitionTo(this.animSet.GetState("Idle"), true);
                    }

                    this.m_rootLayer.Update();

                    for (int int0 = 0; int0 < this.m_subLayers.size(); int0++) {
                        AdvancedAnimator.SubLayerSlot subLayerSlot0 = this.m_subLayers.get(int0);
                        subLayerSlot0.update();
                    }

                    if (this.debugMonitor != null && this.character instanceof IsoGameCharacter) {
                        int int1 = 1 + this.getActiveSubLayerCount();
                        AnimLayer[] animLayers = new AnimLayer[int1];
                        animLayers[0] = this.m_rootLayer;
                        int1 = 0;

                        for (int int2 = 0; int2 < this.m_subLayers.size(); int2++) {
                            AdvancedAnimator.SubLayerSlot subLayerSlot1 = this.m_subLayers.get(int2);
                            if (subLayerSlot1.shouldBeActive) {
                                animLayers[1 + int1] = subLayerSlot1.animLayer;
                                int1++;
                            }
                        }

                        this.debugMonitor.update((IsoGameCharacter)this.character, animLayers);
                    }
                }
            }
        }
    }

    public void render() {
        if (this.character.getAnimationPlayer() != null) {
            if (this.character.getAnimationPlayer().isReady()) {
                if (this.animSet != null) {
                    if (this.m_rootLayer.hasState()) {
                        this.m_rootLayer.render();
                    }
                }
            }
        }
    }

    public void printDebugCharacterActions(String target) {
        if (this.animSet != null) {
            AnimState animState = this.animSet.GetState("actions");
            if (animState != null) {
                boolean boolean0 = false;
                boolean boolean1 = false;

                for (CharacterActionAnims characterActionAnims : CharacterActionAnims.values()) {
                    boolean0 = false;
                    String string;
                    if (characterActionAnims == CharacterActionAnims.None) {
                        string = target;
                        boolean0 = true;
                    } else {
                        string = characterActionAnims.toString();
                    }

                    boolean boolean2 = false;

                    for (AnimNode animNode : animState.m_Nodes) {
                        for (AnimCondition animCondition : animNode.m_Conditions) {
                            if (animCondition.m_Type == AnimCondition.Type.STRING
                                && animCondition.m_Name.toLowerCase().equals("performingaction")
                                && animCondition.m_StringValue.equalsIgnoreCase(string)) {
                                boolean2 = true;
                                break;
                            }
                        }

                        if (boolean2) {
                            break;
                        }
                    }

                    if (boolean2) {
                        if (boolean0) {
                            boolean1 = true;
                        }
                    } else {
                        DebugLog.log("WARNING: did not find node with condition 'PerformingAction = " + string + "' in player/actions/");
                    }
                }

                if (boolean1) {
                    if (DebugLog.isEnabled(DebugType.Animation)) {
                        DebugLog.Animation.debugln("SUCCESS - Current 'actions' TargetNode: '" + target + "' was found.");
                    }
                } else if (DebugLog.isEnabled(DebugType.Animation)) {
                    DebugLog.Animation.debugln("FAIL - Current 'actions' TargetNode: '" + target + "' not found.");
                }
            }
        }
    }

    public ArrayList<String> debugGetVariables() {
        ArrayList arrayList = new ArrayList();
        if (this.animSet != null) {
            for (Entry entry : this.animSet.states.entrySet()) {
                AnimState animState = (AnimState)entry.getValue();

                for (AnimNode animNode : animState.m_Nodes) {
                    for (AnimCondition animCondition : animNode.m_Conditions) {
                        if (animCondition.m_Name != null && !arrayList.contains(animCondition.m_Name.toLowerCase())) {
                            arrayList.add(animCondition.m_Name.toLowerCase());
                        }
                    }
                }
            }
        }

        return arrayList;
    }

    public AnimatorDebugMonitor getDebugMonitor() {
        return this.debugMonitor;
    }

    public void setDebugMonitor(AnimatorDebugMonitor monitor) {
        this.debugMonitor = monitor;
    }

    public IAnimatable getCharacter() {
        return this.character;
    }

    public void updateSpeedScale(String variable, float newSpeed) {
        if (this.m_rootLayer != null) {
            List list = this.m_rootLayer.getLiveAnimNodes();

            for (int int0 = 0; int0 < list.size(); int0++) {
                LiveAnimNode liveAnimNode = (LiveAnimNode)list.get(int0);
                if (liveAnimNode.isActive() && liveAnimNode.getSourceNode() != null && variable.equals(liveAnimNode.getSourceNode().m_SpeedScaleVariable)) {
                    liveAnimNode.getSourceNode().m_SpeedScale = newSpeed + "";

                    for (int int1 = 0; int1 < liveAnimNode.m_AnimationTracks.size(); int1++) {
                        liveAnimNode.m_AnimationTracks.get(int1).SpeedDelta = newSpeed;
                    }
                }
            }
        }
    }

    /**
     * Returns TRUE if any Actuve Live nodes are an Idle animation.  This is useful when determining if the character is currently Idle.
     */
    public boolean containsAnyIdleNodes() {
        if (this.m_rootLayer == null) {
            return false;
        } else {
            boolean boolean0 = false;
            List list = this.m_rootLayer.getLiveAnimNodes();

            for (int int0 = 0; int0 < list.size() && !boolean0; int0++) {
                boolean0 = ((LiveAnimNode)list.get(int0)).isIdleAnimActive();
            }

            for (int int1 = 0; int1 < this.getSubLayerCount(); int1++) {
                AnimLayer animLayer = this.getSubLayerAt(int1);
                list = animLayer.getLiveAnimNodes();

                for (int int2 = 0; int2 < list.size(); int2++) {
                    boolean0 = ((LiveAnimNode)list.get(int2)).isIdleAnimActive();
                    if (!boolean0) {
                        break;
                    }
                }
            }

            return boolean0;
        }
    }

    public AnimLayer getRootLayer() {
        return this.m_rootLayer;
    }

    public int getSubLayerCount() {
        return this.m_subLayers.size();
    }

    public AnimLayer getSubLayerAt(int idx) {
        return this.m_subLayers.get(idx).animLayer;
    }

    public int getActiveSubLayerCount() {
        int int0 = 0;

        for (int int1 = 0; int1 < this.m_subLayers.size(); int1++) {
            AdvancedAnimator.SubLayerSlot subLayerSlot = this.m_subLayers.get(int1);
            if (subLayerSlot.shouldBeActive) {
                int0++;
            }
        }

        return int0;
    }

    public void setRecorder(AnimationPlayerRecorder recorder) {
        this.m_recorder = recorder;
    }

    public boolean isRecording() {
        return this.m_recorder != null && this.m_recorder.isRecording();
    }

    public static class SubLayerSlot {
        public boolean shouldBeActive = false;
        public final AnimLayer animLayer;

        public SubLayerSlot(AnimLayer animLayerx, IAnimatable iAnimatable, IAnimEventCallback iAnimEventCallback) {
            this.animLayer = new AnimLayer(animLayerx, iAnimatable, iAnimEventCallback);
        }

        public void update() {
            this.animLayer.Update();
        }

        public void transitionTo(AnimState animState, boolean boolean0) {
            this.animLayer.TransitionTo(animState, boolean0);
            this.shouldBeActive = animState != null;
        }

        public void applyTransition() {
            if (!this.shouldBeActive) {
                this.transitionTo(null, false);
            }
        }
    }
}
