// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod;

import zombie.debug.DebugLog;

public class SoundBuffer {
    public int Buf_Size;
    public int Buf_Read;
    public int Buf_Write;
    private short[] intdata;
    private int delay;

    public SoundBuffer(int int0) {
        this.Buf_Size = int0;
        this.Buf_Read = 0;
        this.Buf_Write = 0;
        this.delay = 1;
        this.intdata = new short[int0];
    }

    public void get(long long0, short[] shorts) {
        int int0 = this.Buf_Write - this.Buf_Read;
        if (int0 < 0) {
            int0 += this.Buf_Size;
        }

        if (int0 < long0) {
            for (int int1 = 0; int1 < long0 - 1L; int1++) {
                shorts[int1] = 0;
            }
        } else if (int0 > long0 * this.delay * 2L) {
            if (this.delay * long0 * 3L < this.Buf_Size) {
                this.delay++;
            }

            DebugLog.log("[SoundBuffer] correct: delay: " + this.delay);
            this.Buf_Read = (int)(this.Buf_Write - long0 * this.delay);
            if (this.Buf_Read < 0) {
                this.Buf_Read = this.Buf_Read + this.Buf_Size;
            }

            int int2 = 0;

            for (int int3 = this.Buf_Read; int2 < long0 * 2L; int3 = (int3 + 1) % this.Buf_Size) {
                this.intdata[int3] = 0;
                int2++;
            }
        } else {
            int int4 = 0;

            int int5;
            for (int5 = this.Buf_Read; int4 < long0 - 1L && int5 != this.Buf_Write; int5 = (int5 + 1) % this.Buf_Size) {
                shorts[int4] = this.intdata[int5];
                int4++;
            }

            this.Buf_Read = int5;
        }
    }

    public void push(long long0, short[] shorts) {
        boolean boolean0 = false;
        int int0 = 0;

        int int1;
        for (int1 = this.Buf_Write; int0 < long0 - 1L; int1 = (int1 + 1) % this.Buf_Size) {
            this.intdata[int1] = shorts[int0];
            if (shorts[int0] != 0) {
                boolean0 = true;
            }

            int0++;
        }

        if (boolean0) {
            this.Buf_Write = int1;
        }
    }

    public void push(long long0, byte[] bytes) {
        boolean boolean0 = false;
        byte byte0 = 0;

        int int0;
        for (int0 = this.Buf_Write; byte0 < long0 - 1L; int0 = (int0 + 1) % this.Buf_Size) {
            this.intdata[int0] = (short)(bytes[byte0 + 1] * 256 + bytes[byte0]);
            if (bytes[byte0] != 0) {
                boolean0 = true;
            }

            byte0 += 2;
        }

        if (boolean0) {
            this.Buf_Write = int0;
        }
    }

    public short[] buf() {
        return this.intdata;
    }
}
