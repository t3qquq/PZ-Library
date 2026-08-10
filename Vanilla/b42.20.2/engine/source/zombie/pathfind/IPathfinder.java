// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.pathfind;

import zombie.ai.astar.Mover;

public interface IPathfinder {
    void Succeeded(Path arg0, Mover arg1);

    void Failed(Mover arg0);
}
