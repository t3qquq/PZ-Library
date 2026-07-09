// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.objects;

import fmod.fmod.FMODSoundEmitter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Stack;
import zombie.WorldSoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.audio.DummySoundEmitter;
import zombie.audio.parameters.ParameterFireSize;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.util.list.PZArrayUtil;

public class IsoFireManager {
    public static double Red_Oscilator = 0.0;
    public static double Green_Oscilator = 0.0;
    public static double Blue_Oscilator = 0.0;
    public static double Red_Oscilator_Rate = 0.1F;
    public static double Green_Oscilator_Rate = 0.13F;
    public static double Blue_Oscilator_Rate = 0.0876F;
    public static double Red_Oscilator_Val = 0.0;
    public static double Green_Oscilator_Val = 0.0;
    public static double Blue_Oscilator_Val = 0.0;
    public static double OscilatorSpeedScalar = 15.6F;
    public static double OscilatorEffectScalar = 0.0039F;
    public static int MaxFireObjects = 75;
    public static int FireRecalcDelay = 25;
    public static int FireRecalc = FireRecalcDelay;
    public static boolean LightCalcFromBurningCharacters = false;
    public static float FireAlpha = 1.0F;
    public static float SmokeAlpha = 0.3F;
    public static float FireAnimDelay = 0.2F;
    public static float SmokeAnimDelay = 0.2F;
    public static ColorInfo FireTintMod = new ColorInfo(1.0F, 1.0F, 1.0F, 1.0F);
    public static ColorInfo SmokeTintMod = new ColorInfo(0.5F, 0.5F, 0.5F, 1.0F);
    public static final ArrayList<IsoFire> FireStack = new ArrayList<>();
    public static final ArrayList<IsoGameCharacter> CharactersOnFire_Stack = new ArrayList<>();
    private static final IsoFireManager.FireSounds fireSounds = new IsoFireManager.FireSounds(20);
    private static Stack<IsoFire> updateStack = new Stack<>();
    private static final HashSet<IsoGameCharacter> charactersOnFire = new HashSet<>();

    public static void Add(IsoFire NewFire) {
        if (FireStack.contains(NewFire)) {
            System.out.println("IsoFireManager.Add already added fire, ignoring");
        } else {
            if (FireStack.size() < MaxFireObjects) {
                FireStack.add(NewFire);
            } else {
                IsoFire fire = null;
                int int0 = 0;

                for (int int1 = 0; int1 < FireStack.size(); int1++) {
                    if (FireStack.get(int1).Age > int0) {
                        int0 = FireStack.get(int1).Age;
                        fire = FireStack.get(int1);
                    }
                }

                if (fire != null && fire.square != null) {
                    fire.square.getProperties().UnSet(IsoFlagType.burning);
                    fire.square.getProperties().UnSet(IsoFlagType.smoke);
                    fire.RemoveAttachedAnims();
                    fire.removeFromWorld();
                    fire.removeFromSquare();
                }

                FireStack.add(NewFire);
            }
        }
    }

    public static void AddBurningCharacter(IsoGameCharacter BurningCharacter) {
        for (int int0 = 0; int0 < CharactersOnFire_Stack.size(); int0++) {
            if (CharactersOnFire_Stack.get(int0) == BurningCharacter) {
                return;
            }
        }

        CharactersOnFire_Stack.add(BurningCharacter);
    }

    public static void Fire_LightCalc(IsoGridSquare FireSquare, IsoGridSquare TestSquare, int playerIndex) {
        if (TestSquare != null && FireSquare != null) {
            int int0 = 0;
            byte byte0 = 8;
            int0 += Math.abs(TestSquare.getX() - FireSquare.getX());
            int0 += Math.abs(TestSquare.getY() - FireSquare.getY());
            int0 += Math.abs(TestSquare.getZ() - FireSquare.getZ());
            if (int0 <= byte0) {
                float float0 = 0.199F / byte0 * (byte0 - int0);
                float float1 = float0 * 0.6F;
                float float2 = float0 * 0.4F;
                if (TestSquare.getLightInfluenceR() == null) {
                    TestSquare.setLightInfluenceR(new ArrayList<>());
                }

                TestSquare.getLightInfluenceR().add(float0);
                if (TestSquare.getLightInfluenceG() == null) {
                    TestSquare.setLightInfluenceG(new ArrayList<>());
                }

                TestSquare.getLightInfluenceG().add(float1);
                if (TestSquare.getLightInfluenceB() == null) {
                    TestSquare.setLightInfluenceB(new ArrayList<>());
                }

                TestSquare.getLightInfluenceB().add(float2);
                ColorInfo colorInfo = TestSquare.lighting[playerIndex].lightInfo();
                colorInfo.r += float0;
                colorInfo.g += float1;
                colorInfo.b += float2;
                if (colorInfo.r > 1.0F) {
                    colorInfo.r = 1.0F;
                }

                if (colorInfo.g > 1.0F) {
                    colorInfo.g = 1.0F;
                }

                if (colorInfo.b > 1.0F) {
                    colorInfo.b = 1.0F;
                }
            }
        }
    }

