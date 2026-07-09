// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItems;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.ObservationFactory;
import zombie.core.Color;
import zombie.core.ImmutableColor;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoWorld;

public final class SurvivorDesc implements IHumanVisual {
    public final HumanVisual humanVisual = new HumanVisual(this);
    public final WornItems wornItems = new WornItems(BodyLocations.getGroup("Human"));
    SurvivorGroup group = new SurvivorGroup();
    private static int IDCount = 0;
    public static final ArrayList<Color> TrouserCommonColors = new ArrayList<>();
    public static final ArrayList<ImmutableColor> HairCommonColors = new ArrayList<>();
    private final HashMap<PerkFactory.Perk, Integer> xpBoostMap = new HashMap<>();
    private KahluaTable metaTable;
    public String Profession = "";
    protected String forename = "None";
    protected int ID = 0;
    protected IsoGameCharacter Instance = null;
    private boolean bFemale = true;
    protected String surname = "None";
    private String InventoryScript = null;
    protected String torso = "Base_Torso";
    protected final HashMap<Integer, Integer> MetCount = new HashMap<>();
    protected float bravery = 1.0F;
    protected float loner = 0.0F;
    protected float aggressiveness = 1.0F;
    protected float compassion = 1.0F;
    protected float temper = 0.0F;
    protected float friendliness = 0.0F;
    private float favourindoors = 0.0F;
    protected float loyalty = 0.0F;
    public final ArrayList<String> extra = new ArrayList<>();
    private final ArrayList<ObservationFactory.Observation> Observations = new ArrayList<>(0);
    private SurvivorFactory.SurvivorType type = SurvivorFactory.SurvivorType.Neutral;
    public boolean bDead;

    @Override
    public HumanVisual getHumanVisual() {
        return this.humanVisual;
    }

    @Override
    public void getItemVisuals(ItemVisuals itemVisuals) {
        this.wornItems.getItemVisuals(itemVisuals);
    }

    @Override
    public boolean isFemale() {
        return this.bFemale;
    }

    @Override
    public boolean isZombie() {
        return false;
    }

    @Override
    public boolean isSkeleton() {
        return false;
    }

    public WornItems getWornItems() {
        return this.wornItems;
    }

    public void setWornItem(String bodyLocation, InventoryItem item) {
        this.wornItems.setItem(bodyLocation, item);
    }

    public InventoryItem getWornItem(String bodyLocation) {
        return this.wornItems.getItem(bodyLocation);
    }

    public void dressInNamedOutfit(String outfitName) {
        ItemVisuals itemVisuals = new ItemVisuals();
        this.getHumanVisual().dressInNamedOutfit(outfitName, itemVisuals);
        this.getWornItems().setFromItemVisuals(itemVisuals);
    }

    public SurvivorGroup getGroup() {
        return this.group;
    }

    public boolean isLeader() {
        return this.group.getLeader() == this;
    }

    /**
     * @return the IDCount
     */
    public static int getIDCount() {
        return IDCount;
    }

    public void setProfessionSkills(ProfessionFactory.Profession profession) {
        this.getXPBoostMap().clear();
        this.getXPBoostMap().putAll(profession.XPBoostMap);
    }

    public HashMap<PerkFactory.Perk, Integer> getXPBoostMap() {
        return this.xpBoostMap;
    }

    public KahluaTable getMeta() {
        if (this.metaTable == null) {
            this.metaTable = (KahluaTable)LuaManager.caller.pcall(LuaManager.thread, LuaManager.env.rawget("createMetaSurvivor"), this)[1];
        }

        return this.metaTable;
    }

    public int getCalculatedToughness() {
        this.metaTable = this.getMeta();
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("MetaSurvivor");
        Double double0 = (Double)LuaManager.caller.pcall(LuaManager.thread, table.rawget("getCalculatedToughness"), this.metaTable)[1];
        return double0.intValue();
    }

    /**
     * 
     * @param aIDCount the IDCount to set
     */
    public static void setIDCount(int aIDCount) {
        IDCount = aIDCount;
    }

