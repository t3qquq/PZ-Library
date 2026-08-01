// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.characters;

public interface Talker {
    boolean IsSpeaking();

    void Say(String line);

    String getSayLine();

    String getTalkerType();
}
