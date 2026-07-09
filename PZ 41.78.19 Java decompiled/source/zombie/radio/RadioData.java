// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.Language;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioLine;
import zombie.radio.scripting.RadioScript;

/**
 * Turbo
 */
public final class RadioData {
    static boolean PRINTDEBUG = false;
    private boolean isVanilla = false;
    private String GUID;
    private int version;
    private String xmlFilePath;
    private final ArrayList<RadioChannel> radioChannels = new ArrayList<>();
    private final ArrayList<RadioTranslationData> translationDataList = new ArrayList<>();
    private RadioTranslationData currentTranslation;
    private Node rootNode;
    private final Map<String, RadioScript> advertQue = new HashMap<>();
    private static final String fieldStart = "\\$\\{t:";
    private static final String fieldEnd = "\\}";
    private static final String regex = "\\$\\{t:([^}]+)\\}";
    private static final Pattern pattern = Pattern.compile("\\$\\{t:([^}]+)\\}");

    public RadioData(String xmlFile) {
        this.xmlFilePath = xmlFile;
    }

    public ArrayList<RadioChannel> getRadioChannels() {
        return this.radioChannels;
    }

    public boolean isVanilla() {
        return this.isVanilla;
    }

    public static ArrayList<String> getTranslatorNames(Language language) {
        ArrayList arrayList = new ArrayList();
        if (language != Translator.getDefaultLanguage()) {
            for (RadioData radioData : fetchRadioData(false)) {
                for (RadioTranslationData radioTranslationData : radioData.translationDataList) {
                    if (radioTranslationData.getLanguageEnum() == language) {
                        for (String string : radioTranslationData.getTranslators()) {
                            if (!arrayList.contains(string)) {
                                arrayList.add(string);
                            }
                        }
                    }
                }
            }
        }

        return arrayList;
    }

    private static ArrayList<RadioData> fetchRadioData(boolean boolean0) {
        return fetchRadioData(boolean0, DebugLog.isEnabled(DebugType.Radio));
    }

    private static ArrayList<RadioData> fetchRadioData(boolean boolean1, boolean boolean0) {
        ArrayList arrayList0 = new ArrayList();

        try {
            ArrayList arrayList1 = ZomboidFileSystem.instance.getModIDs();
            if (boolean0) {
                System.out.println(":: Searching for radio data files:");
            }

            ArrayList arrayList2 = new ArrayList();
            searchForFiles(ZomboidFileSystem.instance.getMediaFile("radio"), "xml", arrayList2);
            ArrayList arrayList3 = new ArrayList(arrayList2);
            if (boolean1) {
                for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                    String string0 = ZomboidFileSystem.instance.getModDir((String)arrayList1.get(int0));
                    if (string0 != null) {
                        searchForFiles(new File(string0 + File.separator + "media" + File.separator + "radio"), "xml", arrayList2);
                    }
                }
            }

            for (String string1 : arrayList2) {
                RadioData radioData0 = ReadFile(string1);
                if (radioData0 != null) {
                    if (boolean0) {
                        System.out.println(" Found file: " + string1);
                    }

                    for (String string2 : arrayList3) {
                        if (string2.equals(string1)) {
                            radioData0.isVanilla = true;
                        }
                    }

                    arrayList0.add(radioData0);
                } else {
                    System.out.println("[Failure] Cannot parse file: " + string1);
                }
            }

            if (boolean0) {
                System.out.println(":: Searching for translation files:");
            }

            arrayList2.clear();
            searchForFiles(ZomboidFileSystem.instance.getMediaFile("radio"), "txt", arrayList2);
            if (boolean1) {
                for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                    String string3 = ZomboidFileSystem.instance.getModDir((String)arrayList1.get(int1));
                    if (string3 != null) {
                        searchForFiles(new File(string3 + File.separator + "media" + File.separator + "radio"), "txt", arrayList2);
                    }
                }
            }

