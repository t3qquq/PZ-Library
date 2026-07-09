// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.io.Serializable;
import zombie.core.math.PZMath;

/**
 * A simple wrapper round the values required for a colour
 */
public final class Color implements Serializable {
    private static final long serialVersionUID = 1393939L;
    /**
     * The fixed color transparent
     */
    public static final Color transparent = new Color(0.0F, 0.0F, 0.0F, 0.0F);
    /**
     * The fixed colour white
     */
    public static final Color white = new Color(1.0F, 1.0F, 1.0F, 1.0F);
    /**
     * The fixed colour yellow
     */
    public static final Color yellow = new Color(1.0F, 1.0F, 0.0F, 1.0F);
    /**
     * The fixed colour red
     */
    public static final Color red = new Color(1.0F, 0.0F, 0.0F, 1.0F);
    /**
     * The fixed colour purple
     */
    public static final Color purple = new Color(196.0F, 0.0F, 171.0F);
    /**
     * The fixed colour blue
     */
    public static final Color blue = new Color(0.0F, 0.0F, 1.0F, 1.0F);
    /**
     * The fixed colour green
     */
    public static final Color green = new Color(0.0F, 1.0F, 0.0F, 1.0F);
    /**
     * The fixed colour black
     */
    public static final Color black = new Color(0.0F, 0.0F, 0.0F, 1.0F);
    /**
     * The fixed colour gray
     */
    public static final Color gray = new Color(0.5F, 0.5F, 0.5F, 1.0F);
    /**
     * The fixed colour cyan
     */
    public static final Color cyan = new Color(0.0F, 1.0F, 1.0F, 1.0F);
    /**
     * The fixed colour dark gray
     */
    public static final Color darkGray = new Color(0.3F, 0.3F, 0.3F, 1.0F);
    /**
     * The fixed colour light gray
     */
    public static final Color lightGray = new Color(0.7F, 0.7F, 0.7F, 1.0F);
    /**
     * The fixed colour dark pink
     */
    public static final Color pink = new Color(255, 175, 175, 255);
    /**
     * The fixed colour dark orange
     */
    public static final Color orange = new Color(255, 200, 0, 255);
    /**
     * The fixed colour dark magenta
     */
    public static final Color magenta = new Color(255, 0, 255, 255);
    /**
     * The fixed colour dark green
     */
    public static final Color darkGreen = new Color(22, 113, 20, 255);
    /**
     * The fixed colour light green
     */
    public static final Color lightGreen = new Color(55, 148, 53, 255);
    /**
     * The alpha component of the colour
     */
    public float a = 1.0F;
    /**
     * The blue component of the colour
     */
    public float b;
    /**
     * The green component of the colour
     */
    public float g;
    /**
     * The red component of the colour
     */
    public float r;

    public float getR() {
        return this.r;
    }

    public float getG() {
        return this.g;
    }

    public float getB() {
        return this.b;
    }

    public Color() {
    }

    /**
     * Copy constructor
     * 
     * @param color The color to copy into the new instance
     */
    public Color(Color color) {
        if (color == null) {
            this.r = 0.0F;
            this.g = 0.0F;
            this.b = 0.0F;
            this.a = 1.0F;
        } else {
            this.r = color.r;
            this.g = color.g;
            this.b = color.b;
            this.a = color.a;
        }
    }

    /**
     * Create a 3 component colour
     * 
     * @param _r The red component of the colour (0.0
     * @param _g The green component of the colour (0.0
     * @param _b The blue component of the colour (0.0
     */
    public Color(float _r, float _g, float _b) {
        this.r = _r;
        this.g = _g;
        this.b = _b;
        this.a = 1.0F;
    }

    /**
     * Create a 4 component colour
     * 
     * @param _r The red component of the colour (0.0
     * @param _g The green component of the colour (0.0
     * @param _b The blue component of the colour (0.0
     * @param _a The alpha component of the colour (0.0
     */
    public Color(float _r, float _g, float _b, float _a) {
        this.r = PZMath.clamp(_r, 0.0F, 1.0F);
        this.g = PZMath.clamp(_g, 0.0F, 1.0F);
        this.b = PZMath.clamp(_b, 0.0F, 1.0F);
        this.a = PZMath.clamp(_a, 0.0F, 1.0F);
    }

