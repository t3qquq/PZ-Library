// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.core.textures;

public final class TextureNameAlreadyInUseException extends RuntimeException {
    public TextureNameAlreadyInUseException(String name) {
        super("Texture Name " + name + " is already in use");
    }
}
