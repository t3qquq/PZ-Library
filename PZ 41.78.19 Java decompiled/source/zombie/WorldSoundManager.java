// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import java.util.Stack;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerGUI;
import zombie.popman.MPDebugInfo;
import zombie.popman.ZombiePopulationManager;

public final class WorldSoundManager {
    public static final WorldSoundManager instance = new WorldSoundManager();
    public final ArrayList<WorldSoundManager.WorldSound> SoundList = new ArrayList<>();
    private final Stack<WorldSoundManager.WorldSound> freeSounds = new Stack<>();
    private static final WorldSoundManager.ResultBiggestSound resultBiggestSound = new WorldSoundManager.ResultBiggestSound();

    public void init(IsoCell cell) {
    }

    public void initFrame() {
    }

    public void KillCell() {
        this.freeSounds.addAll(this.SoundList);
        this.SoundList.clear();
    }

    public WorldSoundManager.WorldSound getNew() {
        return this.freeSounds.isEmpty() ? new WorldSoundManager.WorldSound() : this.freeSounds.pop();
    }

    public WorldSoundManager.WorldSound addSound(Object source, int x, int y, int z, int radius, int volume) {
        return this.addSound(source, x, y, z, radius, volume, false, 0.0F, 1.0F);
    }

    public WorldSoundManager.WorldSound addSound(Object source, int x, int y, int z, int radius, int volume, boolean stressHumans) {
        return this.addSound(source, x, y, z, radius, volume, stressHumans, 0.0F, 1.0F);
    }

    public WorldSoundManager.WorldSound addSound(
        Object source, int x, int y, int z, int radius, int volume, boolean stressHumans, float zombieIgnoreDist, float stressMod
    ) {
        return this.addSound(source, x, y, z, radius, volume, stressHumans, zombieIgnoreDist, stressMod, false, true, false);
    }

    public WorldSoundManager.WorldSound addSound(
        Object source,
        int x,
        int y,
        int z,
        int radius,
        int volume,
        boolean stressHumans,
        float zombieIgnoreDist,
        float stressMod,
        boolean sourceIsZombie,
        boolean doSend,
        boolean remote
    ) {
        if (radius <= 0) {
            return null;
        } else {
            WorldSoundManager.WorldSound worldSound;
            synchronized (this.SoundList) {
                worldSound = this.getNew().init(source, x, y, z, radius, volume, stressHumans, zombieIgnoreDist, stressMod);
                if (source == null) {
                    worldSound.sourceIsZombie = sourceIsZombie;
                }

                if (!GameServer.bServer) {
                    int int0 = SandboxOptions.instance.Lore.Hearing.getValue();
                    if (int0 == 4) {
                        int0 = 1;
                    }

                    int int1 = (int)PZMath.ceil(radius * this.getHearingMultiplier(int0));
                    int int2 = (x - int1) / 10;
                    int int3 = (y - int1) / 10;
                    int int4 = (int)Math.ceil(((float)x + int1) / 10.0F);
                    int int5 = (int)Math.ceil(((float)y + int1) / 10.0F);

                    for (int int6 = int2; int6 < int4; int6++) {
                        for (int int7 = int3; int7 < int5; int7++) {
                            IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(int6, int7);
                            if (chunk != null) {
                                chunk.SoundList.add(worldSound);
                            }
                        }
                    }
                }

                this.SoundList.add(worldSound);
                ZombiePopulationManager.instance.addWorldSound(worldSound, doSend);
            }

            if (doSend) {
                if (GameClient.bClient) {
                    GameClient.instance.sendWorldSound(worldSound);
                } else if (GameServer.bServer) {
                    GameServer.sendWorldSound(worldSound, null);
                }
            }

            if (Core.bDebug && GameClient.bClient) {
                MPDebugInfo.AddDebugSound(worldSound);
            }

            return worldSound;
        }
    }

    public WorldSoundManager.WorldSound addSoundRepeating(Object source, int x, int y, int z, int radius, int volume, boolean StressHumans) {
        WorldSoundManager.WorldSound worldSound = this.addSound(source, x, y, z, radius, volume, StressHumans, 0.0F, 1.0F);
        if (worldSound != null) {
            worldSound.bRepeating = true;
        }

        return worldSound;
    }

