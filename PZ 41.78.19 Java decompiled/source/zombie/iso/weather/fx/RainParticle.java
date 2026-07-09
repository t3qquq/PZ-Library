// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather.fx;

import zombie.core.Rand;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

public class RainParticle extends WeatherParticle {
    private double angleRadians = 0.0;
    private float lastAngle = -1.0F;
    private float lastIntensity = -1.0F;
    protected float angleOffset = 0.0F;
    private float alphaMod = 0.0F;
    private float incarnateAlpha = 1.0F;
    private float life = 0.0F;
    private RainParticle.RenderPoints rp;
    private boolean angleUpdate = false;
    private float tmpAngle = 0.0F;

    public RainParticle(Texture texture, int int0) {
        super(texture);
        if (int0 > 6) {
            this.bounds.setSize(Rand.Next(1, 2), int0);
        } else {
            this.bounds.setSize(1, int0);
        }

        this.oWidth = this.bounds.getWidth();
        this.oHeight = this.bounds.getHeight();
        this.recalcSizeOnZoom = true;
        this.zoomMultiW = 0.0F;
        this.zoomMultiH = 2.0F;
        this.setLife();
        this.rp = new RainParticle.RenderPoints();
        this.rp.setDimensions(this.oWidth, this.oHeight);
    }

    protected void setLife() {
        this.life = Rand.Next(20, 60);
    }

    @Override
    public void update(float float0) {
        this.angleUpdate = false;
        if (this.updateZoomSize()) {
            this.rp.setDimensions(this.oWidth, this.oHeight);
            this.angleUpdate = true;
        }

        if (this.angleUpdate || this.lastAngle != IsoWeatherFX.instance.windAngle || this.lastIntensity != IsoWeatherFX.instance.windPrecipIntensity.value()) {
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
            this.lastIntensity = IsoWeatherFX.instance.windPrecipIntensity.value();
            this.angleUpdate = true;
        }

        this.position.x = this.position.x + this.velocity.x * (1.0F + IsoWeatherFX.instance.windSpeed * 0.1F) * float0;
        this.position.y = this.position.y + this.velocity.y * (1.0F + IsoWeatherFX.instance.windSpeed * 0.1F) * float0;
        this.life--;
        if (this.life < 0.0F) {
            this.setLife();
            this.incarnateAlpha = 0.0F;
            this.position.set(Rand.Next(0, this.parent.getWidth()), Rand.Next(0, this.parent.getHeight()));
        }

        if (this.incarnateAlpha < 1.0F) {
            this.incarnateAlpha += 0.035F;
            if (this.incarnateAlpha > 1.0F) {
                this.incarnateAlpha = 1.0F;
            }
        }

        super.update(float0, false);
        this.bounds.setLocation((int)this.position.x, (int)this.position.y);
        if (this.angleUpdate) {
            this.tmpAngle += 90.0F;
            if (this.tmpAngle > 360.0F) {
                this.tmpAngle -= 360.0F;
            }

            if (this.tmpAngle < 0.0F) {
                this.tmpAngle += 360.0F;
            }

            this.angleRadians = Math.toRadians(this.tmpAngle);
            this.rp.rotate(this.angleRadians);
        }

        this.alphaMod = 1.0F - 0.2F * IsoWeatherFX.instance.windIntensity.value();
        this.renderAlpha = this.alpha * this.alphaMod * this.alphaFadeMod.value() * IsoWeatherFX.instance.indoorsAlphaMod.value() * this.incarnateAlpha;
        this.renderAlpha *= 0.55F;
        if (IsoWeatherFX.instance.playerIndoors) {
            this.renderAlpha *= 0.5F;
        }
    }

    @Override
    public void render(float float0, float float1) {
        double double0 = float0 + this.bounds.getX();
        double double1 = float1 + this.bounds.getY();
        SpriteRenderer.instance
            .render(
                this.texture,
                double0 + this.rp.getX(0),
                double1 + this.rp.getY(0),
                double0 + this.rp.getX(1),
                double1 + this.rp.getY(1),
                double0 + this.rp.getX(2),
                double1 + this.rp.getY(2),
                double0 + this.rp.getX(3),
                double1 + this.rp.getY(3),
                this.color.r,
                this.color.g,
                this.color.b,
                this.renderAlpha,
                null
            );
    }

    private class Point {
        private double origx;
        private double origy;
        private double x;
        private double y;

        public void setOrig(double double0, double double1) {
            this.origx = double0;
            this.origy = double1;
            this.x = double0;
            this.y = double1;
        }

        public void set(double double0, double double1) {
            this.x = double0;
            this.y = double1;
        }
    }

    private class RenderPoints {
        RainParticle.Point[] points = new RainParticle.Point[4];
        RainParticle.Point center = RainParticle.this.new Point();
        RainParticle.Point dim = RainParticle.this.new Point();

        public RenderPoints() {
            for (int int0 = 0; int0 < this.points.length; int0++) {
                this.points[int0] = RainParticle.this.new Point();
            }
        }

        public double getX(int int0) {
            return this.points[int0].x;
        }

        public double getY(int int0) {
            return this.points[int0].y;
        }

        public void setCenter(float float1, float float0) {
            this.center.set(float1, float0);
        }

        public void setDimensions(float float1, float float0) {
            this.dim.set(float1, float0);
            this.points[0].setOrig(-float1 / 2.0F, -float0 / 2.0F);
            this.points[1].setOrig(float1 / 2.0F, -float0 / 2.0F);
            this.points[2].setOrig(float1 / 2.0F, float0 / 2.0F);
            this.points[3].setOrig(-float1 / 2.0F, float0 / 2.0F);
        }

        public void rotate(double double1) {
            double double0 = Math.cos(double1);
            double double2 = Math.sin(double1);

            for (int int0 = 0; int0 < this.points.length; int0++) {
                this.points[int0].x = this.points[int0].origx * double0 - this.points[int0].origy * double2;
                this.points[int0].y = this.points[int0].origx * double2 + this.points[int0].origy * double0;
            }
        }
    }
}
