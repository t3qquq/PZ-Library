// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.javafmod;

public abstract class FMODGlobalParameter extends FMODParameter {
    public FMODGlobalParameter(String string) {
        super(string);
        if (this.getParameterDescription() != null && !this.getParameterDescription().isGlobal()) {
            boolean boolean0 = true;
        }
    }

    @Override
    public void setCurrentValue(float float0) {
        javafmod.FMOD_Studio_System_SetParameterByID(this.getParameterID(), float0, false);
    }

    @Override
    public void startEventInstance(long var1) {
    }

    @Override
    public void stopEventInstance(long var1) {
    }
}
