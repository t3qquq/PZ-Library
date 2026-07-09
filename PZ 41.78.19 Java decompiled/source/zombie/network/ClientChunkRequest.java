// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.raknet.UdpConnection;

public class ClientChunkRequest {
    public ArrayList<ClientChunkRequest.Chunk> chunks = new ArrayList<>(20);
    private static final ConcurrentLinkedQueue<ClientChunkRequest.Chunk> freeChunks = new ConcurrentLinkedQueue<>();
    public static final ConcurrentLinkedQueue<ByteBuffer> freeBuffers = new ConcurrentLinkedQueue<>();
    public boolean largeArea = false;
    int minX;
    int maxX;
    int minY;
    int maxY;

    public ClientChunkRequest.Chunk getChunk() {
        ClientChunkRequest.Chunk chunk = freeChunks.poll();
        if (chunk == null) {
            chunk = new ClientChunkRequest.Chunk();
        }

        return chunk;
    }

    public void releaseChunk(ClientChunkRequest.Chunk chunk) {
        this.releaseBuffer(chunk);
        freeChunks.add(chunk);
    }

    public void getByteBuffer(ClientChunkRequest.Chunk chunk) {
        chunk.bb = freeBuffers.poll();
        if (chunk.bb == null) {
            chunk.bb = ByteBuffer.allocate(16384);
        } else {
            chunk.bb.clear();
        }
    }

    public void releaseBuffer(ClientChunkRequest.Chunk chunk) {
        if (chunk.bb != null) {
            freeBuffers.add(chunk.bb);
            chunk.bb = null;
        }
    }

    public void releaseBuffers() {
        for (int int0 = 0; int0 < this.chunks.size(); int0++) {
            this.chunks.get(int0).bb = null;
        }
    }

    public void unpack(ByteBuffer bb, UdpConnection connection) {
        for (int int0 = 0; int0 < this.chunks.size(); int0++) {
            this.releaseBuffer(this.chunks.get(int0));
        }

        freeChunks.addAll(this.chunks);
        this.chunks.clear();
        int int1 = bb.getInt();

        for (int int2 = 0; int2 < int1; int2++) {
            ClientChunkRequest.Chunk chunk = this.getChunk();
            chunk.requestNumber = bb.getInt();
            chunk.wx = bb.getInt();
            chunk.wy = bb.getInt();
            chunk.crc = bb.getLong();
            this.chunks.add(chunk);
        }

        this.largeArea = false;
    }

    public void unpackLargeArea(ByteBuffer bb, UdpConnection connection) {
        for (int int0 = 0; int0 < this.chunks.size(); int0++) {
            this.releaseBuffer(this.chunks.get(int0));
        }

        freeChunks.addAll(this.chunks);
        this.chunks.clear();
        this.minX = bb.getInt();
        this.minY = bb.getInt();
        this.maxX = bb.getInt();
        this.maxY = bb.getInt();

        for (int int1 = this.minX; int1 < this.maxX; int1++) {
            for (int int2 = this.minY; int2 < this.maxY; int2++) {
                ClientChunkRequest.Chunk chunk = this.getChunk();
                chunk.requestNumber = bb.getInt();
                chunk.wx = int1;
                chunk.wy = int2;
                chunk.crc = 0L;
                this.releaseBuffer(chunk);
                this.chunks.add(chunk);
            }
        }

        this.largeArea = true;
    }

    public static final class Chunk {
        public int requestNumber;
        public int wx;
        public int wy;
        public long crc;
        public ByteBuffer bb;
    }
}
