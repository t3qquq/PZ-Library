// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.raknet;

import java.nio.ByteBuffer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.network.GameServer;

public class VoiceTest {
    protected static boolean bQuit = false;
    protected static ByteBuffer serverBuf = ByteBuffer.allocate(500000);
    protected static ByteBuffer clientBuf = ByteBuffer.allocate(500000);
    protected static RakNetPeerInterface rnclientPeer;
    protected static RakNetPeerInterface rnserverPeer;

    protected static void rakNetServer(int int0) {
        byte byte0 = 2;
        String string = "test";
        rnserverPeer = new RakNetPeerInterface();
        DebugLog.log("Initialising RakNet...");
        rnserverPeer.Init(false);
        rnserverPeer.SetMaximumIncomingConnections(byte0);
        if (GameServer.IPCommandline != null) {
            rnserverPeer.SetServerIP(GameServer.IPCommandline);
        }

        rnserverPeer.SetServerPort(int0, int0 + 1);
        rnserverPeer.SetIncomingPassword(string);
        rnserverPeer.SetOccasionalPing(true);
        int int1 = rnserverPeer.Startup(byte0);
        System.out.println("RakNet.Startup() return code: " + int1 + " (0 means success)");
    }

    public static ByteBuffer rakNetServerReceive() {
        boolean boolean0 = false;

        do {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            boolean0 = rnserverPeer.Receive(serverBuf);
        } while (!bQuit && !boolean0);

        return serverBuf;
    }

    private static void rakNetServerDecode(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.get() & 255;
        switch (int0) {
            case 0:
            case 1:
                System.out.println("PING");
                break;
            case 16:
                System.out.println("Connection Request Accepted");
                int int2 = byteBuffer.get() & 255;
                long long1 = rnserverPeer.getGuidOfPacket();
                VoiceManager.instance.VoiceConnectReq(long1);
                break;
            case 19:
                System.out.println("ID_NEW_INCOMING_CONNECTION");
                int int1 = byteBuffer.get() & 255;
                long long0 = rnserverPeer.getGuidOfPacket();
                System.out.println("id=" + int1 + " guid=" + long0);
                VoiceManager.instance.VoiceConnectReq(long0);
                break;
            default:
                System.out.println("Received: " + int0);
        }
    }

    protected static void rakNetClient() {
        byte byte0 = 2;
        String string = "test";
        rnclientPeer = new RakNetPeerInterface();
        DebugLog.log("Initialising RakNet...");
        rnclientPeer.Init(false);
        rnclientPeer.SetMaximumIncomingConnections(byte0);
        rnclientPeer.SetClientPort(GameServer.DEFAULT_PORT + Rand.Next(10000) + 1234);
        rnclientPeer.SetOccasionalPing(true);
        int int0 = rnclientPeer.Startup(byte0);
        System.out.println("RakNet.Startup() return code: " + int0 + " (0 means success)");
    }

    public static ByteBuffer rakNetClientReceive() {
        boolean boolean0 = false;

        do {
            try {
                Thread.sleep(1L);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            boolean0 = rnclientPeer.Receive(clientBuf);
        } while (!bQuit && !boolean0);

        return clientBuf;
    }

    private static void rakNetClientDecode(ByteBuffer byteBuffer) {
        int int0 = byteBuffer.get() & 255;
        switch (int0) {
            case 0:
            case 1:
                System.out.println("PING");
                break;
            case 16:
                System.out.println("Connection Request Accepted");
                int int2 = byteBuffer.get() & 255;
                long long1 = rnclientPeer.getGuidOfPacket();
                VoiceManager.instance.VoiceConnectReq(long1);
                break;
            case 19:
                System.out.println("ID_NEW_INCOMING_CONNECTION");
                int int1 = byteBuffer.get() & 255;
                long long0 = rnclientPeer.getGuidOfPacket();
                System.out.println("id=" + int1 + " guid=" + long0);
                VoiceManager.instance.VoiceConnectReq(long0);
                break;
            default:
                System.out.println("Received: " + int0);
        }
    }

    public static void main(String[] var0) {
        DebugLog.log("VoiceTest: START");
        DebugLog.log("version=" + Core.getInstance().getVersion() + " demo=false");
        DebugLog.log("VoiceTest: SteamUtils.init - EXEC");
        SteamUtils.init();
        DebugLog.log("VoiceTest: SteamUtils.init - OK");
        DebugLog.log("VoiceTest: RakNetPeerInterface - EXEC");
        RakNetPeerInterface.init();
        DebugLog.log("VoiceTest: RakNetPeerInterface - OK");
        DebugLog.log("VoiceTest: VoiceManager.InitVMServer - EXEC");
        VoiceManager.instance.InitVMServer();
        DebugLog.log("VoiceTest: VoiceManager.InitVMServer - OK");
        DebugLog.log("VoiceTest: rakNetServer - EXEC");
        rakNetServer(16000);
        DebugLog.log("VoiceTest: rakNetServer - OK");
        DebugLog.log("VoiceTest: rakNetClient - EXEC");
        rakNetClient();
        DebugLog.log("VoiceTest: rakNetClient - OK");
        DebugLog.log("VoiceTest: rnclientPeer.Connect - EXEC");
        rnclientPeer.Connect("127.0.0.1", 16000, "test", false);
        DebugLog.log("VoiceTest: rnclientPeer.Connect - OK");
        Thread thread0 = new Thread() {
            @Override
            public void run() {
                while (!VoiceTest.bQuit && !VoiceTest.bQuit) {
                    ByteBuffer byteBuffer = VoiceTest.rakNetServerReceive();

                    try {
                        VoiceTest.rakNetServerDecode(byteBuffer);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };
        thread0.setName("serverThread");
        thread0.start();
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                while (!VoiceTest.bQuit && !VoiceTest.bQuit) {
                    ByteBuffer byteBuffer = VoiceTest.rakNetClientReceive();

                    try {
                        VoiceTest.rakNetClientDecode(byteBuffer);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        };
        thread1.setName("clientThread");
        thread1.start();
        DebugLog.log("VoiceTest: sleep 10 sec");

        try {
            Thread.sleep(10000L);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }
}
