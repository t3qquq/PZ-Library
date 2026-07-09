// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite;

import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.HashMap;
import zombie.core.Color;
import zombie.debug.DebugLog;
import zombie.iso.SpriteDetails.IsoFlagType;

public final class IsoSpriteManager {
    public static final IsoSpriteManager instance = new IsoSpriteManager();
    public final HashMap<String, IsoSprite> NamedMap = new HashMap<>();
    public final TIntObjectHashMap<IsoSprite> IntMap = new TIntObjectHashMap<>();
    private final IsoSprite emptySprite = new IsoSprite(this);

    public IsoSpriteManager() {
        IsoSprite sprite = this.emptySprite;
        sprite.name = "";
        sprite.ID = -1;
        sprite.Properties.Set(IsoFlagType.invisible);
        sprite.CurrentAnim = new IsoAnim();
        sprite.CurrentAnim.ID = sprite.AnimStack.size();
        sprite.AnimStack.add(sprite.CurrentAnim);
        sprite.AnimMap.put("default", sprite.CurrentAnim);
        this.NamedMap.put(sprite.name, sprite);
    }

    public void Dispose() {
        IsoSprite.DisposeAll();
        IsoAnim.DisposeAll();
        Object[] objects = this.IntMap.values();

        for (int int0 = 0; int0 < objects.length; int0++) {
            IsoSprite sprite = (IsoSprite)objects[int0];
            sprite.Dispose();
            sprite.def = null;
            sprite.parentManager = null;
        }

        this.IntMap.clear();
        this.NamedMap.clear();
        this.NamedMap.put(this.emptySprite.name, this.emptySprite);
    }

    public IsoSprite getSprite(int gid) {
        return this.IntMap.containsKey(gid) ? this.IntMap.get(gid) : null;
    }

    public IsoSprite getSprite(String gid) {
        return this.NamedMap.containsKey(gid) ? this.NamedMap.get(gid) : this.AddSprite(gid);
    }

    public IsoSprite getOrAddSpriteCache(String tex) {
        if (this.NamedMap.containsKey(tex)) {
            return this.NamedMap.get(tex);
        } else {
            IsoSprite sprite = new IsoSprite(this);
            sprite.LoadFramesNoDirPageSimple(tex);
            this.NamedMap.put(tex, sprite);
            return sprite;
        }
    }

    public IsoSprite getOrAddSpriteCache(String tex, Color col) {
        int int0 = (int)(col.r * 255.0F);
        int int1 = (int)(col.g * 255.0F);
        int int2 = (int)(col.b * 255.0F);
        String string = tex + "_" + int0 + "_" + int1 + "_" + int2;
        if (this.NamedMap.containsKey(string)) {
            return this.NamedMap.get(string);
        } else {
            IsoSprite sprite = new IsoSprite(this);
            sprite.LoadFramesNoDirPageSimple(tex);
            this.NamedMap.put(string, sprite);
            return sprite;
        }
    }

    public IsoSprite AddSprite(String tex) {
        IsoSprite sprite = new IsoSprite(this);
        sprite.LoadFramesNoDirPageSimple(tex);
        this.NamedMap.put(tex, sprite);
        return sprite;
    }

    public IsoSprite AddSprite(String tex, int ID) {
        IsoSprite sprite = new IsoSprite(this);
        sprite.LoadFramesNoDirPageSimple(tex);
        if (this.NamedMap.containsKey(tex)) {
            DebugLog.log("duplicate texture " + tex + " ignore ID=" + ID + ", use ID=" + this.NamedMap.get(tex).ID);
            ID = this.NamedMap.get(tex).ID;
        }

        this.NamedMap.put(tex, sprite);
        sprite.ID = ID;
        this.IntMap.put(ID, sprite);
        return sprite;
    }
}
