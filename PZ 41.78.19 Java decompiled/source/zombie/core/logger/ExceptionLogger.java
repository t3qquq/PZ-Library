// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.logger;

import org.lwjglx.opengl.OpenGLException;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugLog;
import zombie.debug.DebugLogStream;
import zombie.debug.LogSeverity;
import zombie.network.GameServer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;
import zombie.ui.UIManager;
import zombie.ui.UITransition;
import zombie.util.Type;

public final class ExceptionLogger {
    private static int exceptionCount;
    private static boolean bIgnore;
    private static boolean bExceptionPopup = true;
    private static long popupFrameMS = 0L;
    private static UITransition transition = new UITransition();
    private static boolean bHide;

    public static synchronized void logException(Throwable throwable) {
        logException(throwable, null);
    }

    public static synchronized void logException(Throwable throwable, String string) {
        logException(throwable, string, DebugLog.General, LogSeverity.Error);
    }

    public static synchronized void logException(Throwable throwable0, String string, DebugLogStream debugLogStream, LogSeverity logSeverity) {
        OpenGLException openGLException = Type.tryCastTo(throwable0, OpenGLException.class);
        if (openGLException != null) {
            RenderThread.logGLException(openGLException, false);
        }

        debugLogStream.printException(throwable0, string, DebugLogStream.generateCallerPrefix(), logSeverity);

        try {
            if (bIgnore) {
                return;
            }

            bIgnore = true;
            exceptionCount++;
            if (!GameServer.bServer) {
                if (bExceptionPopup) {
                    showPopup();
                }

                return;
            }
        } catch (Throwable throwable1) {
            debugLogStream.printException(throwable1, "Exception thrown while trying to logException.", LogSeverity.Error);
            return;
        } finally {
            bIgnore = false;
        }
    }

    public static void showPopup() {
        float float0 = popupFrameMS > 0L ? transition.getElapsed() : 0.0F;
        popupFrameMS = 3000L;
        transition.setIgnoreUpdateTime(true);
        transition.init(500.0F, false);
        transition.setElapsed(float0);
        bHide = false;
    }

    public static void render() {
        if (!UIManager.useUIFBO || Core.getInstance().UIRenderThisFrame) {
            boolean boolean0 = false;
            if (boolean0) {
                popupFrameMS = 3000L;
            }

            if (popupFrameMS > 0L) {
                popupFrameMS = (long)(popupFrameMS - UIManager.getMillisSinceLastRender());
                transition.update();
                int int0 = TextManager.instance.getFontHeight(UIFont.DebugConsole);
                byte byte0 = 100;
                int int1 = int0 * 2 + 4;
                int int2 = Core.getInstance().getScreenWidth() - byte0;
                int int3 = Core.getInstance().getScreenHeight() - (int)(int1 * transition.fraction());
                if (boolean0) {
                    int3 = Core.getInstance().getScreenHeight() - int1;
                }

                SpriteRenderer.instance.renderi(null, int2, int3, byte0, int1, 0.8F, 0.0F, 0.0F, 1.0F, null);
                SpriteRenderer.instance.renderi(null, int2 + 1, int3 + 1, byte0 - 2, int0 - 1, 0.0F, 0.0F, 0.0F, 1.0F, null);
                TextManager.instance.DrawStringCentre(UIFont.DebugConsole, int2 + byte0 / 2, int3, "ERROR", 1.0, 0.0, 0.0, 1.0);
                TextManager.instance
                    .DrawStringCentre(
                        UIFont.DebugConsole, int2 + byte0 / 2, int3 + int0, boolean0 ? "999" : Integer.toString(exceptionCount), 0.0, 0.0, 0.0, 1.0
                    );
                if (popupFrameMS <= 0L && !bHide) {
                    popupFrameMS = 500L;
                    transition.init(500.0F, true);
                    bHide = true;
                }
            }
        }
    }
}
