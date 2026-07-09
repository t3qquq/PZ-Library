// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import zombie.GameWindow;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitCollection;
import zombie.characters.traits.TraitFactory;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;

public final class Literature extends InventoryItem {
    public boolean bAlreadyRead = false;
    public String requireInHandOrInventory = null;
    public String useOnConsume = null;
    private int numberOfPages = -1;
    private String bookName = "";
    private int LvlSkillTrained = -1;
    private int NumLevelsTrained;
    private String SkillTrained = "None";
    private int alreadyReadPages = 0;
    private boolean canBeWrite = false;
    private HashMap<Integer, String> customPages = null;
    private String lockedBy = null;
    private int pageToWrite;
    private List<String> teachedRecipes = null;
    private final int maxTextLength = 16384;

    public Literature(String module, String name, String itemType, String texName) {
        super(module, name, itemType, texName);
        this.setBookName(name);
        this.cat = ItemType.Literature;
        if (this.staticModel == null) {
            this.staticModel = "Book";
        }
    }

    public Literature(String module, String name, String itemType, Item item) {
        super(module, name, itemType, item);
        this.setBookName(name);
        this.cat = ItemType.Literature;
        if (this.staticModel == null) {
            this.staticModel = "Book";
        }
    }

    @Override
    public boolean IsLiterature() {
        return true;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Literature.ordinal();
    }

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "Literature";
    }

    @Override
    public void update() {
        if (this.container != null) {
        }
    }

    @Override
    public boolean finishupdate() {
        return true;
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
        if (this.getLvlSkillTrained() != -1) {
            ObjectTooltip.LayoutItem layoutItem0 = layout.addItem();
            layoutItem0.setLabel(Translator.getText("Tooltip_BookTitle") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem0.setValue(
                Translator.getText("Tooltip_BookTitle_" + this.getLvlSkillTrained(), Translator.getText("Tooltip_BookTitle_" + this.getSkillTrained())),
                1.0F,
                1.0F,
                1.0F,
                1.0F
            );
        }

        if (this.getBoredomChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem1 = layout.addItem();
            int int0 = (int)this.getBoredomChange();
            layoutItem1.setLabel(Translator.getText("Tooltip_literature_Boredom_Reduction") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem1.setValueRight(int0, false);
        }

        if (this.getStressChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem2 = layout.addItem();
            int int1 = (int)(this.getStressChange() * 100.0F);
            layoutItem2.setLabel(Translator.getText("Tooltip_literature_Stress_Reduction") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem2.setValueRight(int1, false);
        }

        if (this.getUnhappyChange() != 0.0F) {
            ObjectTooltip.LayoutItem layoutItem3 = layout.addItem();
            int int2 = (int)this.getUnhappyChange();
            layoutItem3.setLabel(Translator.getText("Tooltip_food_Unhappiness") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem3.setValueRight(int2, false);
        }

        if (this.getNumberOfPages() != -1) {
            ObjectTooltip.LayoutItem layoutItem4 = layout.addItem();
            int int3 = this.getAlreadyReadPages();
            if (tooltipUI.getCharacter() != null) {
                int3 = tooltipUI.getCharacter().getAlreadyReadPages(this.getFullType());
            }

            layoutItem4.setLabel(Translator.getText("Tooltip_literature_Number_of_Pages") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem4.setValue(int3 + " / " + this.getNumberOfPages(), 1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (this.getLvlSkillTrained() != -1) {
            ObjectTooltip.LayoutItem layoutItem5 = layout.addItem();
            String string0 = this.getLvlSkillTrained() + "";
            if (this.getLvlSkillTrained() != this.getMaxLevelTrained()) {
                string0 = string0 + "-" + this.getMaxLevelTrained();
            }

            layoutItem5.setLabel(Translator.getText("Tooltip_Literature_XpMultiplier", string0), 1.0F, 1.0F, 0.8F, 1.0F);
        }

        if (this.getTeachedRecipes() != null) {
            for (String string1 : this.getTeachedRecipes()) {
                ObjectTooltip.LayoutItem layoutItem6 = layout.addItem();
                String string2 = Translator.getRecipeName(string1);
                layoutItem6.setLabel(Translator.getText("Tooltip_Literature_TeachedRecipes", string2), 1.0F, 1.0F, 0.8F, 1.0F);
            }

            if (tooltipUI.getCharacter() != null) {
                ObjectTooltip.LayoutItem layoutItem7 = layout.addItem();
                String string3 = Translator.getText("Tooltip_literature_NotBeenRead");
                if (tooltipUI.getCharacter().getKnownRecipes().containsAll(this.getTeachedRecipes())) {
                    string3 = Translator.getText("Tooltip_literature_HasBeenRead");
                }

                layoutItem7.setLabel(string3, 1.0F, 1.0F, 0.8F, 1.0F);
                if (tooltipUI.getCharacter().getKnownRecipes().containsAll(this.getTeachedRecipes())) {
                    ProfessionFactory.Profession profession = ProfessionFactory.getProfession(tooltipUI.getCharacter().getDescriptor().getProfession());
                    TraitCollection traitCollection = tooltipUI.getCharacter().getTraits();
                    int int4 = 0;
                    int int5 = 0;

                    for (int int6 = 0; int6 < this.getTeachedRecipes().size(); int6++) {
                        String string4 = this.getTeachedRecipes().get(int6);
                        if (profession != null && profession.getFreeRecipes().contains(string4)) {
                            int4++;
                        }

                        for (int int7 = 0; int7 < traitCollection.size(); int7++) {
                            TraitFactory.Trait trait = TraitFactory.getTrait(traitCollection.get(int7));
                            if (trait != null && trait.getFreeRecipes().contains(string4)) {
                                int5++;
                            }
                        }
                    }

                    if (int4 > 0 || int5 > 0) {
                        layoutItem7 = layout.addItem();
                        layoutItem7.setLabel(Translator.getText("Tooltip_literature_AlreadyKnown"), 0.0F, 1.0F, 0.8F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, output);
        byte byte0 = 0;
        if (this.numberOfPages >= 127 && this.numberOfPages < 32767) {
            byte0 = 1;
        } else if (this.numberOfPages >= 32767) {
            byte0 = 2;
        }

        if (this.numberOfPages != -1) {
            bitHeaderWrite.addFlags(1);
            if (byte0 == 1) {
                bitHeaderWrite.addFlags(2);
                output.putShort((short)this.numberOfPages);
            } else if (byte0 == 2) {
                bitHeaderWrite.addFlags(4);
                output.putInt(this.numberOfPages);
            } else {
                output.put((byte)this.numberOfPages);
            }
        }

        if (this.alreadyReadPages != 0) {
            bitHeaderWrite.addFlags(8);
            if (byte0 == 1) {
                output.putShort((short)this.alreadyReadPages);
            } else if (byte0 == 2) {
                output.putInt(this.alreadyReadPages);
            } else {
                output.put((byte)this.alreadyReadPages);
            }
        }

        if (this.canBeWrite) {
            bitHeaderWrite.addFlags(16);
        }

        if (this.customPages != null && this.customPages.size() > 0) {
            bitHeaderWrite.addFlags(32);
            output.putInt(this.customPages.size());

            for (String string : this.customPages.values()) {
                GameWindow.WriteString(output, string);
            }
        }

        if (this.lockedBy != null) {
            bitHeaderWrite.addFlags(64);
            GameWindow.WriteString(output, this.getLockedBy());
        }

        bitHeaderWrite.write();
        bitHeaderWrite.release();
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        this.numberOfPages = -1;
        this.alreadyReadPages = 0;
        this.canBeWrite = false;
        this.customPages = null;
        this.lockedBy = null;
        BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, input);
        if (!bitHeaderRead.equals(0)) {
            byte byte0 = 0;
            if (bitHeaderRead.hasFlags(1)) {
                if (bitHeaderRead.hasFlags(2)) {
                    byte0 = 1;
                    this.numberOfPages = input.getShort();
                } else if (bitHeaderRead.hasFlags(4)) {
                    byte0 = 2;
                    this.numberOfPages = input.getInt();
                } else {
                    this.numberOfPages = input.get();
                }
            }

            if (bitHeaderRead.hasFlags(8)) {
                if (byte0 == 1) {
                    this.alreadyReadPages = input.getShort();
                } else if (byte0 == 2) {
                    this.alreadyReadPages = input.getInt();
                } else {
                    this.alreadyReadPages = input.get();
                }
            }

            this.canBeWrite = bitHeaderRead.hasFlags(16);
            if (bitHeaderRead.hasFlags(32)) {
                int int0 = input.getInt();
                if (int0 > 0) {
                    this.customPages = new HashMap<>();

                    for (int int1 = 0; int1 < int0; int1++) {
                        this.customPages.put(int1 + 1, GameWindow.ReadString(input));
                    }
                }
            }

            if (bitHeaderRead.hasFlags(64)) {
                this.setLockedBy(GameWindow.ReadString(input));
            }
        }

        bitHeaderRead.release();
    }

    /**
     * @return the boredomChange
     */
    @Override
    public float getBoredomChange() {
        return !this.bAlreadyRead ? this.boredomChange : 0.0F;
    }

    /**
     * @return the unhappyChange
     */
    @Override
    public float getUnhappyChange() {
        return !this.bAlreadyRead ? this.unhappyChange : 0.0F;
    }

    /**
     * @return the stressChange
     */
    @Override
    public float getStressChange() {
        return !this.bAlreadyRead ? this.stressChange : 0.0F;
    }

    public int getNumberOfPages() {
        return this.numberOfPages;
    }

    public void setNumberOfPages(int _numberOfPages) {
        this.numberOfPages = _numberOfPages;
    }

    public String getBookName() {
        return this.bookName;
    }

    public void setBookName(String _bookName) {
        this.bookName = _bookName;
    }

    public int getLvlSkillTrained() {
        return this.LvlSkillTrained;
    }

    public void setLvlSkillTrained(int lvlSkillTrained) {
        this.LvlSkillTrained = lvlSkillTrained;
    }

    public int getNumLevelsTrained() {
        return this.NumLevelsTrained;
    }

    public void setNumLevelsTrained(int numLevelsTrained) {
        this.NumLevelsTrained = numLevelsTrained;
    }

    public int getMaxLevelTrained() {
        return this.getLvlSkillTrained() + this.getNumLevelsTrained() - 1;
    }

    public String getSkillTrained() {
        return this.SkillTrained;
    }

    public void setSkillTrained(String skillTrained) {
        this.SkillTrained = skillTrained;
    }

    public int getAlreadyReadPages() {
        return this.alreadyReadPages;
    }

    public void setAlreadyReadPages(int _alreadyReadPages) {
        this.alreadyReadPages = _alreadyReadPages;
    }

    public boolean canBeWrite() {
        return this.canBeWrite;
    }

    public void setCanBeWrite(boolean _canBeWrite) {
        this.canBeWrite = _canBeWrite;
    }

    public HashMap<Integer, String> getCustomPages() {
        if (this.customPages == null) {
            this.customPages = new HashMap<>();
            this.customPages.put(1, "");
        }

        return this.customPages;
    }

    public void setCustomPages(HashMap<Integer, String> _customPages) {
        this.customPages = _customPages;
    }

    public void addPage(Integer index, String text) {
        if (text.length() > 16384) {
            text = text.substring(0, Math.min(text.length(), 16384));
        }

        if (this.customPages == null) {
            this.customPages = new HashMap<>();
        }

        this.customPages.put(index, text);
    }

    public String seePage(Integer index) {
        if (this.customPages == null) {
            this.customPages = new HashMap<>();
            this.customPages.put(1, "");
        }

        return this.customPages.get(index);
    }

    public String getLockedBy() {
        return this.lockedBy;
    }

    public void setLockedBy(String _lockedBy) {
        this.lockedBy = _lockedBy;
    }

    public int getPageToWrite() {
        return this.pageToWrite;
    }

    public void setPageToWrite(int _pageToWrite) {
        this.pageToWrite = _pageToWrite;
    }

    public List<String> getTeachedRecipes() {
        return this.teachedRecipes;
    }

    public void setTeachedRecipes(List<String> _teachedRecipes) {
        this.teachedRecipes = _teachedRecipes;
    }
}
