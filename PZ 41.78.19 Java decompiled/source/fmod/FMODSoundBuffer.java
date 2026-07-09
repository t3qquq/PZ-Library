// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod;

public class FMODSoundBuffer {
    private long sound;
    private byte[] buf1;
    private Long buf1size;
    private Long vadStatus;
    private Long loudness;
    private boolean intError;

    public FMODSoundBuffer(long long0) {
        this.sound = long0;
        this.buf1 = new byte[2048];
        this.buf1size = new Long(0L);
        this.vadStatus = new Long(0L);
        this.loudness = new Long(0L);
        this.intError = false;
    }

    public boolean pull(long var1) {
        int int0 = javafmod.FMOD_Sound_GetData(this.sound, this.buf1, this.buf1size, this.vadStatus, this.loudness);
        this.intError = int0 == -1;
        return int0 == 0;
    }

    public byte[] buf() {
        return this.buf1;
    }

    public long get_size() {
        return this.buf1size;
    }

    public long get_vad() {
        return this.vadStatus;
    }

    public long get_loudness() {
        return this.loudness;
    }

    public boolean get_interror() {
        return this.intError;
    }
}
