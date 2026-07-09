// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import fmod.javafmod;
import fmod.fmod.FMODManager;
import fmod.fmod.FMOD_STUDIO_EVENT_DESCRIPTION;
import fmod.fmod.FMOD_STUDIO_PLAYBACK_STATE;
import zombie.GameSounds;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.audio.GameSound;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class Alarm {
    protected static long inst;
    protected static FMOD_STUDIO_EVENT_DESCRIPTION event;
    public boolean finished = false;
    private int x;
    private int y;
    private float volume;
    private float occlusion;
    private float endGameTime;

    public Alarm(int int0, int int1) {
        this.x = int0;
        this.y = int1;
        byte byte0 = 49;
        float float0 = (float)GameTime.instance.getWorldAgeHours();
        this.endGameTime = float0 + byte0 / 3600.0F * (1440.0F / GameTime.instance.getMinutesPerDay());
    }

    public void update() {
        if (!GameClient.bClient) {
            WorldSoundManager.instance.addSound(this, this.x, this.y, 0, 600, 600);
        }

        if (!GameServer.bServer) {
            this.updateSound();
            if (GameTime.getInstance().getWorldAgeHours() >= this.endGameTime) {
                if (inst != 0L) {
                    javafmod.FMOD_Studio_EventInstance_Stop(inst, false);
                    inst = 0L;
                }

                this.finished = true;
            }
        }
    }

    protected void updateSound() {
        if (!GameServer.bServer && !Core.SoundDisabled && !this.finished) {
            if (FMODManager.instance.getNumListeners() != 0) {
                if (inst == 0L) {
                    event = FMODManager.instance.getEventDescription("event:/Meta/HouseAlarm");
                    if (event != null) {
                        javafmod.FMOD_Studio_LoadEventSampleData(event.address);
                        inst = javafmod.FMOD_Studio_System_CreateEventInstance(event.address);
                    }
                }

                if (inst > 0L) {
                    float float0 = SoundManager.instance.getSoundVolume();
                    GameSound gameSound = GameSounds.getSound("HouseAlarm");
                    if (gameSound != null) {
                        float0 *= gameSound.getUserVolume();
                    }

                    if (float0 != this.volume) {
                        javafmod.FMOD_Studio_EventInstance_SetVolume(inst, float0);
                        this.volume = float0;
                    }

                    javafmod.FMOD_Studio_EventInstance3D(inst, this.x, this.y, 0.0F);
                    if (javafmod.FMOD_Studio_GetPlaybackState(inst) != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_PLAYING.index
                        && javafmod.FMOD_Studio_GetPlaybackState(inst) != FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STARTING.index) {
                        if (javafmod.FMOD_Studio_GetPlaybackState(inst) == FMOD_STUDIO_PLAYBACK_STATE.FMOD_STUDIO_PLAYBACK_STOPPING.index) {
                            this.finished = true;
                            return;
                        }

                        javafmod.FMOD_Studio_StartEvent(inst);
                        System.out.println(javafmod.FMOD_Studio_GetPlaybackState(inst));
                    }

                    float float1 = 0.0F;
                    if (IsoPlayer.numPlayers == 1) {
                        IsoGridSquare square0 = IsoPlayer.getInstance().getCurrentSquare();
                        if (square0 != null && !square0.Is(IsoFlagType.exterior)) {
                            float1 = 0.2F;
                            IsoGridSquare square1 = IsoWorld.instance.getCell().getGridSquare(this.x, this.y, 0);
                            if (square1 != null && square1.getBuilding() == square0.getBuilding()) {
                                float1 = 0.0F;
                            }
                        }
                    }

                    if (this.occlusion != float1) {
                        this.occlusion = float1;
                        javafmod.FMOD_Studio_EventInstance_SetParameterByName(inst, "Occlusion", this.occlusion);
                    }
                }
            }
        }
    }
}
