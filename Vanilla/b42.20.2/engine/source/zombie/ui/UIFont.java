// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.ui;

import zombie.UsedFromLua;

@UsedFromLua
public enum UIFont {
    Small,
    Medium,
    Large,
    Massive,
    MainMenu1,
    MainMenu2,
    Cred1,
    Cred2,
    NewSmall,
    NewMedium,
    NewLarge,
    Code,
    CodeSmall,
    CodeMedium,
    CodeLarge,
    MediumNew,
    AutoNormSmall,
    AutoNormMedium,
    AutoNormLarge,
    Dialogue,
    Intro,
    Handwritten,
    DebugConsole,
    Title,
    SdfRegular,
    SdfBold,
    SdfItalic,
    SdfBoldItalic,
    SdfOldRegular,
    SdfOldBold,
    SdfOldItalic,
    SdfOldBoldItalic,
    SdfRobertoSans,
    SdfCaveat;

    public static UIFont FromString(String str) {
        try {
            return valueOf(str);
        } catch (Exception ex) {
            return null;
        }
    }
}
