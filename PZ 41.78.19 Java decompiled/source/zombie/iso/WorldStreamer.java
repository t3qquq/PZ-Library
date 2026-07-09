// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import zombie.ChunkMapFilenames;
import zombie.GameWindow;
import zombie.SystemDisabler;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.gameStates.GameLoadingState;
import zombie.network.ChunkChecksum;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistics;
import zombie.network.PacketTypes;
import zombie.savefile.PlayerDB;
import zombie.vehicles.VehiclesDB2;

public final class WorldStreamer {
    static final WorldStreamer.ChunkComparator comp = new WorldStreamer.ChunkComparator();
    private static final int CRF_CANCEL = 1;
    private static final int CRF_CANCEL_SENT = 2;
    private static final int CRF_DELETE = 4;
    private static final int CRF_TIMEOUT = 8;
    private static final int CRF_RECEIVED = 16;
    private static final int BLOCK_SIZE = 1024;
    public static WorldStreamer instance = new WorldStreamer();
    private final ConcurrentLinkedQueue<IsoChunk> jobQueue = new ConcurrentLinkedQueue<>();
    private final Stack<IsoChunk> jobList = new Stack<>();
    private final ConcurrentLinkedQueue<IsoChunk> chunkRequests0 = new ConcurrentLinkedQueue<>();
    private final ArrayList<IsoChunk> chunkRequests1 = new ArrayList<>();
    private final ArrayList<WorldStreamer.ChunkRequest> pendingRequests = new ArrayList<>();
    private final ArrayList<WorldStreamer.ChunkRequest> pendingRequests1 = new ArrayList<>();
    private final ConcurrentLinkedQueue<WorldStreamer.ChunkRequest> sentRequests = new ConcurrentLinkedQueue<>();
    private final CRC32 crc32 = new CRC32();
    private final ConcurrentLinkedQueue<ByteBuffer> freeBuffers = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<WorldStreamer.ChunkRequest> waitingToSendQ = new ConcurrentLinkedQueue<>();
    private final ArrayList<WorldStreamer.ChunkRequest> tempRequests = new ArrayList<>();
    private final Inflater decompressor = new Inflater();
    private final byte[] readBuf = new byte[1024];
    private final ConcurrentLinkedQueue<WorldStreamer.ChunkRequest> waitingToCancelQ = new ConcurrentLinkedQueue<>();
    public Thread worldStreamer;
    public boolean bFinished = false;
    private IsoChunk chunkHeadMain;
    private int requestNumber;
    private boolean bCompare = false;
    private boolean NetworkFileDebug;
    private ByteBuffer inMemoryZip;
    private boolean requestingLargeArea = false;
    private volatile int largeAreaDownloads;
    private ByteBuffer bb1 = ByteBuffer.allocate(5120);
    private ByteBuffer bb2 = ByteBuffer.allocate(5120);

    private int bufferSize(int int0) {
        return (int0 + 1024 - 1) / 1024 * 1024;
    }

    private ByteBuffer ensureCapacity(ByteBuffer byteBuffer0, int int0) {
        if (byteBuffer0 == null) {
            return ByteBuffer.allocate(this.bufferSize(int0));
        } else if (byteBuffer0.capacity() < int0) {
            ByteBuffer byteBuffer1 = ByteBuffer.allocate(this.bufferSize(int0));
            return byteBuffer1.put(byteBuffer0.array(), 0, byteBuffer0.position());
        } else {
            return byteBuffer0;
        }
    }

    private ByteBuffer getByteBuffer(int int0) {
        ByteBuffer byteBuffer = this.freeBuffers.poll();
        if (byteBuffer == null) {
            return ByteBuffer.allocate(this.bufferSize(int0));
        } else {
            byteBuffer.clear();
            return this.ensureCapacity(byteBuffer, int0);
        }
    }

    private void releaseBuffer(ByteBuffer byteBuffer) {
        this.freeBuffers.add(byteBuffer);
    }

    private void sendRequests() throws IOException {
        if (!this.chunkRequests1.isEmpty()) {
            if (!this.requestingLargeArea || this.pendingRequests1.size() <= 20) {
                long long0 = System.currentTimeMillis();
                WorldStreamer.ChunkRequest chunkRequest0 = null;
                WorldStreamer.ChunkRequest chunkRequest1 = null;

                for (int int0 = this.chunkRequests1.size() - 1; int0 >= 0; int0--) {
                    IsoChunk chunk = this.chunkRequests1.get(int0);
                    WorldStreamer.ChunkRequest chunkRequest2 = WorldStreamer.ChunkRequest.alloc();
                    chunkRequest2.chunk = chunk;
                    chunkRequest2.requestNumber = this.requestNumber++;
                    chunkRequest2.time = long0;
                    chunkRequest2.crc = ChunkChecksum.getChecksum(chunk.wx, chunk.wy);
                    if (chunkRequest0 == null) {
                        chunkRequest0 = chunkRequest2;
                    } else {
                        chunkRequest1.next = chunkRequest2;
                    }

                    chunkRequest2.next = null;
                    chunkRequest1 = chunkRequest2;
                    this.pendingRequests1.add(chunkRequest2);
                    this.chunkRequests1.remove(int0);
                    if (this.requestingLargeArea && this.pendingRequests1.size() >= 40) {
                        break;
                    }
                }

                this.waitingToSendQ.add(chunkRequest0);
            }
        }
    }

