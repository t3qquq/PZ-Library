// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

public interface SuggestedPalette {
    String getName();

    int getSampleCount();

    int getSampleDepth();

    void getSample(int var1, short[] var2);

    int getFrequency(int var1);
}
