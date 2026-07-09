// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.HashMap;
import zombie.util.StringUtils;

public class AnimationVariableHandlePool {
    private static final Object s_threadLock = "AnimationVariableHandlePool.ThreadLock";
    private static HashMap<String, AnimationVariableHandle> s_handlePool = new HashMap<>();
    private static int s_globalIndexGenerator = 0;

    public static AnimationVariableHandle getOrCreate(String string) {
        synchronized (s_threadLock) {
            return getOrCreateInternal(string);
        }
    }

    private static AnimationVariableHandle getOrCreateInternal(String string) {
        if (!isVariableNameValid(string)) {
            return null;
        } else {
            AnimationVariableHandle animationVariableHandle0 = s_handlePool.get(string);
            if (animationVariableHandle0 != null) {
                return animationVariableHandle0;
            } else {
                AnimationVariableHandle animationVariableHandle1 = new AnimationVariableHandle();
                animationVariableHandle1.setVariableName(string);
                animationVariableHandle1.setVariableIndex(generateNewVariableIndex());
                s_handlePool.put(string, animationVariableHandle1);
                return animationVariableHandle1;
            }
        }
    }

    private static boolean isVariableNameValid(String string) {
        return !StringUtils.isNullOrWhitespace(string);
    }

    private static int generateNewVariableIndex() {
        return s_globalIndexGenerator++;
    }
}
