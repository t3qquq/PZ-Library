// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.network.GameClient;
import zombie.util.Lambda;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;

public final class ActionTransition implements Cloneable {
    String transitionTo;
    boolean asSubstate;
    boolean transitionOut;
    boolean forceParent;
    final List<IActionCondition> conditions = new ArrayList<>();

    public static boolean parse(Element element, String string, List<ActionTransition> list) {
        if (element.getNodeName().equals("transitions")) {
            parseTransitions(element, string, list);
            return true;
        } else if (element.getNodeName().equals("transition")) {
            parseTransition(element, list);
            return true;
        } else {
            return false;
        }
    }

    public static void parseTransition(Element element, List<ActionTransition> list) {
        list.clear();
        ActionTransition actionTransition = new ActionTransition();
        if (actionTransition.load(element)) {
            list.add(actionTransition);
        }
    }

    public static void parseTransitions(Element element, String string, List<ActionTransition> list) {
        list.clear();
        Lambda.forEachFrom(PZXmlUtil::forEachElement, element, string, list, (elementx, stringx, listx) -> {
            if (!elementx.getNodeName().equals("transition")) {
                DebugLog.ActionSystem.warn("Warning: Unrecognised element '" + elementx.getNodeName() + "' in " + stringx);
            } else {
                ActionTransition actionTransition = new ActionTransition();
                if (actionTransition.load(elementx)) {
                    listx.add(actionTransition);
                }
            }
        });
    }

    private boolean load(Element element) {
        try {
            PZXmlUtil.forEachElement(element, elementx -> {
                try {
                    String string = elementx.getNodeName();
                    if ("transitionTo".equalsIgnoreCase(string)) {
                        this.transitionTo = elementx.getTextContent();
                    } else if ("transitionOut".equalsIgnoreCase(string)) {
                        this.transitionOut = StringUtils.tryParseBoolean(elementx.getTextContent());
                    } else if ("forceParent".equalsIgnoreCase(string)) {
                        this.forceParent = StringUtils.tryParseBoolean(elementx.getTextContent());
                    } else if ("asSubstate".equalsIgnoreCase(string)) {
                        this.asSubstate = StringUtils.tryParseBoolean(elementx.getTextContent());
                    } else if ("conditions".equalsIgnoreCase(string)) {
                        PZXmlUtil.forEachElement(elementx, elementxx -> {
                            IActionCondition iActionCondition = IActionCondition.createInstance(elementxx);
                            if (iActionCondition != null) {
                                this.conditions.add(iActionCondition);
                            }
                        });
                    }
                } catch (Exception exceptionx) {
                    DebugLog.ActionSystem.error("Error while parsing xml element: " + elementx.getNodeName());
                    DebugLog.ActionSystem.error(exceptionx);
                }
            });
            return true;
        } catch (Exception exception) {
            DebugLog.ActionSystem.error("Error while loading an ActionTransition element");
            DebugLog.ActionSystem.error(exception);
            return false;
        }
    }

    public String getTransitionTo() {
        return this.transitionTo;
    }

    public boolean passes(ActionContext actionContext, int int1) {
        for (int int0 = 0; int0 < this.conditions.size(); int0++) {
            IActionCondition iActionCondition0 = this.conditions.get(int0);
            if (!iActionCondition0.passes(actionContext, int1)) {
                return false;
            }
        }

        if (Core.bDebug
            && GameClient.bClient
            && (
                DebugOptions.instance.MultiplayerShowPlayerStatus.getValue()
                        && actionContext.getOwner() instanceof IsoPlayer
                        && !((IsoPlayer)actionContext.getOwner()).isGodMod()
                    || DebugOptions.instance.MultiplayerShowZombieStatus.getValue() && actionContext.getOwner() instanceof IsoZombie
            )) {
            StringBuilder stringBuilder = new StringBuilder("Character ")
                .append(actionContext.getOwner().getClass().getSimpleName())
                .append(" ")
                .append("id=")
                .append(actionContext.getOwner().getOnlineID())
                .append(" transition to \"")
                .append(this.transitionTo)
                .append("\":");

            for (IActionCondition iActionCondition1 : this.conditions) {
                stringBuilder.append(" [").append(iActionCondition1.getDescription()).append("]");
            }

            DebugLog.log(DebugType.ActionSystem, stringBuilder.toString());
        }

        return true;
    }

    public ActionTransition clone() {
        ActionTransition actionTransition0 = new ActionTransition();
        actionTransition0.transitionTo = this.transitionTo;
        actionTransition0.asSubstate = this.asSubstate;
        actionTransition0.transitionOut = this.transitionOut;
        actionTransition0.forceParent = this.forceParent;

        for (IActionCondition iActionCondition : this.conditions) {
            actionTransition0.conditions.add(iActionCondition.clone());
        }

        return actionTransition0;
    }
}
