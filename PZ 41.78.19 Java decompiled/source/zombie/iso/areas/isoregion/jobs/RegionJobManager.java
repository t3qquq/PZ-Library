// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.jobs;

import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;

public final class RegionJobManager {
    private static final ConcurrentLinkedQueue<JobSquareUpdate> poolSquareUpdate = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<JobChunkUpdate> poolChunkUpdate = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<JobApplyChanges> poolApplyChanges = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<JobServerSendFullData> poolServerSendFullData = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<JobDebugResetAllData> poolDebugResetAllData = new ConcurrentLinkedQueue<>();

    public static JobSquareUpdate allocSquareUpdate(int int0, int int1, int int2, byte byte0) {
        JobSquareUpdate jobSquareUpdate = poolSquareUpdate.poll();
        if (jobSquareUpdate == null) {
            jobSquareUpdate = new JobSquareUpdate();
        }

        jobSquareUpdate.worldSquareX = int0;
        jobSquareUpdate.worldSquareY = int1;
        jobSquareUpdate.worldSquareZ = int2;
        jobSquareUpdate.newSquareFlags = byte0;
        return jobSquareUpdate;
    }

    public static JobChunkUpdate allocChunkUpdate() {
        JobChunkUpdate jobChunkUpdate = poolChunkUpdate.poll();
        if (jobChunkUpdate == null) {
            jobChunkUpdate = new JobChunkUpdate();
        }

        return jobChunkUpdate;
    }

    public static JobApplyChanges allocApplyChanges(boolean boolean0) {
        JobApplyChanges jobApplyChanges = poolApplyChanges.poll();
        if (jobApplyChanges == null) {
            jobApplyChanges = new JobApplyChanges();
        }

        jobApplyChanges.saveToDisk = boolean0;
        return jobApplyChanges;
    }

    public static JobServerSendFullData allocServerSendFullData(UdpConnection udpConnection) {
        JobServerSendFullData jobServerSendFullData = poolServerSendFullData.poll();
        if (jobServerSendFullData == null) {
            jobServerSendFullData = new JobServerSendFullData();
        }

        jobServerSendFullData.targetConn = udpConnection;
        return jobServerSendFullData;
    }

    public static JobDebugResetAllData allocDebugResetAllData() {
        JobDebugResetAllData jobDebugResetAllData = poolDebugResetAllData.poll();
        if (jobDebugResetAllData == null) {
            jobDebugResetAllData = new JobDebugResetAllData();
        }

        return jobDebugResetAllData;
    }

    public static void release(RegionJob regionJob) {
        regionJob.reset();
        switch (regionJob.getJobType()) {
            case SquareUpdate:
                poolSquareUpdate.add((JobSquareUpdate)regionJob);
                break;
            case ApplyChanges:
                poolApplyChanges.add((JobApplyChanges)regionJob);
                break;
            case ChunkUpdate:
                poolChunkUpdate.add((JobChunkUpdate)regionJob);
                break;
            case ServerSendFullData:
                poolServerSendFullData.add((JobServerSendFullData)regionJob);
                break;
            case DebugResetAllData:
                poolDebugResetAllData.add((JobDebugResetAllData)regionJob);
                break;
            default:
                if (Core.bDebug) {
                    throw new RuntimeException("No pooling for this job type?");
                }
        }
    }
}
