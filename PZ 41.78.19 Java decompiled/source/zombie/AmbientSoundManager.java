// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoWorld;
import zombie.iso.RoomDef;
import zombie.iso.Vector2;
import zombie.network.GameServer;

public final class AmbientSoundManager extends BaseAmbientStreamManager {
    public final ArrayList<AmbientSoundManager.Ambient> ambient = new ArrayList<>();
    private final Vector2 tempo = new Vector2();
    private int electricityShutOffState = -1;
    private long electricityShutOffTime = 0L;
    public boolean initialized = false;

    @Override
    public void update() {
        if (this.initialized) {
            this.updatePowerSupply();
            this.doOneShotAmbients();
        }
    }

    @Override
    public void addAmbient(String var1, int var2, int var3, int var4, float var5) {
    }

    @Override
    public void addAmbientEmitter(float var1, float var2, int var3, String var4) {
    }

    @Override
    public void addDaytimeAmbientEmitter(float var1, float var2, int var3, String var4) {
    }

    @Override
    public void doOneShotAmbients() {
        for (int int0 = 0; int0 < this.ambient.size(); int0++) {
            AmbientSoundManager.Ambient ambientx = this.ambient.get(int0);
            if (ambientx.finished()) {
                DebugLog.log(DebugType.Sound, "ambient: removing ambient sound " + ambientx.name);
                this.ambient.remove(int0--);
            } else {
                ambientx.update();
            }
        }
    }

    @Override
    public void init() {
        if (!this.initialized) {
            this.initialized = true;
        }
    }

    @Override
    public void addBlend(String var1, float var2, boolean var3, boolean var4, boolean var5, boolean var6) {
    }

    @Override
    protected void addRandomAmbient() {
        if (!GameServer.Players.isEmpty()) {
            IsoPlayer player = GameServer.Players.get(Rand.Next(GameServer.Players.size()));
            if (player != null) {
                String string = null;
                if (GameTime.instance.getHour() > 7 && GameTime.instance.getHour() < 21) {
                    switch (Rand.Next(3)) {
                        case 0:
                            if (Rand.Next(10) < 2) {
                                string = "MetaDogBark";
                            }
                            break;
                        case 1:
                            if (Rand.Next(10) < 3) {
                                string = "MetaScream";
                            }
                    }
                } else {
                    switch (Rand.Next(5)) {
                        case 0:
                            if (Rand.Next(10) < 2) {
                                string = "MetaDogBark";
                            }
                            break;
                        case 1:
                            if (Rand.Next(13) < 3) {
                                string = "MetaScream";
                            }
                            break;
                        case 2:
                            string = "MetaOwl";
                            break;
                        case 3:
                            string = "MetaWolfHowl";
                    }
                }

                if (string != null) {
                    float float0 = player.x;
                    float float1 = player.y;
                    double double0 = Rand.Next((float) -Math.PI, (float) Math.PI);
                    this.tempo.x = (float)Math.cos(double0);
                    this.tempo.y = (float)Math.sin(double0);
                    this.tempo.setLength(1000.0F);
                    float0 += this.tempo.x;
                    float1 += this.tempo.y;
                    AmbientSoundManager.Ambient ambientx = new AmbientSoundManager.Ambient(string, float0, float1, 50.0F, Rand.Next(0.2F, 0.5F));
                    this.ambient.add(ambientx);
                    GameServer.sendAmbient(string, (int)float0, (int)float1, 50, Rand.Next(0.2F, 0.5F));
                }
            }
        }
    }

