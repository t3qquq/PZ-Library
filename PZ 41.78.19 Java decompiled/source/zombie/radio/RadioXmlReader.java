// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.globals.RadioGlobal;
import zombie.radio.globals.RadioGlobalBool;
import zombie.radio.globals.RadioGlobalFloat;
import zombie.radio.globals.RadioGlobalInt;
import zombie.radio.globals.RadioGlobalString;
import zombie.radio.globals.RadioGlobalType;
import zombie.radio.globals.RadioGlobalsManager;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioLine;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;

public final class RadioXmlReader {
    private boolean printDebug = false;
    private ArrayList<RadioGlobal> globalQueue;
    private ArrayList<RadioChannel> channelQueue;
    private Map<String, ArrayList<RadioBroadCast>> advertQue;
    private final String charsNormal = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final String charsEncrypt = "UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn";
    private String radioVersion = "1.0";
    private float version = 1.0F;
    private float formatVersion = 1.0F;
    private final Map<String, String> radioFileSettings = new HashMap<>();

    public RadioXmlReader() {
        this(false);
    }

    public RadioXmlReader(boolean boolean0) {
        this.printDebug = boolean0;
    }

    public static RadioData ReadFileHeader(String var0) {
        new RadioXmlReader(ZomboidRadio.DEBUG_XML);
        return null;
    }

