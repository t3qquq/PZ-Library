// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion.jobs;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import zombie.core.Core;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.IsoChunk;
import zombie.iso.areas.isoregion.ChunkUpdate;
import zombie.iso.areas.isoregion.IsoRegions;
import zombie.iso.areas.isoregion.data.DataChunk;
import zombie.iso.areas.isoregion.data.DataRoot;
import zombie.network.GameClient;

public class JobChunkUpdate extends RegionJob {
    private ByteBuffer buffer = ByteBuffer.allocate(65536);
    private int chunkCount = 0;
    private int bufferMaxBytes = 0;
    private long netTimeStamp = -1L;
    private UdpConnection targetConn;

    protected JobChunkUpdate() {
        super(RegionJobType.ChunkUpdate);
    }

    @Override
    protected void reset() {
        this.chunkCount = 0;
        this.bufferMaxBytes = 0;
        this.netTimeStamp = -1L;
        this.targetConn = null;
        this.buffer.clear();
    }

    public UdpConnection getTargetConn() {
        return this.targetConn;
    }

    public void setTargetConn(UdpConnection udpConnection) {
        this.targetConn = udpConnection;
    }

    public int getChunkCount() {
        return this.chunkCount;
    }

    public ByteBuffer getBuffer() {
        return this.buffer;
    }

    public long getNetTimeStamp() {
        return this.netTimeStamp;
    }

    public void setNetTimeStamp(long long0) {
        this.netTimeStamp = long0;
    }

    public boolean readChunksPacket(DataRoot dataRoot, List<Integer> list) {
        this.buffer.position(0);
        int int0 = this.buffer.getInt();
        int int1 = this.buffer.getInt();

        for (int int2 = 0; int2 < int1; int2++) {
            int int3 = this.buffer.getInt();
            int int4 = this.buffer.getInt();
            int int5 = this.buffer.getInt();
            int int6 = this.buffer.getInt();
            dataRoot.select.reset(int5 * 10, int6 * 10, 0, true, false);
            if (GameClient.bClient) {
                if (this.netTimeStamp != -1L && this.netTimeStamp < dataRoot.select.chunk.getLastUpdateStamp()) {
                    int int7 = this.buffer.position();
                    int int8 = this.buffer.getInt();
                    this.buffer.position(int7 + int8);
                    continue;
                }

                dataRoot.select.chunk.setLastUpdateStamp(this.netTimeStamp);
            } else {
                int int9 = IsoRegions.hash(int5, int6);
                if (!list.contains(int9)) {
                    list.add(int9);
                }
            }

            dataRoot.select.chunk.load(this.buffer, int4, true);
            dataRoot.select.chunk.setDirtyAllActive();
        }

        return true;
    }

    public boolean saveChunksToDisk() {
        if (Core.getInstance().isNoSave()) {
            return true;
        } else if (this.chunkCount <= 0) {
            return false;
        } else {
            this.buffer.position(0);
            int int0 = this.buffer.getInt();
            int int1 = this.buffer.getInt();

            for (int int2 = 0; int2 < int1; int2++) {
                this.buffer.mark();
                int int3 = this.buffer.getInt();
                int int4 = this.buffer.getInt();
                int int5 = this.buffer.getInt();
                int int6 = this.buffer.getInt();
                this.buffer.reset();
                File file = IsoRegions.getChunkFile(int5, int6);

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.getChannel().truncate(0L);
                    fileOutputStream.write(this.buffer.array(), this.buffer.position(), int3);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception exception) {
                    DebugLog.log(exception.getMessage());
                    exception.printStackTrace();
                }

                this.buffer.position(this.buffer.position() + int3);
            }

            return true;
        }
    }

    public boolean saveChunksToNetBuffer(ByteBuffer byteBuffer) {
        IsoRegions.log("Server max bytes buffer = " + this.bufferMaxBytes + ", chunks = " + this.chunkCount);
        byteBuffer.put(this.buffer.array(), 0, this.bufferMaxBytes);
        return true;
    }

    public boolean readChunksFromNetBuffer(ByteBuffer byteBuffer, long long0) {
        this.netTimeStamp = long0;
        byteBuffer.mark();
        this.bufferMaxBytes = byteBuffer.getInt();
        this.chunkCount = byteBuffer.getInt();
        byteBuffer.reset();
        IsoRegions.log("Client max bytes buffer = " + this.bufferMaxBytes + ", chunks = " + this.chunkCount);
        this.buffer.position(0);
        this.buffer.put(byteBuffer.array(), byteBuffer.position(), this.bufferMaxBytes);
        return true;
    }

    public boolean canAddChunk() {
        return this.buffer.position() + 1024 < this.buffer.capacity();
    }

    private int startBufferBlock() {
        if (this.chunkCount == 0) {
            this.buffer.position(0);
            this.buffer.putInt(0);
            this.buffer.putInt(0);
        }

        int int0 = this.buffer.position();
        this.buffer.putInt(0);
        return int0;
    }

    private void endBufferBlock(int int0) {
        this.bufferMaxBytes = this.buffer.position();
        this.buffer.position(int0);
        this.buffer.putInt(this.bufferMaxBytes - int0);
        this.chunkCount++;
        this.buffer.position(0);
        this.buffer.putInt(this.bufferMaxBytes);
        this.buffer.putInt(this.chunkCount);
        this.buffer.position(this.bufferMaxBytes);
    }

    public boolean addChunkFromDataChunk(DataChunk dataChunk) {
        if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
            return false;
        } else {
            int int0 = this.startBufferBlock();
            this.buffer.putInt(195);
            this.buffer.putInt(dataChunk.getChunkX());
            this.buffer.putInt(dataChunk.getChunkY());
            dataChunk.save(this.buffer);
            this.endBufferBlock(int0);
            return true;
        }
    }

    public boolean addChunkFromIsoChunk(IsoChunk chunk) {
        if (this.buffer.position() + 1024 >= this.buffer.capacity()) {
            return false;
        } else {
            int int0 = this.startBufferBlock();
            this.buffer.putInt(195);
            this.buffer.putInt(chunk.wx);
            this.buffer.putInt(chunk.wy);
            ChunkUpdate.writeIsoChunkIntoBuffer(chunk, this.buffer);
            this.endBufferBlock(int0);
            return true;
        }
    }

    public boolean addChunkFromFile(ByteBuffer byteBuffer) {
        if (this.buffer.position() + byteBuffer.limit() >= this.buffer.capacity()) {
            return false;
        } else {
            byteBuffer.getInt();
            int int0 = this.startBufferBlock();
            this.buffer.putInt(byteBuffer.getInt());
            this.buffer.putInt(byteBuffer.getInt());
            this.buffer.putInt(byteBuffer.getInt());
            byteBuffer.mark();
            int int1 = byteBuffer.getInt();
            byteBuffer.reset();
            this.buffer.put(byteBuffer.array(), byteBuffer.position(), int1);
            this.endBufferBlock(int0);
            return true;
        }
    }
}
