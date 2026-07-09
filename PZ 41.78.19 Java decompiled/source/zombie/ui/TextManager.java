// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.core.fonts.AngelCodeFont;
import zombie.debug.DebugLog;
import zombie.network.GameServer;
import zombie.network.ServerGUI;

public final class TextManager {
    public AngelCodeFont font;
    public AngelCodeFont font2;
    public AngelCodeFont font3;
    public AngelCodeFont font4;
    public AngelCodeFont main1;
    public AngelCodeFont main2;
    public AngelCodeFont zombiefontcredits1;
    public AngelCodeFont zombiefontcredits2;
    public AngelCodeFont zombienew1;
    public AngelCodeFont zombienew2;
    public AngelCodeFont zomboidDialogue;
    public AngelCodeFont codetext;
    public AngelCodeFont debugConsole;
    public AngelCodeFont intro;
    public AngelCodeFont handwritten;
    public final AngelCodeFont[] normal = new AngelCodeFont[14];
    public AngelCodeFont zombienew3;
    public final AngelCodeFont[] enumToFont = new AngelCodeFont[UIFont.values().length];
    public static final TextManager instance = new TextManager();
    public ArrayList<TextManager.DeferedTextDraw> todoTextList = new ArrayList<>();

    public void DrawString(double x, double y, String str) {
        this.font.drawString((float)x, (float)y, str, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void DrawString(double x, double y, String str, double r, double g, double b, double a) {
        this.font.drawString((float)x, (float)y, str, (float)r, (float)g, (float)b, (float)a);
    }

    public void DrawString(UIFont _font, double x, double y, double zoom, String str, double r, double g, double b, double a) {
        AngelCodeFont angelCodeFont = this.getFontFromEnum(_font);
        angelCodeFont.drawString((float)x, (float)y, (float)zoom, str, (float)r, (float)g, (float)b, (float)a);
    }

    public void DrawString(UIFont _font, double x, double y, String str, double r, double g, double b, double a) {
        AngelCodeFont angelCodeFont = this.getFontFromEnum(_font);
        angelCodeFont.drawString((float)x, (float)y, str, (float)r, (float)g, (float)b, (float)a);
    }

    public void DrawStringUntrimmed(UIFont _font, double x, double y, String str, double r, double g, double b, double a) {
        AngelCodeFont angelCodeFont = this.getFontFromEnum(_font);
        angelCodeFont.drawString((float)x, (float)y, str, (float)r, (float)g, (float)b, (float)a);
    }

    public void DrawStringCentre(double x, double y, String str, double r, double g, double b, double a) {
        x -= this.font.getWidth(str) / 2;
        this.font.drawString((float)x, (float)y, str, (float)r, (float)g, (float)b, (float)a);
    }

    public void DrawStringCentre(UIFont _font, double x, double y, String str, double r, double g, double b, double a) {
        AngelCodeFont angelCodeFont = this.getFontFromEnum(_font);
        x -= angelCodeFont.getWidth(str) / 2;
        angelCodeFont.drawString((float)x, (float)y, str, (float)r, (float)g, (float)b, (float)a);
    }

    public void DrawStringCentreDefered(UIFont _font, double x, double y, String str, double r, double g, double b, double a) {
        this.todoTextList.add(new TextManager.DeferedTextDraw(_font, x, y, str, r, g, b, a));
    }

    public void DrawTextFromGameWorld() {
        for (int int0 = 0; int0 < this.todoTextList.size(); int0++) {
            TextManager.DeferedTextDraw deferedTextDraw = this.todoTextList.get(int0);
            this.DrawStringCentre(
                deferedTextDraw.font,
                deferedTextDraw.x,
                deferedTextDraw.y,
                deferedTextDraw.str,
                deferedTextDraw.r,
                deferedTextDraw.g,
                deferedTextDraw.b,
                deferedTextDraw.a
            );
        }

        this.todoTextList.clear();
    }

    public void DrawStringRight(double x, double y, String str, double r, double g, double b, double a) {
        x -= this.font.getWidth(str);
        this.font.drawString((float)x, (float)y, str, (float)r, (float)g, (float)b, (float)a);
    }

    public TextDrawObject GetDrawTextObject(String str, int maxLineWidth, boolean restrictImages) {
        return new TextDrawObject();
    }

    public void DrawTextObject(double x, double y, TextDrawObject td) {
    }

    public void DrawStringBBcode(UIFont _font, double x, double y, String str, double r, double g, double b, double a) {
    }

    public AngelCodeFont getNormalFromFontSize(int points) {
        return this.normal[points - 11];
    }

    public AngelCodeFont getFontFromEnum(UIFont _font) {
        if (_font == null) {
            return this.font;
        } else {
            AngelCodeFont angelCodeFont = this.enumToFont[_font.ordinal()];
            return angelCodeFont == null ? this.font : angelCodeFont;
        }
    }

    public int getFontHeight(UIFont fontID) {
        AngelCodeFont angelCodeFont = this.getFontFromEnum(fontID);
        return angelCodeFont.getLineHeight();
    }

    public void DrawStringRight(UIFont _font, double x, double y, String str, double r, double g, double b, double a) {
        AngelCodeFont angelCodeFont = this.getFontFromEnum(_font);
        x -= angelCodeFont.getWidth(str);
        angelCodeFont.drawString((float)x, (float)y, str, (float)r, (float)g, (float)b, (float)a);
    }

    private String getFontFilePath(String string3, String string0, String string2) {
        if (string0 != null) {
            String string1 = "media/fonts/" + string3 + "/" + string0 + "/" + string2;
            if (ZomboidFileSystem.instance.getString(string1) != string1) {
                return string1;
            }
        }

        String string4 = "media/fonts/" + string3 + "/" + string2;
        if (ZomboidFileSystem.instance.getString(string4) != string4) {
            return string4;
        } else {
            if (!"EN".equals(string3)) {
                if (string0 != null) {
                    string4 = "media/fonts/EN/" + string0 + "/" + string2;
                    if (ZomboidFileSystem.instance.getString(string4) != string4) {
                        return string4;
                    }
                }

                string4 = "media/fonts/EN/" + string2;
                if (ZomboidFileSystem.instance.getString(string4) != string4) {
                    return string4;
                }
            }

            string4 = "media/fonts/" + string2;
            return ZomboidFileSystem.instance.getString(string4) != string4 ? string4 : "media/" + string2;
        }
    }

    public void Init() throws FileNotFoundException {
        String string0 = ZomboidFileSystem.instance.getString("media/fonts/EN/fonts.txt");
        FontsFile fontsFile = new FontsFile();
        HashMap hashMap0 = new HashMap();
        fontsFile.read(string0, hashMap0);
        String string1 = Translator.getLanguage().name();
        if (!"EN".equals(string1)) {
            string0 = ZomboidFileSystem.instance.getString("media/fonts/" + string1 + "/fonts.txt");
            fontsFile.read(string0, hashMap0);
        }

        HashMap hashMap1 = new HashMap();
        String string2 = null;
        if (Core.OptionFontSize == 2) {
            string2 = "1x";
        } else if (Core.OptionFontSize == 3) {
            string2 = "2x";
        } else if (Core.OptionFontSize == 4) {
            string2 = "3x";
        } else if (Core.OptionFontSize == 5) {
            string2 = "4x";
        }

        for (AngelCodeFont angelCodeFont0 : this.enumToFont) {
            if (angelCodeFont0 != null) {
                angelCodeFont0.destroy();
            }
        }

        Arrays.fill(this.enumToFont, null);

        for (AngelCodeFont angelCodeFont1 : this.normal) {
            if (angelCodeFont1 != null) {
                angelCodeFont1.destroy();
            }
        }

        Arrays.fill(this.normal, null);

        for (UIFont uIFont : UIFont.values()) {
            FontsFileFont fontsFileFont = (FontsFileFont)hashMap0.get(uIFont.name());
            if (fontsFileFont == null) {
                DebugLog.General.warn("font \"%s\" not found in fonts.txt", uIFont.name());
            } else {
                String string3 = this.getFontFilePath(string1, string2, fontsFileFont.fnt);
                String string4 = null;
                if (fontsFileFont.img != null) {
                    string4 = this.getFontFilePath(string1, string2, fontsFileFont.img);
                }

                String string5 = string3 + "|" + string4;
                if (hashMap1.get(string5) != null) {
                    this.enumToFont[uIFont.ordinal()] = (AngelCodeFont)hashMap1.get(string5);
                } else {
                    AngelCodeFont angelCodeFont2 = new AngelCodeFont(string3, string4);
                    this.enumToFont[uIFont.ordinal()] = angelCodeFont2;
                    hashMap1.put(string5, angelCodeFont2);
                }
            }
        }

        try {
            ZomboidFileSystem.instance.IgnoreActiveFileMap.set(true);
            String string6 = new File("").getAbsolutePath().replaceAll("\\\\", "/");
            String string7 = string6 + "/media/fonts/zomboidSmall.fnt";
            String string8 = string6 + "/media/fonts/zomboidSmall_0.png";
            if (string7.startsWith("/")) {
                string7 = "/" + string7;
            }

            this.enumToFont[UIFont.DebugConsole.ordinal()] = new AngelCodeFont(string7, string8);
        } finally {
            ZomboidFileSystem.instance.IgnoreActiveFileMap.set(false);
        }

        for (int int0 = 0; int0 < this.normal.length; int0++) {
            this.normal[int0] = new AngelCodeFont("media/fonts/zomboidNormal" + (int0 + 11) + ".fnt", "media/fonts/zomboidNormal" + (int0 + 11) + "_0");
        }

        this.font = this.enumToFont[UIFont.Small.ordinal()];
        this.font2 = this.enumToFont[UIFont.Medium.ordinal()];
        this.font3 = this.enumToFont[UIFont.Large.ordinal()];
        this.font4 = this.enumToFont[UIFont.Massive.ordinal()];
        this.main1 = this.enumToFont[UIFont.MainMenu1.ordinal()];
        this.main2 = this.enumToFont[UIFont.MainMenu2.ordinal()];
        this.zombiefontcredits1 = this.enumToFont[UIFont.Cred1.ordinal()];
        this.zombiefontcredits2 = this.enumToFont[UIFont.Cred2.ordinal()];
        this.zombienew1 = this.enumToFont[UIFont.NewSmall.ordinal()];
        this.zombienew2 = this.enumToFont[UIFont.NewMedium.ordinal()];
        this.zombienew3 = this.enumToFont[UIFont.NewLarge.ordinal()];
        this.codetext = this.enumToFont[UIFont.Code.ordinal()];
        this.enumToFont[UIFont.MediumNew.ordinal()] = null;
        this.enumToFont[UIFont.AutoNormSmall.ordinal()] = null;
        this.enumToFont[UIFont.AutoNormMedium.ordinal()] = null;
        this.enumToFont[UIFont.AutoNormLarge.ordinal()] = null;
        this.zomboidDialogue = this.enumToFont[UIFont.Dialogue.ordinal()];
        this.intro = this.enumToFont[UIFont.Intro.ordinal()];
        this.handwritten = this.enumToFont[UIFont.Handwritten.ordinal()];
        this.debugConsole = this.enumToFont[UIFont.DebugConsole.ordinal()];
    }

    public int MeasureStringX(UIFont _font, String str) {
        if (GameServer.bServer && !ServerGUI.isCreated()) {
            return 0;
        } else if (str == null) {
            return 0;
        } else {
            AngelCodeFont angelCodeFont = this.getFontFromEnum(_font);
            return angelCodeFont.getWidth(str);
        }
    }

    public int MeasureStringY(UIFont _font, String str) {
        if (_font == null || str == null) {
            return 0;
        } else if (GameServer.bServer && !ServerGUI.isCreated()) {
            return 0;
        } else {
            AngelCodeFont angelCodeFont = this.getFontFromEnum(_font);
            return angelCodeFont.getHeight(str);
        }
    }

    public int MeasureFont(UIFont _font) {
        if (_font == UIFont.Small) {
            return 10;
        } else if (_font == UIFont.Dialogue) {
            return 20;
        } else if (_font == UIFont.Medium) {
            return 20;
        } else if (_font == UIFont.Large) {
            return 24;
        } else if (_font == UIFont.Massive) {
            return 30;
        } else if (_font == UIFont.MainMenu1) {
            return 30;
        } else {
            return _font == UIFont.MainMenu2 ? 30 : this.getFontFromEnum(_font).getLineHeight();
        }
    }

    public static class DeferedTextDraw {
        public double x;
        public double y;
        public UIFont font;
        public String str;
        public double r;
        public double g;
        public double b;
        public double a;

        public DeferedTextDraw(UIFont uIFont, double double0, double double1, String string, double double2, double double3, double double4, double double5) {
            this.font = uIFont;
            this.x = double0;
            this.y = double1;
            this.str = string;
            this.r = double2;
            this.g = double3;
            this.b = double4;
            this.a = double5;
        }
    }

    public interface StringDrawer {
        void draw(UIFont var1, double var2, double var4, String var6, double var7, double var9, double var11, double var13);
    }
}
