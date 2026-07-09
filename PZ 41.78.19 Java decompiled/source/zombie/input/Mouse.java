// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.input;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjglx.LWJGLException;
import org.lwjglx.input.Cursor;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;

public final class Mouse {
    protected static int x;
    protected static int y;
    public static boolean bLeftDown;
    public static boolean bLeftWasDown;
    public static boolean bRightDown;
    public static boolean bRightWasDown;
    public static boolean bMiddleDown;
    public static boolean bMiddleWasDown;
    public static boolean[] m_buttonDownStates;
    public static long lastActivity;
    public static int wheelDelta;
    private static final MouseStateCache s_mouseStateCache = new MouseStateCache();
    public static boolean[] UICaptured = new boolean[10];
    static Cursor blankCursor;
    static Cursor defaultCursor;
    private static boolean isCursorVisible = true;
    private static Texture mouseCursorTexture = null;

    public static int getWheelState() {
        return wheelDelta;
    }

    public static synchronized int getXA() {
        return x;
    }

    public static synchronized int getYA() {
        return y;
    }

    public static synchronized int getX() {
        return (int)(x * Core.getInstance().getZoom(0));
    }

    public static synchronized int getY() {
        return (int)(y * Core.getInstance().getZoom(0));
    }

    public static boolean isButtonDown(int number) {
        return m_buttonDownStates != null ? m_buttonDownStates[number] : false;
    }

    public static void UIBlockButtonDown(int number) {
        UICaptured[number] = true;
    }

    public static boolean isButtonDownUICheck(int number) {
        if (m_buttonDownStates != null) {
            boolean boolean0 = m_buttonDownStates[number];
            if (!boolean0) {
                UICaptured[number] = false;
            } else if (UICaptured[number]) {
                return false;
            }

            return boolean0;
        } else {
            return false;
        }
    }

    public static boolean isLeftDown() {
        return bLeftDown;
    }

    public static boolean isLeftPressed() {
        return !bLeftWasDown && bLeftDown;
    }

    public static boolean isLeftReleased() {
        return bLeftWasDown && !bLeftDown;
    }

    public static boolean isLeftUp() {
        return !bLeftDown;
    }

    public static boolean isMiddleDown() {
        return bMiddleDown;
    }

    public static boolean isMiddlePressed() {
        return !bMiddleWasDown && bMiddleDown;
    }

    public static boolean isMiddleReleased() {
        return bMiddleWasDown && !bMiddleDown;
    }

    public static boolean isMiddleUp() {
        return !bMiddleDown;
    }

    public static boolean isRightDown() {
        return bRightDown;
    }

    public static boolean isRightPressed() {
        return !bRightWasDown && bRightDown;
    }

    public static boolean isRightReleased() {
        return bRightWasDown && !bRightDown;
    }

    public static boolean isRightUp() {
        return !bRightDown;
    }

    public static synchronized void update() {
        MouseState mouseState = s_mouseStateCache.getState();
        if (!mouseState.isCreated()) {
            s_mouseStateCache.swap();

            try {
                org.lwjglx.input.Mouse.create();
            } catch (LWJGLException lWJGLException) {
                lWJGLException.printStackTrace();
            }
        } else {
            bLeftWasDown = bLeftDown;
            bRightWasDown = bRightDown;
            bMiddleWasDown = bMiddleDown;
            int int0 = x;
            int int1 = y;
            x = mouseState.getX();
            y = Core.getInstance().getScreenHeight() - mouseState.getY() - 1;
            bLeftDown = mouseState.isButtonDown(0);
            bRightDown = mouseState.isButtonDown(1);
            bMiddleDown = mouseState.isButtonDown(2);
            wheelDelta = mouseState.getDWheel();
            mouseState.resetDWheel();
            if (m_buttonDownStates == null) {
                m_buttonDownStates = new boolean[mouseState.getButtonCount()];
            }

            for (int int2 = 0; int2 < m_buttonDownStates.length; int2++) {
                m_buttonDownStates[int2] = mouseState.isButtonDown(int2);
            }

            if (int0 != x || int1 != y || wheelDelta != 0 || bLeftWasDown != bLeftDown || bRightWasDown != bRightDown || bMiddleWasDown != bMiddleDown) {
                lastActivity = System.currentTimeMillis();
            }

            s_mouseStateCache.swap();
        }
    }

    public static void poll() {
        s_mouseStateCache.poll();
    }

    public static synchronized void setXY(int _x, int _y) {
        s_mouseStateCache.getState().setCursorPosition(_x, Core.getInstance().getOffscreenHeight(0) - 1 - _y);
    }

    public static Cursor loadCursor(String filename) throws LWJGLException {
        File file = ZomboidFileSystem.instance.getMediaFile("ui/" + filename);
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = ImageIO.read(file);
            int int0 = bufferedImage.getWidth();
            int int1 = bufferedImage.getHeight();
            int[] ints = new int[int0 * int1];

            for (int int2 = 0; int2 < ints.length; int2++) {
                int int3 = int2 % int0;
                int int4 = int1 - 1 - int2 / int0;
                ints[int2] = bufferedImage.getRGB(int3, int4);
            }

            IntBuffer intBuffer = BufferUtils.createIntBuffer(int0 * int1);
            intBuffer.put(ints);
            intBuffer.rewind();
            byte byte0 = 1;
            byte byte1 = 1;
            return new Cursor(int0, int1, byte0, byte1, 1, intBuffer, null);
        } catch (Exception exception) {
            return null;
        }
    }

    public static void initCustomCursor() {
        if (blankCursor == null) {
            try {
                blankCursor = loadCursor("cursor_blank.png");
                defaultCursor = loadCursor("cursor_white.png");
            } catch (LWJGLException lWJGLException0) {
                lWJGLException0.printStackTrace();
            }
        }

        if (defaultCursor != null) {
            try {
                org.lwjglx.input.Mouse.setNativeCursor(defaultCursor);
            } catch (LWJGLException lWJGLException1) {
                lWJGLException1.printStackTrace();
            }
        }
    }

    public static void setCursorVisible(boolean bVisible) {
        isCursorVisible = bVisible;
    }

    public static boolean isCursorVisible() {
        return isCursorVisible;
    }

    public static void renderCursorTexture() {
        if (isCursorVisible()) {
            if (mouseCursorTexture == null) {
                mouseCursorTexture = Texture.getSharedTexture("media/ui/cursor_white.png");
            }

            if (mouseCursorTexture != null && mouseCursorTexture.isReady()) {
                int int0 = getXA();
                int int1 = getYA();
                byte byte0 = 1;
                byte byte1 = 1;
                SpriteRenderer.instance
                    .render(
                        mouseCursorTexture,
                        int0 - byte0,
                        int1 - byte1,
                        mouseCursorTexture.getWidth(),
                        mouseCursorTexture.getHeight(),
                        1.0F,
                        1.0F,
                        1.0F,
                        1.0F,
                        null
                    );
            }
        }
    }
}
