// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.states;

import zombie.ai.State;
import zombie.vehicles.PathFindState2;

public final class PathFindState extends State {
    private static final PathFindState2 _instance = new PathFindState2();

    public static PathFindState2 instance() {
        return _instance;
    }
}
