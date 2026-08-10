// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.runtime;

import java.util.List;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.scripting.ScriptParser;

public interface IRuntimeAnimationCommand {
    void parse(ScriptParser.Block var1);

    void exec(List<Keyframe> var1);
}
