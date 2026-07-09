// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

import java.util.ArrayList;
import zombie.debug.BooleanDebugOption;

public class OptionGroup implements IDebugOptionGroup {
    public final IDebugOptionGroup Group;
    private IDebugOptionGroup m_parentGroup;
    private final String m_groupName;
    private final ArrayList<IDebugOption> m_children = new ArrayList<>();

    public OptionGroup(String string) {
        this.m_groupName = string;
        this.Group = this;
    }

    public OptionGroup(IDebugOptionGroup iDebugOptionGroup, String string) {
        this.m_groupName = getCombinedName(iDebugOptionGroup, string);
        this.Group = this;
        iDebugOptionGroup.addChild(this);
    }

    @Override
    public String getName() {
        return this.m_groupName;
    }

    @Override
    public IDebugOptionGroup getParent() {
        return this.m_parentGroup;
    }

    @Override
    public void setParent(IDebugOptionGroup iDebugOptionGroup) {
        this.m_parentGroup = iDebugOptionGroup;
    }

    @Override
    public Iterable<IDebugOption> getChildren() {
        return this.m_children;
    }

    @Override
    public void addChild(IDebugOption iDebugOption) {
        this.m_children.add(iDebugOption);
        iDebugOption.setParent(this);
        this.onChildAdded(iDebugOption);
    }

    @Override
    public void onChildAdded(IDebugOption iDebugOption) {
        this.onDescendantAdded(iDebugOption);
    }

    @Override
    public void onDescendantAdded(IDebugOption iDebugOption) {
        if (this.m_parentGroup != null) {
            this.m_parentGroup.onDescendantAdded(iDebugOption);
        }
    }

    public static BooleanDebugOption newOption(String string, boolean boolean0) {
        return newOptionInternal(null, string, false, boolean0);
    }

    public static BooleanDebugOption newDebugOnlyOption(String string, boolean boolean0) {
        return newOptionInternal(null, string, true, boolean0);
    }

    public static BooleanDebugOption newOption(IDebugOptionGroup iDebugOptionGroup, String string, boolean boolean0) {
        return newOptionInternal(iDebugOptionGroup, string, false, boolean0);
    }

    public static BooleanDebugOption newDebugOnlyOption(IDebugOptionGroup iDebugOptionGroup, String string, boolean boolean0) {
        return newOptionInternal(iDebugOptionGroup, string, true, boolean0);
    }

    private static BooleanDebugOption newOptionInternal(IDebugOptionGroup iDebugOptionGroup, String string1, boolean boolean0, boolean boolean1) {
        String string0 = getCombinedName(iDebugOptionGroup, string1);
        BooleanDebugOption booleanDebugOption = new BooleanDebugOption(string0, boolean0, boolean1);
        if (iDebugOptionGroup != null) {
            iDebugOptionGroup.addChild(booleanDebugOption);
        }

        return booleanDebugOption;
    }

    private static String getCombinedName(IDebugOptionGroup iDebugOptionGroup, String string1) {
        String string0;
        if (iDebugOptionGroup != null) {
            string0 = String.format("%s.%s", iDebugOptionGroup.getName(), string1);
        } else {
            string0 = string1;
        }

        return string0;
    }
}
