// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import zombie.Lua.LuaManager;
import zombie.core.textures.Texture;

public final class GenericButton extends UIElement {
    public boolean clicked = false;
    public UIElement MessageTarget;
    public boolean mouseOver = false;
    public String name;
    public String text;
    Texture UpTexture = null;
    Texture DownTexture = null;
    private UIEventHandler MessageTarget2 = null;

    public GenericButton(
        UIElement uIElement, float float0, float float1, float float2, float float3, String string0, String string1, Texture texture0, Texture texture1
    ) {
        this.x = float0;
        this.y = float1;
        this.MessageTarget = uIElement;
        this.name = string0;
        this.text = string1;
        this.width = float2;
        this.height = float3;
        this.UpTexture = texture0;
        this.DownTexture = texture1;
    }

    public GenericButton(
        UIEventHandler uIEventHandler,
        float float0,
        float float1,
        float float2,
        float float3,
        String string0,
        String string1,
        Texture texture0,
        Texture texture1
    ) {
        this.x = float0;
        this.y = float1;
        this.MessageTarget2 = uIEventHandler;
        this.name = string0;
        this.text = string1;
        this.width = float2;
        this.height = float3;
        this.UpTexture = texture0;
        this.DownTexture = texture1;
    }

    @Override
    public Boolean onMouseDown(double double1, double double0) {
        if (!this.isVisible()) {
            return Boolean.FALSE;
        } else {
            if (this.getTable() != null && this.getTable().rawget("onMouseDown") != null) {
                Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseDown"), this.table, double1, double0);
            }

            this.clicked = true;
            return Boolean.TRUE;
        }
    }

    @Override
    public Boolean onMouseMove(double double1, double double0) {
        if (this.getTable() != null && this.getTable().rawget("onMouseMove") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseMove"), this.table, double1, double0);
        }

        this.mouseOver = true;
        return Boolean.TRUE;
    }

    @Override
    public void onMouseMoveOutside(double double1, double double0) {
        if (this.getTable() != null && this.getTable().rawget("onMouseMoveOutside") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseMoveOutside"), this.table, double1, double0);
        }

        this.clicked = false;
        this.mouseOver = false;
    }

    @Override
    public Boolean onMouseUp(double double1, double double0) {
        if (this.getTable() != null && this.getTable().rawget("onMouseUp") != null) {
            Object[] objects = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onMouseUp"), this.table, double1, double0);
        }

        if (this.clicked) {
            if (this.MessageTarget2 != null) {
                this.MessageTarget2.Selected(this.name, 0, 0);
            } else {
                this.MessageTarget.ButtonClicked(this.name);
            }
        }

        this.clicked = false;
        return Boolean.TRUE;
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            boolean boolean0 = false;
            if (this.clicked) {
                this.DrawTextureScaled(this.DownTexture, 0.0, 0.0, this.getWidth(), this.getHeight(), 1.0);
                this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
            } else {
                this.DrawTextureScaled(this.UpTexture, 0.0, 0.0, this.getWidth(), this.getHeight(), 1.0);
                this.DrawTextCentre(this.text, this.getWidth() / 2.0, 1.0, 1.0, 1.0, 1.0, 1.0);
            }

            super.render();
        }
    }
}
