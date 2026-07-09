// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.areas.isoregion;

import java.nio.ByteBuffer;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;

public class ChunkUpdate {
    public static void writeIsoChunkIntoBuffer(IsoChunk chunk, ByteBuffer byteBuffer) {
        if (chunk != null) {
            int int0 = byteBuffer.position();
            byteBuffer.putInt(0);
            byteBuffer.putInt(chunk.maxLevel);
            int int1 = (chunk.maxLevel + 1) * 100;
            byteBuffer.putInt(int1);

            for (int int2 = 0; int2 <= chunk.maxLevel; int2++) {
                for (int int3 = 0; int3 < chunk.squares[0].length; int3++) {
                    IsoGridSquare square = chunk.squares[int2][int3];
                    byte byte0 = IsoRegions.calculateSquareFlags(square);
                    byteBuffer.put(byte0);
                }
            }

            int int4 = byteBuffer.position();
            byteBuffer.position(int0);
            byteBuffer.putInt(int4 - int0);
            byteBuffer.position(int4);
        } else {
            byteBuffer.putInt(-1);
        }
    }
}
