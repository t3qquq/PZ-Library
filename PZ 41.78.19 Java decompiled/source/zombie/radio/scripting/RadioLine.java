// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.scripting;

/**
 * Turbo
 */
public final class RadioLine {
    private float r = 1.0F;
    private float g = 1.0F;
    private float b = 1.0F;
    private String text = "<!text missing!>";
    private String effects = "";
    private float airTime = -1.0F;

    public RadioLine(String txt, float red, float green, float blue) {
        this(txt, red, green, blue, null);
    }

    public RadioLine(String txt, float red, float green, float blue, String fx) {
        this.text = txt != null ? txt : this.text;
        this.r = red;
        this.g = green;
        this.b = blue;
        this.effects = fx != null ? fx : this.effects;
    }

    public float getR() {
        return this.r;
    }

    public float getG() {
        return this.g;
    }

    public float getB() {
        return this.b;
    }

    public String getText() {
        return this.text;
    }

    public String getEffectsString() {
        return this.effects;
    }

    public boolean isCustomAirTime() {
        return this.airTime > 0.0F;
    }

    public float getAirTime() {
        return this.airTime;
    }

    public void setAirTime(float _airTime) {
        this.airTime = _airTime;
    }

    public void setText(String _text) {
        this.text = _text;
    }
}
