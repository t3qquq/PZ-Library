// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

import java.util.HashMap;
import org.lwjgl.opengl.ARBShaderObjects;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;

public class Shader implements IShaderProgramListener {
    public static HashMap<Integer, Shader> ShaderMap = new HashMap<>();
    public String name;
    private int m_shaderMapID = 0;
    private final ShaderProgram m_shaderProgram;
    public Texture tex;
    public int width;
    public int height;

    public Shader(String _name) {
        this.name = _name;
        this.m_shaderProgram = ShaderProgram.createShaderProgram(_name, false, false);
        this.m_shaderProgram.addCompileListener(this);
        this.m_shaderProgram.compile();
    }

    public void setTexture(Texture _tex) {
        this.tex = _tex;
    }

    public int getID() {
        return this.m_shaderProgram.getShaderID();
    }

    public void Start() {
        ARBShaderObjects.glUseProgramObjectARB(this.m_shaderProgram.getShaderID());
    }

    public void End() {
        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    public void destroy() {
        this.m_shaderProgram.destroy();
        ShaderMap.remove(this.m_shaderMapID);
        this.m_shaderMapID = 0;
    }

    public void startMainThread(TextureDraw texd, int playerIndex) {
    }

    public void startRenderThread(TextureDraw _tex) {
    }

    public void postRender(TextureDraw texd) {
    }

    public boolean isCompiled() {
        return this.m_shaderProgram.isCompiled();
    }

    @Override
    public void callback(ShaderProgram sender) {
        ShaderMap.remove(this.m_shaderMapID);
        this.m_shaderMapID = sender.getShaderID();
        ShaderMap.put(this.m_shaderMapID, this);
        this.onCompileSuccess(sender);
    }

    protected void onCompileSuccess(ShaderProgram var1) {
    }

    public ShaderProgram getProgram() {
        return this.m_shaderProgram;
    }
}
