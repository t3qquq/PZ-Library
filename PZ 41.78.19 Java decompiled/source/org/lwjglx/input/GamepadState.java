// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.input;

import java.nio.ByteBuffer;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

public final class GamepadState {
    public boolean bPolled = false;
    public final GLFWGamepadState axesButtons = GLFWGamepadState.malloc();
    public final ByteBuffer hats = MemoryUtil.memAlloc(8);
    public int hatState = 0;

    public void set(GamepadState arg0) {
        this.bPolled = arg0.bPolled;
        this.axesButtons.set(arg0.axesButtons);
        this.hats.clear();
        arg0.hats.position(0);
        this.hats.put(arg0.hats);
        this.hatState = arg0.hatState;
    }

    public void quit() {
        this.axesButtons.free();
        MemoryUtil.memFree(this.hats);
    }
}
