// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.debug.DebugLog;

public class RCONServer {
    public static final int SERVERDATA_RESPONSE_VALUE = 0;
    public static final int SERVERDATA_AUTH_RESPONSE = 2;
    public static final int SERVERDATA_EXECCOMMAND = 2;
    public static final int SERVERDATA_AUTH = 3;
    private static RCONServer instance;
    private ServerSocket welcomeSocket;
    private RCONServer.ServerThread thread;
    private String password;
    private ConcurrentLinkedQueue<RCONServer.ExecCommand> toMain = new ConcurrentLinkedQueue<>();

    private RCONServer(int int0, String string, boolean boolean0) {
        this.password = string;

        try {
            this.welcomeSocket = new ServerSocket();
            if (boolean0) {
                this.welcomeSocket.bind(new InetSocketAddress("127.0.0.1", int0));
            } else if (GameServer.IPCommandline != null) {
                this.welcomeSocket.bind(new InetSocketAddress(GameServer.IPCommandline, int0));
            } else {
                this.welcomeSocket.bind(new InetSocketAddress(int0));
            }

            DebugLog.log("RCON: listening on port " + int0);
        } catch (IOException iOException0) {
            DebugLog.log("RCON: error creating socket on port " + int0);
            iOException0.printStackTrace();

            try {
                this.welcomeSocket.close();
                this.welcomeSocket = null;
            } catch (IOException iOException1) {
                iOException1.printStackTrace();
            }

            return;
        }

        this.thread = new RCONServer.ServerThread();
        this.thread.start();
    }

    private void updateMain() {
        for (RCONServer.ExecCommand execCommand = this.toMain.poll(); execCommand != null; execCommand = this.toMain.poll()) {
            execCommand.update();
        }
    }

    public void quit() {
        if (this.welcomeSocket != null) {
            try {
                this.welcomeSocket.close();
            } catch (IOException iOException) {
            }

            this.welcomeSocket = null;
            this.thread.quit();
            this.thread = null;
        }
    }

    public static void init(int int0, String string, boolean boolean0) {
        instance = new RCONServer(int0, string, boolean0);
    }

    public static void update() {
        if (instance != null) {
            instance.updateMain();
        }
    }

    public static void shutdown() {
        if (instance != null) {
            instance.quit();
        }
    }

    private static class ClientThread extends Thread {
        public Socket socket;
        public boolean bAuth;
        public boolean bQuit;
        private String password;
        private InputStream in;
        private OutputStream out;
        private ConcurrentLinkedQueue<RCONServer.ExecCommand> toThread = new ConcurrentLinkedQueue<>();
        private int pendingCommands;

        public ClientThread(Socket socketx, String string) {
            this.socket = socketx;
            this.password = string;

            try {
                this.in = socketx.getInputStream();
                this.out = socketx.getOutputStream();
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }

            this.setName("RCONClient" + socketx.getLocalPort());
        }

