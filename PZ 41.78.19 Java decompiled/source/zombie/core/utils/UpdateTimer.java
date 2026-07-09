// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

public class UpdateTimer {
    private long time = 0L;

    public UpdateTimer() {
        this.time = System.currentTimeMillis() + 3800L;
    }

    public void reset(long _time) {
        this.time = System.currentTimeMillis() + _time;
    }

    public boolean check() {
        return this.time != 0L && System.currentTimeMillis() + 200L >= this.time;
    }

    public long getTime() {
        return this.time;
    }
}
