// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.visual;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.core.skinnedmodel.model.Model;
import zombie.scripting.objects.ModelScript;

public abstract class BaseVisual {
    public abstract void save(ByteBuffer output) throws IOException;

    public abstract void load(ByteBuffer input, int WorldVersion) throws IOException;

    public abstract Model getModel();

    public abstract ModelScript getModelScript();

    public abstract void clear();

    public abstract void copyFrom(BaseVisual other);

    public abstract void dressInNamedOutfit(String outfitName, ItemVisuals itemVisuals);
}
