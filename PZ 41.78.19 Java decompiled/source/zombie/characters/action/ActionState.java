// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import org.w3c.dom.Element;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

public final class ActionState {
    public final String name;
    public final ArrayList<ActionTransition> transitions = new ArrayList<>();
    private String[] m_tags;
    private String[] m_childTags;
    private static final Comparator<ActionTransition> transitionComparator = (actionTransition0, actionTransition1) -> actionTransition1.conditions.size()
        - actionTransition0.conditions.size();

    public ActionState(String _name) {
        this.name = _name;
    }

    public final boolean canHaveSubStates() {
        return !PZArrayUtil.isNullOrEmpty(this.m_childTags);
    }

    public final boolean canBeSubstate() {
        return !PZArrayUtil.isNullOrEmpty(this.m_tags);
    }

    public final boolean canHaveSubState(ActionState child) {
        return canHaveSubState(this, child);
    }

    /**
     * Returns TRUE if the supplied child state can be a child of this state.  To determine this, the parent's childStateTags are compared to the child's parentStateTags.  If there is an overlap, the child is compatible with the parent.
     */
    public static boolean canHaveSubState(ActionState parent, ActionState child) {
        String[] strings0 = parent.m_childTags;
        String[] strings1 = child.m_tags;
        return tagsOverlap(strings0, strings1);
    }

    public static boolean tagsOverlap(String[] strings0, String[] strings1) {
        if (PZArrayUtil.isNullOrEmpty(strings0)) {
            return false;
        } else if (PZArrayUtil.isNullOrEmpty(strings1)) {
            return false;
        } else {
            boolean boolean0 = false;

            for (int int0 = 0; int0 < strings0.length; int0++) {
                String string0 = strings0[int0];

                for (int int1 = 0; int1 < strings1.length; int1++) {
                    String string1 = strings1[int1];
                    if (StringUtils.equalsIgnoreCase(string0, string1)) {
                        boolean0 = true;
                        break;
                    }
                }
            }

            return boolean0;
        }
    }

    public String getName() {
        return this.name;
    }

    public void load(String stateFolderPath) {
        File file0 = new File(stateFolderPath).getAbsoluteFile();
        File[] files = file0.listFiles((var0, string) -> string.toLowerCase().endsWith(".xml"));
        if (files != null) {
            for (File file1 : files) {
                this.parse(file1);
            }

            this.sortTransitions();
        }
    }

    public void parse(File file) {
        ArrayList arrayList0 = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        String string = file.getPath();

        try {
            Element element = PZXmlUtil.parseXml(string);
            if (ActionTransition.parse(element, string, arrayList0)) {
                this.transitions.addAll(arrayList0);
                if (DebugLog.isEnabled(DebugType.ActionSystem)) {
                    DebugLog.ActionSystem.debugln("Loaded transitions from file: %s", string);
                }

                return;
            }

            if (this.parseTags(element, arrayList1, arrayList2)) {
                this.m_tags = PZArrayUtil.concat(this.m_tags, arrayList1.toArray(new String[0]));
                this.m_childTags = PZArrayUtil.concat(this.m_childTags, arrayList2.toArray(new String[0]));
                if (DebugLog.isEnabled(DebugType.ActionSystem)) {
                    DebugLog.ActionSystem.debugln("Loaded tags from file: %s", string);
                }

                return;
            }

            if (DebugLog.isEnabled(DebugType.ActionSystem)) {
                DebugLog.ActionSystem.warn("Unrecognized xml file. It does not appear to be a transition nor a tag(s). %s", string);
            }
        } catch (Exception exception) {
            DebugLog.ActionSystem.error("Error loading: " + string);
            DebugLog.ActionSystem.error(exception);
        }
    }

    private boolean parseTags(Element element, ArrayList<String> arrayList0, ArrayList<String> arrayList1) {
        arrayList0.clear();
        arrayList1.clear();
        if (element.getNodeName().equals("tags")) {
            PZXmlUtil.forEachElement(element, elementx -> {
                if (elementx.getNodeName().equals("tag")) {
                    arrayList1.add(elementx.getTextContent());
                }
            });
            return true;
        } else if (element.getNodeName().equals("childTags")) {
            PZXmlUtil.forEachElement(element, elementx -> {
                if (elementx.getNodeName().equals("tag")) {
                    arrayList1.add(elementx.getTextContent());
                }
            });
            return true;
        } else {
            return false;
        }
    }

    public void sortTransitions() {
        this.transitions.sort(transitionComparator);
    }

    public void resetForReload() {
        this.transitions.clear();
        this.m_tags = null;
        this.m_childTags = null;
    }
}
