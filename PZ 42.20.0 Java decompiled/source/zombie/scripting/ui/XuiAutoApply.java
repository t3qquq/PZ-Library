// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.ui;

import zombie.UsedFromLua;

@UsedFromLua
public enum XuiAutoApply {
    No,
    Always,
    IfSet,
    IfSetAndKeyExists,
    Forbidden;
}