    public static void LightTileWithFire(IsoGridSquare TestSquare) {
    }

    public static void explode(IsoCell cell, IsoGridSquare gridSquare, int power) {
        if (gridSquare != null) {
            IsoGridSquare square = null;
            Object object = null;
            FireRecalc = 1;

            for (int int0 = -2; int0 <= 2; int0++) {
                for (int int1 = -2; int1 <= 2; int1++) {
                    for (int int2 = 0; int2 <= 1; int2++) {
                        square = cell.getGridSquare(gridSquare.getX() + int0, gridSquare.getY() + int1, gridSquare.getZ() + int2);
                        if (square != null && Rand.Next(100) < power && IsoFire.CanAddFire(square, true)) {
                            StartFire(cell, square, true, Rand.Next(100, 250 + power));
                            square.BurnWalls(true);
                        }
                    }
                }
            }
        }
    }

    @Deprecated
    public static void MolotovSmash(IsoCell cell, IsoGridSquare gridSquare) {
    }

    public static void Remove(IsoFire DyingFire) {
        if (!FireStack.contains(DyingFire)) {
            System.out.println("IsoFireManager.Remove unknown fire, ignoring");
        } else {
            FireStack.remove(DyingFire);
        }
    }

    public static void RemoveBurningCharacter(IsoGameCharacter BurningCharacter) {
        CharactersOnFire_Stack.remove(BurningCharacter);
    }

    public static void StartFire(IsoCell cell, IsoGridSquare gridSquare, boolean IgniteOnAny, int FireStartingEnergy, int Life) {
        if (gridSquare.getFloor() != null && gridSquare.getFloor().getSprite() != null) {
            FireStartingEnergy -= gridSquare.getFloor().getSprite().firerequirement;
        }

        if (FireStartingEnergy < 5) {
            FireStartingEnergy = 5;
        }

        if (IsoFire.CanAddFire(gridSquare, IgniteOnAny)) {
            if (GameClient.bClient) {
                DebugLog.General.warn("The StartFire function was called on Client");
            } else if (GameServer.bServer) {
                GameServer.startFireOnClient(gridSquare, FireStartingEnergy, IgniteOnAny, Life, false);
            } else {
                IsoFire fire = new IsoFire(cell, gridSquare, IgniteOnAny, FireStartingEnergy, Life);
                Add(fire);
                gridSquare.getObjects().add(fire);
                if (Rand.Next(5) == 0) {
                    WorldSoundManager.instance.addSound(fire, gridSquare.getX(), gridSquare.getY(), gridSquare.getZ(), 20, 20);
                }
            }
        }
    }

    public static void StartSmoke(IsoCell cell, IsoGridSquare gridSquare, boolean IgniteOnAny, int FireStartingEnergy, int Life) {
        if (IsoFire.CanAddSmoke(gridSquare, IgniteOnAny)) {
            if (GameClient.bClient) {
                ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
                PacketTypes.PacketType.StartFire.doPacket(byteBufferWriter);
                byteBufferWriter.putInt(gridSquare.getX());
                byteBufferWriter.putInt(gridSquare.getY());
                byteBufferWriter.putInt(gridSquare.getZ());
                byteBufferWriter.putInt(FireStartingEnergy);
                byteBufferWriter.putBoolean(IgniteOnAny);
                byteBufferWriter.putInt(Life);
                byteBufferWriter.putBoolean(true);
                PacketTypes.PacketType.StartFire.send(GameClient.connection);
            } else if (GameServer.bServer) {
                GameServer.startFireOnClient(gridSquare, FireStartingEnergy, IgniteOnAny, Life, true);
            } else {
                IsoFire fire = new IsoFire(cell, gridSquare, IgniteOnAny, FireStartingEnergy, Life, true);
                Add(fire);
                gridSquare.getObjects().add(fire);
            }
        }
    }

