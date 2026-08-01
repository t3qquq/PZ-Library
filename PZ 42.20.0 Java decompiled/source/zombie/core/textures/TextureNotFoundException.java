// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.core.textures;

public final class TextureNotFoundException extends RuntimeException {
    public TextureNotFoundException(String name) {
        super("Image " + name + " not found! ");
    }
}
