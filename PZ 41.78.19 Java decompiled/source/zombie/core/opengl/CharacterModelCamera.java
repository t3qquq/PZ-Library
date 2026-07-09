// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

import org.joml.Math;
import org.lwjgl.opengl.GL11;
import zombie.core.Core;
import zombie.core.skinnedmodel.ModelCamera;

public final class CharacterModelCamera extends ModelCamera {
    public static final CharacterModelCamera instance = new CharacterModelCamera();

    @Override
    public void Begin() {
        if (this.m_bUseWorldIso) {
            Core.getInstance().DoPushIsoStuff(this.m_x, this.m_y, this.m_z, this.m_useAngle, this.m_bInVehicle);
            GL11.glDepthMask(this.bDepthMask);
        } else {
            short short0 = 1024;
            short short1 = 1024;
            float float0 = 42.75F;
            float float1 = 0.0F;
            float float2 = -0.45F;
            float float3 = 0.0F;
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            float float4 = (float)short0 / short1;
            boolean boolean0 = false;
            if (boolean0) {
                GL11.glOrtho(-float0 * float4, float0 * float4, float0, -float0, -100.0, 100.0);
            } else {
                GL11.glOrtho(-float0 * float4, float0 * float4, -float0, float0, -100.0, 100.0);
            }

            float float5 = Math.sqrt(2048.0F);
            GL11.glScalef(-float5, float5, float5);
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(float1, float2, float3);
            GL11.glRotatef(30.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotated(Math.toDegrees(this.m_useAngle) + 45.0, 0.0, 1.0, 0.0);
            GL11.glDepthRange(0.0, 1.0);
            GL11.glDepthMask(this.bDepthMask);
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
