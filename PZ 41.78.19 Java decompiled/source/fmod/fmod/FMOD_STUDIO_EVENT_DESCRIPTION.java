// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

import java.util.ArrayList;

public final class FMOD_STUDIO_EVENT_DESCRIPTION {
    public final long address;
    public final String path;
    public final FMOD_GUID id;
    public final boolean bHasSustainPoints;
    public final long length;
    public final ArrayList<FMOD_STUDIO_PARAMETER_DESCRIPTION> parameters = new ArrayList<>();

    public FMOD_STUDIO_EVENT_DESCRIPTION(long arg0, String arg1, FMOD_GUID arg2, boolean arg3, long arg4) {
        this.address = arg0;
        this.path = arg1;
        this.id = arg2;
        this.bHasSustainPoints = arg3;
        this.length = arg4;
    }

    public boolean hasParameter(FMOD_STUDIO_PARAMETER_DESCRIPTION arg0) {
        return this.parameters.contains(arg0);
    }
}
