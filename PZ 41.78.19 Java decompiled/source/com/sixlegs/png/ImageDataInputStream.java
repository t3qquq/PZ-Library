// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.sixlegs.png;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

class ImageDataInputStream extends InputStream {
    private final PngInputStream in;
    private final StateMachine machine;
    private final byte[] onebyte = new byte[1];
    private boolean done;

    public ImageDataInputStream(PngInputStream pngInputStream, StateMachine stateMachine) {
        this.in = pngInputStream;
        this.machine = stateMachine;
    }

    @Override
    public int read() throws IOException {
        return this.read(this.onebyte, 0, 1) == -1 ? -1 : 0xFF & this.onebyte[0];
    }

    @Override
    public int read(byte[] bytes, int int3, int int1) throws IOException {
        if (this.done) {
            return -1;
        } else {
            try {
                int int0 = 0;

                while (int0 != int1 && !this.done) {
                    while (int0 != int1 && this.in.getRemaining() > 0) {
                        int int2 = Math.min(int1 - int0, this.in.getRemaining());
                        this.in.readFully(bytes, int3 + int0, int2);
                        int0 += int2;
                    }

                    if (this.in.getRemaining() <= 0) {
                        this.in.endChunk(this.machine.getType());
                        this.machine.nextState(this.in.startChunk());
                        this.done = this.machine.getType() != 1229209940;
                    }
                }

                return int0;
            } catch (EOFException eOFException) {
                this.done = true;
                return -1;
            }
        }
    }
}
