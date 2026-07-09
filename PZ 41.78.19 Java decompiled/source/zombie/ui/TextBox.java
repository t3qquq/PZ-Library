// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.util.Stack;

public final class TextBox extends UIElement {
    public boolean ResizeParent;
    UIFont font;
    Stack<String> Lines = new Stack<>();
    String Text;
    public boolean Centred = false;

    public TextBox(UIFont uIFont, int int0, int int1, int int2, String string) {
        this.font = uIFont;
        this.x = int0;
        this.y = int1;
        this.Text = string;
        this.width = int2;
        this.Paginate();
    }

    @Override
    public void onresize() {
        this.Paginate();
    }

    @Override
    public void render() {
        if (this.isVisible()) {
            super.render();
            this.Paginate();
            int int0 = 0;

            for (String string : this.Lines) {
                if (this.Centred) {
                    TextManager.instance
                        .DrawStringCentre(
                            this.font,
                            this.getAbsoluteX().intValue() + this.getWidth() / 2.0,
                            this.getAbsoluteY().intValue() + int0,
                            string,
                            1.0,
                            1.0,
                            1.0,
                            1.0
                        );
                } else {
                    TextManager.instance
                        .DrawString(this.font, this.getAbsoluteX().intValue(), this.getAbsoluteY().intValue() + int0, string, 1.0, 1.0, 1.0, 1.0);
                }

                int0 += TextManager.instance.MeasureStringY(this.font, this.Lines.get(0));
            }

            this.setHeight(int0);
        }
    }

    @Override
    public void update() {
        this.Paginate();
        int int0 = 0;

        for (String string : this.Lines) {
            int0 += TextManager.instance.MeasureStringY(this.font, this.Lines.get(0));
        }

        this.setHeight(int0);
    }

    private void Paginate() {
        int int0 = 0;
        this.Lines.clear();
        String[] strings = this.Text.split("<br>");

        for (String string0 : strings) {
            if (string0.length() == 0) {
                this.Lines.add(" ");
            } else {
                while (true) {
                    int int1 = string0.indexOf(" ", int0 + 1);
                    int int2 = int1;
                    if (int1 == -1) {
                        int2 = string0.length();
                    }

                    int int3 = TextManager.instance.MeasureStringX(this.font, string0.substring(0, int2));
                    if (int3 >= this.getWidth()) {
                        String string1 = string0.substring(0, int0);
                        string0 = string0.substring(int0 + 1);
                        this.Lines.add(string1);
                        int1 = 0;
                    } else if (int1 == -1) {
                        this.Lines.add(string0);
                        break;
                    }

                    int0 = int1;
                    if (string0.length() <= 0) {
                        break;
                    }
                }
            }
        }
    }

    public void SetText(String string) {
        this.Text = string;
        this.Paginate();
    }
}
