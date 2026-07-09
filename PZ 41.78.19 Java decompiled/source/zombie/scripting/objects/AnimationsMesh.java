// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting.objects;

import java.util.ArrayList;
import zombie.core.skinnedmodel.model.ModelMesh;
import zombie.scripting.ScriptParser;

public final class AnimationsMesh extends BaseScriptObject {
    public String name = null;
    public String meshFile = null;
    public final ArrayList<String> animationDirectories = new ArrayList<>();
    public ModelMesh modelMesh = null;

    public void Load(String _name, String totalFile) {
        this.name = _name;
        ScriptParser.Block block = ScriptParser.parse(totalFile);
        block = block.children.get(0);

        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("meshFile".equalsIgnoreCase(string0)) {
                this.meshFile = string1;
            } else if ("animationDirectory".equalsIgnoreCase(string0)) {
                this.animationDirectories.add(string1);
            }
        }
    }

    public void reset() {
        this.meshFile = null;
        this.animationDirectories.clear();
        this.modelMesh = null;
    }
}
