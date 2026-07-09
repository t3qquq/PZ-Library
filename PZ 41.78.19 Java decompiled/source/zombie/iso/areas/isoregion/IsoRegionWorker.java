// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import zombie.GameWindow;
import zombie.core.Colors;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoWorld;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.iso.areas.isoregion.jobs.JobApplyChanges;
import zombie.iso.areas.isoregion.jobs.JobChunkUpdate;
import zombie.iso.areas.isoregion.jobs.JobServerSendFullData;
import zombie.iso.areas.isoregion.jobs.JobSquareUpdate;
import zombie.iso.areas.isoregion.jobs.RegionJob;
import zombie.iso.areas.isoregion.jobs.RegionJobManager;
import zombie.iso.areas.isoregion.jobs.RegionJobType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerMap;

/**
 * TurboTuTone.
 */
public final class IsoRegionWorker {
    private Thread thread;
    private boolean bFinished;
    protected static final AtomicBoolean isRequestingBufferSwap = new AtomicBoolean(false);
    private static IsoRegionWorker instance;
    private DataRoot rootBuffer = new DataRoot();
    private List<Integer> discoveredChunks = new ArrayList<>();
    private final List<Integer> threadDiscoveredChunks = new ArrayList<>();
    private int lastThreadDiscoveredChunksSize = 0;
    private final ConcurrentLinkedQueue<RegionJob> jobQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<JobChunkUpdate> jobOutgoingQueue = new ConcurrentLinkedQueue<>();
    private final List<RegionJob> jobBatchedProcessing = new ArrayList<>();
    private final ConcurrentLinkedQueue<RegionJob> finishedJobQueue = new ConcurrentLinkedQueue<>();
    private static final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

    protected IsoRegionWorker() {
        instance = this;
    }

