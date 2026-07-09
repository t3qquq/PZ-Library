// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.runtime;

import java.util.ArrayList;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.scripting.ScriptParser;
import zombie.scripting.objects.BaseScriptObject;

public final class RuntimeAnimationScript extends BaseScriptObject {
    protected String m_name = this.toString();
    protected final ArrayList<IRuntimeAnimationCommand> m_commands = new ArrayList<>();

    public void Load(String name, String totalFile) {
        this.m_name = name;
        ScriptParser.Block block0 = ScriptParser.parse(totalFile);
        block0 = block0.children.get(0);

        for (ScriptParser.Value value : block0.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("xxx".equals(string0)) {
            }
        }

        for (ScriptParser.Block block1 : block0.children) {
            if ("CopyFrame".equals(block1.type)) {
                CopyFrame copyFrame = new CopyFrame();
                copyFrame.parse(block1);
                this.m_commands.add(copyFrame);
            } else if ("CopyFrames".equals(block1.type)) {
                CopyFrames copyFrames = new CopyFrames();
                copyFrames.parse(block1);
                this.m_commands.add(copyFrames);
            }
        }
    }

    public void exec() {
        ArrayList arrayList = new ArrayList();

        for (IRuntimeAnimationCommand iRuntimeAnimationCommand : this.m_commands) {
            iRuntimeAnimationCommand.exec(arrayList);
        }

        float float0 = 0.0F;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            float0 = Math.max(float0, ((Keyframe)arrayList.get(int0)).Time);
        }

        AnimationClip animationClip = new AnimationClip(float0, arrayList, this.m_name, true);
        arrayList.clear();
        ModelManager.instance.addAnimationClip(animationClip.Name, animationClip);
        arrayList.clear();
    }

    public void reset() {
        this.m_name = this.toString();
        this.m_commands.clear();
    }
}
