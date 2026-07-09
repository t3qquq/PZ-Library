// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.Texture;

import zombie.core.textures.Texture;

public class Texture2D {
    Texture tex;

    public Texture2D(Texture texture) {
        this.tex = texture;
    }

    public int getWidth() {
        return this.tex.getWidth();
    }

    public int getHeight() {
        return this.tex.getHeight();
    }

    public int getTexture() {
        return this.tex.getID();
    }

    public void Apply() {
    }
}
