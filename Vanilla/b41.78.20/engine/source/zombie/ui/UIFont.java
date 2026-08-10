// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

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
    MediumNew,
    AutoNormSmall,
    AutoNormMedium,
    AutoNormLarge,
    Dialogue,
    Intro,
    Handwritten,
    DebugConsole,
    Title;

    public static UIFont FromString(String str) {
        try {
            return valueOf(str);
        } catch (Exception exception) {
            return null;
        }
    }
}
