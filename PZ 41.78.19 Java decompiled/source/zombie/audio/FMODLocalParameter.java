// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.javafmod;
import gnu.trove.list.array.TLongArrayList;

public class FMODLocalParameter extends FMODParameter {
    private final TLongArrayList m_instances = new TLongArrayList();

    public FMODLocalParameter(String string) {
        super(string);
        if (this.getParameterDescription() != null && this.getParameterDescription().isGlobal()) {
            boolean boolean0 = true;
        }
    }

    @Override
    public float calculateCurrentValue() {
        return 0.0F;
    }

    @Override
    public void setCurrentValue(float float0) {
        for (int int0 = 0; int0 < this.m_instances.size(); int0++) {
            long long0 = this.m_instances.get(int0);
            javafmod.FMOD_Studio_EventInstance_SetParameterByID(long0, this.getParameterID(), float0, false);
        }
    }

    @Override
    public void startEventInstance(long long0) {
        this.m_instances.add(long0);
        javafmod.FMOD_Studio_EventInstance_SetParameterByID(long0, this.getParameterID(), this.getCurrentValue(), false);
    }

    @Override
    public void stopEventInstance(long long0) {
        this.m_instances.remove(long0);
    }
}