    public void updateMain() {
        UdpConnection udpConnection = GameClient.connection;
        if (this.chunkHeadMain != null) {
            this.chunkRequests0.add(this.chunkHeadMain);
            this.chunkHeadMain = null;
        }

        this.tempRequests.clear();

        for (WorldStreamer.ChunkRequest chunkRequest0 = this.waitingToSendQ.poll(); chunkRequest0 != null; chunkRequest0 = this.waitingToSendQ.poll()) {
            while (chunkRequest0 != null) {
                WorldStreamer.ChunkRequest chunkRequest1 = chunkRequest0.next;
                if ((chunkRequest0.flagsWS & 1) != 0) {
                    chunkRequest0.flagsUDP |= 16;
                } else {
                    this.tempRequests.add(chunkRequest0);
                }

                chunkRequest0 = chunkRequest1;
            }
        }

        if (!this.tempRequests.isEmpty()) {
            ByteBufferWriter byteBufferWriter0 = udpConnection.startPacket();
            PacketTypes.PacketType.RequestZipList.doPacket(byteBufferWriter0);
            byteBufferWriter0.putInt(this.tempRequests.size());

            for (int int0 = 0; int0 < this.tempRequests.size(); int0++) {
                WorldStreamer.ChunkRequest chunkRequest2 = this.tempRequests.get(int0);
                byteBufferWriter0.putInt(chunkRequest2.requestNumber);
                byteBufferWriter0.putInt(chunkRequest2.chunk.wx);
                byteBufferWriter0.putInt(chunkRequest2.chunk.wy);
                byteBufferWriter0.putLong(chunkRequest2.crc);
                if (this.NetworkFileDebug) {
                    DebugLog.log(DebugType.NetworkFileDebug, "requested " + chunkRequest2.chunk.wx + "," + chunkRequest2.chunk.wy + " crc=" + chunkRequest2.crc);
                }
            }

            PacketTypes.PacketType.RequestZipList.send(udpConnection);

            for (int int1 = 0; int1 < this.tempRequests.size(); int1++) {
                WorldStreamer.ChunkRequest chunkRequest3 = this.tempRequests.get(int1);
                this.sentRequests.add(chunkRequest3);
            }
        }

        this.tempRequests.clear();

        for (WorldStreamer.ChunkRequest chunkRequest4 = this.waitingToCancelQ.poll(); chunkRequest4 != null; chunkRequest4 = this.waitingToCancelQ.poll()) {
            this.tempRequests.add(chunkRequest4);
        }

        if (!this.tempRequests.isEmpty()) {
            ByteBufferWriter byteBufferWriter1 = udpConnection.startPacket();
            PacketTypes.PacketType.NotRequiredInZip.doPacket(byteBufferWriter1);

            try {
                byteBufferWriter1.putInt(this.tempRequests.size());

                for (int int2 = 0; int2 < this.tempRequests.size(); int2++) {
                    WorldStreamer.ChunkRequest chunkRequest5 = this.tempRequests.get(int2);
                    if (this.NetworkFileDebug) {
                        DebugLog.log(DebugType.NetworkFileDebug, "cancelled " + chunkRequest5.chunk.wx + "," + chunkRequest5.chunk.wy);
                    }

                    byteBufferWriter1.putInt(chunkRequest5.requestNumber);
                    chunkRequest5.flagsMain |= 2;
                }

                PacketTypes.PacketType.NotRequiredInZip.send(udpConnection);
            } catch (Exception exception) {
                exception.printStackTrace();
                udpConnection.cancelPacket();
            }
        }
    }

    public void getStatistics() {
        MPStatistics.countChunkRequests(
            this.sentRequests.size(), this.chunkRequests0.size(), this.chunkRequests1.size(), this.pendingRequests.size(), this.pendingRequests1.size()
        );
    }

