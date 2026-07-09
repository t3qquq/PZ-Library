// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.Texture;

import java.util.HashMap;
import zombie.core.textures.Texture;

public class TextureManager {
    public static TextureManager Instance = new TextureManager();
    public HashMap<String, Texture2D> Textures = new HashMap<>();

    public boolean AddTexture(String string) {
        Texture texture = Texture.getSharedTexture(string);
        if (texture == null) {
            return false;
        } else {
            this.Textures.put(string, new Texture2D(texture));
            return true;
        }
    }

    public void AddTexture(String string, Texture texture) {
        if (!this.Textures.containsKey(string)) {
            this.Textures.put(string, new Texture2D(texture));
        }
    }
}
