// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.chat.ChatElement;
import zombie.chat.ChatElementOwner;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Drainable;
import zombie.iso.IsoGridSquare;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.WaveSignalDevice;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.UIFont;

public final class VehiclePart implements ChatElementOwner, WaveSignalDevice {
    protected BaseVehicle vehicle;
    protected boolean bCreated;
    protected String partId;
    protected VehicleScript.Part scriptPart;
    protected ItemContainer container;
    protected InventoryItem item;
    protected KahluaTable modData;
    protected float lastUpdated = -1.0F;
    protected short updateFlags;
    protected VehiclePart parent;
    protected VehicleDoor door;
    protected VehicleWindow window;
    protected ArrayList<VehiclePart> children;
    protected String category;
    protected int condition = -1;
    protected boolean specificItem = true;
    protected float wheelFriction = 0.0F;
    protected int mechanicSkillInstaller = 0;
    private float suspensionDamping = 0.0F;
    private float suspensionCompression = 0.0F;
    private float engineLoudness = 0.0F;
    protected VehicleLight light;
    protected DeviceData deviceData;
    protected ChatElement chatElement;
    protected boolean hasPlayerInRange;

    public VehiclePart(BaseVehicle _vehicle) {
        this.vehicle = _vehicle;
    }

    public BaseVehicle getVehicle() {
        return this.vehicle;
    }

    public void setScriptPart(VehicleScript.Part _scriptPart) {
        this.scriptPart = _scriptPart;
    }

    public VehicleScript.Part getScriptPart() {
        return this.scriptPart;
    }

    public ItemContainer getItemContainer() {
        return this.container;
    }

    public void setItemContainer(ItemContainer _container) {
        if (_container != null) {
            _container.parent = this.getVehicle();
            _container.vehiclePart = this;
        }

        this.container = _container;
    }

    public boolean hasModData() {
        return this.modData != null && !this.modData.isEmpty();
    }

    public KahluaTable getModData() {
        if (this.modData == null) {
            this.modData = LuaManager.platform.newTable();
        }

        return this.modData;
    }

    public float getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(float hours) {
        this.lastUpdated = hours;
    }

    public String getId() {
        return this.scriptPart == null ? this.partId : this.scriptPart.id;
    }

    public int getIndex() {
        return this.vehicle.parts.indexOf(this);
    }

    public String getArea() {
        return this.scriptPart == null ? null : this.scriptPart.area;
    }

    public ArrayList<String> getItemType() {
        return this.scriptPart == null ? null : this.scriptPart.itemType;
    }

    public KahluaTable getTable(String id) {
        if (this.scriptPart != null && this.scriptPart.tables != null) {
            KahluaTable table = this.scriptPart.tables.get(id);
            return table == null ? null : LuaManager.copyTable(table);
        } else {
            return null;
        }
    }

    public InventoryItem getInventoryItem() {
        return this.item;
    }

    public void setInventoryItem(InventoryItem _item, int mechanicSkill) {
        this.item = _item;
        this.doInventoryItemStats(_item, mechanicSkill);
        this.getVehicle().updateTotalMass();
        this.getVehicle().bDoDamageOverlay = true;
        if (this.isSetAllModelsVisible()) {
            this.setAllModelsVisible(_item != null);
        }

        this.getVehicle().updatePartStats();
        if (!GameServer.bServer) {
            this.getVehicle().updateBulletStats();
        }
    }

    public void setInventoryItem(InventoryItem _item) {
        this.setInventoryItem(_item, 0);
    }

    public boolean isInventoryItemUninstalled() {
        return this.getItemType() != null && !this.getItemType().isEmpty() && this.getInventoryItem() == null;
    }

    public boolean isSetAllModelsVisible() {
        return this.scriptPart != null && this.scriptPart.bSetAllModelsVisible;
    }

