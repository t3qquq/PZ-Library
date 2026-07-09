// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.shader;

import java.util.ArrayList;

public final class ShaderManager {
    public static final ShaderManager instance = new ShaderManager();
    private final ArrayList<Shader> shaders = new ArrayList<>();

    private Shader getShader(String string, boolean boolean0) {
        for (int int0 = 0; int0 < this.shaders.size(); int0++) {
            Shader shader = this.shaders.get(int0);
            if (string.equals(shader.name) && boolean0 == shader.bStatic) {
                return shader;
            }
        }

        return null;
    }

    public Shader getOrCreateShader(String string, boolean boolean0) {
        Shader shader0 = this.getShader(string, boolean0);
        if (shader0 != null) {
            return shader0;
        } else {
            for (int int0 = 0; int0 < this.shaders.size(); int0++) {
                Shader shader1 = this.shaders.get(int0);
                if (shader1.name.equalsIgnoreCase(string) && !shader1.name.equals(string)) {
                    throw new IllegalArgumentException("shader filenames are case-sensitive");
                }
            }

            shader0 = new Shader(string, boolean0);
            this.shaders.add(shader0);
            return shader0;
        }
    }
}
