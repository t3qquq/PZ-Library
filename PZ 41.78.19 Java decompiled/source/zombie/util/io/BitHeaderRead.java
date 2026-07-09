// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util.io;

public interface BitHeaderRead {
    int getStartPosition();

    void read();

    boolean hasFlags(int var1);

    boolean hasFlags(long var1);

    boolean equals(int var1);

    boolean equals(long var1);

    int getLen();

    void release();
}