        @Override
        public void run() {
            if (this.in != null) {
                if (this.out != null) {
                    while (!this.bQuit) {
                        try {
                            this.runInner();
                        } catch (SocketException socketException) {
                            this.bQuit = true;
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }

                    try {
                        this.socket.close();
                    } catch (IOException iOException) {
                        iOException.printStackTrace();
                    }

                    DebugLog.log("RCON: connection closed " + this.socket.toString());
                }
            }
        }

        private void runInner() throws IOException {
            byte[] bytes0 = new byte[4];
            int int0 = this.in.read(bytes0, 0, 4);
            if (int0 < 0) {
                this.bQuit = true;
            } else {
                ByteBuffer byteBuffer = ByteBuffer.wrap(bytes0);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int int1 = byteBuffer.getInt();
                int int2 = int1;
                byte[] bytes1 = new byte[int1];

                do {
                    int0 = this.in.read(bytes1, int1 - int2, int2);
                    if (int0 < 0) {
                        this.bQuit = true;
                        return;
                    }

                    int2 -= int0;
                } while (int2 > 0);

                byteBuffer = ByteBuffer.wrap(bytes1);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                int int3 = byteBuffer.getInt();
                int int4 = byteBuffer.getInt();
                String string = new String(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit() - byteBuffer.position() - 2);
                this.handlePacket(int3, int4, string);
            }
        }

        private void handlePacket(int int1, int int0, String string) throws IOException {
            if (!"players".equals(string)) {
                DebugLog.log("RCON: ID=" + int1 + " Type=" + int0 + " Body='" + string + "' " + this.socket.toString());
            }

            switch (int0) {
                case 0:
                    if (this.checkAuth()) {
                        ByteBuffer byteBuffer0 = ByteBuffer.allocate(14);
                        byteBuffer0.order(ByteOrder.LITTLE_ENDIAN);
                        byteBuffer0.putInt(byteBuffer0.capacity() - 4);
                        byteBuffer0.putInt(int1);
                        byteBuffer0.putInt(0);
                        byteBuffer0.putShort((short)0);
                        this.out.write(byteBuffer0.array());
                        this.out.write(byteBuffer0.array());
                    }
                    break;
                case 1:
                default:
                    DebugLog.log("RCON: unknown packet Type=" + int0);
                    break;
                case 2:
                    if (!this.checkAuth()) {
                        break;
                    }

                    RCONServer.ExecCommand execCommand = new RCONServer.ExecCommand(int1, string, this);
                    this.pendingCommands++;
                    RCONServer.instance.toMain.add(execCommand);

                    while (this.pendingCommands > 0) {
                        execCommand = this.toThread.poll();
                        if (execCommand != null) {
                            this.pendingCommands--;
                            this.handleResponse(execCommand);
                        } else {
                            try {
                                Thread.sleep(50L);
                            } catch (InterruptedException interruptedException) {
                                if (this.bQuit) {
                                    return;
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    this.bAuth = string.equals(this.password);
                    if (!this.bAuth) {
                        DebugLog.log("RCON: password doesn't match");
                        this.bQuit = true;
                    }

                    ByteBuffer byteBuffer1 = ByteBuffer.allocate(14);
                    byteBuffer1.order(ByteOrder.LITTLE_ENDIAN);
                    byteBuffer1.putInt(byteBuffer1.capacity() - 4);
                    byteBuffer1.putInt(int1);
                    byteBuffer1.putInt(0);
                    byteBuffer1.putShort((short)0);
                    this.out.write(byteBuffer1.array());
                    byteBuffer1.clear();
                    byteBuffer1.putInt(byteBuffer1.capacity() - 4);
                    byteBuffer1.putInt(this.bAuth ? int1 : -1);
                    byteBuffer1.putInt(2);
                    byteBuffer1.putShort((short)0);
                    this.out.write(byteBuffer1.array());
            }
        }

        public void handleResponse(RCONServer.ExecCommand execCommand) {
            String string = execCommand.response;
            if (string == null) {
                string = "";
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(12 + string.length() + 2);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            byteBuffer.putInt(byteBuffer.capacity() - 4);
            byteBuffer.putInt(execCommand.ID);
            byteBuffer.putInt(0);
            byteBuffer.put(string.getBytes());
            byteBuffer.putShort((short)0);

            try {
                this.out.write(byteBuffer.array());
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }

        private boolean checkAuth() throws IOException {
            if (this.bAuth) {
                return true;
            } else {
                this.bQuit = true;
                ByteBuffer byteBuffer = ByteBuffer.allocate(14);
                byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
                byteBuffer.putInt(byteBuffer.capacity() - 4);
                byteBuffer.putInt(-1);
                byteBuffer.putInt(2);
                byteBuffer.putShort((short)0);
                this.out.write(byteBuffer.array());
                return false;
            }
        }

        public void quit() {
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException iOException) {
                }
            }

            this.bQuit = true;
            this.interrupt();

            while (this.isAlive()) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }
    }

    private static class ExecCommand {
        public int ID;
        public String command;
        public String response;
        public RCONServer.ClientThread thread;

        public ExecCommand(int int0, String string, RCONServer.ClientThread clientThread) {
            this.ID = int0;
            this.command = string;
            this.thread = clientThread;
        }

        public void update() {
            this.response = GameServer.rcon(this.command);
            if (this.thread.isAlive()) {
                this.thread.toThread.add(this);
            }
        }
    }

    private class ServerThread extends Thread {
        private ArrayList<RCONServer.ClientThread> connections = new ArrayList<>();
        public boolean bQuit;

        public ServerThread() {
            this.setName("RCONServer");
        }

        @Override
        public void run() {
            while (!this.bQuit) {
                this.runInner();
            }
        }

        private void runInner() {
            try {
                Socket socket = RCONServer.this.welcomeSocket.accept();

                for (int int0 = 0; int0 < this.connections.size(); int0++) {
                    RCONServer.ClientThread clientThread0 = this.connections.get(int0);
                    if (!clientThread0.isAlive()) {
                        this.connections.remove(int0--);
                    }
                }

                if (this.connections.size() >= 5) {
                    socket.close();
                    return;
                }

                DebugLog.log("RCON: new connection " + socket.toString());
                RCONServer.ClientThread clientThread1 = new RCONServer.ClientThread(socket, RCONServer.this.password);
                this.connections.add(clientThread1);
                clientThread1.start();
            } catch (IOException iOException) {
                if (!this.bQuit) {
                    iOException.printStackTrace();
                }
            }
        }

        public void quit() {
            this.bQuit = true;

            while (this.isAlive()) {
                try {
                    Thread.sleep(50L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            for (int int0 = 0; int0 < this.connections.size(); int0++) {
                RCONServer.ClientThread clientThread = this.connections.get(int0);
                clientThread.quit();
            }
        }
    }
}
