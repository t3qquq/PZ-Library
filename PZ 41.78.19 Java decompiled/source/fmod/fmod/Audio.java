// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package fmod.fmod;

public interface Audio {
    boolean isPlaying();

    void setVolume(float arg0);

    void start();

    void pause();

    void stop();

    void setName(String arg0);

    String getName();
}
