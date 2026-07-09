// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package de.jarnbjo.vorbis;

import de.jarnbjo.util.io.BitInputStream;
import java.io.IOException;

class SetupHeader {
    private static final long HEADER = 126896460427126L;
    private CodeBook[] codeBooks;
    private Floor[] floors;
    private Residue[] residues;
    private Mapping[] mappings;
    private Mode[] modes;

    public SetupHeader(VorbisStream vorbisStream, BitInputStream bitInputStream) throws VorbisFormatException, IOException {
        if (bitInputStream.getLong(48) != 126896460427126L) {
            throw new VorbisFormatException("The setup header has an illegal leading.");
        } else {
            int int0 = bitInputStream.getInt(8) + 1;
            this.codeBooks = new CodeBook[int0];

            for (int int1 = 0; int1 < this.codeBooks.length; int1++) {
                this.codeBooks[int1] = new CodeBook(bitInputStream);
            }

            int int2 = bitInputStream.getInt(6) + 1;

            for (int int3 = 0; int3 < int2; int3++) {
                if (bitInputStream.getInt(16) != 0) {
                    throw new VorbisFormatException("Time domain transformation != 0");
                }
            }

            int int4 = bitInputStream.getInt(6) + 1;
            this.floors = new Floor[int4];

            for (int int5 = 0; int5 < int4; int5++) {
                this.floors[int5] = Floor.createInstance(bitInputStream, this);
            }

            int int6 = bitInputStream.getInt(6) + 1;
            this.residues = new Residue[int6];

            for (int int7 = 0; int7 < int6; int7++) {
                this.residues[int7] = Residue.createInstance(bitInputStream, this);
            }

            int int8 = bitInputStream.getInt(6) + 1;
            this.mappings = new Mapping[int8];

            for (int int9 = 0; int9 < int8; int9++) {
                this.mappings[int9] = Mapping.createInstance(vorbisStream, bitInputStream, this);
            }

            int int10 = bitInputStream.getInt(6) + 1;
            this.modes = new Mode[int10];

            for (int int11 = 0; int11 < int10; int11++) {
                this.modes[int11] = new Mode(bitInputStream, this);
            }

            if (!bitInputStream.getBit()) {
                throw new VorbisFormatException("The setup header framing bit is incorrect.");
            }
        }
    }

    public CodeBook[] getCodeBooks() {
        return this.codeBooks;
    }

    public Floor[] getFloors() {
        return this.floors;
    }

    public Residue[] getResidues() {
        return this.residues;
    }

    public Mapping[] getMappings() {
        return this.mappings;
    }

    public Mode[] getModes() {
        return this.modes;
    }
}
