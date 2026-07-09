// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.collision;

import java.util.ArrayList;
import zombie.iso.Vector2;

public final class Polygon {
    public ArrayList<Vector2> points = new ArrayList<>(4);
    public ArrayList<Vector2> edges = new ArrayList<>(4);
    float x = 0.0F;
    float y = 0.0F;
    float x2 = 0.0F;
    float y2 = 0.0F;
    Vector2[] vecs = new Vector2[4];
    Vector2[] eds = new Vector2[4];
    static Vector2 temp = new Vector2();

    public void Set(float float0, float float1, float float2, float float3) {
        this.x = float0;
        this.y = float1;
        this.x2 = float2;
        this.y2 = float3;
        this.points.clear();
        if (this.vecs[0] == null) {
            for (int int0 = 0; int0 < 4; int0++) {
                this.vecs[int0] = new Vector2();
                this.eds[int0] = new Vector2();
            }
        }

        this.vecs[0].x = float0;
        this.vecs[0].y = float1;
        this.vecs[1].x = float2;
        this.vecs[1].y = float1;
        this.vecs[2].x = float2;
        this.vecs[2].y = float3;
        this.vecs[3].x = float0;
        this.vecs[3].y = float3;
        this.points.add(this.vecs[0]);
        this.points.add(this.vecs[1]);
        this.points.add(this.vecs[2]);
        this.points.add(this.vecs[3]);
        this.BuildEdges();
    }

    public Vector2 Center() {
        temp.x = this.x + (this.x2 - this.x) / 2.0F;
        temp.y = this.y + (this.y2 - this.y) / 2.0F;
        return temp;
    }

    public void BuildEdges() {
        this.edges.clear();

        for (int int0 = 0; int0 < this.points.size(); int0++) {
            Vector2 vector0 = this.points.get(int0);
            Vector2 vector1;
            if (int0 + 1 >= this.points.size()) {
                vector1 = this.points.get(0);
            } else {
                vector1 = this.points.get(int0 + 1);
            }

            this.eds[int0].x = vector1.x - vector0.x;
            this.eds[int0].y = vector1.y - vector0.y;
            this.edges.add(this.eds[int0]);
        }
    }

    public void Offset(float float0, float float1) {
        for (int int0 = 0; int0 < this.points.size(); int0++) {
            Vector2 vector = this.points.get(int0);
            vector.x += float0;
            vector.y += float1;
        }
    }

    public void Offset(Vector2 vector) {
        this.Offset(vector.x, vector.y);
    }
}