    public boolean isDead() {
        return this.bDead;
    }

    public SurvivorDesc() {
        this.ID = IDCount++;
        IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
        this.doStats();
    }

    public SurvivorDesc(boolean bNew) {
        this.ID = IDCount++;
        this.doStats();
    }

    public SurvivorDesc(SurvivorDesc other) {
        this.aggressiveness = other.aggressiveness;
        this.bDead = other.bDead;
        this.bFemale = other.bFemale;
        this.bravery = other.bravery;
        this.compassion = other.compassion;
        this.extra.addAll(other.extra);
        this.favourindoors = other.favourindoors;
        this.forename = other.forename;
        this.friendliness = other.friendliness;
        this.InventoryScript = other.InventoryScript;
        this.loner = other.loner;
        this.loyalty = other.loyalty;
        this.Profession = other.Profession;
        this.surname = other.surname;
        this.temper = other.temper;
        this.torso = other.torso;
        this.type = other.type;
    }

    public void meet(SurvivorDesc desc) {
        if (this.MetCount.containsKey(desc.ID)) {
            this.MetCount.put(desc.ID, this.MetCount.get(desc.ID) + 1);
        } else {
            this.MetCount.put(desc.ID, 1);
        }

        if (desc.MetCount.containsKey(this.ID)) {
            desc.MetCount.put(this.ID, desc.MetCount.get(this.ID) + 1);
        } else {
            desc.MetCount.put(this.ID, 1);
        }
    }

    public boolean hasObservation(String o) {
        for (int int0 = 0; int0 < this.Observations.size(); int0++) {
            if (o.equals(this.Observations.get(int0).getTraitID())) {
                return true;
            }
        }

        return false;
    }

    private void savePerk(ByteBuffer byteBuffer, PerkFactory.Perk perk) throws IOException {
        GameWindow.WriteStringUTF(byteBuffer, perk == null ? "" : perk.getId());
    }

    private PerkFactory.Perk loadPerk(ByteBuffer byteBuffer, int int0) throws IOException {
        if (int0 >= 152) {
            String string = GameWindow.ReadStringUTF(byteBuffer);
            PerkFactory.Perk perk0 = PerkFactory.Perks.FromString(string);
            return perk0 == PerkFactory.Perks.MAX ? null : perk0;
        } else {
            int int1 = byteBuffer.getInt();
            if (int1 >= 0 && int1 < PerkFactory.Perks.MAX.index()) {
                PerkFactory.Perk perk1 = PerkFactory.Perks.fromIndex(int1);
                return perk1 == PerkFactory.Perks.MAX ? null : perk1;
            } else {
                return null;
            }
        }
    }

    public void load(ByteBuffer input, int WorldVersion, IsoGameCharacter chr) throws IOException {
        this.ID = input.getInt();
        IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
        this.forename = GameWindow.ReadString(input);
        this.surname = GameWindow.ReadString(input);
        this.torso = GameWindow.ReadString(input);
        this.bFemale = input.getInt() == 1;
        this.Profession = GameWindow.ReadString(input);
        this.doStats();
        if (IDCount < this.ID) {
            IDCount = this.ID;
        }

        this.extra.clear();
        if (input.getInt() == 1) {
            int int0 = input.getInt();

            for (int int1 = 0; int1 < int0; int1++) {
                String string = GameWindow.ReadString(input);
                this.extra.add(string);
            }
        }

        int int2 = input.getInt();

        for (int int3 = 0; int3 < int2; int3++) {
            PerkFactory.Perk perk = this.loadPerk(input, WorldVersion);
            int int4 = input.getInt();
            if (perk != null) {
                this.getXPBoostMap().put(perk, int4);
            }
        }

        this.Instance = chr;
    }

