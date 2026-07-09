// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;
import zombie.ai.State;
import zombie.ai.StateMachine;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.action.ActionState;
import zombie.core.logger.LoggerManager;
import zombie.core.skinnedmodel.advancedanimation.AnimState;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.debug.DebugLog;
import zombie.iso.Vector2;
import zombie.iso.Vector3;

/**
 * Used for recording the activity of an AnimationPlayer
 */
public final class AnimationPlayerRecorder {
    private boolean m_isRecording = false;
    private final AnimationTrackRecordingFrame m_animationTrackFrame;
    private final AnimationNodeRecordingFrame m_animationNodeFrame;
    private final AnimationVariableRecordingFrame m_animationVariableFrame;
    private final IsoGameCharacter m_character;
    private static String s_startupTimeStamp = null;
    private static final SimpleDateFormat s_fileNameSdf = new SimpleDateFormat("yy-MM-dd_HH-mm");

    public AnimationPlayerRecorder(IsoGameCharacter owner) {
        this.m_character = owner;
        String string0 = this.m_character.getUID();
        String string1 = string0 + "_AnimRecorder";
        this.m_animationTrackFrame = new AnimationTrackRecordingFrame(string1 + "_Track");
        this.m_animationNodeFrame = new AnimationNodeRecordingFrame(string1 + "_Node");
        this.m_animationVariableFrame = new AnimationVariableRecordingFrame(string1 + "_Vars");
    }

    public void beginLine(int frameNo) {
        this.m_animationTrackFrame.reset();
        this.m_animationTrackFrame.setFrameNumber(frameNo);
        this.m_animationNodeFrame.reset();
        this.m_animationNodeFrame.setFrameNumber(frameNo);
        this.m_animationVariableFrame.reset();
        this.m_animationVariableFrame.setFrameNumber(frameNo);
    }

    public void endLine() {
        this.m_animationTrackFrame.writeLine();
        this.m_animationNodeFrame.writeLine();
        this.m_animationVariableFrame.writeLine();
    }

    public void discardRecording() {
        this.m_animationTrackFrame.closeAndDiscard();
        this.m_animationNodeFrame.closeAndDiscard();
        this.m_animationVariableFrame.closeAndDiscard();
    }

    public static PrintStream openFileStream(String key, boolean append, Consumer<String> fileNameConsumer) {
        String string = getTimeStampedFilePath(key);

        try {
            fileNameConsumer.accept(string);
            File file = new File(string);
            return new PrintStream(new FileOutputStream(file, append));
        } catch (FileNotFoundException fileNotFoundException) {
            DebugLog.General.error("Exception thrown trying to create animation player recording file.");
            DebugLog.General.error(fileNotFoundException);
            fileNotFoundException.printStackTrace();
            return null;
        }
    }

    private static String getTimeStampedFilePath(String string) {
        return LoggerManager.getLogsDir() + File.separator + getTimeStampedFileName(string) + ".csv";
    }

    private static String getTimeStampedFileName(String string) {
        return getStartupTimeStamp() + "_" + string;
    }

    private static String getStartupTimeStamp() {
        if (s_startupTimeStamp == null) {
            s_startupTimeStamp = s_fileNameSdf.format(Calendar.getInstance().getTime());
        }

        return s_startupTimeStamp;
    }

    public void logAnimWeights(List<AnimationTrack> list, int[] ints, float[] floats, Vector2 vector) {
        this.m_animationTrackFrame.logAnimWeights(list, ints, floats, vector);
    }

    public void logAnimNode(LiveAnimNode liveNode) {
        if (liveNode.isTransitioningIn()) {
            this.m_animationNodeFrame
                .logWeight(
                    "transition(" + liveNode.getTransitionFrom() + "->" + liveNode.getName() + ")",
                    liveNode.getTransitionLayerIdx(),
                    liveNode.getTransitionInWeight()
                );
        }

        this.m_animationNodeFrame.logWeight(liveNode.getName(), liveNode.getLayerIdx(), liveNode.getWeight());
    }

    public void logActionState(ActionState state, List<ActionState> childStates) {
        this.m_animationNodeFrame.logActionState(state, childStates);
    }

    public void logAIState(State state, List<StateMachine.SubstateSlot> subStates) {
        this.m_animationNodeFrame.logAIState(state, subStates);
    }

    public void logAnimState(AnimState state) {
        this.m_animationNodeFrame.logAnimState(state);
    }

    public void logVariables(IAnimationVariableSource varSource) {
        this.m_animationVariableFrame.logVariables(varSource);
    }

    public void logCharacterPos() {
        IsoPlayer player = IsoPlayer.getInstance();
        IsoGameCharacter character = this.getOwner();
        Vector3 vector0 = player.getPosition(new Vector3());
        Vector3 vector1 = character.getPosition(new Vector3());
        Vector3 vector2 = vector0.sub(vector1, new Vector3());
        this.m_animationNodeFrame.logCharacterToPlayerDiff(vector2);
    }

    public IsoGameCharacter getOwner() {
        return this.m_character;
    }

    public boolean isRecording() {
        return this.m_isRecording;
    }

    public void setRecording(boolean value) {
        if (this.m_isRecording != value) {
            this.m_isRecording = value;
            DebugLog.General.println("AnimationPlayerRecorder %s.", this.m_isRecording ? "recording" : "stopped");
        }
    }
}
