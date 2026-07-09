// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import zombie.core.Core;
import zombie.core.math.PZMath;
import zombie.iso.IsoUtils;
import zombie.network.GameServer;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;

public final class ZombieVocalsManager {
    public static final ZombieVocalsManager instance = new ZombieVocalsManager();
    private final HashSet<IsoZombie> m_added = new HashSet<>();
    private final ObjectPool<ZombieVocalsManager.ObjectWithDistance> m_objectPool = new ObjectPool<>(ZombieVocalsManager.ObjectWithDistance::new);
    private final ArrayList<ZombieVocalsManager.ObjectWithDistance> m_objects = new ArrayList<>();
    private final ZombieVocalsManager.Slot[] m_slots;
    private long m_updateMS = 0L;
    private final Comparator<ZombieVocalsManager.ObjectWithDistance> comp = new Comparator<ZombieVocalsManager.ObjectWithDistance>() {
        public int compare(ZombieVocalsManager.ObjectWithDistance objectWithDistance1, ZombieVocalsManager.ObjectWithDistance objectWithDistance0) {
            return Float.compare(objectWithDistance1.distSq, objectWithDistance0.distSq);
        }
    };

    public ZombieVocalsManager() {
        byte byte0 = 20;
        this.m_slots = PZArrayUtil.newInstance(ZombieVocalsManager.Slot.class, byte0, ZombieVocalsManager.Slot::new);
    }

    public void addCharacter(IsoZombie zombie0) {
        if (!this.m_added.contains(zombie0)) {
            this.m_added.add(zombie0);
            ZombieVocalsManager.ObjectWithDistance objectWithDistance = this.m_objectPool.alloc();
            objectWithDistance.character = zombie0;
            this.m_objects.add(objectWithDistance);
        }
    }

    public void update() {
        if (!GameServer.bServer) {
            long long0 = System.currentTimeMillis();
            if (long0 - this.m_updateMS >= 500L) {
                this.m_updateMS = long0;

                for (int int0 = 0; int0 < this.m_slots.length; int0++) {
                    this.m_slots[int0].playing = false;
                }

                if (this.m_objects.isEmpty()) {
                    this.stopNotPlaying();
                } else {
                    for (int int1 = 0; int1 < this.m_objects.size(); int1++) {
                        ZombieVocalsManager.ObjectWithDistance objectWithDistance = this.m_objects.get(int1);
                        IsoZombie zombie0 = objectWithDistance.character;
                        objectWithDistance.distSq = this.getClosestListener(zombie0.x, zombie0.y, zombie0.z);
                    }

                    this.m_objects.sort(this.comp);
                    int int2 = PZMath.min(this.m_slots.length, this.m_objects.size());

                    for (int int3 = 0; int3 < int2; int3++) {
                        IsoZombie zombie1 = this.m_objects.get(int3).character;
                        if (this.shouldPlay(zombie1)) {
                            int int4 = this.getExistingSlot(zombie1);
                            if (int4 != -1) {
                                this.m_slots[int4].playSound(zombie1);
                            }
                        }
                    }

                    for (int int5 = 0; int5 < int2; int5++) {
                        IsoZombie zombie2 = this.m_objects.get(int5).character;
                        if (this.shouldPlay(zombie2)) {
                            int int6 = this.getExistingSlot(zombie2);
                            if (int6 == -1) {
                                int6 = this.getFreeSlot();
                                this.m_slots[int6].playSound(zombie2);
                            }
                        }
                    }

                    this.stopNotPlaying();
                    this.postUpdate();
                    this.m_added.clear();
                    this.m_objectPool.release(this.m_objects);
                    this.m_objects.clear();
                }
            }
        }
    }

    boolean shouldPlay(IsoZombie zombie0) {
        return zombie0.getCurrentSquare() != null;
    }

    int getExistingSlot(IsoZombie zombie0) {
        for (int int0 = 0; int0 < this.m_slots.length; int0++) {
            if (this.m_slots[int0].character == zombie0) {
                return int0;
            }
        }

        return -1;
    }

    int getFreeSlot() {
        for (int int0 = 0; int0 < this.m_slots.length; int0++) {
            if (!this.m_slots[int0].playing) {
                return int0;
            }
        }

        return -1;
    }

    void stopNotPlaying() {
        for (int int0 = 0; int0 < this.m_slots.length; int0++) {
            ZombieVocalsManager.Slot slot = this.m_slots[int0];
            if (!slot.playing) {
                slot.stopPlaying();
                slot.character = null;
            }
        }
    }

    public void postUpdate() {
    }

    private float getClosestListener(float float5, float float6, float float7) {
        float float0 = Float.MAX_VALUE;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null && player.getCurrentSquare() != null) {
                float float1 = player.getX();
                float float2 = player.getY();
                float float3 = player.getZ();
                float float4 = IsoUtils.DistanceToSquared(float1, float2, float3 * 3.0F, float5, float6, float7 * 3.0F);
                if (player.Traits.HardOfHearing.isSet()) {
                    float4 *= 4.5F;
                }

                if (float4 < float0) {
                    float0 = float4;
                }
            }
        }

        return float0;
    }

    public void render() {
        if (Core.bDebug) {
        }
    }

    public static void Reset() {
        for (int int0 = 0; int0 < instance.m_slots.length; int0++) {
            instance.m_slots[int0].stopPlaying();
            instance.m_slots[int0].character = null;
            instance.m_slots[int0].playing = false;
        }
    }

    static final class ObjectWithDistance {
        IsoZombie character;
        float distSq;
    }

    static final class Slot {
        IsoZombie character = null;
        boolean playing = false;

        void playSound(IsoZombie zombie0) {
            if (this.character != null && this.character != zombie0 && this.character.vocalEvent != 0L) {
                this.character.getEmitter().stopSoundLocal(this.character.vocalEvent);
                this.character.vocalEvent = 0L;
            }

            this.character = zombie0;
            this.playing = true;
            if (this.character.vocalEvent == 0L) {
                String string = zombie0.isFemale() ? "FemaleZombieCombined" : "MaleZombieCombined";
                if (!zombie0.getFMODParameters().parameterList.contains(zombie0.parameterZombieState)) {
                    zombie0.parameterZombieState.update();
                    zombie0.getFMODParameters().add(zombie0.parameterZombieState);
                    zombie0.parameterCharacterInside.update();
                    zombie0.getFMODParameters().add(zombie0.parameterCharacterInside);
                    zombie0.parameterPlayerDistance.update();
                    zombie0.getFMODParameters().add(zombie0.parameterPlayerDistance);
                }

                zombie0.vocalEvent = zombie0.getEmitter().playVocals(string);
            }
        }

        void stopPlaying() {
            if (this.character != null && this.character.vocalEvent != 0L) {
                this.character.getEmitter().stopSoundLocal(this.character.vocalEvent);
                this.character.vocalEvent = 0L;
            }
        }
    }
}
