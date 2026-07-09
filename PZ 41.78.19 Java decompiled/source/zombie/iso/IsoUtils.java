// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import org.joml.Vector2f;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;

public final class IsoUtils {
    public static float clamp(float x, float minVal, float maxVal) {
        return Math.min(Math.max(x, minVal), maxVal);
    }

    public static float lerp(float val, float min, float max) {
        return max == min ? min : (clamp(val, min, max) - min) / (max - min);
    }

    public static float smoothstep(float edge0, float edge1, float x) {
        float float0 = clamp((x - edge0) / (edge1 - edge0), 0.0F, 1.0F);
        return float0 * float0 * (3.0F - 2.0F * float0);
    }

    public static float DistanceTo(float fromX, float fromY, float toX, float toY) {
        return (float)Math.sqrt(Math.pow(toX - fromX, 2.0) + Math.pow(toY - fromY, 2.0));
    }

    public static float DistanceTo2D(float fromX, float fromY, float toX, float toY) {
        return (float)Math.sqrt(Math.pow(toX - fromX, 2.0) + Math.pow(toY - fromY, 2.0));
    }

    public static float DistanceTo(float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        return (float)Math.sqrt(Math.pow(toX - fromX, 2.0) + Math.pow(toY - fromY, 2.0) + Math.pow(toZ - fromZ, 2.0));
    }

    public static float DistanceToSquared(float fromX, float fromY, float fromZ, float toX, float toY, float toZ) {
        return (float)(Math.pow(toX - fromX, 2.0) + Math.pow(toY - fromY, 2.0) + Math.pow(toZ - fromZ, 2.0));
    }

    public static float DistanceToSquared(float fromX, float fromY, float toX, float toY) {
        return (float)(Math.pow(toX - fromX, 2.0) + Math.pow(toY - fromY, 2.0));
    }

    public static float DistanceManhatten(float fromX, float fromY, float toX, float toY) {
        return Math.abs(toX - fromX) + Math.abs(toY - fromY);
    }

    public static float DistanceManhatten(float fromX, float fromY, float toX, float toY, float fromZ, float toZ) {
        return Math.abs(toX - fromX) + Math.abs(toY - fromY) + Math.abs(toZ - fromZ) * 2.0F;
    }

    public static float DistanceManhattenSquare(float fromX, float fromY, float toX, float toY) {
        return Math.max(Math.abs(toX - fromX), Math.abs(toY - fromY));
    }

    public static float XToIso(float screenX, float screenY, float floor) {
        float float0 = screenX + IsoCamera.getOffX();
        float float1 = screenY + IsoCamera.getOffY();
        float float2 = (float0 + 2.0F * float1) / (64.0F * Core.TileScale);
        float float3 = (float0 - 2.0F * float1) / (-64.0F * Core.TileScale);
        float2 += 3.0F * floor;
        float3 += 3.0F * floor;
        return float2;
    }

    public static float XToIsoTrue(float screenX, float screenY, int floor) {
        float float0 = screenX + (int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffX;
        float float1 = screenY + (int)IsoCamera.cameras[IsoPlayer.getPlayerIndex()].OffY;
        float float2 = (float0 + 2.0F * float1) / (64.0F * Core.TileScale);
        float float3 = (float0 - 2.0F * float1) / (-64.0F * Core.TileScale);
        float2 += 3 * floor;
        float3 += 3 * floor;
        return float2;
    }

    public static float XToScreen(float objectX, float objectY, float objectZ, int screenZ) {
        float float0 = 0.0F;
        float0 += objectX * (32 * Core.TileScale);
        return float0 - objectY * (32 * Core.TileScale);
    }

    public static float XToScreenInt(int objectX, int objectY, int objectZ, int screenZ) {
        return XToScreen(objectX, objectY, objectZ, screenZ);
    }

    public static float YToScreenExact(float objectX, float objectY, float objectZ, int screenZ) {
        float float0 = YToScreen(objectX, objectY, objectZ, screenZ);
        return float0 - IsoCamera.getOffY();
    }

    public static float XToScreenExact(float objectX, float objectY, float objectZ, int screenZ) {
        float float0 = XToScreen(objectX, objectY, objectZ, screenZ);
        return float0 - IsoCamera.getOffX();
    }

    public static float YToIso(float screenX, float screenY, float floor) {
        float float0 = screenX + IsoCamera.getOffX();
        float float1 = screenY + IsoCamera.getOffY();
        float float2 = (float0 + 2.0F * float1) / (64.0F * Core.TileScale);
        float float3 = (float0 - 2.0F * float1) / (-64.0F * Core.TileScale);
        float2 += 3.0F * floor;
        return float3 + 3.0F * floor;
    }

    public static float YToScreen(float objectX, float objectY, float objectZ, int screenZ) {
        float float0 = 0.0F;
        float0 += objectY * (16 * Core.TileScale);
        float0 += objectX * (16 * Core.TileScale);
        return float0 + (screenZ - objectZ) * (96 * Core.TileScale);
    }

    public static float YToScreenInt(int objectX, int objectY, int objectZ, int screenZ) {
        return YToScreen(objectX, objectY, objectZ, screenZ);
    }

    public static boolean isSimilarDirection(IsoGameCharacter chr, float xA, float yA, float xB, float yB, float similar) {
        Vector2f vector2f0 = new Vector2f(xA - chr.x, yA - chr.y);
        vector2f0.normalize();
        Vector2f vector2f1 = new Vector2f(chr.x - xB, chr.y - yB);
        vector2f1.normalize();
        vector2f0.add(vector2f1);
        return vector2f0.length() < similar;
    }
}
