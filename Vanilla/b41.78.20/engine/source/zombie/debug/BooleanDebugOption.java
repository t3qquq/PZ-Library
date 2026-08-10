// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

import zombie.config.BooleanConfigOption;
import zombie.core.Core;
import zombie.debug.options.IDebugOption;
import zombie.debug.options.IDebugOptionGroup;

public class BooleanDebugOption extends BooleanConfigOption implements IDebugOption {
    private IDebugOptionGroup m_parent;
    private final boolean m_debugOnly;

    public BooleanDebugOption(String name, boolean debugOnly, boolean defaultValue) {
        super(name, defaultValue);
        this.m_debugOnly = debugOnly;
    }

    @Override
    public boolean getValue() {
        return !Core.bDebug && this.isDebugOnly() ? super.getDefaultValue() : super.getValue();
    }

    public boolean isDebugOnly() {
        return this.m_debugOnly;
    }

    @Override
    public IDebugOptionGroup getParent() {
        return this.m_parent;
    }

    @Override
    public void setParent(IDebugOptionGroup parent) {
        this.m_parent = parent;
    }
}
