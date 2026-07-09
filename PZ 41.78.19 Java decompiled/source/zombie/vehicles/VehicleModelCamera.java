// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import org.joml.Math;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;

public final class VehicleModelCamera extends ModelCamera {
    public static final VehicleModelCamera instance = new VehicleModelCamera();

    @Override
    public void Begin() {
        if (this.m_bUseWorldIso) {
            Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, this.m_useAngle, true);
            GL11.glDepthMask(this.bDepthMask);
        } else {
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(-192.0, 192.0, -192.0, 192.0, -1000.0, 1000.0);
            float float0 = Math.sqrt(2048.0F);
            GL11.glScalef(-float0, float0, float0);
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        }
    }

    @Override
    public void End() {
        if (this.m_bUseWorldIso) {
            Core.getInstance().DoPopIsoStuff();
        } else {
            GL11.glDepthFunc(519);
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
        }
    }
}
