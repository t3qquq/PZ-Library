// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Map.Entry;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.core.skinnedmodel.animation.AnimationTrack;

public final class AnimatorDebugMonitor {
    private static final ArrayList<String> knownVariables = new ArrayList<>();
    private static boolean knownVarsDirty = false;
    private String currentState = "null";
    private AnimatorDebugMonitor.MonitoredLayer[] monitoredLayers;
    private final HashMap<String, AnimatorDebugMonitor.MonitoredVar> monitoredVariables = new HashMap<>();
    private final ArrayList<String> customVariables = new ArrayList<>();
    private final LinkedList<AnimatorDebugMonitor.MonitorLogLine> logLines = new LinkedList<>();
    private final Queue<AnimatorDebugMonitor.MonitorLogLine> logLineQueue = new LinkedList<>();
    private boolean floatsListDirty = false;
    private boolean hasFilterChanges = false;
    private boolean hasLogUpdates = false;
    private String logString = "";
    private static final int maxLogSize = 1028;
    private static final int maxOutputLines = 128;
    private static final int maxFloatCache = 1024;
    private final ArrayList<Float> floatsOut = new ArrayList<>();
    private AnimatorDebugMonitor.MonitoredVar selectedVariable;
    private int tickCount = 0;
    private boolean doTickStamps = false;
    private static final int tickStampLength = 10;
    private static final Color col_curstate = Colors.Cyan;
    private static final Color col_layer_nodename = Colors.CornFlowerBlue;
    private static final Color col_layer_activated = Colors.DarkTurquoise;
    private static final Color col_layer_deactivated = Colors.Orange;
    private static final Color col_track_activated = Colors.SandyBrown;
    private static final Color col_track_deactivated = Colors.Salmon;
    private static final Color col_node_activated = Colors.Pink;
    private static final Color col_node_deactivated = Colors.Plum;
    private static final Color col_var_activated = Colors.Chartreuse;
    private static final Color col_var_changed = Colors.LimeGreen;
    private static final Color col_var_deactivated = Colors.Gold;
    private static final String TAG_VAR = "[variable]";
    private static final String TAG_LAYER = "[layer]";
    private static final String TAG_NODE = "[active_nodes]";
    private static final String TAG_TRACK = "[anim_tracks]";
    private boolean[] logFlags = new boolean[AnimatorDebugMonitor.LogType.MAX.value()];

    public AnimatorDebugMonitor(IsoGameCharacter chr) {
        this.logFlags[AnimatorDebugMonitor.LogType.DEFAULT.value()] = true;

        for (int int0 = 0; int0 < this.logFlags.length; int0++) {
            this.logFlags[int0] = true;
        }

        for (int int1 = 0; int1 < 1024; int1++) {
            this.floatsOut.add(0.0F);
        }

        this.initCustomVars();
        if (chr != null && chr.advancedAnimator != null) {
            for (String string : chr.advancedAnimator.debugGetVariables()) {
                registerVariable(string);
            }
        }
    }

    private void initCustomVars() {
        this.addCustomVariable("aim");
        this.addCustomVariable("bdead");
        this.addCustomVariable("bfalling");
        this.addCustomVariable("baimatfloor");
        this.addCustomVariable("battackfrombehind");
        this.addCustomVariable("attacktype");
        this.addCustomVariable("bundervehicle");
        this.addCustomVariable("reanimatetimer");
        this.addCustomVariable("isattacking");
        this.addCustomVariable("canclimbdownrope");
        this.addCustomVariable("frombehind");
        this.addCustomVariable("fallonfront");
        this.addCustomVariable("hashitreaction");
        this.addCustomVariable("hitreaction");
        this.addCustomVariable("collided");
        this.addCustomVariable("collidetype");
        this.addCustomVariable("intrees");
    }

    public void addCustomVariable(String var) {
        String string = var.toLowerCase();
        if (!this.customVariables.contains(string)) {
            this.customVariables.add(string);
        }

        registerVariable(var);
    }

    public void removeCustomVariable(String var) {
        String string = var.toLowerCase();
        this.customVariables.remove(string);
    }

    public void setFilter(int index, boolean b) {
        if (index >= 0 && index < AnimatorDebugMonitor.LogType.MAX.value()) {
            this.logFlags[index] = b;
            this.hasFilterChanges = true;
        }
    }

    public boolean getFilter(int index) {
        return index >= 0 && index < AnimatorDebugMonitor.LogType.MAX.value() ? this.logFlags[index] : false;
    }

    public boolean isDoTickStamps() {
        return this.doTickStamps;
    }

