// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.ai.sadisticAIDirector.SleepingEvent;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.network.ByteBufferWriter;
import zombie.core.utils.OnceEvery;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemSoundManager;
import zombie.inventory.ItemType;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;

public final class AlarmClockClothing extends Clothing {
    private int alarmHour = -1;
    private int alarmMinutes = -1;
    private boolean alarmSet = false;
    private long ringSound;
    private double ringSince = -1.0;
    private int forceDontRing = -1;
    private String alarmSound = "AlarmClockLoop";
    private int soundRadius = 40;
    private boolean isDigital = true;
    public static short PacketPlayer = 1;
    public static short PacketWorld = 2;
    private static final OnceEvery sendEvery = new OnceEvery(2.0F);

    public AlarmClockClothing(String module, String name, String itemType, String texName, String palette, String SpriteName) {
        super(module, name, itemType, texName, palette, SpriteName);
        this.cat = ItemType.AlarmClockClothing;
        if (this.fullType.contains("Classic")) {
            this.isDigital = false;
        }

        this.randomizeAlarm();
    }

    public AlarmClockClothing(String module, String name, String itemType, Item item, String palette, String SpriteName) {
        super(module, name, itemType, item, palette, SpriteName);
        this.cat = ItemType.AlarmClockClothing;
        if (this.fullType.contains("Classic")) {
            this.isDigital = false;
        }

        this.randomizeAlarm();
    }

    private void randomizeAlarm() {
        if (!Core.bLastStand) {
            if (this.isDigital()) {
                this.alarmHour = Rand.Next(0, 23);
                this.alarmMinutes = (int)Math.floor(Rand.Next(0, 59) / 10) * 10;
                this.alarmSet = Rand.Next(15) == 1;
            }
        }
    }

    public IsoGridSquare getAlarmSquare() {
        IsoGridSquare square = null;
        ItemContainer container = this.getOutermostContainer();
        if (container != null) {
            square = container.getSourceGrid();
            if (square == null && container.parent != null) {
                square = container.parent.square;
            }

            InventoryItem item = container.containingItem;
            if (square == null && item != null && item.getWorldItem() != null) {
                square = item.getWorldItem().getSquare();
            }
        }

        if (square == null && this.getWorldItem() != null && this.getWorldItem().getWorldObjectIndex() != -1) {
            square = this.getWorldItem().square;
        }

        return square;
    }

    @Override
    public boolean shouldUpdateInWorld() {
        return this.alarmSet;
    }

    @Override
    public void update() {
        if (this.alarmSet) {
            int int0 = GameTime.instance.getMinutes() / 10 * 10;
            if (!this.isRinging() && this.forceDontRing != int0 && this.alarmHour == GameTime.instance.getHour() && this.alarmMinutes == int0) {
                this.ringSince = GameTime.getInstance().getWorldAgeHours();
            }

            if (this.isRinging()) {
                double double0 = GameTime.getInstance().getWorldAgeHours();
                if (this.ringSince > double0) {
                    this.ringSince = double0;
                }

                IsoGridSquare square = this.getAlarmSquare();
                if (square == null || this.ringSince + 0.5 < double0) {
                    this.stopRinging();
                } else if (!GameClient.bClient && square != null) {
                    WorldSoundManager.instance.addSoundRepeating(null, square.getX(), square.getY(), square.getZ(), this.getSoundRadius(), 3, false);
                }

                if (!GameServer.bServer && this.isRinging()) {
                    ItemSoundManager.addItem(this);
                }
            }

            if (this.forceDontRing != int0) {
                this.forceDontRing = -1;
            }
        }
    }

    @Override
    public void updateSound(BaseSoundEmitter emitter) {
        assert !GameServer.bServer;

        IsoGridSquare square = this.getAlarmSquare();
        if (square != null) {
            emitter.setPos(square.x + 0.5F, square.y + 0.5F, square.z);
            if (!emitter.isPlaying(this.ringSound)) {
                if (this.alarmSound == null || "".equals(this.alarmSound)) {
                    this.alarmSound = "AlarmClockLoop";
                }

                this.ringSound = emitter.playSoundImpl(this.alarmSound, square);
            }

            if (GameClient.bClient && sendEvery.Check() && this.isInLocalPlayerInventory()) {
                GameClient.instance.sendWorldSound(null, square.x, square.y, square.z, this.getSoundRadius(), 3, false, 0.0F, 1.0F);
            }

            this.wakeUpPlayers(square);
        }
    }