    public Color(Color A, Color B, float delta) {
        float float0 = (B.r - A.r) * delta;
        float float1 = (B.g - A.g) * delta;
        float float2 = (B.b - A.b) * delta;
        float float3 = (B.a - A.a) * delta;
        this.r = A.r + float0;
        this.g = A.g + float1;
        this.b = A.b + float2;
        this.a = A.a + float3;
    }

    public void setColor(Color A, Color B, float delta) {
        float float0 = (B.r - A.r) * delta;
        float float1 = (B.g - A.g) * delta;
        float float2 = (B.b - A.b) * delta;
        float float3 = (B.a - A.a) * delta;
        this.r = A.r + float0;
        this.g = A.g + float1;
        this.b = A.b + float2;
        this.a = A.a + float3;
    }

    /**
     * Create a 3 component colour
     * 
     * @param _r The red component of the colour (0
     * @param _g The green component of the colour (0
     * @param _b The blue component of the colour (0
     */
    public Color(int _r, int _g, int _b) {
        this.r = _r / 255.0F;
        this.g = _g / 255.0F;
        this.b = _b / 255.0F;
        this.a = 1.0F;
    }

    /**
     * Create a 4 component colour
     * 
     * @param _r The red component of the colour (0
     * @param _g The green component of the colour (0
     * @param _b The blue component of the colour (0
     * @param _a The alpha component of the colour (0
     */
    public Color(int _r, int _g, int _b, int _a) {
        this.r = _r / 255.0F;
        this.g = _g / 255.0F;
        this.b = _b / 255.0F;
        this.a = _a / 255.0F;
    }

    /**
     * Create a colour from an evil integer packed 0xAARRGGBB. If AA  is specified as zero then it will be interpreted as unspecified  and hence a value of 255 will be recorded.
     * 
     * @param value The value to interpret for the colour
     */
    public Color(int value) {
        int int0 = (value & 0xFF0000) >> 16;
        int int1 = (value & 0xFF00) >> 8;
        int int2 = value & 0xFF;
        int int3 = (value & 0xFF000000) >> 24;
        if (int3 < 0) {
            int3 += 256;
        }

        if (int3 == 0) {
            int3 = 255;
        }

        this.r = int2 / 255.0F;
        this.g = int1 / 255.0F;
        this.b = int0 / 255.0F;
        this.a = int3 / 255.0F;
    }

    /**
     * Converts the supplied binary value into color values, and sets the result to this object.   Performs a clamp on the alpha channel.   Performs a special-case on the alpha channel, where if it is 0, it is set to MAX instead.
     */
    @Deprecated
    public void fromColor(int valueABGR) {
        int int0 = (valueABGR & 0xFF0000) >> 16;
        int int1 = (valueABGR & 0xFF00) >> 8;
        int int2 = valueABGR & 0xFF;
        int int3 = (valueABGR & 0xFF000000) >> 24;
        if (int3 < 0) {
            int3 += 256;
        }

        if (int3 == 0) {
            int3 = 255;
        }

        this.r = int2 / 255.0F;
        this.g = int1 / 255.0F;
        this.b = int0 / 255.0F;
        this.a = int3 / 255.0F;
    }

    public void setABGR(int valueABGR) {
        abgrToColor(valueABGR, this);
    }

    public static Color abgrToColor(int valueABGR, Color out_result) {
        int int0 = valueABGR >> 24 & 0xFF;
        int int1 = valueABGR >> 16 & 0xFF;
        int int2 = valueABGR >> 8 & 0xFF;
        int int3 = valueABGR & 0xFF;
        float float0 = 0.003921569F * int3;
        float float1 = 0.003921569F * int2;
        float float2 = 0.003921569F * int1;
        float float3 = 0.003921569F * int0;
        out_result.r = float0;
        out_result.g = float1;
        out_result.b = float2;
        out_result.a = float3;
        return out_result;
    }

