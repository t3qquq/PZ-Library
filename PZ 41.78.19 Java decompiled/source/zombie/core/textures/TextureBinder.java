// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class TextureBinder {
    public static TextureBinder instance = new TextureBinder();
    public int maxTextureUnits = 0;
    public int[] textureUnitIDs;
    public int textureUnitIDStart = 33984;
    public int textureIndex = 0;
    public int activeTextureIndex = 0;

    public TextureBinder() {
        this.maxTextureUnits = 1;
        this.textureUnitIDs = new int[this.maxTextureUnits];

        for (int int0 = 0; int0 < this.maxTextureUnits; int0++) {
            this.textureUnitIDs[int0] = -1;
        }
    }

    public void bind(int int1) {
        for (int int0 = 0; int0 < this.maxTextureUnits; int0++) {
            if (this.textureUnitIDs[int0] == int1) {
                int int2 = int0 + this.textureUnitIDStart;
                GL13.glActiveTexture(int2);
                this.activeTextureIndex = int2;
                return;
            }
        }

        this.textureUnitIDs[this.textureIndex] = int1;
        GL13.glActiveTexture(this.textureUnitIDStart + this.textureIndex);
        GL11.glBindTexture(3553, int1);
        this.activeTextureIndex = this.textureUnitIDStart + this.textureIndex;
        this.textureIndex++;
        if (this.textureIndex >= this.maxTextureUnits) {
            this.textureIndex = 0;
        }
    }
}
