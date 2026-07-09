// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.input;

import org.lwjglx.input.Controller;
import org.lwjglx.input.Controllers;
import org.lwjglx.input.GamepadState;

public class ControllerState {
    private boolean m_isCreated = false;
    private boolean m_wasPolled = false;
    private final Controller[] m_controllers = new Controller[16];
    private final GamepadState[] m_gamepadState = new GamepadState[16];

    ControllerState() {
        for (int int0 = 0; int0 < this.m_controllers.length; int0++) {
            this.m_gamepadState[int0] = new GamepadState();
        }
    }

    public void poll() {
        boolean boolean0 = !this.m_isCreated;
        this.m_isCreated = this.m_isCreated || Controllers.isCreated();
        if (this.m_isCreated) {
            if (boolean0) {
            }

            this.m_wasPolled = true;
            Controllers.poll(this.m_gamepadState);

            for (int int0 = 0; int0 < Controllers.getControllerCount(); int0++) {
                this.m_controllers[int0] = Controllers.getController(int0);
            }
        }
    }

    public boolean wasPolled() {
        return this.m_wasPolled;
    }

    public void set(ControllerState rhs) {
        this.m_isCreated = rhs.m_isCreated;

        for (int int0 = 0; int0 < this.m_controllers.length; int0++) {
            this.m_controllers[int0] = rhs.m_controllers[int0];
            if (this.m_controllers[int0] != null) {
                this.m_gamepadState[int0].set(rhs.m_gamepadState[int0]);
                this.m_controllers[int0].gamepadState = this.m_gamepadState[int0];
            }
        }

        this.m_wasPolled = rhs.m_wasPolled;
    }

    public void reset() {
        this.m_wasPolled = false;
    }

    public boolean isCreated() {
        return this.m_isCreated;
    }

    public Controller getController(int index) {
        return this.m_controllers[index];
    }

    public void quit() {
        for (int int0 = 0; int0 < this.m_controllers.length; int0++) {
            this.m_gamepadState[int0].quit();
        }
    }
}