    protected void create() {
        if (this.thread == null) {
            this.bFinished = false;
            this.thread = new Thread(ThreadGroups.Workers, () -> {
                while (!this.bFinished) {
                    try {
                        this.thread_main_loop();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });
            this.thread.setPriority(5);
            this.thread.setDaemon(true);
            this.thread.setName("IsoRegionWorker");
            this.thread.setUncaughtExceptionHandler(GameWindow::uncaughtException);
            this.thread.start();
        }
    }

    protected void stop() {
        if (this.thread != null) {
            if (this.thread != null) {
                this.bFinished = true;

                while (this.thread.isAlive()) {
                }

                this.thread = null;
            }

            if (this.jobQueue.size() > 0) {
                DebugLog.IsoRegion.warn("IsoRegionWorker -> JobQueue has items remaining");
            }

            if (this.jobBatchedProcessing.size() > 0) {
                DebugLog.IsoRegion.warn("IsoRegionWorker -> JobBatchedProcessing has items remaining");
            }

            this.jobQueue.clear();
            this.jobOutgoingQueue.clear();
            this.jobBatchedProcessing.clear();
            this.finishedJobQueue.clear();
            this.rootBuffer = null;
            this.discoveredChunks = null;
        }
    }

    protected void EnqueueJob(RegionJob regionJob) {
        this.jobQueue.add(regionJob);
    }

    protected void ApplyChunkChanges() {
        this.ApplyChunkChanges(true);
    }

    protected void ApplyChunkChanges(boolean boolean0) {
        JobApplyChanges jobApplyChanges = RegionJobManager.allocApplyChanges(boolean0);
        this.jobQueue.add(jobApplyChanges);
    }

    private void thread_main_loop() throws InterruptedException, IsoRegionException {
        IsoRegions.PRINT_D = DebugLog.isEnabled(DebugType.IsoRegion);

        for (RegionJob regionJob = this.jobQueue.poll(); regionJob != null; regionJob = this.jobQueue.poll()) {
            switch (regionJob.getJobType()) {
                case ServerSendFullData:
                    if (!GameServer.bServer) {
                        break;
                    }

                    UdpConnection udpConnection = ((JobServerSendFullData)regionJob).getTargetConn();
                    if (udpConnection == null) {
                        if (Core.bDebug) {
                            throw new IsoRegionException("IsoRegion: Server send full data target connection == null");
                        }

                        IsoRegions.warn("IsoRegion: Server send full data target connection == null");
                        break;
                    }

                    IsoRegions.log("IsoRegion: Server Send Full Data to " + udpConnection.idStr);
                    ArrayList arrayList = new ArrayList();
                    this.rootBuffer.getAllChunks(arrayList);
                    JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                    jobChunkUpdate.setTargetConn(udpConnection);

                    for (DataChunk dataChunk : arrayList) {
                        if (!jobChunkUpdate.canAddChunk()) {
                            this.jobOutgoingQueue.add(jobChunkUpdate);
                            jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                            jobChunkUpdate.setTargetConn(udpConnection);
                        }

                        jobChunkUpdate.addChunkFromDataChunk(dataChunk);
                    }

                    if (jobChunkUpdate.getChunkCount() > 0) {
                        this.jobOutgoingQueue.add(jobChunkUpdate);
                    } else {
                        RegionJobManager.release(jobChunkUpdate);
                    }

                    this.finishedJobQueue.add(regionJob);
                    break;
                case DebugResetAllData:
                    IsoRegions.log("IsoRegion: Debug Reset All Data");

                    for (int int0 = 0; int0 < 2; int0++) {
                        this.rootBuffer.resetAllData();
                        if (int0 == 0) {
                            isRequestingBufferSwap.set(true);

                            while (isRequestingBufferSwap.get() && !this.bFinished) {
                                Thread.sleep(5L);
                            }
                        }
                    }

                    this.finishedJobQueue.add(regionJob);
                    break;
                case SquareUpdate:
                case ChunkUpdate:
                case ApplyChanges:
                    IsoRegions.log("IsoRegion: Queueing " + regionJob.getJobType() + " for batched processing.");
                    this.jobBatchedProcessing.add(regionJob);
                    if (regionJob.getJobType() == RegionJobType.ApplyChanges) {
                        this.thread_run_batched_jobs();
                        this.jobBatchedProcessing.clear();
                    }
                    break;
                default:
                    this.finishedJobQueue.add(regionJob);
            }
        }

        Thread.sleep(20L);
    }

    private void thread_run_batched_jobs() throws InterruptedException {
        IsoRegions.log("IsoRegion: Apply changes -> Batched processing " + this.jobBatchedProcessing.size() + " jobs.");

        for (int int0 = 0; int0 < 2; int0++) {
            for (int int1 = 0; int1 < this.jobBatchedProcessing.size(); int1++) {
                RegionJob regionJob0 = this.jobBatchedProcessing.get(int1);
                switch (regionJob0.getJobType()) {
                    case SquareUpdate:
                        JobSquareUpdate jobSquareUpdate0 = (JobSquareUpdate)regionJob0;
                        this.rootBuffer
                            .updateExistingSquare(
                                jobSquareUpdate0.getWorldSquareX(),
                                jobSquareUpdate0.getWorldSquareY(),
                                jobSquareUpdate0.getWorldSquareZ(),
                                jobSquareUpdate0.getNewSquareFlags()
                            );
                        break;
                    case ChunkUpdate:
                        JobChunkUpdate jobChunkUpdate0 = (JobChunkUpdate)regionJob0;
                        jobChunkUpdate0.readChunksPacket(this.rootBuffer, this.threadDiscoveredChunks);
                        break;
                    case ApplyChanges:
                        this.rootBuffer.processDirtyChunks();
                        if (int0 == 0) {
                            isRequestingBufferSwap.set(true);

                            while (isRequestingBufferSwap.get()) {
                                Thread.sleep(5L);
                            }
                        } else {
                            JobApplyChanges jobApplyChanges = (JobApplyChanges)regionJob0;
                            if (!GameClient.bClient && jobApplyChanges.isSaveToDisk()) {
                                for (int int2 = this.jobBatchedProcessing.size() - 1; int2 >= 0; int2--) {
                                    RegionJob regionJob1 = this.jobBatchedProcessing.get(int2);
                                    if (regionJob1.getJobType() == RegionJobType.ChunkUpdate || regionJob1.getJobType() == RegionJobType.SquareUpdate) {
                                        JobChunkUpdate jobChunkUpdate1;
                                        if (regionJob1.getJobType() == RegionJobType.SquareUpdate) {
                                            JobSquareUpdate jobSquareUpdate1 = (JobSquareUpdate)regionJob1;
                                            this.rootBuffer
                                                .select
                                                .reset(
                                                    jobSquareUpdate1.getWorldSquareX(),
                                                    jobSquareUpdate1.getWorldSquareY(),
                                                    jobSquareUpdate1.getWorldSquareZ(),
                                                    true,
                                                    false
                                                );
                                            jobChunkUpdate1 = RegionJobManager.allocChunkUpdate();
                                            jobChunkUpdate1.addChunkFromDataChunk(this.rootBuffer.select.chunk);
                                        } else {
                                            this.jobBatchedProcessing.remove(int2);
                                            jobChunkUpdate1 = (JobChunkUpdate)regionJob1;
                                        }

                                        jobChunkUpdate1.saveChunksToDisk();
                                        if (GameServer.bServer) {
                                            this.jobOutgoingQueue.add(jobChunkUpdate1);
                                        }
                                    }
                                }

                                if (this.threadDiscoveredChunks.size() > 0
                                    && this.threadDiscoveredChunks.size() > this.lastThreadDiscoveredChunksSize
                                    && !Core.getInstance().isNoSave()) {
                                    IsoRegions.log("IsoRegion: Apply changes -> Saving header file to disk.");
                                    File file = IsoRegions.getHeaderFile();

                                    try {
                                        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
                                        dataOutputStream.writeInt(195);
                                        dataOutputStream.writeInt(this.threadDiscoveredChunks.size());

                                        for (Integer integer : this.threadDiscoveredChunks) {
                                            dataOutputStream.writeInt(integer);
                                        }

                                        dataOutputStream.flush();
                                        dataOutputStream.close();
                                        this.lastThreadDiscoveredChunksSize = this.threadDiscoveredChunks.size();
                                    } catch (Exception exception) {
                                        DebugLog.log(exception.getMessage());
                                        exception.printStackTrace();
                                    }
                                }
                            }

                            this.finishedJobQueue.addAll(this.jobBatchedProcessing);
                        }
                }
            }
        }
    }

    protected DataRoot getRootBuffer() {
        return this.rootBuffer;
    }

    protected void setRootBuffer(DataRoot dataRoot) {
        this.rootBuffer = dataRoot;
    }

    protected void load() {
        IsoRegions.log("IsoRegion: Load save map.");
        if (!GameClient.bClient) {
            this.loadSaveMap();
        } else {
            GameClient.sendIsoRegionDataRequest();
        }
    }

    protected void update() {
        for (RegionJob regionJob = this.finishedJobQueue.poll(); regionJob != null; regionJob = this.finishedJobQueue.poll()) {
            RegionJobManager.release(regionJob);
        }

        for (JobChunkUpdate jobChunkUpdate = this.jobOutgoingQueue.poll(); jobChunkUpdate != null; jobChunkUpdate = this.jobOutgoingQueue.poll()) {
            if (GameServer.bServer) {
                IsoRegions.log("IsoRegion: sending changed datachunks packet.");

                try {
                    for (int int0 = 0; int0 < GameServer.udpEngine.connections.size(); int0++) {
                        UdpConnection udpConnection = GameServer.udpEngine.connections.get(int0);
                        if (jobChunkUpdate.getTargetConn() == null || jobChunkUpdate.getTargetConn() == udpConnection) {
                            ByteBufferWriter byteBufferWriter = udpConnection.startPacket();
                            PacketTypes.PacketType.IsoRegionServerPacket.doPacket(byteBufferWriter);
                            ByteBuffer byteBufferx = byteBufferWriter.bb;
                            byteBufferx.putLong(System.nanoTime());
                            jobChunkUpdate.saveChunksToNetBuffer(byteBufferx);
                            PacketTypes.PacketType.IsoRegionServerPacket.send(udpConnection);
                        }
                    }
                } catch (Exception exception) {
                    DebugLog.log(exception.getMessage());
                    exception.printStackTrace();
                }
            }

            RegionJobManager.release(jobChunkUpdate);
        }
    }

    protected void readServerUpdatePacket(ByteBuffer byteBufferx) {
        if (GameClient.bClient) {
            IsoRegions.log("IsoRegion: Receiving changed datachunk packet from server");

            try {
                JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                long long0 = byteBufferx.getLong();
                jobChunkUpdate.readChunksFromNetBuffer(byteBufferx, long0);
                this.EnqueueJob(jobChunkUpdate);
                this.ApplyChunkChanges();
            } catch (Exception exception) {
                DebugLog.log(exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    protected void readClientRequestFullUpdatePacket(ByteBuffer var1, UdpConnection udpConnection) {
        if (GameServer.bServer && udpConnection != null) {
            IsoRegions.log("IsoRegion: Receiving request full data packet from client");

            try {
                JobServerSendFullData jobServerSendFullData = RegionJobManager.allocServerSendFullData(udpConnection);
                this.EnqueueJob(jobServerSendFullData);
            } catch (Exception exception) {
                DebugLog.log(exception.getMessage());
                exception.printStackTrace();
            }
        }
    }

    protected void addDebugResetJob() {
        if (!GameServer.bServer && !GameClient.bClient) {
            this.EnqueueJob(RegionJobManager.allocDebugResetAllData());
        }
    }

    protected void addSquareChangedJob(int int1, int int3, int int5, boolean boolean0, byte byte0) {
        int int0 = int1 / 10;
        int int2 = int3 / 10;
        int int4 = IsoRegions.hash(int0, int2);
        if (this.discoveredChunks.contains(int4)) {
            IsoRegions.log("Update square only, plus any unprocessed chunks in a 7x7 grid.", Colors.Magenta);
            JobSquareUpdate jobSquareUpdate = RegionJobManager.allocSquareUpdate(int1, int3, int5, byte0);
            this.EnqueueJob(jobSquareUpdate);
            this.readSurroundingChunks(int0, int2, 7, false);
            this.ApplyChunkChanges();
        } else {
            if (boolean0) {
                return;
            }

            IsoRegions.log("Adding new chunk, plus any unprocessed chunks in a 7x7 grid.", Colors.Magenta);
            this.readSurroundingChunks(int0, int2, 7, true);
        }
    }

    protected void readSurroundingChunks(int int0, int int1, int int2, boolean boolean0) {
        this.readSurroundingChunks(int0, int1, int2, boolean0, false);
    }

    protected void readSurroundingChunks(int int3, int int5, int int1, boolean boolean2, boolean boolean1) {
        int int0 = 1;
        if (int1 > 0 && int1 <= IsoChunkMap.ChunkGridWidth) {
            int0 = int1 / 2;
            if (int0 + int0 >= IsoChunkMap.ChunkGridWidth) {
                int0--;
            }
        }

        int int2 = int3 - int0;
        int int4 = int5 - int0;
        int int6 = int3 + int0;
        int int7 = int5 + int0;
        JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
        boolean boolean0 = false;

        for (int int8 = int2; int8 <= int6; int8++) {
            for (int int9 = int4; int9 <= int7; int9++) {
                IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int8, int9) : IsoWorld.instance.getCell().getChunk(int8, int9);
                if (chunk != null) {
                    int int10 = IsoRegions.hash(chunk.wx, chunk.wy);
                    if (boolean1 || !this.discoveredChunks.contains(int10)) {
                        this.discoveredChunks.add(int10);
                        if (!jobChunkUpdate.canAddChunk()) {
                            this.EnqueueJob(jobChunkUpdate);
                            jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                        }

                        jobChunkUpdate.addChunkFromIsoChunk(chunk);
                        boolean0 = true;
                    }
                }
            }
        }

        if (jobChunkUpdate.getChunkCount() > 0) {
            this.EnqueueJob(jobChunkUpdate);
        } else {
            RegionJobManager.release(jobChunkUpdate);
        }

        if (boolean0 && boolean2) {
            this.ApplyChunkChanges();
        }
    }

    private void loadSaveMap() {
        try {
            boolean boolean0 = false;
            ArrayList arrayList = new ArrayList();
            File file0 = IsoRegions.getHeaderFile();
            if (file0.exists()) {
                DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file0));
                boolean0 = true;
                int int0 = dataInputStream.readInt();
                int int1 = dataInputStream.readInt();

                for (int int2 = 0; int2 < int1; int2++) {
                    int int3 = dataInputStream.readInt();
                    arrayList.add(int3);
                }

                dataInputStream.close();
            }

            File file1 = IsoRegions.getDirectory();
            File[] files = file1.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File var1, String string) {
                    return string.startsWith("datachunk_") && string.endsWith(".bin");
                }
            });
            JobChunkUpdate jobChunkUpdate = RegionJobManager.allocChunkUpdate();
            ByteBuffer byteBufferx = byteBuffer;
            boolean boolean1 = false;
            if (files != null) {
                for (File file2 : files) {
                    try (FileInputStream fileInputStream = new FileInputStream(file2)) {
                        byteBufferx.clear();
                        int int4 = fileInputStream.read(byteBufferx.array());
                        byteBufferx.limit(int4);
                        byteBufferx.mark();
                        int int5 = byteBufferx.getInt();
                        int int6 = byteBufferx.getInt();
                        int int7 = byteBufferx.getInt();
                        int int8 = byteBufferx.getInt();
                        byteBufferx.reset();
                        int int9 = IsoRegions.hash(int7, int8);
                        if (!this.discoveredChunks.contains(int9)) {
                            this.discoveredChunks.add(int9);
                        }

                        if (arrayList.contains(int9)) {
                            arrayList.remove(arrayList.indexOf(int9));
                        } else {
                            IsoRegions.warn("IsoRegion: A chunk save has been found that was not in header known chunks list.");
                        }

                        if (!jobChunkUpdate.canAddChunk()) {
                            this.EnqueueJob(jobChunkUpdate);
                            jobChunkUpdate = RegionJobManager.allocChunkUpdate();
                        }

                        jobChunkUpdate.addChunkFromFile(byteBufferx);
                        boolean1 = true;
                    }
                }
            }

            if (jobChunkUpdate.getChunkCount() > 0) {
                this.EnqueueJob(jobChunkUpdate);
            } else {
                RegionJobManager.release(jobChunkUpdate);
            }

            if (boolean1) {
                this.ApplyChunkChanges(false);
            }

            if (boolean0 && arrayList.size() > 0) {
                IsoRegions.warn("IsoRegion: " + arrayList.size() + " previously discovered chunks have not been loaded.");
                throw new IsoRegionException("IsoRegion: " + arrayList.size() + " previously discovered chunks have not been loaded.");
            }
        } catch (Exception exception) {
            DebugLog.log(exception.getMessage());
            exception.printStackTrace();
        }
    }
}
