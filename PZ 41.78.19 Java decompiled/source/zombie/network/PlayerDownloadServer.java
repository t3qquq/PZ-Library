// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import org.lwjglx.BufferUtils;
import zombie.ChunkMapFilenames;
import zombie.core.Rand;
import zombie.core.logger.LoggerManager;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoChunk;

public final class PlayerDownloadServer {
    private PlayerDownloadServer.WorkerThread workerThread;
    private final UdpConnection connection;
    private boolean NetworkFileDebug;
    private final CRC32 crc32 = new CRC32();
    private final ByteBuffer bb = ByteBuffer.allocate(1000000);
    private final ByteBuffer sb = BufferUtils.createByteBuffer(1000000);
    private final ByteBufferWriter bbw = new ByteBufferWriter(this.bb);
    private final ArrayList<ClientChunkRequest> ccrWaiting = new ArrayList<>();

    public PlayerDownloadServer(UdpConnection _connection) {
        this.connection = _connection;
        this.workerThread = new PlayerDownloadServer.WorkerThread();
        this.workerThread.setDaemon(true);
        this.workerThread.setName("PlayerDownloadServer" + Rand.Next(Integer.MAX_VALUE));
        this.workerThread.start();
    }

    public void destroy() {
        this.workerThread.putCommand(PlayerDownloadServer.EThreadCommand.Quit, null);

        while (this.workerThread.isAlive()) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException interruptedException) {
            }
        }

        this.workerThread = null;
    }

    public void startConnectionTest() {
    }

    public void receiveRequestArray(ByteBuffer _bb) throws Exception {
        ClientChunkRequest clientChunkRequest = this.workerThread.freeRequests.poll();
        if (clientChunkRequest == null) {
            clientChunkRequest = new ClientChunkRequest();
        }

        clientChunkRequest.largeArea = false;
        this.ccrWaiting.add(clientChunkRequest);
        int int0 = _bb.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            if (clientChunkRequest.chunks.size() >= 20) {
                clientChunkRequest = this.workerThread.freeRequests.poll();
                if (clientChunkRequest == null) {
                    clientChunkRequest = new ClientChunkRequest();
                }

                clientChunkRequest.largeArea = false;
                this.ccrWaiting.add(clientChunkRequest);
            }

            ClientChunkRequest.Chunk chunk = clientChunkRequest.getChunk();
            chunk.requestNumber = _bb.getInt();
            chunk.wx = _bb.getInt();
            chunk.wy = _bb.getInt();
            chunk.crc = _bb.getLong();
            clientChunkRequest.chunks.add(chunk);
        }
    }

    public void receiveRequestLargeArea(ByteBuffer _bb) {
        ClientChunkRequest clientChunkRequest = new ClientChunkRequest();
        clientChunkRequest.unpackLargeArea(_bb, this.connection);

        for (int int0 = 0; int0 < clientChunkRequest.chunks.size(); int0++) {
            ClientChunkRequest.Chunk chunk0 = clientChunkRequest.chunks.get(int0);
            IsoChunk chunk1 = ServerMap.instance.getChunk(chunk0.wx, chunk0.wy);
            if (chunk1 != null) {
                clientChunkRequest.getByteBuffer(chunk0);

                try {
                    chunk1.SaveLoadedChunk(chunk0, this.crc32);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    LoggerManager.getLogger("map").write(exception);
                    clientChunkRequest.releaseBuffer(chunk0);
                }
            }
        }

        this.workerThread.putCommand(PlayerDownloadServer.EThreadCommand.RequestLargeArea, clientChunkRequest);
    }

    public void receiveCancelRequest(ByteBuffer _bb) {
        int int0 = _bb.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = _bb.getInt();
            this.workerThread.cancelQ.add(int2);
        }
    }

    public final int getWaitingRequests() {
        return this.ccrWaiting.size();
    }

    public void update() {
        this.NetworkFileDebug = DebugType.Do(DebugType.NetworkFileDebug);
        if (this.workerThread.bReady) {
            this.removeOlderDuplicateRequests();
            if (this.ccrWaiting.isEmpty()) {
                if (this.workerThread.cancelQ.isEmpty() && !this.workerThread.cancelled.isEmpty()) {
                    this.workerThread.cancelled.clear();
                }
            } else {
                ClientChunkRequest clientChunkRequest = this.ccrWaiting.remove(0);

                for (int int0 = 0; int0 < clientChunkRequest.chunks.size(); int0++) {
                    ClientChunkRequest.Chunk chunk0 = clientChunkRequest.chunks.get(int0);
                    if (this.workerThread.isRequestCancelled(chunk0)) {
                        clientChunkRequest.chunks.remove(int0--);
                        clientChunkRequest.releaseChunk(chunk0);
                    } else {
                        IsoChunk chunk1 = ServerMap.instance.getChunk(chunk0.wx, chunk0.wy);
                        if (chunk1 != null) {
                            try {
                                clientChunkRequest.getByteBuffer(chunk0);
                                chunk1.SaveLoadedChunk(chunk0, this.crc32);
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                LoggerManager.getLogger("map").write(exception);
                                this.workerThread.sendNotRequired(chunk0, false);
                                clientChunkRequest.chunks.remove(int0--);
                                clientChunkRequest.releaseChunk(chunk0);
                            }
                        }
                    }
                }

                if (clientChunkRequest.chunks.isEmpty()) {
                    this.workerThread.freeRequests.add(clientChunkRequest);
                } else {
                    this.workerThread.bReady = false;
                    this.workerThread.putCommand(PlayerDownloadServer.EThreadCommand.RequestZipArray, clientChunkRequest);
                }
            }
        }
    }

    private void removeOlderDuplicateRequests() {
        for (int int0 = this.ccrWaiting.size() - 1; int0 >= 0; int0--) {
            ClientChunkRequest clientChunkRequest0 = this.ccrWaiting.get(int0);

            for (int int1 = 0; int1 < clientChunkRequest0.chunks.size(); int1++) {
                ClientChunkRequest.Chunk chunk = clientChunkRequest0.chunks.get(int1);
                if (this.workerThread.isRequestCancelled(chunk)) {
                    clientChunkRequest0.chunks.remove(int1--);
                    clientChunkRequest0.releaseChunk(chunk);
                } else {
                    for (int int2 = int0 - 1; int2 >= 0; int2--) {
                        ClientChunkRequest clientChunkRequest1 = this.ccrWaiting.get(int2);
                        if (this.cancelDuplicateChunk(clientChunkRequest1, chunk.wx, chunk.wy)) {
                        }
                    }
                }
            }

            if (clientChunkRequest0.chunks.isEmpty()) {
                this.ccrWaiting.remove(int0);
                this.workerThread.freeRequests.add(clientChunkRequest0);
            }
        }
    }

    private boolean cancelDuplicateChunk(ClientChunkRequest clientChunkRequest, int int2, int int1) {
        for (int int0 = 0; int0 < clientChunkRequest.chunks.size(); int0++) {
            ClientChunkRequest.Chunk chunk = clientChunkRequest.chunks.get(int0);
            if (this.workerThread.isRequestCancelled(chunk)) {
                clientChunkRequest.chunks.remove(int0--);
                clientChunkRequest.releaseChunk(chunk);
            } else if (chunk.wx == int2 && chunk.wy == int1) {
                this.workerThread.sendNotRequired(chunk, false);
                clientChunkRequest.chunks.remove(int0);
                clientChunkRequest.releaseChunk(chunk);
                return true;
            }
        }

        return false;
    }

    private void sendPacket(PacketTypes.PacketType packetType) {
        this.bb.flip();
        this.sb.put(this.bb);
        this.sb.flip();
        this.connection.getPeer().SendRaw(this.sb, packetType.PacketPriority, packetType.PacketReliability, (byte)0, this.connection.getConnectedGUID(), false);
        this.sb.clear();
    }

    private ByteBufferWriter startPacket() {
        this.bb.clear();
        return this.bbw;
    }

    private static enum EThreadCommand {
        RequestLargeArea,
        RequestZipArray,
        Quit;
    }

    private final class WorkerThread extends Thread {
        boolean bQuit;
        volatile boolean bReady = true;
        final LinkedBlockingQueue<PlayerDownloadServer.WorkerThreadCommand> commandQ = new LinkedBlockingQueue<>();
        final ConcurrentLinkedQueue<ClientChunkRequest> freeRequests = new ConcurrentLinkedQueue<>();
        final ConcurrentLinkedQueue<Integer> cancelQ = new ConcurrentLinkedQueue<>();
        final ArrayList<Integer> cancelled = new ArrayList<>();
        final CRC32 crcMaker = new CRC32();
        static final int chunkSize = 1000;
        private byte[] inMemoryZip = new byte[20480];
        private final Deflater compressor = new Deflater();

        @Override
        public void run() {
            while (!this.bQuit) {
                try {
                    this.runInner();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        private void runInner() throws InterruptedException, IOException {
            MPStatistic.getInstance().PlayerDownloadServer.End();
            PlayerDownloadServer.WorkerThreadCommand workerThreadCommand = this.commandQ.take();
            MPStatistic.getInstance().PlayerDownloadServer.Start();
            switch (workerThreadCommand.e) {
                case RequestLargeArea:
                    try {
                        this.sendLargeArea(workerThreadCommand.ccr);
                        break;
                    } finally {
                        this.bReady = true;
                    }
                case RequestZipArray:
                    try {
                        this.sendArray(workerThreadCommand.ccr);
                        break;
                    } finally {
                        this.bReady = true;
                    }
                case Quit:
                    this.bQuit = true;
            }
        }

        void putCommand(PlayerDownloadServer.EThreadCommand eThreadCommand, ClientChunkRequest clientChunkRequest) {
            PlayerDownloadServer.WorkerThreadCommand workerThreadCommand = new PlayerDownloadServer.WorkerThreadCommand();
            workerThreadCommand.e = eThreadCommand;
            workerThreadCommand.ccr = clientChunkRequest;

            while (true) {
                try {
                    this.commandQ.put(workerThreadCommand);
                    return;
                } catch (InterruptedException interruptedException) {
                }
            }
        }

        private int compressChunk(ClientChunkRequest.Chunk arg0) {
            this.compressor.reset();
            this.compressor.setInput(arg0.bb.array(), 0, arg0.bb.limit());
            this.compressor.finish();
            if (this.inMemoryZip.length < arg0.bb.limit() * 1.5) {
                this.inMemoryZip = new byte[(int)(arg0.bb.limit() * 1.5)];
            }

            return this.compressor.deflate(this.inMemoryZip, 0, this.inMemoryZip.length, 3);
        }

        private void sendChunk(ClientChunkRequest.Chunk chunk) {
            try {
                long long0 = this.compressChunk(chunk);
                long long1 = long0 / 1000L;
                if (long0 % 1000L != 0L) {
                    long1++;
                }

                long long2 = 0L;

                for (int int0 = 0; int0 < long1; int0++) {
                    long long3 = long0 - long2 > 1000L ? 1000L : long0 - long2;
                    ByteBufferWriter byteBufferWriter = PlayerDownloadServer.this.startPacket();
                    PacketTypes.PacketType.SentChunk.doPacket(byteBufferWriter);
                    byteBufferWriter.putInt(chunk.requestNumber);
                    byteBufferWriter.putInt((int)long1);
                    byteBufferWriter.putInt(int0);
                    byteBufferWriter.putInt((int)long0);
                    byteBufferWriter.putInt((int)long2);
                    byteBufferWriter.putInt((int)long3);
                    byteBufferWriter.bb.put(this.inMemoryZip, (int)long2, (int)long3);
                    PlayerDownloadServer.this.sendPacket(PacketTypes.PacketType.SentChunk);
                    long2 += long3;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                this.sendNotRequired(chunk, false);
            }
        }

        private void sendNotRequired(ClientChunkRequest.Chunk chunk, boolean boolean0) {
            ByteBufferWriter byteBufferWriter = PlayerDownloadServer.this.startPacket();
            PacketTypes.PacketType.NotRequiredInZip.doPacket(byteBufferWriter);
            byteBufferWriter.putInt(1);
            byteBufferWriter.putInt(chunk.requestNumber);
            byteBufferWriter.putByte((byte)(boolean0 ? 1 : 0));
            PlayerDownloadServer.this.sendPacket(PacketTypes.PacketType.NotRequiredInZip);
        }

        private void sendLargeArea(ClientChunkRequest clientChunkRequest) throws IOException {
            for (int int0 = 0; int0 < clientChunkRequest.chunks.size(); int0++) {
                ClientChunkRequest.Chunk chunk = clientChunkRequest.chunks.get(int0);
                int int1 = chunk.wx;
                int int2 = chunk.wy;
                if (chunk.bb != null) {
                    chunk.bb.limit(chunk.bb.position());
                    chunk.bb.position(0);
                    this.sendChunk(chunk);
                    clientChunkRequest.releaseBuffer(chunk);
                } else {
                    File file = ChunkMapFilenames.instance.getFilename(int1, int2);
                    if (file.exists()) {
                        clientChunkRequest.getByteBuffer(chunk);
                        chunk.bb = IsoChunk.SafeRead("map_", int1, int2, chunk.bb);
                        this.sendChunk(chunk);
                        clientChunkRequest.releaseBuffer(chunk);
                    }
                }
            }

            ClientChunkRequest.freeBuffers.clear();
            clientChunkRequest.chunks.clear();
        }

        private void sendArray(ClientChunkRequest clientChunkRequest) throws IOException {
            for (int int0 = 0; int0 < clientChunkRequest.chunks.size(); int0++) {
                ClientChunkRequest.Chunk chunk = clientChunkRequest.chunks.get(int0);
                if (!this.isRequestCancelled(chunk)) {
                    int int1 = chunk.wx;
                    int int2 = chunk.wy;
                    long long0 = chunk.crc;
                    if (chunk.bb != null) {
                        boolean boolean0 = true;
                        if (chunk.crc != 0L) {
                            this.crcMaker.reset();
                            this.crcMaker.update(chunk.bb.array(), 0, chunk.bb.position());
                            boolean0 = chunk.crc != this.crcMaker.getValue();
                            if (boolean0 && PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(
                                    DebugType.NetworkFileDebug, int1 + "," + int2 + ": crc server=" + this.crcMaker.getValue() + " client=" + chunk.crc
                                );
                            }
                        }

                        if (boolean0) {
                            if (PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, int1 + "," + int2 + ": send=true loaded=true");
                            }

                            chunk.bb.limit(chunk.bb.position());
                            chunk.bb.position(0);
                            this.sendChunk(chunk);
                        } else {
                            if (PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, int1 + "," + int2 + ": send=false loaded=true");
                            }

                            this.sendNotRequired(chunk, true);
                        }

                        clientChunkRequest.releaseBuffer(chunk);
                    } else {
                        File file = ChunkMapFilenames.instance.getFilename(int1, int2);
                        if (file.exists()) {
                            long long1 = ChunkChecksum.getChecksum(int1, int2);
                            if (long1 != 0L && long1 == chunk.crc) {
                                if (PlayerDownloadServer.this.NetworkFileDebug) {
                                    DebugLog.log(DebugType.NetworkFileDebug, int1 + "," + int2 + ": send=false loaded=false file=true");
                                }

                                this.sendNotRequired(chunk, true);
                            } else {
                                clientChunkRequest.getByteBuffer(chunk);
                                chunk.bb = IsoChunk.SafeRead("map_", int1, int2, chunk.bb);
                                boolean boolean1 = true;
                                if (chunk.crc != 0L) {
                                    this.crcMaker.reset();
                                    this.crcMaker.update(chunk.bb.array(), 0, chunk.bb.limit());
                                    boolean1 = chunk.crc != this.crcMaker.getValue();
                                }

                                if (boolean1) {
                                    if (PlayerDownloadServer.this.NetworkFileDebug) {
                                        DebugLog.log(DebugType.NetworkFileDebug, int1 + "," + int2 + ": send=true loaded=false file=true");
                                    }

                                    this.sendChunk(chunk);
                                } else {
                                    if (PlayerDownloadServer.this.NetworkFileDebug) {
                                        DebugLog.log(DebugType.NetworkFileDebug, int1 + "," + int2 + ": send=false loaded=false file=true");
                                    }

                                    this.sendNotRequired(chunk, true);
                                }

                                clientChunkRequest.releaseBuffer(chunk);
                            }
                        } else {
                            if (PlayerDownloadServer.this.NetworkFileDebug) {
                                DebugLog.log(DebugType.NetworkFileDebug, int1 + "," + int2 + ": send=false loaded=false file=false");
                            }

                            this.sendNotRequired(chunk, long0 == 0L);
                        }
                    }
                }
            }

            for (int int3 = 0; int3 < clientChunkRequest.chunks.size(); int3++) {
                clientChunkRequest.releaseChunk(clientChunkRequest.chunks.get(int3));
            }

            clientChunkRequest.chunks.clear();
            this.freeRequests.add(clientChunkRequest);
        }

        private boolean isRequestCancelled(ClientChunkRequest.Chunk chunk) {
            for (Integer integer0 = this.cancelQ.poll(); integer0 != null; integer0 = this.cancelQ.poll()) {
                this.cancelled.add(integer0);
            }

            for (int int0 = 0; int0 < this.cancelled.size(); int0++) {
                Integer integer1 = this.cancelled.get(int0);
                if (integer1 == chunk.requestNumber) {
                    if (PlayerDownloadServer.this.NetworkFileDebug) {
                        DebugLog.log(DebugType.NetworkFileDebug, "cancelled request #" + integer1);
                    }

                    this.cancelled.remove(int0);
                    return true;
                }
            }

            return false;
        }
    }

    private static final class WorkerThreadCommand {
        PlayerDownloadServer.EThreadCommand e;
        ClientChunkRequest ccr;
    }
}
