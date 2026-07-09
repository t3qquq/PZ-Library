// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.input;

public final class MouseState {
    private boolean m_isCreated = false;
    private boolean[] m_buttonDownStates = null;
    private int m_mouseX = -1;
    private int m_mouseY = -1;
    private int m_wheelDelta = 0;
    private boolean m_wasPolled = false;

    public void poll() {
        boolean boolean0 = !this.m_isCreated;
        this.m_isCreated = this.m_isCreated || org.lwjglx.input.Mouse.isCreated();
        if (this.m_isCreated) {
            if (boolean0) {
                this.m_buttonDownStates = new boolean[org.lwjglx.input.Mouse.getButtonCount()];
            }

            this.m_mouseX = org.lwjglx.input.Mouse.getX();
            this.m_mouseY = org.lwjglx.input.Mouse.getY();
            this.m_wheelDelta = org.lwjglx.input.Mouse.getDWheel();
            this.m_wasPolled = true;

            for (int int0 = 0; int0 < this.m_buttonDownStates.length; int0++) {
                this.m_buttonDownStates[int0] = org.lwjglx.input.Mouse.isButtonDown(int0);
            }
        }
    }

    public boolean wasPolled() {
        return this.m_wasPolled;
    }

    public void set(MouseState rhs) {
        this.m_isCreated = rhs.m_isCreated;
        if (rhs.m_buttonDownStates != null) {
            if (this.m_buttonDownStates == null || this.m_buttonDownStates.length != rhs.m_buttonDownStates.length) {
                this.m_buttonDownStates = new boolean[rhs.m_buttonDownStates.length];
            }

            System.arraycopy(rhs.m_buttonDownStates, 0, this.m_buttonDownStates, 0, this.m_buttonDownStates.length);
        } else {
            this.m_buttonDownStates = null;
        }

        this.m_mouseX = rhs.m_mouseX;
        this.m_mouseY = rhs.m_mouseY;
        this.m_wheelDelta = rhs.m_wheelDelta;
        this.m_wasPolled = rhs.m_wasPolled;
    }

    public void reset() {
        this.m_wasPolled = false;
    }

    public boolean isCreated() {
        return this.m_isCreated;
    }

    public int getX() {
        return this.m_mouseX;
    }

    public int getY() {
        return this.m_mouseY;
    }

    public int getDWheel() {
        return this.m_wheelDelta;
    }

    public void resetDWheel() {
        this.m_wheelDelta = 0;
    }

    public boolean isButtonDown(int button) {
        return button >= this.m_buttonDownStates.length ? false : this.m_buttonDownStates[button];
    }

    public int getButtonCount() {
        return this.isCreated() ? this.m_buttonDownStates.length : 0;
    }

    public void setCursorPosition(int new_x, int new_y) {
        org.lwjglx.input.Mouse.setCursorPosition(new_x, new_y);
    }
}