    public static void StartFire(IsoCell cell, IsoGridSquare gridSquare, boolean IgniteOnAny, int FireStartingEnergy) {
        StartFire(cell, gridSquare, IgniteOnAny, FireStartingEnergy, 0);
    }

    public static void addCharacterOnFire(IsoGameCharacter character) {
        synchronized (charactersOnFire) {
            charactersOnFire.add(character);
        }
    }

    public static void deleteCharacterOnFire(IsoGameCharacter character) {
        synchronized (charactersOnFire) {
            charactersOnFire.remove(character);
        }
    }

    public static void Update() {
        synchronized (charactersOnFire) {
            charactersOnFire.forEach(IsoGameCharacter::SpreadFireMP);
        }

        Red_Oscilator_Val = Math.sin(Red_Oscilator = Red_Oscilator + Blue_Oscilator_Rate * OscilatorSpeedScalar);
        Green_Oscilator_Val = Math.sin(Green_Oscilator = Green_Oscilator + Blue_Oscilator_Rate * OscilatorSpeedScalar);
        Blue_Oscilator_Val = Math.sin(Blue_Oscilator = Blue_Oscilator + Blue_Oscilator_Rate * OscilatorSpeedScalar);
        Red_Oscilator_Val = (Red_Oscilator_Val + 1.0) / 2.0;
        Green_Oscilator_Val = (Green_Oscilator_Val + 1.0) / 2.0;
        Blue_Oscilator_Val = (Blue_Oscilator_Val + 1.0) / 2.0;
        Red_Oscilator_Val = Red_Oscilator_Val * OscilatorEffectScalar;
        Green_Oscilator_Val = Green_Oscilator_Val * OscilatorEffectScalar;
        Blue_Oscilator_Val = Blue_Oscilator_Val * OscilatorEffectScalar;
        updateStack.clear();
        updateStack.addAll(FireStack);

        for (int int0 = 0; int0 < updateStack.size(); int0++) {
            IsoFire fire = updateStack.get(int0);
            if (fire.getObjectIndex() != -1 && FireStack.contains(fire)) {
                fire.update();
            }
        }

        FireRecalc--;
        if (FireRecalc < 0) {
            FireRecalc = FireRecalcDelay;
        }

        fireSounds.update();
    }

    public static void updateSound(IsoFire fire) {
        fireSounds.addFire(fire);
    }

    public static void stopSound(IsoFire fire) {
        fireSounds.removeFire(fire);
    }

    public static void RemoveAllOn(IsoGridSquare sq) {
        for (int int0 = FireStack.size() - 1; int0 >= 0; int0--) {
            IsoFire fire = FireStack.get(int0);
            if (fire.square == sq) {
                fire.extinctFire();
            }
        }
    }

    public static void Reset() {
        FireStack.clear();
        CharactersOnFire_Stack.clear();
        fireSounds.Reset();
    }

    private static final class FireSounds {
        final ArrayList<IsoFire> fires = new ArrayList<>();
        final IsoFireManager.FireSounds.Slot[] slots;
        final Comparator<IsoFire> comp = new Comparator<IsoFire>() {
            public int compare(IsoFire fire0, IsoFire fire1) {
                float float0 = FireSounds.this.getClosestListener(fire0.square.x + 0.5F, fire0.square.y + 0.5F, fire0.square.z);
                float float1 = FireSounds.this.getClosestListener(fire1.square.x + 0.5F, fire1.square.y + 0.5F, fire1.square.z);
                if (float0 > float1) {
                    return 1;
                } else {
                    return float0 < float1 ? -1 : 0;
                }
            }
        };

        FireSounds(int int0) {
            this.slots = PZArrayUtil.newInstance(IsoFireManager.FireSounds.Slot.class, int0, IsoFireManager.FireSounds.Slot::new);
        }

        void addFire(IsoFire fire) {
            if (!this.fires.contains(fire)) {
                this.fires.add(fire);
            }
        }

