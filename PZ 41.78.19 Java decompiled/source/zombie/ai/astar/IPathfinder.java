// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.astar;

public interface IPathfinder {
    void Failed(Mover mover);

    void Succeeded(Path path, Mover mover);

    String getName();
}