    private void wakeUpPlayers(IsoGridSquare square0) {
        if (!GameServer.bServer) {
            int int0 = this.getSoundRadius();
            int int1 = Math.max(square0.getZ() - 3, 0);
            int int2 = Math.min(square0.getZ() + 3, 8);

            for (int int3 = 0; int3 < IsoPlayer.numPlayers; int3++) {
                IsoPlayer player = IsoPlayer.players[int3];
                if (player != null && !player.isDead() && player.getCurrentSquare() != null && !player.Traits.Deaf.isSet()) {
                    IsoGridSquare square1 = player.getCurrentSquare();
                    if (square1.z >= int1 && square1.z < int2) {
                        float float0 = IsoUtils.DistanceToSquared(square0.x, square0.y, square1.x, square1.y);
                        if (player.Traits.HardOfHearing.isSet()) {
                            float0 *= 4.5F;
                        }

                        if (!(float0 > int0 * int0)) {
                            this.wakeUp(player);
                        }
                    }
                }
            }
        }
    }

    private void wakeUp(IsoPlayer player) {
        if (player.Asleep) {
            SoundManager.instance.setMusicWakeState(player, "WakeNormal");
            SleepingEvent.instance.wakeUp(player);
        }
    }

    public boolean isRinging() {
        return this.ringSince >= 0.0;
    }

    @Override
    public boolean finishupdate() {
        return !this.alarmSet;
    }

    @Override
    public void DoTooltip(ObjectTooltip tooltipUI, ObjectTooltip.Layout layout) {
        ObjectTooltip.LayoutItem layoutItem = layout.addItem();
        layoutItem.setLabel(Translator.getText("IGUI_CurrentTime"), 1.0F, 1.0F, 0.8F, 1.0F);
        int int0 = GameTime.instance.getMinutes() / 10 * 10;
        layoutItem.setValue(GameTime.getInstance().getHour() + ":" + (int0 == 0 ? "00" : int0), 1.0F, 1.0F, 0.8F, 1.0F);
        if (this.alarmSet) {
            layoutItem = layout.addItem();
            layoutItem.setLabel(Translator.getText("IGUI_AlarmIsSetFor"), 1.0F, 1.0F, 0.8F, 1.0F);
            layoutItem.setValue(this.alarmHour + ":" + (this.alarmMinutes == 0 ? "00" : this.alarmMinutes), 1.0F, 1.0F, 0.8F, 1.0F);
        }
    }

    @Override
    public void save(ByteBuffer output, boolean net) throws IOException {
        super.save(output, net);
        output.putInt(this.alarmHour);
        output.putInt(this.alarmMinutes);
        output.put((byte)(this.alarmSet ? 1 : 0));
        output.putFloat((float)this.ringSince);
    }

    @Override
    public void load(ByteBuffer input, int WorldVersion) throws IOException {
        super.load(input, WorldVersion);
        this.alarmHour = input.getInt();
        this.alarmMinutes = input.getInt();
        this.alarmSet = input.get() == 1;
        this.ringSince = input.getFloat();
        this.ringSound = -1L;
    }

    @Override
    public int getSaveType() {
        return Item.Type.AlarmClock.ordinal();
    }

    @Override
    public String getCategory() {
        return this.mainCategory != null ? this.mainCategory : "AlarmClock";
    }

    public void setAlarmSet(boolean _alarmSet) {
        this.stopRinging();
        this.alarmSet = _alarmSet;
        this.ringSound = -1L;
        if (_alarmSet) {
            IsoWorld.instance.CurrentCell.addToProcessItems(this);
            IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
            if (worldInventoryObject != null && worldInventoryObject.getSquare() != null) {
                IsoCell cell = IsoWorld.instance.getCell();
                if (!cell.getProcessWorldItems().contains(worldInventoryObject)) {
                    cell.getProcessWorldItems().add(worldInventoryObject);
                }
            }
        } else {
            IsoWorld.instance.CurrentCell.addToProcessItemsRemove(this);
        }
    }