    public void setAllModelsVisible(boolean visible) {
        if (this.scriptPart != null && this.scriptPart.models != null && !this.scriptPart.models.isEmpty()) {
            for (int int0 = 0; int0 < this.scriptPart.models.size(); int0++) {
                VehicleScript.Model model = this.scriptPart.models.get(int0);
                this.vehicle.setModelVisible(this, model, visible);
            }
        }
    }

    public void doInventoryItemStats(InventoryItem newItem, int mechanicSkill) {
        if (newItem != null) {
            if (this.isContainer()) {
                if (newItem.getMaxCapacity() > 0 && this.getScriptPart().container.conditionAffectsCapacity) {
                    this.setContainerCapacity((int)getNumberByCondition(newItem.getMaxCapacity(), newItem.getCondition(), 5.0F));
                } else if (newItem.getMaxCapacity() > 0) {
                    this.setContainerCapacity(newItem.getMaxCapacity());
                }

                this.setContainerContentAmount(newItem.getItemCapacity());
            }

            this.setSuspensionCompression(getNumberByCondition(newItem.getSuspensionCompression(), newItem.getCondition(), 0.6F));
            this.setSuspensionDamping(getNumberByCondition(newItem.getSuspensionDamping(), newItem.getCondition(), 0.6F));
            if (newItem.getEngineLoudness() > 0.0F) {
                this.setEngineLoudness(getNumberByCondition(newItem.getEngineLoudness(), newItem.getCondition(), 10.0F));
            }

            this.setCondition(newItem.getCondition());
            this.setMechanicSkillInstaller(mechanicSkill);
        } else {
            if (this.scriptPart != null && this.scriptPart.container != null) {
                if (this.scriptPart.container.capacity > 0) {
                    this.setContainerCapacity(this.scriptPart.container.capacity);
                } else {
                    this.setContainerCapacity(0);
                }
            }

            this.setMechanicSkillInstaller(0);
            this.setContainerContentAmount(0.0F);
            this.setSuspensionCompression(0.0F);
            this.setSuspensionDamping(0.0F);
            this.setWheelFriction(0.0F);
            this.setEngineLoudness(0.0F);
        }
    }

    public void setRandomCondition(InventoryItem _item) {
        VehicleType vehicleType = VehicleType.getTypeFromName(this.getVehicle().getVehicleType());
        if (this.getVehicle().isGoodCar()) {
            int int0 = 100;
            if (_item != null) {
                int0 = _item.getConditionMax();
            }

            this.setCondition(Rand.Next(int0 - int0 / 3, int0));
            if (_item != null) {
                _item.setCondition(this.getCondition());
            }
        } else {
            int int1 = 100;
            if (_item != null) {
                int1 = _item.getConditionMax();
            }

            if (vehicleType != null) {
                int1 = (int)(int1 * vehicleType.getRandomBaseVehicleQuality());
            }

            float float0 = 100.0F;
            if (_item != null) {
                int int2 = _item.getChanceToSpawnDamaged();
                if (vehicleType != null) {
                    int2 += vehicleType.chanceToPartDamage;
                }

                if (int2 > 0 && Rand.Next(100) < int2) {
                    float0 = Rand.Next(int1 - int1 / 2, int1);
                }
            } else {
                int int3 = 30;
                if (vehicleType != null) {
                    int3 += vehicleType.chanceToPartDamage;
                }

                if (Rand.Next(100) < int3) {
                    float0 = Rand.Next(int1 * 0.5F, (float)int1);
                }
            }

            switch (SandboxOptions.instance.CarGeneralCondition.getValue()) {
                case 1:
                    float0 -= Rand.Next(float0 * 0.3F, Rand.Next(float0 * 0.3F, float0 * 0.9F));
                    break;
                case 2:
                    float0 -= Rand.Next(float0 * 0.1F, float0 * 0.3F);
                case 3:
                default:
                    break;
                case 4:
                    float0 += Rand.Next(float0 * 0.2F, float0 * 0.4F);
                    break;
                case 5:
                    float0 += Rand.Next(float0 * 0.5F, float0 * 0.9F);
            }

            float0 = Math.max(0.0F, float0);
            float0 = Math.min(100.0F, float0);
            this.setCondition((int)float0);
            if (_item != null) {
                _item.setCondition(this.getCondition());
            }
        }
    }

