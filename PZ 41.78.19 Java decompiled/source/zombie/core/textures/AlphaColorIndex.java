// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import java.io.Serializable;

class AlphaColorIndex implements Serializable {
    byte alpha;
    byte blue;
    byte green;
    byte red;

    AlphaColorIndex(int int0, int int1, int int2, int int3) {
        this.red = (byte)int0;
        this.green = (byte)int1;
        this.blue = (byte)int2;
        this.alpha = (byte)int3;
    }
}