    public void save(ByteBuffer output) throws IOException {
        output.putInt(this.ID);
        GameWindow.WriteString(output, this.forename);
        GameWindow.WriteString(output, this.surname);
        GameWindow.WriteString(output, this.torso);
        output.putInt(this.bFemale ? 1 : 0);
        GameWindow.WriteString(output, this.Profession);
        if (!this.extra.isEmpty()) {
            output.putInt(1);
            output.putInt(this.extra.size());

            for (int int0 = 0; int0 < this.extra.size(); int0++) {
                String string = this.extra.get(int0);
                GameWindow.WriteString(output, string);
            }
        } else {
            output.putInt(0);
        }

        output.putInt(this.getXPBoostMap().size());

        for (Entry entry : this.getXPBoostMap().entrySet()) {
            this.savePerk(output, (PerkFactory.Perk)entry.getKey());
            output.putInt((Integer)entry.getValue());
        }
    }

    public void loadCompact(ByteBuffer input) {
        this.ID = -1;
        this.torso = GameWindow.ReadString(input);
        this.bFemale = input.get() == 1;
        this.extra.clear();
        if (input.get() == 1) {
            byte byte0 = input.get();

            for (int int0 = 0; int0 < byte0; int0++) {
                String string = GameWindow.ReadString(input);
                this.extra.add(string);
            }
        }
    }

    public void saveCompact(ByteBuffer output) throws UnsupportedEncodingException {
        GameWindow.WriteString(output, this.torso);
        output.put((byte)(this.bFemale ? 1 : 0));
        if (!this.extra.isEmpty()) {
            output.put((byte)1);
            output.put((byte)this.extra.size());

            for (String string : this.extra) {
                GameWindow.WriteString(output, string);
            }
        } else {
            output.put((byte)0);
        }
    }

    public void addObservation(String obv) {
        ObservationFactory.Observation observation = ObservationFactory.getObservation(obv);
        if (observation != null) {
            this.Observations.add(observation);
        }
    }

    private void doStats() {
        this.bravery = Rand.Next(2) == 0 ? 10.0F : 0.0F;
        this.aggressiveness = Rand.Next(2) == 0 ? 10.0F : 0.0F;
        this.compassion = 10.0F - this.aggressiveness;
        this.loner = Rand.Next(2) == 0 ? 10.0F : 0.0F;
        this.temper = Rand.Next(2) == 0 ? 10.0F : 0.0F;
        this.friendliness = 10.0F - this.loner;
        this.favourindoors = Rand.Next(2) == 0 ? 10.0F : 0.0F;
        this.loyalty = Rand.Next(2) == 0 ? 10.0F : 0.0F;
    }

    public int getMetCount(SurvivorDesc descriptor) {
        return this.MetCount.containsKey(descriptor.ID) ? this.MetCount.get(descriptor.ID) : 0;
    }

    /**
     * @return the forename
     */
    public String getForename() {
        return this.forename;
    }

    /**
     * 
     * @param _forename the forename to set
     */
    public void setForename(String _forename) {
        this.forename = _forename;
    }

    /**
     * @return the ID
     */
    public int getID() {
        return this.ID;
    }

    /**
     * 
     * @param _ID the ID to set
     */
    public void setID(int _ID) {
        this.ID = _ID;
    }

    /**
     * @return the Instance
     */
    public IsoGameCharacter getInstance() {
        return this.Instance;
    }

    /**
     * 
     * @param _Instance the Instance to set
     */
    public void setInstance(IsoGameCharacter _Instance) {
        this.Instance = _Instance;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return this.surname;
    }

    /**
     * 
     * @param _surname the surname to set
     */
    public void setSurname(String _surname) {
        this.surname = _surname;
    }

    /**
     * @return the InventoryScript
     */
    public String getInventoryScript() {
        return this.InventoryScript;
    }

    /**
     * 
     * @param _InventoryScript the InventoryScript to set
     */
    public void setInventoryScript(String _InventoryScript) {
        this.InventoryScript = _InventoryScript;
    }

    /**
     * @return the torso
     */
    public String getTorso() {
        return this.torso;
    }

    /**
     * 
     * @param _torso the torso to set
     */
    public void setTorso(String _torso) {
        this.torso = _torso;
    }