    public void setGeneralCondition(InventoryItem _item, float baseQuality, float chanceToSpawnDamaged) {
        int int0 = 100;
        int0 = (int)(int0 * baseQuality);
        float float0 = 100.0F;
        if (_item != null) {
            int int1 = _item.getChanceToSpawnDamaged();
            int1 = (int)(int1 + chanceToSpawnDamaged);
            if (int1 > 0 && Rand.Next(100) < int1) {
                float0 = Rand.Next(int0 - int0 / 2, int0);
            }
        } else {
            int int2 = 30;
            int2 = (int)(int2 + chanceToSpawnDamaged);
            if (Rand.Next(100) < int2) {
                float0 = Rand.Next(int0 * 0.5F, (float)int0);
            }
        }

        switch (SandboxOptions.instance.CarGeneralCondition.getValue()) {
            case 1:
                float0 -= Rand.Next(float0 * 0.3F, Rand.Next(float0 * 0.3F, float0 * 0.9F));
                break;
            case 2:
                float0 -= Rand.Next(float0 * 0.1F, float0 * 0.3F);
            case 3:
            default:
                break;
            case 4:
                float0 += Rand.Next(float0 * 0.2F, float0 * 0.4F);
                break;
            case 5:
                float0 += Rand.Next(float0 * 0.5F, float0 * 0.9F);
        }

        float0 = Math.max(0.0F, float0);
        float0 = Math.min(100.0F, float0);
        this.setCondition((int)float0);
        if (_item != null) {
            _item.setCondition(this.getCondition());
        }
    }

    public static float getNumberByCondition(float number, float cond, float min) {
        cond += 20.0F * (100.0F - cond) / 100.0F;
        float float0 = cond / 100.0F;
        return Math.round(Math.max(min, number * float0) * 100.0F) / 100.0F;
    }

    public boolean isContainer() {
        return this.scriptPart == null ? false : this.scriptPart.container != null;
    }

    public int getContainerCapacity() {
        return this.getContainerCapacity(null);
    }

    public int getContainerCapacity(IsoGameCharacter chr) {
        if (!this.isContainer()) {
            return 0;
        } else if (this.getItemContainer() != null) {
            return chr == null ? this.getItemContainer().getCapacity() : this.getItemContainer().getEffectiveCapacity(chr);
        } else if (this.getInventoryItem() != null) {
            return this.scriptPart.container.conditionAffectsCapacity
                ? (int)getNumberByCondition(this.getInventoryItem().getMaxCapacity(), this.getCondition(), 5.0F)
                : this.getInventoryItem().getMaxCapacity();
        } else {
            return this.scriptPart.container.capacity;
        }
    }

    public void setContainerCapacity(int cap) {
        if (this.isContainer()) {
            if (this.getItemContainer() != null) {
                this.getItemContainer().Capacity = cap;
            }
        }
    }

    public String getContainerContentType() {
        return !this.isContainer() ? null : this.scriptPart.container.contentType;
    }

    public float getContainerContentAmount() {
        if (!this.isContainer()) {
            return 0.0F;
        } else {
            if (this.hasModData()) {
                Object object = this.getModData().rawget("contentAmount");
                if (object instanceof Double) {
                    return ((Double)object).floatValue();
                }
            }

            return 0.0F;
        }
    }

    public void setContainerContentAmount(float amount) {
        this.setContainerContentAmount(amount, false, false);
    }

