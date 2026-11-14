// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.characters;

public interface Talker {
    boolean IsSpeaking();

    void Say(String line);

    String getSayLine();

    String getTalkerType();
}
