// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.properties.PropertyContainer;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteGrid;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;

/**
 * Turbo.
 */
public class Moveable extends InventoryItem {
    protected String worldSprite = "";
    private boolean isLight = false;
    private boolean lightUseBattery = false;
    private boolean lightHasBattery = false;
    private String lightBulbItem = "Base.LightBulb";
    private float lightPower = 0.0F;
    private float lightDelta = 2.5E-4F;
    private float lightR = 1.0F;
    private float lightG = 1.0F;
    private float lightB = 1.0F;
    private boolean isMultiGridAnchor = false;
    private IsoSpriteGrid spriteGrid;
    private String customNameFull = "Moveable Object";
    private String movableFullName = "Moveable Object";
    protected boolean canBeDroppedOnFloor = false;
    private boolean hasReadWorldSprite = false;
    protected String customItem = null;

    public Moveable(String module, String name, String type, String tex) {
        super(module, name, type, tex);
        this.cat = ItemType.Moveable;
    }

    public Moveable(String module, String name, String type, Item item) {
        super(module, name, type, item);
        this.cat = ItemType.Moveable;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        if ("Moveable Object".equals(this.movableFullName)) {
            return this.name;
        } else {
            return this.movableFullName.equals(this.name)
                ? Translator.getMoveableDisplayName(this.customNameFull)
                : Translator.getMoveableDisplayName(this.movableFullName) + this.customNameFull.substring(this.movableFullName.length());
        }
    }

    @Override
    public String getDisplayName() {
        return this.getName();
    }

    public boolean CanBeDroppedOnFloor() {
        if (this.worldSprite != null && this.spriteGrid != null) {
            IsoSprite sprite = IsoSpriteManager.instance.getSprite(this.worldSprite);
            PropertyContainer propertyContainer = sprite.getProperties();
            return this.canBeDroppedOnFloor || !propertyContainer.Is("ForceSingleItem");
        } else {
            return this.canBeDroppedOnFloor;
        }
    }

    public String getMovableFullName() {
        return this.movableFullName;
    }

    public String getCustomNameFull() {
        return this.customNameFull;
    }

    public boolean isMultiGridAnchor() {
        return this.isMultiGridAnchor;
    }

    public IsoSpriteGrid getSpriteGrid() {
        return this.spriteGrid;
    }

    public String getWorldSprite() {
        return this.worldSprite;
    }

    public boolean ReadFromWorldSprite(String sprite) {
        if (sprite == null) {
            return false;
        } else if (this.hasReadWorldSprite && this.worldSprite != null && this.worldSprite.equalsIgnoreCase(sprite)) {
            return true;
        } else {
            this.customItem = null;

            try {
                IsoSprite _sprite = IsoSpriteManager.instance.NamedMap.get(sprite);
                if (_sprite != null) {
                    PropertyContainer propertyContainer = _sprite.getProperties();
                    if (propertyContainer.Is("IsMoveAble")) {
                        if (propertyContainer.Is("CustomItem")) {
                            this.customItem = propertyContainer.Val("CustomItem");
                            Item item = ScriptManager.instance.FindItem(this.customItem);
                            if (item != null) {
                                this.Weight = this.ActualWeight = item.ActualWeight;
                            }

                            this.worldSprite = sprite;
                            if (_sprite.getSpriteGrid() != null) {
                                this.spriteGrid = _sprite.getSpriteGrid();
                                int int0 = _sprite.getSpriteGrid().getSpriteIndex(_sprite);
                                this.isMultiGridAnchor = int0 == 0;
                            }

                            return true;
                        }

                        this.isLight = propertyContainer.Is("lightR");
                        this.worldSprite = sprite;
                        float float0 = 1.0F;
                        if (propertyContainer.Is("PickUpWeight")) {
                            float0 = Float.parseFloat(propertyContainer.Val("PickUpWeight")) / 10.0F;
                        }

                        this.Weight = float0;
                        this.ActualWeight = float0;
                        this.setCustomWeight(true);
                        String string0 = "Moveable Object";
                        if (propertyContainer.Is("CustomName")) {
                            if (propertyContainer.Is("GroupName")) {
                                string0 = propertyContainer.Val("GroupName") + " " + propertyContainer.Val("CustomName");
                            } else {
                                string0 = propertyContainer.Val("CustomName");
                            }
                        }

                        this.movableFullName = string0;
                        this.name = string0;
                        this.customNameFull = string0;
                        if (_sprite.getSpriteGrid() != null) {
                            this.spriteGrid = _sprite.getSpriteGrid();
                            int int1 = _sprite.getSpriteGrid().getSpriteIndex(_sprite);
                            int int2 = _sprite.getSpriteGrid().getSpriteCount();
                            this.isMultiGridAnchor = int1 == 0;
                            if (!propertyContainer.Is("ForceSingleItem")) {
                                this.name = this.name + " (" + (int1 + 1) + "/" + int2 + ")";
                            } else {
                                this.name = this.name + " (1/1)";
                            }

                            this.customNameFull = this.name;
                            Texture texture0 = null;
                            String string1 = "Item_Flatpack";
                            if (string1 != null) {
                                texture0 = Texture.getSharedTexture(string1);
                                this.setColor(new Color(Rand.Next(0.7F, 1.0F), Rand.Next(0.7F, 1.0F), Rand.Next(0.7F, 1.0F)));
                            }

                            if (texture0 == null) {
                                texture0 = Texture.getSharedTexture("media/inventory/Question_On.png");
                            }

                            this.setTexture(texture0);
                            this.getModData().rawset("Flatpack", "true");
                        } else if (this.texture == null
                            || this.texture.getName() == null
                            || this.texture.getName().equals("Item_Moveable_object")
                            || this.texture.getName().equals("Question_On")) {
                            Texture texture1 = null;
                            Object object = null;
                            object = sprite;
                            if (sprite != null) {
                                texture1 = Texture.getSharedTexture(sprite);
                                if (texture1 != null) {
                                    texture1 = texture1.splitIcon();
                                }
                            }

                            if (texture1 == null) {
                                if (!propertyContainer.Is("MoveType")) {
                                    object = "Item_Moveable_object";
                                } else if (propertyContainer.Val("MoveType").equals("WallObject")) {
                                    object = "Item_Moveable_wallobject";
                                } else if (propertyContainer.Val("MoveType").equals("WindowObject")) {
                                    object = "Item_Moveable_windowobject";
                                } else if (propertyContainer.Val("MoveType").equals("Window")) {
                                    object = "Item_Moveable_window";
                                } else if (propertyContainer.Val("MoveType").equals("FloorTile")) {
                                    object = "Item_Moveable_floortile";
                                } else if (propertyContainer.Val("MoveType").equals("FloorRug")) {
                                    object = "Item_Moveable_floorrug";
                                } else if (propertyContainer.Val("MoveType").equals("Vegitation")) {
                                    object = "Item_Moveable_vegitation";
                                }

                                if (object != null) {
                                    texture1 = Texture.getSharedTexture((String)object);
                                }
                            }

                            if (texture1 == null) {
                                texture1 = Texture.getSharedTexture("media/inventory/Question_On.png");
                            }

                            this.setTexture(texture1);
                        }

                        this.hasReadWorldSprite = true;
                        return true;
                    }
                }
            } catch (Exception exception) {
                System.out.println("Error in Moveable item: " + exception.getMessage());
            }

            System.out.println("Warning: Moveable not valid");
            return false;
        }
    }

