// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.lwjglx.util.glu;

import org.lwjgl.opengl.GL11;

public class Quadric {
    protected int drawStyle = 100012;
    protected int orientation = 100020;
    protected boolean textureFlag = false;
    protected int normals = 100000;

    protected void normal3f(float float3, float float2, float float1) {
        float float0 = (float)Math.sqrt(float3 * float3 + float2 * float2 + float1 * float1);
        if (float0 > 1.0E-5F) {
            float3 /= float0;
            float2 /= float0;
            float1 /= float0;
        }

        GL11.glNormal3f(float3, float2, float1);
    }

    public void setDrawStyle(int int0) {
        this.drawStyle = int0;
    }

    public void setNormals(int int0) {
        this.normals = int0;
    }

    public void setOrientation(int int0) {
        this.orientation = int0;
    }

    public void setTextureFlag(boolean boolean0) {
        this.textureFlag = boolean0;
    }

    public int getDrawStyle() {
        return this.drawStyle;
    }

    public int getNormals() {
        return this.normals;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public boolean getTextureFlag() {
        return this.textureFlag;
    }

    protected void TXTR_COORD(float float0, float float1) {
        if (this.textureFlag) {
            GL11.glTexCoord2f(float0, float1);
        }
    }

    protected float sin(float float0) {
        return (float)Math.sin(float0);
    }

    protected float cos(float float0) {
        return (float)Math.cos(float0);
    }
}
