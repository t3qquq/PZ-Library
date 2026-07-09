// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio;

import fmod.fmod.FMOD_STUDIO_PARAMETER_DESCRIPTION;
import java.util.ArrayList;

public final class FMODParameterList {
    public final ArrayList<FMODParameter> parameterList = new ArrayList<>();
    public final FMODParameter[] parameterArray = new FMODParameter[96];

    public void add(FMODParameter parameter) {
        this.parameterList.add(parameter);
        if (parameter.getParameterDescription() != null) {
            this.parameterArray[parameter.getParameterDescription().globalIndex] = parameter;
        }
    }

    public FMODParameter get(FMOD_STUDIO_PARAMETER_DESCRIPTION pdesc) {
        return pdesc == null ? null : this.parameterArray[pdesc.globalIndex];
    }

    public void update() {
        for (int int0 = 0; int0 < this.parameterList.size(); int0++) {
            this.parameterList.get(int0).update();
        }
    }
}
