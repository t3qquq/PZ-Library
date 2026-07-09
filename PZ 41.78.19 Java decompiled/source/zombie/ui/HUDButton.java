// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import zombie.core.textures.Texture;
import zombie.network.GameServer;

public class HUDButton extends UIElement {
    boolean clicked = false;
    UIElement display;
    Texture highlight;
    Texture overicon = null;
    boolean mouseOver = false;
    String name;
    Texture texture;
    UIEventHandler handler;
    public float notclickedAlpha = 0.85F;
    public float clickedalpha = 1.0F;

    public HUDButton(String _name, double x, double y, String _texture, String _highlight, UIElement _display) {
        if (!GameServer.bServer) {
            this.display = _display;
            this.name = _name;
            if (this.texture == null) {
                this.texture = Texture.getSharedTexture(_texture);
                this.highlight = Texture.getSharedTexture(_highlight);
            }

            this.x = x;
            this.y = y;
            this.width = this.texture.getWidth();
            this.height = this.texture.getHeight();
        }
    }

    public HUDButton(String _name, float x, float y, String _texture, String _highlight, UIEventHandler _handler) {
        if (!GameServer.bServer) {
            this.texture = Texture.getSharedTexture(_texture);
            this.highlight = Texture.getSharedTexture(_highlight);
            this.handler = _handler;
            this.name = _name;
            if (this.texture == null) {
                this.texture = Texture.getSharedTexture(_texture);
                this.highlight = Texture.getSharedTexture(_highlight);
            }

            this.x = x;
            this.y = y;
            this.width = this.texture.getWidth();
            this.height = this.texture.getHeight();
        }
    }

    public HUDButton(String _name, float x, float y, String _texture, String _highlight, String _overicon, UIElement _display) {
        if (!GameServer.bServer) {
            this.overicon = Texture.getSharedTexture(_overicon);
            this.display = _display;
            this.texture = Texture.getSharedTexture(_texture);
            this.highlight = Texture.getSharedTexture(_highlight);
            this.name = _name;
            if (this.texture == null) {
                this.texture = Texture.getSharedTexture(_texture);
                this.highlight = Texture.getSharedTexture(_highlight);
            }

            this.x = x;
            this.y = y;
            this.width = this.texture.getWidth();
            this.height = this.texture.getHeight();
        }
    }

    public HUDButton(String _name, float x, float y, String _texture, String _highlight, String _overicon, UIEventHandler _handler) {
        if (!GameServer.bServer) {
            this.texture = Texture.getSharedTexture(_texture);
            this.highlight = Texture.getSharedTexture(_highlight);
            this.overicon = Texture.getSharedTexture(_overicon);
            this.handler = _handler;
            this.name = _name;
            if (this.texture == null) {
                this.texture = Texture.getSharedTexture(_texture);
                this.highlight = Texture.getSharedTexture(_highlight);
            }

            this.x = x;
            this.y = y;
            this.width = this.texture.getWidth();
            this.height = this.texture.getHeight();
        }
    }

    @Override
    public Boolean onMouseDown(double x, double y) {
        this.clicked = true;
        return Boolean.TRUE;
    }

    @Override
    public Boolean onMouseMove(double dx, double dy) {
        this.mouseOver = true;
        return Boolean.TRUE;
    }

    @Override
    public void onMouseMoveOutside(double dx, double dy) {
        this.clicked = false;
        if (this.display != null) {
            if (!this.name.equals(this.display.getClickedValue())) {
                this.mouseOver = false;
            }
        }
    }

    @Override
    public Boolean onMouseUp(double x, double y) {
        if (this.clicked) {
            if (this.display != null) {
                this.display.ButtonClicked(this.name);
            } else if (this.handler != null) {
                this.handler.Selected(this.name, 0, 0);
            }
        }

        this.clicked = false;
        return Boolean.TRUE;
    }

    @Override
    public void render() {
        int int0 = 0;
        if (this.clicked) {
            int0++;
        }

        if (!this.mouseOver && !this.name.equals(this.display.getClickedValue())) {
            this.DrawTextureScaled(this.texture, 0.0, int0, this.getWidth(), this.getHeight(), this.notclickedAlpha);
        } else {
            this.DrawTextureScaled(this.highlight, 0.0, int0, this.getWidth(), this.getHeight(), this.clickedalpha);
        }

        if (this.overicon != null) {
            this.DrawTextureScaled(this.overicon, 0.0, int0, this.overicon.getWidth(), this.overicon.getHeight(), 1.0);
        }

        super.render();
    }

    @Override
    public void update() {
        super.update();
    }
}
