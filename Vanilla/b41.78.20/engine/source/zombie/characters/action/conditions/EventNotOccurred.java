// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action.conditions;

import org.w3c.dom.Element;
import zombie.characters.action.ActionContext;
import zombie.characters.action.IActionCondition;

public final class EventNotOccurred implements IActionCondition {
    public String eventName;

    @Override
    public String getDescription() {
        return "EventNotOccurred(" + this.eventName + ")";
    }

    private boolean load(Element element) {
        this.eventName = element.getTextContent().toLowerCase();
        return true;
    }

    @Override
    public boolean passes(ActionContext actionContext, int int0) {
        return !actionContext.hasEventOccurred(this.eventName, int0);
    }

    @Override
    public IActionCondition clone() {
        return null;
    }

    public static class Factory implements IActionCondition.IFactory {
        @Override
        public IActionCondition create(Element element) {
            EventNotOccurred eventNotOccurred = new EventNotOccurred();
            return eventNotOccurred.load(element) ? eventNotOccurred : null;
        }
    }
}
