// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

public final class TextureNameAlreadyInUseException extends RuntimeException {
    public TextureNameAlreadyInUseException(String string) {
        super("Texture Name " + string + " is already in use");
    }
}