    public void setDoTickStamps(boolean _doTickStamps) {
        if (this.doTickStamps != _doTickStamps) {
            this.doTickStamps = _doTickStamps;
            this.hasFilterChanges = true;
        }
    }

    private void queueLogLine(String string) {
        this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, null, true);
    }

    private void queueLogLine(String string, Color color) {
        this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, color, true);
    }

    private void queueLogLine(AnimatorDebugMonitor.LogType logType, String string, Color color) {
        this.addLogLine(logType, string, color, true);
    }

    private void addLogLine(String string) {
        this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, null, false);
    }

    private void addLogLine(String string, Color color) {
        this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, color, false);
    }

    private void addLogLine(String string, Color color, boolean boolean0) {
        this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, color, boolean0);
    }

    private void addLogLine(AnimatorDebugMonitor.LogType logType, String string, Color color) {
        this.addLogLine(logType, string, color, false);
    }

    private void addLogLine(AnimatorDebugMonitor.LogType logType, String string, Color color, boolean boolean0) {
        AnimatorDebugMonitor.MonitorLogLine monitorLogLine = new AnimatorDebugMonitor.MonitorLogLine();
        monitorLogLine.line = string;
        monitorLogLine.color = color;
        monitorLogLine.type = logType;
        monitorLogLine.tick = this.tickCount;
        if (boolean0) {
            this.logLineQueue.add(monitorLogLine);
        } else {
            this.log(monitorLogLine);
        }
    }

    private void log(AnimatorDebugMonitor.MonitorLogLine monitorLogLine) {
        this.logLines.addFirst(monitorLogLine);
        if (this.logLines.size() > 1028) {
            this.logLines.removeLast();
        }

        this.hasLogUpdates = true;
    }

    private void processQueue() {
        while (this.logLineQueue.size() > 0) {
            AnimatorDebugMonitor.MonitorLogLine monitorLogLine = this.logLineQueue.poll();
            this.log(monitorLogLine);
        }
    }

    private void preUpdate() {
        for (Entry entry0 : this.monitoredVariables.entrySet()) {
            ((AnimatorDebugMonitor.MonitoredVar)entry0.getValue()).updated = false;
        }

        for (int int0 = 0; int0 < this.monitoredLayers.length; int0++) {
            AnimatorDebugMonitor.MonitoredLayer monitoredLayer = this.monitoredLayers[int0];
            monitoredLayer.updated = false;

            for (Entry entry1 : monitoredLayer.activeNodes.entrySet()) {
                ((AnimatorDebugMonitor.MonitoredNode)entry1.getValue()).updated = false;
            }

            for (Entry entry2 : monitoredLayer.animTracks.entrySet()) {
                ((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).updated = false;
            }
        }
    }

    private void postUpdate() {
        for (Entry entry0 : this.monitoredVariables.entrySet()) {
            if (((AnimatorDebugMonitor.MonitoredVar)entry0.getValue()).active && !((AnimatorDebugMonitor.MonitoredVar)entry0.getValue()).updated) {
                this.addLogLine(
                    AnimatorDebugMonitor.LogType.VAR,
                    "[variable] : removed -> '"
                        + (String)entry0.getKey()
                        + "', last value: '"
                        + ((AnimatorDebugMonitor.MonitoredVar)entry0.getValue()).value
                        + "'.",
                    col_var_deactivated
                );
                ((AnimatorDebugMonitor.MonitoredVar)entry0.getValue()).active = false;
            }
        }

        for (int int0 = 0; int0 < this.monitoredLayers.length; int0++) {
            AnimatorDebugMonitor.MonitoredLayer monitoredLayer = this.monitoredLayers[int0];

            for (Entry entry1 : monitoredLayer.activeNodes.entrySet()) {
                if (((AnimatorDebugMonitor.MonitoredNode)entry1.getValue()).active && !((AnimatorDebugMonitor.MonitoredNode)entry1.getValue()).updated) {
                    this.addLogLine(
                        AnimatorDebugMonitor.LogType.NODE,
                        "[layer]["
                            + monitoredLayer.index
                            + "] [active_nodes] : deactivated -> '"
                            + ((AnimatorDebugMonitor.MonitoredNode)entry1.getValue()).name
                            + "'.",
                        col_node_deactivated
                    );
                    ((AnimatorDebugMonitor.MonitoredNode)entry1.getValue()).active = false;
                }
            }

            for (Entry entry2 : monitoredLayer.animTracks.entrySet()) {
                if (((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).active && !((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).updated) {
                    this.addLogLine(
                        AnimatorDebugMonitor.LogType.TRACK,
                        "[layer]["
                            + monitoredLayer.index
                            + "] [anim_tracks] : deactivated -> '"
                            + ((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).name
                            + "'.",
                        col_track_deactivated
                    );
                    ((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).active = false;
                }
            }

            if (monitoredLayer.active && !monitoredLayer.updated) {
                this.addLogLine(
                    AnimatorDebugMonitor.LogType.LAYER,
                    "[layer][" + int0 + "] : deactivated (last animstate: '" + monitoredLayer.nodeName + "').",
                    col_layer_deactivated
                );
                monitoredLayer.active = false;
            }
        }
    }

    public void update(IsoGameCharacter character, AnimLayer[] animLayers) {
        if (character != null) {
            this.ensureLayers(animLayers);
            this.preUpdate();

            for (IAnimationVariableSlot iAnimationVariableSlot : character.getGameVariables()) {
                this.updateVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueString());
            }

            for (String string0 : this.customVariables) {
                String string1 = character.getVariableString(string0);
                if (string1 != null) {
                    this.updateVariable(string0, string1);
                }
            }

            this.updateCurrentState(character.getCurrentState() == null ? "null" : character.getCurrentState().getClass().getSimpleName());

            for (int int0 = 0; int0 < animLayers.length; int0++) {
                if (animLayers[int0] != null) {
                    this.updateLayer(int0, animLayers[int0]);
                }
            }

            this.postUpdate();
            this.processQueue();
            this.tickCount++;
        }
    }

    private void updateCurrentState(String string) {
        if (!this.currentState.equals(string)) {
            this.queueLogLine("Character.currentState changed from '" + this.currentState + "' to: '" + string + "'.", col_curstate);
            this.currentState = string;
        }
    }

    private void updateLayer(int int0, AnimLayer animLayer) {
        AnimatorDebugMonitor.MonitoredLayer monitoredLayer = this.monitoredLayers[int0];
        String string = animLayer.getDebugNodeName();
        if (!monitoredLayer.active) {
            monitoredLayer.active = true;
            this.queueLogLine(AnimatorDebugMonitor.LogType.LAYER, "[layer][" + int0 + "] activated -> animstate: '" + string + "'.", col_layer_activated);
        }

        if (!monitoredLayer.nodeName.equals(string)) {
            this.queueLogLine(
                AnimatorDebugMonitor.LogType.LAYER,
                "[layer][" + int0 + "] changed -> animstate from '" + monitoredLayer.nodeName + "' to: '" + string + "'.",
                col_layer_nodename
            );
            monitoredLayer.nodeName = string;
        }

        for (LiveAnimNode liveAnimNode : animLayer.getLiveAnimNodes()) {
            this.updateActiveNode(monitoredLayer, liveAnimNode.getSourceNode().m_Name);
        }

        if (animLayer.getAnimationTrack() != null) {
            for (AnimationTrack animationTrack : animLayer.getAnimationTrack().getTracks()) {
                if (animationTrack.getLayerIdx() == int0) {
                    this.updateAnimTrack(monitoredLayer, animationTrack.name, animationTrack.BlendDelta);
                }
            }
        }

        monitoredLayer.updated = true;
    }

    private void updateActiveNode(AnimatorDebugMonitor.MonitoredLayer monitoredLayer, String string) {
        AnimatorDebugMonitor.MonitoredNode monitoredNode = monitoredLayer.activeNodes.get(string);
        if (monitoredNode == null) {
            monitoredNode = new AnimatorDebugMonitor.MonitoredNode();
            monitoredNode.name = string;
            monitoredLayer.activeNodes.put(string, monitoredNode);
        }

        if (!monitoredNode.active) {
            monitoredNode.active = true;
            this.queueLogLine(
                AnimatorDebugMonitor.LogType.NODE, "[layer][" + monitoredLayer.index + "] [active_nodes] : activated -> '" + string + "'.", col_node_activated
            );
        }

        monitoredNode.updated = true;
    }

    private void updateAnimTrack(AnimatorDebugMonitor.MonitoredLayer monitoredLayer, String string, float float0) {
        AnimatorDebugMonitor.MonitoredTrack monitoredTrack = monitoredLayer.animTracks.get(string);
        if (monitoredTrack == null) {
            monitoredTrack = new AnimatorDebugMonitor.MonitoredTrack();
            monitoredTrack.name = string;
            monitoredTrack.blendDelta = float0;
            monitoredLayer.animTracks.put(string, monitoredTrack);
        }

        if (!monitoredTrack.active) {
            monitoredTrack.active = true;
            this.queueLogLine(
                AnimatorDebugMonitor.LogType.TRACK, "[layer][" + monitoredLayer.index + "] [anim_tracks] : activated -> '" + string + "'.", col_track_activated
            );
        }

        if (monitoredTrack.blendDelta != float0) {
            monitoredTrack.blendDelta = float0;
        }

        monitoredTrack.updated = true;
    }

    private void updateVariable(String string0, String string1) {
        AnimatorDebugMonitor.MonitoredVar monitoredVar = this.monitoredVariables.get(string0);
        boolean boolean0 = false;
        if (monitoredVar == null) {
            monitoredVar = new AnimatorDebugMonitor.MonitoredVar();
            this.monitoredVariables.put(string0, monitoredVar);
            boolean0 = true;
        }

        if (!monitoredVar.active) {
            monitoredVar.active = true;
            monitoredVar.key = string0;
            monitoredVar.value = string1;
            this.queueLogLine(AnimatorDebugMonitor.LogType.VAR, "[variable] : added -> '" + string0 + "', value: '" + string1 + "'.", col_var_activated);
            if (boolean0) {
                registerVariable(string0);
            }
        } else if (string1 == null) {
            if (monitoredVar.isFloat) {
                monitoredVar.isFloat = false;
                this.floatsListDirty = true;
            }

            monitoredVar.value = null;
        } else if (monitoredVar.value == null || !monitoredVar.value.equals(string1)) {
            try {
                float float0 = Float.parseFloat(string1);
                monitoredVar.logFloat(float0);
                if (!monitoredVar.isFloat) {
                    monitoredVar.isFloat = true;
                    this.floatsListDirty = true;
                }
            } catch (NumberFormatException numberFormatException) {
                if (monitoredVar.isFloat) {
                    monitoredVar.isFloat = false;
                    this.floatsListDirty = true;
                }
            }

            if (!monitoredVar.isFloat) {
                this.queueLogLine(
                    AnimatorDebugMonitor.LogType.VAR,
                    "[variable] : updated -> '" + string0 + "' changed from '" + monitoredVar.value + "' to: '" + string1 + "'.",
                    col_var_changed
                );
            }

            monitoredVar.value = string1;
        }

        monitoredVar.updated = true;
    }

    private void buildLogString() {
        ListIterator listIterator = this.logLines.listIterator(0);
        int int0 = 0;
        int int1 = 0;

        while (listIterator.hasNext()) {
            AnimatorDebugMonitor.MonitorLogLine monitorLogLine0 = (AnimatorDebugMonitor.MonitorLogLine)listIterator.next();
            int1++;
            if (this.logFlags[monitorLogLine0.type.value()]) {
                if (++int0 >= 128) {
                    break;
                }
            }
        }

        if (int1 == 0) {
            this.logString = "";
        } else {
            listIterator = this.logLines.listIterator(int1);
            StringBuilder stringBuilder = new StringBuilder();

            while (listIterator.hasPrevious()) {
                AnimatorDebugMonitor.MonitorLogLine monitorLogLine1 = (AnimatorDebugMonitor.MonitorLogLine)listIterator.previous();
                if (this.logFlags[monitorLogLine1.type.value()]) {
                    stringBuilder.append(" <TEXT> ");
                    if (this.doTickStamps) {
                        stringBuilder.append("[");
                        stringBuilder.append(String.format("%010d", monitorLogLine1.tick));
                        stringBuilder.append("]");
                    }

                    if (monitorLogLine1.color != null) {
                        stringBuilder.append(" <RGB:");
                        stringBuilder.append(monitorLogLine1.color.r);
                        stringBuilder.append(",");
                        stringBuilder.append(monitorLogLine1.color.g);
                        stringBuilder.append(",");
                        stringBuilder.append(monitorLogLine1.color.b);
                        stringBuilder.append("> ");
                    }

                    stringBuilder.append(monitorLogLine1.line);
                    stringBuilder.append(" <LINE> ");
                }
            }

            this.logString = stringBuilder.toString();
            this.hasLogUpdates = false;
            this.hasFilterChanges = false;
        }
    }

    public boolean IsDirty() {
        return this.hasLogUpdates || this.hasFilterChanges;
    }

    public String getLogString() {
        if (this.hasLogUpdates || this.hasFilterChanges) {
            this.buildLogString();
        }

        return this.logString;
    }

    public boolean IsDirtyFloatList() {
        return this.floatsListDirty;
    }

    public ArrayList<String> getFloatNames() {
        this.floatsListDirty = false;
        ArrayList arrayList = new ArrayList();

        for (Entry entry : this.monitoredVariables.entrySet()) {
            if (((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).isFloat) {
                arrayList.add(((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).key);
            }
        }

        Collections.sort(arrayList);
        return arrayList;
    }

    public static boolean isKnownVarsDirty() {
        return knownVarsDirty;
    }

    public static List<String> getKnownVariables() {
        knownVarsDirty = false;
        Collections.sort(knownVariables);
        return knownVariables;
    }

    public void setSelectedVariable(String key) {
        if (key == null) {
            this.selectedVariable = null;
        } else {
            this.selectedVariable = this.monitoredVariables.get(key);
        }
    }

    public String getSelectedVariable() {
        return this.selectedVariable != null ? this.selectedVariable.key : null;
    }

    public float getSelectedVariableFloat() {
        return this.selectedVariable != null ? this.selectedVariable.valFloat : 0.0F;
    }

    public String getSelectedVarMinFloat() {
        return this.selectedVariable != null && this.selectedVariable.isFloat && this.selectedVariable.f_min != -1.0F
            ? this.selectedVariable.f_min + ""
            : "-1.0";
    }

    public String getSelectedVarMaxFloat() {
        return this.selectedVariable != null && this.selectedVariable.isFloat && this.selectedVariable.f_max != -1.0F
            ? this.selectedVariable.f_max + ""
            : "1.0";
    }

    public ArrayList<Float> getSelectedVarFloatList() {
        if (this.selectedVariable != null && this.selectedVariable.isFloat) {
            AnimatorDebugMonitor.MonitoredVar monitoredVar = this.selectedVariable;
            int int0 = monitoredVar.f_index - 1;
            if (int0 < 0) {
                int0 = 0;
            }

            float float0 = monitoredVar.f_max - monitoredVar.f_min;

            for (int int1 = 0; int1 < 1024; int1++) {
                float float1 = (monitoredVar.f_floats[int0--] - monitoredVar.f_min) / float0;
                this.floatsOut.set(int1, float1);
                if (int0 < 0) {
                    int0 = monitoredVar.f_floats.length - 1;
                }
            }

            return this.floatsOut;
        } else {
            return null;
        }
    }

    public static void registerVariable(String key) {
        if (key != null) {
            key = key.toLowerCase();
            if (!knownVariables.contains(key)) {
                knownVariables.add(key);
                knownVarsDirty = true;
            }
        }
    }

    private void ensureLayers(AnimLayer[] animLayers) {
        int int0 = animLayers.length;
        if (this.monitoredLayers == null || this.monitoredLayers.length != int0) {
            this.monitoredLayers = new AnimatorDebugMonitor.MonitoredLayer[int0];

            for (int int1 = 0; int1 < int0; int1++) {
                this.monitoredLayers[int1] = new AnimatorDebugMonitor.MonitoredLayer(int1);
            }
        }
    }

    private static enum LogType {
        DEFAULT(0),
        LAYER(1),
        NODE(2),
        TRACK(3),
        VAR(4),
        MAX(5);

        private final int val;

        private LogType(int int1) {
            this.val = int1;
        }

        public int value() {
            return this.val;
        }
    }

    private class MonitorLogLine {
        String line;
        Color color = null;
        AnimatorDebugMonitor.LogType type = AnimatorDebugMonitor.LogType.DEFAULT;
        int tick;
    }

    private class MonitoredLayer {
        int index;
        String nodeName = "";
        HashMap<String, AnimatorDebugMonitor.MonitoredNode> activeNodes = new HashMap<>();
        HashMap<String, AnimatorDebugMonitor.MonitoredTrack> animTracks = new HashMap<>();
        boolean active = false;
        boolean updated = false;

        public MonitoredLayer(int arg1) {
            this.index = arg1;
        }
    }

    private class MonitoredNode {
        String name = "";
        boolean active = false;
        boolean updated = false;
    }

    private class MonitoredTrack {
        String name = "";
        float blendDelta;
        boolean active = false;
        boolean updated = false;
    }

    private class MonitoredVar {
        String key = "";
        String value = "";
        boolean isFloat = false;
        float valFloat;
        boolean active = false;
        boolean updated = false;
        float[] f_floats;
        int f_index = 0;
        float f_min = -1.0F;
        float f_max = 1.0F;

        public void logFloat(float arg0) {
            if (this.f_floats == null) {
                this.f_floats = new float[1024];
            }

            if (arg0 != this.valFloat) {
                this.valFloat = arg0;
                this.f_floats[this.f_index++] = arg0;
                if (arg0 < this.f_min) {
                    this.f_min = arg0;
                }

                if (arg0 > this.f_max) {
                    this.f_max = arg0;
                }

                if (this.f_index >= 1024) {
                    this.f_index = 0;
                }
            }
        }
    }
}