    public static int colorToABGR(Color val) {
        return colorToABGR(val.r, val.g, val.b, val.a);
    }

    public static int colorToABGR(float _r, float _g, float _b, float _a) {
        _r = PZMath.clamp(_r, 0.0F, 1.0F);
        _g = PZMath.clamp(_g, 0.0F, 1.0F);
        _b = PZMath.clamp(_b, 0.0F, 1.0F);
        _a = PZMath.clamp(_a, 0.0F, 1.0F);
        int int0 = (int)(_r * 255.0F);
        int int1 = (int)(_g * 255.0F);
        int int2 = (int)(_b * 255.0F);
        int int3 = (int)(_a * 255.0F);
        return (int3 & 0xFF) << 24 | (int2 & 0xFF) << 16 | (int1 & 0xFF) << 8 | int0 & 0xFF;
    }

    public static int multiplyABGR(int valueABGR, int multiplierABGR) {
        float float0 = getRedChannelFromABGR(valueABGR);
        float float1 = getGreenChannelFromABGR(valueABGR);
        float float2 = getBlueChannelFromABGR(valueABGR);
        float float3 = getAlphaChannelFromABGR(valueABGR);
        float float4 = getRedChannelFromABGR(multiplierABGR);
        float float5 = getGreenChannelFromABGR(multiplierABGR);
        float float6 = getBlueChannelFromABGR(multiplierABGR);
        float float7 = getAlphaChannelFromABGR(multiplierABGR);
        return colorToABGR(float0 * float4, float1 * float5, float2 * float6, float3 * float7);
    }

    public static int multiplyBGR(int valueABGR, int multiplierABGR) {
        float float0 = getRedChannelFromABGR(valueABGR);
        float float1 = getGreenChannelFromABGR(valueABGR);
        float float2 = getBlueChannelFromABGR(valueABGR);
        float float3 = getAlphaChannelFromABGR(valueABGR);
        float float4 = getRedChannelFromABGR(multiplierABGR);
        float float5 = getGreenChannelFromABGR(multiplierABGR);
        float float6 = getBlueChannelFromABGR(multiplierABGR);
        return colorToABGR(float0 * float4, float1 * float5, float2 * float6, float3);
    }

    public static int blendBGR(int valueABGR, int targetABGR) {
        float float0 = getRedChannelFromABGR(valueABGR);
        float float1 = getGreenChannelFromABGR(valueABGR);
        float float2 = getBlueChannelFromABGR(valueABGR);
        float float3 = getAlphaChannelFromABGR(valueABGR);
        float float4 = getRedChannelFromABGR(targetABGR);
        float float5 = getGreenChannelFromABGR(targetABGR);
        float float6 = getBlueChannelFromABGR(targetABGR);
        float float7 = getAlphaChannelFromABGR(targetABGR);
        return colorToABGR(
            float0 * (1.0F - float7) + float4 * float7, float1 * (1.0F - float7) + float5 * float7, float2 * (1.0F - float7) + float6 * float7, float3
        );
    }

    public static int blendABGR(int valueABGR, int targetABGR) {
        float float0 = getRedChannelFromABGR(valueABGR);
        float float1 = getGreenChannelFromABGR(valueABGR);
        float float2 = getBlueChannelFromABGR(valueABGR);
        float float3 = getAlphaChannelFromABGR(valueABGR);
        float float4 = getRedChannelFromABGR(targetABGR);
        float float5 = getGreenChannelFromABGR(targetABGR);
        float float6 = getBlueChannelFromABGR(targetABGR);
        float float7 = getAlphaChannelFromABGR(targetABGR);
        return colorToABGR(
            float0 * (1.0F - float7) + float4 * float7,
            float1 * (1.0F - float7) + float5 * float7,
            float2 * (1.0F - float7) + float6 * float7,
            float3 * (1.0F - float7) + float7 * float7
        );
    }

