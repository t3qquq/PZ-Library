// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

public final class TextureCombinerShaderParam {
    public String name;
    public float min;
    public float max;

    public TextureCombinerShaderParam(String string, float float0, float float1) {
        this.name = string;
        this.min = float0;
        this.max = float1;
    }

    public TextureCombinerShaderParam(String string, float float0) {
        this.name = string;
        this.min = float0;
        this.max = float0;
    }
}
