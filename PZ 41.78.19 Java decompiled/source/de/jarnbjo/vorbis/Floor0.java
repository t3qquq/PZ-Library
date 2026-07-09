// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;

class Floor0 extends Floor {
    private int order;
    private int rate;
    private int barkMapSize;
    private int amplitudeBits;
    private int amplitudeOffset;
    private int[] bookList;

    protected Floor0(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
        this.order = bitInputStream.getInt(8);
        this.rate = bitInputStream.getInt(16);
        this.barkMapSize = bitInputStream.getInt(16);
        this.amplitudeBits = bitInputStream.getInt(6);
        this.amplitudeOffset = bitInputStream.getInt(8);
        int int0 = bitInputStream.getInt(4) + 1;
        this.bookList = new int[int0];

        for (int int1 = 0; int1 < this.bookList.length; int1++) {
            this.bookList[int1] = bitInputStream.getInt(8);
            if (this.bookList[int1] > setupHeader.getCodeBooks().length) {
                throw new VorbisFormatException("A floor0_book_list entry is higher than the code book count.");
            }
        }
    }

    @Override
    protected int getType() {
        return 0;
    }

    @Override
    protected Floor decodeFloor(VorbisStream var1, BitInputStream var2) throws VorbisFormatException, IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void computeFloor(float[] var1) {
        throw new UnsupportedOperationException();
    }
}