    public boolean isLight() {
        return this.isLight;
    }

    public void setLight(boolean _isLight) {
        this.isLight = _isLight;
    }

    public boolean isLightUseBattery() {
        return this.lightUseBattery;
    }

    public void setLightUseBattery(boolean _lightUseBattery) {
        this.lightUseBattery = _lightUseBattery;
    }

    public boolean isLightHasBattery() {
        return this.lightHasBattery;
    }

    public void setLightHasBattery(boolean _lightHasBattery) {
        this.lightHasBattery = _lightHasBattery;
    }

    public String getLightBulbItem() {
        return this.lightBulbItem;
    }

    public void setLightBulbItem(String _lightBulbItem) {
        this.lightBulbItem = _lightBulbItem;
    }

    public float getLightPower() {
        return this.lightPower;
    }

    public void setLightPower(float _lightPower) {
        this.lightPower = _lightPower;
    }

    public float getLightDelta() {
        return this.lightDelta;
    }

    public void setLightDelta(float _lightDelta) {
        this.lightDelta = _lightDelta;
    }

    public float getLightR() {
        return this.lightR;
    }

    public void setLightR(float _lightR) {
        this.lightR = _lightR;
    }

    public float getLightG() {
        return this.lightG;
    }

    public void setLightG(float _lightG) {
        this.lightG = _lightG;
    }

    public float getLightB() {
        return this.lightB;
    }

    public void setLightB(float _lightB) {
        this.lightB = _lightB;
    }

    @Override
    public int getSaveType() {
        return Item.Type.Moveable.ordinal();
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        GameWindow.WriteString(output, this.worldSprite);
        output.put((byte)(this.isLight ? 1 : 0));
        if (this.isLight) {
            output.put((byte)(this.lightUseBattery ? 1 : 0));
            output.put((byte)(this.lightHasBattery ? 1 : 0));
            output.put((byte)(this.lightBulbItem != null ? 1 : 0));
            if (this.lightBulbItem != null) {
                GameWindow.WriteString(output, this.lightBulbItem);
            }

            output.putFloat(this.lightPower);
            output.putFloat(this.lightDelta);
            output.putFloat(this.lightR);
            output.putFloat(this.lightG);
            output.putFloat(this.lightB);
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        this.worldSprite = GameWindow.ReadString(input);
        if (!this.ReadFromWorldSprite(this.worldSprite)
            && this instanceof Radio
            && this.getScriptItem() != null
            && !StringUtils.isNullOrWhitespace(this.getScriptItem().worldObjectSprite)) {
            DebugLog.log("Moveable.load -> Radio item = " + (this.fullType != null ? this.fullType : "unknown"));
        }

        if (this.customItem == null && !StringUtils.isNullOrWhitespace(this.worldSprite) && !this.type.equalsIgnoreCase(this.worldSprite)) {
            this.type = this.worldSprite;
            this.fullType = this.module + "." + this.worldSprite;
        }

        this.isLight = input.get() == 1;
        if (this.isLight) {
            this.lightUseBattery = input.get() == 1;
            this.lightHasBattery = input.get() == 1;
            if (input.get() == 1) {
                this.lightBulbItem = GameWindow.ReadString(input);
            }

            this.lightPower = input.getFloat();
            this.lightDelta = input.getFloat();
            this.lightR = input.getFloat();
            this.lightG = input.getFloat();
            this.lightB = input.getFloat();
        }
    }

    public void setWorldSprite(String WorldSprite) {
        this.worldSprite = WorldSprite;
    }
}
