// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

import fmod.javafmod;
import java.security.InvalidParameterException;
import java.util.HashMap;
import zombie.audio.BaseSoundBank;
import zombie.core.Core;

public class FMODSoundBank extends BaseSoundBank {
    public HashMap<String, FMODVoice> voiceMap = new HashMap<>();
    public HashMap<String, FMODFootstep> footstepMap = new HashMap<>();

    private void check(String string) {
        if (Core.bDebug && javafmod.FMOD_Studio_System_GetEvent("event:/" + string) < 0L) {
            System.out.println("MISSING in .bank " + string);
        }
    }

    @Override
    public void addVoice(String arg0, String arg1, float arg2) {
        FMODVoice fMODVoice = new FMODVoice(arg1, arg2);
        this.voiceMap.put(arg0, fMODVoice);
    }

    @Override
    public void addFootstep(String arg0, String arg1, String arg2, String arg3, String arg4) {
        FMODFootstep fMODFootstep = new FMODFootstep(arg1, arg2, arg3, arg4);
        this.footstepMap.put(arg0, fMODFootstep);
    }

    @Override
    public FMODVoice getVoice(String arg0) {
        return this.voiceMap.containsKey(arg0) ? this.voiceMap.get(arg0) : null;
    }

    @Override
    public FMODFootstep getFootstep(String arg0) {
        if (this.footstepMap.containsKey(arg0)) {
            return this.footstepMap.get(arg0);
        } else {
            throw new InvalidParameterException("Footstep not loaded: " + arg0);
        }
    }
}
