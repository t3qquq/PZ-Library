// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import zombie.GameTime;
import zombie.iso.sprite.IsoSprite;

public class IsoFloorBloodSplat {
    public static final float FADE_HOURS = 72.0F;
    public static HashMap<String, IsoSprite> SpriteMap = new HashMap<>();
    public static String[] FloorBloodTypes = new String[]{
        "blood_floor_small_01",
        "blood_floor_small_02",
        "blood_floor_small_03",
        "blood_floor_small_04",
        "blood_floor_small_05",
        "blood_floor_small_06",
        "blood_floor_small_07",
        "blood_floor_small_08",
        "blood_floor_med_01",
        "blood_floor_med_02",
        "blood_floor_med_03",
        "blood_floor_med_04",
        "blood_floor_med_05",
        "blood_floor_med_06",
        "blood_floor_med_07",
        "blood_floor_med_08",
        "blood_floor_large_01",
        "blood_floor_large_02",
        "blood_floor_large_03",
        "blood_floor_large_04",
        "blood_floor_large_05"
    };
    public float x;
    public float y;
    public float z;
    public int Type;
    public float worldAge;
    public int index;
    public int fade;
    IsoChunk chunk;

    public IsoFloorBloodSplat() {
    }

    public IsoFloorBloodSplat(float float0, float float1, float float2, int int0, float float3) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.Type = int0;
        this.worldAge = float3;
    }

    public void save(ByteBuffer byteBuffer) {
        int int0 = (int)(this.x / 10.0F * 255.0F);
        if (int0 < 0) {
            int0 = 0;
        }

        if (int0 > 255) {
            int0 = 255;
        }

        int int1 = (int)(this.y / 10.0F * 255.0F);
        if (int1 < 0) {
            int1 = 0;
        }

        if (int1 > 255) {
            int1 = 255;
        }

        int int2 = (int)(this.z / 8.0F * 255.0F);
        if (int2 < 0) {
            int2 = 0;
        }

        if (int2 > 255) {
            int2 = 255;
        }

        byteBuffer.put((byte)int0);
        byteBuffer.put((byte)int1);
        byteBuffer.put((byte)int2);
        byteBuffer.put((byte)this.Type);
        byteBuffer.putFloat(this.worldAge);
        byteBuffer.put((byte)this.index);
    }

    public void load(ByteBuffer byteBuffer, int int0) throws IOException {
        if (int0 >= 65) {
            this.x = (byteBuffer.get() & 255) / 255.0F * 10.0F;
            this.y = (byteBuffer.get() & 255) / 255.0F * 10.0F;
            this.z = (byteBuffer.get() & 255) / 255.0F * 8.0F;
            this.Type = byteBuffer.get();
            this.worldAge = byteBuffer.getFloat();
            if (int0 >= 73) {
                this.index = byteBuffer.get();
            }
        } else {
            this.x = byteBuffer.getFloat();
            this.y = byteBuffer.getFloat();
            this.z = byteBuffer.getFloat();
            this.Type = byteBuffer.getInt();
            this.worldAge = (float)GameTime.getInstance().getWorldAgeHours();
        }
    }
}