        void removeFire(IsoFire fire) {
            this.fires.remove(fire);
        }

        void update() {
            if (!GameServer.bServer) {
                for (int int0 = 0; int0 < this.slots.length; int0++) {
                    this.slots[int0].playing = false;
                }

                if (this.fires.isEmpty()) {
                    this.stopNotPlaying();
                } else {
                    Collections.sort(this.fires, this.comp);
                    int int1 = Math.min(this.fires.size(), this.slots.length);

                    for (int int2 = 0; int2 < int1; int2++) {
                        IsoFire fire0 = this.fires.get(int2);
                        if (this.shouldPlay(fire0)) {
                            int int3 = this.getExistingSlot(fire0);
                            if (int3 != -1) {
                                this.slots[int3].playSound(fire0);
                            }
                        }
                    }

                    for (int int4 = 0; int4 < int1; int4++) {
                        IsoFire fire1 = this.fires.get(int4);
                        if (this.shouldPlay(fire1)) {
                            int int5 = this.getExistingSlot(fire1);
                            if (int5 == -1) {
                                int5 = this.getFreeSlot();
                                this.slots[int5].playSound(fire1);
                            }
                        }
                    }

                    this.stopNotPlaying();
                    this.fires.clear();
                }
            }
        }

        float getClosestListener(float float5, float float6, float float7) {
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

        boolean shouldPlay(IsoFire fire) {
            return fire != null && fire.getObjectIndex() != -1 && fire.LifeStage < 4;
        }

        int getExistingSlot(IsoFire fire) {
            for (int int0 = 0; int0 < this.slots.length; int0++) {
                if (this.slots[int0].fire == fire) {
                    return int0;
                }
            }

            return -1;
        }

        int getFreeSlot() {
            for (int int0 = 0; int0 < this.slots.length; int0++) {
                if (!this.slots[int0].playing) {
                    return int0;
                }
            }

            return -1;
        }

        void stopNotPlaying() {
            for (int int0 = 0; int0 < this.slots.length; int0++) {
                IsoFireManager.FireSounds.Slot slot = this.slots[int0];
                if (!slot.playing) {
                    slot.stopPlaying();
                    slot.fire = null;
                }
            }
        }

        void Reset() {
            for (int int0 = 0; int0 < this.slots.length; int0++) {
                this.slots[int0].stopPlaying();
                this.slots[int0].fire = null;
                this.slots[int0].playing = false;
            }
        }

        static final class Slot {
            IsoFire fire;
            BaseSoundEmitter emitter;
            final ParameterFireSize parameterFireSize = new ParameterFireSize();
            long instance = 0L;
            boolean playing;

            void playSound(IsoFire firex) {
                if (this.emitter == null) {
                    this.emitter = (BaseSoundEmitter)(Core.SoundDisabled ? new DummySoundEmitter() : new FMODSoundEmitter());
                    if (!Core.SoundDisabled) {
                        ((FMODSoundEmitter)this.emitter).addParameter(this.parameterFireSize);
                    }
                }

                this.emitter.setPos(firex.square.x + 0.5F, firex.square.y + 0.5F, firex.square.z);

                byte byte0 = switch (firex.LifeStage) {
                    case 1, 3 -> 1;
                    case 2 -> 2;
                    default -> 0;
                };
                this.parameterFireSize.setSize(byte0);
                if (firex.isCampfire()) {
                    if (!this.emitter.isPlaying("CampfireRunning")) {
                        this.instance = this.emitter.playSoundImpl("CampfireRunning", (IsoObject)null);
                    }
                } else if (!this.emitter.isPlaying("Fire")) {
                    this.instance = this.emitter.playSoundImpl("Fire", (IsoObject)null);
                }

                this.fire = firex;
                this.playing = true;
                this.emitter.tick();
            }

            void stopPlaying() {
                if (this.emitter != null && this.instance != 0L) {
                    if (this.emitter.hasSustainPoints(this.instance)) {
                        this.emitter.triggerCue(this.instance);
                        this.instance = 0L;
                    } else {
                        this.emitter.stopAll();
                        this.instance = 0L;
                    }
                } else {
                    if (this.emitter != null && !this.emitter.isEmpty()) {
                        this.emitter.tick();
                    }
                }
            }
        }
    }
}
