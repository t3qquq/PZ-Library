// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

public class ExecuteTimeAnalyse {
    String caption;
    ExecuteTimeAnalyse.TimeStamp[] list;
    int listIndex = 0;

    public ExecuteTimeAnalyse(String string, int int0) {
        this.caption = string;
        this.list = new ExecuteTimeAnalyse.TimeStamp[int0];

        for (int int1 = 0; int1 < int0; int1++) {
            this.list[int1] = new ExecuteTimeAnalyse.TimeStamp();
        }
    }

    public void reset() {
        this.listIndex = 0;
    }

    public void add(String string) {
        this.list[this.listIndex].time = System.nanoTime();
        this.list[this.listIndex].comment = string;
        this.listIndex++;
    }

    public long getNanoTime() {
        return this.listIndex == 0 ? 0L : System.nanoTime() - this.list[0].time;
    }

    public int getMsTime() {
        return this.listIndex == 0 ? 0 : (int)((System.nanoTime() - this.list[0].time) / 1000000L);
    }

    public void print() {
        long long0 = this.list[0].time;
        System.out.println("========== START === " + this.caption + " =============");

        for (int int0 = 1; int0 < this.listIndex; int0++) {
            System.out.println(int0 + " " + this.list[int0].comment + ": " + (this.list[int0].time - long0) / 1000000L);
            long0 = this.list[int0].time;
        }

        System.out.println("END: " + (System.nanoTime() - this.list[0].time) / 1000000L);
        System.out.println("==========  END  === " + this.caption + " =============");
    }

    static class TimeStamp {
        long time;
        String comment;

        public TimeStamp(String string) {
            this.comment = string;
            this.time = System.nanoTime();
        }

        public TimeStamp() {
        }
    }
}
