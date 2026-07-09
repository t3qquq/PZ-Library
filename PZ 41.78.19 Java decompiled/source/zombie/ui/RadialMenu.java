// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.ArrayList;
import zombie.core.SpriteRenderer;
import zombie.core.fonts.AngelCodeFont;
import zombie.core.textures.Texture;
import zombie.input.JoypadManager;
import zombie.input.Mouse;
import zombie.util.StringUtils;

public final class RadialMenu extends UIElement {
    protected int outerRadius = 200;
    protected int innerRadius = 100;
    protected ArrayList<RadialMenu.Slice> slices = new ArrayList<>();
    protected int highlight = -1;
    protected int joypad = -1;
    protected UITransition transition = new UITransition();
    protected UITransition select = new UITransition();
    protected UITransition deselect = new UITransition();
    protected int selectIndex = -1;
    protected int deselectIndex = -1;

    public RadialMenu(int x, int y, int _innerRadius, int _outerRadius) {
        this.setX(x);
        this.setY(y);
        this.setWidth(_outerRadius * 2);
        this.setHeight(_outerRadius * 2);
        this.innerRadius = _innerRadius;
        this.outerRadius = _outerRadius;
    }

    @Override
    public void update() {
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            this.transition.setIgnoreUpdateTime(true);
            this.transition.setFadeIn(true);
            this.transition.update();
            if (!this.slices.isEmpty()) {
                float float0 = this.transition.fraction();
                float float1 = this.innerRadius * 0.85F + this.innerRadius * float0 * 0.15F;
                float float2 = this.outerRadius * 0.85F + this.outerRadius * float0 * 0.15F;

                for (int int0 = 0; int0 < 48; int0++) {
                    float float3 = 7.5F;
                    double double0 = Math.toRadians(int0 * float3);
                    double double1 = Math.toRadians((int0 + 1) * float3);
                    double double2 = this.x + this.width / 2.0F;
                    double double3 = this.y + this.height / 2.0F;
                    double double4 = this.x + this.width / 2.0F;
                    double double5 = this.y + this.height / 2.0F;
                    double double6 = this.x + this.width / 2.0F + float2 * (float)Math.cos(double0);
                    double double7 = this.y + this.height / 2.0F + float2 * (float)Math.sin(double0);
                    double double8 = this.x + this.width / 2.0F + float2 * (float)Math.cos(double1);
                    double double9 = this.y + this.height / 2.0F + float2 * (float)Math.sin(double1);
                    if (int0 == 47) {
                        double9 = double5;
                    }

                    float float4 = 0.1F;
                    float float5 = 0.1F;
                    float float6 = 0.1F;
                    float float7 = 0.45F + 0.45F * float0;
                    SpriteRenderer.instance
                        .renderPoly(
                            (float)double2,
                            (float)double3,
                            (float)double6,
                            (float)double7,
                            (float)double8,
                            (float)double9,
                            (float)double4,
                            (float)double5,
                            float4,
                            float5,
                            float6,
                            float7
                        );
                }

                float float8 = 360.0F / Math.max(this.slices.size(), 2);
                float float9 = this.slices.size() == 1 ? 0.0F : 1.5F;
                int int1 = this.highlight;
                if (int1 == -1) {
                    if (this.joypad != -1) {
                        int1 = this.getSliceIndexFromJoypad(this.joypad);
                    } else {
                        int1 = this.getSliceIndexFromMouse(Mouse.getXA() - this.getAbsoluteX().intValue(), Mouse.getYA() - this.getAbsoluteY().intValue());
                    }
                }

                RadialMenu.Slice slice = this.getSlice(int1);
                if (slice != null && slice.isEmpty()) {
                    int1 = -1;
                }

                if (int1 != this.selectIndex) {
                    this.select.reset();
                    this.select.setIgnoreUpdateTime(true);
                    if (this.selectIndex != -1) {
                        this.deselectIndex = this.selectIndex;
                        this.deselect.reset();
                        this.deselect.setFadeIn(false);
                        this.deselect.init(66.666664F, true);
                    }

                    this.selectIndex = int1;
                }

                this.select.update();
                this.deselect.update();
                float float10 = this.getStartAngle() - 180.0F;

                for (int int2 = 0; int2 < this.slices.size(); int2++) {
                    int int3 = Math.max(6, 48 / Math.max(this.slices.size(), 2));

                    for (int int4 = 0; int4 < int3; int4++) {
                        double double10 = Math.toRadians(float10 + int2 * float8 + int4 * float8 / int3 + (int4 == 0 ? float9 : 0.0F));
                        double double11 = Math.toRadians(float10 + int2 * float8 + (int4 + 1) * float8 / int3 - (int4 == int3 - 1 ? float9 : 0.0F));
                        double double12 = Math.toRadians(float10 + int2 * float8 + int4 * float8 / int3 + (int4 == 0 ? float9 / 2.0F : 0.0F));
                        double double13 = Math.toRadians(float10 + int2 * float8 + (int4 + 1) * float8 / int3 - (int4 == int3 - 1 ? float9 / 1.5 : 0.0));
                        double double14 = this.x + this.width / 2.0F + float1 * (float)Math.cos(double10);
                        double double15 = this.y + this.height / 2.0F + float1 * (float)Math.sin(double10);
                        double double16 = this.x + this.width / 2.0F + float1 * (float)Math.cos(double11);
                        double double17 = this.y + this.height / 2.0F + float1 * (float)Math.sin(double11);
                        double double18 = this.x + this.width / 2.0F + float2 * (float)Math.cos(double12);
                        double double19 = this.y + this.height / 2.0F + float2 * (float)Math.sin(double12);
                        double double20 = this.x + this.width / 2.0F + float2 * (float)Math.cos(double13);
                        double double21 = this.y + this.height / 2.0F + float2 * (float)Math.sin(double13);
                        float float11 = 1.0F;
                        float float12 = 1.0F;
                        float float13 = 1.0F;
                        float float14 = 0.025F;
                        if (int2 == int1) {
                            float14 = 0.25F + 0.25F * this.select.fraction();
                        } else if (int2 == this.deselectIndex) {
                            float14 = 0.025F + 0.475F * this.deselect.fraction();
                        }

                        SpriteRenderer.instance
                            .renderPoly(
                                (float)double14,
                                (float)double15,
                                (float)double18,
                                (float)double19,
                                (float)double20,
                                (float)double21,
                                (float)double16,
                                (float)double17,
                                float11,
                                float12,
                                float13,
                                float14
                            );
                    }

                    Texture texture = this.slices.get(int2).texture;
                    if (texture != null) {
                        double double22 = Math.toRadians(float10 + int2 * float8 + float8 / 2.0F);
                        float float15 = 0.0F + this.width / 2.0F + (float1 + (float2 - float1) / 2.0F) * (float)Math.cos(double22);
                        float float16 = 0.0F + this.height / 2.0F + (float1 + (float2 - float1) / 2.0F) * (float)Math.sin(double22);
                        this.DrawTexture(
                            texture, float15 - texture.getWidth() / 2 - texture.offsetX, float16 - texture.getHeight() / 2 - texture.offsetY, float0
                        );
                    }
                }

                if (slice != null && !StringUtils.isNullOrWhitespace(slice.text)) {
                    this.formatTextInsideCircle(slice.text);
                }
            }
        }
    }

    private void formatTextInsideCircle(String string) {
        UIFont uIFont = UIFont.Medium;
        AngelCodeFont angelCodeFont = TextManager.instance.getFontFromEnum(uIFont);
        int int0 = angelCodeFont.getLineHeight();
        int int1 = 1;

        for (int int2 = 0; int2 < string.length(); int2++) {
            if (string.charAt(int2) == '\n') {
                int1++;
            }
        }

        if (int1 > 1) {
            int int3 = int1 * int0;
            int int4 = this.getAbsoluteX().intValue() + (int)this.width / 2;
            int int5 = this.getAbsoluteY().intValue() + (int)this.height / 2 - int3 / 2;
            int int6 = 0;

            for (int int7 = 0; int7 < string.length(); int7++) {
                if (string.charAt(int7) == '\n') {
                    int int8 = angelCodeFont.getWidth(string, int6, int7);
                    angelCodeFont.drawString(int4 - int8 / 2, int5, string, 1.0F, 1.0F, 1.0F, 1.0F, int6, int7 - 1);
                    int6 = int7 + 1;
                    int5 += int0;
                }
            }

            if (int6 < string.length()) {
                int int9 = angelCodeFont.getWidth(string, int6, string.length() - 1);
                angelCodeFont.drawString(int4 - int9 / 2, int5, string, 1.0F, 1.0F, 1.0F, 1.0F, int6, string.length() - 1);
            }
        } else {
            this.DrawTextCentre(uIFont, string, this.width / 2.0F, this.height / 2.0F - int0 / 2, 1.0, 1.0, 1.0, 1.0);
        }
    }

    public void clear() {
        this.slices.clear();
        this.transition.reset();
        this.transition.init(66.666664F, false);
        this.selectIndex = -1;
        this.deselectIndex = -1;
    }

    public void addSlice(String text, Texture texture) {
        RadialMenu.Slice slice = new RadialMenu.Slice();
        slice.text = text;
        slice.texture = texture;
        this.slices.add(slice);
    }

    private RadialMenu.Slice getSlice(int int0) {
        return int0 >= 0 && int0 < this.slices.size() ? this.slices.get(int0) : null;
    }

    public void setSliceText(int sliceIndex, String text) {
        RadialMenu.Slice slice = this.getSlice(sliceIndex);
        if (slice != null) {
            slice.text = text;
        }
    }

    public void setSliceTexture(int sliceIndex, Texture texture) {
        RadialMenu.Slice slice = this.getSlice(sliceIndex);
        if (slice != null) {
            slice.texture = texture;
        }
    }

    private float getStartAngle() {
        float float0 = 360.0F / Math.max(this.slices.size(), 2);
        return 90.0F - float0 / 2.0F;
    }

    public int getSliceIndexFromMouse(int mx, int my) {
        float float0 = 0.0F + this.width / 2.0F;
        float float1 = 0.0F + this.height / 2.0F;
        double double0 = Math.sqrt(Math.pow(mx - float0, 2.0) + Math.pow(my - float1, 2.0));
        if (!(double0 > this.outerRadius) && !(double0 < this.innerRadius)) {
            double double1 = Math.atan2(my - float1, mx - float0) + Math.PI;
            double double2 = Math.toDegrees(double1);
            float float2 = 360.0F / Math.max(this.slices.size(), 2);
            return double2 < this.getStartAngle() ? (int)((double2 + 360.0 - this.getStartAngle()) / float2) : (int)((double2 - this.getStartAngle()) / float2);
        } else {
            return -1;
        }
    }

    public int getSliceIndexFromJoypad(int _joypad) {
        float float0 = JoypadManager.instance.getAimingAxisX(_joypad);
        float float1 = JoypadManager.instance.getAimingAxisY(_joypad);
        if (!(Math.abs(float0) > 0.3F) && !(Math.abs(float1) > 0.3F)) {
            return -1;
        } else {
            double double0 = Math.atan2(-float1, -float0);
            double double1 = Math.toDegrees(double0);
            float float2 = 360.0F / Math.max(this.slices.size(), 2);
            return double1 < this.getStartAngle() ? (int)((double1 + 360.0 - this.getStartAngle()) / float2) : (int)((double1 - this.getStartAngle()) / float2);
        }
    }

    public void setJoypad(int _joypad) {
        this.joypad = _joypad;
    }

    protected static class Slice {
        public String text;
        public Texture texture;

        boolean isEmpty() {
            return this.text == null && this.texture == null;
        }
    }
}