    public static int tintABGR(int targetABGR, int tintABGR) {
        float float0 = getRedChannelFromABGR(tintABGR);
        float float1 = getGreenChannelFromABGR(tintABGR);
        float float2 = getBlueChannelFromABGR(tintABGR);
        float float3 = getAlphaChannelFromABGR(tintABGR);
        float float4 = getRedChannelFromABGR(targetABGR);
        float float5 = getGreenChannelFromABGR(targetABGR);
        float float6 = getBlueChannelFromABGR(targetABGR);
        float float7 = getAlphaChannelFromABGR(targetABGR);
        return colorToABGR(
            float0 * float3 + float4 * (1.0F - float3), float1 * float3 + float5 * (1.0F - float3), float2 * float3 + float6 * (1.0F - float3), float7
        );
    }

    public static int lerpABGR(int colA, int colB, float alpha) {
        float float0 = getRedChannelFromABGR(colA);
        float float1 = getGreenChannelFromABGR(colA);
        float float2 = getBlueChannelFromABGR(colA);
        float float3 = getAlphaChannelFromABGR(colA);
        float float4 = getRedChannelFromABGR(colB);
        float float5 = getGreenChannelFromABGR(colB);
        float float6 = getBlueChannelFromABGR(colB);
        float float7 = getAlphaChannelFromABGR(colB);
        return colorToABGR(
            float0 * (1.0F - alpha) + float4 * alpha,
            float1 * (1.0F - alpha) + float5 * alpha,
            float2 * (1.0F - alpha) + float6 * alpha,
            float3 * (1.0F - alpha) + float7 * alpha
        );
    }

    public static float getAlphaChannelFromABGR(int valueABGR) {
        int int0 = valueABGR >> 24 & 0xFF;
        return 0.003921569F * int0;
    }

    public static float getBlueChannelFromABGR(int valueABGR) {
        int int0 = valueABGR >> 16 & 0xFF;
        return 0.003921569F * int0;
    }

    public static float getGreenChannelFromABGR(int valueABGR) {
        int int0 = valueABGR >> 8 & 0xFF;
        return 0.003921569F * int0;
    }

    public static float getRedChannelFromABGR(int valueABGR) {
        int int0 = valueABGR & 0xFF;
        return 0.003921569F * int0;
    }

    public static int setAlphaChannelToABGR(int valueABGR, float _a) {
        _a = PZMath.clamp(_a, 0.0F, 1.0F);
        int int0 = (int)(_a * 255.0F);
        return (int0 & 0xFF) << 24 | valueABGR & 16777215;
    }

    public static int setBlueChannelToABGR(int valueABGR, float _b) {
        _b = PZMath.clamp(_b, 0.0F, 1.0F);
        int int0 = (int)(_b * 255.0F);
        return (int0 & 0xFF) << 16 | valueABGR & -16711681;
    }

    public static int setGreenChannelToABGR(int valueABGR, float _g) {
        _g = PZMath.clamp(_g, 0.0F, 1.0F);
        int int0 = (int)(_g * 255.0F);
        return (int0 & 0xFF) << 8 | valueABGR & -65281;
    }

    public static int setRedChannelToABGR(int valueABGR, float _r) {
        _r = PZMath.clamp(_r, 0.0F, 1.0F);
        int int0 = (int)(_r * 255.0F);
        return int0 & 0xFF | valueABGR & -256;
    }

    /**
     * Create a random color.
     */
    public static Color random() {
        return Colors.GetRandomColor();
    }

    /**
     * Decode a number in a string and process it as a colour  reference.
     * 
     * @param nm The number string to decode
     * @return The color generated from the number read
     */
    public static Color decode(String nm) {
        return new Color(Integer.decode(nm));
    }

    /**
     * Add another colour to this one
     * 
     * @param c The colour to add
     */
    public void add(Color c) {
        this.r = this.r + c.r;
        this.g = this.g + c.g;
        this.b = this.b + c.b;
        this.a = this.a + c.a;
    }

