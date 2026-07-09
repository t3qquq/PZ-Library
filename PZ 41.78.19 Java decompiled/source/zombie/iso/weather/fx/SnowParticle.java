// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather.fx;

import zombie.core.Rand;
import zombie.core.textures.Texture;

public class SnowParticle extends WeatherParticle {
    private double angleRadians = 0.0;
    private float lastAngle = -1.0F;
    private float lastIntensity = -1.0F;
    protected float angleOffset = 0.0F;
    private float alphaMod = 0.0F;
    private float incarnateAlpha = 1.0F;
    private float life = 0.0F;
    private float fadeTime = 80.0F;
    private float tmpAngle = 0.0F;

    public SnowParticle(Texture texture) {
        super(texture);
        this.recalcSizeOnZoom = true;
        this.zoomMultiW = 1.0F;
        this.zoomMultiH = 1.0F;
    }

    protected void setLife() {
        this.life = this.fadeTime + Rand.Next(60, 500);
    }

    @Override
    public void update(float float0) {
        if (this.lastAngle != IsoWeatherFX.instance.windAngle || this.lastIntensity != IsoWeatherFX.instance.windPrecipIntensity.value()) {
            this.tmpAngle = IsoWeatherFX.instance.windAngle + (this.angleOffset - this.angleOffset * 0.5F * IsoWeatherFX.instance.windPrecipIntensity.value());
            if (this.tmpAngle > 360.0F) {
                this.tmpAngle -= 360.0F;
            }

            if (this.tmpAngle < 0.0F) {
                this.tmpAngle += 360.0F;
            }

            this.angleRadians = Math.toRadians(this.tmpAngle);
            this.velocity.set((float)Math.cos(this.angleRadians) * this.speed, (float)Math.sin(this.angleRadians) * this.speed);
            this.lastAngle = IsoWeatherFX.instance.windAngle;
        }

        if (this.life >= this.fadeTime) {
            this.position.x = this.position.x + this.velocity.x * IsoWeatherFX.instance.windSpeed * float0;
            this.position.y = this.position.y + this.velocity.y * IsoWeatherFX.instance.windSpeed * float0;
        } else {
            this.incarnateAlpha = this.life / this.fadeTime;
        }

        this.life--;
        if (this.life < 0.0F) {
            this.setLife();
            this.incarnateAlpha = 0.0F;
            this.position.set(Rand.Next(0, this.parent.getWidth()), Rand.Next(0, this.parent.getHeight()));
        }

        if (this.incarnateAlpha < 1.0F) {
            this.incarnateAlpha += 0.05F;
            if (this.incarnateAlpha > 1.0F) {
                this.incarnateAlpha = 1.0F;
            }
        }

        super.update(float0);
        this.updateZoomSize();
        this.alphaMod = 1.0F - 0.2F * IsoWeatherFX.instance.windIntensity.value();
        this.renderAlpha = this.alpha * this.alphaMod * this.alphaFadeMod.value() * IsoWeatherFX.instance.indoorsAlphaMod.value() * this.incarnateAlpha;
        this.renderAlpha *= 0.7F;
    }

    @Override
    public void render(float float0, float float1) {
        super.render(float0, float1);
    }
}
