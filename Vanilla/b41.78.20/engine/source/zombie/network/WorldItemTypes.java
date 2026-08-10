// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import zombie.iso.IsoObject;

public class WorldItemTypes {
    public static IsoObject createFromBuffer(ByteBuffer byteBuffer) {
        Object object = null;
        return IsoObject.factoryFromFileInput(null, byteBuffer);
    }
}
