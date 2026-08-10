// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action;

import java.util.HashMap;
import org.w3c.dom.Element;

public interface IActionCondition {
    HashMap<String, IActionCondition.IFactory> s_factoryMap = new HashMap<>();

    String getDescription();

    boolean passes(ActionContext var1, int var2);

    IActionCondition clone();

    static IActionCondition createInstance(Element element) {
        IActionCondition.IFactory iFactory = s_factoryMap.get(element.getNodeName());
        return iFactory != null ? iFactory.create(element) : null;
    }

    static void registerFactory(String string, IActionCondition.IFactory iFactory) {
        s_factoryMap.put(string, iFactory);
    }

    public interface IFactory {
        IActionCondition create(Element var1);
    }
}
