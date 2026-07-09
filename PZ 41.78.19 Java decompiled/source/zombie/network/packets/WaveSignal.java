// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network.packets;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;

public class WaveSignal implements INetworkPacket {
    int sourceX;
    int sourceY;
    int channel;
    String msg;
    String guid;
    String codes;
    float r;
    float g;
    float b;
    int signalStrength;
    boolean isTV;

    public void set(
        int int0, int int1, int int2, String string0, String string1, String string2, float float0, float float1, float float2, int int3, boolean boolean0
    ) {
        this.sourceX = int0;
        this.sourceY = int1;
        this.channel = int2;
        this.msg = string0;
        this.guid = string1;
        this.codes = string2;
        this.r = float0;
        this.g = float1;
        this.b = float2;
        this.signalStrength = int3;
        this.isTV = boolean0;
    }

    @Override
    public void parse(ByteBuffer byteBuffer, UdpConnection var2) {
        this.sourceX = byteBuffer.getInt();
        this.sourceY = byteBuffer.getInt();
        this.channel = byteBuffer.getInt();
        this.msg = null;
        if (byteBuffer.get() == 1) {
            this.msg = GameWindow.ReadString(byteBuffer);
        }

        this.guid = null;
        if (byteBuffer.get() == 1) {
            this.guid = GameWindow.ReadString(byteBuffer);
        }

        this.codes = null;
        if (byteBuffer.get() == 1) {
            this.codes = GameWindow.ReadString(byteBuffer);
        }

        this.r = byteBuffer.getFloat();
        this.g = byteBuffer.getFloat();
        this.b = byteBuffer.getFloat();
        this.signalStrength = byteBuffer.getInt();
        this.isTV = byteBuffer.get() == 1;
    }

    @Override
    public void write(ByteBufferWriter byteBufferWriter) {
        byteBufferWriter.putInt(this.sourceX);
        byteBufferWriter.putInt(this.sourceY);
        byteBufferWriter.putInt(this.channel);
        byteBufferWriter.putBoolean(this.msg != null);
        if (this.msg != null) {
            byteBufferWriter.putUTF(this.msg);
        }

        byteBufferWriter.putBoolean(this.guid != null);
        if (this.guid != null) {
            byteBufferWriter.putUTF(this.guid);
        }

        byteBufferWriter.putBoolean(this.codes != null);
        if (this.codes != null) {
            byteBufferWriter.putUTF(this.codes);
        }

        byteBufferWriter.putFloat(this.r);
        byteBufferWriter.putFloat(this.g);
        byteBufferWriter.putFloat(this.b);
        byteBufferWriter.putInt(this.signalStrength);
        byteBufferWriter.putBoolean(this.isTV);
    }

    public void process(UdpConnection udpConnection) {
        if (GameServer.bServer) {
            ZomboidRadio.getInstance()
                .SendTransmission(
                    udpConnection.getConnectedGUID(),
                    this.sourceX,
                    this.sourceY,
                    this.channel,
                    this.msg,
                    this.guid,
                    this.codes,
                    this.r,
                    this.g,
                    this.b,
                    this.signalStrength,
                    this.isTV
                );
        } else {
            ZomboidRadio.getInstance()
                .ReceiveTransmission(
                    this.sourceX, this.sourceY, this.channel, this.msg, this.guid, this.codes, this.r, this.g, this.b, this.signalStrength, this.isTV
                );
        }
    }
}
