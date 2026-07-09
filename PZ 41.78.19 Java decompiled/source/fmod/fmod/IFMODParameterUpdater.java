// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

import java.util.BitSet;
import zombie.audio.FMODParameterList;
import zombie.audio.GameSoundClip;

public interface IFMODParameterUpdater {
    FMODParameterList getFMODParameters();

    void startEvent(long arg0, GameSoundClip arg1, BitSet arg2);

    void updateEvent(long arg0, GameSoundClip arg1);

    void stopEvent(long arg0, GameSoundClip arg1, BitSet arg2);
}
