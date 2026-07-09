// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.ZomboidFileSystem;
import zombie.characters.HairOutfitDefinitions;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;

@XmlRootElement
public class HairStyles {
    @XmlElement(
        name = "male"
    )
    public final ArrayList<HairStyle> m_MaleStyles = new ArrayList<>();
    @XmlElement(
        name = "female"
    )
    public final ArrayList<HairStyle> m_FemaleStyles = new ArrayList<>();
    @XmlTransient
    public static HairStyles instance;

    public static void init() {
        instance = Parse(
            ZomboidFileSystem.instance.base.getAbsolutePath()
                + File.separator
                + ZomboidFileSystem.processFilePath("media/hairStyles/hairStyles.xml", File.separatorChar)
        );
        if (instance != null) {
            for (String string0 : ZomboidFileSystem.instance.getModIDs()) {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
                if (mod != null) {
                    String string1 = ZomboidFileSystem.instance.getModDir(string0);
                    HairStyles hairStyles = Parse(
                        string1 + File.separator + ZomboidFileSystem.processFilePath("media/hairStyles/hairStyles.xml", File.separatorChar)
                    );
                    if (hairStyles != null) {
                        for (HairStyle hairStyle0 : hairStyles.m_FemaleStyles) {
                            HairStyle hairStyle1 = instance.FindFemaleStyle(hairStyle0.name);
                            if (hairStyle1 == null) {
                                instance.m_FemaleStyles.add(hairStyle0);
                            } else {
                                if (DebugLog.isEnabled(DebugType.Clothing)) {
                                    DebugLog.Clothing.println("mod \"%s\" overrides hair \"%s\"", string0, hairStyle0.name);
                                }

                                int int0 = instance.m_FemaleStyles.indexOf(hairStyle1);
                                instance.m_FemaleStyles.set(int0, hairStyle0);
                            }
                        }

                        for (HairStyle hairStyle2 : hairStyles.m_MaleStyles) {
                            HairStyle hairStyle3 = instance.FindMaleStyle(hairStyle2.name);
                            if (hairStyle3 == null) {
                                instance.m_MaleStyles.add(hairStyle2);
                            } else {
                                if (DebugLog.isEnabled(DebugType.Clothing)) {
                                    DebugLog.Clothing.println("mod \"%s\" overrides hair \"%s\"", string0, hairStyle2.name);
                                }

                                int int1 = instance.m_MaleStyles.indexOf(hairStyle3);
                                instance.m_MaleStyles.set(int1, hairStyle2);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            instance.m_FemaleStyles.clear();
            instance.m_MaleStyles.clear();
            instance = null;
        }
    }

    public static HairStyles Parse(String filename) {
        try {
            return parse(filename);
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (IOException | JAXBException jAXBException) {
            ExceptionLogger.logException(jAXBException);
        }

        return null;
    }

    public static HairStyles parse(String filename) throws JAXBException, IOException {
        HairStyles hairStyles;
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            JAXBContext jAXBContext = JAXBContext.newInstance(HairStyles.class);
            Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
            hairStyles = (HairStyles)unmarshaller.unmarshal(fileInputStream);
        }

        return hairStyles;
    }

    public HairStyle FindMaleStyle(String name) {
        return this.FindStyle(this.m_MaleStyles, name);
    }

    public HairStyle FindFemaleStyle(String name) {
        return this.FindStyle(this.m_FemaleStyles, name);
    }

    private HairStyle FindStyle(ArrayList<HairStyle> arrayList, String string) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            HairStyle hairStyle = (HairStyle)arrayList.get(int0);
            if (hairStyle.name.equalsIgnoreCase(string)) {
                return hairStyle;
            }

            if ("".equals(string) && hairStyle.name.equalsIgnoreCase("bald")) {
                return hairStyle;
            }
        }

        return null;
    }

    public String getRandomMaleStyle(String outfitName) {
        return HairOutfitDefinitions.instance.getRandomHaircut(outfitName, this.m_MaleStyles);
    }

    public String getRandomFemaleStyle(String outfitName) {
        return HairOutfitDefinitions.instance.getRandomHaircut(outfitName, this.m_FemaleStyles);
    }

    public HairStyle getAlternateForHat(HairStyle style, String category) {
        if ("nohair".equalsIgnoreCase(category) || "nohairnobeard".equalsIgnoreCase(category)) {
            return null;
        } else if (this.m_FemaleStyles.contains(style)) {
            return this.FindFemaleStyle(style.getAlternate(category));
        } else {
            return this.m_MaleStyles.contains(style) ? this.FindMaleStyle(style.getAlternate(category)) : style;
        }
    }

    public ArrayList<HairStyle> getAllMaleStyles() {
        return this.m_MaleStyles;
    }

    public ArrayList<HairStyle> getAllFemaleStyles() {
        return this.m_FemaleStyles;
    }
}