    public void setContainerContentAmount(float amount, boolean force, boolean noUpdateMass) {
        if (this.isContainer()) {
            int int0 = this.scriptPart.container.capacity;
            if (this.getInventoryItem() != null) {
                int0 = this.getInventoryItem().getMaxCapacity();
            }

            if (!force) {
                amount = Math.min(amount, (float)int0);
            }

            amount = Math.max(amount, 0.0F);
            this.getModData().rawset("contentAmount", (double)amount);
            if (this.getInventoryItem() != null) {
                this.getInventoryItem().setItemCapacity(amount);
            }

            if (!noUpdateMass) {
                this.getVehicle().updateTotalMass();
            }
        }
    }

    public int getContainerSeatNumber() {
        return !this.isContainer() ? -1 : this.scriptPart.container.seat;
    }

    public String getLuaFunction(String name) {
        return this.scriptPart != null && this.scriptPart.luaFunctions != null ? this.scriptPart.luaFunctions.get(name) : null;
    }

    protected VehicleScript.Model getScriptModelById(String string) {
        if (this.scriptPart != null && this.scriptPart.models != null) {
            for (int int0 = 0; int0 < this.scriptPart.models.size(); int0++) {
                VehicleScript.Model model = this.scriptPart.models.get(int0);
                if (string.equals(model.id)) {
                    return model;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public void setModelVisible(String id, boolean visible) {
        VehicleScript.Model model = this.getScriptModelById(id);
        if (model != null) {
            this.vehicle.setModelVisible(this, model, visible);
        }
    }

    public VehiclePart getParent() {
        return this.parent;
    }

    public void addChild(VehiclePart child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }

        this.children.add(child);
    }

    public int getChildCount() {
        return this.children == null ? 0 : this.children.size();
    }

    public VehiclePart getChild(int index) {
        return this.children != null && index >= 0 && index < this.children.size() ? this.children.get(index) : null;
    }

    public VehicleDoor getDoor() {
        return this.door;
    }

    public VehicleWindow getWindow() {
        return this.window;
    }

    public VehiclePart getChildWindow() {
        for (int int0 = 0; int0 < this.getChildCount(); int0++) {
            VehiclePart part1 = this.getChild(int0);
            if (part1.getWindow() != null) {
                return part1;
            }
        }

        return null;
    }

    public VehicleWindow findWindow() {
        VehiclePart part0 = this.getChildWindow();
        return part0 == null ? null : part0.getWindow();
    }

    public VehicleScript.Anim getAnimById(String id) {
        if (this.scriptPart != null && this.scriptPart.anims != null) {
            for (int int0 = 0; int0 < this.scriptPart.anims.size(); int0++) {
                VehicleScript.Anim anim = this.scriptPart.anims.get(int0);
                if (anim.id.equals(id)) {
                    return anim;
                }
            }

            return null;
        } else {
            return null;
        }
    }

    public void save(ByteBuffer output) throws IOException {
        GameWindow.WriteStringUTF(output, this.getId());
        output.put((byte)(this.bCreated ? 1 : 0));
        output.putFloat(this.lastUpdated);
        if (this.getInventoryItem() == null) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
            this.getInventoryItem().saveWithSize(output, false);
        }

        if (this.getItemContainer() == null) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
            this.getItemContainer().save(output);
        }

        if (this.hasModData() && !this.getModData().isEmpty()) {
            output.put((byte)1);
            this.getModData().save(output);
        } else {
            output.put((byte)0);
        }

        if (this.getDeviceData() == null) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
            this.getDeviceData().save(output, false);
        }

        if (this.light == null) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
            this.light.save(output);
        }

