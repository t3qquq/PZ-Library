// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public final class AnimationSet {
    protected static final HashMap<String, AnimationSet> setMap = new HashMap<>();
    public final HashMap<String, AnimState> states = new HashMap<>();
    public String m_Name = "";

    public static AnimationSet GetAnimationSet(String name, boolean reload) {
        AnimationSet animationSet = setMap.get(name);
        if (animationSet != null && !reload) {
            return animationSet;
        } else {
            animationSet = new AnimationSet();
            animationSet.Load(name);
            setMap.put(name, animationSet);
            return animationSet;
        }
    }

    public static void Reset() {
        for (AnimationSet animationSet : setMap.values()) {
            animationSet.clear();
        }

        setMap.clear();
    }

    public AnimState GetState(String name) {
        AnimState animState = this.states.get(name.toLowerCase(Locale.ENGLISH));
        if (animState != null) {
            return animState;
        } else {
            DebugLog.Animation.warn("AnimState not found: " + name);
            return new AnimState();
        }
    }

    public boolean containsState(String name) {
        return this.states.containsKey(name.toLowerCase(Locale.ENGLISH));
    }

    public boolean Load(String name) {
        if (DebugLog.isEnabled(DebugType.Animation)) {
            DebugLog.Animation.println("Loading AnimSet: " + name);
        }

        this.m_Name = name;
        String[] strings = ZomboidFileSystem.instance.resolveAllDirectories("media/AnimSets/" + name, var0 -> true, false);

        for (String string0 : strings) {
            String string1 = new File(string0).getName();
            AnimState animState = AnimState.Parse(string1, string0);
            animState.m_Set = this;
            this.states.put(string1, animState);
        }

        return true;
    }

    private void clear() {
        for (AnimState animState : this.states.values()) {
            animState.clear();
        }

        this.states.clear();
    }
}
