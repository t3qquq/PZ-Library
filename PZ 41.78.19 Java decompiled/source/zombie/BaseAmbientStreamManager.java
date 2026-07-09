// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import zombie.iso.RoomDef;

public abstract class BaseAmbientStreamManager {
    public abstract void stop();

    public abstract void doAlarm(RoomDef room);

    public abstract void doGunEvent();

    public abstract void init();

    public abstract void addBlend(String name, float vol, boolean bIndoors, boolean bRain, boolean bNight, boolean bDay);

    protected abstract void addRandomAmbient();

    public abstract void doOneShotAmbients();

    public abstract void update();

    public abstract void addAmbient(String name, int x, int y, int radius, float volume);

    public abstract void addAmbientEmitter(float x, float y, int z, String name);

    public abstract void addDaytimeAmbientEmitter(float x, float y, int z, String name);
}
