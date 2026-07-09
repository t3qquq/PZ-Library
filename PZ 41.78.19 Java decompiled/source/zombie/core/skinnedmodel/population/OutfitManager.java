// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.TreeMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.asset.AssetPath;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

@XmlRootElement
public class OutfitManager {
    public ArrayList<Outfit> m_MaleOutfits = new ArrayList<>();
    public ArrayList<Outfit> m_FemaleOutfits = new ArrayList<>();
    @XmlTransient
    public static OutfitManager instance;
    @XmlTransient
    private final Hashtable<String, OutfitManager.ClothingItemEntry> m_cachedClothingItems = new Hashtable<>();
    @XmlTransient
    private final ArrayList<IClothingItemListener> m_clothingItemListeners = new ArrayList<>();
    @XmlTransient
    private final TreeMap<String, Outfit> m_femaleOutfitMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    @XmlTransient
    private final TreeMap<String, Outfit> m_maleOutfitMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException("OutfitManager Already Initialized.");
        } else {
            instance = tryParse("game", "media/clothing/clothing.xml");
            if (instance != null) {
                instance.loaded();
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            instance.unload();
            instance = null;
        }
    }

    private void loaded() {
        for (String string : ZomboidFileSystem.instance.getModIDs()) {
            ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
            if (mod != null) {
                OutfitManager outfitManager0 = tryParse(string, "media/clothing/clothing.xml");
                if (outfitManager0 != null) {
                    for (Outfit outfit0 : outfitManager0.m_MaleOutfits) {
                        Outfit outfit1 = this.FindMaleOutfit(outfit0.m_Name);
                        if (outfit1 == null) {
                            this.m_MaleOutfits.add(outfit0);
                        } else {
                            if (DebugLog.isEnabled(DebugType.Clothing)) {
                                DebugLog.Clothing.println("mod \"%s\" overrides male outfit \"%s\"", string, outfit0.m_Name);
                            }

                            this.m_MaleOutfits.set(this.m_MaleOutfits.indexOf(outfit1), outfit0);
                        }

                        this.m_maleOutfitMap.put(outfit0.m_Name, outfit0);
                    }

                    for (Outfit outfit2 : outfitManager0.m_FemaleOutfits) {
                        Outfit outfit3 = this.FindFemaleOutfit(outfit2.m_Name);
                        if (outfit3 == null) {
                            this.m_FemaleOutfits.add(outfit2);
                        } else {
                            if (DebugLog.isEnabled(DebugType.Clothing)) {
                                DebugLog.Clothing.println("mod \"%s\" overrides female outfit \"%s\"", string, outfit2.m_Name);
                            }

                            this.m_FemaleOutfits.set(this.m_FemaleOutfits.indexOf(outfit3), outfit2);
                        }

                        this.m_femaleOutfitMap.put(outfit2.m_Name, outfit2);
                    }
                }
            }
        }

        DebugFileWatcher.instance
            .add(new PredicatedFileWatcher(ZomboidFileSystem.instance.getString("media/clothing/clothing.xml"), var0 -> onClothingXmlFileChanged()));
        this.loadAllClothingItems();

        for (Outfit outfit4 : this.m_MaleOutfits) {
            outfit4.m_Immutable = true;

            for (ClothingItemReference clothingItemReference0 : outfit4.m_items) {
                clothingItemReference0.m_Immutable = true;
            }
        }

        for (Outfit outfit5 : this.m_FemaleOutfits) {
            outfit5.m_Immutable = true;

            for (ClothingItemReference clothingItemReference1 : outfit5.m_items) {
                clothingItemReference1.m_Immutable = true;
            }
        }

        Collections.shuffle(this.m_MaleOutfits);
        Collections.shuffle(this.m_FemaleOutfits);
    }

    private static void onClothingXmlFileChanged() {
        DebugLog.Clothing.println("OutfitManager.onClothingXmlFileChanged> Detected change in media/clothing/clothing.xml");
        Reload();
    }

    public static void Reload() {
        DebugLog.Clothing.println("Reloading OutfitManager");
        OutfitManager outfitManager = instance;
        instance = tryParse("game", "media/clothing/clothing.xml");
        if (instance != null) {
            instance.loaded();
        }

        if (outfitManager != null && instance != null) {
            instance.onReloaded(outfitManager);
        }
    }

    private void onReloaded(OutfitManager outfitManager0) {
        PZArrayUtil.copy(this.m_clothingItemListeners, outfitManager0.m_clothingItemListeners);
        outfitManager0.unload();
        this.loadAllClothingItems();
    }

    private void unload() {
        for (OutfitManager.ClothingItemEntry clothingItemEntry : this.m_cachedClothingItems.values()) {
            DebugFileWatcher.instance.remove(clothingItemEntry.m_fileWatcher);
        }

        this.m_cachedClothingItems.clear();
        this.m_clothingItemListeners.clear();
    }

    public void addClothingItemListener(IClothingItemListener iClothingItemListener) {
        if (iClothingItemListener != null) {
            if (!this.m_clothingItemListeners.contains(iClothingItemListener)) {
                this.m_clothingItemListeners.add(iClothingItemListener);
            }
        }
    }

    public void removeClothingItemListener(IClothingItemListener iClothingItemListener) {
        this.m_clothingItemListeners.remove(iClothingItemListener);
    }

    private void invokeClothingItemChangedEvent(String string) {
        for (IClothingItemListener iClothingItemListener : this.m_clothingItemListeners) {
            iClothingItemListener.clothingItemChanged(string);
        }
    }

    public Outfit GetRandomOutfit(boolean boolean0) {
        Outfit outfit;
        if (boolean0) {
            outfit = PZArrayUtil.pickRandom(this.m_FemaleOutfits);
        } else {
            outfit = PZArrayUtil.pickRandom(this.m_MaleOutfits);
        }

        return outfit;
    }

    public Outfit GetRandomNonProfessionalOutfit(boolean boolean0) {
        String string = "Generic0" + (Rand.Next(5) + 1);
        if (Rand.NextBool(4)) {
            if (boolean0) {
                int int0 = Rand.Next(3);
                switch (int0) {
                    case 0:
                        string = "Mannequin1";
                        break;
                    case 1:
                        string = "Mannequin2";
                        break;
                    case 2:
                        string = "Classy";
                }
            } else {
                int int1 = Rand.Next(3);
                switch (int1) {
                    case 0:
                        string = "Classy";
                        break;
                    case 1:
                        string = "Tourist";
                        break;
                    case 2:
                        string = "MallSecurity";
                }
            }
        }

        return this.GetSpecificOutfit(boolean0, string);
    }

    public Outfit GetSpecificOutfit(boolean boolean0, String string) {
        Outfit outfit;
        if (boolean0) {
            outfit = this.FindFemaleOutfit(string);
        } else {
            outfit = this.FindMaleOutfit(string);
        }

        return outfit;
    }

    private static OutfitManager tryParse(String string0, String string1) {
        try {
            return parse(string0, string1);
        } catch (PZXmlParserException pZXmlParserException) {
            pZXmlParserException.printStackTrace();
            return null;
        }
    }

    private static OutfitManager parse(String string0, String string1) throws PZXmlParserException {
        if ("game".equals(string0)) {
            string1 = ZomboidFileSystem.instance.base.getAbsolutePath() + File.separator + ZomboidFileSystem.processFilePath(string1, File.separatorChar);
        } else {
            String string2 = ZomboidFileSystem.instance.getModDir(string0);
            string1 = string2 + File.separator + ZomboidFileSystem.processFilePath(string1, File.separatorChar);
        }

        if (!new File(string1).exists()) {
            return null;
        } else {
            OutfitManager outfitManager = PZXmlUtil.parse(OutfitManager.class, string1);
            if (outfitManager != null) {
                PZArrayUtil.forEach(outfitManager.m_MaleOutfits, outfit -> outfit.setModID(string0));
                PZArrayUtil.forEach(outfitManager.m_FemaleOutfits, outfit -> outfit.setModID(string0));
                PZArrayUtil.forEach(outfitManager.m_MaleOutfits, outfit -> outfitManager.m_maleOutfitMap.put(outfit.m_Name, outfit));
                PZArrayUtil.forEach(outfitManager.m_FemaleOutfits, outfit -> outfitManager.m_femaleOutfitMap.put(outfit.m_Name, outfit));
            }

            return outfitManager;
        }
    }

    private static void tryWrite(OutfitManager outfitManager, String string) {
        try {
            write(outfitManager, string);
        } catch (IOException | JAXBException jAXBException) {
            jAXBException.printStackTrace();
        }
    }

    private static void write(OutfitManager outfitManager, String string) throws IOException, JAXBException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(string)) {
            JAXBContext jAXBContext = JAXBContext.newInstance(OutfitManager.class);
            Marshaller marshaller = jAXBContext.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
            marshaller.marshal(outfitManager, fileOutputStream);
        }
    }

    public Outfit FindMaleOutfit(String string) {
        return this.m_maleOutfitMap.get(string);
    }

    public Outfit FindFemaleOutfit(String string) {
        return this.m_femaleOutfitMap.get(string);
    }

    private Outfit FindOutfit(ArrayList<Outfit> arrayList, String string) {
        Outfit outfit0 = null;

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            Outfit outfit1 = (Outfit)arrayList.get(int0);
            if (outfit1.m_Name.equalsIgnoreCase(string)) {
                outfit0 = outfit1;
                break;
            }
        }

        return outfit0;
    }

    public ClothingItem getClothingItem(String string1) {
        String string0 = ZomboidFileSystem.instance.getFilePathFromGuid(string1);
        if (string0 == null) {
            return null;
        } else {
            OutfitManager.ClothingItemEntry clothingItemEntry0 = this.m_cachedClothingItems.get(string1);
            if (clothingItemEntry0 == null) {
                clothingItemEntry0 = new OutfitManager.ClothingItemEntry();
                clothingItemEntry0.m_filePath = string0;
                clothingItemEntry0.m_guid = string1;
                clothingItemEntry0.m_item = null;
                this.m_cachedClothingItems.put(string1, clothingItemEntry0);
            }

            if (clothingItemEntry0.m_item != null) {
                clothingItemEntry0.m_item.m_GUID = string1;
                return clothingItemEntry0.m_item;
            } else {
                try {
                    String string2 = ZomboidFileSystem.instance.resolveFileOrGUID(string0);
                    clothingItemEntry0.m_item = (ClothingItem)ClothingItemAssetManager.instance.load(new AssetPath(string2));
                    clothingItemEntry0.m_item.m_Name = this.extractClothingItemName(string0);
                    clothingItemEntry0.m_item.m_GUID = string1;
                } catch (Exception exception) {
                    System.err.println("Failed to load ClothingItem: " + string0);
                    ExceptionLogger.logException(exception);
                    return null;
                }

                if (clothingItemEntry0.m_fileWatcher == null) {
                    OutfitManager.ClothingItemEntry clothingItemEntry1 = clothingItemEntry0;
                    String string3 = clothingItemEntry1.m_filePath;
                    string3 = ZomboidFileSystem.instance.getString(string3);
                    clothingItemEntry0.m_fileWatcher = new PredicatedFileWatcher(string3, var2x -> this.onClothingItemFileChanged(clothingItemEntry1));
                    DebugFileWatcher.instance.add(clothingItemEntry0.m_fileWatcher);
                }

                return clothingItemEntry0.m_item;
            }
        }
    }

    private String extractClothingItemName(String string1) {
        String string0 = StringUtils.trimPrefix(string1, "media/clothing/clothingItems/");
        return StringUtils.trimSuffix(string0, ".xml");
    }

    private void onClothingItemFileChanged(OutfitManager.ClothingItemEntry clothingItemEntry) {
        ClothingItemAssetManager.instance.reload(clothingItemEntry.m_item);
    }

    public void onClothingItemStateChanged(ClothingItem clothingItem) {
        if (clothingItem.isReady()) {
            this.invokeClothingItemChangedEvent(clothingItem.m_GUID);
        }
    }

    public void loadAllClothingItems() {
        ArrayList arrayList = ScriptManager.instance.getAllItems();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            Item item = (Item)arrayList.get(int0);
            if (item.replacePrimaryHand != null) {
                String string0 = ZomboidFileSystem.instance
                    .getGuidFromFilePath("media/clothing/clothingItems/" + item.replacePrimaryHand.clothingItemName + ".xml");
                if (string0 != null) {
                    item.replacePrimaryHand.clothingItem = this.getClothingItem(string0);
                }
            }

            if (item.replaceSecondHand != null) {
                String string1 = ZomboidFileSystem.instance
                    .getGuidFromFilePath("media/clothing/clothingItems/" + item.replaceSecondHand.clothingItemName + ".xml");
                if (string1 != null) {
                    item.replaceSecondHand.clothingItem = this.getClothingItem(string1);
                }
            }

            if (!StringUtils.isNullOrWhitespace(item.getClothingItem())) {
                String string2 = ZomboidFileSystem.instance.getGuidFromFilePath("media/clothing/clothingItems/" + item.getClothingItem() + ".xml");
                if (string2 != null) {
                    ClothingItem clothingItem = this.getClothingItem(string2);
                    item.setClothingItemAsset(clothingItem);
                }
            }
        }
    }

    public boolean isLoadingClothingItems() {
        for (OutfitManager.ClothingItemEntry clothingItemEntry : this.m_cachedClothingItems.values()) {
            if (clothingItemEntry.m_item.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public void debugOutfits() {
        this.debugOutfits(this.m_FemaleOutfits);
        this.debugOutfits(this.m_MaleOutfits);
    }

    private void debugOutfits(ArrayList<Outfit> arrayList) {
        for (Outfit outfit : arrayList) {
            this.debugOutfit(outfit);
        }
    }

    private void debugOutfit(Outfit outfit) {
        String string0 = null;

        for (ClothingItemReference clothingItemReference : outfit.m_items) {
            ClothingItem clothingItem = this.getClothingItem(clothingItemReference.itemGUID);
            if (clothingItem != null && !clothingItem.isEmpty()) {
                String string1 = ScriptManager.instance.getItemTypeForClothingItem(clothingItem.m_Name);
                if (string1 != null) {
                    Item item = ScriptManager.instance.getItem(string1);
                    if (item != null && item.getType() == Item.Type.Container) {
                        String string2 = StringUtils.isNullOrWhitespace(item.getBodyLocation()) ? item.CanBeEquipped : item.getBodyLocation();
                        if (string0 != null && string0.equals(string2)) {
                            DebugLog.Clothing.warn("outfit \"%s\" has multiple bags", outfit.m_Name);
                        }

                        string0 = string2;
                    }
                }
            }
        }
    }

    private static final class ClothingItemEntry {
        public ClothingItem m_item;
        public String m_guid;
        public String m_filePath;
        public PredicatedFileWatcher m_fileWatcher;
    }
}
