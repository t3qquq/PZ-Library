// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.action;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;

public final class ActionGroup {
    private static final Map<String, ActionGroup> actionGroupMap = new HashMap<>();
    String initialState;
    private List<ActionState> states = new ArrayList<>();
    private Map<String, ActionState> stateLookup;

    public static ActionGroup getActionGroup(String name) {
        name = name.toLowerCase();
        ActionGroup actionGroup = actionGroupMap.get(name);
        if (actionGroup == null && !actionGroupMap.containsKey(name)) {
            actionGroup = new ActionGroup();
            actionGroupMap.put(name, actionGroup);

            try {
                actionGroup.load(name);
            } catch (Exception exception) {
                DebugLog.ActionSystem.error("Error loading action group: " + name);
                exception.printStackTrace(DebugLog.ActionSystem);
            }

            return actionGroup;
        } else {
            return actionGroup;
        }
    }

    public static void reloadAll() {
        for (Entry entry : actionGroupMap.entrySet()) {
            ActionGroup actionGroup = (ActionGroup)entry.getValue();

            for (ActionState actionState : actionGroup.states) {
                actionState.resetForReload();
            }

            actionGroup.load((String)entry.getKey());
        }
    }

    void load(String string0) {
        if (DebugLog.isEnabled(DebugType.ActionSystem)) {
            DebugLog.ActionSystem.debugln("Loading ActionGroup: " + string0);
        }

        File file0 = ZomboidFileSystem.instance.getMediaFile("actiongroups/" + string0 + "/actionGroup.xml");
        if (file0.exists() && file0.canRead()) {
            this.loadGroupData(file0);
        }

        File file1 = ZomboidFileSystem.instance.getMediaFile("actiongroups/" + string0);
        File[] files = file1.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    String string1 = file2.getPath();
                    ActionState actionState = this.getOrCreate(file2.getName());
                    actionState.load(string1);
                }
            }
        }
    }

    private void loadGroupData(File file) {
        Document document;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);
        } catch (SAXException | IOException | ParserConfigurationException parserConfigurationException) {
            DebugLog.ActionSystem.error("Error loading: " + file.getPath());
            parserConfigurationException.printStackTrace(DebugLog.ActionSystem);
            return;
        }

        document.getDocumentElement().normalize();
        Element element0 = document.getDocumentElement();
        if (!element0.getNodeName().equals("actiongroup")) {
            DebugLog.ActionSystem
                .error("Error loading: " + file.getPath() + ", expected root element '<actiongroup>', received '<" + element0.getNodeName() + ">'");
        } else {
            for (Node node0 = element0.getFirstChild(); node0 != null; node0 = node0.getNextSibling()) {
                if (node0.getNodeName().equals("inherit") && node0 instanceof Element) {
                    String string0 = node0.getTextContent().trim();
                    this.inherit(getActionGroup(string0));
                }
            }

            for (Node node1 = element0.getFirstChild(); node1 != null; node1 = node1.getNextSibling()) {
                if (node1 instanceof Element element1) {
                    String string1 = element1.getNodeName();
                    switch (string1) {
                        case "initial":
                            this.initialState = element1.getTextContent().trim();
                        case "inherit":
                            break;
                        default:
                            DebugLog.ActionSystem.warn("Warning: Unknown element '<>' in '" + file.getPath() + "'");
                    }
                }
            }
        }
    }

    private void inherit(ActionGroup actionGroup0) {
        if (actionGroup0 != null) {
            if (actionGroup0.initialState != null) {
                this.initialState = actionGroup0.initialState;
            }

            for (ActionState actionState0 : actionGroup0.states) {
                ActionState actionState1 = this.getOrCreate(actionState0.name);

                for (ActionTransition actionTransition : actionState0.transitions) {
                    actionState1.transitions.add(actionTransition.clone());
                    actionState1.sortTransitions();
                }
            }
        }
    }

    private void rebuildLookup() {
        HashMap hashMap = new HashMap();

        for (ActionState actionState : this.states) {
            hashMap.put(actionState.name.toLowerCase(), actionState);
        }

        this.stateLookup = hashMap;
    }

    public void addState(ActionState state) {
        this.states.add(state);
        this.stateLookup = null;
    }

    public ActionState get(String state) {
        if (this.stateLookup == null) {
            this.rebuildLookup();
        }

        return this.stateLookup.get(state.toLowerCase());
    }

    ActionState getOrCreate(String string) {
        if (this.stateLookup == null) {
            this.rebuildLookup();
        }

        string = string.toLowerCase();
        ActionState actionState = this.stateLookup.get(string);
        if (actionState == null) {
            actionState = new ActionState(string);
            this.states.add(actionState);
            this.stateLookup.put(string, actionState);
        }

        return actionState;
    }

    public ActionState getInitialState() {
        ActionState actionState = null;
        if (this.initialState != null) {
            actionState = this.get(this.initialState);
        }

        if (actionState == null && this.states.size() > 0) {
            actionState = this.states.get(0);
        }

        return actionState;
    }

    public ActionState getDefaultState() {
        return this.getInitialState();
    }
}