    private void loadReceivedChunks() throws DataFormatException, IOException {
        boolean boolean0 = false;
        int int0 = 0;
        int int1 = 0;

        for (int int2 = 0; int2 < this.pendingRequests1.size(); int2++) {
            WorldStreamer.ChunkRequest chunkRequest = this.pendingRequests1.get(int2);
            if ((chunkRequest.flagsUDP & 16) != 0) {
                if (boolean0) {
                    int0++;
                    if ((chunkRequest.flagsWS & 1) != 0) {
                        int1++;
                    }
                }

                if ((chunkRequest.flagsWS & 1) == 0 || (chunkRequest.flagsMain & 2) != 0) {
                    this.pendingRequests1.remove(int2--);
                    ChunkSaveWorker.instance.Update(chunkRequest.chunk);
                    if ((chunkRequest.flagsUDP & 4) != 0) {
                        File file0 = ChunkMapFilenames.instance.getFilename(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
                        if (file0.exists()) {
                            if (this.NetworkFileDebug) {
                                DebugLog.log(
                                    DebugType.NetworkFileDebug,
                                    "deleting map_" + chunkRequest.chunk.wx + "_" + chunkRequest.chunk.wy + ".bin because it doesn't exist on the server"
                                );
                            }

                            file0.delete();
                            ChunkChecksum.setChecksum(chunkRequest.chunk.wx, chunkRequest.chunk.wy, 0L);
                        }
                    }

                    ByteBuffer byteBuffer = (chunkRequest.flagsWS & 1) != 0 ? null : chunkRequest.bb;
                    if (byteBuffer != null) {
                        try {
                            byteBuffer = this.decompress(byteBuffer);
                        } catch (DataFormatException dataFormatException) {
                            DebugLog.General
                                .error(
                                    "WorldStreamer.loadReceivedChunks: Error while the chunk ("
                                        + chunkRequest.chunk.wx
                                        + ", "
                                        + chunkRequest.chunk.wy
                                        + ") was decompressing"
                                );
                            this.chunkRequests1.add(chunkRequest.chunk);
                            continue;
                        }

                        if (this.bCompare) {
                            File file1 = ChunkMapFilenames.instance.getFilename(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
                            if (file1.exists()) {
                                this.compare(chunkRequest, byteBuffer, file1);
                            }
                        }
                    }

                    if ((chunkRequest.flagsWS & 8) == 0) {
                        if ((chunkRequest.flagsWS & 1) == 0 && !chunkRequest.chunk.refs.isEmpty()) {
                            if (byteBuffer != null) {
                                byteBuffer.position(0);
                            }

                            this.DoChunk(chunkRequest.chunk, byteBuffer);
                        } else {
                            if (this.NetworkFileDebug) {
                                DebugLog.log(
                                    DebugType.NetworkFileDebug,
                                    chunkRequest.chunk.wx + "_" + chunkRequest.chunk.wy + " refs.isEmpty() SafeWrite=" + (byteBuffer != null)
                                );
                            }

                            if (byteBuffer != null) {
                                long long0 = ChunkChecksum.getChecksumIfExists(chunkRequest.chunk.wx, chunkRequest.chunk.wy);
                                this.crc32.reset();
                                this.crc32.update(byteBuffer.array(), 0, byteBuffer.position());
                                if (long0 != this.crc32.getValue()) {
                                    ChunkChecksum.setChecksum(chunkRequest.chunk.wx, chunkRequest.chunk.wy, this.crc32.getValue());
                                    IsoChunk.SafeWrite("map_", chunkRequest.chunk.wx, chunkRequest.chunk.wy, byteBuffer);
                                }
                            }

                            chunkRequest.chunk.resetForStore();

                            assert !IsoChunkMap.chunkStore.contains(chunkRequest.chunk);

                            IsoChunkMap.chunkStore.add(chunkRequest.chunk);
                        }
                    }

                    if (chunkRequest.bb != null) {
                        this.releaseBuffer(chunkRequest.bb);
                    }

                    WorldStreamer.ChunkRequest.release(chunkRequest);
                }
            }
        }

        if (boolean0 && (int0 != 0 || int1 != 0 || !this.pendingRequests1.isEmpty())) {
            DebugLog.log("nReceived=" + int0 + " nCancel=" + int1 + " nPending=" + this.pendingRequests1.size());
        }
    }

    private ByteBuffer decompress(ByteBuffer byteBuffer) throws DataFormatException {
        this.decompressor.reset();
        this.decompressor.setInput(byteBuffer.array(), 0, byteBuffer.position());
        int int0 = 0;
        if (this.inMemoryZip != null) {
            this.inMemoryZip.clear();
        }

        while (!this.decompressor.finished()) {
            int int1 = this.decompressor.inflate(this.readBuf);
            if (int1 != 0) {
                this.inMemoryZip = this.ensureCapacity(this.inMemoryZip, int0 + int1);
                this.inMemoryZip.put(this.readBuf, 0, int1);
                int0 += int1;
            } else if (!this.decompressor.finished()) {
                throw new DataFormatException();
            }
        }

        this.inMemoryZip.limit(this.inMemoryZip.position());
        return this.inMemoryZip;
    }

    private void threadLoop() throws DataFormatException, InterruptedException, IOException {
        if (GameClient.bClient && !SystemDisabler.doWorldSyncEnable) {
            this.NetworkFileDebug = DebugType.Do(DebugType.NetworkFileDebug);

            for (IsoChunk chunk0 = this.chunkRequests0.poll(); chunk0 != null; chunk0 = this.chunkRequests0.poll()) {
                while (chunk0 != null) {
                    IsoChunk chunk1 = chunk0.next;
                    this.chunkRequests1.add(chunk0);
                    chunk0 = chunk1;
                }
            }

            if (!this.chunkRequests1.isEmpty()) {
                comp.init();
                Collections.sort(this.chunkRequests1, comp);
                this.sendRequests();
            }

            this.loadReceivedChunks();
            this.cancelOutOfBoundsRequests();
            this.resendTimedOutRequests();
        }

        for (IsoChunk chunk2 = this.jobQueue.poll(); chunk2 != null; chunk2 = this.jobQueue.poll()) {
            if (this.jobList.contains(chunk2)) {
                DebugLog.log("Ignoring duplicate chunk added to WorldStreamer.jobList");
            } else {
                this.jobList.add(chunk2);
            }
        }

        if (this.jobList.isEmpty()) {
            ChunkSaveWorker.instance.Update(null);
            if (ChunkSaveWorker.instance.bSaving) {
                return;
            }

            if (!this.pendingRequests1.isEmpty()) {
                Thread.sleep(20L);
                return;
            }

            Thread.sleep(140L);
        } else {
            for (int int0 = this.jobList.size() - 1; int0 >= 0; int0--) {
                IsoChunk chunk3 = this.jobList.get(int0);
                if (chunk3.refs.isEmpty()) {
                    this.jobList.remove(int0);
                    chunk3.resetForStore();

                    assert !IsoChunkMap.chunkStore.contains(chunk3);

                    IsoChunkMap.chunkStore.add(chunk3);
                }
            }

            boolean boolean0 = !this.jobList.isEmpty();
            IsoChunk chunk4 = null;
            if (boolean0) {
                comp.init();
                Collections.sort(this.jobList, comp);
                chunk4 = this.jobList.remove(this.jobList.size() - 1);
            }

            ChunkSaveWorker.instance.Update(chunk4);
            if (chunk4 != null) {
                if (chunk4.refs.isEmpty()) {
                    chunk4.resetForStore();

                    assert !IsoChunkMap.chunkStore.contains(chunk4);

                    IsoChunkMap.chunkStore.add(chunk4);
                } else {
                    this.DoChunk(chunk4, null);
                }
            }

            if (boolean0 || ChunkSaveWorker.instance.bSaving) {
                return;
            }
        }

        if (!GameClient.bClient && !GameWindow.bLoadedAsClient && PlayerDB.isAvailable()) {
            PlayerDB.getInstance().updateWorldStreamer();
        }

        VehiclesDB2.instance.updateWorldStreamer();
        if (IsoPlayer.getInstance() != null) {
            Thread.sleep(140L);
        } else {
            Thread.sleep(0L);
        }
    }

    public void create() {
        if (this.worldStreamer == null) {
            if (!GameServer.bServer) {
                this.bFinished = false;
                this.worldStreamer = new Thread(ThreadGroups.Workers, () -> {
                    while (!this.bFinished) {
                        try {
                            this.threadLoop();
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
                this.worldStreamer.setPriority(5);
                this.worldStreamer.setDaemon(true);
                this.worldStreamer.setName("World Streamer");
                this.worldStreamer.setUncaughtExceptionHandler(GameWindow::uncaughtException);
                this.worldStreamer.start();
            }
        }
    }

    public void addJob(IsoChunk chunk, int int0, int int1, boolean boolean0) {
        if (!GameServer.bServer) {
            chunk.wx = int0;
            chunk.wy = int1;
            if (GameClient.bClient && !SystemDisabler.doWorldSyncEnable && boolean0) {
                chunk.next = this.chunkHeadMain;
                this.chunkHeadMain = chunk;
            } else {
                assert !this.jobQueue.contains(chunk);

                assert !this.jobList.contains(chunk);

                this.jobQueue.add(chunk);
            }
        }
    }

    public void DoChunk(IsoChunk chunk, ByteBuffer byteBuffer) {
        if (!GameServer.bServer) {
            this.DoChunkAlways(chunk, byteBuffer);
        }
    }

    public void DoChunkAlways(IsoChunk chunk, ByteBuffer byteBuffer) {
        if (Core.bDebug && DebugOptions.instance.WorldStreamerSlowLoad.getValue()) {
            try {
                Thread.sleep(50L);
            } catch (InterruptedException interruptedException) {
            }
        }

        if (chunk != null) {
            try {
                if (!chunk.LoadOrCreate(chunk.wx, chunk.wy, byteBuffer)) {
                    if (GameClient.bClient) {
                        ChunkChecksum.setChecksum(chunk.wx, chunk.wy, 0L);
                    }

                    chunk.Blam(chunk.wx, chunk.wy);
                    if (!chunk.LoadBrandNew(chunk.wx, chunk.wy)) {
                        return;
                    }
                }

                if (byteBuffer == null) {
                    VehiclesDB2.instance.loadChunk(chunk);
                }
            } catch (Exception exception0) {
                DebugLog.General.error("Exception thrown while trying to load chunk: " + chunk.wx + ", " + chunk.wy);
                exception0.printStackTrace();
                if (GameClient.bClient) {
                    ChunkChecksum.setChecksum(chunk.wx, chunk.wy, 0L);
                }

                chunk.Blam(chunk.wx, chunk.wy);
                if (!chunk.LoadBrandNew(chunk.wx, chunk.wy)) {
                    return;
                }
            }

            if (chunk.jobType != IsoChunk.JobType.Convert && chunk.jobType != IsoChunk.JobType.SoftReset) {
                try {
                    if (!chunk.refs.isEmpty()) {
                        chunk.loadInWorldStreamerThread();
                    }
                } catch (Exception exception1) {
                    exception1.printStackTrace();
                }

                IsoChunk.loadGridSquare.add(chunk);
            } else {
                chunk.doLoadGridsquare();
                chunk.bLoaded = true;
            }
        }
    }

    public void addJobInstant(IsoChunk chunk, int var2, int var3, int int0, int int1) {
        if (!GameServer.bServer) {
            chunk.wx = int0;
            chunk.wy = int1;

            try {
                this.DoChunkAlways(chunk, null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void addJobConvert(IsoChunk chunk, int var2, int var3, int int0, int int1) {
        if (!GameServer.bServer) {
            chunk.wx = int0;
            chunk.wy = int1;
            chunk.jobType = IsoChunk.JobType.Convert;

            try {
                this.DoChunk(chunk, null);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void addJobWipe(IsoChunk chunk, int var2, int var3, int int0, int int1) {
        chunk.wx = int0;
        chunk.wy = int1;
        chunk.jobType = IsoChunk.JobType.SoftReset;

        try {
            this.DoChunkAlways(chunk, null);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public boolean isBusy() {
        return !GameClient.bClient
                || this.chunkRequests0.isEmpty()
                    && this.chunkRequests1.isEmpty()
                    && this.chunkHeadMain == null
                    && this.waitingToSendQ.isEmpty()
                    && this.waitingToCancelQ.isEmpty()
                    && this.sentRequests.isEmpty()
                    && this.pendingRequests.isEmpty()
                    && this.pendingRequests1.isEmpty()
            ? !this.jobQueue.isEmpty() || !this.jobList.isEmpty()
            : true;
    }

    public void stop() {
        DebugLog.log("EXITDEBUG: WorldStreamer.stop 1");
        if (this.worldStreamer != null) {
            this.bFinished = true;
            DebugLog.log("EXITDEBUG: WorldStreamer.stop 2");

            while (this.worldStreamer.isAlive()) {
            }

            DebugLog.log("EXITDEBUG: WorldStreamer.stop 3");
            this.worldStreamer = null;
            this.jobList.clear();
            this.jobQueue.clear();
            DebugLog.log("EXITDEBUG: WorldStreamer.stop 4");
            ChunkSaveWorker.instance.SaveNow();
            ChunkChecksum.Reset();
            DebugLog.log("EXITDEBUG: WorldStreamer.stop 5");
        }
    }

    public void quit() {
        this.stop();
    }

    public void requestLargeAreaZip(int int0, int int1, int int4) throws IOException {
        ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
        PacketTypes.PacketType.RequestLargeAreaZip.doPacket(byteBufferWriter);
        byteBufferWriter.putInt(int0);
        byteBufferWriter.putInt(int1);
        byteBufferWriter.putInt(IsoChunkMap.ChunkGridWidth);
        PacketTypes.PacketType.RequestLargeAreaZip.send(GameClient.connection);
        this.requestingLargeArea = true;
        this.largeAreaDownloads = 0;
        GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_RequestMapData");
        int int2 = 0;
        int int3 = int0 - int4;
        int int5 = int1 - int4;
        int int6 = int0 + int4;
        int int7 = int1 + int4;

        for (int int8 = int5; int8 <= int7; int8++) {
            for (int int9 = int3; int9 <= int6; int9++) {
                if (IsoWorld.instance.MetaGrid.isValidChunk(int9, int8)) {
                    IsoChunk chunk = IsoChunkMap.chunkStore.poll();
                    if (chunk == null) {
                        chunk = new IsoChunk(IsoWorld.instance.CurrentCell);
                    } else {
                        MPStatistics.decreaseStoredChunk();
                    }

                    this.addJob(chunk, int9, int8, true);
                    int2++;
                }
            }
        }

        DebugLog.log("Requested " + int2 + " chunks from the server");
        long long0 = System.currentTimeMillis();
        long long1 = long0;
        int int10 = 0;
        int int11 = 0;

        while (this.isBusy()) {
            long long2 = System.currentTimeMillis();
            if (long2 - long1 > 60000L) {
                GameLoadingState.mapDownloadFailed = true;
                throw new IOException("map download from server timed out");
            }

            int int12 = this.largeAreaDownloads;
            GameLoadingState.GameLoadingString = Translator.getText("IGUI_MP_DownloadedMapData", int12, int2);
            long long3 = long2 - long0;
            if (long3 / 1000L > int10) {
                DebugLog.log("Received " + int12 + " / " + int2 + " chunks");
                int10 = (int)(long3 / 1000L);
            }

            if (int11 < int12) {
                long1 = long2;
                int11 = int12;
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException interruptedException) {
            }
        }

        DebugLog.log("Received " + this.largeAreaDownloads + " / " + int2 + " chunks");
        this.requestingLargeArea = false;
    }

    private void cancelOutOfBoundsRequests() {
        if (!this.requestingLargeArea) {
            for (int int0 = 0; int0 < this.pendingRequests1.size(); int0++) {
                WorldStreamer.ChunkRequest chunkRequest = this.pendingRequests1.get(int0);
                if ((chunkRequest.flagsWS & 1) == 0 && chunkRequest.chunk.refs.isEmpty()) {
                    chunkRequest.flagsWS |= 1;
                    this.waitingToCancelQ.add(chunkRequest);
                }
            }
        }
    }

    private void resendTimedOutRequests() {
        long long0 = System.currentTimeMillis();

        for (int int0 = 0; int0 < this.pendingRequests1.size(); int0++) {
            WorldStreamer.ChunkRequest chunkRequest = this.pendingRequests1.get(int0);
            if ((chunkRequest.flagsWS & 1) == 0 && chunkRequest.time + 8000L < long0) {
                if (this.NetworkFileDebug) {
                    DebugLog.log(DebugType.NetworkFileDebug, "chunk request timed out " + chunkRequest.chunk.wx + "," + chunkRequest.chunk.wy);
                }

                this.chunkRequests1.add(chunkRequest.chunk);
                chunkRequest.flagsWS |= 9;
                chunkRequest.flagsMain |= 2;
            }
        }
    }

    public void receiveChunkPart(ByteBuffer byteBuffer) {
        for (WorldStreamer.ChunkRequest chunkRequest0 = this.sentRequests.poll(); chunkRequest0 != null; chunkRequest0 = this.sentRequests.poll()) {
            this.pendingRequests.add(chunkRequest0);
        }

        int int0 = byteBuffer.getInt();
        int int1 = byteBuffer.getInt();
        int int2 = byteBuffer.getInt();
        int int3 = byteBuffer.getInt();
        int int4 = byteBuffer.getInt();
        int int5 = byteBuffer.getInt();

        for (int int6 = 0; int6 < this.pendingRequests.size(); int6++) {
            WorldStreamer.ChunkRequest chunkRequest1 = this.pendingRequests.get(int6);
            if ((chunkRequest1.flagsWS & 1) != 0) {
                this.pendingRequests.remove(int6--);
                chunkRequest1.flagsUDP |= 16;
            } else if (chunkRequest1.requestNumber == int0) {
                if (chunkRequest1.bb == null) {
                    chunkRequest1.bb = this.getByteBuffer(int3);
                }

                System.arraycopy(byteBuffer.array(), byteBuffer.position(), chunkRequest1.bb.array(), int4, int5);
                if (chunkRequest1.partsReceived == null) {
                    chunkRequest1.partsReceived = new boolean[int1];
                }

                chunkRequest1.partsReceived[int2] = true;
                if (chunkRequest1.isReceived()) {
                    if (this.NetworkFileDebug) {
                        DebugLog.log(DebugType.NetworkFileDebug, "received all parts for " + chunkRequest1.chunk.wx + "," + chunkRequest1.chunk.wy);
                    }

                    chunkRequest1.bb.position(int3);
                    this.pendingRequests.remove(int6);
                    chunkRequest1.flagsUDP |= 16;
                    if (this.requestingLargeArea) {
                        this.largeAreaDownloads++;
                    }
                }
                break;
            }
        }
    }

    public void receiveNotRequired(ByteBuffer byteBuffer) {
        for (WorldStreamer.ChunkRequest chunkRequest0 = this.sentRequests.poll(); chunkRequest0 != null; chunkRequest0 = this.sentRequests.poll()) {
            this.pendingRequests.add(chunkRequest0);
        }

        int int0 = byteBuffer.getInt();

        for (int int1 = 0; int1 < int0; int1++) {
            int int2 = byteBuffer.getInt();
            boolean boolean0 = byteBuffer.get() == 1;

            for (int int3 = 0; int3 < this.pendingRequests.size(); int3++) {
                WorldStreamer.ChunkRequest chunkRequest1 = this.pendingRequests.get(int3);
                if ((chunkRequest1.flagsWS & 1) != 0) {
                    this.pendingRequests.remove(int3--);
                    chunkRequest1.flagsUDP |= 16;
                } else if (chunkRequest1.requestNumber == int2) {
                    if (this.NetworkFileDebug) {
                        DebugLog.log(
                            DebugType.NetworkFileDebug, "NotRequiredInZip " + chunkRequest1.chunk.wx + "," + chunkRequest1.chunk.wy + " delete=" + !boolean0
                        );
                    }

                    if (!boolean0) {
                        chunkRequest1.flagsUDP |= 4;
                    }

                    this.pendingRequests.remove(int3);
                    chunkRequest1.flagsUDP |= 16;
                    if (this.requestingLargeArea) {
                        this.largeAreaDownloads++;
                    }
                    break;
                }
            }
        }
    }

    private void compare(WorldStreamer.ChunkRequest chunkRequest, ByteBuffer byteBuffer, File file) throws IOException {
        IsoChunk chunk0 = IsoChunkMap.chunkStore.poll();
        if (chunk0 == null) {
            chunk0 = new IsoChunk(IsoWorld.instance.getCell());
        } else {
            MPStatistics.decreaseStoredChunk();
        }

        chunk0.wx = chunkRequest.chunk.wx;
        chunk0.wy = chunkRequest.chunk.wy;
        IsoChunk chunk1 = IsoChunkMap.chunkStore.poll();
        if (chunk1 == null) {
            chunk1 = new IsoChunk(IsoWorld.instance.getCell());
        } else {
            MPStatistics.decreaseStoredChunk();
        }

        chunk1.wx = chunkRequest.chunk.wx;
        chunk1.wy = chunkRequest.chunk.wy;
        int int0 = byteBuffer.position();
        byteBuffer.position(0);
        chunk0.LoadFromBuffer(chunkRequest.chunk.wx, chunkRequest.chunk.wy, byteBuffer);
        byteBuffer.position(int0);
        this.crc32.reset();
        this.crc32.update(byteBuffer.array(), 0, int0);
        DebugLog.log(
            "downloaded crc=" + this.crc32.getValue() + " on-disk crc=" + ChunkChecksum.getChecksumIfExists(chunkRequest.chunk.wx, chunkRequest.chunk.wy)
        );
        chunk1.LoadFromDisk();
        DebugLog.log("downloaded size=" + int0 + " on-disk size=" + file.length());
        this.compareChunks(chunk0, chunk1);
        chunk0.resetForStore();

        assert !IsoChunkMap.chunkStore.contains(chunk0);

        IsoChunkMap.chunkStore.add(chunk0);
        chunk1.resetForStore();

        assert !IsoChunkMap.chunkStore.contains(chunk1);

        IsoChunkMap.chunkStore.add(chunk1);
    }

    private void compareChunks(IsoChunk chunk0, IsoChunk chunk1) {
        DebugLog.log("comparing " + chunk0.wx + "," + chunk0.wy);

        try {
            this.compareErosion(chunk0, chunk1);
            if (chunk0.lootRespawnHour != chunk1.lootRespawnHour) {
                DebugLog.log("lootRespawnHour " + chunk0.lootRespawnHour + " != " + chunk1.lootRespawnHour);
            }

            for (int int0 = 0; int0 < 10; int0++) {
                for (int int1 = 0; int1 < 10; int1++) {
                    IsoGridSquare square0 = chunk0.getGridSquare(int1, int0, 0);
                    IsoGridSquare square1 = chunk1.getGridSquare(int1, int0, 0);
                    this.compareSquares(square0, square1);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void compareErosion(IsoChunk chunk1, IsoChunk chunk0) {
        if (chunk1.getErosionData().init != chunk0.getErosionData().init) {
            DebugLog.log("init " + chunk1.getErosionData().init + " != " + chunk0.getErosionData().init);
        }

        if (chunk1.getErosionData().eTickStamp != chunk0.getErosionData().eTickStamp) {
            DebugLog.log("eTickStamp " + chunk1.getErosionData().eTickStamp + " != " + chunk0.getErosionData().eTickStamp);
        }

        if (chunk1.getErosionData().moisture != chunk0.getErosionData().moisture) {
            DebugLog.log("moisture " + chunk1.getErosionData().moisture + " != " + chunk0.getErosionData().moisture);
        }

        if (chunk1.getErosionData().minerals != chunk0.getErosionData().minerals) {
            DebugLog.log("minerals " + chunk1.getErosionData().minerals + " != " + chunk0.getErosionData().minerals);
        }

        if (chunk1.getErosionData().epoch != chunk0.getErosionData().epoch) {
            DebugLog.log("epoch " + chunk1.getErosionData().epoch + " != " + chunk0.getErosionData().epoch);
        }

        if (chunk1.getErosionData().soil != chunk0.getErosionData().soil) {
            DebugLog.log("soil " + chunk1.getErosionData().soil + " != " + chunk0.getErosionData().soil);
        }
    }

    private void compareSquares(IsoGridSquare square1, IsoGridSquare square0) {
        if (square1 != null && square0 != null) {
            try {
                this.bb1.clear();
                square1.save(this.bb1, null);
                this.bb1.flip();
                this.bb2.clear();
                square0.save(this.bb2, null);
                this.bb2.flip();
                if (this.bb1.compareTo(this.bb2) != 0) {
                    boolean boolean0 = true;
                    int int0 = -1;
                    if (this.bb1.limit() == this.bb2.limit()) {
                        for (int int1 = 0; int1 < this.bb1.limit(); int1++) {
                            if (this.bb1.get(int1) != this.bb2.get(int1)) {
                                int0 = int1;
                                break;
                            }
                        }

                        for (int int2 = 0; int2 < square1.getErosionData().regions.size(); int2++) {
                            if (square1.getErosionData().regions.get(int2).dispSeason != square0.getErosionData().regions.get(int2).dispSeason) {
                                DebugLog.log(
                                    "season1="
                                        + square1.getErosionData().regions.get(int2).dispSeason
                                        + " season2="
                                        + square0.getErosionData().regions.get(int2).dispSeason
                                );
                                boolean0 = false;
                            }
                        }
                    }

                    DebugLog.log(
                        "square "
                            + square1.x
                            + ","
                            + square1.y
                            + " mismatch at "
                            + int0
                            + " seasonMatch="
                            + boolean0
                            + " #regions="
                            + square1.getErosionData().regions.size()
                    );
                    if (square1.getObjects().size() == square0.getObjects().size()) {
                        for (int int3 = 0; int3 < square1.getObjects().size(); int3++) {
                            IsoObject object0 = square1.getObjects().get(int3);
                            IsoObject object1 = square0.getObjects().get(int3);
                            this.bb1.clear();
                            object0.save(this.bb1);
                            this.bb1.flip();
                            this.bb2.clear();
                            object1.save(this.bb2);
                            this.bb2.flip();
                            if (this.bb1.compareTo(this.bb2) != 0) {
                                DebugLog.log(
                                    "  1: "
                                        + object0.getClass().getName()
                                        + " "
                                        + object0.getName()
                                        + " "
                                        + (object0.sprite == null ? "no sprite" : object0.sprite.name)
                                );
                                DebugLog.log(
                                    "  2: "
                                        + object1.getClass().getName()
                                        + " "
                                        + object1.getName()
                                        + " "
                                        + (object1.sprite == null ? "no sprite" : object1.sprite.name)
                                );
                            }
                        }
                    } else {
                        for (int int4 = 0; int4 < square1.getObjects().size(); int4++) {
                            IsoObject object2 = square1.getObjects().get(int4);
                            DebugLog.log(
                                "  "
                                    + object2.getClass().getName()
                                    + " "
                                    + object2.getName()
                                    + " "
                                    + (object2.sprite == null ? "no sprite" : object2.sprite.name)
                            );
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        } else {
            if (square1 != null || square0 != null) {
                DebugLog.log("one square is null, the other isn't");
            }
        }
    }

    private static class ChunkComparator implements Comparator<IsoChunk> {
        private Vector2[] pos = new Vector2[4];

        public ChunkComparator() {
            for (int int0 = 0; int0 < 4; int0++) {
                this.pos[int0] = new Vector2();
            }
        }

        public void init() {
            for (int int0 = 0; int0 < 4; int0++) {
                Vector2 vector = this.pos[int0];
                vector.x = vector.y = -1.0F;
                IsoPlayer player = IsoPlayer.players[int0];
                if (player != null) {
                    if (player.lx == player.x && player.ly == player.y) {
                        vector.x = player.x;
                        vector.y = player.y;
                    } else {
                        vector.x = player.x - player.lx;
                        vector.y = player.y - player.ly;
                        vector.normalize();
                        vector.setLength(10.0F);
                        vector.x = vector.x + player.x;
                        vector.y = vector.y + player.y;
                    }
                }
            }
        }

        public int compare(IsoChunk chunk0, IsoChunk chunk1) {
            float float0 = Float.MAX_VALUE;
            float float1 = Float.MAX_VALUE;

            for (int int0 = 0; int0 < 4; int0++) {
                if (this.pos[int0].x != -1.0F || this.pos[int0].y != -1.0F) {
                    float float2 = this.pos[int0].x;
                    float float3 = this.pos[int0].y;
                    float0 = Math.min(float0, IsoUtils.DistanceTo(float2, float3, chunk0.wx * 10 + 5, chunk0.wy * 10 + 5));
                    float1 = Math.min(float1, IsoUtils.DistanceTo(float2, float3, chunk1.wx * 10 + 5, chunk1.wy * 10 + 5));
                }
            }

            if (float0 < float1) {
                return 1;
            } else {
                return float0 > float1 ? -1 : 0;
            }
        }
    }

    private static final class ChunkRequest {
        static final ArrayDeque<WorldStreamer.ChunkRequest> pool = new ArrayDeque<>();
        IsoChunk chunk;
        int requestNumber;
        boolean[] partsReceived = null;
        long crc;
        ByteBuffer bb;
        transient int flagsMain;
        transient int flagsUDP;
        transient int flagsWS;
        long time;
        WorldStreamer.ChunkRequest next;

        boolean isReceived() {
            if (this.partsReceived == null) {
                return false;
            } else {
                for (int int0 = 0; int0 < this.partsReceived.length; int0++) {
                    if (!this.partsReceived[int0]) {
                        return false;
                    }
                }

                return true;
            }
        }

        static WorldStreamer.ChunkRequest alloc() {
            return pool.isEmpty() ? new WorldStreamer.ChunkRequest() : pool.pop();
        }

        static void release(WorldStreamer.ChunkRequest chunkRequest) {
            chunkRequest.chunk = null;
            chunkRequest.partsReceived = null;
            chunkRequest.bb = null;
            chunkRequest.flagsMain = 0;
            chunkRequest.flagsUDP = 0;
            chunkRequest.flagsWS = 0;
            pool.push(chunkRequest);
        }
    }
}
