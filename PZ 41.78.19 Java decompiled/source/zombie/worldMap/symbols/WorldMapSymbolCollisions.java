// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap.symbols;

import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TFloatArrayList;

public final class WorldMapSymbolCollisions {
    final TFloatArrayList m_boxes = new TFloatArrayList();
    final TByteArrayList m_collide = new TByteArrayList();

    boolean addBox(float float0, float float1, float float2, float float3, boolean boolean0) {
        int int0 = this.m_boxes.size() / 4 - 1;
        int int1 = int0 + 1;
        this.m_boxes.add(float0);
        this.m_boxes.add(float1);
        this.m_boxes.add(float0 + float2);
        this.m_boxes.add(float1 + float3);
        this.m_collide.add((byte)(boolean0 ? 1 : 0));
        if (!boolean0) {
            return false;
        } else {
            for (int int2 = 0; int2 <= int0; int2++) {
                if (this.isCollision(int2, int1)) {
                    float0 += float2 / 2.0F;
                    float1 += float3 / 2.0F;
                    this.m_boxes.set(int1 * 4, float0 - 3.0F - 1.0F);
                    this.m_boxes.set(int1 * 4 + 1, float1 - 3.0F - 1.0F);
                    this.m_boxes.set(int1 * 4 + 2, float0 + 3.0F + 1.0F);
                    this.m_boxes.set(int1 * 4 + 3, float1 - 3.0F + 1.0F);
                    return true;
                }
            }

            return false;
        }
    }

    boolean isCollision(int int1, int int0) {
        if (this.m_collide.getQuick(int1) != 0 && this.m_collide.getQuick(int0) != 0) {
            int1 *= 4;
            int0 *= 4;
            float float0 = this.m_boxes.get(int1);
            float float1 = this.m_boxes.get(int1 + 1);
            float float2 = this.m_boxes.get(int1 + 2);
            float float3 = this.m_boxes.get(int1 + 3);
            float float4 = this.m_boxes.get(int0);
            float float5 = this.m_boxes.get(int0 + 1);
            float float6 = this.m_boxes.get(int0 + 2);
            float float7 = this.m_boxes.get(int0 + 3);
            return float0 < float6 && float2 > float4 && float1 < float7 && float3 > float5;
        } else {
            return false;
        }
    }

    boolean isCollision(int int1) {
        for (int int0 = 0; int0 < this.m_boxes.size() / 4; int0++) {
            if (int0 != int1 && this.isCollision(int1, int0)) {
                return true;
            }
        }

        return false;
    }
}
