// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.input;

import org.lwjglx.input.KeyEventQueue;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.core.Core;
import zombie.core.opengl.RenderThread;
import zombie.ui.UIManager;

public final class GameKeyboard {
    private static boolean[] bDown;
    private static boolean[] bLastDown;
    private static boolean[] bEatKey;
    public static boolean bNoEventsWhileLoading = false;
    public static boolean doLuaKeyPressed = true;
    private static final KeyboardStateCache s_keyboardStateCache = new KeyboardStateCache();

    public static void update() {
        if (!s_keyboardStateCache.getState().isCreated()) {
            s_keyboardStateCache.swap();
        } else {
            int int0 = s_keyboardStateCache.getState().getKeyCount();
            if (bDown == null) {
                bDown = new boolean[int0];
                bLastDown = new boolean[int0];
                bEatKey = new boolean[int0];
            }

            boolean boolean0 = Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry;

            for (int int1 = 1; int1 < int0; int1++) {
                bLastDown[int1] = bDown[int1];
                bDown[int1] = s_keyboardStateCache.getState().isKeyDown(int1);
                if (!bDown[int1] && bLastDown[int1]) {
                    if (bEatKey[int1]) {
                        bEatKey[int1] = false;
                        continue;
                    }

                    if (bNoEventsWhileLoading || boolean0 || LuaManager.thread == UIManager.defaultthread && UIManager.onKeyRelease(int1)) {
                        continue;
                    }

                    if (Core.bDebug && !doLuaKeyPressed) {
                        System.out.println("KEY RELEASED " + int1 + " doLuaKeyPressed=false");
                    }

                    if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
                        LuaEventManager.triggerEvent("OnKeyPressed", int1);
                    }

                    if (LuaManager.thread == UIManager.defaultthread) {
                        LuaEventManager.triggerEvent("OnCustomUIKey", int1);
                        LuaEventManager.triggerEvent("OnCustomUIKeyReleased", int1);
                    }
                }

                if (bDown[int1] && bLastDown[int1]) {
                    if (bNoEventsWhileLoading || boolean0 || LuaManager.thread == UIManager.defaultthread && UIManager.onKeyRepeat(int1)) {
                        continue;
                    }

                    if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
                        LuaEventManager.triggerEvent("OnKeyKeepPressed", int1);
                    }
                }

                if (bDown[int1]
                    && !bLastDown[int1]
                    && !bNoEventsWhileLoading
                    && !boolean0
                    && !bEatKey[int1]
                    && (LuaManager.thread != UIManager.defaultthread || !UIManager.onKeyPress(int1))
                    && !bEatKey[int1]) {
                    if (LuaManager.thread == UIManager.defaultthread && doLuaKeyPressed) {
                        LuaEventManager.triggerEvent("OnKeyStartPressed", int1);
                    }

                    if (LuaManager.thread == UIManager.defaultthread) {
                        LuaEventManager.triggerEvent("OnCustomUIKeyPressed", int1);
                    }
                }
            }

            s_keyboardStateCache.swap();
        }
    }

    public static void poll() {
        s_keyboardStateCache.poll();
    }

    /**
     * Has the key been pressed. Not continuous. That is, is the key down now, but was not down before.
     */
    public static boolean isKeyPressed(int key) {
        return isKeyDown(key) && !wasKeyDown(key);
    }

    /**
     * Is the key down. Continuous.
     */
    public static boolean isKeyDown(int key) {
        if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
            return false;
        } else {
            return bDown == null ? false : bDown[key];
        }
    }

    /**
     * Was they key down last frame. Continuous.
     */
    public static boolean wasKeyDown(int key) {
        if (Core.CurrentTextEntryBox != null && Core.CurrentTextEntryBox.DoingTextEntry) {
            return false;
        } else {
            return bLastDown == null ? false : bLastDown[key];
        }
    }

    public static void eatKeyPress(int key) {
        if (key >= 0 && key < bEatKey.length) {
            bEatKey[key] = true;
        }
    }

    public static void setDoLuaKeyPressed(boolean doIt) {
        doLuaKeyPressed = doIt;
    }

    public static KeyEventQueue getEventQueue() {
        assert Thread.currentThread() == GameWindow.GameThread;

        return s_keyboardStateCache.getState().getEventQueue();
    }

    public static KeyEventQueue getEventQueuePolling() {
        assert Thread.currentThread() == RenderThread.RenderThread;

        return s_keyboardStateCache.getStatePolling().getEventQueue();
    }
}
