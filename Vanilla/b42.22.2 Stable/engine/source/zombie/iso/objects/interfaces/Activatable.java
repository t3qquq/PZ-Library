// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.iso.objects.interfaces;

public interface Activatable {
    boolean Activated();

    void Toggle();

    String getActivatableType();
}
