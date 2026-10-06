// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.pathfind;

import java.util.ArrayDeque;

final class PointPool {
    final ArrayDeque<Point> pool = new ArrayDeque<>();

    Point alloc() {
        return this.pool.isEmpty() ? new Point() : this.pool.pop();
    }

    void release(Point pt) {
        this.pool.push(pt);
    }
}