    /**
     * Add another colour to this one
     * 
     * @param c The colour to add
     * @return The copy which has had the color added to it
     */
    public Color addToCopy(Color c) {
        Color color = new Color(this.r, this.g, this.b, this.a);
        color.r = color.r + c.r;
        color.g = color.g + c.g;
        color.b = color.b + c.b;
        color.a = color.a + c.a;
        return color;
    }

    /**
     * Make a brighter instance of this colour
     * @return The brighter version of this colour
     */
    public Color brighter() {
        return this.brighter(0.2F);
    }

    /**
     * Make a brighter instance of this colour
     * 
     * @param scale The scale up of RGB (i.e. if you supply 0.03 the colour will be brightened by 3%)
     * @return The brighter version of this colour
     */
    public Color brighter(float scale) {
        this.r = this.r += scale;
        this.g = this.g += scale;
        this.b = this.b += scale;
        return this;
    }

    /**
     * Make a darker instance of this colour
     * @return The darker version of this colour
     */
    public Color darker() {
        return this.darker(0.5F);
    }

    /**
     * Make a darker instance of this colour
     * 
     * @param scale The scale down of RGB (i.e. if you supply 0.03 the colour will be darkened by 3%)
     * @return The darker version of this colour
     */
    public Color darker(float scale) {
        this.r = this.r -= scale;
        this.g = this.g -= scale;
        this.b = this.b -= scale;
        return this;
    }

    @Override
    public boolean equals(Object other) {
        return !(other instanceof Color color) ? false : color.r == this.r && color.g == this.g && color.b == this.b && color.a == this.a;
    }

    public Color set(Color other) {
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
        this.a = other.a;
        return this;
    }

    public Color set(float _r, float _g, float _b) {
        this.r = _r;
        this.g = _g;
        this.b = _b;
        this.a = 1.0F;
        return this;
    }

    public Color set(float _r, float _g, float _b, float _a) {
        this.r = _r;
        this.g = _g;
        this.b = _b;
        this.a = _a;
        return this;
    }

    /**
     * get the alpha byte component of this colour
     * @return The alpha component (range 0-255)
     */
    public int getAlpha() {
        return (int)(this.a * 255.0F);
    }

    public float getAlphaFloat() {
        return this.a;
    }

    public float getRedFloat() {
        return this.r;
    }

    public float getGreenFloat() {
        return this.g;
    }

    public float getBlueFloat() {
        return this.b;
    }

    /**
     * get the alpha byte component of this colour
     * @return The alpha component (range 0-255)
     */
    public int getAlphaByte() {
        return (int)(this.a * 255.0F);
    }

    /**
     * get the blue byte component of this colour
     * @return The blue component (range 0-255)
     */
    public int getBlue() {
        return (int)(this.b * 255.0F);
    }

    /**
     * get the blue byte component of this colour
     * @return The blue component (range 0-255)
     */
    public int getBlueByte() {
        return (int)(this.b * 255.0F);
    }

    /**
     * get the green byte component of this colour
     * @return The green component (range 0-255)
     */
    public int getGreen() {
        return (int)(this.g * 255.0F);
    }

    /**
     * get the green byte component of this colour
     * @return The green component (range 0-255)
     */
    public int getGreenByte() {
        return (int)(this.g * 255.0F);
    }

    /**
     * get the red byte component of this colour
     * @return The red component (range 0-255)
     */
    public int getRed() {
        return (int)(this.r * 255.0F);
    }

    /**
     * get the red byte component of this colour
     * @return The red component (range 0-255)
     */
    public int getRedByte() {
        return (int)(this.r * 255.0F);
    }

    @Override
    public int hashCode() {
        return (int)(this.r + this.g + this.b + this.a) * 255;
    }

    /**
     * Multiply this color by another
     * 
     * @param c the other color
     * @return product of the two colors
     */
    public Color multiply(Color c) {
        return new Color(this.r * c.r, this.g * c.g, this.b * c.b, this.a * c.a);
    }

