// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

public abstract class BaseSoundListener {
    public int index;
    public float x;
    public float y;
    public float z;

    public BaseSoundListener(int arg0) {
        this.index = arg0;
    }

    public void setPos(float arg0, float arg1, float arg2) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
    }

    public abstract void tick();
}
