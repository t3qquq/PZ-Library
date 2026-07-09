// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import zombie.GameTime;

/**
 * TurboTuTone.
 */
public class HaloTextHelper {
    public static final HaloTextHelper.ColorRGB COLOR_WHITE = new HaloTextHelper.ColorRGB(255, 255, 255);
    public static final HaloTextHelper.ColorRGB COLOR_GREEN = new HaloTextHelper.ColorRGB(137, 232, 148);
    public static final HaloTextHelper.ColorRGB COLOR_RED = new HaloTextHelper.ColorRGB(255, 105, 97);
    private static String[] queuedLines = new String[4];
    private static String[] currentLines = new String[4];
    private static boolean ignoreOverheadCheckOnce = false;

    public static HaloTextHelper.ColorRGB getColorWhite() {
        return COLOR_WHITE;
    }

    public static HaloTextHelper.ColorRGB getColorGreen() {
        return COLOR_GREEN;
    }

    public static HaloTextHelper.ColorRGB getColorRed() {
        return COLOR_RED;
    }

    public static void forceNextAddText() {
        ignoreOverheadCheckOnce = true;
    }

    public static void addTextWithArrow(IsoPlayer player, String text, boolean arrowIsUp, HaloTextHelper.ColorRGB color) {
        addTextWithArrow(player, text, arrowIsUp, color.r, color.g, color.b, color.r, color.g, color.b);
    }

    public static void addTextWithArrow(IsoPlayer player, String text, boolean arrowIsUp, int r, int g, int b) {
        addTextWithArrow(player, text, arrowIsUp, r, g, b, r, g, b);
    }

    public static void addTextWithArrow(IsoPlayer player, String text, boolean arrowIsUp, HaloTextHelper.ColorRGB color, HaloTextHelper.ColorRGB arrowColor) {
        addTextWithArrow(player, text, arrowIsUp, color.r, color.g, color.b, arrowColor.r, arrowColor.g, arrowColor.b);
    }

    public static void addTextWithArrow(IsoPlayer player, String text, boolean arrowIsUp, int r, int g, int b, int aR, int aG, int aB) {
        addText(
            player,
            "[col="
                + r
                + ","
                + g
                + ","
                + b
                + "]"
                + text
                + "[/] [img=media/ui/"
                + (arrowIsUp ? "ArrowUp.png" : "ArrowDown.png")
                + ","
                + aR
                + ","
                + aG
                + ","
                + aB
                + "]"
        );
    }

    public static void addText(IsoPlayer player, String text, HaloTextHelper.ColorRGB color) {
        addText(player, text, color.r, color.g, color.b);
    }

    public static void addText(IsoPlayer player, String text, int r, int g, int b) {
        addText(player, "[col=" + r + "," + g + "," + b + "]" + text + "[/]");
    }

    public static void addText(IsoPlayer player, String text) {
        int int0 = player.getPlayerNum();
        if (!overheadContains(int0, text)) {
            String string = queuedLines[int0];
            if (string == null) {
                string = text;
            } else {
                if (string.contains(text)) {
                    return;
                }

                string = string + "[col=175,175,175], [/]" + text;
            }

            queuedLines[int0] = string;
        }
    }

    private static boolean overheadContains(int int0, String string) {
        if (ignoreOverheadCheckOnce) {
            ignoreOverheadCheckOnce = false;
            return false;
        } else {
            return currentLines[int0] != null && currentLines[int0].contains(string);
        }
    }

    public static void update() {
        for (int int0 = 0; int0 < 4; int0++) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null) {
                if (currentLines[int0] != null && player.getHaloTimerCount() <= 0.2F * GameTime.getInstance().getMultiplier()) {
                    currentLines[int0] = null;
                }

                if (queuedLines[int0] != null && player.getHaloTimerCount() <= 0.2F * GameTime.getInstance().getMultiplier()) {
                    player.setHaloNote(queuedLines[int0]);
                    currentLines[int0] = queuedLines[int0];
                    queuedLines[int0] = null;
                }
            } else {
                if (queuedLines[int0] != null) {
                    queuedLines[int0] = null;
                }

                if (currentLines[int0] != null) {
                    currentLines[int0] = null;
                }
            }
        }
    }

    public static class ColorRGB {
        public int r;
        public int g;
        public int b;
        public int a = 255;

        public ColorRGB(int _r, int _g, int _b) {
            this.r = _r;
            this.g = _g;
            this.b = _b;
        }
    }
}