    /**
     * Scale the components of the colour by the given value
     * 
     * @param value The value to scale by
     */
    public Color scale(float value) {
        this.r *= value;
        this.g *= value;
        this.b *= value;
        this.a *= value;
        return this;
    }

    /**
     * Scale the components of the colour by the given value
     * 
     * @param value The value to scale by
     * @return The copy which has been scaled
     */
    public Color scaleCopy(float value) {
        Color color = new Color(this.r, this.g, this.b, this.a);
        color.r *= value;
        color.g *= value;
        color.b *= value;
        color.a *= value;
        return color;
    }

    @Override
    public String toString() {
        return "Color (" + this.r + "," + this.g + "," + this.b + "," + this.a + ")";
    }

    public void interp(Color to, float delta, Color dest) {
        float float0 = to.r - this.r;
        float float1 = to.g - this.g;
        float float2 = to.b - this.b;
        float float3 = to.a - this.a;
        float0 *= delta;
        float1 *= delta;
        float2 *= delta;
        float3 *= delta;
        dest.r = this.r + float0;
        dest.g = this.g + float1;
        dest.b = this.b + float2;
        dest.a = this.a + float3;
    }

    public void changeHSBValue(float hFactor, float sFactor, float bFactor) {
        float[] floats = java.awt.Color.RGBtoHSB(this.getRedByte(), this.getGreenByte(), this.getBlueByte(), null);
        int int0 = java.awt.Color.HSBtoRGB(floats[0] * hFactor, floats[1] * sFactor, floats[2] * bFactor);
        this.r = (int0 >> 16 & 0xFF) / 255.0F;
        this.g = (int0 >> 8 & 0xFF) / 255.0F;
        this.b = (int0 & 0xFF) / 255.0F;
    }

    public static Color HSBtoRGB(float hue, float saturation, float brightness, Color result) {
        int int0 = 0;
        int int1 = 0;
        int int2 = 0;
        if (saturation == 0.0F) {
            int0 = int1 = int2 = (int)(brightness * 255.0F + 0.5F);
        } else {
            float float0 = (hue - (float)Math.floor(hue)) * 6.0F;
            float float1 = float0 - (float)Math.floor(float0);
            float float2 = brightness * (1.0F - saturation);
            float float3 = brightness * (1.0F - saturation * float1);
            float float4 = brightness * (1.0F - saturation * (1.0F - float1));
            switch ((int)float0) {
                case 0:
                    int0 = (int)(brightness * 255.0F + 0.5F);
                    int1 = (int)(float4 * 255.0F + 0.5F);
                    int2 = (int)(float2 * 255.0F + 0.5F);
                    break;
                case 1:
                    int0 = (int)(float3 * 255.0F + 0.5F);
                    int1 = (int)(brightness * 255.0F + 0.5F);
                    int2 = (int)(float2 * 255.0F + 0.5F);
                    break;
                case 2:
                    int0 = (int)(float2 * 255.0F + 0.5F);
                    int1 = (int)(brightness * 255.0F + 0.5F);
                    int2 = (int)(float4 * 255.0F + 0.5F);
                    break;
                case 3:
                    int0 = (int)(float2 * 255.0F + 0.5F);
                    int1 = (int)(float3 * 255.0F + 0.5F);
                    int2 = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 4:
                    int0 = (int)(float4 * 255.0F + 0.5F);
                    int1 = (int)(float2 * 255.0F + 0.5F);
                    int2 = (int)(brightness * 255.0F + 0.5F);
                    break;
                case 5:
                    int0 = (int)(brightness * 255.0F + 0.5F);
                    int1 = (int)(float2 * 255.0F + 0.5F);
                    int2 = (int)(float3 * 255.0F + 0.5F);
            }
        }

        return result.set(int0 / 255.0F, int1 / 255.0F, int2 / 255.0F);
    }

    public static Color HSBtoRGB(float hue, float saturation, float brightness) {
        return HSBtoRGB(hue, saturation, brightness, new Color());
    }
}
