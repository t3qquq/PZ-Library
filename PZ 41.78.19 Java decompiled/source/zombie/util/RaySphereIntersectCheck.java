// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import zombie.iso.Vector3;

public class RaySphereIntersectCheck {
    static Vector3 toSphere = new Vector3();
    static Vector3 dirNormal = new Vector3();

    public static boolean intersects(Vector3 vector1, Vector3 vector0, Vector3 vector2, float float0) {
        float0 *= float0;
        dirNormal.x = vector0.x;
        dirNormal.y = vector0.y;
        dirNormal.z = vector0.z;
        dirNormal.normalize();
        toSphere.x = vector2.x - vector1.x;
        toSphere.y = vector2.y - vector1.y;
        toSphere.z = vector2.z - vector1.z;
        float float1 = toSphere.getLength();
        float1 *= float1;
        if (float1 < float0) {
            return false;
        } else {
            float float2 = toSphere.dot3d(dirNormal);
            if (float2 < 0.0F) {
                return false;
            } else {
                float float3 = float0 + float2 * float2 - toSphere.getLength();
                return float3 >= 0.0;
            }
        }
    }
}
