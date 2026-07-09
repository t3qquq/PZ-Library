// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.StorySounds;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import java.util.ArrayList;
import java.util.Stack;
import zombie.GameSounds;
import zombie.SoundManager;
import zombie.audio.GameSound;
import zombie.audio.GameSoundClip;
import zombie.characters.IsoPlayer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;

public final class StoryEmitter {
    public int max = -1;
    public float volumeMod = 1.0F;
    public boolean coordinate3D = true;
    public Stack<StoryEmitter.Sound> SoundStack = new Stack<>();
    public ArrayList<StoryEmitter.Sound> Instances = new ArrayList<>();
    public ArrayList<StoryEmitter.Sound> ToStart = new ArrayList<>();
    private Vector2 soundVect = new Vector2();
    private Vector2 playerVect = new Vector2();

    public int stopSound(long channel) {
        return javafmod.FMOD_Channel_Stop(channel);
    }

    public long playSound(String file, float baseVolume, float x, float y, float z, float minRange, float maxRange) {
        if (this.max != -1 && this.max <= this.Instances.size() + this.ToStart.size()) {
            return 0L;
        } else {
            GameSound gameSound = GameSounds.getSound(file);
            if (gameSound == null) {
                return 0L;
            } else {
                GameSoundClip gameSoundClip = gameSound.getRandomClip();
                long long0 = FMODManager.instance.loadSound(file);
                if (long0 == 0L) {
                    return 0L;
                } else {
                    StoryEmitter.Sound sound;
                    if (this.SoundStack.isEmpty()) {
                        sound = new StoryEmitter.Sound();
                    } else {
                        sound = this.SoundStack.pop();
                    }

                    sound.minRange = minRange;
                    sound.maxRange = maxRange;
                    sound.x = x;
                    sound.y = y;
                    sound.z = z;
                    sound.volume = SoundManager.instance.getSoundVolume() * baseVolume * this.volumeMod;
                    sound.sound = long0;
                    sound.channel = javafmod.FMOD_System_PlaySound(long0, true);
                    this.ToStart.add(sound);
                    javafmod.FMOD_Channel_Set3DAttributes(
                        sound.channel,
                        sound.x - IsoPlayer.getInstance().x,
                        sound.y - IsoPlayer.getInstance().y,
                        sound.z - IsoPlayer.getInstance().z,
                        0.0F,
                        0.0F,
                        0.0F
                    );
                    javafmod.FMOD_Channel_Set3DOcclusion(sound.channel, 1.0F, 1.0F);
                    if (IsoPlayer.getInstance() != null && IsoPlayer.getInstance().Traits.Deaf.isSet()) {
                        javafmod.FMOD_Channel_SetVolume(sound.channel, 0.0F);
                    } else {
                        javafmod.FMOD_Channel_SetVolume(sound.channel, sound.volume);
                    }

                    return sound.channel;
                }
            }
        }
    }

    public void tick() {
        for (int int0 = 0; int0 < this.ToStart.size(); int0++) {
            StoryEmitter.Sound sound0 = this.ToStart.get(int0);
            javafmod.FMOD_Channel_SetPaused(sound0.channel, false);
            this.Instances.add(sound0);
        }

        this.ToStart.clear();

        for (int int1 = 0; int1 < this.Instances.size(); int1++) {
            StoryEmitter.Sound sound1 = this.Instances.get(int1);
            if (!javafmod.FMOD_Channel_IsPlaying(sound1.channel)) {
                this.SoundStack.push(sound1);
                this.Instances.remove(sound1);
                int1--;
            } else {
                float float0 = IsoUtils.DistanceManhatten(
                        sound1.x, sound1.y, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, sound1.z, IsoPlayer.getInstance().z
                    )
                    / sound1.maxRange;
                if (float0 > 1.0F) {
                    float0 = 1.0F;
                }

                if (!this.coordinate3D) {
                    javafmod.FMOD_Channel_Set3DAttributes(
                        sound1.channel,
                        Math.abs(sound1.x - IsoPlayer.getInstance().x),
                        Math.abs(sound1.y - IsoPlayer.getInstance().y),
                        Math.abs(sound1.z - IsoPlayer.getInstance().z),
                        0.0F,
                        0.0F,
                        0.0F
                    );
                } else {
                    javafmod.FMOD_Channel_Set3DAttributes(
                        sound1.channel,
                        Math.abs(sound1.x - IsoPlayer.getInstance().x),
                        Math.abs(sound1.z - IsoPlayer.getInstance().z),
                        Math.abs(sound1.y - IsoPlayer.getInstance().y),
                        0.0F,
                        0.0F,
                        0.0F
                    );
                }

                javafmod.FMOD_System_SetReverbDefault(0, FMODManager.FMOD_PRESET_MOUNTAINS);
                javafmod.FMOD_Channel_SetReverbProperties(sound1.channel, 0, 1.0F);
                javafmod.FMOD_Channel_Set3DMinMaxDistance(sound1.channel, sound1.minRange, sound1.maxRange);
                float float1 = 0.0F;
                float float2 = 0.0F;
                IsoGridSquare square = IsoPlayer.getInstance().getCurrentSquare();
                this.soundVect.set(sound1.x, sound1.y);
                this.playerVect.set(IsoPlayer.getInstance().x, IsoPlayer.getInstance().y);
                float float3 = (float)Math.toDegrees(this.playerVect.angleTo(this.soundVect));
                float float4 = (float)Math.toDegrees(IsoPlayer.getInstance().getForwardDirection().getDirectionNeg());
                if (float4 >= 0.0F && float4 <= 90.0F) {
                    float4 = -90.0F - float4;
                } else if (float4 > 90.0F && float4 <= 180.0F) {
                    float4 = 90.0F + (180.0F - float4);
                } else if (float4 < 0.0F && float4 >= -90.0F) {
                    float4 = 0.0F - (90.0F + float4);
                } else if (float4 < 0.0F && float4 >= -180.0F) {
                    float4 = 90.0F - (180.0F + float4);
                }

                float float5 = Math.abs(float3 - float4) % 360.0F;
                float float6 = float5 > 180.0F ? 360.0F - float5 : float5;
                float float7 = (180.0F - float6) / 180.0F;
                float0 /= 0.4F;
                if (float0 > 1.0F) {
                    float0 = 1.0F;
                }

                float1 = 0.85F * float0 * float7;
                float2 = 0.85F * float0 * float7;
                if (square.getRoom() != null) {
                    float1 = 0.75F + 0.1F * float0 + 0.1F * float7;
                    float2 = 0.75F + 0.1F * float0 + 0.1F * float7;
                }

                javafmod.FMOD_Channel_Set3DOcclusion(sound1.channel, float1, float2);
            }
        }
    }

    public static final class Sound {
        public long sound;
        public long channel;
        public float volume;
        public float x;
        public float y;
        public float z;
        public float minRange;
        public float maxRange;
    }
}
