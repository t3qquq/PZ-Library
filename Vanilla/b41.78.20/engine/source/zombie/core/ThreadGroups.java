// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

public final class ThreadGroups {
    public static final ThreadGroup Root = new ThreadGroup("PZ");
    public static final ThreadGroup Main = new ThreadGroup(Root, "Main");
    public static final ThreadGroup Workers = new ThreadGroup(Root, "Workers");
    public static final ThreadGroup Network = new ThreadGroup(Root, "Network");
}
