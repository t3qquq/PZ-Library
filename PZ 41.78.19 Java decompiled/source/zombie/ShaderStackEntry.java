// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import zombie.core.opengl.Shader;
import zombie.util.Pool;
import zombie.util.PooledObject;

public final class ShaderStackEntry extends PooledObject {
    private Shader m_shader;
    private int m_playerIndex;
    private static final Pool<ShaderStackEntry> s_pool = new Pool<>(ShaderStackEntry::new);

    public Shader getShader() {
        return this.m_shader;
    }

    public int getPlayerIndex() {
        return this.m_playerIndex;
    }

    public static ShaderStackEntry alloc(Shader shader, int int0) {
        ShaderStackEntry shaderStackEntry = s_pool.alloc();
        shaderStackEntry.m_shader = shader;
        shaderStackEntry.m_playerIndex = int0;
        return shaderStackEntry;
    }
}