    /**
     * @return the MetCount
     */
    public HashMap<Integer, Integer> getMetCount() {
        return this.MetCount;
    }

    /**
     * @return the bravery
     */
    public float getBravery() {
        return this.bravery;
    }

    /**
     * 
     * @param _bravery the bravery to set
     */
    public void setBravery(float _bravery) {
        this.bravery = _bravery;
    }

    /**
     * @return the loner
     */
    public float getLoner() {
        return this.loner;
    }

    /**
     * 
     * @param _loner the loner to set
     */
    public void setLoner(float _loner) {
        this.loner = _loner;
    }

    /**
     * @return the aggressiveness
     */
    public float getAggressiveness() {
        return this.aggressiveness;
    }

    /**
     * 
     * @param _aggressiveness the aggressiveness to set
     */
    public void setAggressiveness(float _aggressiveness) {
        this.aggressiveness = _aggressiveness;
    }

    /**
     * @return the compassion
     */
    public float getCompassion() {
        return this.compassion;
    }

    /**
     * 
     * @param _compassion the compassion to set
     */
    public void setCompassion(float _compassion) {
        this.compassion = _compassion;
    }

    /**
     * @return the temper
     */
    public float getTemper() {
        return this.temper;
    }

    /**
     * 
     * @param _temper the temper to set
     */
    public void setTemper(float _temper) {
        this.temper = _temper;
    }

    /**
     * @return the friendliness
     */
    public float getFriendliness() {
        return this.friendliness;
    }

    /**
     * 
     * @param _friendliness the friendliness to set
     */
    public void setFriendliness(float _friendliness) {
        this.friendliness = _friendliness;
    }

    /**
     * @return the favourindoors
     */
    public float getFavourindoors() {
        return this.favourindoors;
    }

    /**
     * 
     * @param _favourindoors the favourindoors to set
     */
    public void setFavourindoors(float _favourindoors) {
        this.favourindoors = _favourindoors;
    }

    /**
     * @return the loyalty
     */
    public float getLoyalty() {
        return this.loyalty;
    }

    /**
     * 
     * @param _loyalty the loyalty to set
     */
    public void setLoyalty(float _loyalty) {
        this.loyalty = _loyalty;
    }

    /**
     * @return the Profession
     */
    public String getProfession() {
        return this.Profession;
    }

    /**
     * 
     * @param _Profession the Profession to set
     */
    public void setProfession(String _Profession) {
        this.Profession = _Profession;
    }

    public boolean isAggressive() {
        for (ObservationFactory.Observation observation : this.Observations) {
            if ("Aggressive".equals(observation.getTraitID())) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<ObservationFactory.Observation> getObservations() {
        return this.Observations;
    }

    public boolean isFriendly() {
        for (ObservationFactory.Observation observation : this.Observations) {
            if ("Friendly".equals(observation.getTraitID())) {
                return true;
            }
        }

        return false;
    }

    public SurvivorFactory.SurvivorType getType() {
        return this.type;
    }

    public void setType(SurvivorFactory.SurvivorType _type) {
        this.type = _type;
    }

    public void setFemale(boolean _bFemale) {
        this.bFemale = _bFemale;
    }

    public ArrayList<String> getExtras() {
        return this.extra;
    }

    public ArrayList<ImmutableColor> getCommonHairColor() {
        return HairCommonColors;
    }

    public static void addTrouserColor(ColorInfo color) {
        TrouserCommonColors.add(color.toColor());
    }

    public static void addHairColor(ColorInfo color) {
        HairCommonColors.add(color.toImmutableColor());
    }

    public static Color getRandomSkinColor() {
        return OutfitRNG.Next(3) == 0
            ? new Color(OutfitRNG.Next(0.5F, 0.6F), OutfitRNG.Next(0.3F, 0.4F), OutfitRNG.Next(0.15F, 0.23F))
            : new Color(OutfitRNG.Next(0.9F, 1.0F), OutfitRNG.Next(0.75F, 0.88F), OutfitRNG.Next(0.45F, 0.58F));
    }
}
