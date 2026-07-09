// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.spnetwork;

import java.nio.ByteBuffer;

public abstract class UdpEngine {
    public abstract void Send(ByteBuffer bb);

    public abstract void Receive(ByteBuffer bb);
}
