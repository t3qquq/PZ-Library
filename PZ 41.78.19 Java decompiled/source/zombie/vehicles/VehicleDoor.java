// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.scripting.objects.VehicleScript;

public final class VehicleDoor {
    protected VehiclePart part;
    protected boolean open;
    protected boolean locked;
    protected boolean lockBroken;

    public VehicleDoor(VehiclePart _part) {
        this.part = _part;
    }

    public void init(VehicleScript.Door scriptDoor) {
        this.open = false;
        this.locked = false;
        this.lockBroken = false;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean _open) {
        this.open = _open;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean _locked) {
        this.locked = _locked;
    }

    public boolean isLockBroken() {
        return this.lockBroken;
    }

    public void setLockBroken(boolean broken) {
        this.lockBroken = broken;
    }

    public void save(ByteBuffer output) throws IOException {
        output.put((byte)(this.open ? 1 : 0));
        output.put((byte)(this.locked ? 1 : 0));
        output.put((byte)(this.lockBroken ? 1 : 0));
    }

    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        this.open = input.get() == 1;
        this.locked = input.get() == 1;
        this.lockBroken = input.get() == 1;
    }
}
