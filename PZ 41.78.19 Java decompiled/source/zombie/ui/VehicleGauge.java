// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

public final class VehicleGauge extends UIElement {
    protected int needleX;
    protected int needleY;
    protected float minAngle;
    protected float maxAngle;
    protected float value;
    protected Texture texture;
    protected int needleWidth = 45;

    public VehicleGauge(Texture _texture, int _needleX, int _needleY, float _minAngle, float _maxAngle) {
        this.texture = _texture;
        this.needleX = _needleX;
        this.needleY = _needleY;
        this.minAngle = _minAngle;
        this.maxAngle = _maxAngle;
        this.width = _texture.getWidth();
        this.height = _texture.getHeight();
    }

    public void setNeedleWidth(int newSize) {
        this.needleWidth = newSize;
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            super.render();
            this.DrawTexture(this.texture, 0.0, 0.0, 1.0);
            double double0 = this.minAngle < this.maxAngle
                ? Math.toRadians(this.minAngle + (this.maxAngle - this.minAngle) * this.value)
                : Math.toRadians(this.maxAngle + (this.maxAngle - this.minAngle) * (1.0F - this.value));
            double double1 = this.needleX;
            double double2 = this.needleY;
            double double3 = this.needleX + this.needleWidth * Math.cos(double0);
            double double4 = Math.ceil(this.needleY + this.needleWidth * Math.sin(double0));
            int int0 = this.getAbsoluteX().intValue();
            int int1 = this.getAbsoluteY().intValue();
            SpriteRenderer.instance
                .renderline(null, int0 + (int)double1, int1 + (int)double2, int0 + (int)double3, int1 + (int)double4, 1.0F, 0.0F, 0.0F, 1.0F);
        }
    }

    public void setValue(float _value) {
        this.value = Math.min(_value, 1.0F);
    }

    public void setTexture(Texture newText) {
        this.texture = newText;
    }
}
