// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

public final class FMOD_STUDIO_PARAMETER_DESCRIPTION {
    public final String name;
    public final FMOD_STUDIO_PARAMETER_ID id;
    public final int flags;
    public final int globalIndex;

    public FMOD_STUDIO_PARAMETER_DESCRIPTION(String arg0, FMOD_STUDIO_PARAMETER_ID arg1, int arg2, int arg3) {
        this.name = arg0;
        this.id = arg1;
        this.flags = arg2;
        this.globalIndex = arg3;
    }

    public boolean isGlobal() {
        return (this.flags & FMOD_STUDIO_PARAMETER_FLAGS.FMOD_STUDIO_PARAMETER_GLOBAL.bit) != 0;
    }
}
