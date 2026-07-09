// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

class SuggestedPaletteImpl implements SuggestedPalette {
    private final String name;
    private final int sampleDepth;
    private final byte[] bytes;
    private final int entrySize;
    private final int sampleCount;

    public SuggestedPaletteImpl(String string, int int0, byte[] bytesx) {
        this.name = string;
        this.sampleDepth = int0;
        this.bytes = bytesx;
        this.entrySize = int0 == 8 ? 6 : 10;
        this.sampleCount = bytesx.length / this.entrySize;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSampleCount() {
        return this.sampleCount;
    }

    @Override
    public int getSampleDepth() {
        return this.sampleDepth;
    }

    @Override
    public void getSample(int int1, short[] shorts) {
        int int0 = int1 * this.entrySize;
        if (this.sampleDepth == 8) {
            for (int int2 = 0; int2 < 4; int2++) {
                int int3 = 255 & this.bytes[int0++];
                shorts[int2] = (short)int3;
            }
        } else {
            for (int int4 = 0; int4 < 4; int4++) {
                int int5 = 255 & this.bytes[int0++];
                int int6 = 255 & this.bytes[int0++];
                shorts[int4] = (short)(int5 << 8 | int6);
            }
        }
    }

    @Override
    public int getFrequency(int int1) {
        int int0 = (int1 + 1) * this.entrySize - 2;
        int int2 = 255 & this.bytes[int0];
        int int3 = 255 & this.bytes[int0 + 1];
        return int2 << 8 | int3;
    }
}
