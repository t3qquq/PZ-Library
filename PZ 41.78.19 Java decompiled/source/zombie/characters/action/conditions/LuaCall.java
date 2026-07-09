// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action.conditions;

import org.w3c.dom.Element;
import zombie.characters.action.ActionContext;
import zombie.characters.action.IActionCondition;

public final class LuaCall implements IActionCondition {
    @Override
    public String getDescription() {
        return "<luaCheck>";
    }

    @Override
    public boolean passes(ActionContext var1, int var2) {
        return false;
    }

    @Override
    public IActionCondition clone() {
        return new LuaCall();
    }

    public static class Factory implements IActionCondition.IFactory {
        @Override
        public IActionCondition create(Element var1) {
            return new LuaCall();
        }
    }
}
