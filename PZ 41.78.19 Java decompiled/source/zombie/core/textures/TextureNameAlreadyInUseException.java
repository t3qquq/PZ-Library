// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

public final class TextureNameAlreadyInUseException extends RuntimeException {
    public TextureNameAlreadyInUseException(String string) {
        super("Texture Name " + string + " is already in use");
    }
}
