// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.iso.objects.interfaces;

public interface Activatable {
    boolean Activated();

    void Toggle();

    String getActivatableType();
}
