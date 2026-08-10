// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.astar;

public interface IPathfinder {
    void Failed(Mover mover);

    void Succeeded(Path path, Mover mover);

    String getName();
}
