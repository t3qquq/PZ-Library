// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;

public class PlayWorldSoundPacket implements INetworkPacket {
    String name;
    int x;
    int y;
    byte z;

    public void set(String string, int int0, int int1, byte byte0) {
        this.name = string;
        this.x = int0;
        this.y = int1;
        this.z = byte0;
    }

    public void process() {
        SoundManager.instance.PlayWorldSoundImpl(this.name, false, this.x, this.y, this.z, 1.0F, 20.0F, 2.0F, false);
    }

    public String getName() {
        return this.name;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.x = byteBuffer.getInt();
        this.y = byteBuffer.getInt();
        this.z = byteBuffer.get();
        this.name = GameWindow.ReadString(byteBuffer);
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.x);
        byteBufferWriter.putInt(this.y);
        byteBufferWriter.putByte(this.z);
        byteBufferWriter.putUTF(this.name);
    }

    @Override
    public boolean isConsistent() {
        return this.name != null && !this.name.isEmpty();
    }

    @Override
    public int getPacketSizeBytes() {
        return 12 + this.name.length();
    }

    @Override
    public String getDescription() {
        return "\n\tPlayWorldSoundPacket [name=" + this.name + " | x=" + this.x + " | y=" + this.y + " | z=" + this.z + " ]";
    }
}
