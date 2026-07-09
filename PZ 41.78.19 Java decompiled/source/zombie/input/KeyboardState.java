// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.input;

import org.lwjglx.input.KeyEventQueue;
import org.lwjglx.input.Keyboard;

public final class KeyboardState {
    private boolean m_isCreated = false;
    private boolean[] m_keyDownStates = null;
    private final KeyEventQueue m_keyEventQueue = new KeyEventQueue();
    private boolean m_wasPolled = false;

    public void poll() {
        boolean boolean0 = !this.m_isCreated;
        this.m_isCreated = this.m_isCreated || Keyboard.isCreated();
        if (this.m_isCreated) {
            if (boolean0) {
                this.m_keyDownStates = new boolean[256];
            }

            this.m_wasPolled = true;

            for (int int0 = 0; int0 < this.m_keyDownStates.length; int0++) {
                this.m_keyDownStates[int0] = Keyboard.isKeyDown(int0);
            }
        }
    }

    public boolean wasPolled() {
        return this.m_wasPolled;
    }

    public void set(KeyboardState rhs) {
        this.m_isCreated = rhs.m_isCreated;
        if (rhs.m_keyDownStates != null) {
            if (this.m_keyDownStates == null || this.m_keyDownStates.length != rhs.m_keyDownStates.length) {
                this.m_keyDownStates = new boolean[rhs.m_keyDownStates.length];
            }

            System.arraycopy(rhs.m_keyDownStates, 0, this.m_keyDownStates, 0, this.m_keyDownStates.length);
        } else {
            this.m_keyDownStates = null;
        }

        this.m_wasPolled = rhs.m_wasPolled;
    }

    public void reset() {
        this.m_wasPolled = false;
    }

    public boolean isCreated() {
        return this.m_isCreated;
    }

    public boolean isKeyDown(int button) {
        return this.m_keyDownStates[button];
    }

    public int getKeyCount() {
        return this.m_keyDownStates.length;
    }

    public KeyEventQueue getEventQueue() {
        return this.m_keyEventQueue;
    }
}