        if (this.door == null) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
            this.door.save(output);
        }

        if (this.window == null) {
            output.put((byte)0);
        } else {
            output.put((byte)1);
            this.window.save(output);
        }

        output.putInt(this.condition);
        output.putFloat(this.wheelFriction);
        output.putInt(this.mechanicSkillInstaller);
        output.putFloat(this.suspensionCompression);
        output.putFloat(this.suspensionDamping);
    }

    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        this.partId = GameWindow.ReadStringUTF(input);
        this.bCreated = input.get() == 1;
        this.lastUpdated = input.getFloat();
        if (input.get() == 1) {
            InventoryItem itemx = InventoryItem.loadItem(input, WorldVersion);
            this.item = itemx;
        }

        if (input.get() == 1) {
            if (this.container == null) {
                this.container = new ItemContainer();
                this.container.parent = this.getVehicle();
                this.container.vehiclePart = this;
            }

            this.container.getItems().clear();
            this.container.ID = 0;
            this.container.load(input, WorldVersion);
        }

        if (input.get() == 1) {
            this.getModData().load(input, WorldVersion);
        }

        if (input.get() == 1) {
            if (this.getDeviceData() == null) {
                this.createSignalDevice();
            }

            this.getDeviceData().load(input, WorldVersion, false);
        }

        if (input.get() == 1) {
            if (this.light == null) {
                this.light = new VehicleLight();
            }

            this.light.load(input, WorldVersion);
        }

        if (input.get() == 1) {
            if (this.door == null) {
                this.door = new VehicleDoor(this);
            }

            this.door.load(input, WorldVersion);
        }

        if (input.get() == 1) {
            if (this.window == null) {
                this.window = new VehicleWindow(this);
            }

            this.window.load(input, WorldVersion);
        }

        if (WorldVersion >= 116) {
            this.setCondition(input.getInt());
        }

        if (WorldVersion >= 118) {
            this.setWheelFriction(input.getFloat());
            this.setMechanicSkillInstaller(input.getInt());
        }

        if (WorldVersion >= 119) {
            this.setSuspensionCompression(input.getFloat());
            this.setSuspensionDamping(input.getFloat());
        }
    }

    public int getWheelIndex() {
        if (this.scriptPart != null && this.scriptPart.wheel != null) {
            for (int int0 = 0; int0 < this.vehicle.script.getWheelCount(); int0++) {
                VehicleScript.Wheel wheel = this.vehicle.script.getWheel(int0);
                if (this.scriptPart.wheel.equals(wheel.id)) {
                    return int0;
                }
            }

            return -1;
        } else {
            return -1;
        }
    }

    public void createSpotLight(float xOffset, float yOffset, float dist, float intensity, float dot, int focusing) {
        this.light = this.light == null ? new VehicleLight() : this.light;
        this.light.offset.set(xOffset, yOffset, 0.0F);
        this.light.dist = dist;
        this.light.intensity = intensity;
        this.light.dot = dot;
        this.light.focusing = focusing;
    }

    public VehicleLight getLight() {
        return this.light;
    }

    public float getLightDistance() {
        return this.light == null ? 0.0F : 8.0F + 16.0F * this.getCondition() / 100.0F;
    }

    public float getLightIntensity() {
        return this.light == null ? 0.0F : 0.5F + 0.25F * this.getCondition() / 100.0F;
    }

    public float getLightFocusing() {
        return this.light == null ? 0.0F : 10 + (int)(90.0F * (1.0F - this.getCondition() / 100.0F));
    }

    public void setLightActive(boolean active) {
        if (this.light != null && this.light.active != active) {
            this.light.active = active;
            if (GameServer.bServer) {
                this.vehicle.updateFlags = (short)(this.vehicle.updateFlags | 8);
            }
        }
    }

    public DeviceData createSignalDevice() {
        if (this.deviceData == null) {
            this.deviceData = new DeviceData(this);
        }

        if (this.chatElement == null) {
            this.chatElement = new ChatElement(this, 5, "device");
        }

        return this.deviceData;
    }

    public boolean hasDevicePower() {
        return this.vehicle.getBatteryCharge() > 0.0F;
    }

    @Override
    public DeviceData getDeviceData() {
        return this.deviceData;
    }

    @Override
    public void setDeviceData(DeviceData data) {
        if (data == null) {
            data = new DeviceData(this);
        }

        this.deviceData = data;
        this.deviceData.setParent(this);
    }

    @Override
    public float getDelta() {
        return this.deviceData != null ? this.deviceData.getPower() : 0.0F;
    }

    @Override
    public void setDelta(float d) {
        if (this.deviceData != null) {
            this.deviceData.setPower(d);
        }
    }

    @Override
    public float getX() {
        return this.vehicle.getX();
    }

    @Override
    public float getY() {
        return this.vehicle.getY();
    }

    @Override
    public float getZ() {
        return this.vehicle.getZ();
    }

    @Override
    public IsoGridSquare getSquare() {
        return this.vehicle.getSquare();
    }

    @Override
    public void AddDeviceText(String line, float r, float g, float b, String guid, String codes, int distance) {
        if (this.deviceData != null && this.deviceData.getIsTurnedOn()) {
            this.deviceData.doReceiveSignal(distance);
            if (this.deviceData.getDeviceVolume() > 0.0F) {
                this.chatElement
                    .addChatLine(line, r, g, b, UIFont.Medium, this.deviceData.getDeviceVolumeRange(), "default", true, true, true, true, true, true);
                if (codes != null) {
                    LuaEventManager.triggerEvent("OnDeviceText", guid, codes, this.getX(), this.getY(), this.getZ(), line, this);
                }
            }
        }
    }

    @Override
    public boolean HasPlayerInRange() {
        return this.hasPlayerInRange;
    }

    private boolean playerWithinBounds(IsoPlayer player, float float0) {
        return player != null && !player.isDead()
            ? (player.getX() > this.getX() - float0 || this.getX() < this.getX() + float0)
                && (player.getY() > this.getY() - float0 || this.getY() < this.getY() + float0)
            : false;
    }

    public void updateSignalDevice() {
        if (this.deviceData != null) {
            if (this.deviceData.getIsTurnedOn() && this.isInventoryItemUninstalled()) {
                this.deviceData.setIsTurnedOn(false);
            }

            if (GameClient.bClient) {
                this.deviceData.updateSimple();
            } else {
                this.deviceData.update(true, this.hasPlayerInRange);
            }

            if (!GameServer.bServer) {
                this.hasPlayerInRange = false;
                if (this.deviceData.getIsTurnedOn()) {
                    for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                        IsoPlayer player = IsoPlayer.players[int0];
                        if (this.playerWithinBounds(player, this.deviceData.getDeviceVolumeRange() * 0.6F)) {
                            this.hasPlayerInRange = true;
                            break;
                        }
                    }
                }

                this.chatElement.setHistoryRange(this.deviceData.getDeviceVolumeRange() * 0.6F);
                this.chatElement.update();
            } else {
                this.hasPlayerInRange = false;
            }
        }
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String _category) {
        this.category = _category;
    }

    public int getCondition() {
        return this.condition;
    }

    public void setCondition(int _condition) {
        _condition = Math.min(100, _condition);
        _condition = Math.max(0, _condition);
        if (this.getVehicle().getDriver() != null) {
            if (this.condition > 60 && _condition < 60 && _condition > 40) {
                LuaEventManager.triggerEvent("OnVehicleDamageTexture", this.getVehicle().getDriver());
            }

            if (this.condition > 40 && _condition < 40) {
                LuaEventManager.triggerEvent("OnVehicleDamageTexture", this.getVehicle().getDriver());
            }
        }

        this.condition = _condition;
        if (this.getInventoryItem() != null) {
            this.getInventoryItem().setCondition(_condition);
        }

        this.getVehicle().bDoDamageOverlay = true;
        if ("lightbar".equals(this.getId())) {
            this.getVehicle().lightbarLightsMode.set(0);
            this.getVehicle().setLightbarSirenMode(0);
        }

        if (this.scriptPart != null && this.scriptPart.id != null && this.scriptPart.id.equals("TrailerTrunk")) {
            this.getItemContainer().setCapacity(Math.max(80, _condition));
        }
    }

    public void damage(int amount) {
        if (this.getWindow() != null) {
            this.getWindow().damage(amount);
        } else {
            this.setCondition(this.getCondition() - amount);
            this.getVehicle().transmitPartCondition(this);
        }
    }

    public boolean isSpecificItem() {
        return this.specificItem;
    }

    public void setSpecificItem(boolean _specificItem) {
        this.specificItem = _specificItem;
    }

    public float getWheelFriction() {
        return this.wheelFriction;
    }

    public void setWheelFriction(float _wheelFriction) {
        this.wheelFriction = _wheelFriction;
    }

    public int getMechanicSkillInstaller() {
        return this.mechanicSkillInstaller;
    }

    public void setMechanicSkillInstaller(int _mechanicSkillInstaller) {
        this.mechanicSkillInstaller = _mechanicSkillInstaller;
    }

    public float getSuspensionDamping() {
        return this.suspensionDamping;
    }

    public void setSuspensionDamping(float _suspensionDamping) {
        this.suspensionDamping = _suspensionDamping;
    }

    public float getSuspensionCompression() {
        return this.suspensionCompression;
    }

    public void setSuspensionCompression(float _suspensionCompression) {
        this.suspensionCompression = _suspensionCompression;
    }

    public float getEngineLoudness() {
        return this.engineLoudness;
    }

    public void setEngineLoudness(float _engineLoudness) {
        this.engineLoudness = _engineLoudness;
    }

    public void repair() {
        VehicleScript vehicleScript = this.vehicle.getScript();
        float float0 = this.getContainerContentAmount();
        if (this.isInventoryItemUninstalled()) {
            String string = this.getItemType().get(Rand.Next(this.getItemType().size()));
            if (string != null && !string.isEmpty()) {
                InventoryItem itemx = InventoryItemFactory.CreateItem(string);
                if (itemx != null) {
                    this.setInventoryItem(itemx);
                    if (itemx.getMaxCapacity() > 0) {
                        itemx.setItemCapacity(itemx.getMaxCapacity());
                    }

                    this.vehicle.transmitPartItem(this);
                    this.callLuaVoid(this.getLuaFunction("init"), this.vehicle, this);
                }
            }
        }

        if (this.getDoor() != null && this.getDoor().isLockBroken()) {
            this.getDoor().setLockBroken(false);
            this.vehicle.transmitPartDoor(this);
        }

        if (this.getCondition() != 100) {
            this.setCondition(100);
            if (this.getInventoryItem() != null) {
                this.doInventoryItemStats(this.getInventoryItem(), this.getMechanicSkillInstaller());
            }

            this.vehicle.transmitPartCondition(this);
        }

        if (this.isContainer() && this.getItemContainer() == null && float0 != this.getContainerCapacity()) {
            this.setContainerContentAmount(this.getContainerCapacity());
            this.vehicle.transmitPartModData(this);
        }

        if (this.getInventoryItem() instanceof Drainable && ((Drainable)this.getInventoryItem()).getUsedDelta() < 1.0F) {
            ((Drainable)this.getInventoryItem()).setUsedDelta(1.0F);
            this.vehicle.transmitPartUsedDelta(this);
        }

        if ("Engine".equalsIgnoreCase(this.getId())) {
            byte byte0 = 100;
            int int0 = (int)(vehicleScript.getEngineLoudness() * SandboxOptions.getInstance().ZombieAttractionMultiplier.getValue());
            int int1 = (int)vehicleScript.getEngineForce();
            this.vehicle.setEngineFeature(byte0, int0, int1);
            this.vehicle.transmitEngine();
        }

        this.vehicle.updatePartStats();
        this.vehicle.updateBulletStats();
    }

    private void callLuaVoid(String string, Object object1, Object object2) {
        Object object0 = LuaManager.getFunctionObject(string);
        if (object0 != null) {
            LuaManager.caller.protectedCallVoid(LuaManager.thread, object0, object1, object2);
        }
    }

    public ChatElement getChatElement() {
        return this.chatElement;
    }
}
