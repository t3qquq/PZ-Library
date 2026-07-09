// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.util.io;

import java.io.IOException;

public interface BitInputStream {
    int LITTLE_ENDIAN = 0;
    int BIG_ENDIAN = 1;

    boolean getBit() throws IOException;

    int getInt(int var1) throws IOException;

    int getSignedInt(int var1) throws IOException;

    int getInt(HuffmanNode var1) throws IOException;

    int readSignedRice(int var1) throws IOException;

    void readSignedRice(int var1, int[] var2, int var3, int var4) throws IOException;

    long getLong(int var1) throws IOException;

    void align();

    void setEndian(int var1);
}