    private void readfileheader(String string) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(string);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("RadioData");
        if (nodeList.getLength() > 0) {
            Node node0 = nodeList.item(0);
            Node node1 = null;

            for (Node node2 : this.getChildNodes(node0)) {
                if (this.nodeNameIs(node2, "RootInfo")) {
                    node1 = node2;
                    break;
                }
            }

            this.loadRootInfo(node1);
        }
    }

    public static boolean LoadFile(String string) {
        RadioXmlReader radioXmlReader = new RadioXmlReader(ZomboidRadio.DEBUG_XML);

        try {
            radioXmlReader.start(string);
        } catch (Exception exception) {
            DebugLog.log(DebugType.Radio, "Error loading radio system: " + exception.getMessage());
            exception.printStackTrace();
            boolean boolean0 = false;
        } finally {
            DebugLog.log(DebugType.Radio, "RadioSystem online.");
            return true;
        }
    }

    public static ArrayList<String> LoadTranslatorNames(String string) {
        ArrayList arrayList0 = new ArrayList();
        RadioXmlReader radioXmlReader = new RadioXmlReader(ZomboidRadio.DEBUG_XML);

        try {
            ArrayList arrayList1 = radioXmlReader.readTranslatorNames(string);
            arrayList0 = arrayList1;
        } catch (Exception exception) {
            DebugLog.log(DebugType.Radio, "Error reading translator names: " + exception.getMessage());
            exception.printStackTrace();
        } finally {
            DebugLog.log(DebugType.Radio, "Returning translator names.");
            return arrayList0;
        }
    }

    private void print(String string) {
        if (this.printDebug) {
            DebugLog.log(DebugType.Radio, string);
        }
    }

    private ArrayList<Node> getChildNodes(Node node0) {
        ArrayList arrayList = new ArrayList();
        if (node0.hasChildNodes()) {
            Node node1 = node0.getFirstChild();

            while (node1 != null) {
                if (!(node1 instanceof Element)) {
                    node1 = node1.getNextSibling();
                } else {
                    arrayList.add(node1);
                    node1 = node1.getNextSibling();
                }
            }
        }

        return arrayList;
    }

    private boolean nodeNameIs(Node node, String string) {
        return node.getNodeName().equals(string);
    }

    private String getAttrib(Node node, String string, boolean boolean0) {
        return this.getAttrib(node, string, boolean0, false);
    }

    private String getAttrib(Node node, String string) {
        return this.getAttrib(node, string, true, false).trim();
    }

    private String getAttrib(Node node, String string1, boolean boolean0, boolean boolean1) {
        String string0 = node.getAttributes().getNamedItem(string1).getTextContent();
        if (boolean0) {
            string0 = string0.trim();
        }

        if (boolean1) {
            string0 = string0.toLowerCase();
        }

        return string0;
    }

    private RadioGlobal getGlobalFromQueue(String string) {
        for (RadioGlobal radioGlobal : this.globalQueue) {
            if (radioGlobal != null && radioGlobal.getName().equals(string)) {
                return radioGlobal;
            }
        }

        return null;
    }

    private RadioGlobal createGlobal(String string0, String string1) {
        return this.createGlobal("", string0, string1);
    }

    private RadioGlobal createGlobal(String string2, String string1, String string0) {
        if (string2 != null && string1 != null && string0 != null) {
            RadioGlobalType radioGlobalType = RadioGlobalType.valueOf(string1.trim());
            switch (radioGlobalType) {
                case String:
                    return new RadioGlobalString(string2, string0);
                case Integer:
                    return new RadioGlobalInt(string2, Integer.parseInt(string0.trim()));
                case Float:
                    return new RadioGlobalFloat(string2, Float.parseFloat(string0.trim()));
                case Boolean:
                    return new RadioGlobalBool(string2, Boolean.parseBoolean(string0.trim().toLowerCase()));
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    private ArrayList<String> readTranslatorNames(String string0) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(string0);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();
        ArrayList arrayList = new ArrayList();
        NodeList nodeList = document.getElementsByTagName("TranslationData");
        if (nodeList.getLength() > 0) {
            Node node0 = nodeList.item(0);

            label37:
            for (Node node1 : this.getChildNodes(node0)) {
                if (this.nodeNameIs(node1, "RootInfo")) {
                    for (Node node2 : this.getChildNodes(node1)) {
                        if (this.nodeNameIs(node2, "Translators")) {
                            for (Node node3 : this.getChildNodes(node2)) {
                                String string1 = this.getAttrib(node3, "name", true, false);
                                if (string1 != null) {
                                    arrayList.add(string1);
                                }
                            }
                            break label37;
                        }
                    }
                    break;
                }
            }
        }

        return arrayList;
    }

    private void start(String string) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(string);
        this.print("RadioDataFile: " + file.getAbsolutePath());
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();
        this.globalQueue = new ArrayList<>();
        this.channelQueue = new ArrayList<>();
        this.advertQue = new HashMap<>();
        NodeList nodeList = document.getElementsByTagName("RadioData");
        if (nodeList.getLength() > 0) {
            Node node0 = nodeList.item(0);
            Node node1 = null;

            for (Node node2 : this.getChildNodes(node0)) {
                if (this.nodeNameIs(node2, "RootInfo")) {
                    node1 = node2;
                    break;
                }
            }

            this.loadRootInfo(node1);

            for (Node node3 : this.getChildNodes(node0)) {
                if (this.nodeNameIs(node3, "Globals")) {
                    this.loadGlobals(node3);
                } else if (this.nodeNameIs(node3, "Adverts")) {
                    this.loadAdverts(node3);
                } else if (this.nodeNameIs(node3, "Channels")) {
                    this.loadChannels(node3);
                }
            }
        }

        RadioGlobalsManager radioGlobalsManager = RadioGlobalsManager.getInstance();

        for (RadioGlobal radioGlobal : this.globalQueue) {
            radioGlobalsManager.addGlobal(radioGlobal.getName(), radioGlobal);
        }

        RadioScriptManager radioScriptManager = RadioScriptManager.getInstance();

        for (RadioChannel radioChannel : this.channelQueue) {
            radioScriptManager.AddChannel(radioChannel, false);
        }
    }

    private void loadRootInfo(Node node0) {
        this.print(">>> Loading root info...");
        if (node0 == null) {
            this.print(" -> root info not found, default version = " + this.radioVersion);
            this.radioFileSettings.put("Version", this.radioVersion);
        } else {
            this.print(" -> Reading RootInfo");

            for (Node node1 : this.getChildNodes(node0)) {
                String string0 = node1.getNodeName();
                String string1 = node1.getTextContent();
                if (string0 != null && string1 != null) {
                    this.print("   -> " + string0 + " = " + string1);
                    this.radioFileSettings.put(string0, string1);
                    if (string0.equals("Version")) {
                        this.radioVersion = string1;
                        this.version = Float.parseFloat(this.radioVersion);
                    }
                }
            }
        }
    }

    private void loadGlobals(Node node1) {
        this.print(">>> Loading globals...");

        for (Node node0 : this.getChildNodes(node1)) {
            if (this.nodeNameIs(node0, "GlobalEntry")) {
                String string0 = this.getAttrib(node0, "name");
                String string1 = this.getAttrib(node0, "type");
                String string2 = node0.getTextContent();
                this.print(" -> Found global, name: " + string0 + ", type: " + string1 + ", value: " + string2);
                RadioGlobal radioGlobal = this.createGlobal(string0, string1, string2);
                if (radioGlobal != null) {
                    this.globalQueue.add(radioGlobal);
                } else {
                    this.print(" -> Error adding Global, name: " + string0 + ", type: " + string1 + ", value: " + string2);
                }
            }
        }
    }

    private void loadAdverts(Node node1) {
        this.print(">>> Loading adverts...");

        for (Node node0 : this.getChildNodes(node1)) {
            if (this.nodeNameIs(node0, "AdvertCategory")) {
                String string = this.getAttrib(node0, "name");
                if (!this.advertQue.containsKey(string)) {
                    this.advertQue.put(string, new ArrayList<>());
                }

                this.print(" -> Found category: " + string);

                for (Node node2 : this.getChildNodes(node0)) {
                    RadioBroadCast radioBroadCast = this.loadBroadcast(node2, null);
                    this.advertQue.get(string).add(radioBroadCast);
                }
            }
        }
    }

    private void loadChannels(Node node1) {
        this.print(">>> Loading channels...");

        for (Node node0 : this.getChildNodes(node1)) {
            if (this.nodeNameIs(node0, "ChannelEntry")) {
                String string0 = this.getAttrib(node0, "name");
                String string1 = this.getAttrib(node0, "cat");
                String string2 = this.getAttrib(node0, "freq");
                String string3 = this.getAttrib(node0, "startscript");
                this.print(" -> Found channel: " + string0 + ", on freq: " + string2 + " , category: " + string1 + ", startscript: " + string3);
                RadioChannel radioChannel = new RadioChannel(string0, Integer.parseInt(string2), ChannelCategory.valueOf(string1));
                this.loadScripts(node0, radioChannel);
                radioChannel.setActiveScript(string3, 0);
                this.channelQueue.add(radioChannel);
            }
        }
    }

    private void loadScripts(Node node1, RadioChannel radioChannel) {
        this.print(" --> Loading scripts...");

        for (Node node0 : this.getChildNodes(node1)) {
            if (this.nodeNameIs(node0, "ScriptEntry")) {
                String string0 = this.getAttrib(node0, "name");
                String string1 = this.getAttrib(node0, "loopmin");
                String string2 = this.getAttrib(node0, "loopmin");
                this.print(" ---> Found script: " + string0);
                RadioScript radioScript = new RadioScript(string0, Integer.parseInt(string1), Integer.parseInt(string2));

                for (Node node2 : this.getChildNodes(node0)) {
                    if (this.nodeNameIs(node2, "BroadcastEntry")) {
                        this.loadBroadcast(node2, radioScript);
                    } else if (this.nodeNameIs(node2, "ExitOptions")) {
                        this.loadExitOptions(node2, radioScript);
                    }
                }

                radioChannel.AddRadioScript(radioScript);
            }
        }
    }

    private RadioBroadCast loadBroadcast(Node node0, RadioScript radioScript) {
        String string0 = this.getAttrib(node0, "ID");
        String string1 = this.getAttrib(node0, "timestamp");
        String string2 = this.getAttrib(node0, "endstamp");
        this.print(" ----> BroadCast, Timestamp: " + string1 + ", endstamp: " + string2);
        int int0 = Integer.parseInt(string1);
        int int1 = Integer.parseInt(string2);
        String string3 = this.getAttrib(node0, "preCat");
        int int2 = Integer.parseInt(this.getAttrib(node0, "preChance"));
        String string4 = this.getAttrib(node0, "postCat");
        int int3 = Integer.parseInt(this.getAttrib(node0, "postChance"));
        RadioBroadCast radioBroadCast = new RadioBroadCast(string0, int0, int1);
        if (!string3.equals("none") && this.advertQue.containsKey(string3)) {
            int int4 = Rand.Next(101);
            int int5 = this.advertQue.get(string3).size();
            if (int5 > 0 && int4 <= int2) {
                radioBroadCast.setPreSegment(this.advertQue.get(string3).get(Rand.Next(int5)));
            }
        }

        if (!string4.equals("none") && this.advertQue.containsKey(string4)) {
            int int6 = Rand.Next(101);
            int int7 = this.advertQue.get(string4).size();
            if (int7 > 0 && int6 <= int3) {
                radioBroadCast.setPostSegment(this.advertQue.get(string4).get(Rand.Next(int7)));
            }
        }

        for (Node node1 : this.getChildNodes(node0)) {
            if (this.nodeNameIs(node1, "LineEntry")) {
                String string5 = this.getAttrib(node1, "r");
                String string6 = this.getAttrib(node1, "g");
                String string7 = this.getAttrib(node1, "b");
                String string8 = null;
                String string9 = node1.getTextContent();
                this.print(" -----> New Line, Color: " + string5 + ", " + string6 + ", " + string7);

                for (Node node2 : this.getChildNodes(node1)) {
                    if (this.nodeNameIs(node2, "LineEffects")) {
                        string8 = "";

                        for (Node node3 : this.getChildNodes(node1)) {
                            if (this.nodeNameIs(node3, "Effect")) {
                                String string10 = this.getAttrib(node3, "tag");
                                String string11 = this.getAttrib(node3, "value");
                                string8 = string8 + string10 + "=" + string11 + ",";
                            }
                        }
                        break;
                    }
                }

                string9 = this.simpleDecrypt(string9);
                RadioLine radioLine = new RadioLine(
                    string9, Float.parseFloat(string5) / 255.0F, Float.parseFloat(string6) / 255.0F, Float.parseFloat(string7) / 255.0F, string8
                );
                radioBroadCast.AddRadioLine(radioLine);
            }
        }

        if (radioScript != null) {
            radioScript.AddBroadcast(radioBroadCast);
        }

        return radioBroadCast;
    }

    private String simpleDecrypt(String string1) {
        String string0 = "";

        for (int int0 = 0; int0 < string1.length(); int0++) {
            char char0 = string1.charAt(int0);
            if ("UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn".indexOf(char0) != -1) {
                string0 = string0
                    + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt("UVWKLMABCDEFGXYZHIJOPQRSTNuvwklmabcdefgxyzhijopqrstn".indexOf(char0));
            } else {
                string0 = string0 + char0;
            }
        }

        return string0;
    }

    private void loadExitOptions(Node node1, RadioScript radioScript) {
        for (Node node0 : this.getChildNodes(node1)) {
            if (this.nodeNameIs(node0, "ExitOption")) {
                String string0 = this.getAttrib(node0, "script");
                String string1 = this.getAttrib(node0, "chance");
                String string2 = this.getAttrib(node0, "delay");
                int int0 = Integer.parseInt(string1);
                int int1 = Integer.parseInt(string2);
                radioScript.AddExitOption(string0, int0, int1);
            }
        }
    }
}
