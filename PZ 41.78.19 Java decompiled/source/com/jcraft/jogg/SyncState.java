// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package com.jcraft.jogg;

public class SyncState {
    public byte[] data;
    int bodybytes;
    int fill;
    int headerbytes;
    int returned;
    int storage;
    int unsynced;
    private byte[] chksum = new byte[4];
    private Page pageseek = new Page();

    public int buffer(int int0) {
        if (this.returned != 0) {
            this.fill = this.fill - this.returned;
            if (this.fill > 0) {
                System.arraycopy(this.data, this.returned, this.data, 0, this.fill);
            }

            this.returned = 0;
        }

        if (int0 > this.storage - this.fill) {
            int int1 = int0 + this.fill + 4096;
            if (this.data != null) {
                byte[] bytes = new byte[int1];
                System.arraycopy(this.data, 0, bytes, 0, this.data.length);
                this.data = bytes;
            } else {
                this.data = new byte[int1];
            }

            this.storage = int1;
        }

        return this.fill;
    }

    public int clear() {
        this.data = null;
        return 0;
    }

    public int getBufferOffset() {
        return this.fill;
    }

    public int getDataOffset() {
        return this.returned;
    }

    public void init() {
    }

    public int pageout(Page page) {
        do {
            int int0 = this.pageseek(page);
            if (int0 > 0) {
                return 1;
            }

            if (int0 == 0) {
                return 0;
            }
        } while (this.unsynced != 0);

        this.unsynced = 1;
        return -1;
    }

    public int pageseek(Page page1) {
        int int0 = this.returned;
        int int1 = this.fill - this.returned;
        if (this.headerbytes == 0) {
            if (int1 < 27) {
                return 0;
            }

            if (this.data[int0] != 79 || this.data[int0 + 1] != 103 || this.data[int0 + 2] != 103 || this.data[int0 + 3] != 83) {
                this.headerbytes = 0;
                this.bodybytes = 0;
                int int2 = 0;

                for (int int3 = 0; int3 < int1 - 1; int3++) {
                    if (this.data[int0 + 1 + int3] == 79) {
                        int2 = int0 + 1 + int3;
                        break;
                    }
                }

                if (int2 == 0) {
                    int2 = this.fill;
                }

                this.returned = int2;
                return -(int2 - int0);
            }

            int int4 = (this.data[int0 + 26] & 255) + 27;
            if (int1 < int4) {
                return 0;
            }

            for (int int5 = 0; int5 < (this.data[int0 + 26] & 255); int5++) {
                this.bodybytes = this.bodybytes + (this.data[int0 + 27 + int5] & 255);
            }

            this.headerbytes = int4;
        }

        if (this.bodybytes + this.headerbytes > int1) {
            return 0;
        } else {
            synchronized (this.chksum) {
                System.arraycopy(this.data, int0 + 22, this.chksum, 0, 4);
                this.data[int0 + 22] = 0;
                this.data[int0 + 23] = 0;
                this.data[int0 + 24] = 0;
                this.data[int0 + 25] = 0;
                Page page0 = this.pageseek;
                page0.header_base = this.data;
                page0.header = int0;
                page0.header_len = this.headerbytes;
                page0.body_base = this.data;
                page0.body = int0 + this.headerbytes;
                page0.body_len = this.bodybytes;
                page0.checksum();
                if (this.chksum[0] != this.data[int0 + 22]
                    || this.chksum[1] != this.data[int0 + 23]
                    || this.chksum[2] != this.data[int0 + 24]
                    || this.chksum[3] != this.data[int0 + 25]) {
                    System.arraycopy(this.chksum, 0, this.data, int0 + 22, 4);
                    this.headerbytes = 0;
                    this.bodybytes = 0;
                    int int6 = 0;

                    for (int int7 = 0; int7 < int1 - 1; int7++) {
                        if (this.data[int0 + 1 + int7] == 79) {
                            int6 = int0 + 1 + int7;
                            break;
                        }
                    }

                    if (int6 == 0) {
                        int6 = this.fill;
                    }

                    this.returned = int6;
                    return -(int6 - int0);
                }
            }

            int0 = this.returned;
            if (page1 != null) {
                page1.header_base = this.data;
                page1.header = int0;
                page1.header_len = this.headerbytes;
                page1.body_base = this.data;
                page1.body = int0 + this.headerbytes;
                page1.body_len = this.bodybytes;
            }

            this.unsynced = 0;
            this.returned = this.returned + (int1 = this.headerbytes + this.bodybytes);
            this.headerbytes = 0;
            this.bodybytes = 0;
            return int1;
        }
    }

    public int reset() {
        this.fill = 0;
        this.returned = 0;
        this.unsynced = 0;
        this.headerbytes = 0;
        this.bodybytes = 0;
        return 0;
    }

    public int wrote(int int0) {
        if (this.fill + int0 > this.storage) {
            return -1;
        } else {
            this.fill += int0;
            return 0;
        }
    }
}