    @Override
    public void doGunEvent() {
        ArrayList arrayList = GameServer.getPlayers();
        if (!arrayList.isEmpty()) {
            IsoPlayer player = (IsoPlayer)arrayList.get(Rand.Next(arrayList.size()));
            String string = null;
            float float0 = player.x;
            float float1 = player.y;
            short short0 = 600;
            double double0 = Rand.Next((float) -Math.PI, (float) Math.PI);
            this.tempo.x = (float)Math.cos(double0);
            this.tempo.y = (float)Math.sin(double0);
            this.tempo.setLength(short0 - 100);
            float0 += this.tempo.x;
            float1 += this.tempo.y;
            WorldSoundManager.instance.addSound(null, (int)float0 + Rand.Next(-10, 10), (int)float1 + Rand.Next(-10, 10), 0, 600, 600);
            switch (Rand.Next(6)) {
                case 0:
                    string = "MetaAssaultRifle1";
                    break;
                case 1:
                    string = "MetaPistol1";
                    break;
                case 2:
                    string = "MetaShotgun1";
                    break;
                case 3:
                    string = "MetaPistol2";
                    break;
                case 4:
                    string = "MetaPistol3";
                    break;
                case 5:
                    string = "MetaShotgun1";
            }

            float float2 = 1.0F;
            AmbientSoundManager.Ambient ambientx = new AmbientSoundManager.Ambient(string, float0, float1, 700.0F, float2);
            this.ambient.add(ambientx);
            GameServer.sendAmbient(string, (int)float0, (int)float1, (int)Math.ceil(ambientx.radius), ambientx.volume);
        }
    }

    @Override
    public void doAlarm(RoomDef roomDef) {
        if (roomDef != null && roomDef.building != null && roomDef.building.bAlarmed) {
            float float0 = 1.0F;
            AmbientSoundManager.Ambient ambientx = new AmbientSoundManager.Ambient(
                "burglar2", roomDef.x + roomDef.getW() / 2, roomDef.y + roomDef.getH() / 2, 700.0F, float0
            );
            ambientx.duration = 49;
            ambientx.worldSoundDelay = 3;
            roomDef.building.bAlarmed = false;
            roomDef.building.setAllExplored(true);
            this.ambient.add(ambientx);
            GameServer.sendAlarm(roomDef.x + roomDef.getW() / 2, roomDef.y + roomDef.getH() / 2);
        }
    }

    @Override
    public void stop() {
        this.ambient.clear();
        this.initialized = false;
    }

    private void updatePowerSupply() {
        boolean boolean0 = GameTime.getInstance().NightsSurvived < SandboxOptions.getInstance().getElecShutModifier();
        if (this.electricityShutOffState == -1) {
            IsoWorld.instance.setHydroPowerOn(boolean0);
        }

        if (this.electricityShutOffState == 0) {
            if (boolean0) {
                IsoWorld.instance.setHydroPowerOn(true);
                this.checkHaveElectricity();
                this.electricityShutOffTime = 0L;
            } else if (this.electricityShutOffTime != 0L && System.currentTimeMillis() >= this.electricityShutOffTime) {
                this.electricityShutOffTime = 0L;
                IsoWorld.instance.setHydroPowerOn(false);
                this.checkHaveElectricity();
            }
        }

        if (this.electricityShutOffState == 1 && !boolean0) {
            this.electricityShutOffTime = System.currentTimeMillis() + 2650L;
        }

        this.electricityShutOffState = boolean0 ? 1 : 0;
    }

    private void checkHaveElectricity() {
    }

    public class Ambient {
        public float x;
        public float y;
        public String name;
        public float radius;
        public float volume;
        long startTime;
        public int duration;
        public int worldSoundDelay = 0;

        public Ambient(String string, float float0, float float1, float float2, float float3) {
            this.name = string;
            this.x = float0;
            this.y = float1;
            this.radius = float2;
            this.volume = float3;
            this.startTime = System.currentTimeMillis() / 1000L;
            this.duration = 2;
            this.update();
            LuaEventManager.triggerEvent("OnAmbientSound", string, float0, float1);
        }

        public boolean finished() {
            long long0 = System.currentTimeMillis() / 1000L;
            return long0 - this.startTime >= this.duration;
        }

        public void update() {
            long long0 = System.currentTimeMillis() / 1000L;
            if (long0 - this.startTime >= this.worldSoundDelay) {
                WorldSoundManager.instance.addSound(null, (int)this.x, (int)this.y, 0, 600, 600);
            }
        }
    }
}
