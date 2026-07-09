// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;

class Residue1 extends Residue {
    protected Residue1(BitInputStream bitInputStream, SetupHeader setupHeader) throws VorbisFormatException, IOException {
        super(bitInputStream, setupHeader);
    }

    @Override
    protected int getType() {
        return 1;
    }

    @Override
    protected void decodeResidue(VorbisStream var1, BitInputStream var2, Mode var3, int var4, boolean[] var5, float[][] var6) throws VorbisFormatException, IOException {
        throw new UnsupportedOperationException();
    }
}
