// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.characters.Talker;
import zombie.chat.ChatManager;
import zombie.chat.ChatMessage;
import zombie.core.properties.PropertyContainer;
import zombie.interfaces.IUpdater;
import zombie.iso.IsoGridSquare;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.radio.ZomboidRadio;
import zombie.radio.devices.DeviceData;
import zombie.radio.devices.WaveSignalDevice;
import zombie.scripting.objects.Item;
import zombie.ui.UIFont;
import zombie.util.StringUtils;

/**
 * Turbo
 */
public final class Radio extends Moveable implements Talker, IUpdater, WaveSignalDevice {
    protected DeviceData deviceData = null;
    protected GameTime gameTime;
    protected int lastMin = 0;
    protected boolean doPowerTick = false;
    protected int listenCnt = 0;

    public Radio(String module, String name, String itemType, String texName) {
        super(module, name, itemType, texName);
        this.deviceData = new DeviceData(this);
        this.gameTime = GameTime.getInstance();
        this.canBeDroppedOnFloor = true;
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

    public void doReceiveSignal(int distance) {
        if (this.deviceData != null) {
            this.deviceData.doReceiveSignal(distance);
        }
    }

    @Override
    public void AddDeviceText(String line, float r, float g, float b, String guid, String codes, int distance) {
        if (!ZomboidRadio.isStaticSound(line)) {
            this.doReceiveSignal(distance);
        }

        IsoPlayer player = this.getPlayer();
        if (player != null && this.deviceData != null && this.deviceData.getDeviceVolume() > 0.0F && !player.Traits.Deaf.isSet()) {
            player.SayRadio(line, r, g, b, UIFont.Medium, this.deviceData.getDeviceVolumeRange(), this.deviceData.getChannel(), "radio");
            if (codes != null) {
                LuaEventManager.triggerEvent("OnDeviceText", guid, codes, -1, -1, -1, line, this);
            }
        }
    }

    public void AddDeviceText(ChatMessage msg, float r, float g, float b, String guid, String codes, int distance) {
        if (!ZomboidRadio.isStaticSound(msg.getText())) {
            this.doReceiveSignal(distance);
        }

        IsoPlayer player = this.getPlayer();
        if (player != null && this.deviceData != null && this.deviceData.getDeviceVolume() > 0.0F) {
            ChatManager.getInstance().showRadioMessage(msg);
            if (codes != null) {
                LuaEventManager.triggerEvent("OnDeviceText", guid, codes, -1, -1, -1, msg, this);
            }
        }
    }

    @Override
    public boolean HasPlayerInRange() {
        return false;
    }

    @Override
    public boolean ReadFromWorldSprite(String sprite) {
        if (StringUtils.isNullOrWhitespace(sprite)) {
            return false;
        } else {
            IsoSprite _sprite = IsoSpriteManager.instance.NamedMap.get(sprite);
            if (_sprite != null) {
                PropertyContainer propertyContainer = _sprite.getProperties();
                if (propertyContainer.Is("IsMoveAble")) {
                    if (propertyContainer.Is("CustomItem")) {
                        this.customItem = propertyContainer.Val("CustomItem");
                    }

                    this.worldSprite = sprite;
                    return true;
                }
            }

            System.out.println("Warning: Radio worldsprite not valid, sprite = " + (sprite == null ? "null" : sprite));
            return false;
        }
    }

    @Override
    public int getSaveType() {
        return Item.Type.Radio.ordinal();
    }

    @Override
    public float getDelta() {
        return this.deviceData != null ? this.deviceData.getPower() : 0.0F;
    }

    @Override
    public void setDelta(float delta) {
        if (this.deviceData != null) {
            this.deviceData.setPower(delta);
        }
    }

    @Override
    public IsoGridSquare getSquare() {
        return this.container != null && this.container.parent != null && this.container.parent instanceof IsoPlayer ? this.container.parent.getSquare() : null;
    }

    @Override
    public float getX() {
        IsoGridSquare square = this.getSquare();
        return square == null ? 0.0F : square.getX();
    }

    @Override
    public float getY() {
        IsoGridSquare square = this.getSquare();
        return square == null ? 0.0F : square.getY();
    }

    @Override
    public float getZ() {
        IsoGridSquare square = this.getSquare();
        return square == null ? 0.0F : square.getZ();
    }

    public IsoPlayer getPlayer() {
        return this.container != null && this.container.parent != null && this.container.parent instanceof IsoPlayer ? (IsoPlayer)this.container.parent : null;
    }

    @Override
    public void render() {
    }

    @Override
    public void renderlast() {
    }

    @Override
    public void update() {
        if (this.deviceData != null) {
            if (!GameServer.bServer && !GameClient.bClient || GameClient.bClient) {
                boolean boolean0 = false;

                for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
                    IsoPlayer player = IsoPlayer.players[int0];
                    if (player != null && player.getEquipedRadio() == this) {
                        boolean0 = true;
                        break;
                    }
                }

                if (boolean0) {
                    this.deviceData.update(false, true);
                } else {
                    this.deviceData.cleanSoundsAndEmitter();
                }
            }
        }
    }

    @Override
    public boolean IsSpeaking() {
        return false;
    }

    @Override
    public void Say(String line) {
    }

    @Override
    public String getSayLine() {
        return null;
    }

    @Override
    public String getTalkerType() {
        return "radio";
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        if (this.deviceData != null) {
            output.put((byte)1);
            this.deviceData.save(output, net);
        } else {
            output.put((byte)0);
        }
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        if (this.deviceData == null) {
            this.deviceData = new DeviceData(this);
        }

        if (input.get() == 1) {
            this.deviceData.load(input, WorldVersion, false);
        }

        this.deviceData.setParent(this);
    }
}