            for (String string4 : arrayList2) {
                RadioTranslationData radioTranslationData = RadioTranslationData.ReadFile(string4);
                if (radioTranslationData != null) {
                    if (boolean0) {
                        System.out.println(" Found file: " + string4);
                    }

                    for (RadioData radioData1 : arrayList0) {
                        if (radioData1.GUID.equals(radioTranslationData.getGuid())) {
                            if (boolean0) {
                                System.out.println(" Adding translation: " + radioData1.GUID);
                            }

                            radioData1.translationDataList.add(radioTranslationData);
                        }
                    }
                } else if (boolean0) {
                    System.out.println("[Failure] " + string4);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return arrayList0;
    }

    public static ArrayList<RadioData> fetchAllRadioData() {
        boolean boolean0 = DebugLog.isEnabled(DebugType.Radio);
        ArrayList arrayList = fetchRadioData(true);

        for (int int0 = arrayList.size() - 1; int0 >= 0; int0--) {
            RadioData radioData = (RadioData)arrayList.get(int0);
            if (radioData.loadRadioScripts()) {
                if (boolean0) {
                    DebugLog.Radio.println(" Adding" + (radioData.isVanilla ? " (vanilla)" : "") + " file: " + radioData.xmlFilePath);
                    DebugLog.Radio.println(" - GUID: " + radioData.GUID);
                }

                radioData.currentTranslation = null;
                radioData.translationDataList.clear();
            } else {
                DebugLog.Radio.println("[Failure] Failed to load radio scripts for GUID: " + radioData.GUID);
                DebugLog.Radio.println("          File: " + radioData.xmlFilePath);
                arrayList.remove(int0);
            }
        }

        return arrayList;
    }

    private static void searchForFiles(File file, String string, ArrayList<String> arrayList) {
        if (file.isDirectory()) {
            String[] strings = file.list();

            for (int int0 = 0; int0 < strings.length; int0++) {
                searchForFiles(new File(file.getAbsolutePath() + File.separator + strings[int0]), string, arrayList);
            }
        } else if (file.getAbsolutePath().toLowerCase().contains(string)) {
            arrayList.add(file.getAbsolutePath());
        }
    }

    private static RadioData ReadFile(String string) {
        RadioData radioData = new RadioData(string);
        boolean boolean0 = false;

        try {
            if (DebugLog.isEnabled(DebugType.Radio)) {
                DebugLog.Radio.println("Reading xml: " + string);
            }

            File file = new File(string);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("RadioData");
            if (DebugLog.isEnabled(DebugType.Radio)) {
                DebugLog.Radio.println("RadioData nodes len: " + nodeList.getLength());
            }

            if (nodeList.getLength() > 0) {
                radioData.rootNode = nodeList.item(0);
                boolean0 = radioData.loadRootInfo();
                if (DebugLog.isEnabled(DebugType.Radio)) {
                    DebugLog.Radio.println("valid file: " + boolean0);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return boolean0 ? radioData : null;
    }

    private void print(String string) {
        if (PRINTDEBUG) {
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

    private String toLowerLocaleSafe(String string) {
        return string.toLowerCase(Locale.ENGLISH);
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
            string0 = this.toLowerLocaleSafe(string0);
        }

        return string0;
    }

    private boolean loadRootInfo() {
        boolean boolean0 = DebugLog.isEnabled(DebugType.Radio);
        if (boolean0) {
            DebugLog.Radio.println("Reading RootInfo...");
        }

        for (Node node0 : this.getChildNodes(this.rootNode)) {
            if (this.nodeNameIs(node0, "RootInfo")) {
                if (boolean0) {
                    DebugLog.Radio.println("RootInfo found");
                }

                for (Node node1 : this.getChildNodes(node0)) {
                    String string0 = node1.getNodeName();
                    String string1 = node1.getTextContent();
                    if (string0 != null && string1 != null) {
                        string0 = string0.trim();
                        if (boolean0) {
                            DebugLog.Radio.println("Found element: " + string0);
                        }

                        if (string0.equals("Version")) {
                            if (boolean0) {
                                DebugLog.Radio.println("Version = " + this.version);
                            }

                            this.version = Integer.parseInt(string1);
                        } else if (string0.equals("FileGUID")) {
                            if (boolean0) {
                                DebugLog.Radio.println("GUID = " + string1);
                            }

                            this.GUID = string1;
                        }
                    }
                }
            }
        }

        return this.GUID != null && this.version >= 0;
    }

    private boolean loadRadioScripts() {
        boolean boolean0 = false;
        this.currentTranslation = null;
        this.advertQue.clear();
        if (Core.getInstance().getContentTranslationsEnabled() && Translator.getLanguage() != Translator.getDefaultLanguage()) {
            System.out.println("Attempting to load translation: " + Translator.getLanguage().toString());

            for (RadioTranslationData radioTranslationData : this.translationDataList) {
                if (radioTranslationData.getLanguageEnum() == Translator.getLanguage()) {
                    System.out.println("Translation found!");
                    if (radioTranslationData.loadTranslations()) {
                        this.currentTranslation = radioTranslationData;
                        System.out.println("Count = " + this.currentTranslation.getTranslationCount());
                    } else {
                        System.out.println("Error loading translations for " + this.GUID);
                    }
                }
            }
        } else if (!Core.getInstance().getContentTranslationsEnabled()) {
            System.out.println("NOTE: Community Content Translations are disabled.");
        }

        for (Node node0 : this.getChildNodes(this.rootNode)) {
            if (this.nodeNameIs(node0, "Adverts")) {
                this.loadAdverts(node0);
            }
        }

        for (Node node1 : this.getChildNodes(this.rootNode)) {
            if (this.nodeNameIs(node1, "Channels")) {
                this.loadChannels(node1);
                boolean0 = true;
            }
        }

        return boolean0;
    }

    private void loadAdverts(Node node) {
        this.print(">>> Loading adverts...");
        ArrayList arrayList = new ArrayList();

        for (RadioScript radioScript : this.loadScripts(node, arrayList, true)) {
            if (!this.advertQue.containsKey(radioScript.GetName())) {
                this.advertQue.put(radioScript.GetGUID(), radioScript);
            }
        }
    }

    private void loadChannels(Node node1) {
        this.print(">>> Loading channels...");
        ArrayList arrayList = new ArrayList();

        for (Node node0 : this.getChildNodes(node1)) {
            if (this.nodeNameIs(node0, "ChannelEntry")) {
                String string0 = this.getAttrib(node0, "ID");
                String string1 = this.getAttrib(node0, "name");
                String string2 = this.getAttrib(node0, "cat");
                String string3 = this.getAttrib(node0, "freq");
                String string4 = this.getAttrib(node0, "startscript");
                this.print(
                    " -> Found channel: " + string1 + ", on freq: " + string3 + " , category: " + string2 + ", startscript: " + string4 + ", ID: " + string0
                );
                RadioChannel radioChannel = new RadioChannel(string1, Integer.parseInt(string3), ChannelCategory.valueOf(string2), string0);
                arrayList.clear();
                arrayList = this.loadScripts(node0, arrayList, false);

                for (RadioScript radioScript : arrayList) {
                    radioChannel.AddRadioScript(radioScript);
                }

                radioChannel.setActiveScript(string4, 0);
                this.radioChannels.add(radioChannel);
                radioChannel.setRadioData(this);
            }
        }
    }

    private ArrayList<RadioScript> loadScripts(Node node1, ArrayList<RadioScript> arrayList, boolean boolean0) {
        this.print(" --> Loading scripts...");

        for (Node node0 : this.getChildNodes(node1)) {
            if (this.nodeNameIs(node0, "ScriptEntry")) {
                String string0 = this.getAttrib(node0, "ID");
                String string1 = this.getAttrib(node0, "name");
                String string2 = this.getAttrib(node0, "loopmin");
                String string3 = this.getAttrib(node0, "loopmax");
                this.print(" ---> Found script: " + string1);
                RadioScript radioScript = new RadioScript(string1, Integer.parseInt(string2), Integer.parseInt(string3), string0);

                for (Node node2 : this.getChildNodes(node0)) {
                    if (this.nodeNameIs(node2, "BroadcastEntry")) {
                        this.loadBroadcast(node2, radioScript);
                    } else if (!boolean0 && this.nodeNameIs(node2, "ExitOptions")) {
                        this.loadExitOptions(node2, radioScript);
                    }
                }

                arrayList.add(radioScript);
            }
        }

        return arrayList;
    }

    private RadioBroadCast loadBroadcast(Node node0, RadioScript radioScript1) {
        String string0 = this.getAttrib(node0, "ID");
        String string1 = this.getAttrib(node0, "timestamp");
        String string2 = this.getAttrib(node0, "endstamp");
        this.print(" ----> BroadCast, Timestamp: " + string1 + ", endstamp: " + string2);
        int int0 = Integer.parseInt(string1);
        int int1 = Integer.parseInt(string2);
        String string3 = this.getAttrib(node0, "isSegment");
        boolean boolean0 = this.toLowerLocaleSafe(string3).equals("true");
        String string4 = this.getAttrib(node0, "advertCat");
        RadioBroadCast radioBroadCast = new RadioBroadCast(string0, int0, int1);
        if (!boolean0 && !this.toLowerLocaleSafe(string4).equals("none") && this.advertQue.containsKey(string4) && Rand.Next(101) < 75) {
            RadioScript radioScript0 = this.advertQue.get(string4);
            if (radioScript0.getBroadcastList().size() > 0) {
                if (Rand.Next(101) < 50) {
                    radioBroadCast.setPreSegment(radioScript0.getBroadcastList().get(Rand.Next(radioScript0.getBroadcastList().size())));
                } else {
                    radioBroadCast.setPostSegment(radioScript0.getBroadcastList().get(Rand.Next(radioScript0.getBroadcastList().size())));
                }
            }
        }

        for (Node node1 : this.getChildNodes(node0)) {
            if (this.nodeNameIs(node1, "LineEntry")) {
                String string5 = this.getAttrib(node1, "ID");
                String string6 = this.getAttrib(node1, "r");
                String string7 = this.getAttrib(node1, "g");
                String string8 = this.getAttrib(node1, "b");
                String string9 = null;
                if (node1.getAttributes().getNamedItem("codes") != null) {
                    string9 = this.getAttrib(node1, "codes");
                }

                String string10 = node1.getTextContent();
                this.print(" -----> New Line, Color: " + string6 + ", " + string7 + ", " + string8);
                string10 = this.checkForTranslation(string5, string10);
                RadioLine radioLine = new RadioLine(
                    string10, Float.parseFloat(string6) / 255.0F, Float.parseFloat(string7) / 255.0F, Float.parseFloat(string8) / 255.0F, string9
                );
                radioBroadCast.AddRadioLine(radioLine);
                string10 = string10.trim();
                if (string10.toLowerCase().startsWith("${t:")) {
                    string10 = this.checkForCustomAirTimer(string10, radioLine);
                    radioLine.setText(string10);
                }
            }
        }

        if (radioScript1 != null) {
            radioScript1.AddBroadcast(radioBroadCast, boolean0);
        }

        return radioBroadCast;
    }

    private String checkForTranslation(String string1, String string2) {
        if (this.currentTranslation != null) {
            String string0 = this.currentTranslation.getTranslation(string1);
            if (string0 != null) {
                return string0;
            }

            DebugLog.log(DebugType.Radio, "no translation for: " + string1);
        }

        return string2;
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

    private String checkForCustomAirTimer(String string0, RadioLine radioLine) {
        Matcher matcher = pattern.matcher(string0);
        String string1 = string0;
        float float0 = -1.0F;
        if (matcher.find()) {
            String string2 = matcher.group(1).toLowerCase().trim();

            try {
                float0 = Float.parseFloat(string2);
                radioLine.setAirTime(float0);
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            string1 = string0.replaceFirst("\\$\\{t:([^}]+)\\}", "");
        }

        return float0 >= 0.0F ? "[cdt=" + float0 + "]" + string1.trim() : string1.trim();
    }
}
