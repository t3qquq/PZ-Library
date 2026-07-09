// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.ui.UIManager;

public class IsoLuaMover extends IsoGameCharacter {
    public KahluaTable luaMoverTable;

    public IsoLuaMover(KahluaTable table) {
        super(null, 0.0F, 0.0F, 0.0F);
        this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
        this.luaMoverTable = table;
        if (this.def == null) {
            this.def = IsoSpriteInstance.get(this.sprite);
        }
    }

    public void playAnim(String name, float seconds, boolean looped, boolean playing) {
        this.sprite.PlayAnim(name);
        float float0 = this.sprite.CurrentAnim.Frames.size();
        float float1 = 1000.0F / float0;
        float float2 = float1 * seconds;
        this.def.AnimFrameIncrease = float2 * GameTime.getInstance().getMultiplier();
        this.def.Finished = !playing;
        this.def.Looped = looped;
    }

    @Override
    public String getObjectName() {
        return "IsoLuaMover";
    }

    @Override
    public void update() {
        try {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget("update"), this.luaMoverTable);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.sprite.update(this.def);
        super.update();
    }

    @Override
    public void render(float x, float y, float z, ColorInfo col, boolean bDoAttached, boolean bWallLightingPass, Shader shader) {
        float float0 = this.offsetY;
        float0 -= 100.0F;
        float float1 = this.offsetX;
        float1 -= 34.0F;
        this.sprite.render(this.def, this, this.x, this.y, this.z, this.dir, float1, float0, col, true);

        try {
            LuaManager.caller.pcallvoid(UIManager.getDefaultThread(), this.luaMoverTable.rawget("postrender"), this.luaMoverTable, col, bDoAttached);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
