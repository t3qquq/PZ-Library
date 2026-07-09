// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;

@XmlRootElement
public class ClothingDecals {
    @XmlElement(
        name = "group"
    )
    public final ArrayList<ClothingDecalGroup> m_Groups = new ArrayList<>();
    @XmlTransient
    public static ClothingDecals instance;
    private final HashMap<String, ClothingDecals.CachedDecal> m_cachedDecals = new HashMap<>();

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException("ClothingDecals Already Initialized.");
        } else {
            instance = Parse(
                ZomboidFileSystem.instance.base.getAbsolutePath()
                    + File.separator
                    + ZomboidFileSystem.processFilePath("media/clothing/clothingDecals.xml", File.separatorChar)
            );
            if (instance != null) {
                for (String string0 : ZomboidFileSystem.instance.getModIDs()) {
                    ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
                    if (mod != null) {
                        String string1 = ZomboidFileSystem.instance.getModDir(string0);
                        ClothingDecals clothingDecals = Parse(
                            string1 + File.separator + ZomboidFileSystem.processFilePath("media/clothing/clothingDecals.xml", File.separatorChar)
                        );
                        if (clothingDecals != null) {
                            for (ClothingDecalGroup clothingDecalGroup0 : clothingDecals.m_Groups) {
                                ClothingDecalGroup clothingDecalGroup1 = instance.FindGroup(clothingDecalGroup0.m_Name);
                                if (clothingDecalGroup1 == null) {
                                    instance.m_Groups.add(clothingDecalGroup0);
                                } else {
                                    if (DebugLog.isEnabled(DebugType.Clothing)) {
                                        DebugLog.Clothing.println("mod \"%s\" overrides decal group \"%s\"", string0, clothingDecalGroup0.m_Name);
                                    }

                                    int int0 = instance.m_Groups.indexOf(clothingDecalGroup1);
                                    instance.m_Groups.set(int0, clothingDecalGroup0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            instance.m_cachedDecals.clear();
            instance.m_Groups.clear();
            instance = null;
        }
    }

    public static ClothingDecals Parse(String string) {
        try {
            return parse(string);
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (JAXBException | IOException iOException) {
            ExceptionLogger.logException(iOException);
        }

        return null;
    }

    public static ClothingDecals parse(String string) throws JAXBException, IOException {
        ClothingDecals clothingDecals;
        try (FileInputStream fileInputStream = new FileInputStream(string)) {
            JAXBContext jAXBContext = JAXBContext.newInstance(ClothingDecals.class);
            Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
            clothingDecals = (ClothingDecals)unmarshaller.unmarshal(fileInputStream);
        }

        return clothingDecals;
    }

    public ClothingDecal getDecal(String string0) {
        if (StringUtils.isNullOrWhitespace(string0)) {
            return null;
        } else {
            ClothingDecals.CachedDecal cachedDecal = this.m_cachedDecals.get(string0);
            if (cachedDecal == null) {
                cachedDecal = new ClothingDecals.CachedDecal();
                this.m_cachedDecals.put(string0, cachedDecal);
            }

            if (cachedDecal.m_decal != null) {
                return cachedDecal.m_decal;
            } else {
                String string1 = ZomboidFileSystem.instance.getString("media/clothing/clothingDecals/" + string0 + ".xml");

                try {
                    cachedDecal.m_decal = PZXmlUtil.parse(ClothingDecal.class, string1);
                    cachedDecal.m_decal.name = string0;
                } catch (PZXmlParserException pZXmlParserException) {
                    System.err.println("Failed to load ClothingDecal: " + string1);
                    ExceptionLogger.logException(pZXmlParserException);
                    return null;
                }

                return cachedDecal.m_decal;
            }
        }
    }

    public ClothingDecalGroup FindGroup(String string) {
        if (StringUtils.isNullOrWhitespace(string)) {
            return null;
        } else {
            for (int int0 = 0; int0 < this.m_Groups.size(); int0++) {
                ClothingDecalGroup clothingDecalGroup = this.m_Groups.get(int0);
                if (clothingDecalGroup.m_Name.equalsIgnoreCase(string)) {
                    return clothingDecalGroup;
                }
            }

            return null;
        }
    }

    public String getRandomDecal(String string) {
        ClothingDecalGroup clothingDecalGroup = this.FindGroup(string);
        return clothingDecalGroup == null ? null : clothingDecalGroup.getRandomDecal();
    }

    private static final class CachedDecal {
        ClothingDecal m_decal;
    }
}
