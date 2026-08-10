// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import zombie.util.StringUtils;

public final class AssetPath {
    protected String m_path;

    public AssetPath(String path) {
        this.m_path = path;
    }

    public boolean isValid() {
        return !StringUtils.isNullOrEmpty(this.m_path);
    }

    public int getHash() {
        return this.m_path.hashCode();
    }

    public String getPath() {
        return this.m_path;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ \"" + this.getPath() + "\" }";
    }
}