    public boolean isAlarmSet() {
        return this.alarmSet;
    }

    public void setHour(int hour) {
        this.alarmHour = hour;
        this.forceDontRing = -1;
    }

    public void setMinute(int min) {
        this.alarmMinutes = min;
        this.forceDontRing = -1;
    }

    public int getHour() {
        return this.alarmHour;
    }

    public int getMinute() {
        return this.alarmMinutes;
    }

    public void syncAlarmClock() {
        IsoPlayer player = this.getOwnerPlayer(this.container);
        if (player != null) {
            this.syncAlarmClock_Player(player);
        }

        if (this.worldItem != null) {
            this.syncAlarmClock_World();
        }
    }

    private IsoPlayer getOwnerPlayer(ItemContainer container) {
        if (container == null) {
            return null;
        } else {
            IsoObject object = container.getParent();
            return object instanceof IsoPlayer ? (IsoPlayer)object : null;
        }
    }

    public void syncAlarmClock_Player(IsoPlayer player) {
        if (GameClient.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
            byteBufferWriter.putShort(PacketPlayer);
            byteBufferWriter.putShort((short)player.getPlayerNum());
            byteBufferWriter.putInt(this.id);
            byteBufferWriter.putByte((byte)0);
            byteBufferWriter.putInt(this.alarmHour);
            byteBufferWriter.putInt(this.alarmMinutes);
            byteBufferWriter.putByte((byte)(this.alarmSet ? 1 : 0));
            PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
        }
    }

    public void syncAlarmClock_World() {
        if (GameClient.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
            byteBufferWriter.putShort(PacketWorld);
            byteBufferWriter.putInt(this.worldItem.square.getX());
            byteBufferWriter.putInt(this.worldItem.square.getY());
            byteBufferWriter.putInt(this.worldItem.square.getZ());
            byteBufferWriter.putInt(this.id);
            byteBufferWriter.putByte((byte)0);
            byteBufferWriter.putInt(this.alarmHour);
            byteBufferWriter.putInt(this.alarmMinutes);
            byteBufferWriter.putByte((byte)(this.alarmSet ? 1 : 0));
            PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
        }
    }

    public void syncStopRinging() {
        if (GameClient.bClient) {
            ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
            PacketTypes.PacketType.SyncAlarmClock.doPacket(byteBufferWriter);
            IsoPlayer player = this.getOwnerPlayer(this.container);
            if (player != null) {
                byteBufferWriter.putShort(PacketPlayer);
                byteBufferWriter.putShort((short)player.getPlayerNum());
            } else if (this.getWorldItem() != null) {
                byteBufferWriter.putShort(PacketWorld);
                byteBufferWriter.putInt(this.worldItem.square.getX());
                byteBufferWriter.putInt(this.worldItem.square.getY());
                byteBufferWriter.putInt(this.worldItem.square.getZ());
            } else {
                assert false;
            }

            byteBufferWriter.putInt(this.id);
            byteBufferWriter.putByte((byte)1);
            PacketTypes.PacketType.SyncAlarmClock.send(GameClient.connection);
        }
    }

    public void stopRinging() {
        if (this.ringSound != -1L) {
            this.ringSound = -1L;
        }

        ItemSoundManager.removeItem(this);
        this.ringSince = -1.0;
        this.forceDontRing = GameTime.instance.getMinutes() / 10 * 10;
    }

    public String getAlarmSound() {
        return this.alarmSound;
    }

    public void setAlarmSound(String _alarmSound) {
        this.alarmSound = _alarmSound;
    }

    public int getSoundRadius() {
        return this.soundRadius;
    }

    public void setSoundRadius(int _soundRadius) {
        this.soundRadius = _soundRadius;
    }

    public boolean isDigital() {
        return this.isDigital;
    }
}