    public WorldSoundManager.WorldSound getSoundZomb(IsoZombie zom) {
        IsoChunk chunk = null;
        if (zom.soundSourceTarget == null) {
            return null;
        } else if (zom.getCurrentSquare() == null) {
            return null;
        } else {
            chunk = zom.getCurrentSquare().chunk;
            ArrayList arrayList = null;
            if (chunk != null && !GameServer.bServer) {
                arrayList = chunk.SoundList;
            } else {
                arrayList = this.SoundList;
            }

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                WorldSoundManager.WorldSound worldSound = (WorldSoundManager.WorldSound)arrayList.get(int0);
                if (zom.soundSourceTarget == worldSound.source) {
                    return worldSound;
                }
            }

            return null;
        }
    }

    public WorldSoundManager.ResultBiggestSound getBiggestSoundZomb(int x, int y, int z, boolean ignoreBySameType, IsoZombie zom) {
        float float0 = -1000000.0F;
        WorldSoundManager.WorldSound worldSound0 = null;
        IsoChunk chunk = null;
        if (zom != null) {
            if (zom.getCurrentSquare() == null) {
                return resultBiggestSound.init(null, 0.0F);
            }

            chunk = zom.getCurrentSquare().chunk;
        }

        ArrayList arrayList = null;
        if (chunk != null && !GameServer.bServer) {
            arrayList = chunk.SoundList;
        } else {
            arrayList = this.SoundList;
        }

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            WorldSoundManager.WorldSound worldSound1 = (WorldSoundManager.WorldSound)arrayList.get(int0);
            if (worldSound1 != null && worldSound1.radius != 0) {
                float float1 = IsoUtils.DistanceToSquared(x, y, worldSound1.x, worldSound1.y);
                float float2 = worldSound1.radius * this.getHearingMultiplier(zom);
                if (!(float1 > float2 * float2)
                    && (!(float1 < worldSound1.zombieIgnoreDist * worldSound1.zombieIgnoreDist) || z != worldSound1.z)
                    && (!ignoreBySameType || !worldSound1.sourceIsZombie)) {
                    IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(worldSound1.x, worldSound1.y, worldSound1.z);
                    IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(x, y, z);
                    float float3 = float1 / (float2 * float2);
                    if (square0 != null && square1 != null && square0.getRoom() != square1.getRoom()) {
                        float3 *= 1.2F;
                        if (square1.getRoom() == null || square0.getRoom() == null) {
                            float3 *= 1.4F;
                        }
                    }

                    float3 = 1.0F - float3;
                    if (!(float3 <= 0.0F)) {
                        if (float3 > 1.0F) {
                            float3 = 1.0F;
                        }

                        float float4 = worldSound1.volume * float3;
                        if (float4 > float0) {
                            float0 = float4;
                            worldSound0 = worldSound1;
                        }
                    }
                }
            }
        }

        return resultBiggestSound.init(worldSound0, float0);
    }

    public float getSoundAttract(WorldSoundManager.WorldSound sound, IsoZombie zom) {
        if (sound == null) {
            return 0.0F;
        } else if (sound.radius == 0) {
            return 0.0F;
        } else {
            float float0 = IsoUtils.DistanceToSquared(zom.x, zom.y, sound.x, sound.y);
            float float1 = sound.radius * this.getHearingMultiplier(zom);
            if (float0 > float1 * float1) {
                return 0.0F;
            } else if (float0 < sound.zombieIgnoreDist * sound.zombieIgnoreDist && zom.z == sound.z) {
                return 0.0F;
            } else if (sound.sourceIsZombie) {
                return 0.0F;
            } else {
                IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(sound.x, sound.y, sound.z);
                IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare((double)zom.x, (double)zom.y, (double)zom.z);
                float float2 = float0 / (float1 * float1);
                if (square0 != null && square1 != null && square0.getRoom() != square1.getRoom()) {
                    float2 *= 1.2F;
                    if (square1.getRoom() == null || square0.getRoom() == null) {
                        float2 *= 1.4F;
                    }
                }

                float2 = 1.0F - float2;
                if (float2 <= 0.0F) {
                    return 0.0F;
                } else {
                    if (float2 > 1.0F) {
                        float2 = 1.0F;
                    }

                    return sound.volume * float2;
                }
            }
        }
    }

    public float getStressFromSounds(int x, int y, int z) {
        float float0 = 0.0F;

        for (int int0 = 0; int0 < this.SoundList.size(); int0++) {
            WorldSoundManager.WorldSound worldSound = this.SoundList.get(int0);
            if (worldSound.stresshumans && worldSound.radius != 0) {
                float float1 = IsoUtils.DistanceManhatten(x, y, worldSound.x, worldSound.y);
                float float2 = float1 / worldSound.radius;
                float2 = 1.0F - float2;
                if (!(float2 <= 0.0F)) {
                    if (float2 > 1.0F) {
                        float2 = 1.0F;
                    }

                    float float3 = float2 * worldSound.stressMod;
                    float0 += float3;
                }
            }
        }

        return float0;
    }

    public void update() {
        if (!GameServer.bServer) {
            for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int0];
                if (!chunkMap.ignore) {
                    for (int int1 = 0; int1 < IsoChunkMap.ChunkGridWidth; int1++) {
                        for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; int2++) {
                            IsoChunk chunk = chunkMap.getChunk(int2, int1);
                            if (chunk != null) {
                                chunk.updateSounds();
                            }
                        }
                    }
                }
            }
        }

        int int3 = this.SoundList.size();

        for (int int4 = 0; int4 < int3; int4++) {
            WorldSoundManager.WorldSound worldSound = this.SoundList.get(int4);
            if (worldSound != null && worldSound.life > 0) {
                worldSound.life--;
            } else {
                this.SoundList.remove(int4);
                this.freeSounds.push(worldSound);
                int4--;
                int3--;
            }
        }
    }

    public void render() {
        if (Core.bDebug && DebugOptions.instance.WorldSoundRender.getValue()) {
            if (!GameClient.bClient) {
                if (!GameServer.bServer || ServerGUI.isCreated()) {
                    int int0 = SandboxOptions.instance.Lore.Hearing.getValue();
                    if (int0 == 4) {
                        int0 = 2;
                    }

                    float float0 = this.getHearingMultiplier(int0);

                    for (int int1 = 0; int1 < this.SoundList.size(); int1++) {
                        WorldSoundManager.WorldSound worldSound0 = this.SoundList.get(int1);
                        float float1 = worldSound0.radius * float0;

                        for (double double0 = 0.0; double0 < Math.PI * 2; double0 += Math.PI / 20) {
                            this.DrawIsoLine(
                                worldSound0.x + float1 * (float)Math.cos(double0),
                                worldSound0.y + float1 * (float)Math.sin(double0),
                                worldSound0.x + float1 * (float)Math.cos(double0 + (Math.PI / 20)),
                                worldSound0.y + float1 * (float)Math.sin(double0 + (Math.PI / 20)),
                                worldSound0.z,
                                1.0F,
                                1.0F,
                                1.0F,
                                1.0F,
                                1
                            );
                        }
                    }

                    if (!GameServer.bServer) {
                        IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.getChunkMap(0);
                        if (chunkMap != null && !chunkMap.ignore) {
                            for (int int2 = 0; int2 < IsoChunkMap.ChunkGridWidth; int2++) {
                                for (int int3 = 0; int3 < IsoChunkMap.ChunkGridWidth; int3++) {
                                    IsoChunk chunk = chunkMap.getChunk(int3, int2);
                                    if (chunk != null) {
                                        for (int int4 = 0; int4 < chunk.SoundList.size(); int4++) {
                                            WorldSoundManager.WorldSound worldSound1 = chunk.SoundList.get(int4);
                                            float float2 = worldSound1.radius * float0;

                                            for (double double1 = 0.0; double1 < Math.PI * 2; double1 += Math.PI / 20) {
                                                this.DrawIsoLine(
                                                    worldSound1.x + float2 * (float)Math.cos(double1),
                                                    worldSound1.y + float2 * (float)Math.sin(double1),
                                                    worldSound1.x + float2 * (float)Math.cos(double1 + (Math.PI / 20)),
                                                    worldSound1.y + float2 * (float)Math.sin(double1 + (Math.PI / 20)),
                                                    worldSound1.z,
                                                    0.0F,
                                                    1.0F,
                                                    1.0F,
                                                    1.0F,
                                                    1
                                                );
                                                float float3 = chunk.wx * 10 + 0.1F;
                                                float float4 = chunk.wy * 10 + 0.1F;
                                                float float5 = (chunk.wx + 1) * 10 - 0.1F;
                                                float float6 = (chunk.wy + 1) * 10 - 0.1F;
                                                this.DrawIsoLine(float3, float4, float5, float4, worldSound1.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                                this.DrawIsoLine(float5, float4, float5, float6, worldSound1.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                                this.DrawIsoLine(float5, float6, float3, float6, worldSound1.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                                this.DrawIsoLine(float3, float6, float3, float4, worldSound1.z, 0.0F, 1.0F, 1.0F, 1.0F, 1);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void DrawIsoLine(
        float float1, float float2, float float6, float float7, float float3, float float9, float float10, float float11, float float12, int int0
    ) {
        float float0 = IsoUtils.XToScreenExact(float1, float2, float3, 0);
        float float4 = IsoUtils.YToScreenExact(float1, float2, float3, 0);
        float float5 = IsoUtils.XToScreenExact(float6, float7, float3, 0);
        float float8 = IsoUtils.YToScreenExact(float6, float7, float3, 0);
        LineDrawer.drawLine(float0, float4, float5, float8, float9, float10, float11, float12, int0);
    }

    public float getHearingMultiplier(IsoZombie zombie0) {
        return zombie0 == null ? this.getHearingMultiplier(2) : this.getHearingMultiplier(zombie0.hearing);
    }

    public float getHearingMultiplier(int int0) {
        if (int0 == 1) {
            return 3.0F;
        } else {
            return int0 == 3 ? 0.45F : 1.0F;
        }
    }

    public static final class ResultBiggestSound {
        public WorldSoundManager.WorldSound sound;
        public float attract;

        public WorldSoundManager.ResultBiggestSound init(WorldSoundManager.WorldSound _sound, float _attract) {
            this.sound = _sound;
            this.attract = _attract;
            return this;
        }
    }

    public class WorldSound {
        public Object source = null;
        public int life = 1;
        public int radius;
        public boolean stresshumans;
        public int volume;
        public int x;
        public int y;
        public int z;
        public float zombieIgnoreDist = 0.0F;
        public boolean sourceIsZombie;
        public float stressMod = 1.0F;
        public boolean bRepeating;

        public WorldSoundManager.WorldSound init(Object _source, int _x, int _y, int _z, int _radius, int _volume) {
            return this.init(_source, _x, _y, _z, _radius, _volume, false, 0.0F, 1.0F);
        }

        public WorldSoundManager.WorldSound init(Object _source, int _x, int _y, int _z, int _radius, int _volume, boolean _stresshumans) {
            return this.init(_source, _x, _y, _z, _radius, _volume, _stresshumans, 0.0F, 1.0F);
        }

        public WorldSoundManager.WorldSound init(
            Object _source, int _x, int _y, int _z, int _radius, int _volume, boolean _stresshumans, float _zombieIgnoreDist, float _stressMod
        ) {
            this.source = _source;
            this.life = 1;
            this.x = _x;
            this.y = _y;
            this.z = _z;
            this.radius = _radius;
            this.volume = _volume;
            this.stresshumans = _stresshumans;
            this.zombieIgnoreDist = _zombieIgnoreDist;
            this.stressMod = _stressMod;
            this.sourceIsZombie = _source instanceof IsoZombie;
            this.bRepeating = false;
            LuaEventManager.triggerEvent("OnWorldSound", _x, _y, _z, _radius, _volume, _source);
            return this;
        }

        public WorldSoundManager.WorldSound init(
            boolean _sourceIsZombie, int _x, int _y, int _z, int _radius, int _volume, boolean stressHumans, float _zombieIgnoreDist, float _stressMod
        ) {
            WorldSoundManager.WorldSound worldSound = this.init(null, _x, _y, _z, _radius, _volume, stressHumans, _zombieIgnoreDist, _stressMod);
            worldSound.sourceIsZombie = _sourceIsZombie;
            return worldSound;
        }
    }
}
