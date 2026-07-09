// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.raknet;

import java.nio.ByteBuffer;

public class TestServer {
    static RakNetPeerInterface server;
    static ByteBuffer buf = ByteBuffer.allocate(2048);

    public static void main(String[] var0) {
        server = new RakNetPeerInterface();
        server.SetServerPort(12203, 12204);
        server.Init(false);
        int int0 = server.Startup(32);
        System.out.println("Result: " + int0);
        server.SetMaximumIncomingConnections(32);
        server.SetOccasionalPing(true);
        server.SetIncomingPassword("spiffo");
        boolean boolean0 = false;

        while (!boolean0) {
            String string = "This is a test message";
            ByteBuffer byteBuffer = Receive();
            decode(byteBuffer);
        }
    }

    private static void decode(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.get() & 255;
        switch (int0) {
            case 0:
            case 1:
                System.out.println("PING");
                break;
            case 19:
                int int1 = byteBuffer.get() & 255;
                long long0 = server.getGuidFromIndex(int1);
                break;
            case 21:
                System.out.println("ID_DISCONNECTION_NOTIFICATION");
                break;
            case 22:
                System.out.println("ID_CONNECTION_LOST");
                break;
            case 25:
                System.out.println("ID_INCOMPATIBLE_PROTOCOL_VERSION");
                break;
            default:
                System.out.println("Other: " + int0);
        }
    }

    public static ByteBuffer Receive() {
        int int0 = buf.position();
        boolean boolean0 = false;

        do {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            boolean0 = server.Receive(buf);
        } while (!boolean0);

        return buf;
    }
}
