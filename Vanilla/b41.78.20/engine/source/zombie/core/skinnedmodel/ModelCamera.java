// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

import zombie.core.opengl.IModelCamera;

public abstract class ModelCamera implements IModelCamera {
    public static ModelCamera instance = null;
    public float m_useAngle;
    public boolean m_bUseWorldIso;
    public float m_x;
    public float m_y;
    public float m_z;
    public boolean m_bInVehicle;
    public boolean bDepthMask = true;
}
