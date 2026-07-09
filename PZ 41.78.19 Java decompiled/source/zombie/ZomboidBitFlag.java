// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;

public final class ZomboidBitFlag {
    final EnumSet<IsoFlagType> isoFlagTypeES = EnumSet.noneOf(IsoFlagType.class);

    public ZomboidBitFlag(int size) {
    }

    public ZomboidBitFlag(ZomboidBitFlag fl) {
        if (fl != null) {
            this.isoFlagTypeES.addAll(fl.isoFlagTypeES);
        }
    }

    public void set(int off, boolean b) {
        if (off < IsoFlagType.MAX.index()) {
            if (b) {
                this.isoFlagTypeES.add(IsoFlagType.fromIndex(off));
            } else {
                this.isoFlagTypeES.remove(IsoFlagType.fromIndex(off));
            }
        }
    }

    public void clear() {
        this.isoFlagTypeES.clear();
    }

    public boolean isSet(int off) {
        return this.isoFlagTypeES.contains(IsoFlagType.fromIndex(off));
    }

    public boolean isSet(IsoFlagType flag) {
        return this.isoFlagTypeES.contains(flag);
    }

    public void set(IsoFlagType flag, boolean b) {
        if (b) {
            this.isoFlagTypeES.add(flag);
        } else {
            this.isoFlagTypeES.remove(flag);
        }
    }

    public boolean isSet(IsoObjectType flag) {
        return this.isSet(flag.index());
    }

    public void set(IsoObjectType flag, boolean b) {
        this.set(flag.index(), b);
    }

    public void Or(ZomboidBitFlag zomboidBitFlag0) {
        this.isoFlagTypeES.addAll(zomboidBitFlag0.isoFlagTypeES);
    }

    public void save(DataOutputStream output) throws IOException {
    }

    public void load(DataInputStream input) throws IOException {
    }

    public void getFromLong(long l) {
    }
}
