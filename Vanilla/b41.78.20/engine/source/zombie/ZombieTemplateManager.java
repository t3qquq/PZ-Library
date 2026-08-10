// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.core.textures.Texture;

public class ZombieTemplateManager {
    public Texture addOverlayToTexture(ArrayList<ZombieTemplateManager.BodyOverlay> var1, Texture var2) {
        return null;
    }

    public class BodyOverlay {
        public BodyPartType location;
        public ZombieTemplateManager.OverlayType type;
    }

    public static enum OverlayType {
        BloodLight,
        BloodMedium,
        BloodHeavy;
    }

    public class ZombieTemplate {
        public Texture tex;
    }
}
