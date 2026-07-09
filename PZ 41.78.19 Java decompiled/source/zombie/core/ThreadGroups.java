// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

public final class ThreadGroups {
    public static final ThreadGroup Root = new ThreadGroup("PZ");
    public static final ThreadGroup Main = new ThreadGroup(Root, "Main");
    public static final ThreadGroup Workers = new ThreadGroup(Root, "Workers");
    public static final ThreadGroup Network = new ThreadGroup(Root, "Network");
}
